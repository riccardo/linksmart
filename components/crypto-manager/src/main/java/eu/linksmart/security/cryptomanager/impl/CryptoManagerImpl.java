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

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.cryptomanager.SecurityLevel;
import eu.linksmart.security.cryptomanager.cryptoprocessor.CryptoMessageFormatProcessor;
import eu.linksmart.security.cryptomanager.cryptoprocessor.impl.CryptoFactory;
import eu.linksmart.security.cryptomanager.keymanager.KeyManager;

/**
 * Facade class that provides all public methods from different
 * CryptoMessageFormatProcessors and Keymanagers. <p>
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 * 
 */
@Component(name="CryptoManager", immediate=true)
@Service
@Property(name="service.remote.registration", value="true")
public class CryptoManagerImpl implements CryptoManager{

	private static Logger logger = Logger.getLogger(CryptoManagerImpl.class);
	private static String SEPARATOR = System.getProperty("file.separator");

	final static public String CONFIGFOLDERPATH = 
		"linksmart" + SEPARATOR + "eu.linksmart.security.cryptomanager" + SEPARATOR + "configuration";
	final static public String RESOURCEFOLDERPATH = 
		"linksmart" + SEPARATOR + "eu.linksmart.security.cryptomanager" + SEPARATOR + "resources";

	private static BundleContext context;
	private CryptoMessageFormatProcessor cryptoProcessor;
	private KeyManager keyManager;
	private CryptoManagerConfigurator configurator;
	private boolean activated = false;

    @Reference(name="ConfigurationAdmin",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindConfigAdmin",
            unbind="unbindConfigAdmin",
            policy=ReferencePolicy.STATIC)
    protected ConfigurationAdmin configAdmin = null;

    protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
    	logger.debug("CryptoManager::binding ConfigurationAdmin");
        this.configAdmin = configAdmin;
    }

    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
    	logger.debug("CryptoManager::un-binding ConfigurationAdmin");
        this.configAdmin = null;
    }

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
    @Activate
	protected void activate(ComponentContext context) {
    	logger.info("[activating CryptoManager]");
		CryptoManagerImpl.context = context.getBundleContext();
		// Extract all configuration files from the bundle's jar file to the
		// filesystem
		Hashtable<String, String> HashFilesExtract =
			new Hashtable<String, String>();
		logger.debug("Deploying CryptoManager config files");
		HashFilesExtract.put(CONFIGFOLDERPATH + SEPARATOR + "cryptomanager-config.xml",
		"configuration/cryptomanager-config.xml");
		HashFilesExtract.put(RESOURCEFOLDERPATH + SEPARATOR + "create_cryptomanager_db.sql",
		"resources/create_cryptomanager_db.sql");
		HashFilesExtract.put(RESOURCEFOLDERPATH + SEPARATOR + "delete_cryptomanager_db.sql",
		"resources/delete_cryptomanager_db.sql");
		HashFilesExtract
		.put(RESOURCEFOLDERPATH + SEPARATOR + "keystore.bks", "resources/keystore.bks");
		try {
			JarUtil.createDirectory(CONFIGFOLDERPATH);
			JarUtil.createDirectory(RESOURCEFOLDERPATH);
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
		//configurator = new CryptoManagerConfigurator(this, context.getBundleContext());
        configurator = new CryptoManagerConfigurator(this, context.getBundleContext(),configAdmin);
		configurator.registerConfiguration();
		activated = true;

		logger.info("CryptoManager Activated");
	}

	/**
	 * Clean up work when bundle is stopped
	 */
    @Deactivate
	protected void deactivate(ComponentContext context) {
    	logger.info("[de-activating CryptoManager]");
		activated = false;
		keyManager.close();
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
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#storePublicKeyWithFriendlyName
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean storePublicKeyWithFriendlyName(String friendlyName, String encodedCert, String algorithm_id) throws SQLException {
		return keyManager.storePublicKey(friendlyName, encodedCert, algorithm_id);
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
			String virtualAddress) throws SQLException, NoSuchAlgorithmException,
			IOException, KeyStoreException, CertificateException,
			InvalidKeyException, SecurityException, SignatureException,
			IllegalStateException, NoSuchProviderException {
		String certRef =
			keyManager.generateCertificateWithAttributes(xmlAttributes);

		keyManager.addPrivateKeyForService(virtualAddress, certRef);
		keyManager.addCertificateForService(virtualAddress, certRef);
		logger.info("Generated certificate with attributes for VirtualAddress " + virtualAddress
				+ ". Available by ref " + certRef);
		return certRef;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.impl.CryptoManager#
	 * generateKeyFromPasswordWithFriendlyName(java.lang.String, java.lang.String, int, java.lang.String)
	 */
	public boolean generateKeyFromPasswordWithFriendlyName(String friendlyName, String password, int keyLength, String algo) throws SQLException, KeyStoreException{
		return keyManager.generateKeyFromPassword(friendlyName, password, keyLength, algo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.impl.CryptoManager#
	 * generateKeyFromPassword(java.lang.String, int, java.lang.String)
	 */
	public String generateKeyFromPassword(String password, int keyLength, String algo){
		return keyManager.generateKeyFromPassword(password, keyLength, algo);
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
		return keyManager.generateSymmetricKey(getKeySize(SecurityLevel.MIDDLE, "AES"), "AES");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.impl.CryptoManager#
	 * generateSymmetricKey(java.lang.String)
	 */
	public String generateSymmetricKey(String algo) throws SQLException,
			NoSuchAlgorithmException, IOException, KeyStoreException,
			CertificateException, InvalidKeyException, SecurityException,
			SignatureException, IllegalStateException, NoSuchProviderException {
		return generateSymmetricKey(getKeySize(SecurityLevel.MIDDLE, algo), algo);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#generateSymmetricKey
	 * (java.lang.String)
	 */
	public String generateSymmetricKey(int keySize, String algo) throws SQLException,
	NoSuchAlgorithmException, IOException, KeyStoreException,
	CertificateException, InvalidKeyException, SecurityException,
	SignatureException, IllegalStateException, NoSuchProviderException {
		return keyManager.generateSymmetricKey(keySize, algo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#generateSymmetricKeyWithFriendlyName
	 * (java.lang.String, int, java.lang.String)
	 */
	public boolean generateSymmetricKeyWithFriendlyName(String friendlyName, int keysize, String algo) throws InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, SecurityException, SignatureException, IllegalStateException, NoSuchProviderException, SQLException, IOException
	{
		return keyManager.generateSymmetricKey(friendlyName, keysize, algo);		
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
	 * @see
	 * eu.linksmart.security.cryptomanager.impl.CryptoManager#storeSymmetricKeyWithFriendlyName
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean storeSymmetricKeyWithFriendlyName(String friendlyName, String algo, String key) throws SQLException{
		return keyManager.storeSymmetricKey(friendlyName, algo, key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.impl.CryptoManager#
	 * addCertificateForService(java.lang.String, java.lang.String)
	 */
	public boolean addCertificateForService(String virtualAddress, String certRef) {
		return keyManager.addCertificateForService(virtualAddress, certRef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.impl.CryptoManager#
	 * addPrivateKeyForService(java.lang.String, java.lang.String)
	 */
	public boolean addPrivateKeyForService(String virtualAddress, String certRef) {
		return keyManager.addPrivateKeyForService(virtualAddress, certRef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.impl.CryptoManager#
	 * getCertificateReference(java.lang.String)
	 */
	public String getCertificateReference(String virtualAddress) {
		return keyManager.getCertRefByVirtualAddress(virtualAddress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.impl.CryptoManager#
	 * getPrivateKeyReference(java.lang.String)
	 */
	public String getPrivateKeyReference(String virtualAddress) {
		return keyManager.getPrivateKeyRefByVirtualAddress(virtualAddress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.linksmart.security.cryptomanager.impl.CryptoManager#
	 * getKeySize(eu.linksmart.security.cryptomanager.SecurityLevel, java.lang.String)
	 */
	public int getKeySize(SecurityLevel level, String algo){
		return configurator.getKeySize(level, algo);
	}
	
	public byte[] calculateMac(String identifier, String data, String algorithm) throws NoSuchAlgorithmException, KeyStoreException, InvalidKeyException{
			SecretKey key = keyManager.loadSymmetricKey(identifier, algorithm);
			Mac mac = Mac.getInstance(algorithm);
			mac.init(key);
			
			return mac.doFinal(data.getBytes());
	}

	public boolean identifierExists(String identifier){
		try{
		return keyManager.identifierExists(identifier);
		}catch(SQLException e){
			logger.error("Error in CryptoManager database",e);
			return false;
		}
	}

	public boolean deleteEntry(String identifier) {
		return keyManager.deleteEntry(identifier);
	}
}
