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

package eu.linksmart.security.communication.impl.asym;

import java.util.Properties;

/**
 * This type encapsulates a command sent to the protocol FSM. A command
 * represents commands sent to the FSM via the underlaying network, as well as
 * commands sent by the user using, for example, the {@link SecureSessionGui}.
 * Basically this implementation uses Properties as its parent class to provide
 * easy serialization of an instance to XML.
 * 
 * @author Stephan Heuser - stephan.heuser@sit.fraunhofer.de
 * 
 */
public class Command extends Properties {
	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 7633612571683213173L;

	/**
	 * Commands to be sent between client and server
	 */
	public static final int CLIENT_HELLO = 1;
	public static final int CLIENT_BYE = 2;
	public static final int CLIENT_REQUESTS_INFO = 3;
	public static final int CLIENT_REQUESTS_KEY = 4;
	public static final int CLIENT_INVITE_DOMAIN = 6;
	public static final int CLIENT_ACK = 7;
	public static final int CLIENT_SEND_DOMAIN_CONFIG = 8;

	public static final int SERVER_HERE = 10;
	public static final int SERVER_LIST_PART = 11;
	public static final int SERVER_SEND_INFO = 12;
	public static final int SERVER_SEND_KEY = 13;
	public static final int SERVER_SEND_DOMAIN_ACCEPTANCE = 14;
	public static final int SERVER_BYE = 15;
	public static final int SERVER_NO = 16;

	public static final String SIGNED_PAYLOAD = "signedPayload";
	public static final String ENCRYPTED_PAYLOAD = "encryptedPayload";
	public static final String SIGNED_AND_ENCRYPTED_PAYLOAD = "signedAndEncryptedPayload";
	public static final String SIGNATURE = "signature";
	public static final String COMMAND = "command";
	public static final String SERVER = "server";
	public static final String CLIENT = "client";
	public static final String SYMMETRIC_KEY = "symmetrickey";
	public static final String CLIENT_NONCE = "clientNonce";
	public static final String SERVER_NONCE = "serverNonce";
	public static final String APPLICATION_MESSAGE= "applicationMessage";

	public static final String CLIENT_KEY_IDENTIFIER = "clientKeyIdentifier";
	public static final String SERVER_KEY_IDENTIFIER = "serverKeyIdentifier";

	public static final String DEVICE_DESCRIPTION = "deviceDescription";

	/**
	 * Create a new Command with no information given.
	 */
	
	public Command() {
		
	}
	/**
	 * Create a new command with no payload
	 * 
	 * @param commandType
	 *            type of the command
	 */
	public Command(Integer commandType) {
		setProperty("command", commandType.toString());
		setProperty("data", "");
	}

	/**
	 * Create a new command with a payload
	 * 
	 * @param commandType
	 *            type of the command
	 * @param data
	 *            the payload
	 */

	public Command(Integer commandType, byte[] data) {
		setProperty("command", commandType.toString());
	}

}
