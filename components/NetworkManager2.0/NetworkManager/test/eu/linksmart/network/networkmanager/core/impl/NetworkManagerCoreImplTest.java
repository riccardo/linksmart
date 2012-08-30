package eu.linksmart.network.networkmanager.core.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDInfo;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.connection.Connection;
import eu.linksmart.network.connection.ConnectionManager;
import eu.linksmart.network.connection.NOPConnection;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.utils.Part;

/**
 * Test class for NetworkManagerCoreImpl 
 */
public class NetworkManagerCoreImplTest {
	
	private NetworkManagerCoreImpl nmCoreImpl;
	private HID senderHID;
	private HID receiverHID;
	private String topic;
	byte [] data;

	/**
	 * Setup method which is called before the unit tests are executed
	 * It sets some variable values and mocks.
	 */
	@Before
	public void setUp(){
		senderHID = new HID("354.453.455.323");
		receiverHID = new HID("354.453.993.323");
		topic = "test";
		data = "LinkSmart rocks".getBytes();
		nmCoreImpl = new NetworkManagerCoreImpl();
		nmCoreImpl.myHID = new HID("124.3235.346234.3456");
		
		// Mocked classes
		nmCoreImpl.backboneRouter = mock(BackboneRouter.class);
		nmCoreImpl.identityManager = mock(IdentityManager.class);

		// Mocked methods
		when(nmCoreImpl.backboneRouter.broadcastData(any(HID.class), any(byte[].class))).
			thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		when(nmCoreImpl.backboneRouter.sendDataSynch(eq(nmCoreImpl.myHID), eq(receiverHID), any(byte[].class))).
			thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		when(nmCoreImpl.identityManager.getHIDInfo(receiverHID)).
			thenReturn(new HIDInfo(receiverHID, new Part[]{}));
		when(nmCoreImpl.backboneRouter.sendDataSynch(eq(senderHID), eq(receiverHID), any(byte[].class))).
			thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		when(nmCoreImpl.backboneRouter.sendDataAsynch(any(HID.class), eq(receiverHID), any(byte[].class))).
			thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		when(nmCoreImpl.backboneRouter.sendDataAsynch(any(HID.class), eq(senderHID), any(byte[].class))).
			thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
	}

	/**
	 * Tests broadcastMessage of NetworkManagerCoreImpl. 
	 */
	@Test
	public void testBroadcastMessage() {
		byte [] data = "LinkSmart rocks".getBytes();
		Message message = new Message(topic, senderHID, receiverHID, data);
		
		NMResponse response = nmCoreImpl.broadcastMessage(message);
		
		assertNotNull("Response should not be null", response);
		assertEquals(NMResponse.STATUS_SUCCESS, response.getStatus());
	}

	/**
	 * Tests the synchronous sendMessage call
	 */
	@Test
	public void testSendMessageSync() {
		byte [] data = "LinkSmart rocks".getBytes();
		Message message = new Message(topic, senderHID, receiverHID, data);
		
		NMResponse response = nmCoreImpl.sendMessage(message, true);
		
		verify(nmCoreImpl.backboneRouter).
			sendDataSynch(eq(senderHID), eq(receiverHID), any(byte[].class));
		assertNotNull("Response should not be null", response);
	}

	/**
	 * Tests the asynchronous sendMessage call
	 */
	@Test
	public void testSendMessageAsync() {
		byte [] data = "LinkSmart rocks".getBytes();
		Message message = new Message(topic, senderHID, receiverHID, data);
		
		NMResponse response = nmCoreImpl.sendMessage(message, false);
		
		verify(nmCoreImpl.backboneRouter).
			sendDataAsynch(eq(senderHID), eq(receiverHID), any(byte[].class));
		assertNotNull("Response should not be null", response);
	}
	
	/**
	 * Tests receiveDataSync else-case, which occurs if neither the receiverHID 
	 * is null, nor does the identity manager contain the receiverHIDInfo object.
	 */
	@Test
	public void  testReceiveDataSync() {
		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}
		
		
		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataSynch(senderHID, receiverHID, rawData);
		
		// Check if the response is as expected
		assertEquals("The response was not successful.",  
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	@Test
	public void  testReceiveDataSyncUnsuccessful() {
		HIDInfo receiverHIDInfo = new HIDInfo(receiverHID, new Part[]{});
		Set<HIDInfo> infos = new HashSet<HIDInfo>();
		infos.add(receiverHIDInfo);
		
		when(nmCoreImpl.identityManager.getLocalHIDs()).
			thenReturn(infos);
		
		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}
		
		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataSynch(senderHID, receiverHID, rawData);
		
		// Check if the response is as expected
		assertEquals("The request should not be successful.",  
				NMResponse.STATUS_ERROR, response.getStatus());
		assertEquals("Error in processing request", response.getMessage());
	}
	
	/**
	 * Tests receiveDataSync with broadcasting message, but without processing it
	 * Because it is not processed, an exception should occur.
	 */
	@Test
	public void  testReceiveDataSyncBroadcastUnsucessful() {
		
		nmCoreImpl.connectionManager = mock(ConnectionManager.class);
		Connection connection = mock(Connection.class);
		try {
			when(nmCoreImpl.connectionManager.getBroadcastConnection(eq(senderHID))).
				thenReturn(connection);
			when(connection.processData(eq(senderHID), any(HID.class), any(byte[].class))).
				thenReturn(new Message(topic, senderHID, null, data));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured " +e);
		}
		
		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}
		
		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataSynch(senderHID, null, rawData);
		
		// Check if the response is as expected
		assertEquals("The request was not successful.",  
				NMResponse.STATUS_ERROR, response.getStatus());
		assertEquals("Received a message which has not been processed", response.getMessage());
	}
	
	/**
	 * Tests receiveDataSync with broadcasting message as there is no receiver HID
	 */
	@Test
	public void  testReceiveDataSyncBroadcast() {
		mockForSucessfulReceiveData();
		
		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}
		
		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataSynch(senderHID, null, rawData);
		
		// Check if the response is as expected
		assertEquals("The request was not successful.",  
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Tests receiveData with broadcasting message as there is no receiver HID
	 */
	@Test
	public void  testReceiveDataAsyncBroadcast() {
		mockForSucessfulReceiveData();
		
		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}
		
		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataAsynch(senderHID, null, rawData);
		
		// Check if the response is as expected
		assertEquals("The request was not successful.",  
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Tests receiveData with broadcasting message as there is no receiver HID
	 * and with an empty return message
	 */
	@Test
	public void  testReceiveDataAsyncBroadcastEmtpyMessage() {
		mockForSucessfulReceiveData();
		
		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}
		
		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataAsynch(senderHID, null, rawData);
		
		// Check if the response is as expected
		assertEquals("The request was not successful.",  
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Tests receiveDataSync else-case, which occurs if neither the receiverHID 
	 * is null, nor does the identity manager contain the receiverHIDInfo object.
	 */
	@Test
	public void  testReceiveDataAsync() {
		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}
		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataAsynch(senderHID, receiverHID, rawData);
			
		// Check if the response is as expected
		assertEquals("The response was not successful.",  
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Tests receiveDataAsync with broadcasting message, but without processing it
	 * Because it is not processed, an exception should occur.
	 */
	@Test
	public void  testReceiveDataAsyncBroadcastUnsucessful() {
		
		nmCoreImpl.connectionManager = mock(ConnectionManager.class);
		Connection connection = mock(Connection.class);
		try {
			when(nmCoreImpl.connectionManager.getBroadcastConnection(eq(senderHID))).
				thenReturn(connection);
			when(connection.processData(eq(senderHID), any(HID.class), any(byte[].class))).
				thenReturn(new Message(topic, senderHID, null, data));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured " +e);
		}
		
		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}
		
		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataSynch(senderHID, null, rawData);
		
		// Check if the response is as expected
		assertEquals("The request was not successful.",  
				NMResponse.STATUS_ERROR, response.getStatus());
		assertEquals("Received a message which has not been processed", response.getMessage());
	}

	/**
	 * Tests that if addRemoteHID. the right backboneRouter-method is called
	 */
	@Test
	public void  testAddRemoteHID() {
		HID remoteHID = new HID("354.453.111.323");
		
		nmCoreImpl.addRemoteHID(senderHID, remoteHID);
		
		verify(nmCoreImpl.backboneRouter).addRouteForRemoteHID(senderHID, remoteHID);
	}
	
	/**
	 * Tests getHIDByAttribute with a conjunction 
	 */
	@Test
	public void testGetHIDByAttributesConjunction() {
		Part[] attributes = new Part[]{
				new Part("description", "description"), 
				new Part("PID", "Unique PID")};
		String query = ("(description==description)&&(PID==Unique PID)");
		
		HIDInfo[] foundHIDInfos = nmCoreImpl.getHIDByAttributes(attributes);
		verify(nmCoreImpl.identityManager).getHIDsByAttributes(query);
		assertNotNull(foundHIDInfos);
	}
	
	/**
	 * Tests getHIDByAttribute with a disjunction 
	 */
	@Test
	public void testGetHIDByAttributesDisjunction() {
		Part[] attributes = new Part[] { 
				new Part("DESCRIPTION", "description"), 
				new Part("PID", "Unique PID")};
		String query = ("(DESCRIPTION==description)||(PID==Unique PID)");
		
		HIDInfo[] foundHIDInfos = nmCoreImpl.getHIDByAttributes(attributes, false);
		verify(nmCoreImpl.identityManager).getHIDsByAttributes(query);
		assertNotNull(foundHIDInfos);
	}
	
	/**
	 * Tests getHIDByDescription
	 */
	@Test
	public void testGetHIDByDescription() {
		String description = "description";
		String query = ("(DESCRIPTION==description)");
		
		HIDInfo[] foundHIDInfos = nmCoreImpl.getHIDByDescription(description);
		verify(nmCoreImpl.identityManager).getHIDsByAttributes(query);
		assertNotNull(foundHIDInfos);
	}
	
	/**
	 * Tests getHIDByPID
	 */
	@Test
	public void testGetHIDByPID() {
		String PID = "Unique PID";
		String query = ("(PID==Unique PID)");
		Set<HIDInfo> infos = new HashSet<HIDInfo>();
		infos.add(new HIDInfo(new HID(), new Part[0]));
		when(nmCoreImpl.identityManager.getHIDsByAttributes(query)).thenReturn(infos);
		
		HIDInfo foundHIDInfo = nmCoreImpl.getHIDByPID(PID);
		verify(nmCoreImpl.identityManager).getHIDsByAttributes(query);
		assertNotNull(foundHIDInfo);
	}
	
	/**
	 * Tests if there are more than one HIDInfo returned for getHIDByPID
	 */
	@Test
	public void testGetHIDByPIDWithExceptionSeveralHIDInfos() {
		String PID = "Unique PID";
		String query = ("(PID==Unique PID)");
		
		// Return more than one HID so that an exception is thrown
		Set<HIDInfo> infos = new HashSet<HIDInfo>();
		infos.add(new HIDInfo(new HID(), new Part[0]));
		infos.add(new HIDInfo(new HID(), new Part[0]));
		when(nmCoreImpl.identityManager.getHIDsByAttributes(query)).thenReturn(infos);
		
		try {
			nmCoreImpl.getHIDByPID(PID);
			// if we get here, there was no exception
			fail("There should be an exception because more than one HID was " +
					"returned which is not allowed for getHIDbyPID.");
		} catch(RuntimeException e){
			// Check if an exception with the right message was thrown
			assertEquals("More than one hid found to passed PID", e.getMessage());
		}
		verify(nmCoreImpl.identityManager).getHIDsByAttributes(query);
	}
	
	/**
	 * Tests if there was no PID given for getHIDByPID
	 */
	@Test
	public void testGetHIDByPIDWithExceptionNoPID() {
		String PID = "";
		
		try {
			nmCoreImpl.getHIDByPID(PID);
			// if we get here, there was no exception
			fail("There should be an exception because no PID was given.");
		} catch(RuntimeException e){
			// Check if an exception with the right message was thrown
			assertEquals("PID not specificed", e.getMessage());
		}
		// Checks that method was not called
		verify(nmCoreImpl.identityManager, times(0)).getHIDsByAttributes(any(String.class));
	}
	
	/**
	 * Tests getHIDByQuery
	 */
	@Test
	public void testGetHIDByQuery() {
		String query = ("(PID==Unique PID)");
		
		HIDInfo[] foundHIDInfo = nmCoreImpl.getHIDByQuery(query);
		verify(nmCoreImpl.identityManager).getHIDsByAttributes(query);
		assertNotNull(foundHIDInfo);
	}
	
	/**
	 * Tests createHID and checks if a HIDInfo object is returned.
	 */
	@Test
	public void testCreateHID() {
		String endpoint = "for test not important";
		Part[] attributes = new Part[] { 
				new Part("DESCRIPTION", "description"), 
				new Part("PID", "Unique PID")};
		String backboneName = "backbone1";
		List<String> availableBackbones = new ArrayList<String>();
		availableBackbones.add(backboneName);
		when(nmCoreImpl.backboneRouter.getAvailableBackbones()).
			thenReturn(availableBackbones);
		when(nmCoreImpl.identityManager.createHIDForAttributes(attributes)).
			thenReturn(new HIDInfo(new HID(), attributes));
		
		try {
			// call the method in test
			HIDInfo hid = nmCoreImpl.createHID(attributes, endpoint, backboneName);
			// check that something was returned
			assertNotNull(hid);
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Exception occured " + e);
		}
	}
	
	/**
	 * Tests createHID, throws exception because PID already exists for another HID
	 */
	@Test
	public void testCreateHIDWithException() {
		String endpoint = "for test not important";
		Part[] attributes = new Part[] { 
				new Part("DESCRIPTION", "description"), 
				new Part("PID", "Unique PID")};
		String backboneName = "backbone1";
		
		HIDInfo hidInfo = new HIDInfo(new HID(), attributes);
		Set<HIDInfo> returnedHIDInfos = new HashSet<HIDInfo>();
		returnedHIDInfos.add(hidInfo);
		
		List<String> availableBackbones = new ArrayList<String>();
		availableBackbones.add(backboneName);
		
		when(nmCoreImpl.backboneRouter.getAvailableBackbones()).
			thenReturn(availableBackbones);
		// "returning" that PID already exists
		when(nmCoreImpl.identityManager.getHIDsByAttributes("(PID==Unique PID)")).
			thenReturn(returnedHIDInfos);
		
		try {
			// call the method under test
			nmCoreImpl.createHID(attributes, endpoint, backboneName);
			// Should not reach this point
			fail("An exception should have occured saying that the PID is already in use.");
		} catch (RemoteException e) {
			// This exception should not be thrown
			e.printStackTrace();
			fail("Wrong exception occured " + e);
		} catch (IllegalArgumentException e) {
			assertEquals("PID already in use. Please choose a different one.", 
					e.getMessage());
		}
	}
	
	
	/**
	 * Gets test data needed in tests
	 * @return byte[] from processed message
	 * @throws Exception
	 */
	private byte[] getData() throws Exception {
		Message message = new Message(topic, senderHID, receiverHID, data);
		return new NOPConnection(senderHID, receiverHID).processMessage(message);
	}

	private void mockForSucessfulReceiveData() {
		nmCoreImpl.connectionManager = mock(ConnectionManager.class);
		Connection connection = mock(Connection.class);
		try {
			when(nmCoreImpl.connectionManager.getConnection(any(HID.class), any(HID.class))).
				thenReturn(connection);
			when(nmCoreImpl.connectionManager.getBroadcastConnection(eq(senderHID))).
				thenReturn(connection);
			when(connection.processData(eq(senderHID), any(HID.class), any(byte[].class))).
				thenReturn(new Message(topic, senderHID, senderHID, data));
			when(connection.processMessage(any(Message.class))).
				thenReturn(data);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured " +e);
		}
	}
}
