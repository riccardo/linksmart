package eu.linksmart.network.backbone.impl.soap.integration;

import java.util.List;

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

import javax.inject.Inject;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

@RunWith(PaxExam.class)
public class  BackboneSOAPIT  {

        @Inject
        private Backbone backboneSOAP;

        @Configuration
        public Option[] config() {
        	return new Option[] {
            		ITConfiguration.regressionDefaults(),
                    KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.shell.cfg", "sshPort", "8105"),
                    KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", "1121"),
                    KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", "44467"),
                    KarafDistributionOption.editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", "8087"),
            		features(ITConfiguration.getFeaturesRepositoryURL(),"backbone-soap-it"),  
        	};
        }

    @Test
    public void basicIntegrationChain(){


        // TESTING class retrieval  & listing of security types

        String fromService = backboneSOAP.getName().toString();
        System.out.println("class name : "+fromService);
        Assert.assertEquals("eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl",fromService);
        List<SecurityProperty> securityTypes = backboneSOAP.getSecurityTypesRequired();
        System.out.println("number of security types : "+securityTypes.size());
        assertNotNull(securityTypes);

        // test addition and removal of simple virtual-adress & endpoint as pair
        VirtualAddress va;
        va = new VirtualAddress();
        va.setContextID1(0);
        va.setContextID1(0);
        va.setContextID1(0);
        va.setDeviceID(109499400);
        va.setLevel(0);

        // TESTING basic add and removal of endpoints
        // has to be a valid URL for the soap impl
        String endpoint = "http://is.gd/qNNIop";

        // add endpoint + virtual adress to backbone
        System.out.println("Virtual adress : "+va);
        System.out.println("Endpoint, enjoy ;-) "+endpoint);
        boolean result = backboneSOAP.addEndpoint(va,endpoint);
        assertTrue(result);
        System.out.println("Endpoint added : "+result);

        String EPfromService = backboneSOAP.getEndpoint(va);
        System.out.println("Retrieved endpoint from backbone : "+EPfromService);
        assertEquals(endpoint, EPfromService);

        // remove endpoint from backbone
        result = backboneSOAP.removeEndpoint(va);
        assertTrue(result);
        System.out.println("Endpoint removed : "+result);

        EPfromService = backboneSOAP.getEndpoint(va);
        Assert.assertEquals(null,EPfromService);
        System.out.println("Endpoint after removal : "+EPfromService);

        // TESTING sender & remote service endpoint addition and retrieval

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
        result = backboneSOAP.addEndpoint(va0, endpointSender);

        backboneSOAP.addEndpointForRemoteService(va0, va1);


        String ep = backboneSOAP.getEndpoint(va0);
        System.out.println("Endpoint sender : "+ep);
        ep = backboneSOAP.getEndpoint(va1);
        System.out.println("Endpoint remote service  : "+ep);

        result = backboneSOAP.removeEndpoint(va1);
        System.out.println("Endpoint remote service removed  : "+result);

        assertTrue(result);

        result = backboneSOAP.removeEndpoint(va0);
        System.out.println("Endpoint sender removed  : "+result);

        assertTrue(result);

    }
}
