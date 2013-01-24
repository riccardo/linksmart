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
 * Copyright (C) 2006-2010
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

package eu.linksmart.security.protocols.net.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.linksmart.network.impl.NetworkManagerApplicationSoapBindingImpl;

import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.protocols.net.Consts;
import eu.linksmart.security.protocols.net.SecureSessionController;
import eu.linksmart.security.protocols.net.common.CommandQueue;
import eu.linksmart.security.protocols.net.common.MissingSenderCertificateException;
import eu.linksmart.security.protocols.net.transport.Command;
import eu.linksmart.security.protocols.net.util.ProtocolContext;
import eu.linksmart.security.trustmanager.TrustManager;

/**
 * This is a lazy singleton reference implementation of the
 * 
 * {@link SecureSessionController} interface.
 * 
 * @author Stephan Heuser - stephan.heuser@sit.fraunhofer.de
 * @author Mark Vinkovits - mark.vinkovits@fit.fraunhofer.de
 * 
 */

public class SecureSessionControllerImpl implements SecureSessionController {

	private static SecureSessionController instance = null;
	private static boolean instanceCreated = false;
	
	//new implementation
	private Map<String, ProtocolContext> activeSessions = new HashMap<String, ProtocolContext>();
	
	//old variables
	Set<String> runningSessions = new HashSet<String>();
	List<String> waitingSessions = new ArrayList<String>();
	Map<String, String> roles = new HashMap<String, String>();

	Map<String, CommandQueue> waitingCommands = new HashMap<String, CommandQueue>();
	private Map<String, String> certificateIdentifiers = new HashMap<String, String>();
	private Logger logger = Logger.getLogger(SecureSessionControllerImpl.class);
	private Map<String, Long> timeouts = new HashMap<String, Long>();
	private static HashSet<String> ignoredHIDs = new HashSet<String>();
	private CryptoManager cryptoManager;
	private TrustManager trustManager;
	protected NetworkManagerApplicationSoapBindingImpl nm;
	private double trustThreshold = 0.0;

	/**
	 * Create a new Instance of the SecureSessionController.
	 * 
	 * Singleton pattern is used, as well as dependency injection.
	 * 
	 * 
	 * @param cryptoManager
	 *            Crypto Manager stub
	 * @param trustManager
	 *            Trust Manager stub
	 */
	private SecureSessionControllerImpl(NetworkManagerApplicationSoapBindingImpl nm, CryptoManager cryptoManager, TrustManager trustManager) {
		this.nm = nm;
		this.cryptoManager = cryptoManager;
		this.trustManager = trustManager;
	}

	/**
	 * Returns the current instance.
	 * <p>
	 * The method <code>createInstance</code> MUST be called before this method.
	 * Otherwise this method will return null.
	 * 
	 * @return SecureSessionController Instance
	 */

	public static SecureSessionController getInstance() {
		return instance;
	}

	/**
	 * This method creates an instance of the SecureSession protocol.
	 * <p>
	 * The instance will use the CryptoManager and TrustManager that have been
	 * passed to this method. It doesn't matter whether the interfaces refer to
	 * webservices or OSGi - both works.
	 * 
	 * @param cryptoManager
	 *            a reference to the CryptoManager interface. If no
	 *            CryptoManager can be reached by this interface, the protocol
	 *            will fail.
	 * @param trustManager
	 *            a reference to the TrustManager interface. If no TrustManager
	 *            can be reached by this interface, the protocol assumes a trust
	 *            value of 0.0 and continues.
	 * @return an instance of this object.
	 */
	public synchronized static SecureSessionController createInstance(NetworkManagerApplicationSoapBindingImpl nm, CryptoManager cryptoManager,
			TrustManager trustManager) {
		if (instance == null) {
			instance = new SecureSessionControllerImpl(nm, cryptoManager, trustManager);
			instanceCreated = true;
		}
		return instance;
	}
	
	public static boolean instanceCreated(){
		return instanceCreated;
	}

	public CryptoManager getCryptoManager() {
		return cryptoManager;
	}

	public TrustManager getTrustManager() {
		return trustManager;
	}

	public NetworkManagerApplicationSoapBindingImpl getNetworkManager() {
		return nm;
	}

	public synchronized String getWaitingSession() {
		if (waitingSessions.size() > 0) {
			String session = waitingSessions.remove(0);
			runningSessions.add(session);
			return session;
		}
		return null;
	}

	/**
	 * This will be called from Route Manager on every incoming commmand.
	 * Commands will be queued
	 * 
	 * @param command
	 *            Incoming command
	 */

	public void handleCommand(Command command, String role) {
		//new implementation
		String session = command.getProperty("client") + "#" + command.getProperty("server") + "#" + role;
		
		logger.debug("Received to session " + session + "command " + command.getProperty("command"));
		
		if(!activeSessions.containsKey(session))
		{
			logger.debug("New session!");
			
			if(role == Consts.CLIENT){
				//if role is client protocol starts by creating context
				ProtocolContext context = new ProtocolContext(role, command.getProperty("client"), command.getProperty("server"), session);
				activeSessions.put(session, context);
			} else {
				//if role is server command has to be passed
				ProtocolContext context = new ProtocolContext(role, command.getProperty("client"), command.getProperty("server"), session);
				activeSessions.put(session, context);
				context.handleCommand(command, role);
			}
		} else {
			logger.debug("Passed command to context");
			
			activeSessions.get(session).handleCommand(command, role);
		}
	}

	public void sendCommand(Command command, String role) {
		logger.debug("Sending Command" + command);
		// logger.debug("Type: " + command.getProperty("command"));
		// logger.debug("Client: " + command.getProperty("client"));
		// logger.debug("Server: " + command.getProperty("server"));
		
		String server = command.getProperty("server");
		String client = command.getProperty("client");
		String encodedCommand = null;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			command.storeToXML(bos, null);
			encodedCommand = bos.toString();
			bos.close();
		} catch (IOException e) {
			logger.error("Error while encoding command to XML");
			e.printStackTrace();
		}

		String session = client + "#" + server + "#" + role;

		if (!roles.containsKey(session)) {
			roles.put(session, role);
		}

		Thread t = new Thread(new CommandSenderHelper(nm, command, encodedCommand, roles.get(session)));
		t.start();
	}

	public CommandQueue getCommandQueue(String sessionId) {
		return waitingCommands.get(sessionId);
	}

	public String getCertificateIdentifier(String senderHID, String receiverHID) throws MissingSenderCertificateException {
		// Check whether the sender has a private key. If not, SecureSession is
		// not executed
		String identifier;
		if (ignoredHIDs.contains(senderHID)) {
			return null;
		} else {
			identifier = cryptoManager.getPrivateKeyReference(senderHID);
			if (identifier == null) {
				logger
						.warn("No private key found for HID "
								+ senderHID
								+ ". I won't be able to secure my connections. Make sure you call createCryptoHID. All future connections from this HID will be unprotected");
				ignoredHIDs.add(senderHID);
				return null;
			}
		}

		logger.info("SecureSession Protocol starts between " + senderHID + " and " + receiverHID);
		Command command = new Command(Command.CLIENT_REQUESTS_KEY);
		command.put("server", receiverHID);
		command.put("client", senderHID);
		String sessionId = senderHID + "#" + receiverHID;

		// Start the protocol FSM
		handleCommand(command, Consts.CLIENT);
		identifier = null;

		// Wait until protocol has finished and returned value
		synchronized (certificateIdentifiers) {

			Long starttime = Calendar.getInstance().getTimeInMillis();
			this.timeouts.put(sessionId, starttime);

			while ((identifier = certificateIdentifiers.get(receiverHID)) == null && timeouts.containsKey(sessionId)) {

				long currenttime = Calendar.getInstance().getTimeInMillis();

				if ((currenttime - timeouts.get(sessionId)) > Consts.CERTIFICATE_EXCHANGE_TIMEOUT)
					break;

				try {
					logger.info("Waiting until protocol has finished");
					certificateIdentifiers.wait(20000);

					// Diagnosis block:
					try {
						logger.debug("------------ Testing results of SecureSession protocol --------------");
						logger.debug("Testing whether I have access to my own private key for HID " + senderHID);
						String senderIdent = cryptoManager.getPrivateKeyReference(senderHID);
						logger.debug("  Internal identifier is " + senderIdent);
						PrivateKey senderPrivateKey = cryptoManager.getPrivateKeyByIdentifier(senderIdent);
						if (senderPrivateKey != null) {
							logger.debug("  Private key is available and uses the following algo: " + senderPrivateKey.getAlgorithm());
						} else {
							logger.error("  My private key is null. Something went wrong!");
						}

						logger.debug("Testing whether I have access to the public key of the server at " + receiverHID);
						String receiverIdent = cryptoManager.getCertificateReference(receiverHID);
						logger.debug("  Internal identifier is " + receiverIdent);
						if (receiverIdent != null) {
							Certificate receiverCertificate = cryptoManager.getCertificateByIdentifier(receiverIdent);
							logger.debug("  Certificate is available and uses the following format: " + receiverCertificate.getType());
							logger.debug("  Public key in the certificate is: " + receiverCertificate.getPublicKey());
							logger.debug("  Algo of public key is: " + receiverCertificate.getPublicKey().getAlgorithm());
						} else {
							logger.error("  Certificate is null. Something went wrong!");
						}
						logger.debug("------------------------------------------------------------------------");
					} catch (Throwable t) {
						logger.error(t.getMessage(), t);
					}
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
			return identifier;
		}

	}

	public void setCertificateIdentifier(String HID, String identifier) {
		logger.info("Going to release lock");
		synchronized (certificateIdentifiers) {
			if(identifier == null){
				logger.debug("Protocol has been aborted!");
			} else {
				logger.info("Storing sessionId " + HID + " by identifier " + identifier);
				certificateIdentifiers.put(HID, identifier);
				cryptoManager.addCertificateForHID(HID, identifier);
			}
			certificateIdentifiers.notifyAll();
			logger.info("Released lock");
		}
	}

	public void closeSession(String sessionId) {

		synchronized (activeSessions) {
			activeSessions.remove(sessionId);
			waitingCommands.remove(sessionId);
			roles.remove(sessionId);
			timeouts.remove(sessionId);
		}

	}

	public void setTrustThreshold(double trustth) {
		trustThreshold = trustth;
	}
	
	public double getTrustThreshold(){
		return trustThreshold;
	}

}
