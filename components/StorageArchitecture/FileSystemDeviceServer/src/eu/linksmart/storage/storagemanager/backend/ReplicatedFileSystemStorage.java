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
 * 
 */
package eu.linksmart.storage.storagemanager.backend;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import eu.linksmart.storage.helper.BooleanResponse;
import eu.linksmart.storage.helper.ErrorCodes;
import eu.linksmart.storage.helper.LinkSmartFile;
import eu.linksmart.storage.helper.LinkSmartFileResponse;
import eu.linksmart.storage.helper.LinkSmartFileVectorResponse;
import eu.linksmart.storage.helper.LongResponse;
import eu.linksmart.storage.helper.Response;
import eu.linksmart.storage.helper.StatFSResponse;
import eu.linksmart.storage.helper.StringResponse;
import eu.linksmart.storage.helper.VoidResponse;

import eu.linksmart.limbo.filesystemdevice.client.LinkSmartFSDConnector;
import eu.linksmart.limbo.filesystemdevice.upnp.FileSystemDeviceUPnPActivator;

/**
 * @author fermat
 * 
 */
public class ReplicatedFileSystemStorage extends FileSystemStorage implements
		Runnable {

	private abstract class ReadAction {
		public String path = null;
		public long start = -1;
		public int size = -1;

		public abstract Response action(LinkSmartFSDConnector con);
	}

	private abstract class WriteAction {
		public String path = null;
		public long start = -1;
		public long size = -1;
		public String deviceID = null;
		public String destPath = null;
		Dictionary<String, String> properties = null;
		public boolean recursive = false;
		public String propKey = null;
		public String propVal = null;
		public String data = null;

		public abstract VoidResponse action(LinkSmartFSDConnector con);

		public abstract IncDBEntry createEntry(String deviceId,
				Collection<String> cleanDevices, Date accessTime);

		public abstract IncDBEntry brokenEntry(String deviceId,
				Collection<String> cleanDevices, Date accessTime);
	}

	private static Logger logger = Logger
			.getLogger(ReplicatedFileSystemStorage.class.getName());

	private final static String BASE_PATH = "FileSystemDeviceServer/var";

	private ReentrantReadWriteLock confLock;

	private String varPath;

	private String inconsistencyDBPath;

	private boolean stopp = false;

	private boolean stopped = false;

	/**
	 * Contains the IDs of the backend devices
	 */
	//private Collection<String> devices;

	private Hashtable<String, LinkSmartFSDConnector> connections;

	private Hashtable<String, LinkSmartFSDConnector> cleanConnections;

	private Hashtable<String, LinkSmartFSDConnector> dirtyConnections;

	private boolean available;

	private boolean dirty;

	private InconsistencyDB idb;

	// Constuctor
	public ReplicatedFileSystemStorage(String name, Collection<String> devices,
			String id, String systemId) {
		super(name, REPLICATED_STORAGE, id, systemId);
		logger.debug("Constructor:");
		logger.debug("  Name:  " + name);
		logger.debug("  ID:    " + id);
		logger.debug("  SysID: " + systemId);
		for (String device : devices) {
			logger.debug("  Dev:   " + device);
		}
		varPath = BASE_PATH + "/" + id + "/";
		inconsistencyDBPath = varPath + "/incDB/";
		File f = new File(BASE_PATH);
		if (!f.exists())
			f.mkdirs();
		f = new File(varPath);
		if (!f.exists())
			f.mkdirs();
		idb = new InconsistencyDB(inconsistencyDBPath);

		//this.devices = devices;
		connections = new Hashtable<String, LinkSmartFSDConnector>();
		cleanConnections = new Hashtable<String, LinkSmartFSDConnector>();
		dirtyConnections = new Hashtable<String, LinkSmartFSDConnector>();
		for (String backId : devices) {
			LinkSmartFSDConnector bc = new LinkSmartFSDConnector(backId,
					FileSystemDeviceUPnPActivator.getNMurl(),
					FileSystemDeviceUPnPActivator.getSOAPurl(), "0");
			connections.put(backId, bc);
			cleanConnections.put(backId, bc);
		}
		available = true;
		dirty = false;
		confLock = new ReentrantReadWriteLock();
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void init() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy(boolean deleteData) throws IOException {
		stopp();
	}

	private Response readAction(ReadAction r) {
		synchronized (connections) {
			if (!available) {
				return null;
			}
			for (LinkSmartFSDConnector bc : cleanConnections.values()) {
				Response response = r.action(bc);
				if (response != null) {
					if (!ErrorCodes.isError(response.getErrorCode()))
						return response;
					if (ErrorCodes.isUserError(response.getErrorCode()))
						return response;
				}
			}
			return null;
		}
	}

	public VoidResponse writeAction(WriteAction wr) {
		synchronized (connections) {
			if (!available) {
				return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Drive not available");
			}
			Date accessTime = new Date();
			Vector<LinkSmartFSDConnector> badConnections = new Vector<LinkSmartFSDConnector>();
			Vector<LinkSmartFSDConnector> brokenConnections = new Vector<LinkSmartFSDConnector>();
			Vector<LinkSmartFSDConnector> writtenConnections = new Vector<LinkSmartFSDConnector>();
			Hashtable<String, VoidResponse> responses = new Hashtable<String, VoidResponse>();
			boolean written = false;
			for (LinkSmartFSDConnector bc : cleanConnections.values()) {
				VoidResponse vr = wr.action(bc);
				if (vr == null) {
					badConnections.add(bc);
				} else if (!ErrorCodes.isError(vr.getErrorCode())) {
					written = true;
					writtenConnections.add(bc);
				} else if (ErrorCodes.isUserError(vr.getErrorCode())) {
					if (written) {
						badConnections.add(bc);
					} else {
						Vector<String> cd = new Vector<String>();
						for (LinkSmartFSDConnector hc : cleanConnections.values()) {
							if (!brokenConnections.contains(hc.getFileSystemDeviceId()))
								cd.add(hc.getFileSystemDeviceId());
						}
						for (LinkSmartFSDConnector hc : brokenConnections) {
							cleanConnections.remove(hc.getFileSystemDeviceId());
							dirtyConnections.put(hc.getFileSystemDeviceId(), hc);
							try {
								idb.addEntry(wr.brokenEntry(hc.getFileSystemDeviceId(), cd,
										accessTime));
							} catch (IOException e) {
								logger
										.fatal("Could not update Inconsistency Table!");
								return new VoidResponse(
										ErrorCodes.EC_IO_EXEPTION,
										"Inconsistency Table down!");
							}
						}
						return vr;
					}
				} else if (ErrorCodes.isBroken(vr.getErrorCode())) {
					brokenConnections.add(bc);
					badConnections.add(bc);
				} else {
					badConnections.add(bc);
				}
				responses.put(bc.getFileSystemDeviceId(), vr);
			}
			if (written) {
				for (LinkSmartFSDConnector hc : badConnections) {
					cleanConnections.remove(hc.getFileSystemDeviceId());
					dirtyConnections.put(hc.getFileSystemDeviceId(), hc);
				}
				Vector<String> cd = new Vector<String>();
				for (LinkSmartFSDConnector hc : cleanConnections.values()) {
					cd.add(hc.getFileSystemDeviceId());
				}
				for (LinkSmartFSDConnector hc : dirtyConnections.values()) {
					try {
						idb.addEntry(wr.createEntry(hc.getFileSystemDeviceId(), cd,
								accessTime));
					} catch (IOException e) {
						e.printStackTrace();
						logger.fatal("Could not update Inconsistency Table!");
						return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
								"Inconsistency Table down!");
					}
				}
				return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
			} else {
				Vector<String> cd = new Vector<String>();
				for (LinkSmartFSDConnector hc : cleanConnections.values()) {
					if (!brokenConnections.contains(hc.getFileSystemDeviceId()))
						cd.add(hc.getFileSystemDeviceId());
				}
				for (LinkSmartFSDConnector hc : brokenConnections) {
					cleanConnections.remove(hc.getFileSystemDeviceId());
					dirtyConnections.put(hc.getFileSystemDeviceId(), hc);
					try {
						idb.addEntry(wr.brokenEntry(hc.getFileSystemDeviceId(), cd,
								accessTime));
					} catch (IOException e) {
						logger.fatal("Could not update Inconsistency Table!");
						return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
								"Inconsistency Table down!");
					}
				}
				if (cleanConnections.size() == 0)
					return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
							"No clean devices left. Probably data lost.");
				return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"System Exception on all devices.");
			}
		}
	}

	// readAccess
	@Override
	public BooleanResponse existsPath(String path) {
		confLock.readLock().lock();
		BooleanResponse r = __existsPath(path);
		confLock.readLock().unlock();
		return r;
	}

	private BooleanResponse __existsPath(String path) {
		ReadAction er = new ReadAction() {
			@Override
			public Response action(LinkSmartFSDConnector con) {
				return con.existsPath(this.path);
			}
		};
		er.path = path;
		BooleanResponse br = (BooleanResponse) readAction(er);
		if (br == null)
			return new BooleanResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Drive not available", null);
		return br;
	}

	@Override
	public LinkSmartFileVectorResponse getDirectoryEntries(String path) {
		confLock.readLock().lock();
		LinkSmartFileVectorResponse r = __getDirectoryEntries(path);
		confLock.readLock().unlock();
		return r;
	}

	private LinkSmartFileVectorResponse __getDirectoryEntries(String path) {
		ReadAction er = new ReadAction() {
			@Override
			public Response action(LinkSmartFSDConnector con) {
				return con.getDirectoryEntries(this.path);
			}
		};
		er.path = path;
		LinkSmartFileVectorResponse br = (LinkSmartFileVectorResponse) readAction(er);
		if (br == null)
			return new LinkSmartFileVectorResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Drive not available", null);
		return br;
	}

	@Override
	public LinkSmartFileResponse getFile(String path) {
		confLock.readLock().lock();
		LinkSmartFileResponse r = __getFile(path);
		confLock.readLock().unlock();
		return r;
	}

	private LinkSmartFileResponse __getFile(String path) {
		ReadAction er = new ReadAction() {
			@Override
			public Response action(LinkSmartFSDConnector con) {
				return con.getFile(this.path);
			}
		};
		er.path = path;
		LinkSmartFileResponse br = (LinkSmartFileResponse) readAction(er);
		if (br == null)
			return new LinkSmartFileResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Drive not available", null);
		return br;
	}

	@Override
	public LongResponse getFreeSpace() {
		confLock.readLock().lock();
		LongResponse r = __getFreeSpace();
		confLock.readLock().unlock();
		return r;
	}

	private LongResponse __getFreeSpace() {
		ReadAction er = new ReadAction() {
			@Override
			public Response action(LinkSmartFSDConnector con) {
				return con.getFreeSpace();
			}
		};
		LongResponse br = (LongResponse) readAction(er);
		if (br == null)
			return new LongResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Drive not available", null);
		return br;
	}

	@Override
	public LongResponse getSize() {
		confLock.readLock().lock();
		LongResponse r = __getSize();
		confLock.readLock().unlock();
		return r;
	}

	private LongResponse __getSize() {
		ReadAction er = new ReadAction() {
			@Override
			public Response action(LinkSmartFSDConnector con) {
				return con.getSize();
			}
		};
		LongResponse br = (LongResponse) readAction(er);
		if (br == null)
			return new LongResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Drive not available", null);
		return br;
	}

	@Override
	public StatFSResponse getStatFs() {
		confLock.readLock().lock();
		StatFSResponse r = __getStatFs();
		confLock.readLock().unlock();
		return r;
	}

	private StatFSResponse __getStatFs() {
		ReadAction er = new ReadAction() {
			@Override
			public Response action(LinkSmartFSDConnector con) {
				return con.getStatFS();
			}
		};
		StatFSResponse br = (StatFSResponse) readAction(er);
		if (br == null)
			return new StatFSResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Drive not available", null);
		return br;
	}

	@Override
	public StringResponse readFile(String path, long start, int size) {
		confLock.readLock().lock();
		StringResponse r = __readFile(path, start, size);
		confLock.readLock().unlock();
		return r;
	}

	private StringResponse __readFile(String path, long start, int size) {
		ReadAction er = new ReadAction() {
			@Override
			public Response action(LinkSmartFSDConnector con) {
				return con.readFile(path, start, size);
			}
		};
		er.path = path;
		er.start = start;
		er.size = size;
		StringResponse br = (StringResponse) readAction(er);
		if (br == null)
			return new StringResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Drive not available", null);
		return br;
	}

	// write Access
	@Override
	public VoidResponse clearFile(String path) {
		confLock.readLock().lock();
		VoidResponse r = __clearFile(path);
		confLock.readLock().unlock();
		return r;
	}

	private VoidResponse __clearFile(String path) {
		WriteAction wr = new WriteAction() {
			@Override
			public VoidResponse action(LinkSmartFSDConnector con) {
				return con.clearFile(path);
			}

			@Override
			public IncDBEntry brokenEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createBrokenFileEntry(deviceId, path,
						cleanDevices, accessTime);
			}

			@Override
			public IncDBEntry createEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createClearFileEntry(path, path,
						cleanDevices, accessTime);
			}
		};
		wr.path = path;
		VoidResponse vr = writeAction(wr);
		return vr;
	}

	@Override
	public VoidResponse copy(String sourcePath, String destinationPath) {
		confLock.readLock().lock();
		VoidResponse r = __copy(sourcePath, destinationPath);
		confLock.readLock().unlock();
		return r;
	}

	private VoidResponse __copy(String sourcePath, String destinationPath) {
		WriteAction wr = new WriteAction() {
			@Override
			public VoidResponse action(LinkSmartFSDConnector con) {
				return con.copy(this.path, this.destPath);
			}

			@Override
			public IncDBEntry brokenEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createBrokenDirEntry(deviceId, path,
						cleanDevices, accessTime);
			}

			@Override
			public IncDBEntry createEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createCopyDirEntry(deviceId, path,
						cleanDevices, destPath, accessTime);
			}
		};
		wr.path = sourcePath;
		wr.destPath = destinationPath;
		VoidResponse vr = writeAction(wr);
		return vr;
	}

	@Override
	public VoidResponse createDirectory(String path) {
		confLock.readLock().lock();
		VoidResponse r = __createDirectory(path);
		confLock.readLock().unlock();
		return r;
	}

	private VoidResponse __createDirectory(String path) {
		WriteAction wr = new WriteAction() {
			@Override
			public VoidResponse action(LinkSmartFSDConnector con) {
				return con.createDirectory(this.path);
			}

			@Override
			public IncDBEntry brokenEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createBrokenDirEntry(deviceId, path,
						cleanDevices, accessTime);
			}

			@Override
			public IncDBEntry createEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createCreateDirEntry(deviceId, path,
						cleanDevices, accessTime);
			}
		};
		wr.path = path;
		VoidResponse vr = writeAction(wr);
		return vr;
	}

	@Override
	public VoidResponse createFile(String path,
			Dictionary<String, String> properties) {
		confLock.readLock().lock();
		VoidResponse r = __createFile(path, properties);
		confLock.readLock().unlock();
		return r;
	}

	private VoidResponse __createFile(String path,
			Dictionary<String, String> properties) {
		WriteAction wr = new WriteAction() {
			@Override
			public VoidResponse action(LinkSmartFSDConnector con) {
				return con.createFile(this.path, this.properties);
			}

			@Override
			public IncDBEntry brokenEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createBrokenFileEntry(deviceId, path,
						cleanDevices, accessTime);
			}

			@Override
			public IncDBEntry createEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createCreateFileEntry(deviceId, path,
						cleanDevices, accessTime);
			}
		};
		wr.path = path;
		wr.properties = properties;
		VoidResponse vr = writeAction(wr);
		return vr;
	}

	@Override
	public VoidResponse move(String sourcePath, String destinationPath) {
		confLock.readLock().lock();
		VoidResponse r = __move(sourcePath, destinationPath);
		confLock.readLock().unlock();
		return r;
	}

	private VoidResponse __move(String sourcePath, String destinationPath) {
		WriteAction wr = new WriteAction() {
			@Override
			public VoidResponse action(LinkSmartFSDConnector con) {
				return con.move(this.path, this.destPath);
			}

			@Override
			public IncDBEntry brokenEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createBrokenDirEntry(deviceId, path,
						cleanDevices, accessTime);
			}

			@Override
			public IncDBEntry createEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createMoveDirEntry(deviceId, path,
						cleanDevices, destPath, accessTime);
			}
		};
		wr.path = sourcePath;
		wr.destPath = destinationPath;
		VoidResponse vr = writeAction(wr);
		return vr;
	}

	@Override
	public VoidResponse removeDirectory(String path, boolean recursive) {
		confLock.readLock().lock();
		VoidResponse r = __removeDirectory(path, recursive);
		confLock.readLock().unlock();
		return r;
	}

	private VoidResponse __removeDirectory(String path, boolean recursive) {
		WriteAction wr = new WriteAction() {
			@Override
			public VoidResponse action(LinkSmartFSDConnector con) {
				return con.removeDirectory(this.path, this.recursive);
			}

			@Override
			public IncDBEntry brokenEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createBrokenDirEntry(deviceId, path,
						cleanDevices, accessTime);
			}

			@Override
			public IncDBEntry createEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createRemoveDirEntry(deviceId, path,
						cleanDevices, accessTime);
			}
		};
		wr.path = path;
		wr.recursive = recursive;
		VoidResponse vr = writeAction(wr);
		return vr;
	}

	@Override
	public VoidResponse removeFile(String path) {
		confLock.readLock().lock();
		VoidResponse r = __removeFile(path);
		confLock.readLock().unlock();
		return r;
	}

	private VoidResponse __removeFile(String path) {
		WriteAction wr = new WriteAction() {
			@Override
			public VoidResponse action(LinkSmartFSDConnector con) {
				return con.removeFile(this.path);
			}

			@Override
			public IncDBEntry brokenEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createBrokenFileEntry(deviceId, path,
						cleanDevices, accessTime);
			}

			@Override
			public IncDBEntry createEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createRemoveFileEntry(deviceId, path,
						cleanDevices, accessTime);
			}
		};
		wr.path = path;
		VoidResponse vr = writeAction(wr);
		return vr;
	}

	@Override
	public VoidResponse setFileProperties(String path,
			Dictionary<String, String> properties) {
		confLock.readLock().lock();
		VoidResponse r = __setFileProperties(path, properties);
		confLock.readLock().unlock();
		return r;
	}

	private VoidResponse __setFileProperties(String path,
			Dictionary<String, String> properties) {
		WriteAction wr = new WriteAction() {
			@Override
			public VoidResponse action(LinkSmartFSDConnector con) {
				return con.setFileProperties(this.path, this.properties);
			}

			@Override
			public IncDBEntry brokenEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createBrokenFileEntry(deviceId, path,
						cleanDevices, accessTime);
			}

			@Override
			public IncDBEntry createEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createPropertyFileEntry(deviceId, path,
						cleanDevices, accessTime);
			}
		};
		wr.path = path;
		wr.properties = properties;
		VoidResponse vr = writeAction(wr);
		return vr;
	}

	@Override
	public VoidResponse setFileProperty(String path, String propertyName,
			String propertyValue) {
		confLock.readLock().lock();
		VoidResponse r = __setFileProperty(path, propertyName, propertyValue);
		confLock.readLock().unlock();
		return r;
	}

	private VoidResponse __setFileProperty(String path, String propertyName,
			String propertyValue) {
		WriteAction wr = new WriteAction() {
			@Override
			public VoidResponse action(LinkSmartFSDConnector con) {
				return con.setFileProperty(this.path, this.propKey,
						this.propVal);
			}

			@Override
			public IncDBEntry brokenEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createBrokenFileEntry(deviceId, path,
						cleanDevices, accessTime);
			}

			@Override
			public IncDBEntry createEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createPropertyFileEntry(deviceId, path,
						cleanDevices, accessTime);
			}
		};
		wr.path = path;
		wr.propKey = propertyName;
		wr.propVal = propertyValue;
		VoidResponse vr = writeAction(wr);
		return vr;
	}

	@Override
	public VoidResponse truncateFile(String path, long size) {
		confLock.readLock().lock();
		VoidResponse r = __truncateFile(path, size);
		confLock.readLock().unlock();
		return r;
	}

	private VoidResponse __truncateFile(String path, long size) {
		WriteAction wr = new WriteAction() {
			@Override
			public VoidResponse action(LinkSmartFSDConnector con) {
				return con.truncateFile(this.path, this.size);
			}

			@Override
			public IncDBEntry brokenEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createBrokenFileEntry(deviceId, path,
						cleanDevices, accessTime);
			}

			@Override
			public IncDBEntry createEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createTruncateFileEntry(deviceId, path,
						cleanDevices, size, accessTime);
			}
		};
		wr.path = path;
		wr.size = size;
		VoidResponse vr = writeAction(wr);
		return vr;
	}

	@Override
	public VoidResponse writeFile(String path, long start, String data) {
		confLock.readLock().lock();
		VoidResponse r = __writeFile(path, start, data);
		confLock.readLock().unlock();
		return r;
	}

	private VoidResponse __writeFile(String path, long start, String data) {
		WriteAction wr = new WriteAction() {
			@Override
			public VoidResponse action(LinkSmartFSDConnector con) {
				return con.writeFile(this.path, this.start, this.data);
			}

			@Override
			public IncDBEntry brokenEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createBrokenFileEntry(deviceId, path,
						cleanDevices, accessTime);
			}

			@Override
			public IncDBEntry createEntry(String deviceId,
					Collection<String> cleanDevices, Date accessTime) {
				return IncDBEntry.createWriteFileEntry(deviceId, path,
						cleanDevices, start, data.length(), accessTime);
			}
		};
		wr.path = path;
		wr.start = start;
		wr.data = data;
		VoidResponse vr = writeAction(wr);
		return vr;
	}

	public boolean isDirty() {
		return dirty;
	}

	public VoidResponse changeBackendDevices(Collection<String> devices) {
		confLock.writeLock().lock();
		VoidResponse vr = __changeBackendDevices(devices);
		confLock.writeLock().unlock();
		return vr;
	}

	private VoidResponse __changeBackendDevices(Collection<String> devices) {
		Vector<String> remove = new Vector<String>();
		for (String key : connections.keySet()) {
			if (!devices.contains(key))
				remove.add(key);
		}
		for (String key : remove) {
			logger.info("Remove Dev " + key);
			connections.remove(key);
			dirtyConnections.remove(key);
			cleanConnections.remove(key);
			// TODO: clean IncosistencyDB
		}
		for (String key : devices) {
			if (!connections.keySet().contains(key)) {
				logger.debug("Add Dev " + key);
				LinkSmartFSDConnector hc = new LinkSmartFSDConnector(key,
						FileSystemDeviceUPnPActivator.getNMurl(),
						FileSystemDeviceUPnPActivator.getSOAPurl(), "0");
				connections.put(key, hc);
				dirtyConnections.put(key, hc);
			}
		}
		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	private void sync(LinkSmartFSDConnector hc) throws IOException {
		// TODO: At the moment we copy everything from a clean device to the
		// device
		// to be repaired. This should be enhanced by using the InconsistencyDB
		LinkSmartFileVectorResponse hfvr = hc.getDirectoryEntries("/");
		if (hfvr == null)
			throw new IOException("device no more reachable");
		if (ErrorCodes.isError(hfvr.getErrorCode()))
			throw new IOException("dirty reading root dir EC: "
					+ hfvr.getErrorCode() + " (" + hfvr.getErrorMessage() + ")");
		for (LinkSmartFile hf : hfvr.getResult()) {
			VoidResponse vr = null;
			if (hf.isDirectory()) {
				vr = hc.removeDirectory(hf.getPath(), true);
			} else {
				vr = hc.removeFile(hf.getPath());
			}
			if (vr == null)
				throw new IOException("device no more reachable");
			if (ErrorCodes.isError(vr.getErrorCode()))
				throw new IOException("removing " + hf.getPath() + " EC: "
						+ vr.getErrorCode() + " (" + vr.getErrorMessage() + ")");
		}
		syncDir(hc, "/");
	}

	private void syncDir(LinkSmartFSDConnector dirtyDev, String path)
			throws IOException {
		LinkSmartFileVectorResponse hfvr = __getDirectoryEntries(path);
		if (ErrorCodes.isError(hfvr.getErrorCode()))
			throw new IOException("reading " + path + " EC: "
					+ hfvr.getErrorCode() + " (" + hfvr.getErrorMessage() + ")");
		for (LinkSmartFile hf : hfvr.getResult()) {
			if (hf.isDirectory()) {
				VoidResponse vr = dirtyDev.createDirectory(hf.getPath());
				if (vr == null)
					throw new IOException("device no more reachable");
				if (ErrorCodes.isError(vr.getErrorCode()))
					throw new IOException("creating dir " + hf.getPath()
							+ " EC: " + vr.getErrorCode() + " ("
							+ vr.getErrorMessage() + ")");
				syncDir(dirtyDev, hf.getPath());
			} else {
				VoidResponse vr = dirtyDev.createFile(hf.getPath(), hf
						.getProperties());
				if (vr == null)
					throw new IOException("device no more reachable");
				if (ErrorCodes.isError(vr.getErrorCode()))
					throw new IOException("creating file " + hf.getPath()
							+ " EC: " + vr.getErrorCode() + " ("
							+ vr.getErrorMessage() + ")");
				if (hf.getSize() > 0) {
					long chunks = (hf.getSize() + 4095) / 4096;
					for (long i = 0; i < chunks; i++) {
						StringResponse sr = __readFile(hf.getPath(), i * 4096,
								4096);
						if (ErrorCodes.isError(sr.getErrorCode()))
							throw new IOException("reading file "
									+ hf.getPath() + " EC: "
									+ sr.getErrorCode() + " ("
									+ sr.getErrorMessage() + ")");
						vr = dirtyDev.writeFile(hf.getPath(), i * 4096, sr
								.getResult());
						if (vr == null)
							throw new IOException("device no more reachable");
						if (ErrorCodes.isError(vr.getErrorCode()))
							throw new IOException("creating file "
									+ hf.getPath() + " EC: "
									+ vr.getErrorCode() + " ("
									+ vr.getErrorMessage() + ")");
					}
				}
			}
		}
	}


	public void run() {
		while (!stopp) {
			//logger.debug("checkSync.....");
			boolean holdLock = false;
			Vector<LinkSmartFSDConnector> cleaned = new Vector<LinkSmartFSDConnector>();
			for (LinkSmartFSDConnector hc : dirtyConnections.values()) {
				logger.debug("Sync check " + hc.getId());
				if (hc.getId() != null) {
					try {
						if (!holdLock) {
							confLock.writeLock().lock();
							holdLock = true;
						}
						//logger.debug("Sync it...");
						sync(hc);
						cleaned.add(hc);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			for (LinkSmartFSDConnector hc : cleaned) {
				dirtyConnections.remove(hc.getId());
				cleanConnections.put(hc.getId(), hc);
			}
			if (holdLock)
				confLock.writeLock().unlock();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		stopped = true;
	}

	public void stopp() {
		stopp = true;
		while (!stopped) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
