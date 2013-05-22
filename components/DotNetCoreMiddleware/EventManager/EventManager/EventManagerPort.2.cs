// -----------------------------------------------------------------------
// <copyright file="EventManagerPort2.cs" company="">
// TODO: Update copyright text.
// </copyright>
// -----------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

/// <summary>
/// Description-based subscriptions for Event Manager. (The use of partial classes is to make
/// navigation easier and avoid merge conflicts since the classes are so large.)
/// </summary>


public partial interface EventManagerPort
{
    [System.ServiceModel.OperationContractAttribute(Action = "unsubscribeWithDescription", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name = "unsubscribeWithDescriptionReturn")]
    bool unsubscribeWithDescription(string topic, string description);

    [System.ServiceModel.OperationContractAttribute(Action = "subscribeWithDescription", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name = "subscribeWithDescriptionReturn")]
    bool subscribeWithDescription(string topic, string description, int priority);

    [System.ServiceModel.OperationContractAttribute(Action = "clearSubscriptionsWithDescription", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name = "clearSubscriptionsWithDescriptionReturn")]
    bool clearSubscriptionsWithDescription(string description);

}


