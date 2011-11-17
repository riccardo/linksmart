package eu.linksmart.network.identity;

import java.util.Properties;
import java.util.Set;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDInfo;
import eu.linksmart.network.Message;


/*
 * TODO #NM refactoring
 */
public interface IdentityManager {
	
	public enum HIDAttribute {
		PID, SID, DESCRIPTION
	}
		
	public HID createHID(Properties attributes);
	public boolean updateHIDInfo(HID hid, Properties attr);
	public Set<HID> getHIDs(Properties attributes);
	
	//public HID createHID(long contextID, int level, String description, String endpoint, Properties attr);
	//public HID createHID(long contextID, int level);
	//public HID createHID(String description, String endpoint);
	//public HID createHID();
	//public HID createHID(String description, String endpoint, Properties attr);
	public HIDInfo getHIDInfo(HID hid);
	public Set<HIDInfo> getAllHIDs();
	public Set<HIDInfo> getLocalHIDs();
	public Set<HIDInfo> getRemoteHIDs();
	public Set<HIDInfo> getHIDsByDescription(String description);
	public Set<HIDInfo> getHIDsByAttributes(String query, int maxNum);
}
