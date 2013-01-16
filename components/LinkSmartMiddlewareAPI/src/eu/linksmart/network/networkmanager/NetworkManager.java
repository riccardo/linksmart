package eu.linksmart.network.networkmanager;

import java.rmi.RemoteException;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.Registration;
import eu.linksmart.network.NMResponse;
import eu.linksmart.utils.Part;

/*
 * External Network Manager interface intended to be used by LinkSmart application developers.
 */
public interface NetworkManager extends java.rmi.Remote {
	
	/**
	 * Send data from one LinkSmart node to another node.
	 */
	public NMResponse sendData(VirtualAddress sender, VirtualAddress receiver, byte [] data, boolean synch)	throws RemoteException;
		
	/**
	 * Retrieves VirtualAddress of NetworkManagerCore.
	 * @return
	 */
	public VirtualAddress getService() throws RemoteException;
	
	/**
	 * Creates VirtualAddress for particular service.
	 * @param attributes Attributes as description, PID etc
	 * @param endpoint Backbone specific endpoint, e.g. URL or JXTA id
	 * @param backboneName Class name of the Backbone this service is reachable
	 * @return VirtualAddress instance.
	 * @throws RemoteException
	 */
	public Registration registerService(Part[] attributes, String endpoint, String backboneName)
	throws RemoteException;
	
	/**
	 * @param virtualAddress for particular service.
	 * @return TRUE if operation succeeded and FALSE if not.
	 * @throws RemoteException
	 */
	boolean removeService(VirtualAddress virtualAddress) throws RemoteException;
	
	
	/**
	 * To control what communication channels or backbones the
	 * NetworkManager supports this method provided the list of
	 * names of them. This information can be used by a service
	 * to decide which channel to register over.
	 * @return Class names of the connected Backbones
	 */
	public String[] getAvailableBackbones() throws RemoteException;
	
	/**
	 * Simplest method to get services which match attributes. An array containing registrations of services is 
	 * returned at best effort, meaning that if an entity does not
	 * include some of the searched attributes they are ignored. Will
	 * wait default timeout to discover a remote set of registrations.
	 * @param attributes The attributes the service is supposed to have
	 * @return The services in Registration objects
	 * @throws RemoteException 
	 */
	public Registration[] getServiceByAttributes(Part[] attributes) throws RemoteException;
	
	/**
	 * Method to exactly control gathering of services. 
	 * @param attributes The attributes the service should have
	 * @param timeOut Time to wait for discovery responses
	 * @param returnFirst If true method returns at first found service
	 * @param isStrictRequest <br/>
	 * true - only services will be discovered which possess all attributes <br/>
	 * false - attribute types which a service does not have are ignored
	 * @return Even if returnFirst is set true more registration of services may be available
	 * @throws RemoteException 
	 */
	public Registration[] getServiceByAttributes(
			Part[] attributes,long timeOut,
			boolean returnFirst, boolean isStrictRequest) throws RemoteException;
	
	/**
	 * Gets the VirtualAddress for the available service with the passed PID.
	 * @param PID The persistent identifier of the service.
	 * @return 	The Registration object, 
	 * 			<code>null</code> if no VirtualAddress exists for the given PID.
	 * @throws RemoteException 
	 */
	public Registration getServiceByPID (String PID) throws IllegalArgumentException, RemoteException;
	
	/**
	 * Gets the Registration for the available service(s) with the description.
	 * @param description The relevant service description.
	 * @return The Registration objects.
	 * @throws RemoteException 
	 */
	public Registration [] getServiceByDescription(String description) throws RemoteException;
	
	/**
	 * Gets the VirtualAddress for the locally available services for the passed query.
	 * Remote services cannot be tested against the query containing other
	 * attributes then description. 
	 * This method should only be used by advanced developers.
	 * @param query The formulated query.
	 * @return The Registration objects.
	 * @throws RemoteException 
	 */
	public Registration [] getServiceByQuery(String query) throws RemoteException;
}
