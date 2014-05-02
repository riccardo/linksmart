package eu.linksmart.network.backbone.impl.soap.integration;

import java.io.File;
import java.util.List;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.security.communication.SecurityProperty;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
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
public class  BackboneSOAPIT  {

        @Inject
        private Backbone backboneSOAP;

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
                    features("mvn:eu.linksmart/linksmart-features/2.2.0-SNAPSHOT/xml/features","backbone-soap-it")
                    //features("mvn:eu.linksmart/soap-integration-feature/1.0.0/xml/features","soap-integration-feature")

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
