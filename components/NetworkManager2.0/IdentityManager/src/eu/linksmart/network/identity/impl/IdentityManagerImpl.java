package eu.linksmart.network.identity.impl;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.BroadcastMessage;
import eu.linksmart.network.HID;
import eu.linksmart.network.HIDAttribute;
import eu.linksmart.network.HIDInfo;
import eu.linksmart.network.Message;
import eu.linksmart.network.MessageProcessor;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;

public class IdentityManagerImpl implements IdentityManager, MessageProcessor {

	private static String IDENTITY_MGR = IdentityManagerImpl.class
			.getSimpleName();
	private final static String IDMANAGER_UPDATE_HID_LIST_TOPIC = "IDManagerHIDListUpdate";

	private static Logger LOG = Logger.getLogger(IDENTITY_MGR);

	private ConcurrentHashMap<HID, HIDInfo> localHIDs;
	private ConcurrentHashMap<HID, HIDInfo> remoteHIDs;

	private ConcurrentLinkedQueue<String> queue;

	private NetworkManagerCore networkManagerCore;
	// Time in milliseconds to wait between broadcasts
	private int broadcastSleepMillis = 1000;

	protected void activate(ComponentContext context) {
		LOG.info("Starting " + IDENTITY_MGR);
		this.localHIDs = new ConcurrentHashMap<HID, HIDInfo>();
		this.remoteHIDs = new ConcurrentHashMap<HID, HIDInfo>();
		this.queue = new ConcurrentLinkedQueue<String>();

		Thread broadcastingThread = new Thread(new HIDUpdaterThread());
		broadcastingThread.start();
		LOG.info(IDENTITY_MGR + " started");
	}

	protected void deactivate(ComponentContext context) {
		LOG.info(IDENTITY_MGR + "stopped");
	}

	@Override
	public HID createHID(Properties attributes) {
		// TODO check also for other properties!
		// StringUtils.join(attributes.values().toArray(), "|");
		String description = attributes.getProperty(HIDAttribute.DESCRIPTION
				.name());
		return createHID(description);
	}

	@Override
	public HID createHID(String description) {
		HID hid = createUniqueHID();
		HIDInfo info = new HIDInfo(hid, description);
		addLocalHID(hid, info);
		LOG.debug("Created HID: " + hid.toString());
		return hid;
	}

	private HID createUniqueHID() {
		HID hid;
		do {
			hid = new HID();
		} while (existsDeviceID(hid.getDeviceID()));
		return hid;
	}

	@Override
	public HIDInfo getHIDInfo(HID hid) {
		HIDInfo answer = localHIDs.get(hid);

		if (answer != null) {
			return answer;
		}
		// else, look into the remote list
		answer = remoteHIDs.get(hid);
		return answer;
	}

	/**
	 * Returns a vector containing all the HID inside local+remote Table
	 * 
	 * @return a vector containing all the HIDs inside local + remote idTable
	 */
	public Set<HIDInfo> getAllHIDs() {
		Set<HIDInfo> union = getLocalHIDs();
		union.addAll(getRemoteHIDs());
		return union;
	}

	/**
	 * Returns a vector containing all the local HID inside localHIDs Table
	 * 
	 * @return a vector containing all the HIDs inside idTable
	 */
	public Set<HIDInfo> getLocalHIDs() {
		return new HashSet<HIDInfo>(localHIDs.values());
	}

	/**
	 * Returns a vector containing all the remote HID inside idTable
	 * 
	 * @return a vector containing all the remote HIDs inside idTable
	 */
	public Set<HIDInfo> getRemoteHIDs() {
		return new HashSet<HIDInfo>(localHIDs.values());
	}

	@Override
	public Set<HIDInfo> getHIDsByDescription(String description) {

		String[] toMatch;
		boolean exactMatch = false;

		if (description.contains("*")) { // the match means several strings to
			// match, separated by *
			toMatch = description.split("\\*");
			exactMatch = false;
		} else {
			toMatch = new String[1];
			toMatch[0] = description;
			exactMatch = true;
		}

		/**
		 * algorithm: assume all HIDInfo entries in our table will match then,
		 * for every match string from the query (i.e. if query = ab*cd*ef then
		 * match strings are ab, cd, and ef verify all descriptions for which we
		 * still assume they will match if they indeed match, they survive to
		 * next match string verification if they do not match, then at least
		 * one criterion of the matchstring is not satisfied, hence that HIDInfo
		 * entry will never be in the final set, so we can just remove it from
		 * our set under consideration this should be an optimization to the
		 * (each criterion x each HIDInfo entry) approach
		 */

		Collection<HIDInfo> allDescriptions = localHIDs.values();
		allDescriptions.addAll(remoteHIDs.values()); // because we are searching
		// in ALL hids, local
		// and remote

		String oneDescription;
		for (int i = 0; i < toMatch.length; i++) { // when having an exact
			// Match, length=1, so it
			// will only be executed
			// once, which is the
			// desired behavior
			for (Iterator<HIDInfo> it = allDescriptions.iterator(); it
					.hasNext();) {
				HIDInfo hidInfo = it.next();
				try {
					oneDescription = hidInfo.getDescription();
					if ((!exactMatch && oneDescription.contains(toMatch[i]))
							|| (exactMatch && oneDescription.equals(toMatch[i]))) { // the
						// match
						// criteria
						// is
						// satisfied
						// just pass to next round, this HIDInfo entry survives
						continue;
					} else {
						// this HIDInfo is already a candidate to be thrown out
						// of further consideration
						// but let's do a last check on CryptoHID just in case
						// it matches
						if (hidInfo.getAttributes() != null) {
							oneDescription = hidInfo.getAttributes()
									.getProperty(
											HIDAttribute.DESCRIPTION.name());
							if (oneDescription != null) {
								if ((!exactMatch && oneDescription
										.contains(toMatch[i]))
										|| (exactMatch && oneDescription
												.equals(toMatch[i]))) { // finally
									// we
									// have
									// a
									// match
									// this HIDInfo entry is saved to the next
									// round
									continue;
								}
							}
						}
						// this is like a common ELSE block for all above if-s
						// the HIDInfo entry has not survived, has not matched
						// at least one of the query string match criteria
						// so there's no need to check it in further iterations
						// against other match criteria
						// so let's just remove it now
						it.remove();
					}
				} catch (Exception e) {
					LOG.error("Error in getHIDsbyDescription() "
							+ e.getMessage());
				}

			}
		}
		return new HashSet<HIDInfo>(allDescriptions);
	}

	@Override
	public Set<HIDInfo> getHIDsByAttributes(String query, int maxNum) {
		LinkedList<String> parsedQuery = AttributeQueryParser.parseQuery(query);
		/* Parse the query. */
		HashSet<HIDInfo> results = new HashSet<HIDInfo>();

		Set<Map.Entry<HID, HIDInfo>> allHIDs = localHIDs.entrySet();
		allHIDs.addAll(remoteHIDs.entrySet()); // because we are checking ALL
		// hids
		Iterator<Map.Entry<HID, HIDInfo>> it = allHIDs.iterator();

		while (it.hasNext()) {
			Map.Entry<HID, HIDInfo> entry = it.next();
			Properties attr = entry.getValue().getAttributes();
			if (attr != null) {
				if (AttributeQueryParser.checkAttributes(attr, parsedQuery)) {
					results.add(entry.getValue());
					// TODO
					// careful, maxNum=0 means return all
					if ((results.size() == maxNum) && (maxNum > 0)) {
						return results;
					}
				}
			}
		}
		return results;
	}

	@Override
	public boolean updateHIDInfo(HID hid, Properties attr) {
		HIDInfo toUpdate = localHIDs.get(hid);
		if (toUpdate != null) {
			synchronized (queue) { // because we need to be sure that both
				// deletion of old and insertion of new
				// attributes are in the queue at the same
				// time
				toUpdate.setAttributes(attr);
				localHIDs.replace(hid, toUpdate);
				queue.add("D;" + hid.toString());
				queue.add("A;" + hid.toString() + ";"
						+ toUpdate.getDescription());
			}
			return true;
		} else {
			return false;
		}
	}

	// keep the following two methods near each other
	// because they refer to the same format
	/**
	 * Gets HIDs update
	 * 
	 * @return the update
	 */
	private synchronized BroadcastMessage getHIDListUpdate() {
		String update = "";
		while (queue.peek() != null) {
			update = update + queue.poll() + " ";
		}
		if (update.equals("")) {
			update = " ";
		}
		BroadcastMessage updateMsg = new BroadcastMessage(
				IDMANAGER_UPDATE_HID_LIST_TOPIC, networkManagerCore.getHID(),
				update.getBytes());
		return updateMsg;
	}

	@Override
	public Message processMessage(Message msg) {
		if (msg.getTopic() == IDMANAGER_UPDATE_HID_LIST_TOPIC) {
			if (msg.getSenderHID() != networkManagerCore.getHID()) {
				// this is not an echo of our own broadcast
				// otherwise we do not need to do anything with it
				// else it is a genuine update
				String updates = msg.getData().toString();
				for (String oneUpdate : updates.split(" ")) {
					String[] updateData = oneUpdate.split(";");
					// at this point updateData 0 is operation type A/D, [1] is
					// hid, [2] is description (only if operation=A)
					if (updateData[0] == "A") {
						HID newHID = new HID(updateData[1]);
						HIDInfo newInfo = new HIDInfo(newHID, updateData[1]);
						addRemoteHID(newHID, newInfo);
					} else if (updateData[0] == "D") {
						HID toRemoveHID = new HID(updateData[1]);
						removeRemoteHID(toRemoveHID);
					} else {
						throw new IllegalArgumentException(
								"Unexpected update type for IDManager updates");
					}
				}
			}
			return null; // the complete message has been processed
		} else { // other message type
			// TODO
			return msg; // for the moment pass it on
		}
	}

	@Override
	public boolean removeHID(HID hid) {
		if (removeLocalHID(hid) != null || removeRemoteHID(hid) != null) {
			return true;
		}
		return false;
	}

	/*
	 * Adds a local HID to the IdTable
	 * 
	 * @param hid The HID to be added
	 * 
	 * @param info the HIDInfo
	 * 
	 * @return The previous value associated with that HID, null otherwise
	 */
	private HIDInfo addLocalHID(HID hid, HIDInfo info) {
		if (!localHIDs.containsKey(hid)) {
			localHIDs.put(hid, info);
			queue.add("A;" + hid.toString() + ";" + info.getDescription());
		}
		return localHIDs.get(hid);
	}

	/*
	 * Adds a remote HID to the IdTable
	 * 
	 * @param hid The HID to be added
	 * 
	 * @param info the HIDInfo
	 * 
	 * @return The previous value associated with that HID, null otherwise
	 */
	private HIDInfo addRemoteHID(HID hid, HIDInfo info) {
		if (!remoteHIDs.containsKey(hid)) {
			remoteHIDs.put(hid, info);
		}
		return remoteHIDs.get(hid);
	}

	/*
	 * Removes a local HID from the IdTable
	 * 
	 * @param hid The HID to be removed
	 */
	private HIDInfo removeLocalHID(HID hid) {
		if (localHIDs.containsKey(hid)) {
			queue.add("D;" + hid.toString());
		}
		return localHIDs.remove(hid);
	}

	/*
	 * Removes a remote HID from the IdTable
	 * 
	 * @param hid The HID to be removed
	 * 
	 * @return the result, null if
	 */
	private HIDInfo removeRemoteHID(HID hid) {
		return remoteHIDs.remove(hid);
	}

	/*
	 * Checks inside the idTable if the deviceID has already been assigned
	 * 
	 * @param deviceID The deviceID to be checked
	 * 
	 * @return Returns true if deviceID has already been assigned. False
	 * otherwise.
	 */
	private boolean existsDeviceID(long deviceID) {
		boolean is = false;
		Enumeration<HID> hids;
		hids = localHIDs.keys();
		while (hids.hasMoreElements()) {
			if (hids.nextElement().getDeviceID() == deviceID) {
				is = true;
				LOG.debug("Duplicated deviceID " + deviceID + ". ");
				break;
			}
		}
		return is;
	}

	private class HIDUpdaterThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				if (!queue.isEmpty()) {
					BroadcastMessage m = getHIDListUpdate();
					LOG.debug("Broadcasting Message: " + m);
					networkManagerCore.broadcastMessage(m);
				}
				try {
					Thread.sleep(broadcastSleepMillis);
				} catch (InterruptedException e) {
					LOG.error("Error while waiting", e);
				}
			}

		}

	}

	public void bindNetworkManagerCore(NetworkManagerCore networkManagerCore) {
		this.networkManagerCore = networkManagerCore;
	}

	public void unbindNetworkManagerCore(NetworkManagerCore networkManagerCore) {
		this.networkManagerCore = null;
	}

}
