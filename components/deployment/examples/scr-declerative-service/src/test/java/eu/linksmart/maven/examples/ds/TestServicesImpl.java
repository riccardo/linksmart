package eu.linksmart.maven.examples.ds;

import org.junit.Before;
import org.junit.Test;

import eu.linksmart.maven.examples.ds.service.ExampleService;
import eu.linksmart.maven.examples.ds.service.IExampleService;

import static junit.framework.Assert.assertEquals;

public class TestServicesImpl {

    private String testString = "Weee!";
    private IExampleService eService;
    
    @Before
    public void initProperties() {
        eService = new ExampleService();
    }
    
    @Test
    public void testServicesImplementation() {
    	
        String eService_response = eService.sayHi(testString);
        System.out.println("response from ExampleService: " + eService_response);
        
        assertEquals("Hello-Back-from-ExampleService [Weee!]", eService_response);
    }
}
