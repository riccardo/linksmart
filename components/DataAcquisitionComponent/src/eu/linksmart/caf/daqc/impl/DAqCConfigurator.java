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
package eu.linksmart.caf.daqc.impl;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;


import eu.linksmart.caf.daqc.DataAcquisitionComponent;
import eu.linksmart.utils.Configurator;

/**
 * Data Acquisition Component {@link Configurator}, for persistent configuration
 * management. Extends {@link Configurator} from the LinkSmart Middleware API
 * 
 * @author Michael Crouch
 * 
 */
public class DAqCConfigurator extends Configurator {

	/** ID for the PID configuration */
	public static final String PID = "Daqc.PID";

	/** ID for the Certificate Reference configuration */
	public static final String CERT_REF = "Daqc.CertificateReference";

	/** ID for configuration of the (boolean) flag for renewing certificates */
	public static final String USE_CERT_REF = "Daqc.UseCertReference";

	/** ID for the URL of the Local Event Manager to use */
	public static final String LOCAL_EM_URL = "Daqc.LocalEventManagerUrl";

	/** ID for the Description configuration */
	public static final String DESC = "Daqc.Description";

	/** The SID for the Data Acquisition Component service */
	public static final String DAQC_SERVICE_PID = "eu.linksmart.caf.daqc";
	
	/** the {@link DataAcquisitionComponent} config file location */
	private static final String CONFIGURATION_FILE =
			"/resources/DAQC.properties";

	/** the {@link DAqCApplication} */
	private DAqCApplication daqc;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the {@link BundleContext}
	 * @param daqc
	 *            the {@link DAqCApplication}
	 */
	public DAqCConfigurator(BundleContext context, DAqCApplication daqc) {
		super(context, Logger.getLogger(DAqCConfigurator.class.getName()),
				DAQC_SERVICE_PID, CONFIGURATION_FILE);

		this.daqc = daqc;
	}

	@Override
	public void applyConfigurations(Hashtable updates) {
		daqc.applyConfigurations(updates);
	}

}
