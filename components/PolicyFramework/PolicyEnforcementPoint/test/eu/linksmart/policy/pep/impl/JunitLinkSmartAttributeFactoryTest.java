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

import static org.junit.Assert.*;

import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.xacml.attr.AttributeProxy;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.StringAttribute;

import eu.linksmart.policy.pep.impl.LinkSmartAttributeFactory;

/**
 * Unit test for {@link LinkSmartAttributeFactory}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitLinkSmartAttributeFactoryTest {

	@Test
	public void testLinkSmartAttributeFactory() {
		assertNotNull(new LinkSmartAttributeFactory());
	}

	@Test
	public void testAddDatatype() {
		LinkSmartAttributeFactory haf = new LinkSmartAttributeFactory();
		AttributeProxy atroxy = new AttributeProxy() {			
			@Override
			public AttributeValue getInstance(String theName) throws Exception {
				return new StringAttribute(theName);
			}			
			@Override
			public AttributeValue getInstance(Node theNode) throws Exception {
				return new StringAttribute(theNode.getNodeValue());
			}
		};
		haf.addDatatype("xs:magic", atroxy);
	}

	@Test
	public void testGetSupportedDatatypes() {
		LinkSmartAttributeFactory haf = new LinkSmartAttributeFactory();
		assertNotNull(haf.getSupportedDatatypes());
	}

	@Test
	public void testCreateValueNodeStringDataType() {
		LinkSmartAttributeFactory haf = new LinkSmartAttributeFactory();
	    try{
	        DocumentBuilderFactory factory 
	        		= DocumentBuilderFactory.newInstance();
	        DocumentBuilder parser = factory.newDocumentBuilder();
	        Document doc = parser.newDocument();
	        Element root = doc.createElement("root");
	        root.setAttribute("DataType", "string");
	        doc.appendChild(root);
	        AttributeValue av = haf.createValue(doc.getFirstChild());
	        assertTrue(av instanceof StringAttribute);
	    } catch(Exception e){
	          e.printStackTrace();
	          fail("Exception");
	   }
	}

	@Test
	public void testCreateValueNodeURI() {
		LinkSmartAttributeFactory haf = new LinkSmartAttributeFactory();
	    try{
	        DocumentBuilderFactory factory 
	        		= DocumentBuilderFactory.newInstance();
	        DocumentBuilder parser = factory.newDocumentBuilder();
	        Document doc = parser.newDocument();
	        Element root = doc.createElement("root");
	        doc.appendChild(root);
	        AttributeValue av = haf.createValue(doc.getFirstChild(), 
	        		new URI("string"));
	        assertTrue(av instanceof StringAttribute);
	    } catch(Exception e){
	          e.printStackTrace();
	          fail("Exception");
	   }
	}

	@Test
	public void testCreateValueNodeString() {
		LinkSmartAttributeFactory haf = new LinkSmartAttributeFactory();
	    try{
	        DocumentBuilderFactory factory 
	        		= DocumentBuilderFactory.newInstance();
	        DocumentBuilder parser = factory.newDocumentBuilder();
	        Document doc = parser.newDocument();
	        Element root = doc.createElement("root");
	        doc.appendChild(root);
	        AttributeValue av = haf.createValue(doc.getFirstChild(), "string");
	        assertTrue(av instanceof StringAttribute);
	    } catch(Exception e){
	          e.printStackTrace();
	          fail("Exception");
	   }
	}

	@Test
	public void testCreateValueURIString() {
		LinkSmartAttributeFactory haf = new LinkSmartAttributeFactory();
	    try{
	        DocumentBuilderFactory factory 
	        		= DocumentBuilderFactory.newInstance();
	        DocumentBuilder parser = factory.newDocumentBuilder();
	        Document doc = parser.newDocument();
	        Element root = doc.createElement("root");
	        doc.appendChild(root);
	        AttributeValue av = haf.createValue(new URI("string"), "tadaa");
	        assertTrue(av instanceof StringAttribute);
	    } catch(Exception e){
	          e.printStackTrace();
	          fail("Exception");
	   }
	}

}
