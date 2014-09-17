package eu.linksmart.event.mqtt.tests;

import eu.linksmart.api.event.EventPublicationWrapper;
import eu.linksmart.event.mqtt.impl.MqttServiceProvider;
import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.utils.Part;
import junit.framework.Assert;
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

    private String ebbitsBroker = "ebbits.fit.fraunhofer.de:1883";
    private String e3Broker = "e3.fit.fraunhofer.de:1883";
    private String pahoBroker ="tcp://iot.eclipse.org:1883";
    private String localBroker ="tcp://localhost:1883";

    @Inject
    private EventPublicationWrapper publicationWrapper;

    @Configuration
    public Option[] config() {
        return new Option[] {
                ITConfiguration.regressionDefaults(),
                features(ITConfiguration.getFeaturesRepositoryURL(),"linksmart-event-it"),
        };
    }

    @Test
    public void testMQTTPublisher() throws RemoteException {
        String fromService = publicationWrapper.getClass().getName();
        System.out.println("MQTT publisher class name : " + fromService);

        UUID randomUUID = UUID.randomUUID();
        Part[] payload = new Part[1];
        payload[0] = new Part();
        payload[0].setKey("test-key");
        payload[0].setValue("test-value");

        System.out.println("calling findEventManager...");
        publicationWrapper.findEventManager("client-id", pahoBroker);

        System.out.println("calling publishEvent...");
        assertTrue(publicationWrapper.publishEvent("client-id", "test-topic", payload));

    }
}