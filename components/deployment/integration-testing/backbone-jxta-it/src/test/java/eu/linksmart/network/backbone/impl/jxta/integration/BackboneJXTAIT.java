package eu.linksmart.network.backbone.impl.jxta.integration;

import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.security.communication.SecurityProperty;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import javax.inject.Inject;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

/**
 * Created with IntelliJ IDEA.
 * User: carlos
 * Date: 03.04.14
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PaxExam.class)

@ExamReactorStrategy(PerMethod.class)
public class  BackboneJXTAIT  {

        @Inject
        private Backbone backboneJXTA;


    @Configuration
    public Option[] config() {
        return new Option[] {
                ITConfiguration.regressionDefaults(),
                KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.shell.cfg", "sshPort", "8102"),
                KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", "1118"),
                KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", "44464"),
                KarafDistributionOption.editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", "8084"),
                //features(ITConfiguration.getFeaturesRepositoryURL(),"jxta-integration-feature"),
                features(ITConfiguration.getFeaturesRepositoryURL(),"backbone-jxta-it"),
        };
    }


    @Test
    public void basicIntegrationChain() throws InterruptedException {

//        System.out.println("testing sleep workaround for slow VM...");
//        Thread.currentThread().sleep(10000);
//        System.out.println("woke up after 10 sec.");

        // TEST #1
        // this test retrieves class name & list of security types

        String fromService = backboneJXTA.getName().toString();
        System.out.println("class name : "+fromService);
        Assert.assertEquals("eu.linksmart.network.backbone.impl.jxta.BackboneJXTAImpl",fromService);
        List<SecurityProperty> securityTypes = backboneJXTA.getSecurityTypesRequired();
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

        // has to be a valid URL for the soap impl
        String endpoint = "http://is.gd/qNNIop";

        // add endpoint + virtual adress to backbone
        System.out.println("Virtual adress : "+va);
        System.out.println("Endpoint, enjoy ;-) "+endpoint);
        boolean result = backboneJXTA.addEndpoint(va,endpoint);
        assertTrue(result);
        System.out.println("Endpoint added : "+result);

        String EPfromService = backboneJXTA.getEndpoint(va);
        System.out.println("Retrieved endpoint from backbone : "+EPfromService);
        assertEquals(endpoint, EPfromService);

        // remove endpoint from backbone
        result = backboneJXTA.removeEndpoint(va);
        assertTrue(result);
        System.out.println("Endpoint removed : "+result);

        EPfromService = backboneJXTA.getEndpoint(va);
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

        String endpointSender = "http://is.gd/CW1BeO";
        String endpointRemoteService = "http://is.gd/u4z8Jl";
        result = backboneJXTA.addEndpoint(va0, endpointSender);

        backboneJXTA.addEndpointForRemoteService(va0, va1);


        String ep = backboneJXTA.getEndpoint(va0);
        System.out.println("Endpoint sender : "+ep);
        ep = backboneJXTA.getEndpoint(va1);
        System.out.println("Endpoint remote service  : "+ep);

        result = backboneJXTA.removeEndpoint(va1);
        System.out.println("Endpoint remote service removed  : "+result);

        assertTrue(result);

        result = backboneJXTA.removeEndpoint(va0);
        System.out.println("Endpoint sender removed  : "+result);

        assertTrue(result);

    }

}
