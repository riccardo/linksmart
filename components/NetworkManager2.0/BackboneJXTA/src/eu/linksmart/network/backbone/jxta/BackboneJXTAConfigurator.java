package eu.linksmart.network.backbone.jxta;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.utils.Configurator;

public class BackboneJXTAConfigurator extends Configurator {

	/* Configuration PID & file path. */
	public static String BBJXTA_PID = "eu.linksmart.network.backbone.jxta";
	public static String CONFIGURATION_FILE = "/BBJXTA.properties";

	/* Configuration Keys. */
	public static final String JXTA_HID = "BackboneJXTA.HID";
	public static final String JXTA_DESCRIPTION = "BackboneJXTA.Description";

	public static final String JXTA_LOGS = "Backbone.JXTALogs";
	public static final String PEER_NAME = "Backbone.PeerName";
	public static final String ANNOUNCE_VALIDITY = "Backbone.AnnounceValidity";
	public static final String FACTOR = "Backbone.Factor";
	public static final String WAIT_FOR_RDV_TIME = "Backbone.WaitForRdvTime";
	public static final String SYNCHRONIZED = "Backbone.Synchronized";
	public static final String MODE = "Backbone.Mode";
	public static final String RELAYED = "Backbone.Relayed";
	public static final String EXT_TCP_ADDR = "Backbone.ExtTcpAddr";
	public static final String MULTICAST = "Backbone.Multicast";
	public static final String JXTA_TCP_PORT = "Backbone.TcpPort";
	public static final String JXTA_HTTP_PORT = "Backbone.HttpPort";
	public static final String PIPE_LIFETIME = "Backbone.PipeLifeTime";

	private BackboneJXTAImpl bbJXTA;

	/**
	 * Constructor. Creates a new "NetworkManagerConfigurator" object
	 * 
	 * @param nm
	 *            the network manager implementation
	 * @param context
	 *            the bundle's execution context
	 */
	public BackboneJXTAConfigurator(BackboneJXTAImpl bbJXTA,
			BundleContext context) {
		super(context, Logger.getLogger(BackboneJXTAConfigurator.class
				.getName()), BBJXTA_PID, CONFIGURATION_FILE);
		this.bbJXTA = bbJXTA;
	}

	@SuppressWarnings("rawtypes")
	@Override
	/**
	 * Apply the configuration changes
	 * 
	 * @param updates the configuration changes
	 */
	public void applyConfigurations(Hashtable updates) {
		if (this.bbJXTA != null) {
			this.bbJXTA.applyConfigurations(updates);
		}
	}

}
