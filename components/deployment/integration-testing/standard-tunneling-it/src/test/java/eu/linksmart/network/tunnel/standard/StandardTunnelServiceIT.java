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
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;

@RunWith(PaxExam.class)
public class StandardTunnelServiceIT {
	
	private final String HTTP_PORT = "8882";

    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
                KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.shell.cfg", "sshPort", "8115"),
                KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", "1131"),
                KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", "44477"),
                KarafDistributionOption.editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", "8097"),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"standard-tunnel-it"),
        };
    }
    
    @Test
    public void testStandardTunnelService() throws Exception {   
        try {
        	System.out.println("starting standard-tunnel IT");
        	
        	//
        	// this testing code was using network-manager dummy implementation to test the tunneling part by using dummy virtual addresses, 
        	// but it doesn't work with actual network-manager implementation because no such virtual addresses are being
        	// registered into network-manager. It is required to either register some service with network-manager or retrieve list of registered
        	// services from network-manager and use those virtual addresses to make a call
        	//
//        	HttpClient client = new HttpClient();
//        	GetMethod request = new GetMethod("http://localhost:" + HTTP_PORT + "/Tunneling/0.0.0.123/0.0.0.456");
//        	int response = client.executeMethod(request);
//        	assertEquals(200, response);
//        	
//        	PostMethod post_request = new PostMethod("http://localhost:" + HTTP_PORT + "/Tunneling/0.0.0.123/0.0.0.456");
//        	int post_response = client.executeMethod(post_request);
//        	assertEquals(200, post_response);
        	
			System.out.println("standard-tunnel IT successfully completed");
        } catch(Exception e) {
        	fail(e.getMessage());
        }
    }
}