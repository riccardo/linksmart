/**
 * NetworkManager.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.network.networkmanager.client;

public interface NetworkManager extends javax.xml.rpc.Service {
    public java.lang.String getNetworkManagerPortAddress();

    public eu.linksmart.network.networkmanager.client.NetworkManagerPortType getNetworkManagerPort() throws javax.xml.rpc.ServiceException;

    public eu.linksmart.network.networkmanager.client.NetworkManagerPortType getNetworkManagerPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
