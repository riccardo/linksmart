/**
 * TrustManagerPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.security.trustmanager.client;

public interface TrustManagerPortType extends java.rmi.Remote {
    public java.lang.String getTrustToken(java.lang.String arg0) throws java.rmi.RemoteException;
    public double getTrustValue(java.lang.String arg0) throws java.rmi.RemoteException;
    public java.lang.String createTrustToken() throws java.rmi.RemoteException;
    public boolean createTrustTokenWithFriendlyName(java.lang.String arg0) throws java.rmi.RemoteException;
    public double getTrustValueWithIdentifier(java.lang.String arg0, java.lang.String arg1) throws java.rmi.RemoteException;
}
