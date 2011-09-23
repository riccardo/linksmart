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
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class ConfigLockRequest extends LockRequest {
	
	public static String ROOT_NAME = "ConfigLockRequest";
	
	private String fsdID;
	
	public String getFsdID() {
		return fsdID;
	}
	
	public void setFsdID(String fsdID) {
		this.fsdID = fsdID;
	}
	
	public ConfigLockRequest(String xmlData) throws JDOMException, IOException {
		super(ROOT_NAME);
		Document d = new SAXBuilder().build(new StringReader(xmlData));
		Element xml = d.getRootElement();
		if (!xml.getName().equalsIgnoreCase(ROOT_NAME)) {
			System.out.println("ConfigLockRequest was called to initialize with an Element of type " + xml.getName());
			throw new RuntimeException("ConfigLockRequest was called to initialize with an Element of type " + xml.getName());
		}
		setSender(xml.getAttributeValue("sender"));
		fsdID = xml.getAttributeValue("fsdID");
		setLockType(Short.parseShort(xml.getAttributeValue("lockType")));
	}

	public ConfigLockRequest(String sender, String fsdID, short lockType) {
		super(ROOT_NAME, sender, lockType);
		this.fsdID = fsdID;
	}
	
	public ConfigLockRequest(Element xml) {
		super(ROOT_NAME);
		if (!xml.getName().equalsIgnoreCase(ROOT_NAME)) {
			System.out.println("ConfigLockRequest was called to initialize with an Element of type " + xml.getName());
			throw new RuntimeException("ConfigLockRequest was called to initialize with an Element of type " + xml.getName());
		}
		setSender(xml.getAttributeValue("sender"));
		fsdID = xml.getAttributeValue("fsdID");
		setLockType(Short.parseShort(xml.getAttributeValue("lockType")));
	}
	
	public Element toXML() {
		Element e = new Element(ROOT_NAME);
		e.setAttribute("sender", getSender());
		e.setAttribute("fsdID", fsdID);
		e.setAttribute("lockType", "" + getLockType());
		return e;
	}
	
	public String toXMLString() {
		Document d = new Document(toXML());
		XMLOutputter out = new XMLOutputter();
		return out.outputString(d);
	}
}
