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

import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.rules.Action;
import eu.linksmart.caf.cm.rules.ContextRuleSet;
import eu.linksmart.caf.cm.rules.DeclaredFunction;
import eu.linksmart.caf.cm.rules.DeclaredType;
import eu.linksmart.caf.cm.rules.Rule;

/**
 * Marshaller for encoding and decoding the {@link ContextRuleSet}, and its sub-objects
 * @author Michael Crouch
 *
 */
public class ContextRuleMarshaller {
	
	/**
	 * Encodes the {@link ContextRuleSet} to the {@link PrintStream}
	 * @param ruleSet the {@link ContextRuleSet}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeContextRuleSet(ContextRuleSet ruleSet, PrintStream out) throws XmlMarshallingException {
		out.println("<ContextRuleSet>");
		if (ruleSet.getImports() != null)
		{
			out.println("<Imports>");
			for (int i=0; i<ruleSet.getImports().length; i++)
			{
				out.println("<Import>" + ruleSet.getImports()[i] + "</Import>");
			}
			out.println("</Imports>");
		}
		else
		{
			out.println("<Imports/>");
		}
		
		if (ruleSet.getFunctions() != null)
		{
			out.println("<Functions>");
			for (DeclaredFunction func : ruleSet.getFunctions())
			{
				ContextMetadataMarshaller.encodeDeclaredFunction(func, out);
			}
			out.println("</Functions>");
		}
		else
		{
			out.println("<Functions/>");
		}
		
		if (ruleSet.getFunctions() != null)
		{
			out.println("<Types>");
			for (DeclaredType type : ruleSet.getTypes())
			{
				ContextMetadataMarshaller.encodeDeclaredType(type, out);
			}
			out.println("</Types>");
		}
		else
		{
			out.println("<Types/>");
		}
		
		out.println("<Rules>");
		if (ruleSet.getRules() != null)
		{
			for(int j=0; j< ruleSet.getRules().length; j++)
			{
				encodeRule(ruleSet.getRules()[j], out);
			}
		}
		out.println("</Rules>");
		out.println("</ContextRuleSet>");
	}
	
	/**
	 * Decodes the {@link ContextRuleSet} from the {@link Node}
	 * @param root the {@link Node}
	 * @return the {@link ContextRuleSet}
	 * @throws XmlMarshallingException
	 */
	public static ContextRuleSet decodeContextRuleSet(Node root) throws XmlMarshallingException {
		if (!root.getNodeName().equals("ContextRuleSet"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'ContextRuleSet', was '" + root.getNodeName() + "'" );
		ContextRuleSet ruleSet = new ContextRuleSet();
		ruleSet.setRules(new Rule[0]);
		ruleSet.setImports(new String[0]);
		ruleSet.setFunctions(new DeclaredFunction[0]);
		ruleSet.setTypes(new DeclaredType[0]);
		
		NodeList list = root.getChildNodes();
		for (int i=0; i< list.getLength(); i++)
		{
			Node node = list.item(i);
			if (node.getNodeName().equals("Functions"))
			{
				NodeList funcList = node.getChildNodes();
				List<DeclaredFunction> functions = new ArrayList<DeclaredFunction>();
				for (int k=0; k< funcList.getLength(); k++)
				{
					Node funcNode = funcList.item(k);
					if (funcNode.getNodeName().equals("DeclaredFunction"))
					{
						functions.add(ContextMetadataMarshaller.decodeDeclaredFunction(funcNode));
					}
				}
				if (functions.size() != 0)
					ruleSet.setFunctions((DeclaredFunction[])functions.toArray(new DeclaredFunction[functions.size()]));				
			}
			else if (node.getNodeName().equals("Imports"))
			{
				NodeList importList = node.getChildNodes();
				List<String> imports = new ArrayList<String>();
				for (int k=0; k< importList.getLength(); k++)
				{
					Node importNode = importList.item(k);
					if (importNode.getNodeName().equals("Import"))
					{
						imports.add(CmHelper.getElementValue(importNode));
					}
				}
				if (imports.size() != 0)
					ruleSet.setImports((String[])imports.toArray(new String[imports.size()]));	
			}
			else if (node.getNodeName().equals("Types"))
			{
				NodeList typeList = node.getChildNodes();
				List<DeclaredType> types = new ArrayList<DeclaredType>();
				for (int k=0; k< typeList.getLength(); k++)
				{
					Node typeNode = typeList.item(k);
					if (typeNode.getNodeName().equals("DeclaredType"))
					{
						types.add(ContextMetadataMarshaller.decodeDeclaredType(typeNode));
					}
				}
				if (types.size() != 0)
					ruleSet.setTypes((DeclaredType[])types.toArray(new DeclaredType[types.size()]));						
			}
			else if (node.getNodeName().equals("Rules"))
			{
				NodeList ruleList = node.getChildNodes();
				List<Rule> rules = new ArrayList<Rule>();
				for (int k=0; k< ruleList.getLength(); k++)
				{
					Node ruleNode = ruleList.item(k);
					if (ruleNode.getNodeName().equals("Rule"))
					{
						rules.add(decodeRule(ruleNode));
					}
				}
				if (rules.size() != 0)
					ruleSet.setRules((Rule[])rules.toArray(new Rule[rules.size()]));
			}
		}
		return ruleSet;
	}
	
	/**
	 * Encodes the {@link Rule} to the {@link PrintStream}
	 * @param rule the {@link Rule}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeRule(Rule rule, PrintStream out) throws XmlMarshallingException {
		out.println("<Rule ruleId=\"" + rule.getRuleId() + "\">");
		out.println("<RuleAttributes>");
		if (rule.getRuleAttributes() != null)
		{
			CoreObjectMarshaller.encodeAttributeList(rule.getRuleAttributes(), out);
		}
		out.println("</RuleAttributes>");
		out.print("<WhenClause>");
		out.print(CmHelper.getCDATA(rule.getWhenClause()));
		out.println("</WhenClause>");
		out.println("<ThenActions>");
		if (rule.getActions() != null)
		{
			for(int j=0; j< rule.getActions().length; j++)
			{
				encodeAction(rule.getActions()[j], out);
			}
		}
		out.println("</ThenActions>");
		out.println("</Rule>");
	}
	
	/**
	 * Decodes the {@link Rule} from the {@link Node}
	 * 
	 * @param root the {@link Node}
	 * @return the {@link Rule}
	 * @throws XmlMarshallingException
	 */
	public static Rule decodeRule(Node root) throws XmlMarshallingException {
		if (!root.getNodeName().equals("Rule"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'Rule', was '" + root.getNodeName() + "'" );
		Rule rule = new Rule();
		rule.setActions(new Action[0]);
		rule.setRuleAttributes(new Attribute[0]);
		rule.setWhenClause("");
		
		rule.setRuleId(CmHelper.getAttributeValue("ruleId", root));
		
		NodeList list = root.getChildNodes();
		for (int i=0; i< list.getLength(); i++)
		{
			Node node = list.item(i);
			if (node.getNodeName().equals("RuleAttributes"))
			{
				rule.setRuleAttributes(CoreObjectMarshaller.decodeAttributeList(node));
			}
			else if (node.getNodeName().equals("WhenClause"))
			{
				rule.setWhenClause(CmHelper.getElementValue(node));
			}
			else if (node.getNodeName().equals("ThenActions"))
			{
				NodeList actionNodeList = node.getChildNodes();
				List<Action> actionList = new ArrayList<Action>();
				for (int k=0; k< actionNodeList.getLength(); k++)
				{
					Node actionNode = actionNodeList.item(k);
					if (actionNode.getNodeName().equals("Action"))
					{
						actionList.add(decodeAction(actionNode));
					}
				}
				if (actionList.size() != 0)
					rule.setActions((Action[])actionList.toArray(new Action[actionList.size()]));
			}
		}
		return rule;
	}
	
	/**
	 * Encodes the {@link Action} to the {@link PrintStream}
	 * @param action the {@link Action}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeAction(Action action, PrintStream out) throws XmlMarshallingException {
		out.println("<Action id=\"" + action.getId() + "\">");
		out.println("<Attributes>");
		if (action.getAttributes() != null)
		{
			CoreObjectMarshaller.encodeAttributeList(action.getAttributes(), out);
		}
		out.println("</Attributes>");
		out.println("<Parameters>");
		if (action.getParameters() != null)
		{
			CoreObjectMarshaller.encodeParameterList(action.getParameters(), out);
		}
		out.println("</Parameters>");
		out.println("</Action>");
	}
	
	/**
	 * Decodes the {@link Action} from the {@link Node}
	 * 
	 * @param root the {@link Node}
	 * @return the {@link Action}
	 * @throws XmlMarshallingException
	 */
	public static Action decodeAction(Node root) throws XmlMarshallingException {
		if (!root.getNodeName().equals("Action"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'Action', was '" + root.getNodeName() + "'" );
		Action action = new Action();
		action.setAttributes(new Attribute[0]);
		action.setParameters(new Parameter[0]);
		action.setId(CmHelper.getAttributeValue("id", root));
		
		NodeList list = root.getChildNodes();
		for (int i=0; i< list.getLength(); i++)
		{
			Node node = list.item(i);
			if (node.getNodeName().equals("Attributes"))
			{
				action.setAttributes(CoreObjectMarshaller.decodeAttributeList(node));
			}
			else if (node.getNodeName().equals("Parameters"))
			{
				action.setParameters(CoreObjectMarshaller.decodeParameterList(node));
			}
		}
		return action;
	}
	
	
}
