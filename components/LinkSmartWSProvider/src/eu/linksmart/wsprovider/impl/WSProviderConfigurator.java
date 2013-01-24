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

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.security.axis.CoreSecurityRequestHandler;
import eu.linksmart.security.axis.CoreSecurityResponseHandler;
import eu.linksmart.security.communication.core.SecurityLibrary;
import eu.linksmart.security.communication.core.impl.SecurityLibraryImpl;
import eu.linksmart.utils.Configurator;

/**
 * Class that implements the Configurator of the Service Provider 
 */
public class WSProviderConfigurator extends Configurator {
	
	public final static String CMPID = "eu.linksmart.security.core";
	public final static String CORE_SECURITY_CONFIG = "linksmart.security.core.config";
	public final static String CONFIGURATION_FILE = "/resources/WS.properties";
	
	/**
	 * Constructor
	 * 
	 * @param context the bundle's execution context
	 */
	public WSProviderConfigurator(BundleContext context) {
		super(context, Logger.getLogger(WSProviderConfigurator.class.getName()),
			CMPID, CONFIGURATION_FILE);
	}
	
	/**
	 * Apply the configuration changes
	 * 
	 * @param updates the configuration changes
	 */
	@Override
	public void applyConfigurations(Hashtable updates) {
		if (updates.containsKey(CORE_SECURITY_CONFIG)) {
			try {
				logger.info("Switching Core Security level to "
					+ updates.get(CORE_SECURITY_CONFIG));
				
				SecurityLibraryImpl securityLib = 
					new SecurityLibraryImpl(Short.parseShort((String) 
						updates.get(CORE_SECURITY_CONFIG)));
				
				CoreSecurityRequestHandler.securityLib = securityLib;
				CoreSecurityResponseHandler.securityLib = securityLib;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Loads the default configuration
	 * 
	 * @return the default configuration loaded
	 */
	//commenting out because implemented via properties file
	/*@Override
	public Dictionary loadDefaults() {
		Hashtable h = new Hashtable();
		h.put(CORE_SECURITY_CONFIG, Short.toString(SecurityLibrary.CONF_ENC));
		return h;
	}*/

}
