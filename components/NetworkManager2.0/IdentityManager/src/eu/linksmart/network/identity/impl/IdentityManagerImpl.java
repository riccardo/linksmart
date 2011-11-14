package eu.linksmart.network.identity.impl;

import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.identity.IdentityManager;


/*
 * TODO #NM refactoring
 */
public class IdentityManagerImpl implements IdentityManager{
	private static String IDENTITY_MGR = IdentityManagerImpl.class.getSimpleName();
	
protected void activate(ComponentContext context) {
	
	System.out.println(IDENTITY_MGR + "started");
	
}
protected void deactivate(ComponentContext context) {
	System.out.println(IDENTITY_MGR + "stopped");
}
}
