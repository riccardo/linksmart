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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Hashtable;

public class InconsistencyDB {

	private static class DevHolder {

		private static class SuffixFilter implements FilenameFilter {
			private String suffix;

			public SuffixFilter(String suffix) {
				this.suffix = suffix;
			}

			
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(suffix);
			}
		}

		private File dbDir;
		private String deviceID;
		private long fileID;
		private static final DecimalFormat fileFormat = new DecimalFormat("20");
		private static final SuffixFilter idbFilter = new SuffixFilter(".idb");
		private static final SuffixFilter lockFilter = new SuffixFilter(
				".idb.lock");

		public DevHolder(File incDBDir, String deviceID) {
			this.deviceID = deviceID;
			dbDir = new File(incDBDir, deviceID);
			// TODO: Find highest ID and set + 1
			fileID = 0;
		}

		public String getDeviceID() {
			return deviceID;
		}

		public String storeEntry(String entry) throws IOException {
			synchronized (dbDir) {
				String id = fileFormat.format(fileID) + ".idb";
				File f = new File(dbDir, id);
				fileID++;
				FileWriter fw = new FileWriter(f);
				fw.write(entry);
				fw.close();
				return id;
			}
		}

		public String[] getEntryIDs() {
			String[] filenames = dbDir.list(idbFilter);
			Arrays.sort(filenames);
			return filenames;
		}

		public String[] getLockedEntryIDs() {
			String[] filenames = dbDir.list(lockFilter);
			Arrays.sort(filenames);
			return filenames;
		}

		public String getLockFirstEntry() throws IOException {
			synchronized (dbDir) {
				return getLockEntry(getEntryIDs()[0]);
			}
		}

		public String getLockEntry(String entryID) throws IOException {
			synchronized (dbDir) {
				File f = new File(dbDir, entryID);
				f.renameTo(new File(dbDir, entryID + ".lock"));
				BufferedReader br = new BufferedReader(new FileReader(f));
				String result = entryID + ";" + br.readLine();
				br.close();
				return result;
			}
		}

		public void removeLockedEntry(String entryID) {
			synchronized (dbDir) {
				File f = new File(dbDir, entryID + ".lock");
				f.delete();
			}
		}

		public int size() {
			return dbDir.list(idbFilter).length;
		}
	}

	private String incDBPath;

	private File incDBDir;

	private Hashtable<String, DevHolder> devHolders;

	public InconsistencyDB(String incDBPath) {
		this.incDBPath = incDBPath;
		incDBDir = new File(incDBPath);
		if (!incDBDir.exists())
			incDBDir.mkdirs();
		devHolders = new Hashtable<String, DevHolder>();
	}

	private DevHolder getDevHolder(String deviceID) {
		synchronized (devHolders) {
			DevHolder result = devHolders.get(deviceID);
			if (result == null) {
				result = new DevHolder(incDBDir, deviceID);
				devHolders.put(deviceID, result);
			}
			return result;
		}
	}

	public String addEntry(IncDBEntry entry) throws IOException {
		String device = entry.getDeviceID();
		DevHolder devHolder = getDevHolder(device);
		String entryID = devHolder.storeEntry(entry.toString());
		entry.setEntryID(entryID);
		return entryID;
	}
	
	public String[] getEntryIDs(String deviceID) {
		DevHolder devHolder = getDevHolder(deviceID);
		return devHolder.getEntryIDs();
	}
	
	public String[] getLockedEntryIDs(String deviceID) {
		DevHolder devHolder = getDevHolder(deviceID);
		return devHolder.getLockedEntryIDs();
	}
	
	public IncDBEntry getLockFirstEntry(String deviceID) throws IOException {
		DevHolder devHolder = getDevHolder(deviceID);
		String answer = devHolder.getLockFirstEntry();
		IncDBEntry result = IncDBEntry.readIncDBEntry(answer);
		return result;
	}

	public IncDBEntry getLockFirstEntry(String deviceID, String entryID) throws IOException {
		DevHolder devHolder = getDevHolder(deviceID);
		String answer = devHolder.getLockEntry(entryID);
		IncDBEntry result = IncDBEntry.readIncDBEntry(answer);
		return result;
	}
	
	public void removeLockedEntry(String deviceID, String entryID) {
		DevHolder devHolder = getDevHolder(deviceID);
		devHolder.removeLockedEntry(entryID);
	}
	
	public int size(String deviceID) {
		DevHolder devHolder = getDevHolder(deviceID);
		return devHolder.size();
	}
}
