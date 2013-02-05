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

package eu.linksmart.security.trustmanager.trustmodel.openpgp.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;

import eu.linksmart.security.trustmanager.impl.TrustManagerImpl;
import eu.linksmart.security.trustmanager.util.Util;

/**
 * OpenPGP Public Key Keystore
 * 
 * This is more or less a dummy class which deserializes public keys from the
 * OpenPGP Keys, Trust and Validity Classes and stores them in memory. Upon
 * change of Keys, Trust and Validity these will be saved back to the
 * corresponding files
 * 
 * Singleton Pattern
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 * @author Stephan Heuser (stephan.heuser@sit.fraunhofer.de)
 * 
 */

final public class OpenPGPKeyStore {

	/** Instance variable for Singleton pattern */
	private static OpenPGPKeyStore instance = null;


	public final static String OPENPGPFOLDERPATH = TrustManagerImpl.TRUSTMANAGER_RESOURCE_FOLDERPATH + Util.FILE_SEPERATOR + "openpgp";
	
	/** Path to OpenPGP KeyStore */
	private final static String PATH_KEYSTORE = OPENPGPFOLDERPATH + Util.FILE_SEPERATOR + "keys";

	/** Path to OpenPGP Trust Store */
	private final static String PATH_TRUST = OPENPGPFOLDERPATH + Util.FILE_SEPERATOR + "trust";

	/** Path to OpenPGP Validity Store */
	private final static String PATH_VALIDITY = OPENPGPFOLDERPATH + Util.FILE_SEPERATOR + "validity";

	/** The logger, required for logging */
	private final static Logger logger = Logger.getLogger("trustmanager");

	/** Constant for full Trust, according to OpenPGP Specifications */
	public final static Integer TRUST_FULL = 120;

	/** Constant for marginal Trust, according to OpenPGP Specifications */
	public final static Integer TRUST_MARGINAL = 60;

	/** Constant for no Trust, according to OpenPGP Specifications */
	public final static Integer TRUST_NONE = 0;

	/** Constant for unknown Trust, according to OpenPGP Specifications */
	public final static Integer TRUST_UNKNOWN = 0;
	

	/**
	 * LinkedHashMap containing values (OpenPGP Keys as byte[]) and Keys (ID od
	 * the OpenPGP Key)
	 */
	private static LinkedHashMap<String, byte[]> keys =
			new LinkedHashMap<String, byte[]>();

	/**
	 * LinkedHashMap containing the Trust values for each key, identified by Id
	 */
	private static LinkedHashMap<String, Integer> ownerTrust =
			new LinkedHashMap<String, Integer>();

	/**
	 * LinkedHashMap containing the Validity values for each key, identified by
	 * Id
	 */
	private static LinkedHashMap<String, Double> keyValidity =
			new LinkedHashMap<String, Double>();

	/**
	 * Constructor is private, singleton pattern
	 */
	private OpenPGPKeyStore() {

	}

	/**
	 * Return the OpenPGPKeyStore instance (Singleton)
	 * 
	 * @return the OpenPGPKeyStore instance
	 */
	public static OpenPGPKeyStore getInstance() {
		if (instance == null) {
			instance = new OpenPGPKeyStore();
			initialize();
		}
		return instance;

	}

	/**
	 * Initialize the Trust Store, read keys, owner trust and key validity
	 * values
	 */
	private static void initialize() {
		//try extracting files from jar first
		Hashtable<String, String> HashFilesExtract = new Hashtable<String, String>();
		HashFilesExtract.put(OPENPGPFOLDERPATH + Util.FILE_SEPERATOR + "keys", "resources/openpgp/keys");
		HashFilesExtract.put(OPENPGPFOLDERPATH + Util.FILE_SEPERATOR + "trust", "resources/openpgp/trust");
		HashFilesExtract.put(OPENPGPFOLDERPATH + Util.FILE_SEPERATOR + "validity", "resources/openpgp/validity");
		
		Util.createDirectory(OPENPGPFOLDERPATH);
		Util.extractFilesJar(HashFilesExtract);
		
		File fileKeyStore = new File(PATH_KEYSTORE);
		if (fileKeyStore.exists() && fileKeyStore.canRead()) {
			try {
				// Read Key Store File
				FileInputStream fis = new FileInputStream(fileKeyStore);
				ObjectInputStream ois = new ObjectInputStream(fis);
				keys = (LinkedHashMap<String, byte[]>) ois.readObject();
				ois.close();
				fis.close();
				logger.debug("Old keystore found, imported.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			// save Key Store
			try {
				FileOutputStream fos = new FileOutputStream(fileKeyStore);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(keys);
				oos.flush();
				oos.close();
				fos.flush();
				fos.close();
				logger.debug("No keystore found. Created new keystore.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File fileTrustStore = new File(PATH_TRUST);
		if (fileTrustStore.exists() & fileTrustStore.isFile()
				& fileTrustStore.canRead()) {

			// Existing Trust File, Read File
			try {

				FileInputStream fis = new FileInputStream(fileTrustStore);
				ObjectInputStream ois = new ObjectInputStream(fis);
				ownerTrust = (LinkedHashMap<String, Integer>) ois.readObject();
				ois.close();
				fis.close();
				logger.debug("Old truststore found, imported");

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			// Create new Trust, Validity and Owner File
			try {
				FileOutputStream fos = new FileOutputStream(fileTrustStore);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(ownerTrust);
				oos.flush();
				oos.close();
				fos.flush();
				fos.close();
				logger.debug("No truststore found. Created new truststore.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Key Validity Store
		File fileKeyValidity = new File(PATH_VALIDITY);
		if (fileKeyValidity.exists() & fileKeyValidity.isFile()
				& fileKeyValidity.canRead()) {

			// Existing Trust File, Read File
			try {
				FileInputStream fis = new FileInputStream(fileKeyValidity);
				ObjectInputStream ois = new ObjectInputStream(fis);
				keyValidity = (LinkedHashMap<String, Double>) ois.readObject();
				ois.close();
				fis.close();
				logger.debug("Old validitystore found, imported");

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			// Create new Validity File
			try {
				FileOutputStream fos = new FileOutputStream(fileKeyValidity);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(keyValidity);
				oos.flush();
				oos.close();
				fos.flush();
				fos.close();
				logger
						.debug("No validitystore found. Created new validitystore.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Trust and Key Store are loaded now
	}

	/**
	 * Add a key to the KeyStore, validity will be 0
	 * 
	 * @param key
	 *            to-be-added Public Key
	 * @param keyOwnerTrust
	 *            Owner Trust of the key
	 */

	public void addKey(PGPPublicKey key, Integer keyOwnerTrust) {
		String id = Long.toHexString(key.getKeyID());
		try {
			keys.put(Long.toHexString(key.getKeyID()), key.getEncoded());
			ownerTrust.put(id, keyOwnerTrust);
			keyValidity.put(id, 0.0);
			logger.debug("Key with ID: " + id + " added with trust="
					+ keyOwnerTrust + " and validity=0 !");
			saveChanges();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a key to the KeyStore
	 * 
	 * @param key
	 *            to-be-added Public Key
	 * @param keyOwnerTrust
	 *            Owner Trust of the key
	 * @param keyvalidity
	 *            Validity of the key
	 */

	public void addKey(PGPPublicKey key, Integer keyOwnerTrust,
			double keyvalidity) {
		String id = Long.toHexString(key.getKeyID());
		try {
			keys.put(Long.toHexString(key.getKeyID()), key.getEncoded());
			ownerTrust.put(id, keyOwnerTrust);
			keyValidity.put(id, keyvalidity);
			logger.debug("Key with ID: " + id + " added with trust="
					+ keyOwnerTrust + " and validity= " + keyvalidity + " !");
			saveChanges();
		} catch (IOException e) {
			logger.debug("Error while adding Key with ID: " + id);
			e.printStackTrace();
		}
	}

	/**
	 * Set the validity of a key manually
	 * 
	 * @param id
	 *            of the public key
	 * @param validity
	 *            validity of this key
	 */

	public void setKeyValidity(String id, double validity) {
		keyValidity.put(id, validity);
		logger.debug("Validity of Key with ID: " + id + " set to " + validity
				+ " !");
		saveChanges();
	}

	/**
	 * Retrieve a key from the Key Store
	 * 
	 * @param id
	 *            if of the public key
	 * @return key, if found, else null
	 */

	public PGPPublicKey getKey(String id) {

		byte[] keyEncodedWithChecksum = keys.get(id);

		if (keyEncodedWithChecksum == null) {
			return null;
		} else {
			try {
				// This is a workaround to create a PGPPublicKey from a Byte
				// Array
				ByteArrayInputStream bis =
						new ByteArrayInputStream(keyEncodedWithChecksum);
				PGPObjectFactory pgpFactory = new PGPObjectFactory(bis);
				PGPPublicKeyRing keyring;
				keyring = (PGPPublicKeyRing) pgpFactory.nextObject();
				// Iterator<PGPPublicKey> i = keyring.getPublicKeys();
				PGPPublicKey pk = keyring.getPublicKey();

				if (Long.toHexString(pk.getKeyID()).equals(id)) {
					logger.debug("Key with ID: " + id + " found!");
					return pk;
				}

				/*
				 * while (i.hasNext()) { PGPPublicKey pk = i.next(); if
				 * (Long.toHexString(pk.getKeyID()).equals(id)) {
				 * 
				 * logger.debug("Key with ID: " + id + " found!");
				 * 
				 * return pk; } }
				 */
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * Remove a key from the key store, also removes trust and validity values
	 * 
	 * @param key
	 *            public key to be removed
	 * @return true if successful, false if not
	 */

	public boolean removeKey(PGPPublicKey key) {
		String id = Long.toHexString(key.getKeyID());
		byte[] returnvalue = keys.remove(id);

		if (returnvalue == null) {
			return false;
		}
		ownerTrust.remove(id);
		keyValidity.remove(id);

		logger.debug("Key with ID: " + id + " removed!");

		saveChanges();
		return true;
	}

	/**
	 * Remove a key from the key store, also removes trust and validity values
	 * 
	 * @param key
	 *            id of public key to be removed
	 * @return true if successful, false if not
	 */

	public boolean removeKey(String id) {
		byte[] returnvalue = keys.remove(id);
		if (returnvalue == null) {
			return false;
		}
		ownerTrust.remove(id);
		keyValidity.remove(id);

		logger.debug("Key with ID: " + id + " removed!");

		saveChanges();
		return true;
	}

	/**
	 * Get the validity value of a OpenPGP Public Key identified by Id
	 * 
	 * @param id
	 *            Id of the OpenPGP Key
	 * @return Validity value of the key
	 */

	public Double getValidity(String id) {
		return keyValidity.get(id);
	}

	/**
	 * Get the trust value of a OpenPGP Public Key identified by Id
	 * 
	 * @param id
	 *            Id of the OpenPGP Key
	 * @return Trust value of the key
	 */

	public Integer getTrust(String id) {
		return ownerTrust.get(id);
	}

	/**
	 * Check if a given OpenPGP Public Key Id already exists in the Store
	 * 
	 * @param id
	 *            Id of the OpenPGP Public Key
	 * @return true if it already exists in the Store, false if not
	 */

	public boolean containsKey(String id) {
		if (getKey(id) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Set the trust value of an OpenPGP Public Key identified by Id
	 * 
	 * @param id
	 *            Id of the OpenPGP Public Key
	 * @param trust
	 *            Trust value of this key Id
	 */

	public void setOwnerTrust(String id, int trust) {
		ownerTrust.put(id, trust);
		logger.debug("Trust in Owner of Key with ID: " + id + " set to "
				+ trust + " !");
		saveChanges();
	}

	/**
	 * Save the changes to OpenPGP Keys/Certificates to the Storage Medium This
	 * should be called after every change in a Key/Certificate
	 */

	private void saveChanges() {
		try {
			FileOutputStream fos = new FileOutputStream(PATH_KEYSTORE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(keys);
			oos.close();
			fos.close();

			fos = new FileOutputStream(PATH_TRUST);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ownerTrust);
			oos.close();
			fos.close();

			fos = new FileOutputStream(PATH_VALIDITY);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(keyValidity);
			oos.close();
			fos.close();

			logger
					.debug("Keystore, truststore and validitystore updated and saved!");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a list of all (encoded) keys
	 * 
	 * @return list of keys as byte[]
	 */

	public List<byte[]> getKeys() {
		ArrayList<byte[]> list = new ArrayList<byte[]>(keys.values());
		return list;
	}
}
