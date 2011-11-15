package eu.linksmart.network.connection;

import eu.linksmart.network.HID;

/**
 * Manages and creates connections between two HIDs.
 * @author Vinkovits
 *
 */
public class ConnectionManager {
	/**
	 * Returns {@link Connection} object associated to two entities.
	 * Creates new if there exists none yet.
	 * @param receiverHID
	 * @param senderHID
	 * @return Connection to use for processing
	 */
	Connection getConnection(HID receiverHID, HID senderHID){
		//TODO #NM refactoring
		return new Connection();
	}
}
