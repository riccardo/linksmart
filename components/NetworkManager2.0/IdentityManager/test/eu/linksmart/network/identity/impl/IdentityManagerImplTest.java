package eu.linksmart.network.identity.impl;

import java.rmi.RemoteException;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDInfo;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.identity.impl.IdentityManagerImpl;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.utils.Part;

public class IdentityManagerImplTest {

	private static final Part ATTR_1 = new Part("One", "Eins");
	private static final Part ATTR_2 = new Part("Two", "Zwei");
	private static final long TIMEOUT = 2000;
	private static final long TOLERANCE = 1000;
	private static final HID NM_HID = new HID("0.0.0.0");
	private static HIDInfo MY_HID;
	
	private IdentityManagerImpl identityMgr;

	@Before
	public void setUp() throws RemoteException {
		//create IdMgr and store entities 
		this.identityMgr = new IdentityManagerImpl();
		this.identityMgr.init();
		MY_HID = this.identityMgr.createHIDForAttributes(new Part[]{ATTR_1});
		
		//create NMCore mock object
		this.identityMgr.networkManagerCore = mock(NetworkManagerCore.class);
		when(this.identityMgr.networkManagerCore.
				broadcastMessage(any(Message.class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
			when(this.identityMgr.networkManagerCore.getHID()).thenReturn(NM_HID);
	}
	
	/**
	 * Start discovery and look if method returns only after timeout.
	 */
	@Test
	public void getHIDByAttributesTestTimeOut() {
		long startTime = Calendar.getInstance().getTimeInMillis();
		this.identityMgr.getHIDByAttributes(
				new Part[]{ATTR_1},	TIMEOUT, false, false);
		assertTrue(
				"Method returned before timeout",
				Calendar.getInstance().getTimeInMillis() >= startTime + TIMEOUT &&
				Calendar.getInstance().getTimeInMillis() <= startTime + TIMEOUT + TOLERANCE);
	}
	
	/**
	 * Query for local hid with return first. This
	 * results in the method returning immediately
	 * as queried HID exists locally so no discovery is needed.
	 */
	@Test
	public void getHIDByAttributesTestReturnFirst() {
		long startTime = Calendar.getInstance().getTimeInMillis();
		this.identityMgr.getHIDByAttributes(
				new Part[]{ATTR_1},	TIMEOUT, true, false);
		assertTrue(
				"Method did not return first results.",
				Calendar.getInstance().getTimeInMillis() <= startTime + TOLERANCE);
	}
	
	/**
	 * Query for hid which partially matches but
	 * not fully. If query is strict entity should
	 * not be returned.
	 */
	@Test
	public void getHIDByAttributesTestIsStrict() {
		HIDInfo[] results = this.identityMgr.getHIDByAttributes(
				new Part[]{ATTR_1, ATTR_2},	TIMEOUT, true, true);
		assertTrue(
				"Method returned partial match although strict query.",
				results.length == 0);
	}
	
	/**
	 * Query for local hid which partially matches.
	 * As not strict query partial match should be
	 * returned.
	 */
	@Test
	public void getHIDByAttributesTestIsNotStrict() {
		HIDInfo[] results = this.identityMgr.getHIDByAttributes(
				new Part[]{ATTR_1, ATTR_2},	TIMEOUT, true, false);
		assertTrue("Method did not return partial match.", results.length != 0);
		assertEquals(
				"Method did not return correct match.",
				MY_HID, results[0]);
	}
}
