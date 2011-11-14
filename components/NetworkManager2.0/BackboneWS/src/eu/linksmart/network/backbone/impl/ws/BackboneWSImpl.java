package eu.linksmart.network.backbone.impl.ws;

import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.backbone.Backbone;

/*
 * TODO #NM refactoring
 */
public class BackboneWSImpl implements Backbone {
	private static String BACKBONE_WS = BackboneWSImpl.class.getSimpleName();

	protected void activate(ComponentContext context) {
		System.out.println(BACKBONE_WS + "started");
	}

	protected void deactivate(ComponentContext context) {
		System.out.println(BACKBONE_WS + "stopped");
	}

	
	/**
	 * Sends a message over the webservice communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 */
	public void sendData(HID senderHID, HID receiverHID, Message message) {
		
	}
	

	
	/**
	 * Receives a message over the webservice communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 */
	public void receiveData(HID senderHID, HID receiverHID, Message message) {
		
	}


}
