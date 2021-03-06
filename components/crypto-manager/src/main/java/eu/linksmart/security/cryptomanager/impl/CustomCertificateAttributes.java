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

package eu.linksmart.security.cryptomanager.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.X509Principal;

public class CustomCertificateAttributes {

	private Properties certificateAttributes;
	private final static Logger logger = Logger
			.getLogger(CustomCertificateAttributes.class.getName());
	private static final String DEFAULT_PSEUDONYM = "linksmartPseudonym";
	private static final String DEFAULT_DN_QUALIFIER =
			"eu.linksmart.security";
	private static final String DEFAULT_DN = "LinkSmarttest";
	private static final String DEFAULT_D = "DE";

	private CustomCertificateAttributes() {
		// Do nothing
	}

	private CustomCertificateAttributes(Properties attributes) {
		if (attributes == null) {
			attributes = new Properties();
		}

		// Merge attributes to string
		String mergedAttributes = "";
		Enumeration keyEnum = attributes.keys();
		while (keyEnum.hasMoreElements()) {
			// Get key and value
			String key = (String) keyEnum.nextElement();
			String value = (String) attributes.get(key);

			// Replace non-permitted characters
			key.replace("|", "");
			key.replace(";", "");
			value.replace("|", "");
			value.replace(";", "");

			// merge to string
			mergedAttributes += key + "|" + value + ";";
		}

		// Create all mandatory fields for certificate
		// TODO make mandatory fields configurable via config file
		Properties certificateAttributes = new Properties();
		certificateAttributes.put(X509Principal.C, DEFAULT_D);
		certificateAttributes.put(X509Principal.CN, DEFAULT_DN);
		certificateAttributes.put(X509Principal.DN_QUALIFIER,
				DEFAULT_DN_QUALIFIER);
		certificateAttributes.put(X509Principal.PSEUDONYM,
				DEFAULT_PSEUDONYM);
		certificateAttributes.put(X509Principal.UNIQUE_IDENTIFIER,
				mergedAttributes);

		this.certificateAttributes = certificateAttributes;
	}

	public static CustomCertificateAttributes loadFromXML(String xml)
			throws InvalidPropertiesFormatException, IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
		Properties attributeProperties = new Properties();
		attributeProperties.loadFromXML(bis);
		CustomCertificateAttributes result =
				new CustomCertificateAttributes(attributeProperties);
		return result;
	}

	public static CustomCertificateAttributes loadFromProperties(
			Properties attributes) throws InvalidPropertiesFormatException,
			IOException {
		return new CustomCertificateAttributes(attributes);
	}

	public Hashtable getAttributes() {
		return certificateAttributes;
	}

}
