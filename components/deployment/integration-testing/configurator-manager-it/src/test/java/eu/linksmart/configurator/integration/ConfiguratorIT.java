package eu.linksmart.configurator.integration;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import eu.linksmart.it.utils.ITConfiguration;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;

@RunWith(PaxExam.class)
public class ConfiguratorIT {
	
	private final String HTTP_PORT = "8882";

    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"configurator-manager-it")
        };
    }

    @Test
    public void testConfigurator() throws Exception {   
        try {
        	System.out.println("starting configurator-manager IT");
        	
        	HttpClient client = new HttpClient();

        	assertEquals(200, client.executeMethod(new GetMethod("http://localhost:" + HTTP_PORT + "/LinkSmartStatus")));
            		
    		System.out.println("configurator-manager IT successfully completed");
        } catch(Exception e) {
        	e.printStackTrace();
        	fail(e.getMessage());
        }
    }
 
}