package eu.linksmart.security.communication.impl.sym;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;

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
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.EncryptionConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.CryptoException;
import eu.linksmart.security.communication.SecurityProtocol;
import eu.linksmart.security.communication.VerificationFailureException;
import eu.linksmart.security.communication.util.NonceGenerator;
import eu.linksmart.security.communication.util.impl.Command;
import eu.linksmart.security.communication.util.impl.NonceGeneratorFactory;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.trustmanager.TrustManager;
import eu.linksmart.utils.Base64;

/**
 * Implementation of {@link SecurityProtocol} interface which
 * uses hybrid encryption based on certificates associated
 * with {@link HID}
 * @author Vinkovits
 *
 */
public class SecurityProtocolImpl implements SecurityProtocol {

	/**
	 * JCE Encryption algorithm used
	 */
	public static final String SYMMETRIC_ENCRYPTION_ALGORITHM = "AES";
	/**
	 * JCE MAC algorithm used
	 */
	public static final String MAC_ALGORITHM = "HmacSHA256";
	/**
	 * XMLCipher name of encryption algorithm
	 */
	public static final String XMLCIPHER_ENCRYPTION_ALGORITHM = XMLCipher.AES_128;
	/**
	 * XMLSignature name of signature algorithm
	 */
	public static final String XMLSIGNATURE_MAC_ALGORITHM = XMLSignature.ALGO_ID_MAC_HMAC_SHA256;
	/**
	 * The Log4j logger of this class
	 */
	private static Logger logger = Logger.getLogger(SecurityProtocolImpl.class);

	/**
	 * {@CryptoManager} used to store certificates and keys
	 */
	private CryptoManager cryptoMgr = null;
	/**
	 * {@TrustManager} used to check validity of certificates
	 */
	private TrustManager trustMgr = null;
	/**
	 * The client of this protocol run meaning who started the communication
	 */
	private HID clientHID = null;
	/**
	 * The server of this protocol run meaning who received the request
	 */
	private HID serverHID = null;
	/**
	 * The threshold between 0 and 1 needed for a certificate to be accepted
	 */
	private double trustThreshold;

	/**
	 * NonceGenerator used for creating nonces in this protocol
	 */
	private NonceGenerator nonceGenerator = null;
	/**
	 * Indicates whether the handshake has already been completed successfully
	 */
	private boolean isInitialized = false;
	/**
	 * Implements the handshake for the asymmetric part of the protocol
	 */
	private AsymHandshake asymHandshake = null;
	/**
	 * Implements the handshake for the symmetric part of the protocol 
	 */
	private SymHandshake symHandshake = null;
	/**
	 * Indicates whether asymmetric handshake or symmetric handshake is running
	 */
	private boolean isAsymRunning = false;
	/**
	 * The MAC sym key used by the client
	 */
	private Key localMacKey = null;
	/**
	 * The MAC sym key used by the server
	 */
	private Key remoteMacKey = null;
	/**
	 * The sym encryption key used by the client
	 */
	private Key localEncKey = null;
	/**
	 * The sym encryption key used by the server
	 */
	private Key remoteEncKey = null;
	/**
	 * The IV for the next client message
	 */
	private byte[] localIV = null;
	/**
	 * The IV for the next server message
	 */
	private byte[] remoteIV = null;
	/**
	 * Tells whether handshake protocol is started
	 */
	private boolean isStarted;
	/**
	 * Tells whether this is the client in the handshake
	 */
	private boolean isClient;
	/**
	 * Stores the message with wich the handshake has been started to send it later
	 */
	private Message storedMessage;
	/**
	 * The master key id for this two entities
	 */
	private String masterKeyId;

	public SecurityProtocolImpl(HID clientHID,
			HID serverHID,
			CryptoManager cryptoMgr,
			TrustManager trustMgr,
			double trustThreshold){
		this.clientHID = clientHID;
		this.serverHID = serverHID;
		this.cryptoMgr = cryptoMgr;
		this.trustMgr = trustMgr;
		this.trustThreshold = trustThreshold;
		nonceGenerator = NonceGeneratorFactory.getInstance();
		this.symHandshake = new SymHandshake(this);
		this.asymHandshake = new AsymHandshake(this);
	}

	public Message startProtocol() throws CryptoException {
		//check if certificates are available
		String serverIdentifier = this.cryptoMgr.getCertificateReference(serverHID.toString());
		String clientIdentifier = this.cryptoMgr.getCertificateReference(clientHID.toString());
		boolean mkExists = false;
		String master_identifier = null;
		//if yes check if there is a master symmetric key available
		if(serverIdentifier != null && !serverIdentifier.isEmpty()){
			//generate master key identifier which is the certificate identifiers XORed
			byte[] serverId = serverIdentifier.getBytes();
			byte[] clientId = clientIdentifier.getBytes();
			byte[] mkIdentifier = new byte[(clientId.length >= serverId.length)? clientId.length : serverId.length];
			for(int i=0; i < clientId.length && i < serverId.length;i++){
				mkIdentifier[i] = (byte) (clientId[i] ^ serverId[i]);
			}
			master_identifier = Base64.encodeBytes(mkIdentifier);
			mkExists = cryptoMgr.identifierExists(master_identifier);
		}

		try{	
			//if master key exists use session key handshake else ask for key from server
			if(serverIdentifier != null && !serverIdentifier.isEmpty() && mkExists){
				isAsymRunning = false;
				symHandshake.setMasterKeyIdentifier(master_identifier);
				return this.symHandshake.startProtocol();
			} else {
				isAsymRunning = true;
				return this.asymHandshake.startProtocol();
			}
		}catch(Exception e){
			CryptoException ce = new CryptoException("Error during agreement of session keys");
			ce.initCause(e);
			logger.error("Error during exchange of certificates with HID: " + serverHID.toString());
			throw ce;
		}
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public Message processMessage(Message msg) throws CryptoException, VerificationFailureException, IOException{
		if(!isInitialized){
			if(!isStarted()){
				//check if protocol is started or this message has to be stored
				if(!msg.getTopic().contentEquals(CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC)){
					//this is the client side of a handshake
					isClient = true;
					isStarted = true;
					storedMessage = msg;
					return startProtocol();
				} else if(msg.getTopic().contentEquals(CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC)){
					//this is the server side of a handshake but decide which handshake the message belongs to
					Command cmd = getCommand(msg);
					if(Integer.parseInt(cmd.getProperty(Command.COMMAND)) == Command.CLIENT_HELLO){
						isAsymRunning = false;
						//try to set master key id else drop exception
						getMasterKeyId();
						if(cryptoMgr.identifierExists(this.masterKeyId)){
							symHandshake.setMasterKeyIdentifier(this.masterKeyId);
						}else{
							throw new CryptoException(
									"Entity is trying to agree on session keys but there is no master key for HIDs: "
									+ clientHID.toString() + " " + serverHID.toString());
						}
					}else if(Integer.parseInt(cmd.getProperty(Command.COMMAND)) == Command.CLIENT_REQUESTS_KEY){
						isAsymRunning = true;
					}
					isClient = false;
					isStarted = true;
				}
			}

			//check whether asymmetric or symmetric handshake is running based on message type and certificates
			if(this.isAsymRunning){
				try {
					return asymHandshake.processMessage(msg);
				} catch (CryptoException ce) {
					throw ce;
				} catch (VerificationFailureException ve){
					throw ve;
				} catch (IOException ioe){
					throw ioe;
				} catch (Exception e){
					//make cryptoexception from exception 
					CryptoException ce = new CryptoException("Error processing protocol message");
					ce.initCause(e);
					throw ce;
				}
			}else{
				try {
					return symHandshake.processMessage(msg);
				} catch (Exception e){
					//make cryptoexception from exception 
					CryptoException ce = new CryptoException("Error processing protocol message");
					ce.initCause(e);
					throw ce;
				}
			}
		}
		//cannot do anything with the message so return it
		return msg;
	}

	public Message protectMessage(Message msg) throws Exception {
		if(!isInitialized){
			throw new Exception("Cannot protect message as not initialized");
		}

		// Transform input string to an linksmart:InsideProtected-XML document
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.newDocument();
		//create root element
		Element rootElement = document.createElementNS(INSIDE_SIGNED_MESSAGE_NAMESPACE, INSIDE_SIGNED_MESSAGE_NAME);
		rootElement.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:linksmart", INSIDE_SIGNED_MESSAGE_NAMESPACE);
		//create content element
		Element protectedElement = document.createElementNS(INSIDE_SECURITY_NAMESPACE, INSIDE_PROTECTED_ELEMENT);

		//Do the encryption
		//element to later encrypt
		Element contentElement = document.createElementNS(INSIDE_SECURITY_NAMESPACE, INSIDE_CONTENT_ELEMENT);
		contentElement.appendChild(document.createTextNode(new String(msg.getData())));
		//append elements to root
		protectedElement.appendChild(contentElement);
		rootElement.appendChild(protectedElement);
		document.appendChild(rootElement);

		//get data encryption key
		Key dek = getLocalEncKey();

		// Do the encryption of the content using specified algorithm.
		String algorithmURI = XMLCIPHER_ENCRYPTION_ALGORITHM;
		XMLCipher xmlCipher = XMLCipher.getInstance(algorithmURI);
		xmlCipher.init(XMLCipher.ENCRYPT_MODE, dek);

		// Add the "KeyInfo" element to the message so the receiver gets all
		// necessary information
		EncryptedData encryptedData = xmlCipher.getEncryptedData();
		KeyInfo keyInfo = new KeyInfo(document);
		keyInfo.addText(XMLCIPHER_ENCRYPTION_ALGORITHM);
		encryptedData.setKeyInfo(keyInfo);
		xmlCipher.doFinal(document, rootElement, true);

		//Do signature
		Constants.setSignatureSpecNSprefix("");
		Key signingKey = getLocalMacKey();
		String BaseURI = INSIDE_SIGNED_MESSAGE_NAMESPACE + "/";

		// Create an XML Signature object from the document, BaseURI and given
		// signature algorithm
		XMLSignature sig = new XMLSignature(document, BaseURI, XMLSIGNATURE_MAC_ALGORITHM);

		// Append the signature element to the root element before signing
		// to envelope the signature by the doc.
		rootElement.appendChild(sig.getElement());

		// create the transforms object for the Document/Reference
		Transforms transforms = new Transforms(document);
		transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
		transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_WITH_COMMENTS);
		// Add the above Document/Reference
		sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);			
		sig.sign(signingKey);	
		// Write XML to string
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		DOMSource source = new DOMSource(document);

		StreamResult sresult = new StreamResult();
		sresult.setOutputStream(new java.io.ByteArrayOutputStream());
		transformer.transform(source, sresult);
		String result = (new StringBuffer()).append(sresult.getOutputStream()).toString();

		msg.setData(result.getBytes());
		return msg;
	}

	public Message unprotectMessage(Message msg) throws Exception {
		// string to doc conversion
		javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		ByteArrayInputStream bis = new ByteArrayInputStream(msg.getData());
		Document document = db.parse(bis);

		//get signature element
		NodeList nl = document.getElementsByTagName("Signature");
		if (nl.getLength() == 0) {
			throw new Exception("Cannot find Signature element");
		}
		DOMValidateContext valContext = new DOMValidateContext(getRemoteMacKey(), nl.item(0));

		XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
		javax.xml.crypto.dsig.XMLSignature signature2 = factory.unmarshalXMLSignature(valContext);

		boolean sv = signature2.getSignatureValue().validate(valContext);
		logger.debug("Signature validation status from HID: " + msg.getSenderHID() + "is " + sv);
		if(!sv){
			throw new VerificationFailureException("Signature not valid");
		}

		/*
		 * find the location of the signature element obtain the session key
		 * using the information in the KeyInfo element and hand it back to
		 * be used as the validation key. unmarshal and validate the
		 * signature.
		 */
		if (document.getElementsByTagName(INSIDE_SIGNED_MESSAGE_NAME).getLength() != 1) {
			throw new Exception("Input does not contain " + INSIDE_SIGNED_MESSAGE_NAME);
		}

		// Get the "EncryptedData" element from DOM tree.
		XMLCipher xmlCipher = XMLCipher.getInstance();
		xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
		Element encryptedDataElement = (Element) document.getElementsByTagNameNS(EncryptionConstants.EncryptionSpecNS,
				EncryptionConstants._TAG_ENCRYPTEDDATA).item(0);

		xmlCipher.init(XMLCipher.DECRYPT_MODE, getRemoteEncKey());
		// replace the encrypted data with decrypted contents in the
		// document.
		xmlCipher.doFinal(document, encryptedDataElement);

		String result = document.getElementsByTagName(INSIDE_CONTENT_ELEMENT).item(0).getTextContent();
		msg.setData(result.getBytes());
		return msg;
	}


	public boolean canBroadcast() {
		return false;
	}

	public Message protectBroadcastMessage(Message msg) throws Exception {
		throw new Exception("Broadcasting not supported by security protocol!");
	}

	public Message unprotectBroadcastMessage(Message msg) throws Exception {
		throw new Exception("Broadcasting not supported by security protocol!");
	}

	/*
	 * Methods needed for the secure session handshake protocol
	 */

	/**
	 * Creates a Message object from a prepared Command object
	 */
	protected static Message createMessage(String topic, HID senderHID, HID receiverHID, Command command) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] serializedCommand = null;
		try {
			command.storeToXML(bos, null);
			serializedCommand = bos.toByteArray();
		} finally{
			bos.close();
		}

		return new Message(CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC, senderHID, receiverHID, serializedCommand);
	}

	/**
	 * Parses the incoming message's data field into a Command object
	 * @param msg
	 * @return The command included in the data field
	 * @throws IOException If the data is not a command or cannot be parsed
	 */
	protected static Command getCommand(Message msg) throws IOException{
		Command command = new Command();
		ByteArrayInputStream bis = new ByteArrayInputStream(msg.getData());
		try {
			command.loadFromXML(bis);
		}finally{
			bis.close();
		}

		return command;
	}

	protected CryptoManager getCryptoMgr() {
		return cryptoMgr;
	}

	protected TrustManager getTrustMgr() {
		return trustMgr;
	}

	protected HID getClientHID() {
		return clientHID;
	}

	protected HID getServerHID() {
		return serverHID;
	}

	protected double getTrustThreshold() {
		return trustThreshold;
	}

	protected NonceGenerator getNonceGenerator() {
		return nonceGenerator;
	}

	protected void setInitialized(){
		isInitialized = true;
	}

	/**
	 * @return the localMacKey
	 */
	protected Key getLocalMacKey() {
		return localMacKey;
	}

	/**
	 * @param localMacKey the localMacKey to set
	 */
	protected void setLocalMacKey(Key localMacKey) {
		this.localMacKey = localMacKey;
	}

	/**
	 * @return the remoteMacKey
	 */
	protected Key getRemoteMacKey() {
		return remoteMacKey;
	}

	/**
	 * @param remoteMacKey the remoteMacKey to set
	 */
	protected void setRemoteMacKey(Key remoteMacKey) {
		this.remoteMacKey = remoteMacKey;
	}

	/**
	 * @return the localEncKey
	 */
	protected Key getLocalEncKey() {
		return localEncKey;
	}

	/**
	 * @param localEncKey the localEncKey to set
	 */
	protected void setLocalEncKey(Key localEncKey) {
		this.localEncKey = localEncKey;
	}

	/**
	 * @return the remoteEncKey
	 */
	protected Key getRemoteEncKey() {
		return remoteEncKey;
	}

	/**
	 * @param remoteEncKey the remoteEncKey to set
	 */
	protected void setRemoteEncKey(Key remoteEncKey) {
		this.remoteEncKey = remoteEncKey;
	}

	/**
	 * @return the localIV
	 */
	protected byte[] getLocalIV() {
		return localIV;
	}

	/**
	 * @param localIV the localIV to set
	 */
	protected void setLocalIV(byte[] localIV) {
		this.localIV = localIV;
	}

	/**
	 * @return the remoteIV
	 */
	protected byte[] getRemoteIV() {
		return remoteIV;
	}

	/**
	 * @param remoteIV the remoteIV to set
	 */
	protected void setRemoteIV(byte[] remoteIV) {
		this.remoteIV = remoteIV;
	}

	/**
	 * Sets this protocol run to started
	 */
	protected void setStarted(){
		this.isStarted = true;
	}

	/**
	 * @return Returns whether this protocol run is started
	 */
	protected boolean isStarted(){
		return this.isStarted;
	}

	/**
	 * @return the isClient
	 */
	protected boolean isClient() {
		return isClient;
	}

	/**
	 * @param isClient the isClient to set
	 */
	protected void setClient(boolean isClient) {
		this.isClient = isClient;
	}

	/**
	 * @return the storedMessage
	 */
	protected Message getStoredMessage() {
		return storedMessage;
	}

	/**
	 * @param storedMessage the storedMessage to set
	 */
	protected void setStoredMessage(Message storedMessage) {
		this.storedMessage = storedMessage;
	}

	/**
	 * @param isStarted the isStarted to set
	 */
	protected void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}
	
	/**
	 * Generates and returns the master key id
	 * @return
	 * @throws CryptoException If one of the certificates is missing
	 */
	protected String getMasterKeyId() throws CryptoException{
		if(this.masterKeyId == null){
			//generate master key id which is certificate references XORed
			String clientIdentifier = cryptoMgr.getCertificateReference(
					clientHID.toString());
			String serverIdentifier = cryptoMgr.getCertificateReference(
					serverHID.toString());
			
			if(clientIdentifier == null){
				throw new CryptoException("Does not have own certificate");
			}
			if(serverIdentifier == null){
				throw new CryptoException("Does not have certificate for HID:" + serverHID.toString());
			}
			byte[] serverId = serverIdentifier.getBytes();
			byte[] clientId = clientIdentifier.getBytes();
			byte[] mkIdentifier = new byte[(clientId.length >= serverId.length)? clientId.length : serverId.length];
			for(int i=0; i < clientId.length && i < serverId.length;i++){
				mkIdentifier[i] = (byte) (clientId[i] ^ serverId[i]);
			}
			this.masterKeyId = Base64.encodeBytes(mkIdentifier);
		}
		return masterKeyId;
	}
}