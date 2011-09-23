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

package eu.linksmart.limbo.storagemanagerdevice.client;

import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.JDOMException;

import eu.linksmart.storage.helper.StringResponse;
import eu.linksmart.storage.helper.StringVectorResponse;
import eu.linksmart.storage.helper.VoidResponse;

public class LinkSmartSMConnector {
	String wsAddress;
	static StorageManagerLimboClientPortImpl theClient;

	public LinkSmartSMConnector(String wsAddress) throws IOException {
		super();
		this.wsAddress = wsAddress;
		theClient = new StorageManagerLimboClientPortImpl(new TCPProtocol(),
				wsAddress);
		if (theClient == null)
			throw new IOException("Coulod not instantiate connection to "
					+ wsAddress);
	}

	public StringResponse createStorageDevice(String config) throws IOException {
		String answer = StringEscapeUtils.unescapeXml(theClient
				.createStorageDevice(StringEscapeUtils.escapeXml(config)));
		if (answer == null)
			throw new IOException("The Client returned null");
		try {
			return new StringResponse(answer);
		} catch (JDOMException e) {
			throw new IOException("JDomeException reading string", e);
		}
	}
	
	public VoidResponse deleteStorageDevice(String id) throws IOException {
		String answer = StringEscapeUtils.unescapeXml(theClient
				.deleteStorageDevice(StringEscapeUtils.escapeXml(id)));
		if (answer == null)
			throw new IOException("The Client returned null");
		try {
			return new VoidResponse(answer);
		} catch (JDOMException e) {
			throw new IOException("JDomeException reading string", e);
		}
	}
	
	public StringVectorResponse getSupportedStorageDevices() throws IOException {
		String answer = StringEscapeUtils.unescapeXml(theClient
				.getSupportedStorageDevices());
		if (answer == null)
			throw new IOException("The Client returned null");
		try {
			return new StringVectorResponse(answer);
		} catch (JDOMException e) {
			throw new IOException("JDomeException reading string", e);
		}
	}
	
	public StringVectorResponse getStorageDevices() throws IOException {
		String answer = StringEscapeUtils.unescapeXml(theClient
				.getStorageDevices());
		if (answer == null)
			throw new IOException("The Client returned null");
		try {
			return new StringVectorResponse(answer);
		} catch (JDOMException e) {
			throw new IOException("JDomeException reading string", e);
		}
	}
	
	public StringResponse getStorageDeviceConfig(String id) throws IOException {
		String answer = StringEscapeUtils.unescapeXml(theClient
				.getStorageDeviceConfig(StringEscapeUtils.escapeXml(id)));
		if (answer == null)
			throw new IOException("The Client returned null");
		try {
			return new StringResponse(answer);
		} catch (JDOMException e) {
			throw new IOException("JDomeException reading string", e);
		}
	}
	
	public VoidResponse updateStorageDevice(String config) throws IOException {
		String answer = StringEscapeUtils.unescapeXml(theClient
				.updateStorageDevice(StringEscapeUtils.escapeXml(config)));
		if (answer == null)
			throw new IOException("The Client returned null");
		try {
			return new VoidResponse(answer);
		} catch (JDOMException e) {
			throw new IOException("JDomeException reading string", e);
		}
	}
}
