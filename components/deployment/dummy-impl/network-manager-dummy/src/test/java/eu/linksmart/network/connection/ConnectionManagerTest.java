package eu.linksmart.network.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import eu.linksmart.network.ErrorMessage;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.core.impl.NetworkManagerCoreImplDummy;
import eu.linksmart.security.communication.SecurityProperty;

public class ConnectionManagerTest {

	private VirtualAddress senderVirtualAddress;
	private VirtualAddress receiverVirtualAddress;
	private ConnectionManager connectionMgr;
	private NetworkManagerCoreImplDummy nmCore;
	private IdentityManager idM;
	private NMResponse handshakeResp;
	private NMResponse handshakeRespDecline;

	@Before
	public void setUp(){
		
	}

	/**
	 * Tests if a stored connection is returned on request.
	 */
	@Test
	public void testConnectionRetrieval() {
		
	}

	/**
	 * Tests if a stored broadcast connection is returned on request.
	 */
	@Test
	public void testBroadcastConnectionRetrieval() {
		
	}

	/**
	 * Tests whether when creating a new connection an appropriate handshake is sent out
	 */
	@Test
	public void testStartHandshake() {
		
	}

	/**
	 * Check whether after successful handshake connection is returned
	 */
	@Test
	public void testRetrieveAgreedConnection() {
		
	}

	/**
	 * Check whether after unsuccessful handshake null is returned
	 */
	@Test
	public void testFailedHandshake() {
		
	}
}
