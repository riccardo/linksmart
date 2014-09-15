package eu.linksmart.component;

import eu.linksmart.api.event.EventSubscriber;
import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.utils.Part;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;

import javax.inject.Inject;
import java.rmi.RemoteException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

/* 
 * @RunWith(PaxExam.class) hooks Pax Exam into JUnit and does its magic of 
 * setting up a test container with an OSGi framework and bundles under test
 */
@RunWith(PaxExam.class)
public class ComponentIT {

    private static Logger LOG = Logger.getLogger(ComponentIT.class.getName());

    /*
	 * @Inject triggers the injection of OSGi services into the test.
	 * the test method can access the BundleContext of the probe bundle or any service obtained from the OSGi service registry
	 */
    @Inject
    private MqttClient mqttProvider;

    /*
     * @Configuration returns an array of configuration options. 
     * These options define the set of bundles to be provisioned to the OSGi framework in the test container 
     */
    @Configuration
    public Option[] config() {
        return new Option[] {
        		//
        		// this feature will install all LinkSmart bundles including their required dependencies
        		//
        		ITConfiguration.regressionDefaults(),
        		//
        		// this feature will install all LinkSmart bundles including their required dependencies
        		//
        		features(ITConfiguration.getFeaturesRepositoryURL(),"linksmart"),
        		//
        		// since this integration test is for a given bundle, therefore, that bundle also need to be provisioned inside OSGi container.
        		// any bundle can be installed by following parameter, change those values to fit your service artifact
        		//
               // mavenBundle("org.eclipse.paho", "mqtt-client", "0.4.0"),
                mavenBundle("com.google.code.gson", "gson", "1.7.1"),
                mavenBundle("eu.linksmart", "eu.linksmart.api.event", "1.0-SNAPSHOT"),
                mavenBundle("eu.linksmart.event.mqtt", "MQTTClient", "1.0-SNAPSHOT"),
                logLevel(LogLevelOption.LogLevel.TRACE),
                bundle("file:D:\\Dropbox\\FIT\\workspaces\\almanac\\org.eclipse.paho.mqtt.java\\org.eclipse.paho.client.mqttv3\\target\\org.eclipse.paho.client.mqttv3-1.0.0-bundle.jar"),
                junitBundles()
        };
    }
    
    @Test
    public void testService() throws Exception {   
        try {
            System.out.println(MqttClient.class.getClass().getCanonicalName());
        	assertTrue(true);
        } catch(Exception e) {
        	fail(e.getMessage());
        }
    }
    private Logger mlogger = Logger.getLogger(ComponentIT.class.getName());

    private boolean received = false;
    private int ttl = 10;

//    @Test
//    public void connectivity() {
//        //TODO dummy assertion. Add your proper tests
//        mqttProvider.findEventManager("test","localhost");
//
//
//
//        if(mqttProvider.isEventManagerLocated("test")) {
//
//            assertEquals("Connectivity test pass", true);
//            System.out.println("Connectivity test pass");
//        }else
//
//            Assert.fail("Test fail! There is no Connectivity, probably there is no broker");
//
//    }
//    @Test
//    public void messageLoop() {
//        //TODO dummy assertion. Add your proper tests
//        mqttProvider.findEventManager("test","localhost");
//        mqttProvider.registerCallback(new TestListener(), "test" );
//
//        while (received && ttl !=0){
//            try {
//                if(!mqttProvider.isEventManagerLocated("test"))
//                    mqttProvider.findEventManager("test","localhost");
//                Thread.sleep(1000);
//            } catch(InterruptedException ex) {
//                Thread.currentThread().interrupt();
//            }
//        }
//
//        if(received) {
//
//            assertEquals("Message loop test pass", true);
//
//            System.out.println("Message loop test pass");
//        }else
//
//            Assert.fail("Test fail! The messaging loop could not be tested");
//
//    }
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