package eu.linksmart.network.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.core.impl.NetworkManagerCoreImpl;
import eu.linksmart.security.communication.SecurityProperty;

public class ConnectionManagerTest {

	private VirtualAddress senderVirtualAddress;
	private VirtualAddress receiverVirtualAddress;
	private ConnectionManager connectionMgr;
	private Connection con;
	private BroadcastConnection bCon;
	NetworkManagerCoreImpl nmCore;
	IdentityManager idM;

	@Before
	public void setUp(){
		senderVirtualAddress = new VirtualAddress("354.453.455.323");
		receiverVirtualAddress = new VirtualAddress("354.453.993.323");
		nmCore = mock(NetworkManagerCoreImpl.class);
		connectionMgr = new ConnectionManager(nmCore);
		this.idM = mock(IdentityManager.class);
		connectionMgr.setIdentityManager(idM);
		con = new Connection(senderVirtualAddress, receiverVirtualAddress);
		bCon = new BroadcastConnection(senderVirtualAddress);
		connectionMgr.connections.add(con);
		connectionMgr.connections.add(bCon);
		List<SecurityProperty> policy = new ArrayList<SecurityProperty>();
		policy.add(SecurityProperty.NoSecurity);
		connectionMgr.servicePolicies.put(receiverVirtualAddress, policy);

		when(idM.getServiceInfo(any(VirtualAddress.class))).thenReturn(null);
		when(nmCore.getService()).thenReturn(senderVirtualAddress);
		when(nmCore.getVirtualAddress()).thenReturn(senderVirtualAddress);
		when(nmCore.sendMessage(any(Message.class), any(boolean.class))).thenReturn(new NMResponse(NMResponse.STATUS_ERROR));
	}

	/**
	 * Tests if a stored connection is returned on request.
	 */
	@Test
	public void testConnectionRetrieval() {
		Connection con = connectionMgr.getConnection(senderVirtualAddress, receiverVirtualAddress);
		assertEquals(this.con, con);
	}

	/**
	 * Tests if a stored broadcast connection is returned on request.
	 */
	@Test
	public void testBroadcastConnectionRetrieval() {
		Connection con = null;
		try {
			con = connectionMgr.getBroadcastConnection(senderVirtualAddress);
		} catch (Exception e) {
			fail("Unexpected exception");
		}
		assertEquals(this.bCon, con);
	}

	/**
	 * Tests if a stored connection is returned on request.
	 */
	@Test
	public void testStartHandshake() {
		//convert properties to xml and put it into stream
		Properties props = new Properties();
		props.put(ConnectionManager.HANDSHAKE_COMSECMGRS_KEY, "");
		props.put(ConnectionManager.HANDSHAKE_SECPROPS_KEY, SecurityProperty.NoSecurity.name() + ";");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] serializedPayload = null;
		try {
			props.storeToXML(bos, null);
			serializedPayload = bos.toByteArray();
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}
		//finally create the actual message to be sent
		Message handshakeMsg = new Message(
				Message.TOPIC_CONNECTION_HANDSHAKE,
				senderVirtualAddress,
				receiverVirtualAddress,
				serializedPayload);
		try {
			con = connectionMgr.createConnection(receiverVirtualAddress, senderVirtualAddress, new byte[0]);
			verify(nmCore).sendMessage(handshakeMsg, true);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}


}
