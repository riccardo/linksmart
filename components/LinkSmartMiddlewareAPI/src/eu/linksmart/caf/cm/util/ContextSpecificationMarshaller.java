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
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.linksmart.caf.cm.specification.ContextSpecification;
import eu.linksmart.caf.cm.specification.Definition;
import eu.linksmart.caf.cm.specification.Member;

/**
 * Marshaller for encoding and decoding {@link ContextSpecification}s
 * @author Michael Crouch
 */
public class ContextSpecificationMarshaller {

	/**
	 * Encodes the {@link ContextSpecification} to the {@link PrintStream} 
	 * @param spec the {@link ContextSpecification}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeContextSpecification(ContextSpecification spec, PrintStream out) throws XmlMarshallingException {
		out.println("<ContextSpecification>");
		encodeDefinition(spec.getDefinition(), out);
		
		if (spec.getRuleSet() != null){
			ContextRuleMarshaller.encodeContextRuleSet(spec.getRuleSet(), out);
		}
		else {
			out.println("<ContextRuleSet/>");
		}
		
		if (spec.getRequirementsXml() != null){
			out.println("<XmlRequirements>");
			out.println(CmHelper.getCDATA(spec.getRequirementsXml().trim()));
			out.println("</XmlRequirements>");
		}
		
		out.println("</ContextSpecification>");		
	}
	
	/**
	 * Decodes the {@link ContextSpecification} from the {@link Node}
	 * @param root the {@link Node}
	 * @return the {@link ContextSpecification}
	 * @throws XmlMarshallingException
	 */
	public static ContextSpecification decodeContextSpecification(Node root) throws XmlMarshallingException {
		ContextSpecification spec = new ContextSpecification();
		spec.setRequirementsXml("");
		if (!root.getNodeName().equals("ContextSpecification"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'ContextSpecification', was '" + root.getNodeName() + "'" );
		
			
		NodeList list = root.getChildNodes();
		for (int i=0; i< list.getLength(); i++)
		{
			Node node = list.item(i);
			if (node.getNodeName().equals("Definition"))
			{
				spec.setDefinition(decodeDefinition(node));
			}
			else if (node.getNodeName().equals("ContextRuleSet"))
			{
				spec.setRuleSet(ContextRuleMarshaller.decodeContextRuleSet(node));
			}
			else if (node.getNodeName().equals("XmlRequirements"))
			{
				spec.setRequirementsXml(CmHelper.getElementValue(node));
			}
		}
		
		return spec;
	}
	
	/**
	 * Encodes the {@link Definition} to the {@link PrintStream}
	 * @param def the {@link Definition}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeDefinition(Definition def, PrintStream out) throws XmlMarshallingException {
		out.println("<Definition name=\"" + def.getName() + "\" author=\"" + def.getAuthor() + 
					"\" version=\"" + def.getVersion() + "\" appUri=\"" + def.getApplicationUri() + "\">");
					
		if (def.getMembers() != null)
		{	
			for (int j=0; j<def.getMembers().length; j++)
			{
				encodeMember(def.getMembers()[j], out);
			}
		}
		out.println("</Definition>");
	}
	
	/**
	 * Decodes the {@link Definition} from a {@link Node}
	 * @param root the {@link Node}
	 * @return the {@link Definition}
	 * @throws XmlMarshallingException
	 */
	public static Definition decodeDefinition(Node root) throws XmlMarshallingException {
		Definition def = new Definition();
		
		def.setMembers(new Member[0]);
			
		if (!root.getNodeName().equals("Definition"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'Definition', was '" + root.getNodeName() + "'" );
		
		def.setName(CmHelper.getAttributeValue("name", root));
		def.setAuthor(CmHelper.getAttributeValue("author", root));
		def.setApplicationUri(CmHelper.getAttributeValue("appUri", root));
		def.setVersion(CmHelper.getAttributeValue("version", root));
		NodeList list = root.getChildNodes();
		List<Member> memberList = new ArrayList<Member>();
		for (int i=0; i< list.getLength(); i++)
		{
			Node node = list.item(i);
			if (node.getNodeName().equals("Member"))
			{
				memberList.add(decodeMember(node));
			}
		}
		if (memberList.size() != 0)
			def.setMembers((Member[])memberList.toArray(new Member[memberList.size()]));
		return def;
	}
		
	
	/**
	 * Encodes the {@link Member} to the {@link PrintStream}
	 * @param member the {@link Member}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeMember(Member member, PrintStream out) throws XmlMarshallingException {
		if (member.getDefaultValue() == null)
			member.setDefaultValue("");
		
		out.println("<Member id=\"" + member.getId() + "\" dataType=\"" + member.getDataType() + "\" instanceOf=\"" 
				+ member.getInstanceOf() + "\">" + CmHelper.getCDATA(member.getDefaultValue()) + "</Member>");
	}
	
	/**
	 * Decodes the {@link Member} from the {@link Node}
	 * @param root the {@link Node}
	 * @return the {@link Member}
	 * @throws XmlMarshallingException
	 */
	public static Member decodeMember(Node root) throws XmlMarshallingException {
		if (!root.getNodeName().equals("Member"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'Member', was '" + root.getNodeName() + "'" );
		
		Member member = new Member();
		
		member.setId(CmHelper.getAttributeValue("id", root));
		member.setDataType(CmHelper.getAttributeValue("dataType", root));
		member.setInstanceOf(CmHelper.getAttributeValue("instanceOf", root));
		
		member.setDefaultValue(CmHelper.getElementValue(root));
		
		return member;
	}
	
	

}
