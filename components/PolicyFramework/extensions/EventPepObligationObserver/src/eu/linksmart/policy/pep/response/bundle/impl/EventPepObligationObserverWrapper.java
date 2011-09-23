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
package eu.linksmart.policy.pep.response.bundle.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.eventmanager.EventManagerPort;
import eu.linksmart.eventmanager.Part;
import eu.linksmart.eventmanager.client.EventManagerPortServiceLocator;
import eu.linksmart.network.NetworkManagerApplication;
import com.sun.xacml.Obligation;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pep.impl.PepXacmlConstants;
import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.response.PepObligationObserver;

/**
 * <p>Event dispatching {@link PepObligationObserver} bundle implementation</p>
 * 
 * @author Marco Tiemann
 *
 */
public class EventPepObligationObserverWrapper 
		implements PepObligationObserver {

	/** logger */
	private static final Logger logger 
			= Logger.getLogger(EventPepObligationObserverWrapper.class);
	
	/** default HTTP port */
	private static final String DEFAULT_PORT = "8082";
	
	/** {@link NetworkManagerApplication} */
	private NetworkManagerApplication nm = null;
	
	/** {@link EventPepObligationObserverConfigurator} */
	private EventPepObligationObserverConfigurator configurator = null;
	
	/** HTTP port */
	private String httpPort = null;
	
	/** flag indicating whether the instance has been activated */
	private boolean activated = false;
	
	/** Event Manager PID */
	private String emPid = null;
	
	/** Event Manager HID */
	private String emHid = null;
	
	/** PEP PID */
	private String pepPid = null;
	
	/** PEP HID */
	private String pepHid = null;
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.response.PepObligationObserver#evaluate(
	 * 		com.sun.xacml.Obligation, 
	 * 		eu.linksmart.policy.pep.request.impl.PepRequest, 
	 * 		com.sun.xacml.ctx.ResponseCtx)
	 */
	@Override
	public boolean evaluate(Obligation theObligation, PepRequest theRequest,
			ResponseCtx theResponse) {
		if ((pepHid == null) || (pepPid == null) || (emPid == null)) {
			init();
		}
		try {
			String[] results = nm.getHIDByAttributes(pepHid, pepPid, 
					"(PID==" + emPid + ")", 1000, 1);
			if (results.length > 0) {
				emHid = results[0];
			}				
		} catch (RemoteException re) {
			logger.error("RemoteException: " + re.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", re);
			}
		}
		if (PepXacmlConstants.OBLIGATION_DISPATCH_EVENT.getUrn()
				.equalsIgnoreCase(theObligation.getId().toString())) {
			HashMap<String, String> eventArgs = new HashMap<String, String>();
			String cKey = null;
			String cVal = null;
			String topic = null;
			if (logger.isDebugEnabled()) {
				logger.debug("Found event dispatch obligation");
			}
			for (Object obj : theObligation.getAssignments()) {
				Attribute attr = (Attribute) obj;
				if (PepXacmlConstants.OBLIGATION_DISPATCH_EVENT_TOPIC.getUrn()
						.equalsIgnoreCase(attr.getId().toString())) {
					topic = attr.getValue().encode();
					if (logger.isDebugEnabled()) {
						logger.debug("Event topic: " + topic);
					}					
				}
				if (PepXacmlConstants.OBLIGATION_DISPATCH_EVENT_KEY.getUrn()
						.equalsIgnoreCase(attr.getId().toString())) {
					cKey = attr.getValue().encode();
					cVal = null;
					if (logger.isDebugEnabled()) { 
						logger.debug("Event key: " + cKey);
					}					
				}
				if (PepXacmlConstants.OBLIGATION_DISPATCH_EVENT_VALUE.getUrn()
						.equalsIgnoreCase(attr.getId().toString())) {
					cVal = attr.getValue().encode();
					if (logger.isDebugEnabled()) {
						logger.debug("Event value: " + cVal);
					}
					if ((cKey != null) && (cVal != null)) {
						eventArgs.put(cKey, cVal);
					}
					cKey = null;
					cVal = null;
				}
			}
			// dispatch event if complete
			if (topic == null) {
				if (logger.isInfoEnabled()) {
					logger.info("No topic found");
				}
				return false;
			}
			try {
				if (emHid == null) {
					logger.warn("No event manager HID available");
					return false;
				}
				EventManagerPortServiceLocator emLocator 
						= new EventManagerPortServiceLocator();
				emLocator.setEventManagerPortEndpointAddress(
						getSoapTunnellingAddress(pepHid, emHid, "0"));
				EventManagerPort em = emLocator.getEventManagerPort();
				Part[] parts = new Part[eventArgs.size() + 1];
				parts[0] = new Part("HID", pepHid);				
				int p = 1;
				for (Entry<String, String> ntr : eventArgs.entrySet()) {
					parts[p] = new Part(ntr.getKey(), ntr.getValue());
					p++;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("dispatching event");
				}
				em.publish(topic, parts);
				return true;
			} catch (ServiceException se) {
				logger.error("ServiceException: " + se.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", se);
				}
			} catch (RemoteException re) {
				logger.error("RemoteException: " + re.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", re);
				}
			}
		}
		return false;
	}

	/**
	 * @param theUpdates
	 * 				the configuration updates
	 */
	@SuppressWarnings("unchecked")
	public void applyConfigurations(Hashtable theUpdates) {
		for (Object obj : theUpdates.entrySet()) {
			Entry<String, String> ntr = (Entry) obj;
			String key = ntr.getKey();
			String val = ntr.getValue();
			if (EventPepObligationObserverConfigurator.CONFIG_EM_PID
					.equals(key)) {				
				emPid = val;
				logger.debug("Configured EM PID: " + val);
			} else if (EventPepObligationObserverConfigurator.CONFIG_PEP_PID
					.equals(key)) {
				pepPid = val;
				logger.debug("Configured PEP PID: " + val);
			}
		}
	}
	
	/**
	 * @param theContext
	 * 				the {@link ComponentContext} 
	 */
	protected void activate(ComponentContext theContext) {
		logger.debug("Activating");
		if (nm == null) {
			nm = (NetworkManagerApplication) theContext.locateService(
					"NetworkManager");
		}
		httpPort = System.getProperty("org.osgi.service.http.port");
		if (httpPort == null) {
			httpPort = DEFAULT_PORT;
		}
		if (theContext == null) {
			logger.error("Activation attempt without context");
			return;
		}
		try {
			configurator = new EventPepObligationObserverConfigurator(
					theContext.getBundleContext(), this);
		} catch (UnknownHostException uhe) {
			logger.error("Localhost host name could not be resolved: "
					+ uhe.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", uhe);
			}
		}
		configurator.init();
		// managed service registration
		configurator.registerConfiguration();
		activated = true;
		init();
		logger.debug("Activated");
	}

	/**
	 * @param theContext
	 * 				the {@link ComponentContext} 
	 */
	protected void deactivate(ComponentContext theContext) {
		logger.debug("Deactivating");
		activated = false;
		logger.debug("Deactivated");
	}
	
	/**
	 * @param theConfAdmin
	 * 				the {@link ConfigurationAdmin}
	 */
	protected void configurationBind(ConfigurationAdmin theConfAdmin) {
		if (configurator != null) {
			configurator.bindConfigurationAdmin(theConfAdmin);
			if (activated) {
				configurator.registerConfiguration();
			}
		}
	}
	
	/**
	 * @param theConfAdmin
	 * 				the {@link ConfigurationAdmin}
	 */
	protected void configurationUnbind(ConfigurationAdmin theConfAdmin) {
		configurator.unbindConfigurationAdmin(theConfAdmin);
	}
	
	/** initializes wrapped {@link PepObligationObserver} */
	private void init() {
		emPid = configurator.get(
				EventPepObligationObserverConfigurator.CONFIG_EM_PID);
		if ((emPid == null) || ("".equals(emPid))) {
			// assign name as per default name convention
			try {
				emPid = "EventManager:" 
						+ InetAddress.getLocalHost().getHostName();
				configurator.setConfiguration(
						EventPepObligationObserverConfigurator.CONFIG_EM_PID, 
						emPid);
			} catch (UnknownHostException uhe) {
				logger.error("Could not assign default EventManager name: "
						+ uhe.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", uhe);
				}
			}
		}
		pepPid = configurator.get(
				EventPepObligationObserverConfigurator.CONFIG_PEP_PID);
		if ((pepPid == null) || ("".equals(pepPid))) {
			// assign name as per default name convention
			try {
				pepPid = "PEP:" + InetAddress.getLocalHost().getHostName();
				configurator.setConfiguration(
						EventPepObligationObserverConfigurator.CONFIG_PEP_PID, 
						pepPid);
			} catch (UnknownHostException uhe) {
				logger.error("Could not assign default PEP name: "
						+ uhe.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", uhe);
				}
			}
		}
		pepHid = null;
		try {
			String[] results = nm.getHIDByAttributes("0", 
					EventPepObligationObserverConfigurator.SID, 
					"(PID==" + pepPid + ")",	1000, 1);			
			if (results.length > 0) {
				pepHid = results[0];
			}
			if (pepHid == null) {
				logger.debug("No PEP HID available");
				return;
			}
		} catch (RemoteException re) {
			logger.error("RemotException: " + re.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", re);
			}
		}
	}
	
	/**
	 * @param theFromId
	 * 				the sender HID
	 * @param theToId
	 * 				the receiver HID
	 * @param theSessionId
	 * 				the session ID
	 * @return
	 * 				SOAP tunnelling address
	 */
	private String getSoapTunnellingAddress(String theFromId, String theToId, 
			String theSessionId){
		return "http://localhost:8082/SOAPTunneling/" + theFromId + "/" 
				+ theToId + "/" + theSessionId + "/hola";
	}
	
}
