package eu.linksmart.example.calculator.impl;

import java.rmi.RemoteException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.log4j.Logger;

import org.osgi.service.component.ComponentContext;

import eu.linksmart.example.calculator.Calculator;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.Registration;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;

@Component(name="LinkSmartServiceExample", immediate=true)
@Service({Calculator.class})
@Properties({
    @Property(name="service.exported.interfaces", value="*"),
    @Property(name="service.exported.configs", value="org.apache.cxf.ws"),
    @Property(name="org.apache.cxf.ws.address", value="http://0.0.0.0:9090/cxf/services/Calculator")
})
public class CalculatorImpl implements Calculator {

	private static Logger LOG = Logger.getLogger(CalculatorImpl.class.getName());

	private static final String ENDPOINT = "http://localhost:9090/cxf/services/"	+ Calculator.class.getSimpleName();
	
	private ScheduledThreadPoolExecutor stpe;
	private final long INITIAL_DELAY = 1000;
	private final long PERIOD = 180000; // 3 min
	public boolean flag;

	private Registration myRegistration = null;
	private String backbone = null;

	@Reference(name="NetworkManager",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindNM",
            unbind="unbindNM",
            policy= ReferencePolicy.DYNAMIC)
    private NetworkManager networkManager;
	
	protected void bindNM(NetworkManager nm) {
		LOG.debug("Calculator::binding networkmanager");
		networkManager = nm;
    }

    protected void unbindNM(NetworkManager nm) {
    	LOG.debug("Calculator::un-binding networkmanager");
    	networkManager = null;
    }
	
	@Activate
	protected void activate(ComponentContext context) {
		LOG.info("activating " + context.getBundleContext().getBundle().getSymbolicName());	
		try {
			String[] backbones = networkManager.getAvailableBackbones();
			for (String b : backbones) {
				if (b.contains("soap")) {
					this.backbone = b;
				}
			}
			if (backbone == null) {
				backbone = "eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl";
			}
			LOG.info("using backbone: " + backbone);
		} catch (RemoteException e) {
			LOG.error("unable to retrieve list of backbones from networ-kmanager", e);
		}
		
		if (backbone != null) {
			flag = true;
			initLoopingThread();
		}
		LOG.info("started " + context.getBundleContext().getBundle().getSymbolicName());
	}
	
	@Deactivate
    protected void deactivate(ComponentContext context) {
		LOG.info("de-activating " + context.getBundleContext().getBundle().getSymbolicName());
		if (!flag) {
			invokeRemoveService();
		}
		stpe.shutdownNow();
		networkManager = null;
    }
	
	@Override
	public int add(int a, int b) {
		int result = a + b;
		LOG.info("Invoked Calculator.add: " + a + " + " + b + " = " + result);
		return result;
	}

	private void initLoopingThread() {
		stpe = new ScheduledThreadPoolExecutor(1);
		stpe.scheduleWithFixedDelay(new LoopingServiceThread(), INITIAL_DELAY, PERIOD, TimeUnit.MILLISECONDS);
	}

	private void invokeRegisterService() {
		try {
			myRegistration = networkManager.registerService(
					new Part[] { new Part(ServiceAttribute.DESCRIPTION.name(), DESCRIPTION) }, 
					ENDPOINT, 
					backbone);
			flag = (myRegistration == null || myRegistration.getVirtualAddressAsString().length() == 0) ? true : false;
			LOG.info("virtualAddress of a calculator service: " + myRegistration.getVirtualAddressAsString());
		} catch (RemoteException e) {
			LOG.error("unable to register calculator service with network-manager", e);
		}
	}

	private void invokeRemoveService() {
		try {
			flag = networkManager.removeService(myRegistration.getVirtualAddress());
			LOG.info("removed the virtualAddress of a calculator service: " + myRegistration.getVirtualAddressAsString());
		} catch (RemoteException e) {
			LOG.error("unable to un-register calculator service from network-manager", e);
		}
	}

	/* Inner Thread for looping creating VirtualAddress and removing it */
	private class LoopingServiceThread implements Runnable {
		// log each action
		// invoke service creation
		// or
		// invoke remove service
		// use flag for this
		// service = (flag) ? createService : removeService
		// wait (5min)
		// repeat procedure
		@Override
		public void run() {
			if (flag) {
				invokeRegisterService();
			} else {
				invokeRemoveService();
			}
		}
	}
}