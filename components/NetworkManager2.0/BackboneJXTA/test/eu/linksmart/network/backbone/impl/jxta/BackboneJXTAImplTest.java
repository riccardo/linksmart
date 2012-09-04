package eu.linksmart.network.backbone.impl.jxta;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Random;

import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.socket.JxtaMulticastSocket;

import org.junit.Before;
import org.junit.Test;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import static org.mockito.Mockito.*;

/**
 * The tests cover only the one class. Calls to other classes are mocked
 * and the internals of JXTA are not covered.
 */
public class BackboneJXTAImplTest {

	private static final String MESSAGE = "message";
	private static final String SOURCE_JXTA = "urn:jxta:uuid-59616261646162614E50472050325033B086DCAEA082434DBB2A8F9F8AAA0BAAAA";
	private static final String DEST_JXTA = "urn:jxta:uuid-59616261646162614E50472050325033D500FF4120204D02B5AEA18115DF543503";

	private HID source = null;
	private HID destination = null;
	private BackboneJXTAImpl bjxta = null;
	private NMResponse success = null;

	@Before
	public void setUp() throws URISyntaxException{	
		//constants
		source = new HID("0.0.0.0");
		destination = new HID("1.1.1.1");
		URI destJxtaUri;
		destJxtaUri = new URI(DEST_JXTA);
		success = new NMResponse(NMResponse.STATUS_SUCCESS);
		success.setMessage(MESSAGE);

		//set up classes
		bjxta = new BackboneJXTAImpl();
		bjxta.pipeSyncHandler = mock(PipeSyncHandler.class);
		bjxta.msocket = mock(MulticastSocket.class);
		bjxta.multicastSocket = mock(JxtaMulticastSocket.class);
		bjxta.listOfRemoteEndpoints = new Hashtable<HID, String>();

		//mock methods
		when(bjxta.pipeSyncHandler
				.sendData(
						eq(source.toString()), 
						eq(destination.toString()), 
						any(byte[].class), 
						eq((PeerID) IDFactory.fromURI(destJxtaUri)), 
						eq(false)))
						.thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));

		when(bjxta.pipeSyncHandler
				.sendData(
						eq(source.toString()), 
						eq(destination.toString()), 
						any(byte[].class), 
						eq((PeerID) IDFactory.fromURI(destJxtaUri)), 
						eq(true)))
						.thenReturn(success);

		//status set up
		bjxta.addEndpoint(destination, DEST_JXTA);
	}

	/**
	 * Testing the case where the backbone should send null data. This should
	 * either drop an exception or return an error.
	 */
	@Test
	public void testSendDataAsynchNull() {
		try {
			NMResponse resp = bjxta.sendDataAsynch(source, destination, null);
			assertEquals(
					"Should not send null!",
					NMResponse.STATUS_ERROR,
					resp.getStatus());
		} catch(IllegalArgumentException e) {
			//appropriate handling of error
		}
	}

	/**
	 * Testing the case where the backbone should send empty data. This should
	 * either drop an exception or return an error.
	 */
	@Test
	public void testSendDataAsynchEmpty() {
		try {
			NMResponse resp = bjxta.sendDataAsynch(source, destination, new byte[]{});
			assertEquals(
					"Should not send empty data!",
					NMResponse.STATUS_ERROR,
					resp.getStatus());
		} catch(IllegalArgumentException e) {
			//appropriate handling of error
		}
	}

	/**
	 * Testing the case where the backbone should send data asynch. As the
	 * provided destination is put into the endpoint list this should cause no
	 * problem and should return a success status.
	 */
	@Test
	public void testSendDataAsynch() {
		Random rand = new Random();
		byte[] data = new byte[10];
		rand.nextBytes(data);

		NMResponse resp = bjxta.sendDataAsynch(source, destination, data);
		assertEquals(
				"Sending failed!",
				NMResponse.STATUS_SUCCESS,
				resp.getStatus());
	}

	/**
	 * Testing the case where the backbone should send null data. This should
	 * either drop an exception or return an error.
	 */
	@Test
	public void testSendDataSynchNull() {
		try {
			NMResponse resp = bjxta.sendDataSynch(source, destination, null);
			assertEquals(
					"Should not send null!",
					NMResponse.STATUS_ERROR,
					resp.getStatus());
		} catch(IllegalArgumentException e) {
			//appropriate handling of error
		}
	}

	/**
	 * Testing the case where the backbone should send empty data. This should
	 * either drop an exception or return an error.
	 */
	@Test
	public void testSendDataSynchEmpty() {
		try {
			NMResponse resp = bjxta.sendDataSynch(source, destination, new byte[]{});
			assertEquals(
					"Should not send empty data!",
					NMResponse.STATUS_ERROR,
					resp.getStatus());
		} catch(IllegalArgumentException e) {
			//appropriate handling of error
		}
	}

	/**
	 * Testing the case where the backbone should send data asynch. As the
	 * provided destination is put into the endpoint list this should cause no
	 * problem and should return a success status.
	 */
	@Test
	public void testSendDataSynch() {
		Random rand = new Random();
		byte[] data = new byte[10];
		rand.nextBytes(data);

		NMResponse resp = bjxta.sendDataSynch(source, destination, data);
		assertEquals(
				"Sending failed!",
				NMResponse.STATUS_SUCCESS,
				resp.getStatus());
		assertEquals("Returned data invalid!", MESSAGE, resp.getMessage());
	}

	/**
	 * Adds and endpoint, checks if it is available and then removes it and
	 * checks if it is removed.
	 */
	@Test
	public void testAddGetRemoveEndpoint() {
		boolean added = bjxta.addEndpoint(source, SOURCE_JXTA);
		assertEquals("Adding endpoint failed!", true, added);

		String storedJxta = bjxta.getEndpoint(source);
		assertEquals("Stored and returned endpoint do not match!",
				SOURCE_JXTA,
				storedJxta);

		boolean deleted = bjxta.removeEndpoint(source);
		assertEquals("Removing endpoint failed!", true, deleted);
	}

	/**
	 * Adding null as endpoint. Should return error.
	 */
	@Test
	public void testAddEndpointNull() {
		try {
			boolean added = bjxta.addEndpoint(source, null);
			assertEquals("Stored null endpoint value!", false, added);
		} catch(IllegalArgumentException e) {
			//appropriate handling of error
		}
	}

	/**
	 * Broadcasting data should return success.
	 */
	@Test
	public void testBroadcastData() {
		Random rand = new Random();
		byte[] data = new byte[10];
		rand.nextBytes(data);

		NMResponse resp = bjxta.broadcastData(source, data);
		assertEquals("Could not broadcast!", NMResponse.STATUS_SUCCESS,
				resp.getStatus());
	}

	/**
	 * Broadcasting data should return success.
	 */
	@Test
	public void testBroadcastDataNull() {
		Random rand = new Random();
		byte[] data = new byte[10];
		rand.nextBytes(data);
		try {
			NMResponse resp = bjxta.broadcastData(source, null);
			assertEquals("Could not broadcast!", NMResponse.STATUS_ERROR,
					resp.getStatus());
		} catch (IllegalArgumentException e) {
			//exception dropped which is ok
		}
	}

	/**
	 * Broadcasting empty data should return error or drop exception.
	 */
	@Test
	public void testBroadcastDataEmpty() {
		Random rand = new Random();
		byte[] data = new byte[10];
		rand.nextBytes(data);
		try {
			NMResponse resp = bjxta.broadcastData(source, new byte[]{});
			assertEquals("Could not broadcast!", NMResponse.STATUS_ERROR,
					resp.getStatus());
		} catch (IllegalArgumentException e) {
			//exception dropped which is ok
		}
	}
}
