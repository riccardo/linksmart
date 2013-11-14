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
package eu.linksmart.security.cryptomanager;

/**
 * Interface for Crypto Management in LinkSmart.
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 * @see {@link KeyManager}, {@link CryptoMessageFormatProcessor}
 */

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

public interface CryptoManager {

	/**
	 * Decrypts a LinkSmart message that has been created using the <code>encryptAsymmetric</code> or <code>encryptSymmetric</code> method.
	 * <p>
	 * All relevant information for decrypting the message should be included in the message itself. 
	 * @return In the case of an error, this method
	 * returns null. Else it returns the decrypted data.
	 */
	public String decrypt(String encryptedData);

	/**
	 * Creates a signed LinkSmart message with the default local key.
	 * 
	 * @param data to be signed
	 * @param format
	 *            The message format to be used. Currently, only <code>XMLEnc</code> is supported.
	 * @return Data signed with added meta-data or null if signing failed.
	 */
	public String sign(String data, String format);

	/**
	 * Creates a signed LinkSmart message for a service identified with a VirtualAddress.
	 * 
	 * @param data to be signed
	 * @param format
	 *            The message format to be used. Currently, only <code>XMLEnc</code> is supported.
	 * @param identifier is the VirtualAddress of the service as string
	 * @return Data signed with added meta-data or null if signing failed
	 */
	public String sign(String data, String format, String identifier);
	
	/**
	 * Verifies a signed message. The string has to contain all relevant meta-data
	 * e.g. as produced by the sign method.
	 * @return The original data with meta-data removed or null if signature was invalid. 
	 */
	public String verify(String data);
	
	/**
	 * Encrypts a LinkSmart message so it can be opened by the receiver using the <code>decrypt</code> method.
	 * @param documentString String to be encrypted
	 * @param identifier VirtualAddress of the receiver end
	 * @param format is ignored but must not be null
	 * @return enrypted data with added meta-data
	 * @throws Exception different kinds of exceptions depending on the specific problem
	 */
	public String encryptAsymmetric(String documentString, String identifier, String format) throws Exception;

	/**
	 * Stores a public key in the keystore with random identifier.
	 * 
	 * @param encodedCert
	 *            The certificate containing the public key.
	 * @param algorithm_id Not used at the moment as only X509 is supported
	 */
	public String storePublicKey(String encodedCert, String algorithm_id);
	
	/**
	 * Stores a public key in the keystore with the provided identifier.
	 * @param friendlyName Chosen identifier of key
	 * @param encodedCert The certificate containing the public key.
	 * @param algorithmId Not usedat the moment as only X509 is supported.
	 * @return False if identifier is taken or true if storing succeeded
	 * @throws SQLException When an exception using the underlying database occured
	 */
	public boolean storePublicKeyWithFriendlyName(String friendlyName, String encodedCert, String algorithmId) throws SQLException;
		
	/**
	 * Returns the public key that is stored under a certain identifier.
	 * 
	 * @param identifier
	 *            The identifier of the requested public key
	 * @return The public key as a byte array or null if key could not be retrieved using the identifier
	 * @throws KeyStoreException
	 *             If an exception while accessing the key store occurred.
	 */
	public byte[] getEncodedPublicKeyByIdentifier(String identifier) throws KeyStoreException;

	/**
	 * Returns a public key certificate stored under given identifier.
	 * @param identifier of the requested certificate
	 * @return Certificate object or null if certificate is not found
	 * @throws KeyStoreException
	 */
	public Certificate getCertificateByIdentifier(String identifier) throws KeyStoreException;

	/**
	 * Generates a new public/private key pair (i.e. a certificate and the corresponding private key) and writes attributes into the
	 * certificate. The certificate is then assigned to the given VirtualAddress so it can be used for encrypting messages from this VirtualAddress.
	 * This is the same as calling<br>
	 * <code> 
	 * String certRef = cryptoManager.generateCertificateWithAttributes(xmlAttributes);
	 * cryptoManager.setCertificateReference(certRef, virtualAddress);
	 * </code>
	 * 
	 * @param xmlAttributes An xml containing the attributes to be put into the certificate
	 * @param virtualAddress The VAD to be put into the certificate
	 * 
	 * @return Returns an identifier for this key pair.
	 */
	public String generateCertificateWithAttributes(String xmlAttributes, String virtualAddress) throws SQLException, NoSuchAlgorithmException, IOException,
			KeyStoreException, CertificateException, InvalidKeyException, SecurityException, SignatureException, IllegalStateException,
			NoSuchProviderException;

	/**
	 * Retrieves attributes that are contained within a certificate.
	 * @return Properties object containing the attributes from the certificate
	 */
	public Properties getAttributesFromCertificate(String identifier) throws SQLException, KeyStoreException, CertificateEncodingException;

	/**
	 * Returns private key stored under identifier
	 * @param identifier VAD or other identifier for a private key
	 * @return PrivateKey object or null if identifier did not exist
	 */
	public PrivateKey getPrivateKeyByIdentifier(String identifier);
	
	/**
	 * Generates a symmetric key from a provided string by applying one-way hash functions
	 * @param password String to use for key generation
	 * @param keySize
	 * @param algorithm String name according to JCE
	 * @return identifier used to store generated key
	 * @throws SQLException
	 * @throws KeyStoreException
	 */
	public String generateKeyFromPassword(String password, int keySize, String algorithm) throws SQLException, KeyStoreException;
	
	/**
	 * Generates a symmetric key from a provided string by applying one-way hash functions
	 * @param friendlyName identifier to store new key under
	 * @param password String to use for key generation
	 * @param keySize
	 * @param algorithm String name according to JCE
	 * @return false if identifier is taken
	 * @throws SQLException
	 * @throws KeyStoreException
	 */
	public boolean generateKeyFromPasswordWithFriendlyName(String friendlyName, String password, int keySize, String algorithm) throws SQLException, KeyStoreException;
	
	/**
	 * Generates a new symmetric key
	 * 
	 * @return Returns an identifier for this key.
	 */
	public String generateSymmetricKey() throws SQLException, NoSuchAlgorithmException, IOException, KeyStoreException,
			CertificateException, InvalidKeyException, SecurityException, SignatureException, IllegalStateException,
			NoSuchProviderException;

	/**
	 *@deprecated Use generateSymmetricKey() or generateSymmetricKey(int, String) instead.
	 */
	public String generateSymmetricKey(String algo) throws SQLException, NoSuchAlgorithmException, IOException, KeyStoreException,
			CertificateException, InvalidKeyException, SecurityException, SignatureException, IllegalStateException,
			NoSuchProviderException;
	
	/**
	 * Generates a new symmetric key
	 * @param keySize
	 * @param algorithm String name according to JCE
	 * @return identifier under which key has been stored
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
	public String generateSymmetricKey(int keySize, String algorithm) throws SQLException,
	NoSuchAlgorithmException, IOException, KeyStoreException,
	CertificateException, InvalidKeyException, SecurityException,
	SignatureException, IllegalStateException, NoSuchProviderException;
	
	/**
	 * Generates a symmetric key and stores it under provided friendly name
	 * @param friendlyName Name to be stored under
	 * @param algo Encryption algorithm name as JCE expects
	 * @return false when friendly name already exists
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws SecurityException
	 * @throws SignatureException
	 * @throws IllegalStateException
	 * @throws NoSuchProviderException
	 * @throws SQLException
	 * @throws IOException
	 */
	public boolean generateSymmetricKeyWithFriendlyName(String friendlyName, int keySize, String algorithm) throws InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, SecurityException, SignatureException, IllegalStateException, NoSuchProviderException, SQLException, IOException;
	
	/**
	 * Stores a symmetric key in the key store.
	 * 
	 * @param algo algorithm name according to JCE
	 * @param Base64 encoded key data
	 * 
	 * @return Returns an identifier for this key.
	 */
	public String storeSymmetricKey(String algo, String key) throws SQLException, NoSuchAlgorithmException, IOException, KeyStoreException,
			CertificateException, InvalidKeyException, SecurityException, SignatureException, IllegalStateException,
			NoSuchProviderException;
	
	/**
	 * Stores symmetric key under provided identifier
	 * @param friendlyName under which it gets stored
	 * @param algo algorithm name according to JCE
	 * @param key Base64 encoded string
	 * @return
	 * @throws SQLException
	 */
	public boolean storeSymmetricKeyWithFriendlyName(String friendlyName, String algorithm, String key) throws SQLException;
	
	/**
	 * Symmetrically encrypt a message so it can be opened by the receiver with decrypt.
	 * 
	 * @param documentString to be protected
	 * @param identifier of the key to be used
	 * @param format not used at the moment but must not be null
	 * @return A meta-data annotated protected string
	 * @throws Exception
	 */
	public String encryptSymmetric(String documentString, String identifier, String format) throws Exception;

	
	/**
	 * Returns a list of supported message formats. Currently, only XMLEnc will be returned.
	 */
	public Vector<String> getSupportedFormats();

	/**
	 * Assigns a VirtualAddress to an already existing certificate.
	 * 
	 * @param virtualAddress Address to be assigned to
	 * @param certRef The identifier of the certificate to be used
	 * @return true, if mapping has been created successfully. False otherwise (e.g., if certRef was invalid or did not exist).
	 * @throws SQLException 
	 */
	public boolean addCertificateForService(String virtualAddress, String certRef);
	
	/**
	 * Assigns private key to VirtualAddress.
	 * 
	 * @param virtualAddress Address to be assigned to
	 * @param certRef The identifier of the certificate to be used
	 * @return False if certificate reference did not exist
	 */
	public boolean addPrivateKeyForService(String virtualAddress, String certRef);

	/**
	 * Returns certificate identifier for VirtualAddress.
	 * @param virtualAddress
	 * @return Identifier under which certificate is stored in keystore
	 */
	public String getCertificateReference(String virtualAddress);

	/**
	 * Returns private key identifier for VirtualAddress
	 * @param virtualAddress
	 * @return Identifier under which private key is stored in keystore
	 */
	public String getPrivateKeyReference(String virtualAddress);
	
	/**
	 * Get key size for algorithm based on security level
	 * @param level security level
	 * @param algorithm Algorithm name according to JCE
	 * @return key length in bits
	 */
	public int getKeySize(SecurityLevel level, String algorithm);
	
	/**
	 * Calculates the MAC on a message with a key stored in the internal
	 * keystore of the CryptoManager. This way keys don't leave the manager.
	 * 
	 * @param identifier of the key to be used
	 * @param data for which to calculate MAC for
	 * @param algorithm Algorithm identifier according to provider
	 * @return The calculated MAC on the data
	 * @throws KeystoreException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public byte[] calculateMac(String identifier, String data, String algorithm) throws NoSuchAlgorithmException, KeyStoreException, InvalidKeyException;
	
	/**
	 * To check whether the provided identifier is available
	 * @param identifier
	 * @return True if identifier is available
	 */
	public boolean identifierExists(String identifier);
	
	/**
	 * Deletes an entry in the keystore as overwrites
	 * should not be possible.
	 * @param identifier to be removed
	 * @return True if identifier was found and deleted.
	 */
	public boolean deleteEntry(String identifier);
}
