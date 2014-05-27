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

package eu.linksmart.security.cryptomanager.keymanager.impl;

import eu.linksmart.security.cryptomanager.impl.Configuration;
import eu.linksmart.security.cryptomanager.impl.CryptoManagerImpl;
import eu.linksmart.security.cryptomanager.impl.CustomCertificateAttributes;
import eu.linksmart.security.cryptomanager.keymanager.KeyManager;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Class for managing the keystore and creating cryptographic keys. <p>
 * 
 * @author Julian Sch�tte
 * 
 */
public class KeyManagerImpl implements KeyManager {

	private static KeyManagerImpl instance;
	private final static Logger logger = Logger.getLogger(KeyManagerImpl.class
			.getName());
	private static final String PRIVATE_ENCRYPTION_KEY_PASS = "linksmartdemo";
	private static int CACHE_SIZE = 10;
	/**	 Unique random salt for LinkSmart. */
	/** Number of times to iterate the hash. */
	private static int PASSWORD_DERIVATION_ITERATION = 1000;

	private KeyStore ks;
	private DBmanagement dbase;
	private Random rand = new Random();
	private HashMap<String, String> certificateRefCache =
		new HashMap<String, String>();
	private HashMap<String, Integer> certificateCacheUse = 
		new HashMap<String, Integer>();
	private HashMap<String, String> privateKeyRefCache =
		new HashMap<String, String>();
	private HashMap<String, Integer> privateKeyCacheUse = 
		new HashMap<String, Integer>();
	Timer cacheClearer = null;

	/**
	 * Private constructor to avoid direct instantiation.
	 * 
	 * @throws IOException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	KeyManagerImpl() {
		try {
			init();
		} catch (Exception e) {
			logger.error("Error while initialising the KeyManager", e);
		}
	}

	/**
	 * Do some initialisation: Read the keystore file and initialise the
	 * database.
	 * 
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	private void init() throws KeyStoreException, NoSuchAlgorithmException,
	CertificateException, IOException {
		logger.info("initializing crypto-key-manager");
		// Keystore-Datei laden (Konfiguration in conf/config.xml)
		ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				getClass().getClassLoader());
		try {
			ks =
				KeyStore.getInstance(Configuration.getInstance()
						.getKeyStoreType());
			Thread.currentThread().setContextClassLoader(oldLoader);

			File f =
				new File(CryptoManagerImpl.RESOURCEFOLDERPATH + System.getProperty("file.separator")
						+ Configuration.getInstance().getKeyStoreFile());
			if (f.exists()) {
				logger.debug("Loading keystore file " + f.getAbsolutePath());
				FileInputStream fis = new FileInputStream(f);
				ks.load(fis, Configuration.getInstance().getKeyStorePassword()
						.toCharArray());
				fis.close();
			} else {
				logger.warn("Keystore file does not exist. Creating a new one ("
						+ f.getAbsolutePath() + f.getName() + ")");
				FileOutputStream fos = new FileOutputStream(f);
				ks.store(fos, Configuration.getInstance().getKeyStorePassword()
						.toCharArray());
				fos.close();
			}

			// Object for database access
			dbase = DBmanagement.getInstance();

			//Start cache clearing timer
			cacheClearer = new Timer(true);
			cacheClearer.schedule(new CacheClearer(), 0, 60*60*1000); // 60min*60sec*1000ms
			
			certificateCacheUse = new HashMap<String, Integer>();
			certificateRefCache = new HashMap<String, String>();
			privateKeyCacheUse = new HashMap<String, Integer>();
			privateKeyRefCache = new HashMap<String, String>();

			logger.info("crypto-key-manager initialized");
			
		} catch (SQLException e) {
			logger.error("Error while initialising KeyManager", e);
		}
	}

	public void close()
	{
		try{
			dbase.close();
			cacheClearer.cancel();
		}catch(Exception e){
			logger.warn(e.getMessage(), e);
		}
	}

	/**
	 * Get an instance of this class (singleton pattern)
	 * 
	 * @return
	 */
	public static KeyManagerImpl getInstance() {
		if (instance == null) {
			instance = new KeyManagerImpl();
		}
		return instance;
	}

	/**
	 * Get an instance of the underlying keystore.
	 * 
	 * @return
	 */
	public KeyStore getKeystore() {
		return ks;
	}

	/**
	 * Create a random identifier and store it in the database.
	 * 
	 */
	public String createAndStoreKeyIdentifier() {
		// Identifiers in Datenbank geschrieben.
		String identifier = createKeyIdentifier();
		dbase.storeIdentifier(identifier);
		return identifier;
	}
	
	/**
	 * Creates a random key identifier
	 * @return generated UUID
	 */
	public String createKeyIdentifier(){
		String identifier = java.util.UUID.randomUUID().toString();
		return identifier;
	}


	public boolean deleteEntry(String identifier) {

		logger.debug("Try to delete identifier: " + identifier);

		try {
			if (dbase.identifierExists(identifier)) {
				logger.debug("identifier exists");
				// delete from Hashmaps
				if (privateKeyRefCache.containsValue(identifier)) {
					logger.debug("identifier is in privateKeyRefCache");
					logger.debug("deletion successful: "
							+ privateKeyRefCache.values().remove(identifier));
					this.privateKeyCacheUse.remove(identifier);
				}
				if (certificateRefCache.containsValue(identifier)) {
					logger.debug("identifier is in certificateRefCache");
					logger.debug("deletion successful: "
							+ certificateRefCache.values().remove(identifier));
					this.certificateCacheUse.remove(identifier);
				}
				// delete from KS
				String alias = dbase.getIdentifierAlias(identifier);
				logger.debug("keystore alias for " + identifier + ": " + alias);
				try {
					if (ks.containsAlias(alias)) {
						logger.debug("alias is in keystore, try to delete");
						ks.deleteEntry(alias);
						logger.debug("deletion successful: "
								+ !ks.containsAlias(alias));
					}
				} catch (KeyStoreException e) {
					logger.error(e.getMessage(), e);
				}

				// delete from DB
				logger.debug("delete from DB");
				dbase.removeTableEntry(identifier);
				return true;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return false;

	}

	public String[] getIdentifier() {

		try {
			return dbase.getAllIdentifier();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public Vector<Vector<String>> getIdentifierInfo() {
		try {
			return dbase.getAllIdentifierInfo();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public boolean identifierExists(String friendlyName) throws SQLException {
		return dbase.identifierExists(friendlyName);
	}

	/**
	 * Gibt einen KEK (= Public Key) für einen identifier zurück. <p> Falls kein
	 * Schlüssel existiert, wird ein Paar aus privatem und öffentlichem
	 * Schlüssel erzeugt.
	 * 
	 * @param identifier
	 * @param algorithm_name
	 * @return
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws SQLException
	 * @throws UnrecoverableKeyException
	 * @throws SignatureException
	 * @throws SecurityException
	 * @throws NoSuchProviderException
	 * @throws IllegalStateException
	 */
	public PublicKey loadKeyEncryptionKey(String identifier,
			String algorithm_name) throws InvalidKeyException,
			FileNotFoundException, IOException, InvalidKeySpecException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			SQLException, UnrecoverableKeyException, SecurityException,
			SignatureException, IllegalStateException, NoSuchProviderException {
		String alias = null;
		PublicKey kek = null;
		// In der Datenbank nachsehen, ob es für den identifier einen
		// Schlüssel
		// gibt und falls ja - welchen Alias er hat

		try {
			// ist der identifier gueltig?
			if (dbase.identifierExists(identifier)) {
				alias = dbase.getIdentifierAlias(identifier, algorithm_name);
				logger.debug("Found alias " + alias + " for identifier "
						+ identifier);
			} else {
				logger.debug("identifier " + identifier + " does not exist");
				return null;
			}
			// gibt es ein alias zu identifier?
			if (alias == null || !ks.containsAlias(alias)) {
				logger.error("Does not contain alias "
						+ dbase.getIdentifierAlias(identifier, algorithm_name));
				// // neue Alias wird angelegt
				// dbase.setIdentifierAlias(identifier, algorithm_name);
			} else {
				logger.info("Alias available: "
						+ dbase.getIdentifierAlias(identifier, algorithm_name));
			}
			logger.debug("Is secret key (should be false): "
					+ ks.entryInstanceOf(dbase.getIdentifierAlias(identifier,
							algorithm_name), KeyStore.SecretKeyEntry.class));

			// Den Schluessel, der unter dem Alias im Keystore gespeichert ist
			// abholen.

			kek = (PublicKey) ks.getCertificate(alias).getPublicKey();
			// if (kek == null) {
			// KeyManagerImpl.getInstance().setupPublicPrivateKeypair(identifier);
			// // denn zugriff nochmal versuchen
			// kek = (PublicKey) ks.getKey(dbase.getIdentifierAlias(identifier,
			// algorithm_name), (7)
			// .toCharArray());
			// }
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return kek;
	}

	// ---------------------------------
	/**
	 * Gibt einen KDK (= Private Key) für einen identifier zurück. <p>
	 * 
	 * @param identifier
	 * @param algorithm_name
	 * @return
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws SQLException
	 * @throws UnrecoverableKeyException
	 * @throws SignatureException
	 * @throws SecurityException
	 * @throws NoSuchProviderException
	 * @throws IllegalStateException
	 */
	public PrivateKey loadKeyDecryptionKey(String identifier,
			String algorithm_name) throws InvalidKeyException,
			FileNotFoundException, IOException, InvalidKeySpecException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			SQLException, UnrecoverableKeyException, SecurityException,
			SignatureException, IllegalStateException, NoSuchProviderException {
		String alias = null;
		PrivateKey kek = null;
		// In der Datenbank nachsehen, ob es für den identifier einen
		// Schlüssel
		// gibt und falls ja - welchen Alias er hat

		try {
			// ist der identifier gueltig?
			if (dbase.identifierExists(identifier)) {
				alias = dbase.getIdentifierAlias(identifier, algorithm_name);
				logger.debug("Found alias " + alias + " for identifier "
						+ identifier);
			} else {
				logger.debug("identifier " + identifier + " does not exist");
				return null;
			}
			// gibt es ein alias zu identifier?
			if (alias == null || !ks.containsAlias(alias)) {
				logger.error("Does not contain alias "
						+ dbase.getIdentifierAlias(identifier, algorithm_name));
				// neue Alias wird angelegt
				dbase.setIdentifierAlias(identifier, algorithm_name);
			} else {
				logger.info("Alias available: "
						+ dbase.getIdentifierAlias(identifier, algorithm_name));
			}
			logger.debug("Is private key (should be true): "
					+ ks.entryInstanceOf(dbase.getIdentifierAlias(identifier,
							algorithm_name), KeyStore.PrivateKeyEntry.class));

			// Den Schluessel, der unter dem Alias im Keystore gespeichert ist
			// abholen.
			kek =
				(PrivateKey) ks.getKey(alias,
						(PRIVATE_ENCRYPTION_KEY_PASS).toCharArray());
			if (kek == null) {
				kek =
					(PrivateKey) ks.getKey(dbase.getIdentifierAlias(
							identifier, algorithm_name),
							(PRIVATE_ENCRYPTION_KEY_PASS).toCharArray());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return kek;
	}

	/**
	 * Returns a data encryption key (DEK): A newly generated symmetric key that
	 * can be used in hybrid encryption schemes.
	 * 
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 * @throws SQLException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 */
	public SecretKey loadDataEncryptionKey() throws KeyStoreException,
	NoSuchAlgorithmException, CertificateException, IOException,
	UnrecoverableKeyException, SQLException, InvalidKeyException,
	InvalidKeySpecException {

		// TODO hier koennen wir erstmal jedes Mal einen neuen Schluessel
		// generieren
		// Spaeter koennte man den Schluessel dann auch cachen und nur z.B. alle
		// paar Tage erneuern...
		String jceAlgorithmName = "AES";
		KeyGenerator keyGen = KeyGenerator.getInstance(jceAlgorithmName);
		SecretKey key = keyGen.generateKey();

		return key;
	}

	public String setupPublicPrivateKeypair() throws SQLException,
	NoSuchAlgorithmException, IOException, KeyStoreException,
	CertificateException, InvalidKeyException, SecurityException,
	SignatureException, IllegalStateException, NoSuchProviderException {
		return generateCertificateWithAttributes(null);
	}

	public PrivateKey getPrivateKeyByIdentifier(String identifier) {
		PrivateKey key = null;
		try {
			String alias = dbase.getIdentifierAlias(identifier);
			key =
				(PrivateKey) ks.getKey(alias,
						PRIVATE_ENCRYPTION_KEY_PASS.toCharArray());
		} catch (Exception e) {
			logger.error(e);
		}

		return key;
	}

	/**
	 * Generate a Key encryption key (a public/private key pair) that can be
	 * used for wrapping a data encryption key (DEK).
	 */
	public String generateCertificateWithAttributes(String xmlAttributes)
	throws SQLException, NoSuchAlgorithmException, IOException,
	KeyStoreException, CertificateException, InvalidKeyException,
	SecurityException, SignatureException, IllegalStateException,
	NoSuchProviderException {
		logger.debug("Creating new certificate");
		String algorithm_name = "RSA";
		String identifier = createAndStoreKeyIdentifier();
		String alias = dbase.setIdentifierAlias(identifier, algorithm_name);

		KeyPairGenerator keyGenerator =
			KeyPairGenerator.getInstance(algorithm_name);
		keyGenerator.initialize(1024);

		logger.debug("   Generating a keypair");
		KeyPair kek = keyGenerator.generateKeyPair();

		logger.debug("   Setting up the new certificate");
		// Example is found here:
		// http://forums.sun.com/thread.jspa?threadID=5311471
		Certificate[] certificates = { certGenerator(kek, xmlAttributes) };
		// ks.setKeyEntry(alias, kek.getPrivate().getEncoded(), certificates);
		ks.setKeyEntry(alias, kek.getPrivate(),
				PRIVATE_ENCRYPTION_KEY_PASS.toCharArray(), certificates);

		File f =
			new File(CryptoManagerImpl.RESOURCEFOLDERPATH + "/"
					+ Configuration.getInstance().getKeyStoreFile());
		FileOutputStream fos = new FileOutputStream(f);
		ks.store(fos, Configuration.getInstance().getKeyStorePassword()
				.toCharArray());

		return identifier;
	}

	/**
	 * Returns the attributes stored in the certificate
	 */
	public Properties getAttributesFromCertificate(String identifier)
	throws SQLException, KeyStoreException,
	CertificateEncodingException {
		// FIXME Avoid hardcoded algorithm name here. Might crash otherwise with
		// different algorithms
		Properties map = new Properties();
		String alias = dbase.getIdentifierAlias(identifier, "RSA");

		if (alias != null) {
			X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
			X509Principal principal =
				PrincipalUtil.getIssuerX509Principal(cert);

			Vector oids = principal.getOIDs();
			Vector values = principal.getValues();

			// extract UniqueIdentifier to Map: key|value;key|value;...
			for (int i = 0; i < oids.size(); i++) {
				if (X509Principal.UNIQUE_IDENTIFIER.equals(oids.get(i))) {
					String serializedMap = (String) values.get(i);
					String[] serializedEntries = serializedMap.split(";");
					for (int a = 0; a < serializedEntries.length; a++) {
						String[] keyValue = serializedEntries[a].split("\\|");
						if (keyValue.length >= 2) {
							map.put(keyValue[0], keyValue[1]);
						} else {
							logger.warn("Unexpected format when deserializing certificate attributes: "
									+ serializedMap);
						}
					}
				} else {
					map.put(X509Name.DefaultSymbols.get(oids.get(i)),
							values.get(i));
				}
			}
		}
		return map;
	}

	/**
	 * Retrieves the public key of an identifier. <p> Currently, this method
	 * returns only RSA keys.
	 * 
	 * @throws KeyStoreException
	 */
	public byte[] getEncodedPublicKeyByIdentifier(String identifier)
	throws KeyStoreException {
		byte[] result = null;
		try {
			String alias = dbase.getIdentifierAlias(identifier);
			if (alias != null) {
				Certificate cert = (Certificate) ks.getCertificate(alias);
				if (cert != null) {
					result = cert.getEncoded();
				} else {
					logger.warn("Key of alias " + alias + " and identifier "
							+ identifier + " not found.");
				}
			} else {
				logger.warn("Alias " + alias + " not found.");
			}
		} catch (SQLException e) {
			logger.error(e);
		} catch (CertificateEncodingException e) {
			logger.error(e);
		}

		return result;
	}

	/**
	 * Retrieves the public key of an identifier. <p> Currently, this method
	 * returns only RSA keys.
	 * 
	 * @throws KeyStoreException
	 */
	public Certificate getCertificateByIdentifier(String identifier)
	throws KeyStoreException {
		Certificate result = null;
		try {
			String alias = dbase.getIdentifierAlias(identifier);
			result = (Certificate) ks.getCertificate(alias);
		} catch (SQLException e) {
			logger.error(e);
		}

		return result;
	}

	/**
	 * Stores certificate under provided identifier
	 * @return False if identifier already exists or 
	 * certificate cannot be stored
	 */
	public boolean storePublicKey(String friendlyName, String encodedCert,
			String algorithm_id) throws SQLException {
		if(!identifierExists(friendlyName)){
			// zugriff direkt auf bks ohne vorher in Datenbank nach zu schauen
			// Certificate existiert? identifier zurueck geben : ablegen und
			// identifier erzeugen
			try {
				CertificateFactory certFactory =
					CertificateFactory.getInstance("X509");
				// Certificate cert = certFactory.generateCertificate(new
				// ByteArrayInputStream(encodedCert.getBytes()));
				Certificate cert =
					certFactory.generateCertificate(new ByteArrayInputStream(
							Base64.decode(encodedCert)));
				logger.debug("Certificate uses algorithm: "
						+ cert.getPublicKey().getAlgorithm());
				
				// Alias fuer neue identifier sipeichern
				dbase.storeIdentifier(friendlyName);
				String alias =
					dbase.setIdentifierAlias(friendlyName, cert
							.getPublicKey().getAlgorithm());

				// certificate wird unter generierte Alias gespeicher
				ks.setCertificateEntry(alias, cert);
				logger.info("Stored certificate under new alias " + alias
						+ ". Can be retrieved using identifier " + friendlyName);

				return true;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return false;
			}
		}
		return false;
	}

	/**
	 * Stores a certificate in the keystore.
	 *  @return Identifier for stored certificate
	 */
	public String storePublicKey(String encodedCert, String algorithm_id) {
		// zugriff direkt auf bks ohne vorher in Datenbank nach zu schauen
		// Certificate existiert? identifier zurueck geben : ablegen und
		// identifier erzeugen
		try {
			CertificateFactory certFactory =
				CertificateFactory.getInstance("X509");
			// Certificate cert = certFactory.generateCertificate(new
			// ByteArrayInputStream(encodedCert.getBytes()));
			Certificate cert =
				certFactory.generateCertificate(new ByteArrayInputStream(
						Base64.decode(encodedCert)));
			logger.debug("Certificate uses algorithm: "
					+ cert.getPublicKey().getAlgorithm());
			String identifier = "";
			String alias = "";
			// String aliasExist = null;

			// gibt es diesen certificate
			alias = ks.getCertificateAlias(cert);

			// wenn den certificate gibt...
			if ((alias != null) && alias != ""
				&& alias.contains(cert.getPublicKey().getAlgorithm())) {
				int lenght =
					alias.length()
					- (cert.getPublicKey().getAlgorithm().length() + 1);
				identifier = alias.substring(0, lenght);

				// wird geschaut ob der Alias zu den schluessel in DB vorhanden
				// ist.
				if (dbase.getIdentifierAlias(identifier, cert.getPublicKey()
						.getAlgorithm()) != null) {
					logger.info("Identifier fuer gewaehlte certificate existiert schon!");
				} else {
					dbase.setIdentifierAlias(identifier, cert.getPublicKey()
							.getAlgorithm());
					logger.info("ein Identifier wurde zu existierende Certificat alias in DB erzeugt");
				}

				return identifier;
			} else {// wenn den certificate nicht gibt...

				// neue identifier generieren
				identifier = createAndStoreKeyIdentifier();
				// Alias fuer neue identifier sipeichern
				alias =
					dbase.setIdentifierAlias(identifier, cert
							.getPublicKey().getAlgorithm());

				// certificate wird unter generierte Alias gespeicher
				ks.setCertificateEntry(alias, cert);
				logger.info("Stored certificate under new alias " + alias
						+ ". Can be retrieved using identifier " + identifier);

				return identifier;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Creates a certificate for a public/private key pair.
	 * 
	 * @param kp
	 * @return
	 * @throws InvalidKeyException
	 * @throws SecurityException
	 * @throws SignatureException
	 * @throws CertificateEncodingException
	 * @throws IllegalStateException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InvalidPropertiesFormatException
	 */
	public Certificate certGenerator(KeyPair kp, String xmlAttributes)
	throws InvalidKeyException, SecurityException, SignatureException,
	CertificateEncodingException, IllegalStateException,
	NoSuchProviderException, NoSuchAlgorithmException,
	InvalidPropertiesFormatException, IOException {
		// http://www.bouncycastle.org/wiki/display/JA1/X.509+Public+Key+Certificate+and+Certification+Request+Generation

		// TODO Make some options for certificate generation available in config
		// file
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		certGen.setPublicKey(kp.getPublic());
		certGen.setSignatureAlgorithm("MD2withRSA");

		// Load custom attributes
		CustomCertificateAttributes certificateAttributes;
		if (xmlAttributes != null) {
			certificateAttributes =
				CustomCertificateAttributes.loadFromXML(xmlAttributes);
		} else {
			certificateAttributes =
				CustomCertificateAttributes
				.loadFromProperties(new Properties());
		}

		// Create the Issuer object and assign it to certificate
		X509Principal principal =
			new X509Principal(certificateAttributes.getAttributes());
		certGen.setIssuerDN(principal);
		long randomNumber = rand.nextLong();
		if (randomNumber < 0)
			randomNumber = -1 * randomNumber;
		certGen.setSerialNumber(BigInteger.valueOf(randomNumber));
		Calendar cal_1 = new GregorianCalendar();
		cal_1.set(2048, Calendar.MARCH, 1, 0, 0, 0);
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(cal_1.getTime());
		certGen.setSubjectDN(principal); // note: same as issuer as this is
		// self-signed

		// Finally generate the certificate
		X509Certificate cert = certGen.generate(kp.getPrivate(), "BC");

		return cert;
	}

	/**
	 * Create a new RSA key, store it in the Keystore and update the database.
	 * 
	 * @throws IOException
	 * @throws InvalidPropertiesFormatException
	 * 
	 */
	public void rsaKeygenerator(String PRIVATE_SIGNING_KEY_ALIAS)
	throws InvalidKeyException, CertificateEncodingException,
	SecurityException, SignatureException, IllegalStateException,
	NoSuchProviderException, NoSuchAlgorithmException,
	KeyStoreException, SQLException, InvalidPropertiesFormatException,
	IOException {
		// Create new DSA key pair, store it into keystore and create a
		// reference to it in the database
		//RSAKeyPairGenerator rsakpg = new RSAKeyPairGenerator();
        // TODO swiching from properitary sun implementation to java standard of creating RSA keys
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
		KeyPair dsakp = kpg.generateKeyPair();
		java.security.cert.Certificate[] certificates =
		{ new KeyManagerImpl().certGenerator(dsakp, null) };
		ks.setKeyEntry(PRIVATE_SIGNING_KEY_ALIAS, dsakp.getPrivate()
				.getEncoded(), certificates);

		if (dbase.identifierExists("signing") == false) {
			dbase.storeIdentifier("signing");
			dbase.setIdentifierAlias("signing", "dsa");
		}

	}

	/**
	 * Creates a symmetric key based on provided string and stores it in the
	 * keystore under provided friendly name. The provided password
	 * should be as long as possible for more security.
	 * 
	 * This method can be used to generate the same key
	 * for two parties based on a password.
	 * @throws KeyStoreException 
	 */
	@Override
	public boolean generateKeyFromPassword(String friendlyName, String password,
			int keyLength, String algo) throws SQLException, KeyStoreException {
		try{
			if(!dbase.identifierExists(friendlyName)){
				//set keylength from bit to byte number
				keyLength = (int)((double)keyLength / 8.0);
				//create byte[] from provided password
				
				//create one-way hash function
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				byte[] digest = null;
				byte[] passBytes = Base64.decode(password);

                Random r = new SecureRandom();
                byte[] saltBytes = new byte[32];
                r.nextBytes(saltBytes);

				//concat password bytes with salt bytes
				byte[] iterationValue = new byte[passBytes.length + saltBytes.length];
				for (int i=0; i < passBytes.length; i++) {
					iterationValue[i] = passBytes[i];
				}
				for (int i=0; i < saltBytes.length; i++) {
					iterationValue[passBytes.length + i] = saltBytes[i];
				}
				//iterate hash function 1000 times to generate proper key from password
				for (int i=0; i < PASSWORD_DERIVATION_ITERATION; i++) {
					md.update(iterationValue);
					iterationValue = md.digest();
				}
				
				//add input to digest and concatenate output at end of digest until it reaches key size
				do{
					md.update(iterationValue);
					byte[] temp = md.digest();
					if(digest != null){
						//concat previous digests with new one
						digest = Arrays.copyOf(digest, digest.length + temp.length);
						for(int i=0; i < temp.length; i++){
							digest[digest.length - temp.length + i] = temp[i];
						}
					}else{
						//put created hash into digest
						digest = temp;
					}
					
					//if new array is larger than intended keylength cut end
					if(digest.length > keyLength){
						digest = Arrays.copyOf(digest, keyLength);
					}
					//create value for next iteration
					for(int i=0; i< iterationValue.length; i++){
						iterationValue[i] = (byte)(temp[i%temp.length]^iterationValue[i]);
					}
				}while(digest.length < keyLength);
				//create key from byte array
				SecretKey symKey = new SecretKeySpec(digest, algo);
				String algorithm = symKey.getAlgorithm();
				logger.debug("Algorithm of symmetric key is " + algorithm);

				//store identifier
				dbase.storeIdentifier(friendlyName);
				// Alias fuer neue identifier sipeichern
				String alias = dbase.setIdentifierAlias(friendlyName, algorithm);
				logger.debug("new alias is " + alias);

				// certificate wird unter generierte Alias gespeichert
				ks.setKeyEntry(alias, symKey, Configuration.getInstance()
						.getKeyStorePassword().toCharArray(), null);
				logger.debug("Stored secret key under identifier " + friendlyName);
				return true;
			}
		}catch(NoSuchAlgorithmException e){
			logger.debug("SHA-256 algorithm is not available in your cryptographic extension!",e);
		}
		return false;
	}

	/**
	 * Creates a symmetric key based on provided string and stores it in the
	 * keystore under returned identifier. The provided password
	 * should be as long as possible for more security.
	 * 
	 * This method can be used to generate the same key
	 * for two parties based on a password.
	 */
	@Override
	public String generateKeyFromPassword(String password, int keyLength, String algo) {
		String identifier = createKeyIdentifier();
		try{
			if(!generateKeyFromPassword(identifier, password, keyLength, algo))
				return null;
		}catch(Exception e){
			return null;
		}
		return identifier;
	}

	@Override
	public String generateSymmetricKey(int keySize, String algo)
	throws NoSuchAlgorithmException, SQLException {
		String identifier = createKeyIdentifier();
		if(generateSymmetricKey(identifier, keySize, algo)){
			return identifier;
		}else{
			return null;
		}
	}

	/**
	 * Generate a Key encryption key (a public/private key pair) that can be
	 * used for wrapping a data encryption key (DEK).
	 */
	public String generateSymmetricKey() throws SQLException,
	NoSuchAlgorithmException, IOException, KeyStoreException,
	CertificateException, InvalidKeyException, SecurityException,
	SignatureException, IllegalStateException, NoSuchProviderException {

		// TODO fest eingestellter Algorithmus sollte geändert werden. Mapping
		// von BC nach XMLSec benötigt.
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		// TODO festeingestellte Schlüssellänge (128) wird nicht mit allen
		// Algorithmen funktioniere.
		keyGenerator.init(128);
		SecretKey kek = keyGenerator.generateKey();

		String identifier =
			storeSymmetricKey("AES",
					new String(Base64.encode(kek.getEncoded())));
		return identifier;
	}


	@Override
	public boolean generateSymmetricKey(String friendlyName, int keysize, String algo) throws NoSuchAlgorithmException, SQLException {
		if(!identifierExists(friendlyName)){
			KeyGenerator keyGenerator = KeyGenerator.getInstance(algo);
			keyGenerator.init(keysize);
			SecretKey kek = keyGenerator.generateKey();
			storeSymmetricKey(friendlyName,
					algo,
					new String(Base64.encode(kek.getEncoded())));
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean storeSymmetricKey(String friendlyName, String algo, String key) throws SQLException {
		if(!identifierExists(friendlyName)){
			String alias = "";
			SecretKey symKey = new SecretKeySpec(Base64.decode(key), algo);

			try {
				String algorithm = symKey.getAlgorithm();
				logger.debug("Algorithm of symmetric key is " + algorithm);

				dbase.storeIdentifier(friendlyName);
				// Alias fuer neue identifier sipeichern
				alias = dbase.setIdentifierAlias(friendlyName, algorithm);
				logger.debug("new alias is " + alias);

				// certificate wird unter generierte Alias gespeichert
				ks.setKeyEntry(alias, symKey, Configuration.getInstance()
						.getKeyStorePassword().toCharArray(), null);
				logger.debug("Stored secret key under identifier " + friendlyName);
			} catch (KeyStoreException e) {
				logger.error(e.getMessage(), e);
				return false;
			}
			return true;	
		}
		return false;
	}

	/**
	 * Stores a secret key in the keystore.
	 */
	public String storeSymmetricKey(String algo, String key) {
		String identifier = "";
		String alias = "";
		SecretKey symKey = new SecretKeySpec(Base64.decode(key), algo);

		try {
			// neuen identifier generieren
			identifier = createAndStoreKeyIdentifier();
			logger.debug("Generated new Identifier " + identifier);

			String algorithm = symKey.getAlgorithm();
			logger.debug("Algorithm of symmetric key is " + algorithm);

			// Alias fuer neue identifier sipeichern
			alias = dbase.setIdentifierAlias(identifier, algorithm);
			logger.debug("new alias is " + alias);

			// certificate wird unter generierte Alias gespeichert
			ks.setKeyEntry(alias, symKey, Configuration.getInstance()
					.getKeyStorePassword().toCharArray(), null);
			logger.debug("Stored secret key under identifier " + identifier);
		} catch (KeyStoreException e) {
			logger.error(e.getMessage(), e);
		}

		return identifier;
	}

	/**
	 * Gibt einen symmetrischen geheimen Schlüssel für einen identifier zurück.
	 * <p> Falls kein Schlüssel existiert, wird eine Exception zurückgegeben
	 * 
	 * @param identifier Identifier of key to use
	 * @param algorithm_name Algorithm name according to JCE
	 * @throws KeyStoreException If given identifier with algorithm is not found in keystore or cannot be loaded
	 * @throws NoSuchAlgorithmException If provided algorithm is not known
	 */
	public SecretKey loadSymmetricKey(String identifier, String algorithm_name) 
	throws KeyStoreException, NoSuchAlgorithmException{

		String alias = null;
		SecretKey secretKey = null;

		//does identifier exist
		try {
			if (dbase.identifierExists(identifier)) {
				alias = dbase.getIdentifierAlias(identifier.trim(), algorithm_name);
				logger.debug("Found alias " + alias + " for identifier "
						+ identifier + " and algorithm " + algorithm_name);
			} else {
				logger.debug("identifier " + identifier + " does not exist");
				return null;
			}
		} catch (SQLException e) {
			logger.error("Error using CryptoManager database",e);
		}

		if (alias == null || alias == "") {
			throw new KeyStoreException("No alias for " + identifier + ", "
					+ algorithm_name);
		} else {
			//load key from keystore
			try {
				secretKey =
					(SecretKey) ks.getKey(alias, Configuration.getInstance()
							.getKeyStorePassword().toCharArray());
			} catch (UnrecoverableKeyException e) {
				logger.error("Error in keystore when accessing key: " + alias);
				KeyStoreException ke = new KeyStoreException("Error recovering key");
				ke.initCause(e);
				throw ke;
			}
		}
		return secretKey;
	}



	public boolean addCertificateForService(String virtualAddress, String certRef) {
		try {
			if (dbase.identifierExists(certRef)) {
				if (certificateRefCache.containsKey(virtualAddress)
						&& (getCertificateFromCache(virtualAddress) != certRef)) {
					logger.warn("THIS WILL PROBABLY CAUSE PROBLEMS. An existing certificate reference for VirtualAddress "
							+ virtualAddress
							+ " is requested to be overriden by a different reference. This is only okay if both virtual addresses belong to the same NM.");
				}
				putCertificateToCache(virtualAddress, certRef);
				return true;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * Returns the certificate (i.e. public key) reference for an VirtualAddress or null if
	 * no certificate exists for that VirtualAddress.
	 * 
	 * @param receiverVirtualAddress
	 * @return
	 */
	public String getCertRefByVirtualAddress(String receiverVirtualAddress) {
		return getCertificateFromCache(receiverVirtualAddress);
	}

	/**
	 * Returns the private key reference for an VirtualAddress or null if for that VirtualAddress no
	 * private key exists.
	 * 
	 * @param virtualAddress
	 * @return
	 */
	public String getPrivateKeyRefByVirtualAddress(String virtualAddress) {
		String ref = getPrivateKeyCache(virtualAddress);
		if (ref == null) {
			logger.warn(" Request for non-existent key for virtualAddress " + virtualAddress);
			// Just for debugging
			// for (String key : privateKeyRefCache.keySet()) {
			// logger.trace("  " + key + " -> " + privateKeyRefCache.get(key));
			// }
		}
		return ref;
	}

	public boolean addPrivateKeyForService(String virtualAddress, String privateKeyRef) {
		try {
			if (dbase.identifierExists(privateKeyRef)) {
				if (privateKeyRefCache.containsKey(virtualAddress)
						&& (getPrivateKeyCache(virtualAddress) != privateKeyRef)) {
					logger.warn("THIS WILL PROBABLY CAUSE PROBLEMS. An existing private key reference for VirtualAddress "
							+ virtualAddress
							+ " is requested to be overriden by a different reference");
					new Throwable().printStackTrace();
				}
				logger.debug("Storing private reference for VirtualAddress " + virtualAddress
						+ "  : " + privateKeyRef + " for VirtualAddress " + virtualAddress);
				putPrivateKeyToCache(virtualAddress, privateKeyRef);
				return true;
			} else {
				logger.error("Did not add private key reference "
						+ privateKeyRef
						+ " for VirtualAddress "
						+ virtualAddress
						+ " as there is no key for that reference in the database.");
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return false;

	}


	/**
	 * Puts certificate identifier into cache and creates counter for caching refreshing
	 * @param key Alias for the certificate
	 * @param value Identifier for the certificate
	 */
	private void putCertificateToCache(String key, String value){
		certificateRefCache.put(key, value);
		certificateCacheUse.put(key, new Integer(0));
	}

	/**
	 * Puts private key identifier into cache and creates counter for caching refreshing
	 * @param key Alias for the private key
	 * @param value Identifier for the private key
	 */
	private void putPrivateKeyToCache(String key, String value){
		this.privateKeyRefCache.put(key, value);
		this.privateKeyCacheUse.put(key, new Integer(0));
	}

	/**
	 * Returns certificate identifier for alias and increases counter for caching
	 * @param key Alias for certificate
	 * @return
	 */
	private String getCertificateFromCache(String key){
		if(certificateCacheUse.containsKey(key)){
		//increase usage counter by 1
		this.certificateCacheUse.put(key, this.certificateCacheUse.get(key) + 1);
		return this.certificateRefCache.get(key);
		}else{
			return null;
		}
	}

	/**
	 * Returns private key identifier for alias and increases counter for caching
	 * @param key Alias for private key
	 * @return
	 */
	private String getPrivateKeyCache(String key){
		if(privateKeyCacheUse.containsKey(key)){
		//increase usage counter by 1
		this.privateKeyCacheUse.put(key, this.privateKeyCacheUse.get(key) + 1);
		return this.privateKeyRefCache.get(key);
		}else{
			return null;
		}
	}

	/**
	 * Clears the certificate and privatekey reference cache
	 * by usage number and leaves 10 most used
	 * @author Vinkovits
	 *
	 */
	private class CacheClearer extends TimerTask{

		//creates descending oder by value
		private class EntryComparator implements Comparator<Map.Entry<String, Integer>>{

			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				if(o1.getValue() > o2.getValue()){
					return -1;
				}
				if(o1.getValue() < o2.getValue()){
					return 1;
				}
				return 0;
			}
		}

		@Override
		public void run() {
			//check if caches are too large
			if(privateKeyRefCache.size() > CACHE_SIZE){
				//create list of entries
				ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>();
				for(Map.Entry<String, Integer> e : privateKeyCacheUse.entrySet())
				{
					entries.add(e);
				}
				//sort entries according to values
				Collections.sort(entries, new EntryComparator());
				//clear except 10 largest
				for(int i=CACHE_SIZE; i<entries.size();i++){
					privateKeyRefCache.remove(entries.get(i).getKey());
					privateKeyCacheUse.remove(entries.get(i).getKey());
				}
				//reset rest
				for(int i=0; i<CACHE_SIZE;i++){
					privateKeyCacheUse.put(entries.get(i).getKey(), 0);
				}
			}
			if(certificateRefCache.size() > CACHE_SIZE){
				//create list of entries
				ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>();
				for(Map.Entry<String, Integer> e : certificateCacheUse.entrySet())
				{
					entries.add(e);
				}
				//sort entries according to values
				Collections.sort(entries, new EntryComparator());
				//clear except 10 largest
				for(int i=CACHE_SIZE; i<entries.size();i++){
					certificateRefCache.remove(entries.get(i).getKey());
					certificateCacheUse.remove(entries.get(i).getKey());
				}
				//reset rest
				for(int i=0; i<CACHE_SIZE;i++){
					certificateCacheUse.put(entries.get(i).getKey(), 0);
				}
			}
		}	
	}
	
	private static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
