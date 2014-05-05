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
package eu.linksmart.policy.pdp.ext.function.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

import junit.framework.TestCase;

import org.junit.Test;

import com.sun.xacml.BasicEvaluationCtx;
import com.sun.xacml.ParsingException;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Subject;

import eu.linksmart.policy.pdp.ext.function.impl.XPathFunctions;
import eu.linksmart.policy.pdp.impl.PdpXacmlConstants;

/**
 * Unit test for {@link XPathFunctions}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitXPathFunctionsTest extends TestCase {

	@Test
	public void testXPathFunctions() {
		assertNotNull(new XPathFunctions("", "", false));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEvaluate() {
		String id = StringAttribute.identifier;
		int lastHash = id.lastIndexOf("#");
		int lastColon = id.lastIndexOf(":");
		int index = lastColon;
		if (lastHash > lastColon) {
			index = lastHash;
		}
		id = id.substring(index + 1);
		XPathFunctions xpathMagic = new XPathFunctions(
				StringAttribute.identifier, 
					PdpXacmlConstants.FUNCTION_LINK_SMART_PREFIX.getUrn() + id 
					+ "-xpath-value", false);
		try {
			Subject subject = new Subject(new HashSet());
			HashSet subjects = new HashSet();
			subjects.add(subject);
			HashSet resources = new HashSet();
			try {
				Attribute resource = new Attribute(new URI(
						"urn:oasis:names:tc:xacml:1.0:resource:resource-id"), "", 
						new DateTimeAttribute(), new StringAttribute("test"));
				resources.add(resource);
			} catch (URISyntaxException use) {
				use.printStackTrace();
				fail("Exception");
			}
			subjects.add(subject);
			RequestCtx req = new RequestCtx(subjects, resources, 
					new HashSet(), new HashSet());
			ArrayList<AttributeValue> args = new ArrayList<AttributeValue>();
			args.add(new StringAttribute("<?xml version='1.0'?><EXAMPLE><CUSTOMER id=\"1\" type" +
					"=\"B\">Mr.  Jones</CUSTOMER><CUSTOMER id=\"2\" type=\"C\"" + 
					">Mr.  Johnson</CUSTOMER></EXAMPLE>"));
			args.add(new StringAttribute("//EXAMPLE/CUSTOMER[@id='2' or @type='C']"));
			xpathMagic.evaluate(args, new BasicEvaluationCtx(req));
		} catch (ParsingException pe) {
			pe.printStackTrace();
			fail("Exception");
		}
	}

	@Test
	public void testGetSingleFunction() {
		assertNotNull(XPathFunctions.getSingleFunction(
				StringAttribute.identifier));
	}

	@Test
	public void testGetBagFunction() {
		assertNotNull(XPathFunctions.getBagFunction(
				StringAttribute.identifier));
	}
	
}
