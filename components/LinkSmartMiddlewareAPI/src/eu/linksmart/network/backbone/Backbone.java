package eu.linksmart.network.backbone;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;

/*
 * A Backbone needs to be implemented if a new communication channel (e.g. P2P, JMS, ...) should be used by LinkSmart.
 */
public interface Backbone {
	
	
	/**
	 * Sends a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 * @return
	 */
	public NMResponse sendData(HID senderHID, HID receiverHID, byte[] message);
	

	
	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 * @return
	 */
	public NMResponse receiveData(HID senderHID, HID receiverHID, byte[] message);

	
}
