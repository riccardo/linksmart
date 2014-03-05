using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using Components;
using System.Threading;



namespace EventManagerTests
{
  
    [TestFixture]
    public class EventStorageTest
    {
       
        [SetUp]
        protected void SetUp()
        { }

        [TearDown]
        protected void TearDown()
        { }


        [Test]
        public void StoreAndRetrieve()
        {
            string topic = "test";
            Part[] parts = new Part[] {new Part() {key="1",value="a"}, new Part() {key="2",value="b"} };
            LinkSmartEvent e = new LinkSmartEvent(topic, parts);
            EventStorage.EventStorage store = new EventStorage.EventStorage();
            e = store.storeEvent(e);
            Assert.That(!Guid.Empty.Equals(e.InternalId), "Empty guid after storeEvent.");
            Assert.That(e.IsStored, "IsStored is false after storeEvent.");
            Assert.That(topic.Equals(e.Topic), "topic not matching");
            foreach(Part p in e.Parts) {
                Assert.That(parts.Where(x => x.key.Equals(p.key) && x.value.Equals(p.value)).Any(), "part not matching");
            }

            LinkSmartEvent eDb = store.getEvent(e.InternalId);
            Assert.That(e.InternalId.Equals(eDb.InternalId), "InternalId does not match after getEvent.");
            Assert.That(eDb.IsStored, "IsStored is false after getEvent.");
            Assert.That(!Guid.Empty.Equals(eDb.InternalId));
            Assert.That(topic.Equals(eDb.Topic), "Topic does not match after getEvent.");
            foreach (Part p in eDb.Parts)
            {
                Assert.That(parts.Where(x => x.key.Equals(p.key) && x.value.Equals(p.value)).Any(), "part does not match after getEvent.");
            }
        }


        [Test]
        public void ListEventsDates()
        {
            EventStorage.EventStorage store = new EventStorage.EventStorage();

             string topic1 = "test1";
            Part[] parts1 = new Part[] {new Part() {key="1",value="a"}, new Part() {key="2",value="b"} };
            LinkSmartEvent e1 = new LinkSmartEvent(topic1, parts1);
            e1 = store.storeEvent(e1);
            DateTime t1 = DateTime.Now;
            Thread.Sleep(2000);

             string topic2 = "test2";
            Part[] parts2 = new Part[] {new Part() {key="3",value="a"}, new Part() {key="4",value="b"} };
            LinkSmartEvent e2 = new LinkSmartEvent(topic2, parts2);
            e2 = store.storeEvent(e2);
            DateTime t2 = DateTime.Now;
            Thread.Sleep(2000);

             string topic3 = "test3";
            Part[] parts3 = new Part[] {new Part() {key="5",value="a"}, new Part() {key="6",value="b"} };
            LinkSmartEvent e3 = new LinkSmartEvent(topic3, parts3);
            e3 = store.storeEvent(e3);
            DateTime t3 = DateTime.Now;
            Thread.Sleep(2000);

            var results = store.ListEvents(t1.AddSeconds(1), t3.AddSeconds(1));
            Assert.That(results.Count, Is.EqualTo(2));
            Assert.That(results.Exists(x=>x.Topic.Equals(topic2)));
            Assert.That(results.Exists(x => x.Topic.Equals(topic3)));

             results = store.ListEvents(t1.AddSeconds(-1), t3.AddSeconds(1));
            Assert.That(results.Count, Is.EqualTo(3));
            Assert.That(results.Exists(x => x.Topic.Equals(topic1)));
            Assert.That(results.Exists(x => x.Topic.Equals(topic2)));
            Assert.That(results.Exists(x => x.Topic.Equals(topic3)));

             results = store.ListEvents(t1.AddSeconds(1), t3.AddSeconds(-1));
            Assert.That(results.Count, Is.EqualTo(1));
            Assert.That(results.Exists(x => x.Topic.Equals(topic2)));
    

        }

        [Test]
        public void FailedEventNotification()
        {
            string topic1 = "test1";
            Part[] parts1 = new Part[] { new Part() { key = "1", value = "a" }, new Part() { key = "2", value = "b" } };
            LinkSmartEvent e1 = new LinkSmartEvent(topic1, parts1);

            string subscriptionTopic = "event/sensor/sensor1/temperature";
            Subscription s = new Subscription()
            {
                Topic = subscriptionTopic,
                Parts = new Part[] { new Part() { key = "testar", value = "storage" } }
            };

            EventStorage.SubscriptionStore.Store.AddFailedNotification(s, e1);
        }

        [Test]
        public void TryThread()
        {
            
            
            Thread t1 = new Thread(new ThreadStart(CallEsStore));
            t1.Start();
            Thread t2 = new Thread(new ThreadStart(CallEsStore));
            t2.Start();
            Thread.Sleep(5000);
           
        }

        public void CallEsStore()
        {
            string topic = "test";
            Part[] parts = new Part[] { new Part() { key = "1", value = "a" }, new Part() { key = "2", value = "b" } };
            LinkSmartEvent e = new LinkSmartEvent(topic, parts);
            EventStorage.EventStorage store = new EventStorage.EventStorage();
            e = store.storeEvent(e);
        }
       
    }
}
