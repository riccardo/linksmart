/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
/**
 * TrustManagerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.security.trustmanager.client;

public class TrustManagerServiceLocator extends org.apache.axis.client.Service implements eu.linksmart.security.trustmanager.client.TrustManagerService {

    public TrustManagerServiceLocator() {
    }


    public TrustManagerServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public TrustManagerServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for TrustManager
    private java.lang.String TrustManager_address = "http://localhost:8082/axis/services/TrustManager";

    public java.lang.String getTrustManagerAddress() {
        return TrustManager_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TrustManagerWSDDServiceName = "TrustManager";

    public java.lang.String getTrustManagerWSDDServiceName() {
        return TrustManagerWSDDServiceName;
    }

    public void setTrustManagerWSDDServiceName(java.lang.String name) {
        TrustManagerWSDDServiceName = name;
    }

    public eu.linksmart.security.trustmanager.TrustManager getTrustManager() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TrustManager_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTrustManager(endpoint);
    }

    public eu.linksmart.security.trustmanager.TrustManager getTrustManager(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            eu.linksmart.security.trustmanager.client.TrustManagerSoapBindingStub _stub = new eu.linksmart.security.trustmanager.client.TrustManagerSoapBindingStub(portAddress, this);
            _stub.setPortName(getTrustManagerWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTrustManagerEndpointAddress(java.lang.String address) {
        TrustManager_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (eu.linksmart.security.trustmanager.TrustManager.class.isAssignableFrom(serviceEndpointInterface)) {
                eu.linksmart.security.trustmanager.client.TrustManagerSoapBindingStub _stub = new eu.linksmart.security.trustmanager.client.TrustManagerSoapBindingStub(new java.net.URL(TrustManager_address), this);
                _stub.setPortName(getTrustManagerWSDDServiceName());
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
        if ("TrustManager".equals(inputPortName)) {
            return getTrustManager();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://trustmanager.security.linksmart.eu", "TrustManagerService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://trustmanager.security.linksmart.eu", "TrustManager"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("TrustManager".equals(portName)) {
            setTrustManagerEndpointAddress(address);
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
