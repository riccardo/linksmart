package eu.linksmart.network;

/**
 * Receives and sends messages of specific topics
 * @author Vinkovits
 *
 */
public interface MessageObserver {

	/**
	 * {@link Message} of topic this MessageObserver subscribed to.
	 * @param msg Message to process
	 * @return processed message
	 */
	Message processMessage(Message msg);
}
