package eu.linksmart.network.grand.backbone;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.utils.Configurator;
import org.osgi.service.cm.ConfigurationAdmin;

public class BackboneGrandConfigurator extends Configurator {

	/* Configuration PID & file path. */
	public static String BBSOAP_PID = "eu.linksmart.network.backbone.soap";
	public static String CONFIGURATION_FILE = "/resources/BBSOAP.properties";

	/* Configuration Keys. */
	//see enum SecurityProperty in API, file SecurityProperty.java
	//and configuration section in /resources/BBSOAP.properties
	public static final String SECURITY_PARAMETERS = "BackboneSOAP.SecurityParameters";

	
	private BackboneGrandImpl bbGrandImpl;

	/**
	 * Log4j logger of this class
	 */
	private static Logger logger = Logger
			.getLogger(BackboneGrandConfigurator.class.getName());

	/**
	 * Initializes the JXTA backbone configurator.
	 * 
	 * @param bbJXTAImpl
	 *            instantiation of JXTA backbone
	 * @param context
	 *            A bundle context
	 */
	public BackboneGrandConfigurator(BackboneGrandImpl bbSOAPImpl,
			BundleContext context) {
		super(context, logger, BBSOAP_PID, CONFIGURATION_FILE);
		this.bbGrandImpl = bbSOAPImpl;
	}
	
	/**
	 * Initializes the JXTA backbone configurator.
	 * 
	 * @param bbJXTAImpl
	 *            instantiation of JXTA backbone
	 * @param context
	 *            A bundle context
	 * @param configurationAdmin
	 *            configuration Admin
	 */
    public BackboneGrandConfigurator(BackboneGrandImpl bbSOAPImpl,
                                     BundleContext context,ConfigurationAdmin configurationAdmin) {
        super(context, logger, BBSOAP_PID, CONFIGURATION_FILE,configurationAdmin);
        super.init();
        this.bbGrandImpl = bbSOAPImpl;
    }

	/**
	 * Apply the configuration changes
	 * 
	 * @param updates the configuration changes
	 */
	public void applyConfigurations(Hashtable updates) {
//		bbSOAPImpl.applyConfigurations(updates);
	}

}
