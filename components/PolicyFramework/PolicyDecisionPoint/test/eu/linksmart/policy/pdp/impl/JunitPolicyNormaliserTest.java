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
package eu.linksmart.policy.pdp.impl;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import eu.linksmart.policy.pdp.impl.PolicyNormaliser;

/**
 * Unit test for {@link PolicyNormaliser}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitPolicyNormaliserTest extends TestCase {

	String policy = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Policy Polic" +
			"yId=\"ActionWArgsConditionAccessPolicy\" RuleCombiningAlgId=\"u" +
			"rn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-ove" +
			"rrides\" xmlns=\"urn:oasis:names:tc:xacml:1.0:policy\" xmlns:xs" +
			"i=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocat" +
			"ion=\"urn:oasis:names:tc:xacml:1.0:policy cs-xacml-schema-polic" +
			"y-01.xsd\">	<Target><Subjects><Subject><SubjectMatch MatchId" +
			"=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\"><Attrib" +
			"uteValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">T" +
			"estCaller</AttributeValue><SubjectAttributeDesignator Attribute" +
			"Id=\"linksmart:policy:subject:pid\" DataType=\"http://www.w3.org/20" +
			"01/XMLSchema#string\"/></SubjectMatch></Subject></Subjects><Res" +
			"ources><Resource><ResourceMatch MatchId=\"urn:oasis:names:tc:xa" +
			"cml:1.0:function:string-equal\"><AttributeValue DataType=\"http" +
			"://www.w3.org/2001/XMLSchema#string\">TestLinkSmartApplication:Marc" +
			"o</AttributeValue><ResourceAttributeDesignator AttributeId=\"hy" +
			"dra:policy:resource:pid\" DataType=\"http://www.w3.org/2001/XML" +
			"Schema#string\"/></ResourceMatch></Resource></Resources></Targe" +
			"t><Rule RuleId=\"urn:linksmart:1.0:policy:example:simple-access-pol" +
			"icy:simple-subject-resource-permit\" Effect=\"Permit\"><Target>" +
			"<Actions><Action><ActionMatch MatchId=\"urn:oasis:names:tc:xacm" +
			"l:1.0:function:string-equal\"><AttributeValue DataType=\"http:/" +
			"/www.w3.org/2001/XMLSchema#string\">hello</AttributeValue><Acti" +
			"onAttributeDesignator AttributeId=\"urn:oasis:names:tc:xacml:1." +
			"0:action:action-id\" DataType=\"http://www.w3.org/2001/XMLSchem" +
			"a#string\" /></ActionMatch><ActionMatch MatchId=\"urn:oasis:nam" +
			"es:tc:xacml:1.0:function:string-equal\"><AttributeValue DataTyp" +
			"e=\"http://www.w3.org/2001/XMLSchema#string\">Is it me you're l" +
			"ooking for?</AttributeValue><ActionAttributeDesignator Attribut" +
			"eId=\"linksmart:policy:action:arg0\" DataType=\"http://www.w3.org/2" +
			"001/XMLSchema#string\" /></ActionMatch></Action></Actions></Tar" +
			"get></Rule><Rule RuleId=\"urn:linksmart:1.0:policy:example:simple-a" +
			"ccess-policy:simple-default-deny\" Effect=\"Deny\"/><Obligation" +
			"s><Obligation ObligationId=\"urn:linksmart:1.0:policy:obligation:se" +
			"ndmessage\" FulfillOn=\"Permit\"><AttributeAssignment AttributeId=" +
			"\"urn:linksmart:1.0:policy:obligation:sendmessage:from\" DataType=\"ht" +
			"tp://www.w3.org/2001/XMLSchema#string\">m.tiemann@reading.ac.uk" +
			"</AttributeAssignment><AttributeAssignment AttributeId=\"urn:hy" +
			"dra:1.0:policy:obligation:sendmessage:to\" DataType=\"http://www.w" +
			"3.org/2001/XMLSchema#string\">marco.tiemann@gmail.com</Attribut" +
			"eAssignment><AttributeAssignment AttributeId=\"urn:linksmart:1.0:po" +
			"licy:obligation:sendmessage:subject\" DataType=\"http://www.w3.org" +
			"/2001/XMLSchema#string\">I am a LinkSmart obligation test email.</A" +
			"ttributeAssignment><AttributeAssignment AttributeId=\"urn:linksmart" +
			":1.0:policy:obligation:sendmessage:content\" DataType=\"http://www" +
			".w3.org/2001/XMLSchema#string\">I am a LinkSmart obligation test em" +
			"ail text.</AttributeAssignment></Obligation></Obligations></Pol" +
			"icy>";
	
	@Test
	public void testNormalisePolicy() {
		DocumentBuilderFactory docBuilderFactory 
				= DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringComments(true);
		docBuilderFactory.setValidating(false);
		try {
			DocumentBuilder documentBuilder 
					= docBuilderFactory.newDocumentBuilder();
			Document d = documentBuilder.parse(new InputSource(
					new StringReader(policy)));
			PolicyNormaliser.normalisePolicy(d);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception");
		}
	}

}
