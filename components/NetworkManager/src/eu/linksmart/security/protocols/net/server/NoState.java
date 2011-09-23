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

package eu.linksmart.security.protocols.net.server;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.protocols.net.ProtocolState;
import eu.linksmart.security.protocols.net.State;
import eu.linksmart.security.protocols.net.impl.SecureSessionControllerImpl;
import eu.linksmart.security.protocols.net.transport.Command;
import eu.linksmart.security.protocols.net.util.Base64;
import eu.linksmart.security.protocols.net.util.ProtocolContext;
import eu.linksmart.security.protocols.net.util.XMLMessageUtil;
import eu.linksmart.security.trustmanager.TrustManager;

/**
 * Represents the initial state of the protocol. When receiving a command
 * it should be a keyrequest in response to which the context goes
 * to key sent state.
 * @author Vinkovits
 *
 */
public class NoState implements ProtocolState {

	private Logger logger = Logger.getLogger(NoState.class.getName());
	
	public NoState(){
		
	}
	
	/**
	 * When receiving a request checks the validity of the signature
	 * and if it is correct the context is going to next state
	 */
	public void handleCommand(Command command, ProtocolContext context) {
		if(Integer.parseInt(command.getProperty("command")) == Command.CLIENT_REQUESTS_KEY){
			//if signature on message and certificate are valid go to next state	
			if(verifySignature(command) && verifyCertificate(command, context)){
				//save certificate of client
				context.goToState(State.KEYSENT);
			}
		}
	}
	
	/**
	 * Checks using the TrustManager whether the certificate is trusted.
	 * @param command
	 * @param context
	 * @return true if accepted - false if not
	 */
	private boolean verifyCertificate(Command command, ProtocolContext context){
		String signedPayload = command.getProperty("signedPayload"); 		
		Certificate cert = XMLMessageUtil.getCertificate(signedPayload);
		
		TrustManager t = SecureSessionControllerImpl.getInstance().getTrustManager();        		
		Double trust = new Double(0.0);
		
		if (t!=null) {
			try {
				String encodedcert = Base64.encodeBytes(cert.getEncoded());
				trust = t.getTrustValue(encodedcert);
			} catch (Exception e) {
				logger.error("Error while communicating with Trust Manager "+ e.getMessage());
			} 
		}
		
		logger.debug("Trust in Certificate is: " + trust);
		
		double trustThreshold = SecureSessionControllerImpl.getInstance().getTrustThreshold();
		
		if (trust < trustThreshold ) {
			return false;
		} else {
			
			CryptoManager c = SecureSessionControllerImpl.getInstance().getCryptoManager();
			try {			
				//as certificate is valid store it in keystore
				String clientKeyIdentifier = c.storePublicKey(Base64.encodeBytes(cert.getEncoded()), "");
				context.saveClientPk(clientKeyIdentifier);
			} catch (CertificateEncodingException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return true;
	}
	
	/**
	 * Checks using the CryptoManager whether the signature is valid.
	 * @param command
	 * @return true if valid - false if not
	 */
	private boolean verifySignature(Command command){
		CryptoManager c = SecureSessionControllerImpl.getInstance().getCryptoManager();	
		String signedPayload = command.getProperty("signedPayload");
		
		try {
			
			String verifiedMessage = c.verify(signedPayload);
			logger.debug("Verification of signature. Result: " + (verifiedMessage!=null));
			//if signature is not valid return false
			if (verifiedMessage==null || verifiedMessage.equals("")) {
				return false;
			}
			
			//Verification of Server/Client/Command fields
			Properties props = new Properties();
			if (verifiedMessage!=null) {
				props.loadFromXML(new ByteArrayInputStream(verifiedMessage.trim().getBytes()));
			}
			
			if (!(props.getProperty(Command.SERVER).equals(command.getProperty(Command.SERVER)) 
					&& (props.getProperty(Command.CLIENT).equals(command.getProperty(Command.CLIENT))) 
					&& (props.getProperty(Command.COMMAND).equals(command.getProperty(Command.COMMAND))))) {
				
				logger.debug("Signed Payload Header Fields differ from unsigned Headers! Aborting!");
				return false;
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}		
		return true;
	}
}
