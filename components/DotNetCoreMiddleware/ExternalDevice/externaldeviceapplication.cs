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
﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ExternalDeviceApplication
{
    class ExternalDeviceMain
    {
        static void Main(string[] args)
        {
            ExternalDiscoveryManager myExternal = null;

            string gateway = System.Configuration.ConfigurationSettings.AppSettings["gateway"];
            if (gateway == null || gateway == "") gateway = System.Environment.MachineName;

            myExternal = new ExternalDiscoveryManager("18", "ExternalDiscoveryManager", "CNet", "urn:schemas-upnp-org:IoTdiscoverymanager:externaldiscoverymanager:1");
            myExternal.SetGateway(gateway);
            myExternal.AddServices();

           

            myExternal.Start();
            myExternal.DiscoverDevices();

            bool failedwscreate = false;

            try
            {
                //myExternal.CreateWS();
            }

            catch (Exception e)
            {
                System.Console.WriteLine("Failed to create web services, you probably need to upgrade Visual Studio, error message is:" + e.Message);
                System.Console.WriteLine("The devices created at your gateway will be discovered by the IoT network and their services can be fully used by other IoT devices. But access is only possible through the UPnP protocol");

                failedwscreate = true;

            }

            if (failedwscreate)
            {
                try
                {
                    myExternal.DiscoverDevices();
                }
                catch (Exception e)
                {
                    System.Console.WriteLine("Failed external discovery, error message is:" + e.Message);
                }
            }

            System.Console.WriteLine("Press return to stop all devices");
            System.Console.ReadLine();

            try
            {
                myExternal.StopDevices("");

            }
            catch (Exception e)
            {
            }

            System.Console.WriteLine("Press return to stop the discovery manager");
            System.Console.ReadLine();

            try
            {
                myExternal.StopDevice();
            }
            catch (Exception e)
            {
            }
        }
    }
}
