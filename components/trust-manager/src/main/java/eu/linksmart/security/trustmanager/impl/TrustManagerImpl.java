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
 * Copyright (C) 2006-2010 Fraunhofer SIT,
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

package eu.linksmart.security.trustmanager.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.clients.RemoteWSClientProvider;
import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.security.trustmanager.TrustManager;
import eu.linksmart.security.trustmanager.TrustManagerConfiguration;
import eu.linksmart.security.trustmanager.trustmodel.TrustModel;
import eu.linksmart.security.trustmanager.trustmodel.TrustModelRegistry;
import eu.linksmart.security.trustmanager.util.Base64;
import eu.linksmart.security.trustmanager.util.Util;
import eu.linksmart.utils.Part;

/**
 * This class encapsulates the web service used to verify the authenticity of
 * trust tokens.
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 * @author Stephan Heuser (stephan.heuser@sit.fraunhofer.de)
 * @author Mark Vinkovits (mark.vinkovits@fit.fraunhofer.de)
 */
@Component(name="TrustManager")
@Service({TrustManager.class})
@org.apache.felix.scr.annotations.Properties({
    @Property(name="service.exported.interfaces", value="*"),
    @Property(name="service.exported.configs", value="org.apache.cxf.ws"),
    @Property(name="org.apache.cxf.ws.address", value="http://0.0.0.0:9090/cxf/services/TrustManager")
})
public class TrustManagerImpl implements TrustManager, TrustManagerConfiguration {

	private final static Logger LOG = Logger.getLogger(TrustManagerImpl.class.getName());
	
	public static final String TRUST_MANAGER_PATH = "/cxf/services/TrustManager";
	//public static final String NETWORK_MANAGER_PATH = "/cxf/services/NetworkManagerApplication"; 
	//can be any string from the config.xml class name
	public final static String CURRENT_TRUST_MODEL = "NullTrustModel";
	private static String BACKBONE_SOAP = "eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl";

	private TrustManagerConfigurator configurator;
	private BundleContext context;
	private boolean activated=false;
	private ServiceRegistration trustModelConfigService = null;
	private Registration trustManagerVirtualAddress = null;
	private boolean nmOsgi = false;
	private boolean createdService;
	private String nmAddress = null;
	
	@Reference(name="ConfigurationAdmin",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindConfigAdmin", 
			unbind="unbindConfigAdmin",
			policy=ReferencePolicy.STATIC)
	protected ConfigurationAdmin configAdmin = null;
	
	@Reference(name="NetworkManager",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindNetworkManager", 
			unbind="unbindNetworkManager",
			policy=ReferencePolicy.DYNAMIC)
	private NetworkManager nm = null;
	
	@Reference(name="RemoteWSClientProvider",
			cardinality = ReferenceCardinality.OPTIONAL_UNARY,
			bind="bindWSProvider", 
			unbind="unbindWSProvider",
			policy=ReferencePolicy.DYNAMIC)
	private RemoteWSClientProvider clientProvider;
	
	protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
		LOG.debug("TrustManager::binding ConfigurationAdmin");
		this.configAdmin = configAdmin;
    }
    
    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
    	LOG.debug("TrustManager::un-binding ConfigurationAdmin");
    	this.configAdmin = null;
    }
	
	protected void bindWSProvider(RemoteWSClientProvider clientProvider) {
		LOG.debug("TrustManager::binding ws-client");
		this.clientProvider = clientProvider;
	}

	protected void unbindWSProvider(RemoteWSClientProvider clientProvider) {
		LOG.debug("TrustManager::un-binding ws-client");
		removeTrustManagerService();
		this.clientProvider = null;
		if(!nmOsgi){
			removeTrustManagerService();
			createdService = false;
			this.nm= null;
		}
	}

	protected void bindNetworkManager(NetworkManager netManager) {
		LOG.debug("TrustManager::binding network-manager");
		this.nm = netManager;
		nmOsgi = true;
	}

	protected void unbindNetworkManager(NetworkManager nm) {
		LOG.debug("TrustManager::un-binding network-manager");
		if (activated) {
			removeTrustManagerService();
			createdService = false;
		}
		this.nm = null;
		nmOsgi = false;

	}

	@Activate
	protected void activate(ComponentContext ccontext) {
		LOG.info("[activating TrustManager]");
		this.context = ccontext.getBundleContext();
		//create configuration files
		Hashtable<String, String> HashFilesExtract = new Hashtable<String, String>();
		LOG.debug("Deploying TrustManager config files");
		HashFilesExtract.put(Util.FILE_CONFIG, "configuration/config.xml");

		Util.createDirectory(Util.CONFIGFOLDERPATH);
		Util.extractFilesJar(HashFilesExtract);	

		//set up configurator for trust manager
		configurator = new TrustManagerConfigurator(this, context, configAdmin);
		configurator.registerConfiguration();

		createServiceForTrustManager(false);

		this.activated = true;
		LOG.info("TrustManager activated");		
	}

	@Deactivate
	protected void deactivate(ComponentContext ccontext) {
		LOG.info("de-activating TrustManagerDummy");
		this.activated=false;
	}

	@Override
	public boolean setCurrentTrustModel(String identifier) {
		//unregister previous configuration service
		if(trustModelConfigService != null){
			trustModelConfigService.unregister();
			trustModelConfigService = null;
		}

		TrustModelRegistry registry = TrustModelRegistry.getInstance();
		boolean success = registry.setCurrentTrustModel(identifier);	
		//if change successful to new trustmodel register configuration service
		if(success && TrustModelRegistry.getInstance().getCurrentTrustModel().getConfigurator() != null){
			try {
				trustModelConfigService = this.context.registerService(
						TrustModelRegistry.getInstance().getCurrentTrustModel().getConfigurator().getName(),
						TrustModelRegistry.getInstance().getCurrentTrustModel().getConfiguratorClass().getConstructor().newInstance(),
						null);
			} catch (Exception e) {
				LOG.error("Cannot register configuration serivce for trust model", e);
			}
		}		
		return success;
	}

	@Override
	public String getCurrentTrustModel() {
		TrustModelRegistry registry = TrustModelRegistry.getInstance();
		return registry.getCurrentTrustModel().getIdentifier();
	}

	@Override
	public String[] getSupportedTrustModels() {
		String[] modelArray = new String[0];
		TrustModelRegistry registry = TrustModelRegistry.getInstance();
		List<String> models = registry.getTrustModels();
		if (models!=null) {
			modelArray = new String[models.size()];
			for (int i=0; i<modelArray.length;i++) {
				String model = models.get(i);
				modelArray[i] = model;
			}
		}
		return modelArray;
	}

	/**
	 * Validates a token.
	 * 
	 * @return A trust level value between 0 and 1.
	 */
	@Override
	public double getTrustValue(String token) {

		TrustModel trustmodel =
			TrustModelRegistry.getInstance().getCurrentTrustModel();

		if (token == null || trustmodel == null) {
			return -1;
		} else {

			byte[] token_dec = Base64.decode(token);
			return trustmodel.getTrustValue(token_dec);
		}
	}

	/**
	 * Uses a specific trust model to validate the token.
	 * 
	 * @return A trust level value between 0 and 1.
	 */
	@Override
	public double getTrustValueWithIdentifier(String token,
			String trustModelIdentifier) {

		TrustModelRegistry registry = TrustModelRegistry.getInstance();
		if (token == null || trustModelIdentifier == null) {
			return -1;
		} else if (registry.setCurrentTrustModel(trustModelIdentifier)) {
			byte[] token_dec = Base64.decode(token);
			TrustModel trustmodel = registry.getCurrentTrustModel();
			return trustmodel.getTrustValue(token_dec);
		} else {
			return 0;
		}
	}

	public void applyConfigurations(Hashtable updates) {
		LOG.debug("Applying configurations in TrustManager");

		if (updates.containsKey(TrustManagerConfigurator.TM_TRUST_MODEL)) {
			LOG.info("Applying new trust model configurations: " + updates.get(TrustManagerConfigurator.TM_TRUST_MODEL));

			String trustModelValue = (String) updates.get(TrustManagerConfigurator.TM_TRUST_MODEL);
			setCurrentTrustModel(trustModelValue);
		}
		// check if this code should be call always 
		if (updates.containsKey(TrustManagerConfigurator.PID)) {
			String currentPid = null;
			for (Part attr : this.trustManagerVirtualAddress.getAttributes()) {
				if (attr.getKey().equals(ServiceAttribute.PID.name())) {
					currentPid = attr.getValue();
				}
			}
			String pidaux =(String)updates.get( TrustManagerConfigurator.PID) ;
			if(updates.get( TrustManagerConfigurator.PID) != currentPid ){
				removeTrustManagerService();
				createServiceForTrustManager(true);
				createdService = true;
			}
		}
		if (updates.containsKey(TrustManagerConfigurator.USE_NETWORK_MANAGER) || updates.containsKey(TrustManagerConfigurator.NETWORK_MANAGER_ADDRESS)) {
			boolean useNetworkManager = Boolean.parseBoolean((String) updates.get(TrustManagerConfigurator.USE_NETWORK_MANAGER));
			if (useNetworkManager == true) {
				if (createdService == false){
					createServiceForTrustManager(false);
					createdService = true;
				}
			} else {
				removeTrustManagerService();
				createdService = false;
			}
		}
		// else is handled by OSGI ConfigAdmin

	}

	private void createServiceForTrustManager(boolean renewCert) {
		if (trustManagerVirtualAddress != null) return; //Only do this once
		boolean withNetworkManager = Boolean.parseBoolean(configurator.get(TrustManagerConfigurator.USE_NETWORK_MANAGER));
		LOG.debug("TrustManager with NetworkManagerCore: "+ withNetworkManager);
		if(withNetworkManager) {
			//get network manager reference
			String nmAddress = (String)configurator.get(TrustManagerConfigurator.NETWORK_MANAGER_ADDRESS);
			if(nmAddress != null && !nmAddress.equalsIgnoreCase("local")){
				if(nmOsgi) {
					LOG.error("Cannot use remote Network Manager when local is running!");
				} else {
					//Load the WS client and try to communicated with NM
					if (clientProvider!=null) {
						try {
							if (nm == null) {
								try {
									this.nm = (NetworkManager)clientProvider.
									getRemoteWSClient(NetworkManager.class.getName(), 
											(String)configurator.get(TrustManagerConfigurator.NETWORK_MANAGER_ADDRESS));
								} catch (Exception e1) {
									LOG.error("Error while creating client to NetworkManagerCore: " + e1.getMessage(), e1);
								}
							}	
						} catch (Exception e) {
							LOG.error(e.getMessage(), e);
						}
					} else {
						LOG.error("Cannot get NetworkManager proxy because ClientProvider not running!");
					}
				}
			}
			if(nm != null) {
				//use nm reference to create service
				try {
					//trustmanager has no certificate yet or needs new then create it
					if (configurator.get(TrustManagerConfigurator.CERTIFICATE_REF)==null || renewCert == true) {
						this.trustManagerVirtualAddress = createCertificate();
					} else {
						this.trustManagerVirtualAddress = (nm.registerService(
								new Part[]{
										new Part(
												ServiceAttribute.CERT_REF.name(),
												configurator.get(TrustManagerConfigurator.CERTIFICATE_REF))
										}, 
								"http://localhost:9090"+ TRUST_MANAGER_PATH,
								BACKBONE_SOAP));
						if (this.trustManagerVirtualAddress == null) {
							//Certificate ref is not valid...
							this.trustManagerVirtualAddress = createCertificate();
						}
					}
					LOG.info("TrustManager VirtualAddress: " + trustManagerVirtualAddress);
				} catch (Exception e) {
					LOG.error("Error while creating VirtualAddress for TM: " + e.getMessage(), e);
				}
			}
		}
	}

	private void removeTrustManagerService() {
		if (trustManagerVirtualAddress== null) return; //Only do this once

		if(nm != null){
			try {
				nm.removeService(trustManagerVirtualAddress.getVirtualAddress());
			} catch (Exception e) {
				LOG.error(e);
			}
			trustManagerVirtualAddress = null;
		}
	}

	private Registration createCertificate() throws IOException{
		String pid = configurator.get(TrustManagerConfigurator.PID);
		String[] pidAux;
		//if no PID set use local IP as identifier
		if ((pid == null)||(pid.equals(""))) {
			
			pid = "TrustManager:" + InetAddress.getLocalHost().getHostName();
			
		} else 	if (pid.contains(":")) { // check if the name of the TrustManager is empty after the ':'
			if ((pidAux=pid.split(":")).length<2) {
				
				pid = pidAux[0] + ":" + InetAddress.getLocalHost().getHostName();
			}
		}
		
		
		
		Part[] tmAttr = new Part[]{
			new Part(ServiceAttribute.PID.name(), pid),
			new Part(ServiceAttribute.DESCRIPTION.name(), "TrustManager"),
			new Part(ServiceAttribute.SID.name(), pid)
		};
		// variables for the attepts to get a PID
		int attempts = 3;
		boolean havePID=false;
		String pidaux = pid;
		Registration serviceInfo =null;
		
		// Try to get a PID
		while (!havePID&&attempts>1){

			LOG.debug("TrustManager attempts to obtain PID: " + pidaux);
			try{
				serviceInfo = nm.registerService(tmAttr,
						"http://localhost:9090" + TRUST_MANAGER_PATH, BACKBONE_SOAP);
				havePID= true;
			}catch(IllegalArgumentException ex){
				attempts--;
				
				// generate selected PID + a random UUID
				 pidaux+= pid + java.util.UUID.randomUUID();
				tmAttr = new Part[]{
						new Part(ServiceAttribute.PID.name(), pidaux),
						new Part(ServiceAttribute.DESCRIPTION.name(), "TrustManager"),
						new Part(ServiceAttribute.SID.name(), pidaux)
					};
			}
		}
		
		// If it didn't obtain a selected PID after the attempts 
		if (!havePID|| serviceInfo== null){
			throw new IllegalArgumentException(
					"PID already in use, and the attemps to generate a new one fail. Please choose a different one.");
		}
		
		LOG.debug("TrustManager gets PID: " + pidaux);
		
		// updating configuration
		Part[] attributes = serviceInfo.getAttributes();
		Properties confUpdates = new Properties();
		for (Part attr : attributes) {
			if(attr.getKey().contentEquals(ServiceAttribute.CERT_REF.name())){
				confUpdates.put(
						TrustManagerConfigurator.CERTIFICATE_REF, attr.getValue());
			}
			if (attr.getKey().contentEquals(ServiceAttribute.PID.name())){
				confUpdates.put(TrustManagerConfigurator.PID,pidaux);
			}
		}
		configurator.setConfiguration(confUpdates);
		
		return serviceInfo;
	}

	public Class getTrustModelConfigurator(){
		return TrustModelRegistry.getInstance().getCurrentTrustModel().getConfigurator();
	}

	public String getTrustToken(String identifier) throws RemoteException {
		return TrustModelRegistry.getInstance().getCurrentTrustModel().getTrustToken(identifier);
	}

	public String createTrustToken() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean createTrustTokenWithFriendlyName(String identifier)
	throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}
}
