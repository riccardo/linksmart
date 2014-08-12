package eu.linksmart.network.identity.impl;

import static org.junit.Assert.*;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.network.identity.IdentityManager;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

/* 
 * @RunWith(PaxExam.class) hooks Pax Exam into JUnit and does its magic of 
 * setting up a test container with an OSGi framework and bundles under test
 */
@RunWith(PaxExam.class)
public class IdentityManagerIT {

	/*
	 * @Inject triggers the injection of OSGi services into the test.
	 * the test method can access the BundleContext of the probe bundle or any service obtained from the OSGi service registry
	 */
    @Inject
    private IdentityManager identityManager;

    /*
     * @Configuration returns an array of configuration options. 
     * These options define the set of bundles to be provisioned to the OSGi framework in the test container 
     */
    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"identity-manager-it"),
        };
    }
    
    @Test
    public void testIdentityManager() throws Exception {   
        try {
        	System.out.println("starting identity manager test");
            Assert.assertEquals("eu.linksmart.network.identity.impl.IdentityManagerImpl",identityManager.getClass().getName());
			System.out.println("identity manager test successfully completed");
        } catch(Exception e) {
        	e.printStackTrace();
        	fail(e.getMessage());
        }
    }
    
}