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
import eu.linksmart.caf.cm.query.ContextQuery;
import eu.linksmart.caf.cm.query.QuerySet;
import eu.linksmart.caf.cm.rules.DeclaredFunction;

/**
 * Marshaller for encoding and decoding {@link QuerySet}s
 * @author Michael Crouch
 *
 */
public class QueryMarshaller {

	/**
	 * Encodes the {@link QuerySet} to the {@link PrintStream}
	 * @param querySet the {@link QuerySet}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeQuerySet(QuerySet querySet, PrintStream out) throws XmlMarshallingException {
		out.println("<QuerySet pkgName=\"" + querySet.getPackageName() + "\">");
		if (querySet.getImports() != null)
		{
			out.println("<Imports>");
			for (String str : querySet.getImports())
			{
				out.println("<Import>" + str + "</Import>");
			}
			out.println("</Imports>");
		}
		else
		{
			out.println("<Imports/>");
		}
		out.println("<Functions>");
		if (querySet.getFunctions() != null)
		{
			for (DeclaredFunction func : querySet.getFunctions())
			{
				ContextMetadataMarshaller.encodeDeclaredFunction(func, out);
			}
		}
		out.println("</Functions>");
		
		out.println("<Queries>");
		if (querySet.getQueries() != null)
		{
			for (ContextQuery query : querySet.getQueries())
			{
				encodeContextQuery(query, out);
			}
		}
		out.println("</Queries>");
		out.println("</QuerySet>");
	}
	
	/**
	 * Decodes the {@link QuerySet} from the {@link Node}
	 * @param root the {@link Node}
	 * @return the {@link QuerySet}
	 * @throws XmlMarshallingException
	 */
	public static QuerySet decodeQuerySet(Node root) throws XmlMarshallingException {
		if (!root.getNodeName().equals("QuerySet"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'QuerySet', was '" + root.getNodeName() + "'" );
		
		QuerySet querySet = new QuerySet();
		
		querySet.setPackageName(CmHelper.getAttributeValue("pkgName", root));
		querySet.setImports(new String[0]);
		querySet.setFunctions(new DeclaredFunction[0]);
		querySet.setQueries(new ContextQuery[0]);
		
		NodeList list = root.getChildNodes();
		for (int i=0; i< list.getLength(); i++)
		{
			Node node = list.item(i);
			if (node.getNodeName().equals("Imports"))
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
					querySet.setImports((String[])imports.toArray(new String[imports.size()]));	
			}
			else if (node.getNodeName().equals("Functions"))
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
					querySet.setFunctions((DeclaredFunction[])functions.toArray(new DeclaredFunction[functions.size()]));				
			}
			else if (node.getNodeName().equals("Queries"))
			{
				NodeList queryList = node.getChildNodes();
				List<ContextQuery> queries = new ArrayList<ContextQuery>();
				for (int k=0; k< queryList.getLength(); k++)
				{
					Node queryNode = queryList.item(k);
					if (queryNode.getNodeName().equals("ContextQuery"))
					{
						queries.add(decodeContextQuery(queryNode));
					}
				}
				if (queries.size() != 0)
					querySet.setQueries((ContextQuery[])queries.toArray(new ContextQuery[queries.size()]));
			}
		}
		return querySet;
	}
	
	/**
	 * Encodes the {@link ContextQuery} to the {@link PrintStream}
	 * @param ctxQuery the {@link ContextQuery}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeContextQuery(ContextQuery ctxQuery, PrintStream out) throws XmlMarshallingException {
		out.println("<ContextQuery name=\"" + ctxQuery.getName() + "\">");
		out.println("<Arguments>");
		if (ctxQuery.getArguments() != null)
		{
			CoreObjectMarshaller.encodeAttributeList(ctxQuery.getArguments(), out);
		}
		out.println("</Arguments>");
		
		out.println("<Output>");
		if (ctxQuery.getOutput() != null)
		{
			CoreObjectMarshaller.encodeAttributeList(ctxQuery.getOutput(), out);
		}
		out.println("</Output>");
		
		out.println("<Query>" + CmHelper.getCDATA(ctxQuery.getQuery()) + "</Query>");
		out.println("</ContextQuery>");
	}
	
	/**
	 * Decodes the {@link ContextQuery} from the {@link Node}
	 * @param root the {@link Node}
	 * @return the {@link ContextQuery}
	 * @throws XmlMarshallingException
	 */
	public static ContextQuery decodeContextQuery(Node root) throws XmlMarshallingException {
		if (!root.getNodeName().equals("ContextQuery"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'ContextQuery', was '" + root.getNodeName() + "'" );
		
		ContextQuery ctxQuery = new ContextQuery();
		
		ctxQuery.setName(CmHelper.getAttributeValue("name", root));
		ctxQuery.setArguments(new Attribute[0]);
		ctxQuery.setOutput(new Attribute[0]);
		ctxQuery.setQuery("");
		
		NodeList list = root.getChildNodes();
		for (int i=0; i< list.getLength(); i++)
		{
			Node node = list.item(i);
			if (node.getNodeName().equals("Arguments"))
			{
				ctxQuery.setArguments(CoreObjectMarshaller.decodeAttributeList(node));	
			}
			else if (node.getNodeName().equals("Output"))
			{
				ctxQuery.setOutput(CoreObjectMarshaller.decodeAttributeList(node));				
			}
			else if (node.getNodeName().equals("Query"))
			{
				ctxQuery.setQuery(CmHelper.getElementValue(node));
			}
		}
		return ctxQuery;
	}
}
