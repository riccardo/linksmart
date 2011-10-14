/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
package eu.linksmart.network.identity;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Vector;

import eu.linksmart.network.impl.NetworkManagerApplicationSoapBindingImpl;
import eu.linksmart.types.HID;
import net.jxta.peer.PeerID;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(value=JMock.class)
public class HIDManagerApplicationTest {
	
	Mockery context;
    NetworkManagerApplicationSoapBindingImpl nm;
    
    public HIDManagerApplicationTest() {
        context = new JUnit4Mockery() {{
                setImposteriser(ClassImposteriser.INSTANCE);
        	}};
    }

    @Before
    public void setup() {
        System.setProperty("org.osgi.service.http.port", "8082");
        nm = context.mock(NetworkManagerApplicationSoapBindingImpl.class, "eu.linksmart.network.impl.NetworkManagerApplicationSoapBindingImpl");
    }

    @Test
    public void constructor() throws Exception {
        PeerID peerid = context.mock(PeerID.class);
        HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
        hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
        HID hid = hidmanagerapplication.createHID();
        assertEquals(hid.contextID1, 0L);
        assertEquals(hid.contextID2, 0L);
        assertEquals(hid.contextID3, 0L);
        assertEquals(hid.level, 0L);
        assertTrue(hid.deviceID > 0L);
        
        String localNMHID = hidmanagerapplication.getLocalNMHID();
    	assertEquals("3.2.1.0", localNMHID);
    }
    
    @Test
    public void testGetHIDs() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	hidmanagerapplication.createHID();
    	Vector<String> res = hidmanagerapplication.getHIDs();
    	assertTrue(res.size() == 1);
    	String hid = res.get(0);
    	assertNotNull(hid);
    }
    
    @Test
    public void testGetAllHIDs() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	hidmanagerapplication.createHID();
    	
    	Vector<String> res = hidmanagerapplication.getHIDs();
    	assertTrue(res.size() == 1);
    	hidmanagerapplication.removeAllHIDs();
    	
    	res = hidmanagerapplication.getHIDs();
    	assertTrue(res.size() == 0);
    }
    
    @Test
    public void testGetHIDByDescription() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	HID hid = hidmanagerapplication.createHID("TestService", "http://localhost:8082/axis/services/TestService");
    	Vector hidByDesc = hidmanagerapplication.getHIDsbyDescription("TestService");
    	assertEquals(1, hidByDesc.size());
    	assertEquals(hid.toString(), hidByDesc.get(0));
    }
    
    @Test
    public void testGetHostHIDsByAttributes() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	
    	Properties props1 = new Properties();
    	props1.put("pr1", "10");
    	props1.put("pr2", "11");
    	
    	HID hid1 = hidmanagerapplication.createHID(999, 3, "TestService", "http://localhost:8082/axis/services/TestService", props1);
    	
    	Properties props2 = new Properties();
    	props2.put("pr1", "12");
    	props2.put("pr2", "13");
    	
    	HID hid2 = hidmanagerapplication.createHID(999, 3, "TestService", "http://localhost:8082/axis/services/TestService", props2);
    	
    	Vector<String> hids1 = hidmanagerapplication.getHostHIDbyAttributes("((pr1==10)&&(pr2==11))", 10);
    	assertEquals(1, hids1.size());
    	assertTrue(hids1.contains(hid1.toString()));
    	
    	Vector<String> hids2 = hidmanagerapplication.getHostHIDbyAttributes("((pr1==10)||(pr1==12))", 10);
    	assertEquals(2, hids2.size());
    	assertTrue(hids2.contains(hid1.toString()));
    	assertTrue(hids2.contains(hid2.toString()));
    	
    	Vector<String> hids3 = hidmanagerapplication.getHostHIDbyAttributes("(pr1==*0)", 10);
    	assertEquals(1, hids3.size());
    	assertTrue(hids3.contains(hid1.toString()));
    	
    	Vector<String> hids4 = hidmanagerapplication.getHostHIDbyAttributes("(pr2!=13)", 10);
    	assertEquals(1, hids4.size());
    	assertTrue(hids4.contains(hid1.toString()));
    }
    
    @Test
    public void testGetHIDByDescriptionWithWildcard() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	HID hid1 = hidmanagerapplication.createHID("TestService1", "http://localhost:8082/axis/services/TestService1");
    	HID hid2 = hidmanagerapplication.createHID("TestService2", "http://localhost:8082/axis/services/TestService2");
    	HID hid3 = hidmanagerapplication.createHID("AnotherService", "http://localhost:8082/axis/services/AnotherService");
    	HID hid4 = hidmanagerapplication.createHID("AnotherExtendedService1", "http://localhost:8082/axis/services/AnotherExtendedTestService1");
    	
    	Vector hidByDesc1 = hidmanagerapplication.getHIDsbyDescription("TestService*");
    	assertEquals(2, hidByDesc1.size());
    	assertTrue(hidByDesc1.contains(hid1.toString()));
    	assertTrue(hidByDesc1.contains(hid2.toString()));
    	
    	Vector hidByDesc2 = hidmanagerapplication.getHIDsbyDescription("*Service*");
    	assertEquals(4, hidByDesc2.size());
    	assertTrue(hidByDesc2.contains(hid1.toString()));
    	assertTrue(hidByDesc2.contains(hid2.toString()));
    	assertTrue(hidByDesc2.contains(hid3.toString()));
    	assertTrue(hidByDesc2.contains(hid4.toString()));
    	
    	Vector hidByDesc3 = hidmanagerapplication.getHIDsbyDescription("*Test*1*");
    	assertEquals(1, hidByDesc3.size());
    	assertTrue(hidByDesc3.contains(hid1.toString()));
    	
    	// wildcards limited up to 3
    	Vector hidByDesc4 = hidmanagerapplication.getHIDsbyDescription("A*E*S*1*");
    	assertEquals(0, hidByDesc4.size());
    }
    
    @Test
    public void testGetHIDsFromID() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	HID hid1 = hidmanagerapplication.createHID("TestService1", "http://localhost:8082/axis/services/TestService1");
    	HID hid2 = hidmanagerapplication.createHID("TestService2", "http://localhost:8082/axis/services/TestService2");
    	
    	String result = hidmanagerapplication.getHIDsFromID(peerid);
    	
    	assertTrue(result.indexOf(hid1 + ";TestService1") >= 0);
    	assertTrue(result.indexOf(hid2 + ";TestService2") >= 0);
    }
    
    /*
    @Test //[TID:b8a6c47d-e82a-414a-926c-3495d5da29b3]
    public void testGetHIDsFromIP() throws UnknownHostException {
    	PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	HID hid1 = hidmanagerapplication.createHID("TestService1", "http://localhost:8082/axis/services/TestService1");
    	HID hid2 = hidmanagerapplication.createHID("TestService2", "http://localhost:8082/axis/services/TestService2");
    	
    	String result = hidmanagerapplication.getHIDsFromIP(InetAddress.getLocalHost() + ":8888");
    	
    	assertTrue(result.indexOf(hid1 + ";TestService1") >= 0);
    	assertTrue(result.indexOf(hid2 + ";TestService2") >= 0);
    }
    */
    
    /*
    @Test //[TID:91218d3c-34af-439f-a26c-85cc008746ed]
    public void testGetHIDsFromIP() throws UnknownHostException {
    	PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	HID hid1 = hidmanagerapplication.createHID("TestService1", "http://localhost:8082/axis/services/TestService1");
    	HID hid2 = hidmanagerapplication.createHID("TestService2", "http://localhost:8082/axis/services/TestService2");
    	
    	String result = hidmanagerapplication.printHIDtable();
    	
    	assertTrue(result.indexOf(hid1 + ";TestService1") >= 0);
    	assertTrue(result.indexOf(hid2 + ";TestService2") >= 0);
    }
    */
    
    @Test
    public void testSetHID() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setHIDs("3.2.1.0", peerid, "127.0.0.1:8082");
    	Vector<String> hids = hidmanagerapplication.getHIDs();
    	assertTrue(hids.contains("3.2.1.0"));
    }
    
    @Test
    public void testGetIPFromHID() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setHIDs("3.2.1.0", peerid, "127.0.0.1:8082");
    	
    	String ip = hidmanagerapplication.getIPbyHID("3.2.1.0");
    	assertEquals("127.0.0.1:8082", ip);
    }
    
    @Test
    public void testGetIDFromHID() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setHIDs("3.2.1.0", peerid, "127.0.0.1:8082");
    	
    	PeerID id = hidmanagerapplication.getIDfromHID(new HID("3.2.1.0"));
    	assertEquals(peerid, id);
    }
    
    @Test
    public void testCheckUpToDate2() throws UnknownHostException {
    	PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	hidmanagerapplication.createHID("TestService1", "http://localhost:8082/axis/services/TestService1");
    	String result = hidmanagerapplication.getHIDsFromID(peerid);
    	
    	hidmanagerapplication.createHID("TestService2", "http://localhost:8082/axis/services/TestService2");
    	String result2 = hidmanagerapplication.getHIDsFromID(peerid);
    	
    	assertFalse(hidmanagerapplication.checkUpToDate2(result, peerid, InetAddress.getLocalHost() + ":8888"));
    	assertTrue(hidmanagerapplication.checkUpToDate2(result2, peerid, InetAddress.getLocalHost() + ":8888"));
    }
    
    /*
    @Test //[TID:1a7cd053-5be5-4d89-afd7-ef1e5949805e]
    public void testCheckUpToDate() throws UnknownHostException {
    	PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	hidmanagerapplication.createHID("TestService1", "http://localhost:8082/axis/services/TestService1");
    	hidmanagerapplication.createHID("TestService2", "http://localhost:8082/axis/services/TestService2");
    	
    	String result = hidmanagerapplication.getHIDsFromID(peerid);
    	
    	assertTrue(hidmanagerapplication.checkUpToDate(result, peerid, InetAddress.getLocalHost() + ":8888"));
    }
    */
    
    @Test
    public void testRemoveHID() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	HID hid1 = hidmanagerapplication.createHID("TestService1", "http://localhost:8082/axis/services/TestService1");
    	HID hid2 = hidmanagerapplication.createHID("TestService2", "http://localhost:8082/axis/services/TestService2");
    	
    	Vector<String> hids = hidmanagerapplication.getHIDs();
    	assertEquals(2, hids.size());
    	assertTrue(hids.contains(hid1.toString()));
    	assertTrue(hids.contains(hid2.toString()));
    	
    	hidmanagerapplication.removeHID(hid1);
    	
    	hids = hidmanagerapplication.getHIDs();
    	assertEquals(1, hids.size());
    	assertFalse(hids.contains(hid1.toString()));
    	assertTrue(hids.contains(hid2.toString()));
    }
    
    @Test
    public void testCreateHID() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	
    	HID hid1 = hidmanagerapplication.createHID(999, 1, "Test 1", "http://localhost:8082/axis/services/Test1", new Properties());
    	assertTrue(hid1.getDeviceID() > 0);
    	assertEquals(999, hid1.getContextID1());
    	assertEquals(0, hid1.getContextID2());
    	assertEquals(0, hid1.getContextID3());
    	assertEquals(1, hid1.getLevel());
    	
    	HID hid2 = hidmanagerapplication.createHID(999, 2, "Test 2", "http://localhost:8082/axis/services/Test2", new Properties());
    	assertTrue(hid2.getDeviceID() > 0);
    	assertTrue(hid2.getContextID1() > 0);
    	assertEquals(999, hid2.getContextID2());
    	assertEquals(0, hid2.getContextID3());
    	assertEquals(2, hid2.getLevel());
    	
    	HID hid3 = hidmanagerapplication.createHID(999, 3, "Test 3", "http://localhost:8082/axis/services/Test3", new Properties());
    	assertTrue(hid3.getDeviceID() > 0);
    	assertTrue(hid3.getContextID1() > 0);
    	assertTrue(hid3.getContextID2() > 0);
    	assertEquals(999, hid3.getContextID3());
    	assertEquals(3, hid3.getLevel());
    	
    	HID hid4 = hidmanagerapplication.createHID("Test", "http://localhost:8082/axis/services/Test1");
    	assertTrue(hid4.getDeviceID() > 0);
    	assertEquals(0, hid4.getContextID1());
    	assertEquals(0, hid4.getContextID2());
    	assertEquals(0, hid4.getContextID3());
    	assertEquals(0, hid4.getLevel());
    }
    
    @Test
    public void testCreateGetHID() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	
    	HID hid = hidmanagerapplication.createHID(999, 1, "Test", "http://localhost:8082/axis/services/Test1", new Properties());
    	
    	Vector<String> res = hidmanagerapplication.getHIDs();
    	assertEquals(1, res.size());
    	assertTrue(hid.toString().equals(res.get(0)));
    }
    
    @Test
    public void testGetHIDByContext() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	
    	HID hid1 = hidmanagerapplication.createHID(999, 1);
    	HID hid2 = hidmanagerapplication.createHID(999, 2);
    	
    	Vector<String> res1 = hidmanagerapplication.getHIDs(999L, 1);
    	assertEquals(1, res1.size());
    	assertTrue(hid1.toString().equals(res1.get(0)));
    	
    	Vector<String> res2 = hidmanagerapplication.getHIDs(999L, 2);
    	assertEquals(1, res2.size());
    	assertTrue(hid2.toString().equals(res2.get(0)));
    }
    
    /*
    @Test //[TID:1f526cdb-54ba-4242-81b6-c58396a8ad3a]
    public void testDeleteHIDs() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	
    	HID hid1 = hidmanagerapplication.createHID(999, 1);
    	HID hid2 = hidmanagerapplication.createHID(999, 2);
    	
    	Vector<String> res1 = hidmanagerapplication.getHIDs();
    	assertEquals(2, res1.size());
    	
    	hidmanagerapplication.deleteHIDs(hid1.toString(), peerid);
    	Vector<String> res2 = hidmanagerapplication.getHIDs();
    	assertEquals(1, res2.size());
    	assertTrue(hid2.toString().equals(res2.get(0)));
    }
    */
    
    @Test
    public void testGetHostHIDs() {
    	PeerID peeridLocal = context.mock(PeerID.class, "local");
    	PeerID peeridExternal = context.mock(PeerID.class, "external");
    	
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peeridLocal, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	
    	HID hidLocal = new HID("10.10.10.10");
    	HIDInfo hidInfoLocal = new HIDInfo("127.0.0.1", 8888, "Test", "http://localhost:8082/axis/services/Test", peeridLocal);
    	hidmanagerapplication.addHID(peeridLocal, hidLocal, hidInfoLocal);
    	
    	HID hidExternal = new HID("10.10.10.15");
    	HIDInfo hidInfoExternal = new HIDInfo("192.168.0.10", 8888, "External", "http://localhost:8082/axis/services/External", peeridExternal);
    	hidmanagerapplication.addHID(peeridExternal, hidExternal, hidInfoExternal);
    	
    	Vector<String> res = hidmanagerapplication.getHostHIDs();
    	assertEquals(1, res.size());
    	assertEquals(hidLocal.toString() ,res.get(0));
    }
    
    @Test
    public void testRemoveHIDsFromID() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	
    	hidmanagerapplication.createHID(999, 1);
    	hidmanagerapplication.createHID(999, 2);
    	
    	Vector<String> res1 = hidmanagerapplication.getHIDs();
    	assertEquals(2, res1.size());
    	
    	hidmanagerapplication.removeHIDsFromID(peerid);
    	
    	Vector<String> res2 = hidmanagerapplication.getHIDs();
    	assertEquals(0, res2.size());
    }
    
    @Test
    public void testAddContext() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	
    	HID hid1 = hidmanagerapplication.createHID(999, 2);
    	
    	Vector<String> res1 = hidmanagerapplication.getHIDs();
    	assertEquals(1, res1.size());
    	assertEquals(hid1.toString(), res1.get(0));
    	
    	HID hid2 = hidmanagerapplication.addContext(888, hid1);
    	
    	assertEquals(hid1.getLevel() + 1, hid2.getLevel());
    	
    	Vector<String> res2 = hidmanagerapplication.getHIDs();
    	assertEquals(2, res2.size());
    	assertTrue(res2.contains(hid2.toString()));
    }
    
    /*
    @Test //[TID:0d0c274f-aa34-4d9b-bf83-7633c3fc033f]
    public void testRenewHID() {
    	PeerID peerid = context.mock(PeerID.class);
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	
    	HID hid1 = hidmanagerapplication.createHID(999, 2);
    	
    	Vector<String> res1 = hidmanagerapplication.getHIDs();
    	assertEquals(1, res1.size());
    	assertEquals(hid1.toString(), res1.get(0));
    	
    	HID hid2 = hidmanagerapplication.renewHID(888, 3, hid1);
    	
    	assertEquals(3, hid2.getLevel());
    	
    	Vector<String> res2 = hidmanagerapplication.getHIDs();
    	assertEquals(1, res2.size());
    	assertEquals(hid2.toString(), res2.get(0));
    }
    */
}
