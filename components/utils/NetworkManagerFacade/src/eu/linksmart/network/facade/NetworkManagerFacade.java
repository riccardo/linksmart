package eu.linksmart.network.facade;

import java.rmi.RemoteException;

/**
 * This class provides convience methods for accessing the Network Manager.
 * 
 */
public interface NetworkManagerFacade {

	/**
	 * Registers the service for the given serviceID at the local Network
	 * Manager. An HID for this service will be created.
	 * 
	 * @param serviceID ID of the service to be registered. E.g. MyServiceInterface.class.getSimpleName()
	 * @return The HID under which the service can be accessed.
	 * @throws Exception
	 */
	public String createHIDForServiceID(String serviceID) throws RemoteException;
}
