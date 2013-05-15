package eu.linksmart.network.networkmanager.core;

import java.rmi.RemoteException;
import java.util.List;

import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.security.communication.SecurityProperty;

/*
 * Internal NetworkManager interface used by internal components as backbone router etc.
 */
public interface NetworkManagerCore extends NetworkManager {

	/**
	 * Broadcast a message to all other known LinkSmart nodes.
	 * @param message the Message to broadcast; receiver VirtualAddress will be ignored.
	 * @return 
	 * @throws RemoteException
	 */
	public NMResponse broadcastMessage(Message message);

	/**
	 * Send message from one LinkSmart node to another node.
	 * @param message
	 * @param synch
	 * @return
	 * @throws RemoteException
	 */
	public NMResponse sendMessage(Message message, boolean synch);

	
	/**
	 * Receive data from one LinkSmart node to another node synchronously.
	 * @return Includes the response to the message
	 */
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte [] data);
	
	/**
	 * Receive data from one LinkSmart node to another node asynchronously.
	 * @return Includes only status of delivery attempt
	 */
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte [] data);

	
	/**
	 * Adds an VirtualAddress of a remote service.  
	 * 
	 * @param senderVirtualAddress the VirtualAddress of the sender. Usually a remote NetworkManager
	 * @param remoteVirtualAddress the VirtualAddress of a remote service.
	 */
	public void addRemoteVirtualAddress(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress);

	/**
	 * Informs if the security properties of any VirtualAddress changed
	 * because of change in the Backbone.
	 * @param virtualAddressesToUpdate
	 * @param properties
	 */
	public void updateSecurityProperties(List<VirtualAddress> virtualAddressesToUpdate, List<SecurityProperty> properties);	
}
