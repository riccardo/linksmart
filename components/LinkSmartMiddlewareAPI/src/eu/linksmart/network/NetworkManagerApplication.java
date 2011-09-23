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
 * Copyright (C) 2006-2010 [Telefonica I+D]
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
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.network;

/**
 * Interface of the Network Manager
 */
public interface NetworkManagerApplication extends java.rmi.Remote {
	
	public static final String PID = "PID";
	public static final String SID = "SID";
	public static final String DESCRIPTION = "Des";
	
	/**
	 * It allows to open a session between two HIDs for data exchange. A session
	 * will be established between the senderHID and the receiverHID.
	 * The sessions by default expires 60000 milliseconds.
	 * 
	 * @param senderHID The HID of the sender
	 * @param receiverHID The HID of the receiver
	 * @return The session identifier (uuid) for the generated session
	 */
	public java.lang.String openSession(java.lang.String senderHID,
		java.lang.String receiverHID) throws java.rmi.RemoteException;
	
	/**
	 * @deprecated
	 * Use SOAP tunneling instead
	 */
	public eu.linksmart.network.NMResponse sendData(java.lang.String in0,
		java.lang.String in1, java.lang.String in2, java.lang.String in3)
		throws java.rmi.RemoteException;
	
	/**
	 * @deprecated
	 * Use SOAP tunneling instead
	 */
	public eu.linksmart.network.NMResponse receiveData(java.lang.String in0,
		java.lang.String in1, java.lang.String in2, java.lang.String in3)
		throws java.rmi.RemoteException;

	/**
	 * It allows to close a session using a session identifier. If the session
	 * is not closed, it will be closed after the expiration time (by default
	 * 60000 millisecond)
	 * 
	 * @param sessionID The session identifier.
	 */
	public void closeSession(java.lang.String sessionID)
		throws java.rmi.RemoteException;

	/**
	 * This method allows to get stored data in the session object.
	 * 
	 * @param sessionID The sessionID of the session where the data is stored
	 * @param key The key for the requested parameter
	 * @return The value of the requested parameter or null if the sessionID or
	 * the parameter don't exist.
	 */
	public java.lang.String getSessionParameter(String sessionID, String key)
		throws java.rmi.RemoteException;

	/**
	 * This method allows to store data (key-value pair) in a session object.
	 * 
	 * @param sessionID The sessionID of the session where the data will be stored.
	 * @param key The key on which the data will be stored.
	 * @param value The data to be stored
	 */
	public void setSessionParameter(String sessionID, String key, String value)
		throws java.rmi.RemoteException;

	/**
	 * Operation to synchronize the clientSessionsList with the
	 * serverSessionsList of this Network Manager.
	 * 
	 * @param senderHID	The HID of the client that need to synchronize its
	 * clientSessionsList with the serverSessionsList of this Network Manager
	 * @param receiverHID The HID of the server HID
	 * @return A vector that contains the sessionID to be deleted
	 */
	public java.util.Vector synchronizeSessionsList(String senderHID,
		String receiverHID) throws java.rmi.RemoteException;

	/**
	 * @deprecated
	 */
	public void addSessionRemoteClient(java.lang.String in0,
		java.lang.String in1, java.lang.String in2)
		throws java.rmi.RemoteException;

	/**
	 * Operation to create an HID with a predefined contextID and level It calls
	 * the HID Manager for creating the HID and it will be added to the idTable
	 * 
	 * @param contextID	The desired context ID to be created
	 * @param level	The desired level
	 * @return The {@link String} representation of the HID
	 * @deprecated This method will be deleted in the next release
	 */
	public java.lang.String createHID(long contextID, int level)
		throws java.rmi.RemoteException;

	/**
	 * Operation to create an HID without any context It calls the Identity
	 * Manager for creating the HID and it will be added to the idTable
	 * 
	 * @return The {@link String} representation of the HID
	 */
	public java.lang.String createHID() throws java.rmi.RemoteException;

	/**
	 * Operation to create an HID with a predefined contexID and level and
	 * providing a description for the HID (searching purposes) and the endpoint
	 * of the service behind it (for service invocation)
	 * 
	 * @param contextID	The desired context ID to be created
	 * @param level	The desired level
	 * @param description The description associated with this HID
	 * @param endpoint The endpoint of the service (if there is a service behind)
	 * @return The {@link String} representation of the HID
	 * @deprecated It is better to use from now
	 * {@link #createCryptoHID(String xmlAttributes, String endpoint)}
	 */
	public java.lang.String createHIDwDesc(long contextID, int level,
		java.lang.String description, java.lang.String endpoint)
		throws java.rmi.RemoteException;

	/**
	 * Operation to create an HID providing a description for the HID (searching
	 * purposes) and the endpoint of the service behind it (for service
	 * invocation).
	 * 
	 * @param description The description associated with this HID
	 * @param endpoint The endpoint of the service (if there is a service behind)
	 * @return The {@link String} representation of the HID.
	 * @deprecated It is better to use from now createCryptoHID().
	 */
	public java.lang.String createHIDwDesc(java.lang.String description,
		java.lang.String endpoint) throws java.rmi.RemoteException;

	/**
	 * Operation to create an crypto HID providing the persistent attributes for
	 * this HID and the endpoint of the service behind it (for service
	 * invocation). The crypto HID is the enhanced version of HIDs, that allow
	 * to store persistent information on them (through certificates) and
	 * doesn't propagate the information stored on it. In order to exchange the
	 * stored information, the Session Domain Protocol is used. It returns a
	 * certificate reference that point to the certificate generated. The next
	 * time the HID needs to be created, using the same attributes, the
	 * certificate reference can be used.
	 * 
	 * @param xmlAttributes The attributes (persistent) associated with this HID. 
	 * This	attributes are stored inside the certificate and follow the	Java 
	 * {@link java.util.Properties} xml schema.
	 * @param endpoint The endpoint of the service (if there is a service behind).
	 * @return A {@link eu.linksmart.network.ws.CrypyoHIDResult} containing
	 * {@link String} representation of the HID and the certificate reference (UUID)
	 */
	public CryptoHIDResult createCryptoHID(String xmlAttributes, String endpoint)
		throws java.rmi.RemoteException;

	/**
	 * Operation to create an crypto HID providing a certificate reference (from
	 * a previously created cryptoHID) and an endpoint The crypto HID is the
	 * enhanced version of HIDs, that allow to store persistent information on
	 * them (through certificates)
	 * 
	 * @param certRef The certificate reference from a previously generated 
	 * cryptoHID.
	 * @param endpoint The endpoint of the service (if there is a service behind).
	 * @return The {@link String} representation of the HID.
	 */
	public String createCryptoHIDfromReference(String certRef, String endpoint)
		throws java.rmi.RemoteException;
	
	/**
	 * @deprecated
	 */
	public java.lang.String renewHID(long in0, int in1, java.lang.String in2)
		throws java.rmi.RemoteException;

	/**
	 * Operation to modify the attributes associated with an HID. This method 
	 * provides the means for HID owners to modify the attributes associated 
	 * with an already generated HID. In order to avoid security threats, the 
	 * requester has to provide the unique ownerID that is associated with this
	 * HID. This ownerID is given to the HID creator in the HID creation 
	 * response (still to be implemented).
	 * 
	 * @param ownerUUID The owner UUID associated with the HID. For now, use 
	 * just the HID
	 * @param hid The HID to be modified
	 * @param newXMLAttributes The new attributes following the properties 
	 * format. See createCryptoHID for more information about the format of 
	 * this attributes.
	 * @return The result of the operation in {@link boolean} format. Returns 
	 * false if the HID could not be found or the ownerID is not valid.
	 */
	public boolean renewHIDAttributes(String ownerUUID, String hid, 
		String newXMLAttributes) throws java.rmi.RemoteException;

	/**
	 * Operation to renew the information associated with an HID (description or
	 * endpoint). It can be used for example, to change the transport protocol
	 * for service invocation.
	 * 
	 * @param description The new descritpion (or null if no change is required)
	 * @param endpoint The new endpoint of the service (or null if no change is
	 * required)
	 * @param hid The hid on which the change is requested
	 * @return The {@link String} representation of the HID.
	 */
	public java.lang.String renewHIDInfo(java.lang.String description,
		java.lang.String endpoint, java.lang.String hid)
		throws java.rmi.RemoteException;

	/**
	 * Operation to change the endpoint associated with this HID. This allows 
	 * protocol switching or any application that wants to modify dynamically 
	 * the endpoint on which the service invocations will be forwarded
	 * 
	 * @param ownerUUID The owner UUID associated with the HID. For now, use 
	 * just the HID
	 * @param hid The HID on which the endpoint needs to be modified
	 * @param endpoint The new endpoint for this HID
	 *			
	 * @return The result of the operation in {@link boolean} format. Returns 
	 * false if the HID could not be found or the ownerID is not valid.
	 */
	public boolean renewHIDEndpoint(String ownerUUID, String hid, String endpoint)
		throws java.rmi.RemoteException;
	
	/**
	 * @deprecated
	 */
	public java.lang.String addContext(long in0, java.lang.String in1)
		throws java.rmi.RemoteException;

	/**
	 * Operation to retrieve all HIDs in the LinkSmart P2P network
	 * 
	 * @return A {@link java.util.Vector} containing all the HIDs in the LinkSmart
	 * Network
	 */
	public java.util.Vector getHIDs() throws java.rmi.RemoteException;

	/**
	 * Operation to retrieve all HIDs in the LinkSmart P2P network in String format
	 * 
	 * @return A {@link String} containing all  HIDs in the LinkSmart P2P network,
	 * separated by commas.
	 */
	public java.lang.String getHIDsAsString() throws java.rmi.RemoteException;

	/**
	 * Operation to retrieve the HIDs associated with this Network Manager
	 * (local HIDs)
	 * 
	 * @return A {@link java.util.Vector} containing all the local HIDs of this
	 * Network Manager.
	 */
	public java.util.Vector getHostHIDs() throws java.rmi.RemoteException;

	/**
	 * Operation to retrieve the HIDs associated with this Network Manager
	 * (local HIDs) in String format
	 * 
	 * @return A {@link String} containing all the local HIDs of this
	 * Network Manager, separated by commas.
	 */
	public java.lang.String getHostHIDsAsString()
		throws java.rmi.RemoteException;
	
	/**
	 * @deprecated
	 */
	public java.util.Vector getContextHIDs(java.lang.String contextID,
		java.lang.String level) throws java.rmi.RemoteException;

	/**
	 * @deprecated
	 */
	public java.lang.String getContextHIDsAsString(java.lang.String in0,
		java.lang.String in1) throws java.rmi.RemoteException;

	/**
	 * Operation to retrieve all HIDs in the LinkSmart P2P network that match a
	 * description. The description allows inexact matches using magic
	 * characters
	 * <p>
	 * For example:
	 * <p>
	 * getHIDsbyDescription("Network*") -> Will return all the HIDs with
	 * description starting with Network
	 * getHIDsbyDescription(*Peter'sPortable*") -> Will return all the HIDs with
	 * description containing Peter'sPortable
	 * 
	 * @param description The description to match against
	 * @return A {@link java.util.Vector} containing all the HIDs in the LinkSmart
	 * Network that match the given description
	 */
	public java.util.Vector getHIDsbyDescription(java.lang.String description)
		throws java.rmi.RemoteException;

	/**
	 * Operation to retrieve all HIDs in the LinkSmart P2P network (in String 
	 * format) that match a description. The description allows inexact 
	 * matches using magic characters
	 * <p>
	 * For example:
	 * <p>
	 * getHIDsbyDescription("Network*") -> Will return all the HIDs with
	 * description starting with Network
	 * getHIDsbyDescription(*Peter'sPortable*") -> Will return all the HIDs with
	 * description containing Peter'sPortable
	 * 
	 * @param description The description to match against
	 * @return A {@link String} containing all the HIDs in the LinkSmart P2P network
	 *		 that match the description
	 */
	public java.lang.String getHIDsbyDescriptionAsString(java.lang.String description)
		throws java.rmi.RemoteException;

	/**
	 * Operation to retrieve all HIDs in the LinkSmart P2P network (in String format)
	 * that match a description. The description allows inexact matches using 
	 * magic characters
	 * <p>
	 * For example:
	 * <p>
	 * getHIDsbyDescription("Network*") -> Will return all the HIDs with
	 * description starting with Network
	 * getHIDsbyDescription(*Peter'sPortable*") -> Will return all the HIDs with
	 * description containing Peter'sPortable
	 * 
	 * @param description The description to match against
	 * @return A {@link java.lang.String[]} containing all the HIDs in the LinkSmart 
	 * P2P network that match the description
	 */
	public java.lang.String[] getHostHIDsbyDescription(java.lang.String description)
		throws java.rmi.RemoteException;

	/**
	 * Operation to retrieve all HIDs in the LinkSmart P2P network (in String format) 
	 * that match a description. The description allows inexact matches using 
	 * magic characters
	 * <p>
	 * For example:
	 * <p>
	 * getHIDsbyDescription("Network*") -> Will return all the HIDs with
	 * description starting with Network
	 * getHIDsbyDescription(*Peter'sPortable*") -> Will return all the HIDs with
	 * description containing Peter'sPortable
	 * 
	 * @param description The description to match against
	 * @return A {@link String} containing all the HIDs in the LinkSmart P2P network
	 * that match the description (separated by commas)
	 */
	public java.lang.String getHostHIDsbyDescriptionAsString(
		java.lang.String in0) throws java.rmi.RemoteException;

	/**
	 * Method to retrieve the description associated with a given hid. 
	 * 
	 * @param hid The hid 
	 * @return A {@link String} with the description associated with the requested hid
	 */
	public java.lang.String getDescriptionbyHID(java.lang.String hid)
		throws java.rmi.RemoteException;

	/**
	 * Method to retrieve the ip associated with a given hid. 
	 * 
	 * @param hid The hid 
	 * @return A {@link String} with the ip associated with the requested hid
	 */
	public java.lang.String getIPbyHID(java.lang.String hid)
		throws java.rmi.RemoteException;

	/**
	 * Method to retrieve the endpoint associated with a given hid. 
	 * 
	 * @param hid The hid 
	 * @return A {@link String} with the endpoint associated with the requested hid
	 */
	public java.lang.String getEndpointbyHID(java.lang.String hid)
		throws java.rmi.RemoteException;

	/**
	 * Operation to remove an HID from the Network Manager
	 * 
	 * @return A {@link java.util.Vector} containing all the HIDs in the LinkSmart
	 * Network
	 */
	public void removeHID(java.lang.String in0) throws java.rmi.RemoteException;

	/**
	 * @deprecated
	 */
	public void removeAllHID() throws java.rmi.RemoteException;

	/**
	 * @deprecated
	 */
	public java.lang.String startNM() throws java.rmi.RemoteException;

	/**
	 * @deprecated
	 */
	public java.lang.String stopNM() throws java.rmi.RemoteException;

	/**
	 * @deprecated
	 */
	public java.lang.String getNMPosition() throws java.rmi.RemoteException;

	/**
	 * @deprecated
	 */
	public java.lang.String getNMPositionAuth(java.lang.String in0)
		throws java.rmi.RemoteException;
	
	/**
	 * This method exchanges certificates between two HIDs.
	 * <p>
	 * As a result of this method, two entries will be added to the 
	 * CryptoManager's keystore:<br>
	 * 1. the certificate of <code>receiverHID</code>
	 * 2. a symmetric key that can be used by the Inside LinkSmart module for 
	 * subsequent communication. 
	 * The certificate that has been stored in the CryptoManager can be 
	 * referenced using the return value of this method.
	 *  
	 * @param senderHID Your own HID.
	 * @param receiverHID The target's HID.
	 * @return a String, representing different Attributes that could be 
	 * retrieved from the receiverHID's certificate.
	 */
	public java.lang.String getInformationAssociatedWithHID(
		java.lang.String senderHID, java.lang.String receiverHID)
		throws java.rmi.RemoteException;

	/**
	 * This method searches the LinkSmart Network for the HIDs that contain 
	 * attributes matching the query.
	 * The format of the queries is as follows:<br><p>
	 * <code>(key1=cond1)&&(key2=cond2*)...</code>
	 * <p>
	 * The developer can also use the magic caracter (*) for unexact queries. 
	 * Due to this, the number of HIDs mathing the query might be greater than 
	 * one. Thus, the developer can specify the number of HIDs that will be 
	 * returned and the maximun time to wait for resolving the query. 
	 * For example, if <code>maxTime = 60000 ms</code> and <code>maxHIDs = 4</code> 
	 * the result of the query will be returned when the Network Manager gets 4 
	 * (or more) HIDs that match the query or when maxTime expires.
	 * <p>
	 * The allowed values for maxTime are 0 (search only locally) - 60000 (ms) 
	 * and for maxHIDs 0 (best effort) - maxInt
	 * 	 *  
	 * @param requesterHID Your own HID.
	 * @param requesterAttributes The target's HID.
	 * @param query Query for HID attributes: <code>(key1=cond1) && 
	 * (key2=cond2*)...</code>  * = non-exact match
	 * @param maxTime Maximum time to wait for query resolution in ms
	 * @param maxResponses Maximum number of HIDs to return
	 * @return a String, with the resulting HID separated by blank spaces 
	 * (0.0.0.1 0.0.0.2 ...)
	 */
	public String[] getHIDByAttributes(String requesterHID,
		String requesterAttributes, String query, long maxTime,
		int maxResponses) throws java.rmi.RemoteException;

	public String getHIDByAttributesAsString(String requesterHID, 
			String requesterAttributes, String query, long maxTime, int maxHIDs) throws java.rmi.RemoteException;
}
