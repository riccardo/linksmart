package eu.linksmart.network.networkmanager.client;

public class NetworkManagerPortTypeProxy implements eu.linksmart.network.networkmanager.client.NetworkManagerPortType {
  private String _endpoint = null;
  private eu.linksmart.network.networkmanager.client.NetworkManagerPortType networkManagerPortType = null;
  
  public NetworkManagerPortTypeProxy() {
    _initNetworkManagerPortTypeProxy();
  }
  
  public NetworkManagerPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initNetworkManagerPortTypeProxy();
  }
  
  private void _initNetworkManagerPortTypeProxy() {
    try {
      networkManagerPortType = (new eu.linksmart.network.networkmanager.client.NetworkManagerLocator()).getNetworkManagerPort();
      if (networkManagerPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)networkManagerPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)networkManagerPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (networkManagerPortType != null)
      ((javax.xml.rpc.Stub)networkManagerPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public eu.linksmart.network.networkmanager.client.NetworkManagerPortType getNetworkManagerPortType() {
    if (networkManagerPortType == null)
      _initNetworkManagerPortTypeProxy();
    return networkManagerPortType;
  }
  
  public boolean removeService(eu.linksmart.network.client.VirtualAddress arg0) throws java.rmi.RemoteException{
    if (networkManagerPortType == null)
      _initNetworkManagerPortTypeProxy();
    return networkManagerPortType.removeService(arg0);
  }
  
  public eu.linksmart.network.client.Registration registerService(eu.linksmart.utils.Part[] arg0, java.lang.String arg1, java.lang.String arg2) throws java.rmi.RemoteException{
    if (networkManagerPortType == null)
      _initNetworkManagerPortTypeProxy();
    return networkManagerPortType.registerService(arg0, arg1, arg2);
  }
  
  public eu.linksmart.network.client.NMResponse sendData(eu.linksmart.network.client.VirtualAddress arg0, eu.linksmart.network.client.VirtualAddress arg1, byte[] arg2, boolean arg3) throws java.rmi.RemoteException{
    if (networkManagerPortType == null)
      _initNetworkManagerPortTypeProxy();
    return networkManagerPortType.sendData(arg0, arg1, arg2, arg3);
  }
  
  public java.lang.String[] getAvailableBackbones() throws java.rmi.RemoteException{
    if (networkManagerPortType == null)
      _initNetworkManagerPortTypeProxy();
    return networkManagerPortType.getAvailableBackbones();
  }
  
  public eu.linksmart.network.client.Registration[] getServiceByAttributes1(eu.linksmart.utils.Part[] arg0, long arg1, boolean arg2, boolean arg3) throws java.rmi.RemoteException{
    if (networkManagerPortType == null)
      _initNetworkManagerPortTypeProxy();
    return networkManagerPortType.getServiceByAttributes1(arg0, arg1, arg2, arg3);
  }
  
  public eu.linksmart.network.client.Registration[] getServiceByAttributes(eu.linksmart.utils.Part[] arg0) throws java.rmi.RemoteException{
    if (networkManagerPortType == null)
      _initNetworkManagerPortTypeProxy();
    return networkManagerPortType.getServiceByAttributes(arg0);
  }
  
  public eu.linksmart.network.client.VirtualAddress getService() throws java.rmi.RemoteException{
    if (networkManagerPortType == null)
      _initNetworkManagerPortTypeProxy();
    return networkManagerPortType.getService();
  }
  
  public eu.linksmart.network.client.Registration getServiceByPID(java.lang.String arg0) throws java.rmi.RemoteException, java.lang.IllegalArgumentException{
    if (networkManagerPortType == null)
      _initNetworkManagerPortTypeProxy();
    return networkManagerPortType.getServiceByPID(arg0);
  }
  
  public eu.linksmart.network.client.Registration[] getServiceByQuery(java.lang.String arg0) throws java.rmi.RemoteException{
    if (networkManagerPortType == null)
      _initNetworkManagerPortTypeProxy();
    return networkManagerPortType.getServiceByQuery(arg0);
  }
  
  public eu.linksmart.network.client.Registration[] getServiceByDescription(java.lang.String arg0) throws java.rmi.RemoteException{
    if (networkManagerPortType == null)
      _initNetworkManagerPortTypeProxy();
    return networkManagerPortType.getServiceByDescription(arg0);
  }
  
  
}