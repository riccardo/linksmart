package eu.linksmart.network.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import eu.linksmart.network.HID;
import eu.linksmart.security.communication.CommunicationSecurityManager;

/**
 * Manages and creates connections between two HIDs.
 * @author Vinkovits
 *
 */
public class ConnectionManager {

	private List<Connection> connections = new ArrayList<Connection>();
	private CommunicationSecurityManager comSecMgr = null;

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
				return c;
			}
		}

		//there was no connection found so create new connection
		Connection conn = new Connection(senderHID, receiverHID);
		if(comSecMgr != null){
			conn.setSecurityProtocol(comSecMgr.getSecurityProtocol());
		}
		return conn;
	}

	public Properties getHIDAttributes(byte[] data) {
		// TODO tranten
		return null;
	}
}
