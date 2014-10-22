package eu.linksmart.example.calculator.consumer;

import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;

import eu.linksmart.example.calculator.Calculator;
import eu.linksmart.example.calculator.CalculatorPortType;
import eu.linksmart.it.utils.ITConfiguration;

@RunWith(PaxExam.class)
public class CalculatorConsumerIT {

    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
        		features(ITConfiguration.getServicesFeaturesRepositoryURL(),"linksmart-example-service"),  
        };
    }
    
    @Test
    public void testService() {
    	
    	try {
			
			String calculatorEndPoint_wsdl = "http://localhost:9090/cxf/services/Calculator?wsdl";
			
			System.out.println("initializing calculator service client at: " + calculatorEndPoint_wsdl);
			Calculator calculatorService = new Calculator(new URL(calculatorEndPoint_wsdl));
            
        	System.out.println("instantiated calculator service client.");
        	CalculatorPortType calculatorPortType = calculatorService.getCalculatorPort();
        	System.out.println("instantiated calculator port type.");
        	
            Client client = ClientProxy.getClient(calculatorPortType);
            client.getRequestContext().put(Message.ENDPOINT_ADDRESS, calculatorEndPoint_wsdl);
            
			int result = calculatorPortType.add(31, 11);
			
			System.out.println("*** calculation result: " + result);
			
			client.destroy();
			client = null;
			calculatorPortType = null;
			calculatorService = null;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("unable to access calculator service");
		}
    	
    }
}