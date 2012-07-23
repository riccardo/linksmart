package eu.linksmart.security.communication.impl.sym;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.sql.SQLException;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Properties;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.CryptoException;
import eu.linksmart.security.communication.SecurityProtocol;
import eu.linksmart.security.communication.VerificationFailureException;
import eu.linksmart.security.communication.util.impl.Command;
import eu.linksmart.security.communication.util.impl.XMLMessageUtil;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.utils.Base64;

/**
 * Exchanges certificates and lets the server generate a symmetric key.
 * The establishment of a session symmetric key is also included in
 * this handshake.
 * @author Vinkovits
 *
 */
public class AsymHandshake {

	/**
	 * The Log4j logger of this class.
	 */
	private static Logger logger = Logger.getLogger(SecurityProtocolImpl.class);
	/**
	 * The owner of this handshake class.
	 */
	SecurityProtocolImpl secProtocol = null;
	/**
	 * Indicates for a server state machine that it has already sent keys to the client.
	 */
	private boolean sentKeyToClient = false;
	/**
	 * Indicates for a client state machine that the acknowledgment has been sent.
	 */
	private boolean sentAcknowledgement = false;
	/**
	 * The symmetric handshake, which can generate the authentication tokens.
	 */
	SymHandshake symHandshake = null;
	/**
	 * The master symmetric key.
	 */
	private String masterKeyId;

	protected AsymHandshake(SecurityProtocolImpl secProtocol) {
		this.secProtocol = secProtocol;
		symHandshake = new SymHandshake(secProtocol);
	}

	/**
	 * Behaves as it were the startProtocol method of {@link SecurityProtocol}.
	 * @return First Message to send
	 * @throws Exception
	 */
	protected Message startProtocol() throws Exception {
		HID clientHID = secProtocol.getClientHID();
		HID serverHID = secProtocol.getServerHID();

		//fill fields of message
		Command cmd = new Command(Command.CLIENT_REQUESTS_KEY);	
		cmd.setProperty(Command.CLIENT, clientHID.toString());
		cmd.setProperty(Command.SERVER, serverHID.toString());
		//set client nonce
		String nonce = secProtocol.getNonceGenerator().getNextNonce();
		cmd.setProperty(Command.CLIENT_NONCE, nonce);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		cmd.storeToXML(bos, null);
		symHandshake.setClientNonce(nonce);

		//signing is mainly necessary to send certificate and prove you have private key
		String signedCommand = secProtocol.getCryptoMgr().sign(
				bos.toString(),
				null,
				clientHID.toString());
		cmd.setProperty(Command.SIGNED_PAYLOAD, signedCommand);

		return SecurityProtocolImpl.createMessage(
				CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC,
				clientHID,
				serverHID,
				cmd);
	}

	/**
	 * Behaves exactly as the processMessage method of {@link SecurityProtocol}
	 * @param msg
	 * @return next message to come in handshake
	 * @throws IOException
	 * @throws CryptoException
	 * @throws Exception
	 */
	protected Message processMessage(Message msg) throws IOException, CryptoException, Exception{		
		//cache actual state of security protocol to not have so many method calls
		CryptoManager cryptoMgr = secProtocol.getCryptoMgr();
		HID serverHID = secProtocol.getServerHID();
		HID clientHID = secProtocol.getClientHID();

		//get message content
		Command command = SecurityProtocolImpl.getCommand(msg);
		//check whether this is the correct party
		if(!command.get(Command.CLIENT).equals(clientHID.toString()) 
				|| !command.get(Command.SERVER).equals(serverHID.toString())) {
			throw new VerificationFailureException("Not appropriate sender or receiver of handshake");
		}

		if(secProtocol.isClient()) {
			if(!sentAcknowledgement) {
				//response from the server which should contain a certificate
				if(Integer.parseInt(command.getProperty("command")) == Command.SERVER_SEND_KEY){
					String signedAndEncryptedPayload = command.getProperty(Command.SIGNED_AND_ENCRYPTED_PAYLOAD);

					//verify signature on message
					logger.debug("Verifying message " + signedAndEncryptedPayload);
					String verifiedMessage = cryptoMgr.verify(signedAndEncryptedPayload);
					logger.debug("Signature of Message from Server verified: " + (verifiedMessage!=null));

					//if signature is valid extract symmetric key
					if (verifiedMessage!=null && !verifiedMessage.isEmpty()) {
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
						String clientNonce = symHandshake.getClientNonce();
						if (client.equals(command.getProperty(Command.CLIENT))
								&& server.equals(command.getProperty(Command.SERVER))
								&& cmd.equals(command.getProperty(Command.COMMAND))
								&& clientNonce.equals(props.getProperty(Command.CLIENT_NONCE))) {
							logger.debug("Signed Headers are the same as outer Headers");
							//store received symmetric key and extract symmetric handshake tokens
							storeSymKey(props.getProperty(Command.SYMMETRIC_KEY));
							symHandshake.setServerNonce(props.getProperty(Command.SERVER_NONCE));
							symHandshake.generateSessionKeys();
							//handshake is on client side finished now
							setInitialized();
						} else {
							logger.debug("Encrypted headers are not the same as outer headers, aborting!");
							throw new VerificationFailureException(
									"Signature from HID: " + serverHID + " not matching to required fields!");
						}
					} else {
						logger.debug("Signature not valid, aborting");
						throw new VerificationFailureException("Signature from HID: " + serverHID + " not valid!");
					} 
					//send acknowledgment to server of receiving the key
					Command cmd = new Command(Command.CLIENT_ACK);
					cmd.setProperty(Command.CLIENT, clientHID.toString());
					cmd.setProperty(Command.SERVER, serverHID.toString());
					//also add protected stored message to acknowledgment
					Message storedMessage = null;
					if((storedMessage = secProtocol.getStoredMessage()) != null){
						//put whole message into data field - is independent from connection
						Properties props = new Properties();
						// read the properties of the message and put it into the properties
						Iterator<String> i = storedMessage.getKeySet().iterator();
						while (i.hasNext()) {
							String key = i.next();
							props.put(key, storedMessage.getProperty(key));
						}
						// put application data into properties
						props.put(Command.APPLICATION_MESSAGE, Base64.encodeBytes(storedMessage.getData()));
						props.put(Command.TOPIC, storedMessage.getTopic());
						//convert properties to xml and put it into stream
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						byte[] serializedCommand = null;
						try {
							props.storeToXML(bos, null);
							serializedCommand = bos.toByteArray();
						} catch (IOException e) {
							logger.warn("Message to be sent cannot be parsed!");
						} finally {
							try {
								bos.close();
							} catch (IOException e) {
								logger.error("Error closing stream", e);
							}
						}
						storedMessage.setData(serializedCommand);
						Message protectedMessage = secProtocol.protectMessage(storedMessage);
						cmd.setProperty(Command.APPLICATION_MESSAGE, new String(protectedMessage.getData()));
						secProtocol.setStoredMessage(null);
					}
					//send message
					try {
						setInitialized();
						return SecurityProtocolImpl.createMessage(
								CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC,
								clientHID,
								serverHID,
								cmd);
					} catch (IOException e) {
						//this exception cannot happen
						logger.error("Error creating acknowledgment for server",e);
						return null;
					}
				} else {
					logger.debug("Not appropriate message received from HID: " + serverHID.toString());
				}
			}
		} else {
			//this is server and client sent request or acknowledgment
			if(Integer.parseInt(command.getProperty(Command.COMMAND)) == Command.CLIENT_REQUESTS_KEY) {
				//if signature on message and certificate are valid go to next state	
				if(verifySignature(command) && verifyCertificate(command)){
					//get client nonce from message
					symHandshake.setClientNonce(command.getProperty(Command.CLIENT_NONCE));
					//create symmetric key and store it
					String key = createKey();
					storeSymKey(key);
					//pack key into command
					Command cmd = new Command(Command.SERVER_SEND_KEY);
					cmd.setProperty(Command.SYMMETRIC_KEY, key);
					cmd.setProperty(Command.CLIENT, clientHID.toString());
					cmd.setProperty(Command.SERVER, serverHID.toString());
					//add server nonce for session key agreement
					String serverNonce = secProtocol.getNonceGenerator().getNextNonce();
					symHandshake.setServerNonce(serverNonce);
					symHandshake.generateSessionKeys();
					cmd.setProperty(Command.SERVER_NONCE, serverNonce);
					//add client nonce to ensure freshness of generated key
					cmd.setProperty(Command.CLIENT_NONCE, command.getProperty(Command.CLIENT_NONCE));

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					cmd.storeToXML(bos, null);

					//encrypting and signing message
					logger.debug("Encrypting " + bos.toString());
					logger.debug("Encrypting for " + clientHID.toString());
					String encryptedCommand;
					try {
						encryptedCommand = cryptoMgr.encryptAsymmetric(bos.toString(), clientHID.toString(), "");
					} catch (Exception e) {
						CryptoException ce = new CryptoException("Error encrypting message!");
						ce.initCause(e);
						throw ce;
					}
					String signedCommand = cryptoMgr.sign(encryptedCommand, null, serverHID.toString());

					//add encrypted and remove clear text key from message
					cmd.setProperty(Command.SIGNED_AND_ENCRYPTED_PAYLOAD, signedCommand);
					cmd.remove(Command.SYMMETRIC_KEY);
					cmd.remove(Command.SERVER_NONCE);
					cmd.remove(Command.CLIENT_NONCE);

					sentKeyToClient = true;
					return SecurityProtocolImpl.createMessage(
							CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC,
							serverHID,
							clientHID,
							cmd);
				}
			} else if (sentKeyToClient && Integer.parseInt(command.getProperty("command")) == Command.CLIENT_ACK) {
				setInitialized();
				//read application data out of acknowledgment
				if(command.containsKey(Command.APPLICATION_MESSAGE)) {
					String data = command.getProperty(Command.APPLICATION_MESSAGE);
					Message message = new Message(SecurityProtocol.CIPHER_TEXT, secProtocol.getClientHID(), secProtocol.getServerHID(), data.getBytes());
					message = secProtocol.unprotectMessage(message);
					// open data and divide it into properties of the message and
					// application data
					byte[] serializedMsg = message.getData();
					Properties properties = new Properties();
					try {
						properties.loadFromXML(new ByteArrayInputStream(serializedMsg));
					} catch (InvalidPropertiesFormatException e) {
						logger.error(
								"Unable to load properties from XML data. Data is not valid XML: "
								+ new String(serializedMsg), e);
					} catch (IOException e) {
						logger.error("Unable to load properties from XML data: "
								+ new String(serializedMsg), e);
					}

					// create real message
					message = new Message((String) properties.remove(Command.TOPIC),
							message.getSenderHID(), message.getReceiverHID(), (Base64.decode((String) properties
									.remove(Command.APPLICATION_MESSAGE))));
					// go through the properties and add them to the message
					Iterator<Object> i = properties.keySet().iterator();
					while (i.hasNext()) {
						String key = (String) i.next();
						message.setProperty(key, properties.getProperty(key));
					}
					return message;
				}
			}
		}
		return msg;
	}

	/**
	 * Sets this handshake and the security protocol to initialized state
	 */
	private void setInitialized(){
		secProtocol.setInitialized();
	}

	/**
	 * Associates servers's HID with certificate in {@link CryptoManager}
	 * @param identifier
	 */
	private void saveServerPk(String identifier){
		secProtocol.getCryptoMgr().addCertificateForHID(secProtocol.getServerHID().toString(), identifier);
		logger.debug("Server KEY IDENTIFIER FROM CRYPTOMANAGER: " 
				+ identifier + " has been bound to HID: " + secProtocol.getServerHID().toString());
	}

	/**
	 * Associates client's HID with certificate in {@link CryptoManager}
	 * @param identifier
	 */
	public void saveClientPk(String identifier){
		secProtocol.getCryptoMgr().addCertificateForHID(secProtocol.getClientHID().toString(), identifier);
		logger.debug("CLIENT KEY IDENTIFIER FROM CRYPTOMANAGER: " + identifier + " has been bound to HID: " + secProtocol.getClientHID().toString());
	}

	/**
	 * Stores the received symmetric key into {@link CryptoManager}
	 * @param key Base64 encoded key
	 * @throws CryptoException 
	 */
	private void storeSymKey(String key) throws CryptoException {
		String master_identifier = secProtocol.getMasterKeyId();
		boolean storedSymKey = false;
		try {
			if (secProtocol.getCryptoMgr().identifierExists(master_identifier)) {
				boolean deleted = secProtocol.getCryptoMgr()
										.deleteEntry(master_identifier);
				if(!deleted) {
					throw new CryptoException("Cannot delete previously existing key with identifier: " 
							+ master_identifier);
				}
			}
			storedSymKey = secProtocol.getCryptoMgr().storeSymmetricKeyWithFriendlyName(
					master_identifier,
					SecurityProtocolImpl.MAC_ALGORITHM,
					key);	
		} catch (SQLException e) {
			CryptoException ce = new CryptoException("Error during storing symmetric key in CryptoManager database");
			ce.initCause(e);
			throw ce;
		}
		if(!storedSymKey){
			throw new CryptoException("Cannot store master key with supported identifier: " + master_identifier);
		}

		this.symHandshake.setMasterKeyIdentifier(master_identifier);
	}

	/**
	 * Checks using the CryptoManager whether the signature is valid.
	 * @param command Command received from other party
	 * @return true if valid - false if not
	 * @throws IOException 
	 */
	private boolean verifySignature(Command command) throws IOException{
		String signedPayload = command.getProperty(Command.SIGNED_PAYLOAD);

		String verifiedMessage = secProtocol.getCryptoMgr().verify(signedPayload);
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

		if (secProtocol.getTrustMgr()!=null) {
			try{
				String encodedcert = Base64.encodeBytes(cert.getEncoded());
				trust = secProtocol.getTrustMgr().getTrustValue(encodedcert);
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

		if (trust < secProtocol.getTrustThreshold() ) {
			return false;
		} else {		
			//as certificate is valid store it in keystore
			try{
				String clientKeyIdentifier = secProtocol.getCryptoMgr().storePublicKey(Base64.encodeBytes(cert.getEncoded()), "");
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

	/**
	 * Generates a symmetric key.
	 * @return A Base64 encoded symmetric key
	 * @throws CryptoException
	 */
	private String createKey() throws CryptoException{
		String key = null;
		try {
			//generate key
			KeyGenerator keyGenerator = KeyGenerator.getInstance(SecurityProtocolImpl.SYMMETRIC_ENCRYPTION_ALGORITHM);
			SecretKey secretKey = keyGenerator.generateKey();
			key = Base64.encodeBytes(secretKey.getEncoded());
		} catch (Exception e) {
			CryptoException ce = new CryptoException("Error generating symmetric key!");
			ce.initCause(e);
			throw ce;
		}
		return key;
	}
}
