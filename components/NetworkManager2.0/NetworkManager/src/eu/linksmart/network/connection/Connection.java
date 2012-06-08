package eu.linksmart.network.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.CryptoException;
import eu.linksmart.security.communication.SecurityProtocol;
import eu.linksmart.security.communication.VerificationFailureException;
import eu.linksmart.utils.Base64;

/**
 * Holds properties and objects relevant for a connection between two HIDs.
 * 
 * @author Vinkovits
 * 
 */
public class Connection {

	/**
	 * Name of the property holding the applicaton data
	 */
	private static final String APPLICATION_DATA = "applicationData";
	/**
	 * Name of the property holding the topic of the message
	 */
	private static final String TOPIC = "topic";

	/**
	 * Logger from log4j
	 */
	Logger logger = Logger.getLogger(Connection.class.getName());
	/**
	 * {@link SecurityProtocol} used to protect and unprotect messages
	 */
	protected SecurityProtocol securityProtocol = null;
	/**
	 * The initiator of this communication
	 */
	private HID clientHID = null;
	/**
	 * The called entity of this communication
	 */
	private HID serverHID = null;

	protected Connection(HID clientHID) {
		if (clientHID == null) {
			throw new IllegalArgumentException(
					"Cannot set null for required fields.");
		}
		this.clientHID = clientHID;
	}

	public Connection(HID clientHID, HID serverHID) {
		if (clientHID == null || serverHID == null) {
			throw new IllegalArgumentException(
					"Cannot set null for required fields.");
		}
		this.clientHID = clientHID;
		this.serverHID = serverHID;
	}

	public HID getClientHID() {
		return clientHID;
	}

	public HID getServerHID() {
		return serverHID;
	}

	/**
	 * Set {@link SecurityProtocol} for this connection. It has to be ensured
	 * that no message is processed until no security protocol is set for
	 * connection.
	 * 
	 * @param secProt
	 *            Connection specific object
	 */
	protected void setSecurityProtocol(SecurityProtocol secProt) {
		securityProtocol = secProt;
	}

	/**
	 * Creates a Message object for received data
	 * 
	 * @param data
	 *            Data received over network
	 * @return Message object for further processing
	 */
	public Message processData(HID senderHID, HID receiverHID, byte[] data) {
		// open data and divide it into properties of the message and
		// application data
		Properties properties = new Properties();
		try {
			properties.loadFromXML(new ByteArrayInputStream(data));
		} catch (InvalidPropertiesFormatException e) {
			logger.error(
					"Unable to load properties from XML data. Data is not valid XML: "
							+ new String(data), e);
		} catch (IOException e) {
			logger.error("Unable to load properties from XML data: "
					+ new String(data), e);
		}

		// create real message
		Message message = new Message((String) properties.remove(TOPIC),
				senderHID, receiverHID, (Base64.decode((String) properties
						.remove(APPLICATION_DATA))));
		// go through the properties and add them to the message
		Iterator<Object> i = properties.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			message.setProperty(key, properties.getProperty(key));
		}
		
		//check if application data has to be unprotected
		if (securityProtocol != null && securityProtocol.isInitialized()) {
			// if protocol is initialized than open message with it
			try {
				message = securityProtocol.unprotectMessage(message);
			} catch (Exception e) {
				logger.debug("Cannot unprotect message from HID: "
						+ senderHID.toString());
				return null;
			}
		} else if (securityProtocol != null
				&& !securityProtocol.isInitialized()) {
			// if protocol not initialized then pass it for processing			
			// Process message by Security Protocol
			try {
				message = securityProtocol.processMessage(message);
			} catch (CryptoException e) {
				logger.error("Error during cryptographic operation", e);
			} catch (VerificationFailureException e) {
				logger.error("Error during cryptographic operation", e);
			} catch (IOException e) {
				logger.error("Error during cryptographic operation", e);
			}
		}
		return message;
	}

	/**
	 * Creates a serialized representation of the Message object which can be
	 * sent over network
	 * 
	 * @param msg
	 *            Message to convert
	 * @return Serialized version of the message including all properties
	 * @throws Exception
	 *             When message cannot be processed for sending
	 */
	public byte[] processMessage(Message msg) throws Exception {
		if (securityProtocol != null && !securityProtocol.isInitialized()) {
			// set the message to be sent by security protocol
			msg = securityProtocol.processMessage(msg);
			// the message has to be processed for sending now by the regular
			// code
		}
		// read the properties of the message and put them into one properties
		// object
		Properties props = new Properties();
		Iterator<String> i = msg.getKeySet().iterator();
		while (i.hasNext()) {
			String key = i.next();
			props.put(key, msg.getProperty(key));
		}
		// put application data into properties
		props.put(APPLICATION_DATA, Base64.encodeBytes(msg.getData()));
		props.put(TOPIC, msg.getTopic());
		// convert props into xml and encode it
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
		if (securityProtocol != null
				&& securityProtocol.isInitialized()
				&& props.getProperty(TOPIC).equals(
						CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC)) {
			/*
			 * this could also be a message which has been stored by the
			 * security protocol until now and is becoming sent at last
			 */
			// set all data of the message as data part and protect it
			msg.setData(serializedCommand);
			msg = securityProtocol.protectMessage(msg);
			return msg.getData();
		} else {
			return serializedCommand;
		}
	}
}
