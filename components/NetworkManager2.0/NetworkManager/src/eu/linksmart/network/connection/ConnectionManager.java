package eu.linksmart.network.connection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import eu.linksmart.network.HID;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProperty;

/**
 * Manages and creates connections between two HIDs.
 * @author Vinkovits
 *
 */
public class ConnectionManager {
	/**
	 * Stores the last usage of a connection.
	 */
	private HashMap<Connection, Date> timeouts = new HashMap<Connection, Date>();
	/**
	 * List of stored connections.
	 */
	private List<Connection> connections = new ArrayList<Connection>();
	/**
	 * Designated broadcast connection.
	 */
	private Connection broadcastConnection = null;
	/**
	 * Number of minutes a connection can at least live after last use.
	 */
	private int timeoutMinutes = 30;
	/**
	 * Timer called with period of timeout/2 to clear connections.
	 */
	private Timer timer = null;
	/**
	 * HIDs stored with a fix set of properties needed to be fulfilled by a connection to it.
	 */
	private HashMap<HID, List<SecurityProperty>> hidPolicies = new HashMap<HID, List<SecurityProperty>>();
	/**
	 * List of available managers to use for connection protection.
	 */
	private ArrayList<CommunicationSecurityManager> communicationSecurityManagers = 
		new ArrayList<CommunicationSecurityManager>();


	public ConnectionManager(){
		//Timer which periodically calls the clearer task
		timer = new Timer(true);
		timer.schedule(new ConnectionClearer(), 0, timeoutMinutes * 60 * 1000 / 2);
	}

	public void setCommunicationSecurityManager(CommunicationSecurityManager comSecMgr){
		this.communicationSecurityManagers.add(comSecMgr);
	}

	public void removeCommunicationSecurityManager(CommunicationSecurityManager comSecMgr){
		this.communicationSecurityManagers.remove(comSecMgr);
	}

	/**
	 * Saves this HID to always use a specific configuration.
	 * This includes for example type of security to use.
	 * @param regulatedHID 
	 */
	public void registerHIDPolicy(HID regulatedHID, List<SecurityProperty> properties){
		this.hidPolicies.put(regulatedHID, properties);
	}

	/**
	 * Removes the HID's policy. After this call the HID
	 * will always be handled with the default settings.
	 * @param regulatedHID
	 */
	public void deleteHIDPolicy(HID regulatedHID){
		this.hidPolicies.remove(regulatedHID);
	}

	/**
	 * Returns {@link Connection} object associated to two entities.
	 * Creates new if there exists none yet.
	 * @param receiverHID
	 * @param senderHID
	 * @return Connection to use for processing of communication
	 * @throws Exception If there are policies for an HID but no CommunicationSecurityManager
	 */
	public synchronized Connection getConnection(HID receiverHID, HID senderHID) throws Exception{
		Calendar cal = Calendar.getInstance();
		//find connection belonging to these HIDs
		for(Connection c : connections){
			if((c.getClientHID() == receiverHID && c.getServerHID() == senderHID)
					|| (c.getClientHID() == senderHID && c.getServerHID() == receiverHID)){
				//set last use date of connection
				timeouts.put(c, cal.getTime());
				return c;
			}
		}

		//there was no connection found so create new connection
		//check if there are policies for one of the HIDs
		ArrayList<SecurityProperty> allRequirements = new ArrayList<SecurityProperty>();
		if(this.hidPolicies.containsKey(senderHID) || this.hidPolicies.containsKey(receiverHID)){
			//add both policies to requirement list		
			List<SecurityProperty> senderPolicies = hidPolicies.get(senderHID);
			if(senderPolicies != null){
				//add all properties from this HID
				for(SecurityProperty prop : senderPolicies){
					allRequirements.add(prop);
				}
			}
			List<SecurityProperty> receiverPolicies = hidPolicies.get(receiverHID);
			if(receiverPolicies != null){
				//add only not already needed properties
				for(SecurityProperty prop : receiverPolicies){
					if(!allRequirements.contains(prop)){
						allRequirements.add(prop);
					}
				}
			}
			
			//check if requirements are not colliding
			boolean noEncoding = allRequirements.contains(SecurityProperty.NoEncoding);
			boolean noSecurity = allRequirements.contains(SecurityProperty.NoSecurity);
			boolean justNoEncNoSec = allRequirements.size() == 2 && noEncoding && noSecurity;
			//if there are more requirements and noEnc or noSec is included it must be colliding
			if (!justNoEncNoSec && (allRequirements.size() > 1 && (noEncoding || noSecurity))) {
						throw new Exception("Colliding policies for HIDs");
					}
		}
		
		//if there was no connection that means that the sender is the client and the receiver is the server
		//check if an active connection or a dummy connectin is needed
		Connection conn = null;
		if(true || allRequirements.contains(SecurityProperty.NoEncoding)) {
			conn = new NOPConnection(senderHID, receiverHID);
		} else {
			conn = new Connection(senderHID, receiverHID);
		}
		
		boolean foundComSecMgr = false;
		if (!this.communicationSecurityManagers.isEmpty()) {
			for (CommunicationSecurityManager comSecMgr : this.communicationSecurityManagers) {
				if (matchingPolicies(allRequirements, comSecMgr.getProperties())){
					conn.setSecurityProtocol(comSecMgr.getSecurityProtocol(senderHID, receiverHID));
					foundComSecMgr = true;
					break;
				}
			}
		}
		if (!foundComSecMgr 
				&& allRequirements.size() != 0 
				&& !allRequirements.contains(SecurityProperty.NoSecurity)) {
			//no available communication security manager although required
			throw new Exception("Required properties not fulfilled by ConnectionSecurityManagers");
		}
		//add connection to list
		connections.add(conn);
		timeouts.put(conn, cal.getTime());

		return conn;
	}

	/**
	 * Returns the {@Connection} which does general processing for
	 * broadcast messages.
	 * @param senderHID Sender of the message
	 * @return Connection to be used for getting the contents of the received data
	 * @throws Exception If there are policies for an HID but no CommunicationSecurityManager
	 */
	public synchronized Connection getBroadcastConnection(HID senderHID) throws Exception {
		Calendar cal = Calendar.getInstance();
		//find connection belonging to this HID
		for(Connection c : connections){
			if(c.getClientHID() == senderHID && c.getClass() == BroadcastConnection.class) {
				//set last use date of connection
				timeouts.put(c, cal.getTime());
				return c;
			}
		}

		//there was no connection found so create new connection
		List<SecurityProperty> policies = this.hidPolicies.get(senderHID);
		BroadcastConnection conn = new BroadcastConnection(senderHID);
		boolean foundComSecMgr = false;
		if(!this.communicationSecurityManagers.isEmpty()) {
			if(policies != null){
				for(CommunicationSecurityManager comSecMgr : this.communicationSecurityManagers) {
					if(matchingPolicies(policies, comSecMgr.getProperties())
							&& comSecMgr.getProperties().contains(SecurityProperty.Broadcast))
						conn.setSecurityProtocol(
								comSecMgr.getBroadcastSecurityProtocol(senderHID));
				}
			}
		}
		if(!foundComSecMgr
				&& policies != null 
				&& policies.size() != 0
				&& !(policies.size() == 1 && policies.contains(SecurityProperty.NoSecurity))) {
			//no available communication security manager although required
			throw new Exception("Required properties not fulfilled by ConnectionSecurityManagers");
		}
		//add connection to list
		connections.add(conn);
		timeouts.put(conn, cal.getTime());

		return conn;
	}

	/**
	 * Creates a {@Properties} object form the received data.
	 * It is assumed that every message which does not have
	 * a sender HID is a request for an HID as such sends attributes
	 * @param data Received data from an entity without HID
	 * @return Properties object unserialized from data
	 * @throws IOException 
	 */
//	public Part[] getHIDAttributes(byte[] data) throws IOException {
//		Properties properties = new Properties();
//		try {
//			properties.loadFromXML(new ByteArrayInputStream(data));
//		} catch (Exception e) {
//			IOException ioe = new IOException("Cannot parse received data!");
//			ioe.initCause(e);
//			throw ioe;
//		}
//		return properties;
//	}

	/**
	 * Sets the number of minutes before a connection is closed
	 * @param minutes Timeout to be used
	 */
	public void setConnectionTimeout(int minutes){
		this.timeoutMinutes = minutes;
		timer.cancel();
		timer.schedule(new ConnectionClearer(), 0, timeoutMinutes * 60 * 1000 / 2);
	}

	/**
	 * Checks whether to sets of policies math each other.
	 * @param required List of required properties
	 * @param provided List of provided properties
	 * @return
	 */
	private boolean matchingPolicies(List<SecurityProperty> required, List<SecurityProperty> provided){
		for(SecurityProperty req : required){
			if(!provided.contains(req)){
				return false;
			}
		}
		return true;
	}

	/**
	 * Clears not used connections from list.
	 * @author Vinkovits
	 *
	 */
	private class ConnectionClearer extends TimerTask{

		/**
		 * Runs through all referenced connections and deletes them if timeout expired
		 */
		public void run() {
			//go through all references and remove them when needed
			Set<Connection> livingConns = timeouts.keySet();
			Iterator<Connection> i = livingConns.iterator();
			Calendar calendar = Calendar.getInstance();
			while(i.hasNext()){
				Connection con = i.next();
				if(calendar.getTimeInMillis() - timeouts.get(con).getTime() > timeoutMinutes * 60 * 1000){
					//timeout has expired so delete it
					i.remove();
					connections.remove(con);
				}
			}
		}	
	}
}
