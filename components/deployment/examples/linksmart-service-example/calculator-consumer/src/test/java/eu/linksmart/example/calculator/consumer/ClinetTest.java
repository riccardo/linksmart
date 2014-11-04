package eu.linksmart.example.calculator.consumer;

import java.net.URL;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.linksmart.example.calculator.Calculator;
import eu.linksmart.example.calculator.CalculatorPortType;

public class ClinetTest {
	
	private static Logger LOG = Logger.getLogger(ClinetTest.class.getName());
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testCalculatorClient() {
		
//		try {
//			
//			String calculatorEndPoint_wsdl = "http://localhost:9090/cxf/services/Calculator?wsdl";
//			
//			System.out.println("initializing calculator service client at: " + calculatorEndPoint_wsdl);
//			Calculator calculatorService = new Calculator(new URL(calculatorEndPoint_wsdl));
//            
//        	System.out.println("instantiated calculator service client.");
//        	CalculatorPortType calculatorPortType = calculatorService.getCalculatorPort();
//        	System.out.println("instantiated calculator port type.");
//        	
//            Client client = ClientProxy.getClient(calculatorPortType);
//            client.getRequestContext().put(Message.ENDPOINT_ADDRESS, calculatorEndPoint_wsdl);
//            
//			int result = calculatorPortType.add(31, 11);
//			
//			System.out.println("*** calculation result: " + result);
//			
//			client.destroy();
//			client = null;
//			calculatorPortType = null;
//			calculatorService = null;
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOG.error("unable to access calculator service", e);
//		}
		
	}

}
