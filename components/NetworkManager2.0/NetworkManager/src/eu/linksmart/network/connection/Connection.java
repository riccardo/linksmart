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
	
	/**
	 * Set {@link SecurityProtocol} for this connection. It has
	 * to be ensured that no message is processed
	 * until no security protocol is set for connection.
	 * @param secProt Connection specific object
	 */
	void setSecurityProtocol(SecurityProtocol secProt){
		securityProtocol = secProt;
	}
	
	/**
	 * Creates a Message object for received data
	 * @param data Data received over network
	 * @return Message object for further processing
	 */
	Message processData(byte[] data){
		//TODO #NM refactoring
		return new Message(null, null, null, null);
	}
	
	byte[] processMessage(Message msg){
		//TODO #NM refactoring
		return new byte[]{};
	}
}
