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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import eu.linksmart.storage.helper.Base64;
import eu.linksmart.storage.helper.BooleanResponse;
import eu.linksmart.storage.helper.ErrorCodes;
import eu.linksmart.storage.helper.LinkSmartFile;
import eu.linksmart.storage.helper.LinkSmartFileResponse;
import eu.linksmart.storage.helper.LinkSmartFileVectorResponse;
import eu.linksmart.storage.helper.LongResponse;
import eu.linksmart.storage.helper.ResponseFactory;
import eu.linksmart.storage.helper.StatFS;
import eu.linksmart.storage.helper.StatFSResponse;
import eu.linksmart.storage.helper.StringResponse;
import eu.linksmart.storage.helper.VoidResponse;

/**
 * This class represents storage in the local FileSystem of the server.
 * 
 * @author Sascha Effert <fermat@uni-paderborn.de>
 * 
 */
public class LocalFileSystemStorage extends FileSystemStorage {

	/**
	 * Standard logger .
	 */
	private static Logger logger = Logger
			.getLogger(LocalFileSystemStorage.class);

	/**
	 * The path of the directory to be exported.
	 */
	private String fsPath;

	/**
	 * The exported directory represented as File.
	 */
	private File fs;

	/**
	 * The directory containing the metadata.
	 */
	private File metaDataFS;

	/**
	 * The directory containing the data.
	 */
	private File dataFS;

	public LocalFileSystemStorage(String name, String fsPath) {
		this(name, fsPath, java.util.UUID.randomUUID().toString());
	}

	public LocalFileSystemStorage(String name, String fsPath, String id) {
		this(name, fsPath, id, java.util.UUID.randomUUID().toString());
	}

	/**
	 * create a new LocalFileSystemStorage
	 * 
	 * @param name
	 *            The name of the FileSystem
	 * @param path
	 *            The path of the directory to be exported.
	 */
	public LocalFileSystemStorage(String name, String fsPath, String id,
			String systemId) {
		super(name, LOCAL_STORAGE, id, systemId);
		this.fsPath = fsPath;
		fs = new File(fsPath);
		File f = new File(fsPath);
		logger.info("Path: " + f.getAbsolutePath());
		if (!f.exists()) {
			logger.error("path does not exist, creating");
			if (f.mkdirs()) {
				logger.debug("created");
			} else {
				logger.error("creation failed");
			}
		} else {
			if (!f.isDirectory()) {
				logger.debug("path is no directory");
				return;
			} else {
				if (!f.canWrite()) {
					logger.error("path can not been written");
					return;
				}
				if (!f.canRead()) {
					logger.error("path can not been readen");
					return;
				}
			}
		}
		dataFS = new File(fs, File.separator + "data");
		if (dataFS.exists()) {
			if (!dataFS.isDirectory()) {
				logger.error("datapath is no directory");
				return;
			} else {
				if (!dataFS.canWrite()) {
					logger.error("datapath can not been written");
					return;
				}
				if (!dataFS.canRead()) {
					logger.error("datapath can not been readen");
					return;
				}
			}
		} else {
			if (!dataFS.mkdir())
				logger.error("could not create datadir "
						+ dataFS.getAbsolutePath());
		}
		metaDataFS = new File(fs, File.separator + "metadata");
		if (metaDataFS.exists()) {
			if (!metaDataFS.isDirectory()) {
				logger.debug("metadatapath is no directory");
				return;
			} else {
				if (!metaDataFS.canWrite()) {
					logger.error("metadatapath can not been written");
					return;
				}
				if (!metaDataFS.canRead()) {
					logger.error("metadatapath can not been readen");
					return;
				}
			}
		} else {
			if (!metaDataFS.mkdir())
				logger.error("could not create metadatadir "
						+ metaDataFS.getAbsolutePath());
		}
		logger.info("FS: " + name);
		logger.info("  Data: " + dataFS.getAbsolutePath());
		logger.info("  Metadata: " + metaDataFS.getAbsolutePath());
	}

	/**
	 * Initialize the FileSystem. This method has to called after creating the
	 * object and before calling any other method.
	 * 
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	@Override
	public void init() throws IOException {
		fs = new File(fsPath);
		if (!fs.exists()) {
			throw new IOException(fsPath + " does not exist");
		}
		if (!fs.isDirectory()) {
			throw new IOException(fsPath + " is no directory");
		}
		if (!fs.canWrite()) {
			throw new IOException(fsPath + " is not writeable");
		}
	}

	/**
	 * The path of the exported directory.
	 * 
	 * @return The path of the exported directory.
	 */
	public String getFsPath() {
		return fsPath;
	}

	/**
	 * Clear the given File. This means all data inside the file will be erased.
	 * 
	 * @param path
	 *            The path of the file to be cleared.
	 */
	@Override
	public VoidResponse clearFile(String path) {
		return truncateFile(path, 0);
	}

	/**
	 * Create a new empty Directory.
	 * 
	 * @param path
	 *            The path of the directory to be created.
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	@Override
	public VoidResponse createDirectory(String path) {
		File f = new File(dataFS, path);
		File md = new File(metaDataFS, path);
		if (f.exists()) {
			return new VoidResponse(ErrorCodes.EC_PATH_EXISTS, "path " + path
					+ " exists");
		}
		if (!f.mkdirs()) {
			return new VoidResponse(ErrorCodes.EC_NO_CREATION,
					"Can not create directory " + path);
		}
		if (!md.mkdirs()) {
			return new VoidResponse(ErrorCodes.EC_MD_NO_CREATION,
					"Can not create md directory " + path);
		}
		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	/**
	 * Create a new empty file.
	 * 
	 * @param path
	 *            The path of the file to be created.
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	@Override
	public VoidResponse createFile(String path,
			Dictionary<String, String> properties) {
		File f = new File(dataFS, path);
		File md = new File(metaDataFS, path);

		Dictionary<String, String> data = properties;
		String encodingMethod = data
				.get("eu.linksmart.storage.fsd.encoding.method");
		if (encodingMethod == null) {
			encodingMethod = "text";
			data
					.put("eu.linksmart.storage.fsd.encoding.method",
							encodingMethod);
		} else if ((!encodingMethod.equalsIgnoreCase("base64"))
				&& (!encodingMethod.equalsIgnoreCase("text"))) {
			return new VoidResponse(ErrorCodes.EC_UNKNOWN_ENCODING,
					"unnokn encoding method");
		}

		if (f.exists()) {
			logger.debug(f.getAbsolutePath() + " exists...");
			return new VoidResponse(ErrorCodes.EC_PATH_EXISTS, "path " + path
					+ " exists.");
		}
		// throw new IOException("Can not create file " + path +
		// ". There is a Entry with the same name.");
		logger.debug("will create file: " + f.getAbsolutePath());
		FileWriter fw = null;
		try {
			if (!f.createNewFile()) {
				return new VoidResponse(ErrorCodes.EC_NO_CREATION,
						"Can not create file " + path);
			}
			if (!md.mkdir()) {
				f.delete();
				return new VoidResponse(ErrorCodes.EC_MD_NO_CREATION,
						"Can not create metadata directory " + path);
			}
			File propertiesFile = new File(md, "properties");
			fw = new FileWriter(propertiesFile);
			fw.write(ResponseFactory.dictionaryToXMLString(data));
			fw.close();
			logger.debug("Create File: Anythink seemed to be o.k.");
		} catch (IOException e) {
			e.printStackTrace();
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e1) {
				}
			}
			f.delete();
			File propertiesFile = new File(md, "properties");
			propertiesFile.delete();
			md.delete();
			return new VoidResponse(ErrorCodes.EC_MD_IO_EXCEPTION,
					"IOException creating metadata file: " + e.getMessage());
		}
		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	/**
	 * Destroy this FileSystem.
	 * 
	 * @param deleteData
	 *            if <code>true</code> any stored data will be deleted, else it
	 *            is kept.
	 * @throws IOException
	 *             If anything goes wrong with the underlying storage
	 */
	@Override
	public void destroy(boolean deleteData) throws IOException {
		synchronized (fs) {
			if (deleteData) {
				deleteTree(fs);
			}
		}
	}

	private void deleteTree(File dir) throws IOException {
		File[] entries = dir.listFiles();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].isDirectory())
				deleteTree(entries[i]);
			else {
				System.gc();
				int j = 0;
				while ((!entries[i].delete()) && (j < 10)) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					j++;
					System.gc();
				}
				if (entries[i].exists())
					throw new IOException("Could not delete " + entries[i].getAbsolutePath());
			}
		}
		int j = 0;
		System.gc();
		while (!dir.delete() && (j < 10)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			j++;
			System.gc();
		}
		if (dir.exists())
			throw new IOException("Could not delete " + dir.getAbsolutePath());
	}

	/**
	 * Test if a given path (file or directory) exists.
	 * 
	 * @param path
	 *            The path (file or directory) to test.
	 * @return <code>true</code> if it exists, else <code>false</code>
	 */
	@Override
	public BooleanResponse existsPath(String path) {
		File f = new File(dataFS, path);
		return new BooleanResponse(ErrorCodes.EC_NO_ERROR, null, f.exists());
	}

	/**
	 * Get all entries of the given directory.
	 * 
	 * @param path
	 *            The path to the directory to be read.
	 * @return An Array of LinkSmartFile Objects representing the entries of the
	 *         directory
	 */
	@Override
	public LinkSmartFileVectorResponse getDirectoryEntries(String path) {
		File dir = new File(dataFS, path);
		File md = new File(metaDataFS, path);
		if (!dir.isDirectory()) {
			return new LinkSmartFileVectorResponse(ErrorCodes.EC_IS_NO_DIRECTORY,
					"path " + path + " is no directory", null);
		}
		File[] elements = dir.listFiles();
		Vector<LinkSmartFile> result = new Vector<LinkSmartFile>();
		for (int i = 0; i < elements.length; i++) {
			Dictionary<String, String> properties = null;
			if (elements[i].isFile()) {
				File mdDir = new File(md, elements[i].getName());
				File propertiesFile = new File(mdDir, "properties");
				FileReader fr = null;
				try {
					fr = new FileReader(propertiesFile);
					properties = ResponseFactory.xmlToDictionary(fr);
					fr.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					if (fr != null) {
						try {
							fr.close();
						} catch (IOException e1) {
						}
					}
					return new LinkSmartFileVectorResponse(
							ErrorCodes.EC_NO_PROPERTIES,
							"propertiesFile not found: " + e.getMessage(), null);
				} catch (JDOMException e) {
					e.printStackTrace();
					return new LinkSmartFileVectorResponse(
							ErrorCodes.EC_JDOM_EXCEPTION, "Message: "
									+ e.getMessage(), null);
				} catch (IOException e) {
					e.printStackTrace();
					return new LinkSmartFileVectorResponse(
							ErrorCodes.EC_IO_EXEPTION, "IOException found: "
									+ e.getMessage(), null);
				}
			}
			result.add(new LinkSmartFile(LinkSmartFile.createSubPath(path, elements[i]
					.getName()), elements[i].isFile(), elements[i].length(), 0,
					elements[i].lastModified(), 0, properties));
		}
		return new LinkSmartFileVectorResponse(ErrorCodes.EC_NO_ERROR, null, result);
	}

	/**
	 * Get Information about a file or directory.
	 * 
	 * @param path
	 *            The path of the file or directory information about are
	 *            requested.
	 * @return A LinkSmartFile representing the file or directory
	 */
	@Override
	public LinkSmartFileResponse getFile(String path) {
		File f = new File(dataFS, path);
		if (!f.exists()) {
			return new LinkSmartFileResponse(ErrorCodes.EC_FILE_NOT_FOUND, "path "
					+ path + " does not exist. ", null);
		}
		try {
			Dictionary<String, String> properties = null;
			if (f.isFile()) {
				properties = readProperties(path);
			}
			return new LinkSmartFileResponse(ErrorCodes.EC_NO_ERROR, null,
					new LinkSmartFile(path, f.isFile(), f.length(), 0, f
							.lastModified(), 0, properties));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new LinkSmartFileResponse(ErrorCodes.EC_MD_NO_CREATION,
					"propertiesFile not found: " + e.getMessage(), null);
		} catch (JDOMException e) {
			e.printStackTrace();
			return new LinkSmartFileResponse(ErrorCodes.EC_JDOM_EXCEPTION,
					"Message: " + e.getMessage(), null);
		} catch (IOException e) {
			e.printStackTrace();
			return new LinkSmartFileResponse(ErrorCodes.EC_MD_IO_EXCEPTION,
					"Message: " + e.getMessage(), null);
		}
	}

	/**
	 * Remove a File.
	 * 
	 * @param path
	 *            The path to the file to be removed.
	 */
	@Override
	public VoidResponse removeFile(String path) {
		File f = new File(dataFS, path);
		File md = new File(metaDataFS, path);
		if (!f.isFile())
			return new VoidResponse(ErrorCodes.EC_IS_NO_FILE, "path " + path
					+ " is no file.");
		System.gc();
		int i = 0;
		
		while ((!f.delete()) && (i < 10)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			i++;
			System.gc();
		}
		
		if (f.exists())
			return new VoidResponse(ErrorCodes.EC_NO_REMOVE,
					"Could not remove file " + path);
		try {
			deleteTree(md);
		} catch (IOException e) {
			return new VoidResponse(ErrorCodes.EC_MD_IO_EXCEPTION,
					"error deleting Metadata: " + e.getMessage());
		}
		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	@Override
	public VoidResponse removeDirectory(String path, boolean recursive) {
		File md = new File(metaDataFS, path);
		if (path == null)
			return new VoidResponse(ErrorCodes.EC_REQUIRED_ARG_MISSING, "Path");
		File file = new File(dataFS, path);
		if (!file.exists())
			return new VoidResponse(ErrorCodes.EC_FILE_NOT_FOUND, "Path "
					+ path + " does not exist.");
		if (!file.isDirectory())
			return new VoidResponse(ErrorCodes.EC_IS_NO_DIRECTORY, "Path "
					+ path + " is no directory.");
		if ((!recursive) && (file.list().length != 0))
			return new VoidResponse(ErrorCodes.EC_DIR_NOT_EMPTY, "Path " + path
					+ " is not empty and I shall not remove recursively.");
		try {
			deleteTree(file);
		} catch (IOException e1) {
			return new VoidResponse(ErrorCodes.EC_IO_EXEPTION, "Message: "
					+ path);
		}
		try {
			deleteTree(md);
		} catch (IOException e1) {
			return new VoidResponse(ErrorCodes.EC_MD_IO_EXCEPTION, "Message: "
					+ path);
		}
		return new VoidResponse(0, null);
	}

	/**
	 * Write data to a file.
	 * 
	 * @param path
	 *            The path to the file to be written.
	 * @param start
	 *            The number of bytes to be seeked before writing.
	 * @param data
	 *            The data to write
	 */
	@Override
	public VoidResponse writeFile(String path, long start, String dataString) {
		if (start < 0) {
			return new VoidResponse(ErrorCodes.EC_ARG_ERROR,
					"Start mus be greater or equal 0.");
		}

		File file = new File(dataFS, path);
		if (!file.isFile()) {
			return new VoidResponse(ErrorCodes.EC_IS_NO_FILE, path
					+ " denotes to no file.");
		}
		if (!file.canWrite()) {
			return new VoidResponse(ErrorCodes.EC_SYSTEM_RIGHTS, path
					+ " is not writeable.");
		}
		String encodingMethod = "base64";
		try {
			Dictionary<String, String> properties = readProperties(path);
			String enc = properties
					.get("eu.linksmart.storage.fsd.encoding.method");
			if (enc.equalsIgnoreCase("text"))
				encodingMethod = "text";
		} catch (JDOMException e1) {
			e1.printStackTrace();
			return new VoidResponse(ErrorCodes.EC_JDOM_EXCEPTION,
					"JDomException reading Properties: " + e1.getMessage());
		} catch (IOException e1) {
			e1.printStackTrace();
			return new VoidResponse(ErrorCodes.EC_MD_IO_EXCEPTION,
					"IOException reading Properties: " + e1.getMessage());
		}
		byte[] data;
		if (encodingMethod.equalsIgnoreCase("base64")) {
			data = Base64.decode(dataString);
		} else {
			data = dataString.getBytes();
		}

		if ((start + data.length) > Integer.MAX_VALUE) {
			return new VoidResponse(ErrorCodes.EC_FILESIZE_EXCEEDED,
					"File size greater 2 GB not supported");
		}
		FileChannel fcout = null;
		RandomAccessFile fos = null;
		try {
			ByteBuffer bufferOfExistingFile = ByteBuffer.allocate(data.length);
			bufferOfExistingFile.clear();
			fos = new RandomAccessFile(file, "rw");
			fcout = fos.getChannel();
			ByteBuffer bufferToWrite = ByteBuffer.allocate(data.length);
			bufferToWrite.clear();
			bufferToWrite.put(data).flip();
			fcout.write(bufferToWrite, start);
			bufferOfExistingFile.put(bufferToWrite);
			fos.close();
			fcout.close();
			return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);

		} catch (IOException e) {
			e.printStackTrace();
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			if (fcout != null)
				try {
					fcout.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			return new VoidResponse(ErrorCodes.EC_IO_EXEPTION, "Message "
					+ e.getMessage());
		}

	}

	/**
	 * Read Data from a given File.
	 * 
	 * @param path
	 *            The path of the file to be read.
	 * @param start
	 *            The first byte to be read.
	 * @param size
	 *            The number of bytes to read.
	 * @return A Array of byte with the requested data
	 */
	@Override
	public StringResponse readFile(String path, long start, int size) {
		if (start < 0) {
			return new StringResponse(ErrorCodes.EC_ARG_ERROR,
					"Start must be greater 0", null);
		}
		if (size < 0) {
			return new StringResponse(ErrorCodes.EC_ARG_ERROR,
					"Size must be greater 0", null);
		}
		if ((start + size) > Integer.MAX_VALUE) {
			return new StringResponse(ErrorCodes.EC_FILESIZE_EXCEEDED,
					"File size greatrer 2 GB not supported", null);
		}
		File file = new File(dataFS, path);
		if (!file.isFile()) {
			return new StringResponse(ErrorCodes.EC_IS_NO_FILE, path
					+ " is no file.", null);
		}
		if (start >= file.length()) {
			return new StringResponse(ErrorCodes.EC_ARG_ERROR,
					"stert id behind end of file.", null);
		}
		if (size == 0) {
			if (file.length() < 65536) {
				size = (int) file.length();
			} else {
				size = 65536;
			}
		}
		if ((start + size) > file.length()) {
			if ((file.length() - start) > 65536) {
				size = 65536;
			} else {
				size = (int) (file.length() - start);
			}
		}

		String encodingMethod = "base64";
		try {
			Dictionary<String, String> properties = readProperties(path);
			String enc = properties
					.get("eu.linksmart.storage.fsd.encoding.method");
			if (enc.equalsIgnoreCase("text"))
				encodingMethod = "text";
		} catch (JDOMException e1) {
			e1.printStackTrace();
			return new StringResponse(ErrorCodes.EC_JDOM_EXCEPTION,
					"JDomException reading Properties: " + e1.getMessage(),
					null);
		} catch (IOException e1) {
			e1.printStackTrace();
			return new StringResponse(ErrorCodes.EC_MD_IO_EXCEPTION,
					"Message: " + e1.getMessage(), null);
		}

		BufferedInputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new StringResponse(ErrorCodes.EC_IS_NO_FILE, path
					+ " not found", null);
		}
		try {
			if (start > 0) {
				in.skip(start);
			}
			byte[] data = null;
			data = new byte[(int) size];
			int length = in.read(data, 0, (int) size);
			in.close();
			logger.debug("Read " + length + " chars");
			if (encodingMethod.equalsIgnoreCase("base64")) {
				in.close();
				return new StringResponse(ErrorCodes.EC_NO_ERROR, null, Base64
						.encode(data));
			} else {
				in.close();
				return new StringResponse(ErrorCodes.EC_NO_ERROR, null,
						new String(data));
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return new StringResponse(ErrorCodes.EC_IO_EXEPTION, path
					+ " IOException occured: " + e.getMessage(), null);
		}
	}

	@Override
	public VoidResponse copy(String sourcePath, String destinationPath) {
		File sourceFile = new File(dataFS, sourcePath);
		File mdSourceFile = new File(metaDataFS, sourcePath);
		if (!sourceFile.exists())
			return new VoidResponse(ErrorCodes.EC_FILE_NOT_FOUND, "Source "
					+ sourcePath + " does not exist");
		File destinationFile = new File(dataFS, destinationPath);
		File mdDestinationFile = new File(metaDataFS, destinationPath);
		if (destinationFile.exists()) {
			if (!destinationFile.isDirectory()) {
				return new VoidResponse(ErrorCodes.EC_IS_NO_DIRECTORY,
						"destination is a File");
			}
			File target = new File(destinationFile, sourceFile.getName());
			if (target.exists()) {
				return new VoidResponse(ErrorCodes.EC_DIR_NOT_EMPTY,
						"in destination a file " + sourceFile.getName()
								+ " exists.");
			}
			destinationFile = target;
			mdDestinationFile = new File(mdDestinationFile, sourceFile
					.getName());
		} else {
			File parent = destinationFile.getParentFile();
			if (!parent.exists()) {
				return new VoidResponse(ErrorCodes.EC_FILE_NOT_FOUND,
						"destination and parent do not exists");
			}
		}
		try {
			copy(sourceFile, destinationFile);
		} catch (IOException e) {
			e.printStackTrace();
			return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
					"IOException occured: " + e.getMessage());
		}
		try {
			copy(mdSourceFile, mdDestinationFile);
		} catch (IOException e) {
			e.printStackTrace();
			return new VoidResponse(ErrorCodes.EC_MD_IO_EXCEPTION,
					"IOException occured: " + e.getMessage());
		}
		return new VoidResponse(0, null);
	}

	private void copy(File source, File destination) throws IOException {
		if (source.isDirectory()) {
			if (!destination.mkdir())
				throw new IOException("Can not create directory "
						+ destination.getAbsolutePath());
			File[] entries = source.listFiles();
			for (int i = 0; i < entries.length; i++) {
				File eDest = new File(destination, entries[i].getName());
				copy(entries[i], eDest);
			}
		} else {
			FileChannel in = (new FileInputStream(source)).getChannel();
			FileChannel out = (new FileOutputStream(destination)).getChannel();
			try {
			in.transferTo(0, source.length(), out);
			} catch (IOException e) {
				try {
					in.close();
				} catch (IOException e1) {
				}
				try {
					out.close();
				} catch (IOException e1) {
				}
				throw e;
			}
			in.close();
			out.close();
		}
	}

	@Override
	public LongResponse getFreeSpace() {
		File f = new File(fsPath);
		long result = f.getUsableSpace();
		return new LongResponse(0, null, result);
	}

	@Override
	public LongResponse getSize() {
		File f = new File(fsPath);
		long result = f.getTotalSpace();
		return new LongResponse(0, null, result);
	}

	@Override
	public VoidResponse move(String sourcePath, String destinationPath) {
		File sourceFile = new File(dataFS, sourcePath);
		File mdSourceFile = new File(metaDataFS, sourcePath);
		if (!sourceFile.exists())
			return new VoidResponse(ErrorCodes.EC_FILE_NOT_FOUND, "Source "
					+ sourcePath + " does not exist");
		File destinationFile = new File(dataFS, destinationPath);
		File mdDestinationFile = new File(metaDataFS, destinationPath);
		if (destinationFile.exists()) {
			if (!destinationFile.isDirectory()) {
				return new VoidResponse(ErrorCodes.EC_IS_NO_DIRECTORY,
						"destination " + destinationPath + " is a File");
			}
			File target = new File(destinationFile, sourceFile.getName());
			if (target.exists()) {
				return new VoidResponse(ErrorCodes.EC_DIR_NOT_EMPTY,
						"in destination a file " + sourceFile.getName()
								+ " exists.");
			}
			destinationFile = target;
			mdDestinationFile = new File(mdDestinationFile, sourceFile
					.getName());
		} else {
			File parent = destinationFile.getParentFile();
			if (!parent.exists()) {
				return new VoidResponse(ErrorCodes.EC_FILE_NOT_FOUND,
						"destination and parent do not exists");
			}
		}
		try {
			move(sourceFile, destinationFile);
		} catch (IOException e) {
			e.printStackTrace();
			return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
					"IOException occured: " + e.getMessage());
		}
		try {
			move(mdSourceFile, mdDestinationFile);
		} catch (IOException e) {
			e.printStackTrace();
			return new VoidResponse(ErrorCodes.EC_MD_IO_EXCEPTION,
					"IOException occured: " + e.getMessage());
		}
		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	private void move(File source, File destination) throws IOException {
		if (source.isDirectory()) {
			if (!destination.mkdir())
				throw new IOException("Can not create directory "
						+ destination.getAbsolutePath());
			File[] entries = source.listFiles();
			for (int i = 0; i < entries.length; i++) {
				File eDest = new File(destination, entries[i].getName());
				move(entries[i], eDest);
			}
			if (!source.delete()) {
				throw new IOException("Could not remove Directory "
						+ source.getAbsolutePath());
			}
		} else {
			FileChannel in = (new FileInputStream(source)).getChannel();
			FileChannel out = (new FileOutputStream(destination)).getChannel();
			in.transferTo(0, source.length(), out);
			in.close();
			out.close();
			if (!source.delete()) {
				throw new IOException("Could not remove File "
						+ source.getAbsolutePath());
			}
		}
	}

	@Override
	public StatFSResponse getStatFs() {
		File f = new File(fsPath);
		StatFS stat = new StatFS(f.getTotalSpace(), f.getFreeSpace(), f
				.getUsableSpace());
		return new StatFSResponse(ErrorCodes.EC_NO_ERROR, null, stat);
	}

	@Override
	public VoidResponse truncateFile(String path, long size) {
		File f = new File(dataFS, path);
		if (!f.isFile()) {
			return new VoidResponse(ErrorCodes.EC_IS_NO_FILE, "path " + path
					+ " is no file.");
		}
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(f, "rw");
			rf.setLength(size);
			rf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if (rf != null) {
				try {
					rf.close();
				} catch (IOException e1) {
				}
			}
			return new VoidResponse(ErrorCodes.EC_IS_NO_FILE,
					"File not Found: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			if (rf != null) {
				try {
					rf.close();
				} catch (IOException e1) {
				}
			}
			return new VoidResponse(ErrorCodes.EC_IO_EXEPTION, "Message: "
					+ e.getMessage());
		}
		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	@Override
	public VoidResponse setFileProperties(String path,
			Dictionary<String, String> properties) {
		File file = new File(dataFS, path);
		if (!file.isFile()) {
			return new VoidResponse(ErrorCodes.EC_IS_NO_FILE, path
					+ " is no file.");
		}
		try {
			Dictionary<String, String> dict = properties;
			String encodingMethod = dict
					.get("eu.linksmart.storage.fsd.encoding.method");
			if ((encodingMethod != null)
					&& (!encodingMethod.equalsIgnoreCase("base64"))
					&& (!encodingMethod.equalsIgnoreCase("text"))) {
				return new VoidResponse(ErrorCodes.EC_UNKNOWN_ENCODING,
						"unnokn encoding method " + encodingMethod);
			}
			File mdFile = new File(metaDataFS, path);
			File propFile = new File(mdFile, "properties");
			Dictionary<String, String> origDict = ResponseFactory
					.xmlToDictionary(new FileReader(propFile));
			Enumeration<String> keys = dict.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String value = dict.get(key);
				origDict.put(key, value);
			}
			FileWriter out = new FileWriter(propFile);
			out.write(ResponseFactory.dictionaryToXMLString(origDict));
			out.close();
		} catch (JDOMException e) {
			e.printStackTrace();
			return new VoidResponse(ErrorCodes.EC_JDOM_EXCEPTION,
					"JDOMException occured: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
					"IOExceptio occured: " + e.getMessage());
		}

		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	@Override
	public VoidResponse setFileProperty(String path, String propertyName,
			String propertyValue) {
		File file = new File(dataFS, path);
		if (!file.isFile()) {
			return new VoidResponse(ErrorCodes.EC_IS_NO_FILE, path
					+ " is no file.");
		}
		try {
			if (propertyName
					.equalsIgnoreCase("eu.linksmart.storage.fsd.encoding.method")) {
				return new VoidResponse(ErrorCodes.EC_ARG_ERROR,
						"encoding exchange not supported");
			}
			File mdFile = new File(metaDataFS, path);
			File propFile = new File(mdFile, "properties");
			Dictionary<String, String> origDict = ResponseFactory
					.xmlToDictionary(new FileReader(propFile));
			origDict.put(propertyName, propertyValue);
			FileWriter out = new FileWriter(propFile);
			out.write(ResponseFactory.dictionaryToXMLString(origDict));
			out.close();
		} catch (JDOMException e) {
			e.printStackTrace();
			return new VoidResponse(ErrorCodes.EC_JDOM_EXCEPTION,
					"JDOMException occured: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return new VoidResponse(ErrorCodes.EC_MD_IO_EXCEPTION,
					"IOExceptio occured: " + e.getMessage());
		}

		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	private Dictionary<String, String> readProperties(String path)
			throws JDOMException, IOException {
		File md = new File(metaDataFS, path);
		File propertiesFile = new File(md, "properties");
		FileReader fr = new FileReader(propertiesFile);
		Dictionary<String, String> properties = ResponseFactory
				.xmlToDictionary(fr);
		fr.close();
		return properties;
	}

	@Override
	public void stopp() throws IOException {
	}
}
