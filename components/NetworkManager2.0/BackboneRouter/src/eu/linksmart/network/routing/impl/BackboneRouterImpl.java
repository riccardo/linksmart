package eu.linksmart.network.routing.impl;

import java.util.List;
import java.util.Vector;

import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.routing.BackboneRouter;

/*
 * TODO #NM refactoring
 */
public class BackboneRouterImpl implements BackboneRouter {

	private static String BACKBONE_ROUTER = BackboneRouterImpl.class
			.getSimpleName();

	protected void activate(ComponentContext context) {
		System.out.println(BACKBONE_ROUTER + "started");
	}

	protected void deactivate(ComponentContext context) {
		System.out.println(BACKBONE_ROUTER + "stopped");
	}

	protected void bindBackbone(Backbone backbone) {

	}

	protected void unbindBackbone(Backbone backbone) {

	}

	public void sendData(HID senderHID, HID receiverHID, Message message) {
		// TODO implement this
	}

	public void receiveData(HID senderHID, HID receiverHID, Message message,
			String backboneType) {
		// TODO implement this
	}

	public List<String> getAvailableCommunicationChannels() {
		// TODO implement this
		return new Vector<String>();
	}

}
