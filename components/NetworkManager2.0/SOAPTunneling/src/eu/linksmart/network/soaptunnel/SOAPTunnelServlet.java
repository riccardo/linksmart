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
		
		String path = request.getPathInfo();
		String parts[] = path.split("/", 5);
		// XXX
		// if (parts.length != 4) {
		// return;
		// }

		HID sHid = new HID(parts[1]);
		HID rHid = new HID(parts[2]);
		String url = "";
		if (parts.length > 3 && parts[3].equals("wsdl")) {
			url = "?wsdl";
		} else {
			if (parts.length > 3) {
				url = parts[3];
			}
			// XXX Jonathan Work in Progress
			if (!request.getParameterMap().isEmpty()) {
				url += "?";
				@SuppressWarnings("unchecked")
				Set<Entry<String, String[]>> params = request.getParameterMap()
						.entrySet();
				for (Entry<String, String[]> e : params) {
					String[] values = e.getValue();
					if (values.length == 0) {
						url += e.getKey();
					} else {
						for (String s : values) {
							url += e.getKey() + "=" + s + "&";
						}
					}
				}
			}
		}

		String req = request.getMethod() + " /" + url + " "
				+ request.getProtocol() + "\r\n";
		Enumeration headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String header = (String) headerNames.nextElement();
			String value = request.getHeader(header);
			req = req.concat(header + ": " + value + "\r\n");
		}
		// TODO sendMessage?
		NMResponse r = this.nmCore.sendData(sHid, rHid, req.getBytes());
		String sResp = r.getData();

		// if
		// (r.getSessionID().equals(RouteManagerApplication.ACCESS_DENIED_SESSIONID))
		// throw new AccessException("Access Denied by Security Policies");

		response.setContentLength(sResp.getBytes().length);
		response.setContentType("text/xml");
		response.getWriter().write(sResp);
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

		String header;
		String value;
		String theSOAPAction = "";
		HID senderHID, receiverHID, sessionID;

		String path = request.getPathInfo();
		StringTokenizer token = new StringTokenizer(path, "/");
		String soapRequest = "";
		if (token.countTokens() > 2) {
			senderHID = new HID(token.nextToken());
			receiverHID = new HID(token.nextToken());
			sessionID = new HID(token.nextToken());

			Enumeration headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				header = (String) headerNames.nextElement();
				value = request.getHeader(header);
				soapRequest = soapRequest
						.concat(header + ": " + value + "\r\n");
				if (header.equalsIgnoreCase("SOAPAction")) {
					theSOAPAction = value.replaceAll("\"", "");
				}
			}
			soapRequest = soapRequest + "\r\n";

			String sResp = "";
			boolean accessDenied = false;

			if ((request.getContentLength() > 0)) {
				try {
					BufferedReader reader = request.getReader();
					for (String line = null; (line = reader.readLine()) != null;)
						soapRequest = soapRequest.concat(line);
					logger.debug("Sending soap request through tunnel: "
							+ soapRequest);
					// TODO sendMessage?
					NMResponse r = this.nmCore.sendData(senderHID, receiverHID,
							soapRequest.getBytes());
					sResp = r.getData();

					// if
					// (r.getSessionID().equals(RouteManagerApplication.ACCESS_DENIED_SESSIONID))
					// accessDenied = true;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/**
			 * If access has been denied, we throw an AccessException to report
			 * to the caller correctly
			 */
			if (accessDenied)
				throw new AccessException("Access Denied by Security Policies");

			response.setContentLength(sResp.getBytes().length);
			response.setContentType("text/xml");
			response.getWriter().write(sResp);
		}
	}

}
