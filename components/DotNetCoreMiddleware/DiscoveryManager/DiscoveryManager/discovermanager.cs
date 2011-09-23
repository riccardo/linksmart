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
using System.Collections.Generic;
using System.Text;
using IoT;
using OpenSource.UPnP;
using System.Threading;
using System.Xml;
using System.Xml.Xsl;
using System.Xml.XPath;
using System.ServiceModel;
using System.Runtime.Serialization;
using System.ServiceModel.Description;

namespace DiscoveryManager
{
    class DiscoveryManagerApp
    {
        static void Main(string[] args)
        {
        }
    }
    /// <summary>
    /// The base class for all discovery managers in IoT. A discovery manager is part of the Application Device Manager. A disoovery manager keeps
    /// track of the devices it has discovered. As long as the devices are unresolved they are treated as Embedded devices
    /// of the Discovery Manager. A discovery manager runs locally on a gateway/PC where it looks for remote devices such
    /// as Bluetooth or RF swiches devices. The discovery manager have direct access to the device objects it has created.
    //
    /// </summary>
    public class DiscoveryManager : IoTDevice
    {
        public List<IoT.IoTDevice> m_activedevices;
        public List<IoT.IoTDevice> m_createddevices;
        
        public string m_soaptunnelprefix="/SOAPTunneling/0/";
        public string m_soaptunnelsuffix = "/0/hola";
        public string m_soaptunnelIPaddress = "127.0.0.1";
        public int m_soaptunnelport = 8082;

        /// <summary>
        /// The IoTDevice constructor
        /// </summary>
        /// <param name="IoTID">The IoT identifier</param>
        /// <param name="name">A friendly name that can be used to identify the device</param>
        /// <param name="vendor">The vendor of the device, this is optional</param>
        /// <param name="deviceURN">A unique type identifer following the UPnP standard, for example
        ///urn:schemas-upnp-org:device:bluetooth:1</param>
        public DiscoveryManager(string IoTID, string name, string vendor, string deviceURN)
            : base(IoTID, name, vendor, deviceURN)

        {
            m_activedevices = new List<IoT.IoTDevice>();//A list of IoT devices that have been discovered by thsi Discover manager and still are active
            m_createddevices = new List<IoT.IoTDevice>();//A list of all IoT devices that have been discovered by this Discover manager and created

            m_soaptunnelprefix = System.Configuration.ConfigurationSettings.AppSettings["soaptunnelprefix"];
            m_soaptunnelsuffix = System.Configuration.ConfigurationSettings.AppSettings["soaptunnelsuffix"];
            m_soaptunnelIPaddress = System.Configuration.ConfigurationSettings.AppSettings["soaptunnelIPaddress"];
            string soaptunnelportstring=System.Configuration.ConfigurationSettings.AppSettings["soaptunnelport"];

            if (soaptunnelportstring != "")
                m_soaptunnelport = System.Convert.ToInt32(soaptunnelportstring);

        }

        /// <summary>
        /// Deletes the discovery manager and stops all its discovered devices.
        /// </summary>
         ~DiscoveryManager()

        {
            try
            {
                foreach (IoTDevice theDevice in m_activedevices)
                    theDevice.Stop();
            }
            catch (Exception e)
            {

            }
            StopDevice();
        }
        

        /// <summary>
        /// Starts the discovery process, should be overriden by your own discovery manager
        /// </summary>
        override public void DiscoverDevices()
        {

        }

        /// <summary>
        /// When a device is discovered using UPnP we need to check if we already know about the devcice or not
        /// </summary>
        /// <param name="theDevice">A IoT device</param>
        public bool DeviceKnown(IoTDevice theDevice)
        {
            string remoteaddress = theDevice.m_remoteaddress;

            foreach (IoTDevice olddevice in m_activedevices)
            {
                if (olddevice.m_remoteaddress == remoteaddress)
                {
                    return true;
                }
            }

            return false;

        }

        /// <summary>
        /// Removes a device form the active list based on its physical adddress, for instance if we loose connection with a bluetooth device
        /// </summary>
        /// <param name="remoteaddress">A remote physical device address, for instance a bluetooth address</param>
        public void RemoveActiveDevice(string remoteaddress)
        {
            foreach (IoTDevice theDevice in m_activedevices)
            {
                if (theDevice.m_remoteaddress == remoteaddress)
                {
                    
                    m_activedevices.Remove(theDevice);
                    theDevice.Stop();
                }
            }
        }

        /// <summary>
        /// Removes a device form the active list based on the IoT Device
        /// </summary>
        /// <param name="theDevice">A IoT device</param>
        public void RemoveActiveDevice(IoTDevice theDevice)
        {

            m_activedevices.Remove(theDevice);
              //theDevice.Stop(); Kool

        }

        public void RemoveDevice(UPnPDevice theDevice)
        {

            
        }

        /// <summary>
        /// Initialises a newly discovered device. Call this method when your Discovery Manager has discovered and 
        /// created a IoT Device and then call the Start method
        /// </summary>
        /// <param name="theDevice">A newly discovered but unstarted IoT device</param>
        public void InitialiseDevice(IoTDevice theDevice)
        {
            
            theDevice.SetGateway(m_gateway);
            theDevice.SetWSDLTransform(System.Configuration.ConfigurationSettings.AppSettings["wsdltransform"]);

            theDevice.SetEventManagerUrl(System.Configuration.ConfigurationSettings.AppSettings["eventmanagerurl"]);
            theDevice.SetNetworkManagerUrl(System.Configuration.ConfigurationSettings.AppSettings["networkmanagerurl"]);

            theDevice.AddServices();

            //add device to the internal device lists of the Discovery Manager
            m_activedevices.Add(theDevice);
            m_createddevices.Add(theDevice);

            if (System.Configuration.ConfigurationSettings.AppSettings["removehidsonstop"] == "yes")
                theDevice.SetRemoveHids(true);
            else
                theDevice.SetRemoveHids(false);

            //retrieves various configuration constants
            string wsport = System.Configuration.ConfigurationSettings.AppSettings["wsport"];
            string wsprefix = System.Configuration.ConfigurationSettings.AppSettings["wsprefix"];
            string wssuffix = System.Configuration.ConfigurationSettings.AppSettings["wssuffix"];
            string energyprofiledirectory = System.Configuration.ConfigurationSettings.AppSettings["energyprofiledirectory"];
            string energypolicydirectory = System.Configuration.ConfigurationSettings.AppSettings["energypolicydirectory"];
            string eventprofiledirectory = System.Configuration.ConfigurationSettings.AppSettings["eventprofiledirectory"];

            if (wsport!=null && wsport!="")
                theDevice.SetWSPort(wsport);

            if (wsprefix != null && wsprefix != "")
                theDevice.SetWSPrefix(wsprefix);

            if (wssuffix != null && wssuffix != "")
                theDevice.SetWSSuffix(wssuffix);

            try
            {
                LoadPolicies(theDevice, energyprofiledirectory, energypolicydirectory);//Load energy policies
            }
            catch (Exception e)
            {
                ReportError("LoadPolicy Exception:" + e.Message);
            }

            try
            {
                LoadEvents(theDevice, eventprofiledirectory);//Load any events associated with the device
            }
            catch (Exception e)
            {
                ReportError("LoadEvents Exception:" + e.Message);
            }

            

            try
            {
                if (System.Configuration.ConfigurationSettings.AppSettings["defaultwsbinding"] != "")
                    theDevice.SetDefaultWSBinding(System.Configuration.ConfigurationSettings.AppSettings["defaultwsbinding"]);
            }
            catch (Exception e)
            {
                ReportError("Default WS Binding Exception:" + e.Message);
            }
        }

        virtual public void LoadPolicies(IoTDevice theDevice, string energyprofiledirectory, string energypolicydirectory)
        {

        }

        /// <summary>
        /// Loads an XML Event description, and adds it to the device XML. Normally this should be done by the Ontology Manager
        /// </summary>
        /// <param name="eventdirectory">A valid directory</param>
        public void LoadEvents(IoTDevice theDevice, string eventdirectory)
        {
            string filename = eventdirectory + "\\" + theDevice.FriendlyName + ".xml";

            try
            {
                XmlDocument theDoc = new XmlDocument();

                theDoc.Load(filename);

                XmlNode myRoot = theDoc.SelectSingleNode("/*");

                theDevice.AddCustomFieldInDescription("eventlist", myRoot.InnerXml, "IoT");
            }
            catch (Exception e)
            {
                ReportError("Event loading error:" + e.Message);
            }
        }

       

        /// <summary>
        /// Stops all devices that match an xpath expression. Yhe Xpath expression is applied to the SCPD XML of the device
        /// </summary>
        /// <param name="xpath">A valid Xpath expression</param>
        public void StopDevices(string xpath)
        {
            if (xpath == "")
            {int upperbound=m_activedevices.Count;

                for (int pos=upperbound-1;pos>=0;pos--)
                {
                    IoTDevice device = m_activedevices[pos];

                    m_activedevices.RemoveAt(pos);
                   
                    device.Stop(); 
                }
            }
            else
            {
                IoTDevice[] myDevices = GetIoTDevicesByXpath(xpath);

                foreach (IoTDevice device in myDevices)
                {
                    m_activedevices.Remove(device);
                    device.Stop();
                }
            }
        }

        /// <summary>
        /// Stops all devices that match a HID. 
        /// </summary>
        /// <param name="xpath">A valid HID</param>
        public void StopDeviceWithHID(string endpointtype, string HID)
        {

            foreach (IoTDevice device in m_activedevices)
            {
                string deviceHID=GetCustomFieldFromDescription(endpointtype, "IoT");

                if (deviceHID == HID)
                {
                    m_activedevices.Remove(device);
                    device.Stop();
                }
            }
            
        }

        /// <summary>
        /// Syncs the aqctive device list with the devices known in NetworkManager. 
        /// </summary>
        
        virtual public void SyncDevices()
        {

        }

        /// <summary>
        /// Starts all devices that match an xpath expression. The Xpath expression is applied to the SCPD XML of the device
        /// </summary>
        /// <param name="xpath">A valid Xpath expression</param>
        public void StartDevices(string xpath)
        {
            if (xpath == "")
            {
                foreach (IoTDevice device in m_createddevices)
                    device.Start();
            }
            else
            {
                IoTDevice[] myDevices = GetCreatedIoTDevicesByXpath(xpath);

                foreach (IoTDevice device in myDevices)
                    device.Start();
            }
        }

        /// <summary>
        /// Returns a list of all devices that match an xpath expression. Should not normally be called by other managers
        /// </summary>
        /// <param name="xpath">A valid Xpath expression</param>
        public IoTDevice[] GetIoTDevicesByXpath(string xpath)
        {
            List<IoTDevice> theDevices = new List<IoTDevice>();

            foreach (IoTDevice device in m_activedevices)
            {

                XmlDocument innerdoc = null;

                try
                {
                    innerdoc=device.GetIoTDeviceXml();

                    XmlNode theMatch = innerdoc.SelectSingleNode(xpath);

                    if (theMatch != null)
                    {
                        theDevices.Add(device);
                    }
                }
                catch (Exception e)
                {
                }

            }

            return theDevices.ToArray();
        }

        /// <summary>
        /// Returns a list of all created devices that match an xpath expression. Should not normally be called by other managers
        /// </summary>
        /// <param name="xpath">A valid Xpath expression</param>
        public IoTDevice[] GetCreatedIoTDevicesByXpath(string xpath)
        {
            List<IoTDevice> theDevices = new List<IoTDevice>();

            foreach (IoTDevice device in m_createddevices)
            {

                XmlDocument innerdoc = null;
                try
                {
                    innerdoc = device.GetIoTDeviceXml();

                    XmlNode theMatch = innerdoc.SelectSingleNode(xpath);

                    if (theMatch != null)
                    {
                        theDevices.Add(device);
                    }
                }
                catch (Exception e)
                {
                }

            }

            return theDevices.ToArray();
        }

        /// <summary>
        /// Given a unique UDN which identifies a device in the network, the device is transformed from an
        /// unresolved device into a know device based on the resolvemessage. This can only be applied to devices that are "unresolved"
        /// </summary>
        /// <param name="UDN">The unique address of the device</param>
        /// <param name="resolvemessage">An XML message that describes which type of device the "unknown" device actually is</param>
        override public void ResolveDevice(string UDN, string resolvemessage)
        {
            Console.WriteLine("IoTService_ResolveDevice_discovery(" + ")");
        }

        /// <summary>
        /// Checks if it is possible to create semantic devices from a semantic device specification, base don available active
        /// devices. Should normally not be called by other managers.
        /// </summary>
        /// <param name="activedevices">List of devices to work with</param>
        /// <param name="semurl">Valid url to the semantic device description</param>
        public void CheckSemanticDevices(List<IoT.IoTDevice> activedevices, string semurl)
        {
            XmlDocument mySemDoc = new XmlDocument();

            // string mysemdesc = "<semanticdevice><weatherstation><windmeter><service></service><grounding>urn:schemas-upnp-org:weatherservice::1</grounding></windmeter><rainsensor/><thermometer/><airpressure/></weatherstation></semanticdevice>";

            mySemDoc.Load(semurl);

            XmlNodeList myNodes = mySemDoc.SelectNodes("/*/*/*");

            int upperbound = myNodes.Count;
            int upnppos = 0;

            IoT.IoTDevice[] myDevices = new IoT.IoTDevice[upperbound];

            for (int pos = 0; pos < upperbound; pos++)
            {
                string devicetype = myNodes[pos].Name;

                foreach (IoT.IoTDevice myDevice in activedevices)
                {
                    if (myDevice.DeviceURN.Contains(devicetype))
                    {
                        myDevice.AddCustomFieldInDescription("semanticdevicedescription", myNodes[pos].OuterXml, "IoT");
                        myDevices[upnppos] = myDevice;
                        upnppos++;
                        break;
                    }
                }
            }

            if (upnppos == upperbound)
            {
                XmlNode myNode = mySemDoc.SelectSingleNode("/*/*");
                string semanticdevicename = myNode.Name;

                string devurn = "";

                if (myNode != null)
                    devurn = myNode.Attributes["urn"].InnerText;

                IoT.IoTDevice semanticdevice = new IoT.IoTDevice("", "WeatherStation", "CNet", devurn);

                semanticdevice.FriendlyName = semanticdevicename;

                semanticdevice.DeviceURN = devurn;

                semanticdevice.ManufacturerURL = "http://www.cnet.se";
                semanticdevice.ModelName = semanticdevicename;
                semanticdevice.ModelDescription = "Semantic Device";
                semanticdevice.ModelNumber = "1";
                semanticdevice.HasPresentation = false;

                foreach (IoT.IoTDevice embeddedDevice in myDevices)
                {
                    XmlDocument myDoc = new XmlDocument();

                    myDoc.LoadXml(embeddedDevice.GetCustomFieldFromDescription("semanticdevicedescription", "IoT"));
                    XmlNodeList myServiceMappings = myDoc.SelectNodes("*/service");

                    activedevices.Remove(embeddedDevice);

                    //UPnPDevice myDevice = new UPnPDevice(1800, 1.0, "\\");

                    int innerbound = myServiceMappings.Count;

                    // UPnPService[] myservices = embeddedDevice.GetServices("urn:schemas-upnp-org:weatherservice::1");

                    // int innerbound=myservices.GetLength(0);

                    foreach (XmlNode myMapping in myServiceMappings)
                    {
                        string newurn, groundingurn;

                        newurn = myMapping.SelectSingleNode("serviceurn").InnerText;
                        groundingurn = myMapping.SelectSingleNode("groundingurn").InnerText;
                        UPnPService semService = embeddedDevice.GetService(groundingurn);
                        semService.ServiceURN = newurn;
                        semService.ServiceID = embeddedDevice.FriendlyName + ":1";
                        semService.SCPDURL = semService.ServiceID;
                        semService.ControlURL = semService.ServiceID;
                        semService.EventURL = semService.ServiceID;
                        semanticdevice.AddService(semService);

                    }


                    //semanticdevice.AddDevice(embeddedDevice);
                }

                semanticdevice.AddCustomFieldInDescription("semanticdevicedescription", mySemDoc.OuterXml, "IoT");

                semanticdevice.StartDevice();

            }
        }

        public System.String ExtractSecurityInfo(XmlNode theresolvemessage)
        {
            XmlNode theSecurityNode = theresolvemessage.SelectSingleNode(".//*[name()='securityInfo']");

            if (theSecurityNode != null)
                return theSecurityNode.OuterXml;
            else
                return "";
        }

        public void SetSOAPTunnelAddress(string ipaddress,string prefix, string suffix,string port)
        {
            m_soaptunnelIPaddress = ipaddress;
            m_soaptunnelprefix = prefix;
            m_soaptunnelsuffix = suffix;

            if (port != "") ;
                m_soaptunnelport = System.Convert.ToInt32(port);
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


    //*****************************The Discovery Manager WS************************'
    [ServiceContract()]
    
    public interface IIoTDiscoveryManagerWSService
    {
        [OperationContract]
        void ResolveDevice(System.String deviceURN, System.String resolveMessage);
        [OperationContract]
        void DiscoverDevices();
        [OperationContract]
        void StartDevices(System.String xpath);
        [OperationContract]
        void StopDevices(System.String xpath);
        
    }

    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single,IncludeExceptionDetailInFaults = true)]
    public class DiscoveryManagerWS : IIoTDiscoveryManagerWSService
    {

        
        DiscoveryManager.DiscoveryManager m_discoveryManager;

        public DiscoveryManagerWS(DiscoveryManager.DiscoveryManager theManager)
        {
            m_discoveryManager = theManager;
        }

        /// <summary>
        /// Given a unique UDN which identifies a device in the network, the device is transformed from an
        /// unresolved device into a know device based on the resolvemessage. This can only be applied to devices that are "unresolved"
        /// </summary>
        /// <param name="UDN">The unique address of the device</param>
        /// <param name="resolvemessage">An XML message that describes which type of device the "unknown" device actually is</param>

        public void ResolveDevice(System.String deviceURN, System.String resolveMessage)
        {
            m_discoveryManager.ResolveDevice(deviceURN, resolveMessage);
        }


        /// <summary>
        /// Starts the discovery process
        /// </summary>
        public void DiscoverDevices()
        {
            
            m_discoveryManager.DiscoverDevices();
            
        }

        /// <summary>
        /// Starts all devices that match an xpath expression. The Xpath expression is applied to the SCPD XML of the device
        /// </summary>
        /// <param name="xpath">A valid Xpath expression</param>
        public void StartDevices(string xpath)
        {

            m_discoveryManager.StartDevices(xpath);

        }

        /// <summary>
        /// Stops all devices that match an xpath expression. Yhe Xpath expression is applied to the SCPD XML of the device
        /// </summary>
        /// <param name="xpath">A valid Xpath expression</param>
        public void StopDevices(string xpath)
        {

            m_discoveryManager.StopDevices(xpath);

        }

        
        
    }

}
