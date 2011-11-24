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
// ----------------------------------------------------------------------------
//  Copyright (C) 2010  CNet Svenska AB part of Hydra Project.
// 
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3.0 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.
// 
// http://www.cnet.se mailto:IoT@cnet.se
//  ----------------------------------------------------------------------------

using System;
using OpenSource.UPnP;
using OpenSource.Utilities;
using System.Collections.Generic;
using System.Collections;
using System.ServiceModel;
using System.Runtime.Serialization;
using System.ServiceModel.Description;
using System.Xml;




namespace IoT
{
    public class ApplicationDeviceManagerDevice : IoTDevice
    {
        IoTSmartControlPoint m_smartcontrolpoint;

        public ApplicationDeviceManagerDevice(string IoTID, string name, string vendor, string deviceURN)
            : base(IoTID, name, vendor, deviceURN)
        {

            FriendlyName = "ApplicationDeviceManager";
            Manufacturer = "CNet";
            ManufacturerURL = "http://www.cnet.se";
            ModelName = "Application Device Manager";
            ModelDescription = "Application Device Manager";
            ModelNumber = "1";
            HasPresentation = false;
            DeviceURN = "urn:schemas-upnp-org:IoTdevice:applicationdevicemanager:1";
            Intel.Sample.Dvapplicationdevicemanagerservice applicationdevicemanagerservice = new Intel.Sample.Dvapplicationdevicemanagerservice();
            applicationdevicemanagerservice.External_GetAllGateways = new Intel.Sample.Dvapplicationdevicemanagerservice.Delegate_GetAllGateways(applicationdevicemanagerservice_GetAllGateways);
            applicationdevicemanagerservice.External_GetDeviceXML = new Intel.Sample.Dvapplicationdevicemanagerservice.Delegate_GetDeviceXML(applicationdevicemanagerservice_GetDeviceXML);
            applicationdevicemanagerservice.External_GetIoTDevices = new Intel.Sample.Dvapplicationdevicemanagerservice.Delegate_GetIoTDevices(applicationdevicemanagerservice_GetIoTDevices);
            applicationdevicemanagerservice.External_GetIoTDevicesFromType = new Intel.Sample.Dvapplicationdevicemanagerservice.Delegate_GetIoTDevicesFromType(applicationdevicemanagerservice_GetIoTDevicesFromType);
            applicationdevicemanagerservice.External_GetIoTDevicesFromXpath = new Intel.Sample.Dvapplicationdevicemanagerservice.Delegate_GetIoTDevicesFromXpath(applicationdevicemanagerservice_GetIoTDevicesFromXpath);
            applicationdevicemanagerservice.External_GetUnresolvedDevices = new Intel.Sample.Dvapplicationdevicemanagerservice.Delegate_GetUnresolvedDevices(applicationdevicemanagerservice_GetUnresolvedDevices);
            applicationdevicemanagerservice.External_RemoveDevice = new Intel.Sample.Dvapplicationdevicemanagerservice.Delegate_RemoveDevice(applicationdevicemanagerservice_RemoveDevice);
            AddService(applicationdevicemanagerservice);
            AddIoTService();


        }

        public void SetSmartControlPoint(IoTSmartControlPoint scp)
        {
            m_smartcontrolpoint = scp;
            m_smartcontrolpoint.SetApplicationDeviceManager(this);
        }

        override public void Start()
        {
            StartDevice();

            if (m_smartcontrolpoint!=null)
                m_smartcontrolpoint.SetDACEndpoint(m_wsendpoint);

        }

        override public System.String CreateWS()
        {
            Console.WriteLine("IoTService_CreateWS_ApplicationDeviceManager");

            IoTWCFServiceLibrary.ApplicationDeviceManager myWS = new IoTWCFServiceLibrary.ApplicationDeviceManager(this.m_smartcontrolpoint);

            InitiateWebService(myWS, "IoTWCFServiceLibrary.IApplicationDeviceManager", "ApplicationDeviceManager");

            if (m_smartcontrolpoint != null)
                m_smartcontrolpoint.SetDACEndpoint(m_wsendpoint);

            return m_wsendpoint;
        }

        public System.String applicationdevicemanagerservice_GetAllGateways()
		{
			Console.WriteLine("applicationdevicemanagerservice_GetAllGateways(" + ")");
			
			return m_smartcontrolpoint.GetAllGateways();
		}
		
		public System.String applicationdevicemanagerservice_GetDeviceXML(System.String deviceid, System.String idtype)
		{
			Console.WriteLine("applicationdevicemanagerservice_GetDeviceXML(" + deviceid.ToString() + idtype.ToString() + ")");

            UPnPDevice myDevice = GetDeviceInternal(deviceid, idtype);

            XmlDocument myDoc = GetDeviceXML(myDevice);

            return myDoc.OuterXml;
			
		}
		
		public System.String applicationdevicemanagerservice_GetIoTDevices(System.String gateway)
		{
			Console.WriteLine("applicationdevicemanagerservice_GetIoTDevices(" + gateway.ToString() + ")");

            return m_smartcontrolpoint.GetIoTDevices(gateway);
		}
		
		public System.String applicationdevicemanagerservice_GetIoTDevicesFromType(System.String gateway, System.String deviceType)
		{
			Console.WriteLine("applicationdevicemanagerservice_GetIoTDevicesFromType(" + gateway.ToString() + deviceType.ToString() + ")");

            return applicationdevicemanagerservice_GetIoTDevicesFromXpath("//*[name()='device' and *[name()='gateway' and .='" + gateway + "'] and *[name()='deviceType' and .='" + deviceType + "']]");

		}
		
		public System.String applicationdevicemanagerservice_GetIoTDevicesFromXpath(System.String xpath)
		{
			Console.WriteLine("applicationdevicemanagerservice_GetIoTDevicesFromXpath(" + xpath.ToString() + ")");

            string returnstring = "";

            UPnPDevice[] myDevices = m_smartcontrolpoint.GetIoTDevicesByXpath(xpath);

            foreach (UPnPDevice device in myDevices)
            {
                try
                {
                    XmlDocument myDoc = new XmlDocument();

                    myDoc.Load(device.LocationURL);

                    XmlNode theRoot = myDoc.SelectSingleNode("*");

                    returnstring = returnstring + theRoot.OuterXml;
                }
                catch (Exception e)
                {
                }
            }

            return "<IoTdevices>" + returnstring + "</IoTdevices>";
		}
		
		public System.String applicationdevicemanagerservice_GetUnresolvedDevices(System.String gateway)
		{
			Console.WriteLine("applicationdevicemanagerservice_GetUnresolvedDevices(" + gateway.ToString() + ")");

            
			
			return "Sample String";
		}
		
		public void applicationdevicemanagerservice_RemoveDevice(System.String deviceid, System.String idtype)
		{
			Console.WriteLine("applicationdevicemanagerservice_RemoveDevice(" + deviceid.ToString() + idtype.ToString() + ")");

            UPnPDevice myDevice = GetDeviceInternal(deviceid, idtype);

            m_smartcontrolpoint.InvokeIoTServiceSync(myDevice, "Stop");

		}

        internal UPnPDevice GetDeviceInternal(string deviceid, string idtype)
        {
            UPnPDevice myDevice = null;

            if (idtype == "UDN")
                myDevice = m_smartcontrolpoint.GetIoTDeviceByUDN(deviceid);
            else if (idtype == "FriendlyName")
                myDevice = m_smartcontrolpoint.GetIoTDeviceByFriendlyName(deviceid);
            else if (idtype == "HID")
                myDevice = m_smartcontrolpoint.GetIoTDeviceByHID(deviceid,false);
            else if (idtype == "IoTUDN")
                myDevice = m_smartcontrolpoint.GetIoTDeviceByIoTUDN(deviceid, false);

            return myDevice;
        }

        

        public XmlDocument GetDeviceXML(UPnPDevice thedevice)
        {

            XmlDocument myDoc = new XmlDocument();

            try
            {
                if (thedevice != null)
                {
                    myDoc.Load(thedevice.LocationURL);
                }
                else
                    myDoc.LoadXml("<devicenotfound><friendlyname>" + thedevice.FriendlyName + "</friendlyname><UDN>" + thedevice.UniqueDeviceName + "</UDN></devicenotfound>");
            }

            catch (Exception e)
            {
                myDoc.LoadXml("<error><devicenotfound>" + e.Message + "</devicenotfound></error>");
            }

            return myDoc;
        }
    }
}

namespace IoTWCFServiceLibrary
{
    // You have created a class library to define and implement your WCF service.
    // You will need to add a reference to this library from another project and add 
    // the code to that project to host the service as described below.  Another way
    // to create and host a WCF service is by using the Add New Item, WCF Service 
    // template within an existing project such as a Console Application or a Windows 
    // Application.

    [ServiceContract()]

    public interface IApplicationDeviceManager
    {
        [OperationContract]
        string GetAllGateways();

        [OperationContract]
        void ResolveDevices(System.String gateway, System.String discovermanagertype);
        [OperationContract]
        void DiscoverDevices(System.String gateway);
        [OperationContract]
        string ProcessErrorMessage(string deviceid, string theMessage);
        
        [OperationContract]
        string GetDeviceXML(string deviceid, string idtype);

        [OperationContract]
        string GetIoTDevices(string gateway);
        [OperationContract]
        string GetIoTDevicesEndpoints(string gateway);
        [OperationContract]
        string GetIoTDevicesFromType(string gateway, string devicetype);
        [OperationContract]
        string GetIoTDevicesFromXpath(string xpath);
        [OperationContract]
        string GetUnresolvedDevices(string gateway);
       
        [OperationContract]
        string GetDeviceOntologyDescription(string deviceontology_id);
        
        [OperationContract]
        string InvokeIoTService(string deviceid, string idtype, string method, string arguments);
        [OperationContract]
        string InvokeService(string deviceid, string idtype, string service, string method, string arguments);
        [OperationContract]
        string InvokeServiceXPath(string xpath, string serviceid, string method, string arguments);
        
        [OperationContract]
        string AddDevice(string devicedescription);
        [OperationContract]
        void RemoveDevice(string deviceid, string idtype);
        [OperationContract]
        bool IsRegistered(string HID);
        [OperationContract]
        string GetIoTID(string deviceid, string idtype);


        [OperationContract]
        string GetIoTURL(string gateway, string deviceid, string idtype, string senderHID, string sessionid);

        [OperationContract]
        string GetWSEndpoint(string deviceid,string idtype);
        [OperationContract]
        string GetIoTWSEndpoint(string deviceid, string idtype);
        [OperationContract]
        string GetDACEndpoint(string deviceid, string idtype);
        [OperationContract]
        string GetWSDL(string deviceid, string idtype);
        
        [OperationContract]
        void StartDevices(string xpath);
        [OperationContract]
        void StopDevices(string xpath);

        [OperationContract]
        string GetHID(string application, string devicelocalid);

        [OperationContract]
        string GetHIDsFromXPath(string application, string xpath, string hidtype);

        [OperationContract]
        string GetHIDsbyNMDescription(string description);

        [OperationContract]
        string GetAllLocalHIDS(string hidtype);

        [OperationContract]
        string GetAllLocalDeviceXml();

        [OperationContract]
        string GetAllDeviceXml(string gateway);

        [OperationContract]
        void RegisterCallBackUrl(string url);

        [OperationContract]
        void AddApplicationBinding(string appname,string url);

        [OperationContract]
        string GetIoTURLsFromXpath(string xpath, string hidtype, string sender, string callerNMSoapTunelUriURL);

    }

    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single,IncludeExceptionDetailInFaults = true)]
    /// <summary>
    ///The Application Device Manager manages all knowledge regarding devices that have 
    ///been discovered and are active in the IoT network. The Application Device Manager 
    ///knows about devices from a network perspective but does not handle the locations or 
    ///context of the devices. It provides a web service interface to create web services for the devices,
    ///to get web service endpoints,to invoke service on the devices. The application device manager maintains
    ///a Device Application Catalogue and makes use of a set of Discovery Managers which are running locally
    ///on different gateways to do physical discovery of devices (bluetooth, z-wave, zigbee, rf-swithces,serial ports,
     ///UPnP, et c). The DAC organises the discovered devices around the gateways where they are running.
    /// </summary>
    public class ApplicationDeviceManager : IApplicationDeviceManager
    {


        IoT.IoTSmartControlPoint m_IoTsmartcontrolpoint;

        /// <summary>
        /// The ApplicationDeviceManager Contructor
        /// </summary>
        /// <param name="thesmartpoint">The internal Device Application Catalogue</param>
        public ApplicationDeviceManager(IoT.IoTSmartControlPoint thesmartpoint)
        {
            m_IoTsmartcontrolpoint = thesmartpoint;
        }

        /// <summary>
        /// Initiates a resolve process for unresolved devices of a certain type on a specific gateway.
        /// </summary>
        /// <param name="gateway">The gateway for physical devices</param>
        /// <param name="discovermanagertype">The discovery manager which should resolve the devices, for instance BluetoothDisocverymanager,
        //if this field is empty, all types of devices are resolved.</param>
        public void ResolveDevices(System.String gateway, System.String discovermanagertype)
        {
            m_IoTsmartcontrolpoint.ResolveDevices(gateway, discovermanagertype);
        }

        /// <summary>
        /// Returns a list of all gateways that the Device Application Catalogue currently knows of.
        /// </summary>
        /// <returns>A comma-separated list of gateway names</returns>
        public string GetAllGateways()
        {
            return m_IoTsmartcontrolpoint.GetAllGateways();
        }

        /// <summary>
        /// Initiates a discovery process on a specific gateway. The discovery will be done for all types
        /// of devices.
        /// </summary>
        /// <param name="gateway">The gateway for physical devices, where to do a discovery process</param>
        public void DiscoverDevices(System.String gateway)
        {
            m_IoTsmartcontrolpoint.DiscoverPhysicalDevices(gateway,"");
        }


        /// <summary>
        /// Process an errormessage from a specifc device and returns the result
        /// </summary>
        /// <param name="deviceid">The unique HID for the device</param>
        /// <param name="theMessage">The error message</param>
        public string ProcessErrorMessage(string deviceid, string theMessage)
        {
            return "not implemented";
        }

       

        internal UPnPDevice GetDeviceInternal(string deviceid, string idtype)
        {
            UPnPDevice myDevice = null;

            if (idtype == "UDN")
                myDevice = m_IoTsmartcontrolpoint.GetIoTDeviceByUDN(deviceid);
            else if (idtype == "FriendlyName")
                myDevice = m_IoTsmartcontrolpoint.GetIoTDeviceByFriendlyName(deviceid);
            else if (idtype == "HID")
                myDevice = m_IoTsmartcontrolpoint.GetIoTDeviceByHID(deviceid,false);

            return myDevice;
        }

        /// <summary>
        /// Gives and XML description of a device in SCPD (Service Control Point Document) format
        /// </summary>
        /// <param name="deviceid">The an id for the device</param>
        /// <param name="idtype">The type of identfier used, values could be UDN, FriendlyName, or HID</param>
        /// <returns>A string with an SPCD XML for the device</returns>

        public string GetDeviceXML(string deviceid, string idtype)
        {
            UPnPDevice myDevice = GetDeviceInternal(deviceid, idtype);

            XmlDocument myDoc = GetDeviceXML(myDevice);

            return myDoc.OuterXml;
        }

        public XmlDocument GetDeviceXML(UPnPDevice thedevice)
        {
           
            XmlDocument myDoc = new XmlDocument();

            try
            {
                if (thedevice != null)
                {
                    myDoc.Load(thedevice.LocationURL);
                }
                else
                    myDoc.LoadXml("<devicenotfound><friendlyname>" + thedevice.FriendlyName + "</friendlyname><UDN>" + thedevice.UniqueDeviceName + "</UDN></devicenotfound>");
            }

            catch (Exception e)
            {
                myDoc.LoadXml("<error><devicenotfound>" + e.Message + "</devicenotfound></error>");
            }

            return myDoc;
        }

        /// <summary>
        /// Gives a list of XML descriptions for all devices at a gateway
        /// </summary>
        /// <param name="gateway">The name of the gateway</param>
        /// <returns>A string with SPCD XML:s for all devices at gateway</returns>
        public string GetIoTDevices(string gateway)
        {
            return this.m_IoTsmartcontrolpoint.GetIoTDevices(gateway);
        }

        /// <summary>
        /// Gives a list of XML descriptions for all devices based on a XPath selection
        /// </summary>
        /// <param name="xpath">An XPath expression that will be applied to the device XML as a selectio filter.
        /// Devices that match the Xpath expression, will be selected.</param>
        /// <returns>A string with SPCD XML:s for all devices at gateway</returns>

        public string GetIoTDevicesFromXpath(string xpath)
        {
            string returnstring="";

            UPnPDevice[] myDevices = m_IoTsmartcontrolpoint.GetIoTDevicesByXpath(xpath);

            foreach (UPnPDevice device in myDevices)
            {
                try
                {
                    XmlDocument myDoc = new XmlDocument();

                    myDoc.Load(device.LocationURL);

                    XmlNode theRoot = myDoc.SelectSingleNode("*");

                    returnstring = returnstring + theRoot.OuterXml;
                }
                catch (Exception e)
                {
                }
            }

            return "<IoTdevices>" + returnstring + "</IoTdevices>";

        }

        /// <summary>
        /// Gives a list of web service endpoints for all devices at a gateway
        /// </summary>
        /// <param name="gateway">The name of the gateway,if empty it will return devices for all gateways</param>
        /// <returns>An XML string with pairs of device id:s and their web service endpoints</returns>
        public string GetIoTDevicesEndpoints(string gateway)
        {
            return this.m_IoTsmartcontrolpoint.GetIoTDevicesEndpoints(gateway);
        }

       /// <summary>
        /// Gives a list of XML descriptions for all IoT devices at a gateway based on the device type
        /// </summary>
        /// <param name="gateway">The name of the gateway,if empty it will return devices for all gateways</param>
        /// <param name="devicetype">A device URN</param>
        /// <returns>A string with SPCD XML:s for all devices that match the device type</returns>
        public string GetIoTDevicesFromType(string gateway, string devicetype)
        {
            return GetIoTDevicesFromXpath("//*[name()='device' and *[name()='gateway' and .='"+gateway+"'] and *[name()='deviceType' and .='"+devicetype+"']]");
        }

        /// <summary>
        /// Gives a list of XML descriptions for all unresolved devices at a gateway
        /// </summary>
        /// <param name="gateway">The name of the gateway,if empty it will return devices for all gateways</param>
        /// <returns>A string with SPCD XML:s for all devices that has not yet been resolved</returns>
        public string GetUnresolvedDevices(string gateway)
        {
            return "not implemented";
        }

        /// <summary>
        /// Returns the ontology description of a device as an OWL Document. 
        /// </summary>
        /// <param name="deviceontology_id">The ontology id for the device</param>
        /// <returns>A string with an OWL description of the device</returns>
        public string GetDeviceOntologyDescription(string deviceontology_id)
        {
            return "not implemented";
        }

        /// <summary>
        /// Allows invocation of any method offered in the general IoT service of a device
        /// </summary>
        /// <param name="deviceid">The id for the device</param>
        /// <param name="idtype">The type of identfier used, values could be UDN, FriendlyName, or HID</param>
        /// <param name="method">The method to invoke</param>
        /// <param name="arguments">Arguments to use following the format: par1=12;par2=mystring;par3=45</param>
        /// <returns>A string with the result of the invocation</returns>
        public string InvokeIoTService(string deviceid,string idtype,string method,string arguments)
        {
            UPnPDevice theDevice = GetDeviceInternal(deviceid, idtype);

            if (theDevice != null)
            {
                object myobject=m_IoTsmartcontrolpoint.InvokeIoTServiceSync(theDevice, method, arguments);

                if (myobject != null)
                    return myobject.ToString();
                else
                    return "";
            }
            else
                return "no device found";
        }

        /// <summary>
        /// Allows invocation of any method offered in any service of a device
        /// </summary>
        /// <param name="deviceid">The id for the device</param>
        /// <param name="idtype">The type of identifier used, values could be UDN, FriendlyName, or HID</param>
        /// <param name="serviceid">The serviceid following the format "urn:upnp-org:serviceId:weatherservice:thermometer:1"</param>
        /// <param name="method">The method to invoke</param>
        /// <param name="arguments">Arguments to use following the format: par1=12;par2=mystring;par3=45</param>        /// <returns>A string with the result of the invocation</returns>
        public string InvokeService(string deviceid, string idtype, string serviceid, string method, string arguments)
        {
            UPnPDevice theDevice = GetDeviceInternal(deviceid, idtype);

            if (theDevice != null)
            {
                object myobject = m_IoTsmartcontrolpoint.InvokeServiceSync(theDevice, serviceid, method, arguments);

                if (myobject != null)
                    return myobject.ToString();
                else
                    return "";
            }
            else
                return "no device found";
        }

        /// <summary>
        /// Allows invocation of any method offered in any service on a set of devices selected by an Xpath expression
        /// </summary>
        /// <param name="xpath">An xpath expression to select devices for which the method invocation should be done</param>
        /// <param name="serviceid">The serviceid following the format "urn:upnp-org:serviceId:weatherservice:thermometer:1"</param>
        /// <param name="method">The method to invoke</param>
        /// <param name="arguments">Arguments to use following the format: par1=12;par2=mystring;par3=45</param>        /// <returns>A string with the result of the invocation</returns>
        /// <returns>A string with the result of the invocation</returns>

        public string InvokeServiceXPath(string xpath, string serviceid, string method, string arguments)
        {
            UPnPDevice[] theDevices = m_IoTsmartcontrolpoint.GetIoTDevicesByXpath(xpath);
            string returnstring="";

            foreach (UPnPDevice theDevice in theDevices)
            {
                object myobject = m_IoTsmartcontrolpoint.InvokeServiceSync(theDevice, serviceid, method, arguments);
                returnstring = "<device><UDN>" + theDevice.UniqueDeviceName + "</UDN><FriendlyName>" + theDevice.FriendlyName + "</FriendlyName><result>";
                if (myobject != null)
                    returnstring=returnstring+myobject.ToString()+"</result></device>";
                else
                    returnstring=returnstring+"</result></device>";;
            }
            
            return "<invokeresult>"+returnstring+"</invokeresult>";
        }

        /// <summary>
        /// Allows manual adding of devices to the network that cannot be discovered using the default discovery protocol
        /// </summary>
        /// <param name="devicedescription">An SPCD description of the device to be added</param>
       
        public string AddDevice(string devicedescription)
        {
            return "not implemented";
        }

        /// <summary>
        /// Removes a device from the Device Application Catalogue and stops the device
        /// </summary>
        /// <param name="deviceid">The id for the device</param>
        /// <param name="idtype">The type of identifier used, values could be UDN, FriendlyName, or HID</param>
 
        public void RemoveDevice(string deviceid, string idtype)
        {
            UPnPDevice myDevice = GetDeviceInternal(deviceid, idtype);

            m_IoTsmartcontrolpoint.InvokeIoTServiceSync(myDevice, "Stop");

        }

        /// <summary>
        /// Returns the IoTid for a device
        /// </summary>
        /// <param name="deviceid">The id for the device</param>
        /// <param name="idtype">The type of identifier used, values could be UDN or FriendlyName</param>
        /// 
        public string GetIoTID(string deviceid, string idtype)
        {
            string IoTid="";
            try
            {
                string mydevice = GetDeviceXML(deviceid, idtype);

                XmlDocument myDoc = new XmlDocument();

                myDoc.LoadXml(mydevice);

                IoTid = myDoc.SelectSingleNode(".//*[name()='IoTid']").InnerText;
            }
            catch (Exception e)
            {

            }

            return IoTid;
        }


        public string GetIoTIDFromXpath(string xpath, string hidtype)
        {
            string IoTid = "";
            try
            {
                UPnPDevice[] mydevices=m_IoTsmartcontrolpoint.GetIoTDevicesByXpath(xpath);

                foreach (UPnPDevice thedevice in mydevices)
                {
                    XmlDocument myDoc;

                    myDoc=GetDeviceXML(thedevice);

                    if (myDoc!=null)
                    {
                        XmlNode mynode=myDoc.SelectSingleNode("//*[name()='"+hidtype+"']");

                        if (mynode!=null)
                        {
                            if (IoTid=="")
                                IoTid=mynode.InnerText;
                            else
                                IoTid=IoTid+"," +mynode.InnerText;
                        }
                    }

                    
                }
            }
            catch (Exception e)
            {

            }

            return IoTid;
        }

        
        public string GetIoTURL(string gateway, string deviceid, string idtype, string senderHID, string sessionid)
        {string xpath="";

            if (idtype=="FriendlyName")
                xpath="//*[name()='device' and *[name()='gateway' and .='"+gateway+"'] and *[name()='friendlyName' and .='"+deviceid+"']]/*[name()='IoTid']";
            else if (idtype=="UDN")
                xpath="//*[name()='device' and *[name()='gateway' and .='"+gateway+"'] and *[name()='UDN' and .='"+deviceid+"']]/*[name()='IoTid']";
            
            string deviceHID=GetIoTIDFromXpath(xpath,"IoTidStaticWS");

            string returnURL;


            returnURL = m_IoTsmartcontrolpoint.m_networkmanagerurl + "/SOAPTunneling/" + senderHID + "/" + deviceHID + "/0/hola";

            return returnURL;
        }

        /// <summary>
        /// Returns a list of IoT encoded urls for the devices that match xpath
        /// </summary>
        /// <param name="xpath">Valid xpath expression</param>
        /// <param name="hidtype">The type of hid used, values could be IoTidStaticWS,IoTidIoTWS, IoTidEnergyWS,IoTidDynamicWS plus hydridUPnPService_ followed by UPnPSErvice identifier</param>
        /// <param name="sender">The hid of the sender, normally an empty string</param>
        /// <param name="callerNMSoapTunelUriURL">The url for the callers SOAP tunnel, if null is sent in http://localhost:8082/SOAPTunneling/ is used </param>

        public string GetIoTURLsFromXpath(string xpath,string hidtype, string sender,string callerNMSoapTunelUriURL)
        {
            string returnstring="";

            string type = "";
            string senderHID="";

            if (hidtype != "")
                type = hidtype;
            else
                type = "IoTidStaticWS";

            if (sender != "")
                senderHID = sender;
            else
                senderHID = "0";

            string soapTunnelUri = "http://localhost:8082/SOAPTunneling/";
            if (callerNMSoapTunelUriURL != null)
                if (callerNMSoapTunelUriURL != "")
                    soapTunnelUri = callerNMSoapTunelUriURL;

            string HIDs = GetHIDsFromXPath("", xpath, hidtype);

            string[] hidArray=HIDs.Split(',');

            foreach (string deviceHID in hidArray)
            {
                if (deviceHID.Trim() != "")
                {
                    string callstring = soapTunnelUri + senderHID + "/" + deviceHID + "/0/";

                    if (returnstring == "")
                        returnstring = callstring;
                    else
                        returnstring = returnstring + "," + callstring;
                }
            }

            return returnstring;
        }

        /// <summary>
        /// Tells if a device with a given IoT ID is registered with the catalogue
        /// </summary>
        /// <param name="HID">The id for the device</param>
        /// <returns>True if a devcie with the HID is registered otherwise false</returns>
        public bool IsRegistered(string HID)
        {
            UPnPDevice myDevice = GetDeviceInternal(HID, "HID");

            if (myDevice == null)
                return false;
            else
                return true;
        }

        /// <summary>
        /// Returns the web service endpoint for a given device
        /// </summary>
        /// <param name="deviceid">The id for the device</param>
        /// <param name="idtype">The type of identifier used, values could be UDN, FriendlyName, or HID</param>
        public string GetWSEndpoint(string deviceid, string idtype)
        {
            UPnPDevice myDevice=GetDeviceInternal(deviceid, idtype);
            string returnString = "";

            if (myDevice != null)
            {
                object retval=m_IoTsmartcontrolpoint.InvokeIoTServiceSync(myDevice, "GetWSEndpoint");

                if (retval != null)
                    returnString = retval.ToString();
            }

            return returnString;
        }

        /// <summary>
        /// Returns the endpoint for the generic IoT web service a given device
        /// </summary>
        /// <param name="deviceid">The id for the device</param>
        /// <param name="idtype">The type of identifier used, values could be UDN, FriendlyName, or HID</param>
        public string GetIoTWSEndpoint(string deviceid, string idtype)
        {
            UPnPDevice myDevice = GetDeviceInternal(deviceid, idtype); ;
            string returnString = "";

            if (myDevice != null)
            {
                object retval = m_IoTsmartcontrolpoint.InvokeIoTServiceSync(myDevice, "GetIoTWSEndpoint");

                if (retval != null)
                    returnString = retval.ToString();
            }

            return returnString;
        }

        /// <summary>
        /// Returns the endpoint to the DAC that "owns" a given device
        /// </summary>
        /// <param name="deviceid">The id for the device</param>
        /// <param name="idtype">The type of identifier used, values could be UDN, FriendlyName, or HID</param>
        public string GetDACEndpoint(string deviceid, string idtype)
        {
            UPnPDevice myDevice = GetDeviceInternal(deviceid, idtype); ;
            string returnString = "";

            if (myDevice != null)
            {
                object retval = m_IoTsmartcontrolpoint.InvokeIoTServiceSync(myDevice, "GetDACEndpoint");

                if (retval != null)
                    returnString = retval.ToString();
            }

            return returnString;
        }

        /// <summary>
        /// Returns the WSDL description of a given device
        /// </summary>
        /// <param name="deviceid">The id for the device</param>
        /// <param name="idtype">The type of identifier used, values could be UDN, FriendlyName, or HID</param>
        public string GetWSDL(string deviceid, string idtype)
        {
            UPnPDevice myDevice = GetDeviceInternal(deviceid, idtype);;
            string returnString = "";

            if (myDevice != null)
            {
                object retval = m_IoTsmartcontrolpoint.InvokeIoTServiceSync(myDevice, "GetWSDL");

                if (retval != null)
                    returnString = retval.ToString();
            }

            return returnString;
        }

        /// <summary>
        ///Starts devices that match a given xpath expression. The expression is applied to the SCPD XML of the device
        /// </summary>
        /// <param name="xpath">A valid Xpath expression</param>
        public void StartDevices(string xpath)
        {
            m_IoTsmartcontrolpoint.StartDevices(xpath);
        }

        /// <summary>
        ///Stops devices that match a given xpath expression. The expression is applied to the SCPD XML of the device
        /// </summary>
        /// <param name="xpath">A valid Xpath expression</param>
        public void StopDevices(string xpath)
        {
            m_IoTsmartcontrolpoint.StopDevices(xpath);
        }

        /// <summary>
        /// Returns the IoTid for a device based on the local application id assinged to devices
        /// </summary>
        /// <param name="application">The application were the device resides</param>
        /// <param name="devicelocalid">The local id for the device within the application for instance MyDiscoBall</param>
        public string GetHID(string application, string devicelocalid)
        {
            string xpath = ".//*[name()='IoTUDN' and .='" + devicelocalid + "']";

            return GetHIDsFromXPath(application, xpath, "IoTidStaticWS");
        }

        /// <summary>
        /// Returns the IoTid for a device based on an xpath description which is applied to the SCPD devoce model
        /// </summary>
        /// <param name="application">The application were the device resides</param>
        /// <param name="xpath">a valid xpath expression</param>
        /// <param name="hidtype>the specific IoTid to retrieve from the device (IoTWS, energyWS, et c)</param>

        public string GetHIDsFromXPath(string application, string xpath, string hidtype)
        {
            string returnstring = "";

            try
            {
                returnstring = GetIoTIDFromXpath(xpath, hidtype);
            }
            catch (Exception e)
            {
                returnstring = e.Message;
            }
            return returnstring;
        }


        /// <summary>
        /// Returns the IoTid for a device based on the old style NM GetHIDsByDescription
        /// </summary>
        /// <param name="description">Old style NM description</param>
        
        public string GetHIDsbyNMDescription(string description)
        {
            string myNetworkMgrUrl = m_IoTsmartcontrolpoint.m_networkmanagerurl;
            string returnstring="";

            try
            {
                NetworkManager.NetworkManagerApplicationService myNetworkManager = new NetworkManager.NetworkManagerApplicationService();

                if (myNetworkMgrUrl != "")
                    myNetworkManager.Url = myNetworkMgrUrl;

                returnstring = myNetworkManager.getHIDsbyDescriptionAsString(description);
            }

            catch (Exception e)
            {
                returnstring = e.Message;
            }

            return returnstring;
        }

        public string GetAllLocalHIDS(string hidtype)
        {
            return m_IoTsmartcontrolpoint.GetLocalHIDS(hidtype, true);
        }

        public string GetAllLocalDeviceXml()
        {
            return m_IoTsmartcontrolpoint.GetLocalDeviceXML(true);
        }

        public string GetAllDeviceXml(string gateway)
        {
            return m_IoTsmartcontrolpoint.GetAllDeviceXMLAtGateway(gateway,true);
        }

        public void RegisterCallBackUrl(string url)
        {
            m_IoTsmartcontrolpoint.AddSubscribeUrl(url);
        }

        public void RegisterCallBackTransform(string url)
        {
            m_IoTsmartcontrolpoint.SetCallBackTransform(url);
        }

        public void AddApplicationBinding(string appname, string url)
        {
            m_IoTsmartcontrolpoint.SetApplicationBindingsUrl(url);
        }
    }

}
