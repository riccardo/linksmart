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

package eu.linksmart.limbo.lockmanager.client;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Date;


public class LockManagerLimboClientPortImpl implements LockManagerLimboClientPort{
	
	private String host;
	private int port;
	private String path; 
	private ClientProtocol protocol;	
	
	public LockManagerLimboClientPortImpl(String url){
		this(new TCPProtocol(), url);
	}

	public LockManagerLimboClientPortImpl(ClientProtocol p, String url){
	
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
	
	
	public java.lang.String getLockInfo(java.lang.String lock) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getLockInfo xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<lock>"+lock+"</lock>");
		XMLRequest = XMLRequest.concat("</getLockInfo></soapenv:Body></soapenv:Envelope>");
		String request = "";
		System.out.println("here--------------");
		System.out.println("XMLRequestrequest: "+XMLRequest);
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"getLockInfo"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		System.out.println("--------------");
		System.out.println("request: "+request);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			//
			System.out.println("response = this.protocol.communicateWithServer(request, this.host, this.port): "+response);
			System.out.println("--------------");
			//
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			//
			System.out.println("hp.getRequest(): " + hp.getRequest());
			System.out.println("hp.parseHeader(): "+hp.parseHeader());
			//
			LockManagerLimboClientParser p = new LockManagerLimboClientParser(hp.getRequest(),null);
			//
			System.out.println("p.getRequest(): " + p.getRequest());
			System.out.println("p.parseRequest(hp.getRequest()): "+p.parseRequest(hp.getRequest()));
			//
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public java.lang.String getLock(java.lang.String lock) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getLock xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<lock>"+lock+"</lock>");
		XMLRequest = XMLRequest.concat("</getLock></soapenv:Body></soapenv:Envelope>");
		System.out.println("XMLRequestrequest: "+XMLRequest);
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"getLock"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		
		System.out.println("request: "+request);
		
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port); 
			//
			System.out.println("response = this.protocol.communicateWithServer(request, this.host, this.port): "+response);
			System.out.println("--------------");
			//
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			//
			System.out.println("hp.getRequest(): " + hp.getRequest());
			System.out.println("hp.parseHeader(): "+hp.parseHeader());
			//
			LockManagerLimboClientParser p = new LockManagerLimboClientParser(hp.getRequest(),null);
			//
			System.out.println("p.getRequest(): " + p.getRequest());
			System.out.println("p.parseRequest(hp.getRequest()): "+p.parseRequest(hp.getRequest()));
			//
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public java.lang.String getLockTypes() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getLockTypes xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</getLockTypes></soapenv:Body></soapenv:Envelope>");
		String request = "";
		request = request.concat("POST "+this.path+" HTTP/1.0\r\n");
		request = request.concat("Content-Type: text/xml; charset=utf-8\r\n");
		request = request.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		request = request.concat("User-Agent: Limbo/1.0\r\n");
		request = request.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		request = request.concat("Cache-Control: no-cache\r\n");
		request = request.concat("Pragma: no-cache\r\n");
		request = request.concat("Date: "+new Date().toString()+"\r\n");
		request = request.concat("SOAPAction: "+'"'+"getLockTypes"+'"'+"\r\n");
		request = request.concat("Content-Length: "+XMLRequest.length()+"\r\n\r\n");//FIXME calculate content length
		request = request.concat(XMLRequest);
		System.out.println(request);
		System.out.println("---------------------------");
		//Open Connection -> send request -> wait for answer -> parse response
		try {
			String response = this.protocol.communicateWithServer(request, this.host, this.port);
			System.out.println(response);
			LimboClientHeaderParser hp = new LimboClientHeaderParser(response);
			System.out.println("hp: "+hp.parseHeader());
			System.out.println("hp: "+hp.getRequest());
			LockManagerLimboClientParser p = new LockManagerLimboClientParser(hp.getRequest(),null);
			System.out.println("p: " +p.parseRequest(hp.getRequest()));
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else{
				System.out.println("here error");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
