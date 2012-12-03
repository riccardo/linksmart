package eu.linksmart.network.backbone;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import eu.linksmart.network.VirtualAddress;
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
	 * @param senderVirtualAddress VirtualAddress of the sender
	 * @param receiverVirtualAddress VirtualAddress of the receiver
	 * @param data data to be sent
	 * @return Response of the receiver
	 */
	public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data);

	/**
	 * Sends a message over the specific communication channel and immediately returns.
	 * 
	 * @param senderVirtualAddress VirtualAddress of the sender
	 * @param receiverVirtualAddress VirtualAddress of the receiver
	 * @param data data to be sent
	 * @return Response of the receiver
	 */
	public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data);
	
	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderVirtualAddress
	 * @param receiverVirtualAddress
	 * @param data
	 * @return includes response to message
	 */
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data);
	
	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderVirtualAddress
	 * @param receiverVirtualAddress
	 * @param data
	 * @return includes status of sending attempt
	 */
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data);

	/**
	 * Broadcasts a message over the specific communication channel.
	 * 
	 * @param senderVirtualAddress
	 * @param data
	 * @return
	 */
	public NMResponse broadcastData(VirtualAddress senderVirtualAddress, byte[] data);

	/**
	 * Return the destination address as string that will be used for display
	 * purposes.
	 * 
	 * @param virtualAddress
	 * @return the backbone address represented by the virtual address
	 */
	public String getEndpoint(VirtualAddress virtualAddress);

	/**
	 * Adds a new endpoint to the backbone.
	 * 
	 * @param virtualAddress
	 *            the VirtualAddress that represents the endpoint
	 * @param endpoint
	 *            the endpoint to be reached, in a format that is specific to
	 *            the Backbone implementation, as a String
	 * @return whether adding the endpoint was successful
	 */
	public boolean addEndpoint(VirtualAddress virtualAddress, String endpoint);

	/**
	 * Removes an endpoint from the backbone
	 * @param virtualAddress the VirtualAddress of which the endpoint should be removed
	 * @return whether the endpoint was removed
	 */
	public boolean removeEndpoint(VirtualAddress virtualAddress);
	
	/**
	 * used to apply configurations from the web page 
	 * @param updates
	 */
	public void applyConfigurations(Hashtable updates);
	
	/**
	 * 
	 * @return Backbone implementation's class name
	 */
	public String getName();
	
	
	/**
	 * Returns security types required by using this backbone implementation.
	 * The security types are configured via the LS configuration interface.
	 * See resources/BBJXTA.properties for details on configuration
	 * @return a list of security types required
	 */
	public List<SecurityProperty> getSecurityTypesRequired();
	
	/**
	 * 
	 * @param senderVirtualAddress the VirtualAddress of the network manager.
	 * @param remoteVirtualAddress the VirtualAddress of the service that is connected to the remote network manager.
	 * set the endpoint of remote services that are sent during the backbone advertisement.
	 * this is needed since the virtual addresses are packed in the message of the advertisement.
	 */
	public void addEndpointForRemoteService(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress);
}
