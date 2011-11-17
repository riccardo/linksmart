package eu.linksmart.network.connection;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.security.communication.SecurityProtocol;

/**
 * Holds properties and objects relevant for a connection
 * between two HIDs.
 * @author Vinkovits
 *
 */
public class Connection {
	
	SecurityProtocol securityProtocol = null;
	HID clientHID = null;
	HID serverHID = null;
	
	public Connection(HID clientHID, HID serverHID){
		if(clientHID == null || serverHID == null){
			throw new NullPointerException("Cannot set null for required fields.");
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
		//TODO #NM refactoring
		return new Message(null, null, null, null);
	}
	
	/**
	 * Creates a serialized representation of the
	 * Message object which can be sent over network
	 * @param msg Message to convert
	 * @return Serialized version of the message including all properties
	 */
	public byte[] processMessage(Message msg){
		//TODO #NM refactoring
		return new byte[]{};
	}
}
