using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using Components;

namespace EventManagerTests
{
    [TestFixture]
    public class SubscriptionTest
    {
        [SetUp]
        protected void SetUp()
        { }

        [TearDown]
        protected void TearDown()
        { }

        [Test]
        public void TestSubscribeMatchesEqual()
        {
            string subscriptionTopic = "event/sensor/sensor1/temperature";
            Subscription s = new Subscription();
            s.Topic = subscriptionTopic;

            Assert.That(s.IsMatch(subscriptionTopic));
        }

        [Test]
        public void TestSubscribeMatchesWithWildCard()
        {
            string publishedTopic = "event/sensor/sensor1/temperature";
            string subscriptionTopic = "event/sensor/sensor1/.*";
            Subscription s = new Subscription();
            s.Topic = subscriptionTopic;

            Assert.That(s.IsMatch(publishedTopic));
        }

        [Test]
        public void TestDoesNotMatchTooMuchWithoutWildcard()
        {
            string publishedTopic1 = "event/sensor/sensor1/temperature";
            string subscriptionTopic1 = "event/";
            Subscription s1 = new Subscription();
            s1.Topic = subscriptionTopic1;

            Assert.That(!s1.IsMatch(publishedTopic1));

            string publishedTopic2 = "event12";
            string subscriptionTopic2 = "event1";
            Subscription s2 = new Subscription();
            s2.Topic = subscriptionTopic2;

            Assert.That(!s2.IsMatch(publishedTopic2));

            string publishedTopic3 = "event12";
            string subscriptionTopic3 = "ent12";
            Subscription s3 = new Subscription();
            s3.Topic = subscriptionTopic3;

            Assert.That(!s3.IsMatch(publishedTopic3));
        }
        

        [Test]
        public void TestSubscribeIsCaseSensitive()
        {
            string publishedTopic = "event/sensor/sensor1/temperature";
            string subscriptionTopic = "event/sensor/sensor1/Temperature";
            Subscription s = new Subscription();
            s.Topic = subscriptionTopic;

            Assert.That(!s.IsMatch(publishedTopic));
        }

    }
}
