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

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.upnp.UPnPDevice;

public class LockManagerDeviceUPnPActivator implements BundleActivator {
	private static Logger logger = Logger.getLogger(LockManagerDeviceUPnPActivator.class.getName());
	
	static BundleContext context;

	final static private String FOLDERPATH = "LockManagerDeviceServer/config";
	final static private String LM_CONF_PATH = "LockManagerDeviceServer/config/LMconfig.xml";
	final static private String LM_CONF_PATH_JAR = "LMconfig.xml";
	
	private LockManagerDeviceDevice device;
	static private String LMPath;
	static private String nmAddress;

	private void doServiceRegistration() {

		this.device = new LockManagerDeviceDevice(context);
		this.device.doServiceRegistration();

	}

	
	public void start(BundleContext context) throws Exception {
		try {
			File f = new File(LM_CONF_PATH);
			if (!f.exists()) {
				logger.info("No Config, Placing a default one, which will NOT work.");
				try {
					JarUtil.createFolder(FOLDERPATH);
					Hashtable<String, String> ht = new Hashtable<String, String>();
					ht.put(LM_CONF_PATH, LM_CONF_PATH_JAR);
					JarUtil.extractFilesJar(ht);
				} catch (IOException e) {
					logger.fatal("IOException creating config files", e);
					throw e;
				}
			}
			FileReader fr = new FileReader(LM_CONF_PATH);
			Document d = new SAXBuilder().build(fr);
			Element root = d.getRootElement();
			Element nmConf = root.getChild("NetworkManager");
			nmAddress = nmConf.getAttributeValue("url");
			Element  cm= root.getChild("LockManager");
			LMPath = cm.getAttributeValue("path");
			logger.info("Using NetworkManager at" + nmAddress);
			logger.info("Using Backend Path" + LMPath);
		} catch (IOException e) {
			logger.fatal("IOException reading config", e);
			throw e;
		}
		LockManagerDeviceUPnPActivator.context = context;
		doServiceRegistration();
	}

	
	public void stop(BundleContext context) throws Exception {
		this.device.doServiceUnregistration();
		this.device = null;
	}
	
	public static String getNmAddress() {
		return nmAddress;
	}
	
	public static String getLMPath() {
		return LMPath;
	}
}
