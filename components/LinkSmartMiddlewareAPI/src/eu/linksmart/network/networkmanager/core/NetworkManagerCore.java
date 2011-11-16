package eu.linksmart.network.networkmanager.core;

import java.rmi.RemoteException;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;

/*
 * TODO #NM refactoring
 */
public interface NetworkManagerCore {
	/* external methods */
	/*TODO separate interfaces */
	
	/* internal methods */
	
	/**
	 * Send data from one LinkSmart node to another node.
	 */
	public NMResponse sendData(HID sender, HID receiver, byte [] data)	throws RemoteException;
	
	/**
	 * Receive data from one LinkSmart node to another node.
	 */
	public NMResponse receiveData(HID sender, HID receiver, byte [] data)	throws RemoteException;

	
	/**
	 * Send message from one LinkSmart node to another node.
	 * @param message
	 * @return
	 * @throws RemoteException
	 */
	public NMResponse sendMessage(Message message)	throws RemoteException;
	
	/**
	 * Retrieves HID of NetworkManagerCore.
	 * @return
	 */
	public HID getHID();

}
