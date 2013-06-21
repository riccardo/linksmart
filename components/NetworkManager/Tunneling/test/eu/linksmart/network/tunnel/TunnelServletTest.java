package eu.linksmart.network.tunnel;

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
import eu.linksmart.utils.Part;

public class TunnelServletTest {

	private NetworkManagerCore nmCore;
	private TunnelServlet tunnelServlet;
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
	private Tunnel tunnel;
	private Part[] descriptionOnly;
	private Part[] descNPid;
	private HttpServletRequest requestOld;


	@Before
	public void setUp(){
		this.nmCore = mock(NetworkManagerCore.class);
		this.tunnel = mock(Tunnel.class);
		
		this.tunnelServlet = new TunnelServlet(this.tunnel);
		this.response = mock(HttpServletResponse.class);
		this.outStream = mock(ServletOutputStream.class);
		try {
			when(tunnel.getNM()).thenReturn(this.nmCore);
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

	@Test
	public void testValidURLShort(){	
		try {
			this.tunnelServlet.doPost(requestShort, response);
		} catch (IOException e1) {
			// NOP
		}
		try {
			Mockito.verify(nmCore).getServiceByAttributes(this.descriptionOnly);
			Mockito.verify(nmCore).sendData(
					nmAddress,
					new VirtualAddress(receiverString),
					new String("POST / HTTP/1.1\r\n\r\n").getBytes(),
					true);
		} catch (RemoteException e) {
			//NOP
		}
	}

	@Test
	public void testValidURLong(){	
		try {
			this.tunnelServlet.doPost(requestLong, response);
		} catch (IOException e1) {
			// NOP
		}
		try {
			Mockito.verify(nmCore).getServiceByAttributes(this.descNPid, TunnelServlet.SERVICE_DISCOVERY_TIMEOUT, true, false);
			Mockito.verify(nmCore).sendData(
					new VirtualAddress("0.0.0.1"),
					new VirtualAddress(receiverString),
					new String("POST /some/addition HTTP/1.1\r\n\r\n").getBytes(),
					true);
		} catch (RemoteException e) {
			//NOP
		}
	}


	@Test
	public void testValidUrlWsdl(){	
		try {
			this.tunnelServlet.doGet(requestWsdl, response);
		} catch (IOException e1) {
			// NOP
		}
		try {
			Mockito.verify(nmCore).getServiceByAttributes(this.descriptionOnly);
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
	public void testValidUrlOld(){	
		try {
			this.tunnelServlet.doPost(requestOld, response);
		} catch (IOException e1) {
			// NOP
		}
		try {
			Mockito.verify(nmCore).sendData(
					nmAddress,
					new VirtualAddress(receiverString),
					new String("POST / HTTP/1.1\r\n\r\n").getBytes(),
					true);
		} catch (RemoteException e) {
			//NOP
		}
	}
}