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
package eu.linksmart.policy.pdp.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.linksmart.network.NetworkManagerApplication;
import com.sun.xacml.ParsingException;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pdp.LocalPolicyDecisionPoint;
import eu.linksmart.policy.pdp.PdpAdmin;
import eu.linksmart.policy.pdp.PdpAdminError;
import eu.linksmart.policy.pdp.PolicyDecisionPoint;
import eu.linksmart.policy.pdp.admin.impl.FileSystemPdpAdminService;
import eu.linksmart.policy.pdp.admin.impl.OsgiTrackerPdpAdminService;
import eu.linksmart.policy.pdp.ext.impl.PipCore;
import eu.linksmart.policy.pdp.finder.impl.LocalFolderPolicyFinderModule;
import eu.linksmart.policy.pdp.finder.impl.OsgiTrackerPolicyFinderModule;
import eu.linksmart.policy.pep.PepCacheService;
import eu.linksmart.policy.pep.PepService;
import eu.linksmart.policy.pep.client.PepCacheServiceLocator;

/**
 * Default LinkSmart {@link PolicyDecisionPoint} implementation
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 *
 */
public class PdpApplication implements PdpAdmin, PolicyDecisionPoint, 
		LocalPolicyDecisionPoint {

	/** logger */
	private static final Logger logger = Logger.getLogger(PdpApplication.class);
	
	/** OSGI HTTP port property ID */
	private static final String OSGI_PORT_NAME
			= "org.osgi.service.http.port";
	
	/** preset default HTTP port */
	private static final String DEFAULT_HTTP_PORT = "8082";
	
	/** PDP endpoint */ 
	private static final String DECISION_ENDPOINT 
			= "http://localhost:8082/axis/services/PolicyDecisionPoint";
	
	/** PDP admin endpoint */ 
	private static final String ADMIN_ENDPOINT 
			=  "http://localhost:8082/axis/services/PdpAdmin";
	
	/** PDP policy database location in working directory (for XACML files) */
	private static final String PDP_FILEFOLDER_LOC 
			= "PolicyFramework/PolicyDecisionPoint/PolicyFolder";
	
	/** Default policy repository */
	private static final String DEFAULT_REPOSITORY = "file";
	
	/** Permitted policy repository identifiers */
	private static ArrayList<String> PERMITTED_REPOSITORIES
			= new ArrayList<String>();
	
	static {
		PERMITTED_REPOSITORIES.add("bundle");
		PERMITTED_REPOSITORIES.add("db");
		PERMITTED_REPOSITORIES.add("file");
	}
	
	/** PDP HID */
	private String pdpHID = "";
	
	/** PDP admin HID */
	private String pdpAdminHID = "";
	
	/** XML {@link DocumentBuilder} */
	private DocumentBuilder documentBuilder = null;
	
	/** XML {@link TransformerFactory} */
	private TransformerFactory transformerFactory = null;
	
	/** {@link ComponentContext} */
	private ComponentContext componentCtx = null;
	
	/** {@link BundleContext} */
	private BundleContext bundleCtx = null;
	
	/** {@link PluginLinkSmartPDP} */
	private PluginLinkSmartPDP pdp = null;
	
	/** {@link PdpAdmin} */
	private PdpAdmin pdpAdmin = null;
	
	/** {@link ComponentHidManager} */
	private ComponentHidManager hidManager = null;
	
	/** {@link NetworkManagerApplication} */
	private NetworkManagerApplication nm = null;
	
	/** {@link PdpConfigurator} */
	private PdpConfigurator configurator = null;
	
	/** state flag indicating whether the application has been activated */
	private boolean activated = false;
	
	/** HTTP communication port */
	private String httpPort = DEFAULT_HTTP_PORT;
	
	/** No-args constructor */
	public PdpApplication() {
		super();
		DocumentBuilderFactory docBuilderFactory 
				= DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringComments(true);
		transformerFactory = TransformerFactory.newInstance();
		try {
			documentBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException pe) {
			logger.error("XML document builder not initialized: " 
					+ pe.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", pe);
			}
		}
	}
	
	/**
	 * @return
	 * 				the {@link NetworkManagerApplication}
	 */
	public NetworkManagerApplication getNM() {
		if (nm == null) {
			// get NM Service
			ServiceTracker tracker = new ServiceTracker(bundleCtx, 
					NetworkManagerApplication.class.getName(), null);
			try {
				tracker.open();
				nm = (NetworkManagerApplication) tracker.waitForService(0);
				tracker.close();
			}
			catch (Exception e) {
				logger.error("PDP: Error getting Network Manager OSGi Service: " 
						+ e.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", e);
				}
			}
		}
		return nm;
	}

	@Override
	public ResponseCtx evaluateLocally(RequestCtx request) {
		return pdp.evaluate(request);
	}

	@Override
	public String evaluate(String theReqXml) throws RemoteException {
		return pdp.evaluate(theReqXml);
	}
	
	@Override
	public boolean activatePolicy(String thePolicyId) throws RemoteException {
		flushPepCache();
		if (pdpAdmin == null) {
			logger.error("No PDP admin available");
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
		}
		return pdpAdmin.activatePolicy(thePolicyId);
	}

	@Override
	public boolean deactivatePolicy(String thePolicyId) throws RemoteException {
		flushPepCache();
		if (pdpAdmin == null) {
			logger.error("No PDP admin available");
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
		}
		return pdpAdmin.deactivatePolicy(thePolicyId);
	}

	@Override
	public String[] getActivePolicyList() throws RemoteException {
		if (pdpAdmin == null) {
			logger.error("No PDP admin available");
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
		}
		return pdpAdmin.getActivePolicyList();
	}

	@Override
	public String[] getInActivePolicyList() throws RemoteException {
		if (pdpAdmin == null) {
			logger.error("No PDP admin available");
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
		}
		return pdpAdmin.getInActivePolicyList();
	}

	@Override
	public String getPolicy(String thePolicyId) throws RemoteException {
		if (pdpAdmin == null) {
			logger.error("No PDP admin available");
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
		}
		String policy = pdpAdmin.getPolicy(thePolicyId);
		if (validatePolicy(thePolicyId, policy)) {
			return policy;
		}
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	@Override
	public String getProperty(String theId) throws RemoteException {
		if (pdpAdmin == null) {
			logger.error("No PDP admin available");
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
		}
		return pdpAdmin.getProperty(theId);
	}

	@Override
	public boolean publishPolicy(String thePolicyId, String thePolicy)
			throws RemoteException {
		flushPepCache();
		String normalisedPolicy = normalisePolicy(thePolicy);
		if (pdpAdmin == null) {
			logger.error("No PDP admin available");
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
		}
		if (!validatePolicy(thePolicyId, thePolicy)) {
			/*
			 * this is thrown, because validatePolicy should throw an
			 * exception instead of returning false
			 */ 
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
		}
		return pdpAdmin.publishPolicy(thePolicyId, normalisedPolicy);
	}

	@Override
	public boolean removePolicy(String thePolicyId) throws RemoteException {
		flushPepCache();
		if (pdpAdmin == null) {
			logger.error("No PDP admin available");
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
		}
		return pdpAdmin.removePolicy(thePolicyId);
	}

	@Override
	public boolean setProperty(String theId, String theValue) 
			throws RemoteException {
		flushPepCache();
		if (pdpAdmin == null) {
			logger.error("No PDP admin available");
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
		}
		return pdpAdmin.setProperty(theId, theValue);
	}

	/**
	 * @param theUpdates
	 * 				the configuration update <code>Hashtable</code>
	 */
	@SuppressWarnings("unchecked")
	public void applyConfigurations(Hashtable<?, ?> theUpdates) {
		if ((theUpdates == null) || (theUpdates.size() == 0)) {
			return;
		}
		logger.info("Configuring");
		Set<String> keys = (Set<String>) theUpdates.keySet();
		if (keys.contains(PdpConfigurator.PDP_POLICY_REPOSITORY)) {
			logger.debug("Policy location has changed, reactivating bundle");
			flushPepCache();
			deactivate(componentCtx);
			activate(componentCtx);
			return;
		}
		if (keys.contains(PdpConfigurator.PDP_PID)) {
			try {
				hidManager.renewService(PdpConfigurator.PDP_SERVICE_PID, 
						DECISION_ENDPOINT, true, pdpHID);
			} catch (IOException ioe) {
				logger.error("Error renewing service: " 
						+ ioe.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", ioe);
				}
			}
		}
		if (keys.contains(PdpConfigurator.PDPADMIN_PID)) {
			try {
				hidManager.renewAdminService(
						PdpConfigurator.PDPADMIN_SERVICE_PID,
						DECISION_ENDPOINT, true, pdpAdminHID);
			} catch (IOException ioe) {
				logger.error("Error renewing service: " 
						+ ioe.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", ioe);
				}
			}
		}
		if (keys.contains(PdpConfigurator.PEP_CACHE_SESSION_LIFETIME)) {
			pdp.setSessionLifetime(Long.valueOf((String) theUpdates.get(
					PdpConfigurator.PEP_CACHE_SESSION_LIFETIME)).longValue());
			logger.debug("Configured session lifetime as " 
					+ pdp.getSessionLifetime());
		}
		if (keys.contains(
				PdpConfigurator.PDP_DEFAULT_ALLOW_SESSION_CHACHING)) {
			pdp.setAllowDefaultCaching(Boolean.valueOf((String) theUpdates.get(
					PdpConfigurator.PDP_DEFAULT_ALLOW_SESSION_CHACHING))
							.booleanValue());
			logger.debug("Configured default session caching mode as "
					+ pdp.isAllowDefaultCaching());
		}
		logger.info("Configured");
	}
	
	/**
	 * @return
	 * 				the {@link PdpConfigurator}
	 */
	public PdpConfigurator getConfigurator() {
		return configurator;
	}

	/**
	 * @param theContext
	 * 				the {@link ComponentContext}
	 */
	@SuppressWarnings("unchecked")
	protected void activate(final ComponentContext theContext) {
		logger.info("Activating");
		httpPort = System.getProperty(OSGI_PORT_NAME);
		if (httpPort == null) {
			// only necessary when httpPort gets set to null directly above
			httpPort = DEFAULT_HTTP_PORT;
		}
		componentCtx = theContext;
		bundleCtx = theContext.getBundleContext();
		nm = (NetworkManagerApplication) theContext.locateService(
				"NetworkManager");
		configurator = new PdpConfigurator(bundleCtx, this);
		// managed service registration
		configurator.registerConfiguration();
		hidManager = new ComponentHidManager(this);
		activated = true;
		PdpDecisionConfig pdpConfig = null;
		PipCore pipCore = new PipCore(bundleCtx);
		String dataSource = (String) configurator.getConfiguration().get(
				PdpConfigurator.PDP_POLICY_REPOSITORY);
		if ((dataSource == null) 
				|| (!PERMITTED_REPOSITORIES.contains(dataSource))) {
			dataSource = DEFAULT_REPOSITORY;
		}
		if (dataSource.equalsIgnoreCase("bundle")) {
			logger.debug("Using OSGi bundle listener, ensure that a policy "
					+ "repository bundle is available");
			OsgiTrackerPdpAdminService osgiPdpAdmin
					= new OsgiTrackerPdpAdminService(bundleCtx);
			pdpConfig = new PdpDecisionConfig(pipCore, null, null, osgiPdpAdmin,
					new OsgiTrackerPolicyFinderModule(bundleCtx));
		} else {
			logger.debug("Using file policy repository");
			FileSystemPdpAdminService filePdpAdmin 
					= new FileSystemPdpAdminService(PDP_FILEFOLDER_LOC);
			pdpConfig = new PdpDecisionConfig(pipCore, null, null, filePdpAdmin, 
					new LocalFolderPolicyFinderModule(filePdpAdmin));
		} 
		// instantiate PluginLinkSmartPDP and set configuration parameters
		PluginLinkSmartPDP pluginPdp = new PluginLinkSmartPDP(pdpConfig);
		try {
			Dictionary configuration = configurator.getConfiguration();
			if (configuration.get(PdpConfigurator.PEP_CACHE_SESSION_LIFETIME) 
					!= null) {
				pluginPdp.setSessionLifetime(Long.valueOf(
						configurator.getConfiguration().get(
								PdpConfigurator.PEP_CACHE_SESSION_LIFETIME)
										.toString()).longValue());
				logger.debug("Configured session lifetime as " 
						+ pluginPdp.getSessionLifetime());
			}
			if (configuration.get(
					PdpConfigurator.PDP_DEFAULT_ALLOW_SESSION_CHACHING) 
					!= null) {
				pluginPdp.setAllowDefaultCaching(Boolean.valueOf(
						configurator.getConfiguration().get(
								PdpConfigurator.PDP_DEFAULT_ALLOW_SESSION_CHACHING)
										.toString()).booleanValue());
				logger.debug("Configured default session caching mode as "
						+ pluginPdp.isAllowDefaultCaching());
			}
		} catch (Exception e) {
			logger.warn("Exception while configuring PluginPDP: " 
					+ e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
		}
		pdp = pluginPdp;
		pdpAdmin = pdpConfig.getPdpAdminService();
		/* 
		 * publish the admin service over R-OSGi (cannot be done as DS, as the 
		 * implementing class is chosen at runtime in the code block above)
		 */
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put("service.remote.registration", "true");
		props.put("SOAP.service.name", "PdpAdmin");
		props.put("LINK_SMART.security.config", "");
		theContext.getBundleContext().registerService(PdpAdmin.class.getName(), 
				this, props);
		if (getNM() != null) {	
			try {
				pdpHID = hidManager.registerService(
						PdpConfigurator.PDP_SERVICE_PID, DECISION_ENDPOINT);
				pdpAdminHID = hidManager.registerAdminService(
						PdpConfigurator.PDPADMIN_SERVICE_PID, ADMIN_ENDPOINT);
			} catch (RemoteException re) {
				logger.error("RemoteException while registering service: "
						+ re.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", re);
				}
			} catch (IOException ioe) {
				logger.error("IOException while registering service: "
						+ ioe.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", ioe);
				}
			}
			Hashtable<String, String> pdpProps 
					= new Hashtable<String, String>();
			pdpProps.put("PID", configurator.get(PdpConfigurator.PDP_PID));
			pdpProps.put("HID", pdpHID);
			theContext.getBundleContext().registerService(
					PolicyDecisionPoint.class.getName(), this, pdpProps);
			logger.info("Activated");
		}
		else {
			logger.warn("Unable to publish PDP services to Network Manager");
		}
	}

	/**
	 * @param theContext
	 * 				the {@link ComponentContext}
	 */
	protected void deactivate(ComponentContext theContext) {
		logger.debug("Deactivating");
		if (getNM() != null) {
			hidManager.unregisterService(pdpHID);
			hidManager.unregisterAdminService(pdpAdminHID);
		}
		logger.debug("Deactivated");
	}

	/**
	 * @param theCm
	 * 				the {@link ConfigurationManager}
	 */
	protected void configurationBind(ConfigurationAdmin theCm) {
		if (configurator != null) {
			configurator.bindConfigurationAdmin(theCm);
			if (activated) {
				configurator.registerConfiguration();
			}
		}
	}

	/**
	 * @param theCm
	 * 				the {@link ConfigurationManager}
	 */
	protected void configurationUnbind(ConfigurationAdmin theCm) {
		configurator.unbindConfigurationAdmin(theCm);
	}
	
	/**
	 * Retrieves the {@link PepService} using this instance of self and 
	 * calls it's <code>flushCache</code> method
	 */
	private void flushPepCache() {
		// try to locate local OSGi PEP bundle
		ServiceReference pdpRef = componentCtx.getServiceReference();
		Bundle[] usingBundles = pdpRef.getUsingBundles();
		if (usingBundles.length != 1) {
			logger.warn("Unexpected number of using bundles found: "
					+ usingBundles.length);
			
		} else {
			Bundle pepBundle = usingBundles[0];
			ServiceReference[] refs = pepBundle.getRegisteredServices();
			int rl = refs.length;
			for (int i=0; i < rl; i++) {
				Object ref = bundleCtx.getService(refs[i]);
				if (ref instanceof PepService) {
					try {
						((PepCacheService) ref).flushCache();
						return;
					} catch (RemoteException re) {
						logger.warn("RemoteException while flushing PEP cache");
						if (logger.isDebugEnabled()) {
							logger.debug("Stack trace: ", re);
						}
						return;
					}
				}
			}
		}
		// if no local OSGi bundle was found, use a web service
		String pepPid = (String) configurator.getConfiguration().get(
				PdpConfigurator.PEP_PID);
		// only try web service when PEP PID has been set in configuration
		if ((pepPid != null) && (pepPid.length() > 0)) {
			String pepHid = resPdpPidToHid(pepPid);
			PepCacheServiceLocator pepLocator
					= new PepCacheServiceLocator();
			pepLocator.setPepCacheServiceEndpointAddress(
					resSoapTunnelAddr(pepHid, "0"));
			try {
				pepLocator.getPepCacheService().flushCache();
			} catch (RemoteException re) {
				logger.warn("Could not flush cache in remote PEP");
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", re);
				}
			} catch (ServiceException se) {
				logger.warn("Could not flush cache in remote PEP");
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", se);
				}
			}
		}
	}
	
	/**
	 * @param thePepPid
	 * 				the PEP PID
	 * @return
	 * 				the PEP HID or <code>null</code> when a 
	 * 				{@link RemoteException} occurs
	 */
	private String resPdpPidToHid(String thePepPid) {
		try {
			String query = "((PID==" + thePepPid + ")&&(SID==" 
					+ PdpConfigurator.PEP_SERVICE_PID + "))";
			String[] results = nm.getHIDByAttributes(pdpHID, 
					configurator.get(PdpConfigurator.PDP_PID), query, 1000, 1);
			if (results.length == 0) {
				return null;
			}
			return results[0];
		} catch (RemoteException re) {
			logger.error("Could not retrieve HID for PEP PID " + thePepPid 
					+ ": " + re.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", re);
			}
			return null;
		}
	}
	
	/**
	 * Returns URL for making calls through NetworkManager SoapTunnelling to the  
	 * given Hid
	 * 
	 * @param theRecHid
	 * 				the target HID
	 * @param theSessionId
	 * 				the session ID
	 * @return
	 * 				the URL string
	 */
	private String resSoapTunnelAddr(String theRecHid, String theSessionId) {
		return "http://localhost:" + httpPort + "/SOAPTunneling/" + pdpHID + "/" 
				+ theRecHid + "/" + theSessionId + "/";
	}	
	
	/**
	 * @param thePolicyId
	 * 				the policy ID
	 * @param thePolicy
	 * 				the policy XML <code>String</code>
	 * @return
	 * 				success flag
	 * @throws RemoteException
	 * 				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR
	 * 					if the validation could not be carried out
	 * 				PdpAdminError.POLICY_NOT_VALID
	 * 					if the policy is not valid
	 */
	private boolean validatePolicy(String thePolicyId, String thePolicy) 
			throws RemoteException {
		try {
			if (!pdp.validatePolicy(thePolicy)) {
				logger.error("Could not validate policy " + thePolicyId);
				throw new RemoteException(
						PdpAdminError.POLICY_NOT_VALID.toString());
			}
			return true;
		} catch (ParserConfigurationException pce) {
   			logger.error("Incorrect parser configuration: " 
   					+ pce.getLocalizedMessage());
   			if (logger.isDebugEnabled()) {
   				logger.debug("Stack trace: ", pce);
   			}
   			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString(), pce);
		} catch (SAXException se) {
			logger.error("SAX exception occured: " + se.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", se);
			}
			throw new RemoteException(
					PdpAdminError.POLICY_NOT_VALID.toString(), se);
		} catch (IOException ioe) {
			logger.error("I/O exception occured: " + ioe.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", ioe);
			}
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString(), ioe);
		} catch (ParsingException pe) {
			logger.error("Parsing exception occured: " 
					+ pe.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", pe);
			}
			throw new RemoteException(
					PdpAdminError.POLICY_NOT_VALID.toString(), pe);
		}
	}
	
	/**
	 * @param thePolicy
	 * 				the policy <code>String</code>
	 * @return
	 * 				normalised policy <code>String</code>
	 */
	private String normalisePolicy(String thePolicy) {
		try	{
			Document doc = getXmlStringAsDocument(thePolicy);
			PolicyNormaliser.normalisePolicy(doc);
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(domSource, result);
			return writer.toString();
		}
		catch (Exception e) {
			logger.error("Error processing published policy: " 
					+ e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			return thePolicy;
		}
	}
	
	/**
	 * Parses XML <code>String</code> into a {@link Document}
	 * 
	 * @param theXmlString
	 * 				the XML <code>String</code>
	 * @return
	 * 				the {@link Document}
	 * @throws ParserConfigurationException
	 * 				if thrown by {@link DocumentBuilder}
	 * @throws SAXException
	 * 				if thrown by {@link DocumentBuilder}
	 * @throws IOException
	 * 				if thrown by {@link DocumentBuilder}
	 */
	private Document getXmlStringAsDocument(String theXmlString) 
			throws ParserConfigurationException, SAXException, IOException {
		return documentBuilder.parse(new ByteArrayInputStream(
						theXmlString.getBytes("UTF8")));
	}
	
}
