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
package eu.linksmart.caf.cm.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.drools.builder.KnowledgeBuilder;
import org.drools.io.Resource;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.aom.ApplicationOntologyManager;
import eu.linksmart.aom.client.ApplicationOntologyManagerServiceLocator;
import eu.linksmart.dac.client.ApplicationDeviceManagerServiceLocator;

import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.ContextManager;
import eu.linksmart.caf.cm.ContextManagerError;
import eu.linksmart.caf.cm.ContextResponse;
import eu.linksmart.caf.cm.action.ActionManager;
import eu.linksmart.caf.cm.action.impl.CallInsideLinkSmartAction;
import eu.linksmart.caf.cm.action.impl.DroolsCodeInjectionAction;
import eu.linksmart.caf.cm.action.impl.ExternalEventAction;
import eu.linksmart.caf.cm.action.impl.InternalEventAction;
import eu.linksmart.caf.cm.action.impl.LoggerAction;
import eu.linksmart.caf.cm.engine.CompiledRulePackage;
import eu.linksmart.caf.cm.engine.TimeService;
import eu.linksmart.caf.cm.engine.contexts.Application;
import eu.linksmart.caf.cm.engine.contexts.BaseContext;
import eu.linksmart.caf.cm.engine.members.ContextMember;
import eu.linksmart.caf.cm.event.DeviceStateChangedHandler;
import eu.linksmart.caf.cm.event.OntologyLocationHandler;
import eu.linksmart.caf.cm.event.PolicyAccessEventHandler;
import eu.linksmart.caf.cm.exceptions.ContextManagerException;
import eu.linksmart.caf.cm.exceptions.ErrorListException;
import eu.linksmart.caf.cm.managers.ApplicationContextManager;
import eu.linksmart.caf.cm.managers.ContextEventManager;
import eu.linksmart.caf.cm.managers.ContextUpdateManager;
import eu.linksmart.caf.cm.managers.DeviceContextManager;
import eu.linksmart.caf.cm.managers.DeviceServiceManager;
import eu.linksmart.caf.cm.managers.QueryManager;
import eu.linksmart.caf.cm.managers.RequirementsManager;
import eu.linksmart.caf.cm.managers.RuleEngine;
import eu.linksmart.caf.cm.managers.RulesManager;
import eu.linksmart.caf.cm.managers.SecurityContextManager;
import eu.linksmart.caf.cm.managers.SpecificationManager;
import eu.linksmart.caf.cm.query.ContextQuery;
import eu.linksmart.caf.cm.query.QueryResponse;
import eu.linksmart.caf.cm.query.QueryRow;
import eu.linksmart.caf.cm.query.QuerySet;
import eu.linksmart.caf.cm.rules.DeclaredFunction;
import eu.linksmart.caf.cm.rules.DeclaredType;
import eu.linksmart.caf.cm.rules.Rule;
import eu.linksmart.caf.cm.specification.ContextSpecification;
import eu.linksmart.caf.cm.util.ContextBuilderFactory;
import eu.linksmart.caf.cm.util.XmlMarshallingException;
import eu.linksmart.caf.daqc.DataAcquisitionComponent;
import eu.linksmart.caf.daqc.client.DataAcquisitionComponentServiceLocator;
import eu.linksmart.caf.daqc.report.DaqcReportingService;
import eu.linksmart.caf.daqc.report.DataReport;
import eu.linksmart.caf.daqc.subscription.DaqcSubscription;
import eu.linksmart.dac.ApplicationDeviceManager;
import eu.linksmart.eventmanager.EventManagerPort;
import eu.linksmart.eventmanager.Part;
import eu.linksmart.eventmanager.client.EventManagerPortServiceLocator;
import eu.linksmart.network.NetworkManagerApplication;

/**
 * The main class of the Context Manager, providing the {@link ContextManager}
 * interfaces for the manager Web Service, as well as the OSGi-related service
 * management functionalities.<p> Implements {@link DaqcReportingService} to
 * receive {@link DataReport}s from the {@link DataAcquisitionComponent}
 * 
 * @author Michael Crouch
 * 
 */
public class ContextManagerApplication implements ContextManager {

	/** Base folder for the ContextManager configuration */
	public static final String CM_FOLDER = "ContextManager";

	/** the {@link Logger} */
	private static final Logger logger =
			Logger.getLogger(ContextManagerApplication.class);

	/** Context path of the published Web Service */
	private static final String WS_LOC = "/axis/services/ContextManager";

	/** SID of the {@link EventManagerPort} */
	private static final String EM_SID = "EventManagerPort";

	/** DROOLs date format System property id */
	private static final String DROOLS_DF_VAR = "drools.dateformat";

	/** {@link ConfigurationAdmin} activation */
	private boolean activated = false;

	/** the {@link ComponentContext} */
	private ComponentContext context;

	/** the {@link CmConfigurator} */
	private CmConfigurator configurator;

	/** the {@link ComponentHidManager} */
	private ComponentHidManager hidManager;

	/** the {@link DaqcReportingService} {@link ServiceRegistration} */
	private ServiceRegistration reportingReg;

	/** the {@link CmManagerHub} */
	private CmManagerHub managerHub;

	// // Managers

	/** the {@link SpecificationManager} */
	private SpecificationManager specManager;

	/** the {@link RequirementsManager} */
	private RequirementsManager reqManager;

	/** the {@link RulesManager} */
	private RulesManager ruleManager;

	/** the {@link QueryManager} */
	private QueryManager queryManager;

	/** the {@link RuleEngine} */
	private RuleEngine ruleEngine;

	/** the {@link ContextEventManager} */
	private ContextEventManager ctxEventManager;

	/** the {@link DeviceContextManager} */
	private DeviceContextManager deviceCtxManager;

	/** the {@link ApplicationContextManager} */
	private ApplicationContextManager appCtxManager;

	/** the {@link ContextUpdateManager} */
	private ContextUpdateManager updateManager;

	/** the {@link SecurityContextManager} */
	private SecurityContextManager securityCtxManager;

	/** the {@link DeviceServiceManager} */
	private DeviceServiceManager serviceManager;

	/** the {@link DataAcquisitionComponent} */
	private DataAcquisitionComponent daqc;

	/** the {@link NetworkManagerApplication} */
	private NetworkManagerApplication nm;

	/** the hid */
	private String hid = "0";

	/** the http port */
	private String httpPort;

	/**
	 * OSGi Declarative Services activation.<p> Activator initialises the
	 * Context Manager - creating all internal managers, setting up the Rule
	 * Engine, loading core Rules / Queries and registering the
	 * {@link ContextManager} service with the Network Manager
	 * 
	 * @param context
	 *            the {@link ComponentContext}
	 */
	protected void activate(final ComponentContext context) {
		try {
			logger.info("Initialising Context Manager");
			this.context = context;
			httpPort = System.getProperty("org.osgi.service.http.port");
			if (httpPort == null)
				httpPort = "8082";
			nm =
					(NetworkManagerApplication) context
							.locateService("NetworkManager");
			daqc = null;

			hidManager = new ComponentHidManager(this, getCmUrl());
			configurator = new CmConfigurator(context.getBundleContext(), this);
			configurator.registerConfiguration();

			// Create CM Folder
			File cmFolder = new File(CM_FOLDER);
			cmFolder.mkdirs();

			// Initialise Time Service
			String dtFormat = configurator.get(CmConfigurator.DATETIME_FORMAT);
			if (dtFormat != null) {
				System.setProperty(DROOLS_DF_VAR, dtFormat);
				TimeService.getInstance(dtFormat);
			}

			// Initialise Managers
			logger.info("Building manager hub");
			specManager = new SpecificationManager();
			queryManager = new QueryManager();
			reqManager = new RequirementsManager();
			ruleManager = new RulesManager();
			ruleEngine = RuleEngine.getSingleton();
			ctxEventManager = new ContextEventManager(context);
			deviceCtxManager = new DeviceContextManager();
			appCtxManager = new ApplicationContextManager();
			updateManager = new ContextUpdateManager();
			securityCtxManager = new SecurityContextManager(context);
			serviceManager = new DeviceServiceManager();

			// Initialise Manager Hub
			managerHub = new CmManagerHub(this);
			managerHub.addManager(specManager);
			managerHub.addManager(queryManager);
			managerHub.addManager(reqManager);
			managerHub.addManager(ruleManager);
			managerHub.addManager(ruleEngine);
			managerHub.addManager(ctxEventManager);
			managerHub.addManager(deviceCtxManager);
			managerHub.addManager(appCtxManager);
			managerHub.addManager(updateManager);
			managerHub.addManager(securityCtxManager);
			managerHub.addManager(serviceManager);

			// Set up action manager
			ActionManager actionManager = new ActionManager();
			actionManager.addActionProcessor(new CallInsideLinkSmartAction(
					managerHub));
			actionManager.addActionProcessor(new LoggerAction());
			actionManager.addActionProcessor(new DroolsCodeInjectionAction());
			actionManager.addActionProcessor(new InternalEventAction());
			actionManager
					.addActionProcessor(new ExternalEventAction(managerHub));
			managerHub.addManager(actionManager);

			// Do final initialisation for all internal managers
			logger.info("Initialising managers");
			managerHub.initialiseConfiguration();

			try {
				// Load core Rules and Queries
				logger.info("Loading core rules...");
				loadBaseDrls(ruleEngine, "resources/rules/");
				logger.info("Loading core queries...");
				loadBaseDrls(ruleEngine, "resources/query/");
			} catch (Exception e1) {
				logger.error("Errors loading core rules", e1);
			}

			try {
				/**
				 * Load Globals to Rule Engine. Important to do this AFTER
				 * loading base rules, otherwise an error is thrown.
				 */
				ruleEngine.loadGlobal("actionManager", actionManager);
				//ruleEngine.loadGlobal("TimeService", TimeService.getInstance());
				
				// Publish to Network Manager
				this.hid = hidManager.registerService();
			} catch (RemoteException e) {
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}

			/**
			 * Install Context Event Handlers
			 */
			ctxEventManager.addHandler(new PolicyAccessEventHandler());
			ctxEventManager.addHandler(new DeviceStateChangedHandler());

			/**
			 * Register OSGi-based DAqC reporter, so that the DAqC can report
			 * data directly, if it is running locally, rather than using Web
			 * Services
			 */
			Hashtable<String, String> props = new Hashtable<String, String>();
			props.put("PID", (String) configurator.getConfiguration().get(
					CmConfigurator.PID));
			props.put("SID", CmConfigurator.CM_SERVICE_PID);
			props.put("HID", this.hid);
			reportingReg =
					context.getBundleContext().registerService(
							DaqcReportingService.class.getName(), this, props);
			logger.info("Registered OSGi DaqcReportingService");
			activated = true;
			logger.info("Context Manager active");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * OSGi Declarative Services de-activation.<p> Shuts down all internal
	 * managers, and unregisters services from OSGi and Network Manager
	 * 
	 * @param context
	 *            the {@link ComponentContext}
	 */
	protected void deactivate(ComponentContext context) {
		logger.info("Shutting down Context Manager");
		managerHub.shutdown();

		if (reportingReg != null)
			reportingReg.unregister();

		if (!"0".equals(hid))
			hidManager.unregisterService(hid);
	}

	/**
	 * Gets the HID of the registered {@link ContextManager} Service
	 * 
	 * @return the HID
	 */
	public String getHid() {
		return hid;
	}

	/**
	 * Gets the PID of the {@link ContextManager} Service, from the
	 * {@link CmConfigurator}
	 * 
	 * @return the PID
	 */
	public String getPid() {
		return (String) configurator.getConfiguration().get(CmConfigurator.PID);
	}

	/**
	 * Gets the url of the deployed {@link ContextManager}
	 * 
	 * @return the url
	 */
	public String getCmUrl() {
		return "http://localhost:" + httpPort + WS_LOC;
	}

	/**
	 * o Gets the local stored {@link NetworkManagerApplication} instance
	 * 
	 * @return the {@link NetworkManagerApplication}
	 */
	public NetworkManagerApplication getNM() {
		return nm;
	}

	/**
	 * Loads the base Rule Packages to the {@link RuleEngine} from the specified
	 * location inside the bundle. May be DRLs rule files, or {@link QuerySet}
	 * (*.ctq) files
	 * 
	 * @param ruleEngine
	 *            the {@link RuleEngine}
	 * @param location
	 *            the location to get the files from - must be a folder
	 */
	private void loadBaseDrls(RuleEngine ruleEngine, String location) {
		// Get names of all files in location
		Enumeration<String> locs;
		try {
			BundleContext bc = context.getBundleContext();
			Bundle b = bc.getBundle();
			locs = b.getEntryPaths(location);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return;
		}

		if (locs == null) {
			logger.error("Cannot find Drls at " + location);
		}

		while (locs.hasMoreElements()) {
			String loc = locs.nextElement();
			// if file is a DRL, load as DRL
			if (loc.endsWith(".drl")) {
				URL drlUrl =
						context.getBundleContext().getBundle().getResource(loc);

				try {
					KnowledgeBuilder builder =
							ruleEngine.getKnowledgeBuilderSession();
					Resource res =
							ruleEngine.buildRuleResource(drlUrl.openStream());
					ruleEngine.compileRulePackage(res, builder);
					ruleEngine.addBuiltKnowledge(builder);
					logger.info("RuleSet " + drlUrl.getFile() + " loaded");
				} catch (ErrorListException e) {
					logger.error("Errors in " + drlUrl.getFile() + ":");
					Iterator<ContextManagerError> errors =
							e.getErrorList().iterator();
					while (errors.hasNext()) {
						ContextManagerError error = errors.next();
						logger.error(error.getErrorSubject() + ": "
								+ error.getErrorDescription());
					}
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}

			}
			// if file is ctq, then build QuerySet to DRL, and load
			else if (loc.endsWith(".ctq")) {
				URL url =
						context.getBundleContext().getBundle().getResource(loc);
				try {
					QuerySet querySet =
							ContextBuilderFactory.buildQuerySet(url
									.openStream());
					queryManager.loadQuerySetToEngine(querySet);
					queryManager.storeQuerySet(querySet);
					logger.info("QuerySet " + url.getFile() + " loaded");
				} catch (XmlMarshallingException e) {
					logger.error(e.getMessage(), e);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				} catch (ErrorListException e) {
					logger.error("Errors in " + url.getFile() + ":");
					Iterator<ContextManagerError> errors =
							e.getErrorList().iterator();
					while (errors.hasNext()) {
						ContextManagerError error = errors.next();
						logger.error(error.getErrorSubject() + ": "
								+ error.getErrorDescription());
					}
				}
			}
		}
	}

	/**
	 * Gets the {@link DataAcquisitionComponent} to use for data retrieval.<p>
	 * Uses configured DAqC PID (from the {@link CmConfigurator}, to find a
	 * local OSGi instance of the {@link DataAcquisitionComponent} with that
	 * PID. If it doesn't exist, it then queries the
	 * {@link NetworkManagerApplication} for any DAqC HIDs with that PID. If
	 * this case, the DAqC is remote, and a DAqC Web Service Client is created.
	 * <p> If no DAqC is found, null is returned
	 * 
	 * @return the {@link DataAcquisitionComponent}
	 * @throws RemoteException
	 * @throws ServiceException
	 */
	public DataAcquisitionComponent getDataAcquisitionComponent()
			throws RemoteException, ServiceException {

		if (daqc != null)
			return daqc;

		// get configured DAqC PID
		String daqcPid =
				(String) configurator.getConfiguration().get(
						CmConfigurator.DAQC_PID);
		if ((daqcPid == null) || (daqcPid.equals(""))) {
			// if no daqc specified, try to find local daqc (osgi) and register
			ServiceReference ref =
					context.getBundleContext().getServiceReference(
							DataAcquisitionComponent.class.getName());
			daqcPid = (String) ref.getProperty("PID");
			if (daqcPid != null) {
				configurator.setConfiguration(CmConfigurator.DAQC_PID, daqcPid);
			}
			daqc =
					(DataAcquisitionComponent) context.getBundleContext()
							.getService(ref);
			logger.info("Using local OSGi DAqC instance");
		} else {
			// if daqc specified, first try to match against osgi daqc, and if
			// not get hid
			String filter = "(PID=" + daqcPid + ")";
			try {
				ServiceReference[] refs =
						context.getBundleContext().getServiceReferences(
								DataAcquisitionComponent.class.getName(),
								filter);
				if (refs.length > 0)
					return (DataAcquisitionComponent) context
							.getBundleContext().getService(refs[0]);

			} catch (InvalidSyntaxException e) {
				logger.error(e.getMessage(), e);
			}

			// find on LinkSmart network
			String cmPid =
					(String) configurator.getConfiguration().get(
							CmConfigurator.PID);
			String query =
					"((PID==" + daqcPid + ")&&(SID=="
							+ CmConfigurator.DAQC_SERVICE_PID + "))";
			String[] hids = nm.getHIDByAttributes(hid, cmPid, query, 1000, 1);
			if (hids.length > 0) {
				// create DAqC client and return
				DataAcquisitionComponentServiceLocator locator =
						new DataAcquisitionComponentServiceLocator();
				locator
						.setDataAcquisitionComponentEndpointAddress(getSoapTunnellingAddress(
								hids[0], "0"));
				daqc = locator.getDataAcquisitionComponent();
				logger.info("Using remote DAqC instance, at " + hids[0]);
			} else {
				// No Daqc found
				logger.error("No Data Acquisition Component found!");
				return null;
			}
		}
		return daqc;
	}

	/**
	 * Gets the EventManager to use. If no local instance, use configured PID
	 * 
	 * @return the {@link EventManagerPort}
	 */
	public EventManagerPort getEventManager() {
		/**
		 * Use local EM, from OSGi.
		 */
		ServiceReference ref =
				context.getBundleContext().getServiceReference(
						EventManagerPort.class.getName());
		if (ref != null) {
			return (EventManagerPort) context.getBundleContext()
					.getService(ref);
		}

		/**
		 * If here, there is no local Event Manager, so try to get by the
		 * configured PID
		 */
		String emPid = configurator.get(CmConfigurator.EM_PID);
		if ((emPid != null) && (!"".equals(emPid))) {
			/**
			 * Configured with PID of Event Manager, so get HID, and return WS
			 * client
			 */
			String query = "((PID==" + emPid + ")&&(SID==" + EM_SID + "))";
			try {
				String hid = getHidMatchingQuery(query);
				EventManagerPortServiceLocator locator =
						new EventManagerPortServiceLocator();
				locator
						.setEventManagerPortEndpointAddress(getSoapTunnellingAddress(
								getHid(), hid));
				return locator.getEventManagerPort();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		/**
		 * No Event Manager found. Null returned
		 */
		logger.error("No Event Manager found!");
		return null;
	}

	/**
	 * Returns the configured {@link ApplicationDeviceManager} service.
	 * 
	 * @return the {@link ApplicationDeviceManager}
	 */
	public ApplicationDeviceManager getDeviceManager() {
		String url = configurator.get(CmConfigurator.DAC_URL);
		ApplicationDeviceManagerServiceLocator locator =
				new ApplicationDeviceManagerServiceLocator();
		locator.setApplicationDeviceManagerEndpointAddress(url);
		ApplicationDeviceManager devMgr;
		try {
			devMgr = locator.getApplicationDeviceManager();
			return devMgr;
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Returns the configured {@link ApplicationOntologyManager}
	 * 
	 * @return the {@link ApplicationOntologyManager}
	 */
	public ApplicationOntologyManager getOntologyManager() {
		String useLocal =
				configurator.get(CmConfigurator.ONTOLOGY_USE_LOCAL)
						.toLowerCase();

		if (("yes".equals(useLocal) || "true".equals(useLocal))) {
			/**
			 * Get Local OSGi instance of Ontology Manager. If not available,
			 * use URL.
			 */
			ServiceReference ref =
					context.getBundleContext().getServiceReference(
							ApplicationOntologyManager.class.getName());
			if (ref != null) {
				logger.info("Retrieved local OSGi Ontology Manager");
				return (ApplicationOntologyManager) context.getBundleContext()
						.getService(ref);
			}
		}

		/**
		 * If got here, then either not using local OM, or none exists. Use WS
		 * instead.
		 */
		String omUrl = configurator.get(CmConfigurator.ONTOLOGY_URL);
		ApplicationOntologyManagerServiceLocator locator =
				new ApplicationOntologyManagerServiceLocator();
		locator.setApplicationOntologyManagerServiceOSGiEndpointAddress(omUrl);
		try {
			logger.info("Retrieved OSGi Ontology Manager remote client");
			return locator.getApplicationOntologyManagerServiceOSGi();
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public ContextSpecification[] getContextSpecifications(String[] contextIds)
			throws RemoteException {
		try {
			Set<ContextSpecification> specs =
					specManager.getContextSpecifications(contextIds);
			return specs.toArray(new ContextSpecification[specs.size()]);
		} catch (ContextManagerException e) {
			throw new RemoteException("[" + e.getContextId() + "]"
					+ e.getAction() + ": " + e.getMessage());
		}
	}

	@Override
	public ContextSpecification getContextSpecification(String contextId)
			throws RemoteException {
		try {
			return specManager.getContextSpecification(contextId);
		} catch (ContextManagerException e) {
			throw new RemoteException("[" + e.getContextId() + "]"
					+ e.getAction() + ": " + e.getMessage());
		}
	}

	@Override
	public QueryResponse executeNamedQuery(String queryName,
			Parameter[] queryArgs) throws RemoteException {

		try {
			logger.info("Executing named query '" + queryName + "'");
			return queryManager.executeNamedQuery(queryName, queryArgs);
		} catch (ContextManagerException e) {
			QueryResponse errorResponse = new QueryResponse();
			ContextManagerError[] errors = { e.getError() };
			errorResponse.setErrors(errors);
			errorResponse.setResults(new QueryRow[0]);
			return errorResponse;
		}
	}

	@Override
	public QueryResponse executeSingleQuery(ContextQuery query)
			throws RemoteException {
		try {
			logger.info("Executing single query '" + query.getQuery() + "'");
			return queryManager.executeSingleQuery(query);
		} catch (ContextManagerException e) {
			QueryResponse errorResponse = new QueryResponse();
			ContextManagerError[] errors = { e.getError() };
			errorResponse.setErrors(errors);
			errorResponse.setResults(new QueryRow[0]);
			return errorResponse;
		} catch (ErrorListException e) {
			QueryResponse errorResponse = new QueryResponse();
			errorResponse.setErrors(e.getErrorArray());
			errorResponse.setResults(new QueryRow[0]);
			return errorResponse;
		}
	}

	@Override
	public QuerySet getQuerySet(String pkgName) throws RemoteException {

		try {
			return queryManager.getQuerySet(pkgName);
		} catch (ContextManagerException e) {
			throw new RemoteException("[" + e.getContextId() + "]"
					+ e.getAction() + ": " + e.getMessage());
		}
	}

	@Override
	public String[] getQuerySetPackages() throws RemoteException {
		return queryManager.getStoredPkgs();
	}

	@Override
	public ContextResponse installQuerySet(QuerySet querySet) {

		ContextResponse resp = new ContextResponse();
		try {
			queryManager.loadQuerySetToEngine(querySet);
			queryManager.storeQuerySet(querySet);
			resp.setOk(true);
			resp.setContextId(querySet.getPackageName());
			logger.info("Installed QuerySet '" + querySet.getPackageName() + "'");
		} catch (ErrorListException e) {
			logger.error("Errors installing QuerySet '" + querySet.getPackageName() + "'");
			resp.setOk(false);
			resp.setContextId(querySet.getPackageName());
			ContextManagerError[] errors =
					e.getErrorList().toArray(
							new ContextManagerError[e.getErrorList().size()]);
			resp.setErrors(errors);
			return resp;
		}
		return resp;
	}

	@Override
	public ContextResponse removeQuerySet(String name) {
		ContextResponse response = new ContextResponse();
		response.setOk(true);
		response.setContextId(name);
		response.setErrors(new ContextManagerError[0]);
		
		queryManager.removeQuerySet(name);
		logger.info("Removed QuerySet '" + name + "'");
		return response;
	}

	@Override
	public void removeContextSpecification(String contextId)
			throws RemoteException {
		logger.info("Removing ContextSpecification for '" + contextId + "'");
		
		// Cancel any subscriptions it has
		reqManager.cancelSubscriptions(contextId, true);

		// Remove the stored object
		specManager.removeContextSpecification(contextId);

		// Get Context object from working memory, and remove
		BaseContext toRemove =
				ruleEngine.getContextByContextId(BaseContext.class, contextId);
		try {
			if (toRemove != null)
				ruleEngine.remove(toRemove);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		// Remove any rules from the Rule Engine
		try {
			ruleManager.removeContextSpecification(contextId);
			logger.info("Context '" + contextId + "', removed");
		} catch (ContextManagerException e) {
			String msg = "Error removing context '" + contextId + "': " + e.toString();
			logger.error(msg);
			throw new RemoteException(msg);
		}
		
		ruleEngine.fireAllRules();
	}

	@Override
	public boolean reportAcquiredData(DataReport dataReport)
			throws RemoteException {
		return reqManager.handleReportedData(dataReport);
	}

	@Override
	public String[] getApplicationContextList() throws RemoteException {
		try {
			return specManager.getContextIdList();
		} catch (ContextManagerException e) {
			throw new RemoteException("[" + e.getContextId() + "]"
					+ e.getAction() + ": " + e.getMessage());
		}
	}

	@Override
	public String getCurrentContext(String contextId) {
		BaseContext context =
				 ruleEngine.getContextByContextId(
						BaseContext.class, contextId);
		if (context == null) {
			return "";
		} else {
			return context.toString();
		}
	}

	@Override
	public ContextResponse installApplicationContext(
			ContextSpecification specification, String hid)
			throws RemoteException {
		/**
		 * 1. Create application stubs - also getting information from Ontology
		 * 2. Process requirements / subscriptions 3. Parse ContextRuleSet, and
		 * insert into Rule Engine 4. Insert Application Context
		 */
		
		logger.info("Installing Application context for " + hid);

		// Create response base
		ContextResponse response = new ContextResponse();
		response.setOk(true);
		response.setErrors(new ContextManagerError[0]);
		response.setContextId("");
		try {
			// Create Application Context - includes generation of contextId
			Application appCtx =
					appCtxManager.processContextSpecification(specification);
			appCtx.setHid(hid);
			String contextId = appCtx.getContextId();
			response.setContextId(contextId);
			
			// Build Specification Rule DRL if ctx has rules / types / functions
			boolean toCompile = contextHasRules(specification);
			CompiledRulePackage compiled = null;
			if (toCompile)
				compiled =
						ruleManager.compileContextSpecification(specification,
								contextId);

			// Store Specification XML 
			specManager.storeContextSpecification(contextId, specification);

			// Pass to ApplicationContextManager to resolve requirements
			DaqcSubscription sub =
					appCtxManager.processApplicationSubscriptions(appCtx,
							specification);
			// if subscription not null, pass to requirements manager to set up
			if (sub != null)
				reqManager.processSubscription(contextId, sub);

			// Insert Application context
			ruleEngine.insert(appCtx);

			// Finally add compiled Rule Package to rule engine
			if ((toCompile) && (compiled != null)){
				logger.info("Installed compiled rule package: " + compiled.getEntry()[1]);
				ruleManager.installCompiledRulePackage(compiled);
				ruleEngine.fireAllRules();
			}
			
			logger.info("Application context successfully installed");

		} catch (ContextManagerException e) {
			logger.error("Errors installing Application context: " + e.toString());
			response.setOk(false);
			ContextManagerError[] errors = { e.getError() };
			response.setErrors(errors);

		} catch (ErrorListException e) {
			logger.error("Errors installing Application context: " + e.toString());
			response.setOk(false);
			response.setErrors(e.getErrorArray());
		} catch (Exception e) {
			logger.error("Exception installing Application context: " + e.toString(), e);
			throw new RemoteException(e.getMessage());
		}
		return response;
	}
	
	@Override
	public String installApplicationContextAsXml(String specification,
			String hid) throws RemoteException {
		try {
			logger.info("Building ContextSpecifcation from XML - " + specification);
			ContextSpecification spec = ContextBuilderFactory.buildContext(specification);
			ContextResponse response = installApplicationContext(spec, hid);
			if (response.getErrors().length == 0){
				return response.getContextId();
			}
			else{
				
				StringBuffer sb = new StringBuffer("Error installing Application Context from Xml: \n");
				for (ContextManagerError error : response.getErrors()){
					sb.append(error.getErrorId()).append(" : ");
					sb.append(error.getErrorSubject()).append(" : ");
					sb.append(error.getErrorDescription()).append("\n");
				}
				logger.error(sb.toString());
				throw new RemoteException(sb.toString());
			}
		} catch (IOException e) {
			String msg = "IOException: " + e.toString();
			logger.error(msg);
			throw new RemoteException(msg);
		} catch (XmlMarshallingException e) {
			String msg = "Error marshalling ContextSpecification provided as XML: " + e.getMessage();
			logger.error(msg);
			throw new RemoteException(msg);
		}
	}

	@Override
	public Boolean notify(String topic, Part[] parts) throws RemoteException {
		return ctxEventManager.handleEvent(topic, parts);
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
	 * Called by the {@link CmConfigurator} when changes have been made to the
	 * configurations. <p> If the PID of the Context Manager is changed, the
	 * CryptoHID is renewed - but first all DAqC subscriptions are cancelled,
	 * and then renewed when the new HID is established.
	 * 
	 * @param updates
	 *            {@link Hashtable} containing changed configurations and their
	 *            new values
	 */
	public void applyConfigurations(Hashtable updates) {
		// if PID changed
		// cancel all subs
		// renew cryptoHid
		// renew all subs
		if (updates.containsKey("PID")) {
			logger.info("ContextManager PID updated. Renewing all...");
			reqManager.cancelAllSubscriptions(false);

			try {
				this.hid = hidManager.renewService(true);
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
			try {
				reqManager.renewAllSubscriptions();
				deviceCtxManager.restartAppDeviceManagerListener();

			} catch (ErrorListException e) {
				logger.error("Error renewing " + e.getErrorList().size()
						+ " subscriptions to Daqc: " + e.toString());
			} catch (ContextManagerException e) {
				logger.error("Error renewing subscrriptions: " + e.toString());
			}
		}
	}

	/**
	 * Gets the {@link CmConfigurator}
	 * 
	 * @return the {@link CmConfigurator}
	 */
	public CmConfigurator getConfigurator() {
		return configurator;
	}

	/**
	 * Gets the {@link ComponentContext}
	 * 
	 * @return the {@link ComponentContext}
	 */
	public ComponentContext getComponentContext() {
		return context;
	}

	/**
	 * Returns the URL address for making a call through the Network Manager
	 * SOAPTunnelling from the Context Manager, to the given Hid
	 * 
	 * @param toHid
	 *            the HID to send to
	 * @param sessionId
	 *            the sessionId
	 * @return the endpoint url (as String) for the SOAP Tunneling
	 */
	public String getSoapTunnellingAddress(String toHid, String sessionId) {
		return "http://localhost:" + httpPort + "/SOAPTunneling/" + getHid()
				+ "/" + toHid + "/" + sessionId + "/hola";
	}

	/**
	 * Queries the Network Manager with the given query, to retrieve a single
	 * matching HID. If none found, null is returned
	 * 
	 * @param query
	 *            the query
	 * @return the HID found
	 * @throws ContextManagerException
	 */
	public String getHidMatchingQuery(String query)
			throws ContextManagerException {
		try {
			logger.info("Querying NetworkManager for HID: " + query);
			String[] results =
					nm.getHIDByAttributes(getHid(), getPid(), query, 1000, 1);
			if (results.length == 0)
				return null;
			return results[0];
		} catch (RemoteException e) {
			throw new ContextManagerException("",
					"Error retrieving Hid for query '" + query
							+ "' from Network Manager", e.getLocalizedMessage());
		}
	}

	/**
	 * Scans the {@link ContextSpecification} to see if it contains any
	 * {@link Rule}s, {@link DeclaredFunction}s or {@link DeclaredType}s to
	 * create a Rule Package with
	 * 
	 * @param spec
	 *            the {@link ContextSpecification}
	 * @return the decision
	 */
	private boolean contextHasRules(ContextSpecification spec) {
		if (spec.getRuleSet().getRules().length > 0)
			return true;
		if (spec.getRuleSet().getFunctions().length > 0)
			return true;
		if (spec.getRuleSet().getTypes().length > 0)
			return true;
		return false;

	}

	@Override
	public void setApplicationContextMember(String contextId, String memberKey,
			String value) throws RemoteException {
		/**
		 * 1. Get the application context 2. Get the member with the key 3.
		 * Change the value 4. insert and update
		 */

		Application app =
				(Application) ruleEngine.getContextByContextId(
						Application.class, contextId);
		if (app == null)
			throw new RemoteException("Application Context does not exist");
		ContextMember member = app.getMember(memberKey);

		if (member == null)
			throw new RemoteException(
					"Context does not have member with this key");

		member.setStrValue(value);
		logger.info("Updated ContextMember: contextId=" + contextId 
				+ ", memberId=" + memberKey);
		ruleEngine.update(member);
		ruleEngine.update(app);
		ruleEngine.fireAllRules();
	}

	@Override
	public String DACcallback(String action, String device) {

		return deviceCtxManager.handleDACcallback(action, device);
	}

	@Override
	public String[] getContextIdsMatchingQuery(String query)
			throws RemoteException {
		try {
			return queryManager.executeContextIdQuery(query);
		} catch (ContextManagerException e) {
			throw new RemoteException(e.getContextId() + " : " + e.getAction()
					+ " : " + e.getMessage());
		} catch (ErrorListException e) {
			String msg = "";
			Iterator<ContextManagerError> it = e.getErrorList().iterator();
			while (it.hasNext()) {
				ContextManagerError error = it.next();
				msg =
						msg + error.getErrorId() + " : "
								+ error.getErrorSubject() + " : "
								+ error.getErrorDescription();
				if (it.hasNext())
					msg = msg + "\n";
			}
			throw new RemoteException(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.caf.cm.ContextManager#executeQuery(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String executeQuery(String query) throws RemoteException {
		
		logger.info("Executing query: " + query);
		ContextQuery theQuery = new ContextQuery();
		theQuery.setQuery(query);
		theQuery.setName("QueryAsString");

		QueryResponse response = this.executeSingleQuery(theQuery);
		if (response.getErrors().length > 0) {
			String errorMessage = "Error(s) executing String query: \n";
			for (ContextManagerError error : response.getErrors()) {
				errorMessage +=
						error.getErrorId() + " : " + error.getErrorSubject()
								+ " : " + error.getErrorDescription() + "\n";
			}
			errorMessage = errorMessage.substring(0, errorMessage.length() - 3);
			logger.error(errorMessage);
			throw new RemoteException(errorMessage);
		} else {
			if (response.getResults().length == 1) {
				return response.getResults()[0].getResultContent();
			} else if (response.getResults().length == 0) {
				return "";
			} else if (response.getResults().length > 1) {
				logger.warn("Multiple Query rows found! Only returning first.");
				return response.getResults()[0].getResultContent();
			}

		}
		return "";
	}

	

}
