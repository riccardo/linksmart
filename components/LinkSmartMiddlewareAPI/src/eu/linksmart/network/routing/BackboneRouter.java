package eu.linksmart.network.routing;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.security.communication.SecurityProperty;

/**
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
	public NMResponse sendDataSynch(HID senderHID, HID receiverHID,
			byte[] protectedData);
	
	/**
	 * This method checks by which channel the receiver is reachable and sends
	 * the message.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 */
	public NMResponse sendDataAsynch(HID senderHID, HID receiverHID,
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
	public NMResponse receiveDataAsynch(HID senderHID, HID receiverHID, byte[] data,
			Backbone originatingBackbone);

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
	public NMResponse receiveDataSynch(HID senderHID, HID receiverHID, byte[] data,
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
	 * if) a route to the HID does not exist yet. If the backbone is unavailable a potential route is registered, 
	 * which becomes active as soon as the indicated backbone could be bound. 
	 * 
	 * @param hid
	 *            the HID of which the route is added
	 * @param backbone
	 *            the Backbone through which the HID can be reached
	 * @return whether adding the route was successful (at the moment it is only possible to have one route for an HID.
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
	 *            If null the route is removed independent from the backbone
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
	 * update the backbone configuration
	 * @param backbone configuration from the status page.
	 */
	public void applyConfigurations(Hashtable updates);
	
	/**
	 * returns list of security properties available via a given backbone
	 * necessary because we do not know what backbones we have, and there is no point in creating backbones on the fly to ask them
	 * about the security types they provide
	 * @param backbone A string with the (class)name of the backbone we are interested in. This must be loaded already
	 * @return a list of security parameters configured for that backbone. See the backbone's parameters file and/or the configuration interface for more details
	 */
	public List<SecurityProperty> getBackboneSecurityProperties(String backbone);

	/**
	 * this method is needed by the network manager status page 
	 * @param hid
	 * @return "BackboneType:BackboneAddress"
	 */
	public String getRoute(HID hid);

	Map<HID, Backbone> getCopyOfActiveRouteMap();

	Map<HID, List<RouteEntry>> getCopyOfPotentialRouteMap();
	
}
