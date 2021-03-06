package eu.linksmart.network.networkmanager.port.rest;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.apache.felix.scr.annotations.*;

import eu.linksmart.network.networkmanager.NetworkManager;

@Component(name="eu.linksmart.network.networkmanager.port.rest", immediate=true)
public class NetworkManagerRestPort {

	private static Logger LOG = Logger.getLogger(NetworkManagerRestPort.class.getName());

    @Reference(name="NetworkManager",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindNetworkManager",
            unbind="unbindNetworkManager",
            policy= ReferencePolicy.DYNAMIC)
	protected NetworkManager nmCore;

    @Reference(name="HttpService",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindHttpServlet",
            unbind="unbindHttpServlet",
            policy=ReferencePolicy.STATIC)
    private HttpService http;

	protected NetworkManager getNM() {
		return this.nmCore;
	}

    @Activate
	protected void activate(ComponentContext context) {
        try {
			http.registerServlet("/NetworkManager", new NetworkManagerRestPortServlet(this),
					null, null);
		} catch (Exception e) {
			LOG.error("Error registering servlet", e);
		}
	}

    @Deactivate
	protected void deactivate(ComponentContext context) {
		HttpService http = (HttpService) context.locateService("HttpService");

		try {
			http.unregister("/NetworkManager");
		} catch (Exception e) {
			LOG.error("Error unregistering servlet", e);
		}	
	}

	protected void bindNetworkManager(NetworkManager nmCore) {
		this.nmCore = nmCore;
	}

	protected void unbindNetworkManager(NetworkManager nmCore) {
		this.nmCore = null;
	}

    protected void bindHttpServlet(HttpService http) {
        LOG.debug("StandardTunnelService::binding http-service");
        this.http = http;
    }

    protected void unbindHttpServlet(HttpService http) {
        LOG.debug("StandardTunnelService::un-binding http-service");
        this.http = null;
    }
}
