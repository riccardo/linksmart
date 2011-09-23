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

package eu.linksmart.limbo.filesystemdevice.client;

import java.io.File;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;

import eu.linksmart.storage.helper.BooleanResponse;
import eu.linksmart.storage.helper.LinkSmartFile;
import eu.linksmart.storage.helper.LinkSmartFileResponse;
import eu.linksmart.storage.helper.LinkSmartFileVectorResponse;
import eu.linksmart.storage.helper.LongResponse;
import eu.linksmart.storage.helper.ResponseFactory;

public class LinkSmartSMFile {
	
	private FileSystemDeviceLimboClientPortImpl connection;
	private LinkSmartFile myData;
	private String path;
	private String wsAdress;
	
	public LinkSmartSMFile (String wsAdress, String path) throws LinkSmartIOException {
		this(wsAdress, new FileSystemDeviceLimboClientPortImpl(wsAdress), path);
	}
	
	private LinkSmartSMFile(String wsAdress, FileSystemDeviceLimboClientPortImpl connection, String path) throws LinkSmartIOException {
		this.connection = connection;
		this.path = path;
		this.wsAdress = wsAdress;
		String response = connection.getFile(path);
		response = StringEscapeUtils.unescapeXml(response);
		LinkSmartFileResponse hfResponse = ResponseFactory.readLinkSmartFileResponse(response);
		if (hfResponse.getErrorCode() != 0) {
			throw new LinkSmartIOException("Error " + hfResponse.getErrorCode() + " getting file from Server, message: " + hfResponse.getErrorMessage());
		}
		myData = hfResponse.getResult();
	}
	
	public boolean canExecute() {
		return false;
	}
	
	public boolean canRead() {
		return true;
	}
	
	public boolean canWrite() {
		return true;
	}
	
	public int compareTo(LinkSmartSMFile otherFile) {
		int ws = wsAdress.compareTo(otherFile.getWsAdress());
		if (ws!=0)
			return ws;
		return path.compareTo(otherFile.getPath());
	}
	
	public boolean createNewFile() throws LinkSmartIOException {
		if (exists()) {
			return false;
		}
		String response = connection.createFile(path, "");
		response = StringEscapeUtils.unescapeXml(response);
		BooleanResponse br = ResponseFactory.readBooleanResponse(response);
		if (br.getErrorCode() != 0) {
			throw new LinkSmartIOException("Error " + br.getErrorCode() + " creating file on Server, message: " + br.getErrorMessage());
		}
		return br.getResult();
	}
	
	public boolean delete() throws LinkSmartIOException {
		String response = connection.removeFile(path);
		response = StringEscapeUtils.unescapeXml(response);
		BooleanResponse br = ResponseFactory.readBooleanResponse(response);
		if (br.getErrorCode() != 0) {
			throw new LinkSmartIOException("Error " + br.getErrorCode() + " deleting file on Server, message: " + br.getErrorMessage());
		}
		return br.getResult();
	}
	
	public boolean equals(Object o) {
		if (o==null)
			return false;
		if (!(o instanceof LinkSmartSMFile))
			return false;
		LinkSmartSMFile other = (LinkSmartSMFile) o;
		if (!wsAdress.equals(other.getWsAdress()))
			return false;
		return path.equals(other.getPath());
	}
	
	public boolean exists() {
		return !(myData == null);
	}
	
	public long getFreeSpace() throws LinkSmartIOException {
		String response = connection.getFreeSpace();
		response = StringEscapeUtils.unescapeXml(response);
		LongResponse br = ResponseFactory.readLongResponse(response);
		if (br.getErrorCode() != 0) {
			throw new LinkSmartIOException("Error " + br.getErrorCode() + " checking free space on Server, message: " + br.getErrorMessage());
		}
		return br.getResult();
	}
	
	public String getName() {
		String[] names = path.split("/");
		String name = names[names.length - 1];
		names = name.split("\\\\");
		name = names[names.length - 1];
		return name;
	}
	
	public String getParent() {
		if (path.equals("/"))
			return null;
		if (path.equals("\\"))
			return null;
		File f = new File(path);
		String parentPath = f.getParent();
		return parentPath;
	}
	
	public LinkSmartSMFile getParentFile() throws LinkSmartIOException {
		String parentPath = getParent();
		if (parentPath == null)
			return null;
		return new LinkSmartSMFile(wsAdress, connection, parentPath);
	}
	
	public String getPath() {
		return path;
	}
	
	public long getTotalSpace() throws LinkSmartIOException {
		String response = connection.getSize();
		response = StringEscapeUtils.unescapeXml(response);
		LongResponse br = ResponseFactory.readLongResponse(response);
		if (br.getErrorCode() != 0) {
			throw new LinkSmartIOException("Error " + br.getErrorCode() + " checking total space on Server, message: " + br.getErrorMessage());
		}
		return br.getResult();
	}
	
	public long getUsableSpace() throws LinkSmartIOException {
		return getFreeSpace();
	}
	
	public String getWsAdress() {
		return wsAdress;
	}
	
	public boolean isDirectory() {
		if (myData == null) 
			return false;
		return myData.isDirectory();
	}
	
	public boolean isFile() {
		if (myData == null) 
			return false;
		return myData.isFile();
	}
	
	public boolean isHidden() {
		return false;
	}
	
	public long length() {
		if (myData == null)
			return 0;
		return myData.getSize();
	}
	
	public String[] list() throws LinkSmartIOException {
		LinkSmartSMFile[] files = listFiles();
		Vector<String> result = new Vector<String>();
		for (int i=0; i<files.length; i++) {
			result.add(files[i].getName());
		}
		return (String[]) result.toArray();
	}
	
	public LinkSmartSMFile[] listFiles() throws LinkSmartIOException {
		if (!isDirectory()) {
			return null;
		}
		String response = connection.getDirectoryEntries(path);
		response = StringEscapeUtils.unescapeXml(response);
		LinkSmartFileVectorResponse r = ResponseFactory.readLinkSmartFileVectorResponse(response);
		if (r.getErrorCode() != 0) {
			throw new LinkSmartIOException("Error " + r.getErrorCode() + " checking total space on Server, message: " + r.getErrorMessage());
		}
		Vector<LinkSmartSMFile> result = new Vector<LinkSmartSMFile>();
		for (LinkSmartFile hf:r.getResult()) {
			result.add(new LinkSmartSMFile(wsAdress, connection, hf.getPath()));
		}
		return (LinkSmartSMFile[]) result.toArray();
	}
	
	public boolean mkdir() throws LinkSmartIOException {
		if (exists())
			return false;
		String response = connection.createDirectory(path);
		response = StringEscapeUtils.unescapeXml(response);
		BooleanResponse r = ResponseFactory.readBooleanResponse(response);
		if (r.getErrorCode() != 0) {
			throw new LinkSmartIOException("Error " + r.getErrorCode() + " checking total space on Server, message: " + r.getErrorMessage());
		}
		return r.getResult();
	}
	
	FileSystemDeviceLimboClientPortImpl getConnection() {
		return connection;
	}
}
