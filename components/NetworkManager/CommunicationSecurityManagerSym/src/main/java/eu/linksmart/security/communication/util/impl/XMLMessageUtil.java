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
 * Copyright (C) 2006-2010
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

package eu.linksmart.security.communication.util.impl;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import eu.linksmart.utils.Base64;

/**
 * This class is a utility class for dissecting signed XML Messages.
 * @author Stephan Heuser - stephan.heuser@sit.fraunhofer.de
 *
 */

public class XMLMessageUtil {
	private final static Logger logger = Logger.getLogger(XMLMessageUtil.class.getName());  
	/**
	 * 
	 * Disect a signed xml message and store all the relevant information in a
	 * properties object.
	 * 
	 * @param signedXML The signed XML Message
	 * @return The Certificate embedded in the message
	 */
	
	public static Properties getOriginalProperties(String signedXML) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
		try {
			builder = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(signedXML));
			doc = builder.parse(is);
			
			byte[] serializedProp = null;
			if ("properties".equals(doc.getDocumentElement().getNodeName())) {
				serializedProp = signedXML.getBytes();
			} else {
				String out = doc.getFirstChild().getChildNodes().item(0).getTextContent();
				serializedProp = out.getBytes();
			}
			Properties newp = new Properties();
			newp.loadFromXML(new ByteArrayInputStream(serializedProp));
			return newp;
		} catch (Exception e) {
			logger.error(e);
		} 
		return null;
	}
	
	/**
	 * Get the certificate embedded in a signed XML Message
	 * @param signedXML signed XML Message
	 * @return The Certificate embedded in the message, or null if none is embedded
	 */
	
	public static Certificate getCertificate(String signedXML) {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
		try {
			builder = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(signedXML));
			doc = builder.parse(is);
			Node certnode = doc.getFirstChild().getChildNodes().item(1).getChildNodes().item(5).getChildNodes().item(1);
			byte[] certtext = Base64.decode(certnode.getTextContent().trim());
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			Certificate cert = cf.generateCertificate(new ByteArrayInputStream(certtext));
			return cert;
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
		
	}
}
