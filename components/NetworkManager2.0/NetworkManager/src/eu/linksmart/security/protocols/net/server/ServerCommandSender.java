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

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;


import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.protocols.net.impl.SecureSessionControllerImpl;
import eu.linksmart.security.protocols.net.transport.Command;

/**
 * Helper class that creates the commands on the server side needed 
 * in the SecureSessionProtocol
 * @author Vinkovits
 *
 */
public class ServerCommandSender {

	private static ServerCommandSender instance = null;
	private Logger logger = Logger.getLogger(ServerCommandSender.class.getName());
	
	private ServerCommandSender(){
		
	}
	
	public static synchronized ServerCommandSender getInstance(){
		if(instance == null){
			instance = new ServerCommandSender();
		}
		return instance;
	}
	
	/**
	 * Puts the key parameter into a command and encrypts and signs the message
	 * @param key
	 * @param client
	 * @param server
	 */
	public void sendKey(String key, String client, String server) {
		Command cmd = new Command(Command.SERVER_SEND_KEY);
		cmd.setProperty(Command.DEVICE_DESCRIPTION, "");
		cmd.setProperty("symmetrickey", key);
		cmd.setProperty(Command.CLIENT, client);
		cmd.setProperty(Command.SERVER, server);

		CryptoManager c = SecureSessionControllerImpl.getInstance().getCryptoManager();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			cmd.storeToXML(bos, null);
			
			//encrypting and signing message
			logger.debug("Encrypting " + bos.toString());
			logger.debug("Encrypting for " + cmd.getProperty(Command.CLIENT));
			String encryptedCommand = c.encryptAsymmetric(bos.toString(), cmd.getProperty(Command.CLIENT), "");
			String signedCommand = c.sign(encryptedCommand, null,cmd.getProperty(Command.SERVER));

			
			cmd.setProperty(Command.SIGNED_AND_ENCRYPTED_PAYLOAD, signedCommand);
			cmd.remove("symmetrickey");
			
		} catch (Exception e) {
			logger.error(e);
		}
		send(cmd);
	}
	
	/**
	 * Forwards the finished command to the SecureSessionController for sending
	 * @param command
	 */
	private void send(Command command){
		SecureSessionControllerImpl.getInstance().sendCommand(command, Command.SERVER);
	}
}
