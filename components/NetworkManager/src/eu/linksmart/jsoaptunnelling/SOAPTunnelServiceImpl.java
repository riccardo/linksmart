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
 * Copyright (C) 2006-2010 [Telefonica I+D]
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

/**
 * An implementation of SOAP Tunnel over TCP
 */

package eu.linksmart.jsoaptunnelling;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import eu.linksmart.network.NMResponse;

/**
 * SOAP Tunnel implementation
 */
public class SOAPTunnelServiceImpl implements SOAPTunnelService {

	private static Logger logger = Logger.getLogger(SOAPTunnelServiceImpl.class.getName());
	
	private static final int MAXNUMRETRIES = 15;
	private static final long SLEEPTIME = 20;
	private static final int BUFFSIZE = 16384;

	/**
	 * Receive data over a SOAP tunnel
	 * 
	 * @param endpoint the endpoint
	 * @param data the data
	 * @return the response
	 */
	public NMResponse receiveTunnelData(String endpoint, String data) {
		logger.debug("SOAPTunnel received this message " + data);
		NMResponse resp = new NMResponse();
		resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope "
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
			+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
			+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
			+ "<SendDataResult>" + "Error in SOAP tunneling receiveData"
			+ "</SendDataResult></SendDataResponse></soap:Body></soap:Envelope>");
		resp.setSessionID("");
		
		if (data.startsWith("GET")) {
			// It is a GET request
			try {
				URL urlEndpoint = new URL(endpoint);
				StringTokenizer token = new StringTokenizer(data, "\r\n");
				String header = "", aux = "";
				String dataproc = "";
				String url = "";
				while (token.hasMoreElements()) {
					aux = token.nextToken();
					if (aux.toLowerCase().contains("get")) {
							String parts[] = aux.toLowerCase().split(" ");
						urlEndpoint = new URL(urlEndpoint.toString() + parts[1].replace("/", ""));
						header = aux.replace(parts[1], urlEndpoint.getFile()) + "\r\n";
					}else if (aux.toLowerCase().contains("host")) {
						header = "Host: " + urlEndpoint.getHost() + ":"
							+ urlEndpoint.getPort() + "\r\n";
					}					
					else if (aux.toLowerCase().startsWith("connection")) {
						header = "Connection: close\r\n";
					}
					else if (aux.toLowerCase().startsWith("keep-alive")) {
						header = "";
					}						
					else {
						header =  "\r\n";
					}
					
					dataproc = dataproc + header;
				}
				dataproc = dataproc + "\r\n";
				
				logger.debug("Received GET request at the end of the tunnel:\n" + data);   
				Socket clientSocket = new Socket(urlEndpoint.getHost(), urlEndpoint.getPort());
				OutputStream cos = clientSocket.getOutputStream();
				cos.write(dataproc.getBytes());
				cos.flush();
				InputStream cis = clientSocket.getInputStream();
				String response = "";
				byte[] buffer = new byte[BUFFSIZE];
				
				int bytesRead = 0;
				int total = 0;
				int numRetries = 0;
				do {
					if (cis.available() == 0) {
						if (numRetries >= MAXNUMRETRIES) {
							logger.debug("Error delivering the data to destination:\n"
								+ "Data not available on service. Max Number of "
								+ "retries reached: " + MAXNUMRETRIES); 
							resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
								+ "<soap:Envelope "
								+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
								+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
								+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
								+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
								+ "<SendDataResult>" + "Data not available on service. Max Number "
								+ "of retries reached: " + MAXNUMRETRIES + "</SendDataResult>"
								+ "</SendDataResponse></soap:Body></soap:Envelope>");
							break;
						}
						
						try {
							Thread.currentThread().sleep(SLEEPTIME, 0);
						} catch (InterruptedException e) {
							logger.debug("Error delivering the data to destination:\n" 
								+ e.getMessage()); 
			  				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			  					+ "<soap:Envelope "
			  					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
			  					+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
			  					+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			  					+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
			  					+ "<SendDataResult>" + e.getMessage()
			  					+ "</SendDataResult></SendDataResponse>"
			  					+ "</soap:Body></soap:Envelope>");
						}
						++numRetries;
					}
					bytesRead = cis.read(buffer);
					if (bytesRead > 0) {
						numRetries = 0;
						response = response.concat(new String(buffer, 0, bytesRead));
						total += bytesRead;
					}
				} while (bytesRead != -1);
				
				String[] s = response.split("\r\n\r\n");
				if (s.length > 0) {
					response = s[1];	
				}
				else {
					// In case the SOAP response from the service is empty.
					response = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope "
						+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
						+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
						+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
						+ "<SendDataResult>" + "No SOAP response from the service"
						+ "</SendDataResult></SendDataResponse></soap:Body></soap:Envelope>";
				}
				
				logger.debug("Response:\n" + response);									
				resp.setData(response);
				cos.close();
				cis.close();
			} catch (MalformedURLException e) {
				logger.debug("Error delivering the data to destination:\n"
					+ e.getMessage()); 
				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope "
					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
					+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
					+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
					+ "<SendDataResult>" + e.getMessage() + "</SendDataResult>"
					+ "</SendDataResponse></soap:Body></soap:Envelope>");
			} catch (UnknownHostException e) {
				logger.debug("Error delivering the data to destination:\n"
					+ e.getMessage()); 
				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope "
					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
					+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
					+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
					+ "<SendDataResult>" + e.getMessage() + "</SendDataResult>"
					+"</SendDataResponse></soap:Body></soap:Envelope>");
			} catch (IOException e) {
				logger.debug("Error delivering the data to destination:\n" 
					+ e.getMessage()); 
				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope "
					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
					+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
					+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
					+ "<SendDataResult>" + e.getMessage() + "</SendDataResult>"
					+ "</SendDataResponse></soap:Body></soap:Envelope>");
			}
		}
		else { 
			// It is a POST request.
			try {
				URL urlEndpoint = new URL(endpoint);
				String post;
				if (urlEndpoint.getQuery() != null) { 
					post = "POST " + urlEndpoint.getPath() + "?" 
						+ urlEndpoint.getQuery() + " HTTP/1.0\r\n"; 
					}
				else {
					post = "POST " + urlEndpoint.getPath() + " HTTP/1.0\r\n";
				}
				
				if (data.contains("<ns1:SetAVTransportURI>")) {
					data = data.replace("<ns1:SetAVTransportURI>", "<ns1:SetAVTransportURI "
						+ "xmlns:ns1=\"urn:schemas-upnp-org:service:AVTransport:1\">");
				}
				
				String[] headers = null;
				if (data.contains("\r\n\r\n")) {
					headers = data.split("\r\n\r\n");
				}
				else if (data.contains("\n\n")) {
					headers = data.split("\n\n");
				}
				else {
					logger.error("Wrong headers!!!");
				}
				logger.debug("Length of headers: " + headers.length);
				StringTokenizer token = new StringTokenizer(headers[0], "\r\n");
				String header = "", aux = "";
				
				while (token.hasMoreElements()) {
					aux = token.nextToken();
					
					if (aux.contains("Content-Length")) {
						header = "Content-Length: " + headers[1].length() + "\r\n";
					}
					else if (aux.contains("content-length")) {
						header = "content-length: " + headers[1].length() + "\r\n";
					}						
					else if (aux.toLowerCase().contains("host")) {
						header = "Host: " + urlEndpoint.getHost() + ":"
							+ urlEndpoint.getPort() + "\r\n";
					}
					else {
						header = aux + "\r\n";
					}
					
					if ((aux.contains("Connection:")) || (aux.contains("connection:"))) {
						header = "";
					}
					post = post + header;
				}
				
				String SOAPMessage = post + "\r\n"+ headers[1];
				logger.debug("Received SOAP message at the end of the tunnel:\n" + SOAPMessage);   
				Socket clientSocket = new Socket(urlEndpoint.getHost(), urlEndpoint.getPort());
				OutputStream cos = clientSocket.getOutputStream();
				cos.write(SOAPMessage.getBytes());
				InputStream cis = clientSocket.getInputStream();
				String response = "";
				byte[] buffer = new byte[BUFFSIZE];
				
				int bytesRead = 0;
				int total = 0;
				int numRetries = 0;
				do {
					if (cis.available() == 0) {
						if (numRetries >= MAXNUMRETRIES) {
							logger.debug("Error delivering the data to destination:\n"
								+ "Data not available on service. Max Number of retries "
								+ "reached: " + MAXNUMRETRIES); 
							resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
								+ "<soap:Envelope "
								+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
								+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
								+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
								+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
								+ "<SendDataResult>" + "Data not available on service. Max Number "
								+ "of retries reached: " + MAXNUMRETRIES + "</SendDataResult>"
								+ "</SendDataResponse></soap:Body></soap:Envelope>");
							break;
						}
						
						try {
							Thread.currentThread().sleep(SLEEPTIME, 0);
						} catch (InterruptedException e) {
							logger.debug("Error delivering the data to destination:\n"
								+ e.getMessage()); 
			  				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			  					+ "<soap:Envelope "
			  					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
			  					+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
			  					+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			  					+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
			  					+ "<SendDataResult>" + e.getMessage() + "</SendDataResult>"
			  					+ "</SendDataResponse></soap:Body></soap:Envelope>");
						}
						++numRetries;
					}

					bytesRead = cis.read(buffer);
					if (bytesRead > 0) {
						numRetries = 0;
						response = response.concat(new String(buffer, 0, bytesRead));
						total += bytesRead;
					}
				} while (bytesRead != -1);
				
				String[] s = response.split("\r\n\r\n");
				if (s.length > 1) {
					response = s[1];	
				}
				else {
					// In case the SOAP response from the service is empty.
					response = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
						+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
						+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
						+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
						+ "<SendDataResult>" + "No SOAP response from the service"
						+ "</SendDataResult></SendDataResponse></soap:Body></soap:Envelope>";
				}
				
				logger.debug("Response:\n" + response);									
				resp.setData(response);
				cos.close();
				cis.close();
			} catch (MalformedURLException e) {
				logger.debug("Error delivering the data to destination:\n"
					+ e.getMessage()); 
				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
					+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
					+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
					+ "<SendDataResult>" + e.getMessage() + "</SendDataResult>"
					+ "</SendDataResponse></soap:Body></soap:Envelope>");
			} catch (UnknownHostException e) {
				logger.debug("Error delivering the data to destination:\n"
					+ e.getMessage()); 
				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
					+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
					+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
					+ "<SendDataResult>" + e.getMessage() + "</SendDataResult>"
					+ "</SendDataResponse></soap:Body></soap:Envelope>");
			} catch (IOException e) {
				logger.debug("Error delivering the data to destination:\n"
					+ e.getMessage()); 
				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
					+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
					+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
					+ "<SendDataResult>" + e.getMessage() + "</SendDataResult>"
					+ "</SendDataResponse></soap:Body></soap:Envelope>");
			}
		}
		return resp;
	}

	/**
	 * Main method for testing purposes
	 * 
	 * @param args the arguments
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) 
			throws IOException, InterruptedException {

		URL urlEndpoint = new URL("http://192.168.1.34:4004/gen-desc.xml");
		
		String t = "GET /gen-desc.xml HTTP/1.1\r\n"
			+ "Host: 192.168.1.34:4004\r\n\r\n";
			
		System.out.println(t.length());
		Socket clientSocket = new Socket(urlEndpoint.getHost(), urlEndpoint.getPort());
		OutputStream cos = clientSocket.getOutputStream();
		cos.write(t.getBytes());
		cos.flush();
		InputStream cis = clientSocket.getInputStream();
		BufferedInputStream bais = new BufferedInputStream(cis);
		
		String response = "";
		byte[] buffer = new byte[BUFFSIZE];
		int bytesRead = 0;
		int total = 0;
		int numRetries = 0;
		int bytesread = bais.read(buffer);
		System.out.println(new String(buffer, 0, bytesread));
		do {
			System.out.println("Entro");
			if (cis.available() == 0) {
				
				System.err.println("not available " + numRetries);
				if (numRetries >= MAXNUMRETRIES) {
					logger.debug("Error delivering the data to destination:\n" 
						+ "Data not available on service. Max Number of retries "
						+ "reached: " + MAXNUMRETRIES); 
					response = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
						+ "<soap:Envelope "
						+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
						+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
						+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
						+ "<SendDataResult>" + "Data not available on service. Max Number "
						+ "of retries reached: " + MAXNUMRETRIES + "</SendDataResult>"
						+ "</SendDataResponse></soap:Body></soap:Envelope>";
					break;
				}
				
				try {
					Thread.currentThread().sleep(SLEEPTIME, 0);
				} catch (InterruptedException e) {
					logger.debug("Error delivering the data to destination:\n"
						+ e.getMessage()); 
				}
				++numRetries;
			}
			
			bytesRead = cis.read(buffer);
			
			System.err.println("Leo =" + bytesRead);
			if (bytesRead > 0) {
				numRetries = 0;
				response = response.concat(new String(buffer, 0, bytesRead));
				total += bytesRead;
			}
		} while (bytesRead != -1);

		System.out.println(response);
	}

}
