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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class NullHttpServletRequest implements HttpServletRequest {
	@Override
	public String getContextPath() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getMethod() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getContentLength() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getContentType() {
		throw new RuntimeException("Not implemented");
	}
	
	@Override
	public String getAuthType() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Cookie[] getCookies() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public long getDateHeader(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getHeader(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getHeaderNames() {
		throw new RuntimeException("Not implemented");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getHeaders(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getIntHeader(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getPathInfo() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getPathTranslated() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getQueryString() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getRemoteUser() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getRequestURI() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public StringBuffer getRequestURL() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getRequestedSessionId() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getServletPath() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public HttpSession getSession() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Principal getUserPrincipal() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isUserInRole(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Object getAttribute(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getAttributeNames() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getCharacterEncoding() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getLocalAddr() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getLocalName() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getLocalPort() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Locale getLocale() {
		throw new RuntimeException("Not implemented");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getLocales() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getParameter(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map getParameterMap() {
		throw new RuntimeException("Not implemented");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getParameterNames() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String[] getParameterValues(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getProtocol() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public BufferedReader getReader() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getRealPath(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getRemoteAddr() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getRemoteHost() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getRemotePort() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getScheme() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getServerName() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getServerPort() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isSecure() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void removeAttribute(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		throw new RuntimeException("Not implemented");
	}
}
