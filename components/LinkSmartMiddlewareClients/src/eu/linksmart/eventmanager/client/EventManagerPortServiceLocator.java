/**
 * EventManagerImplementationLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.eventmanager.client;

public class EventManagerPortServiceLocator extends org.apache.axis.client.Service implements eu.linksmart.eventmanager.client.EventManagerPortService {

    public EventManagerPortServiceLocator() {
    }


    public EventManagerPortServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EventManagerPortServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BasicHttpBinding_EventManagerPort
    private java.lang.String BasicHttpBinding_EventManagerPort_address = "http://129.26.160.243:8124/Service";

    public java.lang.String getBasicHttpBinding_EventManagerPortAddress() {
        return BasicHttpBinding_EventManagerPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BasicHttpBinding_EventManagerPortWSDDServiceName = "BasicHttpBinding_EventManagerPort";

    public java.lang.String getBasicHttpBinding_EventManagerPortWSDDServiceName() {
        return BasicHttpBinding_EventManagerPortWSDDServiceName;
    }

    public void setBasicHttpBinding_EventManagerPortWSDDServiceName(java.lang.String name) {
        BasicHttpBinding_EventManagerPortWSDDServiceName = name;
    }

    public eu.linksmart.eventmanager.EventManagerPort getBasicHttpBinding_EventManagerPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicHttpBinding_EventManagerPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicHttpBinding_EventManagerPort(endpoint);
    }

    public eu.linksmart.eventmanager.EventManagerPort getBasicHttpBinding_EventManagerPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            eu.linksmart.eventmanager.client.BasicHttpBinding_EventManagerPortStub _stub = new eu.linksmart.eventmanager.client.BasicHttpBinding_EventManagerPortStub(portAddress, this);
            _stub.setPortName(getBasicHttpBinding_EventManagerPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBasicHttpBinding_EventManagerPortEndpointAddress(java.lang.String address) {
        BasicHttpBinding_EventManagerPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (eu.linksmart.eventmanager.EventManagerPort.class.isAssignableFrom(serviceEndpointInterface)) {
                eu.linksmart.eventmanager.client.BasicHttpBinding_EventManagerPortStub _stub = new eu.linksmart.eventmanager.client.BasicHttpBinding_EventManagerPortStub(new java.net.URL(BasicHttpBinding_EventManagerPort_address), this);
                _stub.setPortName(getBasicHttpBinding_EventManagerPortWSDDServiceName());
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
        if ("BasicHttpBinding_EventManagerPort".equals(inputPortName)) {
            return getBasicHttpBinding_EventManagerPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://eventmanager.linksmart.eu", "EventManagerImplementation");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://eventmanager.linksmart.eu", "BasicHttpBinding_EventManagerPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("BasicHttpBinding_EventManagerPort".equals(portName)) {
            setBasicHttpBinding_EventManagerPortEndpointAddress(address);
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
