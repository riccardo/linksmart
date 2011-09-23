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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import eu.linksmart.storage.helper.Base64;
import eu.linksmart.storage.helper.BooleanResponse;
import eu.linksmart.storage.helper.ErrorCodes;
import eu.linksmart.storage.helper.LinkSmartFile;
import eu.linksmart.storage.helper.LinkSmartFileResponse;
import eu.linksmart.storage.helper.LinkSmartFileVectorResponse;
import eu.linksmart.storage.helper.LongResponse;
import eu.linksmart.storage.helper.StatFS;
import eu.linksmart.storage.helper.StatFSResponse;
import eu.linksmart.storage.helper.StringResponse;
import eu.linksmart.storage.helper.VoidResponse;

import eu.linksmart.limbo.filesystemdevice.client.LinkSmartFSDConnector;
import eu.linksmart.limbo.filesystemdevice.upnp.FileSystemDeviceUPnPActivator;

public class StripedFileSystemStorage extends FileSystemStorage {

	private static final String partPrefix = "part";

	private Hashtable<String, LinkSmartFSDConnector> devices;
	private long stripesize;
	private ArrayList<String> devIDs;
	private ReentrantReadWriteLock confLock;

	public StripedFileSystemStorage(String name, Collection<String> devices,
			long stripesize, String id, String systemId) {
		super(name, STRIPED_STORAGE, id, systemId);
		this.devices = new Hashtable<String, LinkSmartFSDConnector>();
		this.stripesize = stripesize;
		devIDs = new ArrayList<String>(devices);
		Collections.sort(devIDs);
		for (String devId : devIDs) {
			LinkSmartFSDConnector bc = new LinkSmartFSDConnector(devId,
					FileSystemDeviceUPnPActivator.getNMurl(),
					FileSystemDeviceUPnPActivator.getSOAPurl(), "0");
			this.devices.put(devId, bc);
		}
		confLock = new ReentrantReadWriteLock();
	}

	public StripedFileSystemStorage(String name, Collection<String> devices,
			String id, String systemId) {
		this(name, devices, (long) 4096, id, systemId);
	}

	@Override
	public VoidResponse clearFile(String path) {
		confLock.readLock().lock();
		VoidResponse vr = __clearFile(path);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public VoidResponse copy(String sourcePath, String destinationPath) {
		confLock.readLock().lock();
		VoidResponse vr = __copy(sourcePath, destinationPath);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public VoidResponse createDirectory(String path) {
		confLock.readLock().lock();
		VoidResponse vr = __createDirectory(path);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public VoidResponse createFile(String path,
			Dictionary<String, String> properties) {
		confLock.readLock().lock();
		VoidResponse vr = __createFile(path, properties);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public void destroy(boolean deleteData) throws IOException {
	}

	@Override
	public BooleanResponse existsPath(String path) {
		confLock.readLock().lock();
		BooleanResponse vr = __existsPath(path);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public LinkSmartFileVectorResponse getDirectoryEntries(String path) {
		confLock.readLock().lock();
		LinkSmartFileVectorResponse vr = __getDirectoryEntries(path);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public LinkSmartFileResponse getFile(String path) {
		confLock.readLock().lock();
		LinkSmartFileResponse vr = __getFile(path);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public LongResponse getFreeSpace() {
		confLock.readLock().lock();
		LongResponse vr = __getFreeSpace();
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public LongResponse getSize() {
		confLock.readLock().lock();
		LongResponse vr = __getSize();
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public StatFSResponse getStatFs() {
		confLock.readLock().lock();
		StatFSResponse vr = __getStatFs();
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public void init() throws IOException {
	}

	@Override
	public VoidResponse move(String sourcePath, String destinationPath) {
		confLock.readLock().lock();
		VoidResponse vr = __move(sourcePath, destinationPath);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public StringResponse readFile(String path, long start, int size) {
		confLock.readLock().lock();
		StringResponse vr = __readFile(path, start, size);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public VoidResponse removeDirectory(String path, boolean recursive) {
		confLock.readLock().lock();
		VoidResponse vr = __removeDirectory(path, recursive);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public VoidResponse removeFile(String path) {
		confLock.readLock().lock();
		VoidResponse vr = __removeFile(path);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public VoidResponse setFileProperties(String path,
			Dictionary<String, String> properties) {
		confLock.readLock().lock();
		VoidResponse vr = __setFileProperties(path, properties);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public VoidResponse setFileProperty(String path, String propertyName,
			String propertyValue) {
		confLock.readLock().lock();
		VoidResponse vr = __setFileProperty(path, propertyName, propertyValue);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public VoidResponse truncateFile(String path, long size) {
		confLock.readLock().lock();
		VoidResponse vr = __truncateFile(path, size);
		confLock.readLock().unlock();
		return vr;
	}

	@Override
	public VoidResponse writeFile(String path, long start, String data) {
		confLock.readLock().lock();
		VoidResponse vr = __writeFile(path, start, data);
		confLock.readLock().unlock();
		return vr;
	}

	private VoidResponse __clearFile(String path) {
		// Save properties
		LinkSmartFileResponse hfr = getFile(path);
		if (ErrorCodes.isError(hfr.getErrorCode()))
			return new VoidResponse(hfr.getErrorCode(), hfr.getErrorMessage());
		LinkSmartFile file = hfr.getResult();
		if (!file.isDirectory())
			return new VoidResponse(ErrorCodes.EC_IS_NO_DIRECTORY,
					"path denotes not to a directory");
		Dictionary<String, String> properties = file.getProperties();
		for (LinkSmartFSDConnector hc : devices.values()) {
			VoidResponse vr = hc.removeDirectory(path, true);
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Can not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return vr;
			vr = hc.createDirectory(path);
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Can not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return vr;
		}
		String firstID = getDevice(path, 0);
		LinkSmartFSDConnector firstCon = devices.get(firstID);
		VoidResponse vr = firstCon.createFile(partPath(path, 0), properties);
		if (vr == null) {
			return new VoidResponse(ErrorCodes.EC_INCOMPLETE_WRITE_ACCESS,
					"No Connection to device " + firstCon.getFileSystemDeviceId());
		}

		return vr;
	}

	private VoidResponse __copy(String sourcePath, String destinationPath) {
		LinkSmartFileResponse hfr = getFile(sourcePath);
		if (ErrorCodes.isError(hfr.getErrorCode()))
			return new VoidResponse(hfr.getErrorCode(), hfr.getErrorMessage());
		LinkSmartFile sourceFile = hfr.getResult();
		LinkSmartFile destFile = null;
		hfr = getFile(destinationPath);
		if (ErrorCodes.isError(hfr.getErrorCode())) {
			if (hfr.getErrorCode() == ErrorCodes.EC_FILE_NOT_FOUND) {
				// TODO: Check if parent exists
				for (LinkSmartFSDConnector hc2 : devices.values()) {
					VoidResponse vr = hc2.copy(sourcePath, destinationPath);
					if (vr == null)
						return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
								"Can not access " + hc2.getFileSystemDeviceId());
					if (ErrorCodes.isError(vr.getErrorCode()))
						return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
								"Underlying Error: " + vr.getErrorCode() + " ("
										+ vr.getErrorMessage() + ")");
				}
				return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
			} else
				return new VoidResponse(hfr.getErrorCode(), hfr
						.getErrorMessage());
		} else {
			destFile = hfr.getResult();
			if (destFile.isDirectory()) {
				hfr = getFile(LinkSmartFile.createSubPath(destinationPath,
						sourceFile.getName()));
				if (!ErrorCodes.isError(hfr.getErrorCode()))
					return new VoidResponse(ErrorCodes.EC_DIR_NOT_EMPTY,
							"in destination a file " + sourceFile.getName()
									+ " exists.");
				if (hfr.getErrorCode() != ErrorCodes.EC_FILE_NOT_FOUND)
					return new VoidResponse(hfr.getErrorCode(), hfr
							.getErrorMessage());
				for (LinkSmartFSDConnector hc2 : devices.values()) {
					VoidResponse vr = hc2.copy(sourcePath, destinationPath);
					if (vr == null)
						return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
								"Can not access " + hc2.getFileSystemDeviceId());
					if (ErrorCodes.isError(vr.getErrorCode()))
						return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
								"Underlying Error: " + vr.getErrorCode() + " ("
										+ vr.getErrorMessage() + ")");
				}
				return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
			} else
				return new VoidResponse(ErrorCodes.EC_IS_NO_DIRECTORY,
						"Destination is no directory");
		}
	}

	private VoidResponse __createDirectory(String path) {
		// TODO: Check if parent is really a directory
		LinkSmartFileResponse hfr = getFile(path);
		if (!ErrorCodes.isError(hfr.getErrorCode()))
			return new VoidResponse(ErrorCodes.EC_PATH_EXISTS,
					"path already exists");
		if (hfr.getErrorCode() != ErrorCodes.EC_FILE_NOT_FOUND)
			return new VoidResponse(hfr.getErrorCode(), hfr.getErrorMessage());
		String parentDir = LinkSmartFile.getParent(path);
		hfr = getFile(parentDir);
		if (ErrorCodes.isError(hfr.getErrorCode()))
			return new VoidResponse(hfr.getErrorCode(), hfr.getErrorMessage());
		LinkSmartFile parentFile = hfr.getResult();
		if (!parentFile.isDirectory()) {
			return new VoidResponse(ErrorCodes.EC_IS_NO_DIRECTORY,
					"parent is no directory");
		}
		for (LinkSmartFSDConnector hc : devices.values()) {
			VoidResponse vr = hc.createDirectory(path);
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Could not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Underlying error: " + vr.getErrorCode() + "("
								+ vr.getErrorMessage() + ")");
		}
		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	private VoidResponse __createFile(String path,
			Dictionary<String, String> properties) {
		String encoding = properties
				.get("eu.linksmart.storage.fsd.encoding.method");
		if (encoding == null) {
			encoding = "text";
			properties
					.put("eu.linksmart.storage.fsd.encoding.method", encoding);
		} else if ((!encoding.equalsIgnoreCase("test"))
				&& (!encoding.equalsIgnoreCase("base64")))
			return new VoidResponse(ErrorCodes.EC_UNKNOWN_ENCODING, "Encoding "
					+ encoding + " not known");
		String first = getDevice(path, 0);
		LinkSmartFSDConnector firstCon = devices.get(first);
		BooleanResponse br = firstCon.existsPath(path);
		if (br == null)
			return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Can not Access backend Device " + firstCon.getFileSystemDeviceId());
		if (ErrorCodes.isError(br.getErrorCode()))
			return new VoidResponse(br.getErrorCode(), br.getErrorMessage());
		if (br.getResult())
			return new VoidResponse(ErrorCodes.EC_PATH_EXISTS, "path " + path
					+ " already exists");
		VoidResponse vr = firstCon.createDirectory(path);
		if (vr == null)
			return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Can not Access backend Device " + firstCon.getFileSystemDeviceId());
		if (ErrorCodes.isError(vr.getErrorCode()))
			return vr;
		vr = firstCon.createFile(partPath(path, 0), properties);
		if (vr == null)
			return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
					"Can not Access backend Device " + firstCon.getFileSystemDeviceId()
							+ " creating part0");
		if (ErrorCodes.isError(vr.getErrorCode()))
			return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
					"Error creating part0 on " + firstCon.getFileSystemDeviceId());
		for (LinkSmartFSDConnector hc : devices.values()) {
			if (!hc.getFileSystemDeviceId().equals(first)) {
				vr = hc.createDirectory(path);
				if (vr == null)
					return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
							"Can not Access backend Device "
									+ firstCon.getFileSystemDeviceId());
				if (ErrorCodes.isError(vr.getErrorCode()))
					return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
							"Error creating dir on " + firstCon.getFileSystemDeviceId());
			}
		}
		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	private BooleanResponse __existsPath(String path) {
		LinkSmartFileResponse hfr = getFile(path);
		if (!ErrorCodes.isError(hfr.getErrorCode()))
			return new BooleanResponse(ErrorCodes.EC_NO_ERROR, null, true);
		if (hfr.getErrorCode() == ErrorCodes.EC_FILE_NOT_FOUND)
			return new BooleanResponse(ErrorCodes.EC_NO_ERROR, null, false);
		return new BooleanResponse(hfr.getErrorCode(), hfr.getErrorMessage(),
				null);
	}

	private LinkSmartFileVectorResponse __getDirectoryEntries(String path) {
		LinkSmartFileResponse hfr = getFile(path);
		if (ErrorCodes.isError(hfr.getErrorCode()))
			return new LinkSmartFileVectorResponse(hfr.getErrorCode(), hfr
					.getErrorMessage(), null);
		if (!hfr.getResult().isDirectory())
			return new LinkSmartFileVectorResponse(ErrorCodes.EC_IS_NO_DIRECTORY,
					"path denotes to no directory", null);
		String first = getDevice(path, 0);
		LinkSmartFSDConnector firstCon = devices.get(first);
		LinkSmartFileVectorResponse hfvr = firstCon.getDirectoryEntries(path);
		if (hfvr == null)
			return new LinkSmartFileVectorResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Could not access " + firstCon.getFileSystemDeviceId(), null);
		if (ErrorCodes.isError(hfvr.getErrorCode()))
			return hfvr;
		Vector<LinkSmartFile> result = new Vector<LinkSmartFile>();
		for (LinkSmartFile hf : hfvr.getResult()) {
			hfr = getFile(hf.getPath());
			if (hfr == null)
				return new LinkSmartFileVectorResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Can not access " + firstCon.getFileSystemDeviceId(), null);
			if (ErrorCodes.isError(hfr.getErrorCode()))
				return new LinkSmartFileVectorResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Underlying Error " + hfr.getErrorCode() + "("
								+ hfr.getErrorMessage() + ")", null);
			result.add(hfr.getResult());
		}
		return new LinkSmartFileVectorResponse(ErrorCodes.EC_NO_ERROR, null, result);
	}

	private LinkSmartFileResponse __getFile(String path) {
		String first = getDevice(path, 0);
		LinkSmartFSDConnector firstCon = devices.get(first);

		LinkSmartFileVectorResponse vr = firstCon.getDirectoryEntries(path);
		if (vr == null) {
			return new LinkSmartFileResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"No Connection to device " + firstCon.getFileSystemDeviceId(), null);
		}
		if (ErrorCodes.isError(vr.getErrorCode())) {
			if ((vr.getErrorCode() == ErrorCodes.EC_FILE_NOT_FOUND)
					|| (vr.getErrorCode() == ErrorCodes.EC_IS_NO_DIRECTORY))
				return new LinkSmartFileResponse(ErrorCodes.EC_FILE_NOT_FOUND,
						"path does not exist", null);
			return new LinkSmartFileResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Error reading dev " + firstCon.getFileSystemDeviceId() + " ("
							+ vr.getErrorMessage() + ")", null);
		}
		boolean containsFile = false;
		boolean containsDir = false;
		boolean containsPart0 = false;
		Dictionary<String, String> properties = null;
		for (LinkSmartFile hf : vr.getResult()) {
			if (hf.isDirectory())
				containsDir = true;
			if (hf.isFile()) {
				containsFile = true;
				if (hf.getName().equals(partFilename(0))) {
					containsPart0 = true;
					properties = hf.getProperties();
				}
			}
		}
		if (containsDir && containsFile) {
			return new LinkSmartFileResponse(ErrorCodes.EC_IO_EXEPTION,
					"in backend path " + path
							+ " contained files and directories", null);
		}
		if (!containsFile) {
			LinkSmartFile hf = LinkSmartFile.createDirLinkSmartFile(path, 0, 0, 0);
			return new LinkSmartFileResponse(0, null, hf);
		}
		if (!containsPart0)
			return new LinkSmartFileResponse(ErrorCodes.EC_IO_EXEPTION,
					"part0 not found on dev " + first, null);
		Hashtable<String, LinkSmartFileVectorResponse> responses = new Hashtable<String, LinkSmartFileVectorResponse>();
		responses.put(first, vr);
		for (LinkSmartFSDConnector bc : devices.values()) {
			if (!bc.getFileSystemDeviceId().equals(first)) {
				LinkSmartFileVectorResponse hfr = bc.getDirectoryEntries(path);
				if (hfr == null) {
					return new LinkSmartFileResponse(ErrorCodes.EC_NOT_AVAILABLE,
							"No Connection to device " + bc.getFileSystemDeviceId(), null);
				}
				if (ErrorCodes.isError(hfr.getErrorCode())) {
					return new LinkSmartFileResponse(ErrorCodes.EC_IO_EXEPTION,
							"dev " + bc.getFileSystemDeviceId() + " EC: "
									+ hfr.getErrorCode() + " Message: "
									+ hfr.getErrorMessage(), null);
				}
				responses.put(bc.getFileSystemDeviceId(), hfr);
			}
		}
		long parts = 0;
		for (LinkSmartFileVectorResponse hfr : responses.values()) {
			parts += hfr.getResult().size();
		}
		long size = (parts - 1) * stripesize;
		String last = getDevice(path, parts - 1);
		LinkSmartFileVectorResponse hfr = responses.get(last);
		boolean containsLast = false;
		for (LinkSmartFile hf : hfr.getResult()) {
			if (hf.getName().equals(partFilename(parts - 1))) {
				containsLast = true;
				size += hf.getSize();
			}
		}
		if (!containsLast)
			return new LinkSmartFileResponse(ErrorCodes.EC_IO_EXEPTION, "dev "
					+ last + " did not hold last device part" + (parts - 1),
					null);
		LinkSmartFile hf = LinkSmartFile.createFileLinkSmartFile(path, size, 0, 0, 0,
				properties);
		return new LinkSmartFileResponse(ErrorCodes.EC_NO_ERROR, null, hf);
	}

	private LongResponse __getFreeSpace() {
		long space = 0;
		for (LinkSmartFSDConnector hc : devices.values()) {
			LongResponse lr = hc.getFreeSpace();
			if (lr == null)
				return new LongResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Can not access " + hc.getFileSystemDeviceId(), null);
			if (ErrorCodes.isError(lr.getErrorCode()))
				return new LongResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Underlying Error: " + lr.getErrorCode() + "("
								+ lr.getErrorMessage() + ")", null);
			space += lr.getResult();
		}
		return new LongResponse(ErrorCodes.EC_NO_ERROR, null, space);
	}

	private LongResponse __getSize() {
		long space = 0;
		for (LinkSmartFSDConnector hc : devices.values()) {
			LongResponse lr = hc.getSize();
			if (lr == null)
				return new LongResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Can not access " + hc.getFileSystemDeviceId(), null);
			if (ErrorCodes.isError(lr.getErrorCode()))
				return new LongResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Underlying Error: " + lr.getErrorCode() + "("
								+ lr.getErrorMessage() + ")", null);
			space += lr.getResult();
		}
		return new LongResponse(ErrorCodes.EC_NO_ERROR, null, space);
	}

	private StatFSResponse __getStatFs() {
		long space = 0;
		long freeSpace = 0;
		long availableSpace = 0;
		for (LinkSmartFSDConnector hc : devices.values()) {
			StatFSResponse lr = hc.getStatFS();
			if (lr == null)
				return new StatFSResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Can not access " + hc.getFileSystemDeviceId(), null);
			if (ErrorCodes.isError(lr.getErrorCode()))
				return new StatFSResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Underlying Error: " + lr.getErrorCode() + "("
								+ lr.getErrorMessage() + ")", null);
			space += lr.getResult().getSize();
			availableSpace += lr.getResult().getAvailableSize();
			freeSpace += lr.getResult().getFreeSize();
		}
		return new StatFSResponse(ErrorCodes.EC_NO_ERROR, null, new StatFS(
				space, freeSpace, availableSpace));
	}

	private VoidResponse __move(String sourcePath, String destinationPath) {
		LinkSmartFileResponse hfr = getFile(sourcePath);
		if (ErrorCodes.isError(hfr.getErrorCode()))
			return new VoidResponse(hfr.getErrorCode(), hfr.getErrorMessage());
		LinkSmartFile sourceFile = hfr.getResult();
		LinkSmartFile destFile = null;
		hfr = getFile(destinationPath);
		if (ErrorCodes.isError(hfr.getErrorCode())) {
			if (hfr.getErrorCode() == ErrorCodes.EC_FILE_NOT_FOUND) {
				// TODO: Check if parent exists
				for (LinkSmartFSDConnector hc2 : devices.values()) {
					VoidResponse vr = hc2.move(sourcePath, destinationPath);
					if (vr == null)
						return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
								"Can not access " + hc2.getFileSystemDeviceId());
					if (ErrorCodes.isError(vr.getErrorCode()))
						return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
								"Underlying Error: " + vr.getErrorCode() + " ("
										+ vr.getErrorMessage() + ")");
				}
				return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
			} else
				return new VoidResponse(hfr.getErrorCode(), hfr
						.getErrorMessage());
		} else {
			destFile = hfr.getResult();
			if (destFile.isDirectory()) {
				hfr = getFile(LinkSmartFile.createSubPath(destinationPath,
						sourceFile.getName()));
				if (!ErrorCodes.isError(hfr.getErrorCode()))
					return new VoidResponse(ErrorCodes.EC_DIR_NOT_EMPTY,
							"in destination a file " + sourceFile.getName()
									+ " exists.");
				if (hfr.getErrorCode() != ErrorCodes.EC_FILE_NOT_FOUND)
					return new VoidResponse(hfr.getErrorCode(), hfr
							.getErrorMessage());
				for (LinkSmartFSDConnector hc2 : devices.values()) {
					VoidResponse vr = hc2.move(sourcePath, destinationPath);
					if (vr == null)
						return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
								"Can not access " + hc2.getFileSystemDeviceId());
					if (ErrorCodes.isError(vr.getErrorCode()))
						return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
								"Underlying Error: " + vr.getErrorCode() + " ("
										+ vr.getErrorMessage() + ")");
				}
				return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
			} else
				return new VoidResponse(ErrorCodes.EC_IS_NO_DIRECTORY,
						"Destination is no directory");
		}
	}

	private StringResponse __readFile(String path, long start, int size) {
		if (start < 0)
			return new StringResponse(ErrorCodes.EC_ARG_ERROR,
					"start must be greater or equal 0", null);
		if (size < 0)
			return new StringResponse(ErrorCodes.EC_ARG_ERROR,
					"size must be greater 0", null);
		LinkSmartFileResponse hfr = getFile(path);
		if (ErrorCodes.isError(hfr.getErrorCode()))
			return new StringResponse(hfr.getErrorCode(),
					hfr.getErrorMessage(), null);
		if (!hfr.getResult().isFile())
			return new StringResponse(ErrorCodes.EC_IS_NO_FILE,
					"Path denotes to no file", null);
		LinkSmartFile file = hfr.getResult();
		long fileSize = file.getSize();
		if (size == 0)
			return new StringResponse(ErrorCodes.EC_NO_ERROR, null, "");
		if (start >= fileSize)
			return new StringResponse(ErrorCodes.EC_ARG_ERROR,
					"start is behind end of file", null);
		if ((start + size) > fileSize) {
			size = (int) (fileSize - start);
		}
		long firstChunk = start / stripesize;
		long lastChunk = (start + size - 1) / stripesize;
		long firstChunkStart = start % this.stripesize;
		int lastChunkLength = (int) ((start + size) % stripesize);
		if (lastChunkLength == 0)
			lastChunkLength = (int) this.stripesize;
		String encoding = file.getProperties().get(
				"eu.linksmart.storage.fsd.encoding.method");
		if (encoding == null)
			return new StringResponse(ErrorCodes.EC_IO_EXEPTION,
					"File has no encoding", null);
		if ((!encoding.equalsIgnoreCase("text"))
				&& (!encoding.equalsIgnoreCase("base64")))
			return new StringResponse(ErrorCodes.EC_UNKNOWN_ENCODING,
					"Encoding " + encoding + " unknown", null);
		String device;
		LinkSmartFSDConnector hc;
		StringResponse sr;
		if (firstChunk == lastChunk) {
			device = getDevice(path, firstChunk);
			hc = devices.get(device);
			sr = hc.readFile(partPath(path, firstChunk), firstChunkStart,
					lastChunkLength);
			if (sr == null)
				return new StringResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Can not access " + hc.getFileSystemDeviceId(), null);
			if (ErrorCodes.isError(sr.getErrorCode()))
				return new StringResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Underlying Error: " + sr.getErrorCode() + " ("
								+ sr.getErrorMessage() + ")", null);
			return sr;
		}
		Vector<String> answers = new Vector<String>();
		boolean base64 = false;
		if (encoding.equalsIgnoreCase("base64")) {
			base64 = true;
		}
		// StringBuffer answer = new StringBuffer("");
		device = getDevice(path, firstChunk);
		hc = devices.get(device);
		sr = hc.readFile(partPath(path, firstChunk), firstChunkStart,
				(int) (stripesize - firstChunkStart));
		if (sr == null)
			return new StringResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Can not access " + hc.getFileSystemDeviceId(), null);
		if (ErrorCodes.isError(sr.getErrorCode()))
			return new StringResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Underlying Error: " + sr.getErrorCode() + " ("
							+ sr.getErrorMessage() + ")", null);
		answers.add(sr.getResult());
		// if (encoding.equalsIgnoreCase("text")) {
		// answer.append(sr.getResult());
		// } else {
		// answer.append(new String(Base64.decode(sr.getResult())));
		// }
		for (long partNum = firstChunk + 1; partNum < lastChunk; partNum++) {
			device = getDevice(path, partNum);
			hc = devices.get(device);
			sr = hc.readFile(partPath(path, partNum), 0, (int) stripesize);
			if (sr == null)
				return new StringResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Can not access " + hc.getFileSystemDeviceId(), null);
			if (ErrorCodes.isError(sr.getErrorCode()))
				return new StringResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Underlying Error: " + sr.getErrorCode() + " ("
								+ sr.getErrorMessage() + ")", null);
			answers.add(sr.getResult());
			// if (encoding.equalsIgnoreCase("text")) {
			// answer.append(sr.getResult());
			// } else {
			// answer.append(new String(Base64.decode(sr.getResult())));
			// }
		}
		device = getDevice(path, lastChunk);
		hc = devices.get(device);
		sr = hc.readFile(partPath(path, lastChunk), 0, lastChunkLength);
		if (sr == null)
			return new StringResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Can not access " + hc.getFileSystemDeviceId(), null);
		if (ErrorCodes.isError(sr.getErrorCode()))
			return new StringResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Underlying Error: " + sr.getErrorCode() + " ("
							+ sr.getErrorMessage() + ")", null);
		answers.add(sr.getResult());
		if (!base64) {
			StringBuffer sb = new StringBuffer("");
			for (String answer : answers) {
				sb.append(answer);
			}
			return new StringResponse(ErrorCodes.EC_NO_ERROR, null, sb
					.toString());
		} else {
			Vector<byte[]> decDatas = new Vector<byte[]>();
			int fullLength = 0;
			for (String answer : answers) {
				byte[] decData = Base64.decode(answer);
				decDatas.add(decData);
				fullLength += decData.length;
			}
			byte[] decData = new byte[fullLength];
			int destPos = 0;
			for (byte[] decEntry : decDatas) {
				System
						.arraycopy(decEntry, 0, decData, destPos,
								decEntry.length);
				destPos += decEntry.length;
			}
			return new StringResponse(ErrorCodes.EC_NO_ERROR, null, Base64
					.encode(decData));
		}
	}

	private VoidResponse __removeDirectory(String path, boolean recursive) {
		LinkSmartFileResponse hfr = getFile(path);
		if (ErrorCodes.isError(hfr.getErrorCode()))
			return new VoidResponse(hfr.getErrorCode(), hfr.getErrorMessage());
		if (!hfr.getResult().isDirectory())
			return new VoidResponse(ErrorCodes.EC_IS_NO_DIRECTORY,
					"Path denotes to no directory");
		if (!recursive) {
			LinkSmartFileVectorResponse hfvr = getDirectoryEntries(path);
			if (ErrorCodes.isError(hfvr.getErrorCode()))
				return new VoidResponse(hfvr.getErrorCode(), hfvr
						.getErrorMessage());
			if (hfvr.getResult().size() != 0)
				return new VoidResponse(ErrorCodes.EC_DIR_NOT_EMPTY,
						"path is not empty");
		}
		for (LinkSmartFSDConnector hc : devices.values()) {
			VoidResponse vr = hc.removeDirectory(path, recursive);
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Could not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Underlying Error: " + vr.getErrorCode() + "("
								+ vr.getErrorMessage() + ")");
		}
		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	private VoidResponse __removeFile(String path) {
		LinkSmartFileResponse hfr = getFile(path);
		if (ErrorCodes.isError(hfr.getErrorCode()))
			return new VoidResponse(hfr.getErrorCode(), hfr.getErrorMessage());
		if (!hfr.getResult().isFile())
			return new VoidResponse(ErrorCodes.EC_IS_NO_FILE,
					"Path denotes to no file");
		for (LinkSmartFSDConnector hc : devices.values()) {
			VoidResponse vr = hc.removeDirectory(path, true);
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Could not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Underlying Error: " + vr.getErrorCode() + "("
								+ vr.getErrorMessage() + ")");
		}
		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	private VoidResponse __setFileProperties(String path,
			Dictionary<String, String> properties) {
		String encoding = properties
				.get("eu.linksmart.storage.fsd.encoding.method");
		LinkSmartFileResponse hfr = getFile(path);
		if (ErrorCodes.isError(hfr.getErrorCode()))
			return new VoidResponse(hfr.getErrorCode(), "Error getting file ("
					+ hfr.getErrorMessage() + ")");
		LinkSmartFile file = hfr.getResult();
		if (!file.isFile())
			return new VoidResponse(ErrorCodes.EC_IS_NO_FILE,
					"path denotes to no file");
		if (encoding == null) {
			encoding = file.getProperties().get(
					"eu.linksmart.storage.fsd.encoding.method");
			properties
					.put("eu.linksmart.storage.fsd.encoding.method", encoding);
		} else if (!encoding.equalsIgnoreCase(file.getProperties().get(
				"eu.linksmart.storage.fsd.encoding.method")))
			return new VoidResponse(ErrorCodes.EC_ARG_ERROR,
					"Encoding method my not be changed.");
		String device = getDevice(path, 0);
		LinkSmartFSDConnector hc = devices.get(device);
		VoidResponse vr = hc.setFileProperties(partPath(path, 0), properties);
		if (vr == null)
			return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Can not access " + hc.getFileSystemDeviceId());
		return vr;
	}

	private VoidResponse __setFileProperty(String path, String propertyName,
			String propertyValue) {
		if (propertyName
				.equalsIgnoreCase("eu.linksmart.storage.fsd.encoding.method"))
			return new VoidResponse(ErrorCodes.EC_ARG_ERROR,
					"Encoding method my not be changed.");
		LinkSmartFileResponse hfr = getFile(path);
		if (ErrorCodes.isError(hfr.getErrorCode()))
			return new VoidResponse(hfr.getErrorCode(), "Error getting file ("
					+ hfr.getErrorMessage() + ")");
		LinkSmartFile file = hfr.getResult();
		if (!file.isFile())
			return new VoidResponse(ErrorCodes.EC_IS_NO_FILE,
					"path denotes to no file");
		String device = getDevice(path, 0);
		LinkSmartFSDConnector hc = devices.get(device);
		VoidResponse vr = hc.setFileProperty(partPath(path, 0), propertyName,
				propertyValue);
		if (vr == null)
			return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Can not access " + hc.getFileSystemDeviceId());
		return vr;
	}

	private VoidResponse __truncateFile(String path, long size) {
		LinkSmartFileResponse hfr = getFile(path);
		if (ErrorCodes.isError(hfr.getErrorCode()))
			return new VoidResponse(hfr.getErrorCode(), hfr.getErrorMessage());
		if (!hfr.getResult().isFile())
			return new VoidResponse(ErrorCodes.EC_IS_NO_FILE,
					"Path denotes to no file");
		long actSize = hfr.getResult().getSize();
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put("eu.linksmart.storage.fsd.encoding.method", hfr.getResult()
				.getProperties()
				.get("eu.linksmart.storage.fsd.encoding.method"));
		if (actSize == size)
			return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
		long newChunks = (size + this.stripesize - 1) / stripesize;
		long actChunks = (actSize + this.stripesize - 1) / stripesize;
		long lastNewChunk = size % stripesize;
		if (actChunks == newChunks) {
			String devID = getDevice(path, actChunks - 1);
			LinkSmartFSDConnector hc = devices.get(devID);
			VoidResponse vr = hc.truncateFile(partPath(path, actChunks - 1),
					lastNewChunk);
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Can not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Underlying Error: " + vr.getErrorCode() + " ("
								+ vr.getErrorMessage() + ")");
			return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
		}
		if (actSize < size) {
			String devID = getDevice(path, actChunks - 1);
			LinkSmartFSDConnector hc = devices.get(devID);
			VoidResponse vr = hc.truncateFile(partPath(path, actChunks - 1),
					stripesize);
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Can not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Underlying Error: " + vr.getErrorCode() + " ("
								+ vr.getErrorMessage() + ")");
			for (long partNum = actChunks; partNum < (newChunks - 2); partNum++) {
				devID = getDevice(path, partNum);
				hc = devices.get(devID);
				vr = hc.createFile(partPath(path, partNum), props);
				if (vr == null)
					return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
							"Can not access " + hc.getFileSystemDeviceId());
				if (ErrorCodes.isError(vr.getErrorCode()))
					return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
							"Underlying Error: " + vr.getErrorCode() + " ("
									+ vr.getErrorMessage() + ")");
				vr = hc.truncateFile(path + "/part" + partNum, this.stripesize);
				if (vr == null)
					return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
							"Can not access " + hc.getFileSystemDeviceId());
				if (ErrorCodes.isError(vr.getErrorCode()))
					return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
							"Underlying Error: " + vr.getErrorCode() + " ("
									+ vr.getErrorMessage() + ")");
			}
			devID = getDevice(path, newChunks - 1);
			hc = devices.get(devID);
			vr = hc.createFile(partPath(path, newChunks - 1), props);
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Can not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Underlying Error: " + vr.getErrorCode() + " ("
								+ vr.getErrorMessage() + ")");
			vr = hc
					.truncateFile(path + "/part" + (newChunks - 1),
							lastNewChunk);
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Can not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Underlying Error: " + vr.getErrorCode() + " ("
								+ vr.getErrorMessage() + ")");
			return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
		} else {
			String devID;
			LinkSmartFSDConnector hc;
			VoidResponse vr;
			for (long partNum = actChunks; partNum >= newChunks; partNum--) {
				devID = getDevice(path, partNum);
				hc = devices.get(devID);
				vr = hc.removeFile(partPath(path, partNum));
				if (vr == null)
					return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
							"Can not access " + hc.getFileSystemDeviceId());
				if (ErrorCodes.isError(vr.getErrorCode()))
					return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
							"Underlying Error: " + vr.getErrorCode() + " ("
									+ vr.getErrorMessage() + ")");
			}
			devID = getDevice(path, newChunks - 1);
			hc = devices.get(devID);
			vr = hc.truncateFile(partPath(path, newChunks - 1), lastNewChunk);
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Can not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
						"Underlying Error: " + vr.getErrorCode() + " ("
								+ vr.getErrorMessage() + ")");
			return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
		}
	}

	private VoidResponse __writeFile(String path, long start, String data) {
		if (start < 0)
			return new VoidResponse(ErrorCodes.EC_ARG_ERROR,
					"start must be greater or equal 0");
		LinkSmartFileResponse hfr = getFile(path);
		if (ErrorCodes.isError(hfr.getErrorCode()))
			return new VoidResponse(hfr.getErrorCode(), hfr.getErrorMessage());
		if (!hfr.getResult().isFile())
			return new VoidResponse(ErrorCodes.EC_IS_NO_FILE,
					"Path denotes to no file");
		LinkSmartFile file = hfr.getResult();
		long fileSize = file.getSize();
		if (data.length() == 0)
			return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
		if (start > fileSize)
			return new VoidResponse(ErrorCodes.EC_ARG_ERROR,
					"start is behind end of file");
		String encoding = file.getProperties().get(
				"eu.linksmart.storage.fsd.encoding.method");
		if (encoding == null)
			return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
					"File has no encoding");
		if ((!encoding.equalsIgnoreCase("text"))
				&& (!encoding.equalsIgnoreCase("base64")))
			return new VoidResponse(ErrorCodes.EC_UNKNOWN_ENCODING, "Encoding "
					+ encoding + " unknown");
		byte[] encData = null;
		int dataLength = data.length();
		boolean base64 = false;
		if (encoding.equalsIgnoreCase("base64")) {
			encData = Base64.decode(data);
			dataLength = encData.length;
			base64 = true;
		}
		long firstChunk = start / stripesize;
		long lastChunk = (start - 1 + dataLength) / stripesize;
		long firstChunkStart = start % this.stripesize;
		// int lastChunkLength = (int) ((start + encData.length()) %
		// stripesize);
		long lastExistingChunk = (fileSize - 1) / stripesize;

		String device;
		LinkSmartFSDConnector hc;
		VoidResponse vr;
		String dataChunk = null;
		byte[] encDataChunk = null;
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put("eu.linksmart.storage.fsd.encoding.method", encoding);

		if (firstChunk == lastChunk) {
			if (base64)
				encDataChunk = encData;
			else
				dataChunk = data;
		} else {
			if (base64) {
				encDataChunk = Arrays.copyOf(encData, (int) this.stripesize);
				encData = Arrays.copyOfRange(encData, (int) this.stripesize,
						encData.length);
			} else {
				dataChunk = data.substring(0,
						(int) (this.stripesize - firstChunkStart));
				data = data
						.substring((int) (this.stripesize - firstChunkStart));
			}
		}
		device = getDevice(path, firstChunk);
		hc = devices.get(device);
		if (firstChunk > lastExistingChunk) {
			vr = hc.createFile(partPath(path, firstChunk), props);
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Can not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Underlying Error: " + vr.getErrorCode() + " ("
								+ vr.getErrorMessage() + ")");
		}
		if (!base64) {
			vr = hc.writeFile(partPath(path, firstChunk), firstChunkStart,
					dataChunk);
		} else {
			vr = hc.writeFile(partPath(path, firstChunk), firstChunkStart,
					Base64.encode(encDataChunk));
		}
		if (vr == null)
			return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Can not access " + hc.getFileSystemDeviceId());
		if (ErrorCodes.isError(vr.getErrorCode()))
			return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Underlying Error: " + vr.getErrorCode() + " ("
							+ vr.getErrorMessage() + ")");
		if (firstChunk == lastChunk)
			return vr;

		for (long partNum = firstChunk + 1; partNum < lastChunk; partNum++) {
			device = getDevice(path, partNum);
			hc = devices.get(device);
			if (base64) {
				encDataChunk = Arrays.copyOf(encData, (int) this.stripesize);
				encData = Arrays.copyOfRange(encData, (int) this.stripesize,
						encData.length);
			} else {
				dataChunk = data.substring(0, (int) this.stripesize);
				data = data.substring((int) this.stripesize);
			}
			if (partNum > lastExistingChunk) {
				vr = hc.createFile(partPath(path, partNum), props);
				if (vr == null)
					return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
							"Can not access " + hc.getFileSystemDeviceId());
				if (ErrorCodes.isError(vr.getErrorCode()))
					return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
							"Underlying Error: " + vr.getErrorCode() + " ("
									+ vr.getErrorMessage() + ")");
			}
			if (!base64) {
				vr = hc.writeFile(partPath(path, partNum), firstChunkStart,
						dataChunk);
			} else {
				vr = hc.writeFile(partPath(path, partNum), firstChunkStart,
						Base64.encode(encData));
			}
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Can not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Underlying Error: " + vr.getErrorCode() + " ("
								+ vr.getErrorMessage() + ")");
		}
		device = getDevice(path, lastChunk);
		hc = devices.get(device);
		if (base64)
			dataChunk = data;
		else
			encDataChunk = encData;
		if (lastChunk > lastExistingChunk) {
			vr = hc.createFile(partPath(path, lastChunk), props);
			if (vr == null)
				return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Can not access " + hc.getFileSystemDeviceId());
			if (ErrorCodes.isError(vr.getErrorCode()))
				return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"Underlying Error: " + vr.getErrorCode() + " ("
								+ vr.getErrorMessage() + ")");
		}
		if (!base64) {
			vr = hc.writeFile(partPath(path, lastChunk), firstChunkStart,
					dataChunk);
		} else {
			vr = hc.writeFile(partPath(path, lastChunk), firstChunkStart,
					Base64.encode(encData));
		}
		if (vr == null)
			return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Can not access " + hc.getFileSystemDeviceId());
		if (ErrorCodes.isError(vr.getErrorCode()))
			return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Underlying Error: " + vr.getErrorCode() + " ("
							+ vr.getErrorMessage() + ")");
		return vr;
	}

	private String getDevice(String path, long chunk, ArrayList<String> devs) {
		chunk %= devs.size();
		return devs.get((int) chunk);
	}

	private String getDevice(String path, long chunk) {
		return getDevice(path, chunk, devIDs);
	}

	private String partFilename(long partNum) {
		return partPrefix + partNum;
	}

	private String partPath(String path, long partNum) {
		return LinkSmartFile.createSubPath(path, partFilename(partNum));
	}

	public VoidResponse changeBackendDevices(Collection<String> devices) {
		confLock.writeLock().lock();
		VoidResponse vr = __changeBackendDevices(devices);
		confLock.writeLock().unlock();
		return vr;
	}

	private VoidResponse __changeBackendDevices(Collection<String> devices) {
		LinkSmartFileVectorResponse hfvr = __getDirectoryEntries("/");
		if (ErrorCodes.isError(hfvr.getErrorCode()))
			return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
					"Could not get initial Dir Entries, Caused by ErrorCode "
							+ hfvr.getErrorCode() + " ("
							+ hfvr.getErrorMessage() + ")");
		Collection<LinkSmartFile> rootEntries = hfvr.getResult();
		Hashtable<String, LinkSmartFSDConnector> devs = new Hashtable<String, LinkSmartFSDConnector>();
		ArrayList<String> devIDs = new ArrayList<String>(devices);
		Collections.sort(devIDs);
		Vector<String> newDevs = new Vector<String>();
		for (String devId : devIDs) {
			LinkSmartFSDConnector bc = new LinkSmartFSDConnector(devId,
					FileSystemDeviceUPnPActivator.getNMurl(),
					FileSystemDeviceUPnPActivator.getSOAPurl(), "0");
			devs.put(devId, bc);
			LinkSmartFileVectorResponse hfvr2 = bc.getDirectoryEntries("/");
			if (hfvr2 == null)
				return new VoidResponse(ErrorCodes.EC_NOT_AVAILABLE,
						"depending device " + devId + " not reachable.");
			if (ErrorCodes.isError(hfvr2.getErrorCode()))
				return new VoidResponse(
						ErrorCodes.EC_NOT_AVAILABLE,
						"depending device "
								+ devId
								+ " not able to read root directory. Caused by ErrorCode "
								+ hfvr2.getErrorCode() + " ("
								+ hfvr.getErrorMessage() + ")");

			if (!this.devIDs.contains(devId)) {
				if (!hfvr2.getResult().isEmpty())
					return new VoidResponse(ErrorCodes.EC_DIR_NOT_EMPTY,
							"New device " + devId + " is not empty");
				newDevs.add(devId);
			}
		}

		VoidResponse vr = __replace(devs, devIDs, newDevs, rootEntries);
		if (!ErrorCodes.isError(vr.getErrorCode())) {
			Hashtable<String, LinkSmartFSDConnector> oldDevices = this.devices;
			ArrayList<String> oldDevIDs = this.devIDs;
			this.devices = devs;
			this.devIDs = devIDs;
			for (String devID : oldDevIDs) {
				if (!devIDs.contains(devID)) {
					LinkSmartFSDConnector bc = oldDevices.get(devID);
					hfvr = bc.getDirectoryEntries("/");
					if (hfvr == null) {
						System.err
								.println("Could not reach device "
										+ devID
										+ ", so I can not clean it. (No Problem for me)");
					} else if (ErrorCodes.isError(hfvr.getErrorCode())) {
						System.err
								.println("Error reading root dir on dev "
										+ devID
										+ ", so I can not clean it. (No Problem for me), ErrorCode: "
										+ hfvr.getErrorCode() + " ("
										+ hfvr.getErrorMessage() + ")");
					} else {
						for (LinkSmartFile hf : hfvr.getResult()) {
							VoidResponse error;
							if (hf.isDirectory()) {
								error = bc.removeDirectory(hf.getPath(), true);
							} else {
								System.err
										.println("An File in root of a Backend of a Striped Device?? This should not happen...");
								error = bc.removeFile(hf.getPath());
							}
							if (error == null) {
								System.err.println("Could not read dev "
										+ devID + " to remove " + hf.getPath()
										+ ". (No Problem for me)");
							} else if (ErrorCodes.isError(hfvr.getErrorCode())) {
								System.err.println("Could not remove "
										+ hf.getPath() + " on dev " + devID
										+ ". (No Problem for me) errorCode: "
										+ hfvr.getErrorCode() + " ("
										+ hfvr.getErrorMessage() + ")");
							}
						}
					}
				}
			}
		}
		return vr;
	}

	private VoidResponse __replace(Hashtable<String, LinkSmartFSDConnector> devs,
			ArrayList<String> devIDs, Vector<String> newDevs, Collection<LinkSmartFile> entries) {
		for (LinkSmartFile hf : entries) {
			for (String newDev : newDevs) {
				LinkSmartFSDConnector hc = devs.get(newDev);
				VoidResponse vr = hc.createDirectory(hf.getPath());
				if (vr == null)
					return new VoidResponse(ErrorCodes.EC_IO_EXEPTION, "Could not reach new device " + newDev);
				if (ErrorCodes.isError(vr.getErrorCode()))
					return new VoidResponse(ErrorCodes.EC_IO_EXEPTION, "creating dir " + hf.getPath() + " on dev " + newDev + " EC: " + vr.getErrorCode() + " (" + vr.getErrorMessage() + ")");
			}
			if (hf.isDirectory()) {
				LinkSmartFileVectorResponse hfvr = __getDirectoryEntries(hf
						.getPath());
				if (ErrorCodes.isError(hfvr.getErrorCode()))
					return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
							"Error reading directory " + hf.getPath()
									+ ". Caused by ErrorCode "
									+ hfvr.getErrorCode() + " ("
									+ hfvr.getErrorMessage() + ")");
				VoidResponse vr = __replace(devs, devIDs, newDevs, hfvr.getResult());
				if (ErrorCodes.isError(vr.getErrorCode()))
					return vr;
			} else {
				long chunks = (hf.getSize() + this.stripesize - 1)
						/ this.stripesize;
				for (long part = 0; part < chunks; part++) {
					String oldDevID = getDevice(hf.getPath(), part);
					String newDevID = getDevice(hf.getPath(), part, devIDs);
					if (!oldDevID.equals(newDevID)) {
						LinkSmartFSDConnector oldDev = devices.get(oldDevID);
						LinkSmartFSDConnector newDev = devs.get(newDevID);
						LinkSmartFileResponse hfr = oldDev.getFile(partPath(hf
								.getPath(), part));
						if (hfr == null)
							return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
									"Could not reach dev " + oldDev);
						if (ErrorCodes.isError(hfr.getErrorCode()))
							return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
									"Could not get File " + hf.getPath()
											+ " on old Dev " + oldDev
											+ " ErrorCode: "
											+ hfr.getErrorCode() + " ("
											+ hfr.getErrorMessage() + ")");
						VoidResponse vr = newDev.createFile(partPath(hf
								.getPath(), part), hfr.getResult()
								.getProperties());
						if (vr == null)
							return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
									"Could not reach dev " + newDev);
						if (ErrorCodes.isError(vr.getErrorCode()))
							return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
									"Could not create File " + hf.getPath()
											+ " on new Dev " + newDev
											+ " ErrorCode: "
											+ vr.getErrorCode() + " ("
											+ vr.getErrorMessage() + ")");
						StringResponse sr = oldDev.readFile(partPath(hf
								.getPath(), part), 0, (int) (hfr.getResult()
								.getSize()));
						if (sr == null)
							return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
									"Could not reach dev " + oldDev);
						if (ErrorCodes.isError(sr.getErrorCode()))
							return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
									"Could not read File " + hf.getPath()
											+ " on old Dev " + oldDev
											+ " ErrorCode: "
											+ sr.getErrorCode() + " ("
											+ sr.getErrorMessage() + ")");
						vr = newDev.writeFile(partPath(hf.getPath(), part), 0,
								sr.getResult());
						if (vr == null)
							return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
									"Could not reach dev " + newDev);
						if (ErrorCodes.isError(vr.getErrorCode()))
							return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
									"Could not write File " + hf.getPath()
											+ " on new Dev " + newDev
											+ " ErrorCode: "
											+ vr.getErrorCode() + " ("
											+ vr.getErrorMessage() + ")");
						vr = oldDev.removeFile(partPath(hf.getPath(), part));
						if (vr == null)
							return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
									"Could not reach dev " + oldDev);
						if (ErrorCodes.isError(vr.getErrorCode()))
							return new VoidResponse(ErrorCodes.EC_IO_EXEPTION,
									"Could not remove File " + hf.getPath()
											+ " on old Dev " + oldDev
											+ " ErrorCode: "
											+ vr.getErrorCode() + " ("
											+ vr.getErrorMessage() + ")");
					}
				}
			}
		}
		return new VoidResponse(ErrorCodes.EC_NO_ERROR, null);
	}

	@Override
	public void stopp() throws IOException {

	}
}
