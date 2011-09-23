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

package eu.linksmart.limbo.cookiedevice.backend;

import java.io.IOException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

public class HTCookieContainer extends CookieContainer {

	private Hashtable<String, String> data;

	private HTBackend back;

	public HTCookieContainer(String id, String name, HTBackend back)
			throws IOException {
		super(id, name);
		this.data = back.readHT();
		if (this.data == null) {
			this.data = new Hashtable<String, String>();
		}
		this.back = back;
	}

	public Collection<String> getCookieNames() {
		Vector<String> result;
		synchronized (this) {
			result = new Vector<String>();
			Enumeration<String> keyEnum = data.keys();
			while (keyEnum.hasMoreElements()) {
				result.add(keyEnum.nextElement());
			}
		}
		return data.keySet();

	}

	public Dictionary<String, String> getAllData(String regularExpression) {
		Hashtable<String, String> data;
		synchronized (this) {
			data = (Hashtable<String, String>) this.data.clone();
		}
		Hashtable<String, String> result = new Hashtable<String, String>();
		Pattern p = Pattern.compile(regularExpression);
		Enumeration<String> keyEnum = data.keys();
		while (keyEnum.hasMoreElements()) {
			String key = keyEnum.nextElement();
			if (p.matcher(key).matches()) {
				result.put(key, data.get(key));
			}
		}
		return result;
	}

	public String getCookieValue(String cookieName) {
		synchronized (this) {
			return data.get(cookieName);
		}
	}

	public void setCookieValue(String cookieName, String cookieValue)
			throws IOException {
		synchronized (this) {
			data.put(cookieName, cookieValue);
			back.storeHT(data);
		}
	}

	public void removeEntry(String cookieName) throws IOException {
		synchronized (this) {
			data.remove(cookieName);
			back.storeHT(data);
		}
	}

	public void destroy(boolean deleteData) {
		synchronized (this) {
			if (deleteData) {
				back.deleteData();
			}
			back = null;
			data = null;
		}	
	}
}
