package eu.linksmart.network.backbone.impl.osgi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.scr.annotations.*;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.SecurityProperty;

@Component(name="BackboneOSGI")
@Service
public class BackboneOsgiImplDummy implements Backbone {
	
	private Map<VirtualAddress, String> addressEndpointMap = null;
	
    @Reference(name="BackboneRouter",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindBackboneRouter",
            unbind="unbindBackboneRouter",
            policy= ReferencePolicy.STATIC)
	private BackboneRouter bRouter;
    
    protected void bindBackboneRouter(BackboneRouter bbRouter) {
    	System.out.println("BackboneOsgiDummy::binding backbone-router");
        this.bRouter = bbRouter;
    }

    protected void unbindBackboneRouter(BackboneRouter bbRouter) {
    	System.out.println("BackboneOsgiDummy::un-binding backbone-router");
        this.bRouter = null;
    }

    @Activate
    protected void activate (ComponentContext context) {
    	System.out.println("[activating BackboneOsgiDummy]");
		this.addressEndpointMap = new ConcurrentHashMap<VirtualAddress, String>();
	}

    @Deactivate
	protected void deactivate (ComponentContext context) {
		System.out.println("de-activating BackboneOsgiDummy");
	}

    @Override
	public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		return new NMResponse(NMResponse.STATUS_SUCCESS);
	}

	@Override
	public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		return new NMResponse(NMResponse.STATUS_SUCCESS);	
	}

	@Override
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		bRouter.receiveDataSynch(senderVirtualAddress, receiverVirtualAddress, data, this);
		return new NMResponse(NMResponse.STATUS_SUCCESS);
	}
		
	@Override
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		bRouter.receiveDataAsynch(senderVirtualAddress,	receiverVirtualAddress,	data, this);
		return new NMResponse(NMResponse.STATUS_SUCCESS);
	}

	@Override
	public NMResponse broadcastData(VirtualAddress senderVirtualAddress, byte[] data) {
		return new NMResponse(NMResponse.STATUS_SUCCESS);
	}

	@Override
	public String getEndpoint(VirtualAddress virtualAddress) {
		return this.addressEndpointMap.get(virtualAddress);
	}

	@Override
	public boolean addEndpoint(VirtualAddress virtualAddress, String endpoint) {
		this.addressEndpointMap.put(virtualAddress, endpoint);
		return true;
	}

	@Override
	public boolean removeEndpoint(VirtualAddress virtualAddress) {
		this.addressEndpointMap.containsKey(virtualAddress);
		return true;
	}

	@Override
	public String getName() {
		return BackboneOsgiImplDummy.class.getName();
	}

	@Override
	public List<SecurityProperty> getSecurityTypesRequired() {
		ArrayList<SecurityProperty> secProps = new ArrayList<SecurityProperty>();
		secProps.add(SecurityProperty.NoEncoding);
		secProps.add(SecurityProperty.NoSecurity);
		return secProps;
	}

	@Override
	public void addEndpointForRemoteService(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress) {
		this.addressEndpointMap.put(remoteVirtualAddress, this.addressEndpointMap.get(senderVirtualAddress));
	}
}
