package eu.linksmart.network.backbone.data;

import eu.linksmart.network.VirtualAddress;

/**
 * Service interface of endpoints receiving opaque data.
 * 
 * @author pullmann
 * 
 */
public interface DataEndpoint {

	VirtualAddress getVirtualAddress();

	void receive(byte[] data);

}
