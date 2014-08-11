package eu.linksmart.network.grand.impl.integration;

import static org.junit.Assert.*;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.network.backbone.data.DataEndpoint;
import eu.linksmart.network.backbone.Backbone;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;

@RunWith(PaxExam.class)
public class GrandMessageHandlerIT {

    @Inject
    private Backbone backboneGrand;
    @Inject
    private DataEndpoint dataEndpoint;

    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
                KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.shell.cfg", "sshPort", "8111"),
                KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", "1127"),
                KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", "44473"),
                KarafDistributionOption.editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", "8093"),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"grand-message-handler-it")                    
        };
    }
    
    @Test
    public void testService() throws Exception {   
        try {
        	System.out.println("starting grand-message-handler IT");
        	
        	Assert.assertEquals("eu.linksmart.network.backbone.impl.data.BackboneData",backboneGrand.getClass().getName());
        	Assert.assertEquals("eu.linksmart.network.grand.impl.GrandMessageHandlerImpl",dataEndpoint.getClass().getName());
        	            
			System.out.println("grand-message-handler IT successfully completed");
        } catch(Exception e) {
        	e.printStackTrace();
        	fail(e.getMessage());
        }
    }
}