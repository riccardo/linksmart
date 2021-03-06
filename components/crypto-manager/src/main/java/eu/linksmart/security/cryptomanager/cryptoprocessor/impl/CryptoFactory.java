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
 * Copyright (C) 2006-2010 Fraunhofer SIT,
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

package eu.linksmart.security.cryptomanager.cryptoprocessor.impl;

import org.apache.log4j.Logger;

import eu.linksmart.security.cryptomanager.cryptoprocessor.CryptoMessageFormatProcessor;
import eu.linksmart.security.cryptomanager.impl.Configuration;
import eu.linksmart.security.cryptomanager.keymanager.KeyManager;
import eu.linksmart.security.cryptomanager.keymanager.impl.KeyManagerImpl;

/**
 * This factory is used to create an instance of Crypto, depending on the
 * message format. <p> Currently, only XMLEnc is supported.
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 * 
 */
public class CryptoFactory {

	private final static Logger logger = Logger.getLogger("CryptoFactory");

	/**
	 * Returns a <code>Crypto</code> implementation for a certain message
	 * format. <p> Currently, <code>XMLEnc</code> is the only supported format.
	 * 
	 * @param messageFormat
	 * @return
	 */
	public static CryptoMessageFormatProcessor getProcessorInstance(
			String messageFormat) {
		CryptoMessageFormatProcessor instance = null;
		String cryptoClassName =
				Configuration.getInstance().getClassByFormat(messageFormat);
		if (cryptoClassName != null) {
			try {
				Object object = Class.forName(cryptoClassName).newInstance();
				instance =
						(CryptoMessageFormatProcessor) object;
			} catch (InstantiationException e) {
				logger.error("Could not instantiate " + cryptoClassName, e);
			} catch (IllegalAccessException e) {
				logger.error("Illegal access to " + cryptoClassName, e);
			} catch (ClassNotFoundException e) {
				logger.error("Could not find " + cryptoClassName, e);
			}
		} else {
			logger.error("Format " + messageFormat + " has not been configured");
		}
		return instance;
	}

	/**
	 * Gets an instance of the KeyManager.
	 * 
	 * @return
	 */
	public static KeyManager getKeyManagerInstance() {
		return KeyManagerImpl.getInstance();
	}

	/**
	 * Just for testing.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CryptoFactory.getProcessorInstance("XMLEnc");
	}
}
