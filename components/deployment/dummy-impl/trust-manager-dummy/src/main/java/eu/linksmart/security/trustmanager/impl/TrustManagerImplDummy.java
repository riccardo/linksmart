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

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.clients.RemoteWSClientProvider;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.security.trustmanager.TrustManager;
import eu.linksmart.security.trustmanager.TrustManagerConfiguration;

import eu.linksmart.security.trustmanager.trustmodel.TrustModelRegistry;

@Component(name="TrustManager")
@Service({TrustManager.class})
@Properties({
    @Property(name="service.exported.interfaces", value="*"),
    @Property(name="service.exported.configs", value="org.apache.cxf.ws"),
    @Property(name="org.apache.cxf.ws.address", value="http://0.0.0.0:9090/cxf/services/TrustManager")
})
public class TrustManagerImplDummy implements TrustManager, TrustManagerConfiguration {

	public static final String TRUST_MANAGER_PATH = "/cxf/services/TrustManager";
	public final static String CURRENT_TRUST_MODEL = "NullTrustModel";
	private static String BACKBONE_SOAP = "eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl";
	private TrustManagerConfigurator configurator;
	
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
		System.out.println("TrustManagerDummy::binding ConfigurationAdmin");
		this.configAdmin = configAdmin;
    }
    
    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
    	System.out.println("TrustManagerDummy::un-binding ConfigurationAdmin");
    	this.configAdmin = null;
    }
    
    protected void bindNetworkManager(NetworkManager netManager) {
    	System.out.println("TrustManagerDummy::binding network-manager");
		this.nm = netManager;
	}

	protected void unbindNetworkManager(NetworkManager nm) {
		System.out.println("TrustManagerDummy::un-binding ConfigurationAdmin");
	}
	
	protected void bindWSProvider(RemoteWSClientProvider clientProvider) {
		System.out.println("TrustManagerDummy::binding ws-client");
		this.clientProvider = clientProvider;
	}

	protected void unbindWSProvider(RemoteWSClientProvider clientProvider) {
		System.out.println("TrustManagerDummy::un-binding ws-client");
		this.clientProvider = null;
	}

	@Activate
	protected void activate(ComponentContext context) {
		System.out.println("[activating TrustManagerDummy]");
		configurator = new TrustManagerConfigurator(this, context.getBundleContext(), configAdmin);
		configurator.registerConfiguration();
	}

	@Deactivate
	protected void deactivate(ComponentContext ccontext){
		System.out.println("de-activating TrustManagerDummy");
	}

	@Override
	public boolean setCurrentTrustModel(String identifier) {
		TrustModelRegistry registry = TrustModelRegistry.getInstance();
		boolean success = registry.setCurrentTrustModel(identifier);	
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

	@Override
	public double getTrustValue(String token) {
		return 1;
	}

	@Override
	public double getTrustValueWithIdentifier(String token, String trustModelIdentifier) {
		return 0;
	}

	public void applyConfigurations(Hashtable updates) {
		if (updates.containsKey(TrustManagerConfigurator.TM_TRUST_MODEL)) {
			System.out.println("Applying new trust model configurations: " + updates.get(TrustManagerConfigurator.TM_TRUST_MODEL));
			String trustModelValue = (String) updates.get(TrustManagerConfigurator.TM_TRUST_MODEL);
			setCurrentTrustModel(trustModelValue);
		}
	}

	@Override
	public Class getTrustModelConfigurator() {
		return TrustModelRegistry.getInstance().getCurrentTrustModel().getConfigurator();
	}

	@Override
	public String getTrustToken(String identifier) throws RemoteException {
		return TrustModelRegistry.getInstance().getCurrentTrustModel().getTrustToken(identifier);
	}

	@Override
	public String createTrustToken() throws RemoteException {
		return null;
	}

	@Override
	public boolean createTrustTokenWithFriendlyName(String identifier) throws RemoteException {
		return false;
	}
}
