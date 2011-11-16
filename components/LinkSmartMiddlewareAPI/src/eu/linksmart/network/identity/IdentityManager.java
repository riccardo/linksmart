package eu.linksmart.network.identity;

import java.util.Properties;
import java.util.Set;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDInfo;


/*
 * TODO #NM refactoring
 */
public interface IdentityManager {
	
	public enum HIDAttribute {
		PID, SID, DESCRIPTION
	}
		
	public HID createHID(Properties attributes);
	
	public Set<HID> getHIDs(Properties attributes);
	
	public Set<HIDInfo> getHIDsByAttributes(String query, int maxNum);
	
	public Set<HIDInfo> getHIDsByDescription(String description);
	
	public Set<HIDInfo> getAllHIDs();
	
	public  HIDInfo getHIDInfo(HID hid);
}
