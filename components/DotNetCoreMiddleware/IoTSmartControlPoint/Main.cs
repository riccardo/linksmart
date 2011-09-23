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


namespace IoT
{
	/// <summary>
	/// Summary description for Main.
	/// </summary>
    /// 
    
	class ApplicationDeviceManagerApplication
	{
		/// <summary>
		/// The main entry point for the application.
		/// </summary>
        

		[STAThread]

        

		static void Main(string[] args)
        {
            ApplicationDeviceManagerDevice m_devicemanager;
            ApplicationServiceManagerDevice m_servicemanager;
           
            string m_soaptunnelprefix="/SOAPTunneling/0/";
            string m_soaptunnelsuffix="/0/hola";
            string m_soaptunnelIPaddress="127.0.0.1";
            string m_soaptunnelport="8082";
            bool m_externalonly = false;

            string m_IoTserviceid = "urn:upnp-org:serviceId:1";
            string m_limboIoTserviceid = "urn:upnp-org:serviceId:IoTServicePort";//KOOL Changed from "urn:upnp-org:serviceId:IoTservice";
 

            bool m_automaticdiscovery = false;
            bool m_automaticresolve = true;
            string m_discoveryresolveperiod = "12000";

            m_devicemanager = new ApplicationDeviceManagerDevice("0", "ApplicationDeviceManager", "CNet", "urn:schemas-upnp-org:IoTdevice:devicemanager:1");
            string tmpGateway = System.Configuration.ConfigurationSettings.AppSettings["gateway"];
            if (tmpGateway == "") tmpGateway = System.Environment.MachineName;

            m_devicemanager.SetGateway(tmpGateway);
            m_devicemanager.Start();

            m_servicemanager = new ApplicationServiceManagerDevice("1", "ApplicationServiceManager", "CNet", "urn:schemas-upnp-org:IoTdevice:servicemanager:1");

            m_servicemanager.SetGateway(tmpGateway);
            m_servicemanager.Start();


            IoTSmartControlPoint scp = new IoTSmartControlPoint();


            /*this.scp.SetFormDeviceHandler(this.HandleAddedDevice);
            this.scp.SetFormRemoveDeviceHandler(this.HandleRemovedDevice);
            this.scp.SetFormUpdatedDeviceHandler(this.HandleUpdatedDevice);
            */

            scp.SetGateway(tmpGateway);
            scp.SetDiscoveryRules(System.Configuration.ConfigurationSettings.AppSettings["usexslfordiscovery"], System.Configuration.ConfigurationSettings.AppSettings["discovertransform"]);
            scp.SetOntologyUrl(System.Configuration.ConfigurationSettings.AppSettings["ontologyurl"]);
            scp.SetAutomaticDiscoveryAndResolve(m_automaticdiscovery, m_automaticresolve, m_discoveryresolveperiod);
            scp.SetAutomaticDeviceStorage(System.Configuration.ConfigurationSettings.AppSettings["automaticdevicestorage"]);
            scp.SetEnergyPolicyEnforcement(System.Configuration.ConfigurationSettings.AppSettings["energypolicyenforcement"]);
            scp.SetCallBackTransform(System.Configuration.ConfigurationSettings.AppSettings["callbacktransform"]);

            scp.SetRemoveHIDsOnStop(System.Configuration.ConfigurationSettings.AppSettings["removehidsonstop"]);
            //this.scp.AddSubscribeUrl("http://212.214.80.175:81/IoTServiceBrowser/callBackDAC.php");

            scp.SetApplicationBindingsUrl(System.Configuration.ConfigurationSettings.AppSettings["appdevicebindings"]);
            //this.scp.OnRemovedDevice += new UPnPSmartControlPoint.DeviceHandler(this.HandleRemovedDevice);
            //m_devicemanager.CreateWS();
            scp.SetNetworkManagerUrl(System.Configuration.ConfigurationSettings.AppSettings["networkmanagerurl"]);
            scp.SetEventManagerUrl(System.Configuration.ConfigurationSettings.AppSettings["eventmanagerurl"]);
            scp.SetStorageManagerUrl(System.Configuration.ConfigurationSettings.AppSettings["storagemanagerurl"]);
            scp.SetSOAPTunnelAddress(m_soaptunnelIPaddress, m_soaptunnelprefix, m_soaptunnelsuffix, m_soaptunnelport);

            m_devicemanager.SetSmartControlPoint(scp);
            m_devicemanager.SetNetworkManagerUrl(System.Configuration.ConfigurationSettings.AppSettings["networkmanagerurl"]);
            m_devicemanager.SetEventManagerUrl(System.Configuration.ConfigurationSettings.AppSettings["eventmanagerurl"]);



            m_servicemanager.SetSmartControlPoint(scp);
            m_servicemanager.SetNetworkManagerUrl(System.Configuration.ConfigurationSettings.AppSettings["networkmanagerurl"]);
            m_servicemanager.SetEventManagerUrl(System.Configuration.ConfigurationSettings.AppSettings["eventmanagerurl"]);

            string DACEndPoint = m_devicemanager.CreateWS();
            NetworkManager.NetworkManagerApplicationService nm = new NetworkManager.NetworkManagerApplicationService();
            nm.Url = System.Configuration.ConfigurationSettings.AppSettings["networkmanagerurl"];
            string dacHID = nm.createHIDwDesc("ApplicationDeviceManager:" + tmpGateway + ":StaticWS",DACEndPoint);
            m_servicemanager.CreateWS();

            System.Console.WriteLine("Press return to stop Application Device Manager");
            System.Console.ReadLine();
		}

        
	}
}

