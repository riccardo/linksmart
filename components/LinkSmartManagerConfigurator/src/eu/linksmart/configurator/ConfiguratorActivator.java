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
import java.rmi.RemoteException;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;


import eu.linksmart.configurator.impl.ConfiguratorImpl;
import eu.linksmart.configurator.webconf.GetConfigurationServlet;
import eu.linksmart.configurator.webconf.LinkSmartStatus;
import eu.linksmart.network.CryptoHIDResult;
import eu.linksmart.network.NetworkManagerApplication;


/**
 * Activator class
 */
public class ConfiguratorActivator  {
	
	private static String xmlAttributes;
	
	private ComponentContext context;
	private ConfiguratorImpl configuratorImpl;
	private ServiceRegistration configuratorReg;
	private HttpService http;
	private GetConfigurationServlet getConfigurationServlet;
	private Logger logger = Logger.getLogger(ConfiguratorActivator.class.getSimpleName());
	private NetworkManagerApplication nm;
	private CryptoHIDResult cryptoHID;
	
//	private final static String WEB_SERVLET_ALIAS = "/LinkSmartConfigurator";
	private final static String GETCONFIGURATION_SERVLET_ALIAS =
		"/LinkSmartConfigurator/GetConfiguration";
	
	static {
		try {
			xmlAttributes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
				+ "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">"
				+ "<properties>"
				+ "<entry key=\"PID\">" + InetAddress.getLocalHost().getHostName() 
				+ "</entry>"
				+ "<entry key=\"SID\">eu.linksmart.Configuration</entry>"
				+ "</properties>";
		} catch (UnknownHostException e) {
			xmlAttributes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
				+ "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">"
				+ "<properties>"
				+ "<entry key=\"PID\">Unknown</entry>"
				+ "<entry key=\"SID\">eu.linksmart.Configuration</entry>"
				+ "</properties>";
		}
	}

	/**
	 * Activate method
	 *
	 * @param context the bundle's execution context
	 */
	protected void activate(ComponentContext context) {
		this.context = context;
		this.configuratorImpl = new ConfiguratorImpl(context);
		configuratorReg = context.getBundleContext().registerService(
				Configurator.class.getName(), this.configuratorImpl, null);
		
		try {
			if (http != null) {
				registerServlets(http);
			}
			if (nm != null) {
				registerHID(nm);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		logger.info("Started " + ConfiguratorActivator.class.getSimpleName());
	}
	
	/**
	 * Deactivate method
	 * 
	 * @param context the bundle's execution context
	 */
	protected void deactivate(ComponentContext context) {
		this.configuratorReg.unregister();
		logger.info("Stopped" + ConfiguratorActivator.class.getSimpleName());
	}
		
	/**
	 * Register servlets into an Http Service
	 * 
	 * @param http the Http Service to register servlets
	 */
	protected void registerServlets(HttpService http) {
		this.http = http;
		if (configuratorImpl != null) {
			getConfigurationServlet = new GetConfigurationServlet(configuratorImpl);
			try {
				http.registerServlet("/LinkSmartStatus", new LinkSmartStatus(context), null, null);
				http.registerServlet(GETCONFIGURATION_SERVLET_ALIAS,
						getConfigurationServlet, null, null);
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
//		http.unregister(WEB_SERVLET_ALIAS);
		http.unregister("/LinkSmartStatus");
		http.unregister(GETCONFIGURATION_SERVLET_ALIAS);
		
		if ((nm != null) && (cryptoHID != null)) {
			try {
				nm.removeHID(cryptoHID.getHID());
			} catch (RemoteException e) {
				logger.error(e);
			}
		}
	}
	
	/**
	 * Registers an HID in the Network Manager
	 * 
	 * @param nm the NetworkManagerApplication
	 */
	protected void registerHID(NetworkManagerApplication nm) {
		this.nm = nm;
		if (http != null) {
			try {
				this.cryptoHID = nm.createCryptoHID(xmlAttributes, "http://localhost:"
						+ System.getProperty("org.osgi.service.http.port")
						+ "/axis/services/LinkSmartConfigurator");
			} catch (RemoteException e) {
				logger.error(e);
			}
		}
	}
	
	/**
	 * Unregisters an HID in the Network Manager
	 * 
	 * @param nm the NetworkManagerApplication
	 */
	protected void unregisterHID(NetworkManagerApplication nm) {
		if (http != null) {
			try {
				nm.removeHID(cryptoHID.getHID());
				this.cryptoHID = null;
			} catch (RemoteException e) {
				logger.error(e);
			}
		}
		this.nm = null;
	}
	
}
