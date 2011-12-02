package eu.linksmart.network.connection;

import eu.linksmart.network.HID;
import eu.linksmart.security.communication.SecurityProtocol;

public class BroadcastConnection extends Connection {

	public BroadcastConnection(HID clientHID) {
		super(clientHID);
	}
	
	protected void setSecurityProtocol(SecurityProtocol secProtocol){
		if(secProtocol.canBroadcast()){
			this.securityProtocol = secProtocol;
		}else{
			throw  new IllegalArgumentException("Provided security protocol does not support broadcast");
		}
	}
}
