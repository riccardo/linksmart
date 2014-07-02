package eu.linksmart.network.grand.impl.integration;

import static org.junit.Assert.*;
import junit.framework.Assert;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

import org.apache.log4j.Logger;

import eu.linksmart.it.utils.ITConfiguration;

import eu.linksmart.network.VirtualAddress;

import eu.linksmart.network.grand.impl.GrandMessageHandlerImpl;

/* 
 * @RunWith(PaxExam.class) hooks Pax Exam into JUnit and does its magic of 
 * setting up a test container with an OSGi framework and bundles under test
 */
@RunWith(PaxExam.class)
public class GrandMessageHandlerIT {

    /*
	 * @Inject triggers the injection of OSGi services into the test.
	 * the test method can access the BundleContext of the probe bundle or any service obtained from the OSGi service registry
	 */
    @Inject
    private GrandMessageHandlerImpl grandMsgHandler;

    /*
     * @Configuration returns an array of configuration options. 
     * These options define the set of bundles to be provisioned to the OSGi framework in the test container 
     */
    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"grand-message-handler")  // this feature will install all bundles including required dependencies without web-service provider                  
        };
    }
    
    @Test
    public void testService() throws Exception {   
        try {
        	System.out.println("starting grand-message-handler IT");
        	
        	VirtualAddress senderVirtualAddress = new VirtualAddress("0.0.0.123");
			VirtualAddress receiverVirtualAddress = new VirtualAddress("0.0.0.456");
			String URL_PATH = "/" + senderVirtualAddress.toString() + "/" + receiverVirtualAddress;
        	
        	HttpServletRequest httpRequest = new HttpServletRequestWrapper(URL_PATH);
            
            assertEquals(senderVirtualAddress, grandMsgHandler.getSenderVirtualAddressFromPath(httpRequest, senderVirtualAddress));
			
			assertEquals(receiverVirtualAddress, grandMsgHandler.getReceiverVirtualAddressFromPath(httpRequest));
			
			System.out.println("grand-message-handler IT successfully completed");
       	assertTrue(true);
        } catch(Exception e) {
        	fail(e.getMessage());
        }
    }
}