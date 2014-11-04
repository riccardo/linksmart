package eu.linksmart.event.mqtt.tests;

import eu.linksmart.api.event.EventPublicationWrapper;
import eu.linksmart.api.event.EventSubscriber;
import eu.linksmart.api.event.EventSubscriptionWrapper;
import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.utils.Part;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;

import java.rmi.RemoteException;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;


@RunWith(PaxExam.class)
public class  MqttServiceProviderIT  {

    private String pahoBroker ="tcp://iot.eclipse.org:1883";
    private String localBroker ="tcp://localhost:1883";

    private String publisherID = "pubID";
    private String subscriberID = "subID";
    private String payloadKey = "key-1";
    private String payloadValue = "value-1";
    private String topic = "topic-1";

    final Part[] payload = new Part[1];


    @Inject
    private EventPublicationWrapper publicationWrapper;

    @Inject
    private EventSubscriptionWrapper subscriptionWrapper;

    @Configuration
    public Option[] config() {
        return new Option[] {
                ITConfiguration.regressionDefaults(),
                features(ITConfiguration.getFeaturesRepositoryURL(),"linksmart-event-it"),
        };
    }

    @Before public void setupPayload(){

        payload[0] = new Part();
        payload[0].setKey(payloadKey);
        payload[0].setValue(payloadValue);

    }

    @Test
    public void testMQTTPublisAndSubscribeWorkflow() throws RemoteException, InterruptedException {


        System.out.println("[PUBLISHER] calling findEventManager...");
        publicationWrapper.findEventManager(publisherID, pahoBroker);

        boolean publishResponse = publicationWrapper.isEventManagerLocated(publisherID);
        System.out.println("[PUBLISHER] EM located : "+publishResponse);


        // define subscriber thread as inner class
        class EventConsumer extends Thread{

            boolean consumed = false;
            boolean timeout  = false;

            String value = "";
            long t0,t1,threadExecutionTime;
            // when no event arrives from mqtt broker, the consumer thread will die after 30 sec
            long timeOutThreshold = 30000;

            EventConsumer(){
                super();

                System.out.println("[CONSUMER] finding EM...");
                subscriptionWrapper.findEventManager(subscriberID, pahoBroker);
                System.out.println("[CONSUMER]: registering with broker...");
                subscriptionWrapper.registerCallback(new EventSubscriber() {
                    @Override
                    public Boolean notify(String topic, Part[] parts) throws RemoteException {
                        System.out.println("[CONSUMER] recieved notifcation");
                        value = parts[0].getValue();
                        System.out.println("[CONSUMER] recieved payload value : " + value);
                        consumed = true;
                        return Boolean.TRUE;
                    }

                    @Override
                    public Boolean notifyXmlEvent(String topic, String xmlEventString) throws RemoteException {
                        return null;
                    }
                }, subscriberID);
                System.out.println("[CONSUMER] registering with topic...");
                subscriptionWrapper.subscribeWithTopic(subscriberID, topic);
            }
            public String getValue(){
                return value;
            }
            public void run(){
                t0 = System.currentTimeMillis();
                System.out.println("[CONSUMER]: my thread started");
                while((!consumed) && (!timeout)) {
                    try {
                        t1 = System.currentTimeMillis();
                        threadExecutionTime = t1 - t0;
                        System.out.println("[CONSUMER]: thread alive for "+threadExecutionTime/1000.0+" seconds");
                        if(threadExecutionTime > timeOutThreshold){
                            timeout = true;
                            System.out.println("[CONSUMER]: time out detected");
                        }
                        Thread.sleep(50);
                        System.out.println("[CONSUMER] waking up!");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                t1 = System.currentTimeMillis();
                threadExecutionTime = t1 - t0;
                System.out.println("[CONSUMER] thread finished after "+threadExecutionTime/1000.0+" seconds");
            }
        }

        // initalize and start event consumer
        EventConsumer consumer;
        consumer = new EventConsumer();
        consumer.start();

        // publish one event
        System.out.println("[PUBLISHER] publishing under topic : "+topic);
        publishResponse = publicationWrapper.publishEvent(publisherID, topic, payload);
        System.out.println("[PUBLISHER] publishResponse: "+publishResponse);

        // wait until event consumer thread dies
        // consumer dies if it consumes an event or due a time out
        while(consumer.isAlive()){
            Thread.sleep(10);
            System.out.println("waiting for consumer to finish");
        }
        Assert.assertEquals("retrieved wrong value from mqtt broker",payloadValue, consumer.getValue());


    }

}