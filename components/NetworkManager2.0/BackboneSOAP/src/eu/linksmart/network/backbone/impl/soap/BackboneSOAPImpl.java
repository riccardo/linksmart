package eu.linksmart.network.backbone.impl.soap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;

/*
 * TODO #NM refactoring
 */
public class BackboneSOAPImpl implements Backbone {

	private static String BACKBONE_SOAP = BackboneSOAPImpl.class
			.getSimpleName();
	private Map<HID, URL> hidUrlMap;
	private static Logger LOG = Logger.getLogger(BackboneSOAPImpl.class
			.getName());

	protected void activate(ComponentContext context) {
		LOG.info(BACKBONE_SOAP + "started");
	}

	protected void deactivate(ComponentContext context) {
		System.out.println(BACKBONE_SOAP + "stopped");
	}

	/**
	 * Sends a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @return
	 */
	public NMResponse sendData(HID senderHID, HID receiverHID, byte[] data) {
		return null;
	}

	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @return
	 */
	public NMResponse receiveData(HID senderHID, HID receiverHID, byte[] data) {
		return null;
	}

	/**
	 * Broadcasts a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param data
	 * @return
	 */
	public NMResponse broadcastData(HID senderHID, byte[] data) {
		return null;
	}

	/**
	 * Return the destination address as string that will be used for display
	 * purposes.
	 * 
	 * @param hid
	 * @return the backbone address represented by the Hid
	 */
	public String getDestinationAddressAsString(HID hid) {
		return null;
	}

	@Override
	public void applyConfigurations(Hashtable updates) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean addEndpoint(HID hid, String endpoint) {
		if (this.hidUrlMap.containsKey(hid)) {
			return false;
		}
		try {
			URL url = new URL(endpoint);
			this.hidUrlMap.put(hid, url);
			return true;
		} catch (MalformedURLException e) {
			LOG.debug(
					"Unable to add endpoint " + endpoint + " for HID "
							+ hid.toString(), e);
		}
		return false;
	}

	@Override
	public boolean removeEndpoint(HID hid) {
		return this.hidUrlMap.remove(hid) != null;
	}

}
