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
 * ApplicationOntologyManagerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.aom.client;

public class ApplicationOntologyManagerServiceLocator extends org.apache.axis.client.Service implements eu.linksmart.aom.client.ApplicationOntologyManagerService {

    public ApplicationOntologyManagerServiceLocator() {
    }


    public ApplicationOntologyManagerServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ApplicationOntologyManagerServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ApplicationOntologyManagerServiceOSGi
    private java.lang.String ApplicationOntologyManagerServiceOSGi_address = "http://localhost:8082/axis/services/ApplicationOntologyManagerServiceOSGi";

    public java.lang.String getApplicationOntologyManagerServiceOSGiAddress() {
        return ApplicationOntologyManagerServiceOSGi_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ApplicationOntologyManagerServiceOSGiWSDDServiceName = "ApplicationOntologyManagerServiceOSGi";

    public java.lang.String getApplicationOntologyManagerServiceOSGiWSDDServiceName() {
        return ApplicationOntologyManagerServiceOSGiWSDDServiceName;
    }

    public void setApplicationOntologyManagerServiceOSGiWSDDServiceName(java.lang.String name) {
        ApplicationOntologyManagerServiceOSGiWSDDServiceName = name;
    }

    public eu.linksmart.aom.ApplicationOntologyManager getApplicationOntologyManagerServiceOSGi() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ApplicationOntologyManagerServiceOSGi_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getApplicationOntologyManagerServiceOSGi(endpoint);
    }

    public eu.linksmart.aom.ApplicationOntologyManager getApplicationOntologyManagerServiceOSGi(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            eu.linksmart.aom.client.ApplicationOntologyManagerServiceOSGiSoapBindingStub _stub = new eu.linksmart.aom.client.ApplicationOntologyManagerServiceOSGiSoapBindingStub(portAddress, this);
            _stub.setPortName(getApplicationOntologyManagerServiceOSGiWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setApplicationOntologyManagerServiceOSGiEndpointAddress(java.lang.String address) {
        ApplicationOntologyManagerServiceOSGi_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (eu.linksmart.aom.ApplicationOntologyManager.class.isAssignableFrom(serviceEndpointInterface)) {
                eu.linksmart.aom.client.ApplicationOntologyManagerServiceOSGiSoapBindingStub _stub = new eu.linksmart.aom.client.ApplicationOntologyManagerServiceOSGiSoapBindingStub(new java.net.URL(ApplicationOntologyManagerServiceOSGi_address), this);
                _stub.setPortName(getApplicationOntologyManagerServiceOSGiWSDDServiceName());
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
        if ("ApplicationOntologyManagerServiceOSGi".equals(inputPortName)) {
            return getApplicationOntologyManagerServiceOSGi();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://aom.linksmart.eu", "ApplicationOntologyManagerService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://aom.linksmart.eu", "ApplicationOntologyManagerServiceOSGi"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ApplicationOntologyManagerServiceOSGi".equals(portName)) {
            setApplicationOntologyManagerServiceOSGiEndpointAddress(address);
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
