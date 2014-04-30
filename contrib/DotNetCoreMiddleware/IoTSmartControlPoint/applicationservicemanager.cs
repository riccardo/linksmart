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
    public class ApplicationServiceManagerDevice : IoTDevice
    {
        IoTSmartControlPoint m_smartcontrolpoint;

        public ApplicationServiceManagerDevice(string IoTID, string name, string vendor, string deviceURN)
            : base(IoTID, name, vendor, deviceURN)
        {


        }

        public void SetSmartControlPoint(IoTSmartControlPoint scp)
        {
            m_smartcontrolpoint = scp;
        }

        override public void Start()
        {
            StartDevice();

        }

        override public System.String CreateWS()
        {
            Console.WriteLine("IoTService_CreateWS_ApplicationServiceManager");

            IoTWCFServiceLibrary.ApplicationServiceManager myWS = new IoTWCFServiceLibrary.ApplicationServiceManager(this.m_smartcontrolpoint);

            InitiateWebService(myWS, "IoTWCFServiceLibrary.IApplicationServiceManager", "ApplicationServiceManager");


            return m_wsendpoint;
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

    public interface IApplicationServiceManager
    {
        [OperationContract]
        void ResolveDevices(System.String gateway, System.String discovermanagertype);
        [OperationContract]
        void DiscoverDevices(System.String gateway);
        [OperationContract]
        string ProcessErrorMessage(string deviceid, string theMessage);
        [OperationContract]
        string ProcessErrorMessageString(string deviceid, string theMessage);
        [OperationContract]
        string SetDeviceStatus(string deviceid, string statusmessage);
        [OperationContract]
        string GetDeviceInfo(string deviceid);
        [OperationContract]
        string GetDeviceStatus(string deviceid);
        [OperationContract]
        string GetDeviceXML(string deviceid, string idtype);
        [OperationContract]
        string GetDevices(string type);
        [OperationContract]
        string GetDevicesAsXML(string type);
        [OperationContract]
        string GetDeviceOntologyDescriptionAsXML(string deviceontology_id);
        [OperationContract]
        string GetDeviceOntologyDescription(string deviceontology_id);
        [OperationContract]
        string GetProperty(string deviceid, string property);
        [OperationContract]
        bool HasProperty(string deviceid, string property);
        [OperationContract]
        string SetProperty(string deviceid, string property, string value);
        [OperationContract]
        string Invoke(string invokeMessage);
        [OperationContract]
        string AddDevice(string devicedescription);
        [OperationContract]
        string DeleteDevice(string deviceid);
        [OperationContract]
        bool IsRegistered(string HID);
        [OperationContract]
        string GetWSEndpoint(string deviceid,string idtype);
        [OperationContract]
        string GetWSDL(string deviceid, string idtype);


    }

    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single,IncludeExceptionDetailInFaults = true)]
    public class ApplicationServiceManager : IApplicationServiceManager
    {


        IoT.IoTSmartControlPoint m_IoTsmartcontrolpoint;

        public ApplicationServiceManager(IoT.IoTSmartControlPoint thesmartpoint)
        {
            m_IoTsmartcontrolpoint = thesmartpoint;
        }

        public void ResolveDevices(System.String gateway, System.String discovermanagertype)
        {
            m_IoTsmartcontrolpoint.ResolveDevices(gateway, discovermanagertype);
        }

        public void DiscoverDevices(System.String gateway)
        {
            m_IoTsmartcontrolpoint.DiscoverPhysicalDevices(gateway,"");
        }

        public string ProcessErrorMessage(string deviceid, string theMessage)
        {
            return "not implemented";
        }

        public string ProcessErrorMessageString(string deviceid, string theMessage)
        {
            return "not implemented";
        }
        
        public string SetDeviceStatus(string deviceid, string statusmessage)
        {
            return "not implemented";
        }

        
        public string GetDeviceInfo(string deviceid)
        {
            return "not implemented";
        }

        public string GetDeviceStatus(string deviceid)
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
        public string GetDeviceXML(string deviceid, string idtype)
        {
            UPnPDevice myDevice = GetDeviceInternal(deviceid, idtype);
            
            XmlDocument myDoc = new XmlDocument();
            if (myDevice != null)
            {
                myDoc.Load(myDevice.LocationURL);
            }
            else
                myDoc.LoadXml("<devicenotfound><deviceid>"+deviceid+"</deviceid><idtype>"+idtype+"</idtype></devicenotfound>");

            return myDoc.OuterXml;
        }

        public string GetDevices(string type)
        {
            return "not implemented";
        }
        public string GetDevicesAsXML(string type)
        {
            return "not implemented";
        }

        public string GetDeviceOntologyDescriptionAsXML(string deviceontology_id)
        {
            return "not implemented";
        }

        public string GetDeviceOntologyDescription(string deviceontology_id)
        {
            return "not implemented";
        }

        public string GetProperty(string deviceid, string property)
        {
            return "not implemented";
        }

        public bool HasProperty(string deviceid, string property)
        {
            return false;
        }

        public string SetProperty(string deviceid, string property, string value)
        {
            return "not implemented";
        }

        public string Invoke(string invokeMessage)
        {
            return "not implemented";
        }

        public string AddDevice(string devicedescription)
        {
            return "not implemented";
        }

        public string DeleteDevice(string deviceid)
        {
            return "not implemented";
        }

        public bool IsRegistered(string HID)
        {
            return false;
        }

        public string GetWSEndpoint(string deviceid, string idtype)
        {
            UPnPDevice myDevice=GetDeviceInternal(deviceid, idtype);;
            string returnString = "";

            if (myDevice != null)
            {
                object retval=m_IoTsmartcontrolpoint.InvokeIoTServiceSync(myDevice, "GetWSEndpoint");

                if (retval != null)
                    returnString = retval.ToString();
            }

            return returnString;
        }


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

    }

}