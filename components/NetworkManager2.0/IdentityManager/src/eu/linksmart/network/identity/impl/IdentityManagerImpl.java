package eu.linksmart.network.identity.impl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.BroadcastMessage;
import eu.linksmart.network.HID;
import eu.linksmart.network.HIDAttribute;
import eu.linksmart.network.HIDInfo;
import eu.linksmart.network.Message;
import eu.linksmart.network.MessageDistributor;
import eu.linksmart.network.MessageProcessor;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.identity.util.AttributeQueryParser;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.utils.ByteArrayCodec;
import eu.linksmart.utils.Part;
import eu.linksmart.utils.PartConverter;

@SuppressWarnings("deprecation")
public class IdentityManagerImpl implements IdentityManager, MessageProcessor {

	protected static String IDENTITY_MGR = IdentityManagerImpl.class
	.getSimpleName();
	protected static Long HID_KEEP_ALIVE_MS = new Long(5*60*1000);
	protected static Logger LOG = Logger.getLogger(IDENTITY_MGR);

	protected ConcurrentHashMap<HID, HIDInfo> localHIDs;
	protected ConcurrentHashMap<HID, HIDInfo> remoteHIDs;

	protected ConcurrentHashMap<HID, Long> hidLastUpdate;
	protected ConcurrentLinkedQueue<String> queue;

	protected NetworkManagerCore networkManagerCore;

	/*Thread to delete not updated hids*/
	protected Thread hidClearerThread;

	/* Thread that checks for updated in HIDList and sends respective broadcasts */
	protected Thread hidUpdaterThread;
	/* Time in milliseconds to wait between broadcasts */
	protected int broadcastSleepMillis = 1000;

	/* Thread that sends network manager advertisement broadcasts */
	protected Thread advertisingThread;
	/* Time in milliseconds to wait between advertisements */
	protected int advertisementSleepMillis = 60000;
	private boolean advertisingThreadRunning;
	private boolean hidUpdaterThreadRunning;
	private boolean hidClearerThreadRunning;

	protected void activate(ComponentContext context) {
		LOG.info("Starting " + IDENTITY_MGR);
		this.localHIDs = new ConcurrentHashMap<HID, HIDInfo>();
		this.remoteHIDs = new ConcurrentHashMap<HID, HIDInfo>();
		this.queue = new ConcurrentLinkedQueue<String>();
		this.hidLastUpdate = new ConcurrentHashMap<HID, Long>();

		LOG.info(IDENTITY_MGR + " started");
	}

	protected void deactivate(ComponentContext context) {
		LOG.info(IDENTITY_MGR + "stopped");
	}

	@Override
	public HIDInfo createHIDForAttributes(Part[] attributes) {
		HID hid = createUniqueHID();
		HIDInfo info = new HIDInfo(hid, attributes);
		addLocalHID(hid, info);
		LOG.debug("Created HID: " + info.toString());
		return info;
	}

	@Override
	public HIDInfo createHIDForDescription(String description) {
		Part[] attributes = { new Part(HIDAttribute.DESCRIPTION.name(), description)};
		return createHIDForAttributes(attributes);
	}

	protected HID createUniqueHID() {
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
		return new HashSet<HIDInfo>(remoteHIDs.values());
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

		Collection<HIDInfo> allDescriptions = new HashSet<HIDInfo>();
		allDescriptions.addAll(localHIDs.values());
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
					if ((oneDescription != null && !exactMatch && oneDescription.contains(toMatch[i]))
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
							oneDescription = hidInfo.getDescription();
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
					LOG.error("Unable to get HID for description: "
							+ description, e);
				}

			}
		}
		return new HashSet<HIDInfo>(allDescriptions);
	}

	@Override
	public Set<HIDInfo> getHIDsByAttributes(String query) {
		LinkedList<String> parsedQuery = AttributeQueryParser.parseQuery(query);
		/* Parse the query. */
		HashSet<HIDInfo> results = new HashSet<HIDInfo>();


		HashSet<Map.Entry<HID, HIDInfo>> allHIDs = new HashSet<Map.Entry<HID,HIDInfo>>();
		//search in local and remote HIDs
		allHIDs.addAll(localHIDs.entrySet());
		allHIDs.addAll(remoteHIDs.entrySet());
		Iterator<Map.Entry<HID, HIDInfo>> it = allHIDs.iterator();

		while (it.hasNext()) {
			Map.Entry<HID, HIDInfo> entry = it.next();
			Part[] attr = entry.getValue().getAttributes();
			if (attr != null) {
				if (AttributeQueryParser.checkAttributes(attr, parsedQuery)) {
					results.add(entry.getValue());
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
				toUpdate.setAttributes(PartConverter.fromProperties(attr));
				localHIDs.replace(hid, toUpdate);
				//careful, D always before A, because the NMs that listen to this will 
				//execute the actions in order, hence if A is after D, they will first update and then delete the just-entered HID.
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
	 * Looks for updates in the list of HIDs and transforms them into a
	 * {@link BroadcastMessage}
	 * 
	 * @return the update
	 */
	protected synchronized BroadcastMessage getHIDListUpdate() {
		String update = "";
		while (queue.peek() != null) {
			update = update + queue.poll() + " ";
		}
		if (update.equals("")) {
			update = " ";
		}
		BroadcastMessage updateMsg = null;
		try {
			updateMsg = new BroadcastMessage(
					Message.IDMANAGER_UPDATE_HID_LIST_TOPIC, networkManagerCore.getHID(),
					update.getBytes());
		} catch (RemoteException e) {
			// local invocation
		}
		return updateMsg;
	}

	@Override
	public Message processMessage(Message msg) {
		if (msg.getTopic().contentEquals(Message.IDMANAGER_UPDATE_HID_LIST_TOPIC)) {
			try {
				if (!msg.getSenderHID().equals(networkManagerCore.getHID())) {
					// this is not an echo of our own broadcast
					// otherwise we do not need to do anything with it
					// else it is a genuine update
					String updates = new String(msg.getData()); 
					for (String oneUpdate : updates.split(" ")) {
						String[] updateData = oneUpdate.split(";");
						// at this point updateData 0 is operation type A/D, [1] is
						// hid, [2] is description (only if operation=A)
						if (updateData[0].equals("A")) {
							HID newHID = new HID(updateData[1]);
							HIDInfo newInfo = new HIDInfo(newHID, updateData[2]);
							// Add the remoteHID to the internal map of remote HIDs
							addRemoteHID(newHID, newInfo);
							// Add the backbone route for this remote HID
							networkManagerCore.addRemoteHID(msg.getSenderHID(),newHID);
						} else if (updateData[0].equals("D")) {
							HID toRemoveHID = new HID(updateData[1]);
							removeRemoteHID(toRemoveHID);
						} else {
							throw new IllegalArgumentException(
									"Unexpected update type for IDManager updates: " + updateData[0]);
						}
					}
				}
			} catch (RemoteException e) {
				// local invocation
			}
			return null; // the complete message has been processed
		} else if (msg.getTopic().contentEquals(Message.IDMANAGER_NMADVERTISMENT_TOPIC)){
			try {
				if (!msg.getSenderHID().equals(networkManagerCore.getHID())) {
					//check if we already know this network manager
					if(!getRemoteHIDs().contains(msg.getSenderHID())){
						//if we do not know it add HIDs reachable through it
						@SuppressWarnings("unchecked")
						Set<HIDInfo> hidInfos = (Set<HIDInfo>) ByteArrayCodec.decodeByteArrayToObject(msg.getData());
						if (hidInfos != null) {
							Iterator<HIDInfo> i = hidInfos.iterator();
							while (i.hasNext()) {
								HIDInfo oneHIDInfo = i.next();
								addRemoteHID(oneHIDInfo.getHid(), oneHIDInfo);
								// Add the backbone route for this remote HID
								networkManagerCore.addRemoteHID(msg.getSenderHID(), oneHIDInfo.getHid());
							}
						}
					}
				}
			} catch (RemoteException e) {
				LOG.debug("Remote Exception " + e);
			} catch (IOException e) {
				LOG.debug("IO Exception in communication, message maybe damaged? " + e);
			} catch (ClassNotFoundException e) {
				LOG.error("Class not found in reconstructing message. Why? " + e);
			}
			//message is processed
			return null;
		} else { // other message type
			// TODO
			return msg; // for the moment pass it on
		}
	}

	@Override
	public boolean removeHID(HID hid) {
		if (removeLocalHID(hid) != null || removeRemoteHID(hid) != null) {
			LOG.debug("Removed HID: " + hid.toString());
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
	protected HIDInfo addLocalHID(HID hid, HIDInfo info) {
		if (!localHIDs.containsKey(hid)) {
			localHIDs.put(hid, info);
			queue.add("A;" + hid.toString() + ";" + info.getDescription());
		}
		return localHIDs.get(hid);
	}

	/*
	 * Adds a remote HID to the IdTable and updates 
	 * the time stamp of last update
	 * 
	 * @param hid The HID to be added
	 * 
	 * @param info the HIDInfo
	 * 
	 * @return The previous value associated with that HID, null otherwise
	 */
	protected HIDInfo addRemoteHID(HID hid, HIDInfo info) {
		if (!remoteHIDs.containsKey(hid)) {
			remoteHIDs.put(hid, info);
			hidLastUpdate.put(hid, Calendar.getInstance().getTimeInMillis());
		} else {
			hidLastUpdate.put(hid, Calendar.getInstance().getTimeInMillis());
		}
		return remoteHIDs.get(hid);
	}

	/*
	 * Removes a local HID from the IdTable
	 * 
	 * @param hid The HID to be removed
	 */
	protected HIDInfo removeLocalHID(HID hid) {
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
	protected HIDInfo removeRemoteHID(HID hid) {
		hidLastUpdate.remove(hid);
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
	protected boolean existsDeviceID(long deviceID) {
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

	/*
	 * Thread sends broadcast message if there is an update in HIDList
	 */
	protected class HIDUpdaterThread implements Runnable {

		@Override
		public void run() {
			while (hidUpdaterThreadRunning) {
				if (!queue.isEmpty()) {
					BroadcastMessage m = getHIDListUpdate();
					LOG.debug("Broadcasting Message: " + m);
					networkManagerCore.broadcastMessage(m);
				}
				try {
					Thread.sleep(broadcastSleepMillis);
				} catch (InterruptedException e) {
					LOG.info("Thread broadcasting updates stopped!", e);
					hidUpdaterThreadRunning = false;
					break;
				}
			}
		}

	}

	/*
	 * Thread that broadcasts all localHIDs stored by this IdentityManager 
	 */
	protected class AdvertisingThread implements Runnable {

		@Override
		public void run() {
			while (advertisingThreadRunning) {
				if (networkManagerCore != null) {

					//#NM refactoring put list of local HIDs into message
					Set<HIDInfo> localHIDs = getLocalHIDs();
					byte[] localHIDbytes = null;

					try {
						localHIDbytes = ByteArrayCodec.encodeObjectToBytes(localHIDs);
					} catch (IOException e) {
						LOG.error("Cannot convert local HIDs set to bytearray; " + e);

					}
					BroadcastMessage m = null;

					try {
						m = new BroadcastMessage(
								Message.IDMANAGER_NMADVERTISMENT_TOPIC, networkManagerCore
								.getHID(), localHIDbytes);
					} catch (RemoteException e) {
						// local invocation
						LOG.debug("RemoteException: " + e);
					}
					networkManagerCore.broadcastMessage(m);
				}
				try {
					Thread.sleep(advertisementSleepMillis);
				} catch (InterruptedException e) {
					advertisingThreadRunning = false;
					LOG.error("Thread advertising NetworkManager stopped!",e);
					break;
				}
			}
		}

	}

	public void bindNetworkManagerCore(NetworkManagerCore networkManagerCore) {
		this.networkManagerCore = networkManagerCore;

		// Start the threads once NetworkManagerCore is available
		this.hidClearerThread = new Thread(new HIDClearer());
		hidClearerThread.start();
		hidClearerThreadRunning = true;

		hidUpdaterThread = new Thread(new HIDUpdaterThread());
		hidUpdaterThread.start();
		hidUpdaterThreadRunning = true;

		advertisingThread = new Thread(new AdvertisingThread());
		advertisingThread.start();
		advertisingThreadRunning = true;

		//subscribe to messages sent by other identity managers
		((MessageDistributor)this.networkManagerCore).subscribe(
				Message.IDMANAGER_NMADVERTISMENT_TOPIC, this);
		((MessageDistributor)this.networkManagerCore).subscribe(
				Message.IDMANAGER_UPDATE_HID_LIST_TOPIC, this);
	}

	public void unbindNetworkManagerCore(NetworkManagerCore networkManagerCore) {
		advertisingThreadRunning = false;
		hidUpdaterThreadRunning = false;
		hidClearerThreadRunning = false;
		this.networkManagerCore = null;

		//unsubscribe to messages sent by other identity managers
		((MessageDistributor)this.networkManagerCore).unsubscribe(
				Message.IDMANAGER_NMADVERTISMENT_TOPIC, this);
		((MessageDistributor)this.networkManagerCore).unsubscribe(
				Message.IDMANAGER_UPDATE_HID_LIST_TOPIC, this);
	}

	public String getIdentifier() {
		return IDENTITY_MGR;
	}

	protected class HIDClearer implements Runnable {
		public void run() {
			try {
			while(hidClearerThreadRunning) {
				Thread.sleep(advertisementSleepMillis);
				
				List<HID> toDelete = new ArrayList<HID>();
				//check the HIDs to be deleted
				for(HID hid : hidLastUpdate.keySet()) {
					if(hidLastUpdate.get(hid) + HID_KEEP_ALIVE_MS <
							Calendar.getInstance().getTimeInMillis()) {
						toDelete.add(hid);
					}
				}
				//delete the HIDs from the local id table and last update
				for(HID hid : toDelete) {
					if(networkManagerCore != null) {
						try {
						LOG.debug("Removing HID " + hid.toString() + 
								"as it was not updated recently.");
						networkManagerCore.removeHID(hid);
						} catch(RemoteException e) {
							//local access
						}
					}
				}
			}
			} catch(InterruptedException e) {
				LOG.error("Thread removing not advertised HIDs stopped!", e);
				hidClearerThreadRunning = false;
			}
		}
	}
}
