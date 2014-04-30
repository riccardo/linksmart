package eu.linksmart.maven.examples.ds.consumer;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.maven.examples.ds.ICalculatorService;

@Component(name="CalculatorServiceConsumer", immediate=true)
public class CalculatorServiceConsumer {
	
	@Reference
    ICalculatorService calculatorService;

    protected void bindCalculatorService(ICalculatorService calculatorService) {
    	System.out.println("binding ICalculatorService");
    	this.calculatorService = calculatorService;
    }
    
    protected void unbindCalculatorService(ICalculatorService calculatorService) {
    	System.out.println("unbinding ICalculatorService");
    	this.calculatorService = null;
    }
    
    @Activate
	protected void activate(ComponentContext context) {
    	 System.out.println("starting CalculatorServiceConsumer");
         System.out.println("CalculatorServiceConsumer referencing:" + this.calculatorService.getClass().getName());
         System.out.println("CalculatorService response: " + this.calculatorService.add(2, 2));
    }
    
    @Deactivate
	protected void deactivate(ComponentContext context) {
    	System.out.println("stopping CalculatorServiceConsumer");
    }

}
