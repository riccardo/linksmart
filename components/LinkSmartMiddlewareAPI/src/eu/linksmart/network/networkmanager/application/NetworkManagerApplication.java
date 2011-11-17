package eu.linksmart.network.networkmanager.application;

import java.rmi.RemoteException;
import java.util.Properties;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;

/*
 * External Network Manager interface intended to be used by LinkSmart application developers.
 */
public interface NetworkManagerApplication {
	
	/**
	 * Send data from one LinkSmart node to another node.
	 */
	public NMResponse sendData(HID sender, HID receiver, byte [] data)	throws RemoteException;
		
	/**
	 * Retrieves HID of NetworkManagerCore.
	 * @return
	 */
	public HID getHID();
	
	/**
	 * Creates HID for particular service.
	 * @param attribute Attributes as description, PID etc
	 * @return 
	 * @throws RemoteException
	 */
	public HID createHID(Properties attributes) throws RemoteException;

}
