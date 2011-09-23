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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

public class LockResult {
	public static final short LOCK_REJECT = 0;
	public static final short LOCK_GRANTED = 1;
	
	public final static String ROOT_NAME = "LockResult";
	
	private LockRequest request;
	private LockRequest concurrent;
	private short lockGrant;
	
	public LockRequest getRequest() {
		return request;
	}
	
	public void setRequest(LockRequest request) {
		this.request = request;
	}
	
	public LockRequest getConcurrent() {
		return concurrent;
	}
	
	public void setConcurrent(LockRequest concurrent) {
		this.concurrent = concurrent;
	}
	
	public short getLockGrant() {
		return lockGrant;
	}
	
	public void setLockGrant(short lockGrant) {
		this.lockGrant = lockGrant;
	}

	public LockResult(LockRequest request, short lockGrant, LockRequest concurrent) {
		super();
		this.request = request;
		this.concurrent = concurrent;
		this.lockGrant = lockGrant;
	}
	
	public LockResult(Element xml) {
		setLockGrant(Short.parseShort(xml.getAttributeValue("grant")));
		Element e = xml.getChild("LockRequest");
		if (e == null) {
			throw new RuntimeException("No lockRequest");
		}
		
		Element data = e.getChild(FileLockRequest.ROOT_NAME);
		if (data == null) {
			data = e.getChild(ConfigLockRequest.ROOT_NAME);
			setRequest(new ConfigLockRequest(data));
		} else {
			setRequest(new FileLockRequest(data));
		}
		e = xml.getChild("ConcurrentLock");
		setConcurrent(null);
		if (e != null) {
			data = e.getChild(FileLockRequest.ROOT_NAME);
			if (data != null) {
				setConcurrent(new FileLockRequest(data));
			} else {
				data = e.getChild(ConfigLockRequest.ROOT_NAME);
				setConcurrent(new ConfigLockRequest(data));
			}
		}
	}
	
	public Element toXML() {
		Element e = new Element(ROOT_NAME);
		e.setAttribute("grant", "" + getLockGrant());
		Element e2 = new Element("LockRequest");
		e2.addContent(getRequest().toXML());
		e.addContent(e2);
		if (getConcurrent() != null) {
			e2 = new Element("ConcurrentLock");
			e2.addContent(getConcurrent().toXML());
			e.addContent(e2);
		}
		return e;
	}
	
	public String toXMLString() {
		Document d = new Document(toXML());
		XMLOutputter out = new XMLOutputter();
		return out.outputString(d);
	}
}
