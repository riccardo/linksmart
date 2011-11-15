

import java.rmi.RemoteException;

import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.CommunicationSecurityManager;

/*
 * TODO #NM refactoring
 */
public class NetworkManagerImpl implements NetworkManager{
	private static String NETWORK_MGR = NetworkManagerImpl.class.getSimpleName();
	
protected void activate(ComponentContext context) {
	
	System.out.println(NETWORK_MGR + "started");
	
}
protected void deactivate(ComponentContext context) {
	System.out.println(NETWORK_MGR + "stopped");
}

protected void bindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr){
	
}

protected void unbindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr){
	
}

protected void bindIdentityManager(IdentityManager identityMgr){
	
}

protected void unbindIdentityManager(IdentityManager identityMgr){
	
}

protected void bindBackboneRouter(BackboneRouter backboneRouter){
	
}

protected void unbindBackboneRouter(BackboneRouter backboneRouter){
	
}
@Override
public NMResponse sendData(HID sender, HID receiver, byte[] data)
		throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}
@Override
public NMResponse receiveData(HID sender, HID receiver, byte[] data)
		throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}
@Override
public HID getHID() {
	// TODO Auto-generated method stub
	return null;
}


}
