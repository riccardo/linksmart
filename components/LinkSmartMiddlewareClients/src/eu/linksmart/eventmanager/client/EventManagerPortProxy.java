package eu.linksmart.eventmanager.client;

public class EventManagerPortProxy implements eu.linksmart.eventmanager.EventManagerPort {
  private String _endpoint = null;
  private eu.linksmart.eventmanager.EventManagerPort eventManagerPort = null;
  
  public EventManagerPortProxy() {
    _initEventManagerPortProxy();
  }
  
  public EventManagerPortProxy(String endpoint) {
    _endpoint = endpoint;
    _initEventManagerPortProxy();
  }
  
  private void _initEventManagerPortProxy() {
    try {
      eventManagerPort = (new eu.linksmart.eventmanager.client.EventManagerImplementationLocator()).getBasicHttpBinding_EventManagerPort();
      if (eventManagerPort != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)eventManagerPort)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)eventManagerPort)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (eventManagerPort != null)
      ((javax.xml.rpc.Stub)eventManagerPort)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public eu.linksmart.eventmanager.EventManagerPort getEventManagerPort() {
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort;
  }
  
  public boolean subscribe(java.lang.String topic, java.lang.String endpoint, int priority) throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.subscribe(topic, endpoint, priority);
  }
  
  public boolean unsubscribe(java.lang.String topic, java.lang.String endpoint) throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.unsubscribe(topic, endpoint);
  }
  
  public boolean subscribeWithHID(java.lang.String topic, java.lang.String hid, int priority) throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.subscribeWithHID(topic, hid, priority);
  }
  
  public boolean unsubscribeWithHID(java.lang.String topic, java.lang.String hid) throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.unsubscribeWithHID(topic, hid);
  }
  
  public eu.linksmart.eventmanager.Subscription[] getSubscriptions() throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.getSubscriptions();
  }
  
  public boolean clearSubscriptions(java.lang.String endpoint) throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.clearSubscriptions(endpoint);
  }
  
  public boolean clearSubscriptionsWithHID(java.lang.String hid) throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.clearSubscriptionsWithHID(hid);
  }
  
  public boolean setPriority(java.lang.String in0, int in1) throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.setPriority(in0, in1);
  }
  
  public boolean triggerRetryQueue() throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.triggerRetryQueue();
  }
  
  public boolean publish(java.lang.String topic, eu.linksmart.eventmanager.Part[] in1) throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.publish(topic, in1);
  }
  
  public boolean unsubscribeWithDescription(java.lang.String topic, java.lang.String description) throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.unsubscribeWithDescription(topic, description);
  }
  
  public boolean subscribeWithDescription(java.lang.String topic, java.lang.String description, int priority) throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.subscribeWithDescription(topic, description, priority);
  }
  
  public boolean clearSubscriptionsWithDescription(java.lang.String description) throws java.rmi.RemoteException{
    if (eventManagerPort == null)
      _initEventManagerPortProxy();
    return eventManagerPort.clearSubscriptionsWithDescription(description);
  }
  
  
}