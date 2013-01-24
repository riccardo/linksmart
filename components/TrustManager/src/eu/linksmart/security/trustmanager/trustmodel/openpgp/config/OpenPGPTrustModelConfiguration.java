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
package eu.linksmart.security.trustmanager.trustmodel.openpgp.config;

public interface OpenPGPTrustModelConfiguration {

	/**
	 * Remove a key with a given Id from the Key Store
	 * 
	 * @param id
	 *            Id of the key to be removed
	 * @return true if remove was successful, false if not
	 */

	public boolean removeKeyWithID(String id);

	/**
	 * Remove a given public key from the Trust Store.
	 * 
	 * @param key
	 *            The OpenPGP Key, represented as a Base64 encoded
	 *            PGPPublicKeyRing. See Bouncycastle Documentation for mor
	 *            information on how to create PGPPublicKeyRing from, for
	 *            example, .asc files.
	 * 
	 * @return true if successful, false if not
	 */

	public boolean removeKey(String key);

	/**
	 * Add a Key to the OpenPGP Keystore
	 * 
	 * @param key
	 *            The OpenPGP Key, represented as a Base64 encoded
	 *            PGPPublicKeyRing. See Bouncycastle Documentation for mor
	 *            information on how to create PGPPublicKeyRing from, for
	 *            example, .asc files.
	 * @param trust
	 *            Owner trust value associated with the given key
	 * @param validity
	 *            Validity value associated with the given key
	 * @return true if successful, false if not
	 */

	public boolean addKey(String key, int trust, int validity);

	/**
	 * Modify an existing key.
	 * 
	 * @param id
	 *            Id of the existing key
	 * @param trust
	 *            New trust value of the key
	 * @param validity
	 *            New validity value of the key
	 * @return true if settings were successfully applied, false if not
	 */

	public boolean modifyKey(String id, int trust, int validity);

	/**
	 * Get an existing key from the Key store
	 * 
	 * @param id
	 *            Id of the requested OpenPGP Key
	 * @return A Base64 encoded representation of the Public Key, or null if a
	 *         key with the given id could not be retrieved
	 */

	public String getKey(String id);

	/**
	 * Get the Trust value of a key, identified by its Id
	 * 
	 * @param id
	 *            Id of the key
	 * @return Trust value associated with this key
	 */

	public int getTrust(String id);

	/**
	 * Get the Validity value of a key, identified by its Id
	 * 
	 * @param id
	 *            Id of the key
	 * @return Validity value associated with this key
	 */

	public double getValidity(String id);

	/**
	 * Get a list of stored Keys, base64 encoded
	 * 
	 * @return List of base64 encoded Keys
	 */

	public String[] getKeys();

}