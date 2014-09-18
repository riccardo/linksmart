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

package eu.linksmart.security.trustmanager.trustmodel.nullmodel;

import javax.xml.ws.Endpoint;

import eu.linksmart.security.trustmanager.trustmodel.TrustModel;

/**
 * This is the null class model, which is a mostly harmless example of how to
 * write a Trust Model. It is only of limited use, as it performs no checks
 * whatsoever on the token and returns 1 as the trust value.
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 * @author Stephan Heuser (stephan.heuser@sit.fraunhofer.de)
 * 
 */
public class NullTrustModel implements TrustModel {

	/** Identifier of this Trust Model */
	public static final String IDENTIFIER = "NullTrustModel";

	/**
	 * Returns the identifier of this trust model: NullTrustModel.
	 */
	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	/**
	 * This method always returns 1, as this trust model always returns full
	 * trust in all credentials.
	 */
	@Override
	public double getTrustValue(byte[] token) {
		// always returns 1
		return 1;
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void initialize() {
		// do nothing
	}

	@Override
	public Class getConfigurator() {
		// No configuration
		return null;
	}

	@Override
	public Class getConfiguratorClass() {
		// No configuration
		return null;
	}
}
