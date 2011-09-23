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
package eu.linksmart.policy.pep.response.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.sun.xacml.Obligation;
import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.response.PepObligationObserver;
import eu.linksmart.policy.pep.response.impl.OsgiTrackerPepObligationObserver;

/**
 * <p>OSGi bundle tracker {@link ObligationObserver} implementation</p>
 * 
 * @author Marco Tiemann
 *
 */
public class OsgiTrackerPepObligationObserver implements PepObligationObserver {

	/** logger */
	static final Logger logger 
			= Logger.getLogger(OsgiTrackerPepObligationObserver.class);
	
	/** {@link ServiceTracker} */
	private ServiceTracker tracker;
	
	/** {@link ServiceReference}s */
	private Set<ServiceReference> serviceReferences
			= new HashSet<ServiceReference>();
	
	/**
	 * Constructor
	 * 
	 * @param theContext
	 * 				the {@link BundleContext}
	 */	
	public OsgiTrackerPepObligationObserver(BundleContext theContext) {
		tracker = new ServiceTracker(theContext, 
				PepObligationObserver.class.getName(), null);
		tracker.open();
		ServiceReference[] refs = tracker.getServiceReferences();
		if (refs != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Found " + refs.length + " references");
			}
			int rl = refs.length;
			for (int i=0; i< rl; i++) {
				register(refs[i]);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Found no references");
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
			String filter = "(objectclass="  
					+ PepObligationObserver.class.getName() + ")";
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
	 * @see eu.linksmart.policy.pep.PepObligationObserver#evaluate(
	 * 		com.sun.xacml.Obligation, eu.linksmart.policy.pep.PepRequest, 
	 * 		com.sun.xacml.ctx.ResponseCtx)
	 */
	@Override
	public boolean evaluate(Obligation theObligation, PepRequest theRequest,
			ResponseCtx theResponse) {
		boolean agResult = false;
		for (ServiceReference reference : serviceReferences) {
			PepObligationObserver observer 
					= (PepObligationObserver) tracker.getService(reference);
			if (observer == null) {
				serviceReferences.remove(observer);
			} else {
				boolean result = observer.evaluate(theObligation, theRequest, 
						theResponse);
				if (result) {
					agResult = true;
				}
			} 
		}
		return agResult;
	}
	
	/**
	 * <p>Adds {@link PepObligationObserver}s by {@link ServiceReference}</p>
	 * 
	 * @param theService
	 * 				the {@link ServiceReference}
	 */
	final void register(ServiceReference theService) {
		PepObligationObserver module 
				= (PepObligationObserver) tracker.getService(theService);
		if (module == null) {
			logger.error("Could not retrieve PEP obligation observer " 
					+ theService.getBundle().getSymbolicName()); 
			return;
		}
		logger.debug("Adding ObligationObserver " 
				+ module.getClass().getCanonicalName());
		serviceReferences.add(theService);
		logger.info("Added ObligationObserver " 
				+ module.getClass().getCanonicalName() + ", service name "
				+ theService.getBundle().getSymbolicName());
		logger.debug("List of service references for obligation observers: ");
		for (ServiceReference sr : serviceReferences) {
			logger.debug("Observer: " + sr.getBundle().getSymbolicName());
		}
	}
	
	/**
	 * <p>Removes {@link PepObligationObserver}s by {@link ServiceReference}</p>
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
		logger.info("Deregistering ObligationObserver: " 
				+ theService.getBundle().getSymbolicName());
		serviceReferences.remove(theService);
	}

}
