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
 * Copyright (C) 2006-2010 [Telefonica I+D]
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

/**
 * RouteManagerApplication is an intermediate in the communication chain
 */

package eu.linksmart.network.routing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.osgi.framework.ServiceReference;

import eu.linksmart.jsoaptunnelling.SOAPTunnelServiceBTImpl;
import eu.linksmart.jsoaptunnelling.SOAPTunnelServiceImpl;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.NetworkManagerApplication;
import eu.linksmart.network.backbone.BackboneManagerApplication;
import eu.linksmart.network.impl.NetworkManagerApplicationSoapBindingImpl;
import eu.linksmart.network.impl.NetworkManagerConfigurator;
import eu.linksmart.network.transport.Endpoint;
import eu.linksmart.policy.pep.PepResponse;
import eu.linksmart.policy.pep.PepService;
import eu.linksmart.securecomm.HandshakeHelper;
import eu.linksmart.security.communication.inside.InsideHydra;
import eu.linksmart.security.protocols.net.impl.SecureSessionControllerImpl;

/**
 * Implementation of the Route Manager
 */
public class RouteManagerApplication {

	/** Session ID to set when access has been denied */
	public static final String ACCESS_DENIED_SESSIONID = "-6";

	/** Access denied by PDP message */
	private static final String ACCESS_DENIED_MESSAGE = "Access Denied by Access Policies";	

	private Logger logger = Logger.getLogger(RouteManagerApplication.class.getName());
	private NetworkManagerApplicationSoapBindingImpl nm;



	/** Flag indicating whether to default to deny on PEP responses */
	private boolean defaultToDeny = false;

	/**
	 * Constructor
	 * 
	 * @param nm the Network Manager application
	 */
	public RouteManagerApplication(NetworkManagerApplicationSoapBindingImpl nm) {
		this.nm = nm;
		defaultToDeny = Boolean.valueOf(
				NetworkManagerConfigurator.DEFAULT_TO_DENY_ON_PEP_RESPONSE).booleanValue();
	}

	/**
	 * Implementation of the sendData method of the Network Manager Application.
	 * It is called from NetworkManagerApplicationSoapBindingImpl.
	 * 
	 * @param sessionID The ID of the session related to this communication
	 * @param senderHID The HID of the sender
	 * @param receiverHID The HID of the receiver
	 * @param data The data to send in a String format
	 * @return A long indicating the success of the transmission: 0=Success, -1=Echec
	 */
	public NMResponse sendData(java.lang.String sessionID, 
			java.lang.String senderHID, java.lang.String receiverHID, 
			java.lang.String data) throws java.rmi.RemoteException {

		NMResponse response = new NMResponse();

		/* Forward data : Call receiveData web service. */
		/* Retreiving IP address of the endpoint. */
		String IPAddress = nm.hidMgr.getIPfromHID(new eu.linksmart.types.HID(receiverHID));
		StringTokenizer st = new StringTokenizer(IPAddress, ":");
		IPAddress = st.nextToken();
		int port = Integer.parseInt(st.nextToken());

		System.err.println("Routing sendData...");

		URL urlforward = null;
		try {
			urlforward = new URL("http", IPAddress, port, 
					(String) nm.getConfiguration().get(BackboneManagerApplication.servicePath));
		} catch (MalformedURLException e) {
			logger.fatal(e.getMessage());
			logger.fatal(e.getStackTrace());
			response.setData("Error - Not valid URL");
			response.setSessionID("-21");
			return response;
		}

		/* We use client WS provider service (if available) to load the client stubs. */
		if (nm.wsclientProvider == null) {
			logger.error("OpenSesion: WS client for NM is not available. ");
			response.setSessionID("-1");
			response.setData("OpenSesion: WS client for NM is not available.");
			return response;
		}

		NetworkManagerApplication service;
		try {
			/* Load client stubs. */
			service = (NetworkManagerApplication) nm.wsclientProvider.
			getRemoteWSClient(NetworkManagerApplication.class.getName(),
					urlforward.toString(), 
					//TODO refactoring use this when core security is provided
					//					Boolean.parseBoolean((String) 
					//						nm.getConfiguration().get(NetworkManagerConfigurator.USE_CORE_SECURITY))
					false
			);

			/* Call the service. */
			logger.info("Calling NetworkRouteApplicationManager.receiveData at "
					+ urlforward.toString() + " with data = " + data);
			return service.receiveData(sessionID, senderHID, receiverHID, data);
		} catch (Exception e) {
			logger.error("OpenSession: " + e.getLocalizedMessage());
			e.printStackTrace();
			response.setSessionID("-1");
			response.setData("OpenSesion: WS client for NM is not available.");
			return response;
		}
	}

	/**
	 * Implementation of the receiveData method of the Network Manager Application.
	 * It is called from NetworkManagerApplicationSoapBindingImpl.
	 * 
	 * @param sessionID The ID of the session related to this communication
	 * @param senderHID The HID of the sender
	 * @param receiverHID The HID of the receiver
	 * @param data The data to send in a String format
	 * @return A long indicating the success of the transmission: 0 = Success, -1 = Echec
	 */
	public NMResponse receiveData(java.lang.String sessionID, 
			java.lang.String senderHID, java.lang.String receiverHID, 
			java.lang.String data) throws java.rmi.RemoteException {

		logger.debug("RouteManager.Receive Data reached. This is what I got: " + data);

		NMResponse response = new NMResponse();
		response.setData("Error in receiver receiveData!");
		response.setSessionID(sessionID);
		String udData = data;

		try {
			/*
			 * Look for a LinkSmart:InsideProtectedMessage element and if it 
			 * exists, decrypt it
			 */
			StringBuffer strBuffer = new StringBuffer(udData);
			int startPos = udData.indexOf("<" + InsideHydra.INSIDE_PROTECTED_MESSAGE_NAME);
			int endPos = udData.indexOf("</" + InsideHydra.INSIDE_PROTECTED_MESSAGE_NAME
					+ ">") + InsideHydra.INSIDE_PROTECTED_MESSAGE_NAME.length() + 3;

			//protected payload available
			if (startPos != -1 && endPos != -1) {
				if(nm.getSecurityLibrary() == null)
				{
					logger.error("Received protected message but cannot read it as no InsideSecurity bundle is running!");
					response.setData("Cannot read message as there is no security available.");
					return response;
				}
				String protectedElement = strBuffer.substring(startPos, endPos);
				try {
					String unprotectedElement = nm.getSecurityLibrary().AsDecrypt(protectedElement);
					strBuffer.delete(startPos, endPos);
					if (startPos == strBuffer.length()) {
						strBuffer.append(unprotectedElement);
					}
					else {
						strBuffer.insert(startPos, unprotectedElement);
					}
					udData = strBuffer.toString();
				} catch (Exception e2) {
					logger.error("Error when decrypting", e2);
				}
			}

			logger.trace("NetworkRouteManagerApplication.receiveData called at node = "
					+ receiverHID + " with data = " + data);
			logger.info("Received data from " + senderHID + " to " + receiverHID);
			logger.trace(data);

			/* Contact the PEP */
			ServiceReference pepServiceRef = nm.getContext().getBundleContext().
			getServiceReference(PepService.class.getName());

			try {
				if (pepServiceRef != null) {
					PepService pep = (PepService) nm.getContext().getBundleContext().
					getService(pepServiceRef);

					String receiverAttributes = nm.getInformationAssociatedWithHID(
							senderHID, receiverHID);
					String senderAttributes = nm.getInformationAssociatedWithHID(
							receiverHID, senderHID);

					logger.debug("Requesting access decision from " + senderHID
							+ " to " + receiverHID);

					/*
					 * Use attributes retrieved from CryptoHIDs. If not 
					 * available, use attributes from description
					 */
					PepResponse decision;
					if ((receiverAttributes != null) && (senderAttributes != null)) {
						logger.debug("Using attributes from CryptoHID");
						decision = pep.requestAccessDecision(senderHID, 
								senderAttributes, receiverHID, receiverAttributes, 
								udData, sessionID);
					}
					else {
						ByteArrayOutputStream senderBaos = new ByteArrayOutputStream();
						ByteArrayOutputStream receiverBaos = new ByteArrayOutputStream();

						Properties senderProps = new Properties();
						senderProps.put("PID", nm.getDescriptionbyHID(senderHID));
						senderProps.storeToXML(senderBaos, "");

						Properties receiverProps = new Properties();
						receiverProps.put("PID", nm.getDescriptionbyHID(receiverHID));
						receiverProps.storeToXML(receiverBaos, "");

						logger.debug("Using attributes from description");
						decision = pep.requestAccessDecision(senderHID, 
								senderBaos.toString(), receiverHID, 
								receiverBaos.toString(), udData, sessionID);
					}

					logger.debug("Decision is " + decision.getDecision());

					if (defaultToDeny) {
						if (decision.getDecision() != PepResponse.DECISION_PERMIT) {
							NMResponse resp = new NMResponse();
							resp.setData(ACCESS_DENIED_MESSAGE);
							resp.setSessionID(ACCESS_DENIED_SESSIONID);
							return resp;
						}
					}
					else if (decision.getDecision() == PepResponse.DECISION_DENY) {
						NMResponse resp = new NMResponse();
						resp.setData(ACCESS_DENIED_MESSAGE);
						resp.setSessionID(ACCESS_DENIED_SESSIONID);
						return resp;
					}
				}
				else {
					logger.warn("No PEP service found");
					if (defaultToDeny) {
						NMResponse resp = new NMResponse();
						resp.setData(ACCESS_DENIED_MESSAGE);
						resp.setSessionID(ACCESS_DENIED_SESSIONID);
						return resp;
					}
				}
			} catch (Exception e) {
				logger.error("Error during policy enforcement. Will continue "
						+ "without appliying policies. Error message: " + e.getMessage());
				if (logger.isDebugEnabled()) {
					e.printStackTrace();
				}
			}

			String endpoint = nm.hidMgr.getEndpoint(receiverHID);

			if (endpoint.startsWith("btspp:")) {
				/*
				 * Bluetooth Transport
				 * Check that the BT Transport service exists and invoke the 
				 * service. Will take some time, you know...
				 */
				try {
					Endpoint btEndpoint = (Endpoint) nm.getTransports().get("Bluetooth");
					if (btEndpoint != null) {
						response = (new SOAPTunnelServiceBTImpl(btEndpoint, nm.getContext().
								getBundleContext())).receiveTunnelData(endpoint, udData);
						return createResponseMessage(response, receiverHID, senderHID);
					}
					else {
						response.setData("No BT transport service found");
						logger.error("No BT transport service found");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				SOAPTunnelServiceImpl service = new SOAPTunnelServiceImpl();
				response  = (service.receiveTunnelData(endpoint, udData));
				response.setSessionID(sessionID);

				logger.debug("Calling the SOAP Tunneling \n" + "\n"
						+ "Enpoint for HID = " + receiverHID + " : " + endpoint);
			}

			return createResponseMessage(response, receiverHID, senderHID);
		} catch(Exception e1) {
			logger.error("Exception in receiveData " + e1, e1);
		}

		return response;	
	}

	private NMResponse createResponseMessage(NMResponse responseRaw, String senderHID, String receiverHID){
		if (((String)nm.getConfiguration().get(NetworkManagerConfigurator.SECURITY_PROTOCOL)).
				equalsIgnoreCase("securesession")) {
			if(nm.getSecurityLibrary() == null){
				logger.error("Cannot protect response message although required! Install required bundle or turn of security!");
				responseRaw.setData("Cannot protect response message!");
			}else{
				try {
					String certReference = nm.getCryptoManager().getCertificateReference(receiverHID);

					// Use Secure Handshake to retrieve key if not available
					if(certReference == null) {
						// If key of other NM is not available yet, start SecureHandshake
						certReference = SecureSessionControllerImpl.getInstance().
						getCertificateIdentifier(senderHID, receiverHID);

						if (certReference != null) {
							logger.debug("Secure Handshake received a certificate "
									+ "and stored it as identifier " + certReference);
							nm.getCryptoManager().addCertificateForHID(receiverHID, certReference);
						} else {
							logger.error("Secure connection to " + receiverHID
									+ " could not been set up, aborting send. "
									+ "Check previous log messages for errors.");
							//return error
							NMResponse response = new NMResponse();
							response.setSessionID("-1");
							response.setData("Could not send message encrypted although required."); 
							return response;
						}
					}
					String eData;
					eData = nm.getSecurityLibrary().protectInsideHydra(responseRaw.getData(), receiverHID);
					responseRaw.setData(eData);
					return responseRaw;
				} catch (Exception e) {
					logger.error("Error protecting response",e);
					responseRaw.setData("Cannot protect response message!");
				}
			}
		}
		return responseRaw;
	}

}
