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

package eu.linksmart.limbo.cookiedevice.client;

import java.io.IOException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.JDOMException;

import eu.linksmart.limbo.sm.networkmanager.client.NetworkManagerApplicationLimboClientPortImpl;
import eu.linksmart.storage.helper.DictionaryResponse;
import eu.linksmart.storage.helper.ErrorCodes;
import eu.linksmart.storage.helper.StringResponse;
import eu.linksmart.storage.helper.StringVectorResponse;
import eu.linksmart.storage.helper.VoidResponse;

public class LinkSmartCDConnector {
	
	private CookieLimboClientPortImpl theClient;
	
	private NetworkManagerApplicationLimboClientPortImpl nmClient;

	private Collection<String> hids;

	private String senderHID;

	private String nmAdress;

	private String soapAdress;

	private static String defaultNmAdress = null;

	private static String defaultSoapAdress = null;

	private static String defaultSenderHID = "0";
	
	private boolean useLinkSmart;
	
	private String id;
	
	public LinkSmartCDConnector(String id, String nmAdress, String soapAdress,
			String senderHID) {
		this.id = id;
		this.nmAdress = nmAdress;
		this.soapAdress = soapAdress;
		this.senderHID = senderHID;
		nmClient = new NetworkManagerApplicationLimboClientPortImpl(nmAdress);
		if (id.startsWith("http://")) {
			useLinkSmart = false;
			theClient = new CookieLimboClientPortImpl(new TCPProtocol(), id);
		} else {
			useLinkSmart = true;
			discoverDevice();
		}
	}
	
	public LinkSmartCDConnector(String id) {
		this(id, defaultNmAdress, defaultSoapAdress, defaultSenderHID);
		//theClient = new CookieLimboClientPortImpl(new TCPProtocol(), wsAddress); 
	}
	
	private CookieLimboClientPortImpl findConnection() {
		if (useLinkSmart) {
			//logger.debug("LinkSmartFSCDConnection.findConnection for dev " + id);
			for (String hid : hids) {
				String url = soapAdress + "/" + senderHID + "/" + hid + "/0/";
				//logger.info("trying " + url);
				CookieLimboClientPortImpl client = new CookieLimboClientPortImpl(new TCPProtocol(), 
						url);
				if (client.getCookieNames() != null) {
					return client;
				}
				//logger.error("no success");
			}
			discoverDevice();
			for (String hid : hids) {
				String url = soapAdress + "/" + senderHID + "/" + hid + "/0/";
				//logger.info("trying " + url);
				CookieLimboClientPortImpl client = new CookieLimboClientPortImpl(new TCPProtocol(), 
						url);
				if (client.getCookieNames() != null) {
					return client;
				}
				//logger.info("no success");
			}
			//logger.info("No connection available.");
			return null;
		} else {
			//if (connection.getID() == null)
			//	return null;
			return theClient;
		}
	}
	
	private void discoverDevice() {
		//logger.info("Discover " + id);
		Vector<String> result = new Vector<String>();
		String hids = nmClient
				.getHIDsbyDescriptionAsString("CookieDevice::*::" + id
						+ "::StaticWS");
		String[] array = hids.split(" ");
		for (int i = 0; i < array.length; i++) {
			result.add(array[i]);
		}
		this.hids = result;
		//logger.info("Found " + this.hids.size() + " HIDs");
		//for (String id : this.hids) {
		//	logger.debug("    " + id);
		//}
		theClient = null;
	}
	
	public int getEndpoints() {
		return hids.size();
	}

	public void rediscoverDevice() {
		if (useLinkSmart)
			discoverDevice();
	}
	
	public String getCookieDeviceID() {
		return id;
	}
	
	public NetworkManagerApplicationLimboClientPortImpl getNmClient() {
		return nmClient;
	}

	public void setNmClient(
			NetworkManagerApplicationLimboClientPortImpl nmClient) {
		this.nmClient = nmClient;
	}

	public String getSenderHID() {
		return senderHID;
	}

	public void setSenderHID(String senderHID) {
		this.senderHID = senderHID;
	}

	public String getSoapAdress() {
		return soapAdress;
	}

	public void setSoapAdress(String soapAdress) {
		this.soapAdress = soapAdress;
	}

	public static String getDefaultNmAdress() {
		return defaultNmAdress;
	}

	public static void setDefaultNmAdress(String defaultNmAdress) {
		LinkSmartCDConnector.defaultNmAdress = defaultNmAdress;
	}

	public static String getDefaultSoapAdress() {
		return defaultSoapAdress;
	}

	public static void setDefaultSoapAdress(String defaultSoapAdress) {
		LinkSmartCDConnector.defaultSoapAdress = defaultSoapAdress;
	}

	public static String getDefaultSenderHID() {
		return defaultSenderHID;
	}

	public static void setDefaultSenderHID(String defaultSenderHID) {
		LinkSmartCDConnector.defaultSenderHID = defaultSenderHID;
	}

	public StringVectorResponse getCookieNames() {
		if (theClient == null) {
			theClient = findConnection();
		}
		if (theClient == null) {
			return null;
		}
		String response = theClient.getCookieNames();
		while (response == null) {
			theClient = findConnection();
			if (theClient == null)
				return null;
			response = theClient.getCookieNames();
		}
		try {
			return new StringVectorResponse(StringEscapeUtils.unescapeXml(response));
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public DictionaryResponse getAllData() {
		return getAllData(".*");
	}
	
	public DictionaryResponse getAllData(String regularExpression) {
		if (theClient == null) {
			theClient = findConnection();
		}
		if (theClient == null) {
			return null;
		}
		regularExpression = StringEscapeUtils.unescapeXml(regularExpression);
		String response = theClient.getAllData(regularExpression);
		while (response == null) {
			theClient = findConnection();
			if (theClient == null)
				return null;
			response = theClient.getAllData(regularExpression);
		}
		try {
			return new DictionaryResponse(StringEscapeUtils.unescapeXml(response));
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public StringResponse getCookieValue(String cookieName) {
		if (theClient == null) {
			theClient = findConnection();
		}
		if (theClient == null) {
			return null;
		}
		cookieName = StringEscapeUtils.escapeXml(cookieName);
		String response = theClient.getCookieValue(cookieName);
		while (response == null) {
			theClient = findConnection();
			if (theClient == null)
				return null;
			response = theClient.getCookieValue(cookieName);
		}
		try {
			return new StringResponse(StringEscapeUtils.unescapeXml(response));
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public VoidResponse setCookieValue(String cookieName, String cookieValue) {
		if (theClient == null) {
			theClient = findConnection();
		}
		if (theClient == null) {
			return null;
		}
		cookieName = StringEscapeUtils.escapeXml(cookieName);
		cookieValue = StringEscapeUtils.escapeXml(cookieValue);
		String response = theClient.setCookieValue(cookieName, cookieValue);
		while (response == null) {
			theClient = findConnection();
			if (theClient == null)
				return null;
			response = theClient.setCookieValue(cookieName, cookieValue);
		}
		try {
			return new VoidResponse(StringEscapeUtils.unescapeXml(response));
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public VoidResponse removeEntry(String cookieName) {
		if (theClient == null) {
			theClient = findConnection();
		}
		if (theClient == null) {
			return null;
		}
		cookieName = StringEscapeUtils.escapeXml(cookieName);
		String response = theClient.removeEntry(cookieName);
		while (response == null) {
			theClient = findConnection();
			if (theClient == null)
				return null;
			response = theClient.removeEntry(cookieName);
		}
		try {
			return new VoidResponse(StringEscapeUtils.unescapeXml(response));
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
