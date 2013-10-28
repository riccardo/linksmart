package eu.linksmart.network.tunnel;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.utils.Part;

public class TunnelServletTest {

	private NetworkManagerCore nmCore;
	private TunnelServlet tunnelServlet;
	private Tunnel tunnel;
	private HttpServletRequest requestShort;
	private HttpServletResponse response;
	private String receiverString = "0.0.0.6986094776732394497";
	private VirtualAddress receiver = null;
	private String urlShort = "/0?description=\"CalculatorForBeginners\"";
	private VirtualAddress nmAddress;
	private String nmAddressString = "0.0.0.0";
	private NMResponse nmResponse;
	private ServletOutputStream outStream;
	private BasicTunnelService basicTunnelService;
	private Part[] descriptionOnly;


	@Before
	public void setUp(){
		//mock objects
		this.basicTunnelService = mock(BasicTunnelService.class);
		this.response = mock(HttpServletResponse.class);
		this.outStream = mock(ServletOutputStream.class);
		this.nmCore = mock(NetworkManagerCore.class);

		//init objects
		receiver = new VirtualAddress(receiverString);
		nmAddress = new VirtualAddress(nmAddressString);		
		this.tunnel = new Tunnel();
		this.tunnel.nmCore = this.nmCore;
		this.tunnel.basicTunnelService = this.basicTunnelService;
		this.tunnelServlet = new TunnelServlet(this.tunnel);
		this.nmAddress = new VirtualAddress(nmAddressString);
		this.nmResponse = new NMResponse(NMResponse.STATUS_SUCCESS);
		this.nmResponse.setMessage("HTTP/1.1 200 OK");


		//mock methods
		try{
			when(basicTunnelService.getReceiverVirtualAddressFromPath(any(HttpServletRequest.class))).thenReturn(receiver);
			when(basicTunnelService.getSenderVirtualAddressFromPath(any(HttpServletRequest.class), any(VirtualAddress.class))).
			thenReturn(nmAddress);
			when(basicTunnelService.processRequest(any(HttpServletRequest.class), any(HttpServletResponse.class), any(boolean.class))).
			thenReturn(new String());
			when(basicTunnelService.composeResponse(any(byte[].class), any(HttpServletResponse.class))).thenReturn(new byte[]{});
			when(response.getOutputStream()).thenReturn(outStream);
			when(this.nmCore.getServiceByAttributes(any(Part[].class))).thenReturn(new Registration[]{new Registration(receiver, this.descriptionOnly)});
			when(this.nmCore.getVirtualAddress()).thenReturn(nmAddress);
			when(this.nmCore.sendData(
					any(VirtualAddress.class),
					any(VirtualAddress.class),
					any(byte[].class),
					any(boolean.class))).thenReturn(
							nmResponse);
		} catch (Exception e) {
			//NOP
		}

		this.requestShort = mock(HttpServletRequest.class);
		when(this.requestShort.getHeaderNames()).thenReturn(new Hashtable<String, String>().elements());
		when(this.requestShort.getPathInfo()).thenReturn(urlShort.substring(0, urlShort.indexOf("?")));
		when(this.requestShort.getMethod()).thenReturn("POST");
		when(this.requestShort.getProtocol()).thenReturn("HTTP/1.1");
		when(this.requestShort.getQueryString()).thenReturn(this.urlShort.substring(urlShort.indexOf("?") + 1));
	}

	@Test
	public void testRequest() {
		try {
			tunnelServlet.doGet(requestShort, response);
		} catch (IOException e) {
			fail("Caught exception: " + e.getMessage());
		}

		try {
			Mockito.verify(basicTunnelService).getSenderVirtualAddressFromPath(requestShort, nmAddress);
			Mockito.verify(basicTunnelService).getReceiverVirtualAddressFromPath(requestShort);
			Mockito.verify(basicTunnelService).processRequest(requestShort, response, false);
			Mockito.verify(basicTunnelService).composeResponse(nmResponse.getMessageBytes(), response);
		} catch (Exception e) {
			//NOP
		}
	}
}