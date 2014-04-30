package eu.linksmart.security.communication.impl.asym;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.clients.RemoteWSClientProvider;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProperty;
import eu.linksmart.security.communication.SecurityProtocol;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.trustmanager.TrustManager;



/**
 * Provides {@link SecurityProtocol} implementations of
 * specific type to be used for communication protection.
 * @author Vinkovits
 *
 */
public class CommunicationSecurityManagerImpl implements CommunicationSecurityManager{
	private static String COMMUNICATION_SEC_MGR = CommunicationSecurityManagerImpl.class.getSimpleName();
	private static Logger logger = Logger.getLogger(SecurityProtocolAsym.class);

	private BundleContext context = null;
	private CryptoManager cryptoMgr = null;
	private TrustManager trustMgr = null;
	private RemoteWSClientProvider wsProvider = null;
	private CommunicationSecurityManagerConfigurator configurator = null;
	private double trustThreshold;
	private boolean isTrustManagerBundle = false;

	protected void activate(ComponentContext context) {
		this.context = context.getBundleContext();
		configurator = new CommunicationSecurityManagerConfigurator(this, this.context);
		logger.info(COMMUNICATION_SEC_MGR + " started");
	}
	protected void deactivate(ComponentContext context) {
		this.context = null;
		logger.info(COMMUNICATION_SEC_MGR + " stopped");
	}

	protected void bindCryptoManager(CryptoManager cryptoManager){
		cryptoMgr = cryptoManager;
	}

	protected void unbindCryptoManager(CryptoManager cryptoManager){
		cryptoMgr = null;
	}

	protected void bindTrustManager(TrustManager trustManager){
		isTrustManagerBundle = true;
		trustMgr = trustManager;
	}

	protected void unbindTrustManager(TrustManager trustManager){
		trustMgr = null;
		isTrustManagerBundle = false;
	}
	
	protected void bindWSProvider(RemoteWSClientProvider wsProvider){
		this.wsProvider = wsProvider;
	}

	protected void unbindWSProvider(RemoteWSClientProvider wsProvider){
		this.wsProvider = null;
	}
	
	public SecurityProtocol getSecurityProtocol(VirtualAddress clientVirtualAddress, VirtualAddress serverVirtualAddress) {
		SecurityProtocol securityProtocol = new SecurityProtocolAsym(clientVirtualAddress, serverVirtualAddress, cryptoMgr, trustMgr, trustThreshold);
		return securityProtocol;
	}
	public void applyConfigurations(Hashtable updates) {
		if(updates.containsKey(CommunicationSecurityManagerConfigurator.TRUST_THRESHOLD)){
			try{
				trustThreshold = Double.valueOf(configurator.get(CommunicationSecurityManagerConfigurator.TRUST_THRESHOLD));
			}catch(NumberFormatException ne){
				logger.error("Provided trust threshold  is not a double!");
			}
		}
		if(updates.containsKey(CommunicationSecurityManagerConfigurator.TRUST_MANAGER_URL)){
			setTrustManager(configurator.get(CommunicationSecurityManagerConfigurator.TRUST_MANAGER_URL));
		}
	}

	/**
	 * Checks the URL and either gets an OSGi service or
	 * gets from WSProvider a reference to the client stub
	 * @param url URL provided from the configurator
	 */
	private void setTrustManager(String url){
		if(url.equalsIgnoreCase("local")){
			if(!this.isTrustManagerBundle){
				//search for trustmanager service and bind it
				//as using declarative services this should not occur
				ServiceReference sr = context.getServiceReference(TrustManager.class.getName());
				if(sr != null){
					TrustManager tm = (TrustManager)context.getService(sr);
					if(tm != null){
						this.trustMgr = tm;
					} else {
						logger.error("No local TrustManager available");
					}
				} else {
					logger.error("No local TrustManager available");
				}
			} else {
				logger.debug("Already using local TrustManager");
			}
		}else{
			if(this.isTrustManagerBundle){
				//if there is OSGi bundle TrustManager then switching to external TM is not allowed
				logger.warn("There is a local TrustManager available. Change to external is not allowed!");
			}else{
				//get trustmanager service over url
				try {
					this.trustMgr = (TrustManager)this.wsProvider.getRemoteWSClient(TrustManager.class.getName(), 
							url);
				} catch (Exception e) {
					logger.error("Error getting TrustManager service over URL.");
				}
			}
		}
	}
	
	public boolean canBroadcast() {
		return false;
	}

	public SecurityProtocol getBroadcastSecurityProtocol(VirtualAddress clientVirtualAddress) {
		throw new RuntimeException("Manager has no broadcast service");
	}

	public List<SecurityProperty> getProperties() {
		ArrayList<SecurityProperty> properties = new ArrayList<SecurityProperty>();
		properties.add(SecurityProperty.Confidentiality);
		properties.add(SecurityProperty.Asymmetric);
		properties.add(SecurityProperty.Unicast);
		
		return properties;
	}
}