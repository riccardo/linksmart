package eu.linksmart.example.calculator.consumer;

import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;

import org.apache.log4j.Logger;

import org.osgi.service.component.ComponentContext;

import eu.linksmart.example.calculator.Calculator;
import eu.linksmart.example.calculator.CalculatorPortType;
import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;

@Component(name="LinkSmartClientExample", immediate=true)
public class CalculatorClient {

	private static Logger LOG = Logger.getLogger(CalculatorClient.class.getName());
	
	private static final long PERIOD = 40000; //40 sec
	private static final long INITIAL_DELAY = 30000;
	private ScheduledThreadPoolExecutor stpe;
	
	public static final String DESCRIPTION = "CalculatorForBeginners";
	
	@Reference(name="NetworkManager",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindNM",
            unbind="unbindNM",
            policy= ReferencePolicy.DYNAMIC)
    private NetworkManager networkManager;
		
	protected void bindNM(NetworkManager nm) {
		LOG.debug("CalculatorClient::binding networkmanager");
		networkManager = nm;
    }

    protected void unbindNM(NetworkManager nm) {
    	LOG.debug("CalculatorClient::un-binding networkmanager");
    	networkManager = null;
    }
    
    @Activate
	protected void activate(ComponentContext context) {
		LOG.info("activating " + context.getBundleContext().getBundle().getSymbolicName());		
		initLoopingThread();
		LOG.info("started " + context.getBundleContext().getBundle().getSymbolicName());
	}
	
    @Deactivate
	protected void deactivate(ComponentContext context) { 
    	LOG.info("de-activating " + context.getBundleContext().getBundle().getSymbolicName());
		stpe.shutdownNow();
		networkManager = null;
	}

	private void initLoopingThread() {
		stpe = new ScheduledThreadPoolExecutor(1);
		stpe.scheduleWithFixedDelay(new LoopingConsumptionRunnable(), INITIAL_DELAY, PERIOD, TimeUnit.MILLISECONDS);
	}

	private void calculate() {
		
		Registration[] services = null;
		try {
			//ArrayOfRegistration registrations = nmPort.getServiceByDescription(CONSUMER_SERVICE_ID);
			services = networkManager.getServiceByAttributes(new Part[]{new Part(ServiceAttribute.DESCRIPTION.name(), DESCRIPTION)});
			if (services == null || services.length == 0) {
				LOG.error("calculator service was not found in network-manager registrations.");
				return;
			}
		} catch (Exception e1) {
			LOG.error("unable to get calculator service by attributes from NetworkManager", e1);
			return;
		}
		
		String virtualAddress = services[0].getVirtualAddressAsString();
		LOG.info("calculator service found with virtual address: " + virtualAddress);
		
		try {
			//String calculatorEndPoint_wsdl = "http://localhost:9090/cxf/services/Calculator?wsdl";
			String calculator_base_endpoint = "http://localhost:8082/SOAPTunneling/0/";
			String calculator_tunneling_endpoint = calculator_base_endpoint + virtualAddress;
			String calculator_tunneling_wsdl_endpoint = calculator_tunneling_endpoint + "?wsdl";
			
			LOG.info("initializing calculator service client at: " + calculator_tunneling_wsdl_endpoint);
        	Calculator calculatorService = new Calculator(new URL(calculator_tunneling_wsdl_endpoint));
        	CalculatorPortType calculatorPortType = calculatorService.getCalculatorPort();
        	
            Client client = ClientProxy.getClient(calculatorPortType);
            LOG.info("using calculator service URL: " + calculator_tunneling_endpoint);
            client.getRequestContext().put(Message.ENDPOINT_ADDRESS, calculator_tunneling_endpoint);
            
			int result = calculatorPortType.add(31, 11);
			LOG.info("*** calculation result: " + result);
			//
			// clean up the environment after service all to make sure resources are de-allocated
			//
			client.destroy();
			client = null;
			calculatorPortType = null;
			calculatorService = null;
		} catch (Exception e) {
			LOG.error("unable to access calculator service", e);
		}
	}
	
	private class LoopingConsumptionRunnable implements Runnable {
		@Override
		public void run() {
			calculate();
		}
	}
}
