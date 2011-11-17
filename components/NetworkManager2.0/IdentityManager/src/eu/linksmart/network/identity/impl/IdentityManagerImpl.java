package eu.linksmart.network.identity.impl;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.HIDInfo;


/*
 * TODO #NM refactoring
 */
public class IdentityManagerImpl implements IdentityManager {
	private static String IDENTITY_MGR = IdentityManagerImpl.class.getSimpleName();
	
	private ConcurrentHashMap<HID, HIDInfo> localHIDs;
	private ConcurrentHashMap<HID, HIDInfo> remoteHIDs;

	private static Logger LOG = Logger.getLogger(IDENTITY_MGR);
	public ConcurrentLinkedQueue<String> queue;

	
	
	protected void activate(ComponentContext context) {
		this.localHIDs = new ConcurrentHashMap<HID, HIDInfo>();
		this.remoteHIDs = new ConcurrentHashMap<HID, HIDInfo>();
		this.queue = new ConcurrentLinkedQueue<String>();
		LOG.info(IDENTITY_MGR + "started");
	}

	protected void deactivate(ComponentContext context) {
		LOG.info(IDENTITY_MGR + "stopped");
	}

	@Override
	public HID createHID(Properties attributes) {
		// TODO Auto-generated method stub
		HID newHID = null; //TODO generate this HID!
		
		//idTable.put(newHID, attributes);
		return newHID;
	}

	@Override
	public Set<HID> getHIDs(Properties attributes) {
/*		// TODO Auto-generated method stub
		Set<HID> searchResults = null;
		
		for (HID hid : hidTable.keySet()) {
			/*TODO real check: hidTable.get(hid) matches attributes
			if (false ) {
				searchResults.add(hid);
			}
		}
		
		return searchResults;
*/      return null;
		}



//================

	private HID createUniqueHID() {
		HID hid;
		do {
			hid = new HID();
		} while (existsDeviceID(hid.getDeviceID()));
		return hid;
	}
	
	
	/**
	 * Creates an HID from a given contextID and adds it to idTable. If the HID 
	 * can't be created, it returns a null HID (0.0.0.0)
	 * 
	 * @param contextID The desired contextID
	 * @param level The desired level
	 * @param description the description
	 * @param endpoint the endpoint
	 * @param attr the properties associated to this HID
	 * @return The HID created or null HID (0.0.0.0) if it could not be created
	 */
	public HID createHID(long contextID, int level, String description,
			String endpoint, Properties attr) {
		
		Random rnd = new Random();
		HID hid = createUniqueHID();
		HIDInfo info = new HIDInfo(hid, description, endpoint, attr);
		long rndContext;
		switch (level) {
			case 1:
				hid.setContextID1(contextID);
				hid.setLevel(1);
				addLocalHID(hid, info);
				break;
			case 2:	
				rndContext = Math.abs(rnd.nextLong());
				while (rndContext == 0) {
					rndContext = Math.abs(rnd.nextLong());
				}
				hid.setContextID1(rndContext);
				hid.setContextID2(contextID);
				hid.setLevel(2);
				addLocalHID(hid, info);
				break;
			case 3:
				rndContext = Math.abs(rnd.nextLong());
				while (rndContext == 0) {
					rndContext = Math.abs(rnd.nextLong());
				}
				hid.setContextID1(rndContext);
				rndContext = Math.abs(rnd.nextLong());
				while (rndContext == 0) {
					rndContext = Math.abs(rnd.nextLong());
					}
				hid.setContextID2(rndContext);
				hid.setContextID3(contextID);
				hid.setLevel(3);
				addLocalHID(hid, info);
				break;
			default: 
				LOG.error("Invalid level when creating a new HID");
				hid.setDeviceID(0);
				hid.setLevel(0);
		}
		
		LOG.debug("Created HID: " + hid.toString());
		return hid;
	} 

	/**
	 * Creates an HID from a given contextID and adds it to idTable. If the HID 
	 * can't be created, it returns a null HID (0.0.0.0)
	 * 
	 * @param contextID The desired contextID
	 * @param level The desired level
	 * @return the HID created or null HID (0.0.0.0) if it could not be created
	 */
	public HID createHID(long contextID, int level) {
		return createHID(contextID, level, " ", " ", null);
	}
	
	/**
	 * Creates an HID without any context and adds it to idTable. It creates a 
	 * random deviceID. The result will be 0.0.0.randomDevID
	 * 
	 * @param description the description
	 * @param endpoint the endpoint
	 * @return the HID created
	 */
	public HID createHID(String description, String endpoint) {
		HID hid = createUniqueHID();
		HIDInfo info = new HIDInfo(hid, description, endpoint);
		addLocalHID(hid, info);
		LOG.debug("Created HID: " + hid.toString() + " for " + endpoint);		
		return hid;
	} 
	
	
	
	/**
	 * Creates an empty HID
	 * 
	 * @return the HID created or null HID (0.0.0.0) if it could not be created
	 */
	public HID createHID() {
		return createHID(" ", " ");
	}
	
	/**
	 * Creates an HID without any context and adds it to idTable. It creates a 
	 * random deviceID. The result will be 0.0.0.randomDevID
	 * 
	 * @param description the description
	 * @param endpoint the endpoint
	 * @param attr the properties associated to this HID
	 * @return The HID created or null HID (0.0.0.0) if it could not be created
	 */
	public HID createHID(String description, String endpoint, Properties attr) {
		HID hid = createUniqueHID();
		HIDInfo info = new HIDInfo(hid, description, endpoint, attr);
		addLocalHID(hid, info);
		LOG.debug("Created HID " + hid.toString() + " for " + endpoint);
		return hid;
	}	
	
	/**
	 * Adds a local HID to the IdTable
	 * 
	 * @param hid The HID to be added
	 * @param info the HIDInfo
	 * @return The previous value associated with that HID, null otherwise
	 */
	private  HIDInfo addLocalHID(HID hid, HIDInfo info) {
		if (!localHIDs.containsKey(hid)) {
			localHIDs.put(hid, info);
			queue.add("A;"+ hid.toString() + ";" + info.getDescription());
		}
		return localHIDs.get(hid);
	}
	
	/**
	 * Adds a remote HID to the IdTable
	 * 
	 * @param hid The HID to be added
	 * @param info the HIDInfo
	 * @return The previous value associated with that HID, null otherwise
	 */
	private  HIDInfo addRemoteHID(HID hid, HIDInfo info) {
		if (!remoteHIDs.containsKey(hid)) {
			remoteHIDs.put(hid, info);
		}
		return remoteHIDs.get(hid);
	}
	
	/**
	 * Removes a local HID from the IdTable
	 * 
	 * @param hid The HID to be removed
	 * @return the result
	 */
	private String removeLocalHID(HID hid) {
		queue.add("D;" + hid.toString());
		return localHIDs.remove(hid).toString();
	}	
	
	/**
	 * Removes a remote HID from the IdTable
	 * 
	 * @param hid The HID to be removed
	 * @return the result
	 */
	private String removeRemoteHID(HID hid) {
		return remoteHIDs.remove(hid).toString();
	}	
	
	/**
	 * Gets the endpoint address from an HID from idTable
	 * 
	 * @param hid The HID for getting the endpoint associated
	 * @return The endpoint address associated
	 */
	public HIDInfo getHIDInfo(HID hid) {
		HIDInfo answer = localHIDs.get(hid); 
		
		if (answer != null) {
			return answer;
		}
		//else, look into the remote list
		answer = remoteHIDs.get(hid);
		return answer;
	}


	
	/**
	 * Checks inside the idTable if the deviceID has already been assigned
	 * 
	 * @param deviceID The deviceID to be checked
	 * @return Returns true if deviceID has already been assigned. False otherwise.
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
	
	

	/**
	 * Gets the HIDs that match with a given description
	 * 
	 * @param description the description to search HIDs
	 * @return thd HIDs that match with this description
	 */
	public Set<HIDInfo> getHIDsByDescription(String description) {
	
		String[] toMatch;
		boolean exactMatch = false;
		
		if (description.contains("*")) { //the match means several strings to match, separated by *
			toMatch = description.split("\\*");
			exactMatch = false;
		} else {
			toMatch = new String[1];
			toMatch[0] = description;
			exactMatch = true;
		}
		
			/** 
			 * algorithm: assume all HIDInfo entries in our table will match
			 * then, 
			 * 		for every match string from the query (i.e. if query = ab*cd*ef then match strings are ab, cd, and ef
			 * 			verify all descriptions for which we still assume they will match
			 * 			if they indeed match, they survive to next match string verification
			 * 			if they do not match, then at least one criterion of the matchstring is not satisfied, hence that HIDInfo entry 
			 * 				will never be in the final set, so we can just remove it from our set under consideration
			 *			this should be an optimization to the (each criterion x each HIDInfo entry) approach
			 */
			
			
		Collection<HIDInfo> allDescriptions = localHIDs.values();
		allDescriptions.addAll(remoteHIDs.values()); //because we are searching in ALL hids, local and remote
		
		String oneDescription;
		for (int i = 0; i < toMatch.length; i++) { //when having an exact Match, length=1, so it will only be executed once, which is the desired behavior
			for (Iterator<HIDInfo> it = allDescriptions.iterator(); it.hasNext();) {
				HIDInfo hidInfo = it.next();
				try {
					oneDescription = hidInfo.getDescription();
					if ( (!exactMatch && oneDescription.contains(toMatch[i]))
						||
						 (exactMatch && oneDescription.equals(toMatch[i])))
						{ //the match criteria is satisfied
						//just pass to next round, this HIDInfo entry survives
						continue;
					} else {
						//this HIDInfo is already a candidate to be thrown out of further consideration
						//but let's do a last check on CryptoHID just in case it matches
						if (hidInfo.getAttributes() != null) {
							oneDescription = hidInfo.getAttributes().getProperty(HIDAttribute.DESCRIPTION.name());
							if (oneDescription != null) {
								if( (!exactMatch && oneDescription.contains(toMatch[i]))
									||
									(exactMatch && oneDescription.equals(toMatch[i]))
									) { //finally we have a match
									//this HIDInfo entry is saved to the next round
									continue;
								}
							}
						}
						//this is like a common ELSE block for all above if-s
						//the HIDInfo entry has not survived, has not matched at least one of the query string match criteria
						//so there's no need to check it in further iterations against other match criteria
						//so let's just remove it now
						it.remove();
					}
				} catch (Exception e) {
					LOG.error("Error in getHIDsbyDescription() " + e.getMessage()); 
				}
				
			}
		}
	
		return new HashSet<HIDInfo>(allDescriptions);
	}

	/**
	 * Gets HIDs update
	 * 
	 * @return the update
	 */
	private String getHIDsUpdate() {
		String update = "";
		while (queue.peek() != null) {
			update = update + queue.poll() + " ";
		}
		if (update.equals("")) {
			update = " ";
		}
		return update;
	}

	/**
	 * Gets "max" host HIDs by attributes
	 * 
	 * @param query the attributes
	 * @param maxNum the max number of host HIDs returned, or 0 for all
	 * @return the host HIDs
	 */
	public Set<HIDInfo> getHIDsByAttributes(String query, int maxNum) {
		LinkedList<String> parsedQuery = AttributeQueryParser.parseQuery(query);
		/* Parse the query. */
		HashSet<HIDInfo> results = new HashSet<HIDInfo>();
		
		Set<Map.Entry<HID, HIDInfo>> allHIDs = localHIDs.entrySet();
		allHIDs.addAll(remoteHIDs.entrySet()); //because we are checking ALL hids
		Iterator<Map.Entry<HID, HIDInfo>> it = allHIDs.iterator();
		
		while (it.hasNext()) {
			Map.Entry<HID, HIDInfo> entry = it.next();
			Properties attr = entry.getValue().getAttributes();
			if (attr != null) {
				if (AttributeQueryParser.checkAttributes(attr, parsedQuery)) {
					results.add(entry.getValue());
					//TODO
					//careful, maxNum=0 means return all
					if ((results.size() == maxNum) && (maxNum> 0)) {
						return results;
					}
				}
			}
		}
		return results;
	}
	



}
