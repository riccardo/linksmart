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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.linksmart.caf.cm.query.QuerySet;
import eu.linksmart.caf.cm.specification.ContextSpecification;

/**
 * Helper class for building {@link ContextSpecification}s and {@link QuerySet}s 
 * from various sources.
 *
 */
public class ContextBuilderFactory {

	/**
	 * Builds a {@link ContextSpecification} object by reading from the given
	 * {@link InputStream}, representing the XML encoded {@link ContextSpecification}.
	 * @param input the {@link InputStream} 
	 * @return the built {@link ContextSpecification}
	 * @throws IOException
	 * @throws XmlMarshallingException
	 */
	public static ContextSpecification buildContext(InputStream input) throws IOException, XmlMarshallingException{
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
		
		Element root = doc.getDocumentElement();
		return ContextSpecificationMarshaller.decodeContextSpecification(root);
	}
	
	/**
	 * Builds the {@link ContextSpecification} from the XML provided as String 
	 * @param str the XML encoded {@link ContextSpecification}, as String
	 * @return the built {@link ContextSpecification}
	 * @throws IOException
	 * @throws XmlMarshallingException
	 */
	public static ContextSpecification buildContext(String str) throws IOException, XmlMarshallingException{
		ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes("UTF8"));
		return buildContext(bais);
	}
	
	/**
	 * Encodes the {@link ContextSpecification} to String, as XML
	 * @param spec the {@link ContextSpecification}
	 * @return the xml String
	 * @throws XmlMarshallingException
	 */
	public static String encodeContext(ContextSpecification spec) throws XmlMarshallingException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		ContextSpecificationMarshaller.encodeContextSpecification(spec, out);
		String result = out.toString();
		out.close();
		return result;
	}
	
	/**
	 * Stores the {@link ContextSpecification} by writing it, as XML, to the 
	 * given {@link OutputStream}
	 * @param spec the {@link ContextSpecification}
	 * @param output the {@link OutputStream}
	 * @throws XmlMarshallingException
	 */
	public static void storeContext(ContextSpecification spec, OutputStream output) throws XmlMarshallingException{
		PrintStream out = new PrintStream(output);
		ContextSpecificationMarshaller.encodeContextSpecification(spec, out);
		out.close();
	}
	
	/**
	 * Builds a {@link QuerySet} object by reading from the given
	 * {@link InputStream}, representing the XML encoded {@link QuerySet}.
	 * @param input the {@link InputStream} to read from
	 * @return the built {@link QuerySet}
	 * @throws XmlMarshallingException
	 */
	public static QuerySet buildQuerySet(InputStream input) throws XmlMarshallingException{
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
		
		Element root = doc.getDocumentElement();
		return QueryMarshaller.decodeQuerySet(root);
	}
	
	/**
	 * Builds the {@link QuerySet} from the XML provided as String 
	 * @param str the XML encoded {@link QuerySet}, as String
	 * @return the build {@link QuerySet}
	 * @throws UnsupportedEncodingException
	 * @throws XmlMarshallingException
	 */
	public static QuerySet buildQuerySet(String str) throws UnsupportedEncodingException, XmlMarshallingException{
		ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes("UTF8"));
		return buildQuerySet(bais);
	}
	
	/**
	 * Encodes the {@link QuerySet} to String, as XML
	 * @param querySet the {@link QuerySet}
	 * @return the {@link QuerySet} as String xml
	 * @throws XmlMarshallingException
	 */
	public static String encodeQuerySet(QuerySet querySet) throws XmlMarshallingException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		QueryMarshaller.encodeQuerySet(querySet, out);
		String result = out.toString();
		out.close();
		return result;
	}
	
	/**
	 * Stores the {@link QuerySet} by writing it, as XML, to the 
	 * given {@link OutputStream}
	 * @param querySet the {@link QuerySet}
	 * @param output the {@link OutputStream}
	 * @throws XmlMarshallingException
	 */
	public static void storeQuerySet(QuerySet querySet, OutputStream output) throws XmlMarshallingException{
		PrintStream out = new PrintStream(output);
		QueryMarshaller.encodeQuerySet(querySet, out);
		out.close();
	}

}
