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

package eu.linksmart.limbo.storagemanagerdevice.handler;

import eu.linksmart.limbo.storagemanagerdevice.*;

public class LinkSmartServicePortSOAPHandler extends LinkSmartServicePortHandler{
	
	public LinkSmartServicePortSOAPHandler(LinkSmartServicePortHandlers context) {
		super(context);
	}
	
	public void handle() {
		
		String result = this.handleRequest(super.getContext().getRequest());
		super.getContext().setResponse(result);
		if(super.getNextLayer() != null)
			super.getNextLayer().handle();
	}

	private String getSOAPAction(String headers) {
		StringTokenizer tok = new StringTokenizer(headers);
		String SOAPAction = null;
		while(tok.hasMoreTokens()) {
			String line = tok.nextToken("\n");
			if(line.toLowerCase().startsWith("soapaction")) {
				line = line.substring(13,line.length()-2);
				SOAPAction = line;
			}
		}
		return SOAPAction;
	}
	
	public String handleRequest(String theRequest) {

		LinkSmartServicePortOpsImpl operations = super.getContext().getService().getOperations();
		HeaderParser hp = new HeaderParser(theRequest);
		String theSOAPAction = getSOAPAction(hp.getHeader());
		if(!hp.parseHeader() || theSOAPAction == null) {
			String result = "HTTP/1.1 200 Header Invalid\r\n\r\n";
			return result;
		}
		String theSOAPRequest = hp.getRequest();
		if(super.getContext().getService().hasOperation(theSOAPAction)){
			LinkSmartServicePortParser p = new LinkSmartServicePortParser(theSOAPRequest, theSOAPAction);
			if(p.parseRequest(theSOAPRequest)){
			if(p.getOperation().equalsIgnoreCase("GetLinkSmartID") && theSOAPAction.equalsIgnoreCase("GetLinkSmartID")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getLinkSmartIDResponse xmlns="+'"'+'"'+"><LinkSmartID>"+operations.GetLinkSmartID()+"</LinkSmartID></getLinkSmartIDResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("CreateWS") && theSOAPAction.equalsIgnoreCase("CreateWS")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><createWSResponse xmlns="+'"'+'"'+"><WSEndpoint>"+operations.CreateWS()+"</WSEndpoint></createWSResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("GetStatus") && theSOAPAction.equalsIgnoreCase("GetStatus")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getStatusResponse xmlns="+'"'+'"'+"><Status>"+operations.GetStatus()+"</Status></getStatusResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("GetProperty") && theSOAPAction.equalsIgnoreCase("GetProperty")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getPropertyResponse xmlns="+'"'+'"'+"><PropertyValue>"+operations.GetProperty(p.getArg(0).toString())+"</PropertyValue></getPropertyResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("GetHasError") && theSOAPAction.equalsIgnoreCase("GetHasError")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getHasErrorResponse xmlns="+'"'+'"'+"><HasError>"+operations.GetHasError()+"</HasError></getHasErrorResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("GetErrorMessage") && theSOAPAction.equalsIgnoreCase("GetErrorMessage")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getErrorMessageResponse xmlns="+'"'+'"'+"><ErrorMessage>"+operations.GetErrorMessage()+"</ErrorMessage></getErrorMessageResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("GetDiscoveryInfo") && theSOAPAction.equalsIgnoreCase("GetDiscoveryInfo")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getDiscoveryInfoResponse xmlns="+'"'+'"'+"><DiscoveryInfo>"+operations.GetDiscoveryInfo()+"</DiscoveryInfo></getDiscoveryInfoResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("GetDACEndpoint") && theSOAPAction.equalsIgnoreCase("GetDACEndpoint")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getDACEndpointResponse xmlns="+'"'+'"'+"><DACEndpoint>"+operations.GetDACEndpoint()+"</DACEndpoint></getDACEndpointResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("GetWSEndpoint") && theSOAPAction.equalsIgnoreCase("GetWSEndpoint")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getWSEndpointResponse xmlns="+'"'+'"'+"><WSEndpoint>"+operations.GetWSEndpoint()+"</WSEndpoint></getWSEndpointResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("GetLinkSmartWSEndpoint") && theSOAPAction.equalsIgnoreCase("GetLinkSmartWSEndpoint")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getLinkSmartWSEndpointResponse xmlns="+'"'+'"'+"><LinkSmartWSEndpoint>"+operations.GetLinkSmartWSEndpoint()+"</LinkSmartWSEndpoint></getLinkSmartWSEndpointResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("GetWSDL") && theSOAPAction.equalsIgnoreCase("GetWSDL")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getWSDLResponse xmlns="+'"'+'"'+"><wsdl>"+operations.GetWSDL()+"</wsdl></getWSDLResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("SetLinkSmartID") && theSOAPAction.equalsIgnoreCase("SetLinkSmartID")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><setLinkSmartIDResponse xmlns="+'"'+'"'+">");
			operations.SetLinkSmartID(p.getArg(0).toString());
			 s=s.concat("</setLinkSmartIDResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("SetStatus") && theSOAPAction.equalsIgnoreCase("SetStatus")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><setStatusResponse xmlns="+'"'+'"'+">");
			operations.SetStatus(p.getArg(0).toString());
			 s=s.concat("</setStatusResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("SetDACEndpoint") && theSOAPAction.equalsIgnoreCase("SetDACEndpoint")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><setDACEndpointResponse xmlns="+'"'+'"'+">");
			operations.SetDACEndpoint(p.getArg(0).toString());
			 s=s.concat("</setDACEndpointResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("SetProperty") && theSOAPAction.equalsIgnoreCase("SetProperty")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><setPropertyResponse xmlns="+'"'+'"'+">");
			operations.SetProperty(p.getArg(0).toString(), p.getArg(1).toString());
			 s=s.concat("</setPropertyResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("Stop") && theSOAPAction.equalsIgnoreCase("Stop")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><stopResponse xmlns="+'"'+'"'+">");
			operations.Stop();
			 s=s.concat("</stopResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("StopWS") && theSOAPAction.equalsIgnoreCase("StopWS")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><stopWSResponse xmlns="+'"'+'"'+">");
			operations.StopWS();
			 s=s.concat("</stopWSResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("StopLinkSmartWS") && theSOAPAction.equalsIgnoreCase("StopLinkSmartWS")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><stopLinkSmartWSResponse xmlns="+'"'+'"'+">");
			operations.StopLinkSmartWS();
			 s=s.concat("</stopLinkSmartWSResponse></soapenv:Body></soapenv:Envelope>");
				String result = "HTTP/1.1 200 OK\r\n";
				result = result.concat("Server: Limbo Server 1.0 (LinkSmart x86 java/1.5.0_06\r\n");
				result = result.concat("Content-Type: text/xml; charset=utf-8\r\nConnection: close\r\n");
				result = result.concat("Content-Length: "+s.getBytes().length+"\r\n\r\n");
				result = result.concat(s);
				return result;
			}
			else{
				String result = "HTTP/1.1 401 Invalid Message\r\n\r\n";
				return result;
			}
		}
		else{
			String result = "HTTP/1.1 400 Bad Request\r\n\r\n";
     			return result;
		}
	}
	else{
		String result = "HTTP/1.1 403 Inexistent Operation\r\n\r\n";
		return result;
	}	
}

}