package eu.linksmart.network.service.registry;

import java.net.URL;

import eu.linksmart.network.HID;

public interface ServiceRegistry {
	
	public boolean registerService(HID hid, URL url);
	
	public boolean removeService(HID hid);
	
	public boolean updateServiceHID(URL url);
	
	public URL getServiceURL(HID hid);

}
