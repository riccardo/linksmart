package eu.linksmart.network;

/**
 * Holds references of {@link MessageObserver} objects
 * and passes them received {@link Message} of topic
 * their subscribed to. 
 * @author Vinkovits
 *
 */
public interface MessageProvider {

	/**
	 * Method to subscribe to {@link Message} of specific topic
	 * @param topic String name of topic to listen to
	 */
	void subscribe(String topic);
}
