package eu.linksmart.network.identity.impl.crypto;

import java.rmi.RemoteException;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.Registration;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.identity.impl.crypto.IdentityManagerCertImpl;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.utils.Part;

public class IdentityManagerCertImplTest {

	private static final Part ATTR_1 = new Part("One", "Eins");
	private static final Part ATTR_2 = new Part("Two", "Zwei");
	private static final long TIMEOUT = 2000;
	private static final long TOLERANCE = 1000;
	private static final VirtualAddress NM_VIRTUAL_ADDRESS = new VirtualAddress("0.0.0.0");
	private static Registration MY_VIRTUAL_ADDRESS;
	
	private IdentityManagerCertImpl identityMgr;

	@Before
	public void setUp() throws RemoteException {
		//create IdMgr and store entities 
		this.identityMgr = new IdentityManagerCertImpl();
		this.identityMgr.init();
		MY_VIRTUAL_ADDRESS = this.identityMgr.createServiceByAttributes(new Part[]{ATTR_1});
		
		//create NMCore mock object
		this.identityMgr.networkManagerCore = mock(NetworkManagerCore.class);
		when(this.identityMgr.networkManagerCore.
				broadcastMessage(any(Message.class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
			when(this.identityMgr.networkManagerCore.getService()).thenReturn(NM_VIRTUAL_ADDRESS);
	}
	
	/**
	 * Start discovery and look if method returns only after timeout.
	 */
	@Test
	public void getServiceByAttributesTestTimeOut() {
		long startTime = Calendar.getInstance().getTimeInMillis();
		this.identityMgr.getServiceByAttributes(
				new Part[]{ATTR_1},	TIMEOUT, false, false);
		assertTrue(
				"Method returned before timeout",
				Calendar.getInstance().getTimeInMillis() >= startTime + TIMEOUT &&
				Calendar.getInstance().getTimeInMillis() <= startTime + TIMEOUT + TOLERANCE);
	}
	
	
	
	/**
	 * Query for virtualAddress which partially matches but
	 * not fully. If query is strict entity should
	 * not be returned.
	 */
	@Test
	public void getServiceByAttributesTestIsStrict() {
		Registration[] results = this.identityMgr.getServiceByAttributes(
				new Part[]{ATTR_1, ATTR_2},	TIMEOUT, true, true);
		assertTrue(
				"Method returned partial match although strict query.",
				results.length == 0);
	}
	
}
