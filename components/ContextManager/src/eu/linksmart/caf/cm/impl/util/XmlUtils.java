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
package eu.linksmart.caf.cm.impl.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.linksmart.caf.cm.util.XmlMarshallingException;

/**
 * Collection of XML helper methods
 * 
 * @author Michael Crouch
 * 
 */
public class XmlUtils {

	/**
	 * Singleton Constructor
	 */
	private XmlUtils() {
	};

	/**
	 * Gets the text value result of the XPath query
	 * 
	 * @param node
	 *            the {@link Node}
	 * @param expr
	 *            the XPath expression
	 * @return the value
	 */
	public static String getValueOfXpath(Node node, String expr) {
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression compExpr = xpath.compile(expr);
			String result =
					(String) compExpr.evaluate(node, XPathConstants.STRING);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	/**
	 * Gets the {@link NodeList} returned by the Xpath query
	 * 
	 * @param node
	 *            the {@link Node}
	 * @param expr
	 *            the Xpath query
	 * @return the {@link NodeList}
	 */
	public static NodeList getNodesXpath(Node node, String expr) {
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression compExpr = xpath.compile(expr);

			return (NodeList) compExpr.evaluate(node, XPathConstants.NODESET);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the value of the {@link Element}, under the given {@link Element},
	 * with the given Tag name
	 * 
	 * @param theElement
	 *            the root {@link Element}
	 * @param tagName
	 *            the Tag Name to find value of
	 * @return the value
	 */
	public static String getElementValueFromTagName(Element theElement,
			String tagName) {
		NodeList list = theElement.getElementsByTagName(tagName);
		if (list.getLength() > 0)
			return list.item(0).getTextContent();
		return "";
	}

	/**
	 * Parses the String XML into a XML {@link Document} object
	 * 
	 * @param xml
	 *            the xml to parse
	 * @return the {@link Document}
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document getXmlStringAsDocument(String xml)
			throws ParserConfigurationException, SAXException, IOException {

		ByteArrayInputStream bais =
				new ByteArrayInputStream(xml.getBytes("UTF8"));
		return getInputStreamAsDocument(bais);
	}

	/**
	 * Gets the {@link InputStream} XML as a {@link Document}
	 * 
	 * @param is
	 *            the {@link InputStream}
	 * @return the {@link Document}
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document getInputStreamAsDocument(InputStream is)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder docBuilder;

		DocumentBuilderFactory docBuilderFactory =
				DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);

		docBuilder = docBuilderFactory.newDocumentBuilder();
		return docBuilder.parse(is);
	}

	/**
	 * Returns the named attribute's value from the given {@link Node}
	 * 
	 * @param attrName
	 *            the name of the attribute to get the value of
	 * @param node
	 *            the {@link Node}
	 * @return the attribute value
	 * @throws XmlMarshallingException
	 */
	public static String getAttributeValue(String attrName, Node node) {
		NamedNodeMap attrs = node.getAttributes();
		try {
			return attrs.getNamedItem(attrName).getNodeValue();
		} catch (Exception e) {
			return "";
		}
	}
}
