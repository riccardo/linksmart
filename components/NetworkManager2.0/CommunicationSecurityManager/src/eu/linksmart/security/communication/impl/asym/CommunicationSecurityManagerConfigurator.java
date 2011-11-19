package eu.linksmart.security.communication.impl.asym;

import java.util.Hashtable;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.utils.Configurator;

/**
 * Configurator class for the CommunicationSecurityManager
 * which registers itself in the LinkSmart Webconfigurator
 * @author Vinkovits
 *
 */
public class CommunicationSecurityManagerConfigurator extends Configurator {

	/**
	 * Unique identity under which the configuraton is registered
	 */
	protected final static String COMSECMGR_PID = "eu.linksmart.security.communication.impl.asym.CommunicationSecurityManager";
	/**
	 * Path to the default configurations
	 */
	public final static String CONFIGURATION_FILE = "/resources/COMSECMGR.properties";
	/**
	 * Identifier to the property threshold as used by {@link CommunicationSecurityManager}
	 */
	public final static String TRUST_THRESHOLD = "CommunicationSecurityManager.trustThreshold";
	/**
	 * Identifier to the property TrustManager URL as used by {@link CommunicationSecurityManager}
	 */
	public final static String TRUST_MANAGER_URL = "CommunicationSecurityManager.trustManagerURL";
	/**
	 * Log4j logger of this class
	 */
	private static Logger logger = Logger.getLogger(SecurityProtocolAsym.class);
	/**
	 * Implementation reference to be configured by this
	 */
	private CommunicationSecurityManagerImpl comSecMgr = null;
	/*
	 * OSGi context
	 */
	private BundleContext context = null;

	protected CommunicationSecurityManagerConfigurator(CommunicationSecurityManagerImpl comSecMgr, BundleContext context){
		//initialize configurator of CommunicationSecurityManager
		super(context, Logger.getLogger(CommunicationSecurityManagerConfigurator.class.getName()),
				COMSECMGR_PID, CONFIGURATION_FILE);
		this.context = context;
		this.comSecMgr = comSecMgr;
	}

	public void applyConfigurations(Hashtable updates) {
		comSecMgr.applyConfigurations(updates);
	}

}
