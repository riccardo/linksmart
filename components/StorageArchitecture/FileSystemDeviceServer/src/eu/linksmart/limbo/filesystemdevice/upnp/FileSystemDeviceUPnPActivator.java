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

package eu.linksmart.limbo.filesystemdevice.upnp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

//import eu.linksmart.performace.Measurement;
import eu.linksmart.storage.storagemanager.backend.FileSystemStorage;
import eu.linksmart.storage.storagemanager.backend.LocalFileSystemStorage;
import eu.linksmart.storage.storagemanager.backend.ReplicatedFileSystemStorage;
import eu.linksmart.storage.storagemanager.backend.StripedFileSystemStorage;

public class FileSystemDeviceUPnPActivator implements BundleActivator,
		ManagedServiceFactory {

	private static Logger logger = Logger
			.getLogger(FileSystemDeviceUPnPActivator.class.getName());

	private static final String FOLDERPATH = "FileSystemDeviceServer/config";
	private static final String FSD_CONF_PATH = "FileSystemDeviceServer/config/FSDconfig.xml";
	private static final String FSD_CONF_PATH_JAR = "FSDconfig.xml";

	private static String networkManagerAddress;
	private static String soapAddress;

	static BundleContext context;

	private Hashtable<String, FileSystemDeviceDevice> devices = new Hashtable<String, FileSystemDeviceDevice>();
	private Hashtable<String, FileSystemDeviceDevice> devicesByID = new Hashtable<String, FileSystemDeviceDevice>();

	public void start(BundleContext context) throws Exception {
		logger.debug("Reading config");
		try {
			File file = new File(FSD_CONF_PATH);
			if (!file.exists()) {
				logger
						.info("No Config, Placing a default one, which will NOT work.");
				try {
					JarUtil.createFolder(FOLDERPATH);
					Hashtable<String, String> ht = new Hashtable<String, String>();
					ht.put(FSD_CONF_PATH, FSD_CONF_PATH_JAR);
					JarUtil.extractFilesJar(ht);
				} catch (IOException e) {
					logger.fatal("IOException creating config files", e);
					throw e;
				}
			}
			FileReader fileReader = new FileReader(FSD_CONF_PATH);
			Document document = new SAXBuilder().build(fileReader);
			Element root = document.getRootElement();
			Element nmConf = root.getChild("NetworkManager");
			networkManagerAddress = nmConf.getAttributeValue("url");
			soapAddress = nmConf.getAttributeValue("soaptunnel");
			logger.info("Using NetworkManager at" + networkManagerAddress);
			logger.info("Using SOAP-Address " + soapAddress);
		} catch (IOException e) {
			logger.fatal("IOException reading config", e);
			throw e;
		}
		ServiceReference configAdminServiceRef = context
				.getServiceReference(ConfigurationAdmin.class.getName());

		if (configAdminServiceRef != null) {

			ConfigurationAdmin configAdmin = (ConfigurationAdmin) context
					.getService(configAdminServiceRef);
			Configuration[] c = configAdmin
					.listConfigurations("(service.factoryPid=FileSystemFactory)");
			if (c != null) {
				logger.info("Found " + c.length + " file system device/s:");
				for (int i = 0; i < c.length; i++) {
					logger.info("	-" + c[i].getProperties().get("name"));
				}
			}
		}

		context.registerService(ManagedServiceFactory.class.getName(), this,
				getManagedServiceFactoryProperties());

		FileSystemDeviceUPnPActivator.context = context;
	}

	protected Dictionary getManagedServiceFactoryProperties() {
		Dictionary result = new Hashtable();
		result.put(Constants.SERVICE_PID, "FileSystemFactory");
		return result;
	}

	public void stop(BundleContext context) throws Exception {
		Set<Map.Entry<String, FileSystemDeviceDevice>> i = devices.entrySet();
		for (Iterator<Entry<String, FileSystemDeviceDevice>> iterator = i
				.iterator(); iterator.hasNext();) {
			Entry<String, FileSystemDeviceDevice> entry = (Entry<String, FileSystemDeviceDevice>) iterator
					.next();
			entry.getValue().getFileSystem().stopp();
			entry.getValue().doServiceUnregistration();

		}
		devices.clear();
		devicesByID.clear();

		System.gc();
		System.runFinalization();
		System.gc();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.cm.ManagedServiceFactory#deleted(java.lang.String)
	 */
	public void deleted(String pid) {
		//System.out.println("Deleted UPnP device " + " PID = " + pid);
		if (devices.containsKey(pid)) {
			FileSystemDeviceDevice serv = devices.get(pid);
			try {
				serv.getFileSystem().destroy(false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			serv.doServiceUnregistration();
			devices.remove(pid);
			devicesByID.remove(serv.getFileSystem().getId());
		}

		System.gc();
		System.runFinalization();
		System.gc();

	}

	public String getName() {
		return "FileSystemDeviceServer";
	}

	/**
	 * Creates local file system storage.
	 * 
	 * @param dictionary
	 *            Dictionary with specified properties of local file system
	 *            device.
	 * 
	 * @return Local file system storage.
	 */
	private FileSystemStorage createLocalFSS(Dictionary dictionary) {
		logger.debug("Create LocalFileSystemDevice");
		String id = (String) dictionary.get("ID");
		String systemId = (String) dictionary.get("SystemID");
		String path = (String) dictionary.get("Path");
		String name = (String) dictionary.get("Name");
		FileSystemStorage fileSystemStorage = new LocalFileSystemStorage(name,
				path, id, systemId);
		logger.debug("Create LocalFileSystemDevice done");
		return fileSystemStorage;
	}

	/**
	 * Creates replicated file system storage.
	 * 
	 * @param dictionary
	 *            Dictionary with specified properties of replicated file system
	 *            device.
	 * 
	 * @return Replicated file system storage.
	 */
	private FileSystemStorage createReplicatedFSS(Dictionary dictionary) {
		logger.debug("Create ReplicatedFileSystemDevice");
		String id = (String) dictionary.get("ID");
		String systemId = (String) dictionary.get("SystemID");
		String name = (String) dictionary.get("Name");
		Vector<String> backends = new Vector<String>();
		Enumeration keys = dictionary.keys();
		while (keys.hasMoreElements()) {
			String element = (String) keys.nextElement();
			if (element.startsWith("Backend_")) {
				String dev = (String) dictionary.get(element);
				logger.debug("  Dev: " + dev);
				backends.add(dev);
			}
		}
		FileSystemStorage replicatedFileSystemStorage = new ReplicatedFileSystemStorage(
				name, backends, id, systemId);
		logger.debug("Create ReplicatedFileSystemDevice done");
		return replicatedFileSystemStorage;
	}

	/**
	 * Creates striped file system storage.
	 * 
	 * @param dictionary
	 *            Dictionary with specified properties of striped file system
	 *            device.
	 * 
	 * @return Striped file system storage.
	 */
	private FileSystemStorage createStripedFSS(Dictionary dictionary) {
		logger.debug("Create StripedFileSystemDevice");
		String id = dictionary.get("ID").toString();
		String systemId = dictionary.get("SystemID").toString();
		String name = (String) dictionary.get("Name");
		long stripesize = Long.parseLong((String) dictionary.get("StripeSize"));
		Vector<String> backends = new Vector<String>();
		Enumeration keys = dictionary.keys();
		while (keys.hasMoreElements()) {
			String element = (String) keys.nextElement();
			if (element.startsWith("Backend_")) {
				String dev = (String) dictionary.get(element);
				logger.debug("  Dev: " + dev);
				backends.add(dev);
			}
		}
		FileSystemStorage stripedFileSystemStorage = new StripedFileSystemStorage(
				name, backends, stripesize, id, systemId);
		logger.debug("Create StripedFileSystemDevice done");
		return stripedFileSystemStorage;
	}

	public FileSystemDeviceDevice createInstance(String pid,
			Dictionary dictionary) {
		String type = (String) dictionary.get("Type");
		FileSystemStorage fileSystemStorage = null;
		if (type.equalsIgnoreCase("LocalFileSystemDevice")) {
			fileSystemStorage = createLocalFSS(dictionary);
		} else if (type.equalsIgnoreCase("ReplicatedFileSystemDevice")) {
			fileSystemStorage = createReplicatedFSS(dictionary);
		} else if (type.equalsIgnoreCase("StripedFileSystemDevice")) {
			fileSystemStorage = createStripedFSS(dictionary);
		} else {
			fileSystemStorage = null;
			logger.error("Unknown Device Type: " + type);
		}
		if (fileSystemStorage == null) {
			logger.error("Could not create Device!");
			return null;
		} else {
			return new FileSystemDeviceDevice(this.context, dictionary,
					fileSystemStorage);
		}
	}

	public void updated(String pid, Dictionary properties)
			throws ConfigurationException {
			try {
			String id = (String) properties.get("ID");
			FileSystemDeviceDevice serv = devicesByID.get(id);
			if (serv == null) {
				serv = createInstance(pid, properties);
				serv.doServiceRegistration();
				devicesByID.put(serv.getFileSystem().getId(), serv);
				devices.put(pid, serv);
				
				logger.info("Created UPnP device " + properties.get("Name"));
				logger.info("   ID: " + serv.getFileSystem().getId());

			} else {
				Vector<String> backends = new Vector<String>();
				Enumeration keys = properties.keys();
				while (keys.hasMoreElements()) {
					String element = (String) keys.nextElement();
					if (element.startsWith("Backend_")) {
						String dev = (String) properties.get(element);
						logger.debug("  Dev: " + dev);
						backends.add(dev);
					}
				}
				if (serv.getFileSystem().getType() == FileSystemStorage.REPLICATED_STORAGE) {
					ReplicatedFileSystemStorage rfss = (ReplicatedFileSystemStorage) serv
							.getFileSystem();
					rfss.changeBackendDevices(backends);
					logger.info("Update of RFSS " + id + " successfull");
					return;
				}
				if (serv.getFileSystem().getType() == FileSystemStorage.STRIPED_STORAGE) {
					StripedFileSystemStorage rfss = (StripedFileSystemStorage) serv
							.getFileSystem();
					rfss.changeBackendDevices(backends);
					logger.info("Update of SFSS " + id + " successfull");
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets network manager address.
	 * 
	 * @return Network manager address as String in URL form.
	 */
	public static String getNMurl() {
		return networkManagerAddress;
	}

	/**
	 * Gets SOAP address.
	 * 
	 * @return SOAP address as String in URL form.
	 */
	public static String getSOAPurl() {
		return soapAddress;
	}
}
