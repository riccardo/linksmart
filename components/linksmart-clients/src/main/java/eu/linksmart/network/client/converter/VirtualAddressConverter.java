package eu.linksmart.network.client.converter;

import eu.linksmart.network.VirtualAddress;

public class VirtualAddressConverter {

	public static eu.linksmart.network.VirtualAddress toApi(eu.linksmart.network.client.VirtualAddress va) {
		eu.linksmart.network.VirtualAddress vaApi = new VirtualAddress();
		vaApi.setContextID1(va.getContextID1());
		vaApi.setContextID2(va.getContextID2());
		vaApi.setContextID3(va.getContextID3());
		vaApi.setDeviceID(va.getDeviceID());
		
		return vaApi;
	}
	
	public static eu.linksmart.network.client.VirtualAddress toClient(eu.linksmart.network.VirtualAddress va) {
		eu.linksmart.network.client.VirtualAddress vaClient = new eu.linksmart.network.client.VirtualAddress();
		vaClient.setContextID1(va.getContextID1());
		vaClient.setContextID2(va.getContextID2());
		vaClient.setContextID3(va.getContextID3());
		vaClient.setDeviceID(va.getDeviceID());
		
		return vaClient;
	}
}
