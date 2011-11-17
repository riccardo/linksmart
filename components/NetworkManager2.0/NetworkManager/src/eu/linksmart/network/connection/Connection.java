package eu.linksmart.network.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.networkmanager.core.impl.NetworkManagerCoreImpl;
import eu.linksmart.security.communication.CryptoException;
import eu.linksmart.security.communication.SecurityProtocol;
import eu.linksmart.security.communication.VerificationFailureException;

/**
 * Holds properties and objects relevant for a connection
 * between two HIDs.
 * @author Vinkovits
 *
 */
public class Connection {

	private static final String APPLICATOIN_DATA = "applicationData";

	Logger logger = Logger.getLogger(Connection.class.getName());
	private SecurityProtocol securityProtocol = null;
	private HID clientHID = null;
	private HID serverHID = null;

	public Connection(HID clientHID, HID serverHID){
		if(clientHID == null || serverHID == null){
			throw new IllegalArgumentException("Cannot set null for required fields.");
		}
		this.clientHID = clientHID;
		this.serverHID = serverHID;
	}

	public HID getClientHID(){
		return clientHID;
	}

	public HID getServerHID(){
		return serverHID;
	}

	/**
	 * Set {@link SecurityProtocol} for this connection. It has
	 * to be ensured that no message is processed
	 * until no security protocol is set for connection.
	 * @param secProt Connection specific object
	 */
	protected void setSecurityProtocol(SecurityProtocol secProt){
		securityProtocol = secProt;
	}

	/**
	 * Creates a Message object for received data
	 * @param data Data received over network
	 * @return Message object for further processing
	 */
	public Message processData(HID senderHID, HID receiverHID, byte[] data){
		Message msg = new Message("", senderHID, receiverHID, data);

		if(securityProtocol.isInitialized()){
			//if protocol is initialized than open message with it
			try{
				msg = securityProtocol.unprotectMessage(msg);
			}catch(Exception e){
				logger.debug("Cannot unprotect message from HID: " + senderHID.toString());
			}
			try{
				//open data and divide it into properties of the message and application data
				Properties properties = new Properties();
				properties.loadFromXML(new ByteArrayInputStream(msg.getData()));

				//read the application data field from the message and add it as the data field
				msg.setData(((String)properties.remove(APPLICATOIN_DATA)).getBytes());
				//go through the properties and add them to the message
				Iterator<Object> i = properties.keySet().iterator();
				while(i.hasNext()){
					String key = (String)i.next();
					msg.setProperty(key, properties.getProperty(key));
				}
				return msg;
			}catch(Exception e){
				logger.debug("Cannot parse message from HID: " + senderHID.toString());
			}
			return null;
		} else {
			//if protocol not initialized then pass it for processing
			try {
				msg = securityProtocol.processMessage(msg);
				return msg;
			} catch (CryptoException e) {
				logger.warn("Error during cryptographic operation",e);
			} catch (VerificationFailureException e) {
				logger.debug("Signature is not valid from HID: " + senderHID.toString());
			} catch (IOException e) {
				logger.debug("Cannot parse message from HID: " + senderHID.toString());
			}
			return null;
		}	
	}

	/**
	 * Creates a serialized representation of the
	 * Message object which can be sent over network
	 * @param msg Message to convert
	 * @return Serialized version of the message including all properties
	 */
	public byte[] processMessage(Message msg){
		//read the properties of the message and put them into one properties object
		Properties props = new Properties();
		Iterator<String> i = msg.getKeySet().iterator();
		while(i.hasNext()){
			String key = i.next();
			props.put(key, msg.getProperty(key));
		}
		//put application data into properties
		props.put(APPLICATOIN_DATA, msg.getData());

		//convert props into xml and encode it
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] serializedCommand = null;
		try {
			props.storeToXML(bos, null);
			serializedCommand = bos.toByteArray();
		} catch (IOException e) {
			logger.warn("Message to be sent cannot be parsed!");
		} finally{
			try {
				bos.close();
			} catch (IOException e) {
				logger.error("Error closing stream",e);
			}
		}

		return serializedCommand;
	}
}
