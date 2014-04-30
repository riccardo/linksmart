package eu.linksmart.maven.examples.ds;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;

@Component(immediate=true, name="CalculatorService")
@Service
public class CalculatorService implements ICalculatorService {
	
	public CalculatorService() {
		
	}
	
	public int add(int a, int b) {
		System.out.println("add invoked - a [" + a + "] b [" + b + "]");
        return a + b;
	}
	
	@Activate
    public void start() {
        System.out.println("Starting TestService");
    }
    
    @Deactivate
    public void stop() {
        System.out.println("Stopping TestService");
    }

}
