package eu.linksmart.network.networkmanager.core;

import java.rmi.RemoteException;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.networkmanager.application.NetworkManagerApplication;

/*
 * Internal NetworkManager interface used by internal components as backbone router etc.
 */
public interface NetworkManagerCore extends NetworkManagerApplication{
	
	/**
	 * Receive data from one LinkSmart node to another node.
	 */
	public NMResponse receiveData(HID sender, HID receiver, byte [] data)	throws RemoteException;

	/**
	 * Creates an HID based on byte array data
	 */
	public HID createHID(byte [] data)	throws RemoteException;
	

}
