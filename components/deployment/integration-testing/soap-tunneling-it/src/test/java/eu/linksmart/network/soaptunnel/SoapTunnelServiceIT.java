package eu.linksmart.network.soaptunnel;

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

@RunWith(PaxExam.class)
public class SoapTunnelServiceIT {
	
	private final String HTTP_PORT = "9090";

    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"soap-tunnel"),
        };
    }
    
    @Test
    public void testSoapTunnelService() throws Exception {   
        try {
        	System.out.println("starting soap-tunnel IT");
        	
        	HttpClient client = new HttpClient();
  
        	assertEquals(200, client.executeMethod(new GetMethod("http://localhost:" + HTTP_PORT + "/SOAPTunneling/0/0.0.0.6986094776732394497")));
        	
        	assertEquals(200, client.executeMethod(new GetMethod("http://localhost:" + HTTP_PORT + "/SOAPTunneling/0/0.0.0.6986094776732394497/hola")));
        	
        	assertEquals(200, client.executeMethod(new GetMethod("http://localhost:" + HTTP_PORT + "/SOAPTunneling/0/0.0.0.6986094776732394497/wsdl")));
        	
			System.out.println("soap-tunnel IT successfully completed");
        } catch(Exception e) {
        	fail(e.getMessage());
        }
    }
}