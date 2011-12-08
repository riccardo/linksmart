package eu.linksmart.network.networkmanager.impl;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDInfo;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
	
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
		public NMResponse sendData(HID sender, HID receiver, byte[] data)
				throws RemoteException {
			return this.core.sendData(sender, receiver, data);
		}

		@Override
		public HID getHID() {
			return this.core.getHID();
		}

		@Override
		public Boolean removeHID(HID hid) throws RemoteException {
			return this.core.removeHID(hid);
		}

		@Override
		public HID createHID(Properties attributes, String endpoint, String backboneName)
				throws RemoteException {
			return this.core.createHID(attributes, endpoint, backboneName);
		}

		@Override
		public HIDInfo createCryptoHID(String xmlAttributes) {
			return this.core.createCryptoHID(xmlAttributes);
		}

		@Override
		public HIDInfo createCryptoHIDFromReference(String certRef) {
			return this.core.createCryptoHIDFromReference(certRef);
		}

		@Override
		public List<String> getAvailableBackbones() {
			return this.core.getAvailableBackbones();
		}
}
