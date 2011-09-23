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

import java.io.IOException;
import java.util.Dictionary;

import eu.linksmart.storage.helper.BooleanResponse;
import eu.linksmart.storage.helper.LinkSmartFileResponse;
import eu.linksmart.storage.helper.LinkSmartFileVectorResponse;
import eu.linksmart.storage.helper.LongResponse;
import eu.linksmart.storage.helper.StatFSResponse;
import eu.linksmart.storage.helper.StringResponse;
import eu.linksmart.storage.helper.VoidResponse;

/**
 * The FileSystemStorage is the base class for the backend storage of the
 * FileSystemDeviceManager. All backend has to extend this class and to be
 * registerd in the FileSystemManager. The return Type of all methods is String
 * due to Upnp-Services in linksmart project. All returned strings are based on
 * XML-Scheme and at first contain the returned errorCode which is based on
 * Linux error headers (http://www.finalcog.com/c-error-codes-include-errno). At
 * second place, they contain the returned error message. The inherited return
 * value is wrapped at last. and commented in the method comment.
 * 
 * 
 * 
 * @author Felix Dickehage <skyfox@mail.uni-paderborn.de>
 * 
 */

/**
 * @author Skyfox
 * 
 */
public abstract class FileSystemStorage {

	/**
	 * Type is LocalFileSystemStorage
	 */
	public static final int LOCAL_STORAGE = 1;
	public static final int REPLICATED_STORAGE = 2;
	public static final int STRIPED_STORAGE = 3;

	private String id;

	private String systemID;

	/**
	 * The name of the FileSystem
	 */
	private String name;

	/**
	 * The type of the class represents the implementation.
	 */
	private int type;

	/**
	 * Constructor to create a new FileSystemStorage
	 * 
	 * @param name
	 *            The name of the FileSystem
	 * @param type
	 *            The type of the FileSystem
	 * @param id
	 *            Unique file system ID.
	 * @param systemID
	 *            Unique ID in the file system.
	 */
	protected FileSystemStorage(String name, int type, String id, String systemID) {
		this.name = name;
		this.type = type;
		this.id = id;
		this.systemID = systemID;
	}

	/**
	 * Constructor with automatic determination of System ID.
	 * 
	 * @param name
	 *            The name of the FileSystem
	 * @param type
	 *            The type of the FileSystem
	 * @param id
	 *            Unique file system ID.
	 * 
	 * @see FileSystemStorage#FileSystemStorage(String, int, String, String)
	 */
	protected FileSystemStorage(String name, int type, String id) {
		this(name, type, id, java.util.UUID.randomUUID().toString());
	}

	/**
	 * Constructor with automatic determination of file system ID and system ID.
	 * 
	 * @param name
	 *            The name of the FileSystem
	 * @param type
	 *            The type of the FileSystem
	 * 
	 * @see FileSystemStorage#FileSystemStorage(String, int, String, String)
	 */
	protected FileSystemStorage(String name, int type) {
		this(name, type, java.util.UUID.randomUUID().toString());
	}
	
	/**
	 * Get Thie ID of the File System Device
	 * @return the ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get the system ID of the File System device
	 * @return the system ID
	 */
	public String getSystemID() {
		return systemID;
	}

	/**
	 * The name of the FileSystem
	 * 
	 * @return The name of the FileSystem
	 */
	public String getName() {
		return name;
	}

	/**
	 * The type of the class represents the implementation.
	 * 
	 * @return The type of the class represents the implementation.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Initialize the FileSystem. This method has to called after creating the
	 * object and before calling any other method.
	 * 
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	public abstract void init() throws IOException;

	/**
	 * Destroy this FileSystem.
	 * 
	 * @param deleteData
	 *            if <code>true</code> any stored data will be deleted, else it
	 *            is kept.
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	public abstract void destroy(boolean deleteData) throws IOException;
	
	/**
	 * Stops the Device and all subprocesses.
	 * @throws IOException
	 */
	public abstract void stopp() throws IOException;

	/**
	 * Get all entries of the given directory.
	 * 
	 * @param path
	 *            The path to the directory to be read.
	 * @return An Array of LinkSmartFile Objects representing the entries of the
	 *         directory (LinkSmartFileVectorResponse)
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	public abstract LinkSmartFileVectorResponse getDirectoryEntries(String path);

	/**
	 * Get Information about a file or directory.
	 * 
	 * @param path
	 *            The path of the file or directory information about are
	 *            requested.
	 * @return 
	 * 		A String containing path information. (LinkSmartFileResponse)
	 */
	public abstract LinkSmartFileResponse getFile(String path);

	/**
	 * Test if a given path (file or directory) exists.
	 * 
	 * @param path
	 *            The path (file or directory) to test.
	 * @return <code>true<code> if it exists, else <code>false</code> (BooleanResponse)
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	public abstract BooleanResponse existsPath(String path);

	/**
	 * Read Data from a given File.
	 * 
	 * @param path
	 *            The path of the file to be read.
	 * @param start
	 *            The first byte to be read.
	 * @param size
	 *            The number of bytes to read.
	 * @return A Array of byte with the requested data (StringResponse)
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	public abstract StringResponse readFile(String path, long start, int size);

	/**
	 * Write data to a file.
	 * 
	 * @param path
	 *            The path to the file to be written.
	 * @param start
	 *            The number of bytes to be seeked in destination file before
	 *            writing.
	 * @param data
	 *            The data to write
	 * @return success of write. (VoidResponse)
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	public abstract VoidResponse writeFile(String path, long start, String data);

	/**
	 * Clear the given File. This means all data inside the file will be erased.
	 * 
	 * @param path
	 *            The path of the file to be cleared.
	 * @return success of clearing. (VoidResponse)
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	public abstract VoidResponse clearFile(String path);

	/**
	 * Create a new empty file.
	 * 
	 * @param path
	 *            The path of the file to be created.
	 * @return success of creation (VoidResponse)
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	public abstract VoidResponse createFile(String path, Dictionary<String, String> properties);

	/**
	 * Create a new empty Directory.
	 * 
	 * @param path
	 *            The path of the directory to be created.
	 * @return success of creation (VoidResponse)
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	public abstract VoidResponse createDirectory(String path);

	/**
	 * Remove a Directory.
	 * 
	 * @param path
	 *            The path of the directory to be removed.
	 * @param recursive
	 *            if false and directory is not empty, removing will fail
	 * @return success of removing (VoidResponse)
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	public abstract VoidResponse removeDirectory(String path, boolean recursive);

	/**
	 * Remove a File or directory.
	 * 
	 * @param path
	 *            Path to the file or directory to be removed.
	 * @return success of removing (VoidResponse)
	 * 
	 */
	public abstract VoidResponse removeFile(String path);

	/**
	 * 
	 * @param sourcePath
	 * @param destinationPath
	 * @return success of copy (VoidResponse)
	 */
	public abstract VoidResponse copy(String sourcePath, String destinationPath);

	/**
	 * 
	 * @param sourcePath
	 * @param destinationPath
	 * @return success of moving (VoidResponse)
	 */
	public abstract VoidResponse move(String sourcePath, String destinationPath);

	/**
	 * 
	 * @return Wrapped Long-value. Size of FileSystem. (LongResponse)
	 */
	public abstract LongResponse getSize();

	/**
	 * 
	 * @return Free space of FileSystem. (LongResponse)
	 */
	public abstract LongResponse getFreeSpace();
	
	/**
	 * 
	 * @return Information about the FileSystem. (StatFSResponse)
	 */
	public abstract StatFSResponse getStatFs();
	
	/**
	 * Override the Properties of the given File
	 * @param path The path of the File (No directory)
	 * @param properties The new Properties
	 * @return success of overriding properties (VoidResponse)
	 */
	public abstract VoidResponse setFileProperties(String path, Dictionary<String, String> properties);

	/**
	 * Add the given Property
	 * @param path The path of the File (no directory)
	 * @param propertyName The name of the Property
	 * @param propertyValue The value of the property
	 * @return success of adding property (VoidResponse)
	 */
	public abstract VoidResponse setFileProperty(String path, String propertyName, String propertyValue);
	
	/**
	 * change the length of the given File
	 * @param path The path to the File (no directory)
	 * @param size The new size (long)
	 * @return success of changing length. (VoidResponse)
	 */
	public abstract VoidResponse truncateFile(String path, long size);
}
