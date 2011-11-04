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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.xml.security.encryption.XMLCipher;
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
import eu.linksmart.security.communication.utils.CookieProvider;
import eu.linksmart.security.communication.utils.impl.CookieProviderImpl;
import eu.linksmart.security.communication.utils.impl.JarUtil;

/**
 * This class implements the <code>SecurityLibrary</code> interface.
 * 
 * @author Junaid Khan, Julian Schuette
 * 
 */
public class SecurityLibraryImpl implements SecurityLibrary {

	private static Logger logger = Logger.getLogger(SecurityLibraryImpl.class.getName());
	private static SecurityLibrary instance = null;

	private String DATA_FOLDER;
	private String KEYSTORE_PASS;
	private String KEYSTORE_FILE;
	private String KEYSTORE_TYPE;
	private String CORE_ENC_KEY_ALIAS;
	private String CORE_ENC_KEY_PASS;
	private String MAC_KEY_ALIAS;
	private String MAC_KEY_PASS;

	private short config = -1;
	private SecretKey macKey; // For caching my mac key
	private SecretKey encKey; // For caching my enc key
	private Properties settings;
	private Random rand = null;
	private CookieProvider cookieProv = null;

	static {
		/*
		 * We are using the bouncycastle crypto provider (Sun's provider might
		 * not be available on PS3).
		 */
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
	}

	public static synchronized SecurityLibrary getInstance(){
		if(instance == null){
			try{
				instance = new SecurityLibraryImpl();
			}catch(IOException e){
				logger.error("Cannot initialize core security!", e);
				RuntimeException re = new RuntimeException();
				re.initCause(e);
				throw re;
			}
		}
		return instance;
	}

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
	 * @throws IOException
	 */
	private void init() throws IOException{
		logger.debug("Loading settings for SecurityLibrary");
		settings = new Properties();
		logger.debug("Loading file " + "settings.properties");
		try {
			settings.load(this.getClass().getResourceAsStream("/resources/settings.properties"));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		DATA_FOLDER = settings.getProperty("keystore.coresecurity.deploydir", "linksmart/eu.linksmart.wsprovider") + "/data";
		KEYSTORE_FILE = settings.getProperty("keystore.file", "linksmart.core.keystore.ks");
		KEYSTORE_PASS = settings.getProperty("keystore.pass", "coresecurity");
		KEYSTORE_TYPE = settings.getProperty("keystore.type", "BKS");
		CORE_ENC_KEY_ALIAS = settings.getProperty("core.key.alias", "coreenckey");
		CORE_ENC_KEY_PASS = settings.getProperty("core.key.pass", "coresecurity");
		MAC_KEY_ALIAS = settings.getProperty("core_mac.key.alias", "coremackey");
		MAC_KEY_PASS = settings.getProperty("core_mac.key.pass", "coresecurity");

		Hashtable<String, String> HashFilesExtract =
			new Hashtable<String, String>();
		logger.debug("Deploying core security files");
		HashFilesExtract.put(DATA_FOLDER + "/" + KEYSTORE_FILE,
		"data/linksmart.core.keystore.ks");
		JarUtil.createDirectory(DATA_FOLDER);
		JarUtil.extractFilesJar(HashFilesExtract);

		rand = new Random();
		cookieProv = new CookieProviderImpl();
	}

	/**
	 * Creates a new instance of the SecurityLibrary that will use the default
	 * key alias for Core Hydra communication. If the keystore file could not be
	 * opened or the default Core Hydra key alias is not present, an exception
	 * will be thrown.
	 * 
	 * @throws IOException
	 */
	private SecurityLibraryImpl() throws IOException{
		init();
	}

	/**
	 * @see eu.linksmart.security.communication.core.SecurityLibrary#protectCoreMessage(java.lang.String)
	 */
	public String protectCoreMessage(String plaintextData) throws CryptoException {
		if (config == SecurityLibrary.CONF_NULL) {
			logger.debug("Skipping protection. NULL config is set");
			return plaintextData;
		}
		if(config == -1){
			throw new CryptoException("Core security not yet initialized with configuration.");
		}

		String result = "";

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

			Element contentElement = document.createElementNS(CORE_SECURITY_NAMESPACE, CORE_CONTENT_ELEMENT);
			contentElement.appendChild(document.createTextNode(plaintextData));

			protectedElement.appendChild(contentElement);
			rootElement.appendChild(protectedElement);
			document.appendChild(rootElement);

			/*
			 * Get a key to be used for encrypting the element. Here we are
			 * generating an AES key.
			 */
			Key symmetricKey = getDataEncryptionKey();

			/*
			 * Let us encrypt the contents of the document element.
			 */
			String algorithmURI = XMLCipher.AES_192;
			XMLCipher xmlCipher = XMLCipher.getInstance(algorithmURI);
			xmlCipher.init(XMLCipher.ENCRYPT_MODE, symmetricKey);

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
		} catch (Exception e) {
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
		if(config == -1){
			throw new CryptoException("Core security not yet initialized with configuration.");
		}

		String result = "";
		try {

			if (!isValidCoreMessage(protectedData) && !isValidCoreMacMessage(protectedData)) {
				throw new VerificationFailureException("Not in valid Core LinkSmart format.");
			}
			if (isValidCoreMacMessage(protectedData) 
					&& ((config == CONF_ENC_SIG_SPORADIC && rand.nextInt(100) <= 25) || config == CONF_ENC_SIG)) {
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
			SecretKey kek = getDataEncryptionKey();

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
	 * <code>CORE_ENC_KEY_ALIAS</code>.
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
	 * @throws CryptoException 
	 *			   If keystore does not contain alias for core encryption
	 */
	private SecretKey getDataEncryptionKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, Exception {
		if (this.encKey == null) {
			InputStream fis = null;
			try{
				KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
				logger.debug("Loading " + DATA_FOLDER + "/" + KEYSTORE_FILE);
				File f = new File(DATA_FOLDER + "/" + KEYSTORE_FILE);
				fis = new FileInputStream(f);

				// load the keystore
				ks.load(fis, KEYSTORE_PASS.toCharArray());
				if (!ks.containsAlias(CORE_ENC_KEY_ALIAS)) {
					logger.error("Keystore does not contain alias for Core Hydra: " + CORE_ENC_KEY_ALIAS);
					throw new Exception("Keystore does not contain alias for Core LinkSmart: " + CORE_ENC_KEY_ALIAS);
				}

				logger.debug("Using password for Core key: " + (new String(CORE_ENC_KEY_PASS.toCharArray())));
				this.encKey = (SecretKey) ks.getKey(CORE_ENC_KEY_ALIAS, (CORE_ENC_KEY_PASS).toCharArray());

			}finally{
				if(fis != null){
					fis.close();
				}
			}
		}
		return this.encKey;
	}

	/**
	 * Loads a secret MAC key from the keystore.
	 * <p>
	 * The alias of the key is given by constant
	 * <code>MAC_KEY_ALIAS</code>.
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
	 * @throws CryptoException 
	 *			   If keystore does not contain alias for core encryption
	 */
	private SecretKey getMacKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, Exception {
		if (this.macKey == null) {
			InputStream fis = null;
			try{
				KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
				logger.debug("Loading " + DATA_FOLDER + "/" + KEYSTORE_FILE);
				File f = new File(DATA_FOLDER + "/" + KEYSTORE_FILE);
				fis = new FileInputStream(f);

				// load the keystore
				ks.load(fis, KEYSTORE_PASS.toCharArray());
				if (!ks.containsAlias(MAC_KEY_ALIAS)) {
					logger.error("Keystore does not contain alias for Core LinkSmart: " + MAC_KEY_ALIAS);
					throw new Exception("Keystore does not contain alias for Core LinkSmart: " + MAC_KEY_ALIAS);
				}
				this.macKey = (SecretKey) ks.getKey(MAC_KEY_ALIAS, MAC_KEY_PASS.toCharArray());		

			}finally{
				if(fis != null){
					fis.close();
				}
			}
		}

		return macKey;
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
				logger.debug("Input does not contain " + CORE_PROTECTED_MESSAGE_NAME);
			}
		} catch (Exception e) {
			valid = false;
		}
		return valid;
	}

	public boolean isValidCoreMacMessage(String message) {
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
			 * Check if it's a LinkSmart message
			 */

			if (document.getElementsByTagName(CORE_MAC_MESSAGE_NAME).getLength() != 1) {
				valid = false;
				logger.debug("Input does not contain " + CORE_MAC_MESSAGE_NAME);
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

			SecretKey macingKey = getMacKey();

			// XML Signature needs to be namespace aware
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);

			DocumentBuilder db = dbf.newDocumentBuilder();
			org.w3c.dom.Document doc = db.newDocument();

			Element root = doc.createElementNS(CORE_MAC_MESSAGE_NAMESPACE, CORE_MAC_MESSAGE_NAME);
			root.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:linksmart", CORE_MAC_MESSAGE_NAMESPACE);

			doc.appendChild(root);
			root.appendChild(doc.createTextNode(data));

			// The BaseURI is the URI that's used to prepend to relative URIs
			String BaseURI = CORE_MAC_MESSAGE_NAMESPACE + "/";

			// Create an XML Signature object from the document, BaseURI and
			// signature algorithm (in this case DSA)
			XMLSignature sig = new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_MAC_HMAC_SHA256);

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
				sig.sign(macingKey);
			}

			// Transform DOM tree to XML string.
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult sresult = new StreamResult();
			sresult.setOutputStream(new java.io.ByteArrayOutputStream());
			transformer.transform(source, sresult);
			result = new StringBuffer().append(sresult.getOutputStream()).toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "";
		}
		return result;
	}

	public String validate(String data) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, Exception {
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
		XMLSignature signature = new XMLSignature(sigElement, CORE_MAC_MESSAGE_NAMESPACE);

		SecretKey macingKey = getMacKey();
		result = signature.checkSignatureValue(macingKey);
		logger.debug("XML signature is " + ((result == true) ? "valid " : "invalid !!!!!"));

		/*
		 * Decide whether Signature was valid or not
		 */
		if (result == true) {
			Text textElement = (Text) XPathAPI.selectSingleNode(doc, "/" + CORE_MAC_MESSAGE_NAME + "/text()");
			textResult = new String(Base64.decode(textElement.getTextContent()));
		}
		else {
			throw new VerificationFailureException("Not valid message.");
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

	public boolean checkCookie(String cookie) {
		return cookieProv.checkCookie(cookie);
	}

	@Override
	public String getCookie() {
		return cookieProv.getCookie();
	}


}
