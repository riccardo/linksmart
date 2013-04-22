/*

In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
   of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
   such damages shall be statute barred within 12 months subsequent to the delivery of the software.
4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
   and consequential damages - except in cases of intent - is excluded.
This limitation of liability shall also apply if this license agreement shall be subject to law 
stipulating liability clauses corresponding to German law.


GNU LESSER GENERAL PUBLIC LICENSE

Version 3, 29 June 2007

Copyright © 2007 Free Software Foundation, Inc. <http://fsf.org/>

Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.

This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
0. Additional Definitions.

As used herein, “this License” refers to version 3 of the GNU Lesser General Public License, and the “GNU GPL” refers to version 3 of the GNU General Public License.

“The Library” refers to a covered work governed by this License, other than an Application or a Combined Work as defined below.

An “Application” is any work that makes use of an interface provided by the Library, but which is not otherwise based on the Library. Defining a subclass of a class defined by the Library is deemed a mode of using an interface provided by the Library.

A “Combined Work” is a work produced by combining or linking an Application with the Library. The particular version of the Library with which the Combined Work was made is also called the “Linked Version”.

The “Minimal Corresponding Source” for a Combined Work means the Corresponding Source for the Combined Work, excluding any source code for portions of the Combined Work that, considered in isolation, are based on the Application, and not on the Linked Version.

The “Corresponding Application Code” for a Combined Work means the object code and/or source code for the Application, including any data and utility programs needed for reproducing the Combined Work from the Application, but excluding the System Libraries of the Combined Work.
1. Exception to Section 3 of the GNU GPL.

You may convey a covered work under sections 3 and 4 of this License without being bound by section 3 of the GNU GPL.
2. Conveying Modified Versions.

If you modify a copy of the Library, and, in your modifications, a facility refers to a function or data to be supplied by an Application that uses the facility (other than as an argument passed when the facility is invoked), then you may convey a copy of the modified version:

    a) under this License, provided that you make a good faith effort to ensure that, in the event an Application does not supply the function or data, the facility still operates, and performs whatever part of its purpose remains meaningful, or
    b) under the GNU GPL, with none of the additional permissions of this License applicable to that copy.

3. Object Code Incorporating Material from Library Header Files.

The object code form of an Application may incorporate material from a header file that is part of the Library. You may convey such object code under terms of your choice, provided that, if the incorporated material is not limited to numerical parameters, data structure layouts and accessors, or small macros, inline functions and templates (ten or fewer lines in length), you do both of the following:

    a) Give prominent notice with each copy of the object code that the Library is used in it and that the Library and its use are covered by this License.
    b) Accompany the object code with a copy of the GNU GPL and this license document.

4. Combined Works.

You may convey a Combined Work under terms of your choice that, taken together, effectively do not restrict modification of the portions of the Library contained in the Combined Work and reverse engineering for debugging such modifications, if you also do each of the following:

    a) Give prominent notice with each copy of the Combined Work that the Library is used in it and that the Library and its use are covered by this License.
    b) Accompany the Combined Work with a copy of the GNU GPL and this license document.
    c) For a Combined Work that displays copyright notices during execution, include the copyright notice for the Library among these notices, as well as a reference directing the user to the copies of the GNU GPL and this license document.
    d) Do one of the following:
        0) Convey the Minimal Corresponding Source under the terms of this License, and the Corresponding Application Code in a form suitable for, and under terms that permit, the user to recombine or relink the Application with a modified version of the Linked Version to produce a modified Combined Work, in the manner specified by section 6 of the GNU GPL for conveying Corresponding Source.
        1) Use a suitable shared library mechanism for linking with the Library. A suitable mechanism is one that (a) uses at run time a copy of the Library already present on the user's computer system, and (b) will operate properly with a modified version of the Library that is interface-compatible with the Linked Version.
    e) Provide Installation Information, but only if you would otherwise be required to provide such information under section 6 of the GNU GPL, and only to the extent that such information is necessary to install and execute a modified version of the Combined Work produced by recombining or relinking the Application with a modified version of the Linked Version. (If you use option 4d0, the Installation Information must accompany the Minimal Corresponding Source and Corresponding Application Code. If you use option 4d1, you must provide the Installation Information in the manner specified by section 6 of the GNU GPL for conveying Corresponding Source.)

5. Combined Libraries.

You may place library facilities that are a work based on the Library side by side in a single library together with other library facilities that are not Applications and are not covered by this License, and convey such a combined library under terms of your choice, if you do both of the following:

    a) Accompany the combined library with a copy of the same work based on the Library, uncombined with any other library facilities, conveyed under the terms of this License.
    b) Give prominent notice with the combined library that part of it is a work based on the Library, and explaining where to find the accompanying uncombined form of the same work.

6. Revised Versions of the GNU Lesser General Public License.

The Free Software Foundation may publish revised and/or new versions of the GNU Lesser General Public License from time to time. Such new versions will be similar in spirit to the present version, but may differ in detail to address new problems or concerns.

Each version is given a distinguishing version number. If the Library as you received it specifies that a certain numbered version of the GNU Lesser General Public License “or any later version” applies to it, you have the option of following the terms and conditions either of that published version or of any later version published by the Free Software Foundation. If the Library as you received it does not specify a version number of the GNU Lesser General Public License, you may choose any version of the GNU Lesser General Public License ever published by the Free Software Foundation.

If the Library as you received it specifies that a proxy can decide whether future versions of the GNU Lesser General Public License shall apply, that proxy's public statement of acceptance of any version is permanent authorization for you to choose that version for the Library.
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;
using System.Web.Services;
using System.ServiceModel;
using System.Threading.Tasks;
using System.Xml;

namespace EventManager
{
    /// <summary>
    /// Network manager related functions for Event manager. These will be moved to a standalone Network Manager class.
    /// </summary>
    public partial class EventManagerImplementation
    {
        public static string Description = Properties.Settings.Default.EventManagerDesc;
        private static string _hid = string.Empty;
        public static string Hid
        {
            get
            {
                return _hid;
            }
            set
            {
                _hid = value;
                OnHidChange(new EmHidEventArgs() { Hid = value });
            }
        }
        private static void OnHidChange(EmHidEventArgs e)
        {
            EventHandler<EmHidEventArgs> handler = HidChanged;

            // Event will be null if there are no subscribers
            if (handler != null)
            {
                handler(null, e);
            }
        }
        public static event EventHandler<EmHidEventArgs> HidChanged;
        public class EmHidEventArgs : EventArgs
        {
            /// <summary>
            /// The Web Service endpoint for the device service.
            /// </summary>
            public string Hid;
        }



        private static string _address = string.Empty;
        public static string Address
        {
            get
            {
                return _address;
            }
            set
            {
                _address = value;
                OnAddressChange(new EmAddressEventArgs() { WsEndpoint = value });
            }
        }
        private static void OnAddressChange(EmAddressEventArgs e)
        {
             EventHandler<EmAddressEventArgs> handler = AddressChanged;

            // Event will be null if there are no subscribers
            if (handler != null)
            {
                handler(null, e);
            }
        }
        public static event EventHandler<EmAddressEventArgs> AddressChanged;
        public class EmAddressEventArgs : EventArgs
        {
            /// <summary>
            /// The Web Service endpoint for the device service.
            /// </summary>
            public string WsEndpoint;
        }

        public static bool IsRegisteredAtNetworkManager;

        private static string SoapTunnelingEndpoint = "http://127.0.0.1:8082/SOAPTunneling/0/{0}/0/";

        public static string GetNetworkManagerLocalEndpointForHid(string hid)
        {    
            return string.Format(SoapTunnelingEndpoint, hid);
        }

        public static string GetNetworkManagerLocalEndpointForDescription(string description)
        {

            string resultAddress = string.Empty;
            try
            {
                if (Properties.Settings.Default.EventManagerVersion.Equals("2.0"))
                {
                       NetworkManager20ServiceReference.NetworkManager nm = new NetworkManager20ServiceReference.NetworkManager();
                    NetworkManager20ServiceReference.Part a = new NetworkManager20ServiceReference.Part();
                    //a.key = "DESCRIPTION";
                    //a.value = description;
                    //NetworkManager20ServiceReference.NetworkManager.Part[] b = new NetworkManager20ServiceReference.NetworkManager.Part[2];
                    ////b[0] = a;
                    //a = new NetworkManager20ServiceReference.NetworkManager.Part();
                    //a.key = "EVENTMANAGER_VERSION";
                    //a.value = "2.0";
                    var registrations  = nm.getServiceByDescription(description);

                    resultAddress = GetNetworkManagerLocalEndpointForHid((registrations != null && registrations.Count() > 0) ? registrations[0].virtualAddressAsString : string.Empty);
                }
                else
                {
                    string hid = string.Empty;
                    var emHidList = GetHidsByDescription(description);
                    hid = (null != emHidList && emHidList.Count() > 0) ? emHidList.FirstOrDefault() : string.Empty;
                    resultAddress = GetNetworkManagerLocalEndpointForHid(hid);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Subscription description->hid lookup at Network Manager failed: {0}", e.Message);
            }
            return resultAddress;
        }

        public static List<string> GetHidsByDescription(string description)
        {
            var result = new List<string>();
            NetworkManager.NetworkManagerApplicationService client = new NetworkManager.NetworkManagerApplicationService();

            var hidObjects = client.getHIDsbyDescription(description);
           
            if (hidObjects.Count() > 0)
            {
                foreach (var hidObject in hidObjects)
                {
                    foreach (var o in (XmlNode[])hidObject)
                    {
                        if (o.NodeType.Equals(XmlNodeType.Text))
                        {
                            result.Add(o.InnerText);
                        }
                    }
                }
            }

            return result;
        }

        public static void DeregisterOtherDescriptionsAtNetworkManager()
        {
            Console.WriteLine("Removing other registered HIDs for {0} at Network Manager",EventManagerImplementation.Description);
            if (Properties.Settings.Default.EventManagerVersion.Equals("2.0"))
            {
                NetworkManager20ServiceReference.NetworkManager nm = new NetworkManager20ServiceReference.NetworkManager();
                var registrations = nm.getServiceByDescription(EventManagerImplementation.Description);
                foreach (var reg in registrations)
                {
                    try
                    {
                        if (!reg.virtualAddressAsString.Equals(EventManagerImplementation.Registration.virtualAddressAsString))
                        {
                            
                            nm.removeService(reg.virtualAddress);
                            Console.WriteLine("VirtualAddress removed: {0}", reg.virtualAddressAsString);
                        }
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine("Deregister VirtualAddress at Network Manager failed: {0}", e.Message);
                    }
                }
            }
            else
            {
                var hids = GetHidsByDescription(EventManagerImplementation.Description);
                foreach (var hid in hids)
                {
                    if (!hid.Equals(EventManagerImplementation.Hid))
                    {
                        try
                        {
                            NetworkManager.NetworkManagerApplicationService nma = new NetworkManager.NetworkManagerApplicationService();
                            nma.removeHID(hid);
                            Console.WriteLine("HID removed: {0}", hid);
                        }
                        catch (Exception e)
                        {
                            Console.WriteLine("Deregister HID at Network Manager failed: {0}", e.Message);
                        }
                    }
                }
            }
        }

        public static void DeregisterAtNetworkManager()
        {
            DeregisterHidAtNetworkManager(EventManagerImplementation.Hid);
            //try
            //{
            //    if (IsRegisteredAtNetworkManager)
            //    {
            //        NetworkManager.NetworkManagerApplicationService nma = new NetworkManager.NetworkManagerApplicationService();
            //        nma.removeHID(EventManagerImplementation.Hid);
            //        IsRegisteredAtNetworkManager = false;
            //        Console.WriteLine("EM HID removed: {0}", EventManagerImplementation.Hid);
            //    }
            //}
            //catch (Exception e)
            //{
            //    Console.WriteLine("Deregister at Network Manager failed: {0}", e.Message);
            //}
        }

        private static void DeregisterHidAtNetworkManager(string hid)
        {
            try
            {
                if (IsRegisteredAtNetworkManager)
                {
                    if (Properties.Settings.Default.EventManagerVersion.Equals("2.0"))
                    {
                        NetworkManager20ServiceReference.NetworkManager nm = new NetworkManager20ServiceReference.NetworkManager();

                        try
                        {
 
                                nm.removeService(EventManagerImplementation.Registration.virtualAddress);
                                IsRegisteredAtNetworkManager = false;
                                Console.WriteLine("VirtualAddress removed: {0}", EventManagerImplementation.Registration.virtualAddressAsString);
                            
                        }
                        catch (Exception e)
                        {
                            Console.WriteLine("Deregister VirtualAddress at Network Manager failed: {0}", e.Message);
                        }
                    }
                    else
                    {
                        NetworkManager.NetworkManagerApplicationService nma = new NetworkManager.NetworkManagerApplicationService();
                        nma.removeHID(hid);
                        IsRegisteredAtNetworkManager = false;
                        Console.WriteLine("EM HID removed: {0}", EventManagerImplementation.Hid);
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Deregister HID at Network Manager failed: {0}", e.Message);
            }
        }


        public static void RegisterAtNetworkManager(string address)
        {
            Address = address;
            RegisterAtNetworkManager();
            StartNetworkManagerMonitoring();
        }

        public static void RegisterAtNetworkManager()
        {
            try
            {
                if (Properties.Settings.Default.EventManagerVersion.Equals("2.0"))
                {
                    NetworkManager20ServiceReference.NetworkManager nm = new NetworkManager20ServiceReference.NetworkManager();
                    NetworkManager20ServiceReference.Part a = new NetworkManager20ServiceReference.Part();
                    a.key = "DESCRIPTION";
                    a.value = Description;
                    NetworkManager20ServiceReference.Part[] b = new NetworkManager20ServiceReference.Part[2];
                    b[0] = a;
                    a = new NetworkManager20ServiceReference.Part();
                    a.key = "EVENTMANAGER_VERSION";
                    a.value = "2.0";
                    b[1] = a;
                    
                    EventManagerImplementation.Registration = nm.registerService(b, Address, "eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl");
                    EventManagerImplementation.IsRegisteredAtNetworkManager = true;
                    Console.WriteLine("Registered at NM2.0: {0}", EventManagerImplementation.Registration.virtualAddressAsString);
                }
                else
                {
                    NetworkManager.NetworkManagerApplicationService nma = new NetworkManager.NetworkManagerApplicationService();
                    EventManagerImplementation.Hid = nma.createHIDwDesc(Description, Address);
                    IsRegisteredAtNetworkManager = true;
                    Console.WriteLine("EM HID assigned: {0}", EventManagerImplementation.Hid);
                    //Send Event #LINKSMARTADMIN#/EVENTMANAGER/EVENTMANAGERDESCRIPTION
                    //event = "HID"
                    //value = EventManagerImplementation.Hid
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Register at Network Manager failed: {0}", e.Message);
            }
        }

        private static Timer m_timer;
        private static void StartNetworkManagerMonitoring()
        {
            if (null == m_timer)
            {
                m_timer = new Timer(new TimerCallback(CheckNetworkmanager));
            }
            m_timer.Change(30000, 30000);
            Console.WriteLine("Network Manager monitoring process started.");

        }

        private static void CheckNetworkmanager(object obj)
        {
            try
            {
                if (Properties.Settings.Default.UseNetworkManager)
                {
                    try
                    {
                        if (Properties.Settings.Default.EventManagerVersion.Equals("2.0"))
                        {
                            NetworkManager20ServiceReference.NetworkManager nm = new NetworkManager20ServiceReference.NetworkManager();
                            NetworkManager20ServiceReference.Part a = new NetworkManager20ServiceReference.Part();
                            //a.key = "DESCRIPTION";
                            //a.value = Description;
                            //NetworkManager20ServiceReference.NetworkManager.Part[] b = new NetworkManager20ServiceReference.NetworkManager.Part[2];
                            //b[0] = a;
                            //a = new NetworkManager20ServiceReference.NetworkManager.Part();
                            //a.key = "EVENTMANAGER_VERSION";
                            //a.value = "2.0";
                            //b[1] = a;
                            var registrations = nm.getServiceByDescription(Description);
                            if (registrations.Count()<1) {
                                // The NM is up, but EM is no longer registered with the current HID.
                                IsRegisteredAtNetworkManager = false;
                                // Network Manager has been restarted
                                Console.WriteLine("Network Manager has been restarted.");
                                Console.WriteLine("Trying to register at Network Manager.");
                                RegisterAtNetworkManager();
                            
                            }
 //                           NetworkManager20ServiceReference.NetworkManager.Registration c = nm.registerService(b, "http://127.0.0.1:8082/Whatever", "eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl");
                        }
                        else
                        {
                            // If our Hid is no longer registered at the NM, or there if there is no response, we need to re-register.
                            NetworkManager.NetworkManagerApplicationService nma = new NetworkManager.NetworkManagerApplicationService();
                            nma.Timeout = 10000;
                            string description = nma.getDescriptionbyHID(Hid ?? string.Empty);
                            if (!Properties.Settings.Default.EventManagerDesc.Equals(description))
                            {
                                // The NM is up, but EM is no longer registered with the current HID.
                                IsRegisteredAtNetworkManager = false;
                                // Network Manager has been restarted
                                Console.WriteLine("Network Manager has been restarted.");
                                Console.WriteLine("Trying to register at Network Manager.");
                                RegisterAtNetworkManager();
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        IsRegisteredAtNetworkManager = false;
                        // Timeout or other exception
                        Console.WriteLine("Network Manager not avaliable: {0}", e.Message);
                    }
                }

            }
            catch (Exception e)
            {
                Console.WriteLine("Exception in Network Manager monitoring: " + e.Message);
            }
        }


        public static NetworkManager20ServiceReference.Registration Registration { get; set; }
    }
}


