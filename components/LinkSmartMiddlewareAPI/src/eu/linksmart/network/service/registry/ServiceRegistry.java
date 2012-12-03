package eu.linksmart.network.service.registry;

import java.net.URL;

import eu.linksmart.network.VirtualAddress;

public interface ServiceRegistry {
	
	public boolean registerService(VirtualAddress virtualAddress, URL url);
	
	public boolean removeService(VirtualAddress virtualAddress);
	
	public boolean updateServiceVirtualAddress(URL url);
	
	public URL getServiceURL(VirtualAddress virtualAddress);

}
