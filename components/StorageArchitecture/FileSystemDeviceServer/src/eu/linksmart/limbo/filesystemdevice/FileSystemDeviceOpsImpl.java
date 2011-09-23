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


import java.util.Hashtable;
 import java.util.Dictionary;

import eu.linksmart.limbo.filesystemdevice.upnp.*;
import eu.linksmart.limbo.filesystemdevice.upnp.FileSystemDevice.*;

/**
 * FileSystemDeviceOpsImpl is generated by Limbo
 *
 * 
 */
public class FileSystemDeviceOpsImpl {


	private FileSystemDeviceDevice device;
	private FileSystemDeviceUPnPService upnpService;
	clearFileAction clearfile;
	copyAction copy;
	createDirectoryAction createdirectory;
	createFileAction createfile;
	existsPathAction existspath;
	getDirectoryEntriesAction getdirectoryentries;
	getFileAction getfile;
	getFreeSpaceAction getfreespace;
	getIDAction getid;
	getSizeAction getsize;
	getStatFSAction getstatfs;
	moveAction move;
	readFileAction readfile;
	removeDirectoryAction removedirectory;
	removeFileAction removefile;
	setFilePropertiesAction setfileproperties;
	setFilePropertyAction setfileproperty;
	truncateFileAction truncatefile;
	writeFileAction writefile;
	public FileSystemDeviceOpsImpl(FileSystemDeviceDevice device){
		this.device = device;
		this.upnpService = (FileSystemDeviceUPnPService)this.device.getService("urn:upnp-org:serviceId:FileSystemDevice");		
		this.clearfile = (clearFileAction)this.upnpService.getAction("clearFile");
		this.copy = (copyAction)this.upnpService.getAction("copy");
		this.createdirectory = (createDirectoryAction)this.upnpService.getAction("createDirectory");
		this.createfile = (createFileAction)this.upnpService.getAction("createFile");
		this.existspath = (existsPathAction)this.upnpService.getAction("existsPath");
		this.getdirectoryentries = (getDirectoryEntriesAction)this.upnpService.getAction("getDirectoryEntries");
		this.getfile = (getFileAction)this.upnpService.getAction("getFile");
		this.getfreespace = (getFreeSpaceAction)this.upnpService.getAction("getFreeSpace");
		this.getid = (getIDAction)this.upnpService.getAction("getID");
		this.getsize = (getSizeAction)this.upnpService.getAction("getSize");
		this.getstatfs = (getStatFSAction)this.upnpService.getAction("getStatFS");
		this.move = (moveAction)this.upnpService.getAction("move");
		this.readfile = (readFileAction)this.upnpService.getAction("readFile");
		this.removedirectory = (removeDirectoryAction)this.upnpService.getAction("removeDirectory");
		this.removefile = (removeFileAction)this.upnpService.getAction("removeFile");
		this.setfileproperties = (setFilePropertiesAction)this.upnpService.getAction("setFileProperties");
		this.setfileproperty = (setFilePropertyAction)this.upnpService.getAction("setFileProperty");
		this.truncatefile = (truncateFileAction)this.upnpService.getAction("truncateFile");
		this.writefile = (writeFileAction)this.upnpService.getAction("writeFile");

	}

	public java.lang.String clearFile(java.lang.String path ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		try{
			result = this.clearfile.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("clearfilereturn").toString();
	}

	public java.lang.String copy(java.lang.String source, java.lang.String destination ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("source",source);
		args.put("destination",destination);
		try{
			result = this.copy.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("copyreturn").toString();
	}

	public java.lang.String createDirectory(java.lang.String path ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		try{
			result = this.createdirectory.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("createdirectoryreturn").toString();
	}

	public java.lang.String createFile(java.lang.String path, java.lang.String properties ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		args.put("properties",properties);
		try{
			result = this.createfile.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("createfilereturn").toString();
	}

	public java.lang.String existsPath(java.lang.String path ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		try{
			result = this.existspath.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("existspathreturn").toString();
	}

	public java.lang.String getDirectoryEntries(java.lang.String path ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		try{
			result = this.getdirectoryentries.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("getdirectoryentriesreturn").toString();
	}

	public java.lang.String getFile(java.lang.String path ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		try{
			result = this.getfile.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("getfilereturn").toString();
	}

	public java.lang.String getFreeSpace(){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		try{
			result = this.getfreespace.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("getfreespacereturn").toString();
	}

	public java.lang.String getID(){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		try{
			result = this.getid.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("getidreturn").toString();
	}

	public java.lang.String getSize(){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		try{
			result = this.getsize.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("getsizereturn").toString();
	}

	public java.lang.String getStatFS(){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		try{
			result = this.getstatfs.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("getstatfsreturn").toString();
	}

	public java.lang.String move(java.lang.String source, java.lang.String destination ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("source",source);
		args.put("destination",destination);
		try{
			result = this.move.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("movereturn").toString();
	}

	public java.lang.String readFile(java.lang.String path, java.lang.String start, java.lang.String size ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		args.put("start",start);
		args.put("size",size);
		try{
			result = this.readfile.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("readfilereturn").toString();
	}

	public java.lang.String removeDirectory(java.lang.String path, java.lang.Boolean recursive ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		args.put("recursive",recursive);
		try{
			result = this.removedirectory.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("removedirectoryreturn").toString();
	}

	public java.lang.String removeFile(java.lang.String path ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		try{
			result = this.removefile.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("removefilereturn").toString();
	}

	public java.lang.String setFileProperties(java.lang.String path, java.lang.String properties ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		args.put("properties",properties);
		try{
			result = this.setfileproperties.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("createfilereturn").toString();
	}

	public java.lang.String setFileProperty(java.lang.String path, java.lang.String propertiesName, java.lang.String propertiesValue ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		args.put("propertiesName",propertiesName);
		args.put("propertiesValue",propertiesValue);
		try{
			result = this.setfileproperty.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("createfilereturn").toString();
	}

	public java.lang.String truncateFile(java.lang.String path, java.lang.String size ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		args.put("size",size);
		try{
			result = this.truncatefile.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("truncatefilereturn").toString();
	}

	public java.lang.String writeFile(java.lang.String path, java.lang.String start, java.lang.String data ){
		Hashtable args = new Hashtable();
		Dictionary result = null;
		args.put("path",path);
		args.put("start",start);
		args.put("data",data);
		try{
			result = this.writefile.invoke(args);
		}catch(Exception e){
			e.printStackTrace();
		}return result.get("writefilereturn").toString();
	}



}