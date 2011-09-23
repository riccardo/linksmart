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

import java.io.PrintStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.cm.rules.DeclaredFunction;
import eu.linksmart.caf.cm.rules.DeclaredType;

/**
 * Marshaller for encoding and decoding {@link DeclaredFunction} and {@link DeclaredType}
 * objects to and from XML
 * @author Michael Cruoch
 *
 */
public class ContextMetadataMarshaller {
	
		
	/**
	 * Encodes the {@link DeclaredFunction} to the {@link PrintStream}
	 * @param func the {@link DeclaredFunction}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeDeclaredFunction(DeclaredFunction func, PrintStream out) throws XmlMarshallingException {
		out.println("<DeclaredFunction name=\"" + func.getName() + "\" returnType=\"" + func.getReturnType() + "\">");
		out.println("<Arguments>");
		CoreObjectMarshaller.encodeAttributeList(func.getArguments(), out);
		out.println("</Arguments>");
		out.println("<Code>" + CmHelper.getCDATA(func.getCode()) + "</Code>");
		out.println("</DeclaredFunction>");
	}
	
	/**
	 * Decodes the {@link DeclaredFunction} from the {@link Node}
	 * @param root the {@link Node}
	 * @return the {@link DeclaredFunction}
	 * @throws XmlMarshallingException
	 */
	public static DeclaredFunction decodeDeclaredFunction(Node root) throws XmlMarshallingException {
		if (!root.getNodeName().equals("DeclaredFunction"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'DeclaredFunction', was '" + root.getNodeName() + "'" );
		
		DeclaredFunction declaredFunction = new DeclaredFunction();
		
		declaredFunction.setName(CmHelper.getAttributeValue("name", root));
		declaredFunction.setReturnType(CmHelper.getAttributeValue("returnType", root));
		declaredFunction.setArguments(new Attribute[0]);
		
		NodeList list = root.getChildNodes();
		for (int i=0; i< list.getLength(); i++)
		{
			Node node = list.item(i);
			if (node.getNodeName().equals("Arguments"))
			{
				declaredFunction.setArguments(CoreObjectMarshaller.decodeAttributeList(node));
			}
			else if (node.getNodeName().equals("Code"))
			{
				declaredFunction.setCode(CmHelper.getElementValue(node));
			}
		}
		return declaredFunction;
	}
	
	/**
	 * Encodes the {@link DeclaredType} to the {@link PrintStream}
	 * @param type the {@link DeclaredType}]
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeDeclaredType(DeclaredType type, PrintStream out) throws XmlMarshallingException {
		out.println("<DeclaredType name=\"" + type.getName() + "\" factRole=\"" + type.getFactRole() + "\">");
		out.println("<MetaAttributes>");
		CoreObjectMarshaller.encodeAttributeList(type.getMetaAttributes(), out);
		out.println("</MetaAttributes>");
		out.println("<ClassMembers>");
		CoreObjectMarshaller.encodeAttributeList(type.getClassMembers(), out);
		out.println("</ClassMembers>");
		out.println("</DeclaredType>");
	}
	
	/**
	 * Decodes the {@link Node} to a {@link DeclaredType}
	 * @param root the {@link Node}
	 * @return the {@link DeclaredType}
	 * @throws XmlMarshallingException
	 */
	public static DeclaredType decodeDeclaredType(Node root) throws XmlMarshallingException {
		if (!root.getNodeName().equals("DeclaredType"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'DeclaredType', was '" + root.getNodeName() + "'" );
		
		DeclaredType declaredType = new DeclaredType();
		declaredType.setClassMembers(new Attribute[0]);
		declaredType.setMetaAttributes(new Attribute[0]);
		declaredType.setName(CmHelper.getAttributeValue("name", root));
		declaredType.setFactRole(CmHelper.getAttributeValue("factRole", root));
		
		NodeList list = root.getChildNodes();
		for (int i=0; i< list.getLength(); i++)
		{
			Node node = list.item(i);
			if (node.getNodeName().equals("MetaAttributes"))
			{
				declaredType.setMetaAttributes(CoreObjectMarshaller.decodeAttributeList(node));
			}
			else if (node.getNodeName().equals("ClassMembers"))
			{
				declaredType.setClassMembers(CoreObjectMarshaller.decodeAttributeList(node));
			}
		}
		return declaredType;
	}
}
