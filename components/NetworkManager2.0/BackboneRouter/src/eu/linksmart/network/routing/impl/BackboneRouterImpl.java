package eu.linksmart.network.routing.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.network.routing.RouteEntry;
import eu.linksmart.security.communication.SecurityProperty;

public class BackboneRouterImpl implements BackboneRouter {

	

	private Logger logger = Logger
	.getLogger(BackboneRouterImpl.class.getName());
	protected ComponentContext context;
	
	private Map<HID, Backbone> activeRouteMap = new HashMap<HID, Backbone>();
	
	/**
	 * <HID, <backboneName, endpoint> >
	 */
	private Map<HID, List<RouteEntry> > potentialRouteMap = new ConcurrentHashMap<HID, List<RouteEntry> >();
	
	private Map<String, Backbone> availableBackbones = new ConcurrentHashMap<String, Backbone>();
	private NetworkManagerCore nmCore;
	
	private static String BACKBONE_ROUTER = BackboneRouterImpl.class.getSimpleName();
//	private static String ROUTING_JXTA = "JXTA";
	BackboneRouterConfigurator configurator;
	Object backboneAddingLock = new Object();

	protected void activate(ComponentContext context) {
		logger.info("Starting " + BACKBONE_ROUTER);

		this.context = context;

		bindNMCore((NetworkManagerCore) context.locateService(NetworkManagerCore.class.getSimpleName()));

		configurator = new BackboneRouterConfigurator(this,	context.getBundleContext());
		configurator.registerConfiguration();
		logger.info(BACKBONE_ROUTER + " started");
	}

	protected void deactivate(ComponentContext context) {
		logger.info(BACKBONE_ROUTER + "stopped");
	}

	protected void bindBackbone(Backbone backbone) {
		
		if(addBackbone(backbone)){		
			movePotentialToActiveRoutes(backbone);
		}
		
	}

	private void movePotentialToActiveRoutes(Backbone backbone) {
		
		synchronized(backboneAddingLock){
			List<HID> movedHIDList = new ArrayList<HID>();
			
			logger.debug("Moving potential to active routes for backbone " + backbone.getName());
			
			if(!potentialRouteMap.isEmpty()){
				Iterator<Entry<HID, List<RouteEntry> >> iter = potentialRouteMap.entrySet().iterator();
								
				while(iter.hasNext()){
					Map.Entry<HID,List<RouteEntry> > entry = (Map.Entry<HID, List<RouteEntry>>) iter.next();
					
					List<RouteEntry> routeEntryList = entry.getValue();
					
					for(RouteEntry routeEntry : routeEntryList){
							String backboneName = routeEntry.getBackboneName();
							
							if((backbone.getName()!=null) && backbone.getName().contains(backboneName)){
								
								HID hid = (HID) entry.getKey();
								
								String endpoint = routeEntry.getEndpoint();
								
								if(endpoint!=null){
									backbone.addEndpoint(hid, endpoint);
								}
								
								activeRouteMap.put(hid, backbone);
								
								movedHIDList.add(hid);
								
							}
						}
					
				}
				
				//tell nmcore which hids have new securityproperties
				if(backbone.getSecurityTypesRequired()!=null){
					nmCore.updateSecurityProperties(movedHIDList, backbone.getSecurityTypesRequired());
					for(HID hid : movedHIDList){
						potentialRouteMap.remove(hid);
					}
				}
			
			}
		}
		
		
	}
	
	private void removeActiveRoutes(Backbone backbone){
		
		synchronized (backboneAddingLock) {
			List<HID> toBeRemovedHIDList = new ArrayList<HID>();
			
			if(!activeRouteMap.isEmpty()){
				logger.debug("Removing active routes reachable over unbound backbone " + backbone.getName());
				
				Iterator<Entry<HID, Backbone>> iter = activeRouteMap.entrySet().iterator();
				
				while(iter.hasNext()){
					Map.Entry <HID, Backbone> entry = (Map.Entry<HID,Backbone>) iter.next();
					
					Backbone backboneRef = (Backbone) entry.getValue();
					
					if(backboneRef.equals(backbone)){
						
						HID hid = (HID) entry.getKey();
												
						toBeRemovedHIDList.add(hid);
						
					}
					
					
				}

				for(HID hid : toBeRemovedHIDList){
					activeRouteMap.remove(hid);
				}
			}
		}
	}

	protected void unbindBackbone(Backbone backbone) {
		
		removeActiveRoutes(backbone);
		
		availableBackbones.values().remove(backbone);
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
		Backbone b = (Backbone) activeRouteMap.get(receiverHID);
		if (b == null) {
			
			NMResponse nmResponse = new NMResponse(NMResponse.STATUS_ERROR);
			nmResponse.setMessage("Currently the backbone that is assigned to this HID is not available.");
			
			return nmResponse;
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
		Backbone b = (Backbone) activeRouteMap.get(receiverHID);
		if (b == null) {
			NMResponse nmResponse = new NMResponse(NMResponse.STATUS_ERROR);
			nmResponse.setMessage("Currently the backbone that is assigned to this HID is not available.");
			
			return nmResponse;
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

		Backbone b = (Backbone)activeRouteMap.get(senderHID);
		if (b == null) {
			activeRouteMap.put(senderHID, originatingBackbone);
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
	// activeRouteMap.put(newHid, originatingBackbone);
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
	@SuppressWarnings("rawtypes")
	public void applyConfigurations(Hashtable updates) {
		if (updates.containsKey(BackboneRouterConfigurator.COMMUNICATION_TYPE)) {
			logger.info("default route: "+(String) configurator.get(BackboneRouterConfigurator.COMMUNICATION_TYPE));
		}
	}

	// Ruft jeden Backbone auf, der gefunden wird, um Nachricht zu broadcasten
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
		Backbone b = activeRouteMap.get(hid);
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
		return processAddingRoute(hid, backboneName, null);
							
	}

	private boolean processAddingRoute(HID hid, String backboneName, String endpoint) {
		
		
		
		synchronized(backboneAddingLock){
			
			if(backboneName==null || backboneName.isEmpty())
			{
				return false;
			}
			
			Backbone backbone = availableBackbones.get(backboneName);
			
			if (backbone == null) {
				
				//Put hid in potential route map
				
				if(potentialRouteMap.containsKey(hid)){
					return false;
				}
				else{
					
					//TODO LinkSmart Developer In future, if more than one backbone can be assigned to HID, 
					//first check if map is available, and then add backbone and endpoint.
					RouteEntry routeEntry = new RouteEntry(backboneName, endpoint);
					
					List<RouteEntry> routeEntryList = new ArrayList<RouteEntry>();
					
					routeEntryList.add(routeEntry);
					
					potentialRouteMap.put(hid, routeEntryList);
					
					return true;
				}
				
			}
			else{	
				
				//Assign a backbone to the hid route
				
				if(activeRouteMap.containsKey(hid)){
					return false;
				}
				else{
					
					if(endpoint!=null){
						if (!backbone.addEndpoint(hid, endpoint)) {
							return false;
						}
					}
					
					activeRouteMap.put(hid, backbone);
											
					return true;
				}
				
			}
		}
		
	}

	@Override 
	public void addRouteForRemoteHID(HID senderHID, HID remoteHID) {
		Backbone senderBackbone = activeRouteMap.get(senderHID);
		if (senderBackbone != null) {
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
		
		if(hid==null){
			return false;
		}
		
		if(endpoint==null || endpoint.isEmpty()){
			return false;
		}
		
		return processAddingRoute(hid, backboneName, endpoint);
	}

	@Override
	public boolean removeRoute(HID hid, String backbone) {
		synchronized (backboneAddingLock) {
			
			if(potentialRouteMap.get(hid) !=null){
				return (potentialRouteMap.remove(hid)!=null);
			}
			
			if (activeRouteMap.get(hid) == null || 
					!activeRouteMap.get(hid).getClass().getName().equals(backbone)) {
				
				return false;
			}
			activeRouteMap.remove(hid);
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
		
		if(backbone==null ){
			return false;
		}
		
		if (availableBackbones.containsValue(backbone)) {
			return false;
		}
		availableBackbones.put(backbone.getClass().getName(), backbone);
		return true;
	}

	/**
	 * Returns list of security properties required via a given backbone.
	 * @param backbone A string with the (class)name of the backbone we are interested in.
	 * @return a list of security parameters configured for that backbone. See the backbone's parameters file and/or the configuraton interface for more details
	 * ,null if backbone not available yet
	 */
	public List<SecurityProperty> getBackboneSecurityProperties(String backbone) {
		if (!availableBackbones.containsKey(backbone)) {
			return null;
		} else {
			Backbone b = availableBackbones.get(backbone);
			return b.getSecurityTypesRequired();
		}
	}

	@Override
	public Map<HID, Backbone> getCopyOfActiveRouteMap() {
		HashMap<HID, Backbone> copiedMap = new HashMap<HID, Backbone>();
		copiedMap.putAll(activeRouteMap);
		return copiedMap;
	}

	@Override
	public Map<HID, List<RouteEntry>> getCopyOfPotentialRouteMap() {
		HashMap<HID, List<RouteEntry>> copiedMap = new HashMap<HID, List<RouteEntry>>();
		copiedMap.putAll(potentialRouteMap);
		return copiedMap;
	}

}
