/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
/**
 * Copyright (C) 2006-2010 University of Reading,
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.linksmart.policy.pdp.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.policy.pdp.PolicyDecisionPoint;
import eu.linksmart.utils.Part;

/**
 * <p>PDP component HID manager</p>
 *
 * @author Michael Crouch
 * @author Marco Tiemann
 *
 */
public class LinkSmartServiceManager {

	/** logger */
	private static final Logger logger = Logger.getLogger(LinkSmartServiceManager.class);

	/** PDP PID default prefix */
	private static final String DEFAULT_PDP_PREFIX = "PDP:";

	/** {@link PdpApplication} */
	private PdpApplication pdp = null;

	private Registration registration = null;

	private NetworkManager nm = null;

	private static final String BACKBONE_NAME = "eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl";

	/** OSGI HTTP port property ID */
	private static final String OSGI_PORT_NAME
	= "org.osgi.service.http.port";

	/** preset default HTTP port */
	private static final String DEFAULT_HTTP_PORT = "8082";

	/** PDP endpoint */ 
	private static final String ENDPOINT 
	= "http://localhost:9090/cxf/services/PolicyDecisionPoint";

	/** HTTP communication port */
	private String httpPort = DEFAULT_HTTP_PORT;

	private static final String SID = PolicyDecisionPoint.class.getName();

	private static final int NR_PID_ATTEMPTS = 3;

	private static Object registerLock = new Object();

	//FIXME either empty this list regularly or find a fix for the problem
	/*This is a workaround variable to avoid rotating configurations */
	private Map<String, List<String>> tempConfigurationStore = new HashMap<String, List<String>>(); 

	/**
	 * Constructor
	 * 
	 * @param thePdp
	 * 				the {@link PdpApplication}
	 * 
	 */
	public LinkSmartServiceManager(PdpApplication thePdp, NetworkManager nm) {
		pdp = thePdp;
		this.nm = nm;
	}

	protected void init() throws IOException {
		httpPort = System.getProperty(OSGI_PORT_NAME);
		if (httpPort == null) {
			// only necessary when httpPort gets set to null directly above
			httpPort = DEFAULT_HTTP_PORT;
		}

		@SuppressWarnings("rawtypes")
		Dictionary configuration = pdp.getConfigurator().getConfiguration();
		String cert = "";
		String desc = "";
		String pid = ""; 
		boolean renewCert = false;
		if (configuration != null) {
			cert = (String) configuration.get(
					PdpConfigurator.PDPSERVICE_CERT_REF);
			desc = (String) configuration.get(
					PdpConfigurator.PDPSERVICE_DESCRIPTION);
			renewCert = Boolean.parseBoolean((String) 
					configuration.get(PdpConfigurator.RENEW_CERTS));
			pid = (String) configuration.get(PdpConfigurator.PDP_PID);
		}
		registerService(desc, pid, SID, cert, renewCert);
		//		Thread thread = new Thread(new ServiceRegistrator(desc, pid, SID, cert, renewCert));
		//		thread.start();
		logger.info("policy-pdp:service manager is initialized");
	}

	protected VirtualAddress getVirtualAddress() {
		if(registration != null) {
			return registration.getVirtualAddress();
		} else {
			return null;
		}
	}



	/**
	 * @param theSid
	 * 				the SID
	 * @param theEndPoint
	 * 				the endpoint location 
	 * @return
	 * 				the HID
	 * @throws RemoteException 
	 * @throws UnknownHostException thrown if host name is not resolvable
	 */
	public synchronized void registerService(String theDesc, String thePid, String theSid, String theCertRef, boolean renewcert) throws RemoteException, UnknownHostException {
		String pid = thePid;
		String sid = theSid;
		String desc = theDesc;
		String certref = theCertRef;
		//the same configuration was used previously so we ignore it
		if(isInTempConfiguration(ServiceAttribute.PID.name(), pid) || isInTempConfiguration(ServiceAttribute.CERT_REF.name(), certref)) {
			return;
		} else {
			if(theCertRef != null) {
				addTempConfigurationItem(ServiceAttribute.CERT_REF.name(), theCertRef);
			}
			if(thePid != null) {
				addTempConfigurationItem(ServiceAttribute.PID.name(), thePid);
			}
		}
		if(registration != null) {
			//check whether registration should be renewed
			boolean renew = false;
			for(Part attribute : registration.getAttributes()) {
				//change value if requested else read previous one
				if(attribute.getKey().equals(ServiceAttribute.PID.name())) {
					if(thePid != null && !thePid.equals(attribute.getValue().equals(thePid))) {
						renew = true;
					} else {
						pid = attribute.getValue();
					}
				}
				if(attribute.getKey().equals(ServiceAttribute.SID.name())) {
					if(theSid != null && !theSid.equals(attribute.getValue())) {
						renew = true;
					} else {
						sid = attribute.getValue();
					}
				}
				if(attribute.getKey().equals(ServiceAttribute.DESCRIPTION.name())) {
					if(theDesc != null && !theDesc.equals(attribute.getValue())) {
						renew = true;
					} else {
						desc = attribute.getValue();
					}
				}
				if(attribute.getKey().equals(ServiceAttribute.CERT_REF.name())) {
					if(theCertRef != null && !theCertRef.equalsIgnoreCase("null") && !theCertRef.equals(attribute.getValue())) {
						renew = true;
					} else {
						certref = attribute.getValue();
					}
				}
			}
			if(renew || renewcert) {
				if(unregisterService()) {
					registration = null;					
				} else {
					logger.warn("Could not deregister previous service, therefore did not change registration!");
					return;
				}
			} else {
				//no new service registration required
				return;
			}
		}
		synchronized(registerLock) {
			if(registration != null) {
				//some thread has made a registration, we cancel this thread
				return;
			}

			Map<String, Part> attrs = new HashMap<String, Part>(); 
			attrs.put(ServiceAttribute.DESCRIPTION.name(), new Part(ServiceAttribute.DESCRIPTION.name(), desc));
			String pidaux = pid;
			if(pid != null && !pid.equals("") && !pid.equals(DEFAULT_PDP_PREFIX)) {
				attrs.put(ServiceAttribute.PID.name(), new Part(ServiceAttribute.PID.name(), pid));
			} else {
				//if no PID set use local IP as identifier
				pidaux = "PDP:" + InetAddress.getLocalHost().getHostName();
				attrs.put(ServiceAttribute.PID.name(), new Part(ServiceAttribute.PID.name(), pidaux));
			}
			if(sid != null) {
				attrs.put(ServiceAttribute.SID.name(),new Part(ServiceAttribute.SID.name(), sid));
			}
			if(certref != null && !certref.toLowerCase().equals("null") && !renewcert) {
				attrs.put(ServiceAttribute.CERT_REF.name(),new Part(ServiceAttribute.CERT_REF.name(), certref));
			}

			// variables for the attempts to get a PID
			int attempts = NR_PID_ATTEMPTS;
			boolean havePID=false;

			// Try to get a PID
			while (!havePID && attempts>1) {
				try{
					registration = nm.registerService(
							attrs.values().toArray(new Part[]{}), ENDPOINT, BACKBONE_NAME);
					if(registration == null) {
						//this is probably caused by the illegal certificate reference
						attrs.remove(ServiceAttribute.CERT_REF.name());
						registration = nm.registerService(
								attrs.values().toArray(new Part[]{}), ENDPOINT, BACKBONE_NAME);
					}
					havePID= true;
				}catch(IllegalArgumentException ex){
					attempts--;
					// generate selected PID + a random UUID
					attrs.put(ServiceAttribute.PID.name(), new Part(ServiceAttribute.PID.name(), pid + java.util.UUID.randomUUID()));
				}
			}

			//			//save obtained PID to configuration
			//			if(!attrs.get(ServiceAttribute.PID.name()).equals(pid)) {
			//				pdp.getConfigurator().setConfiguration(
			//						PdpConfigurator.PDP_PID, attrs.get(ServiceAttribute.PID.name()).getValue());
			//			}
			//			//store certificate reference
			//			for(Part part : registration.getAttributes()) {
			//				if(part.getKey().equals(ServiceAttribute.CERT_REF.name())) {
			//					if(attrs.containsKey(ServiceAttribute.CERT_REF.name()) &&
			//							!part.getValue().equals(attrs.get(ServiceAttribute.CERT_REF.name()).getValue())) {
			//						pdp.getConfigurator().setConfiguration(PdpConfigurator.PDPSERVICE_CERT_REF, part.getValue());
			//					}
			//					break;
			//				}
			//			}
			// If it didn't obtain a selected PID after the attempts 
			if (!havePID || registration== null) {
				logger.error(
						"PID already in use, and the attemps to generate a new one fail. Please choose a different one.");
			}
		}
	}

	/**
	 * Deregisters the PDP service
	 * 
	 * @return
	 * 				success indicator flag
	 * @throws RemoteException 
	 */
	public boolean unregisterService() throws RemoteException {
		synchronized(registerLock) {
			if(registration != null) {
				return nm.removeService(this.registration.getVirtualAddress());
			} else {
				return true;
			}
		}
	}

	/**
	 * Returns URL for making calls through NetworkManager SoapTunnelling to the  
	 * given Hid
	 * 
	 * @param theRecHid
	 * 				the target HID
	 * @param theSessionId
	 * 				the session ID
	 * @return
	 * 				the URL string
	 */
	private String resSoapTunnelAddr(VirtualAddress theRecVad) {
		return "http://localhost:" + httpPort + "/SOAPTunneling/" + getVirtualAddress() + "/" 
				+ theRecVad + "/";
	}

	protected boolean isRegistered() {
		return registration!=null;
	}

	private synchronized void addTempConfigurationItem(String key, String item) {
		if(!tempConfigurationStore.containsKey(key)) {
			tempConfigurationStore.put(key, new ArrayList<String>());
		}
		if(!tempConfigurationStore.get(key).contains(item)) {
			tempConfigurationStore.get(key).add(item);
		}
	}

	private synchronized boolean isInTempConfiguration(String key, String item) {
		if(tempConfigurationStore.containsKey(key)) {
			return tempConfigurationStore.get(key).contains(item);
		} else {
			return false;
		}
	}

	private class ServiceRegistrator implements Runnable {

		private String theDesc = null;
		private String thePid = null;
		private String theSid = null;
		private String theCertRef = null;
		private boolean renewcert;

		private ServiceRegistrator(String theDesc, String thePid, String theSid, String theCertRef, boolean renewcert) {
			this.theDesc = theDesc;
			this.thePid = thePid;
			this.theSid = theSid;
			this.theCertRef = theCertRef;
			this.renewcert = renewcert;
		}

		public void run() {
			try {
				registerService(theDesc, thePid, theSid, theCertRef, renewcert);
			} catch (RemoteException e) {
				//cannot do anything about it - should also not occur locally
			} catch (UnknownHostException e) {
				logger.error("Wanted to retrive host name because of not provided PID, but was not able to!", e);
			}
			logger.debug("Created PolicyDecisionPoint VAD: " + registration.getVirtualAddressAsString());
		}
	}
}
