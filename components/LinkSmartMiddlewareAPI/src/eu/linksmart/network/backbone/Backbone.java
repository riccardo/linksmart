package eu.linksmart.network.backbone;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.security.communication.SecurityProperty;

/*
 * A Backbone needs to be implemented if a new communication channel (e.g. P2P, JMS, ...) should be used by LinkSmart.
 */
public interface Backbone {

	/**
	 * Sends a message over the specific communication channel and blocks until response comes.
	 * 
	 * @param senderHID HID of the sender
	 * @param receiverHID HID of the receiver
	 * @param data data to be sent
	 * @return Response of the receiver
	 */
	public NMResponse sendDataSynch(HID senderHID, HID receiverHID, byte[] data);

	/**
	 * Sends a message over the specific communication channel and immediately returns.
	 * 
	 * @param senderHID HID of the sender
	 * @param receiverHID HID of the receiver
	 * @param data data to be sent
	 * @return Response of the receiver
	 */
	public NMResponse sendDataAsynch(HID senderHID, HID receiverHID, byte[] data);
	
	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @return includes response to message
	 */
	public NMResponse receiveDataSynch(HID senderHID, HID receiverHID, byte[] data);
	
	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @return includes status of sending attempt
	 */
	public NMResponse receiveDataAsynch(HID senderHID, HID receiverHID, byte[] data);

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
	
	public String getName();
	
	
	/**
	 * returns security types available by using this backbone implementation. 
	 * The security types are configured via the LS configuration interface.
	 * See resources/BBJXTA.properties for details on configuration
	 * @return a list of security types available
	 */
	public List<SecurityProperty> getSecurityTypesAvailable();
	
	/**
	 * 
	 * @param senderHID the HID of the network manager.
	 * @param remoteHID the HID of the service that is connected to the remote network manager.
	 * set the endpoint of remote HIDs that are sent during the backbone advertisement.
	 * this is needed since the HIDs are packed in the message of the advertisement.
	 */
	public void addEndpointForRemoteHID(HID senderHID, HID remoteHID);
}
