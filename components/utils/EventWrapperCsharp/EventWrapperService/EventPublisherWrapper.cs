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
        private ILog Log;
        
        /// <summary>
        /// need Log4Net in your config.app
        /// </summary>
        public EventPublisherWrapper() {
            log4net.Config.XmlConfigurator.Configure();
            Log = LogManager.GetLogger(GetType().Name);
        }

        /// <summary>
        /// SubscribeEvent to an EventManager
        /// </summary>
        /// <param name="eventManagerDesc">DESCRIPTION of the event manager (see http://localhost:8082/LinkSmartStatus)</param>
        /// <param name="eventTopic">event topic to subscribe</param>
        /// <param name="callback">a callback functions where the events are forwarded to </param>
      

        /// <summary>
        /// 
        /// </summary>
        /// <param name="eventManagerDesc">DESCRIPTION of the event manager (see http://localhost:8082/LinkSmartStatus)</param>
        /// <param name="topic">event topic to publish an event to</param>
        /// <param name="parts">payload of the event in Part array</param>
        /// <returns>Status if the publication is queued to the event manager</returns>
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
                    if (emUrl == null) 
                        Log.Debug("re-trying to get the event manager virtual address in " + 
                            Properties.Settings.Default.RetryDelay + " ms ....(" + retry + " x)");                   
                    Thread.Sleep(Properties.Settings.Default.RetryDelay);
                } while (emUrl == null);
                EventManagerImplementation eventManager = new EventManagerImplementation();
                eventManager.Url = emUrl;

                // now try to publish    
                retry = 0;
                bool publishResult = false;
                do
                {
                    try{
                        publishResult = eventManager.publish(topic, parts);                       
                    }catch (Exception e){
                        Log.Error("exception on publish to topic " + topic + ", " + e.Message);
                    }

                    if (!publishResult){
                        retry++;
                        Log.Debug("re-trying to publish to topic (" + topic + ") in " 
                            + Properties.Settings.Default.RetryDelay + " ms ....(" + retry + " x)");                   
                        Thread.Sleep(Properties.Settings.Default.RetryDelay);
                    }            
                } while (!publishResult);
                if (publishResult) Log.Info("event is published to topic : " + topic);
            });

            thPublisher.Start();
            return true;
        }

    }
}
