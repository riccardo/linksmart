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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.security.cryptomanager.CryptoManager;

/**
 * Facade class that provides all public methods from different
 * CryptoMessageFormatProcessors and Keymanagers. <p>
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 * 
 */
public class CryptoManagerImpl implements CryptoManager {

	private static Logger logger = Logger.getLogger(CryptoManagerImpl.class);

	final static public String CONFIGFOLDERPATH = "CryptoManager/conf";
	final static public String RESOURCEFOLDERPATH = "CryptoManager/resources";
	public static BundleContext context;

	private CryptoMessageFormatProcessor cryptoProcessor;
	private KeyManager keyManager;

//	static {
//		// Use this bundle's classloader to initialise the bouncycastle crypto
//		// provider
//		logger.debug("Loading bouncycastle crypto provider");
//		try {
//			ClassLoader sysloader =
//					Thread.currentThread().getContextClassLoader();
//			Class<?> loaded =
//					sysloader.loadClass(BouncyCastleProvider.class.getName());
//			Provider provider = (Provider) loaded.newInstance();
//			Security.addProvider(provider);
//		} catch (Throwable t) {
//			logger.error(t);
//		}
//	}

	/**
	 * Constructor.
	 */
	public CryptoManagerImpl() {

	}

	/**
	 * Start the Cryptomanager as OSGi bundle, publish its functionality as a
	 * web service.
	 * 
	 */
	protected void activate(ComponentContext context) {
		CryptoManagerImpl.context = context.getBundleContext();
		// Extract all configuration files from the bundle's jar file to the
		// filesystem
		Hashtable<String, String> HashFilesExtract =
				new Hashtable<String, String>();
		logger.debug("Deploying CryptoManager config files");
		HashFilesExtract.put(CONFIGFOLDERPATH + "/cryptomanager-config.xml",
				"cryptomanager-config.xml");
		HashFilesExtract.put(CONFIGFOLDERPATH + "/create_cryptomanager_db.sql",
				"create_cryptomanager_db.sql");
		HashFilesExtract.put(CONFIGFOLDERPATH + "/delete_cryptomanager_db.sql",
				"delete_cryptomanager_db.sql");
		HashFilesExtract
				.put(CONFIGFOLDERPATH + "/keystore.bks", "keystore.bks");
		try {
			JarUtil.createFolder(CONFIGFOLDERPATH);
			JarUtil.createFolder(RESOURCEFOLDERPATH);
			JarUtil.extractFilesJar(HashFilesExtract);
		} catch (IOException e) {
			logger.error("Needed folder has not been created...", e);
		}

		// Use this bundle's classloader to initialise the bouncycastle crypto
		// provider
		logger.debug("Loading bouncycastle crypto provider");
		try {
			ClassLoader sysloader =
					Thread.currentThread().getContextClassLoader();
			Class<?> loaded =
					sysloader.loadClass(BouncyCastleProvider.class.getName());
			Provider provider = (Provider) loaded.newInstance();
			Security.addProvider(provider);
		} catch (Throwable t) {
			logger.error(t);
		}

		keyManager = CryptoFactory.getKeyManagerInstance();
		cryptoProcessor = CryptoFactory.getProcessorInstance("XMLEnc");
		System.out.println("CryptoManager Activated");
	}

	/**
	 * Clean up work when bundle is stopped
	 */
	protected void deactivate(ComponentContext context) {

		try {
			DBmanagement.getInstance().close();
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#decrypt(java.lang
	 * .String)
	 */
	public String decrypt(String encryptedData) {
		return cryptoProcessor.decrypt(encryptedData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#encryptAsymmetric
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public String encryptAsymmetric(String documentString, String identifier,
			String format) throws Exception {
		return cryptoProcessor.encryptAsymmetric(documentString, identifier,
				format);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#encryptSymmetric
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public String encryptSymmetric(String documentString, String identifier,
			String format) throws Exception {
		return cryptoProcessor.encryptSymmetric(documentString, identifier,
				format);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.impl.CryptoManager#
	 * getEncodedPublicKeyByIdentifier(java.lang.String)
	 */
	public byte[] getEncodedPublicKeyByIdentifier(String identifier)
			throws KeyStoreException {
		return keyManager.getEncodedPublicKeyByIdentifier(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.impl.CryptoManager#
	 * getCertificateByIdentifier(java.lang.String)
	 */
	public Certificate getCertificateByIdentifier(String identifier)
			throws KeyStoreException {
		return keyManager.getCertificateByIdentifier(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#getSupportedFormats
	 * ()
	 */
	public Vector<String> getSupportedFormats() {
		Vector<String> formats = new Vector<String>();
		formats.copyInto(cryptoProcessor.getSupportedFormats());
		return formats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#sign(java.lang
	 * .String, java.lang.String)
	 */
	public String sign(String data, String format) {
		return cryptoProcessor.sign(data, format);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#sign(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	public String sign(String data, String format, String identifier) {
		return cryptoProcessor.sign(data, format, identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#storePublicKey
	 * (java.lang.String, java.lang.String)
	 */
	public String storePublicKey(String encodedCert, String algorithm_id) {
		return keyManager.storePublicKey(encodedCert, algorithm_id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#verify(java.lang
	 * .String)
	 */
	public String verify(String data) {
		return cryptoProcessor.verify(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.CryptoManager#
	 * generateCertificateWithAttributes(java.lang.String, java.lang.String)
	 */
	public String generateCertificateWithAttributes(String xmlAttributes,
			String hid) throws SQLException, NoSuchAlgorithmException,
			IOException, KeyStoreException, CertificateException,
			InvalidKeyException, SecurityException, SignatureException,
			IllegalStateException, NoSuchProviderException {
		String certRef =
				keyManager.generateCertificateWithAttributes(xmlAttributes);

		keyManager.addPrivateKeyForHID(hid, certRef);
		keyManager.addCertificateForHID(hid, certRef);
		logger.info("Generated certificate with attributes for HID " + hid
				+ ". Available by ref " + certRef);
		return certRef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#generateSymmetricKey
	 * ()
	 */
	public String generateSymmetricKey() throws SQLException,
			NoSuchAlgorithmException, IOException, KeyStoreException,
			CertificateException, InvalidKeyException, SecurityException,
			SignatureException, IllegalStateException, NoSuchProviderException {
		return keyManager.generateSymmetricKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#generateSymmetricKey
	 * (java.lang.String)
	 */
	public String generateSymmetricKey(String algo) throws SQLException,
			NoSuchAlgorithmException, IOException, KeyStoreException,
			CertificateException, InvalidKeyException, SecurityException,
			SignatureException, IllegalStateException, NoSuchProviderException {
		return keyManager.generateSymmetricKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#storeSymmetricKey
	 * (java.lang.String, java.lang.String)
	 */
	public String storeSymmetricKey(String algo, String key)
			throws SQLException, NoSuchAlgorithmException, IOException,
			KeyStoreException, CertificateException, InvalidKeyException,
			SecurityException, SignatureException, IllegalStateException,
			NoSuchProviderException {
		return keyManager.storeSymmetricKey(algo, key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.impl.CryptoManager#
	 * getAttributesFromCertificate(java.lang.String)
	 */
	public Properties getAttributesFromCertificate(String identifier)
			throws SQLException, KeyStoreException,
			CertificateEncodingException {
		return keyManager.getAttributesFromCertificate(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.impl.CryptoManager#
	 * getPrivateKeyByIdentifier(java.lang.String)
	 */
	public PrivateKey getPrivateKeyByIdentifier(String identifier) {
		return keyManager.getPrivateKeyByIdentifier(identifier);
	}

	public boolean addCertificateForHID(String hid, String certRef) {
		return keyManager.addCertificateForHID(hid, certRef);
	}

	public boolean addPrivateKeyForHID(String hid, String certRef) {
		return keyManager.addPrivateKeyForHID(hid, certRef);
	}

	public String getCertificateReference(String hid) {
		return keyManager.getCertRefByHID(hid);
	}

	public String getPrivateKeyReference(String hid) {
		return keyManager.getPrivateKeyRefByHID(hid);
	}

}
