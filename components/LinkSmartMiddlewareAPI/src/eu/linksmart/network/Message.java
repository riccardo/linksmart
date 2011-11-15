package eu.linksmart.network;

import java.util.Properties;

/**
 * An OO representation of a message received over
 * the network. This class is used for internal processing
 * by the modules of NetworkManager.
 * @author Vinkovits
 *
 */
public class Message {

	private Properties properties = new Properties();
	private String topic = null;
	
	public Message(String topic){
		this.topic = topic;
	}
	
	/**
	 * Sets a property of the message which will be included in
	 * the data to be sent.
	 * @param key Key to the property
	 * @param value Value of the property in a 
	 * serialized way in which it can be sent over network
	 * @return The previous value of the property or null if there was none
	 */
	public String setProperty(String key, String value){
		return (String)properties.setProperty(key, value);
	}
	
	/**
	 * Returns actual value of property.
	 * @param key Key of the property
	 * @return Value of the property
	 */
	public String getProperty(String key){
		return properties.getProperty(key);
	}
	
	/**
	 * 
	 * @return Topic of this message
	 */
	public String getTopic(){
		return this.topic;
	}
}
