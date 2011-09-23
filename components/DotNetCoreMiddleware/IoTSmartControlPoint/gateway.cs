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
    /// <summary>
    /// The IoTGateway class represents a phsyical gateway and keeps track of all the devices running at the gateway.
    /// It functions as a helper class for the IoTSMartControlPoint 
    /// </summary>
    public class IoTGateway
    {
        public string m_name;
        public string m_IoTserviceid = "urn:upnp-org:serviceId:1";
        public List<UPnPDevice> m_discoverymanagers;

        //A lookup table for all the different Discovery Manager web services for Discovery Managers running at the gateway
        public Dictionary<string, DiscoveryManagerWS.DiscoveryManagerWS> m_discoveryWS= null;

        //A lookup table for all the different Discovery Manager UPnP versions for Discovery Managers running at the gateway
        public Dictionary<string, UPnPDevice> m_discoveryUPnP = null;


        public List<UPnPDevice> m_semanticdevices;
        public List<UPnPDevice> m_IoTdevices;
        public List<UPnPDevice> m_storagedevices;
        public List<UPnPDevice> m_unresolveddevices;
        public List<UPnPDevice> m_nonIoTdevices;

        public bool m_automaticdiscovery = false;
        public bool m_automaticresolve =false;
        public string m_errormessage = "";


        /// <summary>
        /// The IoTGateway constructor
        /// </summary>
        public IoTGateway(string gateway)
        {
            m_name = gateway;
            m_discoverymanagers = new List<UPnPDevice>();
            m_semanticdevices = new List<UPnPDevice>();
            m_IoTdevices = new List<UPnPDevice>();
            m_storagedevices = new List<UPnPDevice>();
            m_unresolveddevices = new List<UPnPDevice>();
            m_nonIoTdevices = new List<UPnPDevice>();
            m_discoveryWS = new Dictionary<string, DiscoveryManagerWS.DiscoveryManagerWS>();
            m_discoveryUPnP = new Dictionary<string, UPnPDevice>();
        }

        /// <summary>
        /// Add a device to the gateway
        /// </summary>
        /// <param name="theDevice">the device to be added</param>
        /// <param name="devicetype">allowed values are IoTdevice,semanticdevice,storagedevice, unresolved. 
        /// In  case of an empty string the devicetype is decided by the deviceURN</param>
        public void AddDevice(UPnPDevice theDevice, string devicetype)
        {
            string newdevicetype;

            if (devicetype == "")
            {
                string deviceURN = theDevice.DeviceURN;

                if (deviceURN.Contains("unresolved"))
                    newdevicetype = "unresolved";
                else if (deviceURN.Contains("IoTdevice"))
                    newdevicetype = "IoTdevice";
                else if (deviceURN.Contains("semanticdevice"))
                    newdevicetype = "semanticdevice";
                else
                    newdevicetype = "unresolved";
            }
            else
                newdevicetype = devicetype;

            if (newdevicetype == "IoTdevice")
                m_IoTdevices.Add(theDevice);
            else if (newdevicetype == "semanticdevice")
                m_semanticdevices.Add(theDevice);
            else if (newdevicetype == "storagedevice")
                m_storagedevices.Add(theDevice);
            else if (newdevicetype == "unresolved")
                m_unresolveddevices.Add(theDevice);
            else
                m_nonIoTdevices.Add(theDevice);
        }

        /// <summary>
        /// Removes a device from the gateway
        /// </summary>
        /// <param name="theDevice">the device to be removed</param>
        public void RemoveDevice(UPnPDevice theDevice)
        {
            try {
                RemoveDiscoveryManager(theDevice);
            }
            catch 
            { }
            try
            {
                m_IoTdevices.Remove(theDevice);
            }
            catch (Exception e)
            {
            }
            try
            {
                m_storagedevices.Remove(theDevice);

            }
            catch (Exception e)
            {
            }
            try
            {
                m_semanticdevices.Remove(theDevice);
            }
            catch (Exception e)
            {
            }
             try
                {
                    m_unresolveddevices.Remove(theDevice);
                }
             catch (Exception e)
                    {
                    }
             try
                    {
                        m_nonIoTdevices.Remove(theDevice);
                    }
             catch (Exception e)
                    {
                    }
            
        }

        public void SetAutomaticDiscoveryAndResolve(bool discovery, bool resolve)
        {
            m_automaticdiscovery = discovery;
            m_automaticresolve = resolve;
        }

        public void InvokeIoTServiceAsync(UPnPDevice theDevice, string method)
        {
            UPnPService myService = theDevice.GetService(m_IoTserviceid);

            if (myService != null)
            {
                UPnPArgument[] myArgs = new UPnPArgument[0];


                myService.OnInvokeError += new UPnPService.UPnPServiceInvokeErrorHandler(this.HandleInvokeError);
                myService.OnInvokeResponse += new UPnPService.UPnPServiceInvokeHandler(this.HandleInvoke);

                myService.InvokeAsync(method, myArgs, null, new UPnPService.UPnPServiceInvokeHandler(this.HandleInvoke), new UPnPService.UPnPServiceInvokeErrorHandler(this.HandleInvokeError));

            }
        }

        public object InvokeIoTServiceSync(UPnPDevice theDevice, string method)
        {
            UPnPService myService = theDevice.GetService(m_IoTserviceid);
            object myResult = null;
            
            if (myService != null)
            {
                UPnPArgument[] myArgs = new UPnPArgument[0];


                myService.OnInvokeError += new UPnPService.UPnPServiceInvokeErrorHandler(this.HandleInvokeError);
                myService.OnInvokeResponse += new UPnPService.UPnPServiceInvokeHandler(this.HandleInvoke);

                try
                {
                    myResult = myService.InvokeSync(method, myArgs);
                }
                catch (Exception e)
                {
                    myResult = "could not find endpoint, gateway.cs:"+e.Message;
                }

            }

            return myResult;
        }

        public void AddDiscoveryManager(UPnPDevice theDevice, bool withdiscovery)
        {
            object myResult = InvokeIoTServiceSync(theDevice, "GetWSEndpoint");

            if (myResult != null&&myResult.ToString()!="")
            {
                string myWSEndpoint = myResult.ToString();

                InternalAddDiscoveryManager(theDevice, myWSEndpoint, withdiscovery);
            }
            else
            {
                myResult = InvokeIoTServiceSync(theDevice, "CreateWS");

                if (myResult != null)
                {
                    string myWSEndpoint = myResult.ToString();

                    InternalAddDiscoveryManager(theDevice, myWSEndpoint, withdiscovery);
                }
            }
        }

        /// <summary>
        /// Removes a discovery manager from the gateway´if the device really is a discovery manager
        /// </summary>
        /// <param name="theDevice">the device to be removed</param>
        public void RemoveDiscoveryManager(UPnPDevice theDevice)
        {
            lock (m_discoveryWS)
            {
                try
                {
                    m_discoveryWS.Remove(theDevice.FriendlyName);
                }
                catch (Exception e)
                {
                }
            }
        }
        public void InternalAddDiscoveryManager(UPnPDevice theDevice, string wsendpoint, bool withdiscovery)
        {
            m_discoverymanagers.Add(theDevice);

            DiscoveryManagerWS.DiscoveryManagerWS theDiscoveryManager=new DiscoveryManagerWS.DiscoveryManagerWS();

            if (wsendpoint != "")
                   theDiscoveryManager.Url = wsendpoint;

            lock (m_discoveryWS)
            {
                try
                {
                    m_discoveryWS.Add(theDevice.FriendlyName, theDiscoveryManager);
                }
                catch (Exception e)
                {
                }
            }

            lock (m_discoveryUPnP)
            {
                try
                {
                    m_discoveryUPnP.Add(theDevice.FriendlyName, theDevice);
                }

                catch (Exception e)
                {
                }
            }

            if (withdiscovery)
                theDiscoveryManager.DiscoverDevices();


        }

        /// <summary>
        /// Starts a discovery process
        /// </summary>
        /// <param name="discoverytype">the friendly name of the disocvery manager to activate, if empty all discoverymanagers are activated</param>
        public void DiscoverDevices(string discoverytype)
        {
            lock (m_discoveryWS)
            {
                foreach (KeyValuePair<string, DiscoveryManagerWS.DiscoveryManagerWS> kvp in m_discoveryWS)
                {
                    try
                    {
                        if (discoverytype==""||kvp.Key==discoverytype)
                            kvp.Value.DiscoverDevices();
                    }
                    catch (Exception e)
                    {
                        
                            lock (m_discoveryUPnP)
                            {try
                                {
                                    if (discoverytype == "" || kvp.Key == discoverytype)
                                        InvokeIoTServiceSync(m_discoveryUPnP[kvp.Key], "DiscoverDevices");
                                }
                                catch (Exception ex)
                                {
                                 }
                            }
                    }
                }
            }
            
        }

        /// <summary>
        /// Creates a Web services for devices
        /// </summary>
        /// <param name="devicetype">only allowed value is IoTdevice</param>
        public void CreateDeviceWS(string devicetype)
        {
            if (devicetype == "IoTdevices")
            {
                foreach (UPnPDevice thedevice in m_IoTdevices)
                {
                    object wsObject = InvokeIoTServiceSync(thedevice, "GetWSEndpoint");

                    if (wsObject == null || wsObject.ToString() == "")
                    {
                        InvokeIoTServiceAsync(thedevice, "CreateWS");
                    }
                }
                
            }
        }

        /// <summary>
        /// Starts devices that match an xpath expression which is evaluated over the device XML
        /// </summary>
        /// <param name="xpath">valid xpath expression</param>
        public void StartDevices(string xpath)
        {
            lock (m_discoveryWS)
            {
                try
                {
                    foreach (KeyValuePair<string, DiscoveryManagerWS.DiscoveryManagerWS> kvp in m_discoveryWS)
                    {
                        try
                        {
                            kvp.Value.StartDevices(xpath);
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }
                catch (Exception e)
                {
                }
            }
        }

        /// <summary>
        /// Stops devices that match an xpath expression which is evaluated over the device XML
        /// </summary>
        /// <param name="xpath">valid xpath expression</param>
        public void StopDevices(string xpath)
        {
            lock (m_discoveryWS)
            {
                try
                {
                    foreach (KeyValuePair<string, DiscoveryManagerWS.DiscoveryManagerWS> kvp in m_discoveryWS)
                    {
                        try
                        {
                            kvp.Value.StopDevices(xpath);
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }
                catch (Exception ex)
                {
                }
            }
        }

        /// <summary>
        /// Returns a UPnPDevice list of IoT devices of certain type
        /// </summary>
        /// <param name="devicetype">is decided by a contains search in DeviceURN</param>
        public List<UPnPDevice> GetIoTDevicesAsUPnP(string devicetype)
        {List<UPnPDevice> returnDevices=new List<UPnPDevice>();

             foreach (UPnPDevice device in m_IoTdevices)
                {
                 if (devicetype=="" || device.DeviceURN.Contains(devicetype)) 
                 {
                     returnDevices.Add(device);
                 }
             }
         return returnDevices;
        }

        /// <summary>
        /// Returns a XML list of IoT devices of certain type
        /// </summary>
        /// <param name="devicetype">is decided by a contains search in DeviceURN</param>
        public XmlDocument GetIoTDevicesAsXML(string devicetype)
        {
            XmlDocument myDoc = null;
            string returnXML = "";
            List<UPnPDevice> myDevices = GetIoTDevicesAsUPnP(devicetype);

            foreach (UPnPDevice device in myDevices)
                {
                 
                    XmlDocument innerdoc = new XmlDocument();

                    try
                    {
                        innerdoc.Load(device.LocationURL);

                        XmlNode theRoot = innerdoc.SelectSingleNode("/*");

                        if (theRoot != null)
                        {
                            returnXML = returnXML + theRoot.OuterXml;
                        }
                    }
                    catch (Exception e)
                    {
                    }
        
                }

                if (returnXML != "")
                {
                    myDoc = new XmlDocument();
                    myDoc.LoadXml("<IoTdevices>"+returnXML+"</IoTdevices>");
                }
         

            return myDoc;
        }

        /// <summary>
        /// Returns an XmlDocument of endpoints to the WS of the IoT devices at the gateway
        /// </summary>
        public XmlDocument GetIoTDevicesEndpoints()
        {
            XmlDocument myDoc = null;
            string returnXML="";
           
            foreach (UPnPDevice device in m_IoTdevices)
            {
                string name = device.FriendlyName;
                object wsendobject = InvokeIoTServiceSync(device, "GetWSEndpoint");
                string wsendstring = "";

                if (wsendobject != null)
                    wsendstring = wsendobject.ToString();

                returnXML = returnXML + "<IoTdevice><friendlyname>" + name + "</friendlyname><wsendpoint>" + wsendstring + "</wsendpoint></IoTdevice>";
            }

            if (returnXML != "")
            {
                myDoc = new XmlDocument();

                myDoc.LoadXml("<IoTdevices>" + returnXML + "</IoTdevices>");
            }

            return myDoc;

        }

        /// <summary>
        /// Returns an XmlDocument containing the device XML for the unresolved devices at the gateway
        /// </summary>
        /// <param name="discoveryManager">the friendly name of the disocvery manager to activate, if empty all discoverymanagers are activated</param>
        public XmlDocument GetUnresolvedDeviceXml(string discoveryManager)
        {
            XmlDocument myDoc = new XmlDocument();
            string devicexml = "";

            for(int i = 0; i < m_discoverymanagers.Count; i ++)
            //foreach (UPnPDevice theMgr in m_discoverymanagers)
            {
                UPnPDevice theMgr = m_discoverymanagers[i];
                if (discoveryManager == "" || discoveryManager == theMgr.FriendlyName)
                {
                    try
                    {
                        XmlDocument innerDoc = new XmlDocument();

                        innerDoc.Load(theMgr.LocationURL);

                        XmlNode theDeviceList = innerDoc.SelectSingleNode("/*/*[name()='device']");

                        if (theDeviceList != null)
                            devicexml = devicexml + theDeviceList.OuterXml;
                    }
                    catch (Exception e)
                    {
                        ReportError("Error get unresolved XML:"+e.Message);
                    }
                }

            }

            myDoc.LoadXml("<unresolved>" + devicexml + "</unresolved>");

            return myDoc;
        }

        protected void HandleInvoke(UPnPService sender, string MethodName, UPnPArgument[] Args, object ReturnValue, object Handle)
        {
            if (MethodName == "GetWSEndpoint")
            {
                UPnPDevice myMgr = sender.ParentDevice;
                string wsendpoint = ReturnValue.ToString();
                InternalAddDiscoveryManager(myMgr, wsendpoint,m_automaticdiscovery);
            }
            //base.Invoke(new UPnPService.UPnPServiceInvokeHandler(this.HandleInvokeEx), new object[] { sender, MethodName, Args, ReturnValue, Handle });
        }


        protected void HandleInvokeError(UPnPService sender, string MethodName, UPnPArgument[] Args, UPnPInvokeException e, object Handle)
        {
          
        }

        public void ReportError(string message)
        {
            if (m_errormessage != "")
                m_errormessage = m_errormessage + ";" + message;
            else
                m_errormessage = message;
        }
    }
}