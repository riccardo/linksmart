package eu.linksmart.network.routing;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.backbone.Backbone;

/*
 * The BackboneRouter is responsible for selecting the correct channel to send the message according to the receiverHID.
 */
public interface BackboneRouter {

	/**
	 * This method checks by which channel the receiver is reachable and sends
	 * the message.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 */
	public void sendData(HID senderHID, HID receiverHID, Message message);

}
