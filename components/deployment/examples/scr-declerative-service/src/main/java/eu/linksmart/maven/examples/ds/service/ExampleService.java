package eu.linksmart.maven.examples.ds.service;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;

@Component(immediate=true)
@Service
public class ExampleService implements IExampleService {
	
    private String greetingMessage = "Hello-Back-from-ExampleService";

    public ExampleService() {
    }

    public String sayHi(String message) {
        System.out.println("sayHi-ExampleService invoked");
        return greetingMessage + " [" + message + "]";
    }
    
    @Activate
    public void start() {
        System.out.println("Starting ExampleService");
    }
    
    @Deactivate
    public void stop() {
        System.out.println("Stopping ExampleService");
    }

}
