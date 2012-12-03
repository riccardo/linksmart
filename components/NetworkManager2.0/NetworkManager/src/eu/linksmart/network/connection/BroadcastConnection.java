package eu.linksmart.network.connection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProtocol;

public class BroadcastConnection extends Connection {

	private Map<VirtualAddress, SecurityProtocol> broadcastSecProtocols = new ConcurrentHashMap<VirtualAddress, SecurityProtocol>();

	public BroadcastConnection(VirtualAddress serverVirtualAddress) {
		super(serverVirtualAddress);
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
	protected SecurityProtocol getSecurityProtocol(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress) {
		if(comSecMgr != null) {
			//in broadcast connection receiverVirtualAddress will always be null
			if(broadcastSecProtocols.containsKey(senderVirtualAddress)) {
				return broadcastSecProtocols.get(senderVirtualAddress);
			} else {
				SecurityProtocol secProt = comSecMgr.getBroadcastSecurityProtocol(senderVirtualAddress);
				broadcastSecProtocols.put(senderVirtualAddress, secProt);
				return secProt;
			}
		} else {
			return null;
		}
	}
}
