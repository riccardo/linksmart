package eu.linksmart.network;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * An OO representation of a message received over the network. This class is
 * used for internal processing by the modules of NetworkManagerCore.
 * 
 * @author Vinkovits
 * 
 */
public class Message {

	private Properties properties = new Properties();
	private String topic = null;
	private byte[] data = null;
	private HID senderHID = null;
	private HID receiverHID = null;
	
	public final static String TOPIC_APPLICATION = "eu.linksmart.application";
	
	
	/**
	 * 
	 * @param topic the topic of this message
	 * @param senderHID HID of the sender of this message
	 * @param receiverHID HID of the receiver of this message. Can be null e.g. in the case of a {@link BroadcastMessage}
	 * @param data payload of the message. TODO Marco-2012-02-02: Can payload be empty? Like for advertisements
	 */
	public Message(String topic, HID senderHID, HID receiverHID, byte[] data) {
		if (StringUtils.isEmpty(topic) || senderHID == null || data == null)
			throw new IllegalArgumentException(
					"Message cannot have null for required fields");
		this.topic = topic;
		this.senderHID = senderHID;
		this.receiverHID = receiverHID;
		this.data = data;
	}

	/**
	 * Sets a property of the message which will be included in the data to be
	 * sent.
	 * 
	 * @param key
	 *            Key to the property
	 * @param value
	 *            Value of the property in a serialized way in which it can be
	 *            sent over network
	 * @return The previous value of the property or null if there was none
	 */
	public String setProperty(String key, String value) {
		return (String) properties.setProperty(key, value);
	}

	/**
	 * Returns actual value of property.
	 * 
	 * @param key
	 *            Key of the property
	 * @return Value of the property
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Returns the properties saved in the message
	 * 
	 * @return Set with the keys
	 */
	public Set<String> getKeySet() {
		Set<String> stringSet = new HashSet<String>();
		Iterator<Object> i = properties.keySet().iterator();
		while (i.hasNext()) {
			stringSet.add((String) i.next());
		}
		return stringSet;
	}

	public String getTopic() {
		return this.topic;
	}

	public HID getSenderHID() {
		return senderHID;
	}

	public HID getReceiverHID() {
		return receiverHID;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Topic: ").append(getTopic()).append(", Sender: ").append(
				getSenderHID()).append(", Receiver: ").append(getReceiverHID())
				.append(", Data: ").append(new String(data));
		return sb.toString();
	}
}
