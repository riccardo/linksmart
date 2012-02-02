package eu.linksmart.network.routing;

import java.io.ObjectInputStream.GetField;
import java.util.Hashtable;
import java.util.List;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.security.communication.SecurityProperty;

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
	 * Adds a new route to the BackboneRouter. This will succeed if (and only
	 * if) a route to the HID does not exist yet and there is a backbone with
	 * the given name.
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
	 * Adds the backbone route for a remote HID. Uses the backbone of the senderHID,
	 * which should be a remote NetworkManager. 
	 * 
	 * @param senderHID the HID of the sender. Usually a remote NetworkManager
	 * @param remoteHID the HID of a remote service.
	 */
	public void addRouteForRemoteHID(HID senderHID, HID remoteHID);

	/**
	 * Adds a new route to the BackboneRouter. In addition, the endpoint is
	 * propagated to the Backbone.
	 * 
	 * @param hid
	 *            the HID of which the route is added
	 * @param backbone
	 *            the Backbone through which the HID can be reached
	 * @param endpoint
	 * @return whether adding the route was successful
	 */
	public boolean addRouteToBackbone(HID hid, String backbone, String endpoint);

	/**
	 * Removes a route from the BackboneRouter, if the HID was reached through
	 * the given backbone
	 * 
	 * @param hid
	 *            The HID of which the route should be removed
	 * @param backbone
	 *            The name of the backbone through which the HID was reached
	 * @return whether removing the route was successful
	 */
	public boolean removeRoute(HID hid, String backbone);

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
	 * returns list of security properties available via a given backbone
	 * necessary because we do not know what backbones we have, and there is no point in creating backbones on the fly to ask them
	 * about the security types they provide
	 * @param backbone A string with the (class)name of the backbone we are interested in. This must be loaded already
	 * @return a list of security parameters configured for that backbone. See the backbone's parameters file and/or the configuraton interface for more details
	 */
	public List<SecurityProperty> getBackboneSecurityProperties(String backbone);

	/**
	 * @param hid
	 * @return "BackboneType:BackboneAddresse"
	 */
	public String getRoute(HID hid);
	
	public String getBackboneType(HID hid);
	

}
