package eu.linksmart.network.networkmanager.core.impl;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.MessageObserver;
import eu.linksmart.network.MessageProvider;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.connection.Connection;
import eu.linksmart.network.connection.ConnectionManager;
import eu.linksmart.network.HIDAttribute;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.application.impl.NetworkManagerApplicationImpl;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.CommunicationSecurityManager;

/*
 * Core implementation of NetworkManagerCore Interface
 */
public class NetworkManagerCoreImpl extends NetworkManagerApplicationImpl implements NetworkManagerCore, MessageProvider {

	/* Constants */
	private static String NETWORK_MGR_CORE = NetworkManagerCoreImpl.class.getSimpleName();
	private static final String STARTED_MESSAGE = "Started" + NETWORK_MGR_CORE;
	private static final String STARTING_MESSAGE = "Starting" + NETWORK_MGR_CORE;	
	public static String SUCCESSFULL_PROCESSING = "OK";	

	/*
	 * logger
	 */
	Logger LOG = Logger.getLogger(NetworkManagerCoreImpl.class.getName());
	
	/* fields */
	private NetworkManagerCoreConfigurator configurator;	
	private CommunicationSecurityManager commSecMgr;
	private Map<String,ArrayList<MessageObserver>> msgObservers = new HashMap<String,ArrayList<MessageObserver>>();
	
	
protected void activate(ComponentContext context) {
	
	LOG.debug(STARTING_MESSAGE);
	
	init(context);
	
	LOG.debug(STARTED_MESSAGE);
	
}
private void init(ComponentContext context) {
	 this.configurator = new NetworkManagerCoreConfigurator(this, context.getBundleContext());
	 this.myDescription = this.configurator.get(NetworkManagerCoreConfigurator.NM_DESCRIPTION);
	 this.connectionManager = new ConnectionManager();

	
}
protected void deactivate(ComponentContext context) {
	System.out.println(NETWORK_MGR_CORE + "stopped");
}

protected void bindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr){
	this.commSecMgr = commSecMgr;
	this.connectionManager.setCommunicationSecurityManager(this.commSecMgr);
}

protected void unbindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr){
	this.connectionManager.removeCommunicationSecurityManager();
	this.commSecMgr = null;
}

protected void bindIdentityManager(IdentityManager identityManager){
	this.identityManager = identityManager;
	Properties attributes = new Properties();
	attributes.setProperty(HIDAttribute.DESCRIPTION.name(), this.myDescription);
	this.myHID=this.identityManager.createHID(attributes);
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
	List<MessageObserver> observers = msgObservers.get(topic);
	if(observers != null){
		for(MessageObserver observer : observers){
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
		//TODO #NM refactoring 
		//check if message is not intended for host HID, if yes and it has not been processed drop it
		
		//send message over sendMessage method of this and return response of it
		return null;
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

public void subscribe(String topic, MessageObserver observer) {
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
		msgObservers.put(topic, new ArrayList<MessageObserver>());
		msgObservers.get(topic).add(observer);
	}

}

public void unsubscribe(String topic, MessageObserver observer) {
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
	
	HID receiverHID = message.getReceiverHID();
	HID senderHID = message.getSenderHID();
	byte [] data = this.connectionManager.getConnection(receiverHID, senderHID).processMessage(message);
	
	NMResponse response = this.backboneRouter.broadcastData(senderHID, data);
	
	return response;
}

@Override
public NMResponse sendMessage(Message message) {
	
	HID senderHID = message.getReceiverHID();
	
	HID receiverHID = message.getSenderHID();
	
	Connection connection = this.connectionManager.getConnection(receiverHID, senderHID);
	
	byte[] data = connection.processMessage(message);	
	
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
