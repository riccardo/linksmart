package eu.linksmart.network.connection;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;

/**
 * A connection which is a broadcast connection but does nothing with the packages.
 * @author Vinkovits
 *
 */
public class NOPBroadcastConnection extends BroadcastConnection {
	public NOPBroadcastConnection(HID clientHID) {
		super(clientHID);
	}

	public byte[] processMessage(Message msg) throws Exception{
		return msg.getData();
	}
	
	public Message processData(HID senderHID, HID receiverHID, byte[] data){
		return new Message(Message.TOPIC_APPLICATION, senderHID, receiverHID, data);
	}
}
