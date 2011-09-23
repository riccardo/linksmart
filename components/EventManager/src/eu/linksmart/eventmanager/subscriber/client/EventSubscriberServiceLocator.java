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
 * Copyright (C) 2006-2010 [Fraunhofer FIT]
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * EventSubscriberServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.eventmanager.subscriber.client;

public class EventSubscriberServiceLocator extends org.apache.axis.client.Service implements eu.linksmart.eventmanager.subscriber.client.EventSubscriberService {

    public EventSubscriberServiceLocator() {
    }


    public EventSubscriberServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EventSubscriberServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BasicHttpBinding_EventSubscriber
    private java.lang.String BasicHttpBinding_EventSubscriber_address = "http://localhost:8000/SubscriberService";

    public java.lang.String getBasicHttpBinding_EventSubscriberAddress() {
        return BasicHttpBinding_EventSubscriber_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BasicHttpBinding_EventSubscriberWSDDServiceName = "BasicHttpBinding_EventSubscriber";

    public java.lang.String getBasicHttpBinding_EventSubscriberWSDDServiceName() {
        return BasicHttpBinding_EventSubscriberWSDDServiceName;
    }

    public void setBasicHttpBinding_EventSubscriberWSDDServiceName(java.lang.String name) {
        BasicHttpBinding_EventSubscriberWSDDServiceName = name;
    }

    public eu.linksmart.eventmanager.EventSubscriber getBasicHttpBinding_EventSubscriber() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicHttpBinding_EventSubscriber_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicHttpBinding_EventSubscriber(endpoint);
    }

    public eu.linksmart.eventmanager.EventSubscriber getBasicHttpBinding_EventSubscriber(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            eu.linksmart.eventmanager.subscriber.client.BasicHttpBinding_EventSubscriberStub _stub = new eu.linksmart.eventmanager.subscriber.client.BasicHttpBinding_EventSubscriberStub(portAddress, this);
            _stub.setPortName(getBasicHttpBinding_EventSubscriberWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBasicHttpBinding_EventSubscriberEndpointAddress(java.lang.String address) {
        BasicHttpBinding_EventSubscriber_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (eu.linksmart.eventmanager.EventSubscriber.class.isAssignableFrom(serviceEndpointInterface)) {
                eu.linksmart.eventmanager.subscriber.client.BasicHttpBinding_EventSubscriberStub _stub = new eu.linksmart.eventmanager.subscriber.client.BasicHttpBinding_EventSubscriberStub(new java.net.URL(BasicHttpBinding_EventSubscriber_address), this);
                _stub.setPortName(getBasicHttpBinding_EventSubscriberWSDDServiceName());
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
        if ("BasicHttpBinding_EventSubscriber".equals(inputPortName)) {
            return getBasicHttpBinding_EventSubscriber();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://eventmanager.linksmart.eu", "EventSubscriberService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://eventmanager.linksmart.eu", "BasicHttpBinding_EventSubscriber"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("BasicHttpBinding_EventSubscriber".equals(portName)) {
            setBasicHttpBinding_EventSubscriberEndpointAddress(address);
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
