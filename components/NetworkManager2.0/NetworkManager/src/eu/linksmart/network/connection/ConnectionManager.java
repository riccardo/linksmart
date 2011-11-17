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

/**
 * Manages and creates connections between two HIDs.
 * @author Vinkovits
 *
 */
public class ConnectionManager {

	/**
	 * Stores the last usage of a connection
	 */
	private HashMap<Connection, Date> timeouts = new HashMap<Connection, Date>();
	/**
	 * List of stored connections
	 */
	private List<Connection> connections = new ArrayList<Connection>();
	/**
	 * Designated broadcast connection
	 */
	private Connection broadcastConnection = null;
	/**
	 * {@link CommunicationSecurityManager} used to protect messages
	 */
	private CommunicationSecurityManager comSecMgr = null;
	/**
	 * Number of minutes a connection can at least live after last use
	 */
	private int timeoutMinutes = 30;
	/**
	 * Timer called with period of timeout/2 to clear connections
	 */
	private Timer timer = null;

	public ConnectionManager(){
		broadcastConnection = new Connection(null, null);
		//Timer which periodically calls the clearer task
		Timer timer = new Timer(true);
		timer.schedule(new ConnectionClearer(), 0, timeoutMinutes * 60 * 1000 / 2);
	}
	
	public void setCommunicationSecurityManager(CommunicationSecurityManager comSecMgr){
		this.comSecMgr = comSecMgr;
	}

	public void removeCommunicationSecurityManager(){
		comSecMgr = null;
	}

	/**
	 * Returns {@link Connection} object associated to two entities.
	 * Creates new if there exists none yet.
	 * @param receiverHID
	 * @param senderHID
	 * @return Connection to use for processing of communication
	 */
	public synchronized Connection getConnection(HID receiverHID, HID senderHID){
		//find connection belonging to these HIDs
		for(Connection c : connections){
			if((c.getClientHID() == receiverHID && c.getServerHID() == senderHID)
					|| (c.getClientHID() == senderHID && c.getServerHID() == receiverHID)){
				//set last use date of connection
				Calendar cal = Calendar.getInstance();
				timeouts.put(c, cal.getTime());
				return c;
			}
		}

		//there was no connection found so create new connection
		//if there was no connection that means that the sender is the client and the receiver is the server
		Connection conn = new Connection(senderHID, receiverHID);
		if(comSecMgr != null){
			conn.setSecurityProtocol(comSecMgr.getSecurityProtocol(senderHID, receiverHID));
		}
		return conn;
	}
	
	/**
	 * Returns the {@Connection} which does general processing for
	 * broadcast messages
	 * @param senderHID Sender of the message
	 * @return Connection to be used for getting the contents of the received data
	 */
	public synchronized Connection getBroadcastConnection(HID senderHID){
		return broadcastConnection;
	}

	/**
	 * Creates a {@Properties} object form the received data.
	 * It is assumed that every message which does not have
	 * a sender HID is a request for an HID as such sends attributes
	 * @param data Received data from an entity without HID
	 * @return Properties object unserialized from data
	 * @throws IOException 
	 */
	public Properties getHIDAttributes(byte[] data) throws IOException {
		Properties properties = new Properties();
		try {
			properties.loadFromXML(new ByteArrayInputStream(data));
		} catch (Exception e) {
			IOException ioe = new IOException("Cannot parse received data!");
			ioe.initCause(e);
			throw ioe;
		}
		return properties;
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
	
	/**
	 * Clears not used connections from list
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
				}
			}
		}	
	}
}
