package eu.linksmart.security.communication;

/**
 * Returns implementation specific security protocol
 * @author Vinkovits
 *
 */
public interface CommunicationSecurityManager {

	/**
	 * Provides a specific {@link SecurityProtocol} object
	 * for protecting a connection between to entities.
	 * 
	 * @return Object to use to protect messages belonging to one connection
	 */
	SecurityProtocol getSecurityProtocol();
}
