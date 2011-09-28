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
        PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
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
    	PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	hidmanagerapplication.createHID();
    	Vector<String> res = hidmanagerapplication.getHIDs();
    	assertTrue(res.size() == 1);
    	String hid = res.get(0);
    	assertNotNull(hid);
    }
    
    @Test
    public void testGetHIDByDescription() {
    	PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	HID hid = hidmanagerapplication.createHID("TestService", "http://localhost:8082/axis/services/TestService");
    	Vector hidByDesc = hidmanagerapplication.getHIDsbyDescription("TestService");
    	assertTrue(hidByDesc.size() == 1);
    	assertEquals(hid.toString(), hidByDesc.get(0));
    }
    
    @Test
    public void testGetHIDsFromID() {
    	PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	HID hid1 = hidmanagerapplication.createHID("TestService1", "http://localhost:8082/axis/services/TestService1");
    	HID hid2 = hidmanagerapplication.createHID("TestService2", "http://localhost:8082/axis/services/TestService2");
    	
    	String result = hidmanagerapplication.getHIDsFromID(peerid);
    	
    	assertTrue(result.indexOf(hid1 + ";TestService1") >= 0);
    	assertTrue(result.indexOf(hid2 + ";TestService2") >= 0);
    }
    
    @Test
    public void testGetHIDsFromIP() {
    	/*PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setJXTAID(peerid, "3.2.1.0", "Test description", "localhost", "127.0.0.1", "8888", false);
    	HID hid1 = hidmanagerapplication.createHID("TestService1", "http://localhost:8082/axis/services/TestService1");
    	HID hid2 = hidmanagerapplication.createHID("TestService2", "http://localhost:8082/axis/services/TestService2");
    	
    	String result = hidmanagerapplication.getHIDsFromIP("127.0.0.1:8888");
    	
    	assertTrue(result.indexOf(hid1 + ";TestService1") >= 0);*/
    	//assertTrue(result.indexOf(hid2 + ";TestService2") >= 0);
    }
    
    @Test
    public void testSetHID() {
    	PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setHIDs("3.2.1.0", peerid, "127.0.0.1:8082");
    	Vector<String> hids = hidmanagerapplication.getHIDs();
    	assertTrue(hids.contains("3.2.1.0"));
    }
    
    @Test
    public void testGetIPFromHID() {
    	PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setHIDs("3.2.1.0", peerid, "127.0.0.1:8082");
    	
    	String ip = hidmanagerapplication.getIPbyHID("3.2.1.0");
    	assertEquals("127.0.0.1:8082", ip);
    }
    
    @Test
    public void testGetIDFromHID() {
    	PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
    	HIDManagerApplication hidmanagerapplication = new HIDManagerApplication(nm);
    	hidmanagerapplication.setHIDs("3.2.1.0", peerid, "127.0.0.1:8082");
    	
    	PeerID id = hidmanagerapplication.getIDfromHID(new HID("3.2.1.0"));
    	assertEquals(peerid, id);
    }
}
