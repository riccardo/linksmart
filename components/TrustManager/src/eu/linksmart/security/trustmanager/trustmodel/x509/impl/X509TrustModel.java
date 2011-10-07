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
 * Copyright (C) 2006-2010 Fraunhofer FIT,
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

package eu.linksmart.security.trustmanager.trustmodel.x509.impl;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.linksmart.security.trustmanager.impl.TrustManagerImpl;
import eu.linksmart.security.trustmanager.trustmodel.TrustModel;
import eu.linksmart.security.trustmanager.trustmodel.x509.config.X509TrustModelConfiguration;
import eu.linksmart.security.trustmanager.util.Base64.OutputStream;
import eu.linksmart.security.trustmanager.util.Util;

public class X509TrustModel implements TrustModel {

	/** Logger used for logging */
	private Logger logger = Logger.getLogger("trustmanager");
	/** Identifier of this Trust Model */
	public static final String IDENTIFIER = "X509TrustModel";
	
	private Properties settings;
	private KeyStore keystore = null;
	private PKIXParameters params = null;
	private String KEYSTORE_FILE;
	private String KEYSTORE_PASS;
	private String KEYSTORE_TYPE;
	private String KEYSTORE_DEPLOY_DIR;
	private String KEYSTORE_JAR_PATH = "resources/x509/linksmart.trustmanager.keystore.jks";
	public static final String FILE_SEPARATOR =	System.getProperty("file.separator");

	@Override
	public double getTrustValue(byte[] token) {
		//load certificate
		X509Certificate cert = null;
		CertificateFactory cf = null;
		CertPath certPath = null; 
		try {
			cf = CertificateFactory.getInstance("X509");
			ByteArrayInputStream bis = new ByteArrayInputStream(token);
			cert = (X509Certificate) cf.generateCertificate(bis);
			logger.debug("Parsed x509 certificate");

			//create certificate list for certificate to check
			List<X509Certificate> certlist = new ArrayList<X509Certificate>();
			certlist.add(cert);
			certPath = cf.generateCertPath(certlist);
			logger.debug("Created certificate path");
		} catch (CertificateException e) {
			logger.error("Error creating certificate from " + token,e);
			return -1;
		}	

		//validate certificate path
		CertPathValidator certPathValidator;
		try {
			certPathValidator = CertPathValidator.getInstance("PKIX");
			certPathValidator.validate(certPath, params);
		} catch (CertPathValidatorException e) {
			//Certificate not valid
			return 0;
		} catch (Exception e) {
			logger.error("Error during validating certificate!",e);
			return -1;
		}
		//if there is no exception then certificate is valid
		return 1;
	}

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public void initialize() {
		try{
			logger.debug("Loading settings for TrustManager X509");
			settings = new Properties();
			logger.debug("Loading file " + "x509settings.properties");
			try {
				settings.load(this.getClass().getResourceAsStream("/resources/x509/x509settings.properties"));
			} catch (Exception e) {
				logger.error("Error loading X509 settings!", e);
			}
			
			KEYSTORE_FILE = settings.getProperty("trustmanager.x509.keystore.file", "linksmart.trustmanager.keystore.jks");
			KEYSTORE_PASS = settings.getProperty("trustmanager.x509.keystore.pass", "trustmanager");
			KEYSTORE_TYPE = settings.getProperty("trustmanager.x509.keystore.type", "JKS");
			KEYSTORE_DEPLOY_DIR = settings.getProperty("trustmanager.x509.keystore.deploydir", TrustManagerImpl.TRUSTMANAGER_RESOURCE_FOLDERPATH + FILE_SEPARATOR + "X509");

			Hashtable<String, String> hashFilesExtract = new Hashtable<String, String>();
			hashFilesExtract.put(KEYSTORE_DEPLOY_DIR + FILE_SEPARATOR + KEYSTORE_FILE, KEYSTORE_JAR_PATH);
			
			Util.createFolder(KEYSTORE_DEPLOY_DIR);
			Util.extractFilesJar(hashFilesExtract);
			
			// load the keystore
			try {
				loadKeyStore();
			} catch (Exception e) {
				logger.error(e);
			}

			//set parameters
			params = new PKIXParameters(keystore);
			params.setRevocationEnabled(false);
		}catch(Exception e){
			logger.error("Error during initializing X509 trustmodel. Methods will behave unstable!",e);
		}
	}

	private void loadKeyStore() {
		try {
		keystore = KeyStore.getInstance(KEYSTORE_TYPE);
		InputStream fis = new FileInputStream(KEYSTORE_DEPLOY_DIR + FILE_SEPARATOR + KEYSTORE_FILE);
			keystore.load(fis, KEYSTORE_PASS.toCharArray());
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public Class getConfigurator() {
		return X509TrustModelConfiguration.class;
	}

	public void addCertificate(String alias, X509Certificate certificate) throws KeyStoreException {
		TrustedCertificateEntry entry = new TrustedCertificateEntry(certificate);
		keystore.setEntry(alias, entry, null);
		storeKeystore();
	}

	public boolean removeCertificatebyAlias(String alias) {
		try {
			keystore.deleteEntry(alias);
			storeKeystore();
			return true;
		} catch (KeyStoreException e) {
			logger.debug("Cannot find given entry.");
			return false;
		}
	}

	public boolean removeCertificate(X509Certificate certificate) {
		String alias;
		try {
			alias = keystore.getCertificateAlias(certificate);
			keystore.deleteEntry(alias);
			storeKeystore();
			return true;
		} catch (KeyStoreException e) {
			logger.debug("Cannot find given entry.");
			return false;
		}
	}
	
	private void storeKeystore(){
		try {
			FileOutputStream fos = new FileOutputStream(KEYSTORE_DEPLOY_DIR + FILE_SEPARATOR + KEYSTORE_FILE);
				keystore.store(fos, KEYSTORE_PASS.toCharArray());
			} catch (Exception e) {
				logger.error(e);
			}
	}
	
	@Override
	public Class getConfiguratorClass() {
		return X509TrustModelConfigurationImpl.class;
	}

	@Override
	public String getTrustToken(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}
}
