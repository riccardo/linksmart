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

package eu.linksmart.limbo.cookiedevice.upnp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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

import eu.linksmart.limbo.cookiedevice.backend.CookieContainer;
import eu.linksmart.limbo.cookiedevice.backend.HTBackend;
import eu.linksmart.limbo.cookiedevice.backend.HTCookieContainer;
import eu.linksmart.limbo.cookiedevice.backend.LocalHTBackend;
import eu.linksmart.limbo.cookiedevice.backend.NullHTBackend;

public class CookieDeviceUPnPActivator implements BundleActivator,
		ManagedServiceFactory {

	private static Logger logger = Logger
			.getLogger(CookieDeviceUPnPActivator.class.getName());

	private static final String FOLDERPATH = "CookieDeviceServer/config";
	private static final String CD_CONF_PATH = "CookieDeviceServer/config/CDconfig.xml";
	private static final String CD_CONF_PATH_JAR = "CDconfig.xml";

	private static String networkManagerAddress;
	private static String soapAddress;

	private Hashtable<String, CookieDeviceDevice> devices = new Hashtable<String, CookieDeviceDevice>();
	private Hashtable<String, CookieDeviceDevice> devicesByID = new Hashtable<String, CookieDeviceDevice>();

	static BundleContext context;

	private CookieDeviceDevice device;

	public void start(BundleContext context) throws Exception {
		logger.debug("Reading config");
		try {
			File file = new File(CD_CONF_PATH);
			if (!file.exists()) {
				logger.info("No Config, Placing a default one, which will NOT work.");
				try {
					JarUtil.createFolder(FOLDERPATH);
					Hashtable<String, String> ht = new Hashtable<String, String>();
					ht.put(CD_CONF_PATH, CD_CONF_PATH_JAR);
					JarUtil.extractFilesJar(ht);
				} catch (IOException e) {
					logger.fatal("IOException creating config files", e);
					throw e;
				}
			}
			FileReader fileReader = new FileReader(CD_CONF_PATH);
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
					.listConfigurations("(service.factoryPid=CookieFactory)");
			if (c != null) {
				logger.info("Found " + c.length + " cookie device/s:");
				for (int i = 0; i < c.length; i++) {
					logger.info("	-" + c[i].getProperties().get("name"));
				}
			}
		}

		context.registerService(ManagedServiceFactory.class.getName(), this,
				getManagedServiceFactoryProperties());

		CookieDeviceUPnPActivator.context = context;
		// doServiceRegistration();
	}

	@SuppressWarnings("unchecked")
	protected Dictionary getManagedServiceFactoryProperties() {
		Dictionary<String, String> result = new Hashtable<String, String>();
		result.put(Constants.SERVICE_PID, "CookieFactory");
		return result;
	}

	public void stop(BundleContext context) throws Exception {
		Set<Map.Entry<String, CookieDeviceDevice>> i = devices.entrySet();
		for (Iterator<Entry<String, CookieDeviceDevice>> iterator = i
				.iterator(); iterator.hasNext();) {
			Entry<String, CookieDeviceDevice> entry = (Entry<String, CookieDeviceDevice>) iterator
					.next();
			entry.getValue().getContainer().destroy(false);
			entry.getValue().doServiceUnregistration();
		}
		devices.clear();
		devicesByID.clear();

		System.gc();
		System.runFinalization();
		System.gc();

		this.device.doServiceUnregistration();
		this.device = null;
	}

	public void deleted(String pid) {
		// System.out.println("Deleted UPnP device " + " PID = " + pid);
		if (devices.containsKey(pid)) {
			CookieDeviceDevice serv = devices.get(pid);
			serv.getContainer().destroy(false);
			serv.doServiceUnregistration();
			devices.remove(pid);
			devicesByID.remove(serv.getContainer().getId());
		}

		System.gc();
		System.runFinalization();
		System.gc();

	}

	public String getName() {
		return "CookieDeviceServer";
	}

	@SuppressWarnings("unchecked")
	public CookieDeviceDevice createInstance(String pid, Dictionary dictionary)
			throws IOException {
		String type = (String) dictionary.get("Type");
		if (type == null) {
			throw new IOException("CookieDevic needs a type");
		}
		String id = (String) dictionary.get("ID");
		if (id == null) {
			throw new IOException("CookieDevic needs an id");
		}
		String name = (String) dictionary.get("Name");
		if (name == null) {
			throw new IOException("CookieDevic needs an name");
		}
		CookieContainer cc = null;
		if (type.equalsIgnoreCase("HTCookieDevice")) {
			String backType = (String) dictionary.get("BackType");
			if (backType == null) {
				throw new IOException("HTCookieDevice needs a backType");
			}
			HTBackend back = null;
			if (backType.equalsIgnoreCase("Null"))
				back = new NullHTBackend();
			if (backType.equalsIgnoreCase("Local")) {
				String backFile = (String) dictionary.get("BackFile");
				back = new LocalHTBackend(new File(backFile));
			}
			if (back == null)
				throw new IOException("HTCookieDevice does not know backend "
						+ backType);
			cc = new HTCookieContainer(id, name, back);
		}
		if (cc == null)
			throw new IOException("CookieDevice not known: " + type);
		return new CookieDeviceDevice(CookieDeviceUPnPActivator.context,
				dictionary, cc);
	}

	@SuppressWarnings("unchecked")
	public void updated(String pid, Dictionary properties)
			throws ConfigurationException {
		try {
			logger.info("update called with following properties:");
			logger.info("Will create new Cookie Device with following properties: ");
			Enumeration num = properties.keys();
			while (num.hasMoreElements()) {
				String key = (String) num.nextElement();
				String value = (String) properties.get(key);
				logger.info("    " + key + " -> " + value);
			}
			String id = (String) properties.get("ID");
			CookieDeviceDevice serv = devicesByID.get(id);
			if (serv == null) {
				serv = createInstance(pid, properties);
				serv.doServiceRegistration();
				devicesByID.put(serv.getContainer().getId(), serv);
				devices.put(pid, serv);
				logger.info("Created UPnP device " + properties.get("Name"));
			} else {
				logger.warn("Update was called for an existing CookieDevice. This is not supported!");
			}
		} catch (Exception e) {
			logger.warn("Exception while creating device: " + e.getMessage());
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
