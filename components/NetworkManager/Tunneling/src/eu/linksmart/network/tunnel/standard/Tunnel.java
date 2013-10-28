package eu.linksmart.network.tunnel.standard;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.tunnel.BasicTunnelService;

public class Tunnel {

	private static Logger LOG = Logger.getLogger(Tunnel.class.getName());
	protected NetworkManagerCore nmCore;
	protected BasicTunnelService basicTunnelService;

	protected NetworkManagerCore getNM() {
		return this.nmCore;
	}

	public BasicTunnelService getBasicTunnelService() {
		return basicTunnelService;
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
	
	protected void bindBasicTunneling(BasicTunnelService basicTunnelService) {
		this.basicTunnelService = basicTunnelService;
	}
	
	protected void unbindBasicTunneling(BasicTunnelService basicTunnelService) {
		this.basicTunnelService = null;
	}

}
