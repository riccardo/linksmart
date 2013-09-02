package eu.linksmart.network.backbone.impl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
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

	// Maps the LS virtual address to OSGi "component.name" property
	private Map<VirtualAddress, String> addressEndpointMap = new ConcurrentHashMap<VirtualAddress, String>();

	@Override
	public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		NMResponse r = executeServiceCall(senderVirtualAddress,
				receiverVirtualAddress, data);
		return r;
	}

	@Override
	public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		// make call look asynchronous by return the status and separately
		// sending response
		return executeServiceCall(senderVirtualAddress, receiverVirtualAddress,
				data);
	}

	private NMResponse executeServiceCall(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		NMResponse resp = new NMResponse(NMResponse.STATUS_ERROR);

		if (!this.addressEndpointMap.containsKey(receiverVirtualAddress)) {
			resp.setMessage(ENDPOINT_UNREACHABLE);
			return resp;
		}

		// Try to resolve service and pass data
		DataEndpoint service = resolveEndpointComponent(receiverVirtualAddress);
		if (service == null)
			// Error
			return resp;

		// service.receive(data);
		byte[] respData = service.receive(data, senderVirtualAddress);

		// store result of method call (non-blocking by contract)
		resp.setStatus(NMResponse.STATUS_SUCCESS);
		resp.setBytesPrimary(true);
		resp.setMessageBytes(respData);
		return resp;
	}

	private DataEndpoint resolveEndpointComponent(VirtualAddress virtualAddress) {

		String endpoint = addressEndpointMap.get(virtualAddress);

		DataEndpoint service = null;

		if (endpoint != null) {
			try {
				// Assuming usage of SCR for DataEndpoints
				String filter = "(component.name=" + endpoint + ")";

				ServiceReference[] ref = context.getBundleContext()
						.getServiceReferences(DataEndpoint.class.getName(),
								filter);

				// Should be exactly one, if at all.
				if (ref != null && ref.length > 0)
					service = (DataEndpoint) context.getBundleContext()
							.getService(ref[0]);

			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
		return service;
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

	// Clarify exclusion of JXTA broadcast messages sent via this method
	private NMResponse _broadcastData(VirtualAddress senderVirtualAddress,
			byte[] data) {
		boolean success = true;
		try {
			for (VirtualAddress a : addressEndpointMap.keySet()) {
				DataEndpoint service = resolveEndpointComponent(a);
				if (a != null)
					// service.receive(data);
					service.receive(data, senderVirtualAddress);
			}
		} catch (Exception e) {
			success = false;
		}
		if (success) {
			return new NMResponse(NMResponse.STATUS_SUCCESS);
		} else {
			return new NMResponse(NMResponse.STATUS_ERROR);
		}
	}

	@Override
	public NMResponse broadcastData(VirtualAddress senderVirtualAddress,
			byte[] data) {
		return new NMResponse(NMResponse.STATUS_SUCCESS);
	}

	/**
	 * Retrieves the "component.name" property as components's unique,
	 * user-given end-point identifier within the local OSGi registry.
	 */
	@Override
	public String getEndpoint(VirtualAddress virtualAddress) {
		return addressEndpointMap.get(virtualAddress);
	}

	/**
	 * Registers a mapping of the LS virtual address to the {@link DataEndpoint}
	 * 's component name.
	 */
	@Override
	public boolean addEndpoint(VirtualAddress virtualAddress, String endpoint) {
		if (endpoint != null) {
			/*
			 * TODO: test for endpoint's existence. Tricky, since DataEndpoint
			 * (always) registered within DS "activate" method. At this time it
			 * is not registered/resolvable yet. I.e. resolveEndpointComponent()
			 * will return null!
			 */
			addressEndpointMap.put(virtualAddress, endpoint);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeEndpoint(VirtualAddress virtualAddress) {
		if (addressEndpointMap.containsKey(virtualAddress)) {
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
