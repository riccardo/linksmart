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

        [Test]
        public void TestMatchesXpath()
        {
            string publishedTopic = @"<?xml version='1.0' encoding='UTF-8'?>
<Event xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
 xsi:schemaLocation='urn:linksmart/eventmodel/1.0 LinkSmartEventModel.xsd'
 xmlns='urn:linksmart/eventmodel/1.0'
 xmlns:linksmart='urn:linksmart/typelibrary/1.0'>
    <EventMeta>
        <EventID>41B82917-DF69-4CA6-88EC-F57D5A78894A</EventID>
        <EventType linksmart:modelRef='EbbitsEventOntology'>Alarm/*|Trace/*|System/*|...</EventType>
        <linksmart:TimeStamp>2001-02-03T04:05:30+01:00</linksmart:TimeStamp>
        <linksmart:Priority>3</linksmart:Priority>
        <EventExpirationTime>2001-02-04T04:05:30+01:00</EventExpirationTime>
        <Source>ROOM101</Source>
        <Topic linksmart:modelRef='SEEMPubS:Full' xmlns:seempubs-em='urn:seempubs/eventmodel/1.0'>
            <seempubs-em:EventType>MEASUREMENT</seempubs-em:EventType>
            <seempubs-em:ProxyType>ROOM</seempubs-em:ProxyType>
            <seempubs-em:ProxyID>ROOM101</seempubs-em:ProxyID>
            <seempubs-em:ObservablePropertyType>TEMPERATURE</seempubs-em:ObservablePropertyType>
            <seempubs-em:ObservablePropertyID>TEMP_IN_THE_CORNER</seempubs-em:ObservablePropertyID>
        </Topic>
        <TopicString linksmart:modelRef='SEEMPubS:ShortForm'>MEASUREMENT/ROOM/ROOM101/TEMP/TEMP_IN_THE_CORNER</TopicString>
<!--        <Topic modelRef='twitter'>#cornerInRoom101</Topic>-->
    </EventMeta>
    <Content  xmlns:seempubs-em='urn:seempubs/eventmodel/1.0'>
        <ContentMeta>
        <!-- Each event can only contain measurements from one Sensor/OP combination-->
            <seempubs-em:ProxyID>ROOM101</seempubs-em:ProxyID>
            <seempubs-em:ObservablePropertyID>TEMP_IN_THE_CORNER</seempubs-em:ObservablePropertyID>
        </ContentMeta>
        <ContentBody>
            <linksmart:BasicMeasurement>
                <linksmart:ID>ROOM101/TEMP_IN_THE_CORNER</linksmart:ID>
                <linksmart:Value>27</linksmart:Value>
                <linksmart:TimeStamp>2001-02-03T04:04:00+01:00</linksmart:TimeStamp>
            </linksmart:BasicMeasurement>
            <linksmart:BasicMeasurement>
                <linksmart:ID>ROOM101/TEMP_IN_THE_CORNER</linksmart:ID>
                <linksmart:Value>26</linksmart:Value>
                <linksmart:TimeStamp>2001-02-03T04:05:00+01:00</linksmart:TimeStamp>
            </linksmart:BasicMeasurement>            
        </ContentBody>
    </Content>
</Event>
";
            //string subscriptionTopic = "//*[local-name()='ProxyID'][.='PlugwiseProxy:Polito:Secretariat2']";
            string subscriptionTopic = @"//*[local-name()='Topic'][seempubs-em:EventType='MEASUREMENT' 
            and seempubs-em:ProxyID='ROOM101']";
            Subscription s = new Subscription();
            s.Topic = subscriptionTopic;

            Assert.That(s.IsMatch(publishedTopic));
        }

        [Test]
        public void TestNotMatchesXpath()
        {
            string publishedTopic = @"<?xml version='1.0' encoding='UTF-8'?>
<Event xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
 xsi:schemaLocation='urn:linksmart/eventmodel/1.0 LinkSmartEventModel.xsd'
 xmlns='urn:linksmart/eventmodel/1.0'
 xmlns:linksmart='urn:linksmart/typelibrary/1.0'>
    <EventMeta>
        <EventID>41B82917-DF69-4CA6-88EC-F57D5A78894A</EventID>
        <EventType linksmart:modelRef='EbbitsEventOntology'>Alarm/*|Trace/*|System/*|...</EventType>
        <linksmart:TimeStamp>2001-02-03T04:05:30+01:00</linksmart:TimeStamp>
        <linksmart:Priority>3</linksmart:Priority>
        <EventExpirationTime>2001-02-04T04:05:30+01:00</EventExpirationTime>
        <Source>ROOM101</Source>
        <Topic linksmart:modelRef='SEEMPubS:Full' xmlns:seempubs-em='urn:seempubs/eventmodel/1.0'>
            <seempubs-em:EventType>MEASUREMENT</seempubs-em:EventType>
            <seempubs-em:ProxyType>ROOM</seempubs-em:ProxyType>
            <seempubs-em:ProxyID>ROOM102</seempubs-em:ProxyID>
            <seempubs-em:ObservablePropertyType>TEMPERATURE</seempubs-em:ObservablePropertyType>
            <seempubs-em:ObservablePropertyID>TEMP_IN_THE_CORNER</seempubs-em:ObservablePropertyID>
        </Topic>
        <TopicString linksmart:modelRef='SEEMPubS:ShortForm'>MEASUREMENT/ROOM/ROOM101/TEMP/TEMP_IN_THE_CORNER</TopicString>
<!--        <Topic modelRef='twitter'>#cornerInRoom101</Topic>-->
    </EventMeta>
    <Content  xmlns:seempubs-em='urn:seempubs/eventmodel/1.0'>
        <ContentMeta>
        <!-- Each event can only contain measurements from one Sensor/OP combination-->
            <seempubs-em:ProxyID>ROOM101</seempubs-em:ProxyID>
            <seempubs-em:ObservablePropertyID>TEMP_IN_THE_CORNER</seempubs-em:ObservablePropertyID>
        </ContentMeta>
        <ContentBody>
            <linksmart:BasicMeasurement>
                <linksmart:ID>ROOM101/TEMP_IN_THE_CORNER</linksmart:ID>
                <linksmart:Value>27</linksmart:Value>
                <linksmart:TimeStamp>2001-02-03T04:04:00+01:00</linksmart:TimeStamp>
            </linksmart:BasicMeasurement>
            <linksmart:BasicMeasurement>
                <linksmart:ID>ROOM101/TEMP_IN_THE_CORNER</linksmart:ID>
                <linksmart:Value>26</linksmart:Value>
                <linksmart:TimeStamp>2001-02-03T04:05:00+01:00</linksmart:TimeStamp>
            </linksmart:BasicMeasurement>            
        </ContentBody>
    </Content>
</Event>
";
            //string subscriptionTopic = "//*[local-name()='ProxyID'][.='PlugwiseProxy:Polito:Secretariat2']";
            string subscriptionTopic = @"//*[local-name()='Topic'][seempubs-em:EventType='MEASUREMENT' 
            and seempubs-em:ProxyID='ROOM101']";
            Subscription s = new Subscription();
            s.Topic = subscriptionTopic;

            Assert.That(!s.IsMatch(publishedTopic));
        }


        [Test]
        public void ContentSubscription()
        {
            string subscriptionTopic = "event/sensor/sensor1/temperature";
            Subscription s = new Subscription()
            {
                Topic = subscriptionTopic,
                Parts = new Part[] { new Part() { key = "testar", value = "storage" } }
            };


            Assert.That(s.IsContentMatch(new Part[] { new Part() { key = "testar", value = "storage" } }));
            Assert.That(s.IsContentMatch(new Part[] { new Part() { key = "testar", value = "storage" }, new Part() { key = "testarAtt", value = "HaEnTill" } }));
            Assert.That(!s.IsContentMatch(new Part[] { new Part() { key = "testar", value = "storage2" } }));
            Assert.That(!s.IsContentMatch(new Part[] { }));
        }

        [Test]
        public void ContentSubscriptionEmptyShouldNotMatchAnything()
        {
            string subscriptionTopic = "event/sensor/sensor1/temperature";
            Subscription s = new Subscription()
            {
                Topic = subscriptionTopic,
                Parts = new Part[] { }
            };


            Assert.That(!s.IsContentMatch(new Part[] { new Part() { key = "testar", value = "storage" } }));
            Assert.That(!s.IsContentMatch(new Part[] { new Part() { key = "testar", value = "storage" }, new Part() { key = "testarAtt", value = "HaEnTill" } }));
            Assert.That(!s.IsContentMatch(new Part[] { new Part() { key = "testar", value = "storage2" } }));
            Assert.That(!s.IsContentMatch(new Part[] { }));
        }
    }

    
}
