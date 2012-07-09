package eu.linksmart.network.connection;

import java.util.HashMap;

import eu.linksmart.network.HID;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProtocol;

public class BroadcastConnection extends Connection {

	private HashMap<HID, SecurityProtocol> broadcastSecProtocols = new HashMap<HID, SecurityProtocol>();

	public BroadcastConnection(HID serverHID) {
		super(serverHID);
	}

	protected void setCommunicationSecMgr(
			CommunicationSecurityManager comSecMgr) {
		if(comSecMgr.canBroadcast()){
			this.comSecMgr = comSecMgr;
		}else{
			throw  new IllegalArgumentException("Provided security protocol does not support broadcast");
		}
	}

	@Override
	protected SecurityProtocol getSecurityProtocol(HID senderHID,
			HID receiverHID) {
		if(comSecMgr != null) {
			//in broadcast connection receiverHID will always be null
			if(broadcastSecProtocols.containsKey(senderHID)) {
				return broadcastSecProtocols.get(senderHID);
			} else {
				SecurityProtocol secProt = comSecMgr.getBroadcastSecurityProtocol(senderHID);
				broadcastSecProtocols.put(senderHID, secProt);
				return secProt;
			}
		} else {
			return null;
		}
	}
}
