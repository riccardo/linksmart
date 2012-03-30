/**
 * NetworkManagerService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.network.networkmanager.client;

public interface NetworkManagerService extends javax.xml.rpc.Service {
    public java.lang.String getNetworkManagerAddress();

    public eu.linksmart.network.networkmanager.NetworkManager getNetworkManager() throws javax.xml.rpc.ServiceException;

    public eu.linksmart.network.networkmanager.NetworkManager getNetworkManager(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
