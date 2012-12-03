package eu.linksmart.network.connection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProperty;

/**
 * Manages and creates connections between two services.
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
	 * Number of minutes a connection can at least live after last use.
	 */
	private int timeoutMinutes = 30;
	/**
	 * Timer called with period of timeout/2 to clear connections.
	 */
	private Timer timer = null;
	/**
	 * Required properties stored by VirtualAddress. These {@link SecurityProperty} have to
	 * be provided by {@link Connection} between these services.
	 */
	private HashMap<VirtualAddress, List<SecurityProperty>> servicePolicies = new HashMap<VirtualAddress, List<SecurityProperty>>();
	/**
	 * List of available managers to use for connection protection.
	 */
	private ArrayList<CommunicationSecurityManager> communicationSecurityManagers = 
		new ArrayList<CommunicationSecurityManager>();

	/**
	 * Used to avoid access and change of policies
	 */
	private Object policyModificationLock = new Object();

	private VirtualAddress nmVirtualAddress = null;;


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
	 * Saves this VirtualAddress to always use a specific configuration.
	 * This includes for example type of security to use.
	 * @param regulatedVirtualAddress 
	 */
	public void registerServicePolicy(VirtualAddress regulatedVirtualAddress, List<SecurityProperty> properties){
		synchronized(policyModificationLock){
			/*remove all connections which have this virtual address*/
			List<Connection> toRemove = new ArrayList<Connection>();
			//find the relevant connections
			for(Connection con : connections) {
				if(con.getServerVirtualAddress().equals(regulatedVirtualAddress) 
						|| (!(con instanceof BroadcastConnection) 
								&& con.getServerVirtualAddress().equals(regulatedVirtualAddress))) {
					toRemove.add(con);
				}
			}
			if(toRemove.size() != 0) {
				//remove collected connections
				for(Connection con : toRemove) {
					deleteConnection(con);
				}
			}
			//add new policy
			this.servicePolicies.put(regulatedVirtualAddress, properties);
		}
	}

	/**
	 * Removes the VirtualAddress's policy. After this call the VirtualAddress
	 * will always be handled with the default settings.
	 * @param regulatedVirtualAddress
	 */
	public void deleteServicePolicy(VirtualAddress regulatedVirtualAddress){
		this.servicePolicies.remove(regulatedVirtualAddress);
	}

	/**
	 * Returns {@link Connection} object associated to two entities.
	 * Creates new if there exists none yet.
	 * @param receiverVirtualAddress physical endpoint of connection
	 * @param senderVirtualAddress physical endpoint of connection
	 * @return Connection to use for processing of communication
	 * @throws Exception If there are policies for an VirtualAddress but no CommunicationSecurityManager
	 */
	public synchronized Connection getConnection(VirtualAddress receiverVirtualAddress, VirtualAddress senderVirtualAddress) throws Exception{
		Calendar cal = Calendar.getInstance();
		//find connection belonging to these services
		for(Connection c : connections){
			if(!(c instanceof BroadcastConnection) &&
					((c.getClientVirtualAddress().equals(receiverVirtualAddress) && c.getServerVirtualAddress().equals(senderVirtualAddress))
							|| (c.getClientVirtualAddress().equals(senderVirtualAddress) && c.getServerVirtualAddress().equals(receiverVirtualAddress)))){
				//set last use date of connection
				timeouts.put(c, cal.getTime());
				return c;
			}
		}

		if(receiverVirtualAddress.equals(senderVirtualAddress)){
			//add nopconnection to list as this is a reflection message
			Connection conn = new NOPConnection(senderVirtualAddress, receiverVirtualAddress);
			connections.add(conn);
			timeouts.put(conn, cal.getTime());
		}

		//policies should not be changed while reading them
		synchronized(policyModificationLock) {
			//there was no connection found so create new connection
			//policies only apply for connections between this NM and other entity
			List<SecurityProperty> policies = new ArrayList<SecurityProperty>();
			if(receiverVirtualAddress.equals(nmVirtualAddress) || senderVirtualAddress.equals(nmVirtualAddress)) {
				//get virtual address of remote entity
				VirtualAddress remoteVirtualAddress = nmVirtualAddress.equals(receiverVirtualAddress)? senderVirtualAddress : receiverVirtualAddress;
				//check if there are policies for the VirtualAddress
				if(this.servicePolicies.containsKey(remoteVirtualAddress)){
					//add policies to requirement list
					policies = servicePolicies.get(remoteVirtualAddress);
				}

				//check if requirements are not colliding
				boolean noEncoding = policies.contains(SecurityProperty.NoEncoding);
				boolean noSecurity = policies.contains(SecurityProperty.NoSecurity);
				boolean justNoEncNoSec = policies.size() == 2 && noEncoding && noSecurity;
				//if there are more requirements and noEnc or noSec is included it must be colliding
				if (!justNoEncNoSec && (policies.size() > 1 && (noEncoding || noSecurity))) {
					throw new Exception("Colliding policies for services");
				}
			}

			//if there was no connection that means that the sender is the client and the receiver is the server
			//check if an active connection or a dummy connectin is needed
			Connection conn = null;
			if(policies.contains(SecurityProperty.NoEncoding)) {
				conn = new NOPConnection(senderVirtualAddress, receiverVirtualAddress);
			} else {
				conn = new Connection(senderVirtualAddress, receiverVirtualAddress);
			}

			boolean foundComSecMgr = false;
			if (!this.communicationSecurityManagers.isEmpty()) {
				for (CommunicationSecurityManager comSecMgr : this.communicationSecurityManagers) {
					if (matchingPolicies(policies, comSecMgr.getProperties())){
						conn.setCommunicationSecMgr(comSecMgr);
						foundComSecMgr = true;
						break;
					}
				}
			}
			if (!foundComSecMgr 
					&& policies.size() != 0 
					&& !policies.contains(SecurityProperty.NoSecurity)) {
				//no available communication security manager although required
				throw new Exception("Required properties not fulfilled by ConnectionSecurityManagers");
			}
			//add connection to list
			connections.add(conn);
			timeouts.put(conn, cal.getTime());

			return conn;
		}
	}

	/**
	 * Returns the {@Connection} which does general processing for
	 * broadcast messages.
	 * @param senderVirtualAddress Sender of the message
	 * @return Connection to be used for getting the contents of the received data
	 * @throws Exception If there are policies for an VirtualAddress but no CommunicationSecurityManager
	 */
	public synchronized Connection getBroadcastConnection(VirtualAddress senderVirtualAddress) throws Exception {
		Calendar cal = Calendar.getInstance();
		//find connection belonging to this VirtualAddress
		for(Connection c : connections){
			if(c.getServerVirtualAddress().equals(senderVirtualAddress) && c instanceof BroadcastConnection) {
				//set last use date of connection
				timeouts.put(c, cal.getTime());
				return c;
			}
		}

		//FIXME broadcast messages and policies have to be re-thought

		//there was no connection found so create new connection	
		List<SecurityProperty> policies = null;
		if(!senderVirtualAddress.equals(nmVirtualAddress)) {
			policies = this.servicePolicies.get(senderVirtualAddress);
		}
		//check if an active connection or a dummy connection is needed
		BroadcastConnection conn = null;
		if(policies != null && policies.contains(SecurityProperty.NoEncoding)) {
			conn = new NOPBroadcastConnection(senderVirtualAddress);
		} else {
			conn = new BroadcastConnection(senderVirtualAddress);
		}

		boolean foundComSecMgr = false;
		if(!this.communicationSecurityManagers.isEmpty()) {
			if(policies != null){
				for(CommunicationSecurityManager comSecMgr : this.communicationSecurityManagers) {
					if(matchingPolicies(policies, comSecMgr.getProperties())
							&& comSecMgr.getProperties().contains(SecurityProperty.Broadcast)) {
						conn.setCommunicationSecMgr(comSecMgr);
						foundComSecMgr = true;
					}
				}
			}
		}
		if(!foundComSecMgr
				&& policies != null 
				&& policies.size() != 0
				&& !policies.contains(SecurityProperty.NoSecurity)) {
			//no available communication security manager although required
			throw new Exception("Required properties not fulfilled by ConnectionSecurityManagers");
		}
		//add connection to list
		connections.add(conn);
		timeouts.put(conn, cal.getTime());

		return conn;
	}

	/**
	 * Sets the number of minutes before a connection is closed
	 * @param minutes Timeout to be used
	 */
	public void setConnectionTimeout(int minutes){
		this.timeoutMinutes = minutes;
		timer.cancel();
		timer.schedule(new ConnectionClearer(), 0, timeoutMinutes * 60 * 1000 / 2);
	}

	public void setOwnerVirtualAddress(VirtualAddress virtualAddress) {
		nmVirtualAddress  = virtualAddress;
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
	 * Removes connection and its references in timeout.
	 * @param con
	 */
	private void deleteConnection(Connection con) {
		connections.remove(con);
		timeouts.remove(con);
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
