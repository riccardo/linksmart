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

package eu.linksmart.security.trustmanager;

import java.rmi.RemoteException;

/**
 * This interface describes a web service for checking tokens with the Trust
 * Manager, to be used as a web service.
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 * @author Stephan Heuser (stephan.heuser@sit.fraunhofer.de)
 * 
 */
public interface TrustManager extends java.rmi.Remote {

	/**
	 * Evaluate the trust in a given token.
	 * 
	 * @param token
	 *            the to be evaluated token
	 * @return long value (0 - 1), 0 = no trust, 1 = max. trust
	 * @throws RemoteException 
	 */
	public double getTrustValue(String token) throws RemoteException;

	/**
	 * Evaluate the trust in a given token, given a specific trust model
	 * identifier
	 * 
	 * @param token
	 *            the to be evaluated token
	 * @param trustModelIdentifier
	 *            the trustmodel identifier to be used
	 * @return long value (0 - 1), 0 = no trust, 1 = max. trust
	 * @throws RemoteException 
	 */
	public double getTrustValueWithIdentifier(String token,
			String trustModelIdentifier) throws RemoteException;

	/**
	 * Creates a token according to the active trust model.
	 * @return Identifier of created token
	 * @throws RemoteException
	 */
	public String createTrustToken() throws RemoteException;
	
	/**
	 * Creates a token according to the active trust model.
	 * @param identifier Tries to give the token provided identifier
	 * @return False if identifier already taken
	 * @throws RemoteException
	 */
	public boolean createTrustTokenWithFriendlyName(String identifier) throws RemoteException;
	
	
	/**
	 * Returns the trust token of the device with given identifier.
	 * This can be used as trust identification to other NetworkManagers. 
	 * @return Base64 encoded representation of the trust token
	 */
	public String getTrustToken(String identifier) throws RemoteException;
}
