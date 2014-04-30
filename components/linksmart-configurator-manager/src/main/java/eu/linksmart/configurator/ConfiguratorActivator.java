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

/**
 * Activator class of the bundle
 */

package eu.linksmart.configurator;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;

import org.apache.log4j.Logger;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.linksmart.configurator.impl.ConfiguratorImpl;
import eu.linksmart.configurator.webconf.GetConfigurationServlet;
import eu.linksmart.configurator.webconf.LinkSmartStatus;

/**
 * Activator class
 */
@Component(name="LinkSmartManagerConfigurator", immediate=true)
public class ConfiguratorActivator  {

	/**
	 * constants
	 */
	private static final String OSGI_HTTP_SERVICE_PORT_PROPERTY = System.getProperty("org.osgi.service.http.port");
	private final static String WEB_SERVLET_ALIAS = "/LinkSmartConfigurator";
	private final static String GETCONFIGURATION_SERVLET_ALIAS = "/LinkSmartConfigurator/GetConfiguration";

	private static String PID;

	/**
	 * fields
	 */
	private ComponentContext context;
	
	private ConfiguratorImpl configuratorImpl;
	private ServiceRegistration configuratorReg;
	private GetConfigurationServlet getConfigurationServlet;
	private Logger logger = Logger.getLogger(ConfiguratorActivator.class.getName());
	
	private boolean servletsInitialized = false;
	private boolean configAdminInitialized = false;
	
	@Reference(name="ConfigurationAdmin",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindConfigAdmin", 
			unbind="unbindConfigAdmin",
			policy=ReferencePolicy.STATIC)
	public ConfigurationAdmin configAdmin = null;
	
	@Reference(name="HttpService",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="registerServlets", 
			unbind="unregisterServlets", 
			policy=ReferencePolicy.STATIC)
	private HttpService http;

	static {
		try {
			PID = InetAddress.getLocalHost().getHostName();

		} catch (UnknownHostException e) {
			PID = "Unknown";
		}
	}
	
	protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
		logger.debug("ConfiguratorActivator::binding ConfigurationAdmin");
		this.configAdmin = configAdmin;
		this.configuratorImpl = new ConfiguratorImpl();
        this.configuratorImpl.setConfigAdmin(this.configAdmin);
        this.configAdminInitialized = true;
    }
    
    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
    	logger.debug("ConfiguratorActivator::un-binding ConfigurationAdmin");
    	this.configAdmin = null;
    	this.configuratorImpl.setConfigAdmin(null);
    	this.configAdminInitialized = false;
    }
    
	protected void registerServlets(HttpService http) {
		logger.debug("ConfiguratorActivator::binding http-service");
		if(servletsInitialized)
			return;
		this.http = http;
		if (configAdminInitialized) {
			getConfigurationServlet = new GetConfigurationServlet(configuratorImpl);
			try {
				http.registerServlet("/LinkSmartStatus", new LinkSmartStatus(context), null, null);
				logger.info("registering /LinkSmartStatus servlet");
				http.registerServlet(GETCONFIGURATION_SERVLET_ALIAS, getConfigurationServlet, null, null);
				logger.info("registering /LinkSmartConfigurator/GetConfiguration servlet");
				servletsInitialized = true;
			} catch (ServletException e) {
				logger.error(e);
			} catch (NamespaceException e) {
				logger.error(e);
			}
		} 
	}

	/**
	 * Unregister servlets from an Http Service
	 * 
	 * @param http the Http Service to unregister servlets
	 */
	protected void unregisterServlets(HttpService http) {
		logger.debug("ConfiguratorActivator::un-binding http-service");
		if(servletsInitialized) {
			http.unregister(WEB_SERVLET_ALIAS);
			http.unregister(GETCONFIGURATION_SERVLET_ALIAS);
			http.unregister("/LinkSmartStatus");
			this.http = null;
			this.servletsInitialized = false;
		}
	}

	/**
	 * Activate method
	 *
	 * @param context the bundle's execution context
	 */
	@Activate
	protected void activate(ComponentContext context) {
		logger.info("[activating ConfiguratorActivator]");
		this.context = context;
		
		if(!configAdminInitialized) {
			this.configuratorImpl = new ConfiguratorImpl(context);
		} 
		
		configuratorReg = context.getBundleContext().registerService(
				Configurator.class.getName(), this.configuratorImpl, null);

        logger.debug("*** activated ****");

		try {
			if (!servletsInitialized) {
				registerServlets(this.http);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Deactivate method
	 * 
	 * @param context the bundle's execution context
	 */
	@Deactivate
	protected void deactivate(ComponentContext context) {
		this.configuratorReg.unregister();
		if (http != null) {
			unregisterServlets(http);
		}
        logger.debug("*** de-activated ****");
	}
}
