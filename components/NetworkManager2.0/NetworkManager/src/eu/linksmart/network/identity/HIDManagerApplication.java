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
 * The identity manager class holds the identity Table (idTable) containing the
 * known HIDs and the corresponding endpoint addresses for contacting other 
 * Network Managers.
 * It offers six interfaces to the Network Manager:
 * - createHID
 * - removeHID and removeAllHID
 * - renewHID
 * - addContextHID
 * - getHostHIDs
 * - getHIDsFromIP
 * It also offers interfaces to the Backbone Manager to update the idTable with 
 * the information from the Network
 * 
 * @see eu.linksmart.types.HID
 */

package eu.linksmart.network.identity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.jxta.peer.PeerID;

import org.apache.log4j.Logger;


import eu.linksmart.network.HID;
import eu.linksmart.network.NetworkManagerApplication;
import eu.linksmart.network.impl.NetworkManagerApplicationSoapBindingImpl;
import eu.linksmart.network.impl.NetworkManagerConfigurator;

/**
 * The HID Manager
 */
public class HIDManagerApplication {
	
	private ConcurrentHashMap<PeerID, ConcurrentHashMap<HID, HIDInfo>> idTable;
	private String myIP;
	private int myPort;
	private PeerID myID;
	public static int maxHIDsAllowed;
	private static Logger logger = Logger.getLogger(HIDManagerApplication.class.getName());
	public ConcurrentLinkedQueue<String> queue;
	private String myHID;
	private NetworkManagerApplicationSoapBindingImpl nm;

	/**
	 * Constructor
	 * 
	 * @param nm the Network Manager application
	 */
	public HIDManagerApplication(NetworkManagerApplicationSoapBindingImpl nm) {  
		this.nm = nm;
		this.idTable = new ConcurrentHashMap<PeerID, ConcurrentHashMap<HID, HIDInfo>>();
		this.queue = new ConcurrentLinkedQueue<String>();
		InetAddress i;
		
		try {
			i = InetAddress.getLocalHost();
			this.myIP = i.getHostAddress();
			this.myPort = Integer.parseInt(System.getProperty("org.osgi.service.http.port"));
		} catch (UnknownHostException e) {
			logger.error("No host could be found. Please check the network connections...");
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets JXTA ID
	 * 
	 * @param id the id
	 * @param hid the LinkSmart ID
	 * @param myNMDescription the Network Manager description
	 * @param endpoint the endpoint
	 * @param myIP the IP
	 * @param port the port
	 * @param renewCert the certificate
	 * @return true or false depending on the result
	 */
	public boolean setJXTAID(PeerID id, String hid, String myNMDescription,
			String endpoint, String myIP, String port, boolean renewCert) {
		
		/* First, remove the old HID. */
		if ((this.myHID != null)) {
			HID oldhid = new HID(this.myHID);
			if ((idTable.get(myID).containsKey(oldhid))) {
				System.out.println("In");
				idTable.get(myID).remove(oldhid);
				queue.add("D;"+ this.myHID);
			}
		}
		
		this.myID = id;
		if (!idTable.containsKey(id)) {
			idTable.put(id, new ConcurrentHashMap<HID, HIDInfo>());
		}
		
		this.myHID = hid;
		HID nmhid = new HID(hid);
		/* Check if the cryptomgr is null. */
		if (nm.cryptoManager == null) {
			return false;
		}
		
		logger.info("Creating crypto HID");
		String certRef = (String) nm.getConfiguration().get(
			NetworkManagerConfigurator.CERTIFICATE_REF);
		
		Properties attr = null;
		if ((certRef == null) || (certRef.equals("")) || renewCert) {
			/* No certificate reference, will provide a new certificate. */
			attr = new Properties();
			try {
				attr.put("Desc", myNMDescription);
				attr.put("PID", InetAddress.getLocalHost().getHostName());
				attr.put("SID", "NetworkManagerApplication");
			} catch (UnknownHostException e) {
				logger.error(e.getMessage(), e);
			}
			
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				attr.storeToXML(b, "");
				certRef = nm.cryptoManager.generateCertificateWithAttributes(
					new String(b.toByteArray()), hid);
				nm.setConfiguration(
					NetworkManagerConfigurator.CERTIFICATE_REF, certRef);
			} catch (InvalidKeyException e) {
				logger.error(e.getMessage(), e);
				return false;
			} catch (NoSuchAlgorithmException e) {
				logger.error(e.getMessage(), e);
				return false;
			} catch (KeyStoreException e) {
				logger.error(e.getMessage(), e);
				return false;
			} catch (CertificateException e) {
				logger.error(e.getMessage(), e);
			} catch (SecurityException e) {
				logger.error(e.getMessage(), e);
				return false;
			} catch (SignatureException e) {
				logger.error(e.getMessage(), e);
				return false;
			} catch (IllegalStateException e) {
				logger.error(e.getMessage(), e);
				return false;
			} catch (NoSuchProviderException e) {
				logger.error(e.getMessage(), e);
				return false;
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return false;
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return false;
			}			
		}
		else {
			/* Existing certificate reference. Will get Attributes from it. */
			try {
				attr = nm.cryptoManager.getAttributesFromCertificate(certRef);
				if ((!attr.get("Desc").equals(myNMDescription))
						|| (!attr.get("PID").equals(InetAddress.getLocalHost().getHostName()))) {
					/* The attributes are not the same so I will create a new certificate. */
					attr.put("Desc", myNMDescription);
					attr.put("PID", InetAddress.getLocalHost().getHostName());
					ByteArrayOutputStream b = new ByteArrayOutputStream();
					attr.storeToXML(b, "");
					certRef = nm.cryptoManager.generateCertificateWithAttributes(
						new String(b.toByteArray()), hid);
					nm.setConfiguration(NetworkManagerConfigurator.CERTIFICATE_REF, certRef);
				}
				else {
					nm.cryptoManager.addPrivateKeyForHID(hid, certRef);
				}
			} catch (Exception e) {
				/* Error getting cert. Will generate a new one. */
				logger.error(e.getMessage(), e);
				try {
					ByteArrayOutputStream b = new ByteArrayOutputStream();
					attr = new Properties();
					attr.put("Desc", myNMDescription);
					attr.put("PID", InetAddress.getLocalHost().getHostName());
					attr.storeToXML(b, "");
					certRef = nm.cryptoManager.generateCertificateWithAttributes(
						new String(b.toByteArray()), hid);
					nm.setConfiguration(NetworkManagerConfigurator.CERTIFICATE_REF, certRef);
				} catch (Exception ee) {
					logger.error(ee.getMessage(), ee);
				}
				
				try {
					certRef = nm.cryptoManager.generateCertificateWithAttributes(
						attr.toString(), hid);
					nm.setConfiguration(NetworkManagerConfigurator.CERTIFICATE_REF, certRef);
				} catch (Exception ee) {
					logger.error(ee.getMessage(), ee);
					return false;
				}
			}			
		}
		
		addHID(id, nmhid, new HIDInfo(myIP.split(":")[0],
			Integer.parseInt(port), myNMDescription, endpoint, id, attr));
		
		if (renewCert) {
			queue.add("A;" + nmhid + ";" + myNMDescription);
		}
		return true;
	}

	/**
	 * Gets local Network Manager HID
	 * 
	 * @return the local Network Manager HID
	 */
	public String getLocalNMHID() {
		return this.myHID;
	}
	
	/**
	 * Creates an HID from a given contextID and adds it to idTable. If the HID 
	 * can't be created, it returns a null HID (0.0.0.0)
	 * 
	 * @param contextID The desired contextID
	 * @param level The desired level
	 * @param description the description
	 * @param endpoint the endpoint
	 * @param attr the properties associated to this HID
	 * @return The HID created or null HID (0.0.0.0) if it could not be created
	 */
	public HID createHID(long contextID, int level, String description,
			String endpoint, Properties attr) {
		
		Random rnd = new Random();
		HID hid = createHIDwithNewDeviceID();
		
		HIDInfo info = new HIDInfo(myIP, myPort, description, endpoint, myID, attr);
		long rndContext;
		switch (level) {
			case 1:
				hid.contextID1 = contextID;
				hid.level = 1;
				addHID(myID, hid, info);
				break;
			case 2:	
				rndContext = Math.abs(rnd.nextLong());
				while (rndContext == 0) {
					rndContext = Math.abs(rnd.nextLong());
				}
				hid.contextID1 = rndContext;
				hid.contextID2 = contextID;
				hid.level = 2;
				addHID(myID, hid, info);
				break;
			case 3:
				rndContext = Math.abs(rnd.nextLong());
				while (rndContext == 0) {
					rndContext = Math.abs(rnd.nextLong());
				}
				hid.contextID1 = rndContext;
				rndContext = Math.abs(rnd.nextLong());
				while (rndContext == 0) {
					rndContext = Math.abs(rnd.nextLong());
					}
				hid.contextID2 = rndContext;
				hid.contextID3 = contextID;
				hid.level = 3;
				addHID(myID, hid, info);
				break;
			default: 
				logger.error("Invalid level when creating a new HID");
				hid.deviceID = 0;
				hid.level = 0;
		}
		
		logger.debug("Created HID: " + hid.toString());
		queue.add("A;"+ hid + ";" + info.getDescription());
		return hid;
	}

	private HID createHIDwithNewDeviceID() {
		HID hid = new HID();
		while((this.existsDeviceID(hid.getDeviceID()) || (hid.getDeviceID() == 0))) {
			hid = new HID();
		}
		return hid;
	} 

	/**
	 * Creates an HID from a given contextID and adds it to idTable. If the HID 
	 * can't be created, it returns a null HID (0.0.0.0)
	 * 
	 * @param contextID The desired contextID
	 * @param level The desired level
	 * @return the HID created or null HID (0.0.0.0) if it could not be created
	 */
	public HID createHID(long contextID, int level) {
		return createHID(contextID, level, " ", " ", null);
	}
	
	/**
	 * Creates an HID without any context and adds it to idTable. It creates a 
	 * random deviceID. The result will be 0.0.0.randomDevID
	 * 
	 * @param description the description
	 * @param endpoint the endpoint
	 * @return the HID created
	 */
	public HID createHID(String description, String endpoint) {
		HIDInfo info = new HIDInfo(myIP, myPort, description, endpoint, myID);
		HID hid = createHIDwithNewDeviceID();
		addHID(myID, hid, info);
		queue.add("A;" + hid + ";" + info.getDescription());
		logger.debug("Created HID: " + hid.toString() + " for " + endpoint);		
		return hid;
	} 
	
	/**
	 * Creates an empty HID
	 * 
	 * @return the HID created or null HID (0.0.0.0) if it could not be created
	 */
	public HID createHID() {
		return createHID(" ", " ");
	}
	
	/**
	 * Creates an HID without any context and adds it to idTable. It creates a 
	 * random deviceID. The result will be 0.0.0.randomDevID
	 * 
	 * @param description the description
	 * @param endpoint the endpoint
	 * @param attr the properties associated to this HID
	 * @return The HID created or null HID (0.0.0.0) if it could not be created
	 */
	public HID createHID(String description, String endpoint, Properties attr) {
		HIDInfo info = new HIDInfo(myIP, myPort, description, endpoint, myID, attr);
		HID hid = createHIDwithNewDeviceID();
		addHID(myID, hid, info);
		queue.add("A;" + hid + ";" + info.getDescription());
		logger.debug("Created HID " + hid.toString() + " for " + endpoint);
		return hid;
	}	
	
	/**
	 * Adds a local HID to the IdTable
	 * 
	 * @param id the peer id
	 * @param hid The HID to be added
	 * @param info the HIDInfo
	 * @return The previous value associated with that HID, null otherwise
	 */
	public  HIDInfo addHID(PeerID id, HID hid, HIDInfo info) {
		if (!idTable.containsKey(id)) {
			idTable.put(id, new ConcurrentHashMap<HID, HIDInfo>());
		}
		return idTable.get(id).put(hid, info);
	}
	
	/**
	 * Removes a local HID from the IdTable
	 * 
	 * @param hid The HID to be removed
	 * @return the result
	 */
	public String removeHID(HID hid) {
		queue.add("D;" + hid);
		return idTable.get(myID).remove(hid).toString();
	}	
	
	/**
	 * Clears update queue
	 */
	public void clearUpdateQueue() {
		queue.clear();
	}
	
	/**
	 * Removes every HID from the IdTable
	 */
	public  void removeAllHIDs() {
		idTable.clear();
	}	
		
	/**
	 * Gets the endpoint address from an HID from idTable
	 * 
	 * @param hid The HID for getting the endpoint associated
	 * @return The endpoint address associated
	 */
	public  HIDInfo getHID(HID hid) {
		Enumeration<ConcurrentHashMap<HID, HIDInfo>> tableEnumeration = idTable.elements();
		while (tableEnumeration.hasMoreElements()) {
			ConcurrentHashMap<HID, HIDInfo> table = tableEnumeration.nextElement();
			if (table.containsKey(hid)) {
				return table.get(hid);
			}
		}
		return null;
	}

	/**
	 * Renews the context of an existing HID. It removes the previous HID from 
	 * the IdTable and adds the updated HID with the new values
	 * 
	 * @param contextID New context to be added
	 * @param level Level of the contextID to be modified
	 * @param hid HID to be changed
	 * @return The new HID with the contextID and level specified
	 */
	public  HID renewHID(long contextID, int level, HID hid) {		
		HIDInfo info = getHID(hid);
		if (info.getIp() != null) {
			removeHID(hid);
			hid.setContext(contextID, level);
			addHID(myID, hid, info);
			return hid;
		}
		else {
			logger.error("HID " + hid.toString() + " doesn't exists in "
				+ "IDTable. Imposible to execute renew. Please add it to IdTAble");
			return null;
		}
	}

	/**
	 * Adds a new contextID to an existing HID. The level of the HID is 
	 * increased by 1. The idTable is also updated
	 * 
	 * @param contextID The new contextID to be added
	 * @param hid The HID to be changed
	 * @return The HID with new contextID
	 */
	public  HID addContext(long contextID, HID hid) {
		HIDInfo info = getHID(hid);		
		HID newHID = new HID(hid);
		if (newHID.level < 3) {
			newHID.setContext(contextID, newHID.level + 1);
			newHID.level = newHID.level + 1;
			if (info.getIp() != null) {
				addHID(myID, newHID, info);
			}
			else {
				addHID(myID, newHID, new HIDInfo(myIP, myPort, "", " ", myID));
			}
			return newHID;
		}
		else {
			logger.error("HID = " + newHID.toString() + " level out of "
				+ "bounds when adding new context");
			return null;
		}
	}
	
	/**
	 * Gets the endpoint address associated with an HID
	 * 
	 * @param hid The HID
	 * @return The endpoint address associated with an HID
	 */		
	public  String getIPfromHID(HID hid) {
		HIDInfo info = getHID(hid);
		return info.getIp() + ":" + info.getPort();
	}
	
	/**
	 * Gets ID from an HID
	 * 
	 * @param hid the HID
	 * @return the ID associated to the HID
	 */
	public PeerID getIDfromHID(HID hid) {
		PeerID id = getHID(hid).getID();
		return id;
	}
	
	/**
	 * Checks inside the idTable if the deviceID has already been assigned
	 * 
	 * @param deviceID The deviceID to be checked
	 * @return Returns true if deviceID has already been assigned. False otherwise.
	 */
	public boolean existsDeviceID(long deviceID) {
		boolean is = false;
		Enumeration<ConcurrentHashMap<HID, HIDInfo>> tableEnumeration = idTable.elements();
		Enumeration<HID> hids;
		ConcurrentHashMap<HID, HIDInfo> table;
		while (tableEnumeration.hasMoreElements() && !is) {
			table = tableEnumeration.nextElement();
			hids = table.keys();
			while (hids.hasMoreElements()) {
				if (hids.nextElement().deviceID == deviceID) {
					is = true;
					logger.error("Duplicated deviceID " + deviceID + ". "
						+ "Generating another one");
					break;
				}
			}
		}
		return is;
	}
	
	/**
	 * Adds to the idTable an string containing all the HID and their 
	 * description (HID:description HID:description) with an specific IP 
	 * address and PeerID discovered inside a NM_advertisement.
	 * 
	 * @param HIDs An String containing all the HIDs from an IP address
	 * @param jxID The JXTA id
	 * @param ipPort The IP address of the NM holding the HIDs
	 */
	public void setHIDs(String HIDs, PeerID jxID, String ipPort) {
		StringTokenizer vectorHIDs;
		String hid, description, endpoint, ip;
		int port;
		HID id;
		
		vectorHIDs = new StringTokenizer(HIDs, " ");
		StringTokenizer st;
		
		while (vectorHIDs.hasMoreTokens()) {			
			st = new StringTokenizer(vectorHIDs.nextToken(), ";");
			hid = st.nextToken();
			if (st.hasMoreTokens()) {
				description = st.nextToken();
				if (st.hasMoreTokens()) {
					endpoint = st.nextToken();
				}
				else {
					endpoint = " ";
				}
			}
			else {
				description = " ";
				endpoint = " ";
			}
			
			id = new HID(hid);				
			if ((idTable.containsKey(id))) {
				logger.error("Already assigned HID: " + hid);
			}
			else {
				if (id.toString().equals("0.0.0.0")) {
					logger.error("Wrong HID format :  " + hid);
				}
				else {
					st = new StringTokenizer(ipPort, ":");
					ip = st.nextToken();
					port = Integer.parseInt(st.nextToken());
					addHID(jxID, id, 
						new HIDInfo(ip, port, description, endpoint, jxID));
					logger.debug("HID: " + hid + " and IP: " + ipPort
						+ " have been assigned");
				}
			}
		}
	}
	
	/**
	 * Checks if is update
	 * 
	 * @param hids the hids
	 * @param id the peer id
	 * @param endpoint the endpoint
	 * @return true if it is update
	 */
	public boolean checkUpToDate(String hids, PeerID id, String endpoint) {
		ConcurrentHashMap<HID, HIDInfo> table = idTable.get(id);
		if (table == null) {
			return false;
		}
		
		ConcurrentHashMap<HID, HIDInfo> newTable = new ConcurrentHashMap<HID, HIDInfo>();
		String[] hidArray = hids.split(" ");
		String[] endp = endpoint.split(":");
		String[] element;
		
		for (int i = 0; i < hidArray.length; i++) {
			element = hidArray[i].split(";");
			newTable.put(new HID(element[0]), new HIDInfo(endp[0], Integer.parseInt(endp[1]), element[1], " ", id));
		}
		
		int h1 = table.entrySet().hashCode();
		int h2 = newTable.entrySet().hashCode();
		System.out.println(table.toString());
		System.out.println(newTable.toString());
		
		if (table.equals(newTable)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Checks if is update
	 * 
	 * @param hids the hids
	 * @param id the peer id
	 * @param endpoint the endpoint
	 * @return true if it is update
	 */
	public boolean checkUpToDate2(String hids, PeerID id, String endpoint) {
		ConcurrentHashMap<HID, HIDInfo> table = idTable.get(id);
		
		HashMap<HID, HIDInfo> newTable = new HashMap<HID, HIDInfo>();
		String[] element;
		if (table == null) {
			return false;
		}
		
		Set<HID> keySet = table.keySet();
		String[] hidArray = hids.split(" ");

		for (int i = 0; i < hidArray.length; i++) {
			element = hidArray[i].split(";");
			newTable.put(new HID(element[0]), null);
		}
		
		Set<HID> newKeySet = newTable.keySet();
		
		if (keySet.equals(newKeySet)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Checks if is update
	 * 
	 * @param id the peer id
	 * @param newHashCode the new hashcode
	 * @return true if it is update
	 */
	public boolean checkUpToDate2(PeerID id, String newHashCode) {
		try {
			int hashCode = idTable.get(id).keySet().hashCode();
			if (hashCode == Integer.parseInt(newHashCode)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	/**
	 * Delete various HIDs
	 * 
	 * @param delete the HIDs to delete
	 * @param id the peer id
	 */
	public void deleteHIDs(String delete, PeerID id) {
		if (idTable.contains(id)) {
			String[] hids = delete.split(" ");
			for (int i = 0; i < hids.length; i++) {
				idTable.get(id).remove(new HID(hids[i]));
			}
		}
	}
	
	/**
	 * Returns an string containing all the HID assigned with an specific IP 
	 * address inside idTable
	 * 
	 * @param endpoint the endpoint
	 * @return An string containing all the HID assigned with an specific IP 
	 * address: 1.2.2.3:2 4.2.1.3:1 ...
	 */	
	public String getHIDsFromIP(String endpoint) {
		return "";
	}

	/**
	 * Returns an string containing all the HID assigned with an specific IP 
	 * address inside idTable. Used to publish the HIDs to other Network Managers
	 * 
	 * @param id the peer id
	 * @return an string containing all the HID assigned with an specific IP 
	 * address: 1.2.2.3:2 4.2.1.3:1 ...
	 */	
	public String getHIDsFromID(PeerID id) {
		String HIDs = "";
		if (idTable.containsKey(id)) {
			ConcurrentHashMap<HID, HIDInfo> table = idTable.get(id);
			Iterator<Map.Entry<HID, HIDInfo>> it = table.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<HID, HIDInfo> entry = it.next();
				HIDs = HIDs.concat(entry.getKey().toString() + ";"
					+ entry.getValue().getDescription() + " ");
			}
		}
		return HIDs;
	}
	
	/**
	 * Returns a vector containing all the HID inside idTable
	 * 
	 * @return a vector containing all the HIDs inside idTable
	 */	
	public Vector<String> getHIDs() {
		Enumeration<ConcurrentHashMap<HID, HIDInfo>> tableEnumeration = idTable.elements();
		Enumeration<HID> hidsEnum;
		Vector<String> hidsVector = new Vector<String>();
		while (tableEnumeration.hasMoreElements()) {
			hidsEnum =  tableEnumeration.nextElement().keys();
			while (hidsEnum.hasMoreElements()) {
				hidsVector.addElement(hidsEnum.nextElement().toString());
			}
		}
		return hidsVector;
	}	
	
	/**
	 * Returns a vector containing all the HID assigned with my IP address 
	 * inside idTable
	 * 
	 * @param contextID the context id
	 * @param level the level
	 * @return a vector containing all the HIDs assigned to the Host IP address
	 */	
	public Vector<String> getHIDs(Long contextID, int level) {
		Enumeration<ConcurrentHashMap<HID, HIDInfo>> tableEnumeration = idTable.elements();
		Enumeration<HID> hidsEnum;
		HID hid;
		Vector<String> hidsVector = new Vector<String>();
		
		while (tableEnumeration.hasMoreElements()) {
			hidsEnum =  tableEnumeration.nextElement().keys();
			while (hidsEnum.hasMoreElements()) {
				hid = hidsEnum.nextElement();
				if (hid.getContext(level).equals(contextID)) {
					hidsVector.addElement(hid.toString());
				}
			}
		}
		return hidsVector;
	}

	/**
	 * Returns a vector containing all the HID assigned with my jxta ID inside idTable
	 * 
	 * @return a vector containing all the HIDs assigned to the ID
	 */
	public Vector<String> getHostHIDs() {
		Enumeration<HID> hids = idTable.get(myID).keys();
		Vector<String> HIDs = new Vector<String>();
		while (hids.hasMoreElements()) {
			try {
				HIDs.addElement(hids.nextElement().toString());
			} catch (Exception e) {
				logger.error("Error in getHostHIDs() " + e.getMessage());
			}
		}
		return HIDs;
	}	
	
	/**
	 * Removes all HIDs assigned with an specific IP address. This method is 
	 * used to clean all the HIDs controlled by an specific Network Manager
	 * that is not any more connected to the network. This occurs when the 
	 * BBManager doesn't receive any announcement during 
	 * 2*announcementValidityTime miliseconds. In this case, the Identity 
	 * Manager will remove all the associated HIDs to the non-responding IP 
	 * address.
	 * 
	 * @param id the peer id
	 * @see removeHID()
	 */	
	public void removeHIDsFromID(PeerID id) {
		if (idTable.containsKey(id)) {
			idTable.get(id).clear();
		}
	}

	/**
	 * Returns the table of HIDs
	 * 
	 * @return the table of HIDs
	 */
	public String printHIDtable() {
		String table = "";
		return table;
	}

	/**
	 * Gets the endpoint from a given HID
	 * 
	 * @param hid the HID
	 * @return the endpoint of this HID
	 */
	public String getEndpoint(String hid) {		
		HIDInfo hidInfo = getHID(new HID(hid));
		return hidInfo.getEndpoint();
	}

	/**
	 * Gets the HIDs that match with a given description
	 * 
	 * @param description the description to search HIDs
	 * @return thd HIDs that match with this description
	 */
	public Vector getHIDsbyDescription(String description) {
		Enumeration<ConcurrentHashMap<HID, HIDInfo>> tableEnumeration;
		Iterator<Map.Entry<HID, HIDInfo>> it;
		Map.Entry<HID, HIDInfo> hidEntry;
		Vector<String> HIDs = new Vector<String>();
		
		String aux;
		
		if (description.contains("*")) {
			String[] matchs = description.split("\\*");
			switch (matchs.length) {
				case 1:
					tableEnumeration = idTable.elements();
					while (tableEnumeration.hasMoreElements()) {
						it = tableEnumeration.nextElement().entrySet().iterator();
						while (it.hasNext()) {
							hidEntry = it.next();
							try {
								if (hidEntry.getValue().getDescription().contains(matchs[0])) {
									HIDs.addElement(hidEntry.getKey().toString());
								}
								else {
									// Search in CryptoHID
									if(hidEntry.getValue().getAttributes() != null) { 
										if ((aux = hidEntry.getValue().getAttributes().getProperty(NetworkManagerApplication.DESCRIPTION)) != null) {
											if(aux.contains(matchs[0])) {
												HIDs.addElement(hidEntry.getKey().toString());
											}
										}
									}
								}
							} catch (Exception e) {
								logger.error("Error in getHIDsbyDescription() "
									+ e.getMessage()); 
							}
						}
					}
					break;
				case 2:
					tableEnumeration = idTable.elements();
					while (tableEnumeration.hasMoreElements()) {
						it = tableEnumeration.nextElement().entrySet().iterator();
						while (it.hasNext()) {
							hidEntry = it.next();
							try {
								if (hidEntry.getValue().getDescription().contains(matchs[0])
										&& hidEntry.getValue().getDescription().contains(matchs[1])) {
									HIDs.addElement(hidEntry.getKey().toString());
								}
								else {
									// Search in CryptoHID
									if(hidEntry.getValue().getAttributes() != null) { 
										if ((aux = hidEntry.getValue().getAttributes().getProperty(NetworkManagerApplication.DESCRIPTION)) != null) {
											if (aux.contains(matchs[0]) && aux.contains(matchs[1])) {
												HIDs.addElement(hidEntry.getKey().toString());
											}
										}
									}
								}
							} catch (Exception e) {
								logger.error("Error in getHIDsbyDescription() "
									+ e.getMessage());
							}
						}
					}
					break;
				case 3:
					tableEnumeration = idTable.elements();
					while (tableEnumeration.hasMoreElements()) {
						it = tableEnumeration.nextElement().entrySet().iterator();
						while (it.hasNext()) {
							hidEntry = it.next();
							try {
								if (hidEntry.getValue().getDescription().contains(matchs[0])
										&& hidEntry.getValue().getDescription().contains(matchs[1])
										&& hidEntry.getValue().getDescription().contains(matchs[2])) {
									HIDs.addElement(hidEntry.getKey().toString());
								}
								else {
									// Search in CryptoHID
									if(hidEntry.getValue().getAttributes() != null) { 
										if ((aux = hidEntry.getValue().getAttributes().getProperty(NetworkManagerApplication.DESCRIPTION)) != null) {
											if(aux.contains(matchs[0]) && aux.contains(matchs[1]) && aux.contains(matchs[2])) {
												HIDs.addElement(hidEntry.getKey().toString());
											}
										}
									}
								}
							} catch (Exception e) {
								logger.error("Error in getHIDsbyDescription() "
									+ e.getMessage()); 
							}
						}
					}
					break;
				default:
					break;
			}
		}
		else {
			tableEnumeration = idTable.elements();
			while (tableEnumeration.hasMoreElements()) {
				it = tableEnumeration.nextElement().entrySet().iterator();
				while (it.hasNext()) {
					hidEntry = it.next();
					try {
						if (hidEntry.getValue().getDescription().equals(description)) {
							HIDs.addElement(hidEntry.getKey().toString());
						}
						else {
							// Search in CryptoHID
							if(hidEntry.getValue().getAttributes() != null) { 
								if ((aux = hidEntry.getValue().getAttributes().getProperty(NetworkManagerApplication.DESCRIPTION)) != null) {
									if (aux.equals(description)) {
										HIDs.addElement(hidEntry.getKey().toString());
									}
								}
							}
						}
					} catch (Exception e) {
						logger.error("Error in getHIDsbyDescription() " 
							+ e.getMessage());
					}
				}
			}
		}
		return HIDs;
	}
	
	/**
	 * Gets the description of a given HID
	 * 
	 * @param hid the HID
	 * @return the description of the given HID
	 */
	public String getDescriptionbyHID(String hid) {
		HIDInfo info = getHID(new HID(hid));
		if(info == null) {
			return null; 
		}
		else {
			return info.getDescription();
		}
	}

	/**
	 * Gets the IP of a given HID
	 * 
	 * @param hid the HID
	 * @return the description of the given HID
	 */
	public String getIPbyHID(String hid) {
		HIDInfo info = getHID(new HID(hid));
		if(info == null) {
			return null; 
		}
		else {
			return info.getIp() + ":" + info.getPort();
		}
	}

	/**
	 * Gets the description of a given HID
	 * 
	 * @param hid the HID
	 * @return the description of the given HID
	 */
	public String getEndpointbyHID(String hid) {
		HIDInfo info = getHID(new HID(hid));
		if(info == null) {
			return null; 
		}
		else {
			return info.getEndpoint();
		}
	}

	/**
	 * Main method
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		String query = "(((apa!=5)&&(bita!=6))||(x==-1))";
		LinkedList<String> values = new LinkedList<String>();
		char[] chars = query.toCharArray();
		String value = "";
		char previous = ' ';
		
		for (int i = 0; i < chars.length; i++) {
			switch (chars[i]) {
				case '(':
					values.addLast("(");
					value = "";
					break;
				case '&':
					if (previous == '&') {
						values.addLast("&&");
					}
					else {
						/* Create the first operator and push it. */
						if (value != "") {
							values.addLast(value);
							value = "";
						}
					}
					break;
				case '|':
					if (previous == '|') {
						values.addLast("||");
					}
					else {
						/* Create the first operator and push it. */
						if (value != "") {
							values.addLast(value);
							value = "";
						}
					}
					break;
				case '!':
					if (value != "") {
						values.addLast(value);
						value = "";
					}
					break;
				case '=':
					if (previous == '=') {
						values.addLast("==");
					}
					else if (previous == '!') {
						values.addLast("!=");
					}
					else {
						/* Create the first operator and push it. */
						if (value != "") {
							values.addLast(value);
							value = "";
						}
					}
					break;
				case ')':
					if (value != "") {
						values.addLast(value);
						value = "";
					}
					values.addLast(")");
					break;
				default:
					value = value + chars[i];
					break;
			}
			previous = chars[i];
		}
		
		System.out.println("Hello");
		boolean result = false;
		
		for (int j = 0; j < values.size(); j++) {
			if ((values.get(j) != "(") && (values.get(j-1) == "(")) {
				values.set(j, "true");
				values.remove(j+1);
				values.remove(j+1);
				values.remove(j+1);
				values.remove(j-1);
			}
		}
		
		while (!values.isEmpty()) {
			if (values.size() == 1) {
				result =  Boolean.parseBoolean(values.poll());
				break;
			}
			else {
				for (int j = 0; j < values.size(); j++) {
					if ((values.get(j) != "(") && (values.get(j-1) == "(")) {
						boolean op1 = Boolean.parseBoolean(values.get(j));
						String operand = values.get(j+1);
						boolean op2 = Boolean.parseBoolean(values.get(j+2));
						boolean r = false;
						if (operand == "&&") {
							r = op1 & op2;
						}
						else {
							r = op1 | op2;
						}
						values.set(j, String.valueOf(r));
						values.remove(j+1);
						values.remove(j+1);
						values.remove(j+1);
						values.remove(j-1);
					}
				}
			}
		}
	}
	
	/**
	 * Gets HIDs update
	 * 
	 * @return the update
	 */
	public String getHIDsUpdate() {
		String update = "";
		while (queue.peek() != null) {
			update = update + queue.poll() + " ";
		}
		if (update.equals("")) {
			update = " ";
		}
		return update;
	}

	/**
	 * Gets HIDs table hash
	 * 
	 * @return the HIDs table hash
	 */
	public String getTableHash() {
		return String.valueOf(idTable.get(myID).keySet().hashCode());
	}
	
	/**
	 * Gets "max" host HIDs by attributes
	 * 
	 * @param query the attributes
	 * @param maxNum the max number of host HIDs returned
	 * @return the host HIDs
	 */
	public Vector<String> getHostHIDbyAttributes(String query, int maxNum) {
		LinkedList<String> parsedQuery = parseQuery(query);
		/* Parse the query. */
		Vector<String> results = new Vector<String>();
		Iterator<Map.Entry<HID, HIDInfo>> it = idTable.get(myID).entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry<HID, HIDInfo> entry = it.next();
			Properties attr = entry.getValue().getAttributes();
			if (attr != null) {
				if (checkAttributes(attr, parsedQuery)) {
					results.add(entry.getKey().toString());
					if ((results.size() == maxNum) && (maxNum> 0)) {
						return results;
					}
				}
			}
		}
		return results;
	}
	
	private void removeRedundantBrackets(LinkedList<String> values) {
			
			Iterator<String> iterator = values.iterator();
			
			int i=0;
			
			while(iterator.hasNext() && i<values.size()){
				if(values.get(i).equals("(") || values.get(i).equals(")")){
					values.remove(i);
					if(i>0)
					{
						i--;
					}
				}
				else{
					i++;
				}
			}
			
		}

	/**
	 * Checks attributes
	 * 
	 * TODO Student: check if method cannot be written easier or less complex
	 * 
	 * @param attr the attributes
	 * @param query the query
	 * @return true or false depending on the result
	 */
	private boolean checkAttributes(Properties attr, LinkedList<String> query) {
		LinkedList<String> values = (LinkedList<String>) query.clone();
		boolean result = false;
		
		for (int j = 0; j < values.size(); j++) {
			if ((values.get(j) != "(") && (values.get(j - 1) == "(")) {
				String key = values.get(j);
				String op = values.get(j + 1);
				String value = (values.get(j + 2) != ")") ? values.get(j + 2) : "";
				boolean r = false;
				
				if (attr.containsKey(key)) {
					if (op == "==") {
						if (value.contains("*")) {
							String parts[] = value.split("\\*");
							String v = (String) attr.get(key);
							r = true;
							for (int i = 0; i < parts.length; i++) {
								r = r && v.contains(parts[i]);
							}
						}
						else {
							if (attr.get(key).equals(value)) {
								r = true;
							}
						}
					}
					
					if (op == "!=") {
						if (value.contains("*")) {
							String parts[] = value.split("\\*");
							String v = (String) attr.get(key);
							r = true;
							for (int i = 0; i < parts.length; i++) {
								r = r && (!v.contains(parts[i]));
							}
						}
						
						if (!attr.get(key).equals(value)) {
							r = true;
						}
					}
				}
				
				values.set(j, String.valueOf(r));
				values.remove(j + 1);
				values.remove(j + 1);
				if (value != "") {
					values.remove(j + 1);
				}
				values.remove(j - 1);
			}
		}
		
		while (!values.isEmpty()) {
			if (values.size() == 1) {
				result =  Boolean.parseBoolean(values.poll());
				break;
			}
			else {
				
				removeRedundantBrackets(values);
				
				int j=0; //no need to iterate, better shift values to the left
				while(values.size()>1){
					if ((values.get(j) != "(")) {
						
							if (values.get(j + 1)== ")") {
								boolean r = Boolean.parseBoolean(values.get(j));
								values.set(j, String.valueOf(r));
								values.remove(j + 1);
								values.remove(j - 1);
							}
							else {
								boolean op1 = Boolean.parseBoolean(values.get(j));
								String operand = values.get(j + 1);
								boolean op2 = Boolean.parseBoolean(values.get(j + 2));
								boolean r = false;
								if (operand == "&&") {
									r = op1 & op2;
								}
								else {
									r = op1 | op2;
								}
								
								if(!r)
								{
									return false;
								}
								
								
								
								values.set(j+1, String.valueOf(r));
								
								if(!(values.contains("&&") || values.contains("||"))){
									return r;
								}
								else{
									values.remove(j+1);
									values.remove(j + 1);
								}
							}		
					}
					/* FIXME if (( have been used insted of single ( IndexOutOfBoundsException will be thrown */
					else{
						values.remove(j);
					}
				}
			}
		}
		return result;
	}
	
	/* Old original buggy method */
//	private boolean checkAttributes(Properties attr, LinkedList<String> query) {
//		LinkedList<String> values = (LinkedList<String>) query.clone();
//		boolean result = false;
//		
//		for (int j = 0; j < values.size(); j++) {
//			if ((values.get(j) != "(") && (values.get(j - 1) == "(")) {
//				String key = values.get(j);
//				String op = values.get(j + 1);
//				String value = (values.get(j + 2) != ")") ? values.get(j + 2) : "";
//				boolean r = false;
//				
//				if (attr.containsKey(key)) {
//					if (op == "==") {
//						if (value.contains("*")) {
//							String parts[] = value.split("\\*");
//							String v = (String) attr.get(key);
//							r = true;
//							for (int i = 0; i < parts.length; i++) {
//								r = r && v.contains(parts[i]);
//							}
//						}
//						else {
//							if (attr.get(key).equals(value)) {
//								r = true;
//							}
//						}
//					}
//					
//					if (op == "!=") {
//						if (value.contains("*")) {
//							String parts[] = value.split("\\*");
//							String v = (String) attr.get(key);
//							r = true;
//							for (int i = 0; i < parts.length; i++) {
//								r = r && (!v.contains(parts[i]));
//							}
//						}
//						
//						if (!attr.get(key).equals(value)) {
//							r = true;
//						}
//					}
//				}
//				
//				values.set(j, String.valueOf(r));
//				values.remove(j + 1);
//				values.remove(j + 1);
//				if (value != "") {
//					values.remove(j + 1);
//				}
//				values.remove(j - 1);
//			}
//		}
//		
//		while (!values.isEmpty()) {
//			if (values.size() == 1) {
//				result =  Boolean.parseBoolean(values.poll());
//				break;
//			}
//			else {
//				for (int j = 0; j < values.size(); j++) {
//					if ((values.get(j) != "(") && (values.get(j - 1) == "(")) {
//						if (values.get(j + 1)== ")") {
//							boolean r = Boolean.parseBoolean(values.get(j));
//							values.set(j, String.valueOf(r));
//							values.remove(j + 1);
//							values.remove(j - 1);
//						}
//						else {
//							boolean op1 = Boolean.parseBoolean(values.get(j));
//							String operand = values.get(j + 1);
//							boolean op2 = Boolean.parseBoolean(values.get(j + 2));
//							boolean r = false;
//							if (operand == "&&") {
//								r = op1 & op2;
//							}
//							else {
//								r = op1 | op2;
//							}
//							values.set(j, String.valueOf(r));
//							values.remove(j + 1);
//							values.remove(j + 1);
//							values.remove(j + 1);
//							values.remove(j - 1);
//						}
//					}
//				}
//			}
//		}
//		return result;
//	}

	/**
	 * Parses a query
	 * 
	 * @param query the unparsed query
	 * @return the parsed query
	 */
	private LinkedList<String> parseQuery(String query) {
		LinkedList<String> values = new LinkedList<String>();
		char[] chars = query.toCharArray();
		String value = "";
		char previous = ' ';
		for (int i = 0; i < chars.length; i++) {
			switch (chars[i]) {
				case '(':
					values.addLast("(");
					value = "";
					break;
				case '&':
					if (previous == '&') {
						values.addLast("&&");
					}
					else {
						/* Create the first operator and push it. */
						if (value != "") {
							values.addLast(value);
							value = "";
						}
					}
					break;
				case '|':
					if (previous == '|') {
						values.addLast("||");
					}
					else {
						/* Create the first operator and push it. */
						if (value != "") {
							values.addLast(value);
							value = "";
						}
					}
					break;
				case '!':
					if (value != "") {
						values.addLast(value);
						value = "";
					}
					break;
				case '=':
					if (previous == '=') {
						values.addLast("==");
					}
					else if (previous == '!') {
						values.addLast("!=");
					}
					else {
						/* Create the first operator and push it. */
						if (value != "") {
							values.addLast(value);
							value = "";
						}
					}
					break;
				case ')':
					if (value != "") {
						values.addLast(value);
						value = "";
					}
					values.addLast(")");
					break;
				default:
					value = value + chars[i];
					break;
			}
			previous = chars[i];
		}
		return values;
	}

}
