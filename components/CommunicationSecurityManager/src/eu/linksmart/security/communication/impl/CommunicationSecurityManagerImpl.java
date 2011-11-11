package eu.linksmart.security.communication.impl;


import org.osgi.service.component.ComponentContext;

import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.cryptomanager.CryptoManager;



/*
 * TODO #NM refactoring
 */
public class CommunicationSecurityManagerImpl implements CommunicationSecurityManager{
	private static String COMMUNICATION_SEC_MGR = CommunicationSecurityManagerImpl.class.getSimpleName();
	
protected void activate(ComponentContext context) {
	
	System.out.println(COMMUNICATION_SEC_MGR + "started");
	
}
protected void deactivate(ComponentContext context) {
	System.out.println(COMMUNICATION_SEC_MGR + "stopped");
}

protected void bindCryptoManager(CryptoManager cryptoManager){}
protected void unbindCryptoManager(CryptoManager cryptoManager){}

}