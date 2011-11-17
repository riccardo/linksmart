package eu.linksmart.network.networkmanager.core.impl;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.connection.ConnectionManager;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.application.NetworkManagerApplication;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.network.service.registry.ServiceRegistry;
	
	public class NetworkManagerApplicationImpl implements NetworkManagerApplication {
		
		private static final String CREATED_MESSAGE = "Creating Network Manager components";
		
		Logger LOG = Logger.getLogger(NetworkManagerApplicationImpl.class.getName());
	
		protected HID myHID;
	
		protected IdentityManager identityManager;
	
		protected BackboneRouter backboneRouter;
		
		protected ConnectionManager connectionManager;

		protected String myDescription;

		private ServiceRegistry serviceRegistry;
	
		public NetworkManagerApplicationImpl(){
			LOG.info(CREATED_MESSAGE);
		}
	
		@Override
		public HID getHID() {
			return this.myHID;
		}
	
		@Override
		public HID createHID(Properties attributes, URL url) throws RemoteException {
			
			HID newHID = this.identityManager.createHID(attributes);
			
			this.serviceRegistry.registerService(newHID, url);
			
			return newHID;
		}
	
		@Override
		public NMResponse sendData(HID sender, HID receiver, byte[] data)
		throws RemoteException {
			
			NMResponse response = this.backboneRouter.sendData(sender,receiver,data);
			
			return response;
		}

		@Override
		public Boolean removeHID(HID hid) throws RemoteException {
						
			Boolean serviceRemoved =this.serviceRegistry.removeService(hid);
			
			Boolean hidRemoved = this.identityManager.removeHID(hid);
			
			return (serviceRemoved && hidRemoved);
		}

}
