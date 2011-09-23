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
package eu.linksmart.policy.pep.response.bundle.impl;

import java.net.UnknownHostException;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.utils.Configurator;

/**
 * <p>{@link Configurator} implementation for 
 * {@link SendMailPepObligationObserverWrapper}</p>
 * 
 * @author Marco Tiemann
 *
 */
public class SendMailPepObligationObserverConfigurator extends Configurator {
	
	/** configuration page name */
	public static final String SID 
			= "eu.linksmart.policy.pep.obl.mail";
	
	/** flag indicating whether to use debug mode */
	public static final String DEBUG_MODE = "pep.mail.debugmode";
	
	/** SMTP host */
	public static final String SMTP_HOST = "pep.mail.smtphost";

	/** STMP port */
	public static final String SMTP_PORT = "pep.mail.smtport";
	
	/** flag indicating whether authentication is required */
	public static final String AUTH_RQRD = "pep.mail.authrequired";
	
	/** flag indicating whether TLS is required */
	public static final String TLS = "pep.mail.usetls";
	
	/** flag indicating whether SSL is required */
	public static final String SSL = "pep.mail.usessl";
	
	/** user name */
	public static final String USER_NAME = "pep.mail.username";
		
	/** password */
	public static final String USER_PASS = "pep.mail.userpass";
	
	/** flag indicating whether to deliver asynchronously */
	public static final String DELIVER_ASYNCH = "pep.mail.deliverasynch";
	
	/** configuration file path */
	private static final String CONFIG_FILE 
			= "/resources/mailpepobserver.properties";

	/** {@link EventPepObligationObserverWrapper} */
	private SendMailPepObligationObserverWrapper observer = null;
	
	/**
	 * Constructor
	 * 
	 * @param theCtx
	 * 				the {@link BundleContext}
	 * @param theObserver
	 * 				the {@link SendMailPepObligationObserverWrapper}
	 * @throws UnknownHostException
	 * 				if the localhost host name can not be retrieved 
	 */
	public SendMailPepObligationObserverConfigurator(BundleContext theCtx, 
			SendMailPepObligationObserverWrapper theObserver) 
			throws UnknownHostException {
		super(theCtx, Logger.getLogger(
				SendMailPepObligationObserverConfigurator.class), 
				SID, CONFIG_FILE);
		observer = theObserver;
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.utils.Configurator#applyConfigurations(
	 * 		java.util.Hashtable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void applyConfigurations(Hashtable theUpdates) {
		observer.applyConfigurations(theUpdates);
	}

}
