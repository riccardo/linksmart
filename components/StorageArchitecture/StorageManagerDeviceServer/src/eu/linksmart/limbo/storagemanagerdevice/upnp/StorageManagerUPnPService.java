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

package eu.linksmart.limbo.storagemanagerdevice.upnp;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.osgi.framework.BundleContext;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;

import eu.linksmart.storage.helper.ErrorCodes;
import eu.linksmart.storage.helper.ResponseFactory;
import eu.linksmart.storage.helper.StringResponse;

import eu.linksmart.limbo.storagemanagerdevice.backend.CookieDeviceBackend;
import eu.linksmart.limbo.storagemanagerdevice.backend.FileSystemDeviceBackend;
import eu.linksmart.limbo.storagemanagerdevice.backend.StorageDeviceBackend;

public class StorageManagerUPnPService implements UPnPService {
	@SuppressWarnings("unused")
	private static Logger logger = Logger
			.getLogger(StorageManagerUPnPService.class.getName());

	final private String SERVICE_ID = "urn:upnp-org:serviceId:StorageManager";
	final private String SERVICE_TYPE = "urn:schemas-upnp-org:service:StorageManager:1";
	final private String VERSION = "1";

	private resultStateVariable result;
	private configStateVariable config;
	private idStateVariable id;
	private UPnPStateVariable[] states;
	@SuppressWarnings("unchecked")
	private HashMap actions = new HashMap();
	@SuppressWarnings("unused")
	private StorageManagerDeviceDevice device;

	@SuppressWarnings("unused")
	private BundleContext context;
	private Vector<StorageDeviceBackend> storageDevices;

	@SuppressWarnings("unchecked")
	public StorageManagerUPnPService(StorageManagerDeviceDevice device,
			BundleContext context) {
		this.device = device;
		this.context = context;
		storageDevices = new Vector<StorageDeviceBackend>();
		storageDevices.add(new FileSystemDeviceBackend(context));
		storageDevices.add(new CookieDeviceBackend(context));
		result = new resultStateVariable();
		config = new configStateVariable();
		id = new idStateVariable();
		this.states = new UPnPStateVariable[] { result, config, id };

		UPnPAction updateStorageDeviceLocal = new updateStorageDeviceLocalAction(
				config, result, this);
		actions.put(updateStorageDeviceLocal.getName(),
				updateStorageDeviceLocal);
		UPnPAction getSupportedStorageDevices = new getSupportedStorageDevicesAction(
				result, this);
		actions.put(getSupportedStorageDevices.getName(),
				getSupportedStorageDevices);
		UPnPAction updateStorageDevice = new updateStorageDeviceAction(config,
				result, this);
		actions.put(updateStorageDevice.getName(), updateStorageDevice);
		UPnPAction createStorageDevice = new createStorageDeviceAction(config,
				result, this);
		actions.put(createStorageDevice.getName(), createStorageDevice);
		UPnPAction createStorageDeviceLocal = new createStorageDeviceLocalAction(
				config, result, this);
		actions.put(createStorageDeviceLocal.getName(),
				createStorageDeviceLocal);
		UPnPAction getStorageDevices = new getStorageDevicesAction(result, this);
		actions.put(getStorageDevices.getName(), getStorageDevices);
		UPnPAction deleteStorageDevice = new deleteStorageDeviceAction(id,
				result, this);
		actions.put(deleteStorageDevice.getName(), deleteStorageDevice);
		UPnPAction getStorageDeviceConfig = new getStorageDeviceConfigAction(
				id, result, this);
		actions.put(getStorageDeviceConfig.getName(), getStorageDeviceConfig);
		UPnPAction deleteStorageDeviceLocal = new deleteStorageDeviceLocalAction(
				id, result, this);
		actions.put(deleteStorageDeviceLocal.getName(),
				deleteStorageDeviceLocal);
	}

	public UPnPAction getAction(String name) {
		return (UPnPAction) actions.get(name);
	}

	@SuppressWarnings("unchecked")
	public UPnPAction[] getActions() {
		return (UPnPAction[]) (actions.values()).toArray(new UPnPAction[] {});
	}

	public String getId() {
		return SERVICE_ID;
	}

	public UPnPStateVariable getStateVariable(String name) {

		if (name.equals(result.getName()))
			return result;
		else if (name.equals(config.getName()))
			return config;
		else if (name.equals(id.getName()))
			return id;
		return null;
	}

	public UPnPStateVariable[] getStateVariables() {
		return states;
	}

	public String getType() {
		return SERVICE_TYPE;
	}

	public String getVersion() {
		return VERSION;
	}

	public java.lang.String createStorageDevice(java.lang.String config) {
		config = StringEscapeUtils.unescapeXml(config);
		for (StorageDeviceBackend sdb : storageDevices) {
			if (sdb.supportsConfig(config))
				return StringEscapeUtils.escapeXml(sdb.createStorageDevice(
						config).toXMLString());
		}
		return StringEscapeUtils
				.escapeXml(ResponseFactory.createStringResponse(2,
						"unsupported StorageDevice type", null));
	}

	public java.lang.String createStorageDeviceLocal(java.lang.String config) {
		logger.info("creating device");
		config = StringEscapeUtils.unescapeXml(config);
		for (StorageDeviceBackend sdb : storageDevices) {
			if (sdb.supportsConfig(config)) {
				logger.info("Found Backend: " + sdb.getClass().getName());
				StringResponse sr = sdb.createStorageDeviceLocal(config);
				String unescaped = sr.toXMLString();
				String escaped = StringEscapeUtils.escapeXml(unescaped);
				return escaped;
			}
		}
		return StringEscapeUtils
				.escapeXml(ResponseFactory.createStringResponse(2,
						"unsupported StorageDevice type", null));
	}

	public java.lang.String deleteStorageDevice(java.lang.String id) {
		id = StringEscapeUtils.unescapeXml(id);
		for (StorageDeviceBackend sdb : storageDevices) {
			if (sdb.hasID(id))
				return StringEscapeUtils.escapeXml(sdb.deleteStorageDevice(id)
						.toXMLString());
		}
		return StringEscapeUtils.escapeXml(ResponseFactory.createVoidResponse(
				2, "unknown device"));
	}

	public java.lang.String deleteStorageDeviceLocal(java.lang.String id) {
		id = StringEscapeUtils.unescapeXml(id);
		for (StorageDeviceBackend sdb : storageDevices) {
			if (sdb.hasID(id))
				return StringEscapeUtils.escapeXml(sdb
						.deleteStorageDeviceLocal(id).toXMLString());
		}
		return StringEscapeUtils.escapeXml(ResponseFactory.createVoidResponse(
				2, "unknown device"));
	}

	public java.lang.String getSupportedStorageDevices() {
		Vector<String> devs = new Vector<String>();
		for (StorageDeviceBackend sdb : storageDevices)
			devs.addAll(sdb.getSupportedStorageDevices());
		return StringEscapeUtils.escapeXml(ResponseFactory
				.createStringVectorResponse(0, null, devs));
	}

	public java.lang.String getStorageDevices() {
		Vector<String> devs = new Vector<String>();
		for (StorageDeviceBackend sdb : storageDevices)
			devs.addAll(sdb.getStorageDevices());
		String answer = StringEscapeUtils.escapeXml(ResponseFactory
				.createStringVectorResponse(0, null, devs));
		return answer;
	}

	public java.lang.String getStorageDeviceConfig(java.lang.String id) {
		id = StringEscapeUtils.unescapeXml(id);
		for (StorageDeviceBackend sdb : storageDevices) {
			if (sdb.hasID(id))
				return StringEscapeUtils.escapeXml(sdb.getStorageDeviceConfig(
						id).toXMLString());
		}
		return StringEscapeUtils
				.escapeXml(ResponseFactory.createStringResponse(2,
						"No StorageDevice with ID " + id, null));
	}

	public java.lang.String updateStorageDevice(java.lang.String config) {
		config = StringEscapeUtils.unescapeXml(config);
		try {
			Document d = new SAXBuilder().build(new StringReader(config));
			Element root = d.getRootElement();
			String id = root.getAttributeValue("ID");
			if (id == null)
				return StringEscapeUtils.escapeXml(ResponseFactory
						.createVoidResponse(2,
								"Config to be updated need an ID"));
			for (StorageDeviceBackend sdb : storageDevices) {
				if (sdb.hasID(id))
					return StringEscapeUtils.escapeXml(sdb.updateStorageDevice(
							config).toXMLString());
			}
			return StringEscapeUtils.escapeXml(ResponseFactory
					.createVoidResponse(1, "ID unknown"));
		} catch (JDOMException e) {
			e.printStackTrace();
			return StringEscapeUtils.escapeXml(ResponseFactory
					.createStringResponse(ErrorCodes.EC_JDOM_EXCEPTION,
							"JDomException : " + e.getMessage(), null));
		} catch (IOException e) {
			e.printStackTrace();
			return StringEscapeUtils.escapeXml(ResponseFactory
					.createStringResponse(ErrorCodes.EC_JDOM_EXCEPTION,
							"JDomException : " + e.getMessage(), null));
		}
	}

	public java.lang.String updateStorageDeviceLocal(java.lang.String config) {
		config = StringEscapeUtils.unescapeXml(config);
		try {
			Document d = new SAXBuilder().build(new StringReader(config));
			Element root = d.getRootElement();
			String id = root.getAttributeValue("ID");
			if (id == null)
				return StringEscapeUtils.escapeXml(ResponseFactory
						.createVoidResponse(2,
								"Config to be updated need an ID"));
			for (StorageDeviceBackend sdb : storageDevices) {
				if (sdb.hasID(id))
					return StringEscapeUtils.escapeXml(sdb
							.updateStorageDeviceLocal(config).toXMLString());
			}
			return StringEscapeUtils.escapeXml(ResponseFactory
					.createVoidResponse(1, "ID unknown"));
		} catch (JDOMException e) {
			e.printStackTrace();
			return StringEscapeUtils.escapeXml(ResponseFactory
					.createStringResponse(ErrorCodes.EC_JDOM_EXCEPTION,
							"JDomException : " + e.getMessage(), null));
		} catch (IOException e) {
			e.printStackTrace();
			return StringEscapeUtils.escapeXml(ResponseFactory
					.createStringResponse(ErrorCodes.EC_JDOM_EXCEPTION,
							"JDomException : " + e.getMessage(), null));
		}
	}
}
