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
package eu.linksmart.policy.pdp.finder.impl;

import java.net.URI;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.PolicyFinderResult;

/**
 * <p>OSGi bundle tracker {@link PolicyFinderModule} implementation</p>
 * 
 * @author Marco Tiemann
 *
 */
public class OsgiTrackerPolicyFinderModule extends PolicyFinderModule {

	/** logger */
	static final Logger logger 
			= Logger.getLogger(OsgiTrackerPolicyFinderModule.class);
	
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
	public OsgiTrackerPolicyFinderModule(BundleContext theContext) {
		super();
		tracker = new ServiceTracker(theContext, 
				PolicyFinderModule.class.getName(), null);
		tracker.open();
		ServiceReference[] refs = tracker.getServiceReferences();
		if (refs == null) {
			logger.debug("Found no references");
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
			String filter = "(objectclass="  
					+ PolicyFinderModule.class.getName() + ")";
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
	 * @see com.sun.xacml.finder.PolicyFinderModule#findPolicy(
	 * 		com.sun.xacml.EvaluationCtx)
	 */
	@Override
	public PolicyFinderResult findPolicy(EvaluationCtx arg0) {
		PolicyFinderModule mo 
				= (PolicyFinderModule) tracker.getService(serviceReference);
		if (mo != null) {
			return mo.findPolicy(arg0);
		}
		return new PolicyFinderResult();
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#findPolicy(java.net.URI, 
	 * 		int)
	 */
	@Override
	public PolicyFinderResult findPolicy(URI arg0, int arg1) {
		PolicyFinderModule mo 
				= (PolicyFinderModule) tracker.getService(serviceReference);
		if (mo != null) {
			return mo.findPolicy(arg0, arg1);
		}
		return new PolicyFinderResult();
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		PolicyFinderModule mo 
				= (PolicyFinderModule) tracker.getService(serviceReference);
		if (mo != null) {
			return mo.getIdentifier();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#init(
	 * 		com.sun.xacml.finder.PolicyFinder)
	 */
	@Override
	public void init(PolicyFinder arg0) {
		PolicyFinderModule mo 
				= (PolicyFinderModule) tracker.getService(serviceReference);
		if (mo != null) {
			mo.init(arg0);
		}
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#invalidateCache()
	 */
	@Override
	public void invalidateCache() {
		PolicyFinderModule mo 
				= (PolicyFinderModule) tracker.getService(serviceReference);
		if (mo != null) {
			mo.invalidateCache();
		}
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#isIdReferenceSupported()
	 */
	@Override
	public boolean isIdReferenceSupported() {
		PolicyFinderModule mo 
				= (PolicyFinderModule) tracker.getService(serviceReference);
		if (mo != null) {
			return mo.isIdReferenceSupported();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#isRequestSupported()
	 */
	@Override
	public boolean isRequestSupported() {
		PolicyFinderModule mo 
				= (PolicyFinderModule) tracker.getService(serviceReference);
		if (mo != null) {
			return mo.isRequestSupported();
		}
		return true;
	}
	
	/**
	 * Adds {@link PolicyFinderModule}s by {@link ServiceReference}
	 * 
	 * @param theService
	 * 				the {@link ServiceReference}
	 */
	final void register(ServiceReference theService) {
		PolicyFinderModule module 
				= (PolicyFinderModule) tracker.getService(theService);
		if (module == null) {
			logger.error("Could not retrieve any registered " 
					+ "PolicyFinderModule"); 
			return;
		}
		if (module.getClass().getCanonicalName().equals("eu.linksmart.policy." 
				+ "pdp.impl.PdpApplication")) {
			logger.debug("Found PdpApplication, skipping");
			return;
		}
		logger.debug("Setting PolicyFinderModule " 
				+ module.getClass().getCanonicalName());
		serviceReference = theService;
		logger.info("Set external PolicyFinderModule to " 
				+ module.getClass().getCanonicalName() + ", service name "
				+ theService.getBundle().getSymbolicName());
	}
	
	/**
	 * Removes {@link PolicyFinderModule}s by {@link ServiceReference}
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
		logger.info("Deregistering PolicyFinderModule: " 
				+ theService.getBundle().getSymbolicName());
		if (serviceReference.equals(theService)) {
			serviceReference = null;
		}
	}
	
}
