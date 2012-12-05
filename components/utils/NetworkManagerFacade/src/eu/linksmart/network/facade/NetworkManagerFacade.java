package eu.linksmart.network.facade;

import java.rmi.RemoteException;

/**
 * This class provides convience methods for accessing the Network Manager.
 * 
 */
public interface NetworkManagerFacade {

	/**
	 * Register a Web Service at the NetworManager under the given serviceName.
	 * Note that the serviceName and interfaceName can be different. The
	 * serviceName is the name of the service in the LinkSmart network that can
	 * be searched for. The interface name must be the class.getSimpleName() of
	 * the actual Web Service interface.
	 * 
	 * @param serviceName
	 *            The name of service in the LinkSmart network
	 * @param interfaceName
	 *            The name of Web Service interface.
	 * @return the HID that has been created for this service
	 * 
	 * @throws RemoteException
	 */
	public String createHIDForService(String serviceName, String interfaceName)
			throws RemoteException;

	/**
	 * Deregisters a service from the NetworkManager. This means the service's
	 * HID will be removed, thus it can't be found anymore in the LinkSmart
	 * Network. The Web Service is still deployed.
	 * 
	 * @param hid
	 *            the HID of the service that should be removed from the
	 *            NetworkManager
	 * @throws RemoteException
	 */
	public void deregisterService(String hid) throws RemoteException;
}
