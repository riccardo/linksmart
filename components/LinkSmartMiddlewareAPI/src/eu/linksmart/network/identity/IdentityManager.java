package eu.linksmart.network.identity;

import java.util.Map;

import eu.linksmart.network.HID;
import eu.linksmart.network.HID.HIDAttributes;


/*
 * TODO #NM refactoring
 */
public interface IdentityManager {
		
	public HID createHID(Map<HIDAttributes, String> attributes);
	
	public HID getHID(Map<HIDAttributes, String> attributes);
}
