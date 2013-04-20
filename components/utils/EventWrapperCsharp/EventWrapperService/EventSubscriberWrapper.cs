using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;
using System.ServiceModel.Description;
using eu.linksmart.eventing.NetworkManagerStub;
using eu.linksmart.eventing.EventManagerStub;
using System.Threading;
using eu.linksmart.eventing.util;
using log4net;

namespace eu.linksmart.eventing
{
    public class EventSubscriberWrapper : EventSubscriber
    {
        public delegate Boolean NotifyDelegate(String topic, eu.linksmart.eventing.EventManagerStub.Part[] parts);
        private static List<Subscription> successfulSubscriptions = new List<Subscription>();
        private string className;
        ILog Log;
        private class Subscription
        {
            public EventManagerImplementation EventManager;
            public String EventManagerDesc;
            public String Topic;
            public NotifyDelegate Callbacks;

            public Subscription(EventManagerImplementation eventManager, string eventManagerDesc, string topicName, NotifyDelegate callback)
            {
                EventManager = eventManager;
                Topic = topicName;
                Callbacks = callback;
                EventManagerDesc = eventManagerDesc;
            }
        }

      

        

        public string StrVirtualAddress
        {
            get;
            set;
        }

        public string EventSubscriberDescription
        {
            get;
            set;
        }

        public string EventSubscriberAddress{
            get;
            set;
        }

        /// <summary>
        /// Initialize SubscriberWrapper with default :
        ///    classId : "EventSubscriber" + (your machine name)
        ///    EventSubscriber url : "http://localhost:8122/EventSubscriber" + (your machine name)
        /// </summary>
        public EventSubscriberWrapper()
            : this("EventSubscriber" + System.Environment.MachineName, 
                "http://localhost:8122/EventSubscriber" + System.Environment.MachineName)
       { 
         
       }

        /// <summary>
        /// SubscriberWrapper
        /// </summary>
        /// <param name="className">className to subscribe to the event manager</param>
       /// <param name="subscriberUrl">Url of the subscriber service </param>
       public EventSubscriberWrapper(string className, string subscriberUrl)          
       {
           log4net.Config.XmlConfigurator.Configure();
           Log = LogManager.GetLogger(GetType().Name);
           EventSubscriberAddress = subscriberUrl;
           this.className = className;
       }


        /// <summary>
        /// run the subscriber service on a Self-hosted WCF service
        /// </summary>
        /// <returns>Service Host where the Subscriber Service is hosted</returns>
        public ServiceHost StartService()
        {
            string backbone = "eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl";
            Uri[] BaseAddresses = new Uri[] { new Uri(EventSubscriberAddress) };
            //Turn off 100-continue
            System.Net.ServicePointManager.Expect100Continue = false;
            //Create the even subscriber
            ServiceHost serviceHost = new ServiceHost(typeof(EventSubscriberWrapper), BaseAddresses);
            try
            {
                ServiceMetadataBehavior smb;
                if ((smb = serviceHost.Description.Behaviors.Find<ServiceMetadataBehavior>()) == null)
                {
                    smb = new ServiceMetadataBehavior();
                    smb.HttpGetEnabled = true;
                    smb.HttpGetUrl = new Uri(EventSubscriberAddress + "/meta");
                    serviceHost.Description.Behaviors.Add(smb);
                }
                BasicHttpBinding myBinding = new BasicHttpBinding(BasicHttpSecurityMode.None);
                myBinding.Namespace = "http://eventmanager.linksmart.eu";
                serviceHost.AddServiceEndpoint(typeof(IMetadataExchange), MetadataExchangeBindings.CreateMexHttpBinding(), EventSubscriberAddress + "/mex");
                serviceHost.AddServiceEndpoint(typeof(EventSubscriber), myBinding, "");
                serviceHost.Open();

                Thread createHidThread = new Thread(delegate()
                {
                    NetworkManager nm = new NetworkManager();
                    StrVirtualAddress = "";
                    int retry = 0;
                    do
                    {
                        EventSubscriberDescription = "EventSubscriber:" + className + ":" + System.Environment.MachineName;
                        NetworkManagerStub.Part p = new NetworkManagerStub.Part();
                        p.key = "DESCRIPTION";
                        p.value = EventSubscriberDescription;
                        Registration regist = nm.registerService(new NetworkManagerStub.Part[] { p }, EventSubscriberAddress, backbone);
                        StrVirtualAddress = regist.virtualAddressAsString;
                        retry++;
                        if (StrVirtualAddress.Equals("")) 
                            Log.Debug("re-trying to register service :  EventSubscriber:" + System.Environment.MachineName + 
                                " in " + Properties.Settings.Default.RetryDelay + "ms  (" + retry + "x)");
                        else 
                            Log.Info("EventSubscriber Service Registered with VirtualAddress : " + StrVirtualAddress);
                        Thread.Sleep(Properties.Settings.Default.RetryDelay);
                    } while (StrVirtualAddress == "");
      
                });
                createHidThread.Start();
            }
            catch (Exception e) { Log.Debug(e.Message); }
            return serviceHost;
        }

      
        /// <summary>
        /// SubscribeEvent to an EventManager
        /// </summary>
        /// <param name="eventManagerDesc">DESCRIPTION of the event manager (see http://localhost:8082/LinkSmartStatus)</param>
        /// <param name="eventTopic">event topic to subscribe</param>
        /// <param name="callback">a callback functions where the events are forwarded to </param>
        public void SubscribeEvent(string eventManagerDesc, string eventTopic, NotifyDelegate callback)
        {
            Thread subscribeEventThread = new Thread(delegate()
            {
                // try to find the event manager
                int retry = 0;
                String emUrl = "";
                do
                {
                    emUrl = NetworkManagerUtil.GetLinksmartUrlFromDesc(Properties.Settings.Default.NetworkManagerStubUrl, eventManagerDesc);
                    retry++;
                    if (emUrl==null) 
                        Log.Debug("re-trying to get the event manager address in " 
                            + Properties.Settings.Default.RetryDelay + " ms ....(" + retry + " x)");
                    else 
                        Log.Info("EventManager found at " + emUrl);
                    Thread.Sleep(Properties.Settings.Default.RetryDelay);
                } while (emUrl == null);
                EventManagerImplementation eventManager = new EventManagerImplementation();
                eventManager.Url = emUrl;
                Subscription s = new Subscription(eventManager, eventManagerDesc, eventTopic, callback);
                
                // now try to subscribe    
                retry = 0;
                bool subscribeResult = false;
                do{
                    if (EventSubscriberDescription != null)
                    {
                        if (!(subscribeResult = s.EventManager.subscribeWithDescription(s.Topic, EventSubscriberDescription, 0)))
                        {
                            retry++;
                            if (!subscribeResult)
                                Log.Debug("re-trying to subscribe to topic "
                                    + eventTopic + " in " + Properties.Settings.Default.RetryDelay + "ms ....(" + retry + " x)");
                            else Log.Info("topic  : " + eventTopic + " subscribed");                           
                        }
                    }
                    Thread.Sleep(Properties.Settings.Default.RetryDelay);
                } while (!subscribeResult || EventSubscriberDescription==null);

                lock (successfulSubscriptions)
                {
                    successfulSubscriptions.Add(s);
                }
            });

            subscribeEventThread.Start();
          
        }

        /// <summary>
        /// Unsubscribe All Events
        /// </summary>
        public void UnsubscribeAllEvents()
        {
            foreach (Subscription s in successfulSubscriptions)
            {
                s.EventManager.unsubscribe(s.Topic, StrVirtualAddress);                
            }
            
        }


        /// <summary>
        /// Unsubscribe particular topic from an EventManager
        /// </summary>
        public void UnsubscribeTopic(string eventManagerDesc, string topic)
        {
            foreach (Subscription s in successfulSubscriptions){
               if(s.EventManagerDesc.Equals(eventManagerDesc)) s.EventManager.unsubscribeWithDescription(s.Topic, this.EventSubscriberDescription);
            }
        }

        /// <summary>
        /// NOT YET IMPLEMENTED
        /// </summary>
        /// <param name="arg0"></param>
        /// <returns></returns>
        bool? EventSubscriber.notifyXmlEvent(string arg0)
        {
            throw new NotImplementedException();
        }


        bool? EventSubscriber.notify(string topic, EventManagerStub.Part[] parts)
        {
            foreach (Subscription s in successfulSubscriptions)
            {
                if (s.Topic.Equals(topic)) s.Callbacks(topic, parts);
            }
            return true;
        }

    }
}
