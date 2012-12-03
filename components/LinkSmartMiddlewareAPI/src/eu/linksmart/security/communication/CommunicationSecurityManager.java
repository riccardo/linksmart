package eu.linksmart.security.communication;

import java.util.List;

import eu.linksmart.network.VirtualAddress;

/**
 * Returns implementation specific security protocol.
 * @author Vinkovits
 *
 */
public interface CommunicationSecurityManager {

	public static String SECURITY_PROTOCOL_TOPIC = "SecurityHandshake";
	
	/**
	 * Provides a specific {@link SecurityProtocol} object
	 * for protecting a connection between to entities.
	 * 
	 * @param clientVirtualAddress The VirtualAddress which started the communication
	 * @param serverVirtualAddress The VirtualAddress whose service is used
	 * @return Object to use to protect messages belonging to one connection
	 */
	SecurityProtocol getSecurityProtocol(VirtualAddress clientVirtualAddress, VirtualAddress serverVirtualAddress);

	/**
	 * Provides whether this security protocol implementation
	 * can protect broadcast messages.
	 * @return True if broadcast methods can be called
	 */
	boolean canBroadcast();

	/**
	 * The properties of this object can be received to
	 * decide in which case to use its services.
	 * @return a list of {@link SecurityProperty} which are
	 * provided by this object.
	 */
	List<SecurityProperty> getProperties();

	/**
	 * Provides a specific {@link SecurityProtocol} object
	 * for protecting a broadcast connection.
	 * 
	 * @param clientVirtualAddress The VirtualAddress which started the communication
	 * @return Object to use to protect messages belonging to one connection
	 */
	SecurityProtocol getBroadcastSecurityProtocol(VirtualAddress clientVirtualAddress);
}
