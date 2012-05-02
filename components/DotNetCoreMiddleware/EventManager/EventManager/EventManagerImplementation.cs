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
using System.Text.RegularExpressions;
using System.Threading;
using System.Web.Services;
using System.ServiceModel;

namespace EventManager
{
    [ServiceBehavior(Name = "EventManagerImplementation", Namespace = "http://eventmanager.linksmart.eu", IncludeExceptionDetailInFaults=true)]
    class EventManagerImplementation : EventManagerPort
    {
        /// <summary>
        /// Instance of the SubscriberInterface web service
        /// </summary>
        SubscriberInterface.EventSubscriberService eventSubscriberService = new SubscriberInterface.EventSubscriberService();
        /// <summary>
        /// Failed notifications is added to a queue in order to be called later on. This queue is ordered according to priority
        /// </summary>
        RetryQueue retryQueue = new RetryQueue();

        /// <summary>
        /// Instance of Operations.cs
        /// </summary>
        Operations operationsClass = new Operations();
        /// <summary>
        /// Object to lock upon
        /// </summary>
        public readonly object m_Lock = new object();

        /// <summary>
        /// Start subscribing to a topic from an endpoint. The topic is a regular expression that is 
        /// matched to published events using RegEx.IsMatch(topic+"$",RegexOptions.None).
        /// </summary>
        public bool subscribe(string topic, string endpoint, int priority)
        {
            try
            {
                if (Program.subscriptionList.Exists(f => (f.Endpoint != null && f.Endpoint.Equals(endpoint) == true && f.Topic.Equals(topic) == true)))
                { Console.WriteLine("Subscription already exists"); }
                else
                {
                    Components.Subscription subscription = new Components.Subscription(topic, null, endpoint, priority, null, 0, null);
                    Subscribe subscribeClass = new Subscribe(subscription);
                    Thread subscribeThread = new Thread(new ThreadStart(subscribeClass.subscribe));
                    subscribeThread.Start();
                    subscribeThread.Join();
                }
                return true;
            }
            catch { return false; }
        }

        /// <summary>
        /// Start subscribing to a topic from an HID. The topic is a regular expression that is 
        /// matched to published events using RegEx.IsMatch(topic+"$",RegexOptions.None).
        /// </summary>
        public bool unsubscribe(string topic, string endpoint)
        {
            try
            {
                Program.subscriptionList.RemoveAll(f => (f.Endpoint.Equals(endpoint) && f.Topic.Equals(topic)));
                Console.WriteLine("Unsubscribe:\nTopic: {0}\nEndpoint: {1}", topic, endpoint);
                return true;
            }
            catch { return false; }

            #region previous code
            //try
            //{
            //    int exists = Program.subscriptionList.FindIndex(f => f.Endpoint.Equals(endpoint) == true);
            //    if (exists >= 0)
            //    {
            //        if (Program.subscriptionList[exists].Topic.Equals(topic))
            //        {
            //            Program.subscriptionList.RemoveAt(exists);
            //        }
            //        else
            //            Console.WriteLine("Subscription does not exists");
            //    }
            //    else { }
            //    return true;
            //}
            //catch { return false; }
            #endregion
        }

        /// <summary>
        /// Start subscribing to a topic from an HID
        /// </summary>
        public bool subscribeWithHID(string topic, string hid, int priority)
        {
            try
            {
                if (Program.subscriptionList.Exists(f => (f.HID != null && f.HID.Equals(hid) == true && f.Topic.Equals(topic) == true)))
                { Console.WriteLine("Subscription already exists"); }
                else
                {
                    Components.Subscription subscription = new Components.Subscription(topic, hid, null, priority, null, 0, null);
                    Subscribe subscribeClass = new Subscribe(subscription);
                    Thread subscribeThread = new Thread(new ThreadStart(subscribeClass.subscribe));
                    subscribeThread.Start();
                    subscribeThread.Join();
                }
                return true;
            }
            catch { return false; }
        }

        /// <summary>
        /// Unsubscribe to a topic from an HID
        /// </summary>
        public bool unsubscribeWithHID(string topic, string hid)
        {
            try
            {
                Program.subscriptionList.RemoveAll(f => (f.HID.Equals(hid) && f.Topic.Equals(topic)));
                Console.WriteLine("Unsubscribe:\nTopic: {0}\nHID: {1}", topic, hid);
                return true;
            }
            catch { return false; }
        }

        /// <summary>
        /// List all subscriptions that is active
        /// </summary>
        public getSubscriptionsResponse getSubscriptions(getSubscriptionsRequest request) { throw new NotImplementedException(); }

        /// <summary>
        /// Clear all subscriptions from a given endpoint
        /// </summary>
        /// <param name="endpoint">Endpoint to clear</param>
        public bool clearSubscriptions(string endpoint)
        {
            try
            {
                Program.subscriptionList.RemoveAll(f => (f.Endpoint.Equals(endpoint)));
                Console.WriteLine("Subscription cleared:\nEndpoint: {0}", endpoint);
                return true;
            }
            catch { return false; }
        }

        /// <summary>
        /// Clear all subscriptions from a given HID
        /// </summary>
        /// <param name="hid">HID to clear</param>
        public bool clearSubscriptionsWithHID(string hid)
        {
            try
            {
                Program.subscriptionList.RemoveAll(f => (f.HID.Equals(hid)));
                Console.WriteLine("Subscription cleared:\nHID: {0}", hid);
                return true;
            }
            catch { return false; }
        }

        /// <summary>
        /// Method for declaring the level of priority the different events/topics has
        /// </summary>
        public bool setPriority(string topic, int priority)
        {
            try
            {
                foreach (Components.Subscription subscription in Program.subscriptionList.Where(f => f.Topic.Equals(topic) == true))
                { subscription.Priority = priority; }
                //callRetryQueue();
                return true;
            }
            catch { return false; }
        }

        public bool triggerRetryQueue()
        {
            callRetryQueue();
            return true;
        }

        /// <summary>
        /// Send the published event to all subscribers to the specified topic.
        /// The matching of published topic to subscriptions is done with Subscription.IsMatch(string topic),
        /// which uses RexEx to match the subscription topic to published events. 
        /// </summary>
        /// <param name="request">Request that contains topic and values regarding the event</param>
        public publishResponse publish(publishRequest request)
        {   //Make a copy of the list in order to avoid it being changed during the forea.ch loop. As long as copy is used, foreach can stay here. Otherwise -> Notification.cs
            foreach (Components.Subscription subscription in Program.subscriptionList.Where(f => f.IsMatch(request.topic)).ToList())
            {
                subscription.NumberOfRetries = 0;
                if (subscription.DateTime != null) { }
                else { subscription.DateTime = DateTime.Now; }
                Notification notification = new Notification(subscription, request);
                Thread notificationThread = new Thread(new ThreadStart(notification.notify));
                notificationThread.Start();
                notificationThread.Join();
            }

            publishResponse p = new publishResponse();
            p.publishReturn = true;
            return p;
        }

        /// <summary>
        /// Method for calling the RetryQueue. Failed notifications will be repeated
        /// </summary>
        public void callRetryQueue()
        {
            Notification notification;
            while (true)
            {
                Components.Subscription subscription = retryQueue.dequeue();
                if (subscription != null)
                {
                    publishRequest request = new publishRequest();
                    request.topic = subscription.Topic;
                    request.in1 = subscription.Parts;
                    notification = new Notification(subscription, request);
                    Thread retryThread = new Thread(new ThreadStart(notification.notify));
                    retryThread.Start();
                    retryThread.Join();
                }
                else
                    break;
            }
        }
    }
}