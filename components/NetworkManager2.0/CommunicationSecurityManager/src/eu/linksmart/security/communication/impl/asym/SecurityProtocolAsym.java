package eu.linksmart.security.communication.impl.asym;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
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
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.CryptoException;
import eu.linksmart.security.communication.SecurityProtocol;
import eu.linksmart.security.communication.VerificationFailureException;
import eu.linksmart.security.communication.impl.asym.util.XMLMessageUtil;
import eu.linksmart.security.communication.util.NonceGenerator;
import eu.linksmart.security.communication.util.impl.NonceGeneratorFactory;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.trustmanager.TrustManager;
import eu.linksmart.utils.Base64;

public class SecurityProtocolAsym implements SecurityProtocol {

	private static Logger logger = Logger.getLogger(SecurityProtocolAsym.class);

	public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	private String KEYSTORE_PASS;
	private String KEYSTORE_FILE;
	private String KEYSTORE_TYPE;
	private String INSIDE_KEY_ALIAS;
	private String KEYSTORE_DEPLOY_DIR;
	
	private CryptoManager cryptoMgr = null;
	private TrustManager trustMgr = null;
	private HID clientHID = null;
	private HID serverHID = null;
	private double trustThreshold;
	
	private NonceGenerator nonceGenerator = null;
	private KeyStore keyStore = null;
	private boolean isInitialized = false;
	private boolean isClient = false;
	private boolean sentKeyToClient = false;

	public SecurityProtocolAsym(HID clientHID, HID serverHID, CryptoManager cryptoMgr, TrustManager trustMgr, double trustThreshold){
		this.clientHID = clientHID;
		this.serverHID = serverHID;
		this.cryptoMgr = cryptoMgr;
		this.trustMgr = trustMgr;
		this.trustThreshold = trustThreshold;
		nonceGenerator = NonceGeneratorFactory.getInstance();
		//TODO get keystore and preferences to it
		//not used anyway so not implemented so far
	}

	@Override
	public Message startProtocol() throws CryptoException {
		isClient = true;

		Command cmd = new Command(Command.CLIENT_REQUESTS_KEY);	
		cmd.setProperty(Command.CLIENT, clientHID.toString());
		cmd.setProperty(Command.SERVER, serverHID.toString());

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			cmd.storeToXML(bos, null);

			String signedCommand = cryptoMgr.sign(bos.toString(),null, clientHID.toString());
			cmd.setProperty(Command.SIGNED_PAYLOAD, signedCommand);

			return createMessage(CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC, clientHID, serverHID, cmd);
		} catch (Exception e) {
			CryptoException ce = new CryptoException("Error creating initial message");
			ce.initCause(e);
			throw ce;
		}
	}

	@Override
	public boolean isInitialized() {
		return isInitialized;
	}

	@Override
	public Message processMessage(Message msg) throws CryptoException, VerificationFailureException, IOException{
		if(!isInitialized){
			if(isClient){
				//response from the server which should contain a certificate
				Command command = getCommand(msg);
				if(Integer.parseInt(command.getProperty("command")) == Command.SERVER_SEND_KEY){
					String signedAndEncryptedPayload = command.getProperty(Command.SIGNED_AND_ENCRYPTED_PAYLOAD);

					//verify signature on message
					logger.debug("Verifying message " + signedAndEncryptedPayload);
					String verifiedMessage = cryptoMgr.verify(signedAndEncryptedPayload);
					logger.debug("Signature of Message from Server verified: " + (verifiedMessage!=null));

					//if signature is valid extract symmetric key
					if (verifiedMessage!=null && verifiedMessage!="") {
						// Convert input String to XML document
						logger.debug("Encrypted message is: " + verifiedMessage);
						//save received certificate
						Certificate cert = XMLMessageUtil.getCertificate(signedAndEncryptedPayload);
						String serverKeyidentifier;
						try {
							serverKeyidentifier = cryptoMgr.storePublicKey(Base64.encodeBytes(cert.getEncoded()), "RSA");
						} catch (CertificateEncodingException e) {
							IOException ioe = new IOException("Cannot parse received message!");
							ioe.initCause(e);
							throw ioe;
						}
						saveServerPk(serverKeyidentifier);			

						//open message
						String originalMessage = cryptoMgr.decrypt(verifiedMessage);

						logger.debug("Verified: " + verifiedMessage);
						logger.debug("Original: " + originalMessage);

						//check if encrypted message and signed message are the same
						Properties props = new Properties();
						props.loadFromXML(new ByteArrayInputStream(originalMessage.getBytes()));
						String client = props.getProperty(Command.CLIENT);
						String server = props.getProperty(Command.SERVER);
						String cmd = props.getProperty(Command.COMMAND);
						if ((client.equals(command.getProperty(Command.CLIENT))) && (server.equals(command.getProperty(Command.SERVER))) && (cmd.equals(command.getProperty(Command.COMMAND)))) {
							logger.debug("Signed Headers are the same as outer Headers");
							//store received symmetric key
							storeSymKey(props.getProperty(Command.SYMMETRIC_KEY));
						} else {
							logger.debug("Signed Headers are not the same as outer Headers, aborting!");
							throw new VerificationFailureException("Signature from HID: " + serverHID + " not matching to required fields!");
						}
					} else {
						logger.debug("Signature not valid, aborting");
						throw new VerificationFailureException("Signature from HID: " + serverHID + " not valid!");
					} 
					//send acknowledgment to server of receiving key
					Command cmd = new Command(Command.CLIENT_ACK);
					cmd.setProperty(Command.CLIENT, clientHID.toString());
					cmd.setProperty(Command.SERVER, serverHID.toString());
					try {
						isInitialized = true;
						return createMessage(CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC, clientHID, serverHID, cmd);
					} catch (IOException e) {
						//this exception cannot happen
						logger.error("Error creating acknowledgment for server",e);
						return null;
					}
				}
			}else{
				//this is server and client sent request or acknowledgment
				Command command = getCommand(msg);
				if(Integer.parseInt(command.getProperty("command")) == Command.CLIENT_REQUESTS_KEY){
					//if signature on message and certificate are valid go to next state	
					if(verifySignature(command) && verifyCertificate(command)){
						//create symmetric key and create command
						String key = createKey();
						Command cmd = new Command(Command.SERVER_SEND_KEY);
						cmd.setProperty(Command.SYMMETRIC_KEY, key);
						cmd.setProperty(Command.CLIENT, clientHID.toString());
						cmd.setProperty(Command.SERVER, serverHID.toString());

						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						cmd.storeToXML(bos, null);

						//encrypting and signing message
						logger.debug("Encrypting " + bos.toString());
						logger.debug("Encrypting for " + cmd.getProperty(Command.CLIENT));
						String encryptedCommand;
						try {
							encryptedCommand = cryptoMgr.encryptAsymmetric(bos.toString(), cmd.getProperty(Command.CLIENT), "");
						} catch (Exception e) {
							CryptoException ce = new CryptoException("Error encrypting message!");
							ce.initCause(e);
							throw ce;
						}
						String signedCommand = cryptoMgr.sign(encryptedCommand, null,cmd.getProperty(Command.SERVER));

						cmd.setProperty(Command.SIGNED_AND_ENCRYPTED_PAYLOAD, signedCommand);
						cmd.remove(Command.SYMMETRIC_KEY);

						sentKeyToClient = true;
						return createMessage(CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC, serverHID, clientHID, cmd);
					}
				}else if(sentKeyToClient && Integer.parseInt(command.getProperty("command")) == Command.CLIENT_ACK){
					isInitialized = true;
					//indicate that this message has been processed
					return null;
				}
			}
		}
		return msg;
	}

	@Override
	public Message protectMessage(Message msg) throws Exception {
		String encryptedMessage = asymEncrypt(String.valueOf(msg.getData()), clientHID.toString());
		// String signedMessage = asymSign(encryptedMessage, senderHID);
		msg.setData(encryptedMessage.getBytes());
		return msg;
	}

	@Override
	public Message unprotectMessage(Message msg) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	/*
	 * Methods needed for the secure session handshake protocol
	 */
	
	private Message createMessage(String topic, HID senderHID, HID receiverHID, Command command) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] serializedCommand = null;
		try {
			command.storeToXML(bos, null);
			serializedCommand = bos.toByteArray();
		} finally{
			bos.close();
		}

		return new Message(CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC, clientHID, serverHID, serializedCommand);
	}

	private Command getCommand(Message msg) throws IOException{
		Command command = new Command();
		ByteArrayInputStream bis = new ByteArrayInputStream(msg.getData());
		try {
			command.loadFromXML(bis);
		}finally{
			bis.close();
		}

		return command;
	}

	/**
	 * Associates servers's HID with certificate in {@link CryptoManager}
	 * @param identifier
	 */
	private void saveServerPk(String identifier){
		cryptoMgr.addCertificateForHID(serverHID.toString(), identifier);
		logger.debug("Server KEY IDENTIFIER FROM CRYPTOMANAGER: " + identifier + " has been bound to HID: " + serverHID.toString());
	}

	/**
	 * Associates client's HID with certificate in {@link CryptoManager}
	 * @param identifier
	 */
	public void saveClientPk(String identifier){
		cryptoMgr.addCertificateForHID(clientHID.toString(), identifier);
		logger.debug("CLIENT KEY IDENTIFIER FROM CRYPTOMANAGER: " + identifier + " has been bound to HID: " + clientHID.toString());
	}

	/**
	 * Stores the received symmetric key into {@link CryptoManager}
	 * @param key Base64 encoded key
	 */
	private void storeSymKey(String key) {
		//TODO save symmetric key in keystore
		//			CryptoManager c = SecureSessionControllerImpl.getInstance().getCryptoManager();
		//			try{
		//			String identifier = c.storeSymmetricKey("AES", key);
		//			}catch(Exception e){
		//				e.printStackTrace();
		//			}
	}

	/**
	 * Checks using the CryptoManager whether the signature is valid.
	 * @param command Command received from other party
	 * @return true if valid - false if not
	 * @throws IOException 
	 */
	private boolean verifySignature(Command command) throws IOException{
		String signedPayload = command.getProperty(Command.SIGNED_PAYLOAD);

		String verifiedMessage = cryptoMgr.verify(signedPayload);
		logger.debug("Verification of signature. Result: " + (verifiedMessage!=null));
		//if signature is not valid return false
		if (verifiedMessage==null || verifiedMessage.equals("")) {
			return false;
		}

		//Verification of Server/Client/Command fields
		Properties props = new Properties();
		if (verifiedMessage!=null) {
			try {
				props.loadFromXML(new ByteArrayInputStream(verifiedMessage.trim().getBytes()));
			} catch (InvalidPropertiesFormatException e) {
				throw new IOException ("Cannot parse received message!");
			}
		}

		if (!(props.getProperty(Command.SERVER).equals(command.getProperty(Command.SERVER)) 
				&& (props.getProperty(Command.CLIENT).equals(command.getProperty(Command.CLIENT))) 
				&& (props.getProperty(Command.COMMAND).equals(command.getProperty(Command.COMMAND))))) {

			logger.debug("Signed Payload Header Fields differ from unsigned Headers! Aborting!");
			return false;
		}		
		return true;
	}

	/**
	 * Checks using the TrustManager whether the certificate is trusted.
	 * @param command
	 * @param context
	 * @return true if accepted - false if not
	 * @throws IOException 
	 */
	private boolean verifyCertificate(Command command) throws IOException{
		String signedPayload = command.getProperty(Command.SIGNED_PAYLOAD); 		
		Certificate cert = XMLMessageUtil.getCertificate(signedPayload);      		
		Double trust = new Double(0.0);

		if (trustMgr!=null) {
			try{
				String encodedcert = Base64.encodeBytes(cert.getEncoded());
				trust = trustMgr.getTrustValue(encodedcert);
			}catch(RemoteException e){
				IOException ioe = new IOException("Cannot communicate with Trust Manager");
				ioe.initCause(e);
				throw ioe;
			} catch (CertificateEncodingException e) {
				IOException ioe = new IOException("Cannot parse received message!");
				ioe.initCause(e);
				throw ioe;
			}
		}

		logger.debug("Trust in Certificate is: " + trust);

		if (trust < trustThreshold ) {
			return false;
		} else {		
			//as certificate is valid store it in keystore
			try{
				String clientKeyIdentifier = cryptoMgr.storePublicKey(Base64.encodeBytes(cert.getEncoded()), "");
				saveClientPk(clientKeyIdentifier);
			} catch (CertificateEncodingException e) {
				//this exception should not happen as the same method has already invoked once
				IOException ioe = new IOException("Cannot parse received message!");
				ioe.initCause(e);
				throw ioe;
			}
		}
		return true;
	}

	private String createKey() throws CryptoException{
		String key = null;
		try {
			//uses default algorithm of CryptoManager
			key = cryptoMgr.generateSymmetricKey();
		} catch (Exception e) {
			CryptoException ce = new CryptoException("Error generating symmetric key!");
			ce.initCause(e);
			throw ce;
		}
		return key;
	}
	
	/*
	 * Methods needed for the protection and unprotection of messages
	 */
	
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
		return result;
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
		String identifier = cryptoMgr.getPrivateKeyReference(hid);
		return cryptoMgr.getPrivateKeyByIdentifier(identifier);
	}

	// sign using private key
	private PrivateKey loadSigningKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		PrivateKey signingKey = cryptoMgr.getPrivateKeyByIdentifier("hydrademo-rsa");
		return signingKey;
	}
	
	private PublicKey loadKeyEncryptionKey(String receiverHID) throws InvalidKeySpecException, KeyStoreException, NoSuchAlgorithmException, FileNotFoundException, CertificateException{
		loadKeyStore();
		String identifier = cryptoMgr.getCertificateReference(receiverHID);
		return cryptoMgr.getCertificateByIdentifier(identifier).getPublicKey();
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
}