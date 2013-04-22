// -----------------------------------------------------------------------
// <copyright file="EventManagerPort3.cs" company="">
// TODO: Update copyright text.
// </copyright>
// -----------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

/// <summary>
/// XML-based subscriptions for Event Manager. (The use of partial classes is to make
/// navigation easier and avoid merge conflicts since the classes are so large.)
/// </summary>


public partial interface EventManagerPort
{

    /// <summary>
    /// Publishes an event in XML format. This only handles the default namespaces: 
    /// 
    /// xmlns="urn:linksmart/eventmodel/1.0"
    /// xmlns:linksmart="urn:linksmart/typelibrary/1.0"
    /// 
    /// so if the event XML contains other topic namespaces, something like 
    /// //*[local-name()='ProxyID'][.='PlugwiseProxy:Polito:Secretariat2']
    /// will have to be used.
    /// </summary>
    /// <param name="xmlTopicMatchingExpression">The XML topic matching expression. XPath 
    /// </param>
    /// <returns></returns>
    // CODEGEN: Parameter 'in1' requires additional schema information that cannot be captured using the parameter mode. The specific attribute is 'System.Xml.Serialization.XmlArrayAttribute'.
    [System.ServiceModel.OperationContractAttribute(Action = "publishXmlEvent", ReplyAction = "")]
    [System.ServiceModel.XmlSerializerFormatAttribute()]
    [return: System.ServiceModel.MessageParameterAttribute(Name = "publishReturn")]
    bool publishXmlEvent(string eventXmlString);

}


