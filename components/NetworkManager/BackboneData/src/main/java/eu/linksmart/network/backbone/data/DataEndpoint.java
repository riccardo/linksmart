package eu.linksmart.network.backbone.data;

import eu.linksmart.network.VirtualAddress;

/**
 * Service interface of endpoint receiving opaque data.
 * 
 * @author pullmann
 * 
 */
public interface DataEndpoint {

	// void receive(byte[] data);

	// Added sender's VirtualAddress to enable asynchronous response
	// void receive(byte[] data, VirtualAddress from);

	// Added response data
	/**
	 * Method for exchanging arbitrary binary data. It is expected to return
	 * immediately and not to block by contract. Depending on implementation it
	 * might return a concrete result of a synchronous call or a request ID to
	 * be correlated to an asynchronous response sent later via
	 * NetworkManager#send().
	 * 
	 * @param data
	 * @param from
	 * @return
	 */
	byte[] receive(byte[] data, VirtualAddress from);

}
