package eu.linksmart.network.networkmanager.impl;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.Registration;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.utils.Part;
	
	public class NetworkManagerImpl implements NetworkManager {
		
		private NetworkManagerCore core;
		
		private static final String CREATED_MESSAGE = "Creating Network Manager components";
		
		Logger LOG = Logger.getLogger(NetworkManagerImpl.class.getName());
		
		public NetworkManagerImpl(){
			LOG.info(CREATED_MESSAGE);
		}
	
		public void bindNetworkManagerCore(NetworkManagerCore core) {
			this.core = core;
		}
		
		public void unbindNetworkManagerCore(NetworkManagerCore core) {
			this.core = null;
		}

		@Override
		public NMResponse sendData(VirtualAddress sender, VirtualAddress receiver, byte[] data, boolean synch)
				throws RemoteException {
			return this.core.sendData(sender, receiver, data, synch);
		}

		/**
		 * @deprecated getVirtualAddress() should be used instead.
		 */
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
			return this.core.getServiceByAttributes(
					attributes, timeOut, returnFirst, isStrictRequest);
		}
}
