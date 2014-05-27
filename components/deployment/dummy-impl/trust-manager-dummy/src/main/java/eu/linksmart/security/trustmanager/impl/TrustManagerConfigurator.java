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
 * Copyright (C) 2006-2010 [Fraunhofer FIT]
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
 * Configuration parameters of the Network Manager
 */

package eu.linksmart.security.trustmanager.impl;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;

import eu.linksmart.utils.Configurator;

public class TrustManagerConfigurator extends Configurator {

	/* Configuration PID & file path. */
	public final static String TM_PID = "eu.linksmart.security.trustmanager";
	public final static String CONFIGURATION_FILE = "/resources/TM.properties";
	
	public static final String CERTIFICATE_REF = "TrustManager.CertificateReference";
	
	//for setting trust model (NullModel, PGP, etc)
	public static final String TM_TRUST_MODEL = "TrustManager.trustModel";
	public static final String PID = "TrustManager.PID";
	public static final String USE_NETWORK_MANAGER = "TrustManager.useNetworkManager";
	public static final String NETWORK_MANAGER_ADDRESS = "TrustManager.NetworkManagerAddress";
	
	private TrustManagerImplDummy trustManager;

	/**
	 * Constructor. Creates a new "NetworkManagerConfigurator" object
	 * 
	 * @param trustManager the network manager implementation
	 * @param context the bundle's execution context
	 */
	public TrustManagerConfigurator(TrustManagerImplDummy trustManager, 
			BundleContext context) {
		
		super(context, Logger.getLogger(TrustManagerConfigurator.class.getName()),
			TM_PID, CONFIGURATION_FILE);
		this.trustManager = trustManager;
		trustManager.setCurrentTrustModel(this.get(TM_TRUST_MODEL));
	}
	
	/**
	 * Constructor. Creates a new "NetworkManagerConfigurator" object
	 * 
	 * @param trustManager the network manager implementation
	 * @param context the bundle's execution context
	 * @param configuration admin
	 */
	public TrustManagerConfigurator(TrustManagerImplDummy trustManager, 
			BundleContext context, ConfigurationAdmin configAdmin) {
		
		super(context, Logger.getLogger(TrustManagerConfigurator.class.getName()),
			TM_PID, CONFIGURATION_FILE, configAdmin);
		super.init();
		this.trustManager = trustManager;
		trustManager.setCurrentTrustModel(this.get(TM_TRUST_MODEL));
	}
	
	/**
	 * Apply the configuration changes
	 * 
	 * @param updates the configuration changes
	 */
	@Override
	public void applyConfigurations(Hashtable updates) {
		this.trustManager.applyConfigurations(updates);
	}
}
