package eu.linksmart.network.networkmanager;

import java.rmi.RemoteException;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;

/*
 * TODO #NM refactoring
 */
public interface NetworkManager {
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

	public HID getHID();

}
