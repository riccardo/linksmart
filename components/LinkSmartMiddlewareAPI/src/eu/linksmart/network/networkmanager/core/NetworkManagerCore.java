package eu.linksmart.network.networkmanager.core;

import java.io.IOException;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.networkmanager.NetworkManager;

/*
 * Internal NetworkManager interface used by internal components as backbone router etc.
 */
public interface NetworkManagerCore extends NetworkManager {

	/**
	 * Broadcast a message to all other known LinkSmart nodes.
	 * @param message the Message to broadcast; receiver HID will be ignored.
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
	 * Receive data from one LinkSmart node to another node.
	 */
	public NMResponse receiveData(HID senderHID, HID receiverHID, byte [] data);

//	/**
//	 * Creates an HID based on byte array data
//	 */
//	public HID createHID(byte [] data) throws IOException;
	
	/**
	 * Adds an HID of a remote service.  
	 * 
	 * @param senderHID the HID of the sender. Usually a remote NetworkManager
	 * @param remoteHID the HID of a remote service.
	 */
	public void addRemoteHID(HID senderHID, HID remoteHID);

}
