package eu.linksmart.security.communication.impl.sym;

import java.io.IOException;

import org.apache.log4j.Logger;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.Message;

import eu.linksmart.security.communication.CryptoException;
import eu.linksmart.security.communication.SecurityProtocol;
import eu.linksmart.security.communication.VerificationFailureException;
import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.trustmanager.TrustManager;

public class SecurityProtocolImpl implements SecurityProtocol {

	private static Logger logger = Logger.getLogger(SecurityProtocolImpl.class);

	/**
	 * {@CryptoManager} used to store certificates and keys.
	 */
	private CryptoManager cryptoMgr = null;
	/**
	 * {@TrustManager} used to check validity of certificates.
	 */
	private TrustManager trustMgr = null;
	/**
	 * The client of this protocol. Client is the party 
	 * who started the communication.
	 */
	private VirtualAddress clientVirtualAddress = null;
	/**
	 * The server of this protocol. Server is the party 
	 * who received the request.
	 */
	private VirtualAddress serverVirtualAddress = null;
	
	private double trustThreshold;

	public SecurityProtocolImpl(VirtualAddress clientVirtualAddress,
			VirtualAddress serverVirtualAddress,
			CryptoManager cryptoMgr,
			TrustManager trustMgr,
			double trustThreshold) {
	}

	public Message startProtocol() throws CryptoException {
		return null;
	}

	public boolean isInitialized() {
		return true;
	}

	public synchronized Message processMessage(Message msg) throws CryptoException, VerificationFailureException, IOException {
		return null;
	}

	public synchronized Message protectMessage(Message msg) throws Exception {
		return null;
	}

	public synchronized Message unprotectMessage(Message msg) throws Exception {
		return null;
	}

	public boolean canBroadcast() {
		return false;
	}

	public Message protectBroadcastMessage(Message msg) throws Exception {
		throw new Exception("Broadcasting not supported by security protocol!");
	}

	public Message unprotectBroadcastMessage(Message msg) throws Exception {
		throw new Exception("Broadcasting not supported by security protocol!");
	}
}
