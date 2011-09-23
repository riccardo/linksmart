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
import java.util.Hashtable;
import java.util.Vector;

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
import eu.linksmart.storage.helper.HTCookieDevice;
import eu.linksmart.storage.helper.StringResponse;
import eu.linksmart.storage.helper.VoidResponse;

public class CookieDeviceBackend implements StorageDeviceBackend {
	private static Logger logger = Logger.getLogger(CookieDeviceBackend.class
			.getName());

	private BundleContext context;

	public CookieDeviceBackend(BundleContext context) {
		this.context = context;
	}

	@Override
	public StringResponse createStorageDevice(String config) {
		// CookieDevice are not replicated
		return createStorageDeviceLocal(config);
	}

	@Override
	public StringResponse createStorageDeviceLocal(String config) {
		try {
			logger.info("shall create config.");
			Document d = new SAXBuilder().build(new StringReader(config));
			Element root = d.getRootElement();
			String name = root.getAttributeValue("Name");
			if (name == null) {
				return new StringResponse(ErrorCodes.EC_ARG_ERROR,
						"Name is a needed Attribute for any Storage Device",
						null);
			}
			if (root.getAttributeValue("ID") != null) {
				return new StringResponse(
						ErrorCodes.EC_ARG_ERROR,
						"A Storage Device, that shall be created, must not have an ID",
						null);
			}
			String type = root.getName();
			Hashtable<String, String> properties = new Hashtable<String, String>();
			String id = "" + System.currentTimeMillis();
			properties.put("Name", name);
			properties.put("ID", "" + id);
			if (type.equalsIgnoreCase("HTCookieDevice")) {
				properties.put("Type", "HTCookieDevice");
				Element e = root.getChild("Backend");
				Element back = (Element) e.getChildren().get(0);
				String backType = back.getName();
				if (backType.equalsIgnoreCase("NonPersistant")) {
					properties.put("BackType", "Null");
				} else if (backType.equalsIgnoreCase("LocalFile")) {
					properties.put("BackType", "Local");
					String path = back.getAttributeValue("Path");
					if (path == null) {
						return new StringResponse(
								ErrorCodes.EC_ARG_ERROR,
								"Missing Argument Path for BackType LocalFile of HTCookieDevice.",
								null);
					}
					properties.put("BackFile", path);
				} else {
					return new StringResponse(ErrorCodes.EC_ARG_ERROR, backType
							+ " is an unknown BackType of HTCookieDevice.",
							null);
				}
			} else {
				return new StringResponse(
						ErrorCodes.EC_ARG_ERROR,
						"The type "
								+ type
								+ " is unknown (this should never happen, as the CookieDeviceBackend should not take this config)",
						null);
			}
			logger.info("Will create new Cookie Device with following properties: ");
			for (String key : properties.keySet()) {
				logger.info("    " + key + " -> " + properties.get(key));
			}
			ServiceReference configAdminServiceRef = context
					.getServiceReference(ConfigurationAdmin.class.getName());
			if (configAdminServiceRef == null) {
				return new StringResponse(1, "can not get a ServiceReference",
						null);
			}
			ConfigurationAdmin configAdmin = (ConfigurationAdmin) context
					.getService(configAdminServiceRef);
			if (configAdmin == null) {
				return new StringResponse(1,
						"can not get a ConfigurationAdmin", null);
			}
			Configuration cookie = configAdmin.createFactoryConfiguration(
					"CookieFactory", null);
			if (cookie == null) {
				return new StringResponse(1, "can not create cookie", null);
			}
			cookie.update(properties);
			logger.info("created CookieDevice " + id);
			root.setAttribute("ID", id);
			//d = new Document(root);
			//XMLOutputter out = new XMLOutputter();
			HTCookieDevice dev = new HTCookieDevice(config);
			dev.setId(id);
			return new StringResponse(0, null, dev.toXMLString());
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

	@Override
	public VoidResponse deleteStorageDevice(String id) {
		// CookieDevice are not replicated
		return deleteStorageDeviceLocal(id);
	}

	@Override
	public VoidResponse deleteStorageDeviceLocal(String id) {
		try {
			Configuration dev = getCookiesHT().get(id);
			if (dev == null) {
				return new VoidResponse(2, "A CookieDevice with id " + id
						+ " does not exist.");
			} else {
				dev.delete();
				return new VoidResponse(0, null);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new VoidResponse(1,
					"IOError while trying to find CookieDevices");
		}
	}

	@Override
	public StringResponse getStorageDeviceConfig(String id) {
		try {
			Configuration dev = getCookiesHT().get(id);
			if (dev == null) {
				return new StringResponse(2, "A CookieDevice with id " + id
						+ " does not exist.", null);
			} else {
				Dictionary properties = dev.getProperties();
				String type = (String) properties.get("Type");
				if (type == null) {
					return new StringResponse(ErrorCodes.EC_IO_EXEPTION,
							"The CookieDevice has no Type...", null);
				}
				String name = (String) properties.get("Name");
				if (name == null) {
					return new StringResponse(ErrorCodes.EC_IO_EXEPTION,
							"The CookieDevice has no Name...", null);
				}
				Element root = null;
				if (type.equalsIgnoreCase("HTCookieDevice")) {
					root = new Element("HTCookieDevice");
					root.setAttribute("Name", name);
					root.setAttribute("ID", id);
					Element backend = new Element("Backend");
					root.addContent(backend);
					String backType = (String) properties.get("BackType");
					if (backType == null) {
						return new StringResponse(ErrorCodes.EC_IO_EXEPTION,
								"The CookieDevice has no BackType...", null);
					}
					if (backType.equalsIgnoreCase("Null")) {
						backend.addContent(new Element("NonPersistant"));
					} else if (backType.equalsIgnoreCase("Local")) {
						String backFile = (String) properties.get("BackFile");
						if (backFile == null) {
							return new StringResponse(
									ErrorCodes.EC_IO_EXEPTION,
									"The CookieDevice has no BackFile...", null);
						}
						Element back = new Element("LocalFile");
						back.setAttribute("Path", backFile);
						backend.addContent(back);
					} else {
						return new StringResponse(ErrorCodes.EC_IO_EXEPTION,
								"The CookieDevice has an unknown BackType: "
										+ backType, null);
					}
				} else {
					return new StringResponse(ErrorCodes.EC_IO_EXEPTION,
							"The CookieDevice has an unknown Type: " + type,
							null);
				}
				Document d = new Document(root);
				XMLOutputter out = new XMLOutputter();
				return new StringResponse(0, null, out.outputString(d));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new StringResponse(
					1,
					"IOError while trying to find CookieDevices (This should not happen, as the CookieDeviceBackend should not have taken the request)",
					null);
		}

	}

	@Override
	public Collection<String> getStorageDevices() {
		try {
			return getCookiesHT().keySet();
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public Collection<String> getSupportedStorageDevices() {
		Vector<String> result = new Vector<String>();
		result.add("HTCookieDevice");
		return result;
	}

	@Override
	public boolean hasID(String id) {
		try {
			Configuration fs = getCookiesHT().get(id);
			return (fs != null);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
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

	@Override
	public VoidResponse updateStorageDevice(String config) {
		// No Updates for CookieDevices
		return new VoidResponse(ErrorCodes.EC_ARG_ERROR,
				"CookieDevices can not be changed after initialisation");
	}

	@Override
	public VoidResponse updateStorageDeviceLocal(String config) {
		// No Updates for CookieDevices
		return new VoidResponse(ErrorCodes.EC_ARG_ERROR,
				"CookieDevices can not be changed after initialisation");
	}

	private Hashtable<String, Configuration> getCookiesHT() throws IOException {
		Hashtable<String, Configuration> result = new Hashtable<String, Configuration>();
		Configuration[] c = getCookieArray();
		if (c != null) {
			for (int i = 0; i < c.length; i++) {
				result.put((String) c[i].getProperties().get("ID"), c[i]);
			}
		}
		return result;
	}

	private Configuration[] getCookieArray() throws IOException {
		ServiceReference configAdminServiceRef = context
				.getServiceReference(ConfigurationAdmin.class.getName());

		if (configAdminServiceRef != null) {

			ConfigurationAdmin configAdmin = (ConfigurationAdmin) context
					.getService(configAdminServiceRef);
			Configuration[] c;
			try {
				c = configAdmin
						.listConfigurations("(service.factoryPid=CookieFactory)");
				return c;
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
