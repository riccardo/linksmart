package eu.linksmart.network.backbone.impl.osgi.integration;

import java.io.File;
import java.util.List;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.SecurityProperty;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import javax.inject.Inject;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

/**
 * Created with IntelliJ IDEA.
 * User: carlos
 * Date: 03.04.14
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PaxExam.class)

@ExamReactorStrategy(PerMethod.class)
public class  BackboneOSGIIT  {

        private VirtualAddress receiverVirtualAddress = new VirtualAddress("354.453.455.323");
        private VirtualAddress senderVirtualAddress = new VirtualAddress("354.453.993.323");
        private String endpointSender = "endpoint_sender";
        private String endpointReciever = "endpoint_reciever";

        private byte[] revieveBuffer = {0,0};

        @Inject
        private Backbone backboneOSGI;
        @Inject
        private BackboneRouter backboneRouter;

        @Configuration
        public Option[] config() {
            return new Option[] {
                    // Provision and launch a container based on a distribution of Karaf (Apache ServiceMix)
                    karafDistributionConfiguration()
                            .frameworkUrl(
                                    maven()
                                            .groupId("org.apache.servicemix")
                                            .artifactId("apache-servicemix")
                                            .type("zip")
                                            .version("5.0.0"))
                            .karafVersion("3.3.0")
                            .name("Apache ServiceMix")
                            .unpackDirectory(new File("target/servicemix-karaf"))
                            .useDeployFolder(false),

                    //KarafDistributionOption.debugConfiguration("5005", true) ,
                    /*
                    * keeping container sticks around after the test so we can check the contents
                   // of the data directory when things go wrong.
                    */
                    keepRuntimeFolder(),
                    /*
                    * don't bother with local console output as it just ends up cluttering the logs
                    */
                    configureConsole().ignoreLocalConsole(),
                    /*
                    * force the log level to INFO so we have more details during the test. It defaults to WARN.
                    */
                    logLevel(LogLevelOption.LogLevel.INFO),
                    /*
                    * karaf feature will be provisioned to the test container from a local or remote Maven repository
                    * using the standard Maven lookup and caching procedures
                    */
                    features("mvn:eu.linksmart/linksmart-features/2.2.0-SNAPSHOT/xml/features","backbone-osgi-it")


            };
        }

    @Test
    public void basicIntegrationChain(){

        // TEST #1
        // this test retrieves class name & list of security types



        String fromService = backboneOSGI.getName().toString();
        System.out.println("class name : "+fromService);
        Assert.assertEquals("eu.linksmart.network.backbone.impl.osgi.BackboneOsgiImpl",fromService);
        List<SecurityProperty> securityTypes = backboneOSGI.getSecurityTypesRequired();
        System.out.println("number of security types : "+securityTypes.size());
        assertNotNull(securityTypes);

        // TEST #2
        // test addition and removal of simple virtual-adress & endpoint as pair
        VirtualAddress va;
        va = new VirtualAddress();
        va.setContextID1(0);
        va.setContextID1(0);
        va.setContextID1(0);
        va.setDeviceID(109499400);
        va.setLevel(0);
        String endpoint = "ENDPOINT-FUU";

        // add endpoint + virtual adress to backbone
        boolean result = backboneOSGI.addEndpoint(va,endpoint);
        assertTrue(result);
        System.out.println("Endpoint added : "+result);

        String EPfromService = backboneOSGI.getEndpoint(va);
        System.out.println("Retrieved endpoint from backbone : "+EPfromService);
        assertEquals("ENDPOINT-FUU", EPfromService);

        // remove endpoint from backbone
        result = backboneOSGI.removeEndpoint(va);
        assertTrue(result);
        System.out.println("Endpoint removed : "+result);

        EPfromService = backboneOSGI.getEndpoint(va);
        Assert.assertEquals(null,EPfromService);
        System.out.println("Endpoint after removal : "+EPfromService);

        // TEST #3
        // test addition and removal of sender & remote service endpoints

        // sender virtual adress
        VirtualAddress va0;
        va0 = new VirtualAddress();
        va0.setContextID1(0);
        va0.setContextID1(0);
        va0.setContextID1(0);
        va0.setDeviceID(666);
        va0.setLevel(0);

        // remote service virtual adress
        VirtualAddress va1;
        va1 = new VirtualAddress();
        va1.setContextID1(0);
        va1.setContextID1(0);
        va1.setContextID1(0);
        va1.setDeviceID(777);
        va1.setLevel(0);

        String endpointSender = "ENDPOINT-SENDER";
        String endpointRemoteService = "ENDPOINT-REMOTE-SERVICE";
        result = backboneOSGI.addEndpoint(va0, endpointSender);

        backboneOSGI.addEndpointForRemoteService(va0, va1);


        String ep = backboneOSGI.getEndpoint(va0);
        System.out.println("Endpoint sender : "+ep);
        ep = backboneOSGI.getEndpoint(va1);
        System.out.println("Endpoint remote service  : "+ep);

        result = backboneOSGI.removeEndpoint(va1);
        System.out.println("Endpoint remote service removed  : "+result);

        assertTrue(result);

        result = backboneOSGI.removeEndpoint(va0);
        System.out.println("Endpoint sender removed  : "+result);

        assertTrue(result);

        // TEST #4
        // test synch call
//
//
//        result = backboneOSGI.addEndpoint(senderVirtualAddress, endpointSender);
//        result = backboneOSGI.addEndpoint(receiverVirtualAddress, endpointReciever);
//
//        //backboneRouter.addRoute(senderVirtualAddress,backboneOSGI)
//
//
//        //backboneOSGI.receiveDataSynch(senderVirtualAddress,receiverVirtualAddress,buffer2)
//
//        RecieveThread rt = new RecieveThread("recieverThread", backboneOSGI);
//        rt.start();
//
//        byte[] buffer = {0,1};
//        System.out.println("Sending data to reciever...");
//        NMResponse nMresponse = backboneOSGI.sendDataSynch(senderVirtualAddress, receiverVirtualAddress, buffer);
//        nMresponse = backboneOSGI.sendDataSynch(senderVirtualAddress, receiverVirtualAddress, buffer);
//        nMresponse = backboneOSGI.sendDataSynch(senderVirtualAddress, receiverVirtualAddress, buffer);
//        System.out.println("done.");
//
//        System.out.println("NMresponse status :"+nMresponse.getStatus());
//
//
//
//
//    }
//    class RecieveThread extends Thread {
//        private Backbone bb;
//        private int status = 1;
//        public RecieveThread(String str,Backbone bb) {
//            super(str);
//            System.out.println("Reciever thread initalized");
//            this.bb = bb;
//        }
//        public void run() {
//            System.out.println("Reciever thread started");
//            try {
//                while(status!=NMResponse.STATUS_SUCCESS){
//                    NMResponse res = bb.receiveDataSynch(senderVirtualAddress, receiverVirtualAddress, revieveBuffer);
//                    status = res.getStatus();
//                    System.out.println("recieve data synch status : "+res.getStatus());
//                    //sleep(100);
//                }
//                System.out.println("YAY! Recieved data");
//                System.out.println("data: "+revieveBuffer[0]+" , "+revieveBuffer[1]);
//            } catch (Exception e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
    }
}
