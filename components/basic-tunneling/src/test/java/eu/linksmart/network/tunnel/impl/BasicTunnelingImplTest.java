package eu.linksmart.network.tunnel.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.linksmart.network.VirtualAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BasicTunnelingImplTest {
	
	private static Logger LOG = Logger.getLogger(BasicTunnelingImplTest.class.getName());
	
	@Before
	public void setUp() {
	}
	
	/**
	 * Tests the implementation of BasicTunneling interface.
	 */
	@Test
	public void testBasicTunneling() {
		try {
			
			BasicTunnelServiceImpl tunneling = new BasicTunnelServiceImpl();
			
			VirtualAddress senderVirtualAddress = new VirtualAddress("0.0.0.357");
			VirtualAddress receiverVirtualAddress = new VirtualAddress("0.0.0.458");
			
			String URL = "/" + senderVirtualAddress.toString() + "/" + receiverVirtualAddress;
			
			HttpServletRequest mockHttpRequest = mock(HttpServletRequest.class);
			when(mockHttpRequest.getPathInfo()).thenReturn(URL);
			
			assertEquals(senderVirtualAddress, tunneling.getSenderVirtualAddressFromPath(mockHttpRequest, senderVirtualAddress));
			
			assertEquals(receiverVirtualAddress, tunneling.getReceiverVirtualAddressFromPath(mockHttpRequest));
			
			LOG.info("testBasicTunneling is successful");
			
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

}
