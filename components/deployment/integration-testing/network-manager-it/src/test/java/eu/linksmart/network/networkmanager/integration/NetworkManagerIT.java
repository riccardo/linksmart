package eu.linksmart.network.networkmanager.integration;

import static org.junit.Assert.*;
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

import eu.linksmart.network.networkmanager.NetworkManager;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;

/* 
 * @RunWith(PaxExam.class) hooks Pax Exam into JUnit and does its magic of 
 * setting up a test container with an OSGi framework and bundles under test
 */
@RunWith(PaxExam.class)
public class NetworkManagerIT {

    /*
	 * @Inject triggers the injection of OSGi services into the test.
	 * the test method can access the BundleContext of the probe bundle or any service obtained from the OSGi service registry
	 */
    @Inject
    private NetworkManager networkManager;

    /*
     * @Configuration returns an array of configuration options. 
     * These options define the set of bundles to be provisioned to the OSGi framework in the test container 
     */
    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"network-manager-it"),  // this feature will install all bundles including required dependencies without web-service provider
        };
    }
    
    @Test
    public void testService() throws Exception {  
    	
        assertNotNull(networkManager.getClass());
        String className = networkManager.getClass().getCanonicalName();
        System.out.println("ClassName: "+className);
        Assert.assertEquals("eu.linksmart.network.networkmanager.impl.NetworkManagerImpl",className);
    	
        try {
        	assertTrue(true);
        } catch(Exception e) {
        	fail(e.getMessage());
        }
    }
}