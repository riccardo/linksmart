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
import java.rmi.AccessException;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
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

		// split path - path contains HIDs and maybe "wsdl" separated by "/"
		String path = request.getPathInfo();
		String parts[] = path.split("/", 5);
		// extract HIDs from path
		HID sHid = (parts[1].contentEquals("0")) ? nmCore.getHID() : new HID(parts[1]);
		HID rHid = new HID(parts[2]);

		// build parameter string
		StringBuilder queryBuilder = new StringBuilder();
		if (parts.length > 3 && parts[3].equals("wsdl")) {
			queryBuilder.append("wsdl");
		} else {
			if (parts.length > 3) {
				queryBuilder.append(parts[3]);
			}
			String originalQuery = request.getQueryString();
			if (originalQuery != null) {
				if (queryBuilder.length() > 0) {
					queryBuilder.append("&");
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
		sendRequest(sHid, rHid, requestBuilder.toString(), response);

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
		// split path - path contains HIDs and maybe "wsdl" separated by "/"
		String path = request.getPathInfo();
		StringTokenizer token = new StringTokenizer(path, "/");
		if (token.countTokens() > 2) {
			StringBuilder requestBuilder = new StringBuilder();
			String sHID = token.nextToken();
			HID senderHID = (sHID.contentEquals("0")) ? nmCore.getHID() : new HID(sHID);
			HID receiverHID = new HID(token.nextToken());
			// sessionID = new HID(token.nextToken());

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
			sendRequest(senderHID, receiverHID, requestBuilder.toString(), response);
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
	 * @param senderHid the sender HID
	 * @param receiverHid the receiver HID
	 * @param requestString the request message, as a String
	 * @param response the servlet response to add the response message to
	 * @throws IOException
	 */
	private void sendRequest(HID senderHid, HID receiverHid, String requestString,
			HttpServletResponse response) throws IOException {
		NMResponse r = this.nmCore.sendData(senderHid, receiverHid, requestString.getBytes(), true);
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
