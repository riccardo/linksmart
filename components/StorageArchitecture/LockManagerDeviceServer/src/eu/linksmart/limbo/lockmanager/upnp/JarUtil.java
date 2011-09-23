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

package eu.linksmart.limbo.lockmanager.upnp;

import java.io.File;

import java.io.FileOutputStream;

import java.io.IOException;

import java.io.InputStream;

import java.io.OutputStream;

import java.util.ArrayList;

import java.util.Enumeration;

import java.util.Hashtable;

import java.util.StringTokenizer;



public final class JarUtil {



	private static void extractFile(String localpath, String jarpath)

			throws IOException {

		File properties = new File(localpath);

		

		InputStream in = JarUtil.class.getResourceAsStream("/" + jarpath);

		if (in != null) {

			createDirectory(localpath);

			writefile(properties, in);

			in.close();

		}

		

	}



	private static void writefile(File finalfile, InputStream is)

			throws IOException {

		if(finalfile.exists())

			finalfile.delete();

		finalfile.createNewFile();

		OutputStream out = new FileOutputStream(finalfile);

		byte[] buffer = new byte[256];

		while (true) {

			int n = is.read(buffer);

			if (n < 0)

				break;

			out.write(buffer, 0, n);

		}

		out.flush();

		out.close();

	}



	private static void createDirectory(String stringpath) {

		StringTokenizer st = new StringTokenizer(stringpath, "/");

		ArrayList<String> arrLst = new ArrayList<String>();

		while (st.hasMoreTokens()) {

			String token = st.nextToken();

			token = token.trim();

			arrLst.add(token);

		}

		String path = new String();



		for (int i = 0; i < arrLst.size() - 1; i++) {

			path = path + arrLst.get(i);

			createFolder(path);

			path = path + "/";

		}

	}



	public static void createFolder(String path) {

		File directory = new File(path);

		directory.mkdir();

	}



	public static void extractFilesJar(Hashtable fileshash) throws IOException {



		Enumeration filesenum = fileshash.keys();

		String localpath = new String();

		String jarpath = new String();

		while (filesenum.hasMoreElements()) {

			localpath = filesenum.nextElement().toString();

			jarpath = fileshash.get(localpath).toString();

			extractFile(localpath, jarpath);

		}

	}

}


