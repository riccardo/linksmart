/**
 * NetworkManagerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.network.networkmanager.client;

public class NetworkManagerServiceLocator extends org.apache.axis.client.Service implements eu.linksmart.network.networkmanager.client.NetworkManagerService {

    public NetworkManagerServiceLocator() {
    }


    public NetworkManagerServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public NetworkManagerServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for NetworkManager
    private java.lang.String NetworkManager_address = "http://localhost:8082/axis/services/NetworkManager";

    public java.lang.String getNetworkManagerAddress() {
        return NetworkManager_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String NetworkManagerWSDDServiceName = "NetworkManager";

    public java.lang.String getNetworkManagerWSDDServiceName() {
        return NetworkManagerWSDDServiceName;
    }

    public void setNetworkManagerWSDDServiceName(java.lang.String name) {
        NetworkManagerWSDDServiceName = name;
    }

    public eu.linksmart.network.networkmanager.NetworkManager getNetworkManager() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(NetworkManager_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getNetworkManager(endpoint);
    }

    public eu.linksmart.network.networkmanager.NetworkManager getNetworkManager(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            eu.linksmart.network.networkmanager.client.NetworkManagerSoapBindingStub _stub = new eu.linksmart.network.networkmanager.client.NetworkManagerSoapBindingStub(portAddress, this);
            _stub.setPortName(getNetworkManagerWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setNetworkManagerEndpointAddress(java.lang.String address) {
        NetworkManager_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (eu.linksmart.network.networkmanager.NetworkManager.class.isAssignableFrom(serviceEndpointInterface)) {
                eu.linksmart.network.networkmanager.client.NetworkManagerSoapBindingStub _stub = new eu.linksmart.network.networkmanager.client.NetworkManagerSoapBindingStub(new java.net.URL(NetworkManager_address), this);
                _stub.setPortName(getNetworkManagerWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("NetworkManager".equals(inputPortName)) {
            return getNetworkManager();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://networkmanager.network.linksmart.eu", "NetworkManagerService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://networkmanager.network.linksmart.eu", "NetworkManager"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("NetworkManager".equals(portName)) {
            setNetworkManagerEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
