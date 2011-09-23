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
package eu.linksmart.caf.daqc.impl;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.caf.daqc.protocol.DAqCProtocol;
import eu.linksmart.caf.daqc.protocol.ProtocolHub;
import eu.linksmart.caf.daqc.protocol.pull.PullProtocol;
import eu.linksmart.caf.daqc.protocol.push.PushProtocol;
import eu.linksmart.caf.daqc.reporting.DataReportingManager;

import eu.linksmart.caf.daqc.DataAcquisitionComponent;
import eu.linksmart.caf.daqc.report.DaqcReportingService;
import eu.linksmart.caf.daqc.subscription.DaqcSubscription;
import eu.linksmart.caf.daqc.subscription.DaqcSubscriptionResponse;
import eu.linksmart.caf.daqc.subscription.Result;
import eu.linksmart.caf.daqc.subscription.Subscriber;
import eu.linksmart.caf.daqc.subscription.Subscription;
import eu.linksmart.eventmanager.EventManagerPort;
import eu.linksmart.eventmanager.EventSubscriber;
import eu.linksmart.eventmanager.Part;
import eu.linksmart.network.NetworkManagerApplication;

/**
 * The main class of the Data Acquisition Component, providing the
 * {@link DataAcquisitionComponent} interface published as a Web Service. Also
 * implements the {@link EventSubscriber} interface, to receive events.<p>
 * Handles the OSGi related functionalities of service management.
 * 
 * @author Michael Crouch
 * 
 */
public class DAqCApplication implements DataAcquisitionComponent {

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(DAqCApplication.class);

	/** DAqC Web Service Endpoint */
	private static final String ENDPOINT =
			"http://localhost:8082/axis/services/DataAcquisitionComponent";

	/** the {@link ProtocolHub} */
	private ProtocolHub protocolHub = null;
	
	/** the {@link DataReportingManager} */
	private DataReportingManager dataReportingManager = null;

	/** the daqcOsgi {@link ServiceRegistration} */
	private ServiceRegistration daqcOSgireg = null;

	/** the {@link NetworkManagerApplication} */
	private NetworkManagerApplication nm;
	
	/** the {@link EventManagerPort} */
	private EventManagerPort em;

	/** the {@link BundleContext} */
	private BundleContext context;

	/** the http port */
	private String httpPort = "";
	
	/** the daqc hid */
	private String daqcHid = "0";
	
	/** the {@link ComponentHidManager} */
	private ComponentHidManager componentHidManager;
	
	/** the {@link DAqCConfigurator} */
	private DAqCConfigurator configurator;

	/** the activated flag */
	private boolean activated = false;

	/**
	 * Returns the {@link NetworkManagerApplication}
	 * 
	 * @return the {@link NetworkManagerApplication}
	 */
	public NetworkManagerApplication getNetworkManager() {
		return nm;
	}

	/**
	 * Returns the {@link EventManagerPort} if it has been initialised
	 * 
	 * @return the {@link EventManagerPort}
	 */
	public EventManagerPort getEventManager() {
		return em;
	}

	/**
	 * OSGi Declarative Services activation.<p> Initialises the component,
	 * getting the persistent configuration, and registering the service with
	 * the Network Manager. Also initialises the two protocols (Push and Pull).
	 * 
	 * @param context
	 *            the {@link ComponentContext}
	 */
	protected void activate(final ComponentContext context) {

		this.context = context.getBundleContext();
		httpPort = System.getProperty("org.osgi.service.http.port");
		if (httpPort == null)
			httpPort = "8082";

		nm =
				(NetworkManagerApplication) context
						.locateService("NetworkManager");
		em = (EventManagerPort) context.locateService("EventManagerPort");

		configurator = new DAqCConfigurator(context.getBundleContext(), this);
		// Dictionary config = configurator.loadDefaults();
		// configurator.deleteConfig();
		configurator.init();

		componentHidManager = new ComponentHidManager(this, ENDPOINT);
		dataReportingManager = new DataReportingManager();

		// Initialises Push and Pull protocols
		protocolHub = new ProtocolHub();
		protocolHub.addProtocol(new PullProtocol(this));
		protocolHub.addProtocol(new PushProtocol(this));

		// Register with the NM
		try {
			daqcHid = componentHidManager.registerService();
		} catch (Exception e) {
			logger.error("DAqC: Error creating HID in NM", e);
		}

		// Register OSGi Service
		registerDaqcOSGi();

		// Managed Service Registration
		configurator.registerConfiguration();

		activated = true;
	}

	/**
	 * OSGi Declarative Services de-activation.<p> Shuts down all protocols, and
	 * unregisters DAqC services, including the OSGi service, and HID
	 * configuration with the NM
	 * 
	 * @param context
	 *            the {@link ComponentContext}
	 */
	protected void deactivate(ComponentContext context) {

		try {
			protocolHub.shutdownProtocols();
		} catch (Exception e) {
			logger.error("DAqC: Error shutting down protocols - "
					+ e.getLocalizedMessage(), e);
			e.printStackTrace();
		}

		try {
			componentHidManager.unregisterService(daqcHid);
			unregisterDaqcOSGi();
		} catch (Exception e) {
			logger.error("DAqC: Error removing HID from NM - "
					+ e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Returns the HttpPort
	 * 
	 * @return the Http Port
	 */
	public String getHttpPort() {
		return httpPort;
	}

	/**
	 * Registers this {@link DataAcquisitionComponent} on the OSGi framework
	 */
	private void registerDaqcOSGi() {
		Hashtable props = new Hashtable();
		props.put("PID", (String) configurator.getConfiguration().get(
				DAqCConfigurator.PID));
		props.put("SID", DAqCConfigurator.DAQC_SERVICE_PID);
		props.put("HID", daqcHid);
		daqcOSgireg =
				context.registerService(DataAcquisitionComponent.class
						.getName(), this, props);
		logger.info("DAqC: HID=" + daqcHid + ", PID="
				+ configurator.getConfiguration().get(DAqCConfigurator.PID));
	}

	/**
	 * Unregisters the DAqC from the OSGi framework
	 */
	private void unregisterDaqcOSGi() {
		daqcOSgireg.unregister();
	}

	@Override
	public DaqcSubscriptionResponse subscribe(DaqcSubscription subscriptions) {
		Subscriber subscriber = subscriptions.getSubscriber();
		Set<Result> results = new HashSet<Result>();
		for (int i = 0; i < subscriptions.getSubscriptions().length; i++) {
			Subscription sub = subscriptions.getSubscriptions()[i];
			results.add(protocolHub.processSubscription(subscriber, sub));
		}
		DaqcSubscriptionResponse response = new DaqcSubscriptionResponse();
		response.setResults((Result[]) results.toArray(new Result[results
				.size()]));
		return response;
	}

	@Override
	public boolean cancelSubscription(String protocol, String subscriberHid,
			String dataId) {
		return protocolHub.cancelSubscription(protocol, subscriberHid, dataId);
	}

	public boolean notify(String topic, Part[] parts) {
		DAqCProtocol protocol =
				protocolHub.getProtocol(PushProtocol.PROTOCOL_ID);
		if (protocol == null)
			return false;
		try {
			return ((PushProtocol) protocol).notify(topic, parts);
		} catch (Exception e) {
			logger.error("DAqC: Error using PushProtocol - "
					+ e.getLocalizedMessage(), e);
			return false;
		}
	}

	/**
	 * OSGi Declarative Service - Binds the {@link ConfigurationAdmin}
	 * 
	 * @param cm
	 *            the {@link ConfigurationAdmin}
	 */
	protected void configurationBind(ConfigurationAdmin cm) {
		if (configurator != null) {
			configurator.bindConfigurationAdmin(cm);
			if (activated) {
				configurator.registerConfiguration();
			}
		}
	}

	/**
	 * OSGi Declarative Service - Unbinds the {@link ConfigurationAdmin}
	 * 
	 * @param cm
	 *            the {@link ConfigurationAdmin}
	 */
	protected void configurationUnbind(ConfigurationAdmin cm) {
		configurator.unbindConfigurationAdmin(cm);
	}

	/**
	 * Called by the {@link DAqCConfigurator} when changes have been made to the
	 * configuration.<p> If the PID of the DAqC has been change
	 * @param updates the updates
	 */
	public void applyConfigurations(Hashtable updates) {
		if (updates.containsKey(DAqCConfigurator.PID)) {
			String value = (String) updates.get(DAqCConfigurator.PID);
			if (!value.equals((String) configurator.getConfiguration().get(
					DAqCConfigurator.PID)))
				configurator.getConfiguration()
						.put(DAqCConfigurator.PID, value);

			// Unregister
			unregisterDaqcOSGi();

			// Renew Service
			try {
				componentHidManager.renewService(true);
			} catch (Exception e) {
				logger.error("DAqC: Error creating HID in NM", e);
			}

			// Re-register DAqC OSGi
			registerDaqcOSGi();
		}
	}

	/**
	 * Gets the {@link DAqCConfigurator}
	 * 
	 * @return the {@link DAqCConfigurator}
	 */
	public DAqCConfigurator getConfigurator() {
		return configurator;
	}

	/**
	 * Returns the HID of the Data Acquisition Component
	 * 
	 * @return the HID
	 */
	public String getHid() {
		return daqcHid;
	}

	/**
	 * Returns the PID of the Data Acquisition Component
	 * 
	 * @return PID
	 */
	public String getPid() {
		return configurator.get(DAqCConfigurator.PID);
	}

	/**
	 * Gets the initialised {@link NetworkManagerApplication}
	 * 
	 * @return the {@link NetworkManagerApplication}
	 */
	public NetworkManagerApplication getNM() {
		return nm;
	}

	/**
	 * Queries the Network Manager with the given query, to retrieve a single
	 * matching HID. If none found, null is returned
	 * 
	 * @param query
	 *            the query
	 * @return the HID found
	 * @throws Exception
	 */
	public String getHidMatchingQuery(String query) {
		try {
			String[] results =
					nm.getHIDByAttributes(getHid(), getPid(), query, 1000, 1);
			if (results.length == 0)
				return null;
			return results[0];
		} catch (RemoteException e) {
			logger.error("Error retrieving Hid for query '" + query
					+ "' from Network Manager: " + e.getLocalizedMessage(), e);
			return null;
		}
	}

	/**
	 * Gets the {@link DataReportingManager}
	 * 
	 * @return the {@link DataReportingManager}
	 */
	public DataReportingManager getDataReportingManager() {
		return dataReportingManager;
	}

	/**
	 * Searches the local OSGi framework for a {@link DaqcReportingService} with
	 * the given HID
	 * 
	 * @param hid
	 *            the HID of the {@link DaqcReportingService}
	 * @return the {@link DaqcReportingService}
	 */
	public DaqcReportingService getDaqcReportingServiceOSGi(String hid) {
		String filter = "(HID=" + hid + ")";
		ServiceReference[] refs;
		try {
			refs =
					context.getServiceReferences(DaqcReportingService.class
							.getName(), filter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			return null;
		}
		if (refs.length == 0 || refs.length > 1)
			return null;
		return (DaqcReportingService) context.getService(refs[0]);
	}
}
