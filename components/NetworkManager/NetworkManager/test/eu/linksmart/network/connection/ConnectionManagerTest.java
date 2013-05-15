package eu.linksmart.network.connection;

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
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.connection.BroadcastConnection;
import eu.linksmart.network.connection.Connection;
import eu.linksmart.network.connection.ConnectionManager;
import eu.linksmart.network.networkmanager.core.impl.NetworkManagerCoreImpl;

public class ConnectionManagerTest {

	private VirtualAddress senderVirtualAddress;
	private VirtualAddress receiverVirtualAddress;
	private ConnectionManager connectionMgr;
	private Connection con;
	private BroadcastConnection bCon;

	@Before
	public void setUp(){
		senderVirtualAddress = new VirtualAddress("354.453.455.323");
		receiverVirtualAddress = new VirtualAddress("354.453.993.323");
		NetworkManagerCoreImpl nmCore = mock(NetworkManagerCoreImpl.class);
		connectionMgr = new ConnectionManager(nmCore);
		con = new Connection(senderVirtualAddress, receiverVirtualAddress);
		bCon = new BroadcastConnection(senderVirtualAddress);
		connectionMgr.connections.add(con);
		connectionMgr.connections.add(bCon);
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
		Connection con = connectionMgr.getConnection(senderVirtualAddress, receiverVirtualAddress);
		assertEquals(this.con, con);
	}
	
	
}
