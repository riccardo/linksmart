package eu.linksmart.network.routing;

import java.io.ObjectInputStream.GetField;
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
	public NMResponse sendData(HID senderHID, HID receiverHID,
			byte[] protectedData);

	/**
	 * Receives a message which also specifies the communication channel used by
	 * the sender. This will then update the list of HIDs and which backbone
	 * they use.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @param originatingBackbone
	 */
	public NMResponse receiveData(HID senderHID, HID receiverHID, byte[] data,
			Backbone originatingBackbone);

	/**
	 * this method is invoked by backbone when a service requests a new HID to
	 * the network manager. this msg will be firstly accepted by backbone, and
	 * then propagated to the NMCore and IdManager
	 * 
	 * @param tempId
	 * @param receiverHID
	 * @param data
	 * @param originatingBackbone
	 * @return
	 */
	// public NMResponse createHid(HID tempId, HID receiverHID, byte[] data,
	// Backbone originatingBackbone);

	/**
	 * Adds a new route to the BackboneRouter.
	 * 
	 * @param hid
	 *            the HID of which the route is added
	 * @param backbone
	 *            the Backbone through which the HID can be reached
	 * @return whether adding the route was successful
	 * @see {@link getAvailableCommunicationChannels()}
	 */
	public boolean addRoute(HID hid, String backbone);

	/**
	 * Adds a new route to the BackboneRouter. In addition, the endpoint is
	 * propagated to the Backbone.
	 * 
	 * @param hid
	 *            the HID of which the route is added
	 * @param backbone
	 *            the Backbone through which the HID can be reached
	 * @param endpoint
	 * @return
	 */
	public boolean addRouteToBackbone(HID hid, String backbone,
			String endpoint);

	public boolean removeRoute(HID hid);

	/**
	 * Returns a list of backbones available to the network manager.
	 * 
	 * @return list of backbone names (IDs)
	 */
	public List<String> getAvailableBackbones();

	/**
	 * this method is invoked by NMCore to broadcast HIDs.
	 * 
	 * @param sender
	 * @param data
	 * @return
	 */
	public NMResponse broadcastData(HID sender, byte[] data);

	/**
	 * 
	 * @param updates
	 */
	public void applyConfigurations(Hashtable updates);

	/**
	 * @param hid
	 * @return "BackboneType:BackboneAddresse"
	 */
	public String getRoute(HID hid);

}
