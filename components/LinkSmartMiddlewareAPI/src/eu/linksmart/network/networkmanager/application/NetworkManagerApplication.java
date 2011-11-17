package eu.linksmart.network.networkmanager.application;

import java.net.URL;
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
	 * @param attributes Attributes as description, PID etc
	 * @param url URL for executing SOAP Tunneling.
	 * @return HID instance.
	 * @throws RemoteException
	 */
	public HID createHID(Properties attributes, URL url) throws RemoteException;
	
	/**
	 * Note: Boolean instead of boolean for .NET compatibility
	 * @param hid for particular service.
	 * @return TRUE if operation succeeded and FALSE if not.
	 * @throws RemoteException
	 */
	Boolean removeHID(HID hid) throws RemoteException;

}
