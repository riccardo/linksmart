package eu.linksmart.network.identity;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import eu.linksmart.network.HID;


/*
 * TODO #NM refactoring
 */
public interface IdentityManager {
	
	public enum HIDAttribute {
		PID, SID, DESCRIPTION
	}
		
	public HID createHID(Properties attributes);
	
	public Set<HID> getHIDs(Properties attributes);
}
