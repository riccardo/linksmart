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
package eu.linksmart.security.communication.core.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import eu.linksmart.security.communication.CryptoException;
import eu.linksmart.security.communication.VerificationFailureException;
import eu.linksmart.security.communication.core.SecurityLibrary;
import eu.linksmart.security.communication.utils.NonceGenerator;
import eu.linksmart.security.communication.utils.impl.NonceGeneratorFactory;

/**
 * This class implements the <code>SecurityLibrary</code> interface.
 * 
 * @author Junaid Khan, Julian Schuette
 * 
 */
public class SecurityLibraryImpl implements SecurityLibrary {

	private String KEY_ENCRYPTION_KEY_FILE;
	private static Logger logger = Logger.getLogger(SecurityLibraryImpl.class.getName());
	private String KEYSTORE_PASS;
	private String KEYSTORE_FILE;
	private String KEYSTORE_TYPE;
	private String CORE_KEY_ALIAS;
	private String CORE_KEY_PASS;
	private String PRIVATE_SIGNING_KEY_ALIAS;
	private NonceGenerator nonceGenerator = null;
	private String lastNonce = "";
	private short config;
	private String PRIVATE_SIGNING_KEY_PASS;
	private SecretKey privateKey; // For caching my private key
	private SecretKey secretKey; // For caching my secret key
	private PrivateKey signingKey; // For caching my secret key
	private X509Certificate signingCert; // For caching my signature certificate
	private Properties settings;

	public void setConfiguration(short config) {
		this.config = config;
	}

	/**
	 * Initialize all settings this instance of <code>SecurityLibraryImpl</code>
	 * will use.
	 * <p/>
	 * The settings can be configured in the file
	 * <code>settings.properties</code>.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 */
	private void init(short config) throws FileNotFoundException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		logger.debug("Loading settings for SecurityLibrary");
		settings = new Properties();
		logger.debug("Loading file " + "settings.properties");
		try {
			settings.load(this.getClass().getResourceAsStream("/settings.properties"));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		KEY_ENCRYPTION_KEY_FILE = settings.getProperty("kek.file", "kek");
		KEYSTORE_FILE = settings.getProperty("keystore.file", "keystore.bks");
		KEYSTORE_PASS = settings.getProperty("keystore.pass", "hydrademo");
		KEYSTORE_TYPE = settings.getProperty("keystore.type", "BKS");
		CORE_KEY_ALIAS = settings.getProperty("core.key.alias.default", "corekey");
		CORE_KEY_PASS = settings.getProperty("core.key.pass", "hydrademo");
		PRIVATE_SIGNING_KEY_PASS = settings.getProperty("core_sign.key.pass", "hydrademo");
		PRIVATE_SIGNING_KEY_ALIAS = settings.getProperty("core_sign.key.alias.default", "sigkey");
		nonceGenerator = NonceGeneratorFactory.getInstance();
		this.config = config;

		/*
		 * This is a dirty performance trick: The first time the keystore is
		 * opened and keys are loaded, some singletons have to be created. That
		 * takes ~700ms. That's why we do it here for the first time, then the
		 * first "real" encryption won't be delayed.
		 */
		try {
			loadDataEncryptionKey();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	static {
		/*
		 * We are using the bouncycastle crypto provider (Sun's provider might
		 * not be available on PS3).
		 */
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
	}

	/**
	 * Creates a new instance of the SecurityLibrary that will use the default
	 * key alias for Core Hydra communication. If the keystore file could not be
	 * opened or the default Core Hydra key alias is not present, an exception
	 * will be thrown.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 */
	public SecurityLibraryImpl() throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException,
			CertificateException {
		init(SecurityLibrary.CONF_ENC);
	}

	/**
	 * Creates a new instance of the SecurityLibrary that will use the default
	 * key alias for Core Hydra communication. If the keystore file could not be
	 * opened or the default Core Hydra key alias is not present, an exception
	 * will be thrown.
	 * 
	 * @param config
	 *            The configuration to be used for protecting the messages. See
	 *            constants in <code>SecurityLibrary</code>.
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 */
	public SecurityLibraryImpl(short config) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException,
			CertificateException {
		init(config);
	}

	/**
	 * Creates a new instance of the SecurityLibrary that will use
	 * <code>coreKeyIdentifier</code> as the alias for the Core Hydra key. If
	 * <code>coreKeyIdentifier</code> does not exist in the keystore, an
	 * exception will be thrown.
	 * 
	 * @param coreKeyIdentifier
	 */
	public SecurityLibraryImpl(String coreKeyIdentifier) {
		// TODO implement SecurityLibraryImpl(String coreKeyIdentifier)
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * @see eu.linksmart.security.communication.core.SecurityLibrary#protectCoreMessage(java.lang.String)
	 */
	public String protectCoreMessage(String plaintextData) throws CryptoException {
		String result = "";
		if (config == SecurityLibrary.CONF_NULL) {
			logger.debug("Skipping protection. NULL config is set");
			return plaintextData;
		}
		try {
			if (plaintextData == null) {
				plaintextData = "";
			}
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.newDocument();

			Element rootElement = document.createElementNS(CORE_SECURITY_NAMESPACE, CORE_PROTECTED_MESSAGE_NAME);
			rootElement.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:linksmart", CORE_SECURITY_NAMESPACE);
			Element protectedElement = document.createElementNS(CORE_SECURITY_NAMESPACE, CORE_PROTECTED_ELEMENT);

			String nonce = nonceGenerator.getNextNonce();
			Element nonceElement = document.createElementNS(CORE_SECURITY_NAMESPACE, CORE_NONCE_ELEMENT);
			nonceElement.appendChild(document.createTextNode(nonce));

			Element contentElement = document.createElementNS(CORE_SECURITY_NAMESPACE, CORE_CONTENT_ELEMENT);
			contentElement.appendChild(document.createTextNode(plaintextData));

			protectedElement.appendChild(nonceElement);
			protectedElement.appendChild(contentElement);
			rootElement.appendChild(protectedElement);
			document.appendChild(rootElement);

			/*
			 * Get a key to be used for encrypting the element. Here we are
			 * generating an AES key.
			 */
			Key symmetricKey = loadDataEncryptionKey();

			String algorithmURI = XMLCipher.TRIPLEDES_KeyWrap;

			Key kek = loadKeyEncryptionKey();
			XMLCipher keyCipher = XMLCipher.getInstance(algorithmURI);
			keyCipher.init(XMLCipher.WRAP_MODE, kek);
			EncryptedKey encryptedKey = keyCipher.encryptKey(document, symmetricKey);

			/*
			 * Let us encrypt the contents of the document element.
			 */

			algorithmURI = XMLCipher.AES_192;

			XMLCipher xmlCipher = XMLCipher.getInstance(algorithmURI);
			xmlCipher.init(XMLCipher.ENCRYPT_MODE, symmetricKey);

			/*
			 * Uncomment these lines if a key should be embedded in the message
			 * (not neccessary for Core Hydra)
			 */
			EncryptedData encryptedData = xmlCipher.getEncryptedData();
			KeyInfo keyInfo = new KeyInfo(document);
			keyInfo.add(encryptedKey);
			encryptedData.setKeyInfo(keyInfo);
			/*
			 * doFinal - "true" below indicates that we want to encrypt
			 * element's content and not the element itself. Also, the doFinal
			 * method would modify the document by replacing the EncrypteData
			 * element for the data to be encrypted.
			 */
			xmlCipher.doFinal(document, rootElement, true);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			DOMSource source = new DOMSource(document);

			StreamResult sresult = new StreamResult();
			sresult.setOutputStream(new java.io.ByteArrayOutputStream());
			transformer.transform(source, sresult);
			result = (new StringBuffer()).append(sresult.getOutputStream()).toString();

			if (config == SecurityLibrary.CONF_ENC_SIG || config == SecurityLibrary.CONF_ENC_SIG_SPORADIC) {
				result = sign(result);
			}
			// logger.debug("Protected Core message: " + result);
		} catch (Exception e) {
			e.printStackTrace();
			CryptoException ce = new CryptoException(e.getMessage());
			ce.initCause(e);
			throw ce;
		}

		return result;
	}

	/**
	 * @see eu.linksmart.security.communication.core.SecurityLibrary#unprotectCoreMessage(java.lang.String)
	 */
	public String unprotectCoreMessage(String protectedData) throws VerificationFailureException, CryptoException {
		String result = "";
		try {

			if (!isValidCoreMessage(protectedData) && !isValidCoreSigMessage(protectedData)) {
				throw new VerificationFailureException("Not in valid Core Hydra format.");
			}
			if (config != CONF_ENC_SIG_SPORADIC && isValidCoreSigMessage(protectedData)) {
				protectedData = validate(protectedData);
			}

			/*
			 * Convert input string to XML document
			 */
			javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
			ByteArrayInputStream bis = new ByteArrayInputStream(protectedData.getBytes());
			Document document = db.parse(bis);

			/*
			 * Get the "EncryptedData" element from DOM tree.
			 */
			Element encryptedDataElement = (Element) document.getElementsByTagNameNS(EncryptionConstants.EncryptionSpecNS,
					EncryptionConstants._TAG_ENCRYPTEDDATA).item(0);

			/*
			 * Load the key to be used for decrypting the xml data encryption
			 * key.
			 */
			SecretKey kek = loadDataEncryptionKey();

			XMLCipher xmlCipher = XMLCipher.getInstance();
			/*
			 * The key to be used for decrypting xml data would be obtained from
			 * the keyinfo of the EncrypteData using the kek.
			 */
			xmlCipher.init(XMLCipher.DECRYPT_MODE, kek);
			xmlCipher.setKEK(kek);
			/*
			 * The following doFinal call replaces the encrypted data with
			 * decrypted contents in the document.
			 */
			xmlCipher.doFinal(document, encryptedDataElement);

			// Check the nonce
			String nonce = document.getElementsByTagName(CORE_NONCE_ELEMENT).item(0).getTextContent();
			if (this.lastNonce.equals(nonce)) {
				throw new VerificationFailureException("Message contained a nonce I've seen before. Maybe this was a replay attack?");
			}
			lastNonce = nonce;
			result = document.getElementsByTagName(CORE_CONTENT_ELEMENT).item(0).getTextContent();
		} catch (Exception e) {
			// Explicitly excepting the VerificationFailureException is a bit
			// strange here but it's the simplest way to do it. Too bad
			// XMLCipher throws a generic Exception :-(
			if (!(e instanceof VerificationFailureException)) {
				CryptoException ce = new CryptoException(e.getMessage());
				ce.initCause(e);
				throw ce;
			} else {
				throw (VerificationFailureException) e;
			}
		}
		return result;
	}

	// ----------------- other stuff ---------------------------------------

	/**
	 * Loads a secret data encryption key from the keystore.
	 * <p>
	 * The alias of the key is given by constant
	 * <code>PRIVATE_ENCRYPTION_KEY_ALIAS</code>.
	 * 
	 * @return a private key to be used for encryption.
	 * @throws KeyStoreException
	 * @throws IOException
	 *             If an error ocurred while reading from keystore file. Is file
	 *             <code>KEYSTORE_FILE</code> present, readable and in
	 *             <code>KEYSTORE_TYPE</code> format?
	 * @throws CertificateException
	 *             Problems with a certificate occured.
	 * @throws NoSuchAlgorithmException
	 *             If algorithm of the key is invalid. Check if bouncycastle
	 *             crypto provider is in place.
	 * @throws UnrecoverableKeyException
	 *             If the key was not found in keystore or could not be
	 *             recovered. Check if password
	 *             <code>PRIVATE_ENCRYPTION_KEY_PASS</code> is valid and the "<a
	 *             href="http://java.sun.com/javase/downloads/index.jsp">JCE
	 *             Unlimited Strength Jurisdiction Policy</a>" is in place.
	 */
	private SecretKey loadDataEncryptionKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		if (this.privateKey == null) {
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
			logger.debug("Loading " + this.getClass().getResource("/" + KEYSTORE_FILE));
			InputStream fis = this.getClass().getResourceAsStream("/" + KEYSTORE_FILE);

			// load the keystore
			ks.load(fis, KEYSTORE_PASS.toCharArray());
			if (!ks.containsAlias(CORE_KEY_ALIAS)) {
				logger.error("Keystore does not contain alias for Core Hydra: " + CORE_KEY_ALIAS);
			}

			logger.debug("Using password for Core key: " + (new String(CORE_KEY_PASS.toCharArray())));
			SecretKey privateKey = (SecretKey) ks.getKey(CORE_KEY_ALIAS, (CORE_KEY_PASS).toCharArray());

			this.privateKey = privateKey;
		}

		return this.privateKey;

	}

	private PrivateKey loadSigningKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		if (this.signingKey == null) {
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
			logger.debug("Loading " + this.getClass().getResource("/" + KEYSTORE_FILE));
			InputStream fis = this.getClass().getResourceAsStream("/" + KEYSTORE_FILE);

			// load the keystore
			ks.load(fis, KEYSTORE_PASS.toCharArray());
			if (!ks.containsAlias(CORE_KEY_ALIAS)) {
				logger.error("Keystore does not contain alias for Core Hydra: " + CORE_KEY_ALIAS);
			}

			PrivateKey signingKey = (PrivateKey) ks.getKey(PRIVATE_SIGNING_KEY_ALIAS, PRIVATE_SIGNING_KEY_PASS.toCharArray());

			this.signingKey = signingKey;
		}

		return this.signingKey;
	}

	private SecretKey loadKeyEncryptionKey() throws InvalidKeyException, FileNotFoundException, IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		if (this.secretKey == null) {
			String jceAlgorithmName = "DESede";
			System.out.println(this.getClass().getResource("/" + KEY_ENCRYPTION_KEY_FILE).toString());
			InputStream is = this.getClass().getResourceAsStream("/" + KEY_ENCRYPTION_KEY_FILE);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int b = 0;
			while (b != -1) {
				b = is.read();
				if (b != -1)
					bos.write(b);
			}

			DESedeKeySpec keySpec = new DESedeKeySpec(bos.toByteArray());
			SecretKeyFactory skf = SecretKeyFactory.getInstance(jceAlgorithmName);
			SecretKey key = skf.generateSecret(keySpec);
			this.secretKey = key;
		}
		return this.secretKey;
	}

	private X509Certificate loadSigningCertificate() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		if (this.signingCert == null) {
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
			logger.debug("Loading " + this.getClass().getResource("/" + KEYSTORE_FILE));
			InputStream fis = this.getClass().getResourceAsStream("/" + KEYSTORE_FILE);

			// load the keystore
			ks.load(fis, KEYSTORE_PASS.toCharArray());
			if (!ks.containsAlias(CORE_KEY_ALIAS)) {
				logger.error("Keystore does not contain alias for Core Hydra: " + CORE_KEY_ALIAS);
			}

			X509Certificate cert = (X509Certificate) ks.getCertificate(PRIVATE_SIGNING_KEY_ALIAS);
			this.signingCert = cert;
		}
		return this.signingCert;
	}

	/**
	 * 
	 * @see eu.linksmart.security.communication.core.SecurityLibrary#isValidCoreMessage(java.lang.String)
	 */
	public boolean isValidCoreMessage(String message) {
		boolean valid = true;
		try {
			/*
			 * Convert input string to XML document
			 */
			javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
			ByteArrayInputStream bis = new ByteArrayInputStream(message.getBytes());
			Document document = db.parse(bis);

			/*
			 * Check if it's a Hydra message
			 */

			if (document.getElementsByTagName(CORE_PROTECTED_MESSAGE_NAME).getLength() != 1) {
				valid = false;
				logger.info("Input does not contain " + CORE_PROTECTED_MESSAGE_NAME);
			}
		} catch (Exception e) {
			valid = false;
		}
		return valid;
	}

	public boolean isValidCoreSigMessage(String message) {
		boolean valid = true;
		try {
			/*
			 * Convert input string to XML document
			 */
			javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
			ByteArrayInputStream bis = new ByteArrayInputStream(message.getBytes());
			Document document = db.parse(bis);

			/*
			 * Check if it's a Hydra message
			 */

			if (document.getElementsByTagName(CORE_SIGNED_MESSAGE_NAME).getLength() != 1) {
				valid = false;
				logger.info("Input does not contain " + CORE_SIGNED_MESSAGE_NAME);
			}
		} catch (Exception e) {
			valid = false;
		}
		return valid;
	}

	public String sign(String data) {
		String result = "";
		try {
			Constants.setSignatureSpecNSprefix("");
			data = Base64.encode(data.getBytes());

			PrivateKey signingKey = loadSigningKey();

			// XML Signature needs to be namespace aware
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);

			DocumentBuilder db = dbf.newDocumentBuilder();
			org.w3c.dom.Document doc = db.newDocument();

			Element root = doc.createElementNS(CORE_SIGNED_MESSAGE_NAMESPACE, CORE_SIGNED_MESSAGE_NAME);
			root.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:linksmart", CORE_SIGNED_MESSAGE_NAMESPACE);

			doc.appendChild(root);
			root.appendChild(doc.createTextNode(data));

			// The BaseURI is the URI that's used to prepend to relative URIs
			String BaseURI = CORE_SIGNED_MESSAGE_NAMESPACE + "/";

			// Create an XML Signature object from the document, BaseURI and
			// signature algorithm (in this case DSA)
			XMLSignature sig = new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_DSA);

			// Append the signature element to the root element before signing
			// because this is going to be an enveloped signature.
			// This means the signature is going to be enveloped by the
			// document.
			// Two other possible forms are enveloping where the document is
			// inside the signature and detached where they are separate.
			// Note that they can be mixed in 1 signature with separate
			// references as shown below.
			root.appendChild(sig.getElement());

			{
				// create the transforms object for the Document/Reference
				Transforms transforms = new Transforms(doc);

				// First we have to strip away the signature element (it's not
				// part of the signature calculations). The enveloped transform
				// can be used for this.
				transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
				// Part of the signature element needs to be canonicalized. It
				// is a kind of normalizing algorithm for XML. For more
				// information please take a look at the W3C XML Digital
				// Signature webpage.
				transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_WITH_COMMENTS);
				// Add the above Document/Reference
				sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
			}

			{
				// Add in the KeyInfo for the certificate that we used the
				// private
				// key of
				X509Certificate cert = loadSigningCertificate();

				sig.addKeyInfo(cert);
				sig.addKeyInfo(cert.getPublicKey());
				sig.sign(signingKey);
			}
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
			e.printStackTrace();
			result = "";
		}
		return result;
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
			XMLSignature signature = new XMLSignature(sigElement, CORE_SIGNED_MESSAGE_NAMESPACE);

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
				Text textElement = (Text) XPathAPI.selectSingleNode(doc, "/hydra:CoreSignedProtectedMessage/text()");
				textResult = new String(Base64.decode(textElement.getTextContent()));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return textResult;
	}

	public static void main(String args[]) throws Exception {
		System.out.println("core");
		new SecurityLibraryImpl();
	}

	public short getConfig() {
		return this.config;
	}

}
