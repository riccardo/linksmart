package eu.linksmart.network.networkmanager.core.impl;


import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDAttribute;
import eu.linksmart.network.Message;
import eu.linksmart.network.MessageDistributor;
import eu.linksmart.network.MessageProcessor;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.connection.Connection;
import eu.linksmart.network.connection.ConnectionManager;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.network.service.registry.ServiceRegistry;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.tools.GetNetworkManagerStatus;
import eu.linksmart.tools.NetworkManagerApplicationStatus;

/*
 * Core implementation of NetworkManagerCore Interface
 */
public class NetworkManagerCoreImpl implements NetworkManagerCore, MessageDistributor {
	protected IdentityManager identityManager;
	
	protected BackboneRouter backboneRouter;
	
	protected ConnectionManager connectionManager;

	private ServiceRegistry serviceRegistry;

	protected HID myHID;
	
	protected String myDescription;

	/* Constants */
	private static String NETWORK_MGR_CORE = NetworkManagerCoreImpl.class.getSimpleName();
	private static final String STARTED_MESSAGE = "Started" + NETWORK_MGR_CORE;
	private static final String STARTING_MESSAGE = "Starting" + NETWORK_MGR_CORE;	
	public static String SUCCESSFULL_PROCESSING = "OK";	
	public static String ERROR_PROCESSING = "ERROR";

	/*
	 * logger
	 */
	Logger LOG = Logger.getLogger(NetworkManagerCoreImpl.class.getName());
	
	/* fields */
	private NetworkManagerCoreConfigurator configurator;	
	private CommunicationSecurityManager commSecMgr;
	private Map<String,ArrayList<MessageProcessor>> msgObservers = new HashMap<String,ArrayList<MessageProcessor>>();
	
	
protected void activate(ComponentContext context) {
	
	LOG.debug(STARTING_MESSAGE);
	
	init(context);
	
	LOG.debug(STARTED_MESSAGE);
	
}

private void init(ComponentContext context) {
	 this.configurator = new NetworkManagerCoreConfigurator(this, context.getBundleContext());
	 this.myDescription = this.configurator.get(NetworkManagerCoreConfigurator.NM_DESCRIPTION);
	 this.connectionManager = new ConnectionManager();
	 this.connectionManager.setCommunicationSecurityManager(this.commSecMgr);
	 Properties attributes = new Properties();
	 attributes.setProperty(HIDAttribute.DESCRIPTION.name(), this.myDescription);
	 this.myHID=this.identityManager.createHID(attributes);
	 
	 // Init Servlets
	 
		HttpService http = (HttpService) context.locateService("HttpService");
		try {
			http.registerServlet("/NetworkManagerStatus",
					new NetworkManagerApplicationStatus(context, this, identityManager, backboneRouter, serviceRegistry), null, null);
			http.registerServlet("/GetNetworkManagerStatus",
					new GetNetworkManagerStatus(this, identityManager, backboneRouter, serviceRegistry), null, null);
			http.registerResources("/files", "/resources", null);
		} catch (Exception e) {
			LOG.error("Error registering servlets", e);
		}
}

@Override
public HID getHID() {
	return this.myHID;
}

//XXX @Override
public HID createHID(Properties attributes, URL url) throws RemoteException {
	
	HID newHID = this.identityManager.createHID(attributes);
	
	this.serviceRegistry.registerService(newHID, url);
	
	return newHID;
}

@Override
public NMResponse sendData(HID sender, HID receiver, byte[] data)
throws RemoteException {
	
	NMResponse response = this.backboneRouter.sendData(sender,receiver,data);
	
	return response;
}

@Override
public Boolean removeHID(HID hid) throws RemoteException {
				
	Boolean serviceRemoved =this.serviceRegistry.removeService(hid);
	
	Boolean hidRemoved = this.identityManager.removeHID(hid);
	
	return (serviceRemoved && hidRemoved);
}

protected void deactivate(ComponentContext context) {
	System.out.println(NETWORK_MGR_CORE + "stopped");
}

protected void bindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr){
	this.commSecMgr = commSecMgr;
}

protected void unbindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr){
	this.connectionManager.removeCommunicationSecurityManager();
	this.commSecMgr = null;
}

protected void bindIdentityManager(IdentityManager identityManager){
	this.identityManager = identityManager;	
	}

protected void unbindIdentityManager(IdentityManager identityMgr){
	this.identityManager=null;
	
}

protected void bindBackboneRouter(BackboneRouter backboneRouter){
	this.backboneRouter = backboneRouter;
}


protected void unbindBackboneRouter(BackboneRouter backboneRouter){
	this.backboneRouter=null;
}
@Override
public NMResponse receiveData(HID sender, HID receiver, byte[] data) {
	//get connection belonging to HIDs
	Connection conn = connectionManager.getConnection(receiver, sender);
	Message msg = conn.processData(sender, receiver, data);
	String topic = msg.getTopic();
	//go through MsgObservers for additional processing
	List<MessageProcessor> observers = msgObservers.get(topic);
	if(observers != null){
		for(MessageProcessor observer : observers){
			msg = observer.processMessage(msg);
			if(msg == null || msg.getData() == null){
				NMResponse nmresp = new NMResponse();
				nmresp.setData(SUCCESSFULL_PROCESSING);
				return nmresp;
			}
		}
	}
	
	//if message is still existing it has to be forwarded
	if(msg != null && msg.getData() != null && msg.getData().length != 0){
		//check if message is not intended for host HID, if yes and it has not been processed drop it
		if(msg.getReceiverHID() == this.myHID){
			LOG.warn("Received a message which has not been processed");
			NMResponse response = new NMResponse();
			response.setData(ERROR_PROCESSING);
			return response;
		}
		//send message over sendMessage method of this and return response of it
		return sendMessage(msg);
	}else{
		NMResponse response = new NMResponse();
		response.setData(SUCCESSFULL_PROCESSING);
		return response;
	}
}

public void setDescription(String description) {
	
	Properties attributes = new Properties();
	attributes.setProperty(HIDAttribute.DESCRIPTION.name(), this.myDescription);
	
	this.identityManager.updateHIDInfo(this.myHID, attributes);
	
}

public void subscribe(String topic, MessageProcessor observer) {
	//check if topic already exists
	if(msgObservers.containsKey(topic)){
		//add new observer to topic
		if(msgObservers.get(topic).contains(observer)){
			//observer is already in list
			return;
		}else{
			msgObservers.get(topic).add(observer);
		}
	}else{
		//create topic and add observer
		msgObservers.put(topic, new ArrayList<MessageProcessor>());
		msgObservers.get(topic).add(observer);
	}

}

public void unsubscribe(String topic, MessageProcessor observer) {
	if(msgObservers.containsKey(topic)){
		msgObservers.get(topic).remove(observer);
	}
}

@Override
public HID createHID(byte[] data) throws IOException {
		
	Properties attributes = this.connectionManager.getHIDAttributes(data);
		
	HID newHID = this.identityManager.createHID(attributes);
	
	return newHID;
}
@Override
public NMResponse broadcastMessage(Message message) {
	HID senderHID = message.getSenderHID();
	byte[] data;
	try {
		data = this.connectionManager.getBroadcastConnection(senderHID).processMessage(message);
	} catch (Exception e) {
		LOG.warn("Could not create packet from message from HID: " + message.getSenderHID(),e);
		NMResponse response = new NMResponse();
		response.setData(ERROR_PROCESSING);
		return response;
	}
	NMResponse response = this.backboneRouter.broadcastData(senderHID, data);
	
	return response;
}

@Override
public NMResponse sendMessage(Message message) {
	
	HID senderHID = message.getReceiverHID();
	
	HID receiverHID = message.getSenderHID();
	
	Connection connection = this.connectionManager.getConnection(receiverHID, senderHID);
	
	byte[] data;
	try {
		data = connection.processMessage(message);
	} catch (Exception e) {
		LOG.warn("Could not create packet from message from HID: " + message.getSenderHID());
		NMResponse response = new NMResponse();
		response.setData(ERROR_PROCESSING);
		return response;
	}	
	
	NMResponse response = this.backboneRouter.sendData(senderHID, receiverHID, data);
	
	return response;
}

/**
 * Sets the number of minutes before a connection is closed 
 * @param timeout
 */
protected void setConnectionTimeout(int timeout) {
	this.connectionManager.setConnectionTimeout(timeout);	
}

}
