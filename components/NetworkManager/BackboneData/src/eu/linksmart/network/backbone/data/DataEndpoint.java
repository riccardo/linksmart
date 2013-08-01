package eu.linksmart.network.backbone.data;

import eu.linksmart.network.VirtualAddress;

/**
 * Service interface of endpoint receiving opaque data.
 * 
 * @author pullmann
 * 
 */
public interface DataEndpoint {

	//void receive(byte[] data);
	void receive(byte[] data, VirtualAddress from);

}
