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
using System.Runtime.Serialization;
using System.Text.RegularExpressions;
using System.Xml;
using System.Xml.Linq;

namespace Components
{
    [System.CodeDom.Compiler.GeneratedCodeAttribute("svcutil", "4.0.30319.1")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://eventmanager.linksmart.eu")]
    public class Subscription //: IComparable<Subscription>
    {
        /// <summary>
        /// Topic connected to an event (or rather, the filter expression used to match a topic from an event)
        /// </summary>
        [System.Xml.Serialization.XmlIgnore]
        private string topic;
        /// <summary>
        /// Subscriber endpoint
        /// </summary>
        /// <summary>
        /// Topic connected to an event
        /// </summary>
        [System.Xml.Serialization.XmlIgnore]
        private string protocol = "WebService";
        [System.Xml.Serialization.XmlIgnore]
        private string endpoint;
        /// <summary>
        /// Subscriber description (as registered with Network Manager)
        /// </summary>
        [System.Xml.Serialization.XmlIgnore]
        private string description;
        /// <summary>
        /// Subscriber HID
        /// </summary>
        [System.Xml.Serialization.XmlIgnore]
        private string hid;
        /// <summary>
        /// Priority of the event
        /// </summary>
        [System.Xml.Serialization.XmlIgnore]
        private int priority;
        /// <summary>
        /// The filter expression used to match the content in an event
        /// </summary>
        [System.Xml.Serialization.XmlIgnore]
        private Components.Part[] data;
        /// <summary>
        /// Number of retries
        /// </summary>
        [System.Xml.Serialization.XmlIgnore]
        private int numberOfRetries;
        /// <summary>
        /// DateTime for when the event was published (recieved by the EventManager)
        /// </summary>
        [System.Xml.Serialization.XmlIgnore]
        private Nullable<DateTime> dateTime;

  

        //#region IComparable
        ///// <summary>
        ///// Method to make comparison between the subscriptions with respect to priority.
        ///// Uses the CompareTo method within
        ///// </summary>
        //public static Comparison<Subscription> priorityComparison = delegate(Subscription s1, Subscription s2)
        //{
        //    int retries = s1.NumberOfRetries.CompareTo(s2.numberOfRetries);
        //    //switch (retries)
        //    //{
        //    //    case -1:
        //    //        return 1;
        //    //    case 0:
        //    //        return s1.priority.CompareTo(s2.priority);
        //    //    case 1:
        //    //        return -1;
        //    //}
        //    if (retries < 0)
        //            return -1;
        //    else if (retries == 0)
        //            return s1.CompareTo(s2);
        //    else
        //            return 1;

        //    //return s1.CompareTo(s2); 
        //};
        ///// <summary>
        ///// Method to compare subscriptions to one another with respect to priority.
        ///// </summary>
        ///// <param name="other"></param>
        ///// <returns></returns>
        //public int CompareTo(Subscription other)
        //{
        //    return Priority.CompareTo(other.Priority);
        //    //if (NumberOfRetries > other.NumberOfRetries)
        //    //    return 1;
        //    //else if (NumberOfRetries == other.NumberOfRetries)
        //    //    return 0;
        //    //else // if (NumberOfRetries <= other.NumberOfRetries)
        //    //    return -1;
        //    //return Priority.CompareTo(other.Priority);
        //}
        //#endregion

        /// <summary>
        /// Topic connected to an event (the expression used to match an event topic)
        /// </summary>
        [System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = true, Order = 0)]
        public string Topic
        {
            get { return topic; }
            set { topic = value; }
        }
        /// <summary>
        /// Subscribers HID
        /// </summary>
        [System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = true, Order = 1)]
        public string HID
        {
            get { return hid; }
            set { hid = value; }
        }
        /// <summary>
        /// Subscribers endpoint
        /// </summary>
        [System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = true, Order = 2)]
        public string Endpoint
        {
            get { return endpoint; }
            set { endpoint = value; }
        }
        /// <summary>
        /// Subscribers description (as registered with Network Manager))
        /// </summary>
        [System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = true, Order = 3)]
        public string Description
        {
            get { return description; }
            set { description = value; }
        }
        /// <summary>
        /// Priority of the subscription, higher value means higher priority
        /// </summary>
        [System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = false, Order = 4)]
        public int Priority
        {
            get { return priority; }
            set { priority = value; }
        }
        /// <summary>
        /// Part array that contains the content subscription
        /// </summary>
        [System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, IsNullable = true, Order = 5)]
        public Components.Part[] Parts
        {
            get { return data; }
            set { data = value; }
        }
        /// <summary>
        /// Number of retries
        /// </summary>
        //[System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = false, Order = 6)]
        [System.Xml.Serialization.XmlIgnore]
        public int NumberOfRetries
        {
            get { return numberOfRetries; }
            set { numberOfRetries = value; }
        }
        /// <summary>
        /// DateTime for when the EventManager receives the publish call
        /// </summary>
        //[System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = true, Order = 7)]
        [System.Xml.Serialization.XmlIgnore]
        public Nullable<DateTime> @DateTime
        {
            get { return dateTime; }
            set { dateTime = value; }
        }

        /// <summary>
        /// Determines whether the specified published topic is a match to the subscription.
        /// This match uses RegEx.IsMatch(), with the start and end of sting anchors added: 
        /// "^"+subscriptionTopic+"$". A published event "topic12" matches subscriptions to:
        /// "topic12", "topic1.*", but not subscriptions to "topic123" or "opic12".
        /// 
        /// If the regex doesn't match anything, the subscription expression is used as XPath 
        /// and if this results in one or more nodes, IsMatch returns <c>true</c>.
        /// </summary>
        /// <param name="publishedTopic">The published topic.</param>
        /// <returns>
        ///   <c>true</c> if the specified published topic is match; otherwise, <c>false</c>.
        /// </returns>
        public bool IsMatch(string publishedTopic)
        {
            bool isMatch = false;
            try
            {
                isMatch = IsRegexMatch(publishedTopic);
            }
            catch { }
            if (!isMatch)
            {
                try
                {
                    isMatch = IsXpathMatch(publishedTopic);
                    if (!isMatch)
                    {
                        try
                        {
                            isMatch = IsMatchToTopicStringInXmlEvent(publishedTopic);
                        }
                        catch (Exception e)
                        {
                        }
                    }
                }
                catch (Exception e)
                {
                }
            }
            return isMatch;
        }

      
        private bool IsRegexMatch(string publishedTopic)
        {
            bool isMatch = false;
            Regex regex = new Regex("^" + this.topic + "$", RegexOptions.None);
            isMatch = regex.IsMatch(publishedTopic);
            return isMatch;
        }

        private bool IsXpathMatch(string publishedTopic)
        {
            bool isMatch = false;
            // We assume that the published topic is a valid XML document.
            XmlDocument xDoc = new XmlDocument();
            var nsmgr = new XmlNamespaceManager(xDoc.NameTable);
            nsmgr.AddNamespace("", @"urn:linksmart/eventmodel/1.0");
            nsmgr.AddNamespace("linksmart",@"urn:linksmart/typelibrary/1.0");
            nsmgr.AddNamespace("seempubs-em",@"urn:seempubs/eventmodel/1.0");
            xDoc.LoadXml(publishedTopic);
            XmlNode node = xDoc.SelectSingleNode(this.topic, nsmgr);
            isMatch = null != node;
            return isMatch;
        }

        private bool IsMatchToTopicStringInXmlEvent(string publishedTopic)
        {
            bool isMatch = false;
            // We assume that the published topic is a valid XML document.
            XmlDocument xDoc = new XmlDocument();
            var nsmgr = new XmlNamespaceManager(xDoc.NameTable);
            nsmgr.AddNamespace("", @"urn:linksmart/eventmodel/1.0");
            nsmgr.AddNamespace("linksmart", @"urn:linksmart/typelibrary/1.0");
            nsmgr.AddNamespace("seempubs-em", @"urn:seempubs/eventmodel/1.0");
            xDoc.LoadXml(publishedTopic);
            XmlNode node = xDoc.SelectSingleNode("//*[local-name()='TopicString']", nsmgr);
            if (null != node && node.NodeType.Equals(XmlNodeType.Element))
            {
                string topicString = node.InnerText;
                isMatch = IsRegexMatch(topicString);
            }
            isMatch = null != node;
            return isMatch;
        }

        /// <summary>
        /// Determines whether [is content match] [the specified attributes]. I.e., if all the key-value pairs in the attributes match the content in the event.
        /// </summary>
        /// <param name="attributes">The attributes.</param>
        /// <returns>
        ///   <c>true</c> if [is content match] [the specified attributes]; otherwise, <c>false</c>.
        /// </returns>
        public bool IsContentMatch(Components.Part[] eventContent)
        {
            bool isMatch = true;
            if (null != this.Parts&&this.Parts.Length>0)
            {
                foreach (var p in this.Parts)
                {
                    isMatch = isMatch && eventContent.Where(x => x.key.Equals(p.key) && x.value.Equals(p.value)).Any();
                }
            }
            else
            {
                isMatch = false;
            }
            return isMatch;
        }
        

        /// <summary>
        /// Method to create an object. Can be used as an element in a list
        /// </summary>
        public Subscription(string topic, string hid, string endpoint, string description, int priority, Components.Part[] data, int numberOfRetries, Nullable<DateTime> dateTime)
        {
            this.topic = topic;
            this.hid = hid;
            this.endpoint = endpoint;
            this.description = description;
            this.priority = priority;
            this.data = data;
            this.numberOfRetries = numberOfRetries;
            this.dateTime = dateTime;
        }

        /// <summary>
        /// Method to create an object. Can be used as an element in a list
        /// </summary>
        public Subscription(string topic, string hid, string endpoint, string description, int priority, Components.Part[] data, int numberOfRetries, Nullable<DateTime> dateTime, string protocol)
        {
            this.topic = topic;
            this.hid = hid;
            this.endpoint = endpoint;
            this.description = description;
            this.priority = priority;
            this.data = data;
            this.numberOfRetries = numberOfRetries;
            this.dateTime = dateTime;
            this.protocol = protocol;
        }

        public Subscription()
        {
            this.topic = String.Empty;
            this.hid = String.Empty;
            this.endpoint = String.Empty;
            this.description = String.Empty;
            this.priority = int.MaxValue;
            this.data = null;
            this.numberOfRetries = 0;
            this.dateTime = null;
        }

        public void NotifyWasSuccessful()
        {
            this.LastSuccessfulNotifyCall = System.DateTime.Now;
            this.NumberOfRetries = 0;
            this.NumberOfFailedNotifyCalls = 0;
        }

        [System.Xml.Serialization.XmlIgnore]
        private System.DateTime lastSuccessfulNotifyCall = System.DateTime.MinValue;

        [System.Xml.Serialization.XmlIgnore]
        public System.DateTime LastSuccessfulNotifyCall { get { return lastSuccessfulNotifyCall; } set { lastSuccessfulNotifyCall = value; } }

        [System.Xml.Serialization.XmlIgnore]
        private int numberOfFailedNotifyCalls = 0;

        [System.Xml.Serialization.XmlIgnore]
        public int NumberOfFailedNotifyCalls { get { return numberOfFailedNotifyCalls; } set { numberOfFailedNotifyCalls = value; } }

        public void NotifyFailed()
        {
            NumberOfFailedNotifyCalls++;
        }

        public void AddFailedEvent(LinkSmartEvent failedEvent)
        {
            if (this.FailedEvents.Count < 10)
            {
                if (!FailedEvents.Where(x => x.InternalId.Equals(failedEvent.InternalId)).Any())
                {
                    FailedEvents.Add(failedEvent);
                }
            } else {
                this.FailedEvents = new List<LinkSmartEvent>();
                Console.WriteLine("Subscription [{0}   |   {1}   |   {2}   |   {3}] has more than 100 failed events, resetting resend list.", this.Topic, this.Description, this.Endpoint, this.HID);
            }
        }

        [System.Xml.Serialization.XmlIgnore]
        public List<LinkSmartEvent> FailedEvents = new List<LinkSmartEvent>();

        public bool IsSameAs(Subscription otherSubscription) {
            if (null==this.Parts) { this.Parts= new Part[0];}
            if (null==otherSubscription.Parts) { otherSubscription.Parts= new Part[0];}
            bool isTheSame = 
                this.topic.Equals(otherSubscription.topic);
            isTheSame = isTheSame && (this.Parts.Length == otherSubscription.Parts.Length);
            foreach (var p in this.Parts) {
                isTheSame = isTheSame && otherSubscription.Parts.Where(x => x.key.Equals(p.key) && x.value.Equals(p.value)).Any();
            }
            isTheSame = isTheSame && this.Description.Equals(otherSubscription.Description) && this.Endpoint.Equals(otherSubscription.Endpoint) && this.HID.Equals(otherSubscription.HID);
            return isTheSame;
        }


        public bool HasContentFilter()
        {
            return (null != this.Parts && this.Parts.Length > 0);
        }
    }
}
