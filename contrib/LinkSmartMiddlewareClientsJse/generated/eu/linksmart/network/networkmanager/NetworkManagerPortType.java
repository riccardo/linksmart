/**
 * NetworkManagerPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.network.networkmanager;

public interface NetworkManagerPortType extends java.rmi.Remote {
    public boolean removeService(eu.linksmart.network.VirtualAddress arg0) throws java.rmi.RemoteException;
    public eu.linksmart.network.Registration registerService(eu.linksmart.utils.Part[] arg0, java.lang.String arg1, java.lang.String arg2) throws java.rmi.RemoteException;
    public eu.linksmart.network.NMResponse sendData(eu.linksmart.network.VirtualAddress arg0, eu.linksmart.network.VirtualAddress arg1, byte[] arg2, boolean arg3) throws java.rmi.RemoteException;
    public java.lang.String[] getAvailableBackbones() throws java.rmi.RemoteException;
    public eu.linksmart.network.Registration[] getServiceByAttributes1(eu.linksmart.utils.Part[] arg0, long arg1, boolean arg2, boolean arg3) throws java.rmi.RemoteException;
    public eu.linksmart.network.Registration[] getServiceByAttributes(eu.linksmart.utils.Part[] arg0) throws java.rmi.RemoteException;
    public eu.linksmart.network.VirtualAddress getService() throws java.rmi.RemoteException;
    public eu.linksmart.network.Registration getServiceByPID(java.lang.String arg0) throws java.rmi.RemoteException, java.lang.IllegalArgumentException;
    public eu.linksmart.network.Registration[] getServiceByQuery(java.lang.String arg0) throws java.rmi.RemoteException;
    public eu.linksmart.network.Registration[] getServiceByDescription(java.lang.String arg0) throws java.rmi.RemoteException;
}
