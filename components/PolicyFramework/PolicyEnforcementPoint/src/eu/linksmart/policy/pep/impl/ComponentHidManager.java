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
package eu.linksmart.policy.pep.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Dictionary;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.linksmart.network.CryptoHIDResult;

import eu.linksmart.policy.pep.impl.ComponentHidManager;
import eu.linksmart.policy.pep.impl.PepApplication;
import eu.linksmart.policy.pep.impl.PepConfigurator;

/**
 * <p>PEP Component HID manager</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 *
 */
public class ComponentHidManager {

	/** logger */
	private static final Logger logger 
			= Logger.getLogger(ComponentHidManager.class);
	
	/** PID key */
	private static final String PID_KEY = "PID";
	
	/** SID key */
	private static final String SID_KEY = "SID";
	
	/** DESC key */
	private static final String DESC_KEY = "Desc";
	
	/** default PEP prefix */
	private static final String DEFAULT_PEP_PREFIX = "PEP:";
	
	/** {@link PepApplication} */
	private PepApplication pep;
	
	/**
	 * Constructor
	 * 
	 * @param thePep
	 * 				the {@link PepApplication}
	 */
	public ComponentHidManager(PepApplication thePep) {
		super();
		pep = thePep;
	}
	
	/**
	 * Registers service with network manager
	 * 
	 * @param theSid
	 * 				the SID
	 * @param theEndpoint
	 * 				the endpoint
	 * @return
	 * 				the ID
	 * @throws IOException
	 * 				if encountered during lookup or registration
	 */
	@SuppressWarnings("unchecked")
	public String registerService(String theSid, String theEndpoint) 
			throws IOException {
		String cert = "";
		String desc = "";
		boolean renewCert = true;
		Dictionary configuration = pep.getConfigurator().getConfiguration();
		if (configuration != null) {
			cert = (String) pep.getConfigurator().getConfiguration()
					.get(PepConfigurator.CERT_REF);
			desc = (String) pep.getConfigurator().getConfiguration()
					.get(PepConfigurator.DESC);
			renewCert = Boolean.parseBoolean((String) pep.getConfigurator()
					.getConfiguration().get(PepConfigurator.RENEW_CERT));
		}
		if ((cert == null) || (cert.equals("")) || (cert.equals("NULL")) 
				|| (renewCert)) {		
			return renewService(theSid, theEndpoint, false, null);
		}
		String hid = pep.getNM().createCryptoHIDfromReference(
				cert, theEndpoint);
		logger.debug("Created PEP HID: " + hid);
		if (hid == null) {
			renewCert = false;				
			String pid = (String) pep.getConfigurator().getConfiguration()
					.get(PepConfigurator.PEP_PID);
			if ("".equals(pid)) {
				pid = DEFAULT_PEP_PREFIX + InetAddress.getLocalHost()
						.getHostName();
				pep.getConfigurator().setConfiguration(
						PepConfigurator.PEP_PID, pid);
			}
			String propsXml = getXmlProperties(pid, theSid, desc);
			CryptoHIDResult cHID = pep.getNM().createCryptoHID(
					propsXml, theEndpoint);
			logger.debug("Created PEP HID: " + cHID.getHID());
			pep.getConfigurator().setConfiguration(
					PepConfigurator.CERT_REF, cHID.getCertRef());
			pep.getConfigurator().setConfiguration(
					PepConfigurator.RENEW_CERT, 
					Boolean.toString(renewCert));
			return cHID.getHID();
		}
		return hid;				
	}
	
	/**
	 * Renews service by requesting a new certificate and receiving a new HID
	 * 
	 * @param theSid
	 * 				the SID
	 * @param theEndPoint
	 * 				the endpoint
	 * @param theRemoveHid
	 * 				flag indicating whether to remove the HID first
	 * @param theHid
	 * 				the HID to remove 
	 * @return
	 * 				the HID
	 * @throws IOException
	 * 				any thrown <code>IOException</code>
	 */
	@SuppressWarnings("unchecked")
	public String renewService(String theSid, String theEndPoint, 
			boolean theRemoveHid, String theHid) throws IOException {
		if ((theRemoveHid) && (theHid != null)) {
			removeHid(theHid);
		}
		// create new certificate
		boolean renewCert = false;
		String pid = "";
		String desc = "";
		Dictionary configuration = pep.getConfigurator().getConfiguration();
		if (configuration != null) {
			pid = (String) configuration.get(PepConfigurator.PEP_PID);
		}
		if ((pid == null) || (pid.equals(""))) {
			pid = DEFAULT_PEP_PREFIX + InetAddress.getLocalHost()
					.getHostName();
			try {
				pep.getConfigurator().setConfiguration(PepConfigurator.PEP_PID, 
						pid);
			} catch (NullPointerException npe) {
				logger.error("Could not save configuration");
			}
		}
		if (pid.equals(DEFAULT_PEP_PREFIX + InetAddress.getLocalHost()
					.getHostName())) {
			renewCert = true;
		}
		if (configuration != null) {
			desc = (String) configuration.get(PepConfigurator.DESC);
		}
		String propsXml = getXmlProperties(pid, theSid, desc);
		CryptoHIDResult cHID = pep.getNM().createCryptoHID(propsXml, 
				theEndPoint);
		logger.debug("Created PEP HID: " + cHID.getHID());
		try {
			pep.getConfigurator().setConfiguration(
					PepConfigurator.CERT_REF, cHID.getCertRef());
			pep.getConfigurator().setConfiguration(PepConfigurator.RENEW_CERT, 
					Boolean.toString(renewCert));
		} catch (NullPointerException npe) {
			logger.error("Could not save configuration");
		}
		return cHID.getHID();
	}
	
	/**
	 * Deregisters service from Network Manager
	 * 
	 * @param theHid
	 * 				the service HID
	 * @return
	 * 				a flag indicating success or failure
	 */
	public boolean unregisterService(String theHid) {
		return removeHid(theHid);
	}
	
	/**
	 * Removes the service from the Network Manager
	 * 
	 * @param theHid
	 * 				the HID
	 * @return
	 * 				success indicator flag
	 */
	private boolean removeHid(String theHid) {
		try {
			pep.getNM().removeHID(theHid);
			logger.debug("Removed HID: " + theHid);
			return true;
		} catch (Exception e) {
			logger.error("Error removing HID " + theHid + "from Network Manager: " 
					+ e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
		}
		return false;
	}
	
	/**
	 * @param thePid
	 * 				the PID
	 * @param theSid
	 * 				the SID
	 * @param theDesc
	 * 				the description
	 * @return
	 * 				the XML attribute properties
	 * @throws IOException
	 * 				if encountered during lookup or registration
	 */
	private String getXmlProperties(String thePid, String theSid, 
			String theDesc) throws IOException {
		Properties descProps = new Properties();
		if (thePid != null) {
			descProps.setProperty(PID_KEY, thePid);
		}
		if (theDesc != null) {
			descProps.setProperty(DESC_KEY, theDesc);
		}
		if (theSid != null) {
			descProps.setProperty(SID_KEY, theSid);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		descProps.storeToXML(bos, "");
		return bos.toString();
	}
	
}
