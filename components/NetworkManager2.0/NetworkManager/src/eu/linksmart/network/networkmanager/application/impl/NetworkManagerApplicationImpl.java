package eu.linksmart.network.networkmanager.application.impl;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.networkmanager.application.NetworkManagerApplication;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
	
	public class NetworkManagerApplicationImpl implements NetworkManagerApplication {
		
		private NetworkManagerCore core;
		
		private static final String CREATED_MESSAGE = "Creating Network Manager components";
		
		Logger LOG = Logger.getLogger(NetworkManagerApplicationImpl.class.getName());
		
		public NetworkManagerApplicationImpl(){
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
		public HID createHID(Properties attributes, URL url)
				throws RemoteException {
			return this.core.createHID(attributes, url);
		}
}
