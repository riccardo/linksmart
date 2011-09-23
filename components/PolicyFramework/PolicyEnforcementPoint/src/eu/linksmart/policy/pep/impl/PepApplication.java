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
package eu.linksmart.policy.pep.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;

import eu.linksmart.network.NetworkManagerApplication;
import com.sun.xacml.Obligation;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.ctx.Subject;

import eu.linksmart.policy.pdp.LocalPolicyDecisionPoint;
import eu.linksmart.policy.pdp.PolicyDecisionPoint;
import eu.linksmart.policy.pdp.client.PolicyDecisionPointServiceLocator;
import eu.linksmart.policy.pep.PepResponse;
import eu.linksmart.policy.pep.PepService;
import eu.linksmart.policy.pep.cache.impl.PdpSessionCache;
import eu.linksmart.policy.pep.impl.ComponentHidManager;
import eu.linksmart.policy.pep.impl.PdpStub;
import eu.linksmart.policy.pep.impl.PepApplication;
import eu.linksmart.policy.pep.impl.PepConfigurator;
import eu.linksmart.policy.pep.impl.PepXacmlConstants;
import eu.linksmart.policy.pep.impl.PropertyParser;
import eu.linksmart.policy.pep.request.SoapAttrParser;
import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.request.impl.StaxSoapAttrParser;
import eu.linksmart.policy.pep.response.PepObligationObserver;
import eu.linksmart.policy.pep.response.impl.OsgiTrackerPepObligationObserver;
import eu.linksmart.policy.pep.response.impl.SessionCachePepObligationObserver;

/**
 * <p>Default {@link PepService} implementation</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 */
public class PepApplication implements PepService {
	
	/** logger */
	private static final Logger logger = Logger.getLogger(PepApplication.class);
	
	/** preset default HTTP port */
	private static final String DEFAULT_HTTP_PORT = "8082";
	
	/** OSGI HTTP port property ID */
	private static final String OSGI_PORT_NAME
			= "org.osgi.service.http.port";
	
	/** PDP access denied <code>String</code> */
	private static final String PDP_RESPONSE_DENY
			= "PdpAccessDenied";
	
	/** PDP missing <code>String</code> */
	private static final String PDP_RESPONSE_MISSING
			= "PdpMissing";
	
	/** SID key */
	private static final String SID_KEY = "SID";
	
	/** {@link BundleContext} */
	private BundleContext bundleContext = null;
	
	/** {@link NetworkManagerApplication} */
	private NetworkManagerApplication nm = null;
	
	/** {@link PepConfigurator} */
	private PepConfigurator configurator = null;
	
	/** {@link ComponentHidManager} */
	private ComponentHidManager hidManager = null;
	
	/** {@link PdpSessionCache} */
	private PdpSessionCache pdpSessionCache = null;
	
	/** {@link SoapAttrParser} */
	private SoapAttrParser soapAttrExtractor = new StaxSoapAttrParser();
	
	/** {@link PropertyParser} */
	private PropertyParser propertyParser = new PropertyParser();
	
	/** {@link PepObligationObserver}s */
	private List<PepObligationObserver> obligationObservers
			= new ArrayList<PepObligationObserver>();
	
	/** flag indicating whether to use PDP session buffer */
	private boolean usePdpSessionCache = false;
	
	/** flag indicating whether to deny on unfulfilled obligations */
	private boolean denyOnUnfulfilledObligations = true;
	
	/**
	 *  flag indicating whether to stop evaluating further 
	 *  {@link PepObligationObserver}s once an {@link Obligation} has been 
	 *  successfully evaluated
	 */
	private boolean doLazyObligationEvaluation = false;
	
	/** 
	 * flag indicating whether to default to deny for all "Non permit"
	 * PDP responses
	 */
	private boolean defaultToDeny = false;
	
	/** PEP HID */
	private String pepHid = "0.0.0.0";

	/** HTTP communication port */
	private String httpPort = DEFAULT_HTTP_PORT;
	
	/** {@link PdpStub} */
	private PdpStub pdpStub = null;
	
	/** local {@link PolicyDecisionPoint} */
	private LocalPolicyDecisionPoint localPdp = null;
	
	/** state flag indicating whether PEP has been activated */
	private boolean activated = false;
	
	/** 
	 * flag indicating whether to use local session IDs in place of LinkSmart 
	 * sessions
	 */
	private boolean useLocalSessions = false;
	
	/**
	 * @return
	 * 				the {@link NetworkManagerApplication}
	 */
	public NetworkManagerApplication getNM() {
		if (nm == null) {
			// get NM Service
			ServiceTracker tracker = new ServiceTracker(bundleContext, 
					NetworkManagerApplication.class.getName(), null);
			try {
				tracker.open();
				nm = (NetworkManagerApplication) tracker.waitForService(0);
				tracker.close();
			}
			catch (Exception e) {
				logger.error("Error getting Network Manager OSGi Service: " 
						+ e.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", e);
				}
			}
		}
		return nm;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.PepService#requestAccessDecision(
	 * 		java.lang.String, java.lang.String, java.lang.String, 
	 * 		java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public PepResponse requestAccessDecision(final String theSndHid,
			final String theSndCert, final String theRecHid, 
			final String theRecCert, final String theSoapMsg, 
			final String theSessionId) {
		Set<Attribute> actionAttrs = soapAttrExtractor
				.extractActionsFromSoapMsg(theSoapMsg);
		return evalAccessRequest(new PepRequest(actionAttrs, 
				extrActionAsStringFromAttrs(actionAttrs),
				theSndHid, theSndCert, theRecHid, theRecCert, 
				(useLocalSessions) ? buildLocalSessionId(theSndHid, theRecHid) 
						: theSessionId));
	}


	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.PepService#requestAccessDecisionWMethod(
	 * 		java.lang.String, java.lang.String, java.lang.String, 
	 * 		java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public PepResponse requestAccessDecisionWMethod(final String theSndHid, 
			final String theSndCert, final String theRecHid, 
			final String theRecCert, final String theMethodCall, 
			final String theSessionId) {
		Set<Attribute> actionAttrs = extrActionFromCall(theMethodCall);
		return evalAccessRequest(new PepRequest(actionAttrs, 
				extrActionAsStringFromAttrs(actionAttrs),
				theSndHid, theSndCert, theRecHid, theRecCert, 
				(useLocalSessions) ? buildLocalSessionId(theSndHid, theRecHid) 
						: theSessionId));
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.PepService#flushCache()
	 */
	@Override
	public void flushCache() {
		logger.debug("Flushing PDP response cache");
		if ((usePdpSessionCache) && (pdpSessionCache != null)) {
			pdpSessionCache.getSessionMemory().flush();
		}
		
	}

	/**
	 * <p>Changes configuration parameters, for example when a property changes 
	 * and some action has to be taken</p>
	 * 
	 * <p>If properties are read dynamically, nothing needs to be done here.</p>
	 * 
	 * @param theUpdates
	 * 				the configuration updates
	 */
	public synchronized void applyConfigurations(Hashtable<?, ?> theUpdates) {
		if (theUpdates == null) {
			return;
		}
		logger.info("Configuring");
		flushCache();
		if (theUpdates.containsKey(PepConfigurator.PEP_USE_PDP_CACHING)) {
			usePdpSessionCache = Boolean.valueOf(configurator.get(
						PepConfigurator.PEP_USE_PDP_CACHING)).booleanValue();
			logger.debug("PDP session caching: " + usePdpSessionCache);
		}
		if (usePdpSessionCache) {
			pdpSessionCache = new PdpSessionCache();
			if (theUpdates.containsKey(PepConfigurator.PEP_CACHE_LIFETIME)) {
				pdpSessionCache.getSessionMemory().setLifetime(
						Long.valueOf(configurator.get(
								PepConfigurator.PEP_CACHE_LIFETIME)).longValue());			
				logger.debug("PDP session caching lifetime: "
						+ pdpSessionCache.getSessionMemory().getLifetime());
			}
			if (theUpdates.containsKey(PepConfigurator.PEP_CACHE_KEEPALIVE)) {
				pdpSessionCache.getSessionMemory().setKeepAlive(
						Boolean.valueOf(configurator.get(
								PepConfigurator.PEP_CACHE_KEEPALIVE))
										.booleanValue());
				logger.debug("PDP session caching keepalive: "
						+ pdpSessionCache.getSessionMemory().getKeepAlive());
			}
		}
		if (theUpdates.containsKey(
				PepConfigurator.PEP_DEFAULT_TO_DENY_RESPONSE)) {
			defaultToDeny = Boolean.valueOf(configurator.get(
					PepConfigurator.PEP_DEFAULT_TO_DENY_RESPONSE)).booleanValue();
			logger.debug("Default to DENY response: " + defaultToDeny);
		}
		// read configuration parameters for obligation handling
		if (theUpdates.containsKey(
				PepConfigurator.PEP_DENY_ON_UNFULFILLED_OBLIGATION)) {
			denyOnUnfulfilledObligations = Boolean.valueOf(configurator.get(
					PepConfigurator.PEP_DENY_ON_UNFULFILLED_OBLIGATION))
							.booleanValue();
			logger.debug("Deny on unfulfilled obligation: " 
					+ denyOnUnfulfilledObligations);
		}
		if (theUpdates.containsKey(PepConfigurator.PEP_LAZY_OBLIGATION_HANDLING)) {
			doLazyObligationEvaluation = Boolean.valueOf(configurator.get(
					PepConfigurator.PEP_LAZY_OBLIGATION_HANDLING))
							.booleanValue();
			logger.debug("Lazy obligation evaluation: " 
					+ doLazyObligationEvaluation);
		}
		if (theUpdates.containsKey(PepConfigurator.PEP_USE_LOCAL_SESSIONS)) {
			useLocalSessions = Boolean.valueOf(configurator.get(
					PepConfigurator.PEP_USE_LOCAL_SESSIONS)).booleanValue();
			logger.debug("Using local sessions: " + useLocalSessions);
		}		
		// initialize default ObligationObservers
		obligationObservers.clear();
		obligationObservers.add(new SessionCachePepObligationObserver(
				usePdpSessionCache,	pdpSessionCache));
		obligationObservers.add(
				new OsgiTrackerPepObligationObserver(bundleContext));
		
		if (theUpdates.contains(PepConfigurator.PEP_PID)) {
			try {
				hidManager.renewService(PepConfigurator.PEP_SERVICE_PID, "", 
						true, pepHid);
			} catch (IOException ioe) {
				logger.error("Error renewing service: " 
						+ ioe.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", ioe);
				}
			}
		}
		logger.info("Configured");
	}

	/**
	 * @return
	 * 				the {@link PepConfigurator} or <code>null</code>
	 */
	public PepConfigurator getConfigurator() {
		return configurator;
	}
	
	/**
	 * @return
	 * 				the {@link SoapAttrParser}
	 */
	public SoapAttrParser getAttributeParser() {
		return soapAttrExtractor;
	}
	
	/**
	 * @param theParser
	 * 				the {@link SoapAttrParser} to set
	 * @throws IllegalArgumentException
	 * 				if the argument <code>theParser</code> is 
	 * 				<code>null</code>
	 */
	public void setAttrParser(SoapAttrParser theParser) {
		if (theParser == null) {
			throw new IllegalArgumentException("SoapAttrParser may "
					+ "not be null");
		}
		soapAttrExtractor = theParser;
	}
	
	/**
	 * @return
	 * 				the {@link PdpSessionCache}
	 */
	public PdpSessionCache getSessionCache() {
		return pdpSessionCache;
	}
	
	/**
	 * @param theCache
	 * 				the {@link PdpSessionCache}
	 */
	public void setSessionCache(PdpSessionCache theCache) {
		pdpSessionCache  = theCache;
	}
	
	/**
	 * @return
	 * 				the flag indicating whether a session cache is used
	 */
	public boolean isUseSessionCache() {
		return usePdpSessionCache;
	}
	
	/**
	 * @param theFlag
	 * 				a flag determining whether a session cache is to be used
	 */
	public void setUseSessionCache(boolean theFlag) {
		usePdpSessionCache = theFlag;
	}
	
	/**
	 * @return
	 * 				a reference to the {@link PepObligationObserver}s 
	 */
	public List<PepObligationObserver> getObligationObservers() {
		return obligationObservers;
	}
	
	/**
	 * @param theObservers
	 * 				the {@link PepObligationObserver}s
	 * @throws IllegalArgumentException
	 * 				if the argument <code>theObservers</code> is 
	 * 				<code>null</code>
	 */
	public void setObligationObservers(
			List<PepObligationObserver> theObservers) {
		if (theObservers == null) {
			throw new IllegalArgumentException("ObligationObserver list may "
					+ "not be null");
		}
		obligationObservers = theObservers;
	}
	
	/**
	 * Activates instance in bundle
	 * 
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
		if (theContext == null) {
			logger.error("Activation attempt without context");
			return;
		}
		bundleContext = theContext.getBundleContext();
		nm = (NetworkManagerApplication) theContext.locateService(
				"NetworkManager");
		configurator = new PepConfigurator(bundleContext, this);
		configurator.registerConfiguration();
		hidManager = new ComponentHidManager(this);
		activated = true;
		Hashtable activationConf = new Hashtable();
		String val = configurator.get(PepConfigurator.PEP_USE_PDP_CACHING);
		if (val != null) {
			activationConf.put(PepConfigurator.PEP_USE_PDP_CACHING, val); 
		}
		val = configurator.get(PepConfigurator.PEP_CACHE_LIFETIME);
		if (val != null) {
			activationConf.put(PepConfigurator.PEP_CACHE_LIFETIME, val);
		}
		val = configurator.get(PepConfigurator.PEP_CACHE_KEEPALIVE);
		if (val != null) {
			activationConf.put(PepConfigurator.PEP_CACHE_KEEPALIVE, val);
		}
		val = configurator.get(PepConfigurator.PEP_DEFAULT_TO_DENY_RESPONSE);
		if (val != null) {
			activationConf.put(PepConfigurator.PEP_DEFAULT_TO_DENY_RESPONSE,
					val);
		}
		val = configurator.get(
				PepConfigurator.PEP_DENY_ON_UNFULFILLED_OBLIGATION);
		if (val != null) {
			activationConf.put(
					PepConfigurator.PEP_DENY_ON_UNFULFILLED_OBLIGATION, val);
		}
		val = configurator.get(PepConfigurator.PEP_LAZY_OBLIGATION_HANDLING);
		if (val != null) {
			activationConf.put(PepConfigurator.PEP_LAZY_OBLIGATION_HANDLING,
					val);
		}
		val = configurator.get(PepConfigurator.PEP_USE_LOCAL_SESSIONS);
		if (val != null) {
			activationConf.put(PepConfigurator.PEP_USE_LOCAL_SESSIONS, val);
		}
		applyConfigurations(activationConf);
		// initialize PEP and register with NM
		try {
			pepHid = hidManager.registerService(
					PepConfigurator.PEP_SERVICE_PID, "");
		} catch (Exception e) {
			logger.error("Error creating CryptoHID: " 
					+ e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
		}
		logger.info("Activated");
	}
	
	/**
	 * Deactivates instance in bundle
	 * 
	 * @param theContext
	 * 				the {@link ComponentContext}
	 */
	protected void deactivate(ComponentContext theContext) {
		logger.debug("Deactivating");
		hidManager.unregisterService(pepHid);
		logger.debug("Deactivated");
	}

	/**
	 * Binds configuration
	 * 
	 * @param theCm
	 * 				the {@link ConfigurationAdmin}
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
	 * Unbinds configuration
	 * 
	 * @param theCm
	 * 				the {@link ConfigurationAdmin}
	 */
	protected void configurationUnbind(ConfigurationAdmin theCm) {
		configurator.unbindConfigurationAdmin(theCm);
	}
	
	/**
	 * @param theSenderHid
	 * 				the sender HID
	 * @param theReceiverHid
	 * 				the receiver HID
	 * @return
	 * 				a dummy session ID
	 */
	private String buildLocalSessionId(String theSenderHid, 
			String theReceiverHid) {
		return theSenderHid + "::" + theReceiverHid;
	}
	
	/**
	 * Returns subject attributes for the argument sender HID and sender 
	 * attributes
	 * 
	 * @param theSenderHid
	 * 				the sender HID
	 * @param theSenderAttrs
	 * 				the sender attributes
	 * @return
	 * 				the {@link Subject}s
	 */
	private Set<Subject> extrSubject(String theSenderHid, 
			Properties theSenderAttrs) {
		HashSet<Attribute> attrs = new HashSet<Attribute>();
		Attribute attr = new Attribute(
				URI.create(PepXacmlConstants.SUBJECT_SUBJECT_ID.getUrn()),
                null,
                new DateTimeAttribute(),
                new StringAttribute(theSenderHid));
		attrs.add(attr);
		if (theSenderAttrs != null) {
			Enumeration<Object> keys = theSenderAttrs.keys();
			while(keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = theSenderAttrs.getProperty(key);
				attr = new Attribute(
						URI.create(PepXacmlConstants.SUBJECT_LINK_SMART_PREFIX
								.getUrn() + key.toLowerCase()), 
		                null,
		                new DateTimeAttribute(),
		                new StringAttribute(value));
				attrs.add(attr);
			}
		}
		HashSet<Subject> subject = new HashSet<Subject>();
		subject.add(new Subject(attrs));
		return subject;
	}
	
	/**
	 * @param theMethod
	 * 				the method call
	 * @return
	 * 				the {@link Attribute} <code>Set</code>
	 */
	private Set<Attribute> extrActionFromCall(String theMethod){
		HashSet<Attribute> attrs = new HashSet<Attribute>();
		Attribute attr = new Attribute(
				URI.create(PepXacmlConstants.ACTION_ACTION_ID.getUrn()), 
                null,
                new DateTimeAttribute(),
                new StringAttribute(theMethod));
		attrs.add(attr);
		return attrs;
	}
	
	/**
	 * @param theAttrs
	 * 				the {@link Attribute} <code>Set</code>
	 * @return 
	 * 				the <code>String</code>-encoded action attributes or 
	 * 				<code>null</code>
	 */
	private String extrActionAsStringFromAttrs(Set<Attribute> theAttrs) {
		if ((theAttrs != null) && (!theAttrs.isEmpty())) {
			String[] orderedAttributes = new String[theAttrs.size()];
			int i = 0;
			for (Attribute attribute : theAttrs) {
				orderedAttributes[i] = attribute.encode();
				i++;
			}
			if (orderedAttributes.length > 1) {
				Arrays.sort(orderedAttributes);
			}
			String actionMsg = "<ActionAttributes>";
			StringBuffer actionMsgBuff = new StringBuffer();
			actionMsgBuff.append("<ActionAttributes>");
			int oal = orderedAttributes.length;
			for (i=0; i < oal; i++) {
				actionMsg += orderedAttributes[i];
				actionMsgBuff.append(orderedAttributes[i]);
			}
			return actionMsgBuff.append("</ActionAttributes>").toString();
		}
		return null;
	}
	
	/**
	 * @param theReceiverHid
	 * 				the receiver HID
	 * @param theRecAttrs
	 * 				the receiver attributes
	 * @return
	 * 				the {@link Attribute} <code>Set</code>
	 */
	private Set<Attribute> extrResource(String theReceiverHid, 
			Properties theRecAttrs) {
		Set<Attribute> attrs = new HashSet<Attribute>();
		Attribute attr = new Attribute(
				URI.create(PepXacmlConstants.RESOURCE_RESOURCE_ID.getUrn()), 
                null,
                new DateTimeAttribute(),
                new StringAttribute(theReceiverHid));
		attrs.add(attr);
		if (theRecAttrs != null) {
			Enumeration<Object> keys = theRecAttrs.keys();
			while(keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = theRecAttrs.getProperty(key);
				attr = new Attribute(URI.create(
						PepXacmlConstants.RESOURCE_LINK_SMART_PREFIX.getUrn()
								+ key.toLowerCase()), 
		                null,
		                new DateTimeAttribute(),
		                new StringAttribute(value));
				attrs.add(attr);
			}
		}
		return attrs;
	}

	/**
	 * Utility method to retrieve generic locally created attributes (e.g. 
	 * time stamps for incoming requests)
	 * 
	 * @return
	 * 				the {@link Attribute} <code>Set</code>
	 * 				
	 */
	private Set<Attribute> extrEnv() {
		return new HashSet<Attribute>();
	}	

	/**
	 * Checks session cache
	 * 
	 * @param sndHid
	 * 			the sender HID
	 * @param sndProps
	 * 			the sender properties extracted from CryptoHID certificate
	 * @param recHid
	 * 			the received HID
	 * @param recProps
	 * 			the receiver properties extracted from CryptoHID certificates
	 * @param message
	 * 			the SOAP message or method call
	 * @param sessionId
	 * 			the session ID
	 * @return
	 * 			the {@link ResponseCtx} if available data has been sufficient to 
	 * 			evaluate given the session ID, <code>null</code> otherwise
	 */
	private ResponseCtx qrySessionCache(String sndHid, Properties sndProps,
			String recHid, Properties recProps, String message, 
			String sessionId) {
		if (message != null) {
			return pdpSessionCache.evaluate(sessionId, sndHid, 
					recHid, message, System.currentTimeMillis());
		}		
		// default is no decision based on session ID data
		return null;
	}
	
	/**
	 * @param theRequest
	 * 				the {@link RequestCtx}
	 * @return
	 * 				the PDP {@link ResponseCtx}
	 */
	@SuppressWarnings({"unchecked" })
	private ResponseCtx qryPdp(RequestCtx theRequest) {
		try {						
            PolicyDecisionPoint pdp = getPdp();
            if (localPdp != null) {
                return localPdp.evaluateLocally(theRequest);
            }
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			theRequest.encode(baos);
            String reqXml = baos.toString();
            if (pdp != null) {
                String respXml = pdp.evaluate(reqXml);
                return ResponseCtx.getInstance(
                		new ByteArrayInputStream(respXml.getBytes()));
            }
		}
		catch (Exception e) {
			logger.error("Error getting Access Decision from PDP: " 
					+ e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			ArrayList code = new ArrayList();
			code.add(PDP_RESPONSE_DENY);
			Status status = new Status(code, PDP_RESPONSE_DENY);
			Result res = new Result(Result.DECISION_INDETERMINATE, status);
			return new ResponseCtx(res);
		}
		logger.error("No PDP retrieved, hence no decision");
		ArrayList code = new ArrayList();
		code.add(PDP_RESPONSE_MISSING);
		Status status = new Status(code, PDP_RESPONSE_MISSING);
		Result res = new Result(Result.DECISION_INDETERMINATE, status);
		return new ResponseCtx(res);
	}
	
	/**
	 * @param theResp
	 * 				the {@link ResponseCtx}
	 * @param theRequest
	 * 				the {@link PepRequest}
	 * @return 
	 * 				the {@link PepResponse}
	 */
	private PepResponse evalPdpResponse(ResponseCtx theResp, 
			PepRequest theRequest) {
		// only handle the first result from the response
		Result result = (Result) theResp.getResults().iterator().next();
		String msg = result.getStatus().getMessage();
		if (PDP_RESPONSE_DENY.equals(msg)) {
			return new PepResponse(PepResponse.DECISION_NONE, "ERROR", 
					PepResponse.CODE_PEP_REQUEST_ERROR, 
					"Error creating Request from provided attributes");
		}
		if (PDP_RESPONSE_MISSING.equals(msg)) {
			return new PepResponse(PepResponse.DECISION_NONE, "ERROR", 
					PepResponse.CODE_PEP_REQUEST_ERROR, "No PDP result");
		}
		int decision = PepResponse.DECISION_NONE;
		String status = "ERROR";
		int statusCode = PepResponse.CODE_PEP_REQUEST_ERROR;
		String errorMsg = "No results returned";
		// rewrite INDETERMINATE and NOT_APPLICABLE if PEP defaults to deny			
		if (defaultToDeny) {
			if ((Result.DECISION_INDETERMINATE == result.getDecision()) 
					|| (Result.DECISION_NOT_APPLICABLE 
							== result.getDecision())) {
				result = new Result(Result.DECISION_DENY, 
						result.getStatus(),	result.getResource(), 
						result.getObligations());
			}
		}
		switch (result.getDecision()) {
			case Result.DECISION_PERMIT : {
				decision = PepResponse.DECISION_PERMIT;
				status = msg;
				statusCode = PepResponse.CODE_OK;
				errorMsg = "";
				break;
			}
			case Result.DECISION_DENY : {
				decision = PepResponse.DECISION_DENY;
				status = msg;
				statusCode = PepResponse.CODE_OK;
				errorMsg = "";
				break;
			}
			case Result.DECISION_INDETERMINATE : {
				decision = PepResponse.DECISION_NONE;
				status = msg;
				statusCode = PepResponse.CODE_PDP_REQUEST_INDETERMINATE;
				errorMsg = "Error at PDP - Indeterminate";
				break;
			}
			case Result.DECISION_NOT_APPLICABLE : {
				decision = PepResponse.DECISION_NONE;
				status = msg;
				statusCode = PepResponse.CODE_PDP_REQUEST_NONAPPLICABLE;
				errorMsg = "Error at PDP - No Applicable Policy found";
				break;
			}
			default: {
				// intentionally left blank
			}
		}
		boolean fulfilled = fulfillObligations(result, theRequest, theResp);
		// superfluous check, but better to be explicit here 
		if ((denyOnUnfulfilledObligations) && (!fulfilled)) {
			logger.info("One or more obligations could not be satisfied, "
					+ "setting decision to DENY");
			decision = PepResponse.DECISION_DENY;
			status = msg;
			statusCode = PepResponse.CODE_OK;
			errorMsg = "";
		}
		return new PepResponse(decision, status, statusCode, errorMsg);
	}
	
	/**
	 * Carries out all actions required by returned PDP obligations
	 * 
	 * @param theResult
	 * 				the {@link Result}
	 * @param theRequest
	 * 				the {@link PepRequest}
	 * @param theResponse
	 * 				the {@link ResponseCtx}
	 * @return
	 * 				a flag indicating whether all obligations were fulfilled
	 */
	private boolean fulfillObligations(Result theResult, PepRequest theRequest,
			ResponseCtx theResponse) {	
		for (Object obliObj : theResult.getObligations()) {
			Obligation obli = (Obligation) obliObj;
			if (theResult.getDecision() == obli.getFulfillOn()) {
				obli = complete(theRequest, obli);
				boolean success = false;
				for (PepObligationObserver observer : obligationObservers) {					
					boolean boo = observer.evaluate(obli, theRequest, 
							theResponse);
					if ((!success) && (boo))  {
						success = true;
					}
					if ((doLazyObligationEvaluation) && (success)) {
						break;
					}
				}
				if ((denyOnUnfulfilledObligations) && (!success)) {
					logger.debug("Unknown obligation: " 
							+ obli.getId().toString());
						return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Fills in values that were referenced from the request on a best effort 
	 * basis
	 * 
	 * @param theRequest
	 * 				the {@link PepRequest}
	 * @param theObligation
	 * 				the {@link Obligation}
	 * @return
	 * 				the {@link Obligation} to use
	 */
	@SuppressWarnings("unchecked")
	private Obligation complete(PepRequest theRequest, 
			Obligation theObligation) {
		List<Attribute> rsltAssignments = new ArrayList<Attribute>();
		boolean oblChanged = false;
		List<Attribute> origAssignments = theObligation.getAssignments();
		for (Attribute origAttr : origAssignments) {
			String origVal = origAttr.getValue().encode().trim();
			if (origVal.length() == 0) {
				Attribute nuAttr = null;
				for (Subject reqSub : extrSubject(theRequest.getSndHid(), 
						propertyParser.parseXml(theRequest.getSndCert()))) {
					Set<Attribute> reqAttrs = reqSub.getAttributes();
					nuAttr = queryAttributes(reqAttrs, origAttr);
					if (nuAttr != null) {
						break;
					}
				}
				if (nuAttr == null) {
					Set<Attribute> resAttrs = extrResource(
							theRequest.getRecHid(), 
							propertyParser.parseXml(theRequest.getRecCert()));
					nuAttr = queryAttributes(resAttrs, origAttr);
				}
				if (nuAttr == null) {
					nuAttr = queryAttributes(theRequest.getActionAttrs(), 
							origAttr);
				}
				if (nuAttr == null) {
					nuAttr = queryAttributes(extrEnv(), origAttr);
				}
				if (nuAttr == null) {
					rsltAssignments.add(origAttr);
				} else {
					rsltAssignments.add(nuAttr);
					oblChanged = true;
				}			
			} else {
				rsltAssignments.add(origAttr);
			}
		}
		if (oblChanged) {
			return new Obligation(theObligation.getId(),
					theObligation.getFulfillOn(), rsltAssignments);
		}
		return theObligation;
	}
	
	/**
	 * @param theAttributes
	 * 				the {@link Attribute}s
	 * @param theOrigAttr
	 * 				the query {@link Attribute}
	 * @return
	 * 				the changed {@link Attribute} or <code>null</code>
	 */
	private Attribute queryAttributes(Set<Attribute> theAttributes, 
			Attribute theOrigAttr) {
		String origKey = theOrigAttr.getId().toString();
		for (Attribute reqAttr : theAttributes) {
			if (reqAttr.getId().toString().equals(origKey)) {
				return new Attribute(theOrigAttr.getId(), theOrigAttr.getIssuer(), 
						theOrigAttr.getIssueInstant(), reqAttr.getValue());
			}
		}
		return null;
	}
	
	/**
	 * Evaluates access request
	 * 
	 * @param theRequest
	 * 				the {@link PepRequest}
	 * @return
	 * 				the {@link PepResponse}
	 */
	private PepResponse evalAccessRequest(final PepRequest theRequest) {
		// extract properties from certificates
		Properties sndProps = propertyParser.parseXml(theRequest.getSndCert());
		Properties recProps = propertyParser.parseXml(theRequest.getRecCert());
		if ((usePdpSessionCache) && (pdpSessionCache != null)) {
			// evaluate session
			ResponseCtx sesResponse = qrySessionCache(theRequest.getSndHid(), 
					sndProps, theRequest.getRecHid(), recProps, 
					theRequest.getActionAttrString(), 
					theRequest.getSessionId());
			if (sesResponse != null) {
				// session evaluation led to an actual result
				logger.debug("Returning cached decision");
				return evalPdpResponse(sesResponse, theRequest);				
			}
			// else the session cache did not return a result
		}		
		// PEP2PEP calls are permitted without PDP verification
		if (isAuthdPepToPdpCall(sndProps, recProps)) {
			return new PepResponse(PepResponse.DECISION_PERMIT, 
					"Authenticated PEP->PDP call", PepResponse.CODE_OK, "");
		}
		// PEP2PDP call are permitted without PDP verification 
		if (isReqToPdpWithCredentials(sndProps, recProps)) {
			return new PepResponse(PepResponse.DECISION_PERMIT, 
					"Call from user to PDP with application credentials " 
							+ "(user/pass)", PepResponse.CODE_OK, "");
		}		
		RequestCtx req = new RequestCtx(
				extrSubject(theRequest.getSndHid(), sndProps),
				extrResource(theRequest.getRecHid(), recProps),
				theRequest.getActionAttrs(),
				extrEnv());
		ResponseCtx pdpResponse = qryPdp(req);
		if ((usePdpSessionCache) && (pdpSessionCache != null)) {
			// we only care about the first result
			HashSet<Obligation> responseOblis = new HashSet<Obligation>();
			Result r = (Result) pdpResponse.getResults().iterator().next();
			for (Object obliObj : r.getObligations()) {
				Obligation obli = (Obligation) obliObj;
				// only complete applicable obligations to save time
				if (r.getDecision() == obli.getFulfillOn()) {
					responseOblis.add(complete(theRequest, obli));					
				}
			}
			pdpSessionCache.add(theRequest.getSessionId(), 
					theRequest.getSndHid(), theRequest.getRecHid(), 
					theRequest.getActionAttrString(), new ResponseCtx(
							new Result(r.getDecision(), r.getStatus(), 
									r.getResource(), responseOblis)), 
					System.currentTimeMillis());			
		}
		return evalPdpResponse(pdpResponse, theRequest);
	}	
	
	/**
	 * Locates and returns a {@link PdpStub}
	 * 
	 * @return
	 * 			the {@link PolicyDecisionPoint} or <code>null</code> if a PDP 
	 * 			cannot be retrieved locally or remotely
	 */
	private PolicyDecisionPoint getPdp() {
		String pdpPid = (String) configurator.getConfiguration().get(
				PepConfigurator.PDP_PID);		
		if ((pdpPid == null) || (pdpPid.equals(""))) {
			// try to find a local PDP named as per default naming convention
			try {
				String defaultPdpName = "PDP:" 
						+ InetAddress.getLocalHost().getHostName();
				logger.debug("Looking up PDP by default name: "
						+ defaultPdpName);
				String filter = "(PID=" + defaultPdpName + ")";
				ServiceReference[] refs = bundleContext.getServiceReferences(
						PolicyDecisionPoint.class.getName(), filter);
				if ((refs != null) && (refs.length > 0)) {
					PolicyDecisionPoint pdp = (PolicyDecisionPoint) 
							bundleContext.getService(refs[0]);
					if (pdp instanceof LocalPolicyDecisionPoint) {
						localPdp = (LocalPolicyDecisionPoint) pdp;
					}
					logger.info("Discovered PDP with default name: "
							+ defaultPdpName);
					configurator.setConfiguration(
							PepConfigurator.PDP_PID, defaultPdpName);
					pdpStub = new PdpStub(pdpPid, "local", pdp);
					flushCache();
					return pdpStub;
				}
			} catch (UnknownHostException uhe) {
				logger.error("Unknown host: " + uhe.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", uhe);
				}
			} catch (InvalidSyntaxException ise) {
				logger.error("Invalid syntax: " + ise.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", ise);
				}
			} 
			// if all else fails, return null
			return null;
		}		
		if ((pdpStub != null) && (pdpPid.equals(pdpStub.getPid()))) {
			return pdpStub;
		}		
		pdpStub = null;
		/*
		 * find OSGi PDP: if DAQC specified, try to match against OSGi DAQC, 
		 * get HID as fallback
		 */
		String filter = "(PID=" + pdpPid + ")"; 
		try {
			ServiceReference[] refs = bundleContext.getServiceReferences(
					PolicyDecisionPoint.class.getName(), filter);
			if (refs.length > 0) {
				PolicyDecisionPoint pdp 
						= (PolicyDecisionPoint) bundleContext.getService(refs[0]);
				if (pdp instanceof LocalPolicyDecisionPoint) {
					localPdp = (LocalPolicyDecisionPoint) pdp;
				}
				pdpStub = new PdpStub(pdpPid, "local", pdp);
				flushCache();
				return pdpStub;
			}
		} catch (InvalidSyntaxException ise) {
			logger.error("Invalid syntax: " + ise.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", ise);
			}
		}
		// no local PDP from context, get PDP HID and use a WS client
		String pdpHid = resPdpPidToHid(pdpPid);
		PolicyDecisionPointServiceLocator locator 
				= new PolicyDecisionPointServiceLocator();
		locator.setPolicyDecisionPointEndpointAddress(
				resSoapTunnelAddr(pdpHid, "0"));
		try {
			return new PdpStub(pdpPid, pdpHid, 
					locator.getPolicyDecisionPoint());
		} catch (ServiceException se) {
			logger.error("Service Exception: " + se.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", se);
			}
			return null;
		}
	}
		
	/**
	 * Checks whether the request is authenticated as being trusted
	 * 
	 * @param theSndAttrs
	 * 				the sender attributes
	 * @param theRecAttrs
	 * 				the receiver attributes
	 * @return
	 * 				the flag
	 */
	private boolean isReqToPdpWithCredentials(Properties theSndAttrs, 
			Properties theRecAttrs) {
		try {
			// get sender and receiver Service IDs
			String receiverSID = "";
			if (theRecAttrs != null) {
				if (theRecAttrs.containsKey(SID_KEY)) {
					receiverSID = theRecAttrs.getProperty(SID_KEY);
				}
			}			
			if ((receiverSID != null) 
					&& (PepConfigurator.PDP_SERVICE_PID.equals(receiverSID))) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Error in isRequestToPDPwCredentials: " 
					+ e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			return false;
		}
		return false;
	}
	
	/**
	 * Checks whether call is authenticated to access the local PDP (if there 
	 * is one)
	 * 
	 * @param theSndAttrs
	 * 				the sender attributes
	 * @param theRecAttrs
	 * 				the receiver attributes
	 * @return
	 * 				the flag
	 */
	private boolean isAuthdPepToPdpCall(Properties theSndAttrs, 
			Properties theRecAttrs) {
		try {
			// get sender and receiver Service IDs from CryptoHID attributes
			String sndSid = "";
			String recSid = "";
			if (theSndAttrs != null) {
				if (theSndAttrs.containsKey(SID_KEY)) {
					sndSid = theSndAttrs.getProperty(SID_KEY);
				}
			}
			if (theRecAttrs != null) {
				if (theRecAttrs.containsKey(SID_KEY)) {
					recSid = theRecAttrs.getProperty(SID_KEY);
				}
			}
			if ((sndSid.equals(PepConfigurator.PEP_SERVICE_PID)) 
					&& ((recSid.equals(PepConfigurator.PDP_ADMIN_SERVICE_PID)) 
							|| (recSid.equals(PepConfigurator.PDP_SERVICE_PID)))) {
				return true;
			}
		}
		catch (Exception e) {
			logger.error("Error in isAuthenticatedPEPtoPDPCall: " 
					+ e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			return false;
		}
		return false;
	}
	
	/**
	 * @param thePdpPid
	 * 				the PDP PID
	 * @return
	 * 				the PDP HID or <code>null</code> when a 
	 * 				{@link RemoteException} occurs
	 */
	private String resPdpPidToHid(String thePdpPid) {
		try {
			String query = "((PID==" + thePdpPid + ")&&(SID==" 
					+ PepConfigurator.PDP_SERVICE_PID + "))";
			String[] results = nm.getHIDByAttributes(pepHid, 
					configurator.get(PepConfigurator.PEP_PID), query, 1000, 1);
			if (results.length == 0) {
				return null;
			}
			return results[0];
		} catch (RemoteException re) {
			logger.error("Could not retrieve HID for PDP PID " + thePdpPid 
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
		return "http://localhost:" + httpPort + "/SOAPTunneling/" + pepHid + "/" 
				+ theRecHid + "/" + theSessionId + "/";
	}
	
}
