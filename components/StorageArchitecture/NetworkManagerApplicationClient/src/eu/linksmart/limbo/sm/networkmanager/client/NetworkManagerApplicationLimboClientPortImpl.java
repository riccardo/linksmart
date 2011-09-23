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

import eu.linksmart.limbo.sm.networkmanager.client.types.*;

public class NetworkManagerApplicationLimboClientPortImpl implements NetworkManagerApplicationLimboClientPort{
	
	private String host;
	private int port;
	private String path; 
	

	public NetworkManagerApplicationLimboClientPortImpl(String url){
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
	
	public String createHID(long in0, int in1) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><createHID xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("</createHID></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"createHID"+'"'+"\r\n");
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
			
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public void closeSession(String in0) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><closeSession xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("</closeSession></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"closeSession"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public Vector getHIDs() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getHIDs xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</getHIDs></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"getHIDs"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return new Vector().getFromSOAPLine(p.getArg(0).toString());
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String renewHID(long in0, int in1, String in2) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><renewHID xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("<in2>"+in2+"</in2>");
		XMLRequest = XMLRequest.concat("</renewHID></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"renewHID"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String createHIDwDesc(long in0, int in1, String in2, String in3) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><createHIDwDesc xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("<in2>"+in2+"</in2>");
		XMLRequest = XMLRequest.concat("<in3>"+in3+"</in3>");
		XMLRequest = XMLRequest.concat("</createHIDwDesc></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"createHIDwDesc"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public Vector getContextHIDs(String in0, String in1) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getContextHIDs xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("</getContextHIDs></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"getContextHIDs"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return new Vector().getFromSOAPLine(p.getArg(0).toString());
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public void removeHID(String in0) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><removeHID xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("</removeHID></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"removeHID"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public String getHIDsbyDescriptionAsString(String in0) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getHIDsbyDescriptionAsString xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("</getHIDsbyDescriptionAsString></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"getHIDsbyDescriptionAsString"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public Vector getHostHIDs() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getHostHIDs xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</getHostHIDs></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"getHostHIDs"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return new Vector().getFromSOAPLine(p.getArg(0).toString());
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public Vector getHIDsbyDescription(String in0) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getHIDsbyDescription xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("</getHIDsbyDescription></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"getHIDsbyDescription"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return new Vector().getFromSOAPLine(p.getArg(0).toString());
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String createHIDwDesc(String in0, String in1) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><createHIDwDesc xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("</createHIDwDesc></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"createHIDwDesc1"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String getNMPosition() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getNMPosition xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</getNMPosition></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"getNMPosition"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String getContextHIDsAsString(String in0, String in1) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getContextHIDsAsString xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("</getContextHIDsAsString></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"getContextHIDsAsString"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public void addSessionRemoteClient(String in0, String in1, String in2) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><addSessionRemoteClient xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("<in2>"+in2+"</in2>");
		XMLRequest = XMLRequest.concat("</addSessionRemoteClient></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"addSessionRemoteClient"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public String addContext(long in0, String in1) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><addContext xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("</addContext></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"addContext"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public void setSessionParameter(String in0, String in1, String in2) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><setSessionParameter xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("<in2>"+in2+"</in2>");
		XMLRequest = XMLRequest.concat("</setSessionParameter></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"setSessionParameter"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public String renewHIDInfo(String in0, String in1, String in2) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><renewHIDInfo xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("<in2>"+in2+"</in2>");
		XMLRequest = XMLRequest.concat("</renewHIDInfo></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"renewHIDInfo"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String getHostHIDsAsString() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getHostHIDsAsString xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</getHostHIDsAsString></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"getHostHIDAsString"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public Vector synchronizeSessionsList(String in0, String in1) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><synchronizeSessionsList xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("</synchronizeSessionsList></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"synchronizeSessionsList"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return new Vector().getFromSOAPLine(p.getArg(0).toString());
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String getHIDsAsString() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getHIDsAsString xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</getHIDsAsString></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"getHIDsAsString"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String startNM() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><startNM xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</startNM></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"startNM"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public NMResponse receiveData(String in0, String in1, String in2, String in3) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><receiveData xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("<in2>"+in2+"</in2>");
		XMLRequest = XMLRequest.concat("<in3>"+in3+"</in3>");
		XMLRequest = XMLRequest.concat("</receiveData></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"receiveData"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return new NMResponse().getFromSOAPLine(p.getArg(0).toString());
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String openSession(String in0, String in1) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><openSession xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("</openSession></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"openSession"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String getSessionParameter(String in0, String in1) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><getSessionParameter xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("</getSessionParameter></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"getSessionParameter"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String createHID() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><createHID xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</createHID></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"createHID1"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return p.getArg(0).toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public void removeAllHID() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><removeAllHID xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</removeAllHID></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"removeAllHID"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return ;
			else
				return ;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
	}
	public NMResponse sendData(String in0, String in1, String in2, String in3) {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><sendData xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("<in0>"+in0+"</in0>");
		XMLRequest = XMLRequest.concat("<in1>"+in1+"</in1>");
		XMLRequest = XMLRequest.concat("<in2>"+in2+"</in2>");
		XMLRequest = XMLRequest.concat("<in3>"+in3+"</in3>");
		XMLRequest = XMLRequest.concat("</sendData></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"sendData"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
			if((p.parseRequest(hp.getRequest())&&(hp.parseHeader())))
					return new NMResponse().getFromSOAPLine(p.getArg(0).toString());
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public String stopNM() {
		String XMLRequest = new String();
		XMLRequest = XMLRequest.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>");
		XMLRequest = XMLRequest.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
		XMLRequest = XMLRequest.concat("<soapenv:Body><stopNM xmlns="+'"'+'"'+">");
		XMLRequest = XMLRequest.concat("</stopNM></soapenv:Body></soapenv:Envelope>");
		String SOAPMessage = "";
		SOAPMessage = SOAPMessage.concat("POST "+this.path+" HTTP/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Content-Type: text/xml; charset=utf-8\r\n");
		SOAPMessage = SOAPMessage.concat("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
		SOAPMessage = SOAPMessage.concat("User-Agent: Limbo/1.0\r\n");
		SOAPMessage = SOAPMessage.concat("Host: afonso.at.openlaboratory.net:8080\r\n");
		SOAPMessage = SOAPMessage.concat("Cache-Control: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("Pragma: no-cache\r\n");
		SOAPMessage = SOAPMessage.concat("SOAPAction: "+'"'+"stopNM"+'"'+"\r\n");
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
			String request = "";
			n = cis.read(buffer);
			request = request + new String(buffer, 0, n);
			while (cis.available()>0) {
				n = cis.read(buffer);
				request = request + new String(buffer, 0, n);
			}
			LimboClientHeaderParser hp = new LimboClientHeaderParser(request);
			NetworkManagerApplicationLimboClientParser p = new NetworkManagerApplicationLimboClientParser(hp.getRequest(),null);
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
