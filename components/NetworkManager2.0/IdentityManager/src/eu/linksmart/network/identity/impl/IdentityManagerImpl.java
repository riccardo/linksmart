package eu.linksmart.network.identity.impl;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.identity.IdentityManager;


/*
 * TODO #NM refactoring
 */
public class IdentityManagerImpl implements IdentityManager {
	private static String IDENTITY_MGR = IdentityManagerImpl.class
			.getSimpleName();
	
	private ConcurrentHashMap<HID, Properties> hidTable;
	private Logger LOG = Logger.getLogger(IdentityManagerImpl.class); 

	
	
	protected void activate(ComponentContext context) {
		LOG.info(IDENTITY_MGR + "started");
	}

	protected void deactivate(ComponentContext context) {
		LOG.info(IDENTITY_MGR + "stopped");
	}

	@Override
	public HID createHID(Properties attributes) {
		// TODO Auto-generated method stub
		HID newHID = null; //TODO generate this HID!
		
		hidTable.put(newHID, attributes);
		return newHID;
	}

	@Override
	public Set<HID> getHIDs(Properties attributes) {
		// TODO Auto-generated method stub
		Set<HID> searchResults = null;
		
		for (HID hid : hidTable.keySet()) {
			/*TODO real check: hidTable.get(hid) matches attributes*/
			if (false ) {
				searchResults.add(hid);
			}
		}
		
		return searchResults;
	}
}