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
package eu.linksmart.policy.pdp.finder.bundle.impl;

import java.net.URI;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;

import eu.linksmart.network.NetworkManagerApplication;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.PolicyFinderResult;

import eu.linksmart.policy.pdp.PdpAdmin;
import eu.linksmart.policy.pdp.admin.bundle.impl.FilePolicyAdminWrapper;
import eu.linksmart.policy.pdp.finder.impl.LocalFolderPolicyFinderModule;

/**
 * <p>Bundle wrapper for {@link LocalFolderPolicyFinderModule}</p>
 * 
 * @author Marco Tiemann
 *
 */
public class FilePolicyFinderWrapper extends PolicyFinderModule {

	/** logger */
	static final Logger logger 
			= Logger.getLogger(FilePolicyFinderWrapper.class);	
	
	/** default HTTP port */
	private static final String DEFAULT_PORT = "8082";
	
	/** {@link NetworkManagerApplication} */
	protected NetworkManagerApplication nm = null;

	/** HTTP port */
	protected String httpPort;
	
	/** {@link LocalFolderPolicyFinderModule} */
	private LocalFolderPolicyFinderModule finder = null;
	
	/** {@link FilePolicyFinderConfigurator} */
	private FilePolicyFinderConfigurator configurator = null;
	
	/** {@link ServiceTracker} */
	private ServiceTracker tracker = null;
	
	/** flag indicating whether the instance has been activated */
	private boolean activated = false;
	
	/**
	 * @param theUpdates
	 * 				the configuration updates
	 */
	public void applyConfigurations(Hashtable<?, ?> theUpdates) {
		// intentionally left blank
	}
	
	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#findPolicy(
	 * 		com.sun.xacml.EvaluationCtx)
	 */
	@Override
	public PolicyFinderResult findPolicy(EvaluationCtx arg0) {
		if (finder != null) {
			return finder.findPolicy(arg0);
		}
		logger.warn("No finder available");
		return new PolicyFinderResult();
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#findPolicy(java.net.URI, int)
	 */
	@Override
	public PolicyFinderResult findPolicy(URI arg0, int arg1) {
		if (finder != null) {
			return finder.findPolicy(arg0, arg1);
		}
		logger.warn("No finder available");
		return new PolicyFinderResult();
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		if (finder != null) {
			return finder.getIdentifier();
		}
		logger.warn("No finder available");
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#init(
	 * 		com.sun.xacml.finder.PolicyFinder)
	 */
	@Override
	public void init(PolicyFinder arg0) {
		if (finder == null) {
			logger.warn("No finder available");
		} else {
			finder.init(arg0);
		}
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#invalidateCache()
	 */
	@Override
	public void invalidateCache() {
		if (finder == null) {
			logger.warn("No finder available");			
		} else {
			finder.invalidateCache();
		}
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#isIdReferenceSupported()
	 */
	@Override
	public boolean isIdReferenceSupported() {
		if (finder != null) {
			return finder.isIdReferenceSupported();
		}
		logger.warn("No finder available");
		return false;
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.finder.PolicyFinderModule#isRequestSupported()
	 */
	@Override
	public boolean isRequestSupported() {
		if (finder != null) {
			return finder.isRequestSupported();
		}
		logger.warn("No finder available");
		return false;
	}

	/**
	 * Activates bundle
	 * 
	 * @param theContext
	 * 				the {@link ComponentContext}
	 */
	@SuppressWarnings("unchecked")
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
		configurator = new FilePolicyFinderConfigurator(
				theContext.getBundleContext(), this);
		configurator.init();
		// managed service registration
		configurator.registerConfiguration();
		Dictionary conf = configurator.getConfiguration();
		if (conf != null) {
			Enumeration e = conf.keys();
			Hashtable updates = new Hashtable();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				updates.put(key, conf.get(key));
			}
		}
		// tracker for policy admin
		tracker = new ServiceTracker(theContext.getBundleContext(), 
				PdpAdmin.class.getName(), null);
		tracker.open();
		ServiceReference[] refs = tracker.getServiceReferences();
		if (refs == null) {
			logger.debug("Found no references");
		} else {
			logger.debug("Found " + refs.length + " references");
			/*
			 * go through references, we are looking for a 
			 * FilePolicyAdminWrapper only
			 */
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
					+ PdpAdmin.class.getName() + ")";
			theContext.getBundleContext().addServiceListener(sl, filter);
			logger.debug("Added ServiceListener");
		} catch(Exception ex) {
			logger.warn("Exception while adding ServiceListener: "
					+ ex.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", ex);
			}
		}
		
		activated = true;
		logger.debug("Activated");
	}

	/**
	 * Deactivates bundle
	 * 
	 * @param theContext
	 * 				the {@link ComponentContext}
	 */
	protected void deactivate(ComponentContext theContext) {
		logger.debug("Deactivating");
		tracker.close();
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
	
	/**
	 * Adds {@link PdpAdmin}s by {@link ServiceReference}
	 * 
	 * @param theService
	 * 				the {@link ServiceReference}
	 */
	void register(ServiceReference theService) {
		PdpAdmin module = (PdpAdmin) tracker.getService(theService);
		if (module == null) {
			logger.error("Could not retrieve any registered PdpAdmin"); 
			return;
		}
		if (module instanceof FilePolicyAdminWrapper) {
			logger.debug("Found FilePolicyAdminWrapper, registering");
			logger.debug("Setting PdpAdmin "
					+ module.getClass().getCanonicalName() + ", service name "
					+ theService.getBundle().getSymbolicName());
			finder = new LocalFolderPolicyFinderModule(
					((FilePolicyAdminWrapper) module).getAdmin());
		} else {
			logger.debug("Found useless PdpAdmin: " 
					+ module.getClass().getName());
		}
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
		finder = null;
	}
	
}
