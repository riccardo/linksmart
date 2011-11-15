package eu.linksmart.security.communication.impl;

import org.osgi.service.component.ComponentContext;

import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProtocol;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.trustmanager.TrustManager;



/**
 * Provides {@link SecurityProtocol} implementations of
 * specific type to be used for communication protection.
 * @author Vinkovits
 *
 */
public class CommunicationSecurityManagerImpl implements CommunicationSecurityManager{
	private static String COMMUNICATION_SEC_MGR = CommunicationSecurityManagerImpl.class.getSimpleName();
	
protected void activate(ComponentContext context) {
	
	System.out.println(COMMUNICATION_SEC_MGR + "started");
}
protected void deactivate(ComponentContext context) {
	System.out.println(COMMUNICATION_SEC_MGR + "stopped");
}

protected void bindCryptoManager(CryptoManager cryptoManager){
	
}

protected void unbindCryptoManager(CryptoManager cryptoManager){
	
}

protected void bindTrustManager(TrustManager trustManager){
	//TODO #NM refactoring TrustManager should also be reachable over url
}

protected void unbindTrustManager(TrustManager trustManager){
	
}

public SecurityProtocol getSecurityProtocol() {
	// TODO Auto-generated method stub
	return null;
}

}