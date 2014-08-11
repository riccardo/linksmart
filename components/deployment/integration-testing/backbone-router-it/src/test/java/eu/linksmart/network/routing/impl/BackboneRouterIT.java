package eu.linksmart.network.routing.impl;

import java.util.List;

import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.routing.BackboneRouter;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;

import javax.inject.Inject;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

@RunWith(PaxExam.class)
public class  BackboneRouterIT  {

    @Inject
    private BackboneRouter backboneRouter;
    
    @Inject
    private Backbone backboneOSGI;

    private VirtualAddress receiverVirtualAddress = new VirtualAddress("354.453.455.323");
    private VirtualAddress senderVirtualAddress = new VirtualAddress("354.453.993.323");
    private String endpoint0 = "endpoint_sender";
    private String endpoint1 = "endpoint_reciever";


    private byte[] recieveBuffer = {0,0};

    @Configuration
        public Option[] config() {
    		return new Option[] {
        		ITConfiguration.regressionDefaults(),
                    KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.shell.cfg", "sshPort", "8104"),
                    KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", "1120"),
                    KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", "44466"),
                    KarafDistributionOption.editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", "8086"),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"backbone-router-it"),  
    		};
        }

      @Ignore
      public void basicTest() {
          assertTrue(true);
      }
    
    @Before
    public void setUp() {
    }


    @Test
    public void basicTest2() {

       // TEST #01  , add two endpoints to OSGI backbone, add route to backbone router
       //boolean re = backboneOSGI.addEndpoint(this.senderVirtualAddress, this.endpoint0);
       //re = backboneOSGI.addEndpoint(this.receiverVirtualAddress, this.endpoint1);
        //backboneOSGI.

       boolean fromRouter = backboneRouter.addRouteToBackbone(senderVirtualAddress, backboneOSGI.getClass().getName(), endpoint0);
       fromRouter = backboneRouter.addRouteToBackbone(receiverVirtualAddress, backboneOSGI.getClass().getName(), endpoint1);
       //boolean fromRouter = backboneRouter.addRoute(this.senderVirtualAddress, backboneOSGI.getClass().getName());
       //fromRouter = backboneRouter.addRoute(this.receiverVirtualAddress, backboneOSGI.getClass().getName());

       List fromService = backboneRouter.getAvailableBackbones();
       System.out.println("available backbones : " + fromService.size());
       Assert.assertEquals(1,fromService.size());

       // TEST #02 , send asynchronous

       byte[] sendBuffer = {1,1};
       NMResponse a = backboneRouter.sendDataAsynch(senderVirtualAddress, receiverVirtualAddress, sendBuffer);

       System.out.println("status from backbone (async) : "+a.getStatus());
       //assertEquals(NMResponse.STATUS_SUCCESS,a.getStatus());

       // TEST #03 , send synchronous
       //TODO not running at the moment
//        String bb = backboneRouter.getAvailableBackbones().get(0);
//
//        //RecieveThread rt = new RecieveThread("recieverThread",backboneOSGI );
//        //rt.start();
//
//
//        a = backboneRouter.sendDataSynch(senderVirtualAddress, receiverVirtualAddress, sendBuffer);
//
//        System.out.println("status from backbone (sync) : "+a.getStatus());
//        assertEquals(NMResponse.STATUS_SUCCESS,a.getStatus());
    }
    
    class RecieveThread extends Thread {
        private Backbone bb;
        private int status = 1;
        public RecieveThread(String str,Backbone bb) {
            super(str);
            System.out.println("Reciever thread initalized");
            this.bb = bb;
        }
        public void run() {
            System.out.println("Reciever thread started");
            try {
                while(recieveBuffer[0]==0){
                    NMResponse res = bb.receiveDataSynch(senderVirtualAddress, receiverVirtualAddress, recieveBuffer);
                    status = res.getStatus();
                    System.out.println("recieve data synch status : "+res.getStatus());
                    //sleep(10);
                }
                System.out.println("YAY! Recieved data");
                System.out.println("data: "+ recieveBuffer[0]+" , "+ recieveBuffer[1]);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

//    @Test
//    public void basicIntegrationChain() throws InterruptedException {
//
////        System.out.println("testing sleep workaround for slow VM...");
////        Thread.currentThread().sleep(10000);
////        System.out.println("woke up after 10 sec.");
//
//        // TEST #1
//        // this test retrieves class name & list of security types
//
//        String fromService = backboneJXTA.getName().toString();
//        System.out.println("class name : "+fromService);
//        Assert.assertEquals("eu.linksmart.network.backbone.impl.jxta.BackboneJXTAImpl",fromService);
//        List<SecurityProperty> securityTypes = backboneJXTA.getSecurityTypesRequired();
//        System.out.println("number of security types : "+securityTypes.size());
//        assertNotNull(securityTypes);
//
//        // TEST #2
//        // test addition and removal of simple virtual-adress & endpoint as pair
//        VirtualAddress va;
//        va = new VirtualAddress();
//        va.setContextID1(0);
//        va.setContextID1(0);
//        va.setContextID1(0);
//        va.setDeviceID(109499400);
//        va.setLevel(0);
//
//        // has to be a valid URL for the soap impl
//        String endpoint = "http://is.gd/qNNIop";
//
//        // add endpoint + virtual adress to backbone
//        System.out.println("Virtual adress : "+va);
//        System.out.println("Endpoint, enjoy ;-) "+endpoint);
//        boolean result = backboneJXTA.addEndpoint(va,endpoint);
//        assertTrue(result);
//        System.out.println("Endpoint added : "+result);
//
//        String EPfromService = backboneJXTA.getEndpoint(va);
//        System.out.println("Retrieved endpoint from backbone : "+EPfromService);
//        assertEquals(endpoint, EPfromService);
//
//        // remove endpoint from backbone
//        result = backboneJXTA.removeEndpoint(va);
//        assertTrue(result);
//        System.out.println("Endpoint removed : "+result);
//
//        EPfromService = backboneJXTA.getEndpoint(va);
//        Assert.assertEquals(null,EPfromService);
//        System.out.println("Endpoint after removal : "+EPfromService);
//
//        // TEST #3
//        // test addition and removal of sender & remote service endpoints
//
//        // sender virtual adress
//        VirtualAddress va0;
//        va0 = new VirtualAddress();
//        va0.setContextID1(0);
//        va0.setContextID1(0);
//        va0.setContextID1(0);
//        va0.setDeviceID(666);
//        va0.setLevel(0);
//
//        // remote service virtual adress
//        VirtualAddress va1;
//        va1 = new VirtualAddress();
//        va1.setContextID1(0);
//        va1.setContextID1(0);
//        va1.setContextID1(0);
//        va1.setDeviceID(777);
//        va1.setLevel(0);
//
//        String endpointSender = "http://is.gd/CW1BeO";
//        String endpointRemoteService = "http://is.gd/u4z8Jl";
//        result = backboneJXTA.addEndpoint(va0, endpointSender);
//
//        backboneJXTA.addEndpointForRemoteService(va0, va1);
//
//
//        String ep = backboneJXTA.getEndpoint(va0);
//        System.out.println("Endpoint sender : "+ep);
//        ep = backboneJXTA.getEndpoint(va1);
//        System.out.println("Endpoint remote service  : "+ep);
//
//        result = backboneJXTA.removeEndpoint(va1);
//        System.out.println("Endpoint remote service removed  : "+result);
//
//        assertTrue(result);
//
//        result = backboneJXTA.removeEndpoint(va0);
//        System.out.println("Endpoint sender removed  : "+result);
//
//        assertTrue(result);
//
//    }

}
