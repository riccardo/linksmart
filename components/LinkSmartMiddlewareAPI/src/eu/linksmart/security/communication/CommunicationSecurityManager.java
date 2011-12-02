package eu.linksmart.security.communication;

import eu.linksmart.network.HID;

/**
 * Returns implementation specific security protocol
 * @author Vinkovits
 *
 */
public interface CommunicationSecurityManager {

	public static String SECURITY_PROTOCOL_TOPIC = "SecurityHandshake";
	
	/**
	 * Provides a specific {@link SecurityProtocol} object
	 * for protecting a connection between to entities.
	 * 
	 * @param The HID which started the communication
	 * @param The HID whose service is used
	 * @return Object to use to protect messages belonging to one connection
	 */
	SecurityProtocol getSecurityProtocol(HID clientHID, HID serverHID);
	
	/**
	 * Provides whether this security protocol implementation
	 * can protect broadcast messages
	 * @return True if broadcast methods can be called
	 */
	boolean canBroadcast();
	
	/**
	 * Provides a specific {@link SecurityProtocol} object
	 * for protecting a broadcast connection.
	 * 
	 * @param The HID which started the communication
	 * @return Object to use to protect messages belonging to one connection
	 */
	SecurityProtocol getBroadcastSecurityProtocol(HID clientHID);
}
