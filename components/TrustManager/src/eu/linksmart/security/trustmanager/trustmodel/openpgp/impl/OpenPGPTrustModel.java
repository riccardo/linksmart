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
import java.util.Iterator;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPUtil;

import eu.linksmart.security.trustmanager.trustmodel.openpgp.config.OpenPGPTrustModelConfiguration;

import eu.linksmart.security.trustmanager.trustmodel.TrustModel;

/**
 * This is the OpenPGPTrustModel Class, representing a Trust Model which uses
 * OpenPGP for its backend.
 * 
 * No private keys are used on the Server side, Trust value calculation uses a
 * web of trust approach. Parameters for the Web of trust are hardcoded at the
 * moment (see NR_OF_NEEDED_FULLY_TRUSTED_INTRODUCERS,
 * NR_OF_NEEDED_MARGINALLY_TRUSTED_INTRODUCERS).
 * 
 * 
 * @author Julian Schütte (julian.schuette@sit.fraunhofer.de)
 * @author Stephan Heuser (stephan.heuser@sit.fraunhofer.de)
 * 
 */
public class OpenPGPTrustModel implements TrustModel {

	/** Identifier of this Trust Model */
	private final String IDENTIFIER = "OpenPGPTrustModel";

	/**
	 * Number of needed fully trusted introducers, see OpenPGP Web of Trust
	 * documentation. If a key is signed by x fully trusted introducers, its
	 * validity is assumed to be full
	 */
	private final Integer NR_OF_NEEDED_FULLY_TRUSTED_INTRODUCERS = 1;

	/**
	 * Number of needed marginally trusted introducers, see OpenPGP Web of Trust
	 * documentation. If a key is signed by x marginally trusted introducers,
	 * its validity is assumed to be marginal
	 */
	private final Integer NR_OF_NEEDED_MARGINALLY_TRUSTED_INTRODUCERS = 2;

	/** The logger, required for logging */
	private final static Logger logger = Logger.getLogger("trustmanager");
	
	private boolean isInitialized = false;

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public double getTrustValue(byte[] token) {
		try {

			// Get the Key Ring
			InputStream decoderStream =
					PGPUtil.getDecoderStream(new ByteArrayInputStream(token));
			PGPPublicKeyRing keyring = new PGPPublicKeyRing(decoderStream);
			decoderStream.close();

			// Get the Keys
			// Iterator<PGPPublicKey> iterator = keyring.getPublicKeys();

			PGPPublicKey currentkey = keyring.getPublicKey();

			// Get the Signatures
			OpenPGPKeyStore keystore = OpenPGPKeyStore.getInstance();

			// while(iterator.hasNext()) {

			// PGPPublicKey currentkey = iterator.next();

			// Check if key in KeyStore

			String id = Long.toHexString(currentkey.getKeyID());
			logger.debug("Testing Key : " + id);

			if (keystore.getKey(id) != null) {

				if (keystore.getValidity(id) != null) {
					logger.debug("Testing Key : " + id + ": Validity = "
							+ keystore.getValidity(id));

					if ((keystore.getValidity(id)) > 0) {
						return keystore.getValidity(id);
					}
				}
			}

			// This will only be executed when key validity not set, or to low
			// Build path

			Iterator<PGPSignature> signatures = currentkey.getSignatures();

			int nrOfFullyTrustedIntroducers = 0;
			int nrOfMarginallyTrustedIntroducers = 0;
			logger.debug("Signatures.hasNext(): " + signatures.hasNext());
			while (signatures.hasNext()) {
				String currentId =
						Long.toHexString(signatures.next().getKeyID());
				Integer trust = keystore.getTrust(currentId);
				if (trust != null) {
					if (trust == OpenPGPKeyStore.TRUST_FULL) {
						nrOfFullyTrustedIntroducers++;
						logger.debug("Testing Key : " + id
								+ ": Fully Trusted Introducer found.");
					} else if (trust == OpenPGPKeyStore.TRUST_MARGINAL) {
						nrOfMarginallyTrustedIntroducers++;
						logger.debug("Testing Key : " + id
								+ ": Marginally Trusted Introducer found.");
					}
				}
			}

			if (nrOfFullyTrustedIntroducers >= NR_OF_NEEDED_FULLY_TRUSTED_INTRODUCERS) {
				return 1.0;
			}

			if (nrOfMarginallyTrustedIntroducers >= NR_OF_NEEDED_MARGINALLY_TRUSTED_INTRODUCERS) {
				return 0.5;
			}
			// }

		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		logger.debug("RETURN 0");
		return 0;
	}

	@Override
	public void initialize() {
		if(!isInitialized){
			//this automatically initializes the store
		OpenPGPKeyStore.getInstance();
		isInitialized = true;
		}
	}
	
	@Override
	public Class getConfigurator(){
		return OpenPGPTrustModelConfiguration.class;
	}
	
	@Override
	public Class getConfiguratorClass() {
		return OpenPGPTrustModelConfigurationImpl.class;
	}

}
