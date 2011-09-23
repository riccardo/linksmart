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
package eu.linksmart.policy.pdp.admin.bundle.impl;

import java.rmi.RemoteException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.NetworkManagerApplication;

import eu.linksmart.policy.pdp.PdpAdmin;
import eu.linksmart.policy.pdp.PdpAdminError;
import eu.linksmart.policy.pdp.admin.impl.FileSystemPdpAdminService;

/**
 * <p>Bundle wrapper for {@link FileSystemPdpAdminService}</p>
 * 
 * @author Marco Tiemann
 *
 */
public class FilePolicyAdminWrapper implements PdpAdmin {

	/** logger */
	private static final Logger logger 
			= Logger.getLogger(FilePolicyAdminWrapper.class);	
	
	/** default HTTP port */
	private static final String DEFAULT_PORT = "8082";
	
	/** {@link NetworkManagerApplication} */
	protected NetworkManagerApplication nm = null;

	/** HTTP port */
	protected String httpPort;
	
	/** {@link FileSystemPdpAdminService} */
	private FileSystemPdpAdminService admin = null;
	
	/** {@link FilePolicyAdminConfigurator */
	private FilePolicyAdminConfigurator configurator = null;
	
	/** {@link BundleContext} */
	private BundleContext context = null;
	
	/** flag indicating whether the instance has been activated */
	private boolean activated = false;
	
	/**
	 * @param theUpdates
	 * 				the configuration updates
	 */
	public void applyConfigurations(Hashtable<?, ?> theUpdates) {
		if (theUpdates.contains(FilePolicyAdminConfigurator.FILE_PATH)) {
			try {
				context.getBundle().update();
				return;
			} catch (BundleException be) {
				logger.error("BundleException while applying configurations "
						+ be.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", be);
				}
			}
		}
	}
	
	/**
	 * @return
	 * 				the {@link FileSystemPdpAdmin} or <code>null</code>
	 */
	public FileSystemPdpAdminService getAdmin() {
		return admin;
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#activatePolicy(java.lang.String)
	 */
	@Override
	public boolean activatePolicy(String thePolicy) throws RemoteException {
		if (admin != null) {
			return admin.activatePolicy(thePolicy);
		}
		logger.warn("No administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#deactivatePolicy(java.lang.String)
	 */
	@Override
	public boolean deactivatePolicy(String thePolicy) throws RemoteException {
		if (admin != null) {
			return admin.deactivatePolicy(thePolicy);
		}
		logger.warn("No administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getActivePolicyList()
	 */
	@Override
	public String[] getActivePolicyList() throws RemoteException {
		if (admin != null) {
			return admin.getActivePolicyList();
		}
		logger.warn("No administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getInActivePolicyList()
	 */
	@Override
	public String[] getInActivePolicyList() throws RemoteException {
		if (admin != null) {
			return admin.getInActivePolicyList();
		}
		logger.warn("No administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getPolicy(java.lang.String)
	 */
	@Override
	public String getPolicy(String thePolicyId) throws RemoteException {
		if (admin != null) {
			return admin.getPolicy(thePolicyId);
		}
		logger.warn("No administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String theKey) throws RemoteException {
		if (admin != null) {
			return admin.getProperty(theKey);
		}
		logger.warn("No administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#publishPolicy(java.lang.String, 
	 * 		java.lang.String)
	 */
	@Override
	public boolean publishPolicy(String thePolicyId, String thePolicy)
			throws RemoteException {
		if (admin != null) {
			return admin.publishPolicy(thePolicyId, thePolicy);
		}
		logger.warn("No administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#removePolicy(java.lang.String)
	 */
	@Override
	public boolean removePolicy(String thePolicy) throws RemoteException {
		if (admin != null) {
			return admin.removePolicy(thePolicy);
		}
		logger.warn("No administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#setProperty(java.lang.String, 
	 * 		java.lang.String)
	 */
	@Override
	public boolean setProperty(String theKey, String theValue)
			throws RemoteException {
		if (admin != null) {
			return admin.setProperty(theKey, theValue);
		}
		logger.warn("No administrator available");
		throw new RemoteException(
				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
	}

	/**
	 * Activates bundle
	 * 
	 * @param theContext
	 * 				the {@link ComponentContext}
	 */
	@SuppressWarnings("unchecked")
	protected void activate(ComponentContext theContext) {
		logger.debug("Activating");
		if (nm == null) {
			nm = (NetworkManagerApplication) theContext.locateService(
					"NetworkManager");
		}
		httpPort = System.getProperty("org.osgi.service.http.port");
		if (httpPort == null) {
			httpPort = DEFAULT_PORT;
		}
		context = theContext.getBundleContext();
		configurator = new FilePolicyAdminConfigurator(context, this);
		configurator.init();
		configurator.registerConfiguration();
		Dictionary conf = configurator.getConfiguration();
		if (conf != null) {
			Enumeration e = conf.keys();
			Hashtable updates = new Hashtable();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				updates.put(key, conf.get(key));
			}
		}
		admin = new FileSystemPdpAdminService(
					configurator.get(FilePolicyAdminConfigurator.FILE_PATH));
		activated = true;
		logger.debug("Activated");
	}

	/**
	 * Deactivates bundle
	 * 
	 * @param theContext
	 * 				the {@link ComponentContext}
	 */
	protected void deactivate(ComponentContext theContext) {
		logger.debug("Deactivating");
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
	
}
