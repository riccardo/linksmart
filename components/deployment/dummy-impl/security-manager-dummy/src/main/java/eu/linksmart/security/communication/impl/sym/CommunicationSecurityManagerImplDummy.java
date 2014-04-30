package eu.linksmart.security.communication.impl.sym;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.clients.RemoteWSClientProvider;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProperty;
import eu.linksmart.security.communication.SecurityProtocol;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.trustmanager.TrustManager;

@Component(name="CommunicationSecurityManagerSym", immediate=true)
@Service
public class CommunicationSecurityManagerImplDummy implements CommunicationSecurityManager {
	
	private CommunicationSecurityManagerConfigurator configurator = null;
	
	private double trustThreshold = 0.0;
	
	@Reference(name="ConfigurationAdmin",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindConfigAdmin",
            unbind="unbindConfigAdmin",
            policy=ReferencePolicy.STATIC)
    protected ConfigurationAdmin configAdmin = null;
	
	@Reference(name="CryptoManager",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindCryptoManager", 
			unbind="unbindCryptoManager",
			policy=ReferencePolicy.DYNAMIC)
	private CryptoManager cryptoMgr = null;
	
	@Reference(name="TrustManager",
			cardinality = ReferenceCardinality.OPTIONAL_UNARY,
			bind="bindTrustManager", 
			unbind="unbindTrustManager",
			policy=ReferencePolicy.DYNAMIC)
	private TrustManager trustMgr = null;
	
	@Reference(name="RemoteWSClientProvider",
			cardinality = ReferenceCardinality.OPTIONAL_UNARY,
			bind="bindWSProvider", 
			unbind="unbindWSProvider",
			policy=ReferencePolicy.DYNAMIC)
	private RemoteWSClientProvider wsProvider = null;
	
	protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
    	System.out.println("SecurityManagerDummy::binding ConfigurationAdmin");
        this.configAdmin = configAdmin;
    }

    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
    	System.out.println("SecurityManagerDummy::un-binding ConfigurationAdmin");
        this.configAdmin = null;
    }
	
	protected void bindCryptoManager(CryptoManager cryptoManager) {
		System.out.println("SecurityManagerDummy::binding crypto-manager");
		cryptoMgr = cryptoManager;
	}

	protected void unbindCryptoManager(CryptoManager cryptoManager) {
		System.out.println("SecurityManagerDummy::un-binding crypto-manager");
		cryptoMgr = null;
	}

	protected void bindTrustManager(TrustManager trustManager) {
		System.out.println("SecurityManagerDummy::binding trust-manager");
		trustMgr = trustManager;
	}

	protected void unbindTrustManager(TrustManager trustManager) {
		System.out.println("SecurityManagerDummy::un-binding trust-manager");
		trustMgr = null;
	}
	
	protected void bindWSProvider(RemoteWSClientProvider wsProvider) {
		System.out.println("SecurityManagerDummy::binding wsclient-provider");
		this.wsProvider = wsProvider;
	}

	protected void unbindWSProvider(RemoteWSClientProvider wsProvider) {
		System.out.println("SecurityManagerDummy::un-binding wsclient-provider");
		this.wsProvider = null;
	}

	@Activate
	protected void activate(ComponentContext context) {
		System.out.println("[activating SecurityManagerDummy]");
		this.configurator = new CommunicationSecurityManagerConfigurator(this, context.getBundleContext(), this.configAdmin);
	}
	
	@Deactivate
	protected void deactivate(ComponentContext context) {
		System.out.println("de-activating SecurityManagerDummy");
	}

	public SecurityProtocol getSecurityProtocol(VirtualAddress clientVirtualAddress, VirtualAddress serverVirtualAddress) {
		SecurityProtocol securityProtocol = new SecurityProtocolImpl(
				clientVirtualAddress, 
				serverVirtualAddress, 
				cryptoMgr, 
				trustMgr, 
				trustThreshold);
		return securityProtocol;
	}
	
	public boolean canBroadcast() {
		return false;
	}

	public SecurityProtocol getBroadcastSecurityProtocol(VirtualAddress clientVirtualAddress) {
		throw new RuntimeException("Manager has no broadcast service");
	}

	public List<SecurityProperty> getProperties() {
		ArrayList<SecurityProperty> properties = new ArrayList<SecurityProperty>();
		properties.add(SecurityProperty.Authenticity);
		properties.add(SecurityProperty.Confidentiality);
		properties.add(SecurityProperty.Integrity);
		properties.add(SecurityProperty.KeyAgreement);
		properties.add(SecurityProperty.Symmetric);
		properties.add(SecurityProperty.Asymmetric);
		properties.add(SecurityProperty.Unicast);
		return properties;
	}
	
	public void applyConfigurations(Hashtable updates) {
		if(updates.containsKey(CommunicationSecurityManagerConfigurator.TRUST_THRESHOLD)) {
			try{
				trustThreshold = Double.valueOf(configurator.get(CommunicationSecurityManagerConfigurator.TRUST_THRESHOLD));
			} catch(NumberFormatException ne) {
				System.out.println("Provided trust threshold  is not a double!");
			}
		}
		if(updates.containsKey(CommunicationSecurityManagerConfigurator.TRUST_MANAGER_URL)) {
			configurator.get(CommunicationSecurityManagerConfigurator.TRUST_MANAGER_URL);
		}
	}
}