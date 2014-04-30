using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;

namespace EventManager20Interface
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the interface name "IService1" in both code and config file together.
    [ServiceContract(Namespace = "http://eventmanager.linksmart.eu")]
    public interface IEventManager20
    {
        [OperationContract]
        [XmlSerializerFormat]
        void AddSubscription(Components.Subscription subscription);
        [OperationContract]
        [XmlSerializerFormat]
        void RemoveSubscription(Components.Subscription subscription);
    }

   
    
}
