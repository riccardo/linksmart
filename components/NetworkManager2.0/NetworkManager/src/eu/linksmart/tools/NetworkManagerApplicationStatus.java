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

package eu.linksmart.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.ComponentContext;


import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.HID;

/**
 * NetworkManagerApplication Servlet
 */
public class NetworkManagerApplicationStatus extends HttpServlet {

	IdentityManager identityManager;
	ComponentContext context;
	NetworkManagerCore nmCore;
	
	/**
	 * Constructor
	 * 
	 * @param context the bundle's context
	 * @param networkManagerCore the Network Manager Service implementation
	 */
	public NetworkManagerApplicationStatus(ComponentContext context, 
			NetworkManagerCore networkManager, IdentityManager identityManager) {
		
		this.identityManager =  identityManager;
		this.context = context;
	}
	
	/**
	 * Performs the HTTP GET operation
	 * 
	 * @param request HttpServletRequest that encapsulates the request to the servlet 
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		
		response.setContentType("text/html");
		URL cssFile = context.getBundleContext().getBundle().getResource(
			"resources/NetworkManagerCore.css");
		BufferedReader cssReader = new BufferedReader(new InputStreamReader(
			cssFile.openStream()));
		
		String temp;
		String css = "";
		while ((temp = cssReader.readLine()) != null) {
			css = css + temp;
		}
		cssReader.close();
		
		response.getWriter().println("<html><head>"
			+ "<style type=\"text/css\">" + css +"</style></head>");
		response.getWriter().println("<body><table><tr>"
			+ "<td valign=\"middle\" width=80%><h1>"
			+ "Status page for the local Network Manager</h1></td>" 
			+ "<td align=\"right\" width=20%>"
			+ "<img src=\"files/0.gif\" /></td></tr></table>");
		response.getWriter().println("<h1>" + "Total Number of HIDs in the "
			+ "LinkSmart Network: " + identityManager.getHIDs().size() + "</h1>");
		
		/* Print Network Managers Discovered. */
		response.getWriter().println("<table border=1 class=\"stats\" "
			+ "width=100%><tr><td class=\"hed\" width=25%>NETWORK MANAGERS</td>"
			+ "<td class=\"hed\" width=25%>DESCRIPTION</td><td class=\"hed\" "
			+ "width=25%>HOST</td><td class=\"hed\" width=25%>ENDPOINT</td></tr>");
		
		String s = "";
		Vector<String> hids = identityManager.getHIDsbyDescription("NetworkManagerCore*");
		Iterator<String> it = hids.iterator();
		String endpoint;
		String description;
		String ip;
		
		while (it.hasNext()) {
			HID hid = new HID(it.next());
			
			try {
				endpoint = identityManager.getHIDInfo(hid).getEndpoint();
				if (endpoint.equals(" ")) {
					endpoint = "-";
				}
				description = identityManager.getHIDInfo(hid).getDescription();
				ip = identityManager.getHIDInfo(hid).getIp();
				s = s + "<tr><td width=25%>" + hid.toString()
					+ "</td><td width=25%>" + description
					+ "</td><td width=25%>" + ip
					+ "</td><td width=25%>" + endpoint + "</td></tr>";
			}
			catch (Exception e) {
				System.out.println("Error printing " + hid);
			}
		}
		response.getWriter().println(s);						
		response.getWriter().println("</table>");
		
		/* Print Local HIDs. */
		response.getWriter().println("<table border=1 class=\"stats\" width=100%>"
			+ "<tr><td class=\"hed\" width=25%>LOCALHIDS</td>"
			+ "<td class=\"hed\" width=25%>DESCRIPTION</td>"
			+ "<td class=\"hed\" width=25%>HOST</td>"
			+ "<td class=\"hed\" width=25%>ENDPOINT</td></tr>");
		s = "";
		hids = identityManager.getHostHIDs();
		it = hids.iterator();
		
		while (it.hasNext()) {
			HID hid = new HID(it.next());
			try {
				endpoint = identityManager.getHIDInfo(hid).getEndpoint();
				if (endpoint.equals(" ")) {
					endpoint = "-";
				}
				description = identityManager.getHIDInfo(hid).getDescription();
				
				if (description.equals("")) {
					String xmlAtributes = this.identityManager.getInformationAssociatedWithHID(
						identityManager.getLocalNMHID(), hid.toString());
					Properties attr = new Properties();
					attr.loadFromXML(new ByteArrayInputStream(xmlAtributes.getBytes()));
					Enumeration en = attr.keys();
					while (en.hasMoreElements()) {
						String key = (String) en.nextElement();
						if (key.equals("CN") || key.equals("DN")
								|| key.equals("C") || key.equals("Pseudonym")) {
							continue;
						}
						String value = attr.getProperty(key);
						description = description + key + " = " + value + "<br/>";
					}
				}
				ip = identityManager.getHIDInfo(hid).getIp();
				s = s + "<tr><td width=25%>" + hid.toString()
					+ "</td><td width=25%>" + description
					+ "</td><td width=25%>" + ip
					+ "</td><td width=25%>" + endpoint + "</td></tr>";
			} catch (Exception e) {
				System.out.println("Error printing " + hid);
			}
		}
		response.getWriter().println(s);						
		response.getWriter().println("</table>");
		
		/* Print All HIDs. */
		response.getWriter().println("<table border=1 class=\"stats\" width=100%>"
			+ "<tr><td class=\"hed\" width=25%>HID</td>"
			+ "<td class=\"hed\" width=25%>DESCRIPTION</td>"
			+ "<td class=\"hed\" width=25%>HOST</td>"
			+ "<td class=\"hed\" width=25%>ENDPOINT</td></tr>");
		s = "";
		hids = identityManager.getHIDs();
		it = hids.iterator();
		
		while (it.hasNext()) {
			HID hid = new HID(it.next());
			try {
				endpoint = identityManager.getHIDInfo(hid).getEndpoint();
				if (endpoint == " ") {
					endpoint = "-";
				}
				description = identityManager.getHIDInfo(hid).getDescription();
				ip = identityManager.getHIDInfo(hid).getIp();
				s = s + "<tr><td width=25%>" + hid.toString() 
					+ "</td><td width=25%>" + description
					+ "</td><td width=25%>" + ip
					+ "</td><td width=25%>" + endpoint + "</td></tr>";
			}
			catch (Exception e) {
				System.out.println("Error printing " + hid);
			}
		}
		response.getWriter().println(s);
		response.getWriter().println("</table></body></html>");
	}

}
