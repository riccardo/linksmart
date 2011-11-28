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
using System.Text;
using System.Xml;
using System.Xml.Xsl;
using System.Xml.XPath;
using System.Threading;





namespace IoT
{
	/// <summary>
	/// The IoTSmartControlPoint is the core component of the IoT DAC. It is responsible for listening to UPnP discovery messages
    /// to discover new IoT devices. It maintains a set of gateway objects which each contains the devices discovered at the
    /// gateway.
	/// </summary>
	public class IoTSmartControlPoint:UPnPSmartControlPoint
	{
		
		private WeakEvent AddEvent = new WeakEvent();
		private WeakEvent RemoveEvent = new WeakEvent();

        private List<UPnPDevice> m_discoveryManagers;//List of all discovery managers known by this HSCP
        private List<UPnPDevice> m_semanticdevices;
        private List<UPnPDevice> m_IoTdevices;//List of all IoT Devices at gateways "owned" known by this HSCP
        private List<UPnPDevice> m_externalIoTdevices;//List of all IoT Devices at gateways other gateways than this HSCP

        private List<UPnPDevice> m_storagedevices;
        private List<UPnPDevice> m_unresolveddevices;//List of all devices currently not resolved
        private List<UPnPDevice> m_nonIoTdevices;//List of all UPnP devices whcih are nto IoT devices
        private List<IoTGateway> m_gateways;//List of all gateways know by this HSCP

        private UPnPDevice m_applicationdevicemgr;

        IoTApplicationOntologyManager m_ontology = null;//The ontology manager

        public string m_IoTserviceid = "urn:upnp-org:serviceId:1";
        public string m_limboIoTserviceid = "urn:upnp-org:serviceId:IoTServicePort";//KOOL Changed from "urn:upnp-org:serviceId:IoTservice";
        
        public string m_DACEndpoint = "";
        public string m_gateway = "";//The gateway where this HSCP is running
        
        public string m_userules = "";
        public string m_discoveryrules = "";
        public string m_networkmanagerurl = "";
        public string m_eventmanagerurl = "";
        public string m_bindingsurl = "";
        public string m_storagemanagerurl = "";
        

        public bool m_automaticdiscovery = false;
        public bool m_automaticresolve = true;

        public System.Threading.Timer m_minutetimer = null;
        public int m_discoveryresolveperiod = 60000;

        //An externaal application can register devicehandlers to be informed of changes to the devices in the DAC
        public DeviceHandler m_formdevicehandler = null;
        public DeviceHandler m_formremovedevicehandler = null;
        public DeviceHandler m_formupdateddevicehandler = null;


        public SM m_storagemanager = null;

        public UPnPDevice m_upnpstoragemanager = null;

        public string m_externalurlWS = "";

        public bool m_energypolicyenforcement = false;
        public bool m_automaticdevicestorage = false;

        public string m_callbacktransform = "";

        private Object m_IoTlock = new Object();
        private Object m_IoTdevicelock = new Object();

        public string m_soaptunnelprefix = "/SOAPTunneling/0/";
        public string m_soaptunnelsuffix = "/0/hola";
        public string m_soaptunnelIPaddress = "127.0.0.1";
        public int m_soaptunnelport = 8082;

        public bool m_removehidsonstop=true;


        //*******************3 different constructors********************
        public IoTSmartControlPoint()
            : base()
        {

            m_discoveryManagers = new List<UPnPDevice>();
            m_semanticdevices = new List<UPnPDevice>();
            m_IoTdevices = new List<UPnPDevice>();
            m_storagedevices = new List<UPnPDevice>();
            m_unresolveddevices = new List<UPnPDevice>();
            m_nonIoTdevices = new List<UPnPDevice>();
            m_gateways = new List<IoTGateway>();
            m_ontology = new IoTApplicationOntologyManager();

        }

        public IoTSmartControlPoint(DeviceHandler OnAddedDeviceSink)
            : base(OnAddedDeviceSink)
        {

            m_discoveryManagers = new List<UPnPDevice>();
            m_semanticdevices = new List<UPnPDevice>();
            m_IoTdevices = new List<UPnPDevice>();
            m_storagedevices = new List<UPnPDevice>();
            m_unresolveddevices = new List<UPnPDevice>();
            m_nonIoTdevices = new List<UPnPDevice>();
            m_gateways = new List<IoTGateway>();
            m_ontology = new IoTApplicationOntologyManager();

        }

         public IoTSmartControlPoint(DeviceHandler OnAddedDeviceSink, ServiceHandler OnAddedServiceSink, string DevicePartialMatchFilter) : base(OnAddedDeviceSink, OnAddedServiceSink, new string[] { DevicePartialMatchFilter })

        {

            m_discoveryManagers = new List<UPnPDevice>();
            m_semanticdevices = new List<UPnPDevice>();
            m_IoTdevices = new List<UPnPDevice>();
            m_storagedevices = new List<UPnPDevice>();
            m_unresolveddevices = new List<UPnPDevice>();
            m_nonIoTdevices = new List<UPnPDevice>();
            m_gateways = new List<IoTGateway>();
            m_ontology = new IoTApplicationOntologyManager();
        }

        //**************************End of constructors************************************

         //**************************Below are various set functions************************************

         public void SetSOAPTunnelAddress(string ipaddress, string prefix, string suffix, string port)
         {
             m_soaptunnelIPaddress = ipaddress;
             m_soaptunnelprefix = prefix;
             m_soaptunnelsuffix = suffix;

             if (port != "") ;
             m_soaptunnelport = System.Convert.ToInt32(port);
         }

         public void SetCallBackTransform(string callback)
         {
             m_callbacktransform = callback;

             
         }

         public void SetEnergyPolicyEnforcement(string enforcement)
         {
             if (enforcement == "yes")
                 m_energypolicyenforcement = true;
             else
                 m_energypolicyenforcement = false;
         }

         public void SetAutomaticDeviceStorage(string storage)
         {
             if (storage == "yes")
                 m_automaticdevicestorage = true;
             else
                 m_automaticdevicestorage = false;
         }

         public void SetFormDeviceHandler(DeviceHandler theDeviceHandler)
         {
             m_formdevicehandler = theDeviceHandler;
         }

         public void SetFormRemoveDeviceHandler(DeviceHandler theDeviceHandler)
         {
             m_formremovedevicehandler = theDeviceHandler;
         }

         public void SetFormUpdatedDeviceHandler(DeviceHandler theDeviceHandler)
         {
             m_formupdateddevicehandler = theDeviceHandler;
         }

        public void SetDiscoveryRules(string userules, string filename)
        {
            m_userules = userules;
            m_discoveryrules = filename;
            m_ontology.SetDiscoveryRules(m_userules, m_discoveryrules);
        }

        public void SetOntologyUrl(string url)
        {
            m_ontology.SetOntologyUrl(url);
        }

        public void SetStorageManagerUrl(string url)
        {
            m_storagemanagerurl = url;

            m_storagemanager = new SM();

            m_storagemanager.Url = m_storagemanagerurl;

        }

        public void SetRemoveHIDsOnStop(string remove)
        {
            if (remove == "yes")
                m_removehidsonstop = true;
            else
                m_removehidsonstop = false;

        }

        public void SetAutomaticDiscoveryAndResolve(bool discovery, bool resolve, string period)
        {
            m_automaticdiscovery = discovery;
            m_automaticresolve = resolve;

            if (m_automaticresolve)
            {
                m_minutetimer = new System.Threading.Timer(new TimerCallback(ContinousDiscoverResolveDevices));

                m_discoveryresolveperiod = System.Convert.ToInt32(period);

                m_minutetimer.Change(0, m_discoveryresolveperiod);
               
            }
        }

        public void SetNetworkManagerUrl(string url)
        {
            m_networkmanagerurl = url;
        }

        public void SetEventManagerUrl(string url)
        {
            m_eventmanagerurl = url;
        }

        public void SetApplicationBindingsUrl(string url)
        {
            m_bindingsurl = url;
        }

        public void SetApplicationDeviceManager(UPnPDevice appDevmgr)
        {
            m_applicationdevicemgr = appDevmgr;
        }
        //**************************End set functions************************************

        /// <summary>
        /// Adds a url for external listeners which can be reached through a WS call
        /// </summary>
        /// <param name="url">A valid url to a web service following the IoT callback format</param>
        public void AddSubscribeUrl(string url)
        {

            //Add the url as a listner
            if (m_externalurlWS == "")
                m_externalurlWS = url;
            else if (!m_externalurlWS.Contains(url))
                m_externalurlWS = m_externalurlWS + "," + url;


            //Now inform the url about all devices that already exist
            try
            {
                foreach (IoTGateway gw in m_gateways)
                {
                    if (!gw.m_name.Contains("External_"))//Exclude external gateways
                    {
                        foreach (UPnPDevice device in gw.m_IoTdevices)
                        {
                            if (!device.UniqueDeviceName.Contains("IoTStorageDevice") && !device.UniqueDeviceName.Contains("StorageManagerDevice") && !device.DeviceURN.Contains("applicationdevicemanager") && !device.DeviceURN.Contains("servicemanager") && !device.DeviceURN.Contains("discoverymanager"))
                            {
                                device.RefreshDeviceXml();//Make sure device XMl is updated before telling external listener
                                string devicexml = GetTransformedDeviceXML(device, m_callbacktransform);
                                if (devicexml != null && devicexml != "")
                                    InformExternalWS(url, "deviceadded", devicexml);
                            }

                        }
                    }
                }
            }
            catch (Exception e)
            {
                Exception e2 = new Exception("Failed AddSubscribeUrl m_callbacktransform=" + m_callbacktransform + " Reason:" + e.Message, e);
                throw e2;
            }
        }
        


        public void Start()
        {
            //scp = new UPnPSmartControlPoint(new UPnPSmartControlPoint.DeviceHandler(OnAddSink),null,"urn:schemas-upnp-org:IoTdevicecatalogue:DAC:1");
            //scp.OnRemovedDevice += new UPnPSmartControlPoint.DeviceHandler(OnRemoveSink);

        }

        public void StartDevices(string xpath)
        {
            foreach (IoTGateway theGateway in m_gateways)
            {
                theGateway.StartDevices(xpath);
            }
        }

        public void StopDevices(string xpath)
        {
            foreach (IoTGateway theGateway in m_gateways)
            {
                theGateway.StopDevices(xpath);
            }
        }

        public void SetDACEndpoint(System.String theEndpoint)
        {
            m_DACEndpoint = theEndpoint;
        }

        public System.String GetDACEndpoint()
        {
            return m_DACEndpoint;
        }

        public void SetGateway(string theGateway)
        {
            m_gateway = theGateway;
        }

        public string GetAllGateways()
        {
            string returnstring = "";

            foreach (IoTGateway gateway in m_gateways)
            {
                if (returnstring=="")
                    returnstring = returnstring + gateway.m_name;
                else
                    returnstring = returnstring + ","+gateway.m_name;
            }

            return returnstring;
        }

        private UPnPDevice[] GetIoTdevices()
        {
            List<UPnPDevice> theDevices = new List<UPnPDevice>();
            lock (m_IoTdevicelock)
            {
                try
                {
                    foreach (UPnPDevice device in m_IoTdevices)
                    {
                        theDevices.Add(device);

                    }
                }
                catch (Exception e)
                {
                    ReportError("IoT SCP error copying IoTDe3vices:" + e.Message);
                }

            }
            return theDevices.ToArray();
        }

        private void AddIoTdevice(UPnPDevice theDevice)
        {
            lock (m_IoTdevicelock)
            {
                try
                {
                    m_IoTdevices.Add(theDevice);
                }
                catch (Exception e)
                {
                }

            }
        }

        private void RemoveIoTdevice(UPnPDevice theDevice)
        {
            lock (m_IoTdevicelock)
            {
                try
                {
                    m_IoTdevices.Remove(theDevice);
                }
                catch (Exception e)
                {
                }

            }
        }

        /// <summary>
        /// Returns a list of IoT devices that matches a friendly name
        /// </summary>
        /// <param name="friendlyname">A non-empty string</param>
        public UPnPDevice GetIoTDeviceByFriendlyName(string friendlyname)
        {
            foreach (UPnPDevice thedevice in GetIoTdevices())
            {
                if (thedevice.FriendlyName == friendlyname)
                    return thedevice;
            }

            return null;
        }

        /// <summary>
        /// Returns a list of storage devices that matches a UniqueDeviceName
        /// </summary>
        /// <param name="uniquename">A non-empty string</param>
        public UPnPDevice GetStorageDeviceByUniqueName(string uniquename)
        {
            foreach (UPnPDevice thedevice in m_storagedevices)
            {
                if (thedevice.UniqueDeviceName == uniquename)
                    return thedevice;
            }

            return null;
        }

        /// <summary>
        /// Returns the IoT Device that matches a given HID
        /// </summary>
        /// <param name="HID">A registered, valid IoT ID</param>
        public UPnPDevice GetIoTDeviceByHID(string HID, bool usecache)
        {
            if (!usecache)
            {
                foreach (UPnPDevice thedevice in GetIoTdevices())
                {
                    object objectHID;
                    string stringHID = "";

                    objectHID = InvokeIoTServiceSync(thedevice, "GetIoTID");//Makes a true UPnP call to the device

                    if (objectHID != null)
                        stringHID = objectHID.ToString();

                    if (stringHID == HID && stringHID != "")
                        return thedevice;
                }
            }
            else
            {
                foreach (UPnPDevice thedevice in GetIoTdevices())
                {
                    if (thedevice.GetCustomFieldFromDescription("IoTid","IoT")==HID) //Just checks with the cache on the client side
                        return thedevice;
                }
            }
            return null;
        }

        /// <summary>
        /// Returns a list of IoT devices that matches a UniqueDeviceName
        /// </summary>
        /// <param name="UDN">A non-empty string</param>
        public UPnPDevice GetIoTDeviceByUDN(string UDN)
        {
            foreach (UPnPDevice thedevice in GetIoTdevices())
            {
                if (thedevice.UniqueDeviceName == UDN)
                    return thedevice;
            }

            return null;
        }

        /// <summary>
        /// Returns a list of IoT devices that matches a IoTUDN(PID)
        /// </summary>
        /// <param name="IoTUDN">A non-empty string</param>
        public UPnPDevice GetIoTDeviceByIoTUDN(string IoTUDN, bool usecache)
        {
            if (!usecache)
            {
                foreach (UPnPDevice thedevice in GetIoTdevices())
                {
                    try
                    {
                        XmlDocument theDoc = new XmlDocument();

                        theDoc.Load(thedevice.LocationURL);//Call and load the device XML via a UPnP call

                        XmlNode theMatch = theDoc.SelectSingleNode("//*[name='IoTUDN' and .='" + IoTUDN + "']");

                        if (theMatch != null)
                        {
                            return thedevice;
                        }
                    }
                    catch (Exception e)
                    {
                        ReportError("Error searching for IoTUDN:" + IoTUDN + ":" + e.Message);
                    }
                }
            }

            else
            {
                foreach (UPnPDevice thedevice in GetIoTdevices())
                {
                    try
                    {
                       if (thedevice.GetCustomFieldFromDescription("IoTUDN","IoT")==IoTUDN)//Check what is stored in the local UPnP Client
                            return thedevice;
                      
                    }
                    catch (Exception e)
                    {
                        ReportError("Error searching for IoTUDN:" + IoTUDN + ":" + e.Message);
                    }
                }
            }
            return null;
        }

        /// <summary>
        /// Returns a list of IoT devices that matches an xpath expression evaluated against its device XML. It excludes
        /// external devices
        /// </summary>
        /// <param name="xpath">A valid xpath expression</param>
        public UPnPDevice[] GetIoTDevicesByXpath(string xpath)
        {
            List<UPnPDevice> theDevices = new List<UPnPDevice>();

            foreach (UPnPDevice device in GetIoTdevices())
            {

                XmlDocument innerdoc = new XmlDocument();

                try
                {
                    innerdoc.Load(device.LocationURL);

                    XmlNode theMatch = innerdoc.SelectSingleNode(xpath);

                    if (theMatch != null)
                    {
                        theDevices.Add(device);
                    }
                }
                catch (Exception e)
                {
                    ReportError("IoT DAC error searching for xpath:" + xpath + ":" + e.Message);
                }

            }

            return theDevices.ToArray();
        }

        /// <summary>
        /// Returns a list of IoT devices that matches an xpath expression evaluated against its device XML
        /// </summary>
        /// <param name="xpath">A valid xpath expression</param>
        /// <param name="includeexternal">If true, then external devices are included in the search</param>
        public UPnPDevice[] GetIoTDevicesByXpath(string xpath, bool includeexternal)
        {
            List<UPnPDevice> theDevices = new List<UPnPDevice>();


            foreach (UPnPDevice device in GetIoTdevices())
            {

                XmlDocument innerdoc = new XmlDocument();

                try
                {
                    innerdoc.Load(device.LocationURL);

                    XmlNode theMatch = innerdoc.SelectSingleNode(xpath);

                    if (theMatch != null)
                    {
                        theDevices.Add(device);
                    }
                }
                catch (Exception e)
                {
                    ReportError("IoT DAC error searching for xpath:" + xpath + ":" + e.Message);
                }

            }

            foreach (UPnPDevice device in m_externalIoTdevices)
            {

                XmlDocument innerdoc = new XmlDocument();

                try
                {
                    innerdoc.Load(device.LocationURL);

                    XmlNode theMatch = innerdoc.SelectSingleNode(xpath);

                    if (theMatch != null)
                    {
                        theDevices.Add(device);
                    }
                }
                catch (Exception e)
                {
                    ReportError("IoT DAC error searching external devices for xpath:" + xpath + ":" + e.Message);
                }

            }


            return theDevices.ToArray();
        }

        /// <summary>
        /// Returns a list of IoT devices at a specified gateway as a string of device XML
        /// </summary>
        /// <param name="gateway">A valid gateway</param>
        public string GetIoTDevices(string gateway)
        {
            IoTGateway myGateway = GetGatewayFromString(gateway);
            string returnXML="";

            if (myGateway != null)
            {
                XmlDocument myDoc = myGateway.GetIoTDevicesAsXML("");

                returnXML = myDoc.OuterXml;
            }

            return returnXML;
        }

        /// <summary>
        /// Returns a list of endpoints to the WS of the IoT devices at a specified gateway
        /// </summary>
        /// <param name="gateway">A valid gateway</param>
        public string GetIoTDevicesEndpoints(string gateway)
        {
            IoTGateway myGateway = GetGatewayFromString(gateway);
            string returnXML = "";

            if (myGateway != null)
            {
                XmlDocument myDoc = myGateway.GetIoTDevicesEndpoints();

                returnXML = myDoc.OuterXml;
            }

            return returnXML;
        }


        /// <summary>
        /// Get all HIDS for devices at the gateway where this HSCP is running
        /// </summary>
        /// <param name="hidtype">Specifies which IoTid to retreive. Allowed values are: IoTidStaticWS, IoTidIoTWS,IoTidDynamicWS, IoTidEnergyWS, IoTidUPnPIoTidUPnPServiceXXXXX</param>
        public string GetLocalHIDS(string hidtype, bool usecache)
        {
            return GetLocalHIDSAtGateway(m_gateway, hidtype, usecache);
        }

        /// <summary>
        /// Get all HIDS for devices at a gateway
        /// </summary>
        /// <param name="gateway">A valid gateway</param>
        /// <param name="hidtype">Specifies which IoTid to retreive. Allowed values are: IoTidStaticWS, IoTidIoTWS,IoTidDynamicWS, IoTidEnergyWS, IoTidUPnPIoTidUPnPServiceXXXXX</param>
        public string GetLocalHIDSAtGateway(string gateway, string hidtype, bool usecache)
        {
            string resultstring = "";
            if (usecache)
            {
                foreach (UPnPDevice theDevice in GetIoTdevices())
                {
                    string devicegateway = theDevice.GetCustomFieldFromDescription("gateway", "IoT");
                    if (gateway == devicegateway)
                    {
                        string hid = theDevice.GetCustomFieldFromDescription(hidtype, "IoT");
                        if (resultstring == "" && hid!=null)
                            resultstring = hid;
                        else if (hid!=null&&hid != "")
                        {
                            resultstring = resultstring + "," + hid;
                        }
                    }
                }
            }

            return resultstring;
        }

        /// <summary>
        /// Get the device XML for all local devices
        /// </summary>
        public string GetLocalDeviceXML(bool usecache)
        {
            return GetLocalDeviceXMLAtGateway(m_gateway, usecache);
        }

        /// <summary>
        /// Get the device XML for all local devices
        /// </summary>
        /// /// <param name="gateway">A valid gateway</param>
        public string GetLocalDeviceXMLAtGateway(string gateway, bool usecache)
        {
            string resultstring = "<localdevices gateway=\""+gateway+"\">";
            if (usecache)
            {
                foreach (UPnPDevice theDevice in GetIoTdevices())
                {
                    string devicegateway = theDevice.GetCustomFieldFromDescription("gateway", "IoT");
                    if (gateway == devicegateway||gateway=="")
                    {
                        XmlDocument myDoc = theDevice.GetCurrentDeviceXml();
                        
                        XmlNode theRootNode = myDoc.SelectSingleNode("*");

                        if (theRootNode != null)
                             resultstring = resultstring+theRootNode.OuterXml;
                       
                    }
                }
            }

            return resultstring+"</localdevices>";
        }

        /// <summary>
        /// Get the device XML for all external devices
        /// </summary>
        /// /// <param name="gateway">A valid gateway</param>
        public string GetExternalDeviceXMLAtGateway(string gateway, bool usecache)
        {
            string resultstring = "<externaldevices gateway=\"" + gateway + "\">";
            if (usecache)
            {
                foreach (UPnPDevice theDevice in m_externalIoTdevices)
                {
                    string devicegateway = theDevice.GetCustomFieldFromDescription("gateway", "IoT");
                    if (gateway == devicegateway||gateway=="")
                    {
                        XmlDocument myDoc = theDevice.GetCurrentDeviceXml();

                        XmlNode theRootNode = myDoc.SelectSingleNode("*");

                        if (theRootNode != null)
                            resultstring = resultstring + theRootNode.OuterXml;

                    }
                }
            }

            return resultstring + "</externaldevices>";
        }

        /// <summary>
        /// Get the device XML for all external devices
        /// </summary>
        /// /// <param name="gateway">A valid gateway</param>
        public string GetAllDeviceXMLAtGateway(string gateway, bool usecache)
        {
            string resultstring = "<alldevices gateway=\"" + gateway + "\">";

            resultstring = resultstring + GetLocalDeviceXMLAtGateway(gateway, usecache) + GetExternalDeviceXMLAtGateway(gateway, usecache);

            return resultstring + "</alldevices>";
        }


        /// <summary>
        /// Initiates a discovery process at a gateway 
        /// </summary>
        /// <param name="gateway">A valid gateway</param>
        /// <param name="discoverymanagertype">The type of discovery manager to run</param>
        public void DiscoverPhysicalDevices(string gateway, string discoverymanagertype)
        {
            foreach (IoTGateway IoTgateway in m_gateways)
            {

                if (IoTgateway.m_name == gateway)
                {
                    IoTgateway.DiscoverDevices(discoverymanagertype);
                    
                    break;
                }
            }
        }

        /// <summary>
        /// Called whenever the timer event has triggered for discovery/resolving
        /// </summary>
        void ContinousDiscoverResolveDevices(object obj)
        {
            m_minutetimer.Change(Timeout.Infinite, m_discoveryresolveperiod);
            if (m_automaticdiscovery)
                DiscoverPhysicalDevices(m_gateway, "");
            if (m_automaticresolve)
                ResolveDevices(m_gateway, "");
            m_minutetimer.Change(m_discoveryresolveperiod, m_discoveryresolveperiod);
        }

        /// <summary>
        /// Starts a resolveprocess at a gateway
        /// </summary>
        /// <param name="gateway">A valid gateway</param>
        public void ResolveDevices(string gateway)
        {
            ResolveDevices(gateway, "");
        }

        /// <summary>
        /// Given a UPnPDevice it returns the gateway for the device
        /// </summary>
        public string GetGatewayFromDevice(UPnPDevice theDevice)
        {
            XmlDocument myDoc = new XmlDocument();
            string gateway = "";

            try
            {

                string location = theDevice.LocationURL;

                myDoc.Load(location);
                XmlNode myNode = myDoc.SelectSingleNode("//*[name()='gateway']");
                

                if (myNode != null)
                    gateway = myNode.InnerText;

                if (gateway == "Peters SmartPhone")
                {
                    string s = "";
                }



                //Could be a limbodevice (which does not handle device xml correctly
                if (gateway == "")
                {
                    object ws4 = InvokeIoTServiceSync(theDevice, "GetProperty", "property=gateway");
                    if (ws4 != null)
                        gateway = ws4.ToString();
                }
                System.Console.WriteLine("Device : " + theDevice.FriendlyName + " has GW=" + gateway);

            }
            catch (Exception e)
            {

                gateway = "ErrorDevices";

                ReportError("Error searching for device gateway:"+gateway+":"+e.Message);

            }
            return gateway;
        }

        /// <summary>
        /// Looks up the IoTGateway object that corresponds to the gateway string
        /// </summary>
        /// <param name="gateway">A valid gateway</param>
        public IoTGateway GetGatewayFromString(string gateway)
        {
            IoTGateway myGateway = null;

            if (gateway == null || gateway == "")
                gateway = m_gateway;

            if (gateway != "")
            {
                foreach (IoTGateway IoTgateway in m_gateways)
                {
                    if (IoTgateway.m_name == gateway)
                    {
                        myGateway = IoTgateway;
                        break;
                    }
                }

                if (myGateway == null)
                {
                    myGateway = new IoTGateway(gateway);

                    myGateway.SetAutomaticDiscoveryAndResolve(m_automaticdiscovery, m_automaticresolve);
                    m_gateways.Add(myGateway);

                }
            }
            
            return myGateway;
        }

        /// <summary>
        /// Creates Web Services for all devices at a gateway
        /// </summary>
        /// <param name="gateway">A valid gateway</param>
        public void CreateDeviceWS(string gateway)
        {
            IoTGateway myGateway = GetGatewayFromString(gateway);

            if (myGateway != null)
            {
                myGateway.CreateDeviceWS("IoTdevices");
            }
        }

        /// <summary>
        /// Starts resolving all devices at a gateway and a specified discoveryMgr
        /// </summary>
        /// <param name="gateway">A valid gateway</param>
        /// <param name="discoverMgr">The friendly name of a discovery manager, or an empty string</param>
        public void ResolveDevices(string gateway, string discoverMgr)
        {
            IoTGateway myGateway = GetGatewayFromString(gateway);

            //Console.WriteLine("IoTService_ResolveDevices_smartcontrolpoint(" + ")");

            if (myGateway != null)
            {
                XmlDocument myDoc=null;

                //Start by retrieving the device XML for the devices that are unresolved
                myDoc = myGateway.GetUnresolvedDeviceXml(discoverMgr);

                XmlNodeList myManagers = myDoc.SelectNodes("*/*[name()='device']");

                //For each of the discovery managers iterate the list of unresolved devices
                foreach (XmlNode manager in myManagers)
                {
                    XmlNodeList myDevices = manager.SelectNodes(".//*[name()='device']");

                    //For each unresolved device get the discovery info and ask ontology to resolve
                    foreach (XmlNode theDevice in myDevices)
                    {
                        try
                        {
                            XmlNode discoveryNode = theDevice.SelectSingleNode("*[name()='discoveryinfo']");//Get the physical discovery info

                            if (discoveryNode != null)
                            {

                                XmlDocument discoveryDoc = new XmlDocument();

                                //Load XML but first eliminate character 0x19 which causes XML parsing errors
                                discoveryDoc.LoadXml(discoveryNode.InnerText.Replace(System.Convert.ToChar(0x19),' '));

                                //Forward physical discoveryinfo to ontology
                                XmlNode resolvedDevice = m_ontology.ResolveDevice(discoveryDoc.SelectSingleNode("*"));

                                //if device was succesfully resolved we now need to call the Diocvery Manager
                                //and tell it top repalce the unresolved device with a IoT Device specified by the 
                                //XML returned in previous step from the Ontology Manager
                                if (resolvedDevice != null)
                                {
                                    XmlNode theDeviceUDN = theDevice.SelectSingleNode("*[name()='UDN']");

                                    if (theDeviceUDN != null)
                                    {
                                        string resolvemessage = resolvedDevice.OuterXml;
                                        string UDN = theDeviceUDN.InnerText;

                                        UDN = UDN.Substring(5);

                                        XmlNode managertypeNode = manager.SelectSingleNode("*[name()='friendlyName']"); ;
                                        string managertypeString = "";

                                        if (managertypeNode != null)
                                            managertypeString = managertypeNode.InnerText;
                                        try
                                        {
                                            try
                                                {
                                                    lock (myGateway.m_discoveryWS)
                                                    {
                                                        try
                                                        {
                                                            //Call the Web Service of the Discovery Manager with the resolve message and the UDN that idetiifies
                                                            //the unresolved device to be replaced
                                                            myGateway.m_discoveryWS[managertypeString].ResolveDevice(UDN, resolvemessage);
                                                        }
                                                        catch (Exception e1)
                                                        {
                                                        }
                                                    }
                                                }
                                                catch (Exception e)
                                                 {   
                                                //If the Web Service call failed we try to do the same through a UPnP call to the Discovery Manager 
                                                //with the resolve message and the UDN that idetiifiesthe unresolved device to be replaced

                                                    lock (myGateway.m_discoveryUPnP)
                                                    {
                                                        try
                                                        {
                                                            InvokeServiceSync(myGateway.m_discoveryUPnP[managertypeString], m_IoTserviceid, "ResolveDevice", "UDN=" + UDN + ";resolvemessage=" + resolvemessage);
                                                        }
                                                        catch (Exception e2)
                                                        {
                                                        }
                                                    }
                                                }

                                            
                                        }
                                        catch (Exception e)
                                        {
                                            ReportError("DAC error1  resolving devices:" + e.Message);
                                        }
                                    }
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            ReportError("DAC error2 resolving devices:" + e.Message);
                        }
                    }


                }
            }
        }


        /// <summary>
        /// Makes an asynchronous UPnP call to a method in the IoT service
        /// </summary>
        /// <param name="theDevice">The device to call</param>
        /// <param name="method">The IoT method to call</param>
        public void InvokeIoTServiceAsync(UPnPDevice theDevice, string method)
        {
            //need to sort out first if it is a limbo generated device or a normal device
            UPnPService myService = GetIoTSoapService(theDevice, m_IoTserviceid);

            if (myService == null)
                myService = GetIoTSoapService(theDevice, m_limboIoTserviceid);

            if (myService != null)
            {
                //No arguments for the method but we still need an UPnP argument object
                UPnPArgument[] myArgs = new UPnPArgument[0];

                myService.OnInvokeError += new UPnPService.UPnPServiceInvokeErrorHandler(this.HandleInvokeError);
                myService.OnInvokeResponse += new UPnPService.UPnPServiceInvokeHandler(this.HandleInvoke);

                myService.InvokeAsync(method, myArgs, null, new UPnPService.UPnPServiceInvokeHandler(this.HandleInvoke), new UPnPService.UPnPServiceInvokeErrorHandler(this.HandleInvokeError));

            }
        }

        /// <summary>
        /// Makes an synchronous UPnP call to a method in the IoT service
        /// </summary>
        /// <param name="theDevice">The device to call</param>
        /// <param name="method">The IoT method to call</param>
        public object InvokeIoTServiceSync(UPnPDevice theDevice, string method)
        {
            //need to sort out first if it is a limbo generated device or a normal device
            UPnPService myService = GetIoTSoapService(theDevice, m_IoTserviceid);
            object myResult = null;


            string arguments = "";
            bool isLimboSpecial = false;
            int limboIndex = -1; //Used for handling the result index in limbo
            if (myService == null)
            {
                myService = GetIoTSoapService(theDevice, m_limboIoTserviceid);

                switch (method)
                {


                    case "GetProperty":
                        isLimboSpecial = true;
                        arguments += ";propertyvalue=";
                        limboIndex = 1;
                        break;
                    case "CreateWS":
                        isLimboSpecial = true;
                        arguments += "wsendpoint=";
                        limboIndex = 0;
                        break;
                    case "GetDACEndpoint":
                        isLimboSpecial = true;
                        arguments += "dacendpoint=";
                        limboIndex = 0;
                        break;
                    case "GetDiscoveryInfo":
                        isLimboSpecial = true;
                        arguments += "discoveryinfo=";
                        limboIndex = 0;
                        break;
                    case "GetErrorMessage":
                        isLimboSpecial = true;
                        arguments += "errormessage=";
                        limboIndex = 0;
                        break;
                    case "GetHasError":
                        isLimboSpecial = true;
                        arguments += "haserror=";
                        limboIndex = 0;
                        break;
                    case "GetIoTID":
                        isLimboSpecial = true;
                        arguments += "IoTid=";
                        limboIndex = 0;
                        break;
                    case "GetIoTWSEndpoint":
                        isLimboSpecial = true;
                        arguments += "IoTwsendpoint=";
                        limboIndex = 0;
                        break;
                    case "GetStatus":
                        isLimboSpecial = true;
                        arguments += "status=";
                        limboIndex = 0;
                        break;
                    case "GetWSDL":
                        isLimboSpecial = true;
                        arguments += "wsdl=";
                        limboIndex = 0;
                        break;
                    case "GetWSEndpoint":
                        isLimboSpecial = true;
                        arguments += "wsendpoint=";
                        limboIndex = 0;
                        break;
                    case "SetDACEndpoint":
                        isLimboSpecial = false;
                        arguments = arguments.Replace("endpoint=", "dacendpoint=");
                        limboIndex = 0;
                        break;
                }
            }
            if (myService != null)
            {
                UPnPArgument[] myArgs = null;
                if (arguments == "")
                   myArgs =  new UPnPArgument[0];
                else
                    myArgs = CreateUPnPArguments(arguments);

               try
                {
                    myResult = myService.InvokeSync(method, myArgs);
                    if (isLimboSpecial)
                    {
                        myResult = myArgs[limboIndex].DataValue;
                    }
                }
                catch (Exception e)
                {
                    myResult = "could not find endpoint (IoTsmart 1):"+e.Message;

                    ReportError(myResult.ToString());

                    myResult = null;
                }

            }

            return myResult;
        }

        /// <summary>
        /// Use this to call a normal IoT device and when you need to provide arguments
        /// </summary>
        /// <param name="theDevice">The device to call</param>
        /// <param name="method">The IoT method to call</param>
        /// <param name="arguments">The arguments, following the format arg1=X;arg2=Y</param>
        public object InvokeIoTServiceSync(UPnPDevice theDevice, string method, string arguments)
        {
            return InvokeServiceSync(theDevice, m_IoTserviceid,method,arguments);
        }

        /// <summary>
        /// Use this to call a IoT device and when you need to provide arguments
        /// </summary>
        /// <param name="theDevice">The device to call</param>
        /// <param name="serviceid">the UPnP id for the service you want to call</param>
        /// <param name="method">The IoT method to call</param>
        /// <param name="arguments">The arguments, following the format arg1=X;arg2=Y</param>
        public object InvokeServiceSync(UPnPDevice theDevice, string serviceid, string method, string arguments)
        {
            //need to sort out first if it is a limbo generated device or a normal device

            UPnPService myService = GetIoTSoapService(theDevice, serviceid);
            object myResult = null;
            
            bool isLimboSpecial = false;
            int limboIndex = -1; //Used for handling the result index in limbo
            if (serviceid == m_IoTserviceid && myService == null)
            {
                myService = GetIoTSoapService(theDevice, m_limboIoTserviceid);

                switch (method)
                {
 
                    case "GetProperty":
                        isLimboSpecial = true;
                        arguments += ";propertyvalue=";
                        limboIndex = 1;
                        break;
                    case "CreateWS":
                        isLimboSpecial = true;
                        arguments += "wsendpoint=";
                        limboIndex = 0;
                        break;
                    case "GetDACEndpoint":
                        isLimboSpecial = true;
                        arguments += "dacendpoint=";
                        limboIndex = 0;
                        break;
                    case "GetDiscoveryInfo":
                        isLimboSpecial = true;
                        arguments += "discoveryinfo=";
                        limboIndex = 0;
                        break;
                    case "GetErrorMessage":
                        isLimboSpecial = true;
                        arguments += "errormessage=";
                        limboIndex = 0;
                        break;
                    case "GetHasError":
                        isLimboSpecial = true;
                        arguments += "haserror=";
                        limboIndex = 0;
                        break;
                    case "GetIoTID":
                        isLimboSpecial = true;
                        arguments += "IoTid=";
                        limboIndex = 0;
                        break;
                    case "GetIoTWSEndpoint":
                        isLimboSpecial = true;
                        arguments += "IoTwsendpoint=";
                        limboIndex = 0;
                        break;
                    case "GetStatus":
                        isLimboSpecial = true;
                        arguments += "status=";
                        limboIndex = 0;
                        break;
                    case "GetWSDL":
                        isLimboSpecial = true;
                        arguments += "wsdl=";
                        limboIndex = 0;
                        break;
                    case "GetWSEndpoint":
                        isLimboSpecial = true;
                        arguments += "wsendpoint=";
                        limboIndex = 0;
                        break;
                    case "SetDACEndpoint":
                        isLimboSpecial = false;
                        arguments = arguments.Replace("endpoint=", "dacendpoint=");
                        limboIndex = 0;
                        break;
                }
            }


            if (myService != null)
            {
                UPnPArgument[] myArgs = CreateUPnPArguments(arguments);


                myService.OnInvokeError += new UPnPService.UPnPServiceInvokeErrorHandler(this.HandleInvokeError);
                myService.OnInvokeResponse += new UPnPService.UPnPServiceInvokeHandler(this.HandleInvoke);

                try
                {
                    System.Console.WriteLine("UPnP Call: device:" + theDevice.FriendlyName + " serviceid:" + myService.ServiceID + "method:" + method + " Endpoint:" + myService.ControlURL);
                    myResult = myService.InvokeSync(method, myArgs);
                    if (isLimboSpecial)
                    {
                        myResult = myArgs[limboIndex].DataValue;
                    }
                }
                catch (Exception e)
                {
                    myResult = "IoTsmart 3 Error in Normal UPnP Call: device:" + theDevice.FriendlyName + " Endpoint:" + myService.ControlURL + " serviceid:" + myService .ServiceID+ "method:" + method + " args:" + arguments + " error:" + e.Message;
                        System.Console.WriteLine(myResult.ToString());
                        
                        ReportError(myResult.ToString());
                        myResult = null;
                }

            }

            return myResult;
        }

        public object InvokeStorageServiceSync(UPnPDevice theDevice, string method, string config, string storageid)
        {
            if (theDevice == null)
                return null;
            object myResult = null;

            try
            {
                UPnPService myService = GetIoTSoapService(theDevice, "urn:upnp-org:serviceId:StorageManager");
                

                UPnPArgument[] arguments = null;
                UPnPArgument id = new UPnPArgument("id", storageid);
                UPnPArgument fileconfig = new UPnPArgument("config", config);
                UPnPArgument result = new UPnPArgument("result", "");

                switch (method)
                {


                    case "createStorageDevice":
                        arguments = new UPnPArgument[2];
                        arguments[0] = fileconfig;
                        arguments[1] = result;
                        break;

                    case "createStorageDeviceLocal":
                        arguments = new UPnPArgument[2];
                        arguments[0] = fileconfig;
                        arguments[1] = result;
                        break;

                    case "deleteStorageDevice":
                        arguments = new UPnPArgument[2];
                        arguments[0] = id;
                        arguments[1] = result;

                        break;
                    case "deleteStorageDeviceLocal":
                        arguments = new UPnPArgument[2];
                        arguments[0] = id;
                        arguments[1] = result;

                        break;
                    case "getStorageDeviceConfig":
                        arguments = new UPnPArgument[2];
                        arguments[0] = id;
                        arguments[1] = result;

                        break;
                    case "getStorageDevices":
                        arguments = new UPnPArgument[1];
                        arguments[0] = result;

                        break;
                    case "getSupportedStorageDevices":
                        arguments = new UPnPArgument[1];
                        arguments[0] = result;

                        break;
                    case "updateStorageDevice":
                        arguments = new UPnPArgument[2];
                        arguments[0] = fileconfig;
                        arguments[1] = result;
                        break;
                    case "updateStorageDeviceLocal":
                        arguments = new UPnPArgument[2];
                        arguments[0] = fileconfig;
                        arguments[1] = result;
                        break;
                    


                }



                if (myService != null)
                {
                    myService.OnInvokeError += new UPnPService.UPnPServiceInvokeErrorHandler(this.HandleInvokeError);
                    myService.OnInvokeResponse += new UPnPService.UPnPServiceInvokeHandler(this.HandleInvoke);

                    try
                    {
                        myResult = myService.InvokeSync(method, arguments);

                        myResult = arguments[arguments.GetLength(0) - 1].DataValue;

                    }
                    catch (Exception e)
                    {
                        myResult = "IoTsmart 2 Error in Storage UPnP Call: " + method + " args:" + arguments + " error:" + e.Message;
                        System.Console.WriteLine(myResult.ToString());
                        
                        ReportError(myResult.ToString());
                        myResult = null;
                    }

                }
            }
            catch (Exception e)
            {
                ReportError("InvokeStorageServiceSync error:method:" + method + ",id:" + storageid + ",config:" + config + "error:" + e.Message);
            }
            return myResult;
        }


        /// <summary>
        /// Creates UPnPArgument objects for a string with argument/value pairs
        /// </summary>
        /// <param name="arguments">The arguments, following the format arg1=X;arg2=Y</param>
        public UPnPArgument[] CreateUPnPArguments(string arguments)
        {UPnPArgument[] returnArgs;
            char[] splitchars = new char[1];

            splitchars[0] = ';';

            string[] myarguments=arguments.Split(splitchars);

            int upperbound = myarguments.GetLength(0);

            if (upperbound>0)
                returnArgs=new UPnPArgument[upperbound];
            else
                returnArgs=null;
            int returnargspos=0;

            for (int pos = 0; pos < upperbound; pos++)
            {char[] splitcharsinner = new char[1];
                UPnPArgument myUPnPArgument=null;
                string[] myargument;
                splitcharsinner[0] = '=';

                myargument = myarguments[pos].Split(splitcharsinner);
                
                if (myargument.GetLength(0)==2)
                {string value=myargument[1].Trim();
                    if (value!="")
                        value=value.Trim();

                    myUPnPArgument=new UPnPArgument(myargument[0].Trim(),value);

                    returnArgs[returnargspos] = myUPnPArgument;
                    returnargspos++;
                }
                else if (myargument.GetLength(0) == 1)
                {
                    myUPnPArgument = new UPnPArgument(myargument[0].Trim(), "");

                    returnArgs[returnargspos] = myUPnPArgument;
                    returnargspos++;
                }
            }

            return returnArgs;
        }

        /// <summary>
        /// This method is called by the UPnP framework whenever a new UPnP device has been discovered
        /// </summary>
        override public void HandleAddedDevice(UPnPInternalSmartControlPoint sender, UPnPDevice device)
        {
           
            base.HandleAddedDevice(sender, device);

            System.Console.WriteLine("Device " + device.FriendlyName + " found");
           // return;

            bool dacexists = false;

            lock (m_IoTlock)
            {

                if (m_applicationdevicemgr != null)
                   // m_applicationdevicemgr.AddDevice(device);
                try
                {string dynamicwsendpoint="";

                    
                    if (device.BaseURL!=null)
                        dynamicwsendpoint = device.BaseURL.ToString();

                    try
                    {
                        bool hidnotexists = false;

                        //diferentiate between limbo generated devices and normal IoT devices
                        UPnPService theIoTService = GetIoTSoapService(device, m_IoTserviceid);

                        if (theIoTService != null)
                            hidnotexists = RegisterHIDForService(device, theIoTService, dynamicwsendpoint);//Check if a HID for the device already exist
                        else
                        {
                            theIoTService = GetIoTSoapService(device, m_limboIoTserviceid);
                            if (theIoTService != null)
                                hidnotexists = RegisterHIDForService(device, theIoTService, dynamicwsendpoint);
                        }

                        if (!hidnotexists)
                        {
                            object ws = InvokeIoTServiceSync(device, "GetDACEndpoint");//Check if the device belongs to another DAC

                            if (ws != null && ws.ToString() != "")
                                dacexists = true;
                        }

                    }
                    catch (Exception e)
                    {
                        ReportError(device, "Error creating HID for IoT UPnPService:" + e.Message);
                    }

                    if (!dacexists)
                    {

                        try
                        {
                            MakeApplicationBinding(device);//Create a PID for the device
                        }
                        catch (Exception e)
                        {
                            string errorstring = "MakeApplicationBindingException:" + e.Message;

                            ReportError("MakeApplicationBindingException:" + e.Message);
                        }
                    }


                    string devicegateway = GetGatewayFromDevice(device);
                    IoTGateway myGateway = GetGatewayFromString(devicegateway);

                    if (myGateway == null && devicegateway != "")
                    {
                        //A device has been found at a gateway we don´t knwo of, therefore create gateway object
                        myGateway = new IoTGateway(devicegateway);

                        myGateway.SetAutomaticDiscoveryAndResolve(m_automaticdiscovery, m_automaticresolve);
                        m_gateways.Add(myGateway);

                    }

                    if (device.DeviceURN.Contains("IoTdiscoverymanager"))
                    {
                        //If a discovery manager has been found check if a discovery process is going on
                        object ws1 = InvokeIoTServiceSync(device, "GetProperty", "property=discoverystatus");

                        if (m_gateway == devicegateway)
                            m_discoveryManagers.Add(device);

                        if (m_gateway == devicegateway)
                            {
                                myGateway.AddDiscoveryManager(device, m_automaticdiscovery);

                                if (!dacexists)
                                {//Take ownership of thsi Discovery Manager
                                    object ws2 = InvokeIoTServiceSync(device, "SetDACEndpoint", "endpoint=" + m_DACEndpoint);//We are the ones that "owns" the device
                                }
                            }
                        else
                            myGateway.AddDiscoveryManager(device, false);

                        if (ws1 != null && (ws1.ToString() == "discoverycompleted" || ws1.ToString() == "discoverystarted"))
                        {

                            m_formdevicehandler(this, device);//Inform external devicehandler that we have added this device


                            return;
                        }


                       //Maybe we should activate a discovery process, here?

                        /*if (m_gateway == devicegateway && m_automaticdiscovery)
                        {
                            object ws3 = InvokeIoTServiceSync(device, "DiscoverDevices"); 
                        }*/

                        if (m_automaticresolve && m_gateway == devicegateway)
                            ResolveDevices(devicegateway, device.FriendlyName);

                        
                        }
                    else if ((device.DeviceURN.Contains("IoTdevice") || device.DeviceURN.Contains("IoTadevice")) && dacexists)
                    {//the device belongs to another DAC

                        System.Console.WriteLine("Device " + device.FriendlyName + " at "+devicegateway+ "found");
                        if (!device.UniqueDeviceName.Contains("IoTStorageDevice") && !device.UniqueDeviceName.Contains("StorageManagerDevice"))
                        {
                            m_externalIoTdevices.Add(device);//add it to our list of external devices
                            if (devicegateway == "")
                                myGateway = GetGatewayFromString(m_gateway);
                            myGateway.AddDevice(device, "IoTdevice");
                        }
                        else
                        {
                            m_storagedevices.Add(device);
                            if (devicegateway == "")
                                myGateway = GetGatewayFromString(m_gateway);
                            myGateway.AddDevice(device, "storagedevice");
                        }
                    }
                    else if ((device.DeviceURN.Contains("IoTdevice") || device.DeviceURN.Contains("IoTadevice")) && !dacexists)
                    {//the device belongs to this DAC

                        try
                        {
                            
                            if (device.DeviceURN.Contains("IoTadevice") && !dacexists)
                            {//This section should probably be deleted, PR
                                try
                                {
                                    XmlDocument myDoc = new XmlDocument();

                                    myDoc.LoadXml("<sensor><type>Light</type><vendor>Phidget</vendor></sensor>");

                                    XmlNode myNode = myDoc.SelectSingleNode("/*");

                                    XmlDocument deviceDoc = m_ontology.ResolveDevice(myNode);

                                    if (deviceDoc != null)
                                    {
                                        XmlNode testNode = deviceDoc.SelectSingleNode("//*[local-name()='device']");
                                        if (testNode != null)
                                        {
                                            device.DeviceURN = device.DeviceURN.Replace("IoTadevice", "IoTdevice");
                                        }
                                    }
                                }
                                catch (Exception e)
                                {

                                }

                            }


                            object ws4 = InvokeIoTServiceSync(device, "GetProperty", "property=gateway");

                            if (ws4 == null || ws4.ToString() == "")
                            {//KOOL Need to set gateway of limbodevices.
                                object ws5 = InvokeIoTSetPropertyService(device, "gateway", m_gateway);
                            }

                            System.Console.WriteLine("trace past  InvokeIoTServiceSync(device, 'SetProperty', 'property=gateway;value=' + m_gateway)");

                            if (!device.UniqueDeviceName.Contains("IoTStorageDevice") && !device.UniqueDeviceName.Contains("StorageManagerDevice"))
                            {
                                AddIoTdevice(device);
                                if (devicegateway == "")
                                    myGateway = GetGatewayFromString(m_gateway);
                                myGateway.AddDevice(device, "IoTdevice");
                            }
                            else
                            {
                                m_storagedevices.Add(device);
                                if (devicegateway == "")
                                    myGateway = GetGatewayFromString(m_gateway);
                                myGateway.AddDevice(device, "storagedevice");
                            }




                            if (!devicegateway.StartsWith("External_"))
                            {

                                //Kool determine if we have an IoTUDN, default is friendly name
                                string IoTUDN = device.FriendlyName;
                                object IoTUDNobj = InvokeIoTServiceSync(device, "GetProperty", "property=IoTUDN");
                                if (IoTUDNobj != null && IoTUDNobj.ToString() != "")
                                    IoTUDN = IoTUDNobj.ToString();

                                //Check if we have the ontology URI (If so bind the PID to the instance).
                                string deviceURI = "";
                                object deviceURIobj = InvokeIoTServiceSync(device, "GetProperty", "property=deviceURI");
                                if (deviceURIobj != null && deviceURIobj.ToString() != "")
                                    deviceURI = deviceURIobj.ToString();

                                if (IoTUDN != "" && deviceURI != "")
                                {
                                    m_ontology.UpdateURIwithIoTUDN(deviceURI, IoTUDN);
                                }

                                //take ownership of this device
                                object ws6 = InvokeIoTServiceSync(device, "SetDACEndpoint", "endpoint=" + m_DACEndpoint);
                                
                                //Check if it already has a WS
                                object ws7 = InvokeIoTServiceSync(device, "GetWSEndpoint");

                                if (ws7 == null || ws7.ToString() == "")
                                {
                                    string staticWSHID = "";
                                    if (!device.DeviceURN.Contains("applicationdevicemanager"))
                                    {
                                        //Create a static WS and a HID
                                        staticWSHID = CreateWSForDevice(device, IoTUDN + ":StaticWS");
                                    }
                                    else
                                    {
                                        staticWSHID = CreateWSForDevice(device, IoTUDN + ":" + devicegateway + ":StaticWS");
                                    }

                                    //Tell the device about its HID
                                    object ws8 = InvokeIoTSetPropertyService(device, "IoTidStaticWS", staticWSHID);


                                    //KOOLNew 
                                    object HIDdesc = InvokeIoTServiceSync(device, "GetProperty", "property=IoTidStaticWSDescription");
                                    object descRes = null;
                                    if (HIDdesc != null)
                                    {
                                        if (HIDdesc.ToString() == "")
                                        {
                                            descRes = InvokeIoTSetPropertyService(device, "IoTidStaticWSDescription", IoTUDN + ":StaticWS");
                                        }
                                    }
                                    else
                                        descRes = InvokeIoTSetPropertyService(device, "IoTidStaticWSDescription", IoTUDN + ":StaticWS");

                                    //Create a HID for the dynamic WS (which has same endpoint as UPnP)
                                    string dynamicWSHID = CreateHID(device, dynamicwsendpoint, IoTUDN + ":DynamicWS");
                                    object ws9 = InvokeIoTSetPropertyService(device, "IoTidDynamicWS", dynamicWSHID);
                                    object ws10 = InvokeIoTSetPropertyService(device, "dynamicWSEndpoint", dynamicwsendpoint);
                                    
                                    //KOOLNew 
                                    HIDdesc = InvokeIoTServiceSync(device, "GetProperty", "property=IoTidDynamicWSDescription");

                                    if (HIDdesc != null)
                                    {
                                        if (HIDdesc.ToString() == "")
                                        {
                                            descRes = InvokeIoTSetPropertyService(device, "IoTidDynamicWSDescription", IoTUDN + ":DynamicWS");
                                        }
                                    }
                                    else
                                        descRes = InvokeIoTSetPropertyService(device, "IoTidDynamicWSDescription", IoTUDN + ":DynamicWS");

                                    //Create a HID for the UPnP base url
                                    string UPnPHID = CreateHID(device, dynamicwsendpoint, IoTUDN + ":UPnP");
                                    object ws11 = InvokeIoTSetPropertyService(device, "IoTidUPnP", UPnPHID);
                                    object ws12 = InvokeIoTSetPropertyService(device, "UPnPEndpoint", dynamicwsendpoint);
                                    //KOOLNew 
                                    HIDdesc = InvokeIoTServiceSync(device, "GetProperty", "property=IoTidUPnPDescription");


                                    if (HIDdesc != null)
                                    {
                                        if (HIDdesc.ToString() == "")
                                        {
                                            descRes = InvokeIoTSetPropertyService(device, "IoTidUPnPDescription", IoTUDN + ":UPnP");
                                        }
                                    }
                                    else
                                        descRes = InvokeIoTSetPropertyService(device, "IoTidUPnPDescription", IoTUDN + ":UPnP");


                                    //Check if device already has IoT WS (whcih it shoudl since CreateWS has been called
                                    object wsIoT = InvokeIoTServiceSync(device, "GetIoTWSEndpoint");

                                    if (wsIoT != null)
                                    {
                                        string IoTWSEndpoint = wsIoT.ToString();

                                        //Create a HID for the IoT WS
                                        string IoTWSHID = CreateHID(device, IoTWSEndpoint, IoTUDN + ":IoTWS");
                                        object ws13 = InvokeIoTSetPropertyService(device, "IoTidIoTWS", IoTWSHID);
                                        object ws14 = InvokeIoTSetPropertyService(device, "IoTWSEndpoint", IoTWSEndpoint);
                                        //KOOLNew 
                                        HIDdesc = InvokeIoTServiceSync(device, "GetProperty", "property=IoTidIoTWSDescription");

                                        if (HIDdesc != null)
                                        {
                                            if (HIDdesc.ToString() == "")
                                            {
                                                descRes = InvokeIoTSetPropertyService(device, "IoTidIoTWSDescription", IoTUDN + ":IoTWS");
                                            }
                                        }
                                        else
                                            descRes = InvokeIoTSetPropertyService(device, "IoTidIoTWSDescription", IoTUDN + ":IoTWS");

                                    }


                                    //do the same as above for the Energy WS
                                    object wsEnergy = InvokeIoTServiceSync(device, "GetProperty", "property=energyWSEndpoint");

                                    if (wsEnergy != null)
                                    {
                                        string energyWSEndpoint = wsEnergy.ToString();

                                        string energyWSHID = CreateHID(device, energyWSEndpoint, IoTUDN + ":EnergyWS");
                                        object ws15 = InvokeIoTSetPropertyService(device, "IoTidEnergyWS", energyWSHID);
                                        //KOOLNew 
                                        HIDdesc = InvokeIoTServiceSync(device, "GetProperty", "property=IoTidEnergyWSDescription");

                                        if (HIDdesc != null)
                                        {
                                            if (HIDdesc.ToString() == "")
                                            {
                                                descRes = InvokeIoTSetPropertyService(device, "IoTidEnergyWSDescription", IoTUDN + ":EnergyWS");
                                            }
                                        }
                                        else
                                            descRes = InvokeIoTSetPropertyService(device, "IoTidEnergyWSDescription", IoTUDN + ":EnergyWS");

                                    }
                                    if (m_automaticdevicestorage)
                                    {
                                        ProcessDeviceStorage(device, devicegateway);

                                    }

                                    //REgister a HID for each UPnP service
                                    foreach (UPnPService theService in device.Services)
                                    {
                                        try
                                        {
                                            theService.SetSOAPTunnelAddress(m_soaptunnelIPaddress, m_soaptunnelprefix, m_soaptunnelsuffix, m_soaptunnelport.ToString());

                                            if (theService.ServiceID != m_IoTserviceid && theService.ServiceID != m_limboIoTserviceid)
                                                RegisterHIDForService(device, theService, dynamicwsendpoint);

                                        }
                                        catch (Exception e)
                                        {
                                            ReportError(device, "Error creating HID for UPnPService:" + e.Message);
                                        }

                                    }




                                }


                            }
                        }
                        catch (Exception e)
                        {
                            ReportError(device, e.Message);
                            System.Console.WriteLine("Exception in handle added device:" + e.Message);
                        }

                    }


                    else if (device.DeviceURN.Contains("semanticdevice"))
                    {
                        m_semanticdevices.Add(device);
                        myGateway.AddDevice(device, "semanticdevice");
                        InvokeIoTServiceSync(device, "SetDACEndpoint", "endpoint=" + m_DACEndpoint);
                    }
                    else if (device.DeviceURN.Contains("unresolved"))
                    {
                        m_unresolveddevices.Add(device);
                        myGateway.AddDevice(device, "unresolved");
                        InvokeIoTServiceSync(device, "SetDACEndpoint", "endpoint=" + m_DACEndpoint);
                    }

                    else
                    {
                        m_nonIoTdevices.Add(device);
                        myGateway.AddDevice(device, "nonIoT");
                    }

                    try
                    {
                        //Once all WS and HID creation has been done, synchronise so that the UPnP client will have updated device XML
                        SynchroniseUPnPXml(device);
                    }
                    catch (Exception e)
                    {
                        ReportError(device, e.Message);
                    }

                    try
                    {
                        //inform external UPnP device listeners that this device has been added
                        if (m_formdevicehandler != null)
                             m_formdevicehandler(this, device);

                    }

                    catch (Exception e)
                    {
                        ReportError(device, "Error informing formdevicehandler of adding device:" + e.Message);
                    }

                    try
                    {
                        if (!device.UniqueDeviceName.Contains("IoTStorageDevice") && !device.UniqueDeviceName.Contains("StorageManagerDevice") && !device.DeviceURN.Contains("applicationdevicemanager") && !device.DeviceURN.Contains("servicemanager") && !device.DeviceURN.Contains("discoverymanager"))
                        {
                            //inform external URL listeners that this device has been added
                            InformExternalWSList("deviceadded", device);
                        }
                    }
                    catch (Exception e)
                    {
                        ReportError("Error informing external callback of adding device:" + e.Message);
                    }
                }

                catch (Exception e)
                {
                }

            }//locked until here
        }

       
        public void ProcessDeviceStorage(UPnPDevice device, string devicegateway)
        {
            if (!device.UniqueDeviceName.Contains("IoTStorageDevice") && !device.UniqueDeviceName.Contains("StorageManagerDevice"))
            {
                object filesystemdeviceid = InvokeIoTServiceSync(device, "GetProperty", "property=filesystemdeviceid");
                string fileidstring = "";

                if (filesystemdeviceid != null)
                    fileidstring = filesystemdeviceid.ToString();

                if (devicegateway == m_gateway || devicegateway == "")
                    FindFileSystemDeviceForDevice(device, fileidstring, "IoTstorage_" + device.FriendlyName);
            }
            else if (!device.UniqueDeviceName.Contains("StorageManagerDevice"))
            {
                string filesystemudn = device.UniqueDeviceName;
                string filesystemid = filesystemudn.Replace("IoTStorageDevice+", "");
                //string filesystemid = filesystemudn;

                if (devicegateway == m_gateway || devicegateway == "")
                    FindDeviceForFileSystemDevice(device, "//*[name()='filesystemdeviceid' and .='" + filesystemid + "']");
            }
            else
            {
                {
                    m_upnpstoragemanager = device;
                    
                    UPnPDevice[] myDevices = GetIoTDevicesByXpath("//*[name()='gateway' and .='" + m_gateway + "']");

                    foreach (UPnPDevice theDevice in myDevices)
                    {
                       if (!device.UniqueDeviceName.Contains("IoTStorageDevice") && !device.UniqueDeviceName.Contains("StorageManagerDevice") && !device.DeviceURN.Contains("applicationdevicemanager") && !device.DeviceURN.Contains("servicemanager") && !device.DeviceURN.Contains("discoverymanager"))
                            FindFileSystemDeviceForDevice(device, "","IoTstorage_" + device.FriendlyName);
                    }
                    
                }
            }
        }

        /// <summary>
        /// This method is called by the UPnP framework whenever a  UPnP device has been removed (and has left the network)
        /// </summary>
        override public void HandleRemovedDevice(UPnPInternalSmartControlPoint sender, UPnPDevice device)
        {
            lock (m_IoTlock)
            {
                try
                {
                    //Check if we have the ontology URI (If so need to remove the instance from the Ontology).
                    string deviceURI = "";
                    object deviceURIobj = InvokeIoTServiceSync(device, "GetProperty", "property=deviceURI");
                    if (deviceURIobj != null && deviceURIobj.ToString() != "")
                        deviceURI = deviceURIobj.ToString();

                    if (deviceURI != "")
                    {
                        m_ontology.RemoveInstanceWithDeviceURI(deviceURI);
                    }

                    string gateway = device.GetCustomFieldFromDescription("gateway", "IoT");


                    if (!gateway.Contains("External_") && m_removehidsonstop)
                        RemoveAllHIDS(device);

                    base.HandleRemovedDevice(sender, device);



                    try
                    {

                        RemoveIoTdevice(device);
                        
                    }
                    catch (Exception e)
                    {
                        ReportError("Error DAC removing device:" + e.Message);
                    }

                    try
                    {
                        m_storagedevices.Remove(device);
                    }
                    catch (Exception e)
                    {
                        ReportError("Error DAC removing device:" + e.Message);
                    }

                    try
                    {
                        m_unresolveddevices.Remove(device);
                    }
                    catch (Exception e)
                    {
                        ReportError("Error DAC removing device:" + e.Message);
                    }

                    try
                    {
                        m_semanticdevices.Remove(device);
                    }
                    catch (Exception e)
                    {
                        ReportError("Error DAC removing device:" + e.Message);
                    }
                    try
                    {
                        m_nonIoTdevices.Remove(device);
                    }
                    catch (Exception e)
                    {
                        ReportError("Error DAC removing device:" + e.Message);
                    }


                    try
                    {
                        foreach (IoTGateway theGateway in m_gateways)
                        {
                            theGateway.RemoveDevice(device);
                        }
                    }
                    catch (Exception e)
                    {
                        ReportError("Error gateway removing device:" + e.Message);
                    }

                    try
                    {
                        if (m_formremovedevicehandler != null)
                            m_formremovedevicehandler(this, device);
                    }
                    catch (Exception e)
                    {
                        ReportError("Error form removing device:" + e.Message);
                    }

                    try
                    {
                        if (!device.UniqueDeviceName.Contains("IoTStorageDevice") && !device.UniqueDeviceName.Contains("StorageManagerDevice") && !device.DeviceURN.Contains("applicationdevicemanager") && !device.DeviceURN.Contains("servicemanager") && !device.DeviceURN.Contains("discoverymanager"))
                        {
                            InformExternalWSList("devicedeleted", device);
                        }
                    }
                    catch (Exception e)
                    {
                        ReportError("Error informing external callback of removing device:" + e.Message);
                    }
                }
                catch (Exception e)
                {
                }
            }
        }

        /// <summary>
        /// This method is called UPnP framework whenever a  UPnP device has been updated
        /// </summary>
        override public void HandleUpdatedDevice(UPnPInternalSmartControlPoint sender, UPnPDevice device)
        {
            base.HandleUpdatedDevice(sender, device);

            lock (m_IoTlock)
            {
                try
                {
                    if (m_formupdateddevicehandler != null)
                        m_formupdateddevicehandler(this, device);
                }
                catch (Exception e)
                {

                }
            }

        }

        /// <summary>
        /// This method is called to clean up the Network Manager of unused HIDs when a Device has left the network
        /// </summary>
        void RemoveAllHIDS(UPnPDevice device)
        {
            try {
            NetworkManager.NetworkManagerApplicationService myNetworkManager = new NetworkManager.NetworkManagerApplicationService();

            if (m_networkmanagerurl != "")
                myNetworkManager.Url = m_networkmanagerurl;

            string IoTidStaticWS=device.GetCustomFieldFromDescription("IoTidStaticWS","IoT");
            string IoTidIoTWS = device.GetCustomFieldFromDescription("IoTidIoTWS", "IoT");
            string IoTidDynamicWS=device.GetCustomFieldFromDescription("IoTidDynamicWS","IoT");
            string IoTidEnergyWS=device.GetCustomFieldFromDescription("IoTidEnergyWS","IoT");
            string IoTidUPnP=device.GetCustomFieldFromDescription("IoTidUPnP","IoT");

            if (IoTidStaticWS!=null&&IoTidStaticWS!="")
                myNetworkManager.removeHID(IoTidStaticWS);
           
                if (IoTidIoTWS != null && IoTidIoTWS != "")
                myNetworkManager.removeHID(IoTidIoTWS);

            if (IoTidDynamicWS!=null&&IoTidDynamicWS!="")
                myNetworkManager.removeHID(IoTidDynamicWS);

            if (IoTidEnergyWS!=null&&IoTidEnergyWS!="")
                myNetworkManager.removeHID(IoTidEnergyWS);

            if (IoTidUPnP!=null&&IoTidUPnP!="")
                myNetworkManager.removeHID(IoTidUPnP);

            string IoTidUPnPService_urn_schemas_upnp_org_locationservice_1=device.GetCustomFieldFromDescription("IoTidUPnPService_urn_schemas-upnp-org_locationservice_1","IoT");
            string IoTidUPnPService_urn_upnp_org_serviceId_switchservice_1=device.GetCustomFieldFromDescription("IoTidUPnPService_urn_upnp-org_serviceId_switchservice_1","IoT");
            string IoTidUPnPService_urn_schemas_upnp_org_energyservice_1=device.GetCustomFieldFromDescription("IoTidUPnPService_urn_schemas-upnp-org_energyservice_1","IoT");
            string IoTidUPnPService_urn_upnp_org_serviceId_1=device.GetCustomFieldFromDescription("IoTidUPnPService_urn_upnp-org_serviceId_1","IoT");
            string IoTidUPnPService_urn_schemas_upnp_org_memoryservice_1=device.GetCustomFieldFromDescription("IoTidUPnPService_urn_schemas-upnp-org_memoryservice_1","IoT");
            string IoTidUPnPService_urn_upnp_org_serviceId_IoTServicePort = device.GetCustomFieldFromDescription("IoTidUPnPService_urn_upnp_org_serviceId_IoTServicePort", "IoT");
            string IoTidUPnPService_urn_upnp_org_serviceId_StorageManager = device.GetCustomFieldFromDescription("IoTidUPnPService_urn_upnp_org_serviceId_StorageManager", "IoT");

            if (IoTidUPnPService_urn_schemas_upnp_org_locationservice_1!=null&&IoTidUPnPService_urn_schemas_upnp_org_locationservice_1!="")
                myNetworkManager.removeHID(IoTidUPnPService_urn_schemas_upnp_org_locationservice_1);
            
            if (IoTidUPnPService_urn_upnp_org_serviceId_switchservice_1!=null&&IoTidUPnPService_urn_upnp_org_serviceId_switchservice_1!="")
                myNetworkManager.removeHID(IoTidUPnPService_urn_upnp_org_serviceId_switchservice_1);

            if (IoTidUPnPService_urn_schemas_upnp_org_energyservice_1!=null&&IoTidUPnPService_urn_schemas_upnp_org_energyservice_1!="")
                myNetworkManager.removeHID(IoTidUPnPService_urn_schemas_upnp_org_energyservice_1);
    
            if (IoTidUPnPService_urn_upnp_org_serviceId_1!=null&&IoTidUPnPService_urn_upnp_org_serviceId_1!="")
                myNetworkManager.removeHID(IoTidUPnPService_urn_upnp_org_serviceId_1);
       
            if (IoTidUPnPService_urn_schemas_upnp_org_memoryservice_1!=null&&IoTidUPnPService_urn_schemas_upnp_org_memoryservice_1!="")
                myNetworkManager.removeHID(IoTidUPnPService_urn_schemas_upnp_org_memoryservice_1);

            if (IoTidUPnPService_urn_upnp_org_serviceId_IoTServicePort != null && IoTidUPnPService_urn_upnp_org_serviceId_IoTServicePort != "")
                myNetworkManager.removeHID(IoTidUPnPService_urn_upnp_org_serviceId_IoTServicePort);


            if (IoTidUPnPService_urn_upnp_org_serviceId_StorageManager != null && IoTidUPnPService_urn_upnp_org_serviceId_StorageManager != "")
                myNetworkManager.removeHID(IoTidUPnPService_urn_upnp_org_serviceId_StorageManager);

            }

            catch (Exception e)
            {
                ReportError("Remove hid error for device:"+device.FriendlyName+" message:"+e.Message);
            }
        }

       

        public void FindFileSystemDeviceForDevice(UPnPDevice theDevice, string storageid, string storagename)
        {
            try
            {string myFiledevice="";
            try
            {
                if (storageid!="")
                    myFiledevice = m_storagemanager.getStorageDeviceConfig(storageid);
            }
            catch (Exception e)
            {
                theDevice.AddCustomFieldInDescription("errormessage", "StorageManager excpetion:" + e.Message,"IoT");
            }
           
            if (myFiledevice == "")
                {object myFileDeviceObj=null;

                if (storageid != "")
                    myFileDeviceObj = InvokeStorageServiceSync(m_upnpstoragemanager, "getStorageDeviceConfig", "", storageid);

                else
                    myFileDeviceObj = "No FileSystemDevice";

                if (myFileDeviceObj != null)
                        myFiledevice = myFileDeviceObj.ToString();
                }

                if (myFiledevice.Contains("No FileSystemDevice"))
                {
                    object myConfiguration = InvokeStorageServiceSync(m_upnpstoragemanager, "createStorageDevice", "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><LocalFileSystemDevice Name=\"" + storagename + "\" Path=\"C:/Temp/fsData\"/>","");

                    string configuration = "";
                    
                    //configuration=m_storagemanager.createStorageDevice("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><LocalFileSystemDevice Name=\"" + storagename + "\" Path=\"C:/Temp/fsData\"/>");
                   
                    if (myConfiguration!=null)
                        configuration = myConfiguration.ToString();

                    if (configuration!="")
                    {
                        //configuration = myConfiguration.ToString();

                        configuration = configuration.Replace("&lt;", "<");
                        configuration = configuration.Replace("&gt;", ">");
                        configuration = configuration.Replace("&quot;", "'");
                        XmlDocument myDoc = new XmlDocument();

                        try
                        {
                            myDoc.LoadXml(configuration);
                        }
                        catch (Exception e)
                        {
                            ReportError("XML Exception 1a loading configuration:" + configuration + " message:" + e.Message);
                        }

                        XmlNode myID = myDoc.SelectSingleNode("//*[name()='value']");

                        if (myID != null)
                        {
                            string myconfigstring = myID.InnerText;

                            myconfigstring = myconfigstring.Replace("&lt;", "<");
                            myconfigstring = myconfigstring.Replace("&gt;", ">");
                            myconfigstring = myconfigstring.Replace("&quot;", "'");
                            myconfigstring = myconfigstring.Replace("&#xD;", "");


                            XmlDocument myNewDoc = new XmlDocument();
                            try {
                            myNewDoc.LoadXml(myconfigstring);

                            }
                            catch (Exception e)
                                {
                                    ReportError("XML Exception 2 loading configuration:" + myconfigstring + " message:" + e.Message);
                                }

                            XmlNode myID2 = myNewDoc.SelectSingleNode("//*[name()='LocalFileSystemDevice']");
                            string myattrID = "no filesystemdeviceid found";

                            if (myID2 != null)
                            {
                                XmlAttribute myAttr = myID2.Attributes["ID"];


                                myattrID = myAttr.Value;
                            }
                                
                           object ws= InvokeIoTSetPropertyService(theDevice, "filesystemdeviceid",myattrID);
                            

                        }
                    }
                }
                else if (myFiledevice != "")
                {       string configuration = myFiledevice.ToString();

                        configuration = configuration.Replace("&lt;", "<");
                        configuration = configuration.Replace("&gt;", ">");
                        configuration = configuration.Replace("&quot;", "'");

                        XmlDocument myDoc = new XmlDocument();

                        try
                        {
                            myDoc.LoadXml(configuration);
                        }
                        catch (Exception e)
                        {
                            ReportError("XML Exception 1b loading configuration:" + configuration + " message:" + e.Message);
                        }

                        XmlNode myID = myDoc.SelectSingleNode("//*[name()='value']");

                        if (myID != null)
                        {
                            string myconfigstring = myID.InnerText;

                            myconfigstring = myconfigstring.Replace("&lt;", "<");
                            myconfigstring = myconfigstring.Replace("&gt;", ">");
                            myconfigstring = myconfigstring.Replace("&quot;", "'");
                            myconfigstring = myconfigstring.Replace("&#xD;", "");


                            XmlDocument myNewDoc = new XmlDocument();
                            try
                            {
                                myNewDoc.LoadXml(myconfigstring);

                            }
                            catch (Exception e)
                            {
                                ReportError("XML Exception 2 loading configuration:" + myconfigstring + " message:" + e.Message);
                            }

                            XmlNode myID2 = myNewDoc.SelectSingleNode("//*[name()='LocalFileSystemDevice']");
                            string myattrID = "no filesystemdeviceid found";

                            if (myID2 != null)
                            {
                                XmlAttribute myAttr = myID2.Attributes["ID"];


                                myattrID = myAttr.Value;
                            }

                            UPnPDevice theStorageDevice = GetStorageDeviceByUniqueName("IoTStorageDevice+" + myattrID);

                            if (theStorageDevice != null)
                            {
                                object theHID = InvokeIoTServiceSync(theStorageDevice, "GetProperty", "property=IoTidStaticWS");

                                if (theHID != null)
                                {
                                    string theHIDString = theHID.ToString();

                                    object ws2=InvokeIoTSetPropertyService(theDevice, "IoTidStorage",theHIDString);

                                    string filesystemdeviceurl = "";

                                    filesystemdeviceurl = "http://" + m_soaptunnelIPaddress + ":" + m_soaptunnelport.ToString() + "/SOAPTunneling/0/" + theHIDString + "/";

                                    object ws3 = InvokeIoTSetPropertyService(theDevice, "filesystemdeviceurl", filesystemdeviceurl);

                                }
                            }
                            else
                            {
                                object ws3=InvokeIoTSetPropertyService(theDevice, "filesystemdeviceid",myattrID);
                            }
                        }

                    

                }
            }
            catch (Exception e)
            {
                string errormessage = "Error finding filesystemdevice for device, storagename:" + storagename + ":" + e.Message;
                ReportError(errormessage);
                ReportError(theDevice, errormessage);
            }

        }

        public void FindDeviceForFileSystemDevice(UPnPDevice theFileSystemDevice, string xpath)
        {
            try
            {
                UPnPDevice[] theDevices = GetIoTDevicesByXpath(xpath);

                if (theDevices.GetLength(0) >0)
                {
                    object theHID = InvokeIoTServiceSync(theFileSystemDevice, "GetProperty", "property=IoTidStaticWS");

                    if (theHID != null)
                    {
                        string theHIDString = theHID.ToString();

                        foreach (UPnPDevice theDevice in theDevices)
                        {
                            try
                            {
                                object theInnerHID=InvokeIoTSetPropertyService(theDevice, "IoTidStorage",theHIDString);

                                string filesystemdeviceurl = "";

                                filesystemdeviceurl = "http://" + m_soaptunnelIPaddress + ":" + m_soaptunnelport.ToString() + "/SOAPTunneling/0/" + theHIDString + "/";

                                object ws3 = InvokeIoTSetPropertyService(theDevice, "filesystemdeviceurl", filesystemdeviceurl);
                            }
                            catch (Exception e)
                            {

                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                string errormessage = "Error finding device for filesystemdevice, xpath:" + xpath + ":" + e.Message;
                ReportError(errormessage);
                ReportError(theFileSystemDevice, errormessage);
            }
        }

        /// <summary>
        /// Register a HID for a UPnP Service object with the Newtork Manager 
        /// </summary>
        bool RegisterHIDForService(UPnPDevice device, UPnPService theService,string dynamicwsendpoint)
        {
            try
            {

                string service = theService.SCPDURL;
                string newHID = "";

                //string servicendpoint = service.Replace("_scpd.xml", "_control");
                int lastslashpos = service.LastIndexOf('/');

                string serviceendpoint = service.Substring(0, lastslashpos + 1);


                if (serviceendpoint == "" || device.ParentDevice != null)
                    serviceendpoint = dynamicwsendpoint;
               
                if (theService.__controlurl.Contains("http://"))
                    serviceendpoint = theService.__controlurl.Substring(theService.__controlurl.IndexOf("http://"));
                else
                    serviceendpoint = serviceendpoint + "/" + theService.__controlurl;

                XmlDocument deviceXml = device.GetCurrentDeviceXml();
                string servicestring = "IoTidUPnPService_" + theService.ServiceID.Replace(":", "_");

                if (deviceXml != null)
                {

                    XmlNode IoTNode = deviceXml.SelectSingleNode("//*[name()='" + servicestring + "']");

                    if (IoTNode != null && IoTNode.InnerText != "")
                        return false;
                }
                newHID = CreateHID(device, serviceendpoint, device.FriendlyName + ":" + m_gateway + ":" + "UPnPServiceEndpoint" + theService.ServiceURN);

                InvokeIoTSetPropertyService(device, servicestring, newHID);

                return true;
         
            }
            catch (Exception e)
            {
                ReportError(device, "Error creating HID for UPnPService:" + e.Message);
            }

            return false;
        }

        /// <summary>
        /// Reports an error to the device 
        /// </summary>
        /// <param name="device">The device to informl</param>
        /// <param name="message">the error description</param>
        public void ReportError(UPnPDevice device, string message)
        {
            try
            {
                InvokeIoTSetPropertyService(device, "errormessage",message);
            }
            catch (Exception e)
            {
                ReportError(message);
            }
        }

        /// <summary>
        /// Creates web services for the device 
        /// </summary>
        /// <param name="device">The device</param>
        /// <param name="description">a searchable string to be included in the NM database </param>
        public string CreateWSForDevice(UPnPDevice device,string description)
        {
            string returnstring = "";

            try
            {
                //Call the device to create its Web Services.
                object wsObject = InvokeIoTServiceSync(device, "CreateWS");
                

                if (wsObject != null)
                {
                    string endpoint = wsObject.ToString();


                    if (endpoint != "")
                    {
                        returnstring = CreateHID(device, endpoint, description);
                    }
                }
            }
            catch (Exception e)
            {
                ReportError("Error creating WS for device:" + device.FriendlyName + ":" + e.Message);
            }

            return returnstring;
        }

        /// <summary>
        /// Creates a HID for the device 
        /// </summary>
        /// <param name="endpoint">the endpoint to be associated with the HID</param>
        /// <param name="description">a searchable string to be included in the NM database </param>
        public string CreateHID(UPnPDevice device,string endpoint, string description)
        {
            string myHID = "";
             NetworkManager.NetworkManagerApplicationService myNetworkManager = new NetworkManager.NetworkManagerApplicationService();
            try
            {
               
                if (m_networkmanagerurl != "")
                    myNetworkManager.Url = m_networkmanagerurl;

        
                string desc = "";



                if (description == "")
                    desc = device.FriendlyName + ":" + m_gateway;
                else
                    desc = description;
   
                desc = desc.Replace(" ", "");
             
                myHID = myNetworkManager.createHIDwDesc(desc, endpoint);
    
            }

            catch (Exception e)
            {
                ReportError(device, "DAC Error when creating HID:" + e.Message + "M NM url=" + m_networkmanagerurl + " NM url=" +  myNetworkManager.Url);
            }

            return myHID;
        }

        /// <summary>
        /// Informs the external ULR listeners about device changes, such as addition, deletion et c of devices 
        /// </summary>
        /// <param name="action">the event that has happened</param>
        /// <param name="device">the device</param>
        public void InformExternalWSList(string action, UPnPDevice device)
        {
            char[] mysplitchar=new char[1];

            mysplitchar[0]=',';

            if (m_externalurlWS != null && m_externalurlWS != "")
            {
                string[] myurls = m_externalurlWS.Split(mysplitchar);

                foreach (string url in myurls)
                {
                    //Call external service but apply a XSL-T transform to the device first
                    InformExternalWS(url, action, GetTransformedDeviceXML(device, m_callbacktransform));
                }
            }
        }

        /// <summary>
        /// By providing an XSL-T stylesheet it si possible to transform the device XML into a format preferred by the listener
        /// </summary>
        /// <param name="device">the device to be transformed</param>
        /// <param name="transformurl">a valida XSL-T stylesheet</param>
        public string GetTransformedDeviceXML(UPnPDevice device, string transformurl)
        {
            
            
            XmlDocument myDoc = new XmlDocument();

            

            myDoc = device.GetCurrentDeviceXml();

            XslTransform xsltDoc = new XslTransform();
            xsltDoc.Load(transformurl);


            System.IO.StringWriter xWr = new System.IO.StringWriter();
            xsltDoc.Transform(myDoc, null, xWr, null);

            string resultstring = xWr.ToString();

            

            return resultstring;
        }


        
        /// <summary>
        /// Informs a external ULR listener about device changes, such as addition, deletion et c of devices 
        /// </summary>
        /// <param name="action">a string describing the event</param>
        /// <param name="device">a device xml to send to the external URL </param>
        public void InformExternalWS(string url, string action, string device)
        {
            DACCallBackWS.callbackDACwsdl myCallBackWS = new DACCallBackWS.callbackDACwsdl();

           
            myCallBackWS.Url = url;

            myCallBackWS.Timeout = 3000;
            try
            {
                string myresult = myCallBackWS.DACcallback(action, device);
            }
            catch (Exception e)
            {
                System.Console.WriteLine("Callback failed:" + e.Message + " " + e.ToString());
            }
            //myCallBackWS.DACcallbackAsync(action, device);
        }

        

        protected void HandleInvoke(UPnPService sender, string MethodName, UPnPArgument[] Args, object ReturnValue, object Handle)
        {

            //base.Invoke(new UPnPService.UPnPServiceInvokeHandler(this.HandleInvokeEx), new object[] { sender, MethodName, Args, ReturnValue, Handle });
        }


        protected void HandleInvokeError(UPnPService sender, string MethodName, UPnPArgument[] Args, UPnPInvokeException e, object Handle)
        {
            System.Console.WriteLine("UPnP Invoke Error:" + e.Message);

            
            /*if ((sender == this.service) && (MethodName == this.action.Name))
            {
                string text;
                TimeSpan span = DateTime.Now.Subtract(this.invokeTime);
                if (span.TotalMilliseconds >= 1000)
                {
                    text = string.Concat(new object[] { span.Seconds, ".", span.Milliseconds, "sec" });
                }
                else
                {
                    text = span.Milliseconds + "ms";
                }
                if (e.UPNP == null)
                {
                    this.statusBar.Text = "Invocation error (" + text + "): " + e.ToString();
                }
                else
                {
                    this.statusBar.Text = "Invocation Error Code " + e.UPNP.ErrorCode.ToString() + " (" + text + "): " + e.UPNP.ErrorDescription;
                }
            }*/
        }


        /// <summary>
        /// Binds the device to an persistent, application specific identifier, by using an XSL-T transform with different binding rules
        /// The device can then be identified through a programmer supplied identifier, such as BedroomLight
        /// </summary>
        /// <param name="device">the device that has been discovered</param>
        public void MakeApplicationBinding(UPnPDevice theDevice)
        {

            try
            {
                string bindingfile = m_bindingsurl;
                XmlDocument myDoc = theDevice.GetCurrentDeviceXml();
                if (myDoc == null)
                {
                    theDevice.RefreshDeviceXml();
                    myDoc = theDevice.GetCurrentDeviceXml();
                }
                if (myDoc != null)
                {

                    XslTransform xsltDoc = new XslTransform();
                    xsltDoc.Load(bindingfile);

                    XmlUrlResolver myResolver = new XmlUrlResolver();
                    XsltArgumentList xslArg = new XsltArgumentList();

                    XmlReader tmpXml2 = xsltDoc.Transform(myDoc, xslArg, myResolver);

                    XmlDocument outXml = new XmlDocument();
                    outXml.Load(tmpXml2);

                    XmlNode myAppBinding = outXml.SelectSingleNode("//*[name()='binding']");

                    if (myAppBinding != null)
                    {
                        XmlNodeList myProperties = myAppBinding.SelectNodes("*");

                        foreach (XmlNode property in myProperties)
                        {
                            string propname = "";
                            string propvalue = "";

                            propname = property.Name;

                            if (propname != "locationdata")
                            {
                                propvalue = property.InnerText;
                                propvalue = propvalue.Trim();

                                if (property.SelectSingleNode("*") == null)
                                    propvalue = property.InnerXml;
                                else
                                    propvalue = property.OuterXml;

                                propvalue = propvalue.Trim();

                                InvokeIoTSetPropertyService(theDevice, propname,propvalue);
                                
                            }
                            else
                            {
                                XmlNodeList myLocationProperties = property.SelectNodes("*");

                                foreach (XmlNode locationProperty in myLocationProperties)
                                {
                                    string locpropname = "", locpropvalue = "";

                                    locpropname = locationProperty.Name;
                                    locpropvalue = locationProperty.InnerText;
                                    locpropvalue = locpropvalue.Trim();

                                    InvokeIoTServiceSync(theDevice, "SetProperty", "property=" + locpropname + ";value=" + locpropvalue);
                                    theDevice.AddCustomFieldInDescription(locpropname, locpropvalue, "IoTlocation");
                                }

                            }
                            
                        }
                    }
                }
            }

            catch (Exception e)
            {
                ReportError("Applicationbinding:" + e.Message);

            }
        }

        /// <summary>
        /// The UPnP client keeps a local copy of the device XML as it was when the devie was discovered, as local cache
        /// By calling this method you refresh the client copy of the device XML to fit with the real device XML
        /// </summary>
        /// <param name="device">the device to be refreshed</param>
        public void SynchroniseUPnPXml(UPnPDevice theDevice)
        {
            XmlDocument myDoc = theDevice.GetCurrentDeviceXml();
            
            theDevice.RefreshDeviceXml();
            
            myDoc = theDevice.GetCurrentDeviceXml();
           

            if (myDoc != null)
            {
                XmlNameTable myTable;

                XmlNamespaceManager myMgr = new XmlNamespaceManager(myDoc.NameTable);
                myMgr.AddNamespace("IoT", "IoT");
                myMgr.AddNamespace("IoTlocation", "IoTlocation");

                XmlNodeList IoTnodes = myDoc.SelectNodes("/*/*/IoT:*", myMgr);
                XmlNodeList IoTlocationnodes = myDoc.SelectNodes("/*/*/IoTlocation:*", myMgr);

                XmlNodeList theIoTProperties = myDoc.SelectNodes("//IoT:*", myMgr);
                XmlNodeList theIoTLocationProperties = myDoc.SelectNodes("//IoTlocation:*", myMgr);

                foreach (XmlNode theIoTNode in theIoTProperties)
                {
                    theDevice.AddCustomFieldInDescription(theIoTNode.LocalName, theIoTNode.InnerText, "IoT");
                }

                foreach (XmlNode theIoTNode in theIoTLocationProperties)
                {
                    theDevice.AddCustomFieldInDescription(theIoTNode.LocalName, theIoTNode.InnerText, "IoTlocation");
                }
            }
        }

        public object InvokeIoTSetPropertyService(UPnPDevice theDevice, string property, string value)
        {
            try
            {
                theDevice.AddCustomFieldInDescription(property, value, "IoT");
            }
            catch (Exception e)
            {
                ReportError("Add Custom Field exception:" + e.Message);
            }
            return InvokeIoTServiceSync(theDevice,"SetProperty","property="+property+";value="+value);
        }

        public string GetLocalIoTProperty(UPnPDevice theDevice, string property)
        {
            return theDevice.GetCustomFieldFromDescription(property,"IoT");
        }

        UPnPService GetIoTSoapService(UPnPDevice device,string serviceid)
        {
            UPnPService theService = device.GetService(serviceid);

            if (theService != null)
            {
                theService.SetSOAPTunnelAddress(m_soaptunnelIPaddress, m_soaptunnelprefix, m_soaptunnelsuffix, m_soaptunnelport.ToString());
            }

            return theService;
        }

        void ReportError(string errormessage)
        {
            System.Console.WriteLine(errormessage);
        }

        

	}

    public class NetworkManagerBrowser
    {
        /// <summary>
        /// Finds all HIDs that are external to the local NetworkManager
        /// </summary>
        /// <param name="searchString">NetworkManager description search string, e.g. *Fuglesang*</param>
        /// <returns>A string array with the external HIDs</returns>
        public string[] SearchForExternalHIDs(string searchString)
        {
            NetworkManager.NetworkManagerApplicationService nm = new NetworkManager.NetworkManagerApplicationService();
            //Need to set the right endpoint!!!!
            List<string> returnHIDs = new List<string>();
            //Search the NM with search string

            nm.Url = System.Configuration.ConfigurationSettings.AppSettings["networkmanagerurl"];

            string allMatchingHIDs = "";


            allMatchingHIDs = nm.getHIDsbyDescriptionAsString(searchString);


            if (allMatchingHIDs != "")
            {
                allMatchingHIDs = allMatchingHIDs.Trim();
                List<string> DacHIDs = new List<string>(allMatchingHIDs.Split(' '));

                //Find all local HIDs
                string allLocalMatchingHIDs = nm.getHostHIDsbyDescriptionAsString(searchString);
                //If empty, all HIDs are external
                if (allLocalMatchingHIDs == "")
                    returnHIDs = DacHIDs;
                else
                {
                    allLocalMatchingHIDs = allLocalMatchingHIDs.Trim();
                    List<string> LocalDacHIDs = new List<string>(allLocalMatchingHIDs.Split(' '));
                    foreach (string unknownHID in DacHIDs)
                    {
                        //If not found among the local HIDs it is external
                        if (LocalDacHIDs.Find(delegate(string s) { return s == unknownHID; }) == null)
                            returnHIDs.Add(unknownHID);
                    }
                }

            }

            return returnHIDs.ToArray();
        }

        /// <summary>
        /// Compiles nodes from DeviceXML retrieved from all external ApplicationDeviceManagers
        /// </summary>
        /// <param name="search">The string that is used for selecting nodes in the device XMls</param>
        /// <returns>A string list of found values</returns>
        public string[] CompileDACInformation(string search)
        {
            List<string> returnStrings = new List<string>();

            //Find all DACs
            string[] DACs = SearchForExternalHIDs("ApplicationDeviceManager:*:StaticWS");

            ApplicationDeviceManager.ApplicationDeviceManager dacWS = new ApplicationDeviceManager.ApplicationDeviceManager();
            foreach (string theDAC in DACs)
            {
                dacWS.Url = BuildSOAPTunnelUri(theDAC);
                dacWS.Timeout = 4000;
                try
                {
                    string dacRes = dacWS.GetAllLocalHIDS(search);
                    string[] resList = dacRes.Split(',');
                    if (resList != null)
                        returnStrings.AddRange(resList);
                }
                catch (Exception e)
                {
                    System.Console.WriteLine("Exception when calling the WS GetAllLocalHIDS(" + search + "):" + e.Message);
                }


            }


            return returnStrings.ToArray();
        }

        public string[] CompileExternalDeviceXmlInformation()
        {
            List<string> returnStrings = new List<string>();

            //Find all DACs
            string[] DACs = SearchForExternalHIDs("ApplicationDeviceManager:*:StaticWS");

            ApplicationDeviceManager.ApplicationDeviceManager dacWS = new ApplicationDeviceManager.ApplicationDeviceManager();
            foreach (string theDAC in DACs)
            {
                dacWS.Url = BuildSOAPTunnelUri(theDAC);
                dacWS.Timeout = 4000;
                try
                {
                    string dacRes = dacWS.GetAllLocalDeviceXml();

                    if (dacRes != null)
                        returnStrings.Add(dacRes);
                }
                catch (Exception e)
                {
                    System.Console.WriteLine("Exception when calling the WS GetAllLocalDeviceXml():" + e.Message);
                }


            }


            return returnStrings.ToArray();
        }

        /// <summary>
        /// Creates a URI for using the SOAP with the given HID
        /// </summary>
        /// <param name="HID">The HID that is to be used in the SOAP tunnel call</param>
        public string BuildSOAPTunnelUri(string HID)
        {
            return "http://" + System.Configuration.ConfigurationSettings.AppSettings["soaptunnelIPaddress"].ToString() + ":" + System.Configuration.ConfigurationSettings.AppSettings["soaptunnelport"].ToString() + System.Configuration.ConfigurationSettings.AppSettings["soaptunnelprefix"].ToString() + HID + System.Configuration.ConfigurationSettings.AppSettings["soaptunnelsuffix"].ToString();
        }

    }
    
   
}

