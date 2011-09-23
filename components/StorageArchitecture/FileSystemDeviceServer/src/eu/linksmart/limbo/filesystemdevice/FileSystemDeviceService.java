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

package eu.linksmart.limbo.filesystemdevice;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import eu.linksmart.limbo.filesystemdevice.handler.*;
import eu.linksmart.limbo.filesystemdevice.upnp.FileSystemDeviceDevice;


public class FileSystemDeviceService extends FileSystemDeviceEndPoint{

	private FileSystemDeviceOpsImpl operations;
	private Vector operationsNames;
	private int serverPort;
	private String serverHost;
	private String PID;
	
	private FileSystemDeviceHandlers handlers;
	

	public FileSystemDeviceService(String serverHost, int serverPort, FileSystemDeviceDevice device) {
		this.operations = new FileSystemDeviceOpsImpl(device);
		this.operationsNames = new Vector(19);
		this.operationsNames.addElement("clearFile");
		this.operationsNames.addElement("copy");
		this.operationsNames.addElement("createDirectory");
		this.operationsNames.addElement("createFile");
		this.operationsNames.addElement("existsPath");
		this.operationsNames.addElement("getDirectoryEntries");
		this.operationsNames.addElement("getFile");
		this.operationsNames.addElement("getFreeSpace");
		this.operationsNames.addElement("getID");
		this.operationsNames.addElement("getSize");
		this.operationsNames.addElement("getStatFS");
		this.operationsNames.addElement("move");
		this.operationsNames.addElement("readFile");
		this.operationsNames.addElement("removeDirectory");
		this.operationsNames.addElement("removeFile");
		this.operationsNames.addElement("setFileProperties");
		this.operationsNames.addElement("setFileProperty");
		this.operationsNames.addElement("truncateFile");
		this.operationsNames.addElement("writeFile");
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.handlers = new FileSystemDeviceHandlers(this);	
	}

	public Vector getOperationsNames() {
		return this.operationsNames;
	}
	
	public boolean hasOperation(String operationName){
		for(int i=0;i<this.operationsNames.size();i++){
			if(((String)this.operationsNames.elementAt(i)).equalsIgnoreCase(operationName))
				return true;
		}
		return false;
	}
	
	public FileSystemDeviceOpsImpl getOperations() {
		return this.operations;
	}

	public String getServerHost() {
		return this.serverHost;
	}
	
	public int getServerPort() {
		return this.serverPort;
	}
	
	
	public String handleRequest(String request, String clientHost) {
			
		this.handlers.handle(request, clientHost);
		return this.handlers.getResponse();
}

}