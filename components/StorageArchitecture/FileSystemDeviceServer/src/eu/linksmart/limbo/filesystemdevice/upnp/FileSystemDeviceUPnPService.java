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

package eu.linksmart.limbo.filesystemdevice.upnp;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Enumeration;



import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.JDOMException;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import eu.linksmart.storage.helper.ErrorCodes;
import eu.linksmart.storage.helper.LinkSmartFile;
import eu.linksmart.storage.helper.Response;
import eu.linksmart.storage.helper.ResponseFactory;
import eu.linksmart.storage.helper.VoidResponse;

import eu.linksmart.limbo.filesystemdevice.*;
import eu.linksmart.limbo.filesystemdevice.upnp.FileSystemDevice.*;
import eu.linksmart.storage.storagemanager.backend.FileSystemStorage;




public class FileSystemDeviceUPnPService implements UPnPService {

	final private String SERVICE_ID = "urn:upnp-org:serviceId:FileSystemDevice";
	final private String SERVICE_TYPE = "urn:schemas-upnp-org:service:FileSystemDevice:1";
	final private String VERSION ="1";
	
	private propertiesValueStateVariable propertiesValue;
	private removeDirectoryReturnStateVariable removeDirectoryReturn;
	private getStatFSReturnStateVariable getStatFSReturn;
	private existsPathReturnStateVariable existsPathReturn;
	private copyReturnStateVariable copyReturn;
	private startStateVariable start;
	private createDirectoryReturnStateVariable createDirectoryReturn;
	private propertiesStateVariable properties;
	private readFileReturnStateVariable readFileReturn;
	private truncateFileReturnStateVariable truncateFileReturn;
	private createFileReturnStateVariable createFileReturn;
	private sizeStateVariable size;
	private moveReturnStateVariable moveReturn;
	private pathStateVariable path;
	private removeFileReturnStateVariable removeFileReturn;
	private getFreeSpaceReturnStateVariable getFreeSpaceReturn;
	private getFileReturnStateVariable getFileReturn;
	private sourceStateVariable source;
	private destinationStateVariable destination;
	private clearFileReturnStateVariable clearFileReturn;
	private getIDReturnStateVariable getIDReturn;
	private propertiesNameStateVariable propertiesName;
	private recursiveStateVariable recursive;
	private getDirectoryEntriesReturnStateVariable getDirectoryEntriesReturn;
	private writeFileReturnStateVariable writeFileReturn;
	private getSizeReturnStateVariable getSizeReturn;
	private dataStateVariable data;
	private UPnPStateVariable[] states;
	private HashMap actions = new HashMap();
	private FileSystemDeviceDevice device;
	private FileSystemStorage fss;
	

	public FileSystemDeviceUPnPService(FileSystemDeviceDevice device, FileSystemStorage fss) {
		this.device = device;
		this.fss = fss;
		propertiesValue = new propertiesValueStateVariable();
		removeDirectoryReturn = new removeDirectoryReturnStateVariable();
		getStatFSReturn = new getStatFSReturnStateVariable();
		existsPathReturn = new existsPathReturnStateVariable();
		copyReturn = new copyReturnStateVariable();
		start = new startStateVariable();
		createDirectoryReturn = new createDirectoryReturnStateVariable();
		properties = new propertiesStateVariable();
		readFileReturn = new readFileReturnStateVariable();
		truncateFileReturn = new truncateFileReturnStateVariable();
		createFileReturn = new createFileReturnStateVariable();
		size = new sizeStateVariable();
		moveReturn = new moveReturnStateVariable();
		path = new pathStateVariable();
		removeFileReturn = new removeFileReturnStateVariable();
		getFreeSpaceReturn = new getFreeSpaceReturnStateVariable();
		getFileReturn = new getFileReturnStateVariable();
		source = new sourceStateVariable();
		destination = new destinationStateVariable();
		clearFileReturn = new clearFileReturnStateVariable();
		getIDReturn = new getIDReturnStateVariable();
		propertiesName = new propertiesNameStateVariable();
		recursive = new recursiveStateVariable();
		getDirectoryEntriesReturn = new getDirectoryEntriesReturnStateVariable();
		writeFileReturn = new writeFileReturnStateVariable();
		getSizeReturn = new getSizeReturnStateVariable();
		data = new dataStateVariable();
		this.states = new UPnPStateVariable[]{propertiesValue,removeDirectoryReturn,getStatFSReturn,existsPathReturn,copyReturn,start,createDirectoryReturn,properties,readFileReturn,truncateFileReturn,createFileReturn,size,moveReturn,path,removeFileReturn,getFreeSpaceReturn,getFileReturn,source,destination,clearFileReturn,getIDReturn,propertiesName,recursive,getDirectoryEntriesReturn,writeFileReturn,getSizeReturn,data};

		UPnPAction existsPath = new existsPathAction(path,existsPathReturn, this);
		actions.put(existsPath.getName(),existsPath);
		UPnPAction readFile = new readFileAction(path,start,size,readFileReturn, this);
		actions.put(readFile.getName(),readFile);
		UPnPAction getSize = new getSizeAction(getSizeReturn, this);
		actions.put(getSize.getName(),getSize);
		UPnPAction setFileProperties = new setFilePropertiesAction(path,properties,createFileReturn, this);
		actions.put(setFileProperties.getName(),setFileProperties);
		UPnPAction getFreeSpace = new getFreeSpaceAction(getFreeSpaceReturn, this);
		actions.put(getFreeSpace.getName(),getFreeSpace);
		UPnPAction getID = new getIDAction(getIDReturn, this);
		actions.put(getID.getName(),getID);
		UPnPAction createFile = new createFileAction(path,properties,createFileReturn, this);
		actions.put(createFile.getName(),createFile);
		UPnPAction removeDirectory = new removeDirectoryAction(path,recursive,removeDirectoryReturn, this);
		actions.put(removeDirectory.getName(),removeDirectory);
		UPnPAction createDirectory = new createDirectoryAction(path,createDirectoryReturn, this);
		actions.put(createDirectory.getName(),createDirectory);
		UPnPAction getStatFS = new getStatFSAction(getStatFSReturn, this);
		actions.put(getStatFS.getName(),getStatFS);
		UPnPAction getFile = new getFileAction(path,getFileReturn, this);
		actions.put(getFile.getName(),getFile);
		UPnPAction writeFile = new writeFileAction(path,start,data,writeFileReturn, this);
		actions.put(writeFile.getName(),writeFile);
		UPnPAction move = new moveAction(source,destination,moveReturn, this);
		actions.put(move.getName(),move);
		UPnPAction copy = new copyAction(source,destination,copyReturn, this);
		actions.put(copy.getName(),copy);
		UPnPAction clearFile = new clearFileAction(path,clearFileReturn, this);
		actions.put(clearFile.getName(),clearFile);
		UPnPAction truncateFile = new truncateFileAction(path,size,truncateFileReturn, this);
		actions.put(truncateFile.getName(),truncateFile);
		UPnPAction getDirectoryEntries = new getDirectoryEntriesAction(path,getDirectoryEntriesReturn, this);
		actions.put(getDirectoryEntries.getName(),getDirectoryEntries);
		UPnPAction removeFile = new removeFileAction(path,removeFileReturn, this);
		actions.put(removeFile.getName(),removeFile);
		UPnPAction setFileProperty = new setFilePropertyAction(path,propertiesName,propertiesValue,createFileReturn, this);
		actions.put(setFileProperty.getName(),setFileProperty);
	}

	public UPnPAction getAction(String name) {
		return (UPnPAction)actions.get(name);
	}

	public UPnPAction[] getActions() {
		return (UPnPAction[])(actions.values()).toArray(new UPnPAction[]{});
	}

	public String getId() {
		return SERVICE_ID;
	}

	public UPnPStateVariable getStateVariable(String name) {

		if (name.equals(propertiesValue.getName())) return propertiesValue;
		else if (name.equals(removeDirectoryReturn.getName())) return removeDirectoryReturn;
		else if (name.equals(getStatFSReturn.getName())) return getStatFSReturn;
		else if (name.equals(existsPathReturn.getName())) return existsPathReturn;
		else if (name.equals(copyReturn.getName())) return copyReturn;
		else if (name.equals(start.getName())) return start;
		else if (name.equals(createDirectoryReturn.getName())) return createDirectoryReturn;
		else if (name.equals(properties.getName())) return properties;
		else if (name.equals(readFileReturn.getName())) return readFileReturn;
		else if (name.equals(truncateFileReturn.getName())) return truncateFileReturn;
		else if (name.equals(createFileReturn.getName())) return createFileReturn;
		else if (name.equals(size.getName())) return size;
		else if (name.equals(moveReturn.getName())) return moveReturn;
		else if (name.equals(path.getName())) return path;
		else if (name.equals(removeFileReturn.getName())) return removeFileReturn;
		else if (name.equals(getFreeSpaceReturn.getName())) return getFreeSpaceReturn;
		else if (name.equals(getFileReturn.getName())) return getFileReturn;
		else if (name.equals(source.getName())) return source;
		else if (name.equals(destination.getName())) return destination;
		else if (name.equals(clearFileReturn.getName())) return clearFileReturn;
		else if (name.equals(getIDReturn.getName())) return getIDReturn;
		else if (name.equals(propertiesName.getName())) return propertiesName;
		else if (name.equals(recursive.getName())) return recursive;
		else if (name.equals(getDirectoryEntriesReturn.getName())) return getDirectoryEntriesReturn;
		else if (name.equals(writeFileReturn.getName())) return writeFileReturn;
		else if (name.equals(getSizeReturn.getName())) return getSizeReturn;
		else if (name.equals(data.getName())) return data;
		return null;
	}

	public UPnPStateVariable[] getStateVariables() {
		return states;
	}

	public String getType() {
		return SERVICE_TYPE;
	}

	public String getVersion() {
		return VERSION;
	}
	
	private String convPath(String path) {
		return LinkSmartFile.convPath(path);
	}

	public java.lang.String clearFile(java.lang.String path ){
		Response r = fss.clearFile(StringEscapeUtils.unescapeXml(path));
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String copy(java.lang.String source, java.lang.String destination ){
		Response r = fss.copy(StringEscapeUtils.unescapeXml(convPath(source)), StringEscapeUtils.unescapeXml(convPath(destination)));
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String createDirectory(java.lang.String path ){
		//System.out.println("create Directory called for device " + fss.getName());
		Response r = fss.createDirectory(StringEscapeUtils.unescapeXml(path));
		if (r== null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String createFile(java.lang.String path, java.lang.String properties ){
		Dictionary<String, String> prop = null;
		try {
			prop = ResponseFactory.xmlRequestToDictionary(StringEscapeUtils.unescapeXml(properties));
		} catch (JDOMException e) {
			return ResponseFactory.createVoidResponse(ErrorCodes.EC_ARG_ERROR, "malformed Properties");
		} catch (IOException e) {
			return ResponseFactory.createVoidResponse(ErrorCodes.EC_ARG_ERROR, "malformed Properties");
		}
		Response r = fss.createFile(StringEscapeUtils.unescapeXml(convPath(path)), prop);
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String existsPath(java.lang.String path ){
		Response r = fss.existsPath(StringEscapeUtils.unescapeXml(convPath(path)));
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String getDirectoryEntries(java.lang.String path ){
		Response r = fss.getDirectoryEntries(StringEscapeUtils.unescapeXml(convPath(path)));
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String getFile(java.lang.String path ){
		Response r =  fss.getFile(StringEscapeUtils.unescapeXml(convPath(path)));
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String getFreeSpace(){
		Response r =  fss.getFreeSpace();
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String getID(){
		String id = fss.getId();
		String response = StringEscapeUtils.escapeXml(id); 
		return response;
	}

	public java.lang.String getSize(){
		Response r = fss.getSize();
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String getStatFS(){
		Response r = fss.getStatFs();
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String move(java.lang.String source, java.lang.String destination ){
		Response r = fss.move(StringEscapeUtils.unescapeXml(convPath(source)), StringEscapeUtils.unescapeXml(convPath(destination)));
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String readFile(java.lang.String path, java.lang.String start, java.lang.String size ){
		long startLong = 0;
		int sizeLong = 0;
		try {
			startLong = Long.parseLong(StringEscapeUtils.unescapeXml(start));
		} catch (Exception e) {
			return ResponseFactory.createStringResponse(ErrorCodes.EC_ARG_ERROR, "start is not a number", null);
		}
		try {
			sizeLong = Integer.parseInt(StringEscapeUtils.unescapeXml(size));
		} catch (Exception e) {
			return ResponseFactory.createStringResponse(ErrorCodes.EC_ARG_ERROR, "size is not a number", null);
		}
		Response r = fss.readFile(StringEscapeUtils.unescapeXml(convPath(path)), startLong, sizeLong);
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String removeDirectory(java.lang.String path, java.lang.Boolean recursive ){
		Response r = fss.removeDirectory(StringEscapeUtils.unescapeXml(convPath(path)), recursive);
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String removeFile(java.lang.String path ){
		Response r = fss.removeFile(StringEscapeUtils.unescapeXml(convPath(path)));
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String setFileProperties(java.lang.String path, java.lang.String properties ){
		Dictionary<String, String> prop = null;
		try {
			prop = ResponseFactory.xmlRequestToDictionary(StringEscapeUtils.unescapeXml(properties));
		} catch (JDOMException e) {
			return ResponseFactory.createVoidResponse(ErrorCodes.EC_ARG_ERROR, "malformed Properties");
		} catch (IOException e) {
			return ResponseFactory.createVoidResponse(ErrorCodes.EC_ARG_ERROR, "malformed Properties");
		}
		Response r = fss.setFileProperties(StringEscapeUtils.unescapeXml(convPath(path)), prop);
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String setFileProperty(java.lang.String path, java.lang.String propertyName, java.lang.String propertyValue ){
		Response r = fss.setFileProperty(StringEscapeUtils.unescapeXml(convPath(path)), StringEscapeUtils.unescapeXml(propertyName), StringEscapeUtils.unescapeXml(propertyValue));
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String truncateFile(java.lang.String path, java.lang.String size ){
		long sizeLong = 0;
		try {
			sizeLong = Long.parseLong(StringEscapeUtils.unescapeXml(size));
		} catch (Exception e) {
			return ResponseFactory.createStringResponse(ErrorCodes.EC_ARG_ERROR, "size is not a number", null);
		}
		Response r = fss.truncateFile(StringEscapeUtils.unescapeXml(convPath(path)), sizeLong);
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}

	public java.lang.String writeFile(java.lang.String path, java.lang.String start, java.lang.String data ){
		long startLong = 0;
		try {
			startLong = Long.parseLong(StringEscapeUtils.unescapeXml(start));
		} catch (Exception e) {
			return ResponseFactory.createVoidResponse(ErrorCodes.EC_ARG_ERROR, "start is not a number");
		}
		Response r = fss.writeFile(StringEscapeUtils.unescapeXml(convPath(path)), startLong, StringEscapeUtils.unescapeXml(data));
		if (r==null)
			return null;
		return StringEscapeUtils.escapeXml(r.toXMLString());
	}
}