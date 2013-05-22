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
using eu.linksmart.eventing.EventManagerStub;
using log4net;

namespace eu.linksmart.eventing
{
    public class EventSubscriberWrapper : EventSubscriber
    {
        public delegate Boolean NotifyDelegate(String topic, eu.linksmart.eventing.EventManagerStub.Part[] parts);
        private static List<Subscription> successfulSubscriptions = new List<Subscription>();        
        ILog Log = LogManager.GetLogger(typeof(EventSubscriberWrapper));
        public String subscriberDesc = "EventSubscriber:" + System.Environment.MachineName;
        const int retry_delay = 1000;   

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

        public string EventSubscriberHid
        {
            get;
            set;
        }

        public string EventSubscriberDescription
        {
            get;
            set;
        }

        public string EventSubscriberUrl{
            get;
            set;
        }

       public EventSubscriberWrapper() { 
         log4net.Config.XmlConfigurator.Configure();           
       }

       public EventSubscriberWrapper(string subscriberDesc, string eventSubscriberUrl) :      
       this(){
           EventSubscriberUrl = eventSubscriberUrl;  
			this.subscriberDesc = subscriberDesc;
       }


        /// <summary>
        /// this method runs a subscriber service (EventSubscriber) on a Self-hosted WCF service
        /// </summary>
        /// <param name="addr"></param>
        /// <returns></returns>
        public ServiceHost StartService()
        {
            
            Uri[] BaseAddresses = new Uri[] { new Uri(EventSubscriberUrl) };
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
                    smb.HttpGetUrl = new Uri(EventSubscriberUrl + "/meta");
                    serviceHost.Description.Behaviors.Add(smb);
                }
                BasicHttpBinding myBinding = new BasicHttpBinding(BasicHttpSecurityMode.None);
                myBinding.Namespace = "http://eventmanager.linksmart.eu";
                serviceHost.AddServiceEndpoint(typeof(IMetadataExchange), MetadataExchangeBindings.CreateMexHttpBinding(), EventSubscriberUrl + "/mex");
                serviceHost.AddServiceEndpoint(typeof(EventSubscriber), myBinding, "");
                serviceHost.Open();

                Thread createHidThread = new Thread(delegate()
                {
                    NetworkManagerApplicationService nm = new NetworkManagerApplicationService();
                    EventSubscriberHid = "";
                    int retry = 0;
                    do
                    {
                        try
                        {
                            EventSubscriberHid = nm.createHIDwDesc(subscriberDesc, EventSubscriberUrl).Trim();
                            retry++;
                            if (EventSubscriberHid.Equals(""))
                                Log.Debug("re-trying to create HID for " + subscriberDesc + " in " + retry_delay + " ms ....(" + retry + "x)");
                            else 
                                Log.Info("Found EventSubscriberHid : " + EventSubscriberHid);
                            Thread.Sleep(retry_delay);
                        }
                        catch (Exception ex)
                        {
                            Log.Debug("Cannot find the Network Manager just yet. " + ex.Message);
                            
                        }                      
                    } while (EventSubscriberHid == "");
      
                });
                createHidThread.Start();
            }
            catch (Exception e) { Log.Error(e.Message); }
            return serviceHost;
        }

      /// <summary>
      /// subscribe to a topic on an event manager and define 
      /// </summary>
      /// <param name="eventManagerDesc">event manager description, registered on the network manager</param>
      /// <param name="topicName">event topic to subscribe</param>
      /// <param name="callback">a callback method to notify when event is raised</param>
        public void SubscribeEvent(string eventManagerDesc, string topicName, NotifyDelegate callback)
        {
            Thread subscribeEventThread = new Thread(delegate()
            {
                // try to find the event manager
                int retry = 0;
                String emUrl = "";
                while ((emUrl = NetworkManagerUtil.GetLinksmartUrlFromDesc(Properties.Settings.Default.NetworkManagerStubUrl, eventManagerDesc))==null)
                {
                    retry++;
                    Log.Debug("re-trying to get the event manager address in " + retry_delay + " ms ....(" + retry + "x)");                    
                    Thread.Sleep(retry_delay);
                }
                Log.Info("Found emUrl : " + emUrl);
                EventManagerImplementation eventManager = new EventManagerImplementation();
                eventManager.Url = emUrl;
                Subscription s = new Subscription(eventManager, eventManagerDesc, topicName, callback);
                
                // now try to subscribe    
                retry = 0;
                bool subscribeResult = false;
                do{
                    try
                    {
                        // change with subscribe by description
                        if (!(subscribeResult = eventManager.subscribeWithDescription(topicName, subscriberDesc, 0)))
                        {
                            retry++;
                            if (!subscribeResult) 
                                Log.Debug("re-trying to subscribe to topic " + topicName + " in " + retry_delay + " ms ....(" + retry + "x)");
                            else 
                                Log.Info("topic  : " + topicName + " subscribed");
                            Thread.Sleep(retry_delay);
                        }
                    }
                    catch (Exception ex) {
                        Log.Debug(ex.Message);
                    }
                } while (!subscribeResult);

                lock (successfulSubscriptions)
                {
                    successfulSubscriptions.Add(s);
                }
            });

            subscribeEventThread.Start();
          
        }

        /// <summary>
        /// Unsubscribe all subscriptions
        /// </summary>
        public void UnsubscribeAllEvents()
        {
            foreach (Subscription s in successfulSubscriptions)
            {
                s.EventManager.unsubscribe(s.Topic, EventSubscriberHid);                
            }
            
        }


        /// <summary>
        /// Unsubscribe sll subscriptions on a topic
        /// </summary>
        /// <param name="eventManagerDesc"></param>
        /// <param name="topic"></param>
        public void UnsubscribeTopic(string eventManagerDesc, string topic)
        {
            foreach (Subscription s in successfulSubscriptions){
               if(s.EventManagerDesc.Equals(eventManagerDesc)) s.EventManager.unsubscribe(s.Topic, EventSubscriberHid);
            }
        }

        bool EventSubscriber.notify(string topic, eu.linksmart.eventing.EventManagerStub.Part[] parts)
        {
            foreach (Subscription s in successfulSubscriptions)
            {
                if (s.Topic.Equals(topic)) s.Callbacks(topic, parts);
            }
            return true;
        }
 
    }
}
