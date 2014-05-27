package eu.linksmart.network.identity.impl.crypto;

import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.identity.impl.IdentityManagerImpl;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.utils.Part;
import eu.linksmart.utils.PartConverter;

/**
 * The service implementation of Identity Manager.
 * The VirtualAddress is bound by a certificate with specific
 * attributes.
 * @author Vinkovits
 *
 */
@Component(name="IdentityManager", immediate=true)
@Service({IdentityManager.class})
public class IdentityManagerCertImpl extends IdentityManagerImpl {
	/**
	 * The identifier of this implementation bundle.
	 */
	private static String IDENTITY_MGR = IdentityManagerCertImpl.class.getSimpleName();
	
	@Reference(name="NetworkManagerCore",
	cardinality = ReferenceCardinality.OPTIONAL_UNARY,
	bind="bindNetworkManagerCore", 
	unbind="unbindNetworkManagerCore",
	policy=ReferencePolicy.DYNAMIC)
	protected NetworkManagerCore networkManagerCore;

	@Reference(name="CryptoManager",
	cardinality = ReferenceCardinality.MANDATORY_UNARY,
	bind="bindCryptoManager", 
	unbind="unbindCryptoManager",
	policy=ReferencePolicy.DYNAMIC)
	protected CryptoManager cryptoManager;
	
	protected void bindNetworkManagerCore(NetworkManagerCore networkManagerCore) {
		LOG.debug("IdentityManagerCert::binding networkmanager-core");
		this.networkManagerCore = networkManagerCore;
		super.setNetworkManagerCore(networkManagerCore);
    }

	protected void unbindNetworkManagerCore(NetworkManagerCore networkManagerCore) {
		LOG.debug("IdentityManagerCert::un-binding networkmanager-core");
		this.networkManagerCore = null;
		super.unsetNetworkManagerCore(networkManagerCore);
	}

	protected void bindCryptoManager(CryptoManager cryptoManager) {
		LOG.debug("IdentityManager::binding cryptomanager");
		this.cryptoManager = cryptoManager;
		super.setCryptoManager(this.cryptoManager);
	}

	protected void unbindCryptoManager(CryptoManager cryptoManager) {
		LOG.debug("IdentityManager::un-binding cryptomanager");
		this.cryptoManager = null;
		super.unsetCryptoManager(cryptoManager);
	}
	
	@Activate
	protected void activate(ComponentContext context) {
		LOG.info("[activating IdentityManagerCert]");
		super.activate(context);
	}

	@Deactivate
	protected void deactivate(ComponentContext context) {
		LOG.info("de-activating IdentityManagerCert");
		super.deactivate(context);
	}

	/**
	 * Overrides the IdentityManager's createServiceByAttributes and creates
	 * from the attributes a certificate.
	 * If a certificate attribute is included than the identifier is checked
	 * in the CryptoManager and if a certificate is found it is used.
	 * @return null if there was error creating the VirtualAddress TODO add appropriate exceptions
	 */
	public Registration createServiceByAttributes(Part[] parts) {
		Properties attributes = PartConverter.toProperties(parts);
		VirtualAddress virtualAddress = createUniqueVirtualAddress();
		try{
			//create XML to be placed into certificate of service
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			attributes.storeToXML(bos, "");
			String xmlAttributes = bos.toString();
			//if attributes contains certificate reference try to load it
			if(attributes.containsKey(ServiceAttribute.CERT_REF.name())) {
				String certRef = attributes.getProperty(ServiceAttribute.CERT_REF.name());
				Properties certAttributes = cryptoManager.getAttributesFromCertificate(certRef);
				if (certAttributes.size() != 0) {
					//if other attributes were also provided check if they match
					Set<Object> keys = attributes.keySet();
					boolean mismatch = false;
					for(Object key : keys) {
						Object value = certAttributes.get(key);
						if(value != attributes.get(key) && !key.equals(ServiceAttribute.CERT_REF.name())){
							mismatch = true;
							break;
						}
					}
					//if the provided attributes don't match better not create VirtualAddress
					if(!mismatch) {
						attributes = certAttributes;
						cryptoManager.addPrivateKeyForService(virtualAddress.toString(), certRef);
						cryptoManager.addCertificateForService(virtualAddress.toString(), certRef);
					} else {
						LOG.warn("Tried creating VirtualAddress from certificate with wrong attributes.");
						return null;
					}
				} else {
					LOG.warn("Certificate reference does not exist!");
					return null;
				}
			} else {
				// Provide the attributes and the service to generate the certificate
				String certRef = cryptoManager.generateCertificateWithAttributes(
						xmlAttributes, virtualAddress.toString());
				attributes.put(ServiceAttribute.CERT_REF.name(), certRef);
			}
			Registration info = new Registration(virtualAddress, PartConverter.fromProperties(attributes));
			addLocalService(virtualAddress, info);
			LOG.debug("Created VirtualAddress: " + info.toString());
			return info;
		}catch(Exception e){
			LOG.error("Cannot create service!",e);
			return null;
		}
	}

	protected void setCryptoManager(CryptoManager cryptoManager) {
		this.cryptoManager = cryptoManager;
	}

	@Override
	public String getIdentifier() {
		return IDENTITY_MGR;
	}
}
