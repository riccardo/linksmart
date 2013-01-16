package eu.linksmart.network.networkmanager.client;

import java.rmi.RemoteException;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;

public class NetworkManagerEncapsulation implements NetworkManager {

	private NetworkManagerPortType nmPortType = null;;
	
	public NetworkManagerEncapsulation(NetworkManagerPortType nmPortType) {
		this.nmPortType = nmPortType;
	}
	
	@Override
	public NMResponse sendData(VirtualAddress sender, VirtualAddress receiver,
			byte[] data, boolean synch) throws RemoteException {
		return nmPortType.sendData(sender, receiver, data, synch);
	}

	@Override
	public VirtualAddress getService() throws RemoteException {
		return nmPortType.getService();
	}

	@Override
	public Registration registerService(Part[] attributes, String endpoint,
			String backboneName) throws RemoteException {
		return nmPortType.registerService(attributes, endpoint, backboneName);
	}

	@Override
	public boolean removeService(VirtualAddress virtualAddress)
			throws RemoteException {
		return nmPortType.removeService(virtualAddress);
	}

	@Override
	public String[] getAvailableBackbones() throws RemoteException {
		return nmPortType.getAvailableBackbones();
	}

	@Override
	public Registration[] getServiceByAttributes(Part[] attributes)
			throws RemoteException {
		return nmPortType.getServiceByAttributes(attributes);
	}

	@Override
	public Registration[] getServiceByAttributes(Part[] attributes,
			long timeOut, boolean returnFirst, boolean isStrictRequest) throws RemoteException {
		return nmPortType.getServiceByAttributes1(attributes, timeOut, returnFirst, isStrictRequest);
	}

	@Override
	public Registration getServiceByPID(String PID)
			throws IllegalArgumentException, RemoteException {
		return nmPortType.getServiceByPID(PID);
	}

	@Override
	public Registration[] getServiceByDescription(String description)
			throws RemoteException {
		return nmPortType.getServiceByDescription(description);
	}

	@Override
	public Registration[] getServiceByQuery(String query)
			throws RemoteException {
		return nmPortType.getServiceByQuery(query);
	}
}
