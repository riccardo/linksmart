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

package eu.linksmart.limbo.filesystemdevice.handler;

import eu.linksmart.limbo.filesystemdevice.FileSystemDeviceOpsImpl;
import eu.linksmart.limbo.filesystemdevice.FileSystemDeviceParser;
import eu.linksmart.limbo.filesystemdevice.HeaderParser;
import eu.linksmart.limbo.filesystemdevice.StringTokenizer;

public class FileSystemDeviceSOAPHandler extends FileSystemDeviceHandler{
	
	public FileSystemDeviceSOAPHandler(FileSystemDeviceHandlers context) {
		super(context);
	}
	
	@Override
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
	
	// TODO add time measurements
	//Measurement gaging = new Measurement("C:\\LoggerOutput\\log.txt");

	public String handleRequest(String theRequest) {
		
		//assert gaging.logFrequencyResults(1);
		//TODO remove syste.out
		//System.out.println("public String handleRequest(String theRequest) called");
		
		FileSystemDeviceOpsImpl operations = super.getContext().getService().getOperations();
		HeaderParser hp = new HeaderParser(theRequest);
		String theSOAPAction = getSOAPAction(hp.getHeader());
		if(!hp.parseHeader() || theSOAPAction == null) {
			String result = "HTTP/1.1 200 Header Invalid\r\n\r\n";
			return result;
		}
		String theSOAPRequest = hp.getRequest();
		if(super.getContext().getService().hasOperation(theSOAPAction)){
			FileSystemDeviceParser p = new FileSystemDeviceParser(theSOAPRequest, theSOAPAction);
			if(p.parseRequest(theSOAPRequest)){
			if(p.getOperation().equalsIgnoreCase("clearFile") && theSOAPAction.equalsIgnoreCase("clearFile")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><clearFileResponse xmlns="+'"'+'"'+"><clearFileReturn>"+operations.clearFile(p.getArg(0).toString())+"</clearFileReturn></clearFileResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("copy") && theSOAPAction.equalsIgnoreCase("copy")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><copyResponse xmlns="+'"'+'"'+"><copyReturn>"+operations.copy(p.getArg(0).toString(), p.getArg(1).toString())+"</copyReturn></copyResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("createDirectory") && theSOAPAction.equalsIgnoreCase("createDirectory")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><createDirectoryResponse xmlns="+'"'+'"'+"><createDirectoryReturn>"+operations.createDirectory(p.getArg(0).toString())+"</createDirectoryReturn></createDirectoryResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("createFile") && theSOAPAction.equalsIgnoreCase("createFile")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><createFileResponse xmlns="+'"'+'"'+"><createFileReturn>"+operations.createFile(p.getArg(0).toString(), p.getArg(1).toString())+"</createFileReturn></createFileResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("existsPath") && theSOAPAction.equalsIgnoreCase("existsPath")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><existsPathResponse xmlns="+'"'+'"'+"><existsPathReturn>"+operations.existsPath(p.getArg(0).toString())+"</existsPathReturn></existsPathResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("getDirectoryEntries") && theSOAPAction.equalsIgnoreCase("getDirectoryEntries")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getDirectoryEntriesResponse xmlns="+'"'+'"'+"><getDirectoryEntriesReturn>"+operations.getDirectoryEntries(p.getArg(0).toString())+"</getDirectoryEntriesReturn></getDirectoryEntriesResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("getFile") && theSOAPAction.equalsIgnoreCase("getFile")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getFileResponse xmlns="+'"'+'"'+"><getFileReturn>"+operations.getFile(p.getArg(0).toString())+"</getFileReturn></getFileResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("getFreeSpace") && theSOAPAction.equalsIgnoreCase("getFreeSpace")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getFreeSpaceResponse xmlns="+'"'+'"'+"><getFreeSpaceReturn>"+operations.getFreeSpace()+"</getFreeSpaceReturn></getFreeSpaceResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("getID") && theSOAPAction.equalsIgnoreCase("getID")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getIDResponse xmlns="+'"'+'"'+"><getIDReturn>"+operations.getID()+"</getIDReturn></getIDResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("getSize") && theSOAPAction.equalsIgnoreCase("getSize")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getSizeResponse xmlns="+'"'+'"'+"><getSizeReturn>"+operations.getSize()+"</getSizeReturn></getSizeResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("getStatFS") && theSOAPAction.equalsIgnoreCase("getStatFS")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><getStatFSResponse xmlns="+'"'+'"'+"><getStatFSReturn>"+operations.getStatFS()+"</getStatFSReturn></getStatFSResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("move") && theSOAPAction.equalsIgnoreCase("move")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><moveResponse xmlns="+'"'+'"'+"><moveReturn>"+operations.move(p.getArg(0).toString(), p.getArg(1).toString())+"</moveReturn></moveResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("readFile") && theSOAPAction.equalsIgnoreCase("readFile")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><readFileResponse xmlns="+'"'+'"'+"><readFileReturn>"+operations.readFile(p.getArg(0).toString(), p.getArg(1).toString(), p.getArg(2).toString())+"</readFileReturn></readFileResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("removeDirectory") && theSOAPAction.equalsIgnoreCase("removeDirectory")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><removeDirectoryResponse xmlns="+'"'+'"'+"><removeDirectoryReturn>"+operations.removeDirectory(p.getArg(0).toString(), p.convertStringToBoolean(p.getArg(1).toString()))+"</removeDirectoryReturn></removeDirectoryResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("removeFile") && theSOAPAction.equalsIgnoreCase("removeFile")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><removeFileResponse xmlns="+'"'+'"'+"><removeFileReturn>"+operations.removeFile(p.getArg(0).toString())+"</removeFileReturn></removeFileResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("setFileProperties") && theSOAPAction.equalsIgnoreCase("setFileProperties")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><setFilePropertiesResponse xmlns="+'"'+'"'+"><createFileReturn>"+operations.setFileProperties(p.getArg(0).toString(), p.getArg(1).toString())+"</createFileReturn></setFilePropertiesResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("setFileProperty") && theSOAPAction.equalsIgnoreCase("setFileProperty")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><setFilePropertyResponse xmlns="+'"'+'"'+"><createFileReturn>"+operations.setFileProperty(p.getArg(0).toString(), p.getArg(1).toString(), p.getArg(2).toString())+"</createFileReturn></setFilePropertyResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("truncateFile") && theSOAPAction.equalsIgnoreCase("truncateFile")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><truncateFileResponse xmlns="+'"'+'"'+"><truncateFileReturn>"+operations.truncateFile(p.getArg(0).toString(), p.getArg(1).toString())+"</truncateFileReturn></truncateFileResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
				return result;
			}
			if(p.getOperation().equalsIgnoreCase("writeFile") && theSOAPAction.equalsIgnoreCase("writeFile")){
				String s = "";
				s = s.concat("<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>");
				s = s.concat("<soapenv:Envelope xmlns:soapenv="+'"'+"http://schemas.xmlsoap.org/soap/envelope/"+'"'+" xmlns:xsd="+'"'+"http://www.w3.org/2001/XMLSchema"+'"'+" xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'+">");
				s = s.concat("<soapenv:Body><writeFileResponse xmlns="+'"'+'"'+"><writeFileReturn>"+operations.writeFile(p.getArg(0).toString(), p.getArg(1).toString(), p.getArg(2).toString())+"</writeFileReturn></writeFileResponse></soapenv:Body></soapenv:Envelope>");
				String result = s;
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