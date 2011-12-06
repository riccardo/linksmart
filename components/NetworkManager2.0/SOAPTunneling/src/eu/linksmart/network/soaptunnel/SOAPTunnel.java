package eu.linksmart.network.soaptunnel;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.network.networkmanager.core.NetworkManagerCore;


/**
 * Component implementation for SOAPTunnel. Registers the servlet etc.
 * 
 * @author simon
 */
public class SOAPTunnel {

	private static Logger LOG = Logger.getLogger(SOAPTunnel.class.getName());
	private NetworkManagerCore nmCore;

	protected void activate(ComponentContext context) {
		HttpService http = (HttpService) context.locateService("HttpService");
		// String httpPort = System.getProperty("org.osgi.service.http.port");

		try {
			http.registerServlet("/SOAPTunneling", new SOAPTunnelServlet(this.nmCore),
					null, null);
		} catch (Exception e) {
			LOG.error("Error registering servlet", e);
		}
	}

	protected void deactivate(ComponentContext context) {
		HttpService http = (HttpService) context.locateService("HttpService");
		// String httpPort = System.getProperty("org.osgi.service.http.port");

		try {
			http.unregister("/SOAPTunneling");
		} catch (Exception e) {
			LOG.error("Error unregistering servlet", e);
		}	
	}
	
	protected void bindNetworkManagerCore(NetworkManagerCore nmCore) {
		this.nmCore = nmCore;
	}
	
	protected void unbindNetworkManagerCore(NetworkManagerCore nmCore) {
		this.nmCore = null;
	}
}
