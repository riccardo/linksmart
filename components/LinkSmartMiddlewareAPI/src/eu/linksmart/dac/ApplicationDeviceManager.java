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
 * Copyright (C) 2006-2010 T-Connect, Marco Vettorello
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

package eu.linksmart.dac;

/**
 * Interface for the Application Device Manager. build on the DAC version by 29
 * September 2009
 * 
 * @author Marco Vettorello
 * 
 */
public interface ApplicationDeviceManager extends java.rmi.Remote {
	/**
	 * Returns a list of all gateways that the Device Application Catalogue
	 * currently knows of.
	 * 
	 * @return a comma-separated list of gateway names
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getAllGateways() throws java.rmi.RemoteException;

	/**
	 * Initiates a resolve process for unresolved devices of a certain type on a
	 * specific gateway.
	 * 
	 * @param gateway
	 *            the gateway for physical devices
	 * @param discovermanagertype
	 *            the discovery manager which should resolve the devices, for
	 *            instance BluetoothDisocverymanager.
	 * @throws java.rmi.RemoteException
	 */
	void resolveDevices(java.lang.String gateway,
			java.lang.String discovermanagertype)
			throws java.rmi.RemoteException;

	/**
	 * Initiates a discovery process on a specific gateway. The discovery will
	 * be done for all types of devices.
	 * 
	 * @param gateway
	 *            the gateway for physical devices
	 * @throws java.rmi.RemoteException
	 */
	void discoverDevices(java.lang.String gateway)
			throws java.rmi.RemoteException;

	/**
	 * Process an error message from a specific device and returns the result.
	 * 
	 * @param deviceid
	 *            the unique HID for the device
	 * @param theMessage
	 *            the error message
	 * @return the result of the error processing
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String processErrorMessage(java.lang.String deviceid,
			java.lang.String theMessage) throws java.rmi.RemoteException;

	/**
	 * Gives an XML description of a device in SCPD (Service Control Point
	 * Document) format.
	 * 
	 * @param deviceid
	 *            an ID for the device
	 * @param idtype
	 *            the type of identifier used, values could be UDN,
	 *            FriendlyName, or HID
	 * @return a string with an SPCD XML for the device
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getDeviceXML(java.lang.String deviceid,
			java.lang.String idtype) throws java.rmi.RemoteException;

	/**
	 * Gives a list of XML descriptions for all devices at a gateway.
	 * 
	 * @param gateway
	 *            The name of the gateway
	 * @return A string with SPCD XML for all devices at gateway
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getLinkSmartDevices(java.lang.String gateway)
			throws java.rmi.RemoteException;

	/**
	 * Gives a list of web service endpoints for all devices at a gateway.
	 * 
	 * @param gateway
	 *            the name of the gateway,if empty it will return devices for
	 *            all gateways
	 * @return an XML string with pairs of device id:s and their web service
	 *         endpoints
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getLinkSmartDevicesEndpoints(java.lang.String gateway)
			throws java.rmi.RemoteException;

	/**
	 * Gives a list of XML descriptions for all LinkSmart devices at a gateway based
	 * on the device type.
	 * 
	 * @param gateway
	 *            the name of the gateway,if empty it will return devices for
	 *            all gateways
	 * @param devicetype
	 *            A device URN
	 * @return a string with SPCD XML:s for all devices that match the device
	 *         type
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getLinkSmartDevicesFromType(java.lang.String gateway,
			java.lang.String devicetype) throws java.rmi.RemoteException;

	/**
	 * Gives a list of XML descriptions for all devices based on a XPath
	 * selection.
	 * 
	 * @param xpath
	 *            an XPath expression that will be applied to the device XML as
	 *            a selection filter. Devices that match the XPath expression,
	 *            will be selected.
	 * @return a string with SPCD XML for all devices at gateway
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getLinkSmartDevicesFromXpath(java.lang.String xpath)
			throws java.rmi.RemoteException;

	/**
	 * Gives a list of XML descriptions for all unresolved devices at a gateway.
	 * 
	 * @param gateway
	 *            the name of the gateway,if empty it will return devices for
	 *            all gateways
	 * @return a string with SPCD XML for all devices that has not yet been
	 *         resolved
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getUnresolvedDevices(java.lang.String gateway)
			throws java.rmi.RemoteException;

	/**
	 * Returns the ontology description of a device as an OWL Document.
	 * 
	 * @param deviceontology_id
	 *            the ontology id for the device
	 * @return a string with an OWL description of the device
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getDeviceOntologyDescription(
			java.lang.String deviceontology_id) throws java.rmi.RemoteException;

	/**
	 * Allows invocation of any method offered in the general LinkSmart service of a
	 * device.
	 * 
	 * @param deviceid
	 *            the id for the device
	 * @param idtype
	 *            the type of identfier used, values could be UDN, FriendlyName,
	 *            or HID
	 * @param method
	 *            the method to invoke
	 * @param arguments
	 *            arguments to use following the format:
	 *            <code>par1=12;par2=mystring;par3=45</code>
	 * @return a string with the result of the invocation
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String invokeLinkSmartService(java.lang.String deviceid,
			java.lang.String idtype, java.lang.String method,
			java.lang.String arguments) throws java.rmi.RemoteException;

	/**
	 * Allows invocation of any method offered in any service of a device.
	 * 
	 * @param deviceid
	 *            the id for the device
	 * @param idtype
	 *            the type of identfier used, values could be UDN, FriendlyName,
	 *            or HID
	 * @param service
	 *            the serviceid following the format
	 *            "urn:upnp-org:serviceId:weatherservice:thermometer:1"
	 * @param method
	 *            the method to invoke
	 * @param arguments
	 *            arguments to use following the format:
	 *            <code>par1=12;par2=mystring;par3=45</code>
	 * @return a string with the result of the invocation
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String invokeService(java.lang.String deviceid,
			java.lang.String idtype, java.lang.String service,
			java.lang.String method, java.lang.String arguments)
			throws java.rmi.RemoteException;

	/**
	 * Allows invocation of any method offered in any service on a set of
	 * devices selected by an Xpath expression.
	 * 
	 * @param xpath
	 *            an xpath expression to select devices for which the method
	 *            invocation should be done
	 * @param serviceid
	 *            the serviceid following the format
	 *            "urn:upnp-org:serviceId:weatherservice:thermometer:1"
	 * @param method
	 *            the method to invoke
	 * @param arguments
	 *            arguments to use following the format:
	 *            <code>par1=12;par2=mystring;par3=45</code>
	 * @return A string with the result of the invocation
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String invokeServiceXPath(java.lang.String xpath,
			java.lang.String serviceid, java.lang.String method,
			java.lang.String arguments) throws java.rmi.RemoteException;

	/**
	 * Allows manual adding of devices to the network that cannot be discovered
	 * using the default discovery protocol.
	 * 
	 * @param devicedescription
	 *            An SPCD description of the device to be added
	 * @return the success of the process
	 * @throws java.rmi.RemoteException
	 */
	@Deprecated
	java.lang.String addDevice(java.lang.String devicedescription)
			throws java.rmi.RemoteException;

	/**
	 * Removes a device from the Device Application Catalogue and stops the
	 * device.
	 * 
	 * @param deviceid
	 *            The id for the device
	 * @param idtype
	 *            The type of identifier used, values could be UDN,
	 *            FriendlyName, or HID
	 * @throws java.rmi.RemoteException
	 */
	void removeDevice(java.lang.String deviceid, java.lang.String idtype)
			throws java.rmi.RemoteException;

	/**
	 * Tells if a device with a given LinkSmart ID is registered with the catalogue.
	 * 
	 * @param HID
	 *            the id for the device
	 * @return <code>true</code> if a device with the HID is registered
	 *         otherwise <code>false</code>
	 * @throws java.rmi.RemoteException
	 */
	java.lang.Boolean isRegistered(java.lang.String HID)
			throws java.rmi.RemoteException;

	/**
	 * Returns the LinkSmart ID for a device.
	 * 
	 * @param deviceid
	 *            the id for the device
	 * @param idtype
	 *            the type of identifier used, values could be UDN or
	 *            FriendlyName
	 * @return the LinkSmart ID for a device.
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getLinkSmartID(java.lang.String deviceid,
			java.lang.String idtype) throws java.rmi.RemoteException;

	/**
	 * Returns a list of LinkSmart encoded urls for the device that match with the
	 * given parameters.
	 * 
	 * @param gateway
	 *            the name of the gateway
	 * @param deviceid
	 *            the id for the device
	 * @param idtype
	 *            the type of identifier used, values could be UDN or
	 *            FriendlyName
	 * @param senderHID
	 *            he hid of the sender, normally an empty string
	 * @param sessionid
	 *            the id of the session
	 * @return a list of LinkSmart encoded urls for the device
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getLinkSmartURL(java.lang.String gateway,
			java.lang.String deviceid, java.lang.String idtype,
			java.lang.String senderHID, java.lang.String sessionid)
			throws java.rmi.RemoteException;

	/**
	 * Returns the web service endpoint for a given device.
	 * 
	 * @param deviceid
	 *            the id for the device
	 * @param idtype
	 *            the type of identifier used, values could be UDN or
	 *            FriendlyName
	 * @return the URL associated with the web service endpoint
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getWSEndpoint(java.lang.String deviceid,
			java.lang.String idtype) throws java.rmi.RemoteException;

	/**
	 * Returns the endpoint for the generic LinkSmart web service a given device
	 * 
	 * @param deviceid
	 *            the id for the device
	 * @param idtype
	 *            the type of identifier used, values could be UDN or
	 *            FriendlyName
	 * @return the endpoint for the generic LinkSmart web service
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getLinkSmartWSEndpoint(java.lang.String deviceid,
			java.lang.String idtype) throws java.rmi.RemoteException;

	/**
	 * Returns the endpoint to the DAC that "owns" a given device.
	 * 
	 * @param deviceid
	 *            the id for the device
	 * @param idtype
	 *            the type of identifier used, values could be UDN or
	 *            FriendlyName
	 * @return the endpoint to the DAC that "owns" a given device.
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getDACEndpoint(java.lang.String deviceid,
			java.lang.String idtype) throws java.rmi.RemoteException;

	/**
	 * Returns the WSDL description of a given device.
	 * 
	 * @param deviceid
	 *            the id for the device
	 * @param idtype
	 *            the type of identifier used, values could be UDN or
	 *            FriendlyName
	 * @return he WSDL description of a given device
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getWSDL(java.lang.String deviceid, java.lang.String idtype)
			throws java.rmi.RemoteException;

	/**
	 * Starts devices that match a given XPath expression. The expression is
	 * applied to the SCPD XML of the device.
	 * 
	 * @param xpath
	 *            a valid XPath expression
	 * @throws java.rmi.RemoteException
	 */
	void startDevices(java.lang.String xpath) throws java.rmi.RemoteException;

	/**
	 * Stops devices that match a given xpath expression. The expression is
	 * applied to the SCPD XML of the device.
	 * 
	 * @param xpath
	 *            a valid XPath expression
	 * @throws java.rmi.RemoteException
	 */
	void stopDevices(java.lang.String xpath) throws java.rmi.RemoteException;

	/**
	 * Returns the LinkSmart ID for a device based on the local application id
	 * assinged to devices.
	 * 
	 * @param application
	 *            the application were the device resides.
	 * @param devicelocalid
	 *            the local id for the device within the application for
	 *            instance MyDiscoBall.
	 * @return the LinkSmart ID for the device
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getHID(java.lang.String application,
			java.lang.String devicelocalid) throws java.rmi.RemoteException;

	/**
	 * Returns the LinkSmart ID for a device based on an xpath description which is
	 * applied to the SCPD device model.
	 * 
	 * @param application
	 *            the application were the device resides
	 * @param xpath
	 *            a valid xpath expression
	 * @param hidtype
	 *            the specific LinkSmart ID to retrieve from the device (
	 *            <code>linksmartWS, energyWS</code>, etc)
	 * @return the LinkSmart ID for THE device
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getHIDsFromXPath(java.lang.String application,
			java.lang.String xpath, java.lang.String hidtype)
			throws java.rmi.RemoteException;

	/**
	 * Returns the LinkSmart ID for a device based on the old style NM
	 * GetHIDsByDescription.
	 * 
	 * @param description
	 *            Old style NM description
	 * @return the LinkSmart ID for the device
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getHIDsbyNMDescription(java.lang.String description)
			throws java.rmi.RemoteException;

	/**
	 * Returns all local HIDs for a given type.
	 * 
	 * @param hidtype
	 *            the specific LinkSmart ID to retrieve from the device (
	 *            <code>linksmartWS, energyWS</code>, etc)
	 * @return all local HIDs for the given type
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getAllLocalHIDS(java.lang.String hidtype)
			throws java.rmi.RemoteException;

	/**
	 * Gives an XML description of all local device in SCPD (Service Control
	 * Point Document) format.
	 * 
	 * @return an XML description of all local device in SCPD (Service Control
	 *         Point Document) format.
	 * @throws java.rmi.RemoteException
	 */
	java.lang.String getAllLocalDeviceXml() throws java.rmi.RemoteException;

	/**
	 * Register the given URL for real-time callback from the DAC.
	 * 
	 * @param url
	 *            the URL of the callback webservice to register
	 * @throws java.rmi.RemoteException
	 */
	void registerCallBackUrl(java.lang.String url)
			throws java.rmi.RemoteException;
}
