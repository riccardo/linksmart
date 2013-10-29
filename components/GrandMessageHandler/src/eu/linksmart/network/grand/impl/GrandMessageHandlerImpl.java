package eu.linksmart.network.grand.impl;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.grand.backbone.BackboneGrandImpl;
import eu.linksmart.network.grand.tunnel.GrandTunnelServlet;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.network.tunnel.BasicTunnelService;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.backbone.data.DataEndpoint;
import eu.linksmart.security.communication.SecurityProperty;
import eu.linksmart.utils.Part;

public class GrandMessageHandlerImpl implements DataEndpoint, Backbone {

	private static Logger LOG = Logger.getLogger(GrandMessageHandlerImpl.class.getName());
	private static final String OSGI_COMPONENT_NAME = "GrandMessageHandler"; 

	private NetworkManagerCore nmCore;
	private Backbone backboneGrand = null;
	private BackboneRouter bbRouter = null;
	private Registration registration = null;
	private GrandTunnelServlet grandTunnelServlet = null;
	private BasicTunnelService basicTunnelService;

	public NetworkManagerCore getNM() {
		return this.nmCore;
	}
	
	public VirtualAddress getGrandHandlerVAD() {
		return registration.getVirtualAddress();
	}

	public BasicTunnelService getBasicTunnelService() {
		return basicTunnelService;
	}

	protected void activate(ComponentContext context) {
		//start tunneling
		HttpService http = (HttpService) context.locateService("HttpService");
		grandTunnelServlet = new GrandTunnelServlet(this);
		try {
			http.registerServlet("/GrandTunneling", grandTunnelServlet,
					null, null);
		} catch (Exception e) {
			LOG.error("Error registering servlet", e);
		}
		//start backbone
		bbRouter = (BackboneRouter) context
				.locateService(BackboneRouter.class.getSimpleName());
		backboneGrand = new BackboneGrandImpl(context, bbRouter);

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
			LOG.info("Started component " + OSGI_COMPONENT_NAME);
		}
	}

	protected void deactivate(ComponentContext context) {
		HttpService http = (HttpService) context.locateService("HttpService");

		try {
			http.unregister("/GrandTunneling");
		} catch (Exception e) {
			LOG.error("Error unregistering servlet", e);
		}	
		try {
			nmCore.removeService(registration.getVirtualAddress());
		} catch (RemoteException e) {
			//local invocation
		}
	}
	
	protected void bindBasicTunnelService(BasicTunnelService basicTunnelService) {
		this.basicTunnelService = basicTunnelService;
	}
	
	protected void unbindBasicTunnelService(BasicTunnelService basicTunnelService) {
		this.basicTunnelService = null;
	}

	protected void bindNetworkManagerCore(NetworkManagerCore nmCore) {
		this.nmCore = nmCore;
	}

	protected void unbindNetworkManagerCore(NetworkManagerCore nmCore) {
		this.nmCore = null;
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
