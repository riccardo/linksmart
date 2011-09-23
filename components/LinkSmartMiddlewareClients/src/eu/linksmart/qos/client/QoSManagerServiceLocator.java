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
package eu.linksmart.qos.client;

public class QoSManagerServiceLocator extends org.apache.axis.client.Service implements eu.linksmart.qos.client.QoSManagerService {

    public QoSManagerServiceLocator() {
    }


    public QoSManagerServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public QoSManagerServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for QoSManager
    private java.lang.String QoSManager_address = "http://localhost:8082/axis/services/QoSManager";

    public java.lang.String getQoSManagerAddress() {
        return QoSManager_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String QoSManagerWSDDServiceName = "QoSManager";

    public java.lang.String getQoSManagerWSDDServiceName() {
        return QoSManagerWSDDServiceName;
    }

    public void setQoSManagerWSDDServiceName(java.lang.String name) {
        QoSManagerWSDDServiceName = name;
    }

    public eu.linksmart.qos.QoSManager getQoSManager() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(QoSManager_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getQoSManager(endpoint);
    }

    public eu.linksmart.qos.QoSManager getQoSManager(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            eu.linksmart.qos.client.QoSManagerSoapBindingStub _stub = new eu.linksmart.qos.client.QoSManagerSoapBindingStub(portAddress, this);
            _stub.setPortName(getQoSManagerWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setQoSManagerEndpointAddress(java.lang.String address) {
        QoSManager_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (eu.linksmart.qos.client.QoSManager.class.isAssignableFrom(serviceEndpointInterface)) {
                eu.linksmart.qos.client.QoSManagerSoapBindingStub _stub = new eu.linksmart.qos.client.QoSManagerSoapBindingStub(new java.net.URL(QoSManager_address), this);
                _stub.setPortName(getQoSManagerWSDDServiceName());
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
        if ("QoSManager".equals(inputPortName)) {
            return getQoSManager();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://qos.linksmart.eu", "QoSManagerService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://qos.linksmart.eu", "QoSManager"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("QoSManager".equals(portName)) {
            setQoSManagerEndpointAddress(address);
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
