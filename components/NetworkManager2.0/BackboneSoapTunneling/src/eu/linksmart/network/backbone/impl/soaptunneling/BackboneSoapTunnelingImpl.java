package eu.linksmart.network.backbone.impl.soaptunneling;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;
import org.osgi.service.http.HttpService;
/*
 * TODO #NM refactoring
 */
public class BackboneSoapTunnelingImpl implements Backbone {
	
	private static String BACKBONE_SOAPTUNNEL = BackboneSoapTunnelingImpl.class.getSimpleName();
	private HashMap<HID, URL> hidUrlMap;
	private HttpService http;
	private static Logger logger = Logger.getLogger(BackboneSoapTunnelingImpl.class.getName());
	
	protected void activate(ComponentContext context) {
		System.out.println(BACKBONE_SOAPTUNNEL + "started");		
		hidUrlMap = new HashMap<HID, URL>();
		
		http = (HttpService) context.locateService("HttpService");
		String httpPort = System.getProperty("org.osgi.service.http.port");

		try {
			http.registerServlet("/SOAPTunneling", 
					new SOAPTunnelServlet(this), null, null);
		} catch (Exception e) {
			logger.error("Error registering servlets", e);
		}
		
	}

	protected void deactivate(ComponentContext context) {
		System.out.println(BACKBONE_SOAPTUNNEL + "stopped");
	}

	
	/**
	 * Sends a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @return
	 */
	public NMResponse sendData(HID senderHID, HID receiverHID, byte[] data){
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
	public NMResponse receiveData(HID senderHID, HID receiverHID, byte[] data){
		return null;
	}

	
	/**
	 * Broadcasts a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param data
	 * @return
	 */
	public NMResponse broadcastData(HID senderHID, byte[] data){
		return null;
	}

	
	/**	
	 * Return the destination address as string that will be used for display
	 * purposes.
	 * 
	 * @param hid
	 * @return the backbone address represented by the Hid
	 */
	public String getDestinationAddressAsString(HID hid){
		return null;
	} 
		
}
