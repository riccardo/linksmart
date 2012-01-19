/**
 * NetworkManager.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.network.networkmanager.client;

public interface NetworkManager extends java.rmi.Remote {
    public eu.linksmart.network.NMResponse sendData(eu.linksmart.network.HID in0, eu.linksmart.network.HID in1, byte[] in2) throws java.rmi.RemoteException;
    public java.lang.String[] getAvailableBackbones() throws java.rmi.RemoteException;
    public eu.linksmart.network.HID getHID() throws java.rmi.RemoteException;
    public eu.linksmart.network.HID createHID(eu.linksmart.utils.Part[] in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public boolean removeHID(eu.linksmart.network.HID in0) throws java.rmi.RemoteException;
    public eu.linksmart.network.HIDInfo createCryptoHID(java.lang.String in0) throws java.rmi.RemoteException;
    public eu.linksmart.network.HIDInfo createCryptoHIDFromReference(java.lang.String in0) throws java.rmi.RemoteException;
}
