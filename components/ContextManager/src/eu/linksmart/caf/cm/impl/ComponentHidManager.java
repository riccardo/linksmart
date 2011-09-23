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
package eu.linksmart.caf.cm.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.linksmart.network.CryptoHIDResult;

/**
 * Manages the process of creating HIDs with the Network Manager
 * 
 * @author Michael Crouch
 * 
 */
public class ComponentHidManager {

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(ComponentHidManager.class);
	
	/** CrytoHID PID key */
	private static final String PID_KEY = "PID";

	/** CrytoHID SID key */
	private static final String SID_KEY = "SID";

	/** CrytoHID Description key */
	private static final String DESC_KEY = "Desc";

	/** Base of the component name */
	private static final String COMPONENT_NAME = "ContextManager:";

	/** the host name */
	private static String hostName = "NO_HOST_NAME";

	/** the base name */
	private static String baseName;

	/** the {@link ContextManagerApplication} */
	private ContextManagerApplication cm;

	/** the endpoint */
	private String endpoint;

	static {
		/**
		 * Initialise the fall-back name to use as the PID of the component if
		 * none is specified
		 */
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			hostName = "NO_HOST_NAME";
		}

		baseName = COMPONENT_NAME + hostName;
	}

	/**
	 * Constructor
	 * 
	 * @param cm
	 *            the {@link ContextManagerApplication}
	 * @param endpoint
	 *            the Web Service endpoint of the service to register
	 */
	public ComponentHidManager(ContextManagerApplication cm, String endpoint) {
		this.cm = cm;
		this.endpoint = endpoint;
	}

	/**
	 * Registers the service with the Network Manager.<p> If a Crypto-reference
	 * exists, it attempts to use that to create the CryptoHID for the service,
	 * otherwise a new CryptoHID is created.
	 * 
	 * @return the HID of the registered service
	 * @throws IOException
	 */
	public String registerService() throws IOException {
		String cert =
				(String) cm.getConfigurator().getConfiguration().get(
						CmConfigurator.CERT_REF);
		Boolean renewCert =
				Boolean.parseBoolean((String) cm.getConfigurator()
						.getConfiguration().get(CmConfigurator.USE_CERT_REF));

		if ("".equals(cert) || "NULL".equals(cert) || !renewCert) {
			return createNewCryptoHID();
		} else {
			logger.info("Renewing CryptoHID from reference " + cert);
			String hid =
					cm.getNM().createCryptoHIDfromReference(cert, endpoint);
		
			if (hid == null) {
				hid = renewService(false);
			}
			return hid;

		}
	}

	/**
	 * Renews the CryptoHID with the latest configuration, after removing the
	 * previously registered HID
	 * 
	 * @param removeHid
	 *            Boolean flag dictating whether to remove an existing HID
	 * @return the HID of the renewed service
	 * @throws IOException
	 */
	public String renewService(boolean removeHid) throws IOException {

		if (removeHid)
			unregisterService(cm.getHid());
		return createNewCryptoHID();
	}

	/**
	 * Creates the CryptoHID certificate using the properties from the
	 * {@link CmConfigurator}, and registers with the Network Manager
	 * 
	 * @return the registered HID
	 * @throws IOException
	 */
	private String createNewCryptoHID() throws IOException {
		logger.info("Creating a new CryptoHID");
		String pid =
				(String) cm.getConfigurator().getConfiguration().get(
						CmConfigurator.PID);
		if ("".equals(pid)) {
			pid = baseName;
			cm.getConfigurator().setConfiguration(CmConfigurator.PID, pid);
		}

		String sid = CmConfigurator.CM_SERVICE_PID;
		String desc =
				(String) cm.getConfigurator().getConfiguration().get(
						CmConfigurator.DESC);

		Properties descProps = new Properties();
		descProps.setProperty(PID_KEY, pid);
		descProps.setProperty(DESC_KEY, desc);
		descProps.setProperty(SID_KEY, sid);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		descProps.storeToXML(bos, "");

		String propsXml = bos.toString();
		CryptoHIDResult cHID = cm.getNM().createCryptoHID(propsXml, endpoint);
		cm.getConfigurator().setConfiguration(CmConfigurator.CERT_REF,
				cHID.getCertRef());
		return cHID.getHID();
	}

	/**
	 * Unregisters the service from the Network Manager
	 * 
	 * @param hid
	 *            the HID of the service to unregister
	 * @return success
	 */
	public boolean unregisterService(String hid) {
		try {
			logger.info("Unregistering service " + hid);
			cm.getNM().removeHID(hid);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
