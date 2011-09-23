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
 * Copyright (C) 2006-2010 [University of Paderborn]
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

package eu.linksmart.storage.helper;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public abstract class Response {

	private String rootType;

	private int errorCode;

	private String errorMessage;

	protected Response(String rootType, int errorCode, String errorMessage) {
		if (rootType == null) {
			this.rootType = "response";
		} else {
			this.rootType = rootType;
		}
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	protected Response(String rootType, String xmlData) throws JDOMException, IOException {
		this(rootType, new StringReader(xmlData));
	}
	
	protected Response(String rootType, Reader xmlData) throws JDOMException, IOException {
		this.rootType = rootType;
		Document d = new SAXBuilder().build(xmlData);
		Element root = d.getRootElement();
		if (!root.getName().equalsIgnoreCase(rootType))
			throw new JDOMException("root element not of type " + rootType);
		Element error = root.getChild("error");
		this.errorCode = Integer.parseInt(error.getAttributeValue("code"));
		this.errorMessage = error.getText();
		readResult(root.getChild("result"));
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String toXMLString() {
		Document d = new Document(toXMLElement());
		XMLOutputter out = new XMLOutputter();
		return out.outputString(d);
	}

	public Element toXMLElement() {
		Element root = new Element(rootType);
		Element error = new Element("error");
		error.setAttribute("code", "" + errorCode);
		error.setText(errorMessage);
		root.addContent(error);
		Element result = new Element("result");
		writeResult(result);
		if (result != null)
			root.addContent(result);
		return root;
	}

	public String errorOut() {
		if (errorCode == ErrorCodes.EC_NO_ERROR) {
			return null;
		}
		if (errorMessage == null) {
			return "Error " + errorCode + " (without message)";
		}
		return "Error " + errorCode + ": " + errorMessage;
	}
	
	protected abstract void writeResult(Element e);

	protected abstract void readResult(Element e);
}
