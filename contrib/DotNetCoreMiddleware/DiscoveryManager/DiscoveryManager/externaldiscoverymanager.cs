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
using IoT;
using DeviceServiceManager;
using DiscoveryManager;
using System.Xml;
using System.Collections.Generic;
using System.Net;

/// <summary>
/// This class is responsible for discovering external devices, using the P2P architecture of IoT
//
/// </summary>
public class ExternalDiscoveryManager : DiscoveryManager.DiscoveryManager
{
    List<string> m_knownhids = null;//List of the exernal HID:s we currently know of

    public ExternalDiscoveryManager(string IoTID, string name, string vendor, string deviceURN)
        : base(IoTID, name, vendor, deviceURN)
    {

        m_knownhids = new List<string>();
    }

    public override void Start()
    {
        StartDevice();
    }

    override public System.String CreateWS()
    {
        IoTWCFServiceLibrary.DiscoveryManagerWS myWS = new IoTWCFServiceLibrary.DiscoveryManagerWS(this);

        InitiateWebService(myWS, "IoTWCFServiceLibrary.IIoTDiscoveryManagerWSService", "ExternalDiscoveryWS");


        return m_wsendpoint;
    }

    /// <summary>
    /// Syncs the aqctive device list with the devices known in NetworkManager. 
    /// </summary>
    override public void SyncDevices()
    {
        GetNewDevicesFromUPnP();

        
    }
    

    /// <summary>
    /// Starts discovery of external devices. 
    /// </summary>
    public override void DiscoverDevices()
    {
        AddCustomFieldInDescription("discoverystatus", "discoverystarted", "IoT");
        SyncDevices();
        AddCustomFieldInDescription("discoverystatus", "discoverycompleted", "IoT");
    }

   
   

    /// <summary>
    /// Get a list of new devices 
    /// </summary>
    public void GetNewDevicesFromUPnP()
    {
        //Create an object that can read from the Network Manager
        NetworkManagerBrowser myNMBrowser = new NetworkManagerBrowser();

        string[] myHIDS = null;

        try
        {
            //Find all external HIDS in NM
           myHIDS = myNMBrowser.CompileDACInformation("IoTidUPnP");
        }
        catch (Exception e)
        {
            ReportError("External Discovery Exception:" + e.Message);
        }
        if (myHIDS == null) return;
        foreach (string HID in myHIDS)
        {
            if (!HIDKnown(HID))
            {
                
                try
                {
                    //Create a local copy device based on the device XML retrieved
                    CreateIoTDevice("http://"+m_soaptunnelIPaddress+":"+m_soaptunnelport.ToString()+"/SOAPTunneling/0/"+ HID+"/",HID);
                }
        
                catch (Exception e)
                {
                    ReportError("External discovery:" + e.Message);
                }

            }
        }


    }

    /// <summary>
    /// Creates a IoT Device from a url to a device xml
    /// </summary>
    /// <param name="url">A url to a device XML</param>
    /// <param name="HID">A valid IoT ID</param>
    void CreateIoTDevice(string url,string HID)
    {
        XmlDocument SCPD = new XmlDocument();

        try
        {
            //Prepare the http request
            HttpWebRequest httpReq = (HttpWebRequest)HttpWebRequest.Create(url);


            httpReq.Timeout = 4000;

            httpReq.Method = "GET";

            httpReq.KeepAlive = false;

            HttpWebResponse httpResponse = (HttpWebResponse)httpReq.GetResponse();

            //Load and parse the retrieved device XML
            SCPD.Load(httpResponse.GetResponseStream());

            httpResponse.Close();

            //Create a IoT device from the device XML and the HID
            CreateIoTDevice(SCPD, HID);

            return;
        }
        catch (Exception e)
        {
            System.Console.WriteLine("CreateIoTDevice Retrieve SPCD failed HID:"+HID+" URL:"+url+" :" + e.Message);
        }

        //If first attempt fails try with new url
        try
        {
            HttpWebRequest httpReq = (HttpWebRequest)HttpWebRequest.Create(url+"gen-desc.xml");

            httpReq.KeepAlive = false;

            httpReq.Timeout = 4000;

            httpReq.Method = "GET";

            HttpWebResponse httpResponse = (HttpWebResponse)httpReq.GetResponse();

            //Load and parse the retrieved device XML
            SCPD.Load(httpResponse.GetResponseStream());

            httpResponse.Close();

            //Create a IoT device from the device XML and the HID
            CreateIoTDevice(SCPD, HID);

            return;
        }
        catch (Exception e)
        {
            System.Console.WriteLine("CreateIoTDevice Retrieve SPCD failed HID:" + HID + " URL:" + url + " :" + e.Message);
        }
    }

    /// <summary>
    /// Creates a IoT Device from an SCPD file (device XML) and a HID
    /// </summary>
    /// <param name="SCPD">A valid SCPD document</param>
    /// <param name="HID">A valid IoT ID</param>
    void CreateIoTDevice(XmlDocument SCPD,string HID)
    {
        XmlNode myNode = SCPD.SelectSingleNode("//*[name()='device']");
        
        UPnPDevice theDevice = null;

        //Start by creating the UPnP part of the device
        if (myNode!=null)
            theDevice = CreateUPnPDevice(myNode.OuterXml,HID);


        //copy the different IoT properties into the new device
        XmlNamespaceManager myMgr = new XmlNamespaceManager(SCPD.NameTable);
        myMgr.AddNamespace("IoT", "IoT");
        myMgr.AddNamespace("IoTlocation", "IoTlocation");

        XmlNodeList IoTnodes = SCPD.SelectNodes("/*/*/IoT:*", myMgr);
        XmlNodeList IoTlocationnodes = SCPD.SelectNodes("/*/*/IoTlocation:*", myMgr);

        XmlNodeList theIoTProperties = SCPD.SelectNodes("//IoT:*", myMgr);
        XmlNodeList theIoTLocationProperties = SCPD.SelectNodes("//IoTlocation:*", myMgr);

        foreach (XmlNode theIoTNode in theIoTProperties)
        {
            if (theIoTNode.LocalName=="gateway")
                theDevice.AddCustomFieldInDescription(theIoTNode.LocalName, "External_" + theIoTNode.InnerText, "IoT");
            else
                theDevice.AddCustomFieldInDescription(theIoTNode.LocalName, theIoTNode.InnerText, "IoT");
        }

        foreach (XmlNode theIoTNode in theIoTLocationProperties)
        {
            theDevice.AddCustomFieldInDescription(theIoTNode.LocalName, theIoTNode.InnerText, "IoTlocation");
        }

 
        //Create service references
        foreach (UPnPService theService in theDevice.Services)
        {
            string serviceid = theService.ServiceID;

            theService.SCPDURL = "_" + serviceid + "_scpd.xml";
            theService.ControlURL = "_" + serviceid + "_control";
            theService.EventURL = "_" + serviceid + "_event";
        }

        m_knownhids.Add(HID);

        //Advertise the new device in the local network
        theDevice.StartDevice();
        

        return;

        

    }

    /// <summary>
    /// Creates a UPnP Device from an SCPD file (device XML) and a HID
    /// </summary>
    /// <param name="SCPD">A valid SCPD document</param>
    /// <param name="HID">A valid IoT ID</param>
    UPnPDevice CreateUPnPDevice(string SPCD, string HID)
    {
        UPnPDevice theDevice = new UPnPDevice(1800, 1.0, "\\");

        XmlDocument myDoc1 = new XmlDocument();

        myDoc1.LoadXml(SPCD);

        XmlNode myNode = myDoc1.SelectSingleNode("//*[name()='URLBase']");

        if (myNode != null)
        {
            XmlNode parentNode = myNode.ParentNode;

            parentNode.RemoveChild(myNode);
        }

        //Parse the XML
        UPnPDevice.ParseDevice(myDoc1.OuterXml, ref theDevice);



        if (theDevice.Services.Length > 0)
        {
            UPnPService[] newServices = new UPnPService[theDevice.Services.Length];
            int pos = 0;
            int length = theDevice.Services.Length;


            //For each service fetch its action descriptions and create a UPnP Service object
            foreach (UPnPService theService in theDevice.Services)
            {
                try
                {
                    XmlDocument myDoc = new XmlDocument();

                    string fetchstring = theService.SCPDURL;

                    if (fetchstring != null && fetchstring != "" && fetchstring[0] != '/')
                        fetchstring = "/" + fetchstring;

                    HttpWebRequest httpReq = (HttpWebRequest)HttpWebRequest.Create("http://" + m_soaptunnelIPaddress + ":" + m_soaptunnelport.ToString() + "/SOAPTunneling/0/" + HID + fetchstring);

                    httpReq.KeepAlive = false;

                    HttpWebResponse httpResponse = (HttpWebResponse)httpReq.GetResponse();

                    myDoc.Load(httpResponse.GetResponseStream());

                    //Parse the service description into a UPnPService object
                    theService.ParseSCPD(myDoc.InnerXml);
                }
                catch (Exception e)
                {
                    ReportError("Error fetching service document:" + e.Message);

                }
            }
        }



        return theDevice;

    }

    void RemoveIoTDevice(string HID)
    {

    }
    bool HIDKnown(string HID)
    {

        foreach (string knownhid in m_knownhids)
        {
            if (knownhid == HID)
                return true;
        }

        return false;
  
    }

    private void ForceDeviceFailSink(UPnPDeviceFactory sender, Uri LocationUri, Exception e)
    {

    }

    private void ForceDeviceOKSink(UPnPDeviceFactory sender, UPnPDevice d, Uri LocationUri)
    {

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
            DiscoveryManager.NetworkManager.NetworkManagerApplicationService nm = new DiscoveryManager.NetworkManager.NetworkManagerApplicationService();
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

        DiscoveryManager.AppDevMgr.ApplicationDeviceManager dacWS = new DiscoveryManager.AppDevMgr.ApplicationDeviceManager();
        foreach (string theDAC in DACs)
        {
            dacWS.Url = BuildSOAPTunnelUri(theDAC);
            System.Net.ServicePointManager.Expect100Continue = false;
            dacWS.SoapVersion = System.Web.Services.Protocols.SoapProtocolVersion.Soap11;
            dacWS.Timeout = 10000;
            try
            {
                string dacRes = dacWS.GetAllLocalHIDS(search);
                string[] resList = dacRes.Split(',');
                if (resList != null)
                    returnStrings.AddRange(resList);
            }
            catch (Exception e)
            {
                System.Console.WriteLine("Exception when calling the WS GetAllLocalHIDS(" + search + " HID:" + theDAC + "):" + e.Message);
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