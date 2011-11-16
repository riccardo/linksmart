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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.ComponentContext;


import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.networkmanager.impl.NetworkManagerCoreImpl;
import eu.linksmart.network.HID;

/**
 * NetworkManagerStatus Servlet
 */
public class GetNetworkManagerStatus extends HttpServlet {

	IdentityManager identityManager;
	
	private NetworkManagerCore networkManagerCore;
	
	/**
	 * Constructor
	 * 
	 * @param context the bundle's context
	 * @param nmServiceImpl the Network Manager Service implementation
	 */
	public GetNetworkManagerStatus(NetworkManagerCore networkManagerCore, IdentityManager identityManager) {
		
		this.networkManagerCore = networkManagerCore;
		
		this.identityManager = identityManager;
		
	}
	
	/**
	 * Performs the HTTP GET operation
	 * 
	 * @param request HttpServletRequest that encapsulates the request to the servlet 
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		
		Map params  = request.getParameterMap();
		if(params.containsKey("method")) {
			Object s = params.get("method");
			if (((String[]) params.get("method"))[0].equals("getNetworkManagers")) {
				Vector<String> hids = identityManager.getHIDs("NetworkManagerCore*");

				Iterator<String> it = hids.iterator();
				String endpoint;
				String description = "";
				String host;
				
				while (it.hasNext()) {
					HID HID = new HID(it.next());
					String hid = HID.toString();

					try {
						endpoint = identityManager.getHID(HID).getEndpoint();
						description = identityManager.getHID(HID).getDescription();
						if (description.equals("")) {
							description = "";
							String xmlAtributes = networkManagerCore.getInformationAssociatedWithHID(
								networkManagerCore.getHID().toString(), hid.toString());
							Properties attr = new Properties();
							attr.loadFromXML(new ByteArrayInputStream(
								xmlAtributes.getBytes()));
							Enumeration en = attr.keys();
							while (en.hasMoreElements()) {
								String key = (String) en.nextElement();
								if (key.equals("CN") || key.equals("DN")
										|| key.equals("C") || key.equals("Pseudonym")) {
									continue;
								}
								String value = attr.getProperty(key);
								description = description + key +" = " + value + ";";
							}
						}
						
						host = identityManager.getHID(HID).getIp();
						response.getWriter().write(hid + "|" + description
							+ "|" + host + "|" + endpoint);
						if (it.hasNext()) {
							response.getWriter().write("<br>");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else if (((String[]) params.get("method"))[0].equals("getLocalHids")) {
				Vector<String> hids = identityManager.getHostHIDs();
				Iterator<String> it = hids.iterator();
				String endpoint;
				String description;
				String host;
				
				while (it.hasNext()) {
					HID HID = new HID(it.next());
					String hid = HID.toString();
					
					try {
						endpoint = identityManager.getHID(HID).getEndpoint();
						description = identityManager.getHID(HID).getDescription();
						if (description.equals("")) {
							description = "";
							String xmlAtributes = identityManager.getInformationAssociatedWithHID(
								identityManager.getLocalNMHID(), hid.toString());
							Properties attr = new Properties();
							attr.loadFromXML(new ByteArrayInputStream(
								xmlAtributes.getBytes()));
							Enumeration en = attr.keys();
							while (en.hasMoreElements()) {
								String key = (String) en.nextElement();
								if (key.equals("CN") || key.equals("DN")
										|| key.equals("C") || key.equals("Pseudonym")) {
									continue;
								}
								String value = attr.getProperty(key);
								description = description + key +" = " + value + ";";
							}
						}
						host = identityManager.getHID(HID).getIp();
						response.getWriter().write(hid + "|" + description
							+ "|" + host + "|" + endpoint);
						if (it.hasNext()) {
							response.getWriter().write("<br>");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}				
			}
			else if (((String[]) params.get("method"))[0].equals("getRemoteHids")) {
				Vector<String> hids = identityManager.getHIDs();
				Iterator<String> it = hids.iterator();
				String endpoint;
				String description;
				String host;
				
				while (it.hasNext()) {
					HID HID = new HID(it.next());
					String hid = HID.toString();
					boolean out = identityManager.getHostHIDs().contains(hid);
					if (out) {
						continue;
					}
					
					try {
						endpoint = identityManager.getHID(HID).getEndpoint();
						description = identityManager.getHID(HID).getDescription();
						if (description.equals("")) {
							description = "HID entity not adapted to security issues";
						}
						host = identityManager.getHID(HID).getIp();
						response.getWriter().write(hid + "|" + description
							+ "|" + host + "|" + endpoint);
						if (it.hasNext()) {
							response.getWriter().write("<br>");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else if (((String[]) params.get("method"))[0].equals("getNetworkManagerSearch")) {
				Vector<String> hids = identityManager.getHIDs();
				Iterator<String> it = hids.iterator();
				String endpoint;
				String description;
				String host;
				
				while (it.hasNext()) {
					HID HID = new HID(it.next());
					String hid = HID.toString();
					
					try {
						endpoint = identityManager.getHID(HID).getEndpoint();
						description = identityManager.getHID(HID).getDescription();
						if (description.equals("")) {
							description = "HID entity not adapted to security issues";
							boolean in = identityManager.getHostHIDs().contains(hid);
							if (in) {
								description = "";
								String xmlAtributes = this.identityManager.getInformationAssociatedWithHID(
									identityManager.getLocalNMHID(), hid.toString());
								Properties attr = new Properties();
								attr.loadFromXML(new ByteArrayInputStream(
									xmlAtributes.getBytes()));
								Enumeration en = attr.keys();
								while (en.hasMoreElements()) {
									String key = (String) en.nextElement();
									if (key.equals("CN") || key.equals("DN")
											|| key.equals("C") || key.equals("Pseudonym")) {
										continue;
									}
									String value = attr.getProperty(key);
									description = description + key +" = " + value + ";";
								}
							}
						}
						host = identityManager.getHID(HID).getIp();
						response.getWriter().write(hid + "|" + description
							+ "|" + host + "|" + endpoint);
						if (it.hasNext()) {
							response.getWriter().write("<br>");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}	
			}
		}
	}
}
