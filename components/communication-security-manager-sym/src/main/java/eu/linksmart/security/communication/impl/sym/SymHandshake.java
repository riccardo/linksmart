package eu.linksmart.security.communication.impl.sym;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import eu.linksmart.network.Message;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProtocol;
import eu.linksmart.security.communication.VerificationFailureException;
import eu.linksmart.security.communication.util.impl.BytesUtil;
import eu.linksmart.security.communication.util.impl.Command;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.utils.Base64;

/**
 * Establishes symmetric keys from a master symmetric key.
 * @author Vinkovits
 *
 */
public class SymHandshake {

	/**
	 * The Log4j logger of this class.
	 */
	private static Logger logger =
		Logger.getLogger(SecurityProtocolImpl.class);
	/**
	 * The owner of this handshake object
	 */
	private SecurityProtocolImpl secProtocol = null;
	/**
	 * States of the handshake protocol.
	 */
	private Properties properties = new Properties();
	/**
	 * Identifier of master key.
	 */
	private String masterKeyId;
	/**
	 * Indicates for the server that it already sent the auth token.
	 */
	private boolean sentServerAuth = false;

	public SymHandshake(SecurityProtocolImpl securityProtocol) {
		this.secProtocol = securityProtocol;
	}

	/**
	 * Behaves as it were the startProtocol method of {@SecurityProtocol}.
	 * @return First Message to send
	 * @throws IOException
	 */
	public Message startProtocol() throws IOException {
		logger.debug("Sending client nonce");

		//generate nonce for client
		String nonce = secProtocol.getNonceGenerator().getNextNonce();
		properties.setProperty(Command.CLIENT_NONCE, nonce);
		//create client hello message
		Command cmd = new Command(Command.CLIENT_HELLO);
		cmd.setProperty(Command.CLIENT_NONCE, nonce);
		cmd.setProperty(Command.CLIENT, secProtocol.getClientVirtualAddress()
				.toString());
		cmd.setProperty(Command.SERVER, secProtocol.getServerVirtualAddress()
				.toString());

		Message message = SecurityProtocolImpl.createMessage(
				CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC,
				secProtocol.getClientVirtualAddress(),
				secProtocol.getServerVirtualAddress(),
				cmd);

		return message;
	}

	/**
	 * Behaves exactly as the processMessage method of {@link SecurityProtocol}
	 * @param msg
	 * @return next message to come in handshake
	 * @throws Exception
	 */
	public Message processMessage(Message msg) throws Exception {
		VirtualAddress clientVirtualAddress = secProtocol.getClientVirtualAddress();
		VirtualAddress serverVirtualAddress = secProtocol.getServerVirtualAddress();

		//get message content
		Command command = SecurityProtocolImpl.getCommand(msg);
		//check whether this is the correct party
		if(!command.get(Command.CLIENT).equals(clientVirtualAddress.toString())
				|| !command.get(Command.SERVER).equals(serverVirtualAddress.toString())){
			throw new VerificationFailureException("Not appropriate sender or receiver of handshake");
		}

		if(secProtocol.isClient()){
			//process message
			if(Integer.parseInt(command.getProperty("command")) == Command.SERVER_SEND_AUTH){
				logger.debug("Creating client side session keys and controlling server auth token");
				try{
					//read nonce and generate keys
					properties.put(Command.SERVER_NONCE, command.getProperty(Command.SERVER_NONCE));
					//generate session keys
					generateSessionKeys();
					//check received mac
					String authToken = command.getProperty(Command.SERVER_AUTH_TOKEN);

					logger.debug("Received server authToken: " + authToken);
					//recalculate mac to check
					Key serverMacKey = secProtocol.getRemoteMacKey();
					Mac authMac = Mac.getInstance(SecurityProtocolImpl.MAC_ALGORITHM);
					authMac.init(serverMacKey);
					String checkAuth = getAuthenticationToken(authMac);

					logger.debug("Calculated server authToken: " + checkAuth);

					//continue if they are the same
					if(checkAuth.equals(authToken)){
						Key clientMacKey = secProtocol.getLocalMacKey();
						Mac macClient = Mac.getInstance(SecurityProtocolImpl.MAC_ALGORITHM);
						macClient.init(clientMacKey);

						logger.debug("Client mac key:");
						logger.debug(BytesUtil.printBytes(clientMacKey.getEncoded()));

						String ownAuthToken = getAuthenticationToken(macClient);

						logger.debug("Calculated client authToken:");
						logger.debug(ownAuthToken);

						//create client ack message
						Command response = new Command(Command.CLIENT_ACK);
						response.setProperty(Command.CLIENT_AUTH_TOKEN, ownAuthToken);
						response.setProperty(Command.CLIENT, clientVirtualAddress.toString());
						response.setProperty(Command.SERVER, serverVirtualAddress.toString());
						secProtocol.setInitialized();
						//also add protected stored message to acknowledgment
						Message storedMessage = null;
						if((storedMessage = secProtocol.getStoredMessage()) != null){
							Message protectedMessage = secProtocol.protectMessage(storedMessage);
							response.setProperty(Command.APPLICATION_MESSAGE, new String(protectedMessage.getData()));
							storedMessage = null;
						}
						return SecurityProtocolImpl.createMessage(
								CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC,
								clientVirtualAddress,
								serverVirtualAddress,
								response);
					} else {
						logger.debug("Received authentication token is not valid from VirtualAddress: " + serverVirtualAddress.toString());
						throw new VerificationFailureException("Authentication token not valid");
					}
				} catch (NoSuchAlgorithmException e) {
					logger.error("Error getting MAC algorithm.", e);
				}
			} else {
				logger.debug("Cannot interpret message from server VirtualAddress: " + serverVirtualAddress.toString());
			}
		} else {
			if(!sentServerAuth && Integer.parseInt(command.getProperty("command")) == Command.CLIENT_HELLO){
				logger.debug("Creating server side session keys and auth token");
				//this is the server who received a request for session keys
				logger.debug("Client nonce message processing");

				//generate nonce and generate keys
				String serverNonce = secProtocol.getNonceGenerator().getNextNonce();
				properties.setProperty(Command.SERVER_NONCE, serverNonce);
				properties.setProperty(Command.CLIENT_NONCE, command.getProperty(Command.CLIENT_NONCE));
				generateSessionKeys();

				//create auth token
				Key serverMacKey = secProtocol.getLocalMacKey();
				try {
					Mac authMac = null;
					authMac = Mac.getInstance(SecurityProtocolImpl.MAC_ALGORITHM);
					authMac.init(serverMacKey);

					String serverAuth = getAuthenticationToken(authMac);

					logger.debug("Calculated server authToken: " + serverAuth);

					//create response message
					Command response = new Command(Command.SERVER_SEND_AUTH);
					response.setProperty(Command.SERVER_NONCE, serverNonce);
					response.setProperty(Command.SERVER_AUTH_TOKEN, serverAuth);
					response.setProperty(Command.CLIENT, clientVirtualAddress.toString());
					response.setProperty(Command.SERVER, serverVirtualAddress.toString());

					sentServerAuth  = true;
					return SecurityProtocolImpl.createMessage(
							CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC,
							serverVirtualAddress,
							clientVirtualAddress,
							response);
				} catch (NoSuchAlgorithmException e) {
					logger.error("Error getting MAC algorithm",e);
				}
			} else if(sentServerAuth && Integer.parseInt(command.getProperty("command")) == Command.CLIENT_ACK){
				//check if authentication token from client is valid
				logger.debug("Checking if client auth token is valid");
				//recalculate mac to check received token
				Key clientMacKey = secProtocol.getRemoteMacKey();
				Mac macClient = Mac.getInstance(SecurityProtocolImpl.MAC_ALGORITHM);
				macClient.init(clientMacKey);

				String calcAuthToken = getAuthenticationToken(macClient);

				logger.debug("Calculated client authToken: " + calcAuthToken);

				//extract mac from received message
				String clientAuthSent = command.getProperty(Command.CLIENT_AUTH_TOKEN);

				logger.debug("Received client authToken: " + clientAuthSent);

				if(calcAuthToken.equals(clientAuthSent)){
					secProtocol.setInitialized();
					//read application data out of acknowledgment
					if(command.containsKey(Command.APPLICATION_MESSAGE)){
						String data = command.getProperty(Command.APPLICATION_MESSAGE);
						Message message = new Message(SecurityProtocol.CIPHER_TEXT, secProtocol.getClientVirtualAddress(), secProtocol.getServerVirtualAddress(), data.getBytes());
						message = secProtocol.unprotectMessage(message);
						return message;
					}
				} else {
					logger.debug("Received authentication token is not valid from VirtualAddress: " + clientVirtualAddress.toString());
					throw new VerificationFailureException("Authentication token not valid");
				}
			}
		}
		return null;
	}

	/**
	 * Sets the client's nonce for the handshake.
	 * @param nonce
	 */
	protected void setClientNonce(String nonce){
		properties.setProperty(Command.CLIENT_NONCE, nonce);
	}
	
	/**
	 * Returs the client nonce stored in this handshake.
	 * @return previously stored nonce
	 */
	protected String getClientNonce(){
		return properties.getProperty(Command.CLIENT_NONCE);
	}

	/**
	 * Sets the server's nonce for the handshake
	 * @param nonce
	 */
	protected void setServerNonce(String nonce){
		properties.setProperty(Command.SERVER_NONCE, nonce);
	}

	/**
	 * Generates from the stored properties the session keys.
	 * The input is taken from the properties field using the
	 * {@link Command} constants for property names
	 * The creates keys are stored in the {@link SecurityProtocol}
	 * owner of this object.
	 */
	protected void generateSessionKeys(){
		CryptoManager cryptoMgr = secProtocol.getCryptoMgr();
		VirtualAddress clientVirtualAddress = secProtocol.getClientVirtualAddress();
		VirtualAddress serverVirtualAddress = secProtocol.getServerVirtualAddress();

		String sessionKeyCreator = properties.getProperty(Command.CLIENT_NONCE)
		+ properties.getProperty(Command.SERVER_NONCE)
		+ clientVirtualAddress.toString()
		+ serverVirtualAddress.toString();

		try{
		//create session keys
		byte[] keys = cryptoMgr.calculateMac(masterKeyId, new String(sessionKeyCreator + "keys"),
				SecurityProtocolImpl.MAC_ALGORITHM);
		byte[] server = BytesUtil.extractBytes(keys, 128, 0);
		byte[] client = BytesUtil.extractBytes(keys, 128, 128);

		byte[] serverTemp = cryptoMgr.calculateMac(masterKeyId, new String(server),
				SecurityProtocolImpl.MAC_ALGORITHM);
		byte[] serverEnc = BytesUtil.extractBytes(serverTemp, 128, 0);
		byte[] serverMac = BytesUtil.extractBytes(serverTemp, 128, 128);

		byte[] clientTemp = cryptoMgr.calculateMac(masterKeyId, new String(client),
				SecurityProtocolImpl.MAC_ALGORITHM);
		byte[] clientEnc = BytesUtil.extractBytes(clientTemp, 128, 0);
		byte[] clientMac = BytesUtil.extractBytes(clientTemp, 128, 128);

		//IVs for messages - not used at the moment
//		byte[] ivs = cryptoMgr.calculateMac(masterKeyId, new String(sessionKeyCreator + "ivs"),
//		SecurityProtocolImpl.MAC_ALGORITHM);
//		byte[] server_iv = BytesUtil.extractBytes(ivs, 128, 0);
//		byte[] client_iv = BytesUtil.extractBytes(ivs, 128, 128);

		saveSessionKeys(clientEnc, serverEnc, clientMac, serverMac, null, null);
		} catch (KeyStoreException ke){
			logger.warn("Cannot find or load master key for services: "
					+ clientVirtualAddress.toString()
					+ "," + serverVirtualAddress.toString());
		} catch (InvalidKeyException e) {
			logger.error("Stored master key cannot be used for symmetric key generation for services: "
					+ clientVirtualAddress.toString() + "," + serverVirtualAddress.toString());
		} catch (NoSuchAlgorithmException e) {
			//should not happen
			logger.error("Cannot find HmacSHA256 algorithm in JCE");
		}
	}

	/**
	 * Creates the token and calculates the MAC value which can
	 * be sent as authentication to the other party.
	 * @param mac The MAC object to be used for the generation
	 * @return Token to be sent
	 */
	protected String getAuthenticationToken(Mac mac){
		byte[] authToken = mac.doFinal((properties.getProperty(Command.CLIENT_NONCE)
				+ properties.getProperty(Command.SERVER_NONCE)
				+ secProtocol.getClientVirtualAddress().toString()
				+ secProtocol.getServerVirtualAddress().toString()
				+ "authentication")
				.getBytes());
		return Base64.encodeBytes(authToken);
	}
	
	/**
	 * Sets the master key identifier from which session keys will
	 * be generated.
	 * @param identifier Certificate references XORed and Base64 encoded
	 */
	protected void setMasterKeyIdentifier(String identifier){
		this.masterKeyId = identifier;
	}

	/**
	 * Saves the session keys into the {@link SecurityProtocol} owner
	 */
	private void saveSessionKeys(byte[] clientEnc, byte[] serverEnc, byte[] clientMac,
			byte[] serverMac, byte[] clientIV, byte[] serverIV){
		//save keys in security protocol object
		if (secProtocol.isClient()) {
			Key clientMacKey = new SecretKeySpec(clientMac, SecurityProtocolImpl.MAC_ALGORITHM);
			secProtocol.setLocalMacKey(clientMacKey);
			
			Key clientEncKey = new SecretKeySpec(clientEnc, SecurityProtocolImpl.SYMMETRIC_ENCRYPTION_ALGORITHM);
			secProtocol.setLocalEncKey(clientEncKey);
			
			secProtocol.setLocalIV(clientIV);
			
			Key serverMacKey = new SecretKeySpec(serverMac, SecurityProtocolImpl.MAC_ALGORITHM);
			secProtocol.setRemoteMacKey(serverMacKey);
			
			Key serverEncKey = new SecretKeySpec(serverEnc, SecurityProtocolImpl.SYMMETRIC_ENCRYPTION_ALGORITHM);
			secProtocol.setRemoteEncKey(serverEncKey);
			
			secProtocol.setRemoteIV(serverIV);
		} else {
			Key clientMacKey = new SecretKeySpec(clientMac, SecurityProtocolImpl.MAC_ALGORITHM);
			secProtocol.setRemoteMacKey(clientMacKey);
			
			Key clientEncKey = new SecretKeySpec(clientEnc, SecurityProtocolImpl.SYMMETRIC_ENCRYPTION_ALGORITHM);
			secProtocol.setRemoteEncKey(clientEncKey);
			
			secProtocol.setRemoteIV(clientIV);
			
			Key serverMacKey = new SecretKeySpec(serverMac, SecurityProtocolImpl.MAC_ALGORITHM);
			secProtocol.setLocalMacKey(serverMacKey);
			
			Key serverEncKey = new SecretKeySpec(serverEnc, SecurityProtocolImpl.SYMMETRIC_ENCRYPTION_ALGORITHM);
			secProtocol.setLocalEncKey(serverEncKey);
			
			secProtocol.setLocalIV(serverIV);
		}
	}
}
