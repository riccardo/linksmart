package eu.linksmart.network.grand.impl;

import java.rmi.RemoteException;
import java.util.List;

import eu.linksmart.network.backbone.data.DataEndpoint;
import eu.linksmart.network.grand.backbone.BackboneGrandImpl;
import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.grand.tunnel.GrandTunnelServlet;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.network.tunnel.BasicTunnelService;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.security.communication.SecurityProperty;
import eu.linksmart.utils.Part;

@Component(name="GrandMessageHandler",immediate=true)
@Service
public class GrandMessageHandlerImpl implements DataEndpoint, Backbone {

	private static Logger LOG = Logger.getLogger(GrandMessageHandlerImpl.class.getName());
	private static final String OSGI_COMPONENT_NAME = "GrandMessageHandler";

	private Backbone backboneGrand = null;
	
	private Registration registration = null;
	private GrandTunnelServlet grandTunnelServlet = null;
	
    @Reference(name="ConfigurationAdmin",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindConfigurationAdmin",
            unbind="unbindConfigurationAdmin",
            policy=ReferencePolicy.STATIC)
    protected ConfigurationAdmin configAdmin = null;
    
    @Reference(name="NetworkManagerCore",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindNetworkManagerCore",
            unbind="unbindNetworkManagerCore",
            policy=ReferencePolicy.DYNAMIC)
	private NetworkManagerCore nmCore;
    
    @Reference(name="BackboneRouter",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindBackboneRouter",
            unbind="unbindBackboneRouter",
            policy= ReferencePolicy.STATIC)
	private BackboneRouter bbRouter = null;
    
    @Reference(name="BasicTunnelService",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindBasicTunnelService",
            unbind="unbindBasicTunnelService",
            policy=ReferencePolicy.DYNAMIC)
	private BasicTunnelService basicTunnelService;
    
    @Reference(name="HttpService",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindHttpService",
            unbind="unbindHttpService",
            policy=ReferencePolicy.STATIC)
    private HttpService httpService;

    protected void bindConfigurationAdmin(ConfigurationAdmin configAdmin) {
    	LOG.debug("GrandMessageHandler::binding ConfigurationAdmin");
        this.configAdmin = configAdmin;
    }

    protected void unbindConfigurationAdmin(ConfigurationAdmin configAdmin) {
    	LOG.debug("GrandMessageHandler::un-binding ConfigurationAdmin");
        this.configAdmin = null;
    }

    protected void bindBackboneRouter(BackboneRouter bbRouter) {
    	LOG.debug("GrandMessageHandler::binding backbone-router");
        this.bbRouter = bbRouter;
    }

    protected void unbindBackboneRouter(BackboneRouter bbRouter) {
    	LOG.debug("GrandMessageHandler::un-binding backbone-router");
        this.bbRouter = null;
    }

    protected void bindHttpService(HttpService httpService) {
    	LOG.debug("GrandMessageHandler::binding http-service");
        this.httpService = httpService;
    }

    protected void unbindHttpService(HttpService httpService) {
    	LOG.debug("GrandMessageHandler::un-binding http-service");
        this.httpService = null;
    }
    
    protected void bindBasicTunnelService(BasicTunnelService basicTunnelService) {
    	LOG.debug("GrandMessageHandler::binding basic-tunneling");
		this.basicTunnelService = basicTunnelService;
	}
	
	protected void unbindBasicTunnelService(BasicTunnelService basicTunnelService) {
		LOG.debug("GrandMessageHandler::un-binding basic-tunneling");
		this.basicTunnelService = null;
	}

	protected void bindNetworkManagerCore(NetworkManagerCore nmCore) {
		LOG.debug("GrandMessageHandler::binding network-manager-core");
		this.nmCore = nmCore;
	}

	protected void unbindNetworkManagerCore(NetworkManagerCore nmCore) {
		LOG.debug("GrandMessageHandler::un-binding network-manager-core");
		this.nmCore = null;
	}

    public NetworkManagerCore getNM() {
		return this.nmCore;
	}
	
	public VirtualAddress getGrandHandlerVAD() {
		return registration.getVirtualAddress();
	}

	public BasicTunnelService getBasicTunnelService() {
		return basicTunnelService;
	}

	@Activate
	protected void activate(ComponentContext context) {
		LOG.info("[activating GrandMessageHandler]");
		//start tunneling
		grandTunnelServlet = new GrandTunnelServlet(this);
		try {
			httpService.registerServlet("/GrandTunneling", grandTunnelServlet, null, null);
			LOG.info("[registering /GrandTunneling servlet]");
		} catch (Exception e) {
			LOG.error("Error registering servlet", e);
		}
		//start backbone
		//bbRouter = (BackboneRouter) context
		//		.locateService(BackboneRouter.class.getSimpleName());
		backboneGrand = new BackboneGrandImpl(context, bbRouter,configAdmin);
		
		//register with network manager
		try {
			String[] backbones = nmCore.getAvailableBackbones();
			String backbone = null;
			for (String b : backbones) {
				if(b.toLowerCase().contains("backbonedata")) {
					backbone = b;
					break;
				}
			}
			if(backbone == null) {
				backbone = "eu.linksmart.network.backbone.impl.data.BackboneData";
			}
			// Register this peer
			Part[] attributes = new Part[] {
					new Part(ServiceAttribute.DESCRIPTION.name(), OSGI_COMPONENT_NAME),
					new Part("LOCATION", nmCore.getVirtualAddress().toString())
			};
			registration = nmCore.registerService(attributes, OSGI_COMPONENT_NAME, backbone);
		} catch (RemoteException e) {
			//local invocation
		}
		
		if(registration == null) {
			LOG.error("Failed to register service " + OSGI_COMPONENT_NAME);
		} else {
			LOG.info("Started: " + OSGI_COMPONENT_NAME);
		}
	}

	@Deactivate
	protected void deactivate(ComponentContext context) {
		LOG.info("[de-activating GrandMessageHandler]");
		try {
			httpService.unregister("/GrandTunneling");
		} catch (Exception e) {
			LOG.error("Error unregistering servlet", e);
		}	
		try {
			nmCore.removeService(registration.getVirtualAddress());
		} catch (RemoteException e) {
			//local invocation
		}
	}
	
	/*
	 * Method to receive data packets.
	 */
	
	@Override
	public byte[] receive(byte[] data, VirtualAddress senderVirtualAddress) {
		grandTunnelServlet.receiveDataPacket(data);
		return null;
	}


	/*
	 * Backbone methods to be forwarded to the web invocator
	 */

	@Override
	public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		return backboneGrand.sendDataSynch(senderVirtualAddress, receiverVirtualAddress, data);
	}

	@Override
	public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		return backboneGrand.sendDataAsynch(senderVirtualAddress, receiverVirtualAddress, data);
	}

	@Override
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		return backboneGrand.receiveDataSynch(senderVirtualAddress, receiverVirtualAddress, data);
	}

	@Override
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		return backboneGrand.receiveDataAsynch(senderVirtualAddress, receiverVirtualAddress, data);
	}

	@Override
	public NMResponse broadcastData(VirtualAddress senderVirtualAddress,
			byte[] data) {
		return backboneGrand.broadcastData(senderVirtualAddress, data);
	}

	@Override
	public String getEndpoint(VirtualAddress virtualAddress) {
		return backboneGrand.getEndpoint(virtualAddress);
	}

	@Override
	public boolean addEndpoint(VirtualAddress virtualAddress, String endpoint) {
		return backboneGrand.addEndpoint(virtualAddress, endpoint);
	}

	@Override
	public boolean removeEndpoint(VirtualAddress virtualAddress) {
		return backboneGrand.removeEndpoint(virtualAddress);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public List<SecurityProperty> getSecurityTypesRequired() {
		return backboneGrand.getSecurityTypesRequired();
	}

	@Override
	public void addEndpointForRemoteService(
			VirtualAddress senderVirtualAddress,
			VirtualAddress remoteVirtualAddress) {
		backboneGrand.addEndpointForRemoteService(senderVirtualAddress, remoteVirtualAddress);
	}

}
