package eu.linksmart.network.networkmanager.impl;


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
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.CommunicationSecurityManager;

/*
 * TODO #NM refactoring
 */
public class NetworkManagerCoreImpl implements NetworkManagerCore, MessageProvider{

	Logger LOG = Logger.getLogger(NetworkManagerCoreImpl.class.getName());
	
	private static String NETWORK_MGR = NetworkManagerCoreImpl.class.getSimpleName();
	
	public static String SUCCESSFULL_PROCESSING = "OK";

	private static IdentityManager identityManager;

	private Object myHID;

	private String description;

	private NetworkManagerConfigurator configurator;
	
	private static CommunicationSecurityManager commSecMgr;
	private static ConnectionManager connectionManager;
	private static Map<String,ArrayList<MessageObserver>> msgObservers = new HashMap<String,ArrayList<MessageObserver>>();

	
protected void activate(ComponentContext context) {
	
	LOG.debug(NETWORK_MGR + "started");
	
	init(context);
	
}
private void init(ComponentContext context) {
	 this.configurator = new NetworkManagerConfigurator(this, context.getBundleContext());
	 this.description = this.configurator.get(NetworkManagerConfigurator.NM_DESCRIPTION);
	this.connectionManager = new ConnectionManager();

	
}
protected void deactivate(ComponentContext context) {
	System.out.println(NETWORK_MGR + "stopped");
}

protected void bindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr){
	this.commSecMgr = commSecMgr;
	connectionManager.setCommunicationSecurityManager(commSecMgr);
}

protected void unbindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr){
	connectionManager.removeCommunicationSecurityManager();
	this.commSecMgr = null;
}

protected void bindIdentityManager(IdentityManager identityManager){
	this.identityManager = identityManager;
	Properties attributes = new Properties();
	attributes.setProperty(HIDAttribute.DESCRIPTION.name(), this.description);
	this.myHID=this.identityManager.createHID(attributes);
	}

private Properties getProperties() {
	// TODO Auto-generated method stub
	return null;
}
protected void unbindIdentityManager(IdentityManager identityMgr){
	
}

protected void bindBackboneRouter(BackboneRouter backboneRouter){
	
}


protected void unbindBackboneRouter(BackboneRouter backboneRouter){

}
@Override
public NMResponse sendData(HID sender, HID receiver, byte[] data)
throws RemoteException {
	// TODO Auto-generated method stub
	return null;
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
	// TODO Auto-generated method stub
	return null;
}
public void setDescription(String description) {
	
	Properties attributes = new Properties();
	attributes.setProperty(HIDAttribute.DESCRIPTION.name(), this.description);
	
	this.identityManager.update(this.myHID, attributes);
	
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
}
