package eu.linksmart.security.communication;

import java.util.concurrent.TimeoutException;

import eu.linksmart.network.Message;

/**
 * Represents a protocol which is used to protect
 * messages sent over a backbone. After getting a reference
 * to SecurityProtocol it has to be started
 */
public interface SecurityProtocol {

	/**
	 * Starts the initialization of the security
	 * protocol. This might involve handshake or
	 * key agreement protocols.
	 * 
	 * @throws CryptoException If cryptography specific exception occurred
	 * @return Message which has to be sent to other party; null if no message has to be sent
	 */
	Message startProtocol() throws CryptoException;
	
	/**
	 * Processes general messages (like handshake) of 
	 * the protocol. If the message is not identified 
	 * it is returned in its original form.
	 * 
	 * @param msg Message to be processed
	 * @throws CryptoException If cryptography specific exception occurred
	 * @return original Message object if it is not identified
	 */
	Message processMessage(Message msg) throws CryptoException;
	
	/**
	 * Protects the data part of the message with 
	 * the specific protocol. Adds all necessary
	 * meta-information for opening with 
	 * {@link unprotectMessage()}.
	 * 
	 * @param msg Message to be protected
	 * @throws Exception If message cannot be processed
	 * @return Message with protected content
	 */
	Message protectMessage(Message msg) throws Exception;
	
	/**
	 * Opens a protected message body and removes all
	 * security protocol specific meta-information from it.
	 * 
	 * @param msg Message to be opened
	 * @throws Exception If message cannot be processed
	 * @return Message with unprotected body
	 */
	Message unprotectMessage(Message msg) throws Exception;
}
