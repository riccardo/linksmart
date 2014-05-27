/**
 * EventManagerPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.eventmanager;

public interface EventManagerPort extends java.rmi.Remote {
    public boolean publishXmlEvent(java.lang.String eventXmlString) throws java.rmi.RemoteException;
    public boolean subscribe(java.lang.String topic, java.lang.String endpoint, int priority) throws java.rmi.RemoteException;
    public boolean unsubscribe(java.lang.String topic, java.lang.String endpoint) throws java.rmi.RemoteException;
    public boolean subscribeWithHID(java.lang.String topic, java.lang.String hid, int priority) throws java.rmi.RemoteException;
    public boolean unsubscribeWithHID(java.lang.String topic, java.lang.String hid) throws java.rmi.RemoteException;
    public eu.linksmart.eventmanager.Subscription[] getSubscriptions() throws java.rmi.RemoteException;
    public boolean clearSubscriptions(java.lang.String endpoint) throws java.rmi.RemoteException;
    public boolean clearSubscriptionsWithHID(java.lang.String hid) throws java.rmi.RemoteException;
    public boolean setPriority(java.lang.String in0, int in1) throws java.rmi.RemoteException;
    public boolean triggerRetryQueue() throws java.rmi.RemoteException;
    public boolean publish(java.lang.String topic, eu.linksmart.eventmanager.Part[] in1) throws java.rmi.RemoteException;
    public boolean unsubscribeWithDescription(java.lang.String topic, java.lang.String description) throws java.rmi.RemoteException;
    public boolean subscribeWithDescription(java.lang.String topic, java.lang.String description, int priority) throws java.rmi.RemoteException;
    public boolean clearSubscriptionsWithDescription(java.lang.String description) throws java.rmi.RemoteException;
}
