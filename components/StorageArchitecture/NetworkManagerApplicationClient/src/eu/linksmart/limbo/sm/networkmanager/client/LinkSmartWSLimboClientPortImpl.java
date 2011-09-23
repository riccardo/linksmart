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

package eu.linksmart.limbo.sm.networkmanager.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.net.URL;



public class LinkSmartWSLimboClientPortImpl implements LinkSmartWSLimboClientPort{
	
	private String host;
	private int port;
	private String path; 
	

	public LinkSmartWSLimboClientPortImpl(String url){
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
		}
	}
	
	public String GetPhysicalDiscoveryInfo() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetPhysicalDiscoveryInfo xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetPhysicalDiscoveryInfo></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"GetPhysicalDiscoveryInfo"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String GetProperty(String property) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetProperty xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<property>"+property+"</property>");
		XMLRequest = XMLRequest.concat("</GetProperty></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"GetProperty"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public boolean GetHasError() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetHasError xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetHasError></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"GetHasError"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.convertStringToBoolean(p.getArg(0).toString());
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	public void SetLinkSmartID(String linksmartid) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><SetLinkSmartID xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<linksmartid>"+linksmartid+"</linksmartid>");
		XMLRequest = XMLRequest.concat("</SetLinkSmartID></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"SetLinkSmartID"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public void SetProperty(String property, String value) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><SetProperty xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<property>"+property+"</property>");
		XMLRequest = XMLRequest.concat("<value>"+value+"</value>");
		XMLRequest = XMLRequest.concat("</SetProperty></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"SetProperty"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public void SetStatus(String status) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><SetStatus xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<status>"+status+"</status>");
		XMLRequest = XMLRequest.concat("</SetStatus></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"SetStatus"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public String GetErrorMessage() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetErrorMessage xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetErrorMessage></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"GetErrorMessage"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public void SetDACEndpoint(String endpoint) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><SetDACEndpoint xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<endpoint>"+endpoint+"</endpoint>");
		XMLRequest = XMLRequest.concat("</SetDACEndpoint></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"SetDACEndpoint"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public String GetStatus() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetStatus xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetStatus></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"GetStatus"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public void StopDevice() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><StopDevice xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</StopDevice></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"StopDevice"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public String GetLinkSmartID() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetLinkSmartID xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetLinkSmartID></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"GetLinkSmartID"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public void StartDevice() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><StartDevice xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</StartDevice></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"StartDevice"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public String GetDACEndpoint() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><GetDACEndpoint xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</GetDACEndpoint></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"GetDACEndpoint"+'"'+"\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		SOAPMessage = SOAPMessage.concat(XMLRequest);
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			OutputStream cos = clientSocket.getOutputStream();
			cos.write(SOAPMessage.getBytes());
			InputStream cis = clientSocket.getInputStream();
			byte[] buffer = new byte[1024] ;
			int n ;
			String request;
			n = cis.read(buffer);
			request = new String(buffer, 0, n);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			LinkSmartWSLimboClientParser p = new LinkSmartWSLimboClientParser(hp.getRequest(),null);
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
