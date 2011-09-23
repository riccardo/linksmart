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
using System.Runtime.InteropServices;
using System.Reflection;
using System.Runtime.CompilerServices;
using OpenSource.UPnP;



namespace DeviceServiceManager
{
    /// <summary>
    /// The Device Service Manager implements a service interface for physical devices. It should normally not be used
    /// directly by any other manger than the Device managers.
    /// Normally you need to subclass this class for your own IoT devices
    /// </summary>
    public class DeviceServiceManager:Object
    {
    
        public object m_internaldevice;//the physical device object controlled by this Device Service Manager
        public string m_errormessage;
        public UPnPDevice m_IoTdevice;//the IoT device connected  with this Device Service Manager
        public UPnPDevice m_discoverymanager;

        public string Invoke(string serviceid,string methodname,string parameters, string value)
        {
            return "not implemented";
        }

        public string GetErrorMessage()
        {
            return m_errormessage;
        }

        public void SetErrorMessage(string errormessage)
        {
            m_errormessage = errormessage;
        }

        public void ResetError()
        {
            m_errormessage = "";
        }

        public bool HasError()
        {
            return m_errormessage != "";
        }

        public void ReportError(string errorstring)
        {
            m_errormessage = errorstring;

            if (m_IoTdevice != null)
                m_IoTdevice.AddCustomFieldInDescription("errormessage", "deviceservicemanager:" + m_errormessage, "IoT");
            if (m_discoverymanager != null)
                m_discoverymanager.AddCustomFieldInDescription("errormessage", "deviceservicemanager:" + m_errormessage, "IoT");

                        
        }

        virtual public void SetInternalDevice(object internaldevice)
        {
            m_internaldevice = internaldevice;
        }

        public object GetInternalDevice()
        {
            return m_internaldevice;
        }

        public void SetIoTDevice(UPnPDevice IoTdevice)
        {
            m_IoTdevice = IoTdevice;
        }

        public UPnPDevice GetIoTDevice()
        {
            return m_IoTdevice;
        }

        public void SetDiscoveryMgr(UPnPDevice discoverymanager)
        {
            m_discoverymanager = discoverymanager;
        }

        public UPnPDevice GetDiscoveryManager()
        {
            return m_discoverymanager;
        }
     }

    

}
