package eu.linksmart.network.networkmanager.impl;

import java.rmi.RemoteException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.Registration;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.utils.Part;

@Component(name="NetworkManager", immediate=true)
@Service({NetworkManager.class})
public class NetworkManagerImplDummy implements NetworkManager {
	
	@Reference(name="NetworkManagerCore",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindNetworkManagerCore", 
			unbind="unbindNetworkManagerCore",
			policy=ReferencePolicy.DYNAMIC)
	private NetworkManagerCore core;
	
	private static final String CREATED_MESSAGE = "Creating Network Manager components";
	
	Logger LOG = Logger.getLogger(NetworkManagerImplDummy.class.getName());
	
	public NetworkManagerImplDummy() {
		LOG.info(CREATED_MESSAGE);
	}

	protected void bindNetworkManagerCore(NetworkManagerCore core) {
		System.out.println("NetworkManagerDummy::binding network-manager-core");
		this.core = core;
	}
	
	protected void unbindNetworkManagerCore(NetworkManagerCore core) {
		System.out.println("NetworkManagerDummy::un-binding network-manager-core");
		this.core = null;
	}
	
	@Activate
	protected void activate(ComponentContext context) {
		System.out.println("activating NetworkManagerDummy");
	}

	@Deactivate
	protected void deactivate(ComponentContext context) {
		System.out.println("de-activating NetworkManagerDummy");
	}

	@Override
	public NMResponse sendData(VirtualAddress sender, VirtualAddress receiver, byte[] data, boolean synch) throws RemoteException {
		return this.core.sendData(sender, receiver, data, synch);
	}

	@Override
	@Deprecated
	public VirtualAddress getService() throws RemoteException {
		return this.core.getService();
	}
	
	public VirtualAddress getVirtualAddress() throws RemoteException {
		return this.core.getVirtualAddress();
	}

	@Override
	public boolean removeService(VirtualAddress virtualAddress) throws RemoteException {
		return this.core.removeService(virtualAddress);
	}

	@Override
	public Registration registerService(Part[] attributes, String endpoint, String backboneName)
			throws RemoteException {
		return this.core.registerService(attributes, endpoint, backboneName);
	}

	@Override
	public String[] getAvailableBackbones() throws RemoteException {
		return this.core.getAvailableBackbones();
	}

	@Override
	public Registration[] getServiceByAttributes(Part[] attributes) throws RemoteException {
		return this.core.getServiceByAttributes(attributes);
	}

	@Override
	public Registration getServiceByPID(String PID) throws IllegalArgumentException, RemoteException {
		return this.core.getServiceByPID(PID);
	}

	@Override
	public Registration[] getServiceByDescription(String description) throws RemoteException {
		return this.core.getServiceByDescription(description);
	}

	@Override
	public Registration[] getServiceByQuery(String query) throws RemoteException {
		return this.core.getServiceByQuery(query);
	}

	@Override
	public Registration[] getServiceByAttributes(Part[] attributes, long timeOut,
			boolean returnFirst, boolean isStrictRequest) throws RemoteException {
		return this.core.getServiceByAttributes(attributes, timeOut, returnFirst, isStrictRequest);
	}	
	
}
