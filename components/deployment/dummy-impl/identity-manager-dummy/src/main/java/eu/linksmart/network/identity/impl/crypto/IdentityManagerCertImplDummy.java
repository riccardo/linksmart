package eu.linksmart.network.identity.impl.crypto;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;

import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.identity.impl.IdentityManagerImplDummy;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.utils.Part;

@Component(name="IdentityManager", immediate=true)
@Service({IdentityManager.class})
public class IdentityManagerCertImplDummy extends IdentityManagerImplDummy {
	/**
	 * The identifier of this implementation bundle.
	 */
	private static String IDENTITY_MGR = IdentityManagerCertImplDummy.class.getSimpleName();
	
	@Reference(name="CryptoManager",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindCryptoManager", 
			unbind="unbindCryptoManager",
			policy=ReferencePolicy.STATIC)
	protected CryptoManager cryptoManager;
	
	@Reference(name="NetworkManagerCore",
			cardinality = ReferenceCardinality.OPTIONAL_UNARY,
			bind="bindNetworkManagerCore", 
			unbind="unbindNetworkManagerCore",
			policy=ReferencePolicy.DYNAMIC)
		    protected NetworkManagerCore networkManagerCore;
	
	protected void bindCryptoManager(CryptoManager cryptoManager) {
		System.out.println("IdentityManagerDummy::binding cryptomanager");
		this.cryptoManager = cryptoManager;
		super.setCryptoManager(cryptoManager);
	}

	protected void unbindCryptoManager(CryptoManager cryptoManager) {
		System.out.println("IdentityManagerDummy::un-binding cryptomanager");
		super.unsetCryptoManager(cryptoManager);
		this.cryptoManager = null;
	}
	
	protected void bindNetworkManagerCore(NetworkManagerCore networkManagerCore) {
		System.out.println("IdentityManagerCertDummy::binding networkmanager-core");
		this.networkManagerCore = networkManagerCore;
		super.setNetworkManagerCore(networkManagerCore);
    }

	protected void unbindNetworkManagerCore(NetworkManagerCore networkManagerCore) {
		System.out.println("IdentityManagerCertDummy::un-binding networkmanager-core");
		super.unsetNetworkManagerCore(networkManagerCore);
		this.networkManagerCore = null;
	}
	
	@Activate
	protected void activate(ComponentContext context) {
		System.out.println("[activating IdentityManagerCertDummy]");
		super.activate(context);
	}

	@Deactivate
	protected void deactivate(ComponentContext context) {
		System.out.println("de-activating IdentityManagerCertDummy");
		super.deactivate(context);
	}
	
	public Registration createServiceByAttributes(Part[] parts) {
		VirtualAddress virtualAddress = new VirtualAddress();
		Registration registration = new Registration(virtualAddress, parts);
		if (!localServices.containsKey(virtualAddress)) {
			localServices.put(virtualAddress, registration);
		}
		System.out.println("service registered, created VirtualAddress (cert) is: " + registration.toString());
		return registration;
	}

	@Override
	public String getIdentifier() {
		return IDENTITY_MGR;
	}
}
