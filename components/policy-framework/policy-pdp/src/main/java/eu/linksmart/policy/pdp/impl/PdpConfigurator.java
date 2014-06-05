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
package eu.linksmart.policy.pdp.impl;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;

import eu.linksmart.utils.Configurator;

/**
 * <p>{@link PdpApplication} {@link Configurator} implementation</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 *
 */
public class PdpConfigurator extends Configurator  {

	/** PDP PID */
	public static final String PDP_PID = "PdpService.PID";

	/** PDP certificate reference */
	public static final String PDPSERVICE_CERT_REF 
			= "PdpService.CertificateReference";
	
	/** flag indicating whether to use policy database or file repository */
	public static final String PDP_POLICY_REPOSITORY 
			= "Pdp.UsePolicyRepository";
	
	/** flag indicating whether to renew certificates */
	public static final String RENEW_CERTS = "Pdp.RenewCertificates";

	/** PDP service description */
	public static final String PDPSERVICE_DESCRIPTION 
			= "PdpService.Description";
	
	/** PDP service PID */
	public static final String PDP_SERVICE_PID = "eu.linksmart.policy.pdp";

	/** configuration file name and location */
	private static final String CONFIGURATION_FILE 
			= "/resources/pdpconfig.properties";
	
	/** {@link PdpApplication} */
	private PdpApplication pdp;

	/**
	 * Constructor
	 * 
	 * @param theContext
	 * 				the {@link BundleContext}
	 * @param thePdp
	 * 				the {@link PdpApplication} to configure
	 */
	public PdpConfigurator(BundleContext theContext, PdpApplication thePdp) {
		super(theContext,  Logger.getLogger(PdpApplication.class.getName()), PDP_SERVICE_PID, CONFIGURATION_FILE);
		pdp = thePdp;
	}
	
	public PdpConfigurator(BundleContext context, PdpApplication thePdp, ConfigurationAdmin configurationAdmin) {
		super(context, Logger.getLogger(PdpApplication.class.getName()), PDP_SERVICE_PID, CONFIGURATION_FILE, configurationAdmin);
		super.init();
		pdp = thePdp;
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.utils.Configurator#applyConfigurations(
	 * 		java.util.Hashtable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void applyConfigurations(Hashtable updates) {
		pdp.applyConfigurations(updates);
	}
	
}
