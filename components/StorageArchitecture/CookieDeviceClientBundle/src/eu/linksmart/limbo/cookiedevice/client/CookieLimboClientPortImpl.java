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

package eu.linksmart.limbo.cookiedevice.client;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Date;



public class CookieLimboClientPortImpl implements CookieLimboClientPort{
	
	private String host;
	private int port;
	private String path; 
	private ClientProtocol protocol;	

	public CookieLimboClientPortImpl(ClientProtocol p, String url){
	
	  if (url.startsWith("btspp"))
		{
			this.host = url;
			this.port = -1;
			this.path = "/";
		//	this.protocol = p;
			
		}

		if(p instanceof TCPProtocol || p instanceof UDPProtocol) {
		try {
			URL u = new URL(url);
			this.host = u.getHost();
			this.port = u.getPort();
			String path = u.getPath();
			if(path == "")
				this.path = "/";
			else
				this.path = path;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		}
		this.protocol = p;
	}
	
	public java.lang.String getCookieNames() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getCookieNames xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</getCookieNames></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"getKeys"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			CookieLimboClientParser p = new CookieLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String getAllData(java.lang.String regularExpression) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getAllData xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<regularExpression>"+regularExpression+"</regularExpression>");
		XMLRequest = XMLRequest.concat("</getAllData></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"getData"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			CookieLimboClientParser p = new CookieLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String getCookieValue(java.lang.String cookieName) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getCookieValue xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<cookieName>"+cookieName+"</cookieName>");
		XMLRequest = XMLRequest.concat("</getCookieValue></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"getValue"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			CookieLimboClientParser p = new CookieLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String setCookieValue(java.lang.String cookieName, java.lang.String cookieValue) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><setCookieValue xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<cookieName>"+cookieName+"</cookieName>");
		XMLRequest = XMLRequest.concat("<cookieValue>"+cookieValue+"</cookieValue>");
		XMLRequest = XMLRequest.concat("</setCookieValue></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"setValue"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			CookieLimboClientParser p = new CookieLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String removeEntry(java.lang.String cookieName) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><removeEntry xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<cookieName>"+cookieName+"</cookieName>");
		XMLRequest = XMLRequest.concat("</removeEntry></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"removeEntry"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			CookieLimboClientParser p = new CookieLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
