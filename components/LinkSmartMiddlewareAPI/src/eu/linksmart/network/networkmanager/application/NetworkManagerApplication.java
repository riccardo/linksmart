package eu.linksmart.network.networkmanager.application;

import java.rmi.RemoteException;
import java.util.Properties;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;

/*
 * TODO #NM refactoring
 */
public interface NetworkManagerApplication {
	/* external methods */
	/*TODO separate interfaces */
	
	
	/**
	 * Send data from one LinkSmart node to another node.
	 */
	public NMResponse sendData(HID sender, HID receiver, byte [] data)	throws RemoteException;
	
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
	
	/**
	 * Creates HID for particular service.
	 * @param attribute Attributes as description, PID etc
	 * @throws RemoteException
	 */
	public void createHID(Properties attributes) throws RemoteException;

}
