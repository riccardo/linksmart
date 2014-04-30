package eu.linksmart.network.client.converter;

import eu.linksmart.network.client.NMResponse;

public class NMResponseConverter {

	public static eu.linksmart.network.NMResponse toApi(eu.linksmart.network.client.NMResponse nmresp) {
		eu.linksmart.network.NMResponse apiResponse = new eu.linksmart.network.NMResponse(nmresp.getStatus());
		apiResponse.setMessage(nmresp.getMessage());
		
		return apiResponse;
	}
	
	public static eu.linksmart.network.client.NMResponse toClient(eu.linksmart.network.NMResponse nmresp){
		eu.linksmart.network.client.NMResponse clientResp = new NMResponse();
		clientResp.setStatus(nmresp.getStatus());
		clientResp.setMessage(nmresp.getMessage());
		
		return clientResp;		
	}
}
