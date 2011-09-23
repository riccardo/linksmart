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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import sun.security.rsa.RSAKeyPairGenerator;

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
	private static final String PRIVATE_ENCRYPTION_KEY_PASS = "hydrademo";
	private KeyStore ks;
	private DBmanagement dbase;
	private Random rand = new Random();
	private HashMap<String, String> certificateRefCache =
			new HashMap<String, String>();
	private HashMap<String, String> privateKeyRefCache =
			new HashMap<String, String>();

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
					new File(CryptoManagerImpl.CONFIGFOLDERPATH + "/"
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
		} catch (SQLException e) {
			logger.error("Error while initialising KeyManager", e);
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
		String identifier = createKeyIdentifier();
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
				new File(CryptoManagerImpl.CONFIGFOLDERPATH + "/"
						+ Configuration.getInstance().getKeyStoreFile());
		FileOutputStream fos = new FileOutputStream(f);
		ks.store(fos, Configuration.getInstance().getKeyStorePassword()
				.toCharArray());

		return identifier;
	}

	/**
	 * Generate a Key encryption key (a public/private key pair) that can be
	 * used for wrapping a data encryption key (DEK).
	 * 
	 * @throws SQLException
	 * @throws KeyStoreException
	 * @throws CertificateEncodingException
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
	 * Create a new DSA key, store it in the Keystore and update the database.
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
		RSAKeyPairGenerator rsakpg = new RSAKeyPairGenerator();
		KeyPair dsakp = rsakpg.generateKeyPair();
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
	public String createKeyIdentifier() {
		// Identifiers in Datenbank geschrieben.
		String identifier = java.util.UUID.randomUUID().toString();
		dbase.storeIdentifier(identifier);
		return identifier;
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
	 * Stores a certificate in the keystore.
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
					logger.info("ein neuen Alias wurde zu existierende Certificat in DB erzeugt");
				}

				return identifier;
			} else {// wenn den certificate nicht gibt...

				// neue identifier generieren
				identifier = KeyManagerImpl.getInstance().createKeyIdentifier();
				// Alias fuer neue identifier sipeichern
				alias =
						dbase.setIdentifierAlias(identifier, cert
								.getPublicKey().getAlgorithm());

				// neu gespeicherte Alias wird gelesen
				// alias = dbase.getIdentifierAlias(identifier,
				// cert.getPublicKey().getAlgorithm());
				// certificate wird unter generierte Alias gespeicher
				ks.setCertificateEntry(alias, cert);
				logger.info("Stored certificate under new alias " + alias
						+ ". Can be retrieved using identifier " + identifier);

				// FIXME remove the following line. Just for testing.
				// getAttributesFromCertificate(identifier);
				return identifier;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
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
			identifier = KeyManagerImpl.getInstance().createKeyIdentifier();
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

	// ---------------------------------
	/**
	 * Gibt einen symmetrischen geheimen Schlüssel für einen identifier zurück.
	 * <p> Falls kein Schlüssel existiert, wird eine Exception zurückgegeben
	 * 
	 * @param identifier
	 * @param algorithm_name
	 */
	public SecretKey loadSymmetricKey(String identifier, String algorithm_name)
			throws InvalidKeyException, FileNotFoundException, IOException,
			InvalidKeySpecException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, SQLException,
			UnrecoverableKeyException, SecurityException, SignatureException,
			IllegalStateException, NoSuchProviderException {

		String alias = null;
		SecretKey secretKey = null;

		// ist der identifier gueltig?
		if (dbase.identifierExists(identifier)) {
			alias = dbase.getIdentifierAlias(identifier.trim(), algorithm_name);
			logger.debug("Found alias " + alias + " for identifier "
					+ identifier + " and algorithm " + algorithm_name);
		} else {
			logger.debug("identifier " + identifier + " does not exist");
			return null;
		}

		if (alias == null || alias == "") {
			throw new KeyStoreException("No alias for " + identifier + ", "
					+ algorithm_name);
		} else {
			// Den Schluessel, der unter dem Alias im Keystore gespeichert ist
			// abholen.
			secretKey =
					(SecretKey) ks.getKey(alias, Configuration.getInstance()
							.getKeyStorePassword().toCharArray());
		}
		return secretKey;
	}

	// ---------------------------------
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

	public boolean addCertificateForHID(String hid, String certRef) {
		try {
			if (dbase.identifierExists(certRef)) {
				if (certificateRefCache.containsKey(hid)
						&& (certificateRefCache.get(hid) != certRef)) {
					logger.warn("THIS WILL PROBABLY CAUSE PROBLEMS. An existing certificate reference for HID "
							+ hid
							+ " is requested to be overriden by a different reference. This is only okay if both HIDs belong to the same NM.");
				}
				certificateRefCache.put(hid, certRef);
				return true;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * Returns the certificate (i.e. public key) reference for an HID or null if
	 * no certificate exists for that HID.
	 * 
	 * @param receiverHID
	 * @return
	 */
	public String getCertRefByHID(String receiverHID) {
		return certificateRefCache.get(receiverHID);
	}

	/**
	 * Returns the private key reference for an HID or null if for that HID no
	 * private key exists.
	 * 
	 * @param hid
	 * @return
	 */
	public String getPrivateKeyRefByHID(String hid) {
		String ref = privateKeyRefCache.get(hid);
		if (ref == null) {
			logger.warn(" Request for non-existent key for hid " + hid);
			// Just for debugging
			// for (String key : privateKeyRefCache.keySet()) {
			// logger.trace("  " + key + " -> " + privateKeyRefCache.get(key));
			// }
		}
		return ref;
	}

	public boolean addPrivateKeyForHID(String hid, String privateKeyRef) {
		try {
			if (dbase.identifierExists(privateKeyRef)) {
				if (privateKeyRefCache.containsKey(hid)
						&& (privateKeyRefCache.get(hid) != privateKeyRef)) {
					logger.warn("THIS WILL PROBABLY CAUSE PROBLEMS. An existing private key reference for HID "
							+ hid
							+ " is requested to be overriden by a different reference");
					new Throwable().printStackTrace();
				}
				logger.debug("Storing private reference for HID " + hid
						+ "  : " + privateKeyRef + " for HID " + hid);
				privateKeyRefCache.put(hid, privateKeyRef);
				return true;
			} else {
				logger.error("Did not add private key reference "
						+ privateKeyRef
						+ " for HID "
						+ hid
						+ " as there is no key for that reference in the database.");
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return false;

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
				}

				if (certificateRefCache.containsValue(identifier)) {
					logger.debug("identifier is in certificateRefCache");
					logger.debug("deletion successful: "
							+ certificateRefCache.values().remove(identifier));
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

			} else {
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

}
