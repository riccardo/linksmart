package eu.linksmart.maven.examples.ds;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class CalculatorServiceTest {
	
    private CalculatorService calculatorService;
    
    @Before
    public void initProperties() {
    	calculatorService = new CalculatorService();
        
    }
    
    @Test
    public void testCalculatorServiceImplementation() {
    	
        int cService_response = calculatorService.add(2, 2);
        System.out.println("response from CalculatorService: " + cService_response);
        assertEquals(4, 4);   
    }

}
