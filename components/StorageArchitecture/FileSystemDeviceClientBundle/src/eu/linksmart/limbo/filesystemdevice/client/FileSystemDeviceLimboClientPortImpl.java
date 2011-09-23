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

package eu.linksmart.limbo.filesystemdevice.client;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Date;



public class FileSystemDeviceLimboClientPortImpl implements FileSystemDeviceLimboClientPort{
	
	private String host;
	private int port;
	private String path; 
	private ClientProtocol protocol;	

	public FileSystemDeviceLimboClientPortImpl(String url){
		this(new TCPProtocol(), url);
	}
	
	public FileSystemDeviceLimboClientPortImpl(ClientProtocol p, String url){

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
		this.protocol = p;
	}
	
	public java.lang.String clearFile(java.lang.String path) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><clearFile xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("</clearFile></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"clearFile"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String copy(java.lang.String source, java.lang.String destination) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><copy xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<source>"+source+"</source>");
		XMLRequest = XMLRequest.concat("<destination>"+destination+"</destination>");
		XMLRequest = XMLRequest.concat("</copy></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"copy"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String createDirectory(java.lang.String path) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><createDirectory xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("</createDirectory></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"createDirectory"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String createFile(java.lang.String path, java.lang.String properties) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><createFile xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("<properties>"+properties+"</properties>");
		XMLRequest = XMLRequest.concat("</createFile></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"createFile"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String existsPath(java.lang.String path) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><existsPath xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("</existsPath></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"existsPath"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String getDirectoryEntries(java.lang.String path) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getDirectoryEntries xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("</getDirectoryEntries></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"getDirectoryEntries"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String getFile(java.lang.String path) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getFile xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("</getFile></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"getFile"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String getFreeSpace() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getFreeSpace xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</getFreeSpace></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"getFreeSpace"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String getID() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getID xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</getID></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"getID"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String getSize() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getSize xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</getSize></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"getSize"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String getStatFS() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getStatFS xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</getStatFS></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"getStatFS"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String move(java.lang.String source, java.lang.String destination) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><move xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<source>"+source+"</source>");
		XMLRequest = XMLRequest.concat("<destination>"+destination+"</destination>");
		XMLRequest = XMLRequest.concat("</move></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"move"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String readFile(java.lang.String path, java.lang.String start, java.lang.String size) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><readFile xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("<start>"+start+"</start>");
		XMLRequest = XMLRequest.concat("<size>"+size+"</size>");
		XMLRequest = XMLRequest.concat("</readFile></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"readFile"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String removeDirectory(java.lang.String path, java.lang.Boolean recursive) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><removeDirectory xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("<recursive>"+recursive+"</recursive>");
		XMLRequest = XMLRequest.concat("</removeDirectory></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"removeDirectory"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String removeFile(java.lang.String path) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><removeFile xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("</removeFile></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"removeFile"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String setFileProperties(java.lang.String path, java.lang.String properties) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><setFileProperties xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("<properties>"+properties+"</properties>");
		XMLRequest = XMLRequest.concat("</setFileProperties></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"setFileProperties"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String setFileProperty(java.lang.String path, java.lang.String propertiesName, java.lang.String propertiesValue) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><setFileProperty xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("<propertiesName>"+propertiesName+"</propertiesName>");
		XMLRequest = XMLRequest.concat("<propertiesValue>"+propertiesValue+"</propertiesValue>");
		XMLRequest = XMLRequest.concat("</setFileProperty></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"setFileProperty"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String truncateFile(java.lang.String path, java.lang.String size) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><truncateFile xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("<size>"+size+"</size>");
		XMLRequest = XMLRequest.concat("</truncateFile></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"truncateFile"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String writeFile(java.lang.String path, java.lang.String start, java.lang.String data) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><writeFile xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<path>"+path+"</path>");
		XMLRequest = XMLRequest.concat("<start>"+start+"</start>");
		XMLRequest = XMLRequest.concat("<data>"+data+"</data>");
		XMLRequest = XMLRequest.concat("</writeFile></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"writeFile"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			FileSystemDeviceLimboClientParser p = new FileSystemDeviceLimboClientParser(hp.getRequest(),null);
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
