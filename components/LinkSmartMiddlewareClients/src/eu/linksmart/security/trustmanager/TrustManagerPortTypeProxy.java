package eu.linksmart.security.trustmanager;

public class TrustManagerPortTypeProxy implements eu.linksmart.security.trustmanager.TrustManagerPortType {
  private String _endpoint = null;
  private eu.linksmart.security.trustmanager.TrustManagerPortType trustManagerPortType = null;
  
  public TrustManagerPortTypeProxy() {
    _initTrustManagerPortTypeProxy();
  }
  
  public TrustManagerPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initTrustManagerPortTypeProxy();
  }
  
  private void _initTrustManagerPortTypeProxy() {
    try {
      trustManagerPortType = (new eu.linksmart.security.trustmanager.TrustManagerLocator()).getTrustManagerPort();
      if (trustManagerPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)trustManagerPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)trustManagerPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (trustManagerPortType != null)
      ((javax.xml.rpc.Stub)trustManagerPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public eu.linksmart.security.trustmanager.TrustManagerPortType getTrustManagerPortType() {
    if (trustManagerPortType == null)
      _initTrustManagerPortTypeProxy();
    return trustManagerPortType;
  }
  
  public java.lang.String getTrustToken(java.lang.String arg0) throws java.rmi.RemoteException{
    if (trustManagerPortType == null)
      _initTrustManagerPortTypeProxy();
    return trustManagerPortType.getTrustToken(arg0);
  }
  
  public double getTrustValue(java.lang.String arg0) throws java.rmi.RemoteException{
    if (trustManagerPortType == null)
      _initTrustManagerPortTypeProxy();
    return trustManagerPortType.getTrustValue(arg0);
  }
  
  public java.lang.String createTrustToken() throws java.rmi.RemoteException{
    if (trustManagerPortType == null)
      _initTrustManagerPortTypeProxy();
    return trustManagerPortType.createTrustToken();
  }
  
  public boolean createTrustTokenWithFriendlyName(java.lang.String arg0) throws java.rmi.RemoteException{
    if (trustManagerPortType == null)
      _initTrustManagerPortTypeProxy();
    return trustManagerPortType.createTrustTokenWithFriendlyName(arg0);
  }
  
  public double getTrustValueWithIdentifier(java.lang.String arg0, java.lang.String arg1) throws java.rmi.RemoteException{
    if (trustManagerPortType == null)
      _initTrustManagerPortTypeProxy();
    return trustManagerPortType.getTrustValueWithIdentifier(arg0, arg1);
  }
  
  
}