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

package eu.linksmart.limbo.cookiedevice.backend;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import eu.linksmart.storage.helper.ResponseFactory;

public class LocalHTBackend implements HTBackend {

	private File dataFile;
	
	public LocalHTBackend(File dataFile) throws IOException {
		System.out.println("LocalHTBackend Constructor");
		if (dataFile.exists()) {
			if (!dataFile.isFile()) {
				throw new IOException("dataFile has to point to a file. This file needs not to exist.");
			}
			this.dataFile = dataFile;
		} else {
			System.out.println("File does not exist");
			File parent = dataFile.getParentFile();
			if (!parent.exists()) {
				throw new IOException("The given file and the parent directory do not exist.");
			}
			dataFile.createNewFile();
			this.dataFile = dataFile;
			Hashtable<String, String> data = new Hashtable<String, String>();
			
			System.out.println("Will store sample");
			storeHT(data);
		}
	}
	
	@Override
	public Hashtable<String, String> readHT() throws IOException {
		return (Hashtable<String, String>) ResponseFactory.loadDictionaryFromFile(dataFile);
	}

	@Override
	public void storeHT(Hashtable<String, String> data) throws IOException {
		ResponseFactory.storeDictionaryToFile(data, dataFile);
	}
	
	public void deleteData() {
		dataFile.delete();
	}
}
