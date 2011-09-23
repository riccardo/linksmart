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
 * Copyright (C) 2006-2010 [Telefonica I+D]
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

package eu.linksmart.wsprovider.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.ClassUtils;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;


import eu.linksmart.wsprovider.AxisAdmin;
import eu.linksmart.wsprovider.servlet.ServletDescriptor;
import eu.linksmart.wsprovider.servlet.WebApp;
import eu.linksmart.wsprovider.servlet.WebAppDescriptor;


/**
 * Activator class
 */
public class Activator implements ServiceTrackerCustomizer, AxisAdmin {
	
	public static BundleContext context = null;
	public Logger log = Logger.getLogger(Activator.class.getName());
	private static AxisServer axisServer = null;
	private WebApp webApp = null;
	private ServiceTracker registrationTracker;
	public static Dictionary configuration;
	
	private boolean activated = false;
	private WSProviderConfigurator configurator;
	
    public Map exportedServices = new HashMap();

	/**
	 * Gets the axis server
	 * @return the axis server
	 */
	public static AxisServer getAxisServer() {
		return axisServer;
	}
	
	/**
	 * Activate method
	 * 
	 * @param context the bundle's execution context
	 */
	protected void activate(ComponentContext context) {
		this.context = context.getBundleContext();
		
		configurator = new WSProviderConfigurator(context.getBundleContext());
		
		try {
			init();
		} catch (Exception e) {
			log.error("activate: Exception when starting bundle", e);
			e.printStackTrace();
			return;
		}
		
		/* Register as Managed Service to receive configuration modifications. */
		configurator.registerConfiguration();
		
		activated  = true;
	}
	
	/**
	 * Init method
	 */
	private void init() throws Exception {
		ClassUtils.setDefaultClassLoader(this.getClass().getClassLoader());
		
		URL url = this.getClass().getResource("/resources/axis/server-config.wsdd");
		InputStream is = url.openStream();
		
		EngineConfiguration fromBundleResource = new FileProvider(is);
		
		log.info("Configuration file read.");
		axisServer = new AxisServer(fromBundleResource);
		log.info("Axis server started.");
		webApp = new WebApp(getWebAppDescriptor());
		webApp.start(context);
		log.info("Web application started.");
		
		try {
			/* Get the tracker for any registered service that have the WS property. */
			registrationTracker = new ServiceTracker(context, context.createFilter(
				"(&(objectClass=*)(" + AxisAdmin.SOAP_SERVICE_NAME + "=*))"), this);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		
		registrationTracker.open();
	}
	
	/**
	 * Deactivate method
	 * 
	 * @param context the bundle's execution context
	 */
	protected void deactivate(ComponentContext context) {
		try {
			registrationTracker.close();
			webApp.stop(context.getBundleContext());
			webApp = null;
			this.context = null;
			axisServer = null;

			configurator.stop();
			
			log = null;
		} catch (Exception e) {
			log.error("Exception when stopping bundle", e);
		}
		activated = false;
	}

	/**
	 * Gets the web-app descriptor
	 * 
	 * @return the web-app descriptor
	 */
	private WebAppDescriptor getWebAppDescriptor() {
		WebAppDescriptor wad = new WebAppDescriptor();
		
		wad.servlet = new ServletDescriptor[1];
		wad.context = "/axis";
		wad.servlet[0] = new ServletDescriptor("/services", new ServicesServlet());
		return wad;
	}
	
	/**
	 * A service is being added to the <code>ServiceTracker</code>.
	 * 
	 * <p>
	 * This method is called before a service which matched the search
	 * parameters of the <code>ServiceTracker</code> is added to the
	 * <code>ServiceTracker</code>. This method should return the service object
	 * to be tracked for the specified <code>ServiceReference</code>. The
	 * returned service object is stored in the <code>ServiceTracker</code> and
	 * is available from the <code>getService</code> and
	 * <code>getServices</code> methods.
	 * 
	 * @param reference The reference to the service being added to the
	 *        <code>ServiceTracker</code>.
	 * @return The service object to be tracked for the specified referenced
	 *         service or <code>null</code> if the specified referenced service
	 *         should not be tracked.
	 */
    public Object addingService(ServiceReference reference) {
    	String serviceName = 
    		(String) reference.getProperty(AxisAdmin.SOAP_SERVICE_NAME);
    	String[] classes = 
    		(String[]) reference.getProperty(Constants.OBJECTCLASS);
    	String allowedMethods = 
    		(String) reference.getProperty(AxisAdmin.SOAP_SERVICE_METHODS);
    	String security = 
    		(String) reference.getProperty(AxisAdmin.CORE_LINK_SMART_CONFIG);
    	
    	if (serviceName != null) {
    		log.info("Added service: "+serviceName);
    		
    		/* Throws exception if name is invalid. */
    		assertServiceName(serviceName);
    		Object serviceObj = context.getService(reference);	

    		/* By default, the service is configured to have CoreSecurity. */
    		boolean coreSecurity = true;
    		if (security != null) {
    			coreSecurity = Boolean.parseBoolean(security);
    		}
    		
    		ObjectSOAPService soapService =
    			new ObjectSOAPService(axisServer, serviceName, serviceObj, classes,
    				allowedMethods, coreSecurity);
    		
    		soapService.deploy();
    		exportedServices.put(reference, soapService);
    	}
    	return context.getService(reference);
    }
    
	/**
	 * A service tracked by the <code>ServiceTracker</code> has been modified.
	 * 
	 * <p>
	 * This method is called when a service being tracked by the
	 * <code>ServiceTracker</code> has had it properties modified.
	 * 
	 * @param reference The reference to the service that has been modified.
	 * @param service The service object for the specified referenced service.
	 */
    public void modifiedService(ServiceReference reference, Object service) {
    	/* The service properties have been modified. Do nothing. */
    	log.error("Modified service. I will do nothing!");
    }
    
	/**
	 * A service tracked by the <code>ServiceTracker</code> has been removed.
	 * 
	 * <p>
	 * This method is called after a service is no longer being tracked by the
	 * <code>ServiceTracker</code>.
	 * 
	 * @param reference The reference to the service that has been removed.
	 * @param service The service object for the specified referenced service.
	 */
    public void removedService(ServiceReference reference, Object service) {
    	String serviceName = 
    		(String) reference.getProperty(AxisAdmin.SOAP_SERVICE_NAME);
    	
    	if (serviceName != null) {
    		
    		ObjectSOAPService soapService = 
    			(ObjectSOAPService) exportedServices.get(reference);
    		
    		if(soapService != null) {
    			soapService.undeploy();
    			log.info("removed service " + serviceName);
    			exportedServices.remove(reference);
    		}
		}
    }

    /**
     * Check if service name is OK for publishing as SOAP service.
     * This included checking for previous registrations at the same name.
     * 
     * @param serviceName the service name to check
     * @throws IllegalArgumentException if name is not valid
     */
    public void assertServiceName(String serviceName) {
    	if(serviceName == null) {
    		throw new IllegalArgumentException("Service name cannot be null");
    	}
    	
    	if("".equals(serviceName)) {
    		throw new IllegalArgumentException("Service name cannot be empty string");
    	}
    	
    	for(int i = 0; i < serviceName.length(); i++) {
    		if(Character.isWhitespace(serviceName.charAt(i))) {
    			throw new IllegalArgumentException("Service name '" + serviceName 
    				+ "' cannot contain whitespace");
    		}
    	}
    	
    	synchronized(exportedServices) {
    		for(Iterator it = exportedServices.keySet().iterator(); it.hasNext();) {
    			ServiceReference sr = (ServiceReference) it.next();
    			String name = (String) sr.getProperty(AxisAdmin.SOAP_SERVICE_NAME);
    			
    			if(name.equals(serviceName)) {
    				throw new IllegalArgumentException("Service '" + name
    					+ "' is already exported");
    			}
    		}
    	}
    }

    /**
     * Gets the currently published service names.
     * 
     * @return the published service names
     */
    public String[] getPublishedServiceNames() {
    	synchronized(exportedServices) {
    		try {
    			String[] sa = new String[exportedServices.size()];
    			
    			int i = 0;
    			for(Iterator it = exportedServices.keySet().iterator(); it.hasNext();) {
    				ServiceReference sr = (ServiceReference) it.next();
    				String name = (String) sr.getProperty(AxisAdmin.SOAP_SERVICE_NAME);
    				sa[i++] = name;
    			}
    			
    			return sa;
    		} catch (RuntimeException e) {
    			e.printStackTrace();
    			throw e;
    		}
    	}
    }

	/**
	 * Sets the Configuration Admin, thats is a service for administer 
	 * configuration data
	 * 
	 * @param cm the configuration admin
	 */
    protected void bindConfiguration(ConfigurationAdmin cm) {
    	if (configurator != null) {
    		configurator.bindConfigurationAdmin(cm);
    		if (activated) {
    			configurator.registerConfiguration();
    		}
    	}
    }
    
	/**
	 * Unsets the Configuration Admin.
	 * 
	 * @param cm the configuration admin
	 */
	protected void unbindConfiguration(ConfigurationAdmin cm) {
		configurator.unbindConfigurationAdmin(cm);
	}

}
