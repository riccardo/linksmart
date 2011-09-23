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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.policy.pdp.admin.bundle.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

/**
 * <p>PDP DB creator</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann 
 *
 */
public class PdpDbCreator {
	
	/** logger */
	private static final Logger logger 
			= Logger.getLogger(PdpDbCreator.class);
	
	/** Private no-args constructor */
	private PdpDbCreator() {
		super();
	}
	
	/**
	 * Returns whether the PDP exists
	 * 
	 * @param dbLoc 
	 * 				the database location in the file system
	 * @return
	 * 				a flag
	 */
	public static boolean doesDbExist(String dbLoc) {		
		if ((!(dbLoc == null)) && (!dbLoc.equals(""))) {
			File existDbHome = new File(dbLoc);
			if (!existDbHome.exists()) {
				return false;
			}			
			if (existDbHome.isDirectory()) {
				File[] files = existDbHome.listFiles();
				for(int i=0; i<files.length; i++) {
					File file = files[i];
					if (file.getName().equals("conf.xml"))
						return true;
				}
			}
		}			
		return false;
	}
	
	/**
	 * Creates the PDP policy database if it doesn't already exist
	 * 
	 * @param createAtLoc
	 * 				the location to create PDP database at
	 * @param context
	 * 				the {@link BundleContext}
	 * @return
	 * 				a success indicator flag
	 */
	public static boolean createPolicyDb(String createAtLoc, 
			BundleContext context) {
		try	{
			// if folder at createAtLoc doesn't exist, create it
			File dir = new File(createAtLoc);
			if ((!dir.exists()) && (!dir.mkdirs())) {
				return false;				
			}
			// copy the "conf.xml" file from resources to createAtLoc
			URL confUrl = context.getBundle().getResource("resources/conf.xml");
			File confFileInDir = new File(createAtLoc + "/conf.xml");
			if (confUrl != null) {
				InputStream in = confUrl.openStream();
				OutputStream out = new FileOutputStream(confFileInDir);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
			// create "data" directory
		    File dataDir = new File(createAtLoc + "/data/");
		    if (!dataDir.mkdirs()) {
		    	return false;
		    }		    
		    return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Reads a file and returns a <code>String</code> representation
	 * 
	 * @param loc
	 * 				the file location
	 * @param context
	 * 				the {@link BundleContext}
	 * @return
	 * 				the file content <code>String</code>
	 */
	public static String getFileAsString(String loc, BundleContext context) {
		URL confUrl = context.getBundle().getResource(loc);		
		InputStream input;
		try {
			input = confUrl.openStream();
			if (input != null) { 
				StringBuilder sb = new StringBuilder(); 
				String line; 
				try { 
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(input, "UTF-8")); 
					while ((line = reader.readLine()) != null) { 
						sb.append(line).append("\n"); 
					} 
				} finally { 
					input.close(); 
				} 
				return sb.toString(); 
			}
			return ""; 
		} catch (IOException e) {
			logger.debug("Stack trace: ", e);
		}
		return "";
	}
	
}

