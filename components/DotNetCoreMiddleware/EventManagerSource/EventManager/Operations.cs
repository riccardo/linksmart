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
﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using EventStorage;
using System.Configuration;
using System.Globalization;

namespace EventManager
{
    public class Operations
    {
        EventStorage.EventStorage es = new EventStorage.EventStorage();
        SubscriberInterface.EventSubscriberService eventSubscriberService = new SubscriberInterface.EventSubscriberService();
        SubscriberInterface20.EventSubscriber eventSubscriberService20 = new SubscriberInterface20.EventSubscriber();
        private publishRequest request = new publishRequest();
        private RetryQueue retryQueue = new RetryQueue();
        /// <summary>
        /// Add a new subscription to the subscriptionList
        /// </summary>
        public void addSubscription(Components.Subscription subscription)
        {
            SubscriptionStore.Store.SaveSubscription(subscription);
            EventManagerImplementation.subscriptionList.Add(subscription);
            if (!string.IsNullOrEmpty(subscription.HID))
            {
                Log.Debug(string.Format("Subscribe:\nTopic: {0}\nHid: {1}\nPriority: {2}", subscription.Topic, subscription.HID, subscription.Priority));
            }
            else if (!string.IsNullOrEmpty(subscription.Endpoint)) 
            {
                Log.Debug(string.Format("Subscribe:\nTopic: {0}\nEndpoint: {1}\nPriority: {2}", subscription.Topic, subscription.Endpoint, subscription.Priority));
            }
            else if (!string.IsNullOrEmpty(subscription.Description)) {
               Log.Debug(string.Format("Subscribe:\nTopic: {0}\nDescription: {1}\nPriority: {2}", subscription.Topic, subscription.Description, subscription.Priority));
            }
            
        }
        /// <summary>
        /// Notify published event to listed subscribers
        /// 
        //if (subscription.Protocol == "REST")
        //{
        //    string xmlEvent = @"<Event Topic=" + subscription.Topic + "><Part Key=" + subscription.Parts[0].key + 
        //                       " Value=" + subscription.Parts[0].key + "/><Part Key=" + subscription.Parts[1].key + 
        //                       " Value=" + subscription.Parts[1].value + " /></Event>";

        //    XmlDocument doc = new XmlDocument();
        //    doc.LoadXml(xmlEvent);

        //    string jsonText = JsonConvert.SerializeXmlNode(doc);

        //    HttpWebRequest req = (HttpWebRequest)WebRequest.Create(subscription.Endpoint);
        //    //GenericSoapCaller.HttpHandler httpReq = new GenericSoapCaller.HttpHandler();
        //    //httpReq.Init();
        //    StreamWriter sw = new StreamWriter(req.GetRequestStream());
        //    sw.Write(jsonText);
        //    sw.Close();
        //}
        //else
        //{
        /// </summary>
        public void eventNotification(Components.Subscription subscription, Components.LinkSmartEvent request)
        {
            bool notifyResult = false;
            bool notifyResultSpecified = false;            

            lock (this)
            {
                double emVersion = 0;
                double.TryParse(Properties.Settings.Default.EventManagerVersion, NumberStyles.Number, CultureInfo.CreateSpecificCulture("en-US"), out emVersion);
                //double emVersion = 2;
                if (emVersion < 2.0)
                {
                      try
                    {
                        eventSubscriberService.Url = GetEventSubscriberServiceUrl(subscription);
                        eventSubscriberService.Timeout = EventManagerImplementation.SubscriberTimeout; 
                 
                        SubscriberInterface.Part[] parts = CopyPartArray(request.Parts.ToArray());
                        eventSubscriberService.notify(request.Topic, parts, out notifyResult, out notifyResultSpecified);
                        //subscription.Parts = request.Parts.ToArray();
                        //es.storeEvent(subscription);
                        // to many logging. maybe change this to Log4Net in the future.
                        //Log.Debug(("###Event published to: {0}###", (subscription.Endpoint ?? (subscription.HID ?? (subscription.Description))));
                        //eventSubscriberService.notifyAsync(request.topic, parts);
                        subscription.NotifyWasSuccessful();
                    }
                    catch (Exception e)
                    {
                        //Log.Debug((e.Message + e.StackTrace);
                       Log.Error(string.Format("Error: Cannot call SubscriberService. Either the subscriber (" + (subscription.Endpoint ?? (subscription.HID ?? (subscription.Description))) + ") is overloaded, or the subscriber service does not fulfill the notify contract! "));
                        subscription.NotifyFailed();
                        retryQueue.queue(subscription, request); 
                      Log.Debug(string.Format("###Event queued: {0}###", (subscription.Endpoint ?? (subscription.HID ?? (subscription.Description)))));
                    }
                }
                else if (emVersion >= 2.0) {
                    bool? notifyResultNullable = false;
                     try
                    {
                        eventSubscriberService20.Url = GetEventSubscriberServiceUrl(subscription);
                        eventSubscriberService20.Timeout = EventManagerImplementation.SubscriberTimeout;
                
                        SubscriberInterface20.Part[] parts = CopyPart20Array(request.Parts.ToArray());
                        eventSubscriberService20.notify(request.Topic, parts, out notifyResultNullable, out notifyResultSpecified);
                        //subscription.Parts = request.Parts.ToArray();
                        // to many logging. maybe change this to Log4Net in the future.
                        //es.storeEvent(subscription);
                        //Log.Debug(("###Event published to: {0}###", (subscription.Endpoint ?? (subscription.HID ?? (subscription.Description))));
                        //eventSubscriberService.notifyAsync(request.topic, parts);
                        subscription.NotifyWasSuccessful();
                    }
                    catch (Exception e)
                    {
                        Log.Debug(string.Format("Error: Cannot call SubscriberService. Either the subscriber (" + (subscription.Endpoint ?? (subscription.HID ?? (subscription.Description))) + ") is overloaded, or the subscriber service does not fulfill the notify contract! "));
                        subscription.NotifyFailed();
                        retryQueue.queue(subscription, request); 
                       Log.Debug(string.Format("###Event queued: {0}###", (subscription.Endpoint ?? (subscription.HID ?? (subscription.Description)))));
                    }
                }
               
            }
        }

        public void eventNotification(Components.Subscription subscription, string xmlEventString, EventFormat eventFormat)
        {
            bool notifyResult = false;
            bool notifyResultSpecified = false;

            lock (this)
            {
                try
                {
                    eventSubscriberService.Url = GetEventSubscriberServiceUrl(subscription);
                    eventSubscriberService.Timeout = EventManagerImplementation.SubscriberTimeout; ;
             
                    eventSubscriberService.notifyXmlEvent(xmlEventString, out notifyResult, out notifyResultSpecified);
                    //subscription.Parts = request.in1;
                    //es.storeEvent(subscription);
                  Log.Debug(string.Format("###Event published to: {0}###", (subscription.Endpoint ?? (subscription.HID ?? (subscription.Description)))));
                    //eventSubscriberService.notifyXmlEventAsync(xmlEventString);
                    subscription.NotifyWasSuccessful();
                }
                catch {
                    Log.Error("Error: Cannot call SubscriberService. Either the subscriber (" + (subscription.Endpoint ?? (subscription.HID ?? (subscription.Description))) + ") is overloaded, or the subscriber service does not fulfill the notify contract! ");
                    subscription.NotifyFailed();
                    }
            }
        }

        private string GetEventSubscriberServiceUrl(Components.Subscription subscription)
        {
            string eventSubscriberServiceUrl = string.Empty;
            if (!string.IsNullOrEmpty(subscription.Endpoint))
            {
                eventSubscriberServiceUrl = subscription.Endpoint;

            }

            else if (!string.IsNullOrEmpty(subscription.HID))
            {
                eventSubscriberServiceUrl = EventManagerImplementation.GetNetworkManagerLocalEndpointForHid(subscription.HID); // string.Format("http://127.0.0.1:8082/SOAPTunneling/0/{0}/0/", subscription.HID.ToString());
                System.Net.ServicePointManager.Expect100Continue = false;
            }
            else if (!string.IsNullOrEmpty(subscription.Description))
            {

                eventSubscriberServiceUrl = EventManagerImplementation.GetNetworkManagerLocalEndpointForDescription(subscription.Description);
                System.Net.ServicePointManager.Expect100Continue = false;
                if (string.IsNullOrEmpty(eventSubscriberServiceUrl))
                {
                    throw new Exception("Description not found in Network Manager: " + subscription.Description);
                }
                
            }
            else { Log.Debug("Faulty address"); }
            return eventSubscriberServiceUrl;
        }

        private static SubscriberInterface.Part[] CopyPartArray(Components.Part[] partArray)
        {
            SubscriberInterface.Part[] parts = new SubscriberInterface.Part[partArray.Length];
            SubscriberInterface.Part p;
            //SubscriberInterface.Part p1 = new SubscriberInterface.Part();
            //SubscriberInterface.Part p2 = new SubscriberInterface.Part();
            int i = 0;
            foreach (Components.Part a in partArray)//SubscriberInterface.Part p in parts)
            {
                p = new SubscriberInterface.Part();
                p.key = "";
                p.value = "";
                parts[i] = p;
                parts[i].key = partArray[i].key;
                parts[i].value = partArray[i].value;
                i++;
            }
            return parts;
        }

        private static SubscriberInterface20.Part[] CopyPart20Array(Components.Part[] partArray)
        {
            SubscriberInterface20.Part[] parts = new SubscriberInterface20.Part[partArray.Length];
            SubscriberInterface20.Part p;
            //SubscriberInterface.Part p1 = new SubscriberInterface.Part();
            //SubscriberInterface.Part p2 = new SubscriberInterface.Part();
            int i = 0;
            foreach (Components.Part a in partArray)//SubscriberInterface.Part p in parts)
            {
                p = new SubscriberInterface20.Part();
                p.key = "";
                p.value = "";
                parts[i] = p;
                parts[i].key = partArray[i].key;
                parts[i].value = partArray[i].value;
                i++;
            }
            return parts;
        }

       
    }
}