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
/**
 * Copyright (C) 2006-2010 [Telefonica I+D]
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * An implementation of SOAP Tunnel over Bluetooth
 */

package eu.linksmart.jsoaptunnelling;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.transport.Endpoint;

/**
 * BT SOAP Tunnel implementation
 */
public class SOAPTunnelServiceBTImpl implements SOAPTunnelService {

	private static Logger logger = Logger.getLogger(SOAPTunnelServiceBTImpl.class.getName());

	private static final int MAXNUMRETRIES = 15;
	private static final long SLEEPTIME = 20;
	private static final int BUFFSIZE = 16384;
	
	BundleContext context;
	Endpoint endpointService;
	
	/**
	 * Constructor
	 * 
	 * @param r the endpoint 
	 * @param context the bundle's execution context
	 */
	public SOAPTunnelServiceBTImpl(Endpoint r, BundleContext context) {
		this.context = context;
		this.endpointService = r;
	}
	
	/**
	 * Receive data over a SOAP tunnel
	 * 
	 * @param endpoint the endpoint
	 * @param data the data
	 * @return the response
	 */
	public NMResponse receiveTunnelData(String endpoint, String data) {
		
		NMResponse resp = new NMResponse();
		resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>" 
			+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
			+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
			+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
			+ "<SendDataResult>" + "Error in SOAP tunneling receiveData BT" 
			+ "</SendDataResult></SendDataResponse></soap:Body></soap:Envelope>");
		resp.setSessionID("");

		try {
			String post;
			String btEndpoint;
			String[] endpointParts = endpoint.split("!");
			if (endpointParts.length != 2) return resp;
			
			post = "POST " + endpointParts[1] + " HTTP/1.0\r\n";
			btEndpoint = endpointParts[0];
					
			if (data.contains("<ns1:SetAVTransportURI>"))
				data = data.replace("<ns1:SetAVTransportURI>", "<ns1:SetAVTransportURI "
					+ "xmlns:ns1=\"urn:schemas-upnp-org:service:AVTransport:1\">");
			String[] headers = data.split("\r\n\r\n");
			StringTokenizer token = new StringTokenizer(headers[0], "\r\n");
			String header = "", aux = "";
			
			while (token.hasMoreElements()) {
				aux = token.nextToken();

				if (aux.toLowerCase().contains("content-length")) {
					header = "Content-Length: " + headers[1].length() + "\r\n";
				}
				else {
					header = aux + "\r\n";
				}
				
				if (aux.toLowerCase().contains("connection:")) {
					header = "";
				}
				post = post + header;
			}
			
			String SOAPMessage = post + "\r\n" + headers[1];
			String response = endpointService.post(btEndpoint, SOAPMessage);
			
			String[] s = response.split("\r\n\r\n");
			if (s.length > 0) {
				response = s[1];	
			}
			else {
				// In case the SOAP response from the service is empty.
				response = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope "
					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
					+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
					+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
					+ "<SendDataResult>" + "No SOAP response from the service"
					+ "</SendDataResult></SendDataResponse></soap:Body></soap:Envelope>";
			}
            resp.setData(response);
			logger.debug("Received SOAP message at the end of the tunnel:\n" + SOAPMessage);  
			
		} catch (Exception e) {
			logger.debug("Error delivering the data to destination:\n" + e.getMessage());
			resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
				+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body><SendDataResponse xmlns=\"http://hydra.com.eu/\">"
				+ "<SendDataResult>" + e.getMessage() + "</SendDataResult>"
				+ "</SendDataResponse></soap:Body></soap:Envelope>");
		} 	

	return resp;
	}

}
