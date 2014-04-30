package eu.linksmart.network.identity.impl;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.Message;
import eu.linksmart.network.MessageProcessor;
import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.utils.Part;

public class IdentityManagerImplDummy implements IdentityManager, MessageProcessor {
	
	protected static String IDENTITY_MGR = IdentityManagerImplDummy.class.getSimpleName();

	protected static Logger LOG = Logger.getLogger(IDENTITY_MGR);

	protected ConcurrentHashMap<VirtualAddress, Registration> localServices;
	protected ConcurrentHashMap<VirtualAddress, Registration> remoteServices;
	
	protected NetworkManagerCore networkManagerCore;
	
	protected CryptoManager cryptoManager;
	
	protected void setNetworkManagerCore(NetworkManagerCore networkManagerCore) {
		this.networkManagerCore = networkManagerCore;
	}
	
	protected void unsetNetworkManagerCore(NetworkManagerCore networkManagerCore) {
		this.networkManagerCore = null;
	}
	
	protected void setCryptoManager(CryptoManager cryptoManager) {
		this.cryptoManager = cryptoManager;
	}
	
	protected void unsetCryptoManager(CryptoManager cryptoManager) {
		this.cryptoManager = null;
	}

	protected void activate(ComponentContext context) {
		init();
	}

	protected void deactivate(ComponentContext context) {
	}
	
	protected void init() {
		this.localServices = new ConcurrentHashMap<VirtualAddress, Registration>();
		this.remoteServices = new ConcurrentHashMap<VirtualAddress, Registration>();
	}

	@Override
	public Registration createServiceByAttributes(Part[] attributes) {
		System.out.println("creating registration for attribute: " + attributes[0].getKey());
		VirtualAddress virtualAddress = new VirtualAddress();
		Registration registration = new Registration(virtualAddress, attributes);
		if (!localServices.containsKey(virtualAddress)) {
			localServices.put(virtualAddress, registration);
		}
		System.out.println("Created VirtualAddress: " + registration.toString());
		return registration;
	}

	@Override
	public Registration createServiceByDescription(String description) {
		System.out.println("creating registration for description: " + description);
		Part[] attributes = { new Part(ServiceAttribute.DESCRIPTION.name(), description)};
		return createServiceByAttributes(attributes);
	}
	
	@Override
	public boolean updateServiceInfo(VirtualAddress virtualAddress, Properties attr) {
		return true;
	}

	@Override
	public Registration getServiceInfo(VirtualAddress virtualAddress) {
		return localServices.get(virtualAddress);
	}
	
	@Override
	public Set<Registration> getAllServices() {
		return getLocalServices();
	}

	@Override
	public Set<Registration> getLocalServices() {
		return new HashSet<Registration>(localServices.values());
	}

	@Override
	public Set<Registration> getRemoteServices() {
		return new HashSet<Registration>(remoteServices.values());
	}

	@Override
	public Registration[] getServiceByAttributes(Part[] attributes, long timeOut, boolean returnFirst, boolean isStrict) {
		Set<Registration> serviceInfos = new HashSet<Registration>(localServices.values());
		Registration[] serviceInfoRet = new Registration[serviceInfos.size()];
		serviceInfos.toArray(serviceInfoRet);
		return serviceInfoRet;
	}

	@Override
	public Set<Registration> getServicesByDescription(String description) {
		return new HashSet<Registration>(localServices.values());
	}

	@Override
	public Registration[] getServicesByAttributes(String query) {
		Set<Registration> services = new HashSet<Registration>(localServices.values());
		Registration[] serviceInfos = new Registration[services.size()];
		services.toArray(serviceInfos);
		return serviceInfos;
	}

	@Override
	public boolean removeService(VirtualAddress virtualAddress) {
		if (localServices.containsKey(virtualAddress)) {
			localServices.remove(virtualAddress);
			System.out.println("Removed VirtualAddress: " + virtualAddress.toString());
			return true;
		}
		return false;
	}

	@Override
	public String getIdentifier() {
		return IDENTITY_MGR;
	}
	
	@Override
	public Message processMessage(Message msg) {
		return msg;
	}
}
