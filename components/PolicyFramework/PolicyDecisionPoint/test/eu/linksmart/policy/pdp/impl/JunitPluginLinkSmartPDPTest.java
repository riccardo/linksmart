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

import junit.framework.TestCase;

import org.junit.Test;

import eu.linksmart.policy.pdp.impl.PdpDecisionConfig;
import eu.linksmart.policy.pdp.impl.PluginLinkSmartPDP;

/**
 * Unit test for {@link PluginLinkSmartPDP}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitPluginLinkSmartPDPTest extends TestCase {

	@Test
	public void testPluginLinkSmartPDP() {
		assertNotNull(getInstance());
	}

	@Test
	public void testGetSessionLifetime() {
		assertTrue(getInstance().getSessionLifetime() == 30000l);
	}

	@Test
	public void testSetSessionLifetime() {
		PluginLinkSmartPDP pPdp = getInstance();
		assertTrue(pPdp.getSessionLifetime() == 30000l);
		pPdp.setSessionLifetime(60000l);
		assertTrue(pPdp.getSessionLifetime() == 60000l);
	}

	@Test
	public void testIsAllowDefaultCaching() {
		assertTrue(getInstance().isAllowDefaultCaching() == false);
	}

	@Test
	public void testSetAllowDefaultCaching() {
		PluginLinkSmartPDP pPdp = getInstance();
		assertTrue(pPdp.isAllowDefaultCaching() == false);
		pPdp.setAllowDefaultCaching(true);
		assertTrue(pPdp.isAllowDefaultCaching());
	}

	@Test
	public void testEvaluateString() {
		PluginLinkSmartPDP pPdp = getInstance();
		pPdp.setAllowDefaultCaching(true);
		String theReqXml = "";
		String result = pPdp.evaluate(theReqXml);
		assertNotNull(result);
		assertTrue(result.contains("Error parsing Request"));
		theReqXml =	"<Request>"
			+ "<Subject SubjectCategory=\"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\">"
			+ "<Attribute AttributeId=\"linksmart:policy:subject:c\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>DE</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:subject:sid\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>TestCallerSID</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:subject:dn\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>eu.linksmart.security</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>0.0.0.772150907506484044</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:subject:pseudonym\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>linksmartPseudonym</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:subject:sem:instance\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>ser:G1DevPhone</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:subject:cn\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>LinkSmarttest</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:subject:sem:rdf:type\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>ser:G1DevPhone</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:subject:desc\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>TestCallerDesc</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:subject:pid\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>TestCaller</AttributeValue></Attribute>"
			+ "</Subject>"
			+ "<Resource>"
			+ "<Attribute AttributeId=\"linksmart:policy:resource:pseudonym\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>linksmartPseudonym</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:resource:sid\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>UR:TestLinkSmartApplication</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:resource:pid\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>TestLinkSmartApplication</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:resource:c\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>DE</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:resource:desc\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>TestLinkSmartApplication</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:resource:cn\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>LinkSmarttest</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"linksmart:policy:resource:dn\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>eu.linksmart.security</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>0.0.0.2812809247839936688</AttributeValue></Attribute>"
			+ "</Resource>"
			+ "<Action>"
			+ "<Attribute AttributeId=\"linksmart:policy:action:arg0\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>testing</AttributeValue></Attribute>"
			+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" "
			+ "IssueInstant=\"2010-05-11T12:11:54.982000000+01:00\"><AttributeValue>hello</AttributeValue></Attribute>"
			+ "</Action>" + "</Request>";
		result = pPdp.evaluate(theReqXml);
		assertTrue(result.contains("NotApplicable"));
	}

	@Test
	public void testEvaluateRequestCtx() {
		// already tested implicitly above
	}

	@Test
	public void testEvaluateEvaluationCtx() {
		// already tested implicitly above
	}

	private static PluginLinkSmartPDP getInstance() {
		PdpDecisionConfig pdpConfig = new PdpDecisionConfig();
		return new PluginLinkSmartPDP(pdpConfig);
	}

}
