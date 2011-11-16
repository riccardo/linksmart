package eu.linksmart.network.networkmanager.core.impl;


import java.rmi.RemoteException;
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
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.identity.IdentityManager.HIDAttribute;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.networkmanager.impl.NetworkManagerCoreConfigurator;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.CommunicationSecurityManager;

/*
 * Core implementation of NetworkManagerCore Interface
 */
public class NetworkManagerCoreImpl implements NetworkManagerCore, MessageProvider{

	private static final String STARTED = "started";

	private static final String STARTING = "Starting";

	Logger LOG = Logger.getLogger(NetworkManagerCoreImpl.class.getName());
	
	private static String NETWORK_MGR_CORE = NetworkManagerCoreImpl.class.getSimpleName();
	
	public static String SUCCESSFULL_PROCESSING = "OK";	

	private HID myHID;

	private String myDescription;

	private NetworkManagerCoreConfigurator configurator;
	
	private IdentityManager identityManager;
	private CommunicationSecurityManager commSecMgr;
	private ConnectionManager connectionManager;
	private BackboneRouter backboneRouter;
	
	private Map<String,ArrayList<MessageObserver>> msgObservers = new HashMap<String,ArrayList<MessageObserver>>();
	
	
protected void activate(ComponentContext context) {
	
	LOG.debug(STARTING + NETWORK_MGR_CORE);
	
	init(context);
	
	LOG.debug(NETWORK_MGR_CORE + STARTED);
	
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
	connectionManager.setCommunicationSecurityManager(this.commSecMgr);
}

protected void unbindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr){
	connectionManager.removeCommunicationSecurityManager();
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
public NMResponse sendData(HID sender, HID receiver, byte[] data)
throws RemoteException {
	
	NMResponse response = this.backboneRouter.sendData(sender,receiver,data);
	
	return response;
}
@Override
public NMResponse receiveData(HID sender, HID receiver, byte[] data)
throws RemoteException {
	//get connection belonging to HIDs
	Connection conn = connectionManager.getConnection(receiver, sender);
	Message msg = conn.processData(data);
	String topic = msg.getTopic();
	//go through MsgObservers for additional processing
	List<MessageObserver> observers = msgObservers.get(topic);
	if(observers != null){
		for(MessageObserver observer : observers){
			observer.processMessage(msg);
		}
	}
	
	//if message is still existing it has to be forwarded
	if(msg.getData() != null && msg.getData().length != 0){
		//TODO #NM refactoring send message over sendMessage method of self and return response of it
		return null;
	}else{
		NMResponse response = new NMResponse();
		response.setData(SUCCESSFULL_PROCESSING);
		return response;
	}
}

@Override
public HID getHID() {
	return this.myHID;
}
public void setDescription(String description) {
	
	Properties attributes = new Properties();
	attributes.setProperty(HIDAttribute.DESCRIPTION.name(), this.myDescription);
	
	this.identityManager.update(this.myHID, attributes);
	
}

@Override
public NMResponse sendMessage(Message message) throws RemoteException {
	
	HID senderHID = message.getReceiverHID();
	
	HID receiverHID = message.getSenderHID();
	
	Connection connection = this.connectionManager.getConnection(receiverHID, senderHID);
	
	byte[] data = connection.processMessage(message);	
	
	NMResponse response = this.backboneRouter.sendData(senderHID, receiverHID, data);
	
	return response;
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
public HID createHID(byte[] data) throws RemoteException {
		
	Properties attributes = this.connectionManager.getHIDAttributes(data);
		
	HID newHID = this.identityManager.createHID(attributes);
	
	return newHID;
}
@Override
public HID createHID(Properties attributes) throws RemoteException {
	
	HID newHID = this.identityManager.createHID(attributes);
	
	return newHID;
}

}
