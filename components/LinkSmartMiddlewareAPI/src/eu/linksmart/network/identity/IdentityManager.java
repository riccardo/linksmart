package eu.linksmart.network.identity;

import java.util.Properties;
import java.util.Set;
import java.util.Vector;

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
	
	public Vector<String> getHIDsByAttributes(String query, int maxNum);
	
	public Vector<String> getHIDsByDescription(String description);
	
	public  HIDInfo getHIDInfo(HID hid);
}
