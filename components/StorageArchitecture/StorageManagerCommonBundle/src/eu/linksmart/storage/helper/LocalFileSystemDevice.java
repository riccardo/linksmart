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

public class LocalFileSystemDevice {
	
	private String name;
	private String path;
	private String id;
	private String systemId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	
	public LocalFileSystemDevice(String name, String path, String id,
			String systemId) {
		super();
		this.name = name;
		this.path = path;
		this.id = id;
		this.systemId = systemId;
	}
	
	public LocalFileSystemDevice(String name, String path) {
		this(name, path, null, null);
	}
	
	public LocalFileSystemDevice(String xmlData) throws IOException {
		try {
			Document d = new SAXBuilder().build(new StringReader(xmlData));
			Element e = d.getRootElement();
			name = e.getAttributeValue("Name");
			path = e.getAttributeValue("Path");
			id = e.getAttributeValue("ID");
			systemId = e.getAttributeValue("SystemID");
		} catch (JDOMException e1) {
			e1.printStackTrace();
			throw new IOException("JDomException: ", e1);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}
	}
	
	public LocalFileSystemDevice(Element e) {
		name = e.getAttributeValue("Name");
		path = e.getAttributeValue("Path");
		id = e.getAttributeValue("ID");
		systemId = e.getAttributeValue("SystemID");
	}
	
	public Element toXML() {
		Element e = new Element("LocalFileSystemDevice");
		e.setAttribute("Name", name);
		e.setAttribute("Path", path);
		if (id != null)
			e.setAttribute("ID", id);
		if (systemId!=null)
			e.setAttribute("SystemID", systemId);
		return e;
	}
	
	public String toXMLString() {
		Document d = new Document(toXML());
		XMLOutputter out = new XMLOutputter();
		return out.outputString(d);
	}
}
