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

package eu.linksmart.aom.generator;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Class responsible for generators support. Contains various helper methods for XML structure
 * building and serialization.
 * @author Peter Kostelnik
 *
 */
public class Generator {
	/**
	 * Creates XML element.
	 * @param elementName Element name.
	 * @param ns Element namespace.
	 * @return XML element.
	 */
	public Element element(String elementName, String ns){
		Element elm = new Element(elementName);
		elm.setNamespace(Namespace.getNamespace(ns));
		return elm;
	}

	/**
	 * Creates XML element.
	 * @param elementName Element name.
	 * @param attributes Element attributes.
	 * @return XML element.
	 */
	public Element element(String elementName, Map<String, String> attributes){
		Element elm = new Element(elementName);
		for(String attrName : attributes.keySet()){
			String attrValue = attributes.get(attrName);
			if(attrValue == null) attrValue = "";
			elm.setAttribute(new Attribute(attrName, attrValue.trim()));
		}
		return elm;
	}

	/**
	 * Creates XML element.
	 * @param elementName Element name.
	 * @param attributes Element attributes.
	 * @param value Element text value.
	 * @return XML element.
	 */
	public Element element(String elementName, Map<String, String> attributes, String value){
		Element elm = element(elementName, attributes);
		elm.addContent(value);
		return elm;
	}

	/**
	 * Adds the element to the parent element.
	 * @param elementName New element name.
	 * @param value New element text value.
	 * @param superElm Parent element.
	 * @return Added element.
	 */
	public Element addElement(String elementName, String value, Element superElm){
		Element elm = new Element(elementName, superElm.getNamespace());
		if(value != null && !value.trim().equals("")) elm.setText(value);
		superElm.addContent(elm);
		return elm;
	}
	
	/**
	 * Adds the element to the parent element.
	 * @param elementName New element name. 
	 * @param superElm Parent element.
	 * @return Added element.
	 */
	public Element addElement(String elementName, Element superElm){
		return addElement(elementName, (String)null, superElm);
	}

	/**
	 * Adds the element to the parent element.
	 * @param element Element to add.
	 * @param superElm Parent element.
	 * @return Added element.
	 */
	public Element addElement(Element element, Element superElm){
		if(element.getNamespace().getURI().trim().equals("")){
			element.setNamespace(superElm.getNamespace());
		}
		superElm.addContent(element);
		return element;
	}

	/**
	 * Adds the element to the parent element.
	 * @param elementName New element name.
	 * @param attributes New element attributes.
	 * @param superElm Parent element.
	 * @return Added element.
	 */
	public Element addElement(String elementName, Map<String, String> attributes, Element superElm){
		Element elm = element(elementName, attributes);
		elm.setNamespace(superElm.getNamespace());
		superElm.addContent(elm);
		return elm;
	}

	/**
	 * Adds element to super element.
	 * @param elementName New element name.
	 * @param value New element text value.
	 * @param attributes New element attributes.
	 * @param superElm Parent element.
	 * @return Added element.
	 */
	public Element addElement(String elementName, String value, Map<String, String> attributes, Element superElm){
		Element elm = element(elementName, attributes);
		elm.setNamespace(superElm.getNamespace());
		elm.setText(value);
		superElm.addContent(elm);
		return elm;
	}

	/**
	 * Serializes XML element to formated string.
	 * @param elm Element to serialize.
	 * @return Serialized XML element.
	 */
	public String toString(Element elm){
		XMLOutputter out = new XMLOutputter();

		Format format = Format.getPrettyFormat();
		format.setIndent("  ");
		out.setFormat(format);

		try{
			StringWriter writer = new StringWriter();
			out.output(elm, writer);
			return writer.toString();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return "";
	}
}

