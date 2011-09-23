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



public class LinkSmartServicePortLimboClientPortImpl implements LinkSmartServicePortLimboClientPort{
	
	private String host;
	private int port;
	private String path; 
	private ClientProtocol protocol;	

	public LinkSmartServicePortLimboClientPortImpl(ClientProtocol p, String url){
	
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
	
	public java.lang.String GetLinkSmartID() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetLinkSmartID xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetLinkSmartID></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"GetLinkSmartID"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String CreateWS() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><CreateWS xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</CreateWS></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"CreateWS"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String GetStatus() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetStatus xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetStatus></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"GetStatus"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String GetProperty(java.lang.String Property) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetProperty xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<Property>"+Property+"</Property>");
		XMLRequest = XMLRequest.concat("</GetProperty></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"GetProperty"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.Boolean GetHasError() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetHasError xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetHasError></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"GetHasError"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.convertStringToBoolean(p.getArg(0).toString());
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	public java.lang.String GetErrorMessage() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetErrorMessage xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetErrorMessage></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"GetErrorMessage"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String GetDiscoveryInfo() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetDiscoveryInfo xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetDiscoveryInfo></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"GetDiscoveryInfo"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String GetDACEndpoint() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetDACEndpoint xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetDACEndpoint></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"GetDACEndpoint"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String GetWSEndpoint() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetWSEndpoint xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetWSEndpoint></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"GetWSEndpoint"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String GetLinkSmartWSEndpoint() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetLinkSmartWSEndpoint xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetLinkSmartWSEndpoint></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"GetLinkSmartWSEndpoint"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String GetWSDL() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetWSDL xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetWSDL></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"GetWSDL"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public void SetLinkSmartID(java.lang.String LinkSmartID) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><SetLinkSmartID xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<LinkSmartID>"+LinkSmartID+"</LinkSmartID>");
		XMLRequest = XMLRequest.concat("</SetLinkSmartID></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"SetLinkSmartID"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public void SetStatus(java.lang.String Status) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><SetStatus xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<Status>"+Status+"</Status>");
		XMLRequest = XMLRequest.concat("</SetStatus></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"SetStatus"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public void SetDACEndpoint(java.lang.String DACEndpoint) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><SetDACEndpoint xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<DACEndpoint>"+DACEndpoint+"</DACEndpoint>");
		XMLRequest = XMLRequest.concat("</SetDACEndpoint></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"SetDACEndpoint"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public void SetProperty(java.lang.String Property, java.lang.String Value) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><SetProperty xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<Property>"+Property+"</Property>");
		XMLRequest = XMLRequest.concat("<Value>"+Value+"</Value>");
		XMLRequest = XMLRequest.concat("</SetProperty></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"SetProperty"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public void Stop() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><Stop xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</Stop></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"Stop"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public void StopWS() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><StopWS xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</StopWS></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"StopWS"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public void StopLinkSmartWS() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><StopLinkSmartWS xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</StopLinkSmartWS></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"StopLinkSmartWS"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			LinkSmartServicePortLimboClientParser p = new LinkSmartServicePortLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
}
