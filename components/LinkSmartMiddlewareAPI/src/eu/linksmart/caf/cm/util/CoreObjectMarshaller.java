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

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.Parameter;

/**
 * Marshaller for encoding an decoding core Context Awareness Framework objects, such
 * as {@link Attribute} and {@link Parameter}.<p>
 * Also provide helper method for parsing an {@link InputStream} content to {@link Document}
 * @author Michael Crouch
 *
 */
public class CoreObjectMarshaller {

	/**
	 * Encodes the {@link Parameter} to the {@link PrintStream}
	 * @param param the {@link Parameter}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeParameter(Parameter param, PrintStream out) throws XmlMarshallingException {
		out.println("<Parameter name=\"" + param.getName()+ "\" type=\"" + param.getType() + "\">" 
				+ CmHelper.getCDATA(param.getValue()) + "</Parameter>");
	}
	
	/**
	 * Decodes a {@link Parameter} from the {@link Node}
	 * @param root the {@link Node}
	 * @return the {@link Parameter}
	 * @throws XmlMarshallingException
	 */
	public static Parameter decodeParameter(Node root) throws XmlMarshallingException {
		if (!root.getNodeName().equals("Parameter"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'Parameter', was '" + root.getNodeName() + "'" );
		Parameter param = new Parameter();
		param.setName(CmHelper.getAttributeValue("name", root));
		param.setType(CmHelper.getAttributeValue("type", root));
		param.setValue(CmHelper.getElementValue(root));
		return param;
	}
	
	/**
	 * Encodes an array of {@link Parameter}s to the {@link PrintStream}
	 * @param params the array of {@link Parameter}s
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeParameterList(Parameter[] params, PrintStream out) throws XmlMarshallingException{
		if (params != null)
		{
			for( Parameter param : params)
			{
				encodeParameter(param, out);
			}
		}
	}
	
	/**
	 * Decodes all {@link Parameter} children of the {@link Node}
	 * @param root the {@link Node}
	 * @return array of {@link Parameter}s
	 * @throws XmlMarshallingException
	 */
	public static Parameter[] decodeParameterList(Node root) throws XmlMarshallingException{
		NodeList nodeList = root.getChildNodes();
		List<Parameter> paramList = new ArrayList<Parameter>();
		for (int k=0; k< nodeList.getLength(); k++)
		{
			Node paramNode = nodeList.item(k);
			if (paramNode.getNodeName().equals("Parameter"))
				paramList.add(decodeParameter(paramNode));
		}
		if (paramList.size() != 0)
			return (Parameter[])paramList.toArray(new Parameter[paramList.size()]);
		return new Parameter[0];
	}
	
	/**
	 * Encodes the {@link Attribute} to the {@link PrintStream}
	 * @param attr the {@link Attribute}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeAttribute(Attribute attr, PrintStream out) throws XmlMarshallingException {
		out.println("<Attribute id=\"" + attr.getId()+ "\">" + CmHelper.getCDATA(attr.getValue()) + "</Attribute>");
	}
	
	/**
	 * Decodes the {@link Attribute} from the {@link Node}
	 * @param root the {@link Node}
	 * @return the {@link Attribute}
	 * @throws XmlMarshallingException
	 */
	public static Attribute decodeAttribute(Node root) throws XmlMarshallingException {
		if (!root.getNodeName().equals("Attribute"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'Attribute', was '" + root.getNodeName() + "'" );
		
		Attribute attr = new Attribute();
		attr.setId(CmHelper.getAttributeValue("id", root));
		attr.setValue(CmHelper.getElementValue(root));
		
		return attr;
	}
	
	/**
	 * Encodes the array of {@link Attribute}s to the {@link PrintStream}
	 * @param attrs the array of {@link Attribute}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeAttributeList(Attribute[] attrs, PrintStream out) throws XmlMarshallingException{
		if (attrs != null)
		{
			for( Attribute attr : attrs)
			{
				encodeAttribute(attr, out);
			}
		}
	}
	
	/**
	 * Decodes all {@link Attribute} children of the {@link Node}
	 * @param root the {@link Node}
	 * @return array of {@link Attribute}s
	 * @throws XmlMarshallingException
	 */
	public static Attribute[] decodeAttributeList(Node root) throws XmlMarshallingException{
		NodeList nodeList = root.getChildNodes();
		List<Attribute> attrList = new ArrayList<Attribute>();
		for (int k=0; k< nodeList.getLength(); k++)
		{
			Node attrNode = nodeList.item(k);
			if (attrNode.getNodeName().equals("Attribute"))
				attrList.add(decodeAttribute(attrNode));
		}
		if (attrList.size() != 0)
			return (Attribute[])attrList.toArray(new Attribute[attrList.size()]);
		return new Attribute[0];
	}
	
	/**
	 * Parses the input from the {@link InputStream}, into a {@link Document}
	 * @param input the {@link InputStream}
	 * @return the {@link Document}
	 */
	public static Document getInputStreamAsDocument(InputStream input){
		DocumentBuilder docBuilder;
		Document doc = null;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		try
		{
			docBuilder = docBuilderFactory.newDocumentBuilder();
			
		}
		catch (Exception e) {
			System.err.println("Error getting DocumentBuilder from DocumentBuilderFactory");
			return null;
		}
		
		try
		{
			doc = docBuilder.parse(input);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return doc;
	}
}
