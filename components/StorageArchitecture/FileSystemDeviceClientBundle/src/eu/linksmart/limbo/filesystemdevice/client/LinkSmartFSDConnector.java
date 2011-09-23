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

package eu.linksmart.limbo.filesystemdevice.client;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import eu.linksmart.limbo.sm.networkmanager.client.NetworkManagerApplicationLimboClientPortImpl;

import eu.linksmart.storage.helper.BooleanResponse;
import eu.linksmart.storage.helper.LinkSmartFileResponse;
import eu.linksmart.storage.helper.LinkSmartFileVectorResponse;
import eu.linksmart.storage.helper.LongResponse;
import eu.linksmart.storage.helper.ResponseFactory;
import eu.linksmart.storage.helper.StatFSResponse;
import eu.linksmart.storage.helper.StringResponse;
import eu.linksmart.storage.helper.VoidResponse;

//import eu.linksmart.performace.Measurement;

public class LinkSmartFSDConnector {

	/**
	 * Standard logger .
	 */
	private static Logger logger = Logger.getLogger(LinkSmartFSDConnector.class);

	private static final String DEFAULT_FILE_ENCODING = "base64";

	private String id;

	private NetworkManagerApplicationLimboClientPortImpl nmClient;

	private Collection<String> hids;

	private String senderHID;

	private String nmAdress;

	private String soapAdress;

	private static String defaultNmAdress = null;

	private static String defaultSoapAdress = null;

	private static String defaultSenderHID = "0";

	private FileSystemDeviceLimboClientPortImpl connection;

	private boolean useLinkSmart = true;

	public LinkSmartFSDConnector(String id, String nmAdress, String soapAdress,
			String senderHID) {
		this.id = id;
		this.nmAdress = nmAdress;
		this.soapAdress = soapAdress;
		this.senderHID = senderHID;
		nmClient = new NetworkManagerApplicationLimboClientPortImpl(nmAdress);
		if (id.startsWith("http://")) {
			useLinkSmart = false;
			connection = new FileSystemDeviceLimboClientPortImpl(id);
		} else {
			discoverDevice();
		}
	}

	public LinkSmartFSDConnector(String id) {
		this(id, defaultNmAdress, defaultSoapAdress, defaultSenderHID);
	}

	private static String enc(String in) {
		return StringEscapeUtils.escapeXml(in);
	}

	private static String dec(String in) {
		return StringEscapeUtils.unescapeXml(in);
	}

	private void discoverDevice() {
		logger.info("Discover " + id);
		Vector<String> result = new Vector<String>();
		String hids = nmClient
				.getHIDsbyDescriptionAsString("FileSystemDevice::*::" + id
						+ "::StaticWS");
		String[] array = hids.split(" ");
		for (int i = 0; i < array.length; i++) {
			result.add(array[i]);
		}
		this.hids = result;
		logger.info("Found " + this.hids.size() + " HIDs");
		for (String id : this.hids) {
			logger.debug("    " + id);
		}
		connection = null;
	}

	private FileSystemDeviceLimboClientPortImpl findConnection() {
		if (useLinkSmart) {
			logger.debug("LinkSmartFSCDConnection.findConnection for dev " + id);
			for (String hid : hids) {
				String url = soapAdress + "/" + senderHID + "/" + hid + "/0/";
				logger.info("trying " + url);
				FileSystemDeviceLimboClientPortImpl client = new FileSystemDeviceLimboClientPortImpl(
						url);
				if (client.getID() != null) {
					return client;
				}
				logger.error("no success");
			}
			discoverDevice();
			for (String hid : hids) {
				String url = soapAdress + "/0/" + hid + "/0/";
				logger.info("trying " + url);
				FileSystemDeviceLimboClientPortImpl client = new FileSystemDeviceLimboClientPortImpl(
						url);
				if (client.getID() != null) {
					return client;
				}
				logger.info("no success");
			}
			logger.info("No connection available.");
			return null;
		} else {
			if (connection.getID() == null)
				return null;
			return connection;
		}
	}

	public int getEndpoints() {
		return hids.size();
	}

	public void rediscoverDevice() {
		if (useLinkSmart)
			discoverDevice();
	}

	public String getFileSystemDeviceId() {
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
		LinkSmartFSDConnector.defaultNmAdress = defaultNmAdress;
	}

	public static String getDefaultSoapAdress() {
		return defaultSoapAdress;
	}

	public static void setDefaultSoapAdress(String defaultSoapAdress) {
		LinkSmartFSDConnector.defaultSoapAdress = defaultSoapAdress;
	}

	public static String getDefaultSenderHID() {
		return defaultSenderHID;
	}

	public static void setDefaultSenderHID(String defaultSenderHID) {
		LinkSmartFSDConnector.defaultSenderHID = defaultSenderHID;
	}

	public String getId() {
		logger.info("LinkSmartFSDConnection.getId");
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			logger.info("No Connection in getPath");
			return null;
		}
		String response = connection.getID();
		while (response == null) {
			connection = findConnection();
			if (connection == null) {
				logger.info("No Connection in getPath inner");
				return null;
			}
			response = connection.getID();
		}
		logger.info("Result: " + response);
		return dec(response);
	}

	public VoidResponse createDirectory(String path) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.createDirectory(enc(path));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.createDirectory(enc(path));
		}
		return ResponseFactory.readVoidResponse(dec(response));
	}

	public VoidResponse clearFile(String path) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.clearFile(enc(path));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.clearFile(enc(path));
		}
		return ResponseFactory.readVoidResponse(dec(response));
	}

	public LinkSmartFileVectorResponse getDirectoryEntries(String path) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.getDirectoryEntries(enc(path));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.getDirectoryEntries(enc(path));
		}
		return ResponseFactory.readLinkSmartFileVectorResponse(dec(response));
	}

	public BooleanResponse existsPath(String path) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.existsPath(enc(path));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.existsPath(enc(path));
		}
		return ResponseFactory.readBooleanResponse(dec(response));
	}

	// TODO remove time measurements
//	Measurement gaging = Measurement.getSingeltonMeasurementInstance();

	/**
	 * 
	 * @param path
	 *            File path relative to file system device path.
	 * @example "/myFolder/readme.txt"
	 * @return {@link LinkSmartFileVectorResponse} Response message.
	 */
	public LinkSmartFileResponse getFile(String path) {
		logger.info("LinkSmartFSDConnector.getFile " + path);
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			logger.info("No Connection getFile");
			return null;
		}
		String response = connection.getFile(enc(path));
		while (response == null) {
			connection = findConnection();
			if (connection == null) {
				logger.info("No Connection getFile inner");
				return null;
			}
		}
		// TODO Messen von hier
		// start measure
//		assert gaging.startMeasurement();
		response = connection.getFile(enc(path));

		// TODO remove time gaging
		// Stop timing
//		assert gaging.stopMeasurement();
		// Write results to file.
//		assert gaging.logTimeResults(this.toString());
		LinkSmartFileResponse hfr = ResponseFactory.readLinkSmartFileResponse(dec(response));

		logger.error("Result: " + hfr.getErrorCode());
		logger.error("ResultString: " + response);
		return hfr;
	}

	public VoidResponse truncateFile(String path, long size) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.truncateFile(enc(path), enc(size + ""));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.truncateFile(enc(path), enc(size + ""));
		}
		return ResponseFactory.readVoidResponse(dec(response));
	}

	public LongResponse getFreeSpace() {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.getFreeSpace();
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.getFreeSpace();
		}
		return ResponseFactory.readLongResponse(dec(response));
	}

	public VoidResponse copy(String source, String destination) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.copy(enc(source), enc(destination));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.copy(enc(source), enc(destination));
		}
		return ResponseFactory.readVoidResponse(dec(response));
	}

	public VoidResponse removeDirectory(String path, boolean recursive) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.removeDirectory(enc(path), recursive);
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.removeDirectory(enc(path), recursive);
		}
		return ResponseFactory.readVoidResponse(dec(response));
	}

	public VoidResponse setFileProperty(String path, String propertiesName,
			String propertiesValue) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.setFileProperty(enc(path),
				enc(propertiesName), enc(propertiesValue));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.setFileProperty(enc(path),
					enc(propertiesName), enc(propertiesValue));
		}
		return ResponseFactory.readVoidResponse(dec(response));
	}

	public VoidResponse setFileProperties(String path,
			Dictionary<String, String> properties) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String propString = ResponseFactory.dictionaryToXMLString(properties);
		String response = connection.setFileProperties(enc(path),
				enc(propString));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.setFileProperties(enc(path), enc(propString));
		}
		return ResponseFactory.readVoidResponse(dec(response));
	}

	public VoidResponse createFile(String path,
			Dictionary<String, String> properties) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		if (properties == null) {
			properties = new Hashtable<String, String>();
			properties.put("eu.linksmart.storage.fsd.encoding.method",
					DEFAULT_FILE_ENCODING);
		}
		String propString = ResponseFactory.dictionaryToXMLString(properties);
		String response = connection.createFile(enc(path), enc(propString));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.createFile(enc(path), enc(propString));
		}
		return ResponseFactory.readVoidResponse(dec(response));
	}
	
	public VoidResponse createFileBase64(String path) {
		return createFile(path, createFilePropertiesBase64());
	}
	
	public VoidResponse createFileText(String path) {
		return createFile(path, createFilePropertiesText());
	}

	public LongResponse getSize() {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.getSize();
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.getSize();
		}
		return ResponseFactory.readLongResponse(dec(response));
	}

	public StatFSResponse getStatFS() {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.getStatFS();
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.getStatFS();
		}
		return ResponseFactory.readStatFSResponse(dec(response));
	}

	public VoidResponse move(String source, String destination) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.move(enc(source), enc(destination));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.move(enc(source), enc(destination));
		}
		return ResponseFactory.readVoidResponse(dec(response));
	}

	public StringResponse readFile(String path, long start, int size) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.readFile(enc(path), enc(start + ""),
				enc(size + ""));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.readFile(enc(path), enc(start + ""), enc(size
					+ ""));
		}
		return ResponseFactory.readStringResponse(dec(response));
	}

	public VoidResponse writeFile(String path, long start, String data) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.writeFile(enc(path), enc(start + ""),
				enc(data));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.writeFile(enc(path), enc(start + ""),
					enc(data));
		}
		return ResponseFactory.readVoidResponse(dec(response));
	}

	public VoidResponse removeFile(String path) {
		if (connection == null) {
			connection = findConnection();
		}
		if (connection == null) {
			return null;
		}
		String response = connection.removeFile(enc(path));
		while (response == null) {
			connection = findConnection();
			if (connection == null)
				return null;
			response = connection.removeFile(enc(path));
		}
		return ResponseFactory.readVoidResponse(dec(response));
	}

	public static Dictionary<String, String> createFilePropertiesBase64() {
		Hashtable<String, String> ht = new Hashtable<String, String>();
		ht.put("eu.linksmart.storage.fsd.encoding.method", "base64");
		return ht;
	}
	
	public static Dictionary<String, String> createFilePropertiesText() {
		Hashtable<String, String> ht = new Hashtable<String, String>();
		ht.put("eu.linksmart.storage.fsd.encoding.method", "text");
		return ht;
	}
}
