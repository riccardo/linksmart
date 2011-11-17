package eu.linksmart.network;

public class BroadcastMessage extends Message {
	public BroadcastMessage(String topic, HID senderHID, byte[] data){
		super(topic, senderHID, null, data);
	}
}
