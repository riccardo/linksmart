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
package eu.linksmart.caf.cm.managers;

import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.linksmart.aom.ApplicationOntologyManager;

import eu.linksmart.caf.cm.ContextManager;
import eu.linksmart.caf.cm.engine.contexts.Device;
import eu.linksmart.caf.cm.engine.contexts.GatewayLocation;
import eu.linksmart.caf.cm.engine.event.EventMeta;
import eu.linksmart.caf.cm.engine.members.ContextMember;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.impl.ContextManagerApplication;
import eu.linksmart.caf.cm.impl.util.XmlUtils;
import eu.linksmart.caf.cm.requirements.RequirementsProcessor;
import eu.linksmart.caf.cm.util.CmHelper;
import eu.linksmart.caf.daqc.subscription.DaqcSubscription;
import eu.linksmart.caf.daqc.subscription.Subscription;
import eu.linksmart.dac.ApplicationDeviceManager;

/**
 * Manages the monitoring and creation of {@link Device} contexts, including
 * interaction with the {@link ApplicationOntologyManager} and
 * {@link ApplicationDeviceManager} to get the device description, services and
 * events.<p> Created {@link Device} contexts are installed into the
 * {@link RuleEngine}, and removed when they leave the network.
 * 
 * @author Michael Crouch
 * 
 */
public class DeviceContextManager extends CmInternalManager {

	/** The Id for this Internal Manager */
	public static final String MANAGER_ID =
			"eu.linksmart.caf.cm.DeviceContextManager";

	/** Logger */
	private static final Logger logger =
			Logger.getLogger(DeviceContextManager.class);

	/** LinkSmart Device Service types */
	private static final String[] LINK_SMART_SERVICE_TYPES =
			{ "linksmartservice", "powerservice", "locationservice",
					"energyservice", "memoryservice", "bluetoothservice" };

	/** Action id sent to the DACcallback, when a device has been added */
	private static final String DEVICEMANAGER_DEVICEADDED = "deviceadded";

	/** Action id sent to the DACcallback, when a device has been removed */
	private static final String DEVICEMANAGER_DEVICEREMOVED = "devicedeleted";

	/** Element name for the device UDN */
	private static final String UDN = "UDN";

	/** Element name for the device type */
	private static final String DEVICE_TYPE = "deviceType";

	/** Element name for the device URI */
	private static final String DEVICE_URI = "deviceURI";

	/** Element name for the device LinkSmart PID */
	private static final String DEVICE_PID = "linksmartPID";

	/** Element name for the device manufacturer */
	private static final String DEVICE_MANUFACTURER = "manufacturer";

	/** Element name for the device manufacturer URL */
	private static final String DEVICE_MANUFACTURER_URL = "manufacturerURL";

	/** Element name for the device model name */
	private static final String DEVICE_MODELNAME = "modelName";

	/** Element name for the device model description */
	private static final String DEVICE_MODEL_DESC = "modelDescription";

	/** Element name for the device physical gateway */
	private static final String DEVICE_GATEWAY = "gateway";

	/** Element name for the device class (Ontology reference) */
	private static final String DEVICE_ONT_CLASS = "deviceClass";

	/** Element name for the device friendly name */
	private static final String DEVICE_FRIENDLY_NAME = "friendlyName";

	/** the {@link RuleEngine} */
	private RuleEngine ruleEngine = null;

	/** the {@link ContextManagerApplication} */
	private ContextManagerApplication app = null;

	/** the {@link DeviceServiceManager} */
	private DeviceServiceManager serviceManager = null;

	/** the {@link DeviceServiceManager} */
	private RequirementsManager requirementsManager = null;

	/** the {@link ScheduledExecutorService} */
	private final ScheduledExecutorService threadManager;

	/**
	 * stub {@link ScheduledFuture} for the finding
	 * {@link ApplicationDeviceManager} task
	 */
	private ScheduledFuture taskStub = null;

	/**
	 * Boolean flag representing whether the {@link ApplicationDeviceManager} is
	 * connected
	 */
	private boolean connected = false;

	/**
	 * Constructor
	 */
	public DeviceContextManager() {
		threadManager = Executors.newScheduledThreadPool(1);
	}

	@Override
	public String getManagerId() {
		return MANAGER_ID;
	}

	@Override
	public void initialise(CmManagerHub hub) {
		this.ruleEngine = (RuleEngine) hub.getManager(RuleEngine.MANAGER_ID);
		this.app = (ContextManagerApplication) hub.getCmApp();
		this.serviceManager =
				(DeviceServiceManager) hub
						.getManager(DeviceServiceManager.MANAGER_ID);
		this.requirementsManager =
				(RequirementsManager) hub
						.getManager(RequirementsManager.MANAGER_ID);
	}
	
	@Override
	public void completedInit() {
		restartAppDeviceManagerListener();		
	}

	@Override
	public void shutdown() {
		if (threadManager != null)
			threadManager.shutdown();
	}

	/**
	 * Gets whether the {@link ApplicationDeviceManager} is connected
	 * 
	 * @return true if connected
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Starts the listener for the {@link ApplicationDeviceManager}
	 */
	public void restartAppDeviceManagerListener() {
		taskStub =
				threadManager.scheduleAtFixedRate(
						new AttemptConnectDeviceManager(), 10, 30,
						TimeUnit.SECONDS);
	}

	/**
	 * Method called when the {@link ApplicationDeviceManager} has been
	 * connected. <p>
	 * 
	 * Cancels the subscription, subscribes to the DACcallback mechanism, and
	 * gets existing devices.
	 * 
	 * @param deviceManager
	 *            the {@link ApplicationDeviceManager}
	 */
	public void deviceManagerConnected(ApplicationDeviceManager deviceManager) {

		logger.info("Application Device Manager connected!");
		connected = true;
		if (taskStub != null) {
			if (taskStub.cancel(true))
				taskStub = null;
		}

		try {
			deviceManager.registerCallBackUrl(app.getCmUrl());
			// refreshAllDevices();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * Queries the {@link RuleEngine} to find out if a {@link Device} context
	 * already exists for the given device, identified by its UDN.
	 * 
	 * @param deviceUDN
	 *            the UDN
	 * @return whether it exists
	 */
	public boolean deviceHasContext(String deviceUDN) {
		if (ruleEngine.getContextByContextId(Device.class, deviceUDN) != null)
			return true;
		return false;
	}

	/**
	 * Creates the {@link Device} context using the device XML.
	 * 
	 * @param deviceXml
	 *            the device XML
	 * @return the {@link Device}
	 */
	public Device createDeviceStub(Document deviceXml) {
		Element root = deviceXml.getDocumentElement();

		String deviceUDN = XmlUtils.getElementValueFromTagName(root, UDN);
		String name = XmlUtils.getElementValueFromTagName(root, DEVICE_PID);
		String deviceUri =
				XmlUtils.getElementValueFromTagName(root, DEVICE_URI);
		Device device = new Device(deviceUDN, name, deviceUri);

		/** Get base Device Info */
		device.setManufacturer(XmlUtils.getElementValueFromTagName(root,
				DEVICE_MANUFACTURER));
		device.setManufacturerURL(XmlUtils.getElementValueFromTagName(root,
				DEVICE_MANUFACTURER_URL));
		device.setModelName(XmlUtils.getElementValueFromTagName(root,
				DEVICE_MODELNAME));
		device.setModelDescription(XmlUtils.getElementValueFromTagName(root,
				DEVICE_MODEL_DESC));
		device.setDeviceType(XmlUtils.getElementValueFromTagName(root,
				DEVICE_TYPE));
		device.setFriendlyName(XmlUtils.getElementValueFromTagName(root,
				DEVICE_FRIENDLY_NAME));
		
		if ("".equals(device.getName())){
			device.setName(device.getFriendlyName());
		}
		
		if ("".equals(device.getDevicePid())){
			device.setDevicePid(device.getName());
		}

		// get runtime service info
		processDeviceServices(device, deviceXml);
		// set Gateway Location
		String gateway =
				XmlUtils.getElementValueFromTagName(root, DEVICE_GATEWAY);
		device.getHasLocations().add(getGatewayLocation(gateway));

		return device;
	}

	/**
	 * Processes the Device Ontology SCPD with the {@link Device} context.<p>
	 * Involves setting the device class, state variables, and building the
	 * {@link Set} of {@link EventMeta} from the SCPD.
	 * 
	 * @param device
	 *            the {@link Device}
	 * @param ontScpd
	 *            the ontology SCPD {@link Document}
	 */
	private void processOntologyDescription(Device device, Document ontScpd) {
		// if ontology has description
		if (ontScpd != null) {
			device.setDeviceClass(XmlUtils.getElementValueFromTagName(ontScpd
					.getDocumentElement(), DEVICE_ONT_CLASS));

			// process State Variables
			processStateVariables(device, ontScpd);

			Set<EventMeta> events =
					RequirementsProcessor.parseEvents(ontScpd
							.getDocumentElement());
			device.setDeviceEvents(events);
		}
	}

	/**
	 * Processes the various services of the device, from the "deviceadded" XML.
	 * <p> This involves creating the LinkSmart-specific device services as contexts
	 * (if they are present), such as the EnergyService.
	 * 
	 * @param device
	 *            the {@link Device}
	 * @param deviceXml
	 *            the device XML
	 */
	private void processDeviceServices(Device device, Document deviceXml) {

		NodeList services =
				XmlUtils.getNodesXpath(deviceXml.getDocumentElement(),
						"//service");
		for (int i = 0; i < services.getLength(); i++) {
			Element service = (Element) services.item(i);
			String serviceType = 
				XmlUtils.getElementValueFromTagName(service, "serviceType");
					
			String serviceId =
				XmlUtils.getElementValueFromTagName(service, "serviceId");

			if (!isLinkSmartService(serviceType)) {
				if (device.getHid() == null || "".equals(device.getHid())) {
					/** If it is the main device service */
					device.setServiceId(serviceId);
					device.setServiceType(serviceType);
					String hid =
							service.getElementsByTagName("linksmartId").item(0)
									.getTextContent();
					device.setHid(hid);
				}
			} else {
				/** Else process it as a LinkSmart-service */
				serviceManager.processService(device, service);
			}
		}
	}

	/**
	 * Evaluate whether the serviceType given is one of the defined
	 * LinkSmart-services. If not, it is the actual device service.
	 * 
	 * @param serviceType
	 *            the serviceType to match
	 * @return whether it is the main device service
	 */
	private boolean isLinkSmartService(String serviceType) {
		String lc = serviceType.toLowerCase(Locale.ENGLISH);
		for (String linksmartService : LINK_SMART_SERVICE_TYPES) {
			if (lc.contains(linksmartService))
				return true;
		}
		return false;
	}

	/**
	 * Processes the Device StateVariables, as described in the
	 * {@link ApplicationOntologyManager} description.<p> Establishes the
	 * {@link Device} State Variables, creating them as {@link ContextMember}s.
	 * 
	 * @param device
	 *            the {@link Device}
	 * @param ontScpd
	 *            the SCPD {@link Document}, from the
	 *            {@link ApplicationOntologyManager}
	 */
	private static void processStateVariables(Device device, Document ontScpd) {

		// get output state variables
		Set<String> stateVars = new HashSet<String>();
		String xpath = "//serviceStateTable/stateVariable";
		NodeList list = XmlUtils.getNodesXpath(ontScpd, xpath);
		for (int i = 0; i < list.getLength(); i++) {
			Element stateVar = (Element) list.item(i);
			String name = XmlUtils.getElementValueFromTagName(stateVar, "name");
			String type =
					XmlUtils.getElementValueFromTagName(stateVar, "dataType");

			// for each stateVariable, create an associated ContextMember
			ContextMember member =
					new ContextMember(device, name, null, type, name);
			device.addMember(member);

			// add to stateVar list
			stateVars.add(name);
		}
		device.setStateVariables(stateVars);
	}

	/**
	 * Retrieves the SCPD Description for the {@link Device}, from the
	 * {@link ApplicationOntologyManager}, and returns as a {@link Document}.
	 * 
	 * @param device
	 *            the {@link Device}
	 * @return the SCPD Description as a {@link Document}
	 */
	private Document getOntologyDescription(Device device) {
		try {
			if (device.getDeviceUri() == null 
					||	"".equals(device.getDeviceUri())){
				logger.warn("No Device Ontology URI provided for device: " 
						+ device.getFriendlyName());
				return null;
			}
			
			logger.info("Retrieve Device Ontology description for "
					+ device.getDeviceUri());
			
			ApplicationOntologyManager ontMan = app.getOntologyManager();
			String scpd = ontMan.getSCPD(device.getDeviceUri());
			if (scpd != null) {
				logger.info("SCPD for "	+ device.getDeviceUri() + 
						" successfully retrieved");
				return CmHelper.getXmlStringAsDocument(scpd);
			}
			logger.error("Device Ontology has no description for '"
					+ device.getDeviceUri() + "'");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Processes the {@link Subscription}s for the given {@link Device}, into
	 * the {@link DaqcSubscription}, which is returned. <p> Includes any Events
	 * described in the Event subscription, and configured requirements for
	 * PULLing device data.
	 * 
	 * @param device
	 *            the {@link Device}
	 * @param scpd
	 *            the Ontology SCPD
	 * @return the {@link DaqcSubscription}
	 */
	public DaqcSubscription processDeviceSubscriptions(Device device,
			Document scpd) {

		// Get Event (PUSH) Subscriptions
		Set<Subscription> pushSubs =
				RequirementsProcessor.parseEventsToSubscriptions(device
						.getDeviceEvents());

		// Get PULL Subscriptions
		Set<Subscription> pullSubs =
				requirementsManager.getDeviceRequirements(device, scpd);

		Set<Subscription> allSubs = new HashSet<Subscription>();
		allSubs.addAll(pullSubs);
		allSubs.addAll(pushSubs);

		return requirementsManager.getDaqcSubscription(allSubs, true, device
				.getContextId());
	}

	/**
	 * Handles the DACcallback call from the {@link ApplicationDeviceManager}
	 * .<p> This involves either (i) adding a new device context , or (ii)
	 * removing a device context.<p>
	 * 
	 * @param action
	 *            the action (either <code>"deviceadded"</code> or
	 *            <code>"devicedeleted"</code>)
	 * @param device
	 *            the device XML
	 * @return the string to return to the {@link ApplicationDeviceManager}
	 */
	public String handleDACcallback(final String action, final String device) {
		logger.info("DACcallback received. Action=" + action + ", DeviceXml=" + device);
		if (device.equals(null)|| "".equals(device)){
			logger.info("DeviceXml is null");
			return "";
		}
		
		threadManager.execute(new Runnable() {

			@Override
			public void run() {
				if (action.equals(DEVICEMANAGER_DEVICEADDED)) {
					/** Add device */
					try {
						String xml = removeXmlDeclaration(device);
						Document deviceXml =
								XmlUtils.getXmlStringAsDocument(xml);
						Device deviceCtx = createDeviceStub(deviceXml);

						Document ontScpd = getOntologyDescription(deviceCtx);
						if (ontScpd != null) {
							processOntologyDescription(deviceCtx, ontScpd);
						}
						
						if (deviceCtx.getHid() == null 
								|| "".equals(deviceCtx.getHid())){
							logger.info("'" + deviceCtx.getDevicePid() + "' ["
								+ deviceCtx.getContextId() + "] is not a LinkSmart Device");
							return;
						}
																		
						ruleEngine.insert(deviceCtx);
						
						if (ontScpd != null){
						DaqcSubscription daqcSub =
								processDeviceSubscriptions(deviceCtx, ontScpd);
						
						requirementsManager.processSubscription(deviceCtx
								.getContextId(), daqcSub);
						}
						
						logger.info("Device context created for "
								+ deviceCtx.getFriendlyName() + " : "
								+ deviceCtx.getDevicePid() + " ["
								+ deviceCtx.getContextId() + "]");
						ruleEngine.fireAllRules();
					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				} else if (action.equals(DEVICEMANAGER_DEVICEREMOVED)) {
					/**
					 * Remove Device
					 * 
					 * Un-subscribe for data Retract Device context
					 */

					try {
						String xml = removeXmlDeclaration(device);
						Document deviceXml =
								XmlUtils.getXmlStringAsDocument(xml);
						String contextId =
								XmlUtils.getElementValueFromTagName(deviceXml
										.getDocumentElement(), UDN);
						requirementsManager
								.cancelSubscriptions(contextId, true);
						Device theDevice =
								(Device) ruleEngine.getContextByContextId(
										Device.class, contextId);
						if (theDevice != null){
							ruleEngine.remove(theDevice);
							ruleEngine.fireAllRules();
						}
					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				} else {
					logger.error("Unrecognised DACcallback action - " + action);
				}
			}
		});

		return "";
	}
	
	/**
	 * Removes the XML declaration from the first line of the
	 * XML document. This avoids problems parsing XML declared
	 * as having UTF-16 encoding
	 * @param xml the xml
	 * @return the new xml
	 */
	private String removeXmlDeclaration(String xml){
		if (xml.startsWith("<?xml")){
			int index = xml.indexOf(">") + 1;
			return xml.substring(index);
		}
		return xml;
	}

	/**
	 * Gets the {@link GatewayLocation} for the given gateway. If it doesn't
	 * exist in the {@link RuleEngine}, it is created
	 * 
	 * @param gw
	 *            the gateway name
	 * @return the {@link GatewayLocation}
	 */
	private GatewayLocation getGatewayLocation(String gw) {
		GatewayLocation gateway =
				(GatewayLocation) ruleEngine.getContextByName(
						GatewayLocation.class, gw);
		if (gateway == null) {
			gateway = new GatewayLocation(gw);
		}
		return gateway;
	}

	/**
	 * {@link Runnable} to attempt the connection with the
	 * {@link ApplicationDeviceManager}, by calling the "getAllGateways"
	 * method.<p> If successful, the {@link DeviceContextManager} is notified.
	 * 
	 * @author Michael Crouch
	 * 
	 */
	private class AttemptConnectDeviceManager implements Runnable {

		@Override
		public void run() {

			ApplicationDeviceManager deviceManager = app.getDeviceManager();
			logger.debug("Attempting connection to ApplicationDeviceManager");
			if (deviceManager != null) {
				try {
					deviceManager.getAllGateways();
				} catch (RemoteException e) {
					// Error connecting to Device Manager.
					// It isn't running at this url. No action.
					return;
				}
			}

			// if success
			deviceManagerConnected(deviceManager);
		}

	}
}
