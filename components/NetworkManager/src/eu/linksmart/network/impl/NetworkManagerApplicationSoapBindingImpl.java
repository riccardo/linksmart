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
 * Network Manager Implementation class. It contains the implementation of the 
 * interfaces of the Network Manager and is responsible of the instantiation of 
 * the Network Manager sub managers
 */

package eu.linksmart.network.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.axis.SimpleChain;
import org.apache.axis.configuration.BasicClientConfig;
import org.apache.axis.transport.http.CommonsHTTPSender;
import org.apache.log4j.Logger;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.clients.RemoteWSClientProvider;
import eu.linksmart.jsoaptunnelling.SOAPTunnelServlet;
import eu.linksmart.network.CryptoHIDResult;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.NetworkManagerApplication;
import eu.linksmart.network.backbone.BackboneManagerApplication;
import eu.linksmart.network.backbone.PipeSyncHandler;
import eu.linksmart.network.backbone.SocketHandler;
import eu.linksmart.network.identity.HIDManagerApplication;
import eu.linksmart.network.routing.RouteManagerApplication;
import eu.linksmart.network.session.Session;
import eu.linksmart.network.session.SessionManagerApplication;
import eu.linksmart.network.session.SessionSync;
import eu.linksmart.network.transport.Endpoint;
import eu.linksmart.securecomm.HandshakeHelper;
import eu.linksmart.security.communication.inside.InsideHydra;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.protocols.net.impl.SecureSessionControllerImpl;
import eu.linksmart.security.trustmanager.TrustManager;
import eu.linksmart.tools.GetNetworkManagerStatus;
import eu.linksmart.tools.JarUtil;
import eu.linksmart.tools.NetworkManagerApplicationStatus;
import eu.linksmart.types.HID;

/**
 * Implementation of the Network Manager
 */
public class NetworkManagerApplicationSoapBindingImpl 
implements eu.linksmart.network.NetworkManagerApplication {

	private Logger logger = Logger.getLogger(
			NetworkManagerApplicationSoapBindingImpl.class.getName());
	final static private String SEEDSPATH = "NetworkManager/config/seeds.txt";
	final static private String SEEDSJARPATH = "seeds.txt";
	final static private String NMSECPATH = "NetworkManager/Security/crypto.properties";
	final static private String NMSECJARPATH = "crypto.properties";
	final static public String CONFIGFOLDERPATH = "NetworkManager/config";
	final static public String SECFOLDERPATH = "NetworkManager/Security";
	public final static Hashtable<String, String> HashFilesExtract = new 
	Hashtable<String, String>();

	static {
		HashFilesExtract.put(SEEDSPATH, SEEDSJARPATH);
		HashFilesExtract.put(NMSECPATH, NMSECJARPATH);
	}

	public TrustManager trustManager = null;
	public CryptoManager cryptoManager = null;
	public InsideHydra secLib;

	// Network submanagers
	public HIDManagerApplication hidMgr;
	public SessionManagerApplication sessionMgr;
	public BackboneManagerApplication backboneMgr;
	public RouteManagerApplication routeMgr;
	public PipeSyncHandler pipeSyncHandler;
	public SocketHandler socketHandler;
	public SessionSync sessionSync; 

	public ComponentContext context;
	private ServiceRegistration nmServiceReg;

	private HttpService http;
	private String httpPort;

	private HashMap<String, Endpoint> transport;
	public RemoteWSClientProvider wsclientProvider;
	private boolean activated = false;
	private NetworkManagerConfigurator configurator;
	private boolean trustManagerOsgi = false;

	protected static boolean geoLocatorAcceded = false;
	protected static String lastKnownPosition;

	/**
	 * Instantiates the network manager submanagers and 
	 * registrates the servlets
	 */
	public void init() {
		configurator = new NetworkManagerConfigurator(this,
				context.getBundleContext());
		hidMgr = new HIDManagerApplication(this);
		sessionMgr = new SessionManagerApplication(this);
		backboneMgr = new BackboneManagerApplication(this);
		routeMgr = new RouteManagerApplication(this);
		pipeSyncHandler = new PipeSyncHandler(this);
		sessionSync = new SessionSync(this);
		socketHandler = new SocketHandler(this);

		configurator.registerConfiguration();

		logger.debug("Network Manager Activated");
		if (configurator.get(NetworkManagerConfigurator.SECURITY_PROTOCOL).
				equals("securesession")) {
			logger.debug("Secure Session Protocol active");
		}

		// Servlets Registration
		http = (HttpService) context.locateService("HttpService");
		httpPort = System.getProperty("org.osgi.service.http.port");
		try {
			http.registerServlet("/SOAPTunneling", 
					new SOAPTunnelServlet(this), null, null);
			http.registerServlet("/NetworkManagerStatus",
					new NetworkManagerApplicationStatus(context, this), null, null);
			http.registerServlet("/GetNetworkManagerStatus", 
					new GetNetworkManagerStatus(context, this) , null, null);
			http.registerResources("/files", "/resources", null);
		} catch (Exception e) {
			logger.error("Error registering servlets", e);
		}

		createSecureSession();
	}

	/**
	 * Activate method
	 * 
	 * @param context the bundle's execution context
	 */
	protected void activate(ComponentContext context) {
		this.context = context;

		try {
			JarUtil.createFolder(SECFOLDERPATH);
			JarUtil.createFolder(CONFIGFOLDERPATH);
			JarUtil.extractFilesJar(HashFilesExtract);
		} catch (IOException e) {
			logger.error("Needed folder has not been created...");
		}

		init();
		activated  = true;
	}

	/**
	 * Deactivate method
	 * 
	 * @param context the bundle's context
	 */
	protected void deactivate(ComponentContext context) {		
		// Unregister servlets
		http.unregister("/SOAPTunneling");
		http.unregister("/NetworkManagerStatus");
		http.unregister("/GetNetworkManagerStatus");

		this.stop();
		configurator.stop();

		System.runFinalization();
		logger.debug("Network Manager Deactivated");
		activated = false;
	}

	/**
	 * Binds the Trust Manager
	 * 
	 * @param trustManagerService the Trust Manager to bind
	 */
	protected void bindTrustManager(TrustManager trustManagerService) {
		this.trustManager = trustManagerService;
		this.trustManagerOsgi = true;

		//initialize inside security
		createSecureSession();
	}

	/**
	 * Unbinds the Trust Manager
	 * 
	 * @param trustManagerService the Trust Manager to unbind
	 */
	protected void unbindTrustManager(TrustManager trustManagerService) {
		//only unbind if osgi bundle is used
		if(this.trustManagerOsgi){
			this.trustManager = null;
		}
		this.trustManagerOsgi = false;
	}

	/**
	 * Binds InsideHydraSecurity
	 * @param insideHydraSecurity to use
	 */
	protected void bindInsideSecurity(InsideHydra insideHydraSecurity){
		this.secLib = insideHydraSecurity;
	}

	/**
	 * Unbinds InsideHydraSecurity
	 * @param insideHydraSecurity to use
	 */
	protected void unbindInsideSecurity(InsideHydra insideHydraSecurity){
		this.secLib = null;
	}

	/**
	 * Binds the Crypto Manager
	 * 
	 * @param cryptoManagerService the Crypto Manager to bind
	 */
	protected void cryptoBind(CryptoManager cryptoManagerService) {
		this.cryptoManager = cryptoManagerService;

		//initialize inside security
		createSecureSession();
	}

	/**
	 * Unbinds the Crypto Manager
	 * 
	 * @param cryptoManagerService the Crypto Manager to unbind
	 */
	protected void cryptoUnbind(CryptoManager cryptoManagerService) {
		this.cryptoManager = null;
	}

	/**
	 * Adds a transport
	 * @param transportRef the transport to add
	 */
	protected void addTransport(ServiceReference transportRef) {
		this.transport.put((String) transportRef.getProperty("Type"), 
				(Endpoint) context.getBundleContext().getService(transportRef));
	}

	/**
	 * Deletes a transport
	 * @param transportRef the transport to delete
	 */
	protected void deleteTransport(ServiceReference transportRef) {
		this.transport.remove((String) transportRef.getProperty("Type"));
	}

	/**
	 * Gets the list of transports
	 * @return the list of transports
	 */
	public HashMap getTransports() {
		return transport;
	}

	/**
	 * Binds a remote WS Client Provider
	 * 
	 * @param wsclientProvider the remote WS client provider to bind
	 */
	protected void bindRemoteWSClientProvider(
			RemoteWSClientProvider wsclientProvider) {

		this.wsclientProvider = wsclientProvider;
		// Here we get all the clients that we need!
	}

	/**
	 * Unbinds a remote WS Client Provider
	 * 
	 * @param transportRef the transport
	 */
	protected void unbindRemoteWSClientProvider(ServiceReference transportRef) {
		this.wsclientProvider = null;
	}

	/**
	 * Gets the configuration
	 * 
	 * @return the configuration
	 */
	public Dictionary getConfiguration() {
		return configurator.getConfiguration();
	}

	/**
	 * Sets the configuration
	 * 
	 * @param key key value
	 * @param value value value
	 */
	public void setConfiguration(String key, String value) {
		configurator.setConfiguration(key, value);
	}

	/**
	 * Binds a configuration
	 * 
	 * @param cm the configuration admin
	 */
	protected void configurationBind(ConfigurationAdmin cm) {
		if (configurator != null) {
			configurator.bindConfigurationAdmin(cm);
			if (activated) {
				configurator.registerConfiguration();
			}
		}
	}

	/**
	 * Unbinds a configuration
	 * 
	 * @param cm the configuration admin
	 */
	protected void configurationUnbind(ConfigurationAdmin cm) {
		if(configurator != null) {
			configurator.unbindConfigurationAdmin(cm);
		}
	}

	/**
	 * Gets the context
	 * 
	 * @return the context
	 */
	public ComponentContext getContext() {
		return context;
	}

	/**
	 * Gets the Crypto Manager
	 * 
	 * @return the Crypto Manager
	 */
	public CryptoManager getCryptoManager() {
		return cryptoManager;
	}

	public InsideHydra getSecurityLibrary(){
		return secLib;
	}

	/**
	 * Gets the HID Manager Application
	 * 
	 * @return the HID Manager Application
	 */
	public HIDManagerApplication getHIDManagerApplication() {
		return hidMgr;
	}

	/**
	 * Gets the pipe synchronization handler
	 * 
	 * @return the pipe synchronization handler
	 */
	public PipeSyncHandler getPipeSyncHandler() {
		return pipeSyncHandler;
	}

	/**
	 * Stop
	 */
	public void stop() {
		socketHandler.stopSockets();
		sessionSync.stopPipes();
		pipeSyncHandler.stopPipes();
		sessionMgr.stopSessionManager();
		backboneMgr.stopJXTA();
		socketHandler = null;
		sessionSync = null;
		pipeSyncHandler = null;
		sessionMgr = null;
		routeMgr = null;
		backboneMgr = null;
		hidMgr = null;
	}



	/**
	 * @return "OK"
	 * @deprecated
	 */
	public String startNM() {
		return "OK";
	};

	/**
	 * @return ""
	 * @deprecated
	 */
	public String stopNM() {
		return "";
	};

	/**
	 * It allows to open a session between two HIDs for data exchange. A session
	 * will be established between the senderHID and the receiverHID.
	 * The sessions by default expires 60000 milliseconds.
	 * 
	 * @param senderHID The HID of the sender
	 * @param receiverHID The HID of the receiver
	 * @return The session identifier (uuid) for the generated session
	 */
	public String openSession(String senderHID, String receiverHID) {
		String mySessionID = "-1";
		String sessionID;

		/* 
		 * If the senderHID (client) is local, this means that the session has 
		 * to be created remotely
		 */
		if (hidMgr.getHostHIDs().contains(senderHID)) {
			sessionID = sessionMgr.sessionIDGenerator();

			if ((configurator.get(NetworkManagerConfigurator.COMMUNICATION_TYPE)).equals("P2P")) {
				sessionSync.createRemoteSessionServer(sessionID, senderHID, receiverHID);
				try {
					addSessionRemoteClient(sessionID, senderHID, receiverHID);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				return sessionID;
			}
			//else if ((configurator.get(NetworkManagerConfigurator.
			//		COMMUNICATION_TYPE)).equals("WS")) { }
			else {
				return "-1";
			}
		}
		else {
			Session mySession = sessionMgr.createSession(senderHID, receiverHID);

			if(mySession != null) {
				sessionID = mySession.getSessionID();
			}
			else {
				// Maximum number of sessions reached
				logger.error("Error - Cannot create session, Maximum number "
						+ "of sessions reached");
				return "-1";
			}

			mySessionID = mySession.getSessionID();
			mySession.saveSession();

			// Retreiving IP address of the endpoint
			if ((configurator.get(NetworkManagerConfigurator.
					COMMUNICATION_TYPE)).equals("P2P")) {
				sessionSync.createRemoteSessionClient(mySessionID, senderHID, receiverHID);
			}
			else if ((configurator.get(NetworkManagerConfigurator.
					COMMUNICATION_TYPE)).equals("WS")) {
				String IPAddress = hidMgr.getIPfromHID(new 
						eu.linksmart.types.HID(senderHID));
				StringTokenizer st = new StringTokenizer(IPAddress, ":");
				IPAddress = st.nextToken();

				/* We add the session on client side also. */
				URL urlforward = null;

				try {
					urlforward = new URL("http", IPAddress,	Integer.parseInt(httpPort),
							configurator.get(BackboneManagerApplication.servicePath));
				} catch (MalformedURLException e) {
					logger.fatal("Could not open a session. Wrong parameters: " 
							+ "http" + " " + IPAddress + " " + httpPort + " " 
							+ configurator.get(BackboneManagerApplication.servicePath));
					logger.fatal(e.getMessage());
					logger.fatal(e.getStackTrace());
					return "-1";
				}

				/*
				 * We use client WS provider service (if available) to load 
				 * the client stubs
				 */
				if (wsclientProvider == null) {
					logger.error("OpenSesion: WS client for NM is not available. ");
					return "-1";
				}

				NetworkManagerApplication service;
				try {
					// Load client stubs
					service = (NetworkManagerApplication) 
					wsclientProvider.getRemoteWSClient(
							NetworkManagerApplication.class.getName(),
							urlforward.toString(), 
							//TODO refactoring use this when core security is provided
							//							Boolean.parseBoolean(configurator.get(
							//								NetworkManagerConfigurator.USE_CORE_SECURITY))
							false
					);

					// Call the service
					service.addSessionRemoteClient(mySessionID, senderHID, receiverHID);
					// Vect will now contain the requested sessionID
				} catch (Exception e) {
					logger.error("OpenSession: " + e.getLocalizedMessage());
					e.printStackTrace();
					return "-1";
				}
			}
			else {
				return "-1";
			}
		}

		return mySessionID;
	}

	/**
	 * Sends data
	 * 
	 * @param sessionID the session id
	 * @param senderHID the sender HID
	 * @param receiverHID the receiver HID
	 * @param data the data to send
	 * @return the NM response
	 * 
	 * @deprecated
	 * Use SOAP tunneling instead
	 */
	public NMResponse sendData(String sessionID, String senderHID, 
			String receiverHID, String data) throws RemoteException  {

		logger.debug("SENDDATA");
		logger.debug("SessionID: " + sessionID);

		NMResponse response = new NMResponse();

		/*
		 * Check if the senderHID has been specified, if not it will use the 
		 * Network Manager HID so it will be possible to receive the response.
		 */
		if (senderHID.equals("0")) {
			senderHID = this.getHostHIDsbyDescription("NetworkManager*")[0];
		}

		// We check if sessionID is valid.
		if(!sessionID.equals("0") && !sessionMgr.isSessionIDvalidClient(sessionID)) {
			logger.warn("Error - Invalid sessionID");
		}

		String seData = data;
		if ((configurator.get(NetworkManagerConfigurator.SECURITY_PROTOCOL)).
				equalsIgnoreCase("securesession")) {
			if(secLib == null){
				logger.error("No InsideSecurity bundle available! Cannot secure connection, please turn off securesession or install needed bundle!");
				//return error
				response.setSessionID("-1");
				response.setData("Could not send message encrypted although required."); 
				return response;
			}
			try {
				/*
				 * Check if public key of receiver is already available
				 */
				String certReference = cryptoManager.getCertificateReference(receiverHID);

				// Use Secure Handshake to retrieve key if not available
				if(certReference == null) {
					// If key of other NM is not available yet, start SecureHandshake
					certReference = SecureSessionControllerImpl.getInstance().
					getCertificateIdentifier(senderHID, receiverHID);

					if (certReference != null) {
						logger.debug("Secure Handshake received a certificate "
								+ "and stored it as identifier " + certReference);
						cryptoManager.addCertificateForHID(receiverHID, certReference);
					} else {
						logger.error("Secure connection to " + receiverHID
								+ " could not been set up, aborting send. "
								+ "Check previous log messages for errors.");
						//return error
						response.setSessionID("-1");
						response.setData("Could not send message encrypted although required."); 
						return response;
					}
				}
				// Divide the http header from the SOAP message
				String[] headers = seData.split("\r\n\r\n");

				// Protect payload
				String eData = "";
				try {
					eData = secLib.protectInsideHydra(headers[1], receiverHID);
					// Add headers again
					seData = headers[0] + "\r\n\r\n" + eData;
				} catch (ArrayIndexOutOfBoundsException e) {
					logger.debug("ArrayIndexException. Here is the "
							+ "data: " + seData, e);
				}
			} catch(Throwable e) {
				logger.error("Exception in sendData, in using security:", e);
			}
		}

		/*
		 * For merging with Pipe communication, we will include here a PropSkip 
		 * call checking for the value of CommunicationType property.
		 */
		if (hidMgr.getHostHIDs().contains(receiverHID)) {
			response = receiveData(sessionID, senderHID, receiverHID, seData);
		}
		else {
			if ((configurator.get(NetworkManagerConfigurator.
					COMMUNICATION_TYPE)).equals("P2P")) {
				logger.info("Sending data over pipe to HID= " + receiverHID);
				response = pipeSyncHandler.sendData(sessionID, senderHID, 
						receiverHID, seData);
			}

			if ((configurator.get(NetworkManagerConfigurator.
					COMMUNICATION_TYPE)).equals("WS")) {
				logger.info("Sending data through WS to HID= " + receiverHID);
				response = routeMgr.sendData(sessionID, senderHID, 
						receiverHID, seData);
			}
		}

		if (response.getSessionID() == "-1") {
			logger.error("There was an error in sending process. Status = "
					+ response.getSessionID() + " Data = " + response.getData());
		}
		else {
			logger.info("Data was sent sucessfully. Status =" 
					+ response.getSessionID());
		}

		// If this is a new session, we update clientSessionsList
		if(sessionID.equals("0") 
				&& !response.getSessionID().equalsIgnoreCase("-1")) {
			sessionMgr.addSessionLocalClient(response.getSessionID(), 
					senderHID, receiverHID);
		}

		//open response message if encrypted
		/*
		 * Look for a LinkSmart:InsideProtectedMessage element and if it 
		 * exists, decrypt it
		 */
		String udData = response.getData();
		StringBuffer strBuffer = new StringBuffer(udData);
		int startPos = udData.indexOf("<" + InsideHydra.INSIDE_PROTECTED_MESSAGE_NAME);
		int endPos = udData.indexOf("</" + InsideHydra.INSIDE_PROTECTED_MESSAGE_NAME
				+ ">") + InsideHydra.INSIDE_PROTECTED_MESSAGE_NAME.length() + 3;

		//protected payload available
		if (startPos != -1 && endPos != -1) {
			String protectedElement = strBuffer.substring(startPos, endPos);
			try {
				String unprotectedElement = secLib.AsDecrypt(protectedElement);
				strBuffer.delete(startPos, endPos);
				if (startPos == strBuffer.length()) {
					strBuffer.append(unprotectedElement);
				}
				else {
					strBuffer.insert(startPos, unprotectedElement);
				}
				udData = strBuffer.toString();
				response.setData(udData);
			} catch (Exception e2) {
				logger.error("Error when decrypting", e2);
			}
		}
		return response;
	}

	/**
	 * Receive data
	 * 
	 * @param sessionID the session id
	 * @param senderHID the sender HID
	 * @param receiverHID the receiver HID
	 * @param data the data
	 * @return the NM response
	 * @deprecated
	 * Use SOAP tunneling instead
	 */
	public NMResponse receiveData(String sessionID, String senderHID, 
			String receiverHID, String data) throws RemoteException {

		logger.debug("receive data called");
		NMResponse response = new NMResponse();

		// SessionID checking
		if(sessionID.equals("0")) {
			// A new session will be created
			Session mySession = sessionMgr.createSession(senderHID, receiverHID);

			if(mySession != null) {
				sessionID = mySession.getSessionID();
			}
			else {
				// Maximum number of sessions reached
				logger.error("Error - Cannot create session, Maximum number "
						+ "of sessions reached");
				response.setSessionID("-1");
				response.setData("Error - Session creation");
				return response;
			}

			// Saving session
			mySession.saveSession();
		}
		else if (!sessionID.equals("-3")) { 
			// We check if the sessionID is known on the server
			boolean sessionLoaded = sessionMgr.loadSession(sessionID);

			// Session ID is not on sessionServerList nor on disk
			if(!sessionMgr.isSessionIDvalidServer(sessionID) && !sessionLoaded) {
				logger.error("Error - Invalid SessionID");
				response.setSessionID("-1");
				response.setData("Error - Invalid SessionID");
				return response;
			}
			else {
				logger.debug("Session " + sessionID + "already exist !");
			}
		}

		// We call receiveData with the right sessionID
		response = routeMgr.receiveData(sessionID, senderHID, receiverHID, data);

		if(response.getSessionID().equals("-1")) {
			logger.error("Error with Routing Manager - receiveData()");
			return response;
		}
		else {
			return response;
		}
	}

	/**
	 * It allows to close a session using a session identifier. If the session
	 * is not closed, it will be closed after the expiration time (by default
	 * 60000 millisecond)
	 * 
	 * @param sessionID The session identifier.
	 */
	public void closeSession(String sessionID) {
		// Distant client
		sessionMgr.removeSessionClient(sessionID);
		sessionMgr.closeSession(sessionID);
	}

	/**
	 * This method allows to get stored data in the session object.
	 * 
	 * @param sessionID The sessionID of the session where the data is stored
	 * @param key The key for the requested parameter
	 * @return The value of the requested parameter or null if the sessionID or
	 * the parameter don't exist.
	 */
	public java.lang.String getSessionParameter(String sessionID, String key) {
		// We check if sessionID is valid
		if(!sessionMgr.isSessionIDvalidServer(sessionID)) {
			logger.error("Error - Invalid sessionID");
			return "-1";
		}
		else {
			return sessionMgr.getSessionParameter(sessionID, key);
		}
	}

	/**
	 * This method allows to store data (key-value pair) in a session object.
	 * 
	 * @param sessionID The sessionID of the session where the data will be stored.
	 * @param key The key on which the data will be stored.
	 * @param value The data to be stored
	 */
	public void setSessionParameter(String sessionID, String key, String value) {
		if(sessionMgr.isSessionIDvalidServer(sessionID)) {
			sessionMgr.setSessionParameter(sessionID, key, value);
		}
		else {
			logger.error("Error - Invalid sessionID");
		}
	}

	/**
	 * Operation to synchronize the clientSessionsList with the
	 * serverSessionsList of this Network Manager.
	 * 
	 * @param senderHID	The HID of the client that need to synchronize its
	 * clientSessionsList with the serverSessionsList of this Network Manager
	 * @param receiverHID The HID of the server HID
	 * @return A vector that contains the sessionID to be deleted
	 */
	public java.util.Vector synchronizeSessionsList(String senderHID, 
			String receiverHID) {
		return sessionMgr.synchronizeSessionsList(senderHID, receiverHID);
	}

	/**
	 * @param sessionID the session ID
	 * @param senderHID the sender HID
	 * @param receiverHID the receiver HID
	 * @deprecated
	 */
	public void addSessionRemoteClient(String sessionID, String senderHID, 
			String receiverHID)	throws RemoteException {		
		sessionMgr.addSessionLocalClient(sessionID, senderHID, receiverHID);
	}

	/**
	 * Operation to create an HID with a predefined contextID and level It calls
	 * the HID Manager for creating the HID and it will be added to the idTable
	 * 
	 * @param contextID	The desired context ID to be created
	 * @param level	The desired level
	 * @return The {@link String} representation of the HID
	 * @deprecated This method will be deleted in the next release
	 */
	public String createHID(long contextID, int level) {
		return hidMgr.createHID(contextID, level).toString();
	}

	/**
	 * Operation to create an HID without any context It calls the Identity
	 * Manager for creating the HID and it will be added to the idTable
	 * 
	 * @return The {@link String} representation of the HID
	 */
	public String createHID() {
		return hidMgr.createHID().toString();
	}

	/**
	 * Operation to create an HID with a predefined contexID and level and
	 * providing a description for the HID (searching purposes) and the endpoint
	 * of the service behind it (for service invocation)
	 * 
	 * @param contextID	The desired context ID to be created
	 * @param level	The desired level
	 * @param description The description associated with this HID
	 * @param endpoint The endpoint of the service (if there is a service behind)
	 * @return The {@link String} representation of the HID
	 * @deprecated It is better to use from now
	 * {@link #createCryptoHID(String xmlAttributes, String endpoint)}
	 */
	public String createHIDwDesc(long contextID, int level, String description,
			String endpoint) throws RemoteException {

		return hidMgr.createHID(contextID, level, description, endpoint,
				null).toString();
	}

	/**
	 * Operation to create an HID providing a description for the HID (searching
	 * purposes) and the endpoint of the service behind it (for service
	 * invocation).
	 * 
	 * @param description The description associated with this HID
	 * @param endpoint The endpoint of the service (if there is a service behind)
	 * @return The {@link String} representation of the HID.
	 * @deprecated It is better to use from now createCryptoHID().
	 */
	public String createHIDwDesc(String description, String endpoint)
	throws RemoteException {
		return hidMgr.createHID(description, endpoint).toString();
	}

	/**
	 * Operation to create an crypto HID providing the persistent attributes for
	 * this HID and the endpoint of the service behind it (for service
	 * invocation). The crypto HID is the enhanced version of HIDs, that allow
	 * to store persistent information on them (through certificates) and
	 * doesn't propagate the information stored on it. In order to exchange the
	 * stored information, the Session Domain Protocol is used. It returns a
	 * certificate reference that point to the certificate generated. The next
	 * time the HID needs to be created, using the same attributes, the
	 * certificate reference can be used.
	 * 
	 * @param xmlAttributes The attributes (persistent) associated with this HID. 
	 * This	attributes are stored inside the certificate and follow the	Java 
	 * {@link java.util.Properties} xml schema.
	 * @param endpoint The endpoint of the service (if there is a service behind).
	 * @return A {@link eu.linksmart.network.ws.CrypyoHIDResult} containing
	 * {@link String} representation of the HID and the certificate reference (UUID)
	 */
	public CryptoHIDResult createCryptoHID(String xmlAttributes, String endpoint) {
		/* 
		 * If Cryptomanager is not bound yet, wait for a short time and try 
		 * to get it directly
		 */
		if (cryptoManager == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}

			if (cryptoManager == null) {
				cryptoManager = (CryptoManager) 
				context.locateService(CryptoManager.class.getName());
			}
		}

		if (cryptoManager != null) {
			HID hid = null;
			Properties attr = new Properties();
			try {
				attr.loadFromXML(new ByteArrayInputStream(xmlAttributes.getBytes()));
				hid = hidMgr.createHID("", endpoint, attr);

				// Provide the attributes and the hid to generate the attributes
				String certRef = cryptoManager.generateCertificateWithAttributes(
						xmlAttributes, hid.toString());

				return new CryptoHIDResult(hid.toString(), certRef);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.warn("CryptoManager not bound or exception occured. Cannot create CryptoHID");
		return null;
	}	

	/**
	 * Operation to create an crypto HID providing a certificate reference (from
	 * a previously created cryptoHID) and an endpoint The crypto HID is the
	 * enhanced version of HIDs, that allow to store persistent information on
	 * them (through certificates)
	 * 
	 * @param certRef The certificate reference from a previously generated 
	 * cryptoHID.
	 * @param endpoint The endpoint of the service (if there is a service behind).
	 * @return The {@link String} representation of the HID.
	 */
	public String createCryptoHIDfromReference(String certRef, String endpoint) {
		ServiceReference cryptoRef = context.getBundleContext().
		getServiceReference(CryptoManager.class.getName());

		if (cryptoRef == null) {
			return null;
		}

		cryptoManager = (CryptoManager) context.getBundleContext().getService(cryptoRef);
		if (cryptoManager == null) {
			return null;
		}

		HID hid = null;
		try {
			Properties attr = cryptoManager.getAttributesFromCertificate(certRef);
			if(attr.size() != 0){
				hid = hidMgr.createHID("", endpoint, attr);
				cryptoManager.addPrivateKeyForHID(hid.toString(), certRef);
				return hid.toString();
			}else{
				logger.warn("Certificate reference does not exist!");
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 

		return null;
	}

	/**
	 * Renews an HID
	 * @param contextID the context ID
	 * @param level the level
	 * @param hid the HID to renew
	 * @return the HID renewed 
	 * @deprecated
	 */
	public String renewHID(long contextID, int level, String hid) {
		HID hid_t = new HID(hid);
		return hidMgr.renewHID(contextID, level, hid_t).toString();
	}

	/**
	 * Operation to modify the attributes associated with an HID. This method 
	 * provides the means for HID owners to modify the attributes associated 
	 * with an already generated HID. In order to avoid security threats, the 
	 * requester has to provide the unique ownerID that is associated with this
	 * HID. This ownerID is given to the HID creator in the HID creation 
	 * response (still to be implemented).
	 * 
	 * @param ownerUUID The owner UUID associated with the HID. For now, use 
	 * just the HID
	 * @param hid The HID to be modified
	 * @param newXMLAttributes The new attributes following the properties 
	 * format. See createCryptoHID for more information about the format of 
	 * this attributes.
	 * @return The result of the operation in {@link boolean} format. Returns 
	 * false if the HID could not be found or the ownerID is not valid.
	 */
	public boolean renewHIDAttributes(String ownerUUID, String hid,
			String newXMLAttributes) throws RemoteException {

		return false;
	}

	/**
	 * Operation to renew the information associated with an HID (description or
	 * endpoint). It can be used for example, to change the transport protocol
	 * for service invocation.
	 * 
	 * @param description The new descritpion (or null if no change is required)
	 * @param endpoint The new endpoint of the service (or null if no change is
	 * required)
	 * @param hid The hid on which the change is requested
	 * @return The {@link String} representation of the HID.
	 */
	public String renewHIDInfo(String description, String endpoint, String hid)
	throws RemoteException {

		return null;
	}

	/**
	 * Operation to change the endpoint associated with this HID. This allows 
	 * protocol switching or any application that wants to modify dynamically 
	 * the endpoint on which the service invocations will be forwarded
	 * 
	 * @param ownerUUID The owner UUID associated with the HID. For now, use 
	 * just the HID
	 * @param hid The HID on which the endpoint needs to be modified
	 * @param endpoint The new endpoint for this HID
	 *			
	 * @return The result of the operation in {@link boolean} format. Returns 
	 * false if the HID could not be found or the ownerID is not valid.
	 */
	public boolean renewHIDEndpoint(String ownerUUID, String hid,
			String endpoint) throws RemoteException {

		return false;
	}

	/**
	 * Adds a context
	 * @param contextID the context HID
	 * @param hid the HID
	 * @return the context
	 * @deprecated
	 */
	public String addContext(long contextID, String hid) {
		HID hid_t = new HID(hid);
		return hidMgr.addContext(contextID, hid_t).toString();
	}

	/**
	 * Operation to retrieve all HIDs in the LinkSmart P2P network
	 * 
	 * @return A {@link java.util.Vector} containing all the HIDs in the LinkSmart
	 * Network
	 */
	public java.util.Vector getHIDs() {
		return hidMgr.getHIDs();
	}

	/**
	 * Operation to retrieve all HIDs in the LinkSmart P2P network in String format
	 * 
	 * @return A {@link String} containing all  HIDs in the LinkSmart P2P network,
	 * separated by commas.
	 */
	public String getHIDsAsString() throws RemoteException {
		String result = "";
		Vector<String> s = hidMgr.getHIDs();

		for (Iterator<String> i = s.iterator(); i.hasNext();) {
			result = result + i.next() + " ";
		}

		return result;
	}

	/**
	 * Operation to retrieve the HIDs associated with this Network Manager
	 * (local HIDs)
	 * 
	 * @return A {@link java.util.Vector} containing all the local HIDs of this
	 * Network Manager.
	 */
	public java.util.Vector getHostHIDs() {
		return hidMgr.getHostHIDs();
	}

	/**
	 * Operation to retrieve the HIDs associated with this Network Manager
	 * (local HIDs) in String format
	 * 
	 * @return A {@link String} containing all the local HIDs of this
	 * Network Manager, separated by commas.
	 */
	public String getHostHIDsAsString() throws RemoteException {
		String result = "";
		Vector<String> s = hidMgr.getHostHIDs();

		for (Iterator<String> i = s.iterator(); i.hasNext();) {
			result = result + i.next() + " ";
		}

		return result;
	}

	/**
	 * Get the context HIDs
	 * @param contextID the context ID
	 * @param level the level
	 * @return the list of context HIDs 
	 * @deprecated
	 */
	public java.util.Vector getContextHIDs(String contextID, String level) {
		return hidMgr.getHIDs(Long.parseLong(contextID), Integer.parseInt(level));
	}

	/**
	 * Get the context HIDs in string format
	 * @param contextID the context ID
	 * @param level the level
	 * @return the list of context HIDs in string format
	 * @deprecated
	 */
	public String getContextHIDsAsString(String contextID, String level)
	throws RemoteException {

		String result = "";
		Vector<String> s = hidMgr.getHIDs(Long.parseLong(contextID),
				Integer.parseInt(level));

		for (Iterator<String> i = s.iterator(); i.hasNext();) {
			result = result + i.next() + " ";
		}

		return result;
	}

	/**
	 * Operation to retrieve all HIDs in the LinkSmart P2P network that match a
	 * description. The description allows inexact matches using magic
	 * characters
	 * <p>
	 * For example:
	 * <p>
	 * getHIDsbyDescription("Network*") -> Will return all the HIDs with
	 * description starting with Network
	 * getHIDsbyDescription(*Peter'sPortable*") -> Will return all the HIDs with
	 * description containing Peter'sPortable
	 * 
	 * @param description The description to match against
	 * @return A {@link java.util.Vector} containing all the HIDs in the LinkSmart
	 * Network that match the given description
	 */
	public Vector getHIDsbyDescription(String description)
	throws RemoteException {

		return hidMgr.getHIDsbyDescription(description);
	}

	/**
	 * Operation to retrieve all HIDs in the LinkSmart P2P network (in String 
	 * format) that match a description. The description allows inexact 
	 * matches using magic characters
	 * <p>
	 * For example:
	 * <p>
	 * getHIDsbyDescription("Network*") -> Will return all the HIDs with
	 * description starting with Network
	 * getHIDsbyDescription(*Peter'sPortable*") -> Will return all the HIDs with
	 * description containing Peter'sPortable
	 * 
	 * @param description The description to match against
	 * @return A {@link String} containing all the HIDs in the LinkSmart P2P network
	 *		 that match the description
	 */
	public String getHIDsbyDescriptionAsString(String description)
	throws RemoteException {

		String result = "";
		Vector<String> s = hidMgr.getHIDsbyDescription(description);

		try {
			for (Iterator<String> i = s.iterator(); i.hasNext();) {
				result = result + i.next() + " ";
			}
		} catch (NullPointerException e) {
			logger.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * Operation to retrieve all HIDs in the LinkSmart P2P network (in String format)
	 * that match a description. The description allows inexact matches using 
	 * magic characters
	 * <p>
	 * For example:
	 * <p>
	 * getHIDsbyDescription("Network*") -> Will return all the HIDs with
	 * description starting with Network
	 * getHIDsbyDescription(*Peter'sPortable*") -> Will return all the HIDs with
	 * description containing Peter'sPortable
	 * 
	 * @param description The description to match against
	 * @return A {@link java.lang.String[]} containing all the HIDs in the LinkSmart 
	 * P2P network that match the description
	 */
	public String[] getHostHIDsbyDescription(String description)
	throws RemoteException {

		Vector<String> hostHIDs = hidMgr.getHostHIDs();
		Vector<String> hidsValid = new Vector<String>();
		Enumeration<String> allHIDs = hidMgr.getHIDsbyDescription(description).elements();

		while (allHIDs.hasMoreElements()) {
			String hid = allHIDs.nextElement();
			if (hostHIDs.contains(hid)) {
				hidsValid.add(hid);
			}
		}

		String[] hids = new String[hidsValid.size()];
		hidsValid.copyInto(hids);
		return hids;
	}

	/**
	 * Operation to retrieve all HIDs in the LinkSmart P2P network (in String format) 
	 * that match a description. The description allows inexact matches using 
	 * magic characters
	 * <p>
	 * For example:
	 * <p>
	 * getHIDsbyDescription("Network*") -> Will return all the HIDs with
	 * description starting with Network
	 * getHIDsbyDescription(*Peter'sPortable*") -> Will return all the HIDs with
	 * description containing Peter'sPortable
	 * 
	 * @param description The description to match against
	 * @return A {@link String} containing all the HIDs in the LinkSmart P2P network
	 * that match the description (separated by commas)
	 */
	public String getHostHIDsbyDescriptionAsString(String description)
	throws RemoteException {

		String hids = "";
		Vector<String> hostHIDs = hidMgr.getHostHIDs();
		Enumeration<String> allHIDs = hidMgr.getHIDsbyDescription(description).elements();
		while (allHIDs.hasMoreElements()) {
			String hid = allHIDs.nextElement();
			if (hostHIDs.contains(hid)) {
				hids = hids + " " + hid;
			}
		}

		return hids;
	}

	/**
	 * Method to retrieve the description associated with a given hid. 
	 * 
	 * @param hid The hid 
	 * @return A {@link String} with the description associated with the requested hid
	 */
	public String getDescriptionbyHID(String hid) throws RemoteException {
		return hidMgr.getDescriptionbyHID(hid);
	}

	/**
	 * Method to retrieve the ip associated with a given hid. 
	 * 
	 * @param hid The hid 
	 * @return A {@link String} with the ip associated with the requested hid
	 */
	public String getIPbyHID(String hid) throws RemoteException {
		return hidMgr.getIPbyHID(hid);
	}

	/**
	 * Method to retrieve the endpoint associated with a given hid. 
	 * 
	 * @param hid The hid 
	 * @return A {@link String} with the endpoint associated with the requested hid
	 */
	public String getEndpointbyHID(String hid) throws RemoteException {
		return hidMgr.getEndpointbyHID(hid);
	}

	/**
	 * Get the Host HID endpoint
	 * @param hid the HID
	 * @return the Host HID endpoint
	 * @deprecated
	 */
	public String getHostHIDEndpoint(String hid) throws RemoteException {
		return hidMgr.getHID(new HID(hid)).getEndpoint();
	}

	/**
	 * Operation to remove an HID from the Network Manager
	 * @param hid the HID to remove
	 * Network
	 */
	public void removeHID(String hid) {
		HID hid_t = new HID(hid);
		hidMgr.removeHID(hid_t);
	}

	/**
	 * Removes all HIDs
	 * @deprecated
	 */
	public void removeAllHID() {
		hidMgr.removeAllHIDs();
	}

	/**
	 * Gets the NM position
	 * 
	 * @return last know NM position
	 * @throws RemoteException
	 * @deprecated
	 */
	public String getNMPosition() throws RemoteException {
		if(!geoLocatorAcceded) {
			/*
			 * Token used for IP blocking geolocation sites. These variables 
			 * holds the latitude and longitude of the Network Manager
			 */
			String latitude = null;
			String longitude = null;

			try {
				URL tempURL = new URL("http://www.geobytes.com/ipLocator.htm");
				HttpURLConnection tempConn = (HttpURLConnection) tempURL.openConnection();
				InputStream tempInStream = tempConn.getInputStream();
				InputStreamReader tempIsr = new InputStreamReader(tempInStream);
				BufferedReader tempBr = new BufferedReader(tempIsr);
				String thisLine;

				while ((thisLine = tempBr.readLine()) != null) {
					if (thisLine.contains("Latitude")) {
						latitude = tempBr.readLine();
						latitude = latitude.replace(" <td align=\"right\">"
								+ "<input name=\"ro-no_bots_pls10\" value=\"", "");
						latitude = latitude.replace("\" size=\"20\" readonly></td>", "");
					}

					if (thisLine.contains("Longitude")) {
						longitude = tempBr.readLine();
						longitude = longitude.replace(" <td align=\"right\">"
								+ "<input name=\"ro-no_bots_pls19\" value=\"", "");
						longitude = longitude.replace("\" size=\"20\" readonly></td>", "");
					}
				}

				tempBr.close();
				tempInStream.close();
			} catch (Exception ex) {
				logger.debug(ex);
				latitude = "NULL";
				longitude = "NULL";
			}

			this.lastKnownPosition = latitude + ";" + longitude;
			geoLocatorAcceded = true;
			return latitude + ";" + longitude;
		}
		else {
			return this.lastKnownPosition;
		}
	}

	/**
	 * @param in0 deprecated
	 * @throws RemoteException
	 * @return the NM position auth
	 * @deprecated
	 */
	public String getNMPositionAuth(String in0) throws RemoteException {
		double result = 0;

		try {
			result = trustManager.getTrustValue(in0);
			logger.info("TrustManager result:" + Double.toString(result));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		if (result > 0) {
			return getNMPosition();
		}

		return "";
	}

	/**
	 * This method exchanges certificates between two HIDs.
	 * <p>
	 * As a result of this method, two entries will be added to the 
	 * CryptoManager's keystore:<br>
	 * 1. the certificate of <code>receiverHID</code>
	 * 2. a symmetric key that can be used by the Inside LinkSmart module for 
	 * subsequent communication. 
	 * The certificate that has been stored in the CryptoManager can be 
	 * referenced using the return value of this method.
	 *  
	 * @param senderHID Your own HID.
	 * @param receiverHID The target's HID.
	 * @return a String, representing different Attributes that could be 
	 * retrieved from the receiverHID's certificate.
	 */
	public String getInformationAssociatedWithHID(String senderHID, String receiverHID) {
		if (!hidMgr.getHIDs().contains(receiverHID)) {
			logger.error("Could not find receiver HID: " + receiverHID);
			return null;
		}

		// First Check whether there is a key for receiverHID
		String certReference = cryptoManager.getCertificateReference(receiverHID);
		if (certReference == null) {
			/* 
			 * We don't have a key yet. Start secure handshake to retrieve 
			 * key and information.
			 */
			try {
				// Synchronously start the secure session protocol
				logger.debug(" ----------- Starting SecureSession protocol to "
						+ "retrieve keys for " + senderHID + " and " + receiverHID
						+ "  ------------");
				certReference = SecureSessionControllerImpl.getInstance().
				getCertificateIdentifier(senderHID, receiverHID);
				logger.debug(" ----------- SecureSession protocol finished with "
						+ "cert reference " + certReference + "  ------------");

				if (certReference != null) {
					cryptoManager.addCertificateForHID(receiverHID, certReference);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			logger.debug("Key for HIDs " + senderHID + " and " + receiverHID
					+ " already exists under reference " + certReference);
		}

		// If we still got no key something went wrong and we return null
		if (certReference == null) {
			return null;
		}

		/*
		 * Now we have a session key and we can retrieve any attributes which 
		 * are associated with it. get reference to CryptoManager
		 */
		getCryptoManager();

		// Now use CryptoManager to retrieve information from the certificate
		try {
			// Retrieve attributes using the CryptoManager
			logger.debug("Retrieving attributes from certificate " + certReference);
			Properties attributes = 
				this.cryptoManager.getAttributesFromCertificate(certReference);

			// Convert attributes to XML using the Properties object
			logger.debug("Converting the properties to xml: " + attributes);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			attributes.storeToXML(bos, "");

			logger.debug("returning " + bos.toString());

			/* Return XML as string. */
			return bos.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * This method searches the LinkSmart Network for the HIDs that contain 
	 * attributes matching the query.
	 * The format of the queries is as follows:<br><p>
	 * <code>(key1=cond1)&&(key2=cond2*)...</code>
	 * <p>
	 * The developer can also use the magic caracter (*) for unexact queries. 
	 * Due to this, the number of HIDs mathing the query might be greater than 
	 * one. Thus, the developer can specify the number of HIDs that will be 
	 * returned and the maximun time to wait for resolving the query. 
	 * For example, if <code>maxTime = 60000 ms</code> and <code>maxHIDs = 4</code> 
	 * the result of the query will be returned when the Network Manager gets 4 
	 * (or more) HIDs that match the query or when maxTime expires.
	 * <p>
	 * The allowed values for maxTime are 0 (search only locally) - 60000 (ms) 
	 * and for maxHIDs 0 (best effort) - maxInt
	 * 	 *  
	 * @param requesterHID Your own HID.
	 * @param requesterAttributes The target's HID.
	 * @param query Query for HID attributes: <code>(key1=cond1) && 
	 * (key2=cond2*)...</code>  * = non-exact match
	 * @param maxTime Maximum time to wait for query resolution in ms
	 * @param maxHIDs Maximum number of HIDs to return
	 * @return a String, with the resulting HID separated by blank spaces 
	 * (0.0.0.1 0.0.0.2 ...)
	 */
	public String[] getHIDByAttributes(String requesterHID, 
			String requesterAttributes, String query, long maxTime, int maxHIDs) {

		/* Check locally. */
		if (maxHIDs < 1) {
			maxHIDs = Integer.MAX_VALUE;
		}

		if (maxTime > 60000) {
			maxTime = 60000;
		}

		Vector<String> result = hidMgr.getHostHIDbyAttributes(query, maxHIDs);
		if ((result.size() == maxHIDs)) {
			return (String[]) result.toArray(new String[result.size()]);
		}

		if (maxTime <= 0) {
			return (String[]) result.toArray(new String[result.size()]);
		}

		maxHIDs = maxHIDs - result.size();

		// Check remotelly using the backbone Manager
		Vector<String> res = backboneMgr.sendAttributesQuery(requesterHID, 
				requesterAttributes, query, maxTime, maxHIDs);

		Enumeration en = result.elements();
		while (en.hasMoreElements()) {
			res.add((String) en.nextElement());
		}

		return (String[]) res.toArray(new String[res.size()]);
	}
	/**
	 * This method searches the LinkSmart Network for the HIDs that contain 
	 * attributes matching the query.
	 * The format of the queries is as follows:<br><p>
	 * <code>(key1=cond1)&&(key2=cond2*)...</code>
	 * <p>
	 * The developer can also use the magic caracter (*) for unexact queries. 
	 * Due to this, the number of HIDs mathing the query might be greater than 
	 * one. Thus, the developer can specify the number of HIDs that will be 
	 * returned and the maximun time to wait for resolving the query. 
	 * For example, if <code>maxTime = 60000 ms</code> and <code>maxHIDs = 4</code> 
	 * the result of the query will be returned when the Network Manager gets 4 
	 * (or more) HIDs that match the query or when maxTime expires.
	 * <p>
	 * The allowed values for maxTime are 0 (search only locally) - 60000 (ms) 
	 * and for maxHIDs 0 (best effort) - maxInt
	 * 	 *  
	 * @param requesterHID Your own HID.
	 * @param requesterAttributes The target's HID.
	 * @param query Query for HID attributes: <code>(key1=cond1) && 
	 * (key2=cond2*)...</code>  * = non-exact match
	 * @param maxTime Maximum time to wait for query resolution in ms
	 * @param maxHIDs Maximum number of HIDs to return
	 * @return a String, with the resulting HID separated by blank spaces 
	 * (0.0.0.1 0.0.0.2 ...)
	 */
	public String getHIDByAttributesAsString(String requesterHID, 
			String requesterAttributes, String query, long maxTime, int maxHIDs) {

		String[] result =  getHIDByAttributes(requesterHID, requesterAttributes, query, maxTime, maxHIDs);
		String ret = "";
		for(String s : result){
			ret += s + " ";			
		} 
		return ret;
	}

	protected void setTrustThreshold(double trustth){
		if(SecureSessionControllerImpl.getInstance() != null){
			SecureSessionControllerImpl.getInstance().setTrustThreshold(trustth);
		}
	}

	protected void setTrustManager(String url){
		if(url.equalsIgnoreCase("local")){
			if(!this.trustManagerOsgi){
				//search for trustmanager service and bind it
				//as using declarative services this should not occur
				ServiceReference sr = context.getBundleContext().getServiceReference(TrustManager.class.getName());
				if(sr != null){
					TrustManager tm = (TrustManager)context.getBundleContext().getService(sr);
					if(tm != null){
						this.trustManager = tm;
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
			if(this.trustManagerOsgi){
				//if there is OSGi bundle TrustManager then switching to external TM is not allowed
				logger.warn("There is a local TrustManager available. Change to external is not allowed!");
			}else{
				//get trustmanager service over url
				try {
					this.trustManager = (TrustManager)this.wsclientProvider.getRemoteWSClient(TrustManager.class.getName(), 
							(String)configurator.get(NetworkManagerConfigurator.TRUSTMANAGER_URL), false);
					createSecureSession();
				} catch (Exception e) {
					logger.error("Error getting TrustManager service.");
				}
			}
		}
	}

	private void createSecureSession(){
		if(!SecureSessionControllerImpl.instanceCreated() && cryptoManager != null && trustManager != null && configurator != null){
			SecureSessionControllerImpl.createInstance(this, 
					this.cryptoManager, this.trustManager);
			//set trust threshold
			SecureSessionControllerImpl.getInstance().setTrustThreshold(
					Double.valueOf(
							(String)configurator.get(NetworkManagerConfigurator.TRUSTMANAGER_TRUST_THRESHOLD)));
		}
	}
}
