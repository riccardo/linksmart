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

package eu.linksmart.security.protocols.net.util;

import java.util.Properties;

import org.apache.log4j.Logger;


import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.protocols.net.Consts;
import eu.linksmart.security.protocols.net.ProtocolState;
import eu.linksmart.security.protocols.net.State;
import eu.linksmart.security.protocols.net.client.AcknowledgementSentState;
import eu.linksmart.security.protocols.net.client.ClientCommandSender;
import eu.linksmart.security.protocols.net.client.KeyRequestSentState;
import eu.linksmart.security.protocols.net.impl.SecureSessionControllerImpl;
import eu.linksmart.security.protocols.net.server.AcknowledgedState;
import eu.linksmart.security.protocols.net.server.FailedState;
import eu.linksmart.security.protocols.net.server.KeySentState;
import eu.linksmart.security.protocols.net.server.NoState;
import eu.linksmart.security.protocols.net.server.ServerCommandSender;
import eu.linksmart.security.protocols.net.transport.Command;

/**
 * 
 * This class contains the context information of a given protocol run
 * including messages to be processed and actual state. For each session
 * a ProtocolContext should be created and destroyed when finished.
 * 
 * @author Mark Vinkovits - mark.vinkovits@fit.fraunhofer.de
 * 
 */
public class ProtocolContext {

	private ProtocolState state = null;
	private Properties props = new Properties();
	private String role = null;
	private Logger logger = Logger.getLogger(ProtocolContext.class.getName());
	
	public ProtocolContext(String contextRole, String client, String server, String session) {
		role = contextRole;
		props.setProperty("ClientId", client);
		props.setProperty("ServerId", server);
		props.setProperty("SessionId", session);
		
		if(role == Consts.CLIENT){
			state = new KeyRequestSentState(this);
		} else {
			state = new NoState();
		}
	}
	
	public void handleCommand(Command command, String protocolRole)
	{
		if(role.equals(protocolRole)){
			state.handleCommand(command, this);
		}
	}
	
	public void goToState(State newState)
	{
		if(role == Consts.SERVER){
			switch(newState){
			case KEYSENT:
				state = new KeySentState(this);
				break;
			case ACKNOWLEDGED:
				state = new AcknowledgedState(this);
				break;
			case FAILED:
				state = new FailedState(this);
				break;
			}
		}else{
			switch(newState){
			case ACKNOWLEDGEMENTSENT:
				state = new AcknowledgementSentState(this);
				break;
			case KEYREQUESTSENT:
				state = new KeyRequestSentState(this);
				break;
			}
		}		
	}
	
	public void saveClientPk(String identifier){
		props.setProperty("Identifier", identifier);
		CryptoManager c = SecureSessionControllerImpl.getInstance().getCryptoManager();
		c.addCertificateForHID(props.getProperty("ClientId"), identifier);
		logger.debug("CLIENT KEY IDENTIFIER FROM CRYPTOMANAGER: " + identifier + " has been bound to HID: " + props.getProperty("ClientId"));
	}
	
	public void saveServerPk(String identifier){
		props.setProperty("Identifier", identifier);
		CryptoManager c = SecureSessionControllerImpl.getInstance().getCryptoManager();
		c.addCertificateForHID(props.getProperty("ServerId"), identifier);
		logger.debug("Server KEY IDENTIFIER FROM CRYPTOMANAGER: " + identifier + " has been bound to HID: " + props.getProperty("ServerId"));
	}
	
	public void setSentSymKey(String key){
		props.setProperty("SymKey", key);
	}
	
	public void storeSentSymKey(){
		storeSymKey(props.getProperty("SymKey"));
	}
	
	public void sendKey(String key){
		ServerCommandSender.getInstance().sendKey(key, props.getProperty("ClientId"), props.getProperty("ServerId"));
	}

	public void sendKeyRequest() {
		ClientCommandSender.getInstance().sendKeyRequest(props.getProperty("ServerId"), props.getProperty("ClientId"));		
	}
	
	public void sendAck(){
		ClientCommandSender.getInstance().sendAck(props.getProperty("ServerId"), props.getProperty("ClientId"));
	}
	
	public void setIdentifier(String identifier){
		props.setProperty("Identifier", identifier);
	}

	public void storeSymKey(String key) {
		//TODO save symmetric key in keystore
//		CryptoManager c = SecureSessionControllerImpl.getInstance().getCryptoManager();
//		try{
//		String identifier = c.storeSymmetricKey("AES", key);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
	}
	
	public void quit(){
		SecureSessionControllerImpl.getInstance().setCertificateIdentifier(props.getProperty("SessionId"), null);
		SecureSessionControllerImpl.getInstance().closeSession(props.getProperty("SessionId"));
	}
	
	public void finished(){
		SecureSessionControllerImpl.getInstance().setCertificateIdentifier(
				role.equals(Consts.CLIENT)? props.getProperty("ServerId"): props.getProperty("ClientId"),
				props.getProperty("Identifier"));
		SecureSessionControllerImpl.getInstance().closeSession(props.getProperty("SessionId"));
	}
}
