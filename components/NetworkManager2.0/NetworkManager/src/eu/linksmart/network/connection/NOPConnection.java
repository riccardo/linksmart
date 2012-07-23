package eu.linksmart.network.connection;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;

/**
 * {@link Connection} which does not encode provided messages or data.
 * @author Vinkovits
 *
 */
public class NOPConnection extends Connection {

	public NOPConnection(HID clientHID, HID serverHID) {
		super(clientHID, serverHID);
	}
	
	public byte[] processMessage(Message msg) throws Exception{
		return msg.getData();
	}
	
	public Message processData(HID senderHID, HID receiverHID, byte[] data){
		return new Message(Message.TOPIC_APPLICATION, senderHID, receiverHID, data);
	}

}
