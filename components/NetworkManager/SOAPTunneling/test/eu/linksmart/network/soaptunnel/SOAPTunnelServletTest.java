package eu.linksmart.network.soaptunnel;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;

public class SOAPTunnelServletTest {

	private NetworkManagerCore nmCore;
	private SOAPTunnelServlet soapTunnelServlet;
	private HttpServletRequest requestShort;
	private HttpServletRequest requestLong;
	private HttpServletRequest requestWsdl;
	private HttpServletRequest requestBad;
	private HttpServletResponse response;
	private String receiverString = "0.0.0.6986094776732394497";
	private String urlShort = "/0/0.0.0.6986094776732394497";
	private String urlLong = "/0/0.0.0.6986094776732394497/hola";
	private String urlWsdl = "/0/0.0.0.6986094776732394497/wsdl";
	private String urlBad = "kaputto";
	private VirtualAddress nmAddress;
	private String nmAddressString = "0.0.0.0";
	private NMResponse nmResponse;
	private PrintWriter pWriter;


	@Before
	public void setUp(){
		this.nmCore = mock(NetworkManagerCore.class);
		this.soapTunnelServlet = new SOAPTunnelServlet(nmCore);
		this.response = mock(HttpServletResponse.class);
		this.pWriter = new PrintWriter(new ByteArrayOutputStream());
		try {
			when(response.getWriter()).thenReturn(pWriter);
		} catch (IOException e1) {
			//NOP
		}
		this.nmAddress = new VirtualAddress(nmAddressString);
		this.nmResponse = new NMResponse(NMResponse.STATUS_SUCCESS);
		this.nmResponse.setMessage(
				"HTTP/1.1 200 OK\r\n" +
				"Content-Encoding: gzip\r\n" +
				"Connection: Keep-Alive\r\n" +
				"Transfer-Encoding: chunked\r\n" +
				"Content-Type: text/html; charset=UTF-8\r\n\r\nBla");

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

		this.requestShort = mock(HttpServletRequest.class);
		when(this.requestShort.getHeaderNames()).thenReturn(new Hashtable<String, String>().elements());
		when(this.requestShort.getPathInfo()).thenReturn(urlShort);
		when(this.requestShort.getMethod()).thenReturn("GET");
		when(this.requestShort.getProtocol()).thenReturn("HTTP/1.1");
		this.requestLong = mock(HttpServletRequest.class);
		when(this.requestLong.getHeaderNames()).thenReturn(new Hashtable<String, String>().elements());
		when(this.requestLong.getPathInfo()).thenReturn(urlLong);
		when(this.requestLong.getMethod()).thenReturn("GET");
		when(this.requestLong.getProtocol()).thenReturn("HTTP/1.1");
		this.requestWsdl = mock(HttpServletRequest.class);
		when(this.requestWsdl.getHeaderNames()).thenReturn(new Hashtable<String, String>().elements());
		when(this.requestWsdl.getPathInfo()).thenReturn(urlWsdl);
		when(this.requestWsdl.getMethod()).thenReturn("GET");
		when(this.requestWsdl.getProtocol()).thenReturn("HTTP/1.1");
		this.requestBad = mock(HttpServletRequest.class);
		when(this.requestBad.getHeaderNames()).thenReturn(new Hashtable<String, String>().elements());
		when(this.requestBad.getPathInfo()).thenReturn(urlBad);
		when(this.requestBad.getMethod()).thenReturn("GET");
		when(this.requestBad.getProtocol()).thenReturn("HTTP/1.1");
	}
	
	@Test
	/**
	 * Checks whether the headers which are returned with the method are set in the response.
	 */
	public void testHeaderForwarding() {
		try {
			this.soapTunnelServlet.doPost(requestShort, response);
		} catch (IOException e1) {
			// NOP
		}
		
		Mockito.verify(response).setHeader("Content-Encoding", "gzip");
		Mockito.verify(response).setHeader("Connection", "Keep-Alive");
		Mockito.verify(response).setHeader("Content-Encoding", "gzip");
		Mockito.verify(response).setHeader("Content-Type", "text/html; charset=UTF-8");
		Mockito.verify(response).setHeader("Transfer-Encoding", "chunked");
	}

	@Test
	/**
	 * Tests whether a short URL is passed properly into VirtualAddresses.
	 * Sender VA is 0 so should be NM.
	 */
	public void testValidURLShort(){	
		try {
			this.soapTunnelServlet.doPost(requestShort, response);
		} catch (IOException e1) {
			// NOP
		}
		try {
			Mockito.verify(nmCore).sendData(
					nmAddress,
					new VirtualAddress(receiverString),
					new String("\r\n").getBytes(),
					true);
		} catch (RemoteException e) {
			//NOP
		}
	}

	@Test
	/**
	 * Tests whether a short URL is passed properly into VirtualAddresses.
	 */
	public void testValidURLong(){	
		try {
			this.soapTunnelServlet.doPost(requestLong, response);
		} catch (IOException e1) {
			// NOP
		}
		try {
			Mockito.verify(nmCore).sendData(
					nmAddress,
					new VirtualAddress(receiverString),
					new String("\r\n").getBytes(),
					true);
		} catch (RemoteException e) {
			//NOP
		}
	}


	@Test
	/**
	 * Tests whether the WSDL attribute in the request is properly forwarded.
	 */
	public void testValidURWsdl(){	
		try {
			this.soapTunnelServlet.doGet(requestWsdl, response);
		} catch (IOException e1) {
			// NOP
		}
		try {
			Mockito.verify(nmCore).sendData(
					nmAddress,
					new VirtualAddress(receiverString),
					new String("GET /?wsdl HTTP/1.1\r\n").getBytes(),
					true);
		} catch (RemoteException e) {
			//NOP
		}
	}

	@Test
	/**
	 * Tests whether a bad URL is properly handled.
	 */
	public void testValidURLBad(){	
		try {
			this.soapTunnelServlet.doPost(requestBad, response);
			Mockito.verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (IOException e1) {
			//successful test
		}
	}
}


