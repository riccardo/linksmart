package eu.linksmart.network.connection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import eu.linksmart.network.HID;
import eu.linksmart.security.communication.CommunicationSecurityManager;

/**
 * Manages and creates connections between two HIDs.
 * @author Vinkovits
 *
 */
public class ConnectionManager {

	private List<Connection> connections = new ArrayList<Connection>();
	private Connection broadcastConnection = null;
	private CommunicationSecurityManager comSecMgr = null;

	public ConnectionManager(){
		broadcastConnection = new Connection(null, null);
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
}
