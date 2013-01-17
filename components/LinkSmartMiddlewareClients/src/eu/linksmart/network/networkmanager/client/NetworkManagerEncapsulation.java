package eu.linksmart.network.networkmanager.client;

import java.rmi.RemoteException;

import eu.linksmart.network.client.Registration;
import eu.linksmart.network.client.converter.NMResponseConverter;
import eu.linksmart.network.client.converter.RegistrationConverter;
import eu.linksmart.network.client.converter.VirtualAddressConverter;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;

public class NetworkManagerEncapsulation implements NetworkManager {

	private NetworkManagerPortType nmPortType = null;;
	
	public NetworkManagerEncapsulation(NetworkManagerPortType nmPortType) {
		this.nmPortType = nmPortType;
	}
	
	@Override
	public eu.linksmart.network.NMResponse sendData(
			eu.linksmart.network.VirtualAddress sender,
			eu.linksmart.network.VirtualAddress receiver,
			byte[] data, boolean synch) throws RemoteException {
		eu.linksmart.network.client.NMResponse response = 
			nmPortType.sendData(
					VirtualAddressConverter.toClient(sender),
					VirtualAddressConverter.toClient(receiver), data, synch);
		return NMResponseConverter.toApi(response);
	}

	@Override
	public eu.linksmart.network.VirtualAddress getService() throws RemoteException {
		return VirtualAddressConverter.toApi(nmPortType.getService());
	}

	@Override
	public eu.linksmart.network.Registration registerService(Part[] attributes, String endpoint,
			String backboneName) throws RemoteException {
		return RegistrationConverter.toApi(
				nmPortType.registerService(attributes, endpoint, backboneName));
	}

	@Override
	public boolean removeService(eu.linksmart.network.VirtualAddress virtualAddress)
			throws RemoteException {
		return nmPortType.removeService(
				VirtualAddressConverter.toClient(virtualAddress));
	}

	@Override
	public String[] getAvailableBackbones() throws RemoteException {
		return nmPortType.getAvailableBackbones();
	}

	@Override
	public eu.linksmart.network.Registration[] getServiceByAttributes(Part[] attributes)
			throws RemoteException {
		Registration[] regs = nmPortType.getServiceByAttributes(attributes);
		eu.linksmart.network.Registration[] regsApi = new eu.linksmart.network.Registration[regs.length];
		
		for(int i=0; i<regs.length; i++) {
			regsApi[i] = RegistrationConverter.toApi(regs[i]);
		}
		
		return regsApi;
	}

	@Override
	public eu.linksmart.network.Registration[] getServiceByAttributes(Part[] attributes,
			long timeOut, boolean returnFirst, boolean isStrictRequest) throws RemoteException {		
		Registration[] regs = nmPortType.getServiceByAttributes1(
				attributes, timeOut, returnFirst, isStrictRequest);
		eu.linksmart.network.Registration[] regsApi = 
			new eu.linksmart.network.Registration[regs.length];
		
		for(int i=0; i<regs.length; i++) {
			regsApi[i] = RegistrationConverter.toApi(regs[i]);
		}
		
		return regsApi;
	}

	@Override
	public eu.linksmart.network.Registration getServiceByPID(String PID)
			throws IllegalArgumentException, RemoteException {
		return RegistrationConverter.toApi(nmPortType.getServiceByPID(PID));
	}

	@Override
	public eu.linksmart.network.Registration[] getServiceByDescription(String description)
			throws RemoteException {
		Registration[] regs = nmPortType.getServiceByDescription(description);
		eu.linksmart.network.Registration[] regsApi = 
			new eu.linksmart.network.Registration[regs.length];
		
		for(int i=0; i<regs.length; i++) {
			regsApi[i] = RegistrationConverter.toApi(regs[i]);
		}
		
		return regsApi;
	}

	@Override
	public eu.linksmart.network.Registration[] getServiceByQuery(String query)
			throws RemoteException {
		Registration[] regs = nmPortType.getServiceByQuery(query);
		eu.linksmart.network.Registration[] regsApi = 
			new eu.linksmart.network.Registration[regs.length];
		
		for(int i=0; i<regs.length; i++) {
			regsApi[i] = RegistrationConverter.toApi(regs[i]);
		}
		
		return regsApi;
	}
}
