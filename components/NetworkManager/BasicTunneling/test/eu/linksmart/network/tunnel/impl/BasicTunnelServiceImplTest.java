package eu.linksmart.network.tunnel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
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
import eu.linksmart.network.tunnel.BasicTunnelService;
import eu.linksmart.network.tunnel.impl.BasicTunnelServiceImpl;
import eu.linksmart.utils.Part;

public class BasicTunnelServiceImplTest {

	private NetworkManagerCore nmCore;
	private HttpServletRequest requestShort;
	private HttpServletRequest requestLong;
	private HttpServletRequest requestWsdl;
	private HttpServletResponse response;
	private String receiverString = "0.0.0.6986094776732394497";
	private String urlShort = "/0?description=\"CalculatorForBeginners\"";
	private String urlLong = "/0.0.0.1/default/some/addition?description=\"CalculatorForBeginners\"&pid=\"010\"";
	private String urlWsdl = "/0/wsdl?description=\"CalculatorForBeginners\"";
	private String urlOld = "/0/0.0.0.6986094776732394497";
	private VirtualAddress nmAddress;
	private String nmAddressString = "0.0.0.0";
	private NMResponse nmResponse;
	private ServletOutputStream outStream;
	private BasicTunnelServiceImpl tunnel;
	private Part[] descriptionOnly;
	private Part[] descNPid;
	private HttpServletRequest requestOld;


	@Before
	public void setUp(){
		this.nmCore = mock(NetworkManagerCore.class);		
		this.tunnel = new BasicTunnelServiceImpl();
		this.tunnel.nmCore = this.nmCore;
		
		this.response = mock(HttpServletResponse.class);
		this.outStream = mock(ServletOutputStream.class);
		try {
			when(response.getOutputStream()).thenReturn(outStream);
		} catch (IOException e1) {
			//NOP
		}
		
		this.descriptionOnly = new Part[]{new Part("DESCRIPTION", "CalculatorForBeginners")};
		this.descNPid = new Part[]{
				new Part("DESCRIPTION", "CalculatorForBeginners"),
				new Part("PID", "010")};
		this.nmAddress = new VirtualAddress(nmAddressString);
		this.nmResponse = new NMResponse(NMResponse.STATUS_SUCCESS);
		this.nmResponse.setMessage("Bla");

		try {
			when(this.nmCore.getService()).thenReturn(nmAddress);
			when(this.nmCore.sendData(
					any(VirtualAddress.class),
					any(VirtualAddress.class),
					any(byte[].class),
					any(boolean.class))).thenReturn(
							nmResponse);
		} catch (RemoteException e) {
			//NOP
		}
		
		try {
			when(this.nmCore.getServiceByAttributes(any(Part[].class))).
			thenReturn(new Registration[]{
					new Registration(new VirtualAddress(this.receiverString), "CalculatorForBeginners")});
			when(this.nmCore.getServiceByAttributes(any(Part[].class), any(int.class), any(boolean.class), any(boolean.class))).
			thenReturn(new Registration[]{
					new Registration(new VirtualAddress(this.receiverString), "CalculatorForBeginners")});
		} catch (RemoteException e) {
			//NOP
		}

		this.requestShort = mock(HttpServletRequest.class);
		when(this.requestShort.getHeaderNames()).thenReturn(new Hashtable<String, String>().elements());
		when(this.requestShort.getPathInfo()).thenReturn(urlShort.substring(0, urlShort.indexOf("?")));
		when(this.requestShort.getMethod()).thenReturn("POST");
		when(this.requestShort.getProtocol()).thenReturn("HTTP/1.1");
		when(this.requestShort.getQueryString()).thenReturn(this.urlShort.substring(urlShort.indexOf("?") + 1));
		this.requestLong = mock(HttpServletRequest.class);
		when(this.requestLong.getHeaderNames()).thenReturn(new Hashtable<String, String>().elements());
		when(this.requestLong.getPathInfo()).thenReturn(urlLong.substring(0, urlLong.indexOf("?")));
		when(this.requestLong.getMethod()).thenReturn("POST");
		when(this.requestLong.getProtocol()).thenReturn("HTTP/1.1");
		when(this.requestLong.getQueryString()).thenReturn(this.urlLong.substring(urlLong.indexOf("?") + 1));
		this.requestWsdl = mock(HttpServletRequest.class);
		when(this.requestWsdl.getHeaderNames()).thenReturn(new Hashtable<String, String>().elements());
		when(this.requestWsdl.getPathInfo()).thenReturn(urlWsdl.substring(0, urlWsdl.indexOf("?")));
		when(this.requestWsdl.getMethod()).thenReturn("GET");
		when(this.requestWsdl.getProtocol()).thenReturn("HTTP/1.1");
		when(this.requestWsdl.getQueryString()).thenReturn(this.urlWsdl.substring(urlWsdl.indexOf("?") + 1));
		this.requestOld = mock(HttpServletRequest.class);
		when(this.requestOld.getHeaderNames()).thenReturn(new Hashtable<String, String>().elements());
		when(this.requestOld.getPathInfo()).thenReturn(urlOld);
		when(this.requestOld.getMethod()).thenReturn("POST");
		when(this.requestOld.getProtocol()).thenReturn("HTTP/1.1");
		when(this.requestOld.getQueryString()).thenReturn("");
	}

	/**
	 * Checks whether irrelevant parts are correctly removed if there is only "/0" in url.
	 */
	@Test
	public void testValidURLShort(){	
		String requestString = null;
		try {
			requestString = this.tunnel.processRequest(requestShort, response, true);
		} catch (IOException e1) {
			fail("Caughted exeption: " + e1.getMessage());
		}
		assertEquals("POST / HTTP/1.1\r\n\r\n", requestString);
	}
	
	/**
	 * Check whether query provided in path is correctly parsed if query only contains description.
	 */
	@Test
	public void testReceiverQueryParsingShort() {
		VirtualAddress receiver = null;
		try {
			receiver = this.tunnel.getReceiverVirtualAddressFromPath(requestShort);
		} catch (Exception e1) {
			fail("Caughted exeption: " + e1.getMessage());
		}
		try {
			Mockito.verify(nmCore).getServiceByAttributes(this.descriptionOnly);
		} catch (RemoteException e) {
			//NOP
		}
		assertEquals(new VirtualAddress(receiverString), receiver);
	}
	
	/**
	 * Check whether query provided in path is correctly parsed if query contains multiple items.
	 */
	@Test
	public void testReceiverQueryParsingLong() {
		VirtualAddress receiver = null;
		try {
			receiver = this.tunnel.getReceiverVirtualAddressFromPath(requestLong);
		} catch (Exception e1) {
			fail("Caughted exeption: " + e1.getMessage());
		}
		try {
			Mockito.verify(nmCore).getServiceByAttributes(this.descNPid, BasicTunnelService.SERVICE_DISCOVERY_TIMEOUT, true, false);
		} catch (RemoteException e) {
			//NOP
		}
		assertEquals(new VirtualAddress(receiverString), receiver);
	}
	
	/**
	 * Checks whether urls with receiver address are properly parsed.
	 */
	@Test
	public void testReceiverInUrlParsing(){
		VirtualAddress receiver = null;
		try {
			receiver = this.tunnel.getReceiverVirtualAddressFromPath(requestOld);
		} catch (Exception e1) {
			fail("Caughted exeption: " + e1.getMessage());
		}
		assertEquals(new VirtualAddress(receiverString), receiver);
	}

	/**
	 * Checks whether irrelevant parts are correctly removed if path contains:
	 * - sender VAD
	 * - default swith
	 * - additional path
	 * - query.
	 */
	@Test
	public void testValidURLong(){	
		String requestString = null;
		try {
			requestString = this.tunnel.processRequest(requestLong, response, true);
		} catch (IOException e1) {
			fail("Caughted exeption: " + e1.getMessage());
		}
		assertEquals("POST /some/addition HTTP/1.1\r\n\r\n",requestString); 
	}

	/**
	 * Checks whether wsdl request are properly parsed.
	 */
	@Test
	public void testValidUrlWsdl(){	
		String requestString = null;
		try {
			requestString = this.tunnel.processRequest(requestWsdl, response, false);
		} catch (IOException e1) {
			fail("Caughted exeption: " + e1.getMessage());
		}
		assertEquals("GET /?wsdl HTTP/1.1\r\n", requestString);
	}
	
	/**
	 * Checks whether urls with receiver address are properly parsed.
	 */
	@Test
	public void testValidUrlOld(){	
		String requestString = null;
		try {
			requestString = this.tunnel.processRequest(requestOld, response, true);
		} catch (IOException e1) {
			fail("Caught exception: " + e1.getMessage());
		}
		assertEquals("POST / HTTP/1.1\r\n\r\n", requestString);
	}
}