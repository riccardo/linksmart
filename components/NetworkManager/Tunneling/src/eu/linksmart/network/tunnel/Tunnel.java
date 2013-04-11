package eu.linksmart.network.tunnel;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.network.networkmanager.core.NetworkManagerCore;

public class Tunnel {

	private static Logger LOG = Logger.getLogger(Tunnel.class.getName());
	private NetworkManagerCore nmCore;

	protected NetworkManagerCore getNM() {
		return this.nmCore;
	}
	
	protected void activate(ComponentContext context) {
		HttpService http = (HttpService) context.locateService("HttpService");

		try {
			http.registerServlet("/Tunneling", new TunnelServlet(this),
					null, null);
		} catch (Exception e) {
			LOG.error("Error registering servlet", e);
		}
	}

	protected void deactivate(ComponentContext context) {
		HttpService http = (HttpService) context.locateService("HttpService");

		try {
			http.unregister("/Tunneling");
		} catch (Exception e) {
			LOG.error("Error unregistering servlet", e);
		}	
	}
	
	protected void bindNetworkManager(NetworkManagerCore nmCore) {
		this.nmCore = nmCore;
	}
	
	protected void unbindNetworkManager(NetworkManagerCore nmCore) {
		this.nmCore = null;
	}

}
