// -----------------------------------------------------------------------
// <copyright file="EventManagerImplementation_2.cs" company="">
// TODO: Update copyright text.
// </copyright>
// -----------------------------------------------------------------------

namespace EventManager
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading;
    using System.ServiceModel;
    using System.Net;
    using System.Net.Sockets;
    using System.ServiceModel.Description;
    using EventStorage;
    using EventManager20Interface;

    /// <summary>
    /// TODO: Update summary.
    /// </summary>
    public partial class EventManagerImplementation
    {
        public static bool IsRunning;
        private static JSONServer jss = new JSONServer();
        private static  ServiceHost serviceHost;
        public static void Start()
        {
            if (!IsRunning)
            {
                InitiateStoredSubscriptions();
                String ipAddress = "";
                if (!Properties.Settings.Default.UseIPv6)
                    ipAddress = Dns.GetHostAddresses(Dns.GetHostName()).Where(ip => ip.AddressFamily == AddressFamily.InterNetwork).First().ToString();
                else
                    ipAddress = Dns.GetHostAddresses(Dns.GetHostName()).Where(ip => ip.AddressFamily == AddressFamily.InterNetworkV6).First().ToString();

                string address = string.Format("http://{0}:{1}/Service", "127.0.0.1", "8124"); //Change localhost to your own ip-address

                //We want to bind on all avaialable addresses 
                string bindingAddress = string.Format("http://{0}:{1}/Service", "0.0.0.0", "8124");
                Uri[] BaseAddresses = new Uri[] { new Uri(address) };
                //Turn off 100-continue
                System.Net.ServicePointManager.Expect100Continue = false;

                //Create the even subscriber
                serviceHost = new ServiceHost(typeof(EventManagerImplementation), BaseAddresses);
                try
                {
                    ServiceMetadataBehavior smb;
                    if ((smb = serviceHost.Description.Behaviors.Find<ServiceMetadataBehavior>()) == null)
                    {
                        smb = new ServiceMetadataBehavior();
                        smb.HttpGetEnabled = true;
                        smb.HttpGetUrl = new Uri(address + "/Meta");
                        serviceHost.Description.Behaviors.Add(smb);
                    }
                    BasicHttpBinding myBinding = new BasicHttpBinding(BasicHttpSecurityMode.None);
                    myBinding.MaxReceivedMessageSize = int.MaxValue;
                    myBinding.MaxBufferSize = int.MaxValue;
                    myBinding.ReaderQuotas.MaxArrayLength = int.MaxValue;
                    myBinding.ReaderQuotas.MaxStringContentLength = int.MaxValue;
                    myBinding.ReaderQuotas.MaxDepth = int.MaxValue;
                    myBinding.ReaderQuotas.MaxBytesPerRead = int.MaxValue;
                 

                    myBinding.Namespace = "http://eventmanager.linksmart.eu";
                    serviceHost.AddServiceEndpoint(typeof(IMetadataExchange), MetadataExchangeBindings.CreateMexHttpBinding(), address + "mex");
                    serviceHost.AddServiceEndpoint(typeof(EventManagerPort), myBinding, "");
                    serviceHost.AddServiceEndpoint(typeof(IEventManager20), myBinding, "/20");
                    serviceHost.Open();

                    Console.WriteLine("Event manager listening at: {0}", address);
                    if (serviceHost.State == CommunicationState.Opened && Properties.Settings.Default.UseNetworkManager)
                    {
                        EventManagerImplementation.RegisterAtNetworkManager(address);
                    }
                }
                catch (Exception e) { Console.WriteLine(e.Message); }

                //EventManagerImplementation m_eventmanager = new EventManager.EventManagerImplementation();
                jss.start();
                Address = address;
                IsRunning = true;
                Console.WriteLine("Event manager has started.");
            }
        }

        private static void InitiateStoredSubscriptions()
        {
            var subscriptions = SubscriptionStore.Store.ListSubscriptions();
            foreach (var subscription in subscriptions) {
                if (!EventManagerImplementation.subscriptionList.Any(s=>s.Topic.Equals(subscription.Topic) &&
                    (
                    (s.Description.Equals(subscription.Description)&&!string.IsNullOrEmpty(s.Description)) ||
                    (s.HID.Equals(subscription.HID)&&!string.IsNullOrEmpty(s.HID)) ||
                    (s.Endpoint.Equals(subscription.Endpoint)&&!string.IsNullOrEmpty(s.Endpoint))
                    )
                    ))
                {
                EventManagerImplementation.subscriptionList.Add(subscription);
                    Console.WriteLine("Subscription added from persistent storage: [{0}], {1} {2} {3}",subscription.Topic,subscription.Endpoint,subscription.HID,subscription.Description);
                }
            }
        }

        public static void Stop()
        {
            if (IsRunning) {
                
            m_timer.Change(System.Threading.Timeout.Infinite, System.Threading.Timeout.Infinite);
            
            jss.stop();
            serviceHost.Close();

            // remove hid on shutdown
            if (Properties.Settings.Default.UseNetworkManager)
            {
                EventManagerImplementation.DeregisterAtNetworkManager();
            }

            EventManagerImplementation.subscriptionList.Clear();
            IsRunning = false;
            Console.WriteLine("Event manager has stopped.");
            }
        }

        /// <summary>
        /// List where the subscriptions are stored
        /// </summary>
        public static List<Components.Subscription> subscriptionList = new List<Components.Subscription>();
        /// <summary>
        /// List where the failed event notifications are stored
        /// </summary>
        public static List<Components.Subscription> failedEventList = new List<Components.Subscription>();

        public bool unsubscribeWithDescription(string topic, string description)
        {
            try
            {
                EventManagerImplementation.subscriptionList.RemoveAll(f => (f.Description.Equals(description) && f.Topic.Equals(topic)));
                Components.Subscription subscription = new Components.Subscription(topic, null, null, description, 0, null, 0, null);
                SubscriptionStore.Store.RemoveSubscription(subscription);
                //SubscriptionStore.Store.RemoveSubscriptionWithTopicAndDescription(topic, description);
                Console.WriteLine("Unsubscribe:\nTopic: {0}\nDescription: {1}", topic, description);
                return true;
            }
            catch { return false; }
        }

        public bool subscribeWithDescription(string topic, string description, int priority)
        {
            try
            {
                if (EventManagerImplementation.subscriptionList.Exists(f => (f.Description != null && f.Description.Equals(description) == true && f.Topic.Equals(topic) == true)))
                { Console.WriteLine("Subscription already exists"); }
                else
                {
                    Components.Subscription subscription = new Components.Subscription(topic, null, null, description, priority, null, 0, null);
                    Subscribe subscribeClass = new Subscribe(subscription);
                    Thread subscribeThread = new Thread(new ThreadStart(subscribeClass.subscribe));
                    subscribeThread.Start();
                    //subscribeThread.Join();
                }
                return true;
            }
            catch { return false; }
        }

        public bool clearSubscriptionsWithDescription(string description)
        {
            try
            {
                EventManagerImplementation.subscriptionList.RemoveAll(f => (f.Description.Equals(description)));
                Components.Subscription subscription = new Components.Subscription(null, null, null, description, 0, null, 0, null);
                SubscriptionStore.Store.RemoveSubscriptions(subscription);
                Console.WriteLine("Subscription cleared:\nDescription: {0}", description);
                return true;
            }
            catch { return false; }
        }



        public static void RemovePersistentSubscriptions(DateTime dateTime)
        {
          
            SubscriptionStore.Store.RemoveSubscriptions(dateTime);

            // Not entirely safe; the event notification should stop while this is going on.
            //EventManagerImplementation.subscriptionList.Clear();
            //InitiateStoredSubscriptions();
     
        }
    }
}

