package eu.linksmart.network.backbone.impl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.backbone.data.DataEndpoint;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.SecurityProperty;

public class BackboneData implements Backbone {

	private BackboneRouter bRouter;

	private Logger LOG = Logger.getLogger(BackboneData.class.getName());

	private static final String ENDPOINT_UNREACHABLE = "Unknown endpoint";

	private ComponentContext context;

	protected void bindBackboneRouter(BackboneRouter router) {
		bRouter = router;
	}

	protected void unbindBackboneRouter(BackboneRouter router) {
		bRouter = null;
	}

	// Maps the LS virtual address to OSGi "service.id" property
	private Map<VirtualAddress, String> addressEndpointMap = new ConcurrentHashMap<VirtualAddress, String>();

	// Maps the LS virtual address to OSGi service
	private Map<VirtualAddress, DataEndpoint> addressServiceMap = new ConcurrentHashMap<VirtualAddress, DataEndpoint>();

	protected void addDataEndpoint(DataEndpoint endpoint, Map properties) {
		addDataEndpoint(endpoint, properties.get(Constants.SERVICE_ID)
				.toString());
	}

	protected void addDataEndpoint(DataEndpoint endpoint, String id) {
		addressEndpointMap.put(endpoint.getVirtualAddress(), id);
		addressServiceMap.put(endpoint.getVirtualAddress(), endpoint);
	}

	protected void removeDataEndpoint(DataEndpoint endpoint) {
		addressEndpointMap.remove(endpoint.getVirtualAddress());
		addressServiceMap.remove(endpoint.getVirtualAddress());
	}

	@Override
	public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		NMResponse r = executeServiceCall(receiverVirtualAddress, data);
		return r;
	}

	@Override
	public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		// make call look asynchronous by return the status and separately
		// sending response
		NMResponse resp = executeServiceCall(receiverVirtualAddress, data);
		if (bRouter != null) {
			Thread sender = new Thread(new ResponseSender(senderVirtualAddress,
					receiverVirtualAddress, resp));
			sender.start();
		}
		return new NMResponse(NMResponse.STATUS_SUCCESS);
	}

	private class ResponseSender implements Runnable {
		NMResponse response;
		private VirtualAddress senderVirtualAddress;
		private VirtualAddress receiverVirtualAddress;

		protected ResponseSender(VirtualAddress senderVirtualAddress,
				VirtualAddress receiverVirtualAddress, NMResponse response) {
			this.senderVirtualAddress = senderVirtualAddress;
			this.receiverVirtualAddress = receiverVirtualAddress;
			this.response = response;
		}

		public void run() {
			bRouter.sendDataAsynch(senderVirtualAddress,
					receiverVirtualAddress, response.getMessage().getBytes());
		}
	}

	private NMResponse executeServiceCall(
			VirtualAddress receiverVirtualAddress, byte[] data) {
		NMResponse resp = new NMResponse(NMResponse.STATUS_ERROR);

		if (!this.addressServiceMap.containsKey(receiverVirtualAddress)) {
			resp.setMessage(ENDPOINT_UNREACHABLE);
			return resp;
		}
		addressServiceMap.get(receiverVirtualAddress).receive(data);
		return new NMResponse(NMResponse.STATUS_SUCCESS);
	}

	@Override
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		NMResponse r = null;
		if (bRouter != null) {
			bRouter.receiveDataSynch(senderVirtualAddress,
					receiverVirtualAddress, data, this);
			r = new NMResponse(NMResponse.STATUS_SUCCESS);
		} else {
			r = new NMResponse(NMResponse.STATUS_ERROR);
			r.setMessage("No BackboneRouter available");
		}
		return r;
	}

	@Override
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		if (bRouter != null) {
			bRouter.receiveDataAsynch(senderVirtualAddress,
					receiverVirtualAddress, data, this);
			return new NMResponse(NMResponse.STATUS_SUCCESS);
		} else {
			NMResponse response = new NMResponse(NMResponse.STATUS_ERROR);
			response.setMessage("No BackboneRouter available");
			return response;
		}
	}

	@Override
	public NMResponse broadcastData(VirtualAddress senderVirtualAddress,
			byte[] data) {
		boolean success = true;
		try {
			for (VirtualAddress a : addressServiceMap.keySet())
				addressServiceMap.get(a).receive(data);
		} catch (Exception e) {
			success = false;
		}
		if (success) {
			return new NMResponse(NMResponse.STATUS_SUCCESS);
		} else {
			return new NMResponse(NMResponse.STATUS_ERROR);
		}
	}

	/**
	 * Retrieves the "service.id" property as service's unique end-point within
	 * the local OSGi registry. Applies to {@link DataEndpoint} services only.
	 */
	@Override
	public String getEndpoint(VirtualAddress virtualAddress) {
		// Filter according to availability of the service itself
		if (addressServiceMap.containsKey(virtualAddress))
			return this.addressEndpointMap.get(virtualAddress);
		return null;
	}

	/**
	 * Registers an end-point only when it is a valid "service.id" property of a
	 * {@link DataEndpoint} service registered within the service registry.
	 */
	@Override
	public boolean addEndpoint(VirtualAddress virtualAddress, String endpoint) {
		DataEndpoint service = null;
		if (!addressServiceMap.containsKey(virtualAddress)) {
			try {
				String filter = "(service.id=" + endpoint + ")";
				ServiceReference[] ref = context.getBundleContext()
						.getServiceReferences(DataEndpoint.class.getName(),
								filter);

				if (ref != null && ref.length > 0)
					service = (DataEndpoint) context.getBundleContext()
							.getService(ref[0]);

			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
			if (service != null
					&& service.getVirtualAddress().equals(virtualAddress)) {
				addDataEndpoint(service, endpoint);
				return true;
			}
			return false;
		}
		// Endpoint already registered
		return true;
	}

	@Override
	public boolean removeEndpoint(VirtualAddress virtualAddress) {
		if (addressServiceMap.containsKey(virtualAddress)) {
			DataEndpoint endpoint = addressServiceMap.get(virtualAddress);
			removeDataEndpoint(endpoint);
			addressEndpointMap.remove(virtualAddress);
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return BackboneData.class.getName();
	}

	@Override
	public List<SecurityProperty> getSecurityTypesRequired() {
		ArrayList<SecurityProperty> secProps = new ArrayList<SecurityProperty>();
		secProps.add(SecurityProperty.NoEncoding);
		secProps.add(SecurityProperty.NoSecurity);
		return secProps;
	}

	@Override
	public void addEndpointForRemoteService(
			VirtualAddress senderVirtualAddress,
			VirtualAddress remoteVirtualAddress) {
		this.addressEndpointMap.put(remoteVirtualAddress,
				this.addressEndpointMap.get(senderVirtualAddress));
	}

	protected void activate(ComponentContext context) {
		this.context = context;
		this.addressEndpointMap = new ConcurrentHashMap<VirtualAddress, String>();
		LOG.info("BackboneData started");
	}

	protected void deactivate(ComponentContext context) {
		LOG.info("BackboneData stopped");
	}

}
