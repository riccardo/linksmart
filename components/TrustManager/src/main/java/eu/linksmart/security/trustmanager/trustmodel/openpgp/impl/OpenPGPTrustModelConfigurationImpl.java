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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jws.soap.SOAPBinding;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPUtil;

import eu.linksmart.security.trustmanager.trustmodel.openpgp.config.OpenPGPTrustModelConfiguration;
import eu.linksmart.security.trustmanager.util.Base64;

/**
 * This is the OpenPGPTrustModel Configuration class, representing a Web Service
 * which may be used to configure the Web Of Trust Settings, and to
 * add/remove/modify Public Keys and their parameters.
 * 
 * @author Stephan Heuser (stephan.heuser@sit.fraunhofer.de)
 * 
 */

public class OpenPGPTrustModelConfigurationImpl implements OpenPGPTrustModelConfiguration {

	/* (non-Javadoc)
	 * @see eu.linksmart.security.trustmanager.trustmodel.openpgp.config.OpenPGPTrustModelConfiguration#removeKeyWithID(java.lang.String)
	 */

	@Override
	public boolean removeKeyWithID(String id) {
		return OpenPGPKeyStore.getInstance().removeKey(id);
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.security.trustmanager.trustmodel.openpgp.config.OpenPGPTrustModelConfiguration#removeKey(java.lang.String)
	 */

	@Override
	public boolean removeKey(String key) {

		boolean flag = false;
		byte[] encodedKey = Base64.decode(key);
		try {

			InputStream decoderStream =
					PGPUtil.getDecoderStream(new ByteArrayInputStream(
							encodedKey));
			PGPPublicKeyRing keyring = new PGPPublicKeyRing(decoderStream);
			decoderStream.close();

			Iterator<PGPPublicKey> keys = keyring.getPublicKeys();
			while (keys.hasNext()) {
				// OpenPGPKeyStore.getInstance().addKey(keys.next(), trust);
				PGPPublicKey currentKey = keys.next();
				if (OpenPGPKeyStore.getInstance().removeKey(currentKey) == true) {
					flag = true;
					// logger.debug("Key with ID "
					// + Long.toHexString(currentKey.getKeyID())
					// + " removed!");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return flag;
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.security.trustmanager.trustmodel.openpgp.config.OpenPGPTrustModelConfiguration#addKey(java.lang.String, int, int)
	 */

	@Override
	public boolean addKey(String key, int trust, int validity) {

		boolean flag = false;
		byte[] encodedKey = Base64.decode(key);
		try {

			// Check trust and validity, normalize if needed

			InputStream decoderStream =
					PGPUtil.getDecoderStream(new ByteArrayInputStream(
							encodedKey));
			PGPPublicKeyRing keyring = new PGPPublicKeyRing(decoderStream);
			decoderStream.close();

			PGPPublicKey currentKey = keyring.getPublicKey();
			OpenPGPKeyStore.getInstance().addKey(currentKey, trust, validity);
			return true;

			/*
			 * Iterator<PGPPublicKey> keys = keyring.getPublicKeys();
			 * 
			 * 
			 * 
			 * while (keys.hasNext()) { PGPPublicKey currentKey = keys.next();
			 * OpenPGPKeyStore.getInstance().addKey(currentKey, trust,
			 * validity); //logger.debug("Key with ID " // +
			 * Long.toHexString(currentKey.getKeyID()) + " added!"); flag =
			 * true;
			 * 
			 * }
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.security.trustmanager.trustmodel.openpgp.config.OpenPGPTrustModelConfiguration#modifyKey(java.lang.String, int, int)
	 */

	@Override
	public boolean modifyKey(String id, int trust, int validity) {
		OpenPGPKeyStore keystore = OpenPGPKeyStore.getInstance();

		if (keystore.containsKey(id)) {
			keystore.setKeyValidity(id, validity);
			keystore.setOwnerTrust(id, trust);
			// logger.debug("Key with ID " + id + " modified!");
			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.security.trustmanager.trustmodel.openpgp.config.OpenPGPTrustModelConfiguration#getKey(java.lang.String)
	 */

	@Override
	public String getKey(String id) {

		PGPPublicKey pk = OpenPGPKeyStore.getInstance().getKey(id);
		if (pk != null) {
			try {
				// logger.debug("Key with ID " + Long.toHexString(pk.getKeyID())
				// + " retrieved!");
				return (Base64.encodeBytes(pk.getEncoded()));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.security.trustmanager.trustmodel.openpgp.config.OpenPGPTrustModelConfiguration#getTrust(java.lang.String)
	 */

	@Override
	public int getTrust(String id) {
		return OpenPGPKeyStore.getInstance().getTrust(id);
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.security.trustmanager.trustmodel.openpgp.config.OpenPGPTrustModelConfiguration#getValidity(java.lang.String)
	 */

	@Override
	public double getValidity(String id) {
		return OpenPGPKeyStore.getInstance().getValidity(id);
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.security.trustmanager.trustmodel.openpgp.config.OpenPGPTrustModelConfiguration#getKeys()
	 */

	@Override
	public String[] getKeys() {

		ArrayList<String> encodedKeys = new ArrayList<String>();

		for (byte[] current : OpenPGPKeyStore.getInstance().getKeys()) {
			encodedKeys.add(Base64.encodeBytes(current));
		}
		String[] values = encodedKeys.toArray(new String[0]);
		return values;

	}

}
