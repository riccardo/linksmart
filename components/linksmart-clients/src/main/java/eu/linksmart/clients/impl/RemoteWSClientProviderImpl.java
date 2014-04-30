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
 * Implementation of the {@link eu.linksmart.clients.RemoteWSClientProvider}
 */

package eu.linksmart.clients.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.util.ArrayList;

import org.apache.axis.EngineConfiguration;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.clients.RemoteWSClientProvider;


/**
 * Class that provides remotely WS Clients of LinkSmart Middleware
 */
public class RemoteWSClientProviderImpl implements RemoteWSClientProvider {

	/**
	 * logger
	 */
	Logger LOG = Logger.getLogger(RemoteWSClientProvider.class.getName());
	
	/**
	 * Path to store the clients relative to project root
	 */
	private static final String LINK_SMART_MIDDLEWARE_CLIENTS_FILE_PATH = 
		"/com/eu/linksmart/clients/impl/LinkSmartMiddlewareClients";

	/**
	 * Shows a info message when activate
	 * 
	 * @param context the bundle's execution context
	 */
	protected void activate(ComponentContext context) {
		LOG.info("Remote Client WS Activated");
	}

	/**
	 * Shows a info message when deactivate
	 * 
	 * @param context the bundle's execution context
	 */
	protected void deactivate(ComponentContext context) {
		LOG.info("Remote Client WS Deactivated");
	}

	/**
	 * Retrieves a list of available manager clients
	 * 
	 * @return an array with the list of available manager clients
	 */
	public String[] getManagerClients() {

		ArrayList<String> managers = new ArrayList<String>();

		InputStream stream = this.getClass().getResourceAsStream(
				LINK_SMART_MIDDLEWARE_CLIENTS_FILE_PATH);
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader entrada = new BufferedReader(reader);

		String client;
		try {
			while((client = entrada.readLine()) != null)
				managers.add(client);
		} catch (IOException e) {
			return (String[]) managers.toArray();
		}

		String[] aux = new String[]{}; 
		return managers.toArray(aux);
	}

	/**
	 * This method retrieves the client stubs for a LinkSmart Java Manager. 
	 * Providing a className for the service desired, this method returns a 
	 * client Remote object, that can be casted to the specific service 
	 * interface in order to call the desired methods (it is required that 
	 * the Manager WS interface extends java.rmi.Remote
	 * <p>The interface of this method also accepts to configure the endpoint 
	 * where the service is located (even using SOAP Tunneling) and the Core 
	 * LinkSmart Security Configuration.
	 * 
	 * @param className The class name for the service interface to be called 
	 * (for example, eu.linksmart.network.NetworkManagerApplication)
	 * @param endpoint The endpoint to be used, or null if you want to use the 
	 * default one
	 * @param coreSecurityConfig true if you want use coreLinkSmartSecurity 
	 * (will encrypt messages)
	 * @throws Exception if there is an exception during code generation 
	 * @return {@link java.rmi.Remote} containing the client stubs for 
	 * the required service
	 */
	public Object getRemoteWSClient(String className, String endpoint) throws Exception {

		try {
			//get locator object
			String servicePackage = className.substring(0, className.lastIndexOf("."));
			String serviceName = className.substring(className.lastIndexOf(".") + 1);
			Class l = Class.forName(servicePackage + ".client." +
					serviceName + "Locator");
			Object service = null;


			Constructor cons = l.getConstructor();
			service = cons.newInstance();

			//get port type from locator 
			Remote remote; 
			if (endpoint != null) {
				URL url = new URL(endpoint);
				Method method = l.getMethod("get" + serviceName+"Port", URL.class);
				remote = (Remote) method.invoke(service, url);
			}
			else {
				Method method = l.getMethod("get" + serviceName+"Port", null);
				remote = (Remote) method.invoke(service, new Object[0]);
			}
			//get encapsulation object and pass it port type
			Class encClass = Class.forName(servicePackage + 
					".client." + 
					serviceName + "Encapsulation");

			//get the porttype inteface
			Class[] interfaces = remote.getClass().getInterfaces();
			Class portIntf = null;
			for(Class intf : interfaces) {
				if (intf.getName().contains("PortType")) {
					portIntf = intf;
					break;
				}
			}

			Constructor consEnc = encClass.getConstructor(portIntf);
			Object enc = consEnc.newInstance(remote);
			return enc;
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (SecurityException e) {
			throw e;
		} catch (NoSuchMethodException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (InstantiationException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw e;
		} catch (MalformedURLException e) {
			throw e;
		} 
	}

	/**
	 * Main method
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		(new RemoteWSClientProviderImpl()).getEngineConfiguration();
	}



	/**
	 * @return an engine configuration
	 */
	private EngineConfiguration getEngineConfiguration() {
		org.apache.axis.configuration.XMLStringProvider config = null;
		String configString = "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" "
			+ "xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\">\n"
			+ "<globalConfiguration>\n"
			+ "<requestFlow>\n"
			+ "<handler name=\"URLMapper\" "
			+ "type=\"java:org.apache.axis.handlers.http.URLMapper\" />\n"
			+ "<handler "
			+ "type=\"java:eu.linksmart.security.axis.CoreSecurityRequestHandler\">\n"
			+ "</handler>\n"
			+ "</requestFlow>\n"
			+ "<responseFlow>\n"
			+ "<handler name=\"URLMapper\" "
			+ "type=\"java:org.apache.axis.handlers.http.URLMapper\" />\n"
			+ "<handler "
			+ "type=\"java:eu.linksmart.security.axis.CoreSecurityResponseHandler\">\n"
			+ "</handler>\n"
			+ "</responseFlow>\n"
			+ "</globalConfiguration>\n"
			+ "<transport name=\"http\" "
			+ "pivot=\"java:org.apache.axis.transport.http.HTTPSender\" />\n"
			+ "<transport name=\"local\" "
			+ "pivot=\"java:org.apache.axis.transport.local.LocalSender\" />\n"
			+ "<transport name=\"java\" "
			+ "pivot=\"java:org.apache.axis.transport.java.JavaSender\" />\n"
			+ "</deployment>\n";

		config = new org.apache.axis.configuration.XMLStringProvider(
				configString.toString());
		return config;
	}

}
