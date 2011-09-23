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
 * Copyright (C) 2006-2010 [Telefonica I+D]
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

package eu.linksmart.securecomm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * HandshakeHelper
 */
public class HandshakeHelper {

	private static Properties properties = null;
	private static KeyStore ks = null;
	private static Logger logger = Logger.getLogger(HandshakeHelper.class);

	private static final String CONFIGURATION_FILE =
		"NetworkManager/Security/crypto.properties";

	static {
		try {
			FileInputStream f = null;
			f = new FileInputStream(CONFIGURATION_FILE);
			properties = new Properties();
			
			try {
				properties.load(f);
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.out.println(e.getStackTrace());
			}

			f.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}
	}

	static {
		try {
			HandshakeHelper.loadProperties();
			HandshakeHelper.loadKeystore();
		} catch (Exception e) {
			logger.error("Error in static", e);
		}
	}

	/**
	 * Constructor
	 */
	public HandshakeHelper() {}

	/**
	 * Stores a certificate into the internal store
	 * 
	 * @param alias the alias of the certificate
	 * @param cert the certificate
	 * @throws Exception
	 */
	public static void storeCert(String alias, X509Certificate cert)
			throws Exception {

		try {
			KeyStore ks = HandshakeHelper.loadKeystore();

			if (!ks.containsAlias(alias)) {
				KeyStore.TrustedCertificateEntry entry = 
					new KeyStore.TrustedCertificateEntry(cert);
				ks.setEntry(alias, entry, null);
				ks.setCertificateEntry(alias, cert);
				java.io.FileOutputStream fos = null;
				
				try {
					fos = new java.io.FileOutputStream(properties.getProperty(
						"org.apache.ws.security.crypto.merlin.file"));
					ks.store(fos, properties.getProperty(
						"org.apache.ws.security.crypto.merlin.keystore.password").
							toCharArray());
				} finally {
					if (fos != null) {
						fos.close();
					}
				}
			} else {
				logger.info("Cert already in keystore!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Stored certificate with alias " + alias + " to file "
			+ properties.getProperty("org.apache.ws.security.crypto.merlin.file"));
	}

	/**
	 * Deletes a certificate from the internal keystore
	 * 
	 * @param alias the alias of the certificate
	 */
	public static void deleteCert(String alias) throws Exception {
		KeyStore ks = HandshakeHelper.loadKeystore();

		if (ks.containsAlias(alias)) {
			ks.deleteEntry(alias);
			java.io.FileOutputStream fos = null;
			try {
				fos = new java.io.FileOutputStream(properties.getProperty(
					"org.apache.ws.security.crypto.merlin.file"));
				ks.store(fos, properties.getProperty(
					"org.apache.ws.security.crypto.merlin.keystore.password").
						toCharArray());
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		}
	}

	/**
	 * Deletes all certificates from the internal keystore
	 */
	public static void deleteAllCerts() throws Exception {
		KeyStore ks = HandshakeHelper.loadKeystore();
		for (Enumeration<String> enu = ks.aliases(); enu.hasMoreElements();) {
			ks.deleteEntry(enu.nextElement());
		}
	}

	/**
	 * Load the properties from the configuration file
	 * @throws Exception
	 */
	private static void loadProperties() throws Exception {
		properties = new Properties();
		properties.load(new FileInputStream(CONFIGURATION_FILE));
	}

	/**
	 * Loads the internal keystore
	 * 
	 * @return the internal keystore
	 * @throws Exception
	 */
	private static KeyStore loadKeystore() throws Exception {
		ks = KeyStore.getInstance(KeyStore.getDefaultType());
		HandshakeHelper.loadProperties();
		InputStream is = new FileInputStream(properties.getProperty(
			"org.apache.ws.security.crypto.merlin.file"));
		ks.load(is, properties.getProperty(
			"org.apache.ws.security.crypto.merlin.keystore.password").toCharArray());
		is.close();

		return ks;
	}

}
