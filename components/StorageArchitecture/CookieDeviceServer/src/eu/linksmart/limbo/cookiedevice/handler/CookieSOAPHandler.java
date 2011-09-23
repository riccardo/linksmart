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

package eu.linksmart.limbo.cookiedevice.handler;

import eu.linksmart.limbo.cookiedevice.*;

public class CookieSOAPHandler extends CookieHandler {

	public CookieSOAPHandler(CookieHandlers context) {
		super(context);
	}

	public void handle() {

		String result = this.handleRequest(super.getContext().getRequest());
		super.getContext().setResponse(result);
		if (super.getNextLayer() != null)
			super.getNextLayer().handle();
	}

	private String getSOAPAction(String headers) {
		StringTokenizer tok = new StringTokenizer(headers);
		String SOAPAction = null;
		while (tok.hasMoreTokens()) {
			String line = tok.nextToken("\n");
			if (line.toLowerCase().startsWith("soapaction")) {
				line = line.substring(13, line.length() - 2);
				SOAPAction = line;
			}
		}
		return SOAPAction;
	}

	public String handleRequest(String theRequest) {
		// TODO: set addHeader to true to get old behaviour
		boolean addHeader = false;

		CookieOpsImpl operations = super.getContext().getService()
				.getOperations();
		HeaderParser hp = new HeaderParser(theRequest);
		String theSOAPAction = getSOAPAction(hp.getHeader());
		if (!hp.parseHeader() || theSOAPAction == null) {
			String result = "HTTP/1.1 200 Header Invalid\r\n\r\n";
			return result;
		}
		String theSOAPRequest = hp.getRequest();
		if (super.getContext().getService().hasOperation(theSOAPAction)) {
			CookieParser p = new CookieParser(theSOAPRequest, theSOAPAction);
			if (p.parseRequest(theSOAPRequest)) {
				if (p.getOperation().equalsIgnoreCase("getCookieNames")
						&& theSOAPAction.equalsIgnoreCase("getKeys")) {
					String s = "";
					s = s.concat("<?xml version=" + '"' + "1.0" + '"'
							+ " encoding=" + '"' + "utf-8" + '"' + "?>");
					s = s.concat("<soapenv:Envelope xmlns:soapenv=" + '"'
							+ "http://schemas.xmlsoap.org/soap/envelope/" + '"'
							+ " xmlns:xsd=" + '"'
							+ "http://www.w3.org/2001/XMLSchema" + '"'
							+ " xmlns:xsi=" + '"'
							+ "http://www.w3.org/2001/XMLSchema-instance" + '"'
							+ ">");
					s = s.concat("<soapenv:Body><getKeysResponse xmlns="
							+ '"'
							+ '"'
							+ "><result>"
							+ operations.getCookieNames()
							+ "</result></getKeysResponse></soapenv:Body></soapenv:Envelope>");
					String result = "";
					if (addHeader) {
						result = result.concat("HTTP/1.1 200 OK\r\n");
						result = result
								.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
						result = result
								.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
						result = result.concat("Content-Length: "
								+ s.getBytes().length + "\r\n\r\n");
					}
					result = result.concat(s);
					return result;
				}
				if (p.getOperation().equalsIgnoreCase("getAllData")
						&& theSOAPAction.equalsIgnoreCase("getData")) {
					String s = "";
					s = s.concat("<?xml version=" + '"' + "1.0" + '"'
							+ " encoding=" + '"' + "utf-8" + '"' + "?>");
					s = s.concat("<soapenv:Envelope xmlns:soapenv=" + '"'
							+ "http://schemas.xmlsoap.org/soap/envelope/" + '"'
							+ " xmlns:xsd=" + '"'
							+ "http://www.w3.org/2001/XMLSchema" + '"'
							+ " xmlns:xsi=" + '"'
							+ "http://www.w3.org/2001/XMLSchema-instance" + '"'
							+ ">");
					s = s.concat("<soapenv:Body><getDataResponse xmlns="
							+ '"'
							+ '"'
							+ "><result>"
							+ operations.getAllData(p.getArg(0).toString())
							+ "</result></getDataResponse></soapenv:Body></soapenv:Envelope>");
					String result = "";
					if (addHeader) {
						result = result.concat("HTTP/1.1 200 OK\r\n");
						result = result
								.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
						result = result
								.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
						result = result.concat("Content-Length: "
								+ s.getBytes().length + "\r\n\r\n");
					}
					result = result.concat(s);
					return result;
				}
				if (p.getOperation().equalsIgnoreCase("getCookieValue")
						&& theSOAPAction.equalsIgnoreCase("getValue")) {
					String s = "";
					s = s.concat("<?xml version=" + '"' + "1.0" + '"'
							+ " encoding=" + '"' + "utf-8" + '"' + "?>");
					s = s.concat("<soapenv:Envelope xmlns:soapenv=" + '"'
							+ "http://schemas.xmlsoap.org/soap/envelope/" + '"'
							+ " xmlns:xsd=" + '"'
							+ "http://www.w3.org/2001/XMLSchema" + '"'
							+ " xmlns:xsi=" + '"'
							+ "http://www.w3.org/2001/XMLSchema-instance" + '"'
							+ ">");
					s = s.concat("<soapenv:Body><getValueResponse xmlns="
							+ '"'
							+ '"'
							+ "><result>"
							+ operations.getCookieValue(p.getArg(0).toString())
							+ "</result></getValueResponse></soapenv:Body></soapenv:Envelope>");
					String result = "";
					if (addHeader) {
						result = result.concat("HTTP/1.1 200 OK\r\n");
						result = result
								.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
						result = result
								.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
						result = result.concat("Content-Length: "
								+ s.getBytes().length + "\r\n\r\n");
					}
					result = result.concat(s);
					return result;
				}
				if (p.getOperation().equalsIgnoreCase("setCookieValue")
						&& theSOAPAction.equalsIgnoreCase("setValue")) {
					String s = "";
					s = s.concat("<?xml version=" + '"' + "1.0" + '"'
							+ " encoding=" + '"' + "utf-8" + '"' + "?>");
					s = s.concat("<soapenv:Envelope xmlns:soapenv=" + '"'
							+ "http://schemas.xmlsoap.org/soap/envelope/" + '"'
							+ " xmlns:xsd=" + '"'
							+ "http://www.w3.org/2001/XMLSchema" + '"'
							+ " xmlns:xsi=" + '"'
							+ "http://www.w3.org/2001/XMLSchema-instance" + '"'
							+ ">");
					s = s.concat("<soapenv:Body><setValueResponse xmlns="
							+ '"'
							+ '"'
							+ "><result>"
							+ operations.setCookieValue(p.getArg(0).toString(),
									p.getArg(1).toString())
							+ "</result></setValueResponse></soapenv:Body></soapenv:Envelope>");
					String result = "";
					if (addHeader) {
						result = result.concat("HTTP/1.1 200 OK\r\n");
						result = result
								.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
						result = result
								.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
						result = result.concat("Content-Length: "
								+ s.getBytes().length + "\r\n\r\n");
					}
					result = result.concat(s);
					return result;
				}
				if (p.getOperation().equalsIgnoreCase("removeEntry")
						&& theSOAPAction.equalsIgnoreCase("removeEntry")) {
					String s = "";
					s = s.concat("<?xml version=" + '"' + "1.0" + '"'
							+ " encoding=" + '"' + "utf-8" + '"' + "?>");
					s = s.concat("<soapenv:Envelope xmlns:soapenv=" + '"'
							+ "http://schemas.xmlsoap.org/soap/envelope/" + '"'
							+ " xmlns:xsd=" + '"'
							+ "http://www.w3.org/2001/XMLSchema" + '"'
							+ " xmlns:xsi=" + '"'
							+ "http://www.w3.org/2001/XMLSchema-instance" + '"'
							+ ">");
					s = s.concat("<soapenv:Body><removeEntryResponse xmlns="
							+ '"'
							+ '"'
							+ "><result>"
							+ operations.removeEntry(p.getArg(0).toString())
							+ "</result></removeEntryResponse></soapenv:Body></soapenv:Envelope>");
					String result = "";
					if (addHeader) {
						result = result.concat("HTTP/1.1 200 OK\r\n");
						result = result
								.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
						result = result
								.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
						result = result.concat("Content-Length: "
								+ s.getBytes().length + "\r\n\r\n");
					}
					result = result.concat(s);
					return result;
				} else {
					String result = "HTTP/1.1 401 Invalid Message\r\n\r\n";
					return result;
				}
			} else {
				String result = "HTTP/1.1 400 Bad Request\r\n\r\n";
				return result;
			}
		} else {
			String result = "HTTP/1.1 403 Inexistent Operation\r\n\r\n";
			return result;
		}
	}

}