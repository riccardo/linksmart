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
 * Copyright (C) 2006-2010
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
package eu.linksmart.limbo.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class LimboServletRequest extends NullHttpServletRequest {

	private String contextPath, method;
	private String httpVersion;
	private Map<String, String> headers = new HashMap<String, String>();
	private StringBuffer entityBody;
	private InetAddress clientAddress;

	public LimboServletRequest(InetAddress clientAddress, String request) {
		this.clientAddress = clientAddress;
		// Request-Line   = Method SP Request-URI SP HTTP-Version CRLF
		String[] requestLineParts = request.split(" ");
		method = requestLineParts[0];
		contextPath = requestLineParts[1]; // FIXME: assumes context = URI
		httpVersion = requestLineParts[2];
	}

	void setHeader(String headerLine) {
		String[] elements = headerLine.split(": ");
		headers.put(elements[0], elements[1]);
	}

	@Override
	public String getHeader(String name) {
		return headers.get(name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		Vector<String> result = new Vector<String>();
		result.addAll(headers.keySet());
		return result.elements();
	}

	@Override
	public String getProtocol() {
		return httpVersion;
	}

	@Override
	public String getContextPath() {
		return contextPath;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestURI() {
		return contextPath.split("\\?")[0];
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(entityBody.toString().getBytes()))); // FIXME: Encoding...
	}

	public void setEntityBody(StringBuffer buffer) {
		entityBody = buffer;
	}

	@Override
	public String getRemoteAddr() {
		return clientAddress.toString();
	}
	
	@Override
	public String getPathInfo() {
		return null;
	}
}
