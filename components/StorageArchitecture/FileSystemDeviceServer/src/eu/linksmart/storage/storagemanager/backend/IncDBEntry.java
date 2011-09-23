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

package eu.linksmart.storage.storagemanager.backend;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

public class IncDBEntry {
	
	public static final int DIRECTORY_FILE_TYPE = 0;
	public static final int FILE_FILE_TYPE = 1;
	
	public static final int CREATE_ACCESS_TYPE = 0;
	public static final int REMOVE_ACCESS_TYPE = 1;
	public static final int WRITE_ACCESS_TYPE = 2;
	public static final int BROKEN_ACCESS_TYPE = 3;
	public static final int COPY_ACCESS_TYPE = 4;
	public static final int MOVE_ACCESS_TYPE = 5;
	public static final int CLEAR_ACCESS_TYPE = 6;
	public static final int PROPERTY_ACCESS_TYPE = 7;
	public static final int TRUNCATE_ACCESS_TYPE = 8;
	
	private String entryID;
	
	private String deviceID;
	
	private String path;
	
	private int fileType;
	
	private int accessType;
	
	private Date accessTime;
	
	private Collection<String> cleanDevices;
	
	private String destinationPath;
	
	private long startArea;
	
	private long endArea;
	
	private IncDBEntry (String entryID, String deviceID, String path, int fileType, int accessType, Collection<String> cleanDevices, long startArea, long endArea, String destinationPath, Date accessTime) {
		this.entryID = entryID;
		this.deviceID = deviceID;
		this.path = path;
		this.fileType = fileType;
		this.accessType = accessType;
		this.cleanDevices = cleanDevices;
		this.startArea = startArea;
		this.endArea = endArea;
		this.destinationPath = destinationPath;
		this.accessTime = accessTime;
	}
	
	public static IncDBEntry createCreateDirEntry (String deviceID, String path, Collection<String> cleanDevices, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, DIRECTORY_FILE_TYPE, CREATE_ACCESS_TYPE, cleanDevices, 0, 0, null, accessTime);
	}
	
	public static IncDBEntry createRemoveDirEntry(String deviceID, String path, Collection<String> cleanDevices, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, DIRECTORY_FILE_TYPE, REMOVE_ACCESS_TYPE, cleanDevices, 0, 0, null, accessTime);
	}
	
	public static IncDBEntry createBrokenDirEntry(String deviceID, String path, Collection<String> cleanDevices, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, DIRECTORY_FILE_TYPE, BROKEN_ACCESS_TYPE, cleanDevices, 0, 0, null, accessTime);
	}
	
	public static IncDBEntry createCopyDirEntry(String deviceID, String path, Collection<String> cleanDevices, String destinationPath, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, DIRECTORY_FILE_TYPE, COPY_ACCESS_TYPE, cleanDevices, 0, 0, destinationPath, accessTime);
	}
	
	public static IncDBEntry createMoveDirEntry(String deviceID, String path, Collection<String> cleanDevices, String destinationPath, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, DIRECTORY_FILE_TYPE, MOVE_ACCESS_TYPE, cleanDevices, 0, 0, destinationPath, accessTime);
	}
	
	
	
	public static IncDBEntry createCreateFileEntry(String deviceID, String path, Collection<String> cleanDevices, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, FILE_FILE_TYPE, CREATE_ACCESS_TYPE, cleanDevices, 0, 0, null, accessTime);
	}
	
	public static IncDBEntry createRemoveFileEntry(String deviceID, String path, Collection<String> cleanDevices, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, FILE_FILE_TYPE, REMOVE_ACCESS_TYPE, cleanDevices, 0, 0, null, accessTime);
	}
	
	public static IncDBEntry createBrokenFileEntry(String deviceID, String path, Collection<String> cleanDevices, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, FILE_FILE_TYPE, BROKEN_ACCESS_TYPE, cleanDevices, 0, 0, null, accessTime);
	}
	
	public static IncDBEntry createCopyFileEntry(String deviceID, String path, Collection<String> cleanDevices, String destinationPath, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, FILE_FILE_TYPE, COPY_ACCESS_TYPE, cleanDevices, 0, 0, destinationPath, accessTime);
	}
	
	public static IncDBEntry createMoveFileEntry(String deviceID, String path, Collection<String> cleanDevices, String destinationPath, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, FILE_FILE_TYPE, MOVE_ACCESS_TYPE, cleanDevices, 0, 0, destinationPath, accessTime);
	}
	
	public static IncDBEntry createWriteFileEntry(String deviceID, String path, Collection<String> cleanDevices, long startArea, long endArea, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, FILE_FILE_TYPE, WRITE_ACCESS_TYPE, cleanDevices, startArea, endArea, null, accessTime);
	}
	
	public static IncDBEntry createClearFileEntry(String deviceID, String path, Collection<String> cleanDevices, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, FILE_FILE_TYPE, CLEAR_ACCESS_TYPE, cleanDevices, 0, 0, null, accessTime);
	}
	
	public static IncDBEntry createPropertyFileEntry(String deviceID, String path, Collection<String> cleanDevices, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, FILE_FILE_TYPE, PROPERTY_ACCESS_TYPE, cleanDevices, 0, 0, null, accessTime);
	}
	
	public static IncDBEntry createTruncateFileEntry(String deviceID, String path, Collection<String> cleanDevices, long size, Date accessTime) {
		return new IncDBEntry(null, deviceID, path, FILE_FILE_TYPE, TRUNCATE_ACCESS_TYPE, cleanDevices, 0, size, null, accessTime);
	}
	
	public void setEntryID(String entryID) {
		this.entryID = entryID;
	}

	public String getEntryID() {
		return entryID;
	}
	
	public String getDeviceID() {
		return deviceID;
	}
	
	public String getPath() {
		return path;
	}

	public int getFileType() {
		return fileType;
	}

	public int getAccessType() {
		return accessType;
	}

	public Date getAccessTime() {
		return accessTime;
	}

	public Collection<String> getCleanDevices() {
		return cleanDevices;
	}

	public long getStartArea() {
		return startArea;
	}

	public long getEndArea() {
		return endArea;
	}
	
	public String getDestinationPath() {
		return destinationPath;
	}
	
	public String toString() {
		StringBuffer out = new StringBuffer(deviceID);
		out.append(";");
		out.append(path);
		out.append(";");
		out.append(fileType);
		out.append(";");
		out.append(accessType);
		out.append(";");
		out.append(accessTime.getTime());
		out.append(";");
		out.append(startArea);
		out.append(";");
		out.append(endArea);
		out.append(";");
		out.append(destinationPath);
		out.append(";");
		Iterator<String> cdIt = cleanDevices.iterator();
		while (cdIt.hasNext()) {
			out.append(cdIt.next());
			if (cdIt.hasNext()) {
				out.append(",");
			}
		}
		return out.toString();
	}
	
	public static IncDBEntry readIncDBEntry(String entry) {
		String[] fields = entry.split(";");
		String entryID = fields[0];
		String deviceID = fields[1]; 
		String path = fields[2];
		int fileType = Integer.parseInt(fields[3]);
		int accessType = Integer.parseInt(fields[4]);
		Date accessTime = new Date(Long.parseLong(fields[5]));
		long startArea = Long.parseLong(fields[6]);
		long endArea = Long.parseLong(fields[7]);
		String destinationPath = fields[8];
		Vector<String> cleanDevices = new Vector<String>();
		String[] devices = fields[9].split(",");
		for (int i=0; i<devices.length; i++) {
			cleanDevices.add(devices[i]);
		}
		return new IncDBEntry(entryID, deviceID, path, fileType, accessType, cleanDevices, startArea, endArea, destinationPath, accessTime);
	}
}
