using eu.linksmart.eventing;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;



namespace ConsoleApplication1
{
    class Program
    {
        static void Main(string[] args)
        {

            //example of subscribing events
            EventSubscriberWrapper subscriber = new EventSubscriberWrapper("MyTestSubscriber", "http://localhost:8122/MyEventSubscriber");
            subscriber.StartService();
            EventSubscriberWrapper.NotifyDelegate callback = new EventSubscriberWrapper.NotifyDelegate(notify);
            subscriber.SubscribeEvent("EventManager:FIT:Test", "EVENT/TEST", callback);


            //example of publishing events
            EventPublisherWrapper publisher = new EventPublisherWrapper();
            eu.linksmart.eventing.EventManagerStub.Part p = new eu.linksmart.eventing.EventManagerStub.Part();
            p.key="testKey";
            p.value = "testValue";
            eu.linksmart.eventing.EventManagerStub.Part[] parts = new eu.linksmart.eventing.EventManagerStub.Part[]{ p };

            //Console.ReadLine();

            while (true)
            {
                publisher.PublishEvent("EventManager:FIT:Test", "EVENT/TEST", parts);
                System.Threading.Thread.Sleep(2000);
            }            
        }

        static bool notify(string topic, eu.linksmart.eventing.EventManagerStub.Part[] parts) {
            // recieve the events
            Console.WriteLine(topic + " " + parts[0].key + " : " + parts[0].value);            
            return true;
        }
    }
}
