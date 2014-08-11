package eu.linksmart.security.trustmanager.impl.integration;

import static org.junit.Assert.*;
import junit.framework.Assert;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.security.trustmanager.TrustManager;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;

@RunWith(PaxExam.class)
public class TrustManagerIT {
	
    @Inject
    private TrustManager trustManager;

    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
                KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.shell.cfg", "sshPort", "8116"),
                KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", "1132"),
                KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", "44478"),
                KarafDistributionOption.editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", "8098"),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"trust-manager-it"),                    
        };
    }
    
    @Test
    public void testService() throws Exception { 
        try {
        	System.out.println("starting trust-manager IT");
        	assertNotNull(trustManager.getClass());
            String className = trustManager.getClass().getCanonicalName();
            System.out.println("ClassName: "+className);
            Assert.assertEquals("eu.linksmart.security.trustmanager.impl.TrustManagerImpl",className);
            System.out.println("trust-manager IT successfully completed");
        } catch(Exception e) {
        	fail(e.getMessage());
        }
    }
}