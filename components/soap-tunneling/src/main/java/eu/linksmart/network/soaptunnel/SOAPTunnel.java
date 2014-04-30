package eu.linksmart.network.soaptunnel;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.network.networkmanager.core.NetworkManagerCore;

/**
 * Component implementation for SOAPTunnel. Registers the servlet etc.
 * 
 * @author simon
 */
@Component(name="SOAPTunneling", immediate=true)
public class SOAPTunnel {

	private static Logger LOG = Logger.getLogger(SOAPTunnel.class.getName());
	
	@Reference(name="NetworkManagerCore",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindNetworkManagerCore", 
			unbind="unbindNetworkManagerCore",
			policy=ReferencePolicy.DYNAMIC)
	private NetworkManagerCore nmCore;
	
	@Reference(name="HttpService",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindHttpServlet", 
			unbind="unbindHttpServlet", 
			policy=ReferencePolicy.STATIC)
	private HttpService http;
	
	protected void bindNetworkManagerCore(NetworkManagerCore nmCore) {
		LOG.debug("SoapTunnel::binding network-manager-core");
		this.nmCore = nmCore;
	}
	
	protected void unbindNetworkManagerCore(NetworkManagerCore nmCore) {
		LOG.debug("SoapTunnel::un-binding network-manager-core");
		this.nmCore = null;
	}
	
	protected void bindHttpServlet(HttpService http) {
		LOG.debug("SoapTunnel::binding http-service");
		this.http = http;
	}
	
	protected void unbindHttpServlet(HttpService http) {
		LOG.debug("SoapTunnel::un-binding http-service");
		this.http = null;
	}

	@Activate
	protected void activate(ComponentContext context) {
		LOG.info("[activating SoapTunnel]");
		//HttpService http = (HttpService) context.locateService("HttpService");
		// String httpPort = System.getProperty("org.osgi.service.http.port");
		try {
			this.http.registerServlet("/SOAPTunneling", new SOAPTunnelServlet(this.nmCore),	null, null);
			LOG.info("registring /SOAPTunneling servlet]");
		} catch (Exception e) {
			LOG.error("Error registering soap-tunneling servlet", e);
		}
	}

	@Deactivate
	protected void deactivate(ComponentContext context) {
		LOG.info("[de-activating SoapTunnel]");
		//HttpService http = (HttpService) context.locateService("HttpService");
		// String httpPort = System.getProperty("org.osgi.service.http.port");
		try {
			this.http.unregister("/SOAPTunneling");
		} catch (Exception e) {
			LOG.error("Error unregistering servlet", e);
		}	
	}
	
}
