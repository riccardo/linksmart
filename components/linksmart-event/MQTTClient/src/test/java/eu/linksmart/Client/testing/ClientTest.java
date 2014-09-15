package eu.linksmart.Client.testing;

import eu.linksmart.api.event.EventSubscriber;
import eu.linksmart.event.mqtt.impl.MqttServiceProvider;
import eu.linksmart.utils.Part;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;
import java.rmi.RemoteException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

//import eu.linksmart.it.utils.ITConfiguration;

@RunWith(PaxExam.class)
//@ExamReactorStrategy(PerMethod.class)
public class ClientTest {

    @Inject
    private MqttServiceProvider mqttProvider;

    @Configuration
    public org.ops4j.pax.exam.Option[] config() {
        return options(
                //eu.linksmart.it.utils.ITConfiguration.regressionDefults(true),
                //features(ITConfiguration.getFeaturesRepositoryURL(),"linksmart"),

                features("mvn:eu.linksmart/linksmart-features/2.2.0-SNAPSHOT/xml/features","linksmart"),
                mavenBundle("org.eclipse.paho", "mqtt-client", "0.4.0"),
                mavenBundle("eu.linksmart", "eu.linksmart.api.networkmanager", "2.2.0-SNAPSHOT"),
                mavenBundle("com.google.code.gson", "gson", "1.7.1"),
                mavenBundle("eu.linksmart", "eu.linksmart.api.utils", "2.2.0-SNAPSHOT"),
                mavenBundle("eu.linksmart", "eu.linksmart.api.event", "1.0-SNAPSHOT"),
                junitBundles()
        );
    }

    private Logger mlogger = Logger.getLogger(ClientTest.class.getName());

    private boolean received = false;
    private int ttl = 10;

    @Test
    public void connectivity() {
        //TODO dummy assertion. Add your proper tests
        mqttProvider.findEventManager("test","localhost");



        if(mqttProvider.isEventManagerLocated("test")) {

            assertEquals("Connectivity test pass", true);
            System.out.println("Connectivity test pass");
        }else

            fail("Test fail! There is no Connectivity, probably there is no broker");

    }
    @Test
    public void messageLoop() {
        //TODO dummy assertion. Add your proper tests
        mqttProvider.findEventManager("test","localhost");
        mqttProvider.registerCallback(new TestListener(), "test" );

        while (received && ttl !=0){
            try {
                if(!mqttProvider.isEventManagerLocated("test"))
                    mqttProvider.findEventManager("test","localhost");
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        if(received) {

            assertEquals("Message loop test pass", true);

            System.out.println("Message loop test pass");
        }else

            fail("Test fail! The messaging loop could not be tested");

    }
    class TestListener implements EventSubscriber {

        @Override
        public Boolean notify(String topic, Part[] parts) throws RemoteException {
            received = true;
            return null;
        }

        @Override
        public Boolean notifyXmlEvent(String xmlEventString) throws RemoteException {

            received = true;
            return null;
        }
    };
}