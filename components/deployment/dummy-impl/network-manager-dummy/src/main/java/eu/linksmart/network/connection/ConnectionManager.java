package eu.linksmart.network.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.log4j.Logger;

import eu.linksmart.network.ErrorMessage;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProperty;

/**
 * Manages and creates connections between two services.
 * @author Vinkovits
 *
 */
public class ConnectionManager {
	
	Logger logger = Logger.getLogger(ConnectionManager.class.getName());
	
	protected HashMap<VirtualAddress, List<SecurityProperty>> servicePolicies = new HashMap<VirtualAddress, List<SecurityProperty>>();
	
	private ArrayList<CommunicationSecurityManager> communicationSecurityManagers = new ArrayList<CommunicationSecurityManager>();

	protected static final String HANDSHAKE_COMSECMGRS_KEY = "CommunicationSecurityManagers";
	protected static final String HANDSHAKE_SECPROPS_KEY = "SecurityProperties";
	protected static final String HANDSHAKE_DECLINE = "CommunicationDeclined";
	protected static final String HANDSHAKE_ACCEPT = "CommunicationAccepted";

	private NetworkManagerCore nmCore;
	private IdentityManager idM;
	
	public ConnectionManager(NetworkManagerCore nmCore){
		this.nmCore = nmCore;
	}

	public void setIdentityManager(IdentityManager idM) {
		this.idM = idM;
	}

	public void setCommunicationSecurityManager(CommunicationSecurityManager comSecMgr){
		this.communicationSecurityManagers.add(comSecMgr);
	}

	public void removeCommunicationSecurityManager(CommunicationSecurityManager comSecMgr){
		this.communicationSecurityManagers.remove(comSecMgr);
	}

	public void registerServicePolicy(VirtualAddress regulatedVirtualAddress, List<SecurityProperty> properties, boolean forceChange) {
		this.servicePolicies.put(regulatedVirtualAddress, properties);
	}

	public void removeServicePolicy(VirtualAddress virtualAddress) {
		servicePolicies.remove(virtualAddress);
	}

	public void deleteServicePolicy(VirtualAddress regulatedVirtualAddress){
		this.servicePolicies.remove(regulatedVirtualAddress);
	}

}
