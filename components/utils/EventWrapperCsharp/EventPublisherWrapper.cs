using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using eu.linksmart.eventing.EventManagerStub;
using eu.linksmart.eventing.util;
using log4net;

namespace eu.linksmart.eventing
{
    public class EventPublisherWrapper
    {
        ILog Log = LogManager.GetLogger(typeof(EventSubscriberWrapper));
        const int retry_delay = 1000;
        /// <summary>
        /// This method finds the EventManager with the "eventManagerDesc" 
        /// by asking the local network manager
        /// and (re-)tries to publish the event until it succeeds. 
        /// </summary>
        /// <param name="eventManagerDesc">event manager desc that is registered on the network manager</param>
        /// <param name="topic"></param>
        /// <param name="parts"></param>
        /// <returns></returns>
        public bool PublishEvent(string eventManagerDesc, string topic, Part[] parts) {

            Thread thPublisher = new Thread(delegate()
            {
                // try to find the event manager
                int retry = 0;
                String emUrl = null;
                while ((emUrl = NetworkManagerUtil.GetLinksmartUrlFromDesc(Properties.Settings.Default.NetworkManagerStubUrl, eventManagerDesc))==null)
                {
                    retry++;
                    Log.DebugFormat("re-trying to get the event manager address in " + retry_delay + " ms ....({0}x)", retry);                   
                    Thread.Sleep(retry_delay);
                } while (emUrl == "");
                EventManagerImplementation eventManager = new EventManagerImplementation();
                eventManager.Url = emUrl;

                // now try to publish    
                retry = 0;
                bool subscribeResult = false;
                do
                {
                    try{
                        subscribeResult = eventManager.publish(topic, parts);                       
                    }
                    catch (Exception e){
                        Log.ErrorFormat("exception on publish to topic " + topic + ", " + e.Message);
                    }

                    if (!subscribeResult){
                        retry++;
                        Log.DebugFormat("re-trying to publish to topic " + topic + " in " + retry_delay * retry + " ms  ....({0}x)", retry);
                        Thread.Sleep(retry_delay *  retry);
                    }            
                } while (!subscribeResult && retry < 5);
                if (subscribeResult) 
                    Log.DebugFormat("event is published to topic : " + topic);
                else
                    Log.ErrorFormat("Event with topic {0} can't be published ", topic);
            });

            thPublisher.Start();
            return true;
        }

    }
}
