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
 * Copyright (C) 2006-2010 
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
package eu.linksmart.eventmanager.impl;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.utils.Configurator;

/**
 * 
 * Class to interface with the LinkSmart Manager Configurator
 * The default configuration file is found in resources/properties
 * before deployment
 *
 */
public class EventManagerConfigurator extends Configurator {

	private static final String CONFIGURATION_FILE = "/resources/properties/EM.properties";
	private static final String EM_PID = "eu.linksmart.eventmanager";
	
	public static final String USE_NETWORK_MANAGER = "EventManager.useNetworkManager";
	public static final String USE_NETWORK_MANAGER_OSGI = "EventManager.useNetworkManagerOSGi";
	public static final String PID = "EventManager.PID";
	public static final String NM_ADDRESS = "EventManager.NetworkManagerAddress";
	public static final String CERT_REF = "EventManager.CertificateReference";
	
	private EventManagerPortBindingImpl eventmgr;

	public EventManagerConfigurator(BundleContext context, EventManagerPortBindingImpl eventmgr) {
		super(context, Logger.getLogger(EventManagerConfigurator.class.getName()), 
				EM_PID, CONFIGURATION_FILE);
		this.eventmgr = eventmgr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void applyConfigurations(Hashtable updates) {
		eventmgr.applyConfigurations(updates);
	}

}
