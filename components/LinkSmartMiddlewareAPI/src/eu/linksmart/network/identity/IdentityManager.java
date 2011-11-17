package eu.linksmart.network.identity;

import java.util.Properties;
import java.util.Set;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDInfo;

/**
 * The IdentityManager is responsible for creating and storing HIDs.
 * 
 * 
 */
public interface IdentityManager {

	/**
	 * Attributes describing an HID. 
	 *
	 */
	public enum HIDAttribute {
		PID, SID, DESCRIPTION
	}

	/**
	 * Creates an HID with the given attributes. Attributes should be one of
	 * {@link HIDAttribute}
	 * 
	 * @param attributes Attributes describing the HID. E.g. description.
	 * @return
	 */
	public HID createHID(Properties attributes);

	public boolean updateHIDInfo(HID hid, Properties attr);

	// public Set<HID> getHIDs(Properties attributes);

	// public HID createHID(long contextID, int level, String description,
	// String endpoint, Properties attr);
	// public HID createHID(long contextID, int level);
	// public HID createHID(String description, String endpoint);
	// public HID createHID();
	// public HID createHID(String description, String endpoint, Properties
	// attr);
	public HIDInfo getHIDInfo(HID hid);

	public Set<HIDInfo> getAllHIDs();

	public Set<HIDInfo> getLocalHIDs();

	public Set<HIDInfo> getRemoteHIDs();

	public Set<HIDInfo> getHIDsByDescription(String description);

	public Set<HIDInfo> getHIDsByAttributes(String query, int maxNum);
	
	public boolean removeHID(HID hid);
}
