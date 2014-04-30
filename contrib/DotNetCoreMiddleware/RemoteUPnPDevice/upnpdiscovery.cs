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
﻿// ----------------------------------------------------------------------------
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
using System.Linq;
using System.Text;
using OpenSource.UPnP;
using OpenSource.Utilities;
using IoT;
using DeviceServiceManager;
using DiscoveryManager;
using System.Xml;
using System.Text.RegularExpressions;



public class UPnPDiscoveryManager : DiscoveryManager.DiscoveryManager
{
    private Object m_lock = new Object();
    UPnPSmartControlPoint m_smartcontrolpoint = null;
    string[] m_filters;
    public UPnPDiscoveryManager(string IoTID, string name, string vendor, string deviceURN)
        : base(IoTID, name, vendor, deviceURN)
    {
        string filters = global::RemoteUPnPDevice.Properties.Settings.Default.UPnPFilter;

        if (filters != "")
        {
            char[] splitchar = new char[1];

            splitchar[0] = ',';
            m_filters = filters.Split(splitchar);
        }

    }

    public override void Start()
    {
        StartDevice();
    }

    override public System.String CreateWS()
    {
        IoTWCFServiceLibrary.DiscoveryManagerWS myWS = new IoTWCFServiceLibrary.DiscoveryManagerWS(this);

        InitiateWebService(myWS, "IoTWCFServiceLibrary.IIoTDiscoveryManagerWSService", "UPnPDiscoveryWS");


        return m_wsendpoint;
    }

    override public void DiscoverDevices()
    {
        AddCustomFieldInDescription("discoverystatus", "discoverystarted", "IoT");

        System.Console.WriteLine("Discovery started");

        m_smartcontrolpoint = new UPnPSmartControlPoint(new UPnPSmartControlPoint.DeviceHandler(this.HandleAddedDevice));

    }

    protected void HandleAddedDevice(UPnPSmartControlPoint sender, UPnPDevice device)
    {


        //m_smartcontrolpoint.HandleAddedDevice(sender, device);
        //string softmediarender = System.Configuration.ConfigurationSettings.AppSettings["softmediarenderer"];

        System.Console.WriteLine("Discovered:" + device.FriendlyName);

        lock (m_lock)
        {
            if (KeepDevice(device))
            {
                //if (device.DeviceURN.Contains("WeightScale"))
                //{
                //    WeightScaleDevice newDevice = new WeightScaleDevice("111", device.FriendlyName, device.Manufacturer, "urn:schemas-upnp-org:IoTdevice:weightscale:1");

                //    newDevice.SetRemoteDevice(device);

                //    InitialiseDevice(newDevice);

                //    newDevice.Start();

                //}
                //else if (device.DeviceURN.Contains("MediaRenderer")&& !device.FriendlyName.Contains("Intel Media Renderer"))
                //{
                //    MediaRendererDevice newDevice = new MediaRendererDevice("111", device.FriendlyName, device.Manufacturer, "urn:schemas-upnp-org:IoTdevice:hardmediarenderer:1");

                //    newDevice.SetRemoteDevice(device);

                //    InitialiseDevice(newDevice);

                //    newDevice.Start();

                //}
                //else if (device.DeviceURN.Contains("MediaRenderer") && device.FriendlyName.Contains("Intel Media Renderer") && softmediarender=="no")
                //{
                //    MediaRendererDevice newDevice = new MediaRendererDevice("111", device.FriendlyName, device.Manufacturer, "urn:schemas-upnp-org:IoTdevice:hardmediarenderer:1");

                //    newDevice.SetRemoteDevice(device);

                //    InitialiseDevice(newDevice);

                //    newDevice.Start();

                //} 
                //else
                //{

                System.Console.WriteLine("Create Device:" + device.FriendlyName);
                RemoteUPnPDevice.RemoteUPnPDevice newDevice = new RemoteUPnPDevice.RemoteUPnPDevice("111", device.FriendlyName, device.Manufacturer, "urn:schemas-upnp-org:IoTdevice:remotedevice:1");

                newDevice.SetRemoteDevice(device);

                InitialiseDevice(newDevice);

                newDevice.Start();

                //}
            }
        }


    }

    public bool KeepDevice(UPnPDevice theDevice)
    {


        if (theDevice.DeviceURN.Contains(":IoTdevice:") || theDevice.DeviceURN.Contains("IoTdiscoverymanager"))
            return false;
        else
        {
            foreach (string filterurn in m_filters)
            {
                Regex filterPattern = new Regex(filterurn);

                 //filterPattern.IsMatch(theDevice.DeviceURN.Trim());
                if (filterPattern.IsMatch(theDevice.DeviceURN.Trim()))
                    return true;
            }
        }

        return false;
    }
}
