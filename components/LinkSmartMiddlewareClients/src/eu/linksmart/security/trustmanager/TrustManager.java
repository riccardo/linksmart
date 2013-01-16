/**
 * TrustManager.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.security.trustmanager;

public interface TrustManager extends javax.xml.rpc.Service {
    public java.lang.String getTrustManagerPortAddress();

    public eu.linksmart.security.trustmanager.TrustManagerPortType getTrustManagerPort() throws javax.xml.rpc.ServiceException;

    public eu.linksmart.security.trustmanager.TrustManagerPortType getTrustManagerPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
