package eu.linksmart.network;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * An OO representation of a message received over
 * the network. This class is used for internal processing
 * by the modules of NetworkManagerCore.
 * @author Vinkovits
 *
 */
public class Message {

	private Properties properties = new Properties();
	private String topic = null;
	private byte[] data = null;
	private HID senderHID = null;
	private HID receiverHID = null;
	
	public Message(String topic, HID senderHID, HID receiverHID, byte[] data){
		if(StringUtils.isEmpty(topic) || senderHID == null || receiverHID == null || data == null || data.length == 0)
			throw new IllegalArgumentException("Message cannot have null for required fields");
		this.topic = topic;
		this.senderHID = senderHID;
		this.receiverHID = receiverHID;
		this.data = data;
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
	
	public String getTopic(){
		return this.topic;
	}
	
	public HID getSenderHID(){
		return senderHID;
	}
	
	public HID getReceiverHID(){
		return receiverHID;
	}
	
	public byte[] getData(){
		return data;
	}
}
