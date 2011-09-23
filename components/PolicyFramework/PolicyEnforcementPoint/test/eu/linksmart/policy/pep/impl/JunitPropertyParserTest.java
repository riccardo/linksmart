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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.policy.pep.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.linksmart.policy.pep.impl.PropertyParser;

/**
 * Unit test for {@link PropertyParser}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitPropertyParserTest {
	
	/* test XML message to parse */
	private static String testXml = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?> "  
			+ "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">"
			+ "<properties>"
			+ "<comment/>"
			+ "<entry key=\"CN\">LinkSmarttest</entry>"
			+ "<entry key=\"Des\">EventManager:cars</entry>"
			+ "<entry key=\"DN\">eu.linksmart.security</entry>"
			+ "<entry key=\"C\">DE</entry>"
			+ "<entry key=\"SID\">EventManagerPort</entry>"
			+ "<entry key=\"Pseudonym\">linksmartPseudonym</entry>"
			+ "<entry key=\"PID\">EventManager:cars</entry>"
			+ "</properties>";

	@Test
	public void testParseXml() {
		for (int i=0; i < 1000; i++) {
			PropertyParser parser = new PropertyParser();
			Properties props = new Properties();
			HashMap<String, String> data = new HashMap<String, String>();
			for (int j=0; j < 10; j++) {
				String key = UUID.randomUUID().toString();
				String val = UUID.randomUUID().toString();
				data.put(key, val);
				props.setProperty(key, val);
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				props.storeToXML(baos, null);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				fail("IO exception");
			}
			String propXml = baos.toString();
			Properties parsProps = parser.parseXml(propXml);
			for (String key : data.keySet()) {
				if (!(parsProps.containsKey(key))) {
					fail("Key missing");
				}
				if (!data.get(key).equals(parsProps.getProperty(key))) {
					fail("Value incorrect");
				}
			}
		}
	}
	
	@Test
	public void testParseXmlWActualMessage() {
		PropertyParser parser = new PropertyParser();
		Properties parsProps = parser.parseXml(testXml);
		System.out.println(parsProps.toString());
	}

}
