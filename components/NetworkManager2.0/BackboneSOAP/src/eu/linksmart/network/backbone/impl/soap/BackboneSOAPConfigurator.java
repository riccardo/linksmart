package eu.linksmart.network.backbone.impl.soap;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.utils.Configurator;

public class BackboneSOAPConfigurator extends Configurator {

	/* Configuration PID & file path. */
	public static String BBSOAP_PID = "eu.linksmart.network.backbone.soap";
	public static String CONFIGURATION_FILE = "/resources/BBSOAP.properties";

	/* Configuration Keys. */
	//see enum SecurityProperty in API, file SecurityProperty.java
	//and configuration section in /resources/BBSOAP.properties
	public static final String SECURITY_PARAMETERS = "BackboneJXTA.SecurityParameters";

	
	private BackboneSOAPImpl bbSOAPImpl;

	/**
	 * Log4j logger of this class
	 */
	private static Logger logger = Logger
			.getLogger(BackboneSOAPConfigurator.class.getName());

	/**
	 * Initializes the JXTA backbone configurator.
	 * 
	 * @param bbJXTAImpl
	 *            instantiation of JXTA backbone
	 * @param context
	 *            A bundle context
	 */
	public BackboneSOAPConfigurator(BackboneSOAPImpl bbSOAPImpl,
			BundleContext context) {
		super(context, logger, BBSOAP_PID, CONFIGURATION_FILE);
		this.bbSOAPImpl = bbSOAPImpl;
	}

	/**
	 * Apply the configuration changes
	 * 
	 * @param updates the configuration changes
	 */
	public void applyConfigurations(Hashtable updates) {
		bbSOAPImpl.applyConfigurations(updates);
	}

}
