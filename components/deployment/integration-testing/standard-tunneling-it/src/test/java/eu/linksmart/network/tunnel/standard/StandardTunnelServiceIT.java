package eu.linksmart.network.tunnel.standard;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import eu.linksmart.it.utils.ITConfiguration;

@RunWith(PaxExam.class)
public class StandardTunnelServiceIT {
	
	private final String HTTP_PORT = "9090";

    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"standard-tunnel"),
        };
    }
    
    @Test
    public void testStandardTunnelService() throws Exception {   
        try {
        	System.out.println("starting standard-tunnel IT");
        	
        	HttpClient client = new HttpClient();
        	GetMethod request = new GetMethod("http://localhost:" + HTTP_PORT + "/Tunneling/0.0.0.123/0.0.0.456");
        	int response = client.executeMethod(request);
        	assertEquals(200, response);
        	
        	PostMethod post_request = new PostMethod("http://localhost:" + HTTP_PORT + "/Tunneling/0.0.0.123/0.0.0.456");
        	int post_response = client.executeMethod(post_request);
        	assertEquals(200, post_response);
        	
			System.out.println("standard-tunnel IT successfully completed");
        } catch(Exception e) {
        	fail(e.getMessage());
        }
    }
}