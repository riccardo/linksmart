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

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;

import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.policy.pdp.PolicyDecisionPoint;
import eu.linksmart.policy.pip.PolicyInformationPoint;

/**
 * Default LinkSmart {@link PolicyDecisionPoint} implementation
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 *
 */
@Component(name="eu.linksmart.policy.pdp", immediate=true)
@Service({PolicyDecisionPoint.class})
@org.apache.felix.scr.annotations.Properties({
        @Property(name="service.exported.interfaces", value="*"),
        @Property(name="service.exported.configs", value="org.apache.cxf.ws"),
        @Property(name="org.apache.cxf.ws.address", value="http://0.0.0.0:9090/cxf/services/PolicyDecisionPoint")
})
public class PdpApplication implements PolicyDecisionPoint {

	/** logger */
	private static final Logger logger = Logger.getLogger(PdpApplication.class);

	/** Default policy repository */
	private static final String DEFAULT_REPOSITORY = "file";

	private static String SEPARATOR = System.getProperty("file.separator");

	/** PDP policy database location in working directory (for XACML files) */
	private static final String PDP_FILEFOLDER_LOC 
	= "linksmart" + SEPARATOR + "eu.linksmart.policy" + SEPARATOR + "PolicyDecisionPoint" + SEPARATOR + "PolicyFolder";

	/** Permitted policy repository identifiers */
	private static ArrayList<String> PERMITTED_REPOSITORIES = new ArrayList<String>();

	private List<AttributeFinderModule> pips = new ArrayList<AttributeFinderModule>();

	static {
		//		PERMITTED_REPOSITORIES.add("bundle");
		//		PERMITTED_REPOSITORIES.add("db");
		PERMITTED_REPOSITORIES.add("file");
	}

	/** The PDP implementation used **/
	private PDP pdp = null;

	/** {@link LinkSmartServiceManager} */
	private LinkSmartServiceManager serviceManager = null;

	/** {@link PdpConfigurator} */
	private PdpConfigurator configurator = null;

	/** state flag indicating whether the application has been activated */
	private boolean activated = false;

	Balana balana = null;

	private AttributeFinder attributeFinder;
	
	@Reference(name="ConfigurationAdmin",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
		    bind="bindConfigAdmin",
		    unbind="unbindConfigAdmin",
		    policy=ReferencePolicy.STATIC)
	protected ConfigurationAdmin configAdmin = null;
	
	@Reference(name="NetworkManager",
            cardinality = ReferenceCardinality.OPTIONAL_UNARY,
            bind="bindNetworkManager",
            unbind="unbindNetworkManager",
            policy= ReferencePolicy.DYNAMIC)
	private NetworkManager nm = null;
	
	@Reference(name="PolicyInformationPoint",
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            bind="bindPolicyInformationPoint",
            unbind="unbindPolicyInformationPoint",
            policy= ReferencePolicy.DYNAMIC)
	private PolicyInformationPoint pip;
	
	protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
		logger.debug("binding policy-pdp:configAdmin");
        this.configAdmin = configAdmin;
    }

    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
    	logger.debug("un-binding policy-pdp:configAdmin");
        this.configAdmin = null;
    }
	
	protected void bindNetworkManager(NetworkManager nm) {
		logger.debug("binding policy-pdp:networkmanager");
		this.nm = nm;
		if(activated) {
			serviceManager = new LinkSmartServiceManager(this, nm);
			try {
				serviceManager.init();
			} catch (IOException e) {
				logger.error("PolicyDecisionPoint could not be registered because PID was not unique and not generateable!",e);
			}
		}
	}

	protected void unbindNetworkManager(NetworkManager nm) {
		logger.debug("un-binding policy-pdp:networkmanager");
		if(serviceManager != null) {
			try {
				serviceManager.unregisterService();
			} catch (RemoteException e) {
				//should not occur at local invocation and cannot do anything about it
				logger.warn("Error deregistering from Network Manager", e);
			}
		}
		this.nm = null;
	}
	
	protected void bindPolicyInformationPoint(PolicyInformationPoint pip) {
		logger.debug("binding policy-pdp:policy information point");
		pips.add(new PipAttachementPoint(pip));
		if(attributeFinder != null) {
			attributeFinder.setModules(pips);
		}
	}

	protected synchronized void unbindPolicyInformationPoint(PolicyInformationPoint pip) {
		logger.debug("unbinding policy-pdp:policy information point");
		int index = 0;
		boolean match = false;
		//find the unbinded pip from the list
		for(AttributeFinderModule attrFinder : pips) {
			PipAttachementPoint piap = (PipAttachementPoint)attrFinder;
			if(piap.getPip().getId().equals(pip.getId())) {
				match = true;
				break;
			}
			index++;
		}
		//remove found pip and replace list in attributefinder
		if(match) {
			pips.remove(index);
			if(attributeFinder != null) {
				this.attributeFinder.setModules(pips);
			}
		}
	}
	
	@Activate
	protected void activate(ComponentContext theContext) {
		logger.info("activating policy-pdp");
		
		configurator = new PdpConfigurator(theContext.getBundleContext(), this, configAdmin);
		configurator.registerConfiguration();
				
		//set up PDP

		//check repository location - at the moment only file based supported
		//String dataSource = (String) configurator.getConfiguration().get(
		//		PdpConfigurator.PDP_POLICY_REPOSITORY);
		//if ((dataSource == null) 
		//		|| (!PERMITTED_REPOSITORIES.contains(dataSource))) {
		//	dataSource = DEFAULT_REPOSITORY;
		//	logger.warn("Only file based policy store supported at the moment");
		//}
		// using file based policy repository. so set the policy location as system property
		logger.debug("Using file policy repository");
		createDirectory(PDP_FILEFOLDER_LOC);
		System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, PDP_FILEFOLDER_LOC);

		balana = Balana.getInstance();
		PDPConfig pdpConfig = balana.getPdpConfig();
		attributeFinder = pdpConfig.getAttributeFinder();
		attributeFinder.setModules(pips);

		// instantiate PDP with linksmart attributefindermodule
		pdp = new PDP(
				new PDPConfig(
						attributeFinder,
						pdpConfig.getPolicyFinder(),
						pdpConfig.getResourceFinder(),
						true));

		if(nm != null) {
			try {
				serviceManager = new LinkSmartServiceManager(this, nm);
				serviceManager.init();
			} catch (IOException e) {
				logger.error("Error getting hostname of machine, please configure a unique PID and restart", e);
			} catch (IllegalArgumentException ei) {
				logger.error("Error registering with NetworkManager because of PID problem", ei);
			}
		}
			
		activated = true;
		logger.info("policy-pdp is activated");
	}

	@Deactivate
	protected void deactivate(ComponentContext theContext) {
		logger.debug("deactivating policy-pdp");
		if(serviceManager != null) {
			try {
				serviceManager.unregisterService();
			} catch (RemoteException e) {
				//should not occur at local invocation and cannot do anything about it
				logger.warn("Error deregistering from Network Manager", e);
			}
		}
		activated = false;
	}
	
	@Override
	public String evaluate(String theReqXml) throws RemoteException {
		return pdp.evaluate(theReqXml);
	}

	/**
	 * @param theUpdates
	 * 				the configuration update <code>Hashtable</code>
	 */
	@SuppressWarnings("unchecked")
	public void applyConfigurations(Hashtable<?, ?> theUpdates) {
		if(!activated) {
			return;
		}
		if ((theUpdates == null) || (theUpdates.size() == 0)) {
			return;
		}
		if(serviceManager == null) {
			return;
		}
		logger.info("Configuring");
		try {
			//provide any kind of update into method, even if its null - method handles check of null parameters
			String renew = (String)theUpdates.get(PdpConfigurator.RENEW_CERTS);
			serviceManager.registerService(
					(String)theUpdates.get(PdpConfigurator.PDPSERVICE_DESCRIPTION),
					(String)theUpdates.get(PdpConfigurator.PDP_PID),
					null,
					(String)theUpdates.get(PdpConfigurator.PDPSERVICE_CERT_REF),
					(renew == null)? false : Boolean.parseBoolean(renew));
		} catch (IOException ioe) {
			logger.error("Error renewing service: " 
					+ ioe.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", ioe);
			}
		}
		logger.info("Configured");
	}

	/**
	 * @return
	 * 				the {@link PdpConfigurator}
	 */
	public PdpConfigurator getConfigurator() {
		return configurator;
	}

	public static void createDirectory(String stringpath) {
		//check if the directory is existing
		File dir = new File(stringpath);
		if(dir.exists()) {
			return;
		} else {
			StringTokenizer st = new StringTokenizer(stringpath, SEPARATOR);
			ArrayList<String> arrLst = new ArrayList<String>();
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				token = token.trim();
				arrLst.add(token);
			}
			String path = "";

			for (int i = 0; i < arrLst.size(); i++) {
				path = path + arrLst.get(i);
				File directory = new File(path);
				directory.mkdir();
				path = path + "/";
			}
		}
	}

}
