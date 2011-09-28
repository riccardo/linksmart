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

package eu.linksmart.security.cryptomanager.cryptoprocessor;

/**
 * Common interface for all CryptoProcessors. <p>
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 */
public interface CryptoMessageFormatProcessor {

	/**
	 * Decrypts a Linksmart message that has been created using the
	 * <code>encryptAsymmetric</code> method. <p> All relevant information for
	 * decrypting the message should be included in the message itself. In the
	 * case of an error, this method returns null.
	 */
	public abstract String decrypt(String encryptedData);

	/**
	 * Encrypts a Linksmart message so it can be opened by the receiver using the
	 * <code>decryptAsymmetric</code> method.
	 */
	public abstract String encryptAsymmetric(String documentString,
			String identifier, String format) throws Exception;

	/**
	 * Symmetrically encrypt a Linksmart message so it can be opened by the
	 * receiver.
	 * 
	 * @param documentString
	 * @param identifier
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public String encryptSymmetric(String documentString, String identifier,
			String format) throws Exception;

	/**
	 * Creates a signed Linksmart message
	 * 
	 * @param data
	 * @param identifier
	 * @param format
	 *            The message format to be used. Currently, only
	 *            <code>XMLEnc</code> is supported.
	 * @return
	 * @throws Exception
	 */
	public abstract String sign(String data, String format);

	/**
	 * Retrieves a list of supported message formats.
	 * 
	 * @return
	 */
	public abstract String[] getSupportedFormats();

	/**
	 * Verifies a signed Linksmart message
	 * 
	 */
	public abstract String verify(String data);

	/**
	 * Creates a signed message.
	 * 
	 * @param data
	 * @param format
	 * @param identifier
	 * @return
	 */
	public abstract String sign(String data, String format, String identifier);
}