package eu.linksmart.network.networkmanager;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDInfo;
import eu.linksmart.network.NMResponse;

/*
 * External Network Manager interface intended to be used by LinkSmart application developers.
 */
public interface NetworkManager {
	
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
	 * @param xmlAttributes
	 *            The attributes (persistent) associated with this HID. This
	 *            attributes are stored inside the certificate and follow the
	 *            Java {@link java.util.Properties} xml schema.
	 * @return A {@link eu.linksmart.network.ws.CrypyoHIDResult} containing
	 *         {@link String} representation of the HID and the certificate
	 *         reference (UUID)
	 */
	public HIDInfo createCryptoHID(String xmlAttributes);
	
	/**
	 * Operation to create an CryptoHID providing a certificate reference (from
	 * a previously created CryptoHID). The CryptoHID is the
	 * enhanced version of HIDs, that allow to store persistent information on
	 * them (through certificates)
	 * 
	 * @param certRef
	 *            The certificate reference from a previously generated
	 *            cryptoHID.
	 * @return The {@link String} representation of the HID.
	 */
	public HIDInfo createCryptoHIDFromReference(String certRef);

}
