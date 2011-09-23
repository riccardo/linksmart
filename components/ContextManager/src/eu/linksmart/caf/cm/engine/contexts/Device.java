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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.caf.cm.engine.contexts;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import eu.linksmart.caf.cm.engine.event.EventMeta;

/**
 * Context representing the context of a Device on the LinkSmart Network.
 * Automatically generated using the information from the DAC. <p>
 * 
 * Device Context can also contain information of the associated LinkSmart services,
 * including:<p> <ul> <li>Energy Service</li> <li>Power Service</li>
 * <li>Location Service</li> </ul>
 * 
 * @author Michael Crouch
 * 
 */
public class Device extends MemberedContext {

	/** the device Uri (Device Ontology reference) */
	private String deviceUri;

	/** the device UDN, which is also used as it's contextId */
	private String udn;

	/** the device type */
	private String deviceType;

	/** the serviceId of the main device service */
	private String serviceId;

	/** the serviceType of the main device service */
	private String serviceType;

	/** the PID of the device */
	private String devicePid;

	/** the device ontology class */
	private String deviceClass;

	/** the friendly name */
	private String friendlyName;

	/** the manufacturer */
	private String manufacturer;

	/** the manufacturer url */
	private String manufacturerURL;

	/** the model name */
	private String modelName;

	/** the model description */
	private String modelDescription;

	/** {@link Set} of the StateVariables of the device */
	private Set<String> stateVariables;

	/**
	 * {@link Map} of the device's additional services, mapped by their
	 * serviceType
	 */
	private Map<String, Service> deviceServices;

	/**
	 * {@link Set} of the constructed metadata about device events
	 */
	private Set<EventMeta> deviceEvents;

	/**
	 * Constructor
	 * 
	 * @param uniqueDeviceName
	 *            the UDN of the device
	 * @param name
	 *            the name / PID
	 * @param deviceUri
	 *            the ontology uri
	 */
	public Device(String uniqueDeviceName, String name, String deviceUri) {
		super(uniqueDeviceName, name);
		this.udn = uniqueDeviceName;
		this.devicePid = name;
		this.deviceUri = deviceUri;
		deviceServices = new HashMap<String, Service>();
		deviceEvents = new HashSet<EventMeta>();
		
	}

	/**
	 * Gets the {@link Map} of deviceServices. <p> {@link Map} key is the
	 * Service Type (as described in the device service XML, and the value is
	 * the {@link Service} context.
	 * 
	 * @return the deviceServices
	 */
	public Map<String, Service> getDeviceServices() {
		return deviceServices;
	}

	/**
	 * Sets the {@link Map} of deviceServices
	 * 
	 * @param deviceServices
	 *            the deviceServices to set
	 */
	public void setDeviceServices(Map<String, Service> deviceServices) {
		this.deviceServices = deviceServices;
	}

	/**
	 * Adds the {@link Service} to the Device
	 * 
	 * @param service
	 *            the {@link Service}
	 */
	public void addDeviceService(Service service) {
		deviceServices.put(service.getServiceType(), service);
	}

	/**
	 * Gets the Device {@link Service} (if it exists), with the given
	 * serviceType
	 * 
	 * @param serviceType
	 *            the serviceType
	 * @return the {@link Service}
	 */
	public Service getDeviceService(String serviceType) {
		return deviceServices.get(serviceType);
	}

	/**
	 * Gets the String Device URI (from the Device Ontology) indicating what
	 * type of Device it is
	 * 
	 * @return the Device Ontology reference
	 */
	public String getDeviceUri() {
		return deviceUri;
	}

	/**
	 * Sets the String Device URI (from the Device Ontology) indicating what
	 * type of Device it is
	 * 
	 * @param deviceUri
	 *            the Device Ontology reference to set
	 */
	public void setDeviceUri(String deviceUri) {
		this.deviceUri = deviceUri;
	}

	/**
	 * Gets the id of the device service
	 * 
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * Sets the id of the device service
	 * 
	 * @param serviceId
	 *            the serviceId
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * Gets the service type
	 * 
	 * @return the service type
	 */
	public String getServiceType() {
		return serviceType;
	}

	/**
	 * Sets the service type
	 * 
	 * @param serviceType
	 *            the serviceType
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * Gets the device UDN
	 * 
	 * @return the udn
	 */
	public String getUdn() {
		return udn;
	}

	/**
	 * Sets the device UDN
	 * 
	 * @param udn
	 *            the udn
	 */
	public void setUdn(String udn) {
		this.udn = udn;
	}

	/**
	 * Gets the Device Type
	 * 
	 * @return the device Type
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * Sets the Device Type
	 * 
	 * @param deviceType
	 *            the device type
	 */
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * Gets the {@link Set} of Event descriptions, as {@link EventMeta}s, from
	 * the device context
	 * 
	 * @return the {@link Set} of {@link EventMeta}s
	 */
	public Set<EventMeta> getDeviceEvents() {
		return deviceEvents;
	}

	/**
	 * Sets the {@link Set} of Event descriptions, as {@link EventMeta}s, to the
	 * device context
	 * 
	 * @param deviceEvents
	 *            the {@link Set} of {@link EventMeta}s
	 */
	public void setDeviceEvents(Set<EventMeta> deviceEvents) {
		this.deviceEvents = deviceEvents;
	}

	/**
	 * Gets all device state variable types
	 * 
	 * @return {@link Set} of all state variables
	 */
	public Set<String> getStateVariables() {
		return stateVariables;
	}

	/**
	 * Sets all device state variables
	 * 
	 * @param stateVariables
	 *            {@link Set} of all state variables
	 */
	public void setStateVariables(Set<String> stateVariables) {
		this.stateVariables = stateVariables;
	}

	/**
	 * Gets the Device PID
	 * 
	 * @return the PID
	 */
	public String getDevicePid() {
		return devicePid;
	}

	/**
	 * Sets the Device PID
	 * 
	 * @param devicePid
	 *            the PID
	 */
	public void setDevicePid(String devicePid) {
		this.devicePid = devicePid;
	}

	/**
	 * Gets the friendlyName
	 * 
	 * @return friendlyName
	 */
	public String getFriendlyName() {
		return friendlyName;
	}

	/**
	 * Sets the friendlyName
	 * 
	 * @param friendlyName
	 *            the friendlyName
	 */
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	/**
	 * Gets the Device Ontology class
	 * 
	 * @return the class
	 */
	public String getDeviceClass() {
		return deviceClass;
	}

	/**
	 * Sets the Device Ontology class
	 * 
	 * @param deviceClass
	 *            the class
	 */
	public void setDeviceClass(String deviceClass) {
		this.deviceClass = deviceClass;
	}

	/**
	 * Gets the device manufacturer
	 * 
	 * @return the manufacturer
	 */
	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * Sets the device manufacturer
	 * 
	 * @param manufacturer
	 *            the manufacturer
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	/**
	 * Gets the device manufacturer URL
	 * 
	 * @return the manufacturer URL
	 */
	public String getManufacturerURL() {
		return manufacturerURL;
	}

	/**
	 * Sets the device manufacturer URL
	 * 
	 * @param manufacturerURL
	 *            the manufacturer URL
	 */
	public void setManufacturerURL(String manufacturerURL) {
		this.manufacturerURL = manufacturerURL;
	}

	/**
	 * Gets the device model name
	 * 
	 * @return the model name
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * Sets the device model name
	 * 
	 * @param modelName
	 *            the model name
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * Gets the device model description
	 * 
	 * @return the model description
	 */
	public String getModelDescription() {
		return modelDescription;
	}

	/**
	 * Sets the device model description
	 * 
	 * @param modelDescription
	 *            the model description
	 */
	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}

	/**
	 * Encodes the Device Context, including the {@link EnergyService}
	 */
	@Override
	public void encode(PrintStream out) {
		Map<String, String> ctxAttrs = new LinkedHashMap<String, String>();
		ctxAttrs.put("devicePID", devicePid);
		ctxAttrs.put("deviceURI", deviceUri);
		ctxAttrs.put("deviceType", deviceType);
		ctxAttrs.put("deviceClass", deviceClass);
		ctxAttrs.put("serviceId", serviceId);
		ctxAttrs.put("serviceType", serviceType);
		ctxAttrs.put("friendlyName", friendlyName);
		ctxAttrs.put("UDN", udn);
		ctxAttrs.put("modelName", modelName);
		ctxAttrs.put("modelDescription", modelDescription);
		ctxAttrs.put("manufacturer", manufacturer);
		ctxAttrs.put("manufacturerURL", manufacturerURL);

		Set<String> additions = new HashSet<String>();
		additions.add(encodeMembers());

		// encode services
		Iterator<Service> it = deviceServices.values().iterator();
		while (it.hasNext()) {
			additions.add(it.next().toString());
		}

		this.encode(null, ctxAttrs, additions, out);
	}
}
