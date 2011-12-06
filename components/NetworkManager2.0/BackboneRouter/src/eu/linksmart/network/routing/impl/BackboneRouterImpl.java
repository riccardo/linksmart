package eu.linksmart.network.routing.impl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.routing.BackboneRouter;

/*
 * TODO #NM refactoring
 */
public class BackboneRouterImpl implements BackboneRouter {

	private Logger logger = Logger
			.getLogger(BackboneRouterImpl.class.getName());
	private HashMap<HID, Backbone> hidBackboneMap;
	private List<Backbone> availableBackbones;
	private NetworkManagerCore nmCore;
	private String defaultRoute;

	private static String BACKBONE_ROUTER = BackboneRouterImpl.class
			.getSimpleName();
	private static String ROUTING_JXTA = "JXTA";
	BackboneRouterConfigurator configurator;

	protected void activate(ComponentContext context) {
		logger.info("Starting " + BACKBONE_ROUTER);

		hidBackboneMap = new HashMap<HID, Backbone>();
		availableBackbones = new ArrayList<Backbone>();
		configurator = new BackboneRouterConfigurator(this,
				context.getBundleContext());
		logger.info(BACKBONE_ROUTER + " started");
	}

	protected void deactivate(ComponentContext context) {
		logger.info(BACKBONE_ROUTER + "stopped");
	}

	protected void bindBackbone(Backbone backbone) {
		if (!availableBackbones.contains(backbone))
			availableBackbones.add(backbone);
	}

	protected void unbindBackbone(Backbone backbone) {
		availableBackbones.remove(backbone);
	}

	protected void bindNMCore(NetworkManagerCore core) {
		nmCore = core;
	}

	protected void unbindNMCore(NetworkManagerCore core) {
		nmCore = null;
	}

	/**
	 * Receives a message over the communication channel from which the Hid
	 * came.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @return
	 */
	@Override
	public NMResponse sendData(HID senderHID, HID receiverHID, byte[] data) {
		Backbone b = (Backbone) hidBackboneMap.get(receiverHID);
		return b.sendData(senderHID, receiverHID, data);
	}

	/**
	 * Receives a message which also specifies the communication channel used by
	 * the sender. This will then update the list of HIDs and which backbone
	 * they use.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @param originatingBackbone
	 */
	@Override
	public NMResponse receiveData(HID senderHID, HID receiverHID, byte[] data,
			Backbone originatingBackbone) {

		// TODO: check why backbone would be needed - and if it is needed one
		// has to check if receiverHID is NULL (in case of broadcast message
		// receiverHID is NULL)
		// 
		// Backbone b = (Backbone)hidBackboneMap.get(receiverHID);
		hidBackboneMap.put(senderHID, originatingBackbone);

		return nmCore.receiveData(senderHID, receiverHID, data);

	}

	/**
	 * this method is invoked by backbone when a service requests a new HID to
	 * the network manager. this msg will be firstly accepted by backbone, and
	 * then propagated to the NMCore and IdManager
	 * 
	 * @param tempId
	 * @param receiverHID
	 * @param data
	 * @param originatingBackbone
	 * @return
	 */
	@Override
	public NMResponse createHid(HID tempId, HID receiverHID, byte[] data,
			Backbone originatingBackbone) {

		try {
			HID newHid = nmCore.createHID(data);
			hidBackboneMap.put(newHid, originatingBackbone);
			return nmCore.sendData(newHid, receiverHID, data);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new NMResponse(NMResponse.STATUS_ERROR);

	}

	/**
	 * Returns a list of communication channels available to the network
	 * manager.
	 * 
	 * @return list of communication channels
	 */
	@Override
	public List<Backbone> getAvailableCommunicationChannels() {
		return availableBackbones;
	}

	/**
	 * This function is called when the configuration is updated from the web
	 * page.
	 * 
	 * @param updates
	 */
	@Override
	public void applyConfigurations(Hashtable updates) {
		if (updates.containsKey(BackboneRouterImpl.BACKBONE_ROUTER)) {
			this.defaultRoute = (String) configurator
					.get(BackboneRouterConfigurator.COMMUNICATION_TYPE);
		}
	}

	/**
	 * Broadcasts a message over the all communication channel.
	 * 
	 * @param senderHid
	 * @param data
	 * @return
	 */
	@Override
	public NMResponse broadcastData(HID senderHid, byte[] data) {
		boolean success = true;
		String failedBroadcast = "";
		for (Backbone bb : availableBackbones) {
			if (bb.broadcastData(senderHid, data).getData() == NMResponse.STATUS_ERROR) {
				failedBroadcast += " " + bb.getClass();
				success = false;
			}
		}

		if (!success)
			return new NMResponse(NMResponse.STATUS_ERROR);
		else
			return new NMResponse(NMResponse.STATUS_SUCCESS);
	}

	/**
	 * this function return information about the backbone from which the hid
	 * come from the format is BackboneType:BackboneAddresse e.g.:
	 * WS;http://202.12.11.11/axis/services
	 * 
	 * @param hid
	 * @return BackboneType:BackboneAddresse
	 */
	@Override
	public String getRoute(HID hid) {
		Backbone b = hidBackboneMap.get(hid);
		return b.toString() + ";" + b.getDestinationAddressAsString(hid);
	}

}
