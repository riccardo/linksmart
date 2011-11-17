package eu.linksmart.network.identity;

import java.util.Properties;
import java.util.Set;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDInfo;

/**
 * The IdentityManager is responsible for creating and storing {@link HID}.
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
	 * Creates a local {@link HID} with the given attributes and stores it. It
	 * creates a random deviceID. The result will be 0.0.0.randomDeviceId.
	 * Attributes should be one of {@link HIDAttribute}.
	 * 
	 * @param attributes
	 *            Attributes describing the HID. E.g. description.
	 * @return the {@link HID} that has been created.
	 */
	public HID createHID(Properties attributes);

	/**
	 * Creates a local {@link HID} without any context and stores. It creates a
	 * random deviceID. The result will be 0.0.0.randomDeviceId.
	 * 
	 * @param description
	 *            the description
	 * @return the HID created
	 */
	public HID createHID(String description);

	/**
	 * Updates the attributes of the given {@link HID}.
	 * 
	 * @param hid
	 *            The HID for which the attributes should be changed.
	 * @param attributes
	 *            New attributes that will replace the old ones.
	 * @return true, if successful
	 */
	public boolean updateHIDInfo(HID hid, Properties attributes);

	// public Set<HID> getHIDs(Properties attributes);
	// public HID createHID(long contextID, int level, String description,
	// String endpoint, Properties attr);
	// public HID createHID(long contextID, int level);

	// public HID createHID();
	// public HID createHID(String description, String endpoint, Properties
	// attr);

	/**
	 * Returns the {@link HIDInfo} for a given {@link HID}.
	 * 
	 * @return the {@link HIDInfo}
	 */
	public HIDInfo getHIDInfo(HID hid);

	/**
	 * Returns all local and remote {@link HIDInfo}s that are stored by this
	 * IdentityManager.
	 * 
	 * @return all {@link HIDInfo}s
	 */
	public Set<HIDInfo> getAllHIDs();

	/**
	 * Returns all local {@link HIDInfo}s that are currently stored by this
	 * IdentityManager. Local are all {@link HIDInfo}s that are registered at
	 * the associated NetworkManager.
	 * 
	 * @return all local {@link HIDInfo}s.
	 */
	public Set<HIDInfo> getLocalHIDs();

	/**
	 * Returns all remote {@link HIDInfo}s that are currently stored by this
	 * IdentityManager. Remote are all {@link HIDInfo}s that are registered at
	 * remote NetworkManagers.
	 * 
	 * @return all remote {@link HIDInfo}s.
	 */
	public Set<HIDInfo> getRemoteHIDs();

	/**
	 * Return all {@link HIDInfo}s that match the given description. Wildcards
	 * can be used. TODO Explain the use of wildcards
	 * 
	 * @param description
	 * @return
	 */
	public Set<HIDInfo> getHIDsByDescription(String description);

	/**
	 * Returns all {@link HIDInfo}s that match the given query. TODO Explian the
	 * query language
	 * 
	 * @param query
	 * @param maxNum
	 * @return
	 */
	public Set<HIDInfo> getHIDsByAttributes(String query, int maxNum);

	/**
	 * Removes the given {@link HID} from the internal HID-store. Does nothing
	 * if the hid is not stored by this IdentityManager
	 * 
	 * @param hid
	 * @return true, if {@link HID} has been removed successfully.
	 */
	public boolean removeHID(HID hid);
}
