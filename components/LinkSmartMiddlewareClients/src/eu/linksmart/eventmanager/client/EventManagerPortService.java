/**
 * EventManagerImplementation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.eventmanager.client;

public interface EventManagerPortService extends javax.xml.rpc.Service {
    public java.lang.String getBasicHttpBinding_EventManagerPortAddress();

    public eu.linksmart.eventmanager.EventManagerPort getEventManagerPort() throws javax.xml.rpc.ServiceException;

    public eu.linksmart.eventmanager.EventManagerPort getEventManagerPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
