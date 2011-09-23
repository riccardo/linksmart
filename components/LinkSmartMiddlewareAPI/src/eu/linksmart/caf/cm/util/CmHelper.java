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
package eu.linksmart.caf.cm.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.daqc.subscription.Subscription;

/**
 * Helper class for dealing with Context Awareness Framework objects.<p>
 * 
 * @author Michael Crouch
 *
 */
public class CmHelper {

	/**
	 * Helper method that normalises from the given string,
	 * inserting a '_' in all areas of whitespace.
	 * Also makes everything lowercase.
	 * 
	 * "My Room" -> "my_room"
	 * @param str the String to normalise
	 * @return the normalised string
	 */
	public static String normalise(String str)
	{
		String[] temp = str.split(" ");
		System.out.println(temp.length);
		String outStr = "";
		for (int i=0; i<temp.length; i++)
		{
			String bit = temp[i];
			if (!bit.equals(""))
			{
				if ((outStr.length() > 0) && (!outStr.endsWith("_")))
					outStr += "_";
				
					outStr += bit;
			
			}
		}
		return outStr.toLowerCase();
	}
	
	/**
	 * Returns the given string wrapped in XML CDATA notation
	 * @param str the string to wrap
	 * @return the wrapped string
	 */
	public static String getCDATA(String str){
		return "<![CDATA[" + str + "]]>";
	}
	
	/**
	 * Returns the named attribute's value from the given {@link Node}
	 * 
	 * @param attrName the name of the attribute to get the value of
	 * @param node the {@link Node}
	 * @return the attribute value
	 * @throws XmlMarshallingException
	 */
	public static String getAttributeValue(String attrName, Node node) throws XmlMarshallingException {
		NamedNodeMap attrs = node.getAttributes();
		try
		{			
			return attrs.getNamedItem(attrName).getNodeValue();
		}
		catch (Exception e) {
			throw new XmlMarshallingException("Error retrieving attribute '" + attrName + "' from node '" + node.getNodeName());
		}
	}
	
	/**
	 * Helper method for creating an {@link Attribute}
	 * @param id the id for the created {@link Attribute}
	 * @param value the value for the created {@link Attribute}
	 * @return the created {@link Attribute}
	 */
	public static Attribute createAttribute(String id, String value){
		Attribute attr = new Attribute();
		attr.setId(id);
		attr.setValue(value);
		return attr;
	}
	
	/**
	 * Helper method to create a {@link Parameter}
	 * @param name the name for the {@link Parameter}
	 * @param type the type for the {@link Parameter}
	 * @param value the value for the {@link Parameter}
	 * @return the created {@link Parameter}
	 */
	public static Parameter createParameter(String name, String type, String value){
		Parameter param = new Parameter();
		param.setName(name);
		param.setType(type);
		param.setValue(value);
		return param;
	}
	
	/**
	 * Helper method to create a {@link Subscription}
	 * @param dataId the dataId
	 * @param protocol the protocol
	 * @param parameters the array of {@link Parameter}s
	 * @param attributes the array of {@link Attribute}s
	 * @return the {@link Subscription}
	 */
	public static Subscription createSubscription(String dataId, String protocol, Parameter[] parameters, Attribute[] attributes){
		Subscription sub = new Subscription();
		sub.setDataId(dataId);
		sub.setProtocol(protocol);
		sub.setParameters(parameters);
		sub.setAttributes(attributes);
		return sub;
	}
	
	/**
	 * Returns the value of the given element from the {@link Node}
	 * @param node the {@link Node}
	 * @return the value
	 * @throws XmlMarshallingException
	 */
	public static String getElementValue(Node node) throws XmlMarshallingException {
		try
		{
			Element element = (Element)node;
			return element.getTextContent();
		}
		catch (Exception e) {
			throw new XmlMarshallingException("Error retrieving element value of node '" + node.getNodeName());
		}
	}
	
	/**
	 * Parses the String XML into a XML {@link Document} object
	 * @param xml the xml to parse
	 * @return the {@link Document}
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document getXmlStringAsDocument(String xml) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilder docBuilder;
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
	
		docBuilder = docBuilderFactory.newDocumentBuilder();
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes("UTF8"));
		return docBuilder.parse(bais);
	}
	

	/**
	 * Returns the {@link Attribute} value with the given id from the array
	 * @param id the id 
	 * @param attrs the array of {@link Attribute}
	 * @return the value
	 */
	public static String getAttributeValueWithId(String id, Attribute[] attrs){
		for (Attribute attr : attrs)
		{
			if (attr.getId().equalsIgnoreCase(id))
				return attr.getValue();
		}
		return null;
	}
	
}


