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
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class StripedFileSystemDevice {
	private String name;
	private String id;
	private String systemId;
	private Long stripeSize;
	private Vector<String> backendDevices;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public Vector<String> getBackendDevices() {
		return backendDevices;
	}
	public void setBackendDevices(Vector<String> backendDevices) {
		this.backendDevices = backendDevices;
	}
	public Long getStripeSize() {
		return stripeSize;
	}
	public void setStripeSize(Long stripeSize) {
		this.stripeSize = stripeSize;
	}
	
	
	public StripedFileSystemDevice(String name, String id, String systemId, Long stripeSize,
			Vector<String> backendDevices) {
		super();
		this.name = name;
		this.id = id;
		this.systemId = systemId;
		this.stripeSize = stripeSize;
		this.backendDevices = backendDevices;
	}
	
	public StripedFileSystemDevice(String name, String id, String systemId, Long stripeSize) {
		this(name, id, systemId, stripeSize, new Vector<String>());
	}
	
	public StripedFileSystemDevice(String name, Long stripeSize) {
		this(name, null, null, stripeSize);
	}
	
	public StripedFileSystemDevice(String name, Long stripeSize, Vector<String> backendDevices) {
		this(name, null, null, stripeSize, backendDevices);
	}
	
	@SuppressWarnings("unchecked")
	public StripedFileSystemDevice(String xmlData) throws IOException {
		try {
			backendDevices = new Vector<String>();
			Document d = new SAXBuilder().build(new StringReader(xmlData));
			Element e = d.getRootElement();
			name = e.getAttributeValue("Name");
			stripeSize = new Long(e.getAttributeValue("StripeSize"));
			id = e.getAttributeValue("ID");
			systemId = e.getAttributeValue("SystemID");
			List backends = e.getChildren("BackendDevice");
			for (Object o : backends) {
				Element device = (Element)o;
				backendDevices.add(device.getAttributeValue("id"));
			}
		} catch (JDOMException e1) {
			e1.printStackTrace();
			throw new IOException("JDomException: ", e1);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public StripedFileSystemDevice(Element e) {
		name = e.getAttributeValue("Name");
		id = e.getAttributeValue("ID");
		systemId = e.getAttributeValue("SystemID");
		backendDevices = new Vector<String>();
		stripeSize = new Long(e.getAttributeValue("StripeSize"));
		List backends = e.getChildren("BackendDevice");
		for (Object o : backends) {
			Element device = (Element)o;
			backendDevices.add(device.getAttributeValue("id"));
		}
	}
	
	public Element toXML() {
		Element root = new Element("StripedFileSystemDevice");
		root.setAttribute("Name", name);
		root.setAttribute("ID", id);
		root.setAttribute("SystemID", systemId);
		if (stripeSize != null) {
			root.setAttribute("StripeSize", stripeSize.toString());
		}
		for (String backID : backendDevices) {
			Element bd = new Element("BackendDevice");
			bd.setAttribute("ID", backID);
			root.addContent(bd);
		}
		return root;
	}
	
	public String toXMLString() {
		Document d = new Document(toXML());
		XMLOutputter out = new XMLOutputter();
		return out.outputString(d);
	}
}
