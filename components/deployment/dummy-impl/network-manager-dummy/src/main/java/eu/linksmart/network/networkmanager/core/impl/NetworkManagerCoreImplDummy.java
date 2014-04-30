package eu.linksmart.network.networkmanager.core.impl;

import java.rmi.RemoteException;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.linksmart.network.Message;
import eu.linksmart.network.MessageDistributor;
import eu.linksmart.network.MessageProcessor;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.connection.ConnectionManager;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProperty;
import eu.linksmart.tools.GetNetworkManagerStatus;
import eu.linksmart.utils.Part;

@Component(name="NetworkManagerCore", immediate=true)
@Service({NetworkManagerCore.class})
public class NetworkManagerCoreImplDummy implements NetworkManagerCore, MessageDistributor {
	
	@Reference(name="ConfigurationAdmin",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindConfigAdmin", 
			unbind="unbindConfigAdmin",
			policy=ReferencePolicy.STATIC)
	protected ConfigurationAdmin configAdmin = null;
	
	@Reference(name="HttpService",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindHttpService", 
			unbind="unbindHttpService", 
			policy=ReferencePolicy.STATIC)
	protected HttpService http = null;
	
	@Reference(name="IdentityManager",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindIdentityManager", 
			unbind="unbindIdentityManager",
			policy=ReferencePolicy.DYNAMIC)
	protected IdentityManager identityManager = null;
	
	@Reference(name="CommunicationSecurityManager",
			cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
			bind="bindCommunicationSecurityManager", 
			unbind="unbindCommunicationSecurityManager", 
			policy=ReferencePolicy.DYNAMIC)
	protected CommunicationSecurityManager communicationSecurityManager = null;
	
	@Reference(name="BackboneRouter",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindBackboneRouter", 
			unbind="unbindBackboneRouter",
			policy=ReferencePolicy.DYNAMIC)
	protected BackboneRouter backboneRouter = null;
	
	protected ConnectionManager connectionManager = new ConnectionManager(this);
	protected VirtualAddress myVirtualAddress;
	protected String myDescription;

	private static String NETWORK_MGR_ENDPOINT = "http://localhost:9090/cxf/services/NetworkManager";

	Logger LOG = Logger.getLogger(NetworkManagerCoreImplDummy.class.getName());

	private NetworkManagerCoreConfigurator configurator;
	
	protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
		System.out.println("NetworkManagerCoreDummy::binding configadmin");
		this.configAdmin = configAdmin;
    }
    
    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
    	System.out.println("NetworkManagerCoreDummy::un-binding configadmin");
    	this.configAdmin = null;
    }
    
    protected void bindHttpService(HttpService http) {
    	System.out.println("NetworkManagerCoreDummy::binding httpservice");
    	this.http = http;
    }
    
    protected void unbindHttpService(HttpService http) {
    	System.out.println("NetworkManagerCoreDummy::un-binding httpservice");
    	this.http = null;
    }

	protected void bindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr) {
		System.out.println("NetworkManagerCoreDummy::binding security-manager");
		this.communicationSecurityManager = commSecMgr;
		this.connectionManager.setCommunicationSecurityManager(communicationSecurityManager);
	}

	protected void unbindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr) {
		System.out.println("NetworkManagerCoreDummy::un-binding security-manager");
		this.connectionManager.removeCommunicationSecurityManager(communicationSecurityManager);
		this.communicationSecurityManager = null;
	}

	protected void bindIdentityManager(IdentityManager identityManager) {
		System.out.println("NetworkManagerCoreDummy::binding identity-manager");
		this.identityManager = identityManager;
		this.connectionManager.setIdentityManager(identityManager);
	}

	protected void unbindIdentityManager(IdentityManager identityMgr) {
		System.out.println("NetworkManagerCoreDummy::un-binding identity-manager");
		this.identityManager = null;
		this.connectionManager.setIdentityManager(null);
	}

	protected void bindBackboneRouter(BackboneRouter backboneRouter) {
		System.out.println("NetworkManagerCoreDummy::binding backbone-router");
		this.backboneRouter = backboneRouter;
	}

	protected void unbindBackboneRouter(BackboneRouter backboneRouter) {
		System.out.println("NetworkManagerCoreDummy::un-binding backbone-router");
		this.backboneRouter = null;
	}
	
	@Activate
	protected void activate(ComponentContext context) {
		System.out.println("[activating NetworkManagerCoreDummy]");
		init(context);
	}
	
	@Deactivate
	protected void deactivate(ComponentContext context) {
		System.out.println("de-activating NetworkManagerCoreDummy");
	}
	
	private void init(ComponentContext context) {
		this.configurator = new NetworkManagerCoreConfigurator(this, context.getBundleContext(), this.configAdmin);
		this.configurator.registerConfiguration();
		this.myDescription = this.configurator.get(NetworkManagerCoreConfigurator.NM_DESCRIPTION);
		Part[] attributes = { new Part(ServiceAttribute.DESCRIPTION.name(),	this.myDescription) };

		try {
			this.myVirtualAddress = registerService(attributes, NETWORK_MGR_ENDPOINT, "eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl").getVirtualAddress();
		} catch (RemoteException e) {
			LOG.error("PANIC - RemoteException thrown on local access of own method", e);
		} catch (Exception e) {
			LOG.error("Error creating registraiton for NetworkManager. This will cause serious problems!", e);
		}

		try {
			http.registerServlet("/GetNetworkManagerStatus", new GetNetworkManagerStatus(this, identityManager, backboneRouter), null, null);
			http.registerResources("/files", "/resources", null);
		} catch (ServletException e) {
			LOG.error("Error registering servlets", e);
		} catch (NamespaceException e) {
			LOG.error("Error registering servlet namespace", e);
		} catch (Exception e) {
			LOG.error("Error registering servlets", e);
		}
	}

	@Override
	@Deprecated
	public VirtualAddress getService() {
		return this.myVirtualAddress;
	}

	@Override
	public VirtualAddress getVirtualAddress() {
		return this.myVirtualAddress;
	}

	@Override
	public Registration registerService(Part[] attributes, String endpoint, String backboneName) throws RemoteException {
		Registration newRegistration = this.identityManager.createServiceByAttributes(attributes);
		List<SecurityProperty> properties = this.backboneRouter.getBackboneSecurityProperties(backboneName);
		// register VirtualAddress with backbone policies in connection manager
		this.connectionManager.registerServicePolicy(newRegistration.getVirtualAddress(), properties, true);
		// add route to selected backbone
		this.backboneRouter.addRouteToBackbone(newRegistration.getVirtualAddress(), backboneName, endpoint);
		return newRegistration;
	}

	@Override
	public boolean removeService(VirtualAddress virtualAddress) throws RemoteException {
		Boolean virtualAddressRemoved = this.identityManager.removeService(virtualAddress);
		this.connectionManager.deleteServicePolicy(virtualAddress);
		this.backboneRouter.removeRoute(virtualAddress, null);
		return virtualAddressRemoved;
	}
	
	@Override
	public Registration[] getServiceByAttributes(Part[] attributes) {
		return identityManager.getServiceByAttributes(attributes, IdentityManager.SERVICE_RESOLVE_TIMEOUT, false, false);
	}

	@Override
	public Registration[] getServiceByQuery(String query) {
		return identityManager.getServicesByAttributes(query);
	}

	@Override
	public Registration[] getServiceByAttributes(Part[] attributes, long timeOut, boolean returnFirst, boolean isStrictRequest) {
		return identityManager.getServiceByAttributes(attributes, timeOut, returnFirst, isStrictRequest);
	}
	
	@Override
	public Registration[] getServiceByDescription(String description) {
		Part part_description = new Part(ServiceAttribute.DESCRIPTION.name(), description);
		return getServiceByAttributes(new Part[] { part_description });
	}

	@Override
	public Registration getServiceByPID(String PID) throws IllegalArgumentException {
		Part part_description = new Part(ServiceAttribute.PID.name(), PID);
		Registration[] registrations = getServiceByAttributes(new Part[] { part_description });
		return registrations[0];
	}
	
	public void updateDescription(String description) {
	}
	
	@Override
	public String[] getAvailableBackbones() {
		List<String> backbones = this.backboneRouter.getAvailableBackbones();
		String[] backboneNames = new String[backbones.size()];
		return backbones.toArray(backboneNames);
	}

	@Override
	public void updateSecurityProperties(List<VirtualAddress> virtualAddressesToUpdate, List<SecurityProperty> properties) {
	}
	
	@Override
	public void addRemoteVirtualAddress(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress) {
	}
	
	@Override
	public NMResponse sendData(VirtualAddress sender, VirtualAddress receiver, byte[] data, boolean synch) throws RemoteException {
		NMResponse response = new NMResponse();
		response.setStatus(NMResponse.STATUS_SUCCESS);
		response.setMessage("HTTP/1.1 200 OK\r\n" +
				"Content-Encoding: gzip\r\n" +
				"Connection: Keep-Alive\r\n" +
				"Transfer-Encoding: chunked\r\n" +
				"Content-Type: text/html; charset=UTF-8\r\n\r\nBla");
		return response;
	}
	
	@Override
	public NMResponse sendMessage(Message message, boolean synch) {
		return null;
	}

	@Override
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
		return null;
	}

	@Override
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
		return null;
	}

	@Override
	public NMResponse broadcastMessage(Message message) {
		return null;
	}
	
	@Override
	public void subscribe(String topic, MessageProcessor observer) {
	}

	@Override
	public void unsubscribe(String topic, MessageProcessor observer) {
	}
}
