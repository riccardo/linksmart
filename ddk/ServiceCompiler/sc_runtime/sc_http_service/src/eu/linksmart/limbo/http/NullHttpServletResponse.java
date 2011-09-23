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
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public abstract class NullHttpServletResponse implements HttpServletResponse {

	@Override
	public void addCookie(Cookie arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean containsHeader(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String encodeRedirectUrl(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String encodeURL(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String encodeUrl(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void sendError(int arg0) throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setStatus(int arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setStatus(int arg0, String arg1) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void flushBuffer() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getBufferSize() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getCharacterEncoding() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getContentType() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Locale getLocale() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isCommitted() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void reset() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void resetBuffer() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setBufferSize(int arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setContentLength(int arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setContentType(String arg0) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setLocale(Locale arg0) {
		throw new RuntimeException("Not implemented");
	}

}
