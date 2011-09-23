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
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.policy.pep.response.impl.BundlePepObligationObserverWrapper;

/**
 * <p>Bundle wrapper around the {@link SendMailPepObligationObserver}</p>
 * 
 * @author Marco Tiemann
 *
 */
public class SendMailPepObligationObserverWrapper 
		extends BundlePepObligationObserverWrapper {

	/** logger */
	private static final Logger logger 
			= Logger.getLogger(SendMailPepObligationObserverWrapper.class);
	
	/** {@link SendMailPepObligationObserverConfigurator} */
	private SendMailPepObligationObserverConfigurator configurator = null;
	
	/** flag indicating whether the instance has been activated */
	private boolean activated = false;
	
	/** No-args constructor */
	public SendMailPepObligationObserverWrapper() {
		super(new SendMailPepObligationObserver());
	}

	/**
	 * Activates this bundle
	 * 
	 * @param theContext
	 * 				the {@link ComponentContext} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void activate(ComponentContext theContext) {
		logger.debug("Activating");
		super.activate(theContext);
		try {
			configurator = new SendMailPepObligationObserverConfigurator(
					theContext.getBundleContext(), this);
		} catch (UnknownHostException uhe) {
			logger.error("Localhost host name could not be resolved: "
					+ uhe.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", uhe);
			}
		}
		configurator.init();
		// managed service registration
		configurator.registerConfiguration();
		SendMailPepObligationObserver mailObserver
				= (SendMailPepObligationObserver) observer;
		Dictionary conf = configurator.getConfiguration();
		Enumeration e = conf.keys();
		Hashtable updates = new Hashtable();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			updates.put(key, conf.get(key));
		}
		mailObserver.applyConfigurations(updates);
		activated = true;
		logger.debug("Activated");
	}

	/**
	 * Dectivates this bundle
	 * 
	 * @param theContext
	 * 				the {@link ComponentContext} 
	 */
	@Override
	protected void deactivate(ComponentContext theContext) {
		logger.debug("Deactivating");
		super.deactivate(theContext);
		activated = false;
		logger.debug("Deactivated");
	}
	
	/**
	 * @param theConfAdmin
	 * 				the {@link ConfigurationAdmin}
	 */
	protected void configurationBind(ConfigurationAdmin theConfAdmin) {
		if (configurator != null) {
			configurator.bindConfigurationAdmin(theConfAdmin);
			if (activated) {
				configurator.registerConfiguration();
			}
		}
	}
	
	/**
	 * @param theConfAdmin
	 * 				the {@link ConfigurationAdmin}
	 */
	protected void configurationUnbind(ConfigurationAdmin theConfAdmin) {
		configurator.unbindConfigurationAdmin(theConfAdmin);
	}
	
	/**
	 * @param theUpdates
	 * 				the configuration updates
	 */
	@SuppressWarnings("unchecked")
	public void applyConfigurations(Hashtable theUpdates) {
		if (observer == null) {
			return;
		}
		SendMailPepObligationObserver mailObserver
				= (SendMailPepObligationObserver) observer;
		mailObserver.applyConfigurations(theUpdates);
	}
	
}
