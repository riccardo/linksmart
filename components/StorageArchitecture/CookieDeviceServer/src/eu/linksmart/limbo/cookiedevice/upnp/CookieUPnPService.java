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

package eu.linksmart.limbo.cookiedevice.upnp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Enumeration;

import org.apache.commons.lang.StringEscapeUtils;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import eu.linksmart.storage.helper.ErrorCodes;
import eu.linksmart.storage.helper.ResponseFactory;

import eu.linksmart.limbo.cookiedevice.*;
import eu.linksmart.limbo.cookiedevice.backend.CookieContainer;

public class CookieUPnPService implements UPnPService {

	final private String SERVICE_ID = "urn:upnp-org:serviceId:Cookie";
	final private String SERVICE_TYPE = "urn:schemas-upnp-org:service:Cookie:1";
	final private String VERSION = "1";

	private regularExpressionStateVariable regularExpression;
	private resultStateVariable result;
	private cookieValueStateVariable cookieValue;
	private cookieNameStateVariable cookieName;
	private UPnPStateVariable[] states;
	private HashMap actions = new HashMap();
	private CookieDeviceDevice device;

	private CookieContainer container;

	public CookieUPnPService(CookieDeviceDevice device) {
		this.device = device;
		this.container = device.getContainer();
		regularExpression = new regularExpressionStateVariable();
		result = new resultStateVariable();
		cookieValue = new cookieValueStateVariable();
		cookieName = new cookieNameStateVariable();
		this.states = new UPnPStateVariable[] { regularExpression, result,
				cookieValue, cookieName };

		UPnPAction setCookieValue = new setCookieValueAction(cookieName,
				cookieValue, result, this);
		actions.put(setCookieValue.getName(), setCookieValue);
		UPnPAction removeEntry = new removeEntryAction(cookieName, result, this);
		actions.put(removeEntry.getName(), removeEntry);
		UPnPAction getCookieValue = new getCookieValueAction(cookieName,
				result, this);
		actions.put(getCookieValue.getName(), getCookieValue);
		UPnPAction getAllData = new getAllDataAction(regularExpression, result,
				this);
		actions.put(getAllData.getName(), getAllData);
		UPnPAction getCookieNames = new getCookieNamesAction(result, this);
		actions.put(getCookieNames.getName(), getCookieNames);
	}

	public UPnPAction getAction(String name) {
		return (UPnPAction) actions.get(name);
	}

	public UPnPAction[] getActions() {
		return (UPnPAction[]) (actions.values()).toArray(new UPnPAction[] {});
	}

	public String getId() {
		return SERVICE_ID;
	}

	public UPnPStateVariable getStateVariable(String name) {

		if (name.equals(regularExpression.getName()))
			return regularExpression;
		else if (name.equals(result.getName()))
			return result;
		else if (name.equals(cookieValue.getName()))
			return cookieValue;
		else if (name.equals(cookieName.getName()))
			return cookieName;
		return null;
	}

	public UPnPStateVariable[] getStateVariables() {
		return states;
	}

	public String getType() {
		return SERVICE_TYPE;
	}

	public String getVersion() {
		return VERSION;
	}

	public java.lang.String getCookieNames() {
		Collection<String> keys = container.getCookieNames();
		return StringEscapeUtils
				.escapeXml(ResponseFactory.createStringVectorResponse(
						ErrorCodes.EC_NO_ERROR, null, keys));
	}

	public java.lang.String getAllData(java.lang.String regularExpression) {
		regularExpression = StringEscapeUtils.unescapeXml(regularExpression);
		return StringEscapeUtils.escapeXml(ResponseFactory
				.createDictionaryResponse(ErrorCodes.EC_NO_ERROR, null,
						container.getAllData(regularExpression)));
	}

	public java.lang.String getCookieValue(java.lang.String cookieName) {
		cookieName = StringEscapeUtils.unescapeXml(cookieName);
		String value = container.getCookieValue(cookieName);
		return StringEscapeUtils.escapeXml(ResponseFactory
				.createStringResponse(ErrorCodes.EC_NO_ERROR, null, value));
	}

	public java.lang.String setCookieValue(java.lang.String cookieName,
			java.lang.String cookieValue) {
		cookieName = StringEscapeUtils.unescapeXml(cookieName);
		cookieValue = StringEscapeUtils.unescapeXml(cookieValue);
		try {
			container.setCookieValue(cookieName, cookieValue);
			return StringEscapeUtils.escapeXml(ResponseFactory
					.createVoidResponse(ErrorCodes.EC_NO_ERROR, null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return StringEscapeUtils.escapeXml(ResponseFactory
					.createVoidResponse(ErrorCodes.EC_IO_EXEPTION,
							"IOException: " + e.getMessage()));
		}
	}

	public java.lang.String removeEntry(java.lang.String cookieName) {
		cookieName = StringEscapeUtils.unescapeXml(cookieName);
		try {
			container.removeEntry(cookieName);
			return StringEscapeUtils.escapeXml(ResponseFactory
					.createVoidResponse(ErrorCodes.EC_NO_ERROR, null));
		} catch (IOException e) {
			e.printStackTrace();
			return StringEscapeUtils.escapeXml(ResponseFactory
					.createVoidResponse(ErrorCodes.EC_IO_EXEPTION,
							"IOException: " + e.getMessage()));
		}
	}
}
