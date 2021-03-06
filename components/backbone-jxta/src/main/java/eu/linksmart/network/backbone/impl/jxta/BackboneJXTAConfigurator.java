package eu.linksmart.network.backbone.impl.jxta;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.utils.Configurator;
import org.osgi.service.cm.ConfigurationAdmin;

public class BackboneJXTAConfigurator extends Configurator {

	/* Configuration PID & file path. */
	public static String BBJXTA_PID = "eu.linksmart.network.backbone.jxta";
	public static String CONFIGURATION_FILE = "/resources/BBJXTA.properties";

	/* Configuration Keys. */
	public static final String JXTA_DESCRIPTION = "BackboneJXTA.Description";

	public static final String JXTA_LOGS = "BackboneJXTA.JXTALogs";
	public static final String PEER_NAME = "BackboneJXTA.PeerName";
	public static final String ANNOUNCE_VALIDITY = "BackboneJXTA.AnnounceValidity";
	public static final String FACTOR = "BackboneJXTA.Factor";
	public static final String WAIT_FOR_RDV_TIME = "BackboneJXTA.WaitForRdvTime";
	public static final String SYNCHRONIZED = "BackboneJXTA.Synchronized";
	public static final String MODE = "BackboneJXTA.Mode";
	public static final String RELAYED = "BackboneJXTA.Relayed";
	public static final String EXT_TCP_ADDR = "BackboneJXTA.ExtTcpAddr";
	public static final String MULTICAST = "BackboneJXTA.Multicast";
	public static final String JXTA_TCP_PORT = "BackboneJXTA.TcpPort";
	public static final String JXTA_HTTP_PORT = "BackboneJXTA.HttpPort";
	public static final String PIPE_LIFETIME = "BackboneJXTA.PipeLifeTime";

	public static final String MULTIMEDIA_PORT = "BackboneJXTA.MultimediaPort";


	public static final String MODE_SUPERNODE = "SuperNode";
	public static final String MODE_NODE = "Node";


	public static final String SUPERNODE_TCP_URI = "BackboneJXTA.SuperNodeTCPURI";
	public static final String SUPERNODE_HTTP_URI = "BackboneJXTA.SuperNodeHTTPURI";

	//see enum SecurityProperty in API, file SecurityProperty.java
	//and configuration section in /resources/BBJXTA.properties
	public static final String SECURITY_PARAMETERS = "BackboneJXTA.SecurityParameters";

	
	private BackboneJXTAImpl bbJXTAImpl;

	/**
	 * Log4j logger of this class
	 */
	private static Logger logger = Logger
			.getLogger(BackboneJXTAConfigurator.class.getName());


    /**
     * Initializes the JXTA backbone configurator.
     *
     * @param bbJXTAImpl
     *            instantiation of JXTA backbone
     * @param context
     *            A bundle context
     * @param configurationAdmin configAdmin reference for proper setup*
     */
    public BackboneJXTAConfigurator(BackboneJXTAImpl bbJXTAImpl,
                                    BundleContext context, ConfigurationAdmin configurationAdmin) {
        super(context, logger, BBJXTA_PID, CONFIGURATION_FILE,configurationAdmin);
        super.init();
        this.bbJXTAImpl = bbJXTAImpl;
    }

    /**
	 * Initializes the JXTA backbone configurator.
	 * 
	 * @param bbJXTAImpl
	 *            instantiation of JXTA backbone
	 * @param context
	 *            A bundle context
	 */
	public BackboneJXTAConfigurator(BackboneJXTAImpl bbJXTAImpl,
			BundleContext context) {
		super(context, logger, BBJXTA_PID, CONFIGURATION_FILE);
		this.bbJXTAImpl = bbJXTAImpl;
	}

	@SuppressWarnings("rawtypes")
	@Override
	/**
	 * Apply the configuration changes
	 * 
	 * @param updates the configuration changes
	 */
	public void applyConfigurations(Hashtable updates) {
		bbJXTAImpl.applyConfigurations(updates);
	}

}
