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
 * Copyright (C) 2006-2010 [University of Paderborn]
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

package eu.linksmart.limbo.filesystemdevice;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.linksmart.limbo.filesystemdevice.upnp.FileSystemDeviceDevice;

public class FileSystemDeviceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String location;
	private String clientHost;
	private FileSystemDeviceService service;

	public FileSystemDeviceServlet(String serverHost, int port, String path,
			FileSystemDeviceDevice device) {
		super();
		try {
			location = "http://" + InetAddress.getLocalHost().getHostAddress()
					+ ":" + port + path;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		service = new FileSystemDeviceService(serverHost, port, device);

	}

	public String getEndpoint() {
		return location;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html");
		response.getWriter().println(
				"<h1>" + this.getClass().getName() + "</h1>");
	}

	// TODO removed time measurements
	//Measurement gaging = Measurement.getMeasurementInstance("C:\\LoggerOutput\\log.txt");

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		//TODO remove print
		//System.out.println("doPost() called");
		
		//measure
		//assert gaging.startMeasurement();
	
		String requestAsString = "";
		String responseAsString = "";
		String header = "";
		String value = "";
		this.clientHost = request.getRemoteAddr();
		if (this.clientHost.equals("127.0.0.1")) {
			try {
				this.clientHost = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		try {

			requestAsString = requestAsString.concat(request.getMethod()
					+ " / " + request.getProtocol() + "\r\n");

			Enumeration headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				header = (String) headerNames.nextElement();
				value = request.getHeader(header);
				requestAsString = requestAsString.concat(header + ": " + value
						+ "\r\n");
			}
			requestAsString = requestAsString.concat("\r\n");
			BufferedReader reader = request.getReader();
			for (String line = null; (line = reader.readLine()) != null;)
				requestAsString = requestAsString.concat(line);

			responseAsString = service.handleRequest(requestAsString,
					this.clientHost);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		response.setContentLength(responseAsString.getBytes().length);
		response.getWriter().write(responseAsString);
		
		//TODO remove time gaging
		// Stop timing
		//assert gaging.stopMeasurement();
		// Write results to file.
		//assert gaging.logTimeResults(this.toString());
		
	}
	
}