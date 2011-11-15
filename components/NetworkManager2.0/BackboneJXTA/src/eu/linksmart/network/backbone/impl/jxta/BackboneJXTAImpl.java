package eu.linksmart.network.backbone.impl.jxta;

import java.util.Hashtable;

import net.jxta.discovery.DiscoveryListener;
import net.jxta.logging.Logging;
import net.jxta.rendezvous.RendezvousListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.routing.BackboneRouter;

/*
 * TODO #NM refactoring
 */
public class BackboneJXTAImpl implements Backbone, RendezvousListener, DiscoveryListener {

	private static String BACKBONE_JXTA = BackboneJXTAImpl.class
			.getSimpleName();

	private Logger logger = Logger.getLogger(BackboneJXTAImpl.class.getName());
	protected ComponentContext context;
	private BackboneJXTAConfigurator configurator; 

//	protected String myIP;
//	protected String myHID;

	private BackboneRouter bbRouter;
	private NetworkManagerImpl nm;
	private HIDManagerApplication hidManager;

	
	/**
	 * Standard constructor must not be used because a BackboneRouter instance
	 * is needed. Please use the appropriate constructor.
	 */
	private void init() {
	}

	/**
	 * Instantiates the JXTA backbone.
	 * 
	 * @param bbRouter
	 */
	public void init(BackboneRouter bbRouter, NetworkManagerImpl nm) {
		this.bbRouter = bbRouter;
		this.nm = nm;
		hidManager = nm.getHIDManagerApplication();

		configurator = new BackboneJXTAConfigurator(this,
				context.getBundleContext());
		configurator.registerConfiguration();
		logger.debug("Backbone JXTA initialised");
	}

	/**
	 * Apply the configuration changes
	 * 
	 * @param updates
	 *            the configuration changes
	 */
	public void applyConfigurations(Hashtable updates) {
		if (updates.containsKey(BackboneJXTAConfigurator.JXTA_LOGS)) {
			if (updates.get(BackboneJXTAConfigurator.JXTA_LOGS).equals("OFF")) {
				System.setProperty(Logging.JXTA_LOGGING_PROPERTY,
						Level.OFF.toString());
			} else if (updates.get(BackboneJXTAConfigurator.JXTA_LOGS).equals(
					"ON")) {
				System.setProperty(Logging.JXTA_LOGGING_PROPERTY,
						Level.ALL.toString());
			}
		}
		// if (updates.containsKey(BackboneJXTAConfigurator.JXTA_DESCRIPTION)
		// || updates.containsKey(BackboneJXTAConfigurator.JXTA_HID)) {
		// this.myHID = (String) nm.getConfiguration().get(
		// NetworkManagerConfigurator.NM_HID);
		//
		// hidManager.setJXTAID(
		// peerID,
		// myHID,
		// (String) nm.getConfiguration().get(
		// NetworkManagerConfigurator.NM_DESCRIPTION),
		// "http://localhost:"
		// + System.getProperty("org.osgi.service.http.port")
		// + servicePath, myIP, System
		// .getProperty("org.osgi.service.http.port"), true);
		// }
	}

	protected void activate(ComponentContext context) {
		System.out.println(BACKBONE_JXTA + "started");
	}

	protected void deactivate(ComponentContext context) {
		System.out.println(BACKBONE_JXTA + "stopped");
	}

	/**
	 * Sends a message over the JXTA communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 */
	public NMResponse sendData(HID senderHID, HID receiverHID, Message message) {
		// TODO implement this
		return new NMResponse();
	}

	/**
	 * Receives a message over the JXTA communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 */
	public NMResponse receiveData(HID senderHID, HID receiverHID,
			Message message) {
		// TODO implement this
		return new NMResponse();
	}

	
	// here follows the JXTA specific stuff coming from the old NetworkManager Backbone.
	
	
}
