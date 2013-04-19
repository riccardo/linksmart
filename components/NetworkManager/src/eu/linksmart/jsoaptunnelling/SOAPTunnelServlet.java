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

package eu.linksmart.jsoaptunnelling;

import java.io.BufferedReader;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.NetworkManagerApplication;
import eu.linksmart.network.routing.RouteManagerApplication;


/**
 * SOAP Tunnel servlet
 */
public class SOAPTunnelServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SOAPTunnelServlet.class.getName());
	private NetworkManagerApplication nm;

	/**
	 * Constructor with parameters
	 * 
	 * @param nm the Network Manager application
	 */
	public SOAPTunnelServlet(NetworkManagerApplication nm) {
		this.nm = nm;
	}
	
	private void processDatalessRequest(HttpServletRequest request, HttpServletResponse response)
	throws IOException, RemoteException, AccessException {		
		String path = request.getPathInfo();
		String parts[] = path.split("/",5);
		if (parts.length != 4) {
			return;
		}

		String senderHID = parts[1];
		String receiverHID = parts[2];
		String url = parts[3];
		if(request.getQueryString() != null && request.getQueryString().length() != 0) {
			url = url.concat("?"+request.getQueryString());
		}
		String req = request.getMethod() + " /" + url + " " + request.getProtocol() + "\r\n";
		
		Enumeration headerNames = request.getHeaderNames();

		while(headerNames.hasMoreElements()) {
			String header = (String) headerNames.nextElement();
			String value = request.getHeader(header);
			req = req.concat(header + ": " + value + "\r\n");
		}

		NMResponse r = nm.sendData("0", senderHID, receiverHID, req);
		String sResp = r.getData();

		if (r.getSessionID().equals(RouteManagerApplication.ACCESS_DENIED_SESSIONID))
			throw new AccessException("Access Denied by Security Policies");

		String body = new String();
		//check response status and if error response BAD_GATEWAY else parse response for client
		if (!sResp.startsWith("HTTP/1.1 200 OK")) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			//set whole response data as body
			body = sResp;
		} else {		
			//take headers from data and add them to response
			String[] headers = sResp.split("\r\n");
			//use it to get index of data element
			int i = 0;
			//go through headers and put them to response until empty line is reached
			for (String header : headers) {	
				if(header.contentEquals("")) {
					break;
				}
				if(!header.toLowerCase().startsWith("http")) {
					String[] headerParts = header.split(":");
					if(headerParts.length == 2) {
						response.setHeader(headerParts[0], headerParts[1]);
					}
				}
				i++;
			}
			//concat remaining elements of 'headers' array (the real data) into response body
			for(i++;i < headers.length;i++) {
				body = body.concat(headers[i]);
			}
		}
		//write body data
		response.setContentLength(body.getBytes().length);
		response.getWriter().write(body);
	}
	
	/**
	 * Performs the HTTP DELETE operation
	 * Is identical to GET
	 * @param request HttpServletRequest that encapsulates the request to the servlet 
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */
	public void doDelete(HttpServletRequest request, HttpServletResponse response) 
	throws IOException {
		processDatalessRequest(request, response);
	}

	/**
	 * Performs the HTTP GET operation
	 * 
	 * @param request HttpServletRequest that encapsulates the request to the servlet 
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws IOException {
		processDatalessRequest(request, response);
	}

	/**
	 * Performs the HTTP POST operation
	 * 
	 * @param request HttpServletRequest that encapsulates the request to the servlet
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */
	public void doPut(HttpServletRequest request, HttpServletResponse response) 
	throws IOException {

		String header;
		String value;
		String theSOAPAction = "";
		String senderHID, receiverHID, sessionID;

		String path = request.getPathInfo();
		StringTokenizer token = new StringTokenizer(path, "/");
		String SOAPRequest = "";
		if (token.countTokens() > 2) {
			senderHID = token.nextToken();
			receiverHID = token.nextToken();
			sessionID = token.nextToken();

			Enumeration headerNames = request.getHeaderNames();
			while(headerNames.hasMoreElements()) {
				header = (String) headerNames.nextElement();
				value = request.getHeader(header);
				SOAPRequest = SOAPRequest.concat(header + ": " + value + "\r\n");				
				if(header.equalsIgnoreCase("SOAPAction")) {
					theSOAPAction = value.replaceAll("\"", "");
				}
			}
			SOAPRequest = SOAPRequest + "\r\n";

			String sResp = "";
			boolean accessDenied = false;

			if((request.getContentLength() > 0)) {
				try {
					BufferedReader reader = request.getReader();
					for(String line = null; (line = reader.readLine()) != null;)
						SOAPRequest = SOAPRequest.concat(line);
					logger.debug("Sending soap request through tunnel: " + SOAPRequest);
					NMResponse r = nm.sendData(sessionID, senderHID, receiverHID, SOAPRequest);
					sResp = r.getData();

					if (r.getSessionID().equals(RouteManagerApplication.ACCESS_DENIED_SESSIONID))
						accessDenied = true;

				} catch(Exception e) {
					e.printStackTrace();
				}
			}

			/** 
			 * If access has been denied, we throw an AccessException to
			 * report to the caller correctly
			 */
			if (accessDenied)
				throw new AccessException("Access Denied by Security Policies");

			response.setContentLength(sResp.getBytes().length);
			response.setContentType("text/xml");
			response.getWriter().write(sResp);
		}
	}
	
	/**
	 * Performs the HTTP POST operation
	 * 
	 * @param request HttpServletRequest that encapsulates the request to the servlet
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws IOException {

		String header;
		String value;
		String theSOAPAction = "";
		String senderHID, receiverHID, sessionID;

		String path = request.getPathInfo();
		StringTokenizer token = new StringTokenizer(path, "/");
		String SOAPRequest = "";
		if (token.countTokens() > 2) {
			senderHID = token.nextToken();
			receiverHID = token.nextToken();
			sessionID = token.nextToken();

			Enumeration headerNames = request.getHeaderNames();
			while(headerNames.hasMoreElements()) {
				header = (String) headerNames.nextElement();
				value = request.getHeader(header);
				SOAPRequest = SOAPRequest.concat(header + ": " + value + "\r\n");				
				if(header.equalsIgnoreCase("SOAPAction")) {
					theSOAPAction = value.replaceAll("\"", "");
				}
			}
			SOAPRequest = SOAPRequest + "\r\n";

			String sResp = "";
			boolean accessDenied = false;

			if((request.getContentLength() > 0)) {
				try {
					BufferedReader reader = request.getReader();
					for(String line = null; (line = reader.readLine()) != null;)
						SOAPRequest = SOAPRequest.concat(line);
					logger.debug("Sending soap request through tunnel: " + SOAPRequest);
					NMResponse r = nm.sendData(sessionID, senderHID, receiverHID, SOAPRequest);
					sResp = r.getData();

					if (r.getSessionID().equals(RouteManagerApplication.ACCESS_DENIED_SESSIONID))
						accessDenied = true;

				} catch(Exception e) {
					e.printStackTrace();
				}
			}

			/** 
			 * If access has been denied, we throw an AccessException to
			 * report to the caller correctly
			 */
			if (accessDenied)
				throw new AccessException("Access Denied by Security Policies");

			response.setContentLength(sResp.getBytes().length);
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(sResp);
		}
	}

}
