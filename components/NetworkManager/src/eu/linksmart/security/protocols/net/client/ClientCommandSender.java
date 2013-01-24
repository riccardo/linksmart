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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import eu.linksmart.network.impl.NetworkManagerApplicationSoapBindingImpl;

import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.protocols.net.impl.SecureSessionControllerImpl;
import eu.linksmart.security.protocols.net.transport.Command;

/**
 * Creates the commands on the client side from the required parameters
 * @author Mark Vinkovits - mark.vinkovits@fraunhofer.fit.de
 *
 */
public class ClientCommandSender {

	private static ClientCommandSender instance = null;
	
	private ClientCommandSender(){
		
	}
	
	public static synchronized ClientCommandSender getInstance(){
		if(instance == null){
			instance = new ClientCommandSender();
		}
		return instance;
	}
	
	/**
	 * Creates an acknowledgment command
	 * @param server
	 * @param client
	 */
	public void sendAck(String server, String client){
		Command cmd = new Command(Command.CLIENT_ACK);
		cmd.setProperty(Command.CLIENT, client);
		cmd.setProperty(Command.SERVER, server);
		send(cmd);
	}
	
	/**
	 * Creates a key request command and signs it with the clients public key
	 * @param server
	 * @param client
	 */
	public void sendKeyRequest(String server, String client){
		Command cmd = new Command(Command.CLIENT_REQUESTS_KEY);
		cmd.setProperty(Command.CLIENT, client);
		cmd.setProperty(Command.SERVER, server);
		
		CryptoManager cryptoManager = SecureSessionControllerImpl.getInstance().getCryptoManager();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			cmd.storeToXML(bos, null);
			NetworkManagerApplicationSoapBindingImpl nm = SecureSessionControllerImpl.getInstance().getNetworkManager();
			
			String signedCommand = cryptoManager.sign(bos.toString(),null, cmd.getProperty(Command.CLIENT));
			cmd.setProperty("signedPayload", signedCommand);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		send(cmd);
	}
	
	/**
	 * Forwards the finished command to the SecureSessionController for sending
	 * @param command
	 */
	private void send(Command command){
		SecureSessionControllerImpl.getInstance().sendCommand(command, Command.CLIENT);
	}
}
