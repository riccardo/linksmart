package eu.linksmart.security.communication.impl.sym;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;

import eu.linksmart.security.communication.SecurityProperty;
import eu.linksmart.utils.Configurator;

public class CommunicationSecurityManagerConfigurator extends Configurator {

	protected final static String COMSECMGR_PID = "eu.linksmart.security.communication.sym";
	
	public static String SECURITY_PROVIDED = "CommunicationSecurityManager.providedSecurityProperties";
	
	public final static String CONFIGURATION_FILE = "/resources/COMSECMGR.properties";
	
	public final static String TRUST_THRESHOLD = "CommunicationSecurityManager.trustThreshold";
	
	public final static String TRUST_MANAGER_URL = "CommunicationSecurityManager.trustManagerURL";
	
	private static Logger logger = Logger.getLogger(SecurityProtocolImpl.class);
	
	private CommunicationSecurityManagerImplDummy comSecMgr = null;
	
	private BundleContext context = null;

	protected CommunicationSecurityManagerConfigurator(CommunicationSecurityManagerImplDummy comSecMgr, BundleContext context){
		//initialize configurator of CommunicationSecurityManager
		super(context, Logger.getLogger(CommunicationSecurityManagerConfigurator.class.getName()), COMSECMGR_PID, CONFIGURATION_FILE);
		this.context = context;
		this.comSecMgr = comSecMgr;
		//show provided services in configuration
		StringBuilder sb = new StringBuilder();
		List<SecurityProperty> securityProps = comSecMgr.getProperties();
		int i = 0;
		for(SecurityProperty prop : securityProps){
			i++;
			if (i == securityProps.size()) {
				sb.append(prop.name());
			} else {
				sb.append(prop.name() + ", ");
			}
		}
		this.setConfiguration(SECURITY_PROVIDED, sb.toString());
	}
	
	public CommunicationSecurityManagerConfigurator(CommunicationSecurityManagerImplDummy comSecMgr, BundleContext context, ConfigurationAdmin configurationAdmin) {
        super(context, Logger.getLogger(CommunicationSecurityManagerConfigurator.class.getName()), COMSECMGR_PID, CONFIGURATION_FILE, configurationAdmin);
        super.init();
        this.comSecMgr = comSecMgr;
        StringBuilder sb = new StringBuilder();
		List<SecurityProperty> securityProps = this.comSecMgr.getProperties();
		int i = 0;
		for(SecurityProperty prop : securityProps){
			i++;
			if (i == securityProps.size()) {
				sb.append(prop.name());
			} else {
				sb.append(prop.name() + ", ");
			}
		}
		this.setConfiguration(SECURITY_PROVIDED, sb.toString());
    }
	
	public void applyConfigurations(Hashtable updates) {
		this.comSecMgr.applyConfigurations(updates);
	}
}
