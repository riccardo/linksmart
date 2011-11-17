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

	private Logger logger = Logger.getLogger(BackboneRouterImpl.class.getName());	
	private HashMap<HID, Backbone> hidBackboneMap;
	private List<Backbone> availableBackbones;
	private NetworkManagerCore nmCore;
	private String defaultRoute;
	
	private static String BACKBONE_ROUTER = BackboneRouterImpl.class.getSimpleName();				
	private static String ROUTING_JXTA ="JXTA"; 
	BackboneRouterConfigurator configurator;
	
	protected void activate(ComponentContext context) {
		System.out.println(BACKBONE_ROUTER + "started");
		
		hidBackboneMap = new HashMap<HID, Backbone>();
		availableBackbones =  new ArrayList<Backbone>();
		configurator = new BackboneRouterConfigurator(this, context.getBundleContext());
	}

	protected void deactivate(ComponentContext context) {
		System.out.println(BACKBONE_ROUTER + "stopped");
	}

	protected void bindBackbone(Backbone backbone) {
		if(!availableBackbones.contains(backbone))availableBackbones.add(backbone);		
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

	@Override
	public NMResponse sendData(HID senderHID, HID receiverHID,
			byte[] data) {
		Backbone b = (Backbone)hidBackboneMap.get(receiverHID);
		return b.sendData(senderHID, receiverHID, data);		
	}

	@Override
	public NMResponse receiveData(HID senderHID, HID receiverHID,
			byte[] data, Backbone originatingBackbone) {
		
		Backbone b = (Backbone)hidBackboneMap.get(receiverHID);
		hidBackboneMap.put(senderHID, originatingBackbone);
				
		return nmCore.receiveData(senderHID, receiverHID, data);				
		
	}

	@Override
	public NMResponse createHid(HID tempId, HID receiverHID,
			byte[] data, Backbone originatingBackbone) {
		
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

	@Override
	public List<Backbone> getAvailableCommunicationChannels() {
		return availableBackbones;
	}
	
	@Override
	public void applyConfigurations(Hashtable updates) {
		if(updates.containsKey(BackboneRouterImpl.BACKBONE_ROUTER)){
			this.defaultRoute = (String) configurator.get(BackboneRouterConfigurator.COMMUNICATION_TYPE);
		}			 
	}

	@Override
	public NMResponse broadcastData(HID senderHid, byte[] data) {
		boolean success = true;
		String failedBroadcast = ""; 
		for(Backbone bb : availableBackbones){
			if(bb.broadcastData(senderHid, data).getData() == NMResponse.STATUS_ERROR){
				failedBroadcast += " " + bb.getClass();
				success = false;
			}
		}		
		
		if(!success)return new NMResponse(NMResponse.STATUS_ERROR);
		else return new NMResponse(NMResponse.STATUS_SUCCESS);
	}

	@Override
	public String getRoute(HID hid) {
		// TODO Auto-generated method stub
		return null;
	}


}
