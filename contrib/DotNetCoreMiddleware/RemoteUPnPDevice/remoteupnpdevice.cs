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
using System.Collections;
using System.Text;
using System.Xml;
using System.Threading;
using OpenSource.UPnP;
using IoT;

using OpenSource.Utilities;

using System.Diagnostics;

using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;

using System.ServiceModel;
using System.Runtime.Serialization;
using System.ServiceModel.Description;





namespace RemoteUPnPDevice
{
    /// <summary>
    /// a Device Manager for remote UPnP devices of any type. Turns any UPnP device into a IoT Device
    /// </summary>
    public class RemoteUPnPDevice:IoTDevice
    {
        UPnPDevice m_remotedevice=null;

        

        public RemoteUPnPDevice(string IoTID, string name, 
            string vendor, string deviceURN)
            : base(IoTID, name, vendor, deviceURN)
        {

            FriendlyName = name;
            Manufacturer = vendor;
            ManufacturerURL = "http://www.cnet.se";
            ModelName = name;
            ModelDescription = "Remote UPnP Device";
            ModelNumber = "1";
            HasPresentation = false;
            DeviceURN = deviceURN;

        }

        public void SetRemoteDevice(UPnPDevice theDevice)
        {
            m_remotedevice = theDevice;

            try
            {

               foreach (UPnPService theService in theDevice.Services)
               {
                AddService(theService);

                if (m_servicesgetfilter == "")
                    m_servicesgetfilter = theService.ServiceURN;
                else
                    m_servicesgetfilter = m_servicesgetfilter + "," + theService.ServiceURN;
                }

                
                IoTService_SetStatus("Remote Device Attached");

                foreach (UPnPDevice childDevice in theDevice.EmbeddedDevices)
                {
                    RemoteUPnPDevice remoteChildDevice= new RemoteUPnPDevice("111", childDevice.FriendlyName, childDevice.Manufacturer, "urn:schemas-upnp-org:IoTdevice:remotedevice:1");

                    remoteChildDevice.SetGateway(m_gateway);
                    remoteChildDevice.SetWSDLTransform(m_wsdltransform);
                    remoteChildDevice.SetRemoteDevice(childDevice);

                    AddDevice(remoteChildDevice);
                }

                IoTService_SetStatus("Remote Device and Child Devices Attached");
                
            }
            catch (Exception e)
            {
                ReportError(e.Message);
                IoTService_SetStatus("Remote Device Not Attached");
                

            }
            
        }

        public UPnPDevice GetRemoteDevice()
        {
            return m_remotedevice;
        }

        override public System.String CreateWS()
        {
            Console.WriteLine("IoTService_CreateWS_RemoteUPnP");

            IoTWCFServiceLibrary.RemoteUPnPDeviceWS myWS = new IoTWCFServiceLibrary.RemoteUPnPDeviceWS(this);

            InitiateWebService(myWS, "IoTWCFServiceLibrary.IIoTRemoteUPnPDeviceWSService", "RemoteUPnPDeviceWS"+this.UniqueDeviceName);


            return m_wsendpoint;
        }

        public UPnPArgument[] CreateUPnPArguments(string arguments)
        {
            UPnPArgument[] returnArgs;
            char[] splitchars = new char[1];

            splitchars[0] = ';';

            string[] myarguments = arguments.Split(splitchars);

            int upperbound = myarguments.GetLength(0);

            if (upperbound > 0)
                returnArgs = new UPnPArgument[upperbound];
            else
                returnArgs = null;
            int returnargspos = 0;

            for (int pos = 0; pos < upperbound; pos++)
            {
                char[] splitcharsinner = new char[1];
                UPnPArgument myUPnPArgument = null;
                string[] myargument;
                splitcharsinner[0] = '=';

                myargument = myarguments[pos].Split(splitcharsinner);

                if (myargument.GetLength(0) == 2)
                {
                    string stringvalue = myargument[1].Trim();
                    object objectvalue = null;

                    //need more conversions
                    if (stringvalue == "true" || stringvalue == "false")
                        objectvalue = System.Convert.ToBoolean(stringvalue);

                    myUPnPArgument = new UPnPArgument(myargument[0].Trim(), objectvalue);

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
 
        override public HTTPMessage IoTInvoke(string Control, string XML, string SOAPACTION, HTTPSession WebSession)
        {
            string actionName = "";
            ArrayList varList = new ArrayList();
            StringReader input = new StringReader(XML);
            XmlTextReader reader2 = new XmlTextReader(input);

            reader2.Read();
            reader2.MoveToContent();
            if (reader2.LocalName == "Envelope")
            {
                reader2.Read();
                reader2.MoveToContent();
                if (reader2.LocalName == "Body")
                {
                    reader2.Read();
                    reader2.MoveToContent();
                    actionName = reader2.LocalName;
                    reader2.Read();
                    reader2.MoveToContent();
                    while (((reader2.LocalName != actionName) && (reader2.LocalName != "Envelope")) && (reader2.LocalName != "Body"))
                    {
                        UPnPArgument argument = new UPnPArgument(reader2.LocalName, reader2.ReadString());
                        varList.Add(argument);
                        if (((reader2.LocalName == "") || !reader2.IsStartElement()) || reader2.IsEmptyElement)
                        {
                            reader2.Read();
                            reader2.MoveToContent();
                        }
                    }
                }
            }
            object retVal = "";
            bool flag = false;
            int index = 0;
            index = 0;
            ArrayList list2 = new ArrayList();

            while (index < this.Services.Length)
            {
                if (this.Services[index].ControlURL == Control)
                {
                    UPnPAction action = null;

                    if (actionName != "QueryStateVariable")
                    {
                        action = this.Services[index].GetAction(actionName);
                        if (action == null)
                        {
                            break;
                        }
                        
                        InvokerInfoStruct struct2 = new InvokerInfoStruct();
                        struct2.WebSession = WebSession;
                        struct2.MethodName = actionName;
                        struct2.SOAPAction = SOAPACTION;
                        foreach (UPnPArgument argument2 in action.Arguments)
                        {
                            if (argument2.IsReturnValue)
                            {
                                struct2.RetArg = (UPnPArgument)argument2.Clone();
                            }
                            if (argument2.Direction == "out")
                            {
                                list2.Add(argument2.Clone());
                            }
                        }
                        struct2.OutArgs = (UPnPArgument[])list2.ToArray(typeof(UPnPArgument));
                        this.InvokerInfo[Thread.CurrentThread.GetHashCode()] = struct2;
                    }

                    varList.AddRange(list2);
                    UPnPArgument[] myUPnPArguments=(UPnPArgument[]) varList.ToArray(typeof(UPnPArgument));


                    foreach (UPnPArgument argument3 in myUPnPArguments)
                    {
                        argument3.DataValue = UPnPService.CreateObjectInstance(action.GetArg(argument3.Name).RelatedStateVar.GetNetType(), (string)argument3.DataValue);
                    }

                    retVal = this.Services[index].InvokeSync(actionName, myUPnPArguments);
                    flag = true;
                    break;
                }
                index++;
            }
            if (!flag)
            {
                throw new UPnPCustomException(0x191, "Invalid Action: " + actionName);
            }
            return this.IoTWSParseInvokeResponse(actionName, SOAPACTION, "http://tempuri.org/", retVal, (UPnPArgument[])varList.ToArray(typeof(UPnPArgument)));
        }

        override public void InitiateWebService(object ws, string implementedContract, string baseaddress, string binding)
        {
            string localendpoint = "";

            try
            {
                if (ParentDevice == null)
                {
                    

                    if (LocalIPEndPoints.GetLength(0) > 0)
                    {
                        bool found = false;
                        foreach (IPEndPoint ip in LocalIPEndPoints)
                        {
                            if (ip.AddressFamily == System.Net.Sockets.AddressFamily.InterNetwork)
                                if (!found)
                                {
                                    found = true;
                                    localendpoint = ip.Address.ToString();
                                    m_wsport = ip.Port.ToString();
                                }
                        }

                    }
                    
                }
                else
                {
                    bool found = false;
                    foreach (IPEndPoint ip in ParentDevice.LocalIPEndPoints)
                    {
                        if (ip.AddressFamily == System.Net.Sockets.AddressFamily.InterNetwork)
                            if (!found)
                            {
                                found = true;
                                localendpoint = ip.Address.ToString();
                            }
                    }

                }


                string fullbaseaddress = m_wsprefix + localendpoint + ":" + m_wsport;

               

                if (ws != null)
                {
                    XmlDocument myWSDL = new XmlDocument();

                    try
                    {

                        m_wsendpoint = fullbaseaddress;
                        AddCustomFieldInDescription("wsendpoint", fullbaseaddress, "IoT");

                        HTTPMessage theMessage = null;
                        
                        theMessage=IoTGet("/?wsdl",null);
                        

                        System.Text.UTF8Encoding myUTF8Encode = new UTF8Encoding();

                        m_wsdl = myUTF8Encode.GetString(theMessage.BodyBuffer);

                        AddCustomFieldInDescription("staticWSwsdl", m_wsdl, "IoT");


                    }
                    catch (Exception e)
                    {
                        ReportError(e.Message);
                    }



                    fullbaseaddress = "http://" + localendpoint + ":"+"8080" + "/IoTdevice/" + UniqueDeviceName;
                    m_status = "web service initiated";
                    AddCustomFieldInDescription("status", "web service initiated", "IoT");

                }

                InitiateIoTWSWebService(fullbaseaddress, binding);
                InitiateEnergyWSWebService(fullbaseaddress + "/energy", binding);

                foreach (IoTDevice theEmbeddedDevice in EmbeddedDevices)
                {
                    theEmbeddedDevice.CreateWS();
                }
            }
            catch (Exception e)
            {
                ReportError(e.Message);
            }

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
    public interface IIoTRemoteUPnPDeviceWSService
    {
        [OperationContract]
        System.String InvokeWS(System.String serviceId, System.String action, System.String parameters);

    }

    /// <summary>
    /// a IoT Device for remote UPnP devices of any type. Any UPnP device can be turned into a IoT Device
    /// </summary>
    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single,IncludeExceptionDetailInFaults = true)]
    public class RemoteUPnPDeviceWS : IIoTRemoteUPnPDeviceWSService
    {


       RemoteUPnPDevice.RemoteUPnPDevice m_remoteupnpdevice;

        public RemoteUPnPDeviceWS(RemoteUPnPDevice.RemoteUPnPDevice theDevice)
        {
            m_remoteupnpdevice = theDevice;
        }

        /// <summary>
        /// Allows invocation of any UPnP service from a WS call
        /// </summary>
        /// /// <param name="serviceid">The UPnP serviceid</param>
        /// <param name="action">The UPnP action name</param>
        /// <param name="parameters">The parameters to the action in format param=value</param>
        /// <returns>A text string with the given SMS message</returns>
        public System.String InvokeWS(System.String serviceId, System.String action, System.String parameters)
        {
            UPnPService theService = m_remoteupnpdevice.GetService(serviceId);
            UPnPArgument[] myArgs=m_remoteupnpdevice.CreateUPnPArguments(parameters);

            object myreturn=theService.InvokeSync(action, myArgs);

            string myReturnString = "";
            
            if (myreturn!=null)
                myReturnString=myreturn.ToString();

            return myReturnString;
        }

        
    }



}
