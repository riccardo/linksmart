package eu.linksmart.network.identity.impl.crypto;

import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.Set;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDAttribute;
import eu.linksmart.network.HIDInfo;
import eu.linksmart.network.identity.impl.IdentityManagerImpl;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.utils.Part;
import eu.linksmart.utils.PartConverter;

/**
 * The CryptoHID implementation of Identity Manager.
 * The HID is bound by a certificate with specific
 * attributes.
 * @author Vinkovits
 *
 */
public class IdentityManagerCryptoImpl extends IdentityManagerImpl {
	/**
	 * The identifier of this implementation bundle.
	 */
	private static String IDENTITY_MGR = IdentityManagerCryptoImpl.class
	.getSimpleName();
	/**
	 * CryptoHIDs are based on certificates.
	 */
	private CryptoManager cryptoManager;

	/**
	 * Overrides the IdentityManager's createHIDForAttributes and creates
	 * from the attributes a certificate.
	 * If a certificate attribute is included than the identifier is checked
	 * in the CryptoManager and if a certificate is found it is used.
	 * @return null if there was error creating the HID TODO add appropriate exceptions
	 */
	public HIDInfo createHIDForAttributes(Part[] parts) {
		Properties attributes = PartConverter.toProperties(parts);
		HID hid = createUniqueHID();
		try{
			//create XML to be placed into certificate of CryptoHID
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			attributes.storeToXML(bos, "");
			String xmlAttributes = bos.toString();
			//if attributes contains certificate reference try to load it
			if(attributes.containsKey(HIDAttribute.CERT_REF.name())) {
				String certRef = attributes.getProperty(HIDAttribute.CERT_REF.name());
				Properties certAttributes = cryptoManager.getAttributesFromCertificate(certRef);
				if (certAttributes.size() != 0) {
					//if other attributes were also provided check if they match
					Set<Object> keys = attributes.keySet();
					boolean mismatch = false;
					for(Object key : keys) {
						Object value = certAttributes.get(key);
						if(value != attributes.get(key) && !key.equals(HIDAttribute.CERT_REF.name())){
							mismatch = true;
							break;
						}
					}
					//if the provided attributes don't match better not create HID
					if(!mismatch) {
					cryptoManager.addPrivateKeyForHID(hid.toString(), certRef);
					cryptoManager.addCertificateForHID(hid.toString(), certRef);
					} else {
						LOG.warn("Tried creating HID from certificate with wrong attributes.");
						return null;
					}
				} else {
					LOG.warn("Certificate reference does not exist!");
					return null;
				}
			} else {
				// Provide the attributes and the hid to generate the certificate
				String certRef = cryptoManager.generateCertificateWithAttributes(
						xmlAttributes, hid.toString());
				attributes.put(HIDAttribute.CERT_REF.name(), certRef);
			}
			HIDInfo info = new HIDInfo(hid, PartConverter.fromProperties(attributes));
			addLocalHID(hid, info);
			LOG.debug("Created HID: " + info.toString());
			return info;
		}catch(Exception e){
			LOG.error("Cannot create CryptoHID!",e);
			return null;
		}
	}

	protected void bindCryptoManager(CryptoManager cryptoManager) {
		this.cryptoManager = cryptoManager;
	}

	protected void unbindCryptoManager(CryptoManager cryptoManager) {
		this.cryptoManager = null;
	}
	
	public String getIdentifier() {
		return IDENTITY_MGR;
	}
}
