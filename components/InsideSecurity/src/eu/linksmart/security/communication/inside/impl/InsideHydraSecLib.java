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
package eu.linksmart.security.communication.inside.impl;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Hashtable;
import java.util.Properties;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.osgi.service.component.ComponentContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import eu.linksmart.security.communication.VerificationFailureException;
import eu.linksmart.security.communication.inside.InsideHydra;
import eu.linksmart.security.communication.utils.NonceGenerator;
import eu.linksmart.security.communication.utils.impl.NonceGeneratorFactory;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.communication.CryptoException;


/**
 * 
 * @author Junaid Khan, Julian Schuette
 * 
 */
public class InsideHydraSecLib implements InsideHydra {

	private static Logger logger = Logger.getLogger(InsideHydraSecLib.class.getName());
	public static final String FILE_SEPERATOR =
		System.getProperty("file.separator");
	private String KEYSTORE_PASS;
	private String KEYSTORE_FILE;
	private String KEYSTORE_TYPE;
	private String INSIDE_KEY_ALIAS;
	private String KEYSTORE_DEPLOY_DIR;
	private NonceGenerator nonceGenerator = null;
	private String lastNonce = "";
	private Properties settings;
	private static KeyStore keyStore;
	static X509Certificate cert;
	private CryptoManager cryptoManager;

	// set up and configuration (adapted from core hydra)
	
	public InsideHydraSecLib(){
		try {
			init();
		} catch (Exception e) {
			logger.error("Cannot initialize InsideHydraSecLib", e);
		}
	}
	
	/**
	 * Activates and initializes the InsideSecurity bundle
	 * @param context
	 */
	protected void activate(ComponentContext context) {
		try {
			init();
			logger.debug("InsideSecurity activated");
		} catch (Exception e) {
			logger.error("Cannot initialize insideSecurity!", e);
		}
	}
	
	/**
	 * Deactivates and initializes the InsideSecurity bundle
	 * @param context
	 */
	protected void deactivate(ComponentContext context){
		
	}
	
	private void init() throws FileNotFoundException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException {

		logger.debug("Loading settings for Inside Hydra");
		settings = new Properties();
		logger.debug("Loading file " + "settings.properties");
		try {
			settings.load(this.getClass().getResourceAsStream("/settings.properties"));
		} catch (Exception e) {
			logger.error("Error loading settings for InsideSecurity!", e);
		}
		
		KEYSTORE_FILE = settings.getProperty("keystore.file", "linksmart.keystore.jks");
		KEYSTORE_PASS = settings.getProperty("keystore.pass", "hydranmpw");
		KEYSTORE_TYPE = settings.getProperty("keystore.type", "JKS");
		INSIDE_KEY_ALIAS = settings.getProperty("inside.key.alias", "mynmkeypair");
		KEYSTORE_DEPLOY_DIR = settings.getProperty("keystore.insidehydra.deploydir", "InsideHydra");
		nonceGenerator = NonceGeneratorFactory.getInstance();

		Hashtable<String, String> hashFilesExtract = new Hashtable<String, String>();
		hashFilesExtract.put(KEYSTORE_DEPLOY_DIR + FILE_SEPERATOR + KEYSTORE_FILE, KEYSTORE_FILE);
		
		JarUtil.createFolder(KEYSTORE_DEPLOY_DIR);
		
		try {
			JarUtil.extractFilesJar(hashFilesExtract);
		} catch (IOException e1) {
			logger.error("Required library does not exist.");
		}
		
		// load the keystore
		try {
			loadKeyStore();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	static {
		/*
		 * We are using the bouncycastle crypto provider (Sun's provider might
		 * not be available on PS3).
		 */
		if (Security.getProvider("BC") == null) {
			Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 6);
		}
		org.apache.xml.security.Init.init();
	}
	
	/**
	 * Binds the CryptoManager
	 * @param cm
	 */
	protected void bindCryptoManager(CryptoManager cm){
		this.cryptoManager = cm;
	}
	
	/**
	 * Unbinds the CryptoManager
	 * @param cm
	 */
	protected void unbindCryptoManager(CryptoManager cm){
		this.cryptoManager = null;
	}

	private void loadKeyStore() throws KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException {
		keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
		InputStream fis = new FileInputStream(KEYSTORE_DEPLOY_DIR + "/" + KEYSTORE_FILE);
		try {
			keyStore.load(fis, KEYSTORE_PASS.toCharArray());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	
	// --------------------------------------------------------------
	// InsideHydraCommunication helper methods
	// asymmetric encryption and decryption (adapted from core hydra by julian)

	public String asymEncrypt(String encstr, String receiverHID) throws Exception {
		String result = "";
		try {
			if (encstr == null) {
				encstr = "";
			}

			// Transform input string to an hydra:InsideProtected-XML document
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.newDocument();

			Element rootElement = document.createElementNS(INSIDE_SECURITY_NAMESPACE, INSIDE_PROTECTED_MESSAGE_NAME);

			rootElement.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:linksmart", INSIDE_SECURITY_NAMESPACE);
			Element protectedElement = document.createElementNS(INSIDE_SECURITY_NAMESPACE, INSIDE_PROTECTED_ELEMENT);

			// Add a nonce to prevent replay attacks
			String nonce = nonceGenerator.getNextNonce();
			Element nonceElement = document.createElementNS(INSIDE_SECURITY_NAMESPACE, INSIDE_NONCE_ELEMENT);
			nonceElement.appendChild(document.createTextNode(nonce));
			Element contentElement = document.createElementNS(INSIDE_SECURITY_NAMESPACE, INSIDE_CONTENT_ELEMENT);
			contentElement.appendChild(document.createTextNode(encstr));
			protectedElement.appendChild(nonceElement);
			protectedElement.appendChild(contentElement);
			rootElement.appendChild(protectedElement);
			document.appendChild(rootElement);

			// Get a public key (KEK) to encrypt the symmetric key (DEK).
			PublicKey kek = loadKeyEncryptionKey(receiverHID);

			// Get a key to encrypt the data (DEK)
			/*
			 * Load a symmetric encryption key used for encryption. The alias
			 * used is (SES_receiverHID).
			 */
			String keystoreAlias = "SES_" + receiverHID;
			Key dek = loadDataEncryptionKey(keystoreAlias);

			// Encrypt the DEK using the KEK. This is called "Wrapping"
			String algorithmURI = XMLCipher.RSA_v1dot5;
			logger.debug("retrieving " + XMLCipher.RSA_v1dot5);

			XMLCipher keyCipher = XMLCipher.getInstance(algorithmURI);
			keyCipher.init(XMLCipher.WRAP_MODE, kek);
			EncryptedKey encryptedKey = keyCipher.encryptKey(document, dek);

			// Add the identifier to the message. So the receiver knows which
			// key to
			// use for decryption.
			// encryptedKey.setCarriedName(identifier);

			// Now do the "real" encryption of the content using AES.
			algorithmURI = XMLCipher.AES_128;
			XMLCipher xmlCipher = XMLCipher.getInstance(algorithmURI);
			xmlCipher.init(XMLCipher.ENCRYPT_MODE, dek);

			// Add the "KeyInfo" element to the message so the receiver gets all
			// necessary information
			EncryptedData encryptedData = xmlCipher.getEncryptedData();
			KeyInfo keyInfo = new KeyInfo(document);
			keyInfo.add(encryptedKey);
			keyInfo.addText(receiverHID);
			encryptedData.setKeyInfo(keyInfo);
			xmlCipher.doFinal(document, rootElement, true);

			// Write XML to string
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			DOMSource source = new DOMSource(document);

			StreamResult sresult = new StreamResult();
			sresult.setOutputStream(new java.io.ByteArrayOutputStream());
			transformer.transform(source, sresult);
			result = (new StringBuffer()).append(sresult.getOutputStream()).toString();

		} catch (Exception e) {
			logger.error("error in encr()", e);
		}

		// return encryptedXMLAsString;
		return result;

	}

	// decryption
	// taken from core hydra
	// decrypt with private key

	public String AsDecrypt(String encrData) throws Exception, CryptoException {
		String result = "";
		try {
			// Convert input string to XML document

			javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

			ByteArrayInputStream bis = new ByteArrayInputStream(encrData.getBytes());
			Document document = db.parse(bis);

			// Get the "EncryptedData" element from DOM tree.
			XMLCipher xmlCipher = XMLCipher.getInstance();
			xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
			Element encryptedDataElement = (Element) document.getElementsByTagNameNS(EncryptionConstants.EncryptionSpecNS,
					EncryptionConstants._TAG_ENCRYPTEDDATA).item(0);
			EncryptedData encryptedDataObject = xmlCipher.loadEncryptedData(document, encryptedDataElement);

			// Load the key to be used for decrypting the xml data encryption
			// key.
			// need to get the key from keystore

			Key dek = null;
			if (encryptedDataObject.getKeyInfo().itemEncryptedKey(0) != null) {
				// Retrieve information about the Public Key used to encrypt the
				// DEK
				EncryptedKey ek = encryptedDataObject.getKeyInfo().itemEncryptedKey(0);
				 String receiverHID =
				 encryptedDataObject.getKeyInfo().getTextFromTextChild().trim();

				// Try to retrieve the corresponding Private Key
				PrivateKey kek = loadKeyDecryptionKey(receiverHID);
				
				// Set the cipher to "Unwrap" mode and use the Private Key to
				// extract the DEK
				xmlCipher.init(XMLCipher.UNWRAP_MODE, kek);
				dek = xmlCipher.decryptKey(ek, encryptedDataObject.getEncryptionMethod().getAlgorithm());
			} else {
				logger.error("Message does not contain an encrypted key element. Using only symmetric encryption is not implemented yet");
				// TODO Implement pure symmetric encryption: Get symmetric key
				// from keystore by alias (receiverHID XOR senderHID).
				// String identifier =
				// encryptedDataObject.getKeyInfo().getTextFromTextChild();
				// dek = loadSymmetricKey(identifier);
				// cipher = XMLCipher.getInstance();
				// cipher.init(XMLCipher.DECRYPT_MODE, dek);
			}

			/*
			 * The key to be used for decrypting xml data would be obtained from
			 * the keyinfo of the EncrypteData using the kek.
			 */

			xmlCipher.init(XMLCipher.DECRYPT_MODE, dek);

			// replace the encrypted data with decrypted contents in the
			// document.
			xmlCipher.doFinal(document, encryptedDataElement);

			// check the nonce
			String nonce = document.getElementsByTagName(INSIDE_NONCE_ELEMENT).item(0).getTextContent();
			if (this.lastNonce.equals(nonce)) {
				throw new VerificationFailureException("Message contained a nonce seen before. Maybe this was a replay attack?");
			}
			lastNonce = nonce;
			result = document.getElementsByTagName(INSIDE_CONTENT_ELEMENT).item(0).getTextContent();

		} catch (Exception e) {
			if (!(e instanceof VerificationFailureException)) {
				throw e;
			} else {
				throw (VerificationFailureException) e;
			}
			// EXPILICITY stating a verification failure exception
		}

		return result;
	}

	// asymmetric signing
	public String asymSign(String string) {
		// FIXME Signatures do not work yet. (Tests do not validate)
		String result = "";

		// Convert input string to XML document
		try {
			Constants.setSignatureSpecNSprefix("");
			string = Base64.encode(string.getBytes());

			PrivateKey signingKey = loadSigningKey();

			// XML Signature needs to be namespace aware
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);

			DocumentBuilder db = dbf.newDocumentBuilder();
			org.w3c.dom.Document doc = db.newDocument();

			// sign asymmetrically (TAKEN FROM CORE SECURITY)

			Element root = doc.createElementNS(INSIDE_SIGNED_MESSAGE_NAMESPACE, INSIDE_SIGNED_MESSAGE_NAME);
			root.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:linksmart", INSIDE_SIGNED_MESSAGE_NAMESPACE);

			doc.appendChild(root);
			root.appendChild(doc.createTextNode(string));

			String BaseURI = INSIDE_SIGNED_MESSAGE_NAMESPACE + "/";

			// Create an XML Signature object from the document, BaseURI and DSA
			// signature algorithm

			XMLSignature sig = new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);

			// Append the signature element to the root element before signing
			// to envelope the signature by the doc.

			root.appendChild(sig.getElement());

			// create the transforms object for the Document/Reference
			Transforms transforms = new Transforms(doc);
			transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_WITH_COMMENTS);
			// Add the above Document/Reference
			sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

			// Add in the KeyInfo for the private key certificate used
			X509Certificate cert = loadSigningCertificate();
			sig.addKeyInfo(cert);
			sig.addKeyInfo(cert.getPublicKey());
			sig.sign(signingKey);

			// Transform DOM tree to XML string.
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult sresult = new StreamResult();
			sresult.setOutputStream(new java.io.ByteArrayOutputStream());
			transformer.transform(source, sresult);
			result = (new StringBuffer()).append(sresult.getOutputStream()).toString();

		} catch (Exception e) {
			result = "";
			e.printStackTrace();
		}
		return result;

	}

	// signature verification
	public boolean AsVerify(String message) throws Exception {
		// FIXME Signatures do not work yet. (Tests do not validate)
		boolean verified = true;
		System.out.println("in verify I got: " + message);

		try {

			// string to doc conversion
			javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
			ByteArrayInputStream bis = new ByteArrayInputStream(message.getBytes());
			Document document = db.parse(bis);

			NodeList nl = document.getElementsByTagName("Signature");
			if (nl.getLength() == 0) {
				throw new Exception("Cannot find Signature element");
			}
			DOMValidateContext valContext = new DOMValidateContext(cert.getPublicKey(), nl.item(0));

			XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
			javax.xml.crypto.dsig.XMLSignature signature2 = factory.unmarshalXMLSignature(valContext);

			boolean sv = signature2.getSignatureValue().validate(valContext);
			System.out.println("signature validation status: " + sv);

			/*
			 * find the location of the signature element obtain the public key
			 * using the information in the KeyInfo element and hand it back to
			 * be used as the validation key. unmarshal and validate the
			 * signature. return true if the signature is valid and false if it
			 * is invalid.
			 */
			if (document.getElementsByTagName(INSIDE_SIGNED_MESSAGE_NAME).getLength() != 1) {
				verified = false;
				logger.info("Input does not contain " + INSIDE_SIGNED_MESSAGE_NAME);
			}

		}

		catch (Exception e) {
			e.printStackTrace();
			verified = false;
		}

		// function should return boolean value to indicate if signature can be
		// verified or not
		return verified;

	}

	// eventually put everything inside protect (sign/encrypt) and unprotect
	// (verify/decrypt)

	// get the Message out of an encrypted Document after decryption and
	// verification

	// -----------------------------------------------------------------------------

	// USEFUL METHODS taken from core hydra
	private X509Certificate loadSigningCertificate() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		if (!keyStore.containsAlias(INSIDE_KEY_ALIAS)) {
			logger.error("Keystore does not contain alias for inside Hydra signing key: " + INSIDE_KEY_ALIAS);
		}

		X509Certificate cert = (X509Certificate) keyStore.getCertificate(INSIDE_KEY_ALIAS);
		return cert;
	}

	/**
	 * Returns a symmetric secret key that can be used for encryption.
	 * 
	 * @param keystoreAlias
	 *            Alias by which the key is looked up in the keystore. If the
	 *            alias does not exist, a new key is generated and returned.
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchProviderException
	 */
	private SecretKey loadDataEncryptionKey(String keystoreAlias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException,
			UnrecoverableKeyException, NoSuchProviderException {
		SecretKey secretKey = null;

		// First try: get it from keystore
		if (keyStore.containsAlias(keystoreAlias)) {
			try {
				secretKey = (SecretKey) keyStore.getKey(keystoreAlias, this.KEYSTORE_PASS.toCharArray());
			} catch (ClassCastException e) {
				logger.warn("Unexpected: When looking for secret key " + keystoreAlias
						+ " I found an entry but it could not be casted to SecretKey. Will continue with a generated key", e);
			}
		}

		if (secretKey == null) {
			// Second try: generate the symmetric key by myself
			logger.warn("Keystore does not contain alias " + keystoreAlias + " for symmetric key. Generating one on my own");

			String jceAlgorithmName = "DESede";
			KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
			secretKey = keyGenerator.generateKey();
		}
		return secretKey;

	}

	// private keys for decryption
	private PrivateKey loadKeyDecryptionKey(String hid) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, UnrecoverableKeyException {
		String identifier = cryptoManager.getPrivateKeyReference(hid);
		return cryptoManager.getPrivateKeyByIdentifier(identifier);
//		if (!keyStore.containsAlias(INSIDE_KEY_ALIAS)) {
//			logger.error("Keystore does not contain alias for decryption key " + INSIDE_KEY_ALIAS);
//		}
//		PrivateKey decryptionKey = (PrivateKey) keyStore.getKey(INSIDE_KEY_ALIAS, INSIDE_KEY_PASS.toCharArray());
//		return decryptionKey;
	}

	// sign using private key
	private PrivateKey loadSigningKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		PrivateKey signingKey = cryptoManager.getPrivateKeyByIdentifier("hydrademo-rsa");
		return signingKey;
		
//		X509EncodedKeySpec spec = new X509EncodedKeySpec(pk);
//		KeyFactory factory = KeyFactory.getInstance("RSA");
//		RSAPublicKey publicKey = (RSAPublicKey) factory.generatePublic(spec); 
//		return publicKey;
//		
//		if (!keyStore.containsAlias(INSIDE_KEY_ALIAS)) {
//			logger.error("Keystore does not contain alias for signing key " + INSIDE_KEY_ALIAS);
//		}
//		PrivateKey signingKey = (PrivateKey) keyStore.getKey(INSIDE_KEY_ALIAS, INSIDE_KEY_PASS.toCharArray());
//
//		return signingKey;
	}

	public String validate(String data) {
		logger.debug("Validating: \n" + data + "\n -------------------------");
		String textResult = null;
		boolean result = false;

		// No need for schema validation here.
		boolean schemaValidate = false;
		final String signatureSchemaFile = "data/xmldsig-core-schema.xsd";

		javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();

		if (schemaValidate) {
			dbf.setAttribute("http://apache.org/xml/features/validation/schema", Boolean.TRUE);
			dbf.setAttribute("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.TRUE);
			dbf.setValidating(true);
			dbf.setAttribute("http://xml.org/sax/features/validation", Boolean.TRUE);
		}

		dbf.setNamespaceAware(true);
		dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);

		if (schemaValidate) {
			dbf.setAttribute("http://apache.org/xml/properties/schema/external-schemaLocation", Constants.SignatureSpecNS + " " + signatureSchemaFile);
		}

		try {
			javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(new org.apache.xml.security.utils.IgnoreAllErrorHandler());

			if (schemaValidate) {
				db.setEntityResolver(new org.xml.sax.EntityResolver() {
					public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) throws org.xml.sax.SAXException {

						if (systemId.endsWith("xmldsig-core-schema.xsd")) {
							try {
								return new org.xml.sax.InputSource(new FileInputStream(signatureSchemaFile));
							} catch (FileNotFoundException ex) {
								throw new org.xml.sax.SAXException(ex);
							}
						} else {
							return null;
						}
					}
				});
			}

			// Create DOM tree from input data as XML string.
			ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());
			org.w3c.dom.Document doc = db.parse(bis);
			Element nscontext = XMLUtils.createDSctx(doc, "ds", Constants.SignatureSpecNS);

			// Check for a "ds:Signature" element in the DOM tree
			Element sigElement = (Element) XPathAPI.selectSingleNode(doc, "//ds:Signature[1]", nscontext);
			XMLSignature signature = new XMLSignature(sigElement, INSIDE_SIGNED_MESSAGE_NAMESPACE);

			// XMLUtils.outputDOMc14nWithComments(signature.getElement(),
			// System.out);

			// Get the KeyInfo element within the signature element.
			KeyInfo ki = signature.getKeyInfo();

			if (ki != null) {

				// There should be a X509.3 certificate contained in the
				// KeyInfo...
				if (ki.containsX509Data()) {
					logger.debug("Could find a X509Data element in the KeyInfo");
				}
				X509Certificate cert = signature.getKeyInfo().getX509Certificate();

				if (cert != null) {
					// Try to validate the signature using the contained
					// certificate.
					result = signature.checkSignatureValue(cert);
					logger.debug("XML signature is " + ((result == true) ? "valid " : "invalid !!!!!"));
				} else {
					// If there is no certificate in the KeyInfo, the maybe a
					// public key?
					logger.warn("Did not find a Certificate");
					PublicKey pk = signature.getKeyInfo().getPublicKey();

					if (pk != null) {
						// Try to validate the signature using the public key
						result = signature.checkSignatureValue(pk);
						logger.debug("The XML signature in file is " + ((result == true) ? "valid " : "invalid !!!!!"));
					} else {
						logger.debug("Did not find a public key, so I can't check the signature");
					}
				}
			} else {
				logger.debug("Did not find a KeyInfo");
			}
			/*
			 * Decide whether Signature was valid or not
			 */
			if (result == true) {
				Text textElement = (Text) XPathAPI.selectSingleNode(doc, "/hydra:InsideSignedProtectedMessage/text()");
				textResult = new String(Base64.decode(textElement.getTextContent()));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return textResult;
	}

	private PublicKey loadKeyEncryptionKey(String receiverHID) throws InvalidKeySpecException, KeyStoreException, NoSuchAlgorithmException, FileNotFoundException, CertificateException{
		loadKeyStore();
		String identifier = cryptoManager.getCertificateReference(receiverHID);
		return cryptoManager.getCertificateByIdentifier(identifier).getPublicKey();
//		if (!keyStore.containsAlias(receiverHID)) {
//			logger.error("Keystore does not contain alias for encryption key " + receiverHID);
//		}
//		PublicKey publicKey = (PublicKey) keyStore.getCertificate(receiverHID).getPublicKey();
//		return publicKey;
	}

	// ---------------------------------------------------------------------------------


	public String protectInsideHydra(String message, String receiverHID) throws Exception {
		String encryptedMessage = asymEncrypt(message, receiverHID);
		// String signedMessage = asymSign(encryptedMessage, senderHID);
		return encryptedMessage;
	}

	public String unprotectInsideHydra(String message) throws Exception {
		// if (AsVerify(message)) {
		String decryptedMessage = AsDecrypt(message);
		return decryptedMessage;
		// } else {
		// return "";
		// }
	}

}