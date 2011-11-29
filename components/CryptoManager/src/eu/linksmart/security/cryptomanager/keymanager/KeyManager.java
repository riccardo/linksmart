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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
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
	public abstract String setupPublicPrivateKeypair() throws SQLException,
			NoSuchAlgorithmException, IOException, KeyStoreException,
			CertificateException, InvalidKeyException, SecurityException,
			SignatureException, IllegalStateException, NoSuchProviderException;

	public abstract String generateCertificateWithAttributes(
			String xmlAttributes) throws SQLException,
			NoSuchAlgorithmException, IOException, KeyStoreException,
			CertificateException, InvalidKeyException, SecurityException,
			SignatureException, IllegalStateException, NoSuchProviderException;

	/**
	 * Generates a Key Encryption Key (KEK) and returns it <p> A KEK is a
	 * self-signed certificate, i.e. a pair of public and private key.
	 * 
	 * @param identifier
	 * @param algorithm_name
	 * @throws SQLException
	 * @throws NoSuchAlgorithmException
	 */
	public abstract String generateSymmetricKey(int keySize, String algo) throws NoSuchAlgorithmException, SQLException;

	/**
	 * Creates a unique and random identifier and stores it. <p> This identifier
	 * references all symmetric cryptographic keys of an entity (so, there are n
	 * keys belonging to one identifier). Given the identifier, the
	 * CryptoManager chooses automatically the right key that belongs to the
	 * cryptographic algorithm that should be used.
	 * 
	 * @return a unique and random identifier.
	 */
	public abstract String createAndStoreKeyIdentifier();

	/**
	 * Returns all public keys that belong to the <code>identifier</code>. <p>
	 * 
	 * @param identifier
	 * @return
	 * @throws KeyStoreException
	 */
	public abstract byte[] getEncodedPublicKeyByIdentifier(String identifier)
			throws KeyStoreException;

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
	public abstract String storePublicKey(String encodedCert,
			String algorithm_id);

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
	public abstract String storeSymmetricKey(String algo, String key)
			throws SQLException, NoSuchAlgorithmException, IOException,
			KeyStoreException, CertificateException, InvalidKeyException,
			SecurityException, SignatureException, IllegalStateException,
			NoSuchProviderException;

	public abstract Properties getAttributesFromCertificate(String identifier)
			throws SQLException, KeyStoreException,
			CertificateEncodingException;

	public abstract PrivateKey getPrivateKeyByIdentifier(String identifier);

	public abstract Certificate getCertificateByIdentifier(String identifier)
			throws KeyStoreException;

	public abstract boolean addPrivateKeyForHID(String hid, String certRef);

	public abstract boolean addCertificateForHID(String hid, String certRef);

	/**
	 * Returns the certificate reference for an HID or null if no certificate
	 * exists for that HID.
	 * 
	 * @param receiverHID
	 * @return
	 */
	public abstract String getCertRefByHID(String receiverHID);

	public abstract String getPrivateKeyRefByHID(String hid);

	public abstract boolean deleteEntry(String identifier);

	public abstract String[] getIdentifier();

	public abstract Vector<Vector<String>> getIdentifierInfo();

	public abstract void close();

	public abstract boolean generateSymmetricKey(String friendlyName, int keySize, String algo) 
	throws NoSuchAlgorithmException, SQLException;

	public abstract boolean storeSymmetricKey(String friendlyName, String algo,
			String key) throws SQLException;

	public abstract boolean storePublicKey(String friendlyName,
			String encodedCert, String algorithm_id) throws SQLException;

	public abstract boolean generateKeyFromPassword(
			String friendlyName, String password, int keySize, String algo) throws SQLException, KeyStoreException;
	
	public abstract String generateKeyFromPassword(
			String password, int keySize, String algo);
	
	public SecretKey loadSymmetricKey(String identifier, String algorithm_name) 
	throws NoSuchAlgorithmException,KeyStoreException;
}