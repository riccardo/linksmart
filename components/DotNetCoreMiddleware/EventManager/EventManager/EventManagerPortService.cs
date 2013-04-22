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
﻿//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:4.0.30319.239
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------



using System;
[System.CodeDom.Compiler.GeneratedCodeAttribute("System.ServiceModel", "4.0.0.0")]
[System.ServiceModel.ServiceContractAttribute(Namespace="http://eventmanager.linksmart.eu", ConfigurationName="EventManagerPort")]
public partial interface EventManagerPort
{

    [System.ServiceModel.OperationContractAttribute(Action = "subscribe", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name="subscribeReturn")]
    bool subscribe(string topic, string endpoint, int priority);

    [System.ServiceModel.OperationContractAttribute(Action = "unsubscribe", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name="unsubscribeReturn")]
    bool unsubscribe(string topic, string endpoint);

    [System.ServiceModel.OperationContractAttribute(Action = "subscribeWithHID", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name="subscribeWithHIDReturn")]
    bool subscribeWithHID(string topic, string hid, int priority);

    [System.ServiceModel.OperationContractAttribute(Action = "unsubscribeWithHID", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name="unsubscribeWithHIDReturn")]
    bool unsubscribeWithHID(string topic, string hid);
    
    // CODEGEN: Parameter 'getSubscriptionsReturn' requires additional schema information that cannot be captured using the parameter mode. The specific attribute is 'System.Xml.Serialization.XmlArrayAttribute'.
    [System.ServiceModel.OperationContractAttribute(Action = "getSubscriptions", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name="getSubscriptionsReturn")]
    getSubscriptionsResponse getSubscriptions(getSubscriptionsRequest request);

    [System.ServiceModel.OperationContractAttribute(Action = "clearSubscriptions", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name="clearSubscriptionsReturn")]
    bool clearSubscriptions(string endpoint);

    [System.ServiceModel.OperationContractAttribute(Action = "clearSubscriptionsWithHID", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name="clearSubscriptionsWithHIDReturn")]
    bool clearSubscriptionsWithHID(string hid);

    [System.ServiceModel.OperationContractAttribute(Action = "setPriority", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name="setPriorityReturn")]
    bool setPriority(string in0, int in1);

    [System.ServiceModel.OperationContractAttribute(Action = "triggerRetryQueue", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name = "triggerRetryQueueReturn")]
    bool triggerRetryQueue();
    
    // CODEGEN: Parameter 'in1' requires additional schema information that cannot be captured using the parameter mode. The specific attribute is 'System.Xml.Serialization.XmlArrayAttribute'.
    [System.ServiceModel.OperationContractAttribute(Action = "publish", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name="publishReturn")]
    publishResponse publish(publishRequest request);
}

///// <remarks/>
//[System.CodeDom.Compiler.GeneratedCodeAttribute("svcutil", "4.0.30319.1")]
//[System.SerializableAttribute()]
//[System.Diagnostics.DebuggerStepThroughAttribute()]
//[System.ComponentModel.DesignerCategoryAttribute("code")]
//[System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://eventmanager.linksmart.eu")]
//public class Subscription
//{
  
    //private string hIDField;

    //private string uRLField;

    //private System.Nullable<System.DateTime> dateField;

    //private string topicField;

    ///// <remarks/>
    //[System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = true, Order = 0)]
    //public string HID
    //{
    //    get
    //    {
    //        return this.hIDField;
    //    }
    //    set
    //    {
    //        this.hIDField = value;
    //    }
    //}

    ///// <remarks/>
    //[System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = true, Order = 1)]
    //public string URL
    //{
    //    get
    //    {
    //        return this.uRLField;
    //    }
    //    set
    //    {
    //        this.uRLField = value;
    //    }
    //}

    ///// <remarks/>
    //[System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = true, Order = 2)]
    //public System.Nullable<System.DateTime> date
    //{
    //    get
    //    {
    //        return this.dateField;
    //    }
    //    set
    //    {
    //        this.dateField = value;
    //    }
    //}

    ///// <remarks/>
    //[System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = true, Order = 3)]
    //public string topic
    //{
    //    get
    //    {
    //        return this.topicField;
    //    }
    //    set
    //    {
    //        this.topicField = value;
    //    }
    //}
//}

/////// <remarks/>
//[System.CodeDom.Compiler.GeneratedCodeAttribute("svcutil", "4.0.30319.1")]
//[System.SerializableAttribute()]
//[System.Diagnostics.DebuggerStepThroughAttribute()]
//[System.ComponentModel.DesignerCategoryAttribute("code")]
//[System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://eventmanager.linksmart.eu")]
//public partial class Part
//{

//    private string keyField;

//    private string valueField;

//    /// <remarks/>
//    [System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = true, Order = 0)]
//    public string key
//    {
//        get
//        {
//            return this.keyField;
//        }
//        set
//        {
//            this.keyField = value;
//        }
//    }

//    /// <remarks/>
//    [System.Xml.Serialization.XmlElementAttribute(Form = System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable = true, Order = 1)]
//    public string value
//    {
//        get
//        {
//            return this.valueField;
//        }
//        set
//        {
//            this.valueField = value;
//        }
//    }
//}

[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.CodeDom.Compiler.GeneratedCodeAttribute("System.ServiceModel", "4.0.0.0")]
[System.ComponentModel.EditorBrowsableAttribute(System.ComponentModel.EditorBrowsableState.Advanced)]
[System.ServiceModel.MessageContractAttribute(WrapperName="getSubscriptions", WrapperNamespace="http://eventmanager.linksmart.eu", IsWrapped=true)]
public partial class getSubscriptionsRequest
{
    
    public getSubscriptionsRequest()
    {
    }
}

[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.CodeDom.Compiler.GeneratedCodeAttribute("System.ServiceModel", "4.0.0.0")]
[System.ComponentModel.EditorBrowsableAttribute(System.ComponentModel.EditorBrowsableState.Advanced)]
[System.ServiceModel.MessageContractAttribute(WrapperName="getSubscriptionsResponse", WrapperNamespace="http://eventmanager.linksmart.eu", IsWrapped=true)]
public partial class getSubscriptionsResponse
{
    
    [System.ServiceModel.MessageBodyMemberAttribute(Namespace="", Order=0)]
    [System.Xml.Serialization.XmlArrayAttribute()]
    [System.Xml.Serialization.XmlArrayItemAttribute("item", Form=System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable=false)]
    public Components.Subscription[] getSubscriptionsReturn;
    
    public getSubscriptionsResponse()
    {
    }
    
    public getSubscriptionsResponse(Components.Subscription[] getSubscriptionsReturn)
    {
        this.getSubscriptionsReturn = getSubscriptionsReturn;
    }
}

[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.CodeDom.Compiler.GeneratedCodeAttribute("System.ServiceModel", "4.0.0.0")]
[System.ComponentModel.EditorBrowsableAttribute(System.ComponentModel.EditorBrowsableState.Advanced)]
[System.ServiceModel.MessageContractAttribute(WrapperName="publish", WrapperNamespace="http://eventmanager.linksmart.eu", IsWrapped=true)]
public partial class publishRequest
{   
    [System.ServiceModel.MessageBodyMemberAttribute(Namespace="", Order=0)]
    public string topic;
    
    [System.ServiceModel.MessageBodyMemberAttribute(Namespace="", Order=1)]
    [System.Xml.Serialization.XmlArrayAttribute()]
    [System.Xml.Serialization.XmlArrayItemAttribute("item", Form=System.Xml.Schema.XmlSchemaForm.Unqualified, IsNullable=false)]
    public Components.Part[] in1;
    
    public publishRequest()
    {
    }
    
    public publishRequest(string topic, Components.Part[] in1)
    {
        this.topic = topic;
        this.in1 = in1;
    }
}

[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.CodeDom.Compiler.GeneratedCodeAttribute("System.ServiceModel", "4.0.0.0")]
[System.ComponentModel.EditorBrowsableAttribute(System.ComponentModel.EditorBrowsableState.Advanced)]
[System.ServiceModel.MessageContractAttribute(WrapperName="publishResponse", WrapperNamespace="http://eventmanager.linksmart.eu", IsWrapped=true)]
public partial class publishResponse
{
    
    [System.ServiceModel.MessageBodyMemberAttribute(Namespace="", Order=0)]
    public bool publishReturn;
    
    public publishResponse()
    {
    }
    
    public publishResponse(bool publishReturn)
    {
        this.publishReturn = publishReturn;
    }
}

[System.CodeDom.Compiler.GeneratedCodeAttribute("System.ServiceModel", "4.0.0.0")]
public interface EventManagerPortChannel : EventManagerPort, System.ServiceModel.IClientChannel
{
}

[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.CodeDom.Compiler.GeneratedCodeAttribute("System.ServiceModel", "4.0.0.0")]
public partial class EventManagerPortClient : System.ServiceModel.ClientBase<EventManagerPort>, EventManagerPort
{
    
    public EventManagerPortClient()
    {
    }
    
    public EventManagerPortClient(string endpointConfigurationName) : 
            base(endpointConfigurationName)
    {
    }
    
    public EventManagerPortClient(string endpointConfigurationName, string remoteAddress) : 
            base(endpointConfigurationName, remoteAddress)
    {
    }
    
    public EventManagerPortClient(string endpointConfigurationName, System.ServiceModel.EndpointAddress remoteAddress) : 
            base(endpointConfigurationName, remoteAddress)
    {
    }
    
    public EventManagerPortClient(System.ServiceModel.Channels.Binding binding, System.ServiceModel.EndpointAddress remoteAddress) : 
            base(binding, remoteAddress)
    {
    }
    
    public bool subscribe(string topic, string endpoint, int priority)
    {
        return base.Channel.subscribe(topic, endpoint, priority);
    }
    
    public bool unsubscribe(string topic, string endpoint)
    {
        return base.Channel.unsubscribe(topic, endpoint);
    }
    
    public bool subscribeWithHID(string topic, string hid, int priority)
    {
        return base.Channel.subscribeWithHID(topic, hid, priority);
    }
    
    public bool unsubscribeWithHID(string topic, string hid)
    {
        return base.Channel.unsubscribeWithHID(topic, hid);
    }


    public bool subscribeWithDescription(string topic, string description, int priority)
    {
        return base.Channel.subscribeWithDescription(topic, description, priority);
    }

    public bool unsubscribeWithDescription(string topic, string description)
    {
        return base.Channel.unsubscribeWithDescription(topic, description);
    }

    [System.ComponentModel.EditorBrowsableAttribute(System.ComponentModel.EditorBrowsableState.Advanced)]
    getSubscriptionsResponse EventManagerPort.getSubscriptions(getSubscriptionsRequest request)
    {
        return base.Channel.getSubscriptions(request);
    }
    
    public Components.Subscription[] getSubscriptions()
    {
        getSubscriptionsRequest inValue = new getSubscriptionsRequest();
        getSubscriptionsResponse retVal = ((EventManagerPort)(this)).getSubscriptions(inValue);
        return retVal.getSubscriptionsReturn;
    }
    
    public bool clearSubscriptions(string endpoint)
    {
        return base.Channel.clearSubscriptions(endpoint);
    }
    
    public bool clearSubscriptionsWithHID(string hid)
    {
        return base.Channel.clearSubscriptionsWithHID(hid);
    }

    public bool clearSubscriptionsWithDescription(string description)
    {
        return base.Channel.clearSubscriptionsWithDescription(description);
    }
    
    public bool setPriority(string topic, int priority)
    {
        return base.Channel.setPriority(topic, priority);
    }
    public bool triggerRetryQueue()
    {
        return base.Channel.triggerRetryQueue();
    }

    [System.ComponentModel.EditorBrowsableAttribute(System.ComponentModel.EditorBrowsableState.Advanced)]
    publishResponse EventManagerPort.publish(publishRequest request)
    {
        return base.Channel.publish(request);
    }
    
    public bool publish(string topic, Components.Part[] in1)
    {
        publishRequest inValue = new publishRequest();
        inValue.topic = topic;
        inValue.in1 = in1;
        publishResponse retVal = ((EventManagerPort)(this)).publish(inValue);
        return retVal.publishReturn;
    }

    public bool publishXmlEvent(string xmlEventString)
    {

        return base.Channel.publishXmlEvent(xmlEventString);
    }
}