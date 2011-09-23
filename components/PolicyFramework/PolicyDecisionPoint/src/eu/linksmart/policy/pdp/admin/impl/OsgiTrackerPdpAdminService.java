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
package eu.linksmart.policy.pdp.admin.impl;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import eu.linksmart.policy.pdp.PdpAdmin;
import eu.linksmart.policy.pdp.PdpAdminError;

/**
 * <p>OSGi bundle tracker {@link PdpAdmin} implementation</p>
 * 
 * @author Marco Tiemann
 *
 */
public class OsgiTrackerPdpAdminService implements PdpAdmin {

	/** logger */
	static final Logger logger 
			= Logger.getLogger(OsgiTrackerPdpAdminService.class);
	
	/** {@link ServiceTracker} */
	private final ServiceTracker tracker;
	
	/** {@link ServiceReference} */
	private ServiceReference serviceReference = null;
	
	/**
	 * Constructor
	 * 
	 * @param theContext
	 * 				the {@link BundleContext}
	 */	
	public OsgiTrackerPdpAdminService(BundleContext theContext) {
		tracker = new ServiceTracker(theContext, PdpAdmin.class.getName(), 
				null);
		tracker.open();
		ServiceReference[] refs = tracker.getServiceReferences();
		if (refs == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Found no references");
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Found " + refs.length + " references");
			}
			for (ServiceReference ref : refs) {
				register(ref);
			}
		}
		ServiceListener sl = new ServiceListener() {
			@Override
			public void serviceChanged(ServiceEvent theEvent) {
				logger.debug("Checking services");
				switch (theEvent.getType()) {
					case ServiceEvent.REGISTERED : {
						logger.debug("Registering");
						register(theEvent.getServiceReference());
						return;
					}
					case ServiceEvent.MODIFIED : { 
						logger.debug("MODIFIED");
						return; 
					}
					case ServiceEvent.UNREGISTERING : {
						ServiceReference sr = theEvent.getServiceReference();
						logger.debug("Removing " 
								+ sr.getBundle().getSymbolicName());
						remove(sr);
						return;
					}
					default: {
						// intentionally left blank
					}
				}
			}
		};
		try {
			String filter = "(objectclass=" + PdpAdmin.class.getName() + ")";
			theContext.addServiceListener(sl, filter);
			logger.debug("Added ServiceListener");
		} catch(Exception e) {
			logger.warn("Exception while adding ServiceListener: "
					+ e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#activatePolicy(java.lang.String)
	 */
	@Override
	public boolean activatePolicy(String thePolicy) throws RemoteException {
		if ((tracker != null) && (serviceReference != null)) {
			PdpAdmin mo = (PdpAdmin) tracker.getService(serviceReference);
			if (mo != null) {
				return mo.activatePolicy(thePolicy);
			}
		}
		logger.warn("No PDP administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#deactivatePolicy(java.lang.String)
	 */
	@Override
	public boolean deactivatePolicy(String thePolicy) throws RemoteException {
		if ((tracker != null) && (serviceReference != null)) {
			PdpAdmin mo = (PdpAdmin) tracker.getService(serviceReference);
			if (mo != null) {
				return mo.deactivatePolicy(thePolicy);
			}
		}
		logger.warn("No PDP administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getActivePolicyList()
	 */
	@Override
	public String[] getActivePolicyList() throws RemoteException {
		if ((tracker != null) && (serviceReference != null)) {
			PdpAdmin mo = (PdpAdmin) tracker.getService(serviceReference);
			if (mo != null) {
				return mo.getActivePolicyList();
			}
		}
		logger.warn("No PDP administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getInActivePolicyList()
	 */
	@Override
	public String[] getInActivePolicyList() throws RemoteException {
		if ((tracker != null) && (serviceReference != null)) {
			PdpAdmin mo = (PdpAdmin) tracker.getService(serviceReference);
			if (mo != null) {
				return mo.getInActivePolicyList();
			}
		}
		logger.warn("No PDP administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getPolicy(java.lang.String)
	 */
	@Override
	public String getPolicy(String thePolicyId) throws RemoteException {
		if ((tracker != null) && (serviceReference != null)) {
			PdpAdmin mo = (PdpAdmin) tracker.getService(serviceReference);
			if (mo != null) {
				return mo.getPolicy(thePolicyId);
			}
		}
		logger.warn("No PDP administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String theKey) throws RemoteException {
		if ((tracker != null) && (serviceReference != null)) {
			PdpAdmin mo = (PdpAdmin) tracker.getService(serviceReference);
			if (mo != null) {
				return mo.getProperty(theKey);
			}
		}
		logger.warn("No PDP administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#publishPolicy(java.lang.String, 
	 * 		java.lang.String)
	 */
	@Override
	public boolean publishPolicy(String thePolicyId, String thePolicy)
			throws RemoteException {
		if ((tracker != null) && (serviceReference != null)) {
			PdpAdmin mo = (PdpAdmin) tracker.getService(serviceReference);
			if (mo != null) {
				return mo.publishPolicy(thePolicyId, thePolicy);
			}
		}
		logger.warn("No PDP administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#removePolicy(java.lang.String)
	 */
	@Override
	public boolean removePolicy(String thePolicy) throws RemoteException {
		if ((tracker != null) && (serviceReference != null)) {
			PdpAdmin mo = (PdpAdmin) tracker.getService(serviceReference);
			if (mo != null) {
				return mo.removePolicy(thePolicy);
			}
		}
		logger.warn("No PDP administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#setProperty(java.lang.String, 
	 * 		java.lang.String)
	 */
	@Override
	public boolean setProperty(String theKey, String theValue)
			throws RemoteException {
		if ((tracker != null) && (serviceReference != null)) {
			PdpAdmin mo = (PdpAdmin) tracker.getService(serviceReference);
			if (mo != null) {
				return mo.setProperty(theKey, theValue);
			}
		}
		logger.warn("No PDP administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/**
	 * Adds {@link PdpAdmin}s by {@link ServiceReference}
	 * 
	 * @param theService
	 * 				the {@link ServiceReference}
	 */
	final void register(ServiceReference theService) {
		PdpAdmin module = (PdpAdmin) tracker.getService(theService);
		if (module == null) {
			logger.error("Could not retrieve any registered PdpAdmin"); 
			return;
		}
		if (module.getClass().getCanonicalName().equals("eu.linksmart.policy." 
				+ "pdp.impl.PdpApplication")) {
			logger.debug("Found PdpApplication, skipping");
			return;
		}
		logger.debug("Setting PdpAdmin "
				+ module.getClass().getCanonicalName());
		serviceReference = theService;
		logger.info("Set external PdpAdmin to " 
				+ module.getClass().getCanonicalName() + ", service name "
				+ theService.getBundle().getSymbolicName());
	}
	
	/**
	 * Removes {@link PdpAdmin}s by {@link ServiceReference}
	 * 
	 * @param theService
	 * 				the {@link ServiceReference}
	 */
	void remove(ServiceReference theService) {
		/* 
		 * OSGi tracker returns null when the unregister state has been messaged
		 * for a service; the service can no longer be retrieved via the 
		 * service reference
		 */
		logger.info("Deregistering PdpAdmin: " 
				+ theService.getBundle().getSymbolicName());
		if (serviceReference.equals(theService)) {
			serviceReference = null;
		}
	}
	
}
