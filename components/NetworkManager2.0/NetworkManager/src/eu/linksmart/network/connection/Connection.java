package eu.linksmart.network.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import eu.linksmart.network.ErrorMessage;
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
	public static final String APPLICATION_DATA = "applicationData";
	/**
	 * Name of the property holding the topic of the message
	 */
	public static final String TOPIC = "topic";

	/**
	 * Logger from log4j
	 */
	Logger logger = Logger.getLogger(Connection.class.getName());
	/**
	 * {@link SecurityProtocol} used to protect and unprotect messages
	 */
	protected Map<HIDTuple, SecurityProtocol> securityProtocols = new ConcurrentHashMap<Connection.HIDTuple, SecurityProtocol>();
	/**
	 * The initiator of this communication
	 */
	private HID clientHID = null;
	/**
	 * The called entity of this communication
	 */
	private HID serverHID = null;
	protected CommunicationSecurityManager comSecMgr;

	protected Connection(HID serverHID) {
		if (serverHID == null) {
			throw new IllegalArgumentException(
			"Cannot set null for required fields.");
		}
		this.serverHID = serverHID;
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
	 * Set {@link CommunicationSecurityManager} for this connection. It has to 
	 * be ensured that no message is processed until CommunicationSecurityManager
	 * is set for connection.
	 * 
	 * @param comSecMgr
	 *            Reference for creator of {@link SecurityProtocol}
	 */	
	protected void setCommunicationSecMgr(CommunicationSecurityManager comSecMgr) {
		this.comSecMgr = comSecMgr;
	}

	/**
	 * Creates a Message object for received data.
	 * @param senderHID logical endpoint of message
	 * @param receiverHID logical endpoint of message
	 * @param data
	 *            Data received over network
	 * @return Message object for further processing
	 */
	public Message processData(HID senderHID, HID receiverHID, byte[] data) {
		SecurityProtocol securityProtocol = getSecurityProtocol(senderHID, receiverHID);
		Message message = null;
		//check if application data has to be unprotected
		if (securityProtocol != null && securityProtocol.isInitialized()) {
			// if protocol is initialized than open message with it
			try {
				message = unserializeMessage(data, false, senderHID, receiverHID);
				message = securityProtocol.unprotectMessage(message);
				//use data field of message to reconstruct original message
				if(!message.getTopic().
						equals(CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC)) {
					message = unserializeMessage(message.getData(), true, senderHID, receiverHID);
				}
			} catch (Exception e) {
				logger.debug("Cannot unprotect message from HID: "
						+ senderHID.toString());
				message = new ErrorMessage(ErrorMessage.ERROR,
						message.getSenderHID(), 
						message.getReceiverHID(), 
						e.getMessage().getBytes());
			}
		} else { 
			message = unserializeMessage(data, true, senderHID, receiverHID);
			if (securityProtocol != null
					&& !securityProtocol.isInitialized()) {
				// if protocol not initialized then pass it for processing			
				// Process message by Security Protocol
				try {
					message = securityProtocol.processMessage(message);
				} catch (CryptoException e) {
					logger.error("Error during cryptographic operation", e);
					message = new ErrorMessage(ErrorMessage.ERROR,
							message.getSenderHID(), 
							message.getReceiverHID(), 
							e.getMessage().getBytes());
				} catch (VerificationFailureException e) {
					logger.error("Error during cryptographic operation", e);
					message = new ErrorMessage(ErrorMessage.ERROR,
							message.getSenderHID(), 
							message.getReceiverHID(), 
							e.getMessage().getBytes());
				} catch (IOException e) {
					logger.error("Error during cryptographic operation", e);
					message = new ErrorMessage(ErrorMessage.ERROR,
							message.getSenderHID(), 
							message.getReceiverHID(), 
							e.getMessage().getBytes());
				}
			}
		}
		return message;
	}

	/**
	 * Gets the {@link SecurityProtocol} object assigned to provided HIDs from
	 * the HashMap. If no object is stored a new one is created.
	 * @param senderHID
	 * @param receiverHID
	 * @return Object assigned to this HIDs
	 */
	protected SecurityProtocol getSecurityProtocol(HID senderHID, HID receiverHID) {
		if(comSecMgr != null) {
			HIDTuple hidTuple = new HIDTuple(senderHID, receiverHID);
			if(securityProtocols.containsKey(hidTuple)) {
				return securityProtocols.get(hidTuple);
			} else {
				SecurityProtocol secProt = comSecMgr.getSecurityProtocol(senderHID, receiverHID);
				securityProtocols.put(hidTuple, secProt);
				return secProt;
			}
		} else {
			return null;
		}
	}

	/**
	 * Creates a serialized representation of the Message object which can be
	 * sent over network
	 * @param msg
	 *            Message to convert
	 * @return Serialized version of the message including all properties
	 * @throws Exception
	 *             When message cannot be processed for sending
	 */
	public byte[] processMessage(Message msg) throws Exception {
		SecurityProtocol securityProtocol = getSecurityProtocol(msg.getSenderHID(), msg.getReceiverHID());
		if (securityProtocol != null && !securityProtocol.isInitialized()) {
			// set the message to be sent by security protocol
			msg = securityProtocol.processMessage(msg);
			// the message has to be processed for sending now by the regular
			// code
		}

		//serialize the message into one stream to protect it
		byte[] serializedCommand = serializeMessage(msg, true);
		//protect the stream if should be
		if (securityProtocol != null
				&& securityProtocol.isInitialized()
				&& !msg.getTopic().contentEquals(
						CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC)) {
			/*
			 * this could also be a message which has been stored by the
			 * security protocol until now and is becoming sent at last
			 */
			// set all data of the message as data part and protect it
			msg.setData(serializedCommand);
			msg = securityProtocol.protectMessage(msg);
			//serialize the propertyless created protected dummy message
			return serializeMessage(msg, false);
		} else {
			return serializedCommand;
		}
	}

	/**
	 * Creates a stream from the provided message
	 * @param msg to be serialized
	 * @param includeProperties
	 * @return
	 */
	private byte[] serializeMessage(Message msg, boolean includeProperties) {
		Properties props = new Properties();
		if(includeProperties) {
			// read the properties of the message and put it into the properties
			Iterator<String> i = msg.getKeySet().iterator();
			while (i.hasNext()) {
				String key = i.next();
				props.put(key, msg.getProperty(key));
			}
		}
		// put application data into properties
		props.put(APPLICATION_DATA, Base64.encodeBytes(msg.getData()));
		props.put(TOPIC, msg.getTopic());
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
		return serializedCommand;
	}

	/**
	 * Creates message from received byte stream.
	 * @param serializedMsg Stream to read from
	 * @param includeProps Fill properties fields of msg
	 * @param senderHID
	 * @param receiverHID
	 * @return
	 */
	private Message unserializeMessage(byte[] serializedMsg, boolean includeProps, HID senderHID, HID receiverHID) {
		// open data and divide it into properties of the message and
		// application data
		Properties properties = new Properties();
		try {
			properties.loadFromXML(new ByteArrayInputStream(serializedMsg));
		} catch (InvalidPropertiesFormatException e) {
			logger.error(
					"Unable to load properties from XML data. Data is not valid XML: "
					+ new String(serializedMsg), e);
			return new ErrorMessage(ErrorMessage.RECEPTION_ERROR,
					senderHID, receiverHID, e.getMessage().getBytes());
		} catch (IOException e) {
			logger.error("Unable to load properties from XML data: "
					+ new String(serializedMsg), e);
			return new ErrorMessage(ErrorMessage.RECEPTION_ERROR,
					senderHID, receiverHID, e.getMessage().getBytes());
		}

		// create real message
		Message message = new Message((String) properties.remove(TOPIC),
				senderHID, receiverHID, (Base64.decode((String) properties
						.remove(APPLICATION_DATA))));
		if(includeProps) {
			// go through the properties and add them to the message
			Iterator<Object> i = properties.keySet().iterator();
			while (i.hasNext()) {
				String key = (String) i.next();
				message.setProperty(key, properties.getProperty(key));
			}
		}
		return message;
	}

	class HIDTuple {
		private HID hid1 = null;
		private HID hid2 = null;

		public HIDTuple(HID one, HID two) {
			hid1 = one;
			hid2 = two;
		}

		public HID getHID1() {
			return hid1;
		}

		public HID getHID2() {
			return hid2;
		}

		@Override
		public boolean equals(Object o)  {
			HIDTuple tuple = (HIDTuple)o;
			if((tuple.getHID1().equals(hid1) && tuple.getHID2().equals(hid2))
					|| (tuple.getHID1().equals(hid2) && tuple.getHID2().equals(hid1))) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			int hid1Hash = hid1.hashCode();
			int hid2Hash = hid2.hashCode();
			//returned hash must be indifferent for tuples with same two HIDs
			return hid1Hash & hid2Hash;
		}
	}
}
