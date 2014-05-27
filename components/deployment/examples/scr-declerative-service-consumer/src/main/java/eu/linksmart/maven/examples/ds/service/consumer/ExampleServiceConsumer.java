package eu.linksmart.maven.examples.ds.service.consumer;

import org.apache.felix.scr.annotations.*;

import eu.linksmart.maven.examples.ds.service.IExampleService;

@Component(name="ExampleServiceConsumer", immediate=true)
public class ExampleServiceConsumer {
	
    @Reference(name="ExampleService",
			cardinality = ReferenceCardinality.OPTIONAL_UNARY,
			policy=ReferencePolicy.DYNAMIC,
			bind="bindService", 
			unbind="unbindService")
    IExampleService exampleService;
    
    public ExampleServiceConsumer() {
    	
    }
    
    protected void bindService(IExampleService exampleService) {
    	System.out.println("binding IExampleService");
    	this.exampleService = exampleService;
    }
    
    protected void unbindService(IExampleService exampleService) {
    	System.out.println("unbinding IExampleService");
    	this.exampleService = null;
    }
    
    @Activate
    public void start() {
        System.out.println("starting ExampleServiceConsumer");
        System.out.println("ExampleServiceConsumer referencing: " + this.exampleService.getClass().getName());
        System.out.println("ExampleService response: " + this.exampleService.sayHi("ExampleServiceConsumer"));
    }
    
    @Deactivate
    public void stop() {
        System.out.println("stopping ExampleServiceConsumer");
    }
        
    
}
