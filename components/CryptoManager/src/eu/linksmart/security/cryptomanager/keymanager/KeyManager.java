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

package eu.linksmart.security.cryptomanager.keymanager;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

import javax.crypto.SecretKey;

/**
 * Interface for key management.
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 * 
 */
public interface KeyManager {

	/**
	 * Creates a Key Encryption Key (KEK) and stores it for the specified
	 * identifier. <p> A KEK is a self-signed certificate, i.e. a pair of public
	 * and private key.
	 * 
	 * @param identifier
	 * @param algorithm_name
	 * @throws SQLException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws InvalidKeyException
	 * @throws SecurityException
	 * @throws SignatureException
	 * @throws IllegalStateException
	 * @throws NoSuchProviderException
	 */
	abstract String setupPublicPrivateKeypair() throws SQLException,
	NoSuchAlgorithmException, IOException, KeyStoreException,
	CertificateException, InvalidKeyException, SecurityException,
	SignatureException, IllegalStateException, NoSuchProviderException;

	/**
	 * Generates a certificate with provided attributes
	 * @param xmlAttributes Attributes to include into certificate
	 * @return Identifier of certificate
	 * @throws SQLException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws InvalidKeyException
	 * @throws SecurityException
	 * @throws SignatureException
	 * @throws IllegalStateException
	 * @throws NoSuchProviderException
	 */
	abstract String generateCertificateWithAttributes(
			String xmlAttributes) throws SQLException,
			NoSuchAlgorithmException, IOException, KeyStoreException,
			CertificateException, InvalidKeyException, SecurityException,
			SignatureException, IllegalStateException, NoSuchProviderException;

	/**
	 * Generates a Key Encryption Key (KEK) and returns it <p> A KEK is a
	 * self-signed certificate, i.e. a pair of  public and private key.
	 * 
	 * @param identifier
	 * @param algorithm_name
	 * @throws SQLException
	 * @throws NoSuchAlgorithmException
	 */
	abstract String generateSymmetricKey(int keySize, String algo) throws NoSuchAlgorithmException, SQLException;

	/**
	 * Creates a unique and random identifier and stores it. <p> This identifier
	 * references all symmetric cryptographic keys of an entity (so, there are n
	 * keys belonging to one identifier). Given the identifier, the
	 * CryptoManager chooses automatically the right key that belongs to the
	 * cryptographic algorithm that should be used.
	 * 
	 * @return a unique and random identifier.
	 */
	abstract String createAndStoreKeyIdentifier();

	/**
	 * Returns all  public keys that belong to the <code>identifier</code>. <p>
	 * 
	 * @param identifier
	 * @return
	 * @throws KeyStoreException
	 */
	abstract byte[] getEncodedPublicKeyByIdentifier(String identifier)	throws KeyStoreException;

	/**
	 * Stores a public key in the keystore and returns the identifier for this
	 * key. <p>
	 * 
	 * @param encodedCert
	 *            the DER-encoded certificate, either in binary or in printable
	 *            (BASE64) format.
	 * @param algorithm_id
	 * @return
	 */
	abstract String storePublicKey(String encodedCert,	String algorithm_id);

	/**
	 * Stores a secret key in the keystore and returns the identifier for this
	 * key
	 * 
	 * @param key
	 *            The key in the format as returned by
	 *            <code>generateSymmetricKey</code>.
	 * @return
	 * @throws SQLException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws InvalidKeyException
	 * @throws SecurityException
	 * @throws SignatureException
	 * @throws IllegalStateException
	 * @throws NoSuchProviderException
	 */
	abstract String storeSymmetricKey(String algo, String key)
	throws SQLException, NoSuchAlgorithmException, IOException,
	KeyStoreException, CertificateException, InvalidKeyException,
	SecurityException, SignatureException, IllegalStateException,
	NoSuchProviderException;

	/**
	 * 
	 * @param identifier
	 * @return
	 * @throws SQLException
	 * @throws KeyStoreException
	 * @throws CertificateEncodingException
	 */
	abstract Properties getAttributesFromCertificate(String identifier)
	throws SQLException, KeyStoreException,
	CertificateEncodingException;

	/**
	 * Returns the private key stored under provided identifier
	 * @param identifier
	 * @return 
	 */
	abstract PrivateKey getPrivateKeyByIdentifier(String identifier);

	/**
	 * Returns the certificate stored under provided identifier
	 * @param identifier
	 * @return
	 * @throws KeyStoreException
	 */
	abstract Certificate getCertificateByIdentifier(String identifier)
	throws KeyStoreException;

	/**
	 * Associates an HID with a stored private key
	 * @param hid HID to be associated to
	 * @param certRef Identifier used in store for private key
	 * @return
	 */
	abstract boolean addPrivateKeyForHID(String hid, String certRef);

	/**
	 * Associates an HID with a stored certificate
	 * @param hid HID to be associated to
	 * @param certRef Identifier used in store for certificate
	 * @return
	 */
	abstract boolean addCertificateForHID(String hid, String certRef);

	/**
	 * Returns the certificate reference for an HID or null if no certificate
	 * exists for that HID.
	 * 
	 * @param receiverHID
	 * @return
	 */
	abstract String getCertRefByHID(String receiverHID);

	/**
	 * Returns the identifier of the private key for this HID
	 * @param hid
	 * @return
	 */
	abstract String getPrivateKeyRefByHID(String hid);

	/**
	 * Removes an entry from local store
	 * @param identifier
	 * @return
	 */
	abstract boolean deleteEntry(String identifier);

	abstract String[] getIdentifier();

	abstract Vector<Vector<String>> getIdentifierInfo();

	abstract void close();

	/**
	 * Generates a symmetric key for given identifier
	 * @param friendlyName Identifier to be stored under
	 * @param keySize
	 * @param algo Algorithm name according to JCE
	 * @return True if key could be generated for this identifier
	 * @throws NoSuchAlgorithmException
	 * @throws SQLException
	 */
	abstract boolean generateSymmetricKey(String friendlyName, int keySize, String algo) 
	throws NoSuchAlgorithmException, SQLException;

	/**
	 * Stores a key under provided identifier
	 * @param friendlyName
	 * @param algo
	 * @param key Base64 encoded key
	 * @return True if key could be stored
	 * @throws SQLException
	 */
	abstract boolean storeSymmetricKey(String friendlyName, String algo,
			String key) throws SQLException;

	/**
	 * Stores an encoded certificate under provided identifier
	 * @param friendlyName
	 * @param encodedCert
	 * @param algorithm_id
	 * @return
	 * @throws SQLException
	 */
	abstract boolean storePublicKey(String friendlyName,
			String encodedCert, String algorithm_id) throws SQLException;

	/**
	 * Generates a symmetric key from provided password. Two calls with the
	 * same password always create the same key
	 * @param friendlyName
	 * @param password
	 * @param keySize
	 * @param algo Algorithm name according to JCE
	 * @return
	 * @throws SQLException
	 * @throws KeyStoreException
	 */
	abstract boolean generateKeyFromPassword(
			String friendlyName, String password, int keySize, String algo) throws SQLException, KeyStoreException;

	/**
	 * Generates a symmetric key from provided password. Two calls with the
	 * same password always create the same key
	 * @param password
	 * @param keySize
	 * @param algo Algorithm name according to JCE
	 * @return
	 */
	abstract String generateKeyFromPassword(
			String password, int keySize, String algo);

	/**
	 * Returns the stored symmetric key
	 * @param identifier Identifier of stored key
	 * @param algorithm_name Algorithm name according to JCE
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	abstract SecretKey loadSymmetricKey(String identifier, String algorithm_name) 
	throws NoSuchAlgorithmException,KeyStoreException;

	/**
	 * Tells whether the provided identifier exists
	 * @param identfier
	 * @return
	 */
	abstract boolean identifierExists(String identifier) throws SQLException;
}