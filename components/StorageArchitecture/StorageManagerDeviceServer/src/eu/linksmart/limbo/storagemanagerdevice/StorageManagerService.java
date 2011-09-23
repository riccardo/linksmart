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

package eu.linksmart.limbo.storagemanagerdevice;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import eu.linksmart.limbo.storagemanagerdevice.handler.*;


public class StorageManagerService extends StorageManagerEndPoint{

	private StorageManagerOpsImpl operations;
	private Vector operationsNames;
	private int serverPort;
	private String serverHost;
	private String PID;
	
	private StorageManagerHandlers handlers;
	

	public StorageManagerService(String serverHost, int serverPort) {
		this.operations = new StorageManagerOpsImpl();
		this.operationsNames = new Vector(9);
		this.operationsNames.addElement("createFileSystemDevice");
		this.operationsNames.addElement("createFileSystemDeviceLocal");
		this.operationsNames.addElement("deleteFileSystemDevice");
		this.operationsNames.addElement("deleteFileSystemDeviceLocal");
		this.operationsNames.addElement("getSupportedFileSystemDevices");
		this.operationsNames.addElement("getFileSystemDevices");
		this.operationsNames.addElement("findFileSystemDevice");
		this.operationsNames.addElement("updateFileSystemDevice");
		this.operationsNames.addElement("updateFileSystemDeviceLocal");
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.handlers = new StorageManagerHandlers(this);	
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
	
	public StorageManagerOpsImpl getOperations() {
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