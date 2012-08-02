package eu.linksmart.network.networkmanager;

import java.rmi.RemoteException;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDInfo;
import eu.linksmart.network.NMResponse;
import eu.linksmart.utils.Part;

/*
 * External Network Manager interface intended to be used by LinkSmart application developers.
 */
public interface NetworkManager extends java.rmi.Remote {
	
	/**
	 * Send data from one LinkSmart node to another node.
	 */
	public NMResponse sendData(HID sender, HID receiver, byte [] data, boolean synch)	throws RemoteException;
		
	/**
	 * Retrieves HID of NetworkManagerCore.
	 * @return
	 */
	public HID getHID() throws RemoteException;
	
	/**
	 * Creates HID for particular service.
	 * @param attributes Attributes as description, PID etc
	 * @param endpoint Backbone specific endpoint, e.g. URL or JXTA id
	 * @param backboneName Class name of the Backbone this service is reachable
	 * @return HID instance.
	 * @throws RemoteException
	 */
	public HIDInfo createHID(Part[] attributes, String endpoint, String backboneName)
	throws RemoteException;
	
	/**
	 * @param hid for particular service.
	 * @return TRUE if operation succeeded and FALSE if not.
	 * @throws RemoteException
	 */
	boolean removeHID(HID hid) throws RemoteException;
	
	
	/**
	 * Operation to create an crypto HID providing the persistent attributes for
	 * this HID. The crypto HID is the enhanced version of HIDs, that allow
	 * to store persistent information on them (through certificates) and
	 * doesn't propagate the information stored on it. In order to exchange the
	 * stored information, the Session Domain Protocol is used. It returns a
	 * certificate reference that point to the certificate generated. The next
	 * time the HID needs to be created, using the same attributes, the
	 * certificate reference can be used.
	 * 
	 * @deprecated The implementation independent createHID method should be used
	 * @param xmlAttributes
	 *            The attributes (persistent) associated with this HID. This
	 *            attributes are stored inside the certificate and follow the
	 *            Java {@link java.util.Properties} xml schema.
	 * @return A {@link eu.linksmart.network.ws.CrypyoHIDResult} containing
	 *         {@link String} representation of the HID and the certificate
	 *         reference (UUID)
	 */
	@Deprecated
	public HIDInfo createCryptoHID(String xmlAttributes, String endpoint) throws RemoteException;
	
	/**
	 * Operation to create an CryptoHID providing a certificate reference (from
	 * a previously created CryptoHID). The CryptoHID is the
	 * enhanced version of HIDs, that allow to store persistent information on
	 * them (through certificates)
	 * 
	 * @deprecated The more general createHID method should be used
	 * @param certRef
	 *            The certificate reference from a previously generated
	 *            cryptoHID.
	 * @return The {@link String} representation of the HID.
	 */
	@Deprecated
	public HIDInfo createCryptoHIDFromReference(String certRef, String endpoint) throws RemoteException;
	
	/**
	 * To control what communication channels or backbones the
	 * NetworkManager supports this method provided the list of
	 * names of them. This information can be used by a service
	 * to decide which channel to register over.
	 * @return Class names of the connected Backbones
	 */
	public String[] getAvailableBackbones() throws RemoteException;
	
	/**
	 * Gets the HID for the available services with the given attributes. 
	 * Attributes are connected via conjunction.
	 * @param attributes The attributes the service is supposed to have
	 * @return The HIDs in HIDInfo objects
	 */
	public HIDInfo[] getHIDByAttributes(Part[] attributes);
	
	/**
	 * Gets the HID for the available services with the given attributes. 
	 * @param attributes The attributes the service is supposed to have
	 * @param isConjunction if true the attributes are connected with a logical "AND", 
	 * 			if false attributes are connected with a logical "OR"
	 * @return The HIDs in HIDInfo objects
	 */
	public HIDInfo[] getHIDByAttributes(Part[] attributes, boolean isConjunction);
	
	/**
	 * Gets the HID for the available service with the passed PID.
	 * @param PID The persistent identifier of the service.
	 * @return The HIDInfo obect.
	 */
	public HIDInfo getHIDByPID(String PID);
	
	/**
	 * Gets the HIDInfo for the available service(s) with the description.
	 * @param description The relevant service description.
	 * @return The HIDInfo object.
	 */
	public HIDInfo [] getHIDByDescription(String description);
}
