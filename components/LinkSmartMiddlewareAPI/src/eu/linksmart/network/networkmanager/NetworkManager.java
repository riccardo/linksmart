package eu.linksmart.network.networkmanager;

import java.rmi.RemoteException;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.Registration;
import eu.linksmart.network.NMResponse;
import eu.linksmart.utils.Part;


/**
 * Provides the means to register and find services in the LinkSmart network using the services' attributes.
 * Can also be used for direct communication between two LinkSmart nodes.
 */
 // External Network Manager interface intended to be used by LinkSmart application developers.
public interface NetworkManager extends java.rmi.Remote {
	
	/**
	 * 
	 * Send data from one LinkSmart node to another node.
	 * @param sender The virtual address of the sender
	 * @param receiver The virtual address of the receiver
	 * @param data The data to be sent
	 * @param synch boolean indicating whether method call should be synchronous or asynchronous
	 * @return Response instance
	 * @throws RemoteException
	 */
	public NMResponse sendData(VirtualAddress sender, VirtualAddress receiver, byte [] data, boolean synch)	
			throws RemoteException;
		
	/**
	 * @deprecated getVirtualAddress() should be used instead.
	 * @return
	 */
	@Deprecated
	public VirtualAddress getService() throws RemoteException;
	
	/**
	 * Retrieves VirtualAddress of NetworkManagerCore instance.
	 * @throws RemoteException
	 * @return
	 */
	public VirtualAddress getVirtualAddress() throws RemoteException;
	
	/**
	 * Creates a VirtualAddress for a particular service.
	 * @param attributes Attributes such as PID (which should be unique) or description
	 * @param endpoint Backbone specific endpoint for the service, e.g. URL or JXTA id
	 * @param backboneName Class name of the Backbone from which this service is reachable
	 * @return VirtualAddress instance.
	 * @throws RemoteException
	 */
	public Registration registerService(Part[] attributes, String endpoint, String backboneName)
	throws RemoteException;
	
	/**
	 * Removes a particular service from internal memory
	 * @param virtualAddress for particular service.
	 * @return TRUE if operation succeeded and FALSE if not.
	 * @throws RemoteException
	 */
	boolean removeService(VirtualAddress virtualAddress) throws RemoteException;
	
	
	/**
	 * Provides the list of names of communication channels or backbones the 
	 * NetworkManager supports. This information can be used by a service to 
	 * decide which channel to register over.
	 * @return Class names of the connected Backbones
	 */
	public String[] getAvailableBackbones() throws RemoteException;
	
	/**
	 * Simplest method to get services which match given attributes. If one or more 
	 * services with these particular attributes are found, an array containing 
	 * the registrations of services is returned. If a service does not contain all 
	 * attributes which were used for the search (e.g. the service does not contain 
	 * an attribute "description"), but the values of the other required attributes 
	 * match, the service will be returned. If it has all attributes, but not all values 
	 * of these attributes match the required values, the service is not returned.
	 * Method will wait default timeout to discover a remote set of registrations.
	 * @param attributes The attributes the service is supposed to have
	 * @return The services as registration objects, containing virtual addresses 
	 * and attributes
	 * @throws RemoteException
	 */
	public Registration[] getServiceByAttributes(Part[] attributes) throws RemoteException;
	
	/**
	 * Method to get services with given attributes. Requires additional parameters to 
	 * control in detail how to search and what services to search for. 
	 * @param attributes The attributes the service should have
	 * @param timeOut Time to wait for discovery responses
	 * @param returnFirst If true, method will stop searching when one service is found. 
	 * If more than one service is found at the same time, the other services will be 
	 * returned as well.
	 * @param isStrictRequest <br/>
	 * true - only services will be discovered which possess all attributes <br/>
	 * false - attribute types which a service does not have are ignored as long there is at 
	 * least one matching attribute
	 * @return The services with matching attributes as registration objects. Even if 
	 * returnFirst is set true more than one registration of services may be available.
	 * @throws RemoteException 
	 */
	public Registration[] getServiceByAttributes(
			Part[] attributes,long timeOut,
			boolean returnFirst, boolean isStrictRequest) throws RemoteException;
	
	/**
	 * Gets the VirtualAddress for the available service with a given PID.
	 * @param PID The persistent identifier of the service.
	 * @return 	The Registration object, 
	 * 			<code>null</code> if no VirtualAddress exists for the given PID.
	 * @throws RemoteException 
	 */
	public Registration getServiceByPID (String PID) throws IllegalArgumentException, RemoteException;
	
	/**
	 * Gets the Registration for the available service(s) with a given description.
	 * @param description The required service description.
	 * @return The services with matching descriptions as registration objects
	 * @throws RemoteException 
	 */
	public Registration [] getServiceByDescription(String description) throws RemoteException;
	
	/**
	 * Gets the registration objects for the locally available services matching 
	 * the passed query. Remote services cannot be tested against the query 
	 * containing other attributes than 'description'. 
	 * This method should only be used by advanced developers.
	 * @param query The formulated query.
	 * @return The matching services as registration objects
	 * @throws RemoteException 
	 */
	public Registration [] getServiceByQuery(String query) throws RemoteException;
}
