package eu.linksmart.network.backbone;

import java.util.Hashtable;

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
	 * @param data
	 * @return
	 */
	public NMResponse sendData(HID senderHID, HID receiverHID, byte[] data);

	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @return
	 */
	public NMResponse receiveData(HID senderHID, HID receiverHID, byte[] data);

	/**
	 * Broadcasts a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param data
	 * @return
	 */
	public NMResponse broadcastData(HID senderHID, byte[] data);

	/**
	 * Return the destination address as string that will be used for display
	 * purposes.
	 * 
	 * @param hid
	 * @return the backbone address represented by the Hid
	 */
	public String getEndpoint(HID hid);

	/**
	 * Adds a new endpoint to the backbone.
	 * 
	 * @param hid
	 *            the HID that represents the endpoint
	 * @param endpoint
	 *            the endpoint to be reached, in a format that is specific to
	 *            the Backbone implementation, as a String
	 * @return whether adding the endpoint was successful
	 */
	public boolean addEndpoint(HID hid, String endpoint);

	/**
	 * Removes an endpoint from the backbone
	 * @param hid the HID of which the endpoint should be removed
	 * @return whether the endpoint was removed
	 */
	public boolean removeEndpoint(HID hid);
	
	/**
	 * 
	 * @param updates
	 */
	public void applyConfigurations(Hashtable updates);

}
