package eu.linksmart.network.backbone.data;

/**
 * Service interface of endpoint receiving opaque data.
 * 
 * @author pullmann
 * 
 */
public interface DataEndpoint {

	void receive(byte[] data);

}
