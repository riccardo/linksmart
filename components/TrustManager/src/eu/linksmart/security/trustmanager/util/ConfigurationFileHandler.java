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

package eu.linksmart.security.trustmanager.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This is a utility class used for XML Configuration File Processing
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 * @author Stephan Heuser (stephan.heuser@sit.fraunhofer.de)
 */
public class ConfigurationFileHandler {

	private ArrayList<String> trustModelClassNames = new ArrayList<String>();

	/**
	 * Constructor.
	 */
	public ConfigurationFileHandler() {
		File configFile = new File(Util.FILE_CONFIG);
		if (configFile.exists() & configFile.isFile() & configFile.canRead()) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			try {
				DocumentBuilder documentbuilder = dbf.newDocumentBuilder();
				Document dom = documentbuilder.parse(Util.FILE_CONFIG);
				Element docelement = dom.getDocumentElement();
				NodeList nodelist =
						docelement.getElementsByTagName("TrustModel");

				if (nodelist != null && nodelist.getLength() > 0) {
					for (int i = 0; i < nodelist.getLength(); i++) {
						Element element = (Element) nodelist.item(i);
						trustModelClassNames.add(getTextValue(element,
								"ClassName"));
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Retrieves the names of all supported trust models.
	 * 
	 * @return a List of trust model names.
	 */
	public List<String> getTrustModelClassNames() {
		return trustModelClassNames;
	}

	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

}
