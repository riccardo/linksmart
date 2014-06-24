package eu.linksmart.network.backbone.impl.data.integration;

import static org.junit.Assert.*;

import java.util.List;

import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.security.communication.SecurityProperty;
import junit.framework.Assert;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

import org.apache.log4j.Logger;

import eu.linksmart.it.utils.ITConfiguration;

/* 
 * @RunWith(PaxExam.class) hooks Pax Exam into JUnit and does its magic of 
 * setting up a test container with an OSGi framework and bundles under test
 */
@RunWith(PaxExam.class)
public class BackboneDataIT {
   
//    private static Logger LOG = Logger.getLogger(ComponentIT.class.getName());

    /*
	 * @Inject triggers the injection of OSGi services into the test.
	 * the test method can access the BundleContext of the probe bundle or any service obtained from the OSGi service registry
	 */

    @Inject
    private Backbone backboneData;
 
    /*
     * @Configuration returns an array of configuration options. 
     * These options define the set of bundles to be provisioned to the OSGi framework in the test container 
     */
    @Configuration
    public Option[] config() {
    	return new Option[] {
        		ITConfiguration.regressionDefaults(),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"backbone-data"),  
        };
    }

    
    @Test
    public void testService() throws Exception {
    	
        String fromService = backboneData.getName().toString();
        System.out.println("class name : "+fromService);
        Assert.assertEquals("eu.linksmart.network.backbone.impl.data.BackboneData",fromService);
        List<SecurityProperty> securityTypes = backboneData.getSecurityTypesRequired();
        System.out.println("number of security types : "+securityTypes.size());
        assertNotNull(securityTypes);

        try {
        	assertTrue(true);
        } catch(Exception e) {
        	fail(e.getMessage());
        }
    }
}