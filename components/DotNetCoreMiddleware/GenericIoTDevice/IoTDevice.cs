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
using IoT;
using DeviceServiceManager;
using System.Collections.Generic;
using System.Collections;
using System.ServiceModel;
using System.Runtime.Serialization;
using System.ServiceModel.Description;
using System.IO;
using System.Text;
using System.Xml;
using System.Xml.Xsl;
using System.Web.Services.Protocols;
using System.Threading;
using System.Net;




namespace IoT
{
	/// <summary>
	/// IoTDevice is the base class for all IoT Device Managers. It provides a service interface to the 
    /// lower level functions of the device. It offers the following main functions:
    /// •	Maps requests to device services
    ///•	Response generation
    ///•	Advertising IoT device description
    ///•	Advertises device services

	/// </summary>
	public class IoTDevice:UPnPDevice
	{
        public string m_IoTid;
        public string m_errormessage;
        public string m_status;

        //the endpoint to the web service for the device
        public string m_wsendpoint;
        public object m_webservice;

        //the endpoint to the generic IoT web service
        public string m_IoTwsendpoint;
        public object m_IoTwebservice;

        //the endpoint to the generic energy web service
        public string m_energywsendpoint;
        public object m_energywebservice;

        //the default binding to use for the web services
        public string m_defaultwsbinding = "BasicHttp";

        //the wsdl file for the web service
        public string m_wsdl;

        //the underlying physical device object, for instance a bluetooth device
        public object m_internaldevice = null;
        public string m_remoteaddress = "";//the underlying physical address of the internal device, for instance a bluetooth address

        //the Device SErvice Manager that provides the device with an interface to the services of the udnerlying physical device
        public DeviceServiceManager.DeviceServiceManager m_deviceservicemanager = null;
        
        //the gateway where the device is executing
        public string m_gateway="";

        //the endpoint to the DAC that has discovered the device and "owns" it
        public string m_DACEndpoint="";
        public string m_securityinfo = "";

        //the XSL-t file that is used when a WSDL file is dynamically generated from the SCPD, used for DynamicWS
        public string m_wsdltransform = "c:\\IoTdevices\\IoT_scpd2wsdl.xsl";

        //members associated with the power consumption
        public int m_currentpowerconsumption = 0;
        public int m_maxpowerconsumption = 0;
        public bool m_onbattery = false;
        public int m_remainingbatterylevel = 0;
        public int m_remainingbatterytime = 0;

        //members associated with the energy consumption
        public string m_activetime = "";//seconds since the device was turned on
        public int m_standbytime = 0;
        public int m_totalenergyconsumption = 0;//total energy consumption since it was started
        public int m_energyconsumption = 0;

        //array containing the member variables for which an event should be generated when the variable changes
        public string[] m_eventvariables=null;

        //urls for the Network and Event manager associated with the IoT Device
        public string m_eventmanagerurl = "http://localhost:8082/axis/services/EventManagerPort";
        public string m_networkmanagerurl = "http://localhost:8082/services/NetworkManagerApplication";

        string m_IoTservicediscoverymode = "";
        string m_powerservicediscoverymode = "";
        string m_deviceservicediscoverymode = "";

        public bool m_removehidsonstop = false;

        public string m_wsprefix = "http://";
        public string m_wsport = ":8080";
        public string m_wssuffix = "";

        //a list with all the IoT ids associated with the services of the device
        List<string> m_IoTids=null;

        public Dictionary<string, string> m_servicehids = null;

        public Timer m_timer;
        public Timer m_minutetimer;

        //Energy policy and profiel for the device
        public EnergyPolicy m_energypolicy=null;
        public EnergyProfile m_energyprofile = null;

        //tells if the energypolicy should be enforced or not
        public bool m_energypolicyenforcement = false;

        //members associated with the energy profile
        public string m_remaininglifetime = null;
        public string m_startcost = null;
        public string m_shutdowncost = null;
        public int m_numberons = 0;


        public DateTime m_currentdate;

        //Each IoT Device can have a FileSystemDevice connected
        public string m_storagename = "";
        public string m_filesystemdeviceurl;
        public FileSystemDevice m_filesystem;

        
        private Object m_IoTdevicelock = new Object();

        public string m_servicesgetfilter = "";

        public IoTDevice(bool nativeupnp): base()
        {
            if (nativeupnp)
            {
                FriendlyName = "Generic IoT UPnP Device";
                
                m_status = "Started";

                m_errormessage = "";
            }
        }

        /// <summary>
        /// The IoTDevice constructor
        /// </summary>
        /// <param name="IoTID">The IoT identifier</param>
        /// <param name="name">A friendly name that can be used to identify the device</param>
        /// <param name="vendor">The vendor of the device, this is optional</param>
        /// <param name="deviceURN">A unique type identifer following the UPnP standard, for example
        ///urn:schemas-upnp-org:device:bluetooth:1</param>
        public IoTDevice(string IoTID, string name, string vendor, string deviceURN)
            : base(1800, 1.0, "\\")
        {

            if (name != "unresolved") //unresolved devices shoudl use other constructor
            {
                FriendlyName = name;
                Manufacturer = vendor;
                ManufacturerURL = "http://www.cnet.se";
                ModelName = name;
                ModelDescription = "Generic IoT Device";
                ModelNumber = "1";
                HasPresentation = false;
                DeviceURN = deviceURN;
               

                m_status = "initiated";


                if (m_energypolicyenforcement) //if true will initiate regular energy checks
                {
                    m_minutetimer = new Timer(new TimerCallback(Minute_Tick));

                    m_minutetimer.Change(60000, 60000);
                }


            }
            else
            {
                FriendlyName = "unresolved device";

                

                m_status = "unresolved";
            }

            m_IoTid = IoTID;
            m_errormessage = "";

            m_IoTids = new List<string>();
            m_servicehids = new Dictionary<string, string>();//currently not used
            m_currentdate = DateTime.Now.Date;

        }

        /// <summary>
        /// The IoT device constructor for unresolved devices, that are treated as embedded UPnP devices
        /// </summary>
        /// <param name="IoTID">The IoT identifier</param>
        /// <param name="deviceURN">A unique type identifer following the UPnP standard, for example
        ///urn:schemas-upnp-org:device:bluetooth:1</param>
        
        public IoTDevice(string IoTID, string deviceURN)
            : base(1.1,"")
        {

           
            FriendlyName = "unresolved device";


            m_status = "unresolved";


            m_IoTid = IoTID;
            m_errormessage = "";

            m_IoTids = new List<string>();
            m_servicehids = new Dictionary<string, string>();
            m_currentdate = DateTime.Now.Date;



        }


        /// <summary>
        /// creates a timer for regular checks of energy policies
        /// </summary>
        /// <param name="enforcement">Values "yes" or "no"</param>
        virtual public void SetEnergyPolicyEnforcement(string enforcement)
        {
            if (enforcement == "yes")//
            {
                m_energypolicyenforcement = true;

               
                m_minutetimer = new Timer(new TimerCallback(Minute_Tick));

                m_minutetimer.Change(60000, 60000);

            }
            else
                m_energypolicyenforcement = false;
        }

        void Timer_Tick(object obj)
        {
            DeviceTick(obj);
        }


        void Minute_Tick(object obj)
        {
            DateTime theTime = DateTime.Now;

            if (theTime.Date != m_currentdate)
            {
                NewDay();
            }

            CheckEnergyPolicyViolation();
        }

        virtual public void DeviceTick(object obj)
        {

        }

        
        

        /// <summary>
        /// Override this to create your own device energy policy behaviour
        /// </summary>
        virtual public void CheckEnergyPolicyViolation()
        {
        }


        /// <summary>
        /// Adds the generic IoT services to the IoT device manager
        /// </summary>
        virtual public void AddIoTService()
        {
            if (m_IoTservicediscoverymode != "no" && m_IoTservicediscoverymode != "silent")
            {
                //create the different service actions and their delegates
                IoT.DvIoTService IoTService = new IoT.DvIoTService();
                IoTService.External_GetErrorMessage = new IoT.DvIoTService.Delegate_GetErrorMessage(IoTService_GetErrorMessage);
                IoTService.External_GetHasError = new IoT.DvIoTService.Delegate_GetHasError(IoTService_GetHasError);
                IoTService.External_GetIoTID = new IoT.DvIoTService.Delegate_GetIoTID(IoTService_GetIoTID);
                IoTService.External_GetStatus = new IoT.DvIoTService.Delegate_GetStatus(IoTService_GetStatus);

                
                IoTService.External_SetIoTID = new IoT.DvIoTService.Delegate_SetIoTID(IoTService_SetIoTID);
                IoTService.External_SetStatus = new IoT.DvIoTService.Delegate_SetStatus(IoTService_SetStatus);
                IoTService.External_GetDiscoveryInfo = new IoT.DvIoTService.Delegate_GetDiscoveryInfo(IoTService_GetDiscoveryInfo);
                IoTService.External_SetDACEndpoint = new IoT.DvIoTService.Delegate_SetDACEndpoint(IoTService_SetDACEndpoint);
                IoTService.External_SetProperty = new IoT.DvIoTService.Delegate_SetProperty(IoTService_SetProperty);
                IoTService.External_GetProperty = new IoT.DvIoTService.Delegate_GetProperty(IoTService_GetProperty);

                IoTService.External_GetSecurityInfo = new IoT.DvIoTService.Delegate_GetSecurityInfo(IoTService_GetSecurityInfo);

                IoTService.External_Stop = new IoT.DvIoTService.Delegate_Stop(IoTService_Stop);



                if (DeviceURN.Contains("discoverymanager"))//Special handling for discover managers, need to remove some methods
                {
                    IoTService.GetUPnPService().RemoveMethod("SetIoTID");
                    IoTService.GetUPnPService().RemoveMethod("GetErrorMessage");
                    IoTService.GetUPnPService().RemoveMethod("GetIoTID");
                    IoTService.GetUPnPService().RemoveMethod("GetStatus");
                    IoTService.GetUPnPService().RemoveMethod("SetStatus");
                    IoTService.GetUPnPService().RemoveMethod("GetHasError");
                    IoTService.GetUPnPService().RemoveMethod("GetDiscoveryInfo");
                    IoTService.GetUPnPService().RemoveMethod("GetSecurityInfo");

                   
                    IoTService.External_CreateWS = new IoT.DvIoTService.Delegate_CreateWS(IoTService_CreateWS);

                    IoTService.External_GetWSEndpoint = new IoT.DvIoTService.Delegate_GetWSEndpoint(IoTService_GetWSEndpoint);
                    IoTService.External_GetWSDL = new IoT.DvIoTService.Delegate_GetWSDL(IoTService_GetWSDL);

                    IoTService.External_ResolveDevice = new IoT.DvIoTService.Delegate_ResolveDevice(IoTService_ResolveDevice);
                    IoTService.External_DiscoverDevices = new IoT.DvIoTService.Delegate_DiscoverDevices(IoTService_DiscoverDevices);

                    IoTService.External_GetDACEndpoint = new IoT.DvIoTService.Delegate_GetDACEndpoint(IoTService_GetDACEndpoint);
                    IoTService.External_GetIoTWSEndpoint = new IoT.DvIoTService.Delegate_GetIoTWSEndpoint(IoTService_GetIoTWSEndpoint);

                }
                else if (m_IoTservicediscoverymode == "IoTid")
                {
                    IoTService.GetUPnPService().RemoveMethod("SetIoTID");
                    IoTService.GetUPnPService().RemoveMethod("GetErrorMessage");
                    
                    IoTService.GetUPnPService().RemoveMethod("GetStatus");
                    IoTService.GetUPnPService().RemoveMethod("SetStatus");
                    IoTService.GetUPnPService().RemoveMethod("SetDACEndpoint");
                    IoTService.GetUPnPService().RemoveMethod("GetDACEndpoint");
                    IoTService.GetUPnPService().RemoveMethod("GetIoTWSEndpoint");

                    IoTService.GetUPnPService().RemoveMethod("GetHasError");
                    IoTService.GetUPnPService().RemoveMethod("ResolveDevice");
                    IoTService.GetUPnPService().RemoveMethod("DiscoverDevices");
                    IoTService.GetUPnPService().RemoveMethod("GetWSEndpoint");
                    IoTService.GetUPnPService().RemoveMethod("GetWSDL");
                    IoTService.GetUPnPService().RemoveMethod("CreateWS");
                    IoTService.GetUPnPService().RemoveMethod("GetDiscoveryInfo");
                    IoTService.GetUPnPService().RemoveMethod("GetSecurityInfo");

                    IoTService.GetUPnPService().RemoveMethod("GetProperty");
                    IoTService.GetUPnPService().RemoveMethod("SetProperty");

                    IoTService.GetUPnPService().RemoveMethod("Stop");
                    IoTService.GetUPnPService().RemoveMethod("StopIoTWS");
                    IoTService.GetUPnPService().RemoveMethod("StopWS");                   

                }
                else
                {
                    IoTService.External_GetWSEndpoint = new IoT.DvIoTService.Delegate_GetWSEndpoint(IoTService_GetWSEndpoint);
                    IoTService.External_GetWSDL = new IoT.DvIoTService.Delegate_GetWSDL(IoTService_GetWSDL);
                    IoTService.External_CreateWS = new IoT.DvIoTService.Delegate_CreateWS(IoTService_CreateWS);
                    IoTService.External_StopWS = new IoT.DvIoTService.Delegate_StopWS(IoTService_StopWS);
                    IoTService.External_StopIoTWS = new IoT.DvIoTService.Delegate_StopIoTWS(IoTService_StopIoTWS);

                    IoTService.External_GetDACEndpoint = new IoT.DvIoTService.Delegate_GetDACEndpoint(IoTService_GetDACEndpoint);
                    IoTService.External_GetIoTWSEndpoint = new IoT.DvIoTService.Delegate_GetIoTWSEndpoint(IoTService_GetIoTWSEndpoint);

                }

                AddService(IoTService);
            }
        }

        /// <summary>
        /// Adds some UPnP services for devices that are unresolved
        /// </summary>
        virtual public void AddUnresolvedIoTService()
        {
            IoT.DvIoTService IoTService = new IoT.DvIoTService();

            IoTService.External_GetDiscoveryInfo = new IoT.DvIoTService.Delegate_GetDiscoveryInfo(IoTService_GetDiscoveryInfo);
            AddService(IoTService);

            IoTService.GetUPnPService().RemoveMethod("SetIoTID");
            IoTService.GetUPnPService().RemoveMethod("GetErrorMessage");
            IoTService.GetUPnPService().RemoveMethod("GetIoTID");
            IoTService.GetUPnPService().RemoveMethod("GetStatus");
            IoTService.GetUPnPService().RemoveMethod("SetStatus");
            IoTService.GetUPnPService().RemoveMethod("SetDACEndpoint");
            IoTService.GetUPnPService().RemoveMethod("GetHasError");
            IoTService.GetUPnPService().RemoveMethod("ResolveDevice");
            IoTService.GetUPnPService().RemoveMethod("DiscoverDevices");
            IoTService.GetUPnPService().RemoveMethod("GetWSEndpoint");
            IoTService.GetUPnPService().RemoveMethod("GetWSDL");
            IoTService.GetUPnPService().RemoveMethod("CreateWS");

        }


        /// <summary>
        /// Adds power consumption UPnP services
        /// </summary>
        virtual public void AddPowerConsumptionService()
        {
            if (m_powerservicediscoverymode != "no" && m_powerservicediscoverymode!="silent")
            {
                IoT.DvPowerService PowerService = new IoT.DvPowerService();
                PowerService.External_GetCurrentConsumption = new IoT.DvPowerService.Delegate_GetCurrentConsumption(PowerService_GetCurrentConsumption);
                PowerService.External_GetMaxPowerConsumption = new IoT.DvPowerService.Delegate_GetMaxPowerConsumption(PowerService_GetMaxPowerConsumption);
                PowerService.External_GetRemainingBatteryTime = new IoT.DvPowerService.Delegate_GetRemainingBatteryTime(PowerService_GetRemainingBatteryTime);
                PowerService.External_GetRemainingBatteryLevel = new IoT.DvPowerService.Delegate_GetRemainingBatteryLevel(PowerService_GetRemainingBatteryLevel);
                PowerService.External_GetStandByTime = new IoT.DvPowerService.Delegate_GetStandByTime(PowerService_GetStandByTime);
                PowerService.External_GetTimeActive = new IoT.DvPowerService.Delegate_GetTimeActive(PowerService_GetTimeActive);
                PowerService.External_GetTimeUntilStandBy = new IoT.DvPowerService.Delegate_GetTimeUntilStandBy(PowerService_GetTimeUntilStandBy);
                PowerService.External_IsBatteryOperated = new IoT.DvPowerService.Delegate_IsBatteryOperated(PowerService_IsBatteryOperated);
                PowerService.External_SetStandByTime = new IoT.DvPowerService.Delegate_SetStandByTime(PowerService_SetStandByTime);
                AddService(PowerService);
            }
        }

        /// <summary>
        /// Adds energy related UPnP services
        /// </summary>
        virtual public void AddEnergyServices()
        {
            IoT.DvEnergyService EnergyService = new IoT.DvEnergyService();
            EnergyService.External_GetCurrentUsage = new IoT.DvEnergyService.Delegate_GetCurrentUsage(EnergyService_GetCurrentUsage);
            EnergyService.External_GetTotalUsage = new IoT.DvEnergyService.Delegate_GetTotalUsage(EnergyService_GetTotalUsage);

            EnergyService.External_GetDeviceEnergyPolicy = new IoT.DvEnergyService.Delegate_GetDeviceEnergyPolicy(EnergyService_GetDeviceEnergyPolicy);
            EnergyService.External_GetEnergyMode = new IoT.DvEnergyService.Delegate_GetEnergyMode(EnergyService_GetEnergyMode);
            EnergyService.External_GetEnergyPolicyStatus = new IoT.DvEnergyService.Delegate_GetEnergyPolicyStatus(EnergyService_GetEnergyPolicyStatus);
            EnergyService.External_GetEnergyProfile = new IoT.DvEnergyService.Delegate_GetEnergyProfile(EnergyService_GetEnergyProfile);
            EnergyService.External_GetEnergyClass = new IoT.DvEnergyService.Delegate_GetEnergyClass(EnergyService_GetEnergyClass);
            EnergyService.External_GetRemainingLifeTime = new IoT.DvEnergyService.Delegate_GetRemainingLifeTime(EnergyService_GetRemainingLifeTime);

            EnergyService.External_GetMaxEffect = new IoT.DvEnergyService.Delegate_GetMaxEffect(EnergyService_GetMaxEffect);
            EnergyService.External_GetMinEffect = new IoT.DvEnergyService.Delegate_GetMinEffect(EnergyService_GetMinEffect);
            EnergyService.External_GetAverageEffect = new IoT.DvEnergyService.Delegate_GetAverageEffect(EnergyService_GetAverageEffect);

            EnergyService.External_SetDeviceEnergyPolicy = new IoT.DvEnergyService.Delegate_SetDeviceEnergyPolicy(EnergyService_SetDeviceEnergyPolicy);
            EnergyService.External_SetDeviceEnergyPolicyFromURL = new IoT.DvEnergyService.Delegate_SetDeviceEnergyPolicyFromURL(EnergyService_SetDeviceEnergyPolicyFromURL);
            EnergyService.External_SetEnergyProfile = new IoT.DvEnergyService.Delegate_SetEnergyProfile(EnergyService_SetEnergyProfile);
            EnergyService.External_SetEnergyProfileFromURL = new IoT.DvEnergyService.Delegate_SetEnergyProfileFromURL(EnergyService_SetEnergyProfileFromURL);
            AddService(EnergyService);
        }

        /// <summary>
        /// Adds location related UPnP services
        /// </summary>
        virtual public void AddLocationServices()
        {
            IoT.DvLocationService LocationService = new IoT.DvLocationService();
            LocationService.External_GetCurrentPosition = new IoT.DvLocationService.Delegate_GetCurrentPosition(LocationService_GetCurrentPosition);
            LocationService.External_GetLocationModel = new IoT.DvLocationService.Delegate_GetLocationModel(LocationService_GetLocationModel);
            LocationService.External_GetLocationProperty = new IoT.DvLocationService.Delegate_GetLocationProperty(LocationService_GetLocationProperty);
            LocationService.External_SetCurrentPosition = new IoT.DvLocationService.Delegate_SetCurrentPosition(LocationService_SetCurrentPosition);
            LocationService.External_SetLocationModel = new IoT.DvLocationService.Delegate_SetLocationModel(LocationService_SetLocationModel);
            LocationService.External_SetLocationProperty = new IoT.DvLocationService.Delegate_SetLocationProperty(LocationService_SetLocationProperty);
            
            AddService(LocationService);
        }

        /// <summary>
        /// Adds memory/storage related UPnP services
        /// </summary>
        virtual public void AddMemoryServices()
        {
            IoT.DvMemoryService MemoryService = new IoT.DvMemoryService();
            MemoryService.External_GetLogModel = new IoT.DvMemoryService.Delegate_GetLogModel(MemoryService_GetLogModel);
            MemoryService.External_LogProperty = new IoT.DvMemoryService.Delegate_LogProperty(MemoryService_LogProperty);
            MemoryService.External_ReloadState = new IoT.DvMemoryService.Delegate_ReloadState(MemoryService_ReloadState);
            MemoryService.External_RetrieveEventLog = new IoT.DvMemoryService.Delegate_RetrieveEventLog(MemoryService_RetrieveEventLog);
            MemoryService.External_RetrieveModelLog = new IoT.DvMemoryService.Delegate_RetrieveModelLog(MemoryService_RetrieveModelLog);
            MemoryService.External_RetrievePropertyLog = new IoT.DvMemoryService.Delegate_RetrievePropertyLog(MemoryService_RetrievePropertyLog);
            MemoryService.External_SaveState = new IoT.DvMemoryService.Delegate_SaveState(MemoryService_SaveState);
            MemoryService.External_SetLogModel = new IoT.DvMemoryService.Delegate_SetLogModel(MemoryService_SetLogModel);
            MemoryService.External_StartModelLog = new IoT.DvMemoryService.Delegate_StartModelLog(MemoryService_StartModelLog);
            MemoryService.External_StopModelLog = new IoT.DvMemoryService.Delegate_StopModelLog(MemoryService_StopModelLog);

            AddService(MemoryService);
        }


        /// <summary>
        /// Adds the different UPnP services to a IoT Device
        /// </summary>
        virtual public void AddServices()
        {
            if (FriendlyName == "unresolved device")
                AddUnresolvedIoTService();
            else
            {
                AddIoTService();

                //AddPowerConsumptionService();//depreciated

                AddEnergyServices();

                AddMemoryServices();

                AddLocationServices();
            }
        }


        /// <summary>
        /// Sets the unique application persistent identifier (PID) for a device
        /// </summary>
        virtual public void SetIoTUDN(string application, string id)
        {
            if (application=="")
                AddCustomFieldInDescription("IoTUDN", id, "IoT");
            else
                AddCustomFieldInDescription("IoTUDN", application+":"+id, "IoT");
        }


        /// <summary>
        /// Sets the gateway of the device
        /// </summary>
        virtual public void SetGateway(string gateway)
        {
            m_gateway = gateway;
            AddCustomFieldInDescription("gateway", m_gateway, "IoT");
        }

        virtual public string GetGateway()
        {
            return m_gateway;
        }

        virtual public void SetWSDLTransform(string transformfile)
        {
            m_wsdltransform = transformfile;
        }

        virtual public string GetWSDLTransform()
        {
            return m_wsdltransform;
        }

        
        virtual public void SetInternalDevice(object thedevice)
        {
            m_internaldevice=thedevice;

        }

        virtual public object GetInternalDevice()
        {
            return m_internaldevice;

        }

        virtual public void SetDeviceServiceManager(DeviceServiceManager.DeviceServiceManager themanager)
        {
            m_deviceservicemanager = themanager;

            m_deviceservicemanager.SetIoTDevice(this);

        }

        virtual public DeviceServiceManager.DeviceServiceManager GetDeviceServiceManager()
        {
            return m_deviceservicemanager;

        }

        /// <summary>
        /// Call this to inform about an error in the device
        /// </summary>
        virtual public void ReportError(string errormessage)
        {
            m_errormessage = errormessage;

            AddCustomFieldInDescription("errormessage", m_errormessage, "IoT");//add the error message to the device XML
            try
            {
                System.Console.WriteLine("Error:" + errormessage);
            }
            catch { }
        }

        /// <summary>
        /// Call this when the device has recovered from the error
        /// </summary>
        virtual public void ResetError()
        {
            m_errormessage = "";

            AddCustomFieldInDescription("errormessage", "", "IoT");//clear the error message from the device XML

            m_deviceservicemanager.ResetError();
            
        }

        virtual public System.String GetDACEndpoint()
        {
            
            return m_DACEndpoint;
        }

        virtual public System.String SetDACEndpoint(System.String theDAC)
        {

            return m_DACEndpoint;
        }

        virtual public void SetDefaultWSBinding(System.String binding)
        {
            if (binding != null)
                m_defaultwsbinding = binding;
        }

        virtual public System.String GetDefaultWSBinding()
        {

            return m_defaultwsbinding;
        }

        
        virtual public void SetSecurityInfo(System.String securityinfo)
        {
            m_securityinfo = securityinfo;
            AddCustomFieldInDescription("securityinfo", securityinfo, "IoT");
        }

        /// <summary>
        /// Call this to listen to events for changes in the member variables specified
        /// </summary>
        /// <param name="eventvariables">a string specifying which variables to generate change events for</param>
        virtual public void SetEventVariables(string eventvariables)
        {
            char[] splitchar=new char[1];

            splitchar[0]=',';
            m_eventvariables = eventvariables.Split(splitchar);
        }

        virtual public void SetEventManagerUrl(string url)
        {
            if (url != null)
                m_eventmanagerurl=url;
        }

        virtual public void SetNetworkManagerUrl(string url)
        {
            m_networkmanagerurl = url;
            AddCustomFieldInDescription("networkmanager", url, "IoT");
        }

        virtual public void SetFileSystemDeviceUrl(string url)
        {
            m_filesystemdeviceurl = url;
            AddCustomFieldInDescription("filesystemdeviceurl", url, "IoT");
        }

        virtual public void SetDiscoveryMode(string mode)
        {
            if (mode != "")
            {
                char[] mySplitchars = new char[1];

                mySplitchars[0] = ',';
                string [] discoverymodes=mode.Split(mySplitchars);

                m_IoTservicediscoverymode="no";
                m_powerservicediscoverymode="no";
                m_deviceservicediscoverymode="no";

                foreach (string themode in discoverymodes)
                {
                    if (themode == "IoTservice")
                        m_IoTservicediscoverymode = "yes";
                    else if (themode == "IoTid")
                        m_IoTservicediscoverymode = "IoTid";
                    else if (themode == "powerservice")
                        m_powerservicediscoverymode = "yes";
                    else if (themode == "deviceservice")
                        m_deviceservicediscoverymode = "yes";
                    else if (themode =="silent")
                    {
                        m_IoTservicediscoverymode="silent";
                        m_powerservicediscoverymode = "silent";
                        m_deviceservicediscoverymode="silent";
                    }
                }

                AddCustomFieldInDescription("discoverymode", mode, "IoT");
            }
        }

        /// <summary>
        /// Starts the UPnPDevice which will cause the Hdyra DEvice to get an IP address
        /// </summary>
        virtual public void Start()
		{
            if (m_IoTservicediscoverymode != "silent")
            {
                StartDevice();
                m_status = "started";
                AddCustomFieldInDescription("status", m_status, "IoT");
            }
		}

        /// <summary>
        /// Stops the UPnPDevice which will cause the IoT DEvice to disappear from the network
        /// </summary>
        virtual public void Stop()
		{
			StopDevice();
            m_status = "stopped";
            AddCustomFieldInDescription("status", m_status, "IoT");

            if (m_removehidsonstop)
                RemoveIoTID("");
           
		}

        virtual public void SetRemoveHids(bool removehidsonstop)
        {
            m_removehidsonstop = removehidsonstop;
        }

        virtual public void SetWSPrefix(string wsprefix)
        {
            m_wsprefix = wsprefix;
        }

        virtual public void SetWSSuffix(string wssuffix)
        {
            m_wssuffix = wssuffix;
        }

        virtual public void SetWSPort(string wsport)
        {
            m_wsport = wsport;
        }

        virtual public void RemoveIoTID(string IoTid)
        {
            try { 
                    NetworkManager.NetworkManagerApplicationService myNetworkManager = new NetworkManager.NetworkManagerApplicationService();
                    
                    if (m_networkmanagerurl != "")
                        myNetworkManager.Url = m_networkmanagerurl;

                    if (IoTid=="")
                        {
                            if (m_IoTids!=null)
                            {  
                                foreach (string hid in m_IoTids)
                                    {
                                       
                                        myNetworkManager.removeHID(hid);

                                    }
                            }
                        }
                    else
                            myNetworkManager.removeHID(IoTid);
                 }
               catch (Exception e)
                    {
                        ReportError("Removing IoTIds:"+e.Message);
                    }
        }


        /// <summary>
        /// Each device has a device XML document associated with it. The AddCustomFieldInDescription allows to add and change elements in the device XML.
        /// This allows a user of the device to add his own variables to the device.  
        /// </summary>
        /// <param name="FieldName">a valid XML element name</param>
        /// <param name="FieldValue">a valid XML string</param>
        /// <param name="Namespace">a namespace to distinguish different types of fields. The name space "IoT" is reserved for IoT internal use</param>
        override public void AddCustomFieldInDescription(string FieldName, string FieldValue, string Namespace)
        {
            lock (m_IoTdevicelock)
            {
                try
                {
                    base.AddCustomFieldInDescription(FieldName, FieldValue, Namespace);

                    if (GetCustomFieldFromDescription("IoTidStaticWS", "IoT") != "")
                    {
                        if (m_eventvariables != null)
                        {
                            foreach (string myvar in m_eventvariables)//check if we should generate an event
                            {
                                if (FieldName == myvar)
                                {
                                    GenerateIoTVariableChanged(FieldName, FieldValue);//generate a change event

                                }
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                }
            }
        }

        /// <summary>
        /// Each device has a device XML document associated with it. The AddCustomFieldInDescription allows to add and change elements in the device XML.
        /// This allows a user of the device to add his own variables to the device. The GetCustomFieldFromDescription method allwos to 
        /// retrieve values from the Device XML
        /// </summary>
        /// <param name="FieldName">a valid XML element name</param>
        /// <param name="Namespace">specifies which namespace to use when retrieving the specified field</param>
        override public string GetCustomFieldFromDescription(string FieldName, string Namespace)
        {
            string result = "";

            lock (m_IoTdevicelock)
            {
                try
                {
                    result = base.GetCustomFieldFromDescription(FieldName, Namespace);
                }
                catch (Exception e)
                {
                }
            }

            return result;
        }

        virtual public void DeviceMainLoop()
        {

        }

        virtual public void DeviceOn()
        {
            ReduceLifeTime("on");
            m_numberons++;
            AddCustomFieldInDescription("numberons", m_numberons.ToString(), "IoT");
        }

        virtual public void DeviceOff()
        {
            ReduceLifeTime("off");
        }

        virtual public void ReduceLifeTime(string action)
        {
            if (m_remaininglifetime != null && m_remaininglifetime!="")
                {
                    int lifetime = System.Convert.ToInt32(m_remaininglifetime);
                    int startcost = System.Convert.ToInt32(m_startcost);
                    int shutdowncost = System.Convert.ToInt32(m_shutdowncost);

                    if (action == "on")
                    {
                        lifetime = lifetime - startcost;
                    }
                    else
                    {
                        lifetime = lifetime - shutdowncost;
                        int runningtime=CalculateSecondsElapsed(m_activetime);

                        lifetime = lifetime - runningtime;
                    }

                    m_remaininglifetime = lifetime.ToString();
                }
        }

        /// <summary>
        /// Generates a change event
        /// </summary>
        /// <param name="IoTvar">the variable that changed</param>
        /// <param name="IoTvalue">the new value</param>
        virtual public void GenerateIoTVariableChanged(string IoTvar, string IoTvalue)
        {
            try
            {
                //Added to avoid 100 continue
                System.Net.ServicePointManager.Expect100Continue = false;
                EventManager.EventManagerService myEventMgr = new EventManager.EventManagerService();//connect with Event Manager

                myEventMgr.SoapVersion = SoapProtocolVersion.Soap11;
                

                myEventMgr.Url = m_eventmanagerurl;

                EventManager.part[] myParts = new EventManager.part[4];//create the event parts

                myParts[0] = new EventManager.part();

                myParts[0].key = "IoTVariable";
                myParts[0].value = IoTvar;

                myParts[1] = new EventManager.part();

                myParts[1].key = "IoTVariableValue";
                myParts[1].value = IoTvalue;

                myParts[2] = new EventManager.part();

                myParts[2].key = "IoTidStaticWS";
                myParts[2].value = GetCustomFieldFromDescription("IoTidStaticWS", "IoT");

                myParts[3] = new EventManager.part();
                myParts[3].key = "DeviceURN";
                myParts[3].value = DeviceURN;

                myEventMgr.publish("deviceStateChanged", myParts);
            }
            catch (Exception e)
            {
                ReportError("eventmanager error:"+e.Message);
            }
        }

        /// <summary>
        /// Publishes a specified event
        /// </summary>
        /// <param name="topic">the event topic</param>
        /// <param name="keys">the event keys</param>
        /// <param name="values">the key values, in same order as keys</param>
        virtual public bool PublishEvent(string topic, string[] keys,string[] values)
        {
            bool success = false;

            try
            {
                EventManager.EventManagerService myEventMgr = new EventManager.EventManagerService();

                myEventMgr.Url = m_eventmanagerurl;

                EventManager.part[] myParts = new EventManager.part[keys.Length];

                for (int pos = 0; pos < keys.Length; pos++)
                {
                    myParts[pos] = new EventManager.part(); 
                    myParts[pos].key = keys[pos];
                    myParts[pos].value = values[pos];
                }

                myEventMgr.publish(topic, myParts);
                success = true;
            }
            catch (Exception e)
            {
                ReportError(e.Message);
            }

            return success;
        }

        /// <summary>
        /// Overrides base class Invoke in order to transform a WS call into a UPnP call
        /// </summary>
        override public HTTPMessage Invoke(string Control, string XML, string SOAPACTION, HTTPSession WebSession)
        {
            if (Control == "" || Control.Contains("?wsdl"))//override when a wsdl is requested
            {
                string myControlString = "_urn:upnp-org:serviceId:1_control";
                char[] myChars=new char[1];

                myChars[0]='#';
                string[] mySplit=SOAPACTION.Split(myChars);

                string myServiceURN=mySplit[0];

                myServiceURN = myServiceURN.Substring(1);


                for (int pos = 0; pos < this.Services.Length; pos++)
                {
                    if (this.Services[pos].ServiceURN == myServiceURN)
                        myControlString = this.Services[pos].ControlURL;
                    
                }

                return IoTInvoke(myControlString, XML, SOAPACTION, WebSession);
            }
            else
            {
                return base.Invoke(Control, XML, SOAPACTION, WebSession);
            }
        }

        /// <summary>
        /// Translates an incoming WS call to a UPnP call
        /// </summary>
        virtual public HTTPMessage IoTInvoke(string Control, string XML, string SOAPACTION, HTTPSession WebSession)
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
            
            while (index < this.Services.Length)
            {
                if (this.Services[index].ControlURL == Control)
                {
                    if (actionName != "QueryStateVariable")
                    {
                        UPnPAction action = this.Services[index].GetAction(actionName);
                        if (action == null)
                        {
                            break;
                        }
                        ArrayList list2 = new ArrayList();
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
                   

                    retVal = this.Services[index].InvokeLocal(actionName, ref varList);
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


        /// <summary>
        /// Parses a UPnP response into a WS response
        /// </summary>
        virtual public HTTPMessage IoTWSParseInvokeResponse(string MethodTag, string SOAPACTION, string urn, object RetVal, UPnPArgument[] OutArgs)
        {
            HTTPMessage message = new HTTPMessage();
            
            MemoryStream w = new MemoryStream(0x1000);
            XmlTextWriter writer = new XmlTextWriter(w, Encoding.UTF8);
            writer.Formatting = Formatting.Indented;
            writer.Indentation = 3;
            writer.WriteStartDocument();
            string ns = "http://schemas.xmlsoap.org/soap/envelope/";
            writer.WriteStartElement("s", "Envelope", ns);
            writer.WriteAttributeString("s", "encodingStyle", ns, "http://schemas.xmlsoap.org/soap/encoding/");
            writer.WriteStartElement("s", "Body", ns);
            if (!SOAPACTION.EndsWith("#QueryStateVariable\""))
            {
                writer.WriteStartElement("",MethodTag + "Response","http://tempuri.org/");
                
                //writer.WriteStartElement("u", MethodTag + "Response", urn);
                if (RetVal != null)
                {
                    
                   writer.WriteElementString("",((UPnPArgument)RetVal).Name + "Result", "", UPnPService.SerializeObjectInstance(((UPnPArgument)RetVal).DataValue));

                }
                foreach (UPnPArgument argument in OutArgs)
                {
                    //writer.WriteElementString(argument.Name, UPnPService.SerializeObjectInstance(argument.DataValue));
                    if (argument.Direction=="out")
                        writer.WriteElementString("",argument.Name + "Result", "", UPnPService.SerializeObjectInstance(argument.DataValue));

                }
            }
            else
            {
                string text2 = "urn:schemas-upnp-org:control-1-0";
                writer.WriteStartElement("u", MethodTag + "Response", text2);
                writer.WriteElementString("return", UPnPStringFormatter.EscapeString(UPnPService.SerializeObjectInstance(RetVal)));
            }
            writer.WriteEndElement();
            writer.WriteEndElement();
            writer.WriteEndElement();
            writer.WriteEndDocument();
            writer.Flush();
            byte[] buffer = new byte[w.Length - 3];
            w.Seek((long)3, SeekOrigin.Begin);
            w.Read(buffer, 0, buffer.Length);
            writer.Close();
            message.StatusCode = 200;
            message.StatusData = "OK";
            message.AddTag("Content-Type", "text/xml");
            message.AddTag("EXT", "");
            message.AddTag("Server", "Windows NT/5.0, UPnP/1.0, Intel CLR SDK/1.0");
            message.BodyBuffer = buffer;
            return message;
        }

        /// <summary>
        /// Overrides base class Get to return a WSDL which is dynamically generated from the SCPD document
        /// </summary>
        override public HTTPMessage Get(string GetWhat, IPEndPoint local)
        {
            if (GetWhat.Contains("/?wsdl"))
            {
                return IoTGet(GetWhat, local);
            }
            else
                return base.Get(GetWhat, local);
        }

        override public HTTPMessage Post(String MethodData, String XML, String SOAPACTION, HTTPSession WebSession)
        {
           

            return IoTPost(MethodData, XML, SOAPACTION, WebSession);
        }

        /// <summary>
        /// Applies an XSL-T transform to SCPD document to generate a WSDL file
        /// </summary>
        virtual public HTTPMessage IoTGet(string GetWhat, IPEndPoint local)
        {
            
            HTTPMessage message = new HTTPMessage();
            
            GetWhat = GetWhat.Substring(1);
            
            bool flag = false;
            

            if (GetWhat.Contains("?wsdl"))
            {
                message.StatusCode = 200;
                message.StatusData = "OK";
                message.AddTag("Content-Type", "text/xml");
                    try
                    {
                        
                        flag = true;
                        

                        XslTransform xsltDoc = new XslTransform();
                        xsltDoc.Load(m_wsdltransform);

                        XmlUrlResolver myResolver = new XmlUrlResolver();
                        XsltArgumentList xslArg = new XsltArgumentList();
                        
                        string mySPCDstring = "<services>";

                        char[] mySplitChar=new char[1];

                        mySplitChar[0]='=';

                        string[] splitstring = GetWhat.Split(mySplitChar);
                        bool filtersexist = false;

                        if (splitstring.Length == 2)
                            filtersexist = true;
                        else if (m_servicesgetfilter != "")
                        {
                            string split=GetWhat + "=" + m_servicesgetfilter;

                            splitstring = split.Split(mySplitChar);
                            filtersexist = true;
                        }

                        for (int pos = 0; pos < this.Services.Length; pos++)
                        {
                            
                            if (!filtersexist||IncludeServiceSCPD(splitstring[1],pos)) {
                                XmlDocument serviceXML = new XmlDocument();
                                byte[] myServiceBytes = this.Services[pos].GetSCPDXml();

                                System.Text.UTF8Encoding myUTF8EncodeService = new UTF8Encoding();

                                string mystring = myUTF8EncodeService.GetString(myServiceBytes);

                                serviceXML.LoadXml(mystring);

                                XmlNode mySPCD = serviceXML.SelectSingleNode(".//*[name()='scpd']");



                                if (mySPCD != null)
                                    {
                                        XmlAttribute myAttribute = serviceXML.CreateAttribute("IoT", "serviceid", "IoT");
                                        myAttribute.Value = this.Services[pos].ServiceURN;

                                        mySPCD.Attributes.SetNamedItem(myAttribute);


                                        mySPCDstring = mySPCDstring + mySPCD.OuterXml;
                                    }
                            }
                        }
                        mySPCDstring = mySPCDstring + "</services>";

                        XmlDocument FullSPCDDocument = new XmlDocument();



                        FullSPCDDocument.LoadXml(mySPCDstring);

                        XmlReader tmpXml2 = xsltDoc.Transform(FullSPCDDocument, xslArg, myResolver);


                        XmlDocument outXml = new XmlDocument();
                        outXml.Load(tmpXml2);

                        System.Text.UTF8Encoding myUTF8Encode = new UTF8Encoding();

                        message.BodyBuffer = myUTF8Encode.GetBytes(outXml.OuterXml);
                    }
                    catch (Exception e)
                    {
                        System.Text.UTF8Encoding myUTF8Encode = new UTF8Encoding();

                        message.BodyBuffer = myUTF8Encode.GetBytes(e.Message);

                    }
                
                }

            
            return message;
        }

        virtual public HTTPMessage IoTPost(String MethodData, String XML, String SOAPACTION, HTTPSession WebSession)
        {
            return base.Post(MethodData, XML, SOAPACTION, WebSession);
        }

        virtual public bool IncludeServiceSCPD(string filter, int servicepos)
        {
            bool flag = false;
            char[] mySplitChar=new char[1];

            mySplitChar[0]=',';

            string[] myfilters = filter.Split(mySplitChar);

            string serviceURN = this.Services[servicepos].ServiceURN;

            for (int pos = 0; pos<myfilters.Length; pos++)
            {
                if (serviceURN.Contains(myfilters[pos]))
                {
                    flag=true;
                    break;
                }
            }

            return flag;
        }

        virtual public object ExecuteInternalMethod(string serviceid, string method, string arglist)
        {
            UPnPService theService;
            char[] mysplit = new char[1];
            ArrayList myArgumentList;

            mysplit[0]=',';

            string[] myargs = arglist.Split(mysplit);

            int upperbound = myargs.Length;

            mysplit[0]='=';

            myArgumentList = new ArrayList();

            for (int pos = 0; pos < upperbound; pos++)
            {
                UPnPArgument myUPnPArg;
                

                string name, value;

                string[] mystringarg=myargs[pos].Split(mysplit);

                if (mystringarg.Length == 2)
                    {
                        name = mystringarg[0];
                        value = mystringarg[1];
                        

                        myUPnPArg = new UPnPArgument(name, value);

                   

                        myArgumentList.Add(myUPnPArg);
                    }
                
            }
            
            theService = GetService(serviceid);

            return theService.IoTInvokeLocal(method, ref myArgumentList);

        }

        virtual public void ResolveDevice(string UDN, string resolvemessage)
        {
            Console.WriteLine("IoTService_ResolveDevice_IoTdevice(" + ")");

        }

        virtual public void DiscoverDevices()
        {
            Console.WriteLine("IoTService_DiscoverDevices_IoTdevice(" + ")");

        }

        virtual public System.String CreateWS()
        {
            Console.WriteLine("IoTService_CreateWS_IoTdevice");

            foreach (IoTDevice theEmbeddedDevice in EmbeddedDevices)
            {
                theEmbeddedDevice.CreateWS();
            }

            return "";
        }
        virtual public void InitiateWebService(object ws, string implementedContract, string baseaddress)
        {
            if (m_defaultwsbinding!="")
                InitiateWebService(ws, implementedContract, baseaddress, m_defaultwsbinding);
            else
                InitiateWebService(ws, implementedContract, baseaddress, "BasicHttp");
        }

        virtual public void InitiateWebService(object ws, string implementedContract, string baseaddress,string binding)
        {
            string localendpoint = "";

            try
            {
                if (ParentDevice == null)
                {
                    //Peeter Kool, need to find a IPv4 address

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
                                }
                        }

                    }
                    /*else
                        localendpoint = ParentDevice.LocalIPEndPoints[0].Address.ToString();*/
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
                  

                string fullbaseaddress = m_wsprefix + localendpoint + m_wsport + "/" + baseaddress;

                if (m_wssuffix != "")
                    fullbaseaddress = fullbaseaddress + "/" + m_wssuffix;

                if (ws != null)
                {
                    XmlDocument myWSDL = new XmlDocument();

                    try
                    {
                        IoTDeviceWSHost.StartService(ws, implementedContract, fullbaseaddress, binding);

                        m_webservice = ws;
                        m_wsendpoint = fullbaseaddress;

                        myWSDL.Load(fullbaseaddress + "?wsdl");

                        m_wsdl = myWSDL.OuterXml;


                        AddCustomFieldInDescription("wsendpoint", m_wsendpoint, "IoT");
                        AddCustomFieldInDescription("staticWSwsdl", m_wsdl, "IoT");
                        

                    }
                    catch (Exception e)
                    {
                        ReportError(e.Message);
                    }



                    fullbaseaddress = "http://" + localendpoint + m_wsport+"/IoTdevice/" + UniqueDeviceName;
                    m_status = "web service initiated";
                    AddCustomFieldInDescription("status", "web service initiated", "IoT");
                    
                }

                InitiateIoTWSWebService(fullbaseaddress,binding);
                InitiateEnergyWSWebService(fullbaseaddress+"/energy",binding);

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

        virtual public void InitiateIoTWSWebService(string baseaddress,string binding)
        {
            IoTWCFServiceLibrary.IoTDeviceWS myIoTWS = new IoTWCFServiceLibrary.IoTDeviceWS(this);

            IoTDeviceWSHost.StartService(myIoTWS, "IoTWCFServiceLibrary.IIoTDeviceWSService", baseaddress, binding);
            m_IoTwebservice = myIoTWS;
            m_IoTwsendpoint = baseaddress;

            
        }

        virtual public void InitiateEnergyWSWebService(string baseaddress, string binding)
        {
            IoTWCFServiceLibrary.IoTDeviceEnergyWS myEnergyWS = new IoTWCFServiceLibrary.IoTDeviceEnergyWS(this);

            IoTDeviceWSHost.StartService(myEnergyWS, "IoTWCFServiceLibrary.IIoTDeviceEnergyWSService", baseaddress, binding);
            m_energywebservice = myEnergyWS;
            m_energywsendpoint = baseaddress;
            AddCustomFieldInDescription("energyWSEndpoint", m_energywsendpoint, "IoT");

         }

        

        virtual public void SetPowerData(int maxpower, int currentpower, int standbytime, bool onbattery)
        {
            m_maxpowerconsumption = maxpower;
            m_currentpowerconsumption = currentpower;
            m_standbytime = standbytime;
            m_onbattery = onbattery;
        }

        virtual public int CalculateCurrentEnergyConsumption()
        {
            return CalculateEnergyConsumption(m_currentpowerconsumption, m_activetime);
        }

        virtual public int CalculateEnergyConsumption(int powerconsumption, string activetime)
        {
            int seconds = CalculateSecondsElapsed(activetime);

            return powerconsumption * seconds;
        }

        virtual public int CalculateSecondsElapsed(string activetime)
        {
            if (m_activetime != "")
            {
                DateTime start = DateTime.Parse(m_activetime);

                DateTime end = DateTime.Now;

                TimeSpan dt = end - start;


                return System.Convert.ToInt32(dt.TotalSeconds);
            }
            else
                return 0;
        }

        virtual public bool ValidateAction(string service, string action)
        {
            if (m_energypolicy == null)
                return true;
            else
                return m_energypolicy.ValidateAction(service, action);
        }

        virtual public void EnergyPolicyLoaded()
        {
            string standbytime = m_energypolicy.GetEnergyPolicyValue("//*[name()='standby' and @type='time']/*[name()='starttime']");

            m_standbytime = System.Convert.ToInt32(standbytime);

            AddCustomFieldInDescription("standbytime", standbytime, "IoT");
        }

        virtual public void NewDay()
        {
            m_numberons = 0;
            m_currentdate = DateTime.Now.Date;
        }

        virtual public XmlDocument GetIoTDeviceXml()
        
        {
            XmlDocument myDoc = new XmlDocument();

           

            try
            {
                byte[] myDeviceBytes = GetRootDeviceXML(null);

                System.Text.UTF8Encoding myUTF8EncodeService = new UTF8Encoding();

                string mystring = myUTF8EncodeService.GetString(myDeviceBytes);

                myDoc.LoadXml(mystring);

            }

            catch (Exception e)
            {
                ReportError("IoTWS:" + e.Message);
                myDoc=null;
            }

            return myDoc;
        }

        //*********************Below are the UPnP Services the device offers to the outside**************************

        //*********************Start IoT Service************************************

        /// <summary>
        /// Returns the physical discovery information that is associated with the device
        /// </summary>
        /// <returns>An XML string</returns>
        virtual public System.String IoTService_GetDiscoveryInfo()
        {
            Console.WriteLine("IoTService_GetDiscoveryInfo(" + ")");

            //object retval=ExecuteInternalMethod("urn:upnp-org:serviceId:1", "GetIoTID", "IoTID=23");

            return GetCustomFieldFromDescription("discoveryinfo", "IoT"); ;
        }

        virtual public void IoTService_ResolveDevice(string UDN, string resolvemessage)
        {
            Console.WriteLine("IoTService_ResolveDevice_IoTdevice(" + ")");

            //object retval=ExecuteInternalMethod("urn:upnp-org:serviceId:1", "GetIoTID", "IoTID=23");

            ResolveDevice(UDN, resolvemessage);
        }

        virtual public void IoTService_DiscoverDevices()
        {
            Console.WriteLine("IoTService_DiscoverDevices()");

            DiscoverDevices();

        }

        /// <summary>
        ///Creates a web service for the device
        /// </summary>
        /// <returns>The endpoint for the web service</returns>
        virtual public System.String IoTService_CreateWS()
        {
            Console.WriteLine("IoTService_CreateWS()");

            return CreateWS();
        }

        /// <summary>
        ///Returns an errormessage for the device
        /// </summary>
        /// /// <returns>Latest errormessage</returns>
        virtual public System.String IoTService_GetErrorMessage()
		{
			Console.WriteLine("IoTService_GetErrorMessage(" + ")");

            //object retval=ExecuteInternalMethod("urn:upnp-org:serviceId:1", "GetIoTID", "IoTID=23");
			
			return m_errormessage;
		}

        /// <summary>
        ///Tells if the device has an error
        /// </summary>
        /// /// <returns>True if error, false otherwise</returns>
		virtual public System.Boolean IoTService_GetHasError()
		{
			Console.WriteLine("IoTService_GetHasError(" + ")");
			
			return m_errormessage!="";
		}

        /// <summary>
        ///Returns the IoTID for the device
        /// </summary>
        /// <returns>The HID of the device</returns>
		virtual public System.String IoTService_GetIoTID()
		{
			Console.WriteLine("IoTService_GetIoTID(" + ")");
			
			return m_IoTid;
		}

        /// <summary>
        ///Returns the status for the device
        /// </summary>
        /// <returns>The status of the device</returns>
		virtual public System.String IoTService_GetStatus()
		{
			Console.WriteLine("IoTService_GetStatus(" + ")");
			
			return m_status;
		}

        /// <summary>
        ///Sets the IoTID for the device
        /// </summary>
        /// <param name="IoTID">The valid IoT ID</param>
		virtual public void IoTService_SetIoTID(System.String IoTID)
		{
			Console.WriteLine("IoTService_SetIoTID(" + IoTID.ToString() + ")");

            m_IoTid = IoTID;
            AddCustomFieldInDescription("IoTid", m_IoTid, "IoT");
		}
        /// <summary>
        ///Sets a property of the device. A developer can choose any properties he like to use and set.
        /// </summary>
        /// <param name="property">A valid property name (valid XML element name)</param>
        /// <param name="value">The value of the property</param>
        virtual public void IoTService_SetProperty(System.String Property, System.String Value)
        {
            Console.WriteLine("IoTService_SetProperty(" + Property.ToString() + Value.ToString() + ")");

            if (Property == "errormessage")
            {
                if (m_errormessage != "")
                    Value = m_errormessage + ";" + Value;
            }
            AddCustomFieldInDescription(Property, Value, "IoT");

            if (Property == "status")
                m_status = Value;
            else if (Property == "IoTid")
                m_IoTid = Value;
            else if (Property == "DACEndpoint")
                m_DACEndpoint = Value;
            else if (Property == "WSEndpoint")
                m_wsendpoint = Value;
            else if (Property == "ErrorMessage")
                m_errormessage = Value;
            else if (Property == "gateway")
                m_gateway = Value;


        }

        /// <summary>
        ///Returns a property of the device. A developer can choose any properties he like to use and set.
        /// </summary>
        /// <param name="property">A valid property name (valid XML element name)</param>
        /// <returns>The value of the property</returns>
        virtual public System.String IoTService_GetProperty(System.String Property)
		{
			Console.WriteLine("IoTService_GetProperty(" + Property.ToString() + ")");
			
			return GetCustomFieldFromDescription(Property,"IoT");
		}
        /// <summary>
        ///Sets the status for the device
        /// </summary>
        /// <param name="Status">A status value choosen by the developer</param>
		virtual public void IoTService_SetStatus(System.String Status)
		{
			Console.WriteLine("IoTService_SetStatus(" + Status.ToString() + ")");

            m_status = Status;
            AddCustomFieldInDescription("status", m_status, "IoT");
		}

        /// <summary>
        ///Returns the endpoint of the DAC that has discovered the device
        /// </summary>
        /// <returns>The endpoint of the current DAC</returns>
        virtual public System.String IoTService_GetDACEndpoint()
        {
            Console.WriteLine("IoTService_GetDACEndpoint(" + ")");

            return m_DACEndpoint;
        }

        /// <summary>
        ///Sets the endpoint of the DAC that has discovered the device
        /// </summary>
        /// <param name="endpoint">A valid endpoint address</param>
        virtual public void IoTService_SetDACEndpoint(System.String endpoint)
        {
            Console.WriteLine("IoTService_SetDACEndpoint(" + endpoint + ")");

            m_DACEndpoint = endpoint;
            AddCustomFieldInDescription("DACEndpoint", m_DACEndpoint, "IoT");
        }

        /// <summary>
        ///Returns the endpoint where the device's web service is located
        /// </summary>
        /// <returns>The endpoint of the device WS</returns>
        virtual public System.String IoTService_GetWSEndpoint()
        {
            Console.WriteLine("IoTService_GetWSEndpoint()");

            if (m_wsendpoint != "")
                return m_wsendpoint;
            else
                return "no endpoint available";
        }

        /// <summary>
        ///Returns the endpoint where the device's generic IoT web service is located
        /// </summary>
        /// <returns>The endpoint of the device generic IoT WS</returns>
        virtual public System.String IoTService_GetIoTWSEndpoint()
        {
            Console.WriteLine("IoTService_GetIoTWSEndpoint(" + ")");

            return m_IoTwsendpoint;
        }
        /// <summary>
        ///Returns the WSDL describing the web service for the device
        /// </summary>
        /// <returns>The WSDL that describes the device WS</returns>
        virtual public System.String IoTService_GetWSDL()
        {
            Console.WriteLine("IoTService_GetWSDL()");

            if (m_wsdl != "")
                return m_wsdl;
            else
                return "no endpoint available";
        }
        /// <summary>
        ///Stops the device, which will cause it to be removed from its current DAC. The device can later be re-started.
        /// </summary>
        virtual public void IoTService_Stop()
        {
            Console.WriteLine("IoTService_Stop()");

            if (m_webservice != null)
                IoTDeviceWSHost.StopService();

            Stop();
        }

        /// <summary>
        ///Stops the web service associated with the device.
        /// </summary>
        virtual public void IoTService_StopWS()
        {
            Console.WriteLine("IoTService_StopWS()");

            if (m_webservice != null)
                IoTDeviceWSHost.StopService();


        }
        /// <summary>
        ///Stops the generic IoT web service of the device.
        /// </summary>
        virtual public void IoTService_StopIoTWS()
        {
            Console.WriteLine("IoTService_StopIoTWS()");

            if (m_webservice != null)
                IoTDeviceWSHost.StopService();

        }

        virtual public System.String IoTService_GetSecurityInfo()
        {
            Console.WriteLine("IoTService_GetSecurityInfo()");

            return GetCustomFieldFromDescription("securityinfo", "IoT"); ;
        }

        //*********************End IoT Service************************************



        //*********************Start Energy Service************************************

        /// <summary>
        /// Returns the current effect for the device in its current operating mode
        /// </summary>
        /// <returns>An string representing a number</returns>
        virtual public System.String EnergyService_GetCurrentUsage()
        {
            Console.WriteLine("EnergyService_GetCurrentEffect()");
            if (m_energyprofile != null)
            {
                string energymode = GetCustomFieldFromDescription("energymode", "IoT");
                if (energymode == "")
                    energymode = "running";
                return m_energyprofile.GetAverageEffect(energymode);
            }
            else
                return "<noenergyprofile/>";
        }

        /// <summary>
        /// Returns the total energy consumption for the device
        /// </summary>
        /// <param name="since">values="today","lastturnon". An empty string will return the consumption since the device was first registered</param>
        /// <returns>An string representing a number</returns>
        virtual public System.String EnergyService_GetTotalUsage(System.String since)
		{
			Console.WriteLine("EnergyService_GetTotalUsage(" + since.ToString() + ")");
			
			return "Sample String";
		}

        /// <summary>
        /// Returns the current energy policy assigned to the device
        /// </summary>
        /// <returns>An XML string</returns>
        virtual public System.String EnergyService_GetDeviceEnergyPolicy()
        {
            
            Console.WriteLine("EnergyService_GetDeviceEnergyPolicy(" + ")");
            return m_energypolicy.GetEnergyPolicy();
        }

        /// <summary>
        /// Returns the current energy class for the device including the classification system
        /// </summary>
        /// <returns>A string</returns>
        virtual public System.String EnergyService_GetEnergyClass()
        {
            Console.WriteLine("EnergyService_GetEnergyClass(" + ")");

            string classification=m_energyprofile.GetEnergyProfileValue(".//*[name()='energyclassification']/*[name()='system']");
            string value = m_energyprofile.GetEnergyProfileValue(".//*[name()='energyclassification']/*[name()='value']");


            return "System:" + classification + "\nvalue:" + value;
        }

        /// <summary>
        /// Returns the current mode energy mode for the device
        /// </summary>
        /// <returns>A string</returns>
        virtual public System.String EnergyService_GetEnergyMode()
        {
            Console.WriteLine("EnergyService_GetEnergyMode(" + ")");

            string energymode = GetCustomFieldFromDescription("energymode", "IoT");

            return energymode;
        }

        /// <summary>
        /// Returns the status for the different properties expressed in the energy policy
        /// </summary>
        /// <returns>A string</returns>
        virtual public System.String EnergyService_GetEnergyPolicyStatus()
        {
            Console.WriteLine("EnergyService_GetEnergyPolicyStatus(" + ")");

            return "Not implemented yet";
        }

        /// <summary>
        /// Returns the current energy profile assigned to the device
        /// </summary>
        /// <returns>An XML string</returns>
        virtual public System.String EnergyService_GetEnergyProfile()
        {
           
            Console.WriteLine("EnergyService_GetEnergyProfile(" + ")");

            if (m_energyprofile != null)
            {
                return m_energyprofile.GetEnergyProfile();
            }
            else
                return "<noenergyprofile/>";
        }

        /// <summary>
        /// Returns the max effect value for the current operating mode of the device
        /// </summary>
        /// <returns>A string</returns>
        virtual public System.String EnergyService_GetMaxEffect()
        {
            Console.WriteLine("EnergyService_GetMaxEffect(" + ")");

            if (m_energyprofile != null)
            {string energymode=GetCustomFieldFromDescription("energymode","IoT");
            if (energymode == "")
                energymode = "running";
                return m_energyprofile.GetMaxEffect(energymode);
            }
            else
                return "<noenergyprofile/>";

            
        }

        /// <summary>
        /// Returns the min effect value for the current operating mode of the device
        /// </summary>
        /// <returns>A string</returns>
        virtual public System.String EnergyService_GetMinEffect()
        {
            Console.WriteLine("EnergyService_GetMinEffect(" + ")");

            if (m_energyprofile != null)
            {
                string energymode = GetCustomFieldFromDescription("energymode", "IoT");
                if (energymode == "")
                    energymode = "running";
                return m_energyprofile.GetMinEffect(energymode);
            }
            else
                return "<noenergyprofile/>";
        }

        /// <summary>
        /// Returns the average effect value for the current operating mode of the device
        /// </summary>
        /// <returns>A string</returns>
        virtual public System.String EnergyService_GetAverageEffect()
        {
            Console.WriteLine("EnergyService_GetAverageEffect(" + ")");

            if (m_energyprofile != null)
            {
                string energymode = GetCustomFieldFromDescription("energymode", "IoT");
                if (energymode == "")
                    energymode = "running";
                return m_energyprofile.GetAverageEffect(energymode);
            }
            else
                return "<noenergyprofile/>";
        }

        

        /// <summary>
        /// Returns the estimated current remaining lifetime for the device
        /// </summary>
        /// <returns>A string</returns>
        virtual public System.String EnergyService_GetRemainingLifeTime()
        {
            Console.WriteLine("EnergyService_GetRemainingLifeTime(" + ")");

            int elapsedseconds = 0;
            string energymode = GetCustomFieldFromDescription("energymode", "IoT");

            if (energymode == "running")
                elapsedseconds = CalculateSecondsElapsed(m_activetime);

            int remtime = System.Convert.ToInt32(m_remaininglifetime) - elapsedseconds;
            return remtime.ToString();
        }

        virtual public void EnergyService_SetDeviceEnergyPolicy(System.String deviceEnergyPolicy)
        {
            if (m_energypolicy == null)
            {
                m_energypolicy = new EnergyPolicy();

                m_energypolicy.SetDevice(this);
            }
                m_energypolicy.LoadPolicy(deviceEnergyPolicy);

                EnergyPolicyLoaded();

            Console.WriteLine("EnergyService_SetDeviceEnergyPolicy(" + deviceEnergyPolicy.ToString() + ")");
        }

        virtual public void EnergyService_SetDeviceEnergyPolicyFromURL(System.String deviceEnergyPolicyURL)
        {
            if (m_energypolicy == null)
                {
                    m_energypolicy = new EnergyPolicy();
                    m_energypolicy.SetDevice(this);
                }
                m_energypolicy.LoadPolicyFromURL(deviceEnergyPolicyURL);

                EnergyPolicyLoaded();

            Console.WriteLine("EnergyService_SetDeviceEnergyPolicyFromURL(" + deviceEnergyPolicyURL.ToString() + ")");
        }

        virtual public void EnergyService_SetEnergyProfile(System.String energyProfile)
        {
            if (m_energyprofile == null)
                {
                    m_energyprofile = new EnergyProfile();
                    m_energyprofile.SetDevice(this);
                }
                m_energyprofile.LoadProfile(energyProfile);

                if (m_remaininglifetime == null)
                {
                    m_remaininglifetime = m_energyprofile.GetExpectedLifeTime();

                    m_startcost = m_energyprofile.GetEnergyProfileValue(".//*[name()='lifetime']/*[name()='startcost']");
                    m_shutdowncost = m_energyprofile.GetEnergyProfileValue(".//*[name()='lifetime']/*[name()='shutdowncost']");
                }
          
            Console.WriteLine("EnergyService_SetEnergyProfile(" + energyProfile.ToString() + ")");
        }

        virtual public void EnergyService_SetEnergyProfileFromURL(System.String energyProfileURL)
        {
            if (m_energyprofile == null)
            {
                m_energyprofile = new EnergyProfile();
                m_energyprofile.SetDevice(this);
            }
            
            m_energyprofile.LoadProfileFromURL(energyProfileURL);

            if (m_remaininglifetime == null)
            {
                m_remaininglifetime = m_energyprofile.GetExpectedLifeTime();

                m_startcost = m_energyprofile.GetEnergyProfileValue(".//*[name()='lifetime']/*[name()='startcost']");
                m_shutdowncost = m_energyprofile.GetEnergyProfileValue(".//*[name()='lifetime']/*[name()='shutdowncost']");
            }
          
            Console.WriteLine("EnergyService_SetEnergyProfileFromURL(" + energyProfileURL.ToString() + ")");
        }

        //*********************End Energy Service************************************


        //*********************Start Location Service************************************


        virtual public System.String LocationService_GetCurrentPosition()
		{
			Console.WriteLine("LocationService_GetCurrentPosition()");

            string returnstring = GetCustomFieldFromDescription("position", "IoTlocation");

            return returnstring;
		}
		
		virtual public System.String LocationService_GetLocationModel()
		{
			Console.WriteLine("LocationService_GetLocationModel(" + ")");

            string returnstring = GetCustomFieldFromDescription("locationmodel", "IoTlocation");

            return returnstring;
		}
		
		virtual public System.String LocationService_GetLocationProperty(System.String key)
		{
			Console.WriteLine("LocationService_GetLocationProperty(" + key.ToString() + ")");

            string returnstring = GetCustomFieldFromDescription(key, "IoTlocation");

            return returnstring;
		}
		
		virtual public void LocationService_SetCurrentPosition(System.String currentposition)
		{
			Console.WriteLine("LocationService_SetCurrentPosition(" + currentposition.ToString() + ")");

            AddCustomFieldInDescription("position", currentposition, "IoTlocation");
		}
		
		virtual public void LocationService_SetLocationModel(System.String locationmodel)
		{
			Console.WriteLine("LocationService_SetLocationModel(" + locationmodel.ToString() + ")");
            AddCustomFieldInDescription("locationmodel", locationmodel, "IoTlocation");
		}
		
		virtual public void LocationService_SetLocationProperty(System.String locationproperty, System.String value)
		{
			Console.WriteLine("LocationService_SetLocationProperty(" + locationproperty.ToString() + value.ToString() + ")");

            AddCustomFieldInDescription(locationproperty, value, "IoTlocation");
		}

        //*********************End Location Service************************************

        //*********************Start Memory Service************************************
		
		virtual public System.String MemoryService_GetLogModel(System.String key)
		{
			Console.WriteLine("MemoryService_GetLogModel(" + key.ToString() + ")");

            			
			return "Sample String";
		}
		
		virtual public void MemoryService_LogProperty(System.String key, System.String property, System.Int32 interval)
		{
			Console.WriteLine("MemoryService_LogProperty(" + key.ToString() + property.ToString() + interval.ToString() + ")");
		}
		
		virtual public void MemoryService_ReloadState(System.String key)
		{
			Console.WriteLine("MemoryService_ReloadState(" + key.ToString() + ")");
		}
		
		virtual public System.String MemoryService_RetrieveEventLog(System.String From, System.String To)
		{
			Console.WriteLine("MemoryService_RetrieveEventLog(" + From.ToString() + To.ToString() + ")");

            
			
			return m_filesystem.getFile("c:\\IoTdevices\\basicphone\\mylog.xml");
		}
		
		virtual public void MemoryService_RetrieveModelLog(System.String key)
		{
			Console.WriteLine("MemoryService_RetrieveModelLog(" + key.ToString() + ")");
		}
		
		virtual public System.String MemoryService_RetrievePropertyLog(System.String key, System.String From, System.String To)
		{
			Console.WriteLine("MemoryService_RetrievePropertyLog(" + key.ToString() + From.ToString() + To.ToString() + ")");
			
			return "Sample String";
		}
		
		virtual public void MemoryService_SaveState(System.String key)
		{
			Console.WriteLine("MemoryService_SaveState(" + key.ToString() + ")");

		}
		
		virtual public void MemoryService_SetLogModel(System.String key, System.String logmodel)
		{
			Console.WriteLine("MemoryService_SetLogModel(" + key.ToString() + logmodel.ToString() + ")");
		}
		
		virtual public void MemoryService_StartModelLog(System.String key)
		{
			Console.WriteLine("MemoryService_StartModelLog(" + key.ToString() + ")");

            m_filesystem = new FileSystemDevice();

            if (m_filesystemdeviceurl == null || m_filesystemdeviceurl == "")
            {
                string filesystemdeviceurl = GetCustomFieldFromDescription("filesystemdeviceurl", "IoT");

                m_filesystemdeviceurl = filesystemdeviceurl;
            }

            if (m_filesystemdeviceurl != null && m_filesystemdeviceurl != "")
            {
                m_filesystem.Url = m_filesystemdeviceurl;

                string filesystemid=m_filesystem.getID();

                m_filesystem.createFile("c:\\IoTdevices\\basicphone\\mylog.xml", "");

                m_filesystem.writeFile("c:\\IoTdevices\\basicphone\\mylog.xml", "0", "<somestuff>Here is some stuff</somestuff>");

                
            }
            
		}
		
		virtual public void MemoryService_StopModelLog(System.String key)
		{
			Console.WriteLine("MemoryService_StopModelLog(" + key.ToString() + ")");
		}
        //*********************End Memory Service************************************

        //*********************Start Power Service (depreciated)************************************

        virtual public System.String PowerService_GetCurrentConsumption()
        {
            Console.WriteLine("PowerService_GetCurrentConsumption(" + ")");

            return m_currentpowerconsumption.ToString();
        }

        virtual public System.String PowerService_GetMaxPowerConsumption()
        {
            Console.WriteLine("PowerService_GetMaxPowerConsumption(" + ")");

            return m_maxpowerconsumption.ToString(); ;
        }

        virtual public System.String PowerService_GetRemainingBatteryLevel()
        {
            Console.WriteLine("PowerService_GetRemainingBatteryLevel(" + ")");

            return m_remainingbatterylevel.ToString();
        }

        virtual public System.String PowerService_GetRemainingBatteryTime()
        {
            Console.WriteLine("PowerService_GetRemainingBatteryTime(" + ")");

            return m_remainingbatterytime.ToString();
        }

        virtual public System.String PowerService_GetStandByTime()
        {
            Console.WriteLine("PowerService_GetStandByTime(" + ")");

            return m_standbytime.ToString();
        }

        virtual public System.String PowerService_GetTimeActive()
        {
            Console.WriteLine("PowerService_GetTimeActive(" + ")");

            return m_activetime;
        }

        virtual public System.String PowerService_GetTimeUntilStandBy()
        {
            Console.WriteLine("PowerService_GetTimeUntilStandBy(" + ")");

            return "Not implemented";
        }

        virtual public System.Boolean PowerService_IsBatteryOperated()
        {
            Console.WriteLine("PowerService_IsBatteryOperated(" + ")");

            return m_onbattery;
        }

        virtual public void PowerService_SetStandByTime(System.String StandByTime)
        {
            Console.WriteLine("PowerService_SetStandByTime(" + StandByTime.ToString() + ")");

            m_standbytime = System.Convert.ToInt32(StandByTime);

            m_timer = new Timer(new TimerCallback(Timer_Tick));

            m_timer.Change(10000, 10000);
        }

        //*********************End Power Service (depreciated)************************************
	}


    //*******************************Below is the Web service related code************************

    public class IoTDeviceWSHost
    {
        internal static ServiceHost myServiceHost = null;

        public static void StartService(object theWS, string implementedContract, string stringbaseaddress, string binding)
        {
            //Consider putting the baseAddress in the configuration system
            //and getting it here with AppSettings
            Uri baseAddress = new Uri(stringbaseaddress);

            //Instantiate new ServiceHost 
            //myServiceHost = new ServiceHost(typeof(WCFServiceLibrary1.BasicPhoneWS), baseAddress);


            myServiceHost = new ServiceHost(theWS, baseAddress);

          
            //implementedContract=="WCFServiceLibrary1.IService1"

            if (binding == "BasicHttp")
            {
                BasicHttpBinding myBinding = new BasicHttpBinding();

                myServiceHost.AddServiceEndpoint(implementedContract, new BasicHttpBinding(), stringbaseaddress);

                ServiceMetadataBehavior smb = new ServiceMetadataBehavior();
                smb.HttpGetEnabled = true;

                myServiceHost.Description.Behaviors.Add(smb);

                myServiceHost.AddServiceEndpoint(typeof(IMetadataExchange), MetadataExchangeBindings.CreateMexHttpBinding(), stringbaseaddress + "mex");
            }
            else if (binding == "WebHttp")
            {
                //ServiceEndpoint myEndpoint = myServiceHost.AddServiceEndpoint(implementedContract, new WebHttpBinding(), stringbaseaddress);

                //myEndpoint.Behaviors.Add(new WebHttpBehavior());
            }

            else if (binding == "WSHttp")
            {
                
            }

            //Open myServiceHost
            myServiceHost.Open();



        }

        internal static void StopService()
        {
            //Call StopService from your shutdown logic (i.e. dispose method)
            if (myServiceHost.State != CommunicationState.Closed)
                myServiceHost.Close();
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

    //***********************The generic IoT Web Service**************************
    [ServiceContract()]
    public interface IIoTDeviceWSService
    {

        [OperationContract]
        System.String GetIoTID();

        [OperationContract]
        System.String GetStatus();

        [OperationContract]
        System.String GetProperty(System.String Property);

        [OperationContract]
        System.Boolean GetHasError();

        [OperationContract]
        System.String GetErrorMessage();

        [OperationContract]
        System.String GetPhysicalDiscoveryInfo();

        [OperationContract]
        System.String GetIoTDeviceXML();

        [OperationContract]

        void SetIoTID(System.String IoTID);

        [OperationContract]
        void SetStatus(System.String Status);


        [OperationContract]
        void SetProperty(System.String Property, System.String Value);


        [OperationContract]
        System.String GetDACEndpoint();


        [OperationContract]
        void StartDevice();


        [OperationContract]
        void StopDevice();



    }

    /// <summary>
    /// IoTDevice is the base class for all IoT Device Managers. It provides a service interface to the 
    /// lower level functions of the device. It offers the following main functions:
    /// •	Maps requests to device services
    ///•	Response generation
    ///•	Advertising IoT device description
    ///•	Advertises device services

    /// </summary>
    /// 
    
    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single,IncludeExceptionDetailInFaults = true)]
    public class IoTDeviceWS : IIoTDeviceWSService
    {


        IoTDevice m_IoTdevice;

        public IoTDeviceWS(IoTDevice theDevice)
        {
            m_IoTdevice = theDevice;
        }

        /// <summary>
        ///Returns the IoTID for the device
        /// </summary>
        /// <returns>The HID of the device</returns>
        public System.String GetIoTID()
        {
            return m_IoTdevice.IoTService_GetIoTID();
        }

        /// <summary>
        ///Returns the status for the device
        /// </summary>
        /// <returns>The status of the device</returns>
        public System.String GetStatus()
        {
            return m_IoTdevice.IoTService_GetStatus();
        }


        /// <summary>
        ///Returns a property of the device. A developer can choose any properties he like to use and set.
        /// </summary>
        /// <param name="property">A valid property name (valid XML element name)</param>
        /// <returns>The value of the property</returns>
        public System.String GetProperty(System.String Property)
        {
            return m_IoTdevice.GetCustomFieldFromDescription(Property, "IoT");
        }

        /// <summary>
        ///Tells if the device has an error
        /// </summary>
        /// /// <returns>True if error, false otherwise</returns>
        public System.Boolean GetHasError()
        {
            return m_IoTdevice.IoTService_GetHasError();
        }

        /// <summary>
        ///Returns an errormessage for the device
        /// </summary>
        /// /// <returns>Latest errormessage</returns>
        public System.String GetErrorMessage()
        {
            return m_IoTdevice.IoTService_GetErrorMessage();
        }

        /// <summary>
        /// Returns the physical discovery information that is associated with the device
        /// </summary>
        /// <returns>An XML string</returns>
        public System.String GetPhysicalDiscoveryInfo()
        {
            return m_IoTdevice.IoTService_GetDiscoveryInfo();
        }

        /// <summary>
        ///Returns the device model XML for the IoT Device
        /// </summary>
        /// <returns>the device model XML in SCPD format</returns>
        public System.String GetIoTDeviceXML()
        {
            XmlDocument myDoc = new XmlDocument();

            string returnstring = "<noxml/>";

            try
            {
                byte[] myDeviceBytes = m_IoTdevice.GetRootDeviceXML(null);

                System.Text.UTF8Encoding myUTF8EncodeService = new UTF8Encoding();

                string mystring = myUTF8EncodeService.GetString(myDeviceBytes);

                myDoc.LoadXml(mystring);

                returnstring = myDoc.OuterXml;
            }

            catch (Exception e)
            {
                m_IoTdevice.ReportError("IoTWS:" + e.Message);
                returnstring = "<error>" + "IoTWS:" + e.Message + "</error>";
            }

            return returnstring;
        }

        /// <summary>
        ///Returns the endpoint of the DAC that has discovered the device
        /// </summary>
        /// <returns>The endpoint of the current DAC</returns>
        public System.String GetDACEndpoint()
        {
            return m_IoTdevice.GetDACEndpoint();
        }

        /// <summary>
        ///Sets the IoTID for the device
        /// </summary>
        /// <param name="IoTID">The valid IoT ID</param>
        public void SetIoTID(System.String IoTID)
        {
            m_IoTdevice.IoTService_SetIoTID(IoTID);
        }

        /// <summary>
        ///Sets the status for the device
        /// </summary>
        /// <param name="Status">A status value choosen by the developer</param>
        public void SetStatus(System.String Status)
        {
            m_IoTdevice.IoTService_SetStatus(Status);
        }

        /// <summary>
        ///Sets a property of the device. A developer can choose any properties he like to use and set.
        /// </summary>
        /// <param name="property">A valid property name (valid XML element name)</param>
        /// <param name="value">The value of the property</param>
        public void SetProperty(System.String Property, System.String Value)
        {
            m_IoTdevice.AddCustomFieldInDescription(Property, Value, "IoT");
        }

        /// <summary>
        ///Starts the device
        /// </summary>
        public void StartDevice()
        {
            m_IoTdevice.Start();
        }

        /// <summary>
        ///Stops the device, which will cause it to be removed from its current DAC. The device can later be re-started.
        /// </summary>
        public void StopDevice()
        {
            m_IoTdevice.Stop();
        }
    }


    //***********************The Energy Web Service**************************
    [ServiceContract()]
    public interface IIoTDeviceEnergyWSService
    {

        [OperationContract]
        System.String GetCurrentEffect();
        [OperationContract]
        System.String GetCurrentUsage();
        [OperationContract]
        System.String GetTotalUsage(System.String since);

        [OperationContract]
        System.String GetDeviceEnergyPolicy();
        [OperationContract]
        System.String GetEnergyClass();
        [OperationContract]
        System.String GetEnergyMode();
        [OperationContract]
        System.String GetEnergyPolicyStatus();
        [OperationContract]
        System.String GetEnergyProfile();
        [OperationContract]
        System.String GetMaxEffect();
        [OperationContract]
        System.String GetMinEffect();
        [OperationContract]
        System.String GetAverageEffect();

        [OperationContract]
        System.String GetRemainingLifeTime();
    }

    /// <summary>
    /// Every IoT device has an energy service for management of basic energy properties
    /// </summary>
    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single,IncludeExceptionDetailInFaults = true)]
    public class IoTDeviceEnergyWS : IIoTDeviceEnergyWSService
    {

        IoTDevice m_IoTdevice;

        public IoTDeviceEnergyWS(IoTDevice theDevice)
        {
            m_IoTdevice = theDevice;
        }


        /// <summary>
        /// Returns the current effect for the device in its current operating mode
        /// </summary>
        /// <returns>An string representing a number</returns>
        public System.String GetCurrentEffect()
        {
            return m_IoTdevice.EnergyService_GetCurrentUsage();
        }

        /// <summary>
        /// Returns the current energy consumption in Watt for the device in its current operating mode
        /// </summary>
        /// <returns>An string representing a number</returns>
        public System.String GetCurrentUsage()
        {
            return m_IoTdevice.EnergyService_GetCurrentUsage();
        }

        /// <summary>
        /// Returns the total energy consumption for the device
        /// </summary>
        /// <param name="since">values="today","lastturnon". An empty string will return the consumption since the device was first registered</param>
        /// <returns>An string representing a number</returns>
        public System.String GetTotalUsage(System.String since)
        {
            return m_IoTdevice.EnergyService_GetTotalUsage(since);
        }

        /// <summary>
        /// Returns the current energy policy assigned to the device
        /// </summary>
        /// <returns>An XML string</returns>
        public System.String GetDeviceEnergyPolicy()
        {
            return m_IoTdevice.EnergyService_GetDeviceEnergyPolicy();
        }

        /// <summary>
        /// Returns the current energy class for the device including the classification system
        /// </summary>
        /// <returns>A string</returns>
        public System.String GetEnergyClass()
        {
            return m_IoTdevice.EnergyService_GetEnergyClass();
        }

        /// <summary>
        /// Returns the current mode eneryg mode for the device
        /// </summary>
        /// <returns>A string</returns>
        public System.String GetEnergyMode()
        {
            return m_IoTdevice.EnergyService_GetEnergyMode();
        }

        /// <summary>
        /// Returns the status for the different properties expressed in the energy policy
        /// </summary>
        /// <returns>A string</returns>
        public System.String GetEnergyPolicyStatus()
        {
            return m_IoTdevice.EnergyService_GetEnergyPolicyStatus();
        }

        /// <summary>
        /// Returns the current energy profile assigned to the device
        /// </summary>
        /// <returns>An XML string</returns>
        public System.String GetEnergyProfile()
        {
            return m_IoTdevice.EnergyService_GetEnergyProfile();
        }

        /// <summary>
        /// Returns the max effect value for the current operating mode of the device
        /// </summary>
        /// <returns>A string</returns>
        public System.String GetMaxEffect()
        {
            return m_IoTdevice.EnergyService_GetMaxEffect();
        }

        /// <summary>
        /// Returns the min effect value for the current operating mode of the device
        /// </summary>
        /// <returns>A string</returns>
        public System.String GetMinEffect()
        {
            return m_IoTdevice.EnergyService_GetMinEffect();
        }

        /// <summary>
        /// Returns the average effect value for the current operating mode of the device
        /// </summary>
        /// <returns>A string</returns>
        public System.String GetAverageEffect()
        {
            return m_IoTdevice.EnergyService_GetAverageEffect();
        }

        /// <summary>
        /// Returns the estimated current remaining lifetime for the device
        /// </summary>
        /// <returns>A string</returns>
        public System.String GetRemainingLifeTime()
        {
            return m_IoTdevice.EnergyService_GetRemainingLifeTime();
        }

    }
}


