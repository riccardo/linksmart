package eu.linksmart.network.routing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;

public class BackboneRouterImplTest {
	
	private BackboneRouterImpl backboneRouter = new BackboneRouterImpl();
	private HID receiverHID = new HID("354.453.455.323");
	private HID senderHID = new HID("354.453.993.323");
	
	
	/**
	 * Test sendDataSync of BackboneRouter. Backbone and route must be predefined.
	 */
	@Test
	public void testSendDataSync() {
		// set up
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		backboneRouter.addRoute(receiverHID, backbone.getClass().getName());
		when(backbone.sendDataSynch(eq(senderHID), eq(receiverHID),any(byte[].class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.sendDataSynch(senderHID, receiverHID, new byte[]{});
		// assert that something came back
		assertNotNull(response);
	}
	
	/**
	 * Test sendDataSync of BackboneRouter. Backbone and route must not be predefined,
	 * so that an IllegalArgumentException will be thrown as no backbone to sent 
	 * the message to can be found.
	 */
	@Test
	public void testSendDataSyncWithException() {
		// as the route was not set, there should be an exception
		try {
			backboneRouter.sendDataSynch(senderHID, receiverHID, new byte[]{});
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {
			assertEquals("No Backbone found to reach HID " + receiverHID, 
					e.getMessage());
		}
	}
	
	/**
	 * Test sendDataSync of BackboneRouter. Backbone and route must not be predefined,
	 * so that an IllegalArgumentException will be thrown as no backbone to sent 
	 * the message to can be found.
	 */
	@Test
	public void testSendDataSyncSendNullWithException() {
		// as the route was not set, there should be an exception
		try {
			backboneRouter.sendDataSynch(senderHID, null, new byte[]{});
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {
			assertEquals("No Backbone found to reach HID " + null, 
					e.getMessage());
		}
	}
	
	/**
	 * Test sendDataAsync of BackboneRouter. Backbone and route must be predefined.
	 */
	@Test
	public void testSendDataAsync() {
		// set up
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		backboneRouter.addRoute(receiverHID, backbone.getClass().getName());
		when(backbone.sendDataAsynch(eq(senderHID), eq(receiverHID), any(byte[].class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.sendDataAsynch(
				senderHID, 
				receiverHID, 
				new byte[]{});
		// assert that something came back
		assertNotNull(response);
	}
	
	/**
	 * Test sendDataAsync of BackboneRouter. Backbone and route must not be predefined,
	 * so that an IllegalArgumentException will be thrown as no backbone to sent 
	 * the message to can be found.
	 */
	@Test
	public void testSendDataAsyncWithException() {
		// as the route was not set, there should be an exception
		try {
			backboneRouter.sendDataAsynch(senderHID, receiverHID, new byte[]{});
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {
			assertEquals("No Backbone found to reach HID " + receiverHID, 
					e.getMessage());
		}
	}
	
	/**
	 * Test method receiveDataSync where backbone's route is already known.
	 */
	@Test
	public void testReceiveDataSyncWithKnownRoute() {
		// set up
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		backboneRouter.addRoute(senderHID, backbone.getClass().getName());
		
		NetworkManagerCore networkManagerCore = mock(NetworkManagerCore.class);
		backboneRouter.bindNMCore(networkManagerCore);
		when(networkManagerCore.receiveDataSynch(
					eq(senderHID), eq(receiverHID), any(byte[].class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.receiveDataSynch(
				senderHID, 
				receiverHID, 
				new byte[]{}, 
				backbone);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should be successful.", 
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Test method receiveDataSync where backbone's route is not known.
	 */
	@Test
	public void testReceiveDataSyncWithoutKnownRoute() {
		// set up
		Backbone backbone = mock(Backbone.class);
		
		NetworkManagerCore networkManagerCore = mock(NetworkManagerCore.class);
		backboneRouter.bindNMCore(networkManagerCore);
		when(networkManagerCore.receiveDataSynch(
					eq(senderHID), eq(receiverHID), any(byte[].class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.receiveDataSynch(
				senderHID, 
				receiverHID, 
				new byte[]{}, 
				backbone);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should be successful.", 
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Test method receiveDataSync where no NetworkManagerCore was set in 
	 * BackboneRouter.
	 */
	@Test
	public void testReceiveDataSyncUnsuccesful() {
		// set up
		Backbone backbone = mock(Backbone.class);
		
		// call method to test
		NMResponse response = backboneRouter.receiveDataSynch(
				senderHID, 
				receiverHID, 
				new byte[]{}, 
				backbone);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Response should have error status.", 
				NMResponse.STATUS_ERROR, response.getStatus());
	}
	
	/**
	 * Test method receiveDataAsync where backbone's route is already known.
	 */
	@Test
	public void testReceiveDataAsyncWithKnownRoute() {
		// set up
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		backboneRouter.addRoute(senderHID, backbone.getClass().getName());
		
		NetworkManagerCore networkManagerCore = mock(NetworkManagerCore.class);
		backboneRouter.bindNMCore(networkManagerCore);
		when(networkManagerCore.receiveDataAsynch(
					eq(senderHID), eq(receiverHID), any(byte[].class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.receiveDataAsynch(
				senderHID, 
				receiverHID, 
				new byte[]{}, 
				backbone);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should be successful.", 
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Test method receiveDataAsync where backbone's route is not known.
	 */
	@Test
	public void testReceiveDataAsyncWithoutKnownRoute() {
		// set up
		Backbone backbone = mock(Backbone.class);
		
		NetworkManagerCore networkManagerCore = mock(NetworkManagerCore.class);
		backboneRouter.bindNMCore(networkManagerCore);
		when(networkManagerCore.receiveDataAsynch(
					eq(senderHID), eq(receiverHID), any(byte[].class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.receiveDataAsynch(
				senderHID, 
				receiverHID, 
				new byte[]{}, 
				backbone);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should be successful.", 
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Test method receiveDataAsync where no NetworkManagerCore was set in 
	 * BackboneRouter.
	 */
	@Test
	public void testReceiveDataAsyncUnsuccesful() {
		// set up
		Backbone backbone = mock(Backbone.class);
		
		// call method to test
		NMResponse response = backboneRouter.receiveDataAsynch(
				senderHID, 
				receiverHID, 
				new byte[]{}, 
				backbone);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Response should have error status.", 
				NMResponse.STATUS_ERROR, response.getStatus());
	}
	
	/**
	 * Tests broadcastData with two known backbones where one broadcast is 
	 * successful and one is not. As one is successful, the whole broadcast is 
	 * considered successful.
	 */
	@Test
	public void testBroadcastDataWith2Backbones() {
		// set up
		Backbone backbone1 = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone1);
		backboneRouter.addRoute(senderHID, backbone1.getClass().getName());
		when(backbone1.broadcastData(eq(senderHID), any(byte[].class))).
			thenReturn(new NMResponse(NMResponse.STATUS_ERROR));
		
		Backbone backbone2 = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone2);
		backboneRouter.addRoute(senderHID, backbone2.getClass().getName());
		when(backbone2.broadcastData(eq(senderHID), any(byte[].class))).
			thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.broadcastData(senderHID, new byte[]{});
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should be successful.", 
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Tests broadcastData with null values. Status should be error status.
	 */
	@Test
	public void testBroadcastDataWithNullValue() {
		// call method to test
		NMResponse response = backboneRouter.broadcastData(null, null);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should be successful.", 
				NMResponse.STATUS_ERROR, response.getStatus());
	}
	
	/**
	 * Tests broadcastData with one backbone with an unsuccessful broadcast.
	 */
	@Test
	public void testBroadcastDataUnsuccessful() {
		// set up
		Backbone backbone1 = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone1);
		backboneRouter.addRoute(senderHID, backbone1.getClass().getName());
		when(backbone1.broadcastData(eq(senderHID), any(byte[].class))).
			thenReturn(new NMResponse(NMResponse.STATUS_ERROR));
		
		// call method to test
		NMResponse response = backboneRouter.broadcastData(senderHID, new byte[]{});
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should not be successful.", 
				NMResponse.STATUS_ERROR, response.getStatus());
	}
	
	/**
	 * Tests method addRouteToBackbone. As all parameters are set, the call 
	 * should return true.
	 */
	@Test
	public void testAddRouteToBackbone() {
		// set up
		String endpoint = "endpoint";
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		when(backbone.addEndpoint(senderHID, endpoint)).thenReturn(true);
		
		// call method to test
		boolean successful = backboneRouter.addRouteToBackbone(
				senderHID, 
				backbone.getClass().getName(), 
				endpoint);
		// check results
		assertEquals("Adding the backbone should not fail.", 
				true, successful);
	}
	
	/**
	 * Tests method addRouteToBackbone. As senderHID is null, the call 
	 * should return false.
	 */
	@Test
	public void testAddRouteToBackboneSenderNull() {
		// set up
		String endpoint = "endpoint";
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		
		// call method to test
		boolean successful = backboneRouter.addRouteToBackbone(
				null, 
				backbone.getClass().getName(), 
				endpoint);
		// check results
		assertEquals("Adding the backbone should fail.", 
				false, successful);
	}
	
	/**
	 * Tests method addRouteToBackbone. As backbone name is null, the call 
	 * should return false.
	 */
	@Test
	public void testAddRouteToBackboneBackboneNull() {
		// set up
		String endpoint = "endpoint";
		
		// call method to test
		boolean successful = backboneRouter.addRouteToBackbone(
				senderHID, 
				null, 
				endpoint);
		// check results
		assertEquals("Adding the backbone should fail.", 
				false, successful);
	}
	
	/**
	 * Tests method addRouteToBackbone. As senderHID is null, the call 
	 * should return false.
	 */
	@Test
	public void testAddRouteToBackboneEndpointNull() {
		// set up
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		
		// call method to test
		boolean successful = backboneRouter.addRouteToBackbone(
				senderHID, 
				backbone.getClass().getName(), 
				null);
		// check results
		assertEquals("Adding the backbone should fail.", 
				false, successful);
	}
	
	/**
	 * Tests method addRouteToBackbone. All parameters are set, but there will 
	 * be an error adding the endpoint to the backbone and therefore the 
	 * method-call should return false.
	 */
	@Test
	public void testAddRouteToBackboneEnpointFailure() {
		// set up
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);

		// call method to test
		boolean successful = backboneRouter.addRouteToBackbone(
				senderHID, 
				backbone.getClass().getName(), 
				"Endpoint");
		// check results
		assertEquals("Adding the backbone should fail.", 
				false, successful);
	}
	
	/**
	 * Tests method addRouteToBackbone. As there is no entry in 
	 * 'availableBackbones', the call should return false.
	 */
	@Test
	public void testAddRouteToBackboneNotAvailable() {
		// call method to test
		boolean successful = backboneRouter.addRouteToBackbone(
				senderHID, 
				"BackboneName", 
				"Endpoint");
		// check results
		assertEquals("Adding the backbone should fail.", 
				false, successful);
	}
	
	/**
	 * Tests method addRouteToBackbone. As there is already is an entry in the
	 * Map 'hidBackboneMap', the call should return false.
	 */
	@Test
	public void testAddRouteToBackboneTwice() {
		// set up
		String endpoint = "endpoint";
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		when(backbone.addEndpoint(senderHID, endpoint)).thenReturn(true);
		boolean successfulFirst = backboneRouter.addRouteToBackbone(
				senderHID, 
				backbone.getClass().getName(), 
				endpoint);
		
		// call method to test
		boolean successfulSecond = backboneRouter.addRouteToBackbone(
				senderHID, 
				backbone.getClass().getName(), 
				endpoint);
		// check results
		assertEquals("Adding the backbone for the first time should not fail.", 
				true, successfulFirst);
		assertEquals("Adding the backbone for the second time should fail.", 
				false, successfulSecond);
	}
	
	/**
	 * Tests method removeRoute. As the route was not saved prior to the
	 * removal, the method under test should return false. 
	 */
	@Test
	public void testRemoveRouteUnsuccesful() {
		// call method to test
		boolean successful = backboneRouter.removeRoute(senderHID, "BackboneName");
		// check results
		assertEquals("Remove the backbone should return false as there is no " +
				"backbone to remove.", false, successful);
	}
	
	/**
	 * Tests method removeRoute. 
	 */
	@Test
	public void testRemoveRoute() {
		// set up
		String endpoint = "endpoint";
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		when(backbone.addEndpoint(senderHID, endpoint)).thenReturn(true);
			backboneRouter.addRouteToBackbone(
					senderHID, 
					backbone.getClass().getName(), 
					endpoint);
		// call method to test
		boolean successfulRemove = backboneRouter.removeRoute(senderHID, 
				backbone.getClass().getName());
		// check results
		assertEquals("Remove the backbone should return true.", 
				true, successfulRemove);
	}
}
