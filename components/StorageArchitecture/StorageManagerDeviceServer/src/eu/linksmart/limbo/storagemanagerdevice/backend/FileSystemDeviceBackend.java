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

package eu.linksmart.limbo.storagemanagerdevice.backend;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import eu.linksmart.storage.helper.ErrorCodes;
import eu.linksmart.storage.helper.LocalFileSystemDevice;
import eu.linksmart.storage.helper.ReplicatedFileSystemDevice;
import eu.linksmart.storage.helper.ResponseFactory;
import eu.linksmart.storage.helper.StringResponse;
import eu.linksmart.storage.helper.StripedFileSystemDevice;
import eu.linksmart.storage.helper.VoidResponse;

public class FileSystemDeviceBackend implements StorageDeviceBackend {
	private static Logger logger = Logger
			.getLogger(FileSystemDeviceBackend.class.getName());

	private BundleContext context;

	public FileSystemDeviceBackend(BundleContext context) {
		this.context = context;
	}

	public StringResponse createStorageDevice(String config) {
		logger.info("StorageManager.createFileSystemDevice called");
		// At the Moment FileSystemDevices are not replicated.
		return createStorageDeviceLocal(config);
	}

	public StringResponse createStorageDeviceLocal(String config) {
		logger.info("StorageManager.createFileSystemDeviceLocal called");
		try {
			Document d = new SAXBuilder().build(new StringReader(config));
			Element root = d.getRootElement();
			if (root.getName().equalsIgnoreCase("LocalFileSystemDevice")) {
				return createLocalFileSystemDevice(config);
			} else if (root.getName().equalsIgnoreCase(
					"ReplicatedFileSystemDevice")) {
				return createReplicatedFileSystemDevice(config);
			} else if (root.getName().equalsIgnoreCase(
					"StripedFileSystemDevice")) {
				return createStripedFileSystemDevice(config);
			} else {
				return new StringResponse(25, "Unknown Device type", null);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new StringResponse(1,
					"IOError while trying to find FileSystemDevices", null);
		} catch (JDOMException e) {
			e.printStackTrace();
			return new StringResponse(ErrorCodes.EC_JDOM_EXCEPTION,
					"JDomException while trying to find FileSystemDevices",
					null);
		}
	}

	public VoidResponse deleteStorageDevice(java.lang.String id) {
		// At the Moment there are no parallel devices
		return deleteStorageDeviceLocal(id);
	}

	public VoidResponse deleteStorageDeviceLocal(java.lang.String id) {
		try {
			Configuration dev = getFileSystemsHT().get(id);
			if (dev == null) {
				return new VoidResponse(2, "A FileSystemDevice with id " + id
						+ " does not exist.");
			} else {
				dev.delete();
				return new VoidResponse(0, null);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new VoidResponse(1,
					"IOError while trying to find FileSystemDevices");
		}
	}

	@SuppressWarnings("unchecked")
	public StringResponse getStorageDeviceConfig(java.lang.String id) {
		try {
			Configuration fs = getFileSystemsHT().get(id);
			if (fs == null) {
				return null;
			}
			Dictionary properties = fs.getProperties();
			if (((String) properties.get("Type"))
					.equalsIgnoreCase("LocalFileSystemDevice")) {
				String result = findLocalFileSystemDevice(properties);
				return new StringResponse(0, null, result);
			} else if (((String) properties.get("Type"))
					.equalsIgnoreCase("ReplicatedFileSystemDevice")) {
				String result = findReplicatedFileSystemDevice(properties);
				return new StringResponse(0, null, result);
			} else if (((String) properties.get("Type"))
					.equalsIgnoreCase("StripedFileSystemDevice")) {
				String result = findStripedFileSystemDevice(properties);
				return new StringResponse(0, null, result);
			} else {
				return new StringResponse(4, "FileSystemType "
						+ ((String) properties.get("Type")) + " unknown...",
						null);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new StringResponse(1,
					"IOError while trying to find FileSystemDevices", null);
		}
	}

	public Collection<String> getStorageDevices() {
		try {
			return getFileSystemsHT().keySet();
		} catch (IOException e) {
			return null;
		}
	}

	public Collection<String> getSupportedStorageDevices() {
		Vector<String> devs = new Vector<String>();
		devs.add("LocalFileSystemDevice");
		devs.add("ReplicatedFileSystemDevice");
		devs.add("StripedFileSystemDevice");
		return devs;
	}

	public boolean hasID(String id) {
		try {
			Configuration fs = getFileSystemsHT().get(id);
			return (fs != null);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean supportsConfig(String config) {
		try {
			Document d = new SAXBuilder().build(new StringReader(config));
			Element e = d.getRootElement();
			for (String s : getSupportedStorageDevices()) {
				if (e.getName().equalsIgnoreCase(s))
					return true;
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (JDOMException e) {
			e.printStackTrace();
			return false;
		}
	}

	public VoidResponse updateStorageDevice(String config) {
		/* At the moment there are parallel devices */
		return updateStorageDeviceLocal(config);
	}

	@SuppressWarnings("unchecked")
	public VoidResponse updateStorageDeviceLocal(String config) {
		try {
			Document d = new SAXBuilder().build(new StringReader(config));
			Element root = d.getRootElement();
			String id = root.getAttributeValue("ID");
			Configuration fs = getFileSystemsHT().get(id);
			if (fs == null) {
				return new VoidResponse(2, "No FileSystemDevice with Name "
						+ id);
			}
			Dictionary properties = fs.getProperties();
			String type = (String) properties.get("Type");
			if (!type.equalsIgnoreCase(root.getName())) {
				return new VoidResponse(4,
						"Change of Type of Device not supported");
			}
			if (type == null)
				return new VoidResponse(4, "File System has no type...");
			if (type.equalsIgnoreCase("LocalFileSystemDevice"))
				return updateLocalFileSystemDevice(fs, config);
			if (type.equalsIgnoreCase("ReplicatedFileSystemDevice"))
				return updateReplicatedFileSystemDevice(fs, config);
			if (type.equalsIgnoreCase("StripedFileSystemDevice"))
				return updateStripedFileSystemDevice(fs, config);
			return new VoidResponse(4, "update not supported yet...");
		} catch (IOException e) {
			e.printStackTrace();
			return new VoidResponse(1,
					"IOError while trying to find FileSystemDevices");
		} catch (JDOMException e) {
			e.printStackTrace();
			return new VoidResponse(ErrorCodes.EC_JDOM_EXCEPTION,
							"JDomException : " + e.getMessage());
		}
	}

	private StringResponse createLocalFileSystemDevice(String config)
			throws IOException {
		LocalFileSystemDevice loc = new LocalFileSystemDevice(config);
		if (loc.getPath() == null) {
			return new StringResponse(2, "no path", null);
		}
		if (loc.getName() == null) {
			return new StringResponse(2, "no name", null);
		}
		loc.setId("" + System.currentTimeMillis());
		loc.setSystemId("" + System.currentTimeMillis());
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put("Type", "LocalFileSystemDevice");
		properties.put("Name", loc.getName());
		properties.put("Path", loc.getPath());
		properties.put("ID", "" + loc.getId());
		properties.put("SystemID", "" + loc.getSystemId());
		ServiceReference configAdminServiceRef = context
				.getServiceReference(ConfigurationAdmin.class.getName());
		if (configAdminServiceRef == null) {
			return new StringResponse(1, "can not get a ServiceReference", null);
		}
		ConfigurationAdmin configAdmin = (ConfigurationAdmin) context
				.getService(configAdminServiceRef);
		if (configAdmin == null) {
			return new StringResponse(1, "can not get a ConfigurationAdmin",
					null);
		}
		Configuration fs = configAdmin.createFactoryConfiguration(
				"FileSystemFactory", null);
		if (fs == null) {
			return new StringResponse(1, "can not create FileSystem", null);
		}
		fs.update(properties);
		context.ungetService(configAdminServiceRef);
		return new StringResponse(0, null, loc.toXMLString());
	}

	private StringResponse createReplicatedFileSystemDevice(String config)
			throws IOException {
		Hashtable<String, String> properties = new Hashtable<String, String>();
		ReplicatedFileSystemDevice rep = new ReplicatedFileSystemDevice(config);
		rep.setId("" + System.currentTimeMillis());
		rep.setSystemId("" + System.currentTimeMillis());
		properties.put("Type", "ReplicatedFileSystemDevice");
		properties.put("Name", rep.getName());
		properties.put("ID", rep.getId());
		properties.put("SystemID", rep.getSystemId());

		int i = 0;
		for (String backID : rep.getBackendDevices()) {
			properties.put("Backend_" + i, backID);
			i++;
		}
		ServiceReference configAdminServiceRef = context
				.getServiceReference(ConfigurationAdmin.class.getName());
		if (configAdminServiceRef == null) {
			return new StringResponse(1, "can not get a ServiceReference", null);
		}
		ConfigurationAdmin configAdmin = (ConfigurationAdmin) context
				.getService(configAdminServiceRef);
		if (configAdmin == null) {
			return new StringResponse(1, "can not get a ConfigurationAdmin",
					null);
		}
		Configuration fs = configAdmin.createFactoryConfiguration(
				"FileSystemFactory", null);
		if (fs == null) {
			return new StringResponse(1, "can not create FileSystem", null);
		}
		fs.update(properties);
		context.ungetService(configAdminServiceRef);
		return new StringResponse(0, null, rep.toXMLString());
	}

	private StringResponse createStripedFileSystemDevice(String config)
			throws IOException {
		StripedFileSystemDevice strip = new StripedFileSystemDevice(config);
		strip.setId("" + System.currentTimeMillis());
		strip.setSystemId("" + System.currentTimeMillis());
		Hashtable<String, String> properties = new Hashtable<String, String>();
		Long stripeSize = strip.getStripeSize();
		if (stripeSize == null) {
			stripeSize = new Long(4096);
			strip.setStripeSize(stripeSize);
		}
		properties.put("Type", "StripedFileSystemDevice");
		properties.put("Name", strip.getName());
		properties.put("StripeSize", stripeSize.toString());
		properties.put("ID", "" + strip.getId());
		properties.put("SystemID", "" + strip.getSystemId());

		int i = 0;
		for (String backID : strip.getBackendDevices()) {
			properties.put("Backend_" + i, backID);
			i++;
		}
		ServiceReference configAdminServiceRef = context
				.getServiceReference(ConfigurationAdmin.class.getName());
		if (configAdminServiceRef == null) {
			return new StringResponse(1, "can not get a ServiceReference", null);
		}
		ConfigurationAdmin configAdmin = (ConfigurationAdmin) context
				.getService(configAdminServiceRef);
		if (configAdmin == null) {
			return new StringResponse(1, "can not get a ConfigurationAdmin",
					null);
		}
		Configuration fs = configAdmin.createFactoryConfiguration(
				"FileSystemFactory", null);
		if (fs == null) {
			return new StringResponse(1, "can not create FileSystem", null);
		}
		fs.update(properties);
		context.ungetService(configAdminServiceRef);
		return new StringResponse(0, null, strip.toXMLString());
	}

	@SuppressWarnings("unchecked")
	private String findLocalFileSystemDevice(Dictionary properties) {
		LocalFileSystemDevice loc = new LocalFileSystemDevice(
				(String) properties.get("Name"), (String) properties
						.get("Path"), (String) properties.get("ID"),
				(String) properties.get("SystemID"));
		return loc.toXMLString();
	}

	@SuppressWarnings("unchecked")
	private String findReplicatedFileSystemDevice(Dictionary properties) {
		Enumeration keys = properties.keys();
		Vector<String> backendDevices = new Vector<String>();
		while (keys.hasMoreElements()) {
			String element = (String) keys.nextElement();
			if (element.startsWith("Backend_")) {
				String dev = (String) properties.get(element);
				backendDevices.add(dev);
			}
		}
		ReplicatedFileSystemDevice rep = new ReplicatedFileSystemDevice(
				(String) properties.get("Name"), (String) properties.get("ID"),
				(String) properties.get("SystemID"), backendDevices);
		return rep.toXMLString();
	}

	@SuppressWarnings("unchecked")
	private String findStripedFileSystemDevice(Dictionary properties) {
		Enumeration keys = properties.keys();
		Vector<String> backendDevices = new Vector<String>();
		while (keys.hasMoreElements()) {
			String element = (String) keys.nextElement();
			if (element.startsWith("Backend_")) {
				String dev = (String) properties.get(element);
				backendDevices.add(dev);
			}
		}
		StripedFileSystemDevice rep = new StripedFileSystemDevice(
				(String) properties.get("Name"), (String) properties.get("ID"),
				(String) properties.get("SystemID"), new Long(
						(String) properties.get("StripeSize")), backendDevices);
		return rep.toXMLString();
	}

	private Configuration[] getFileSystemsArray() throws IOException {
		ServiceReference configAdminServiceRef = context
				.getServiceReference(ConfigurationAdmin.class.getName());

		if (configAdminServiceRef != null) {

			ConfigurationAdmin configAdmin = (ConfigurationAdmin) context
					.getService(configAdminServiceRef);
			Configuration[] c;
			try {
				c = configAdmin
						.listConfigurations("(service.factoryPid=FileSystemFactory)");
				return c;
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Hashtable<String, Configuration> getFileSystemsHT()
			throws IOException {
		Hashtable<String, Configuration> result = new Hashtable<String, Configuration>();
		Configuration[] c = getFileSystemsArray();
		if (c != null) {
			for (int i = 0; i < c.length; i++) {
				result.put((String) c[i].getProperties().get("ID"), c[i]);
			}
		}
		return result;
	}

	private VoidResponse updateLocalFileSystemDevice(Configuration fs,
			String config) {
		return new VoidResponse(4,
				"A Local FileSystemDevice has nothing to change");
	}

	@SuppressWarnings("unchecked")
	private VoidResponse updateReplicatedFileSystemDevice(Configuration fs,
			String config) {
		try {
			Dictionary properties = fs.getProperties();
			ReplicatedFileSystemDevice rep = new ReplicatedFileSystemDevice(
					config);

			Vector<String> backKeys = new Vector<String>();
			Enumeration keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				if (key.startsWith("Backend_"))
					backKeys.add(key);
			}
			for (String key : backKeys)
				properties.remove(key);

			int i = 0;
			for (String backID : rep.getBackendDevices()) {
				properties.put("Backend_" + i, backID);
				i++;
			}
			try {
				fs.update(properties);
			} catch (IOException e) {
				e.printStackTrace();
				return new VoidResponse(1,
						"IOError while trying to send update");
			}
			return new VoidResponse(0, null);
		} catch (IOException e) {
			e.printStackTrace();
			return new VoidResponse(1,
					"IOError while trying to find FileSystemDevices");
		}
	}

	@SuppressWarnings("unchecked")
	private VoidResponse updateStripedFileSystemDevice(Configuration fs,
			String config) {
		try {
		Dictionary properties = fs.getProperties();
		StripedFileSystemDevice strip = new StripedFileSystemDevice(config);
		Vector<String> backKeys = new Vector<String>();
		Enumeration keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith("Backend_"))
				backKeys.add(key);
		}
		for (String key : backKeys)
			properties.remove(key);

		int i = 0;
		for (String backID : strip.getBackendDevices()) {
			properties.put("Backend_" + i, backID);
			i++;
		}
		try {
			fs.update(properties);
		} catch (IOException e) {
			e.printStackTrace();
			return new VoidResponse(1, "IOError while trying to send update");
		}
		return new VoidResponse(0, null);
		} catch (IOException e) {
			e.printStackTrace();
			return new VoidResponse(1,
					"IOError while trying to find FileSystemDevices");
		}
	}
}
