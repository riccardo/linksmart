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
                String emUrl = "";
                do
                {
                    emUrl = NetworkManagerUtil.GetLinksmartUrlFromDesc(Properties.Settings.Default.NetworkManagerStubUrl, eventManagerDesc);
                    retry++;
                    if (emUrl.Equals("")) Log.DebugFormat("re-trying to get the event manager address in 500 ms ....({0}x)", retry);                   
                    Thread.Sleep(500);
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
                        Log.DebugFormat("exception on publish to topic " + topic + ", " + e.Message);
                    }

                    if (!subscribeResult){
                        retry++;
                        Log.DebugFormat("re-trying to publish to topic " + topic + " in 500 ms ....({0}x)", retry);
                        Thread.Sleep(500);
                    }            
                } while (!subscribeResult);
                if (subscribeResult) Log.InfoFormat("event is published to topic : " + topic);
            });

            thPublisher.Start();
            return true;
        }

    }
}
