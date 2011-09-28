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

import java.util.Properties;

import net.jxta.peer.PeerID;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.ReturnValueAction;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(value=JMock.class)
public class HIDInfoTest {
	
	Mockery context;
	
	public HIDInfoTest() {
        context = new JUnit4Mockery() {{
                setImposteriser(ClassImposteriser.INSTANCE);
        	}};
    }
	
	@Test
	public void testConstructor() {
		PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
		String description = "TestService";
		String endpoint = "http://localhost:8082/axis/services/TestService";
		HIDInfo info = new HIDInfo("127.0.0.1", 8082, description, endpoint, peerid);
		assertEquals("127.0.0.1", info.getIp());
		assertEquals(8082, info.getPort());
		assertEquals(peerid, info.getID());
		assertEquals(endpoint, info.getEndpoint());
		assertEquals(description, info.getDescription());
		assertEquals(null, info.getAttributes());
	}
	
	@Test
	public void testConstructorWithAttributes() {
		PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
		String description = "TestService";
		String endpoint = "http://localhost:8082/axis/services/TestService";
		Properties properties = context.mock(Properties.class, "java.util.Properties");
		HIDInfo info = new HIDInfo("127.0.0.1", 8082, description, endpoint, peerid, properties);
		assertEquals("127.0.0.1", info.getIp());
		assertEquals(8082, info.getPort());
		assertEquals(peerid, info.getID());
		assertEquals(endpoint, info.getEndpoint());
		assertEquals(description, info.getDescription());
		assertEquals(properties, info.getAttributes());
	}
	
	@Test
	public void testSetMethods() {
		PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
		String description = "TestService";
		String endpoint = "http://localhost:8082/axis/services/TestService";
		HIDInfo info = new HIDInfo("127.0.0.1", 8082, description, endpoint, peerid);
		
		PeerID newPeerid = peerid;
		String newDescription = "NewTestService";
		String newEndpoint = "http://localhost:8082/axis/services/NewTestService";
		Properties newProperties = context.mock(Properties.class, "java.util.Properties");
		
		info.setIp("192.168.0.1");
		info.setPort(9999);
		info.setID(newPeerid);
		info.setDescription(newDescription);
		info.setEndpoint(newEndpoint);
		info.setAttributes(newProperties);
		
		assertEquals("192.168.0.1", info.getIp());
		assertEquals(9999, info.getPort());
		assertEquals(newPeerid, info.getID());
		assertEquals(newEndpoint, info.getEndpoint());
		assertEquals(newDescription, info.getDescription());
		assertEquals(newProperties, info.getAttributes());
	}
	
	@Test
	public void testToString() {
		PeerID peerid = context.mock(PeerID.class, "net.jxta.peer.PeerID");
		
		String description = "TestService";
		String endpoint = "http://localhost:8082/axis/services/TestService";
		HIDInfo info = new HIDInfo("127.0.0.1", 8082, description, endpoint, peerid);
		
		assertEquals("127.0.0.1:8082:" + description + ":" + endpoint + ":net.jxta.peer.PeerID", info.toString());
	}
}
