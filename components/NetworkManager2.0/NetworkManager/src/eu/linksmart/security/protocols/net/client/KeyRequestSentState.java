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

package eu.linksmart.security.protocols.net.client;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
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

/**
 * When entering the state sends a key request and then awaits a key from the server
 * @author Vinkovits
 *
 */
public class KeyRequestSentState implements ProtocolState {


	private Logger logger = Logger.getLogger(KeyRequestSentState.class.getName());
	
	public KeyRequestSentState(ProtocolContext context){
		context.sendKeyRequest();
	}
	
	/**
	 * Waits for the key from the server and when receiving it checks it 
	 * using the CryptoManager, then stores the received key. When 
	 * everything is OK context is instructed to go to acknowledgment state.
	 */
	public void handleCommand(Command command, ProtocolContext context) {
		if(Integer.parseInt(command.getProperty("command")) == Command.SERVER_SEND_KEY){
			CryptoManager c = SecureSessionControllerImpl.getInstance().getCryptoManager();
			String signedAndEncryptedPayload = command.getProperty(Command.SIGNED_AND_ENCRYPTED_PAYLOAD);
			
			try {
				//verify signature on message
				logger.debug("Verifying message " + signedAndEncryptedPayload);
				String verifiedMessage = c.verify(signedAndEncryptedPayload);
				logger.debug("Signature of Message from Server verified: " + (verifiedMessage!=null));

				//if signature is valid extract symmetric key
				if (verifiedMessage!=null && verifiedMessage!="") {
						// Convert input String to XML document
					try {	
						logger.debug("Encrypted message is: " + verifiedMessage);
					} catch (Exception e) {
						logger.error("Error when extracting encrypted message " ,e);
					}
					Certificate cert = XMLMessageUtil.getCertificate(signedAndEncryptedPayload);
					String serverKeyidentifier = c.storePublicKey(Base64.encodeBytes(cert.getEncoded()), "");
					context.saveServerPk(serverKeyidentifier);			
					
					//open message
					String originalMessage = c.decrypt(verifiedMessage);
					
					logger.debug("Verified: " + verifiedMessage);
					logger.debug("Original: " + originalMessage);
					
					//check if encrypted message and signed message are the same
					Properties props = new Properties();
					props.loadFromXML(new ByteArrayInputStream(originalMessage.getBytes()));
					String client = props.getProperty(Command.CLIENT);
					String server = props.getProperty(Command.SERVER);
					String cmd = props.getProperty(Command.COMMAND);
					if ((client.equals(command.getProperty(Command.CLIENT))) && (server.equals(command.getProperty(Command.SERVER))) && (cmd.equals(command.getProperty(Command.COMMAND)))) {
						logger.debug("Signed Headers are the same as outer Headers");
						//store received symmetric key
						context.storeSymKey(props.getProperty("symmetrickey"));
					} else {
						logger.debug("Signed Headers are not the same as outer Headers, aborting!");
						context.quit();
					}
				} else {
					logger.debug("Signature not valid, aborting");
					context.quit();
				}
				
			} catch (Exception e) {
				logger.error(e);
			} 
			context.goToState(State.ACKNOWLEDGEMENTSENT);
		}
	}
}
