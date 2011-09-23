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

package eu.linksmart.security.cryptomanager.impl;

import java.io.File;
import java.util.Collection;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * Keeps all configuration for the CryptoManager. <p> This class reads the
 * configuration from <code>cond/config.xml</code>.
 * 
 * @author Julian Schütte
 * 
 */
public class Configuration extends XMLConfiguration {

	private static final long serialVersionUID = -3666397351160503356L;
	private final static Logger logger = Logger.getLogger(Configuration.class
			.getName());
	private static Configuration instance;
	private static String configFileOSGi = CryptoManagerImpl.CONFIGFOLDERPATH
			+ "/cryptomanager-config.xml";
	private static String configFile = "src/cryptomanager-config.xml";

	/**
	 * Private default constructor. Nobody should ever call this.
	 */
	private Configuration() {

	}

	/**
	 * Private Constructor to avoid direct instantiation.
	 * 
	 * @param fileName
	 *            Configuration file name.
	 */
	private Configuration(String fileName) {
		init(fileName);
	}

	/**
	 * Do some initialisation, read the config file.
	 * 
	 * @param fileName
	 *            Configuration file name.
	 */
	private void init(String fileName) {
		logger.debug("Loading configuration from " + fileName);
		try {
			load(new File(configFileOSGi));
		} catch (ConfigurationException e) {
			try {
				load(new File(configFile));
			} catch (Exception e2) {
				logger.error(e);
			}
		}
	}

	/**
	 * Singleton access method.
	 * 
	 * @return Singleton
	 */
	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration(configFileOSGi);
		}
		return instance;
	}

	/**
	 * Only for testing purposes.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		Configuration config = Configuration.getInstance();
		logger.debug(config.getString("database.user-name"));
		logger.debug(config.getString("database.password"));
	}

	/**
	 * Return the message formats that are supported by this implementation <p>
	 * 
	 * @return A String array of the message format names.
	 */
	public String[] getSupportedMessageFormats() {
		String[] messageFormats =
				instance.getStringArray("formats.format.name");
		logger.debug(messageFormats);
		return messageFormats;
	}

	/**
	 * Returns the type of the keystore in use. <p> Possible values are
	 * <code>BKS</code> or <code>JKS</code>, for example.
	 * 
	 * @return
	 */
	public String getKeyStoreType() {
		return instance.getString("keystore.type");
	}

	/**
	 * Returns the file name of the keystore file.
	 * 
	 * @return
	 */
	public String getKeyStoreFile() {
		return instance.getString("keystore.file");
	}

	/**
	 * Returns the password that shall be used to open the keystore file.
	 * 
	 * @return
	 */
	public String getKeyStorePassword() {
		return instance.getString("keystore.password");
	}

	/**
	 * Returns the class name of a Crypto implementation, given the implemented
	 * message format. <p>
	 * 
	 * @param messageFormat
	 *            The name of the message format.
	 * @return The full qualified name of the class implementing the message
	 *         format.
	 */
	public String getClassByFormat(String messageFormat) {
		Object obj = instance.getProperty("formats.format.name");
		if (obj instanceof Collection) {
			int size = ((Collection) obj).size();
			for (int i = 0; i < size; i++) {
				if (messageFormat.equals(this.getProperty("formats.format(" + i
						+ ").name"))) {
					return (String) this.getProperty("formats.format(" + i
							+ ").class");
				}
			}
		} else if (obj instanceof String) {
			String val = (String) this.getProperty("formats.format.class"); 
			return val;
		} else {
			logger.warn("Unknown message format: " + messageFormat);
		}

		return null;
	}
}
