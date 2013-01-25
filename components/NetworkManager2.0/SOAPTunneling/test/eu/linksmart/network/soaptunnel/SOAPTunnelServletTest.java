package eu.linksmart.network.soaptunnel;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import eu.linksmart.network.networkmanager.core.NetworkManagerCore;

public class SOAPTunnelServletTest {
	
	private NetworkManagerCore nmCore;
	private SOAPTunnelServlet soapTunnelServlet;

	@Before
	public void setUp(){
		this.nmCore = mock(NetworkManagerCore.class);
		this.soapTunnelServlet = new SOAPTunnelServlet(nmCore);
	}
	
	@Test
	public void testDoGet(){
		
		String url = "http://localhost:8082/SOAPTunneling/0/0.0.0.6986094776732394497";
		
		 
		assertTrue(soapTunnelServlet.checkIfValidRequest(url));
	}
	
}

	
