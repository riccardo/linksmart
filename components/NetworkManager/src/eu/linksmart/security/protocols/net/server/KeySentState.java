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

import org.apache.log4j.Logger;


import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.protocols.net.ProtocolState;
import eu.linksmart.security.protocols.net.State;
import eu.linksmart.security.protocols.net.impl.SecureSessionControllerImpl;
import eu.linksmart.security.protocols.net.transport.Command;
import eu.linksmart.security.protocols.net.util.ProtocolContext;

/**
 * When entering the state a key is sent to other party. Acknowledgment or
 * a further key request is expected.
 * @author Mark Vinkovits - mark.vinkovits@fit.fraunhofer.de
 *
 */
public class KeySentState implements ProtocolState {

	private Logger logger = Logger.getLogger(KeySentState.class.getName());
	private short nrOfTries = 0;
	
	/**
	 * Generates and sends symmetric key to client
	 * @param context
	 */
	public KeySentState(ProtocolContext context){
		generateAndSendKey(context);
	}
	
	/**
	 * When receiving and acknowledgment finishes protocol
	 * When receiving a key request repeats previous message
	 */
	public void handleCommand(Command command, ProtocolContext context) {
		//if acknowledgment received go to acknowledged state
		if(Integer.parseInt(command.getProperty("command")) == Command.CLIENT_ACK){
			context.goToState(State.ACKNOWLEDGED);
		} else if(Integer.parseInt(command.getProperty("command")) == Command.CLIENT_REQUESTS_KEY){
			//if key requested resend key
			generateAndSendKey(context);
		} else {
			//else command is not appropriate here and finish protocol
			context.goToState(State.FAILED);
		}
	}
	
	private void generateAndSendKey(ProtocolContext context){
		String key = createKey();
		if(key != null)
		{
			//if sendkey returns false then go to failed state as there were already 3 tries
			if(!sendKey(key, context)){
				context.goToState(State.FAILED);
			}
		}
	}
	
	private String createKey(){
		CryptoManager c = SecureSessionControllerImpl.getInstance().getCryptoManager();
		//FIXME make key algorithm configurable via CM
		//		String algo = props.getProperty("symmetricKeyGeneratorAlgorithm");
		String algo = "AES";
		String key = null;
		try {
			key = c.generateSymmetricKey(algo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}
	
	private boolean sendKey(String key, ProtocolContext context){
		//It is only possible to try 3 times after that new protocol has to be started
		if(nrOfTries <= 3){
			nrOfTries++;

			context.setSentSymKey(key);
			context.sendKey(key);
			
			return true;
		}else{
			logger.debug("Max number of tries reached!");
			return false;
		}
	}

}
