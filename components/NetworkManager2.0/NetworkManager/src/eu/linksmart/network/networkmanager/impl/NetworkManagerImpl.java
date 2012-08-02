package eu.linksmart.network.networkmanager.impl;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDInfo;
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
		public NMResponse sendData(HID sender, HID receiver, byte[] data, boolean synch)
				throws RemoteException {
			return this.core.sendData(sender, receiver, data, synch);
		}

		@Override
		public HID getHID() throws RemoteException {
			return this.core.getHID();
		}

		@Override
		public boolean removeHID(HID hid) throws RemoteException {
			return this.core.removeHID(hid);
		}

		@Override
		public HIDInfo createHID(Part[] attributes, String endpoint, String backboneName)
				throws RemoteException {
			return this.core.createHID(attributes, endpoint, backboneName);
		}

		@Override
		public HIDInfo createCryptoHID(String xmlAttributes, String endpoint) throws RemoteException {
			return this.core.createCryptoHID(xmlAttributes, endpoint);
		}

		@Override
		public HIDInfo createCryptoHIDFromReference(String certRef, String endpoint) throws RemoteException {
			return this.core.createCryptoHIDFromReference(certRef, endpoint);
		}

		@Override
		public String[] getAvailableBackbones() throws RemoteException {
			return this.core.getAvailableBackbones();
		}

		@Override
		public HIDInfo[] getHIDByAttributes(Part[] attributes) {
			return this.core.getHIDByAttributes(attributes);
		}

		@Override
		public HIDInfo[] getHIDByAttributes(Part[] attributes,
				boolean isConjunction) {
			
			return this.core.getHIDByAttributes(attributes, isConjunction);
		}

		@Override
		public HIDInfo getHIDByPID(String PID) throws IllegalArgumentException {
			return this.core.getHIDByPID(PID);
		}

		@Override
		public HIDInfo[] getHIDByDescription(String description) {
			return this.core.getHIDByDescription(description);
		}

		@Override
		public HIDInfo[] getHIDByQuery(String query) {
			return this.core.getHIDByQuery(query);
		}
}
