package eu.linksmart.network.tunnel.standard;

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
import eu.linksmart.network.tunnel.BasicTunnelService;

@Component(name="Tunneling", immediate=true)
public class Tunnel {

	private static Logger LOG = Logger.getLogger(Tunnel.class.getName());
	
	@Reference(name="NetworkManagerCore",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindNetworkManagerCore", 
			unbind="unbindNetworkManagerCore",
			policy=ReferencePolicy.DYNAMIC)
	protected NetworkManagerCore nmCore;
	
	@Reference(name="BasicTunnelService",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindBasicTunneling", 
			unbind="unbindBasicTunneling",
			policy=ReferencePolicy.DYNAMIC)
	protected BasicTunnelService basicTunnelService;
	
	@Reference(name="HttpService",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindHttpServlet", 
			unbind="unbindHttpServlet", 
			policy=ReferencePolicy.STATIC)
	private HttpService http;
	
	protected void bindNetworkManagerCore(NetworkManagerCore nmCore) {
		LOG.debug("StandardTunnelService::binding network-manager-core");
		this.nmCore = nmCore;
	}
	
	protected void unbindNetworkManagerCore(NetworkManagerCore nmCore) {
		LOG.debug("StandardTunnelService::un-binding network-manager-core");
		this.nmCore = null;
	}
	
	protected void bindBasicTunneling(BasicTunnelService basicTunnelService) {
		LOG.debug("StandardTunnelService::binding basic-tunneling");
		this.basicTunnelService = basicTunnelService;
	}
	
	protected void unbindBasicTunneling(BasicTunnelService basicTunnelService) {
		LOG.debug("StandardTunnelService::un-binding basic-tunneling");
		this.basicTunnelService = null;
	}
	
	protected void bindHttpServlet(HttpService http) {
		LOG.debug("StandardTunnelService::binding http-service");
		this.http = http;
	}
	
	protected void unbindHttpServlet(HttpService http) {
		LOG.debug("StandardTunnelService::un-binding http-service");
		this.http = null;
	}

	@Activate
	protected void activate(ComponentContext context) {
		LOG.info("[activating StandardTunnelService]");
		//HttpService http = (HttpService) context.locateService("HttpService");
		try {
			this.http.registerServlet("/Tunneling", new TunnelServlet(this), null, null);
			LOG.info("[registering /Tunneling servlet]");
		} catch (Exception e) {
			LOG.error("Error registering servlet", e);
		}
	}

	@Deactivate
	protected void deactivate(ComponentContext context) {
		LOG.info("[de-activating StandardTunnelService]");
		//HttpService http = (HttpService) context.locateService("HttpService");
		try {
			this.http.unregister("/Tunneling");
		} catch (Exception e) {
			LOG.error("Error unregistering servlet", e);
		}	
	}
	
	protected NetworkManagerCore getNM() {
		return this.nmCore;
	}

	public BasicTunnelService getBasicTunnelService() {
		return basicTunnelService;
	}
	
}
