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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;

public class LimboServletResponse extends NullHttpServletResponse {
	private StringWriter stringWriter = new StringWriter();
	private String contentType;
	private int contentLength;
	private Map<String,String> headers = new HashMap<String, String>();
	private int sc = SC_OK;
	
	public LimboServletResponse() {
		setHeader("Content-Type", "text/html");
	}
	
	@Override
	public void sendError(int sc, String msg) {
		throw new RuntimeException("Not implemented");
	}
	
	@Override
	public PrintWriter getWriter() {
		return new PrintWriter(stringWriter);
	}
	
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return new ServletOutputStream() {
			@Override
			public void write(int b) throws IOException {
				stringWriter.write(b);
			}};
	}

	@Override
	public void setContentType(String type) {
		this.contentType = type;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContentLength(int len) {
		this.contentLength = len;
	}
	
	public StringBuffer getOutput() {
		return stringWriter.getBuffer();
	}
	
	@Override
	public void setHeader(String name, String value) {
		headers .put(name, value);
	}

	protected Map<String, String> getHeaders() {
		return headers;
	}
	
	@Override
	public void setStatus(int sc) {
		this.sc  = sc;
	}

	public int getStatusCode() {
		return sc;
	}
}
