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
package eu.linksmart.policy.pep.impl;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.utils.Configurator;

import eu.linksmart.policy.pep.impl.PepApplication;

/**
 * <p>PEP {@link Configurator} implementation</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 *
 */
public class PepConfigurator extends Configurator  {
	
	/** PEP PID */
	public static final String PEP_PID = "Pep.PID";
	
	/** certificate reference */
	public static final String CERT_REF = "Pep.CertificateReference";
	
	/** renew certificate flag */
	public static final String RENEW_CERT = "Pep.RenewCertificate";

	/** human-readable description flag */
	public static final String DESC = "PepService.Description";
	
	/** PEP service PID */
	public static final String PEP_SERVICE_PID = "eu.linksmart.policy.pep";
	
	/** PDP service PID */
	public static final String PDP_SERVICE_PID = "eu.linksmart.policy.pdp";
	
	/** PDP admin service PID */
	public static final String PDP_ADMIN_SERVICE_PID 
			= "eu.linksmart.policy.pdp.admin";
	
	/** PDP PID */
	public static final String PDP_PID = "Pep.PdpPID";
	
	/** flag indicating whether to use PDP result caching in PEP */
	public static final String PEP_USE_PDP_CACHING = "Pep.SessionCache.Active";
	
	/** lifetime of a result cache entry */
	public static final String PEP_CACHE_LIFETIME = "Pep.SessionCache.Lifetime";
	
	/** flag indicating whether subsequent request prolong the cache lifetime */
	public static final String PEP_CACHE_KEEPALIVE 
			= "Pep.SessionCache.Keepalive";
	
	/** flag indicating whether to fail on unfulfilled obligations */
	public static final String PEP_DENY_ON_UNFULFILLED_OBLIGATION
			= "Pep.DenyOnUnfulfilledObligation";
	
	/** 
	 * flag indicating whether to default to deny responses for indefinite ones 
	 */
	public static final String PEP_DEFAULT_TO_DENY_RESPONSE
			= "Pep.DefaultToDenyResponse";
	
	/** 
	 * flag indicating whether to stop evaluating an obligation once it has 
	 * been handled successfully by one {@link PepObligationObserver}
	 */
	public static final String PEP_LAZY_OBLIGATION_HANDLING
			= "Pep.LazyObligationHandling";
	
	/** flag indicating whether to use local sessions */
	public static final String PEP_USE_LOCAL_SESSIONS
			= "Pep.UseLocalSessions";

	/** configuration file location */
	private static final String CONFIGURATION_FILE 
			= "/resources/pepconfig.properties";
	
	/** {@link PepApplication} */
	private PepApplication pep = null;

	/** 
	 * Constructor
	 * 
	 * @param theContext
	 * 				the {@link BundleContext} 
	 * @param thePepApp
	 * 				the {@link PepApplication}
	 */
	public PepConfigurator(BundleContext theContext, PepApplication thePepApp) {
		super(theContext, Logger.getLogger(PepApplication.class.getName()), 
				PEP_SERVICE_PID, CONFIGURATION_FILE);
		pep = thePepApp;
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.utils.Configurator#applyConfigurations(
	 * 		java.util.Hashtable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void applyConfigurations(Hashtable theUpdates) {
		pep.applyConfigurations(theUpdates);
	}
	
}
