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
 * Copyright (C) 2006-2010 Technical University of Kosice
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

package eu.linksmart.aom.ontology.util;

import java.util.UUID;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.schema.LiteralProperty;
import eu.linksmart.aom.ontology.schema.OntologyClass;
import eu.linksmart.aom.ontology.schema.Property;
import eu.linksmart.aom.ontology.schema.ResourceProperty;

/**
 * Class responsible for creating and manipulation of ontology resources.
 * 
 * @author Peter Kostelnik
 *
 */
public class ResourceUtil {

	/**
	 * Creates the new statement.
	 * @param s Subject URI.
	 * @param p Ontology property.
	 * @param o Object URI.
	 * @param f Triplestore specific value factory.
	 * @return Created statement.
	 */
	public static Statement statement(String s, 
			Property p, 
			String o, 
			ValueFactory f){

		return f.createStatement(
				f.createURI(s), 
				f.createURI(p.stringValue()), 
				f.createURI(o));
	}

	/**
	 * Creates the new statement with literal value.
	 * @param s Subject URI.
	 * @param p Ontology property.
	 * @param o Object URI.
	 * @param xsdType URI of literal XSD type.
	 * @param f Triplestore specific value factory.
	 * @return Created statement.
	 */
	public static Statement statement(String s, 
			Property p, 
			String o,
			URI xsdType, 
			ValueFactory f){

		if(o == null) o = "";

		return f.createStatement(
				f.createURI(s), 
				f.createURI(p.stringValue()), 
				f.createLiteral(o, xsdType));
	}

	/**
	 * Creates the new statement.
	 * @param s Subject URI.
	 * @param p Property URI.
	 * @param o Object URI.
	 * @param f Triplestore specific value factory.
	 * @return Created statement.
	 */
	public static Statement statement(String s, 
			String p, 
			String o,
			ValueFactory f){

		return statement(s, new ResourceProperty(p), o, f);
	}

	/**
	 * Creates the new statement with literal value.
	 * @param s Subject URI.
	 * @param p Property URI.
	 * @param o Object URI.
	 * @param xsdType URI of literal XSD type.
	 * @param f Triplestore specific value factory.
	 * @return Created statement.
	 */
	public static Statement statement(String s, 
			String p, 
			String o,
			String xsdType,
			ValueFactory f){

		return statement(s, new LiteralProperty(p), o, new URIImpl(xsdType), f);
	}

	/**
	 * Creates the new statement and adds it to the graph.
	 * @param s Subject URI.
	 * @param p Ontology property.
	 * @param o Object URI.
	 * @param f Triplestore specific value factory.
	 * @param g Graph to be extended.
	 * @return Notification on operation success.
	 */
	public static void addStatement(String s, Property p, String o, 
			ValueFactory f,
			Graph g){
		g.add(ResourceUtil.statement(s, p, o, f));
	}

	/**
	 * Creates the new statement with literal value and adds it to the graph.
	 * @param s Subject URI.
	 * @param p Ontology property.
	 * @param o Object URI.
	 * @param xsdType XSD type URI.
	 * @param f Triplestore specific value factory.
	 * @param g Graph to be extended.
	 * @return Notification on operation success.
	 */
	public static void addStatement(String s, Property p, String o,
			URI xsdType, 
			ValueFactory f,
			Graph g){
		g.add(ResourceUtil.statement(s, p, o, xsdType, f));
	}

	/**
	 * Removes the statement from the graph.
	 * @param s Subject URI.
	 * @param p Ontology property.
	 * @param o Object URI.
	 * @param f Triplestore specific value factory.
	 * @param g Graph to be extended.
	 * @return Notification on operation success.
	 */
	public static void removeStatement(String s, Property p, String o, 
			ValueFactory f,
			Graph g){
		g.remove(ResourceUtil.statement(s, p, o, f));
	}

	/**
	 * Removes the statement with literal value from the graph.
	 * @param s Subject URI.
	 * @param p Ontology property.
	 * @param o Object URI.
	 * @param xsdType XSD type URI.
	 * @param f Triplestore specific value factory.
	 * @param g Graph to be extended.
	 * @return Notification on operation success.
	 */
	public static void removeStatement(String s, Property p, String o, URI xsdType, 
			ValueFactory f,
			Graph g){
		g.remove(ResourceUtil.statement(s, p, o, xsdType, f));
	}
}
