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

public class BackboneRouterImpl implements BackboneRouter {

	private Logger logger = Logger
	.getLogger(BackboneRouterImpl.class.getName());
	protected ComponentContext context;

	private Map<HID, Backbone> hidBackboneMap = new HashMap<HID, Backbone>();
	private Map<String, Backbone> availableBackbones = new HashMap<String, Backbone>();
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

		configurator = new BackboneRouterConfigurator(this,
				context.getBundleContext());
		configurator.registerConfiguration();
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
	 * Sends a message over the communication channel from which the Hid
	 * came.
	 * 
	 * @param senderHID HID of the sender
	 * @param receiverHID HID of the receiver
	 * @param data data to be sent
	 * @return success status response of the network manager
	 */
	@Override
	public NMResponse sendDataSynch(HID senderHID, HID receiverHID, byte[] data) {
		Backbone b = (Backbone) hidBackboneMap.get(receiverHID);
		if (b == null) {
			throw new IllegalArgumentException(
					"No Backbone found to reach HID " + receiverHID);
		}
		return b.sendDataSynch(senderHID, receiverHID, data);
	}
	
	/**
	 * Sends a message over the communication channel from which the Hid
	 * came.
	 * 
	 * @param senderHID HID of the sender
	 * @param receiverHID HID of the receiver
	 * @param data data to be sent
	 * @return success status response of the network manager
	 */
	@Override
	public NMResponse sendDataAsynch(HID senderHID, HID receiverHID, byte[] data) {
		Backbone b = (Backbone) hidBackboneMap.get(receiverHID);
		if (b == null) {
			throw new IllegalArgumentException(
					"No Backbone found to reach HID " + receiverHID);
		}
		return b.sendDataAsynch(senderHID, receiverHID, data);
	}

	/**
	 * Receives a message which also specifies the communication channel used by
	 * the sender. This will then update the list of HIDs and which backbone
	 * they use.
	 * 
	 * @param senderHID HID of the sender
	 * @param receiverHID HID of the receiver
	 * @param data data to be sent
	 * @param originatingBackbone which backbone is used
	 * @param true to process request synchronously
	 * @return success status response of the network manager
	 */
	@Override
	public NMResponse receiveDataSynch(HID senderHID, HID receiverHID, byte[] data,
			Backbone originatingBackbone) {

		return receiveData(senderHID, receiverHID, data, originatingBackbone, true);
	}
	
	/**
	 * Receives a message which also specifies the communication channel used by
	 * the sender. This will then update the list of HIDs and which backbone
	 * they use.
	 * 
	 * @param senderHID HID of the sender
	 * @param receiverHID HID of the receiver
	 * @param data data to be sent
	 * @param originatingBackbone which backbone is used
	 * @param true to process request synchronously
	 * @return success status response of the network manager
	 */
	@Override
	public NMResponse receiveDataAsynch(HID senderHID, HID receiverHID, byte[] data,
			Backbone originatingBackbone) {

		return receiveData(senderHID, receiverHID, data, originatingBackbone, false);
	}

	/**
	 * Method to receive data as synchronous and asynchronous are handled the same way
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @param originatingBackbone
	 * @param synch
	 * @return
	 */
	private NMResponse receiveData(HID senderHID, HID receiverHID, byte[] data,
			Backbone originatingBackbone, boolean synch) {

		// TODO: check why backbone for receiverHID would be needed - and if it
		// is needed one has to check if receiverHID is NULL (in case of
		//		 broadcast messagereceiverHID is NULL)
		Backbone b = (Backbone)hidBackboneMap.get(senderHID);
		if (b == null) {
			hidBackboneMap.put(senderHID, originatingBackbone);
		}

		// TODO #NM refactoring check case when there is no core what to do
		// Has to be considered as future feature for relay nodes
		if (nmCore != null) {
			if(synch) {
				return nmCore.receiveDataSynch(senderHID, receiverHID, data);
			} else {
				return	nmCore.receiveDataAsynch(senderHID, receiverHID, data);
			}
		} else {
			return new NMResponse(NMResponse.STATUS_ERROR);
		}
	}

	/**
	 * this method is invoked by backbone when a service requests a new HID to
	 * the network manager. this msg will be firstly accepted by backbone, and
	 * then propagated to the NMCore and IdManager
	 * 
	 * @param tempId temporary HID of the sender
	 * @param receiverHID HID of the receiver
	 * @param data data to be sent
	 * @param originatingBackbone which backbone is used
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
	// logger.error(e.getMessage(), e);
	// }
	// return new NMResponse(NMResponse.STATUS_ERROR);
	//
	// }

	/**
	 * This function is called when the configuration is updated from the web
	 * page.
	 * 
	 * @param updates updated configuration data
	 */
	@Override
	public void applyConfigurations(Hashtable updates) {
		if (updates.containsKey(BackboneRouterConfigurator.COMMUNICATION_TYPE)) {
			this.defaultRoute = (String) configurator
			.get(BackboneRouterConfigurator.COMMUNICATION_TYPE);
		}
	}

	/**
	 * Broadcasts a message over the all communication channel.
	 * 
	 * @param senderHid HID of the sender
	 * @param data data to be sent
	 * @return success if data could be delivered on at least one backbone. error if no backbone was successful
	 */
	@Override
	public NMResponse broadcastData(HID senderHid, byte[] data) {
		boolean success = false;
		for (Backbone bb : availableBackbones.values()) {
			logger.debug("BBRouter broadcastData (from " + senderHid + ") over Backbone: "
					+ bb.getClass().getName());
			NMResponse response = bb.broadcastData(senderHid, data);
			if (response != null && response.getStatus() == NMResponse.STATUS_SUCCESS) {
				success = true;
			}
		}

		if (success)
			return new NMResponse(NMResponse.STATUS_SUCCESS); 
		else
			return new NMResponse(NMResponse.STATUS_ERROR);
	}

	/**
	 * This function returns information about the backbone of the HID
	 * The return format is BackboneType:BackboneAddresse
	 * Example: BackboneSOAP:http://202.12.11.11/axis/services
	 * 
	 * @param hid HID of the node to request the route from
	 * @return BackboneType:BackboneAddresse
	 */
	@Override
	public String getRoute(HID hid) {
		Backbone b = hidBackboneMap.get(hid);
		if (b == null) {
			return null;
		} else {
			return b.getName().concat(":").concat(b.getEndpoint(hid));
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

	@Override 
	public void addRouteForRemoteHID(HID senderHID, HID remoteHID) {
		Backbone senderBackbone = hidBackboneMap.get(senderHID);
		if (senderBackbone != null){
			addRoute(remoteHID, senderBackbone.getName());
			senderBackbone.addEndpointForRemoteHID(senderHID, remoteHID);
		}
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

	@Override
	public String getBackboneType(HID hid) {
		Backbone backbone = hidBackboneMap.get(hid);
		if (backbone != null) {
			return backbone.getName();
		} else {
			return null;
		}
	}
}
