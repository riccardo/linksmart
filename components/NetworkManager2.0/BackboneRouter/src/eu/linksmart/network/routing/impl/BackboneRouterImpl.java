package eu.linksmart.network.routing.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.SecurityProperty;

/*
 * TODO #NM refactoring
 */
public class BackboneRouterImpl implements BackboneRouter {

	private Logger logger = Logger
			.getLogger(BackboneRouterImpl.class.getName());
	protected ComponentContext context;

	private Map<HID, Backbone> hidBackboneMap;
	private Map<String, Backbone> availableBackbones;
	private NetworkManagerCore nmCore;
	private String defaultRoute;

	private static String BACKBONE_ROUTER = BackboneRouterImpl.class
			.getSimpleName();
	private static String ROUTING_JXTA = "JXTA";
	BackboneRouterConfigurator configurator;

	protected void activate(ComponentContext context) {
		logger.info("Starting " + BACKBONE_ROUTER);

		this.context = context;

		bindNMCore((NetworkManagerCore) context
				.locateService(NetworkManagerCore.class.getSimpleName()));

		hidBackboneMap = new HashMap<HID, Backbone>();
		availableBackbones = new HashMap<String, Backbone>();
		configurator = new BackboneRouterConfigurator(this,
				context.getBundleContext());
		logger.info(BACKBONE_ROUTER + " started");
	}

	protected void deactivate(ComponentContext context) {
		logger.info(BACKBONE_ROUTER + "stopped");
	}

	protected void bindBackbone(Backbone backbone) {
		addBackbone(backbone);
	}

	protected void unbindBackbone(Backbone backbone) {
		removeBackbone(backbone);
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
		if (b == null) {
			throw new IllegalArgumentException(
					"No Backbone found to reach HID " + receiverHID);
		}
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

		// TODO: check why backbone for receiverHID would be needed - and if it
		// is needed one has to check if receiverHID is NULL (in case of
		// broadcast messagereceiverHID is NULL)
		//
		// Backbone b = (Backbone)hidBackboneMap.get(receiverHID);
		hidBackboneMap.put(senderHID, originatingBackbone);
		// TODO #NM refactoring check case when there is no core what to do
		if (nmCore != null) {
			return nmCore.receiveData(senderHID, receiverHID, data);
		} else {
			return new NMResponse("ERROR: No NMCore available!");
		}

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
	// @Override
	// public NMResponse createHid(HID tempId, HID receiverHID, byte[] data,
	// Backbone originatingBackbone) {
	//
	// try {
	// HID newHid = nmCore.createHID(data);
	// hidBackboneMap.put(newHid, originatingBackbone);
	// return nmCore.sendData(newHid, receiverHID, data);
	// } catch (RemoteException e) {
	// logger.error(e.getMessage(), e);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return new NMResponse(NMResponse.STATUS_ERROR);
	//
	// }

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
		for (Backbone bb : availableBackbones.values()) {
			logger.debug("BBRouter broadcastData over Backbone: "
					+ bb.getClass().getName());
			NMResponse response = bb.broadcastData(senderHid, data);
			if (response == null) {
				continue;
			}
			if (response.getData() == NMResponse.STATUS_ERROR) {
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
		if (b == null) {
			return null;
		} else {
			return b.toString() + ";" + b.getEndpoint(hid);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.network.routing.BackboneRouter#addRoute(eu.linksmart.network
	 * .HID, java.lang.String)
	 */
	@Override
	public boolean addRoute(HID hid, String backboneName) {
		synchronized (hidBackboneMap) {

			if (hidBackboneMap.containsKey(hid)) {
				return false;
			}
			Backbone backbone = availableBackbones.get(backboneName);
			if (backbone == null) {
				return false;
			}
			hidBackboneMap.put(hid, backbone);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.network.routing.BackboneRouter#addRouteToBackbone(eu.linksmart
	 * .network.HID, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean addRouteToBackbone(HID hid, String backboneName,
			String endpoint) {
		synchronized (hidBackboneMap) {
			if (hidBackboneMap.containsKey(hid)) {
				return false;
			}
			Backbone backbone = availableBackbones.get(backboneName);
			if (backbone == null) {
				return false;
			}
			if (!backbone.addEndpoint(hid, endpoint)) {
				return false;
			}
			hidBackboneMap.put(hid, backbone);
		}
		return true;
	}

	@Override
	public boolean removeRoute(HID hid, String backbone) {
		synchronized (hidBackboneMap) {

			if (!hidBackboneMap.get(hid).getClass().getName().equals(backbone)) {
				return false;
			}
			hidBackboneMap.remove(hid);
		}
		return true;
	}

	/**
	 * Returns a list of communication channels available to the network
	 * manager.
	 * 
	 * @return list of communication channels
	 */
	@Override
	public List<String> getAvailableBackbones() {
		return new ArrayList<String>(availableBackbones.keySet());
	}

	/**
	 * Adds a new backbone to the list of available backbones, if a backbone
	 * with this name does not exist already.
	 * 
	 * @param backbone
	 *            the Backbone to add
	 * @return whether the Backbone was added
	 */
	private boolean addBackbone(Backbone backbone) {
		if (availableBackbones.containsValue(backbone)) {
			return false;
		}
		availableBackbones.put(backbone.getClass().getName(), backbone);
		return true;
	}

	/**
	 * Removes a backbone from the list of available backbones, if it is
	 * contained therein
	 * 
	 * @param backbone
	 *            the Backbone to remove
	 * @return whether the Backbone was removed (i.e., whether it was present)
	 */
	private boolean removeBackbone(Backbone backbone) {
		// Remove all routes; need to use Collections.singleton() for
		// removeAll(Collection) - remove() would only remove one instance
		hidBackboneMap.values().removeAll(Collections.singleton(backbone));
		for (Entry<HID, Backbone> e : this.hidBackboneMap.entrySet()) {
			if (e.getValue() == backbone) {
				hidBackboneMap.remove(e.getKey());
			}
		}
		return availableBackbones.values().remove(backbone);
	}
	/**
	 * returns list of security properties available via a given backbone
	 * necessary because we do not know what backbones we have, and there is no point in creating backbones on the fly to ask them
	 * about the security types they provide
	 * @param backbone A string with the (class)name of the backbone we are interested in. This must be loaded already
	 * @return a list of security parameters configured for that backbone. See the backbone's parameters file and/or the configuraton interface for more details
	 */
	public List<SecurityProperty> getBackboneSecurityProperties(String backbone) {
		ArrayList<SecurityProperty> answer = new ArrayList<SecurityProperty>();
		if (!availableBackbones.containsKey(backbone)) {
			logger.error("Requested backbone does not exist in availableBackbones: " + backbone);
			return answer;
		} else {
			Backbone b = availableBackbones.get(backbone);
			return b.getSecurityTypesAvailable();
		}
	}
}
