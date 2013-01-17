package eu.linksmart.network.client.converter;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.client.Registration;

public class RegistrationConverter {
	
	public static eu.linksmart.network.client.Registration toClient(eu.linksmart.network.Registration reg) {
		eu.linksmart.network.client.Registration clientReg = new Registration();
		eu.linksmart.network.client.VirtualAddress clientVa = 
			VirtualAddressConverter.toClient(reg.getVirtualAddress());
		clientReg.setVirtualAddress(clientVa);
		clientReg.setAttributes(reg.getAttributes());
		
		return clientReg;
	}
	
	public static eu.linksmart.network.Registration toApi(eu.linksmart.network.client.Registration reg) {
		eu.linksmart.network.VirtualAddress apiVa = 
			VirtualAddressConverter.toApi(reg.getVirtualAddress());
		
		eu.linksmart.network.Registration apiReg = 
			new eu.linksmart.network.Registration(apiVa, reg.getAttributes());
		
		return apiReg;
	}
}
