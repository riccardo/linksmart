package eu.linksmart.security.communication.impl.asym;

import java.util.Hashtable;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.utils.Configurator;

public class CommunicationSecurityManagerConfigurator extends Configurator {

	protected final static String COMSECMGR_PID = "eu.linksmart.security.communication.impl.asym.CommunicationSecurityManager";
	public final static String CONFIGURATION_FILE = "/resources/COMSECMGR.properties";
	public final static String TRUST_THRESHOLD = "CommunicationSecurityManager.trustThreshold";
	public final static String TRUST_MANAGER_URL = "CommunicationSecurityManager.trustManagerURL";

	private static Logger logger = Logger.getLogger(SecurityProtocolAsym.class);

	private CommunicationSecurityManagerImpl comSecMgr = null;
	private BundleContext context = null;

	protected CommunicationSecurityManagerConfigurator(CommunicationSecurityManagerImpl comSecMgr, BundleContext context){
		//initialize configurator of CommunicationSecurityManager
		super(context, Logger.getLogger(CommunicationSecurityManagerConfigurator.class.getName()),
				COMSECMGR_PID, CONFIGURATION_FILE);
		this.context = context;
		this.comSecMgr = comSecMgr;
	}

	@Override
	public void applyConfigurations(Hashtable updates) {
		comSecMgr.applyConfigurations(updates);
	}

}
