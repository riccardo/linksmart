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

/**
 * LinkSmartFile.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.storage.helper;

import java.util.Dictionary;

import org.jdom.Element;

public class LinkSmartFile {

	private boolean directory;

	private String path;

	private long lastAccessTime;

	private long lastModifiedTime;

	private long creationTime;

	private long size;

	private Dictionary<String, String> properties;

	public static String seperator = "/";

	public LinkSmartFile(Element e) {
		directory = new Boolean(e.getAttributeValue("isDirectory"));
		path = convPath(e.getAttributeValue("path"));
		lastAccessTime = new Long(e.getAttributeValue("aTime"));
		lastModifiedTime = new Long(e.getAttributeValue("mTime"));
		creationTime = new Long(e.getAttributeValue("cTime"));
		if (!directory) {
			size = new Long(e.getAttributeValue("size"));
			properties = ResponseFactory.xmlDataToDictionary(e
					.getChild("properties"));
		}
	}

	/**
	 * instantiate a new LinkSmartFile initialized with the given values. This
	 * constructor is needed for some auto generated classes.
	 * 
	 * @param directory
	 *            is the stored Element a directory? If not it must be a file
	 * @param file
	 *            is the stored Element a file? If not it must be a directory
	 * @param path
	 *            The path to the file or directory on the local
	 *            FileSystemDevice
	 * @param size
	 *            The size of the file (undefined for directories)
	 * @param lastAccessTime
	 *            time of last access
	 * @param lastModifiedTime
	 *            time of last modification
	 * @param creationTime
	 *            time of creation
	 * @param properties
	 *            properties of the file (null for directories)
	 */
	public LinkSmartFile(boolean directory, boolean file, String path, long size,
			long lastAccessTime, long lastModifiedTime, long creationTime,
			Dictionary<String, String> properties) {
		this.directory = directory;
		this.path = convPath(path);
		this.size = size;
		this.lastAccessTime = lastAccessTime;
		this.lastModifiedTime = lastModifiedTime;
		this.creationTime = creationTime;
		this.properties = properties;
	}

	/**
	 * instantiate a new LinkSmartFile initialized with the given values.
	 * 
	 * @param path
	 *            The path to the file or directory on the local
	 *            FileSystemDevice
	 * @param isFile
	 *            is the stored Element a file? If not it must be a directory
	 * @param size
	 *            The size of the file (undefined for directories)
	 * @param lastAccessTime
	 *            time of last access
	 * @param lastModifiedTime
	 *            time of last modification
	 * @param creationTime
	 *            time of creation
	 * @param properties
	 *            properties of the file (null for directories)
	 */
	public LinkSmartFile(String path, boolean isFile, long size,
			long lastAccessTime, long lastModifiedTime, long creationTime,
			Dictionary<String, String> properties) {
		this(!isFile, isFile, path, size, lastAccessTime, lastModifiedTime,
				creationTime, properties);
	}

	/**
	 * Create a new LinkSmartFile denoting a directory.
	 * 
	 * @param path
	 *            path of the directory on the FileSystemDevice
	 * @param lastAccessTime
	 *            time of last access
	 * @param lastModifiedTime
	 *            time of last modification
	 * @param creationTime
	 *            time of creation
	 * @return A HydaFile denoting to the given directory
	 */
	public static LinkSmartFile createDirLinkSmartFile(String path,
			long lastAccessTime, long lastModifiedTime, long creationTime) {
		return new LinkSmartFile(path, false, 0, lastAccessTime, lastModifiedTime,
				creationTime, null);
	}

	/**
	 * Create a new LinkSmartFile denoting a file.
	 * 
	 * @param path
	 *            The path of the file on the FileSystemStorageDevice
	 * @param size
	 *            The size of the file
	 * @param lastAccessTime
	 *            time of last access
	 * @param lastModifiedTime
	 *            time of last modification
	 * @param creationTime
	 *            time of creation
	 * @param properties
	 *            properties of the file
	 * @return A LinkSmartFile denoting the given file
	 */
	public static LinkSmartFile createFileLinkSmartFile(String path, long size,
			long lastAccessTime, long lastModifiedTime, long creationTime,
			Dictionary<String, String> properties) {
		return new LinkSmartFile(path, true, size, lastAccessTime,
				lastModifiedTime, creationTime, properties);
	}

	/**
	 * Gets the directory value for this LinkSmartFile.
	 * 
	 * @return directory
	 */
	public boolean isDirectory() {
		return directory;
	}

	/**
	 * Gets the file value for this LinkSmartFile.
	 * 
	 * @return file
	 */
	public boolean isFile() {
		return !isDirectory();
	}

	/**
	 * Gets the path value for this LinkSmartFile.
	 * 
	 * @return path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Gets the size value for this LinkSmartFile.
	 * 
	 * @return size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Gets the time of last access to the entity in ms since 1.1.1970
	 * 
	 * @return time of last access
	 */
	public long getLastAccessTime() {
		return lastAccessTime;
	}

	/**
	 * Gets the time of last modification to the entity in ms since 1.1.1970
	 * 
	 * @return time of last modification
	 */
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * Gets the time of creation to the entity in ms since 1.1.1970
	 * 
	 * @return time of creation
	 */
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * Get the properties of the file. For directories this results in null.
	 * 
	 * @return the properties of the file
	 */
	public Dictionary<String, String> getProperties() {
		return properties;
	}

	/**
	 * Get the unqualified name of the file or directory.
	 * 
	 * @return the unqualified name
	 */
	public String getName() {
		int pos = path.lastIndexOf("/");
		return path.substring(pos + 1);
	}

	/**
	 * convert this LinkSmartFile into XML.
	 * 
	 * @return xml representation of this LinkSmartFile
	 */
	public Element toXML() {
		Element e = new Element("linksmartFile");
		e.setAttribute("isDirectory", "" + directory);
		e.setAttribute("path", path);
		e.setAttribute("aTime", "" + lastAccessTime);
		e.setAttribute("mTime", "" + lastModifiedTime);
		e.setAttribute("cTime", "" + creationTime);
		if (!directory) {
			e.setAttribute("size", "" + size);
			e.addContent(ResponseFactory.dictionaryToXML(getProperties()));
		}
		return e;
	}

	/**
	 * convert the given path into a valid LinkSmart Path. This means we replace any
	 * backslash by slash.
	 * 
	 * @param path
	 *            The path to be converted
	 * @return the converted path
	 */
	public static String convPath(String path) {
		return path.replaceAll("\\\\", "/");
	}

	/**
	 * given to paths this method creates a valid linksmart path in the form of
	 * parent/child.
	 * 
	 * @param parent
	 *            the first part of the path
	 * @param child
	 *            the second part of the path
	 * @return the connected path
	 */
	public static String createSubPath(String parent, String child) {
		parent = convPath(parent);
		child = convPath(child);
		while (parent.endsWith("/"))
			parent = parent.substring(0, parent.length() - 1);
		while (child.startsWith("/"))
			child = child.substring(1);
		return parent + "/" + child;
	}

	/**
	 * Like createSubPath, but the first part of the created path is the path of
	 * this LinkSmartFile. Therefore this LinkSmartFile has to refer a directory.
	 * 
	 * @param child
	 *            The second part of the path
	 * @return the connected path
	 */
	public String createSubPath(String child) {
		if (!this.isDirectory())
			return null;
		return createSubPath(path, child);
	}

	/**
	 * Get the parent of the given path. If the given path denotes to the root
	 * directory, then the root directory is returned.
	 * 
	 * @param path
	 *            The path of the file the root directory is wanted for
	 * @return the path to the parent directory
	 */
	public static String getParent(String path) {
		path = convPath(path);
		int pos = path.lastIndexOf("/");
		if (pos == 0)
			return "/";
		return path.substring(0, pos);
	}

	/**
	 * Get the parent of the path of this LinkSmartFile. If the path denotes to the
	 * root directory, then the root directory is returned.
	 * 
	 * @return the path to the parent directory
	 */
	public String getParent() {
		return getParent("" + path);
	}
}
