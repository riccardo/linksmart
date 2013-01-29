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

package eu.linksmart.network.soaptunnel;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;

/**
 * SOAP Tunnel servlet
 */
public class SOAPTunnelServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
	.getLogger(SOAPTunnelServlet.class.getName());
	private NetworkManagerCore nmCore;

	/**
	 * Constructor with parameters
	 * 
	 * @param nm
	 *            the Network Manager application
	 * 
	 */
	public SOAPTunnelServlet(NetworkManagerCore nmCore) {
		this.nmCore = nmCore;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public boolean checkIfValidURLInRequest(String path){
		
		String parts[] = path.split("/", 6);

		if ((parts.length > 3 && parts[3].equals("wsdl")) 
				|| (parts.length == 6 && parts[5].equals("wsdl"))) {
			return true;
		} else {
			//if attributes were passed with the url add them
			if (parts.length > 3) {
				return true;
			}
			
		}
		
		return false;	
	}

	/**
	 * Performs the HTTP GET operation
	 * 
	 * @param request
	 *            HttpServletRequest that encapsulates the request to the
	 *            servlet
	 * @param response
	 *            HttpServletResponse that encapsulates the response from the
	 *            servlet
	 */

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException {

		// split path - path contains virtual addresses and maybe "wsdl" separated by "/"
		String path = request.getPathInfo();
		String parts[] = path.split("/", 6);
		// extract virtual addresses from path
		VirtualAddress sVirtualAddress = (parts[1].contentEquals("0")) ? nmCore.getService() : new VirtualAddress(parts[1]);
		VirtualAddress rVirtualAddress = new VirtualAddress(parts[2]);

		// build parameter string
		StringBuilder queryBuilder = new StringBuilder();
		//parts[5] is checked for compatibility e.g. ../hola/0/wsdl
		if ((parts.length > 3 && parts[3].equals("wsdl")) 
				|| (parts.length == 6 && parts[5].equals("wsdl"))) {
			queryBuilder.append("wsdl");
		} else {
			//if attributes were passed with the url add them
			if (parts.length > 3) {
				queryBuilder.append(parts[3]);
			}
			String originalQuery = request.getQueryString();
			if (originalQuery != null) {
				if (queryBuilder.length() > 0) {
					queryBuilder.append("?");
				}
				queryBuilder.append(originalQuery);
			}
		}

		// build request

		StringBuilder requestBuilder = new StringBuilder();
		HttpServletRequestWrapper hsrw = new HttpServletRequestWrapper(request);
		//		hsrw.

		// append request line
		requestBuilder.append(request.getMethod()).append(" /");
		if (queryBuilder.length() > 0) {
			requestBuilder.append("?").append(queryBuilder.toString());
		}
		requestBuilder.append(" ").append(request.getProtocol()).append("\r\n");

		// append headers - no body because this is a GET request
		requestBuilder.append(buildHeaders(request));

		// send request to NetworkManagerCore
		sendRequest(sVirtualAddress, rVirtualAddress, requestBuilder.toString(), response);

	}


	/**
	 * Performs the HTTP POST operation
	 * 
	 * @param request
	 *            HttpServletRequest that encapsulates the request to the
	 *            servlet
	 * @param response
	 *            HttpServletResponse that encapsulates the response from the
	 *            servlet
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		// split path - path contains virtual addresses and maybe "wsdl" separated by "/"
		String path = request.getPathInfo();
		StringTokenizer token = new StringTokenizer(path, "/");
		if (token.countTokens() >= 2) {
			StringBuilder requestBuilder = new StringBuilder();
			String sVirtualAddress = token.nextToken();
			VirtualAddress senderVirtualAddress = (sVirtualAddress.contentEquals("0")) ? nmCore.getService() : new VirtualAddress(sVirtualAddress);
			VirtualAddress receiverVirtualAddress = new VirtualAddress(token.nextToken());
			// sessionID = new VirtualAddress(token.nextToken());

			// append headers and blank line for end of headers
			requestBuilder.append(buildHeaders(request));
			requestBuilder.append("\r\n");

			// append content
			if ((request.getContentLength() > 0)) {
				try {
					BufferedReader reader = request.getReader();
					for (String line = null; (line = reader.readLine()) != null;)
						requestBuilder.append(line);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// send request to NetworkManagerCore
			logger.debug("Sending soap request through tunnel");
			sendRequest(senderVirtualAddress, receiverVirtualAddress, requestBuilder.toString(), response);
		}
	}

	/**
	 * Builds the string that represents the headers of a HttpServletRequest
	 * @param request the HttpServletRequest of which to extract the headers
	 * @return the String representing the headers
	 */
	public String buildHeaders(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		Enumeration headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String header = (String) headerNames.nextElement();
			String value = request.getHeader(header);
			builder.append(header + ": " + value + "\r\n");
		}

		return builder.toString();
	}

	/**
	 * Sends a request via NetworkManagerCore and adds the response to the HttpServletResponse
	 * @param senderVirtualAddress the sender VirtualAddress
	 * @param receiverVirtualAddress the receiver VirtualAddress
	 * @param requestString the request message, as a String
	 * @param response the servlet response to add the response message to
	 * @throws IOException
	 */
	private void sendRequest(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, String requestString,
			HttpServletResponse response) throws IOException {
		NMResponse r = this.nmCore.sendData(senderVirtualAddress, receiverVirtualAddress, requestString.getBytes(), true);
		if(r.getStatus() != NMResponse.STATUS_SUCCESS) {
			throw new IOException(r.getMessage());
		}
		String sResp = r.getMessage();

		// /**
		// * If access has been denied, we throw an AccessException to
		// report
		// * to the caller correctly
		// */
		// if (accessDenied)
		// throw new AccessException("Access Denied by Security Policies");

		response.setContentLength(sResp.getBytes().length);
		response.setContentType("text/xml");
		response.getWriter().write(sResp);

	}
}
