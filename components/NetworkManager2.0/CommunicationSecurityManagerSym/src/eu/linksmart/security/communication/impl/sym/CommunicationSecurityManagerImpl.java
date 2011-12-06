package eu.linksmart.security.communication.impl.sym;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.clients.RemoteWSClientProvider;
import eu.linksmart.network.HID;
import eu.linksmart.security.communication.CommunicationSecurityManager;
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
	private static String COMMUNICATION_SEC_MGR = 
		CommunicationSecurityManagerImpl.class.getSimpleName();
	/**
	 * Logger for this class
	 */
	private static Logger logger = Logger.getLogger(SecurityProtocolImpl.class);
	/**
	 * BundleContext of this bundle
	 */
	private BundleContext context = null;
	/**
	 * CryptoManager to be used by this bundle for cryptographic operations
	 */
	private CryptoManager cryptoMgr = null;
	/**
	 * TrustManager to be used to verify certificates
	 */
	private TrustManager trustMgr = null;
	/**
	 * WSProvider to get access to remote managers
	 */
	private RemoteWSClientProvider wsProvider = null;
	/**
	 * Webconfiguration provider for this bundle
	 */
	private CommunicationSecurityManagerConfigurator configurator = null;
	/**
	 * Minimal trust required in certificate to be accepted
	 */
	private double trustThreshold;
	/**
	 * Indicates whether TrustManager is running locally
	 */
	private boolean isTrustManagerBundle = false;

	/**
	 * Activate methods which stores bundlecontext
	 * @param context
	 */
	protected void activate(ComponentContext context) {
		this.context = context.getBundleContext();
		this.configurator = new CommunicationSecurityManagerConfigurator(this, this.context);
		logger.info(COMMUNICATION_SEC_MGR + " started");
	}
	
	/**
	 * Deactivate method of bundle which has nothing to do
	 * @param context
	 */
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
	
	public SecurityProtocol getSecurityProtocol(HID clientHID, HID serverHID) {
		SecurityProtocol securityProtocol = new SecurityProtocolImpl(
				clientHID, 
				serverHID, 
				cryptoMgr, 
				trustMgr, 
				trustThreshold);
		return securityProtocol;
	}
	
	public void applyConfigurations(Hashtable updates) {
		if(updates.containsKey(CommunicationSecurityManagerConfigurator.TRUST_THRESHOLD)){
			try{
				trustThreshold = Double.valueOf(
						configurator.get(
								CommunicationSecurityManagerConfigurator.TRUST_THRESHOLD));
			}catch(NumberFormatException ne){
				logger.error("Provided trust threshold  is not a double!");
			}
		}
		if(updates.containsKey(CommunicationSecurityManagerConfigurator.TRUST_MANAGER_URL)){
			setTrustManager(configurator.get(
					CommunicationSecurityManagerConfigurator.TRUST_MANAGER_URL));
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
					this.trustMgr = (TrustManager)this.wsProvider.getRemoteWSClient(
							TrustManager.class.getName(), 
							url, false);
				} catch (Exception e) {
					logger.error("Error getting TrustManager service over URL.");
				}
			}
		}
	}

	public boolean canBroadcast() {
		return false;
	}

	public SecurityProtocol getBroadcastSecurityProtocol(HID clientHID) {
		throw new RuntimeException("Manager has no broadcast service");
	}
}