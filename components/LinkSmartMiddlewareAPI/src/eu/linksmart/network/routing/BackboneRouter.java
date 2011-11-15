package eu.linksmart.network.routing;

import java.util.Hashtable;
import java.util.List;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;

/*
 * The BackboneRouter is responsible for selecting the correct channel to send the message according to the receiverHID.
 * 
 * A list of HIDs is maintained that stores the communication channel. This list will always be updated which communication 
 * channel was used the last time for a specific HID. Even if a HID supports multiple communication channels the one will 
 * be used that this HID sent data.
 * 
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
	public NMResponse sendData(HID senderHID, HID receiverHID, String protectedData);
	
	/**
	 * Receives a message which also specifies the communication channel used by
	 * the sender. This will then update the list of HIDs and which backbone
	 * they use.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 * @param backboneType
	 */
	public NMResponse receiveData(HID senderHID, HID receiverHID, String protectedData, String backboneType);

	/**
	 * Returns a list of communication channels available to the network
	 * manager.
	 * 
	 * @return list of communication channels
	 */
	public List<String> getAvailableCommunicationChannels();

	public void applyConfigurations(Hashtable updates);

}
