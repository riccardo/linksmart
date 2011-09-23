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
package eu.linksmart.policy.pdp.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Dictionary;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.linksmart.network.CryptoHIDResult;

/**
 * <p>PDP component HID manager</p>
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
	
	/** Description key */
	private static final String DESC_KEY = "Desc";
	
	/** PDP PID default prefix */
	private static final String DEFAULT_PDP_PREFIX = "PDP:";	
	
	/** {@link PdpApplication} */
	private PdpApplication pdp = null;
	
	/**
	 * Constructor
	 * 
	 * @param thePdp
	 * 				the {@link PdpApplication}
	 * 
	 */
	public ComponentHidManager(PdpApplication thePdp) {
		pdp = thePdp;
	}
	
	/**
	 * @param theSid
	 * 				the SID
	 * @param theEndPoint
	 * 				the endpoint location 
	 * @return
	 * 				the HID
	 * @throws IOException
	 * 				any <code>IOException</code> that is thrown
	 */
	@SuppressWarnings("unchecked")
	public String registerService(String theSid, String theEndPoint) 
			throws IOException {
		Dictionary configuration = pdp.getConfigurator().getConfiguration();
		String cert = "";
		String desc = "";
		boolean renewCert = true;
		if (configuration != null) {
		cert = (String) configuration.get(
				PdpConfigurator.PDPSERVICE_CERT_REF);
		desc = (String) configuration.get(
				PdpConfigurator.PDPSERVICE_DESCRIPTION);
		renewCert = Boolean.parseBoolean((String) 
				configuration.get(PdpConfigurator.RENEW_CERTS));
		}		
		if ((cert == null) || (cert.equals("")) || (cert.equals("NULL")) 
				|| (renewCert)) {
			return renewService(theSid, theEndPoint, false, null);
		}
		String hid = pdp.getNM().createCryptoHIDfromReference(cert, theEndPoint);
		logger.debug("Created PDP HID: " + hid);
		if (hid == null) {
			renewCert = false;				
			String pid = (String) pdp.getConfigurator().getConfiguration()
					.get(PdpConfigurator.PDP_PID);
			if ("".equals(pid)) {
				pid = DEFAULT_PDP_PREFIX 
						+ InetAddress.getLocalHost().getHostName();
				pdp.getConfigurator().setConfiguration(
						PdpConfigurator.PDP_PID, pid);
			}		
			String propsXml = getXmlProperties(pid, theSid, desc);
			CryptoHIDResult cHID = pdp.getNM().createCryptoHID(propsXml, 
					theEndPoint);
			logger.debug("Created PDP HID: " + cHID.getHID());
			pdp.getConfigurator().setConfiguration(
					PdpConfigurator.PDPSERVICE_CERT_REF, cHID.getCertRef());
			pdp.getConfigurator().setConfiguration(
					PdpConfigurator.RENEW_CERTS,
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
		Dictionary configuration = pdp.getConfigurator().getConfiguration();
		String pid = null;
		if (configuration != null) {
			pid = (String) configuration.get(PdpConfigurator.PDP_PID);
		}
		if ((pid == null) || (pid.equals(""))) {
			pid = DEFAULT_PDP_PREFIX + InetAddress.getLocalHost()
					.getHostName();
			try {
				pdp.getConfigurator().setConfiguration(PdpConfigurator.PDP_PID, 
						pid);
			} catch (NullPointerException npe) {
				logger.error("Could not save configuration");
			}
		}
		if (pid.equals(DEFAULT_PDP_PREFIX + InetAddress.getLocalHost()
					.getHostName())) {
			renewCert = true;
		}
		String desc = "";
		if (configuration != null) {
			desc = (String) configuration.get(
				PdpConfigurator.PDPSERVICE_DESCRIPTION);
		}
		String propsXml = getXmlProperties(pid, theSid, desc);
		CryptoHIDResult cHID = pdp.getNM().createCryptoHID(propsXml, 
				theEndPoint);
		logger.debug("Created PDP HID: " + cHID.getHID());
		try {
			pdp.getConfigurator().setConfiguration(
					PdpConfigurator.PDPSERVICE_CERT_REF, cHID.getCertRef());
			pdp.getConfigurator().setConfiguration(PdpConfigurator.RENEW_CERTS, 
					Boolean.toString(renewCert));
		} catch (NullPointerException npe) {
			logger.error("Could not save configuration");
		}
		return cHID.getHID();
	}
	
	/**
	 * @param theSid
	 * 				the SID
	 * @param theEndpoint
	 * 				the endpoint location 
	 * @return
	 * 				the HID
	 * @throws IOException
	 * 				any <code>IOException</code> that is thrown
	 */	
	@SuppressWarnings("unchecked")
	public String registerAdminService(String theSid, String theEndpoint) 
			throws IOException {
		Dictionary configuration = pdp.getConfigurator().getConfiguration();
		if (configuration == null) {
			return renewAdminService(theSid, theEndpoint, false, null);
		}
		String cert = (String) configuration.get(
				PdpConfigurator.PDPADMINSERVICE_CERT_REF);
		String desc = (String) configuration.get(
				PdpConfigurator.PDPADMINSERVICE_DESCRIPTION);
		boolean renewCert = Boolean.parseBoolean((String) configuration.get(
				PdpConfigurator.RENEW_CERTS));
		if ((cert == null) || (desc == null)) {
			return renewAdminService(theSid, theEndpoint, false, null);
		}
		if (("".equals(cert)) || ("NULL".equals(cert)) || (renewCert))	{					
			return renewAdminService(theSid, theEndpoint, false, null);
		}
		String hid = pdp.getNM().createCryptoHIDfromReference(cert, theEndpoint);
		logger.debug("Created PDP Admin HID: " + hid);
		if (hid == null) {
			renewCert = false;
			
			String pid = (String) pdp.getConfigurator().getConfiguration().get(
					PdpConfigurator.PDPADMIN_PID);
			if (pid.equals("")) {
				pid = "PDP:Admin:" + InetAddress.getLocalHost().getHostName();
				pdp.getConfigurator().setConfiguration(
						PdpConfigurator.PDPADMIN_PID, pid);
			}
			if (pid.equals("PDP:Admin:" 
					+ InetAddress.getLocalHost().getHostName())) {
				renewCert = true;
			}
			String propsXml = getXmlProperties(pid, theSid, desc);
			CryptoHIDResult cHID = pdp.getNM().createCryptoHID(propsXml, 
					theEndpoint);
			logger.debug("Created PDP Admin HID from scratch: " + cHID.getHID());
			pdp.getConfigurator().setConfiguration(
					PdpConfigurator.PDPADMINSERVICE_CERT_REF, cHID.getCertRef());
			pdp.getConfigurator().setConfiguration(
					PdpConfigurator.RENEW_CERTS, 
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
	public String renewAdminService(String theSid, String theEndPoint, 
			boolean theRemoveHid, String theHid) throws IOException {
		if ((theRemoveHid) && (theHid != null)) {
			removeHid(theHid);
		}
		// create new certificate
		boolean renewCert = false;
		Dictionary configuration = pdp.getConfigurator().getConfiguration();
		String pid = null;
		if (configuration != null) {
			pid = (String) configuration.get(PdpConfigurator.PDPADMIN_PID);
		}
		if ((pid == null) || (pid.equals(""))) {
			pid = "PDP:Admin:" + InetAddress.getLocalHost().getHostName();
			try {
				pdp.getConfigurator().setConfiguration(
						PdpConfigurator.PDPADMIN_PID, pid);
			} catch (NullPointerException npe) {
				logger.error("Could not wrote configuration");
			}
		}			
		if (pid.equals("PDP:Admin:" 
				+ InetAddress.getLocalHost().getHostName())) {
			renewCert = true;
		}
		String desc = "";
		if (configuration != null) {
			desc = (String) configuration.get(
					PdpConfigurator.PDPADMINSERVICE_DESCRIPTION);
		}
		String propsXml = getXmlProperties(pid, theSid, desc);
		CryptoHIDResult cHID = pdp.getNM().createCryptoHID(propsXml, 
				theEndPoint);
		logger.debug("Renewed PDP Admin HID from scratch: " + cHID.getHID());
		try {
			pdp.getConfigurator().setConfiguration(
					PdpConfigurator.PDPADMINSERVICE_CERT_REF, cHID.getCertRef());
			pdp.getConfigurator().setConfiguration(PdpConfigurator.RENEW_CERTS, 
					Boolean.toString(renewCert));
		} catch (NullPointerException npe) {
			logger.error("Could not save configuration");
		}
		return cHID.getHID();
	}
	
	/**
	 * Deregisters the PDP service
	 * 
	 * @param theHid
	 * 				the HID
	 * @return
	 * 				success indicator flag
	 */
	public boolean unregisterService(String theHid) {
		return removeHid(theHid);
	}
	
	/**
	 * Deregisters the PDP Admin service
	 * 
	 * @param theHid
	 * 				the HID
	 * @return
	 * 				success indicator flag
	 */
	public boolean unregisterAdminService(String theHid) {
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
			pdp.getNM().removeHID(theHid);
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
	 * 				the XML properties <code>String</code>
	 * @throws IOException
	 * 				any <code>IOException</code> that is thrown
	 */
	private String getXmlProperties(String thePid, String theSid, 
			String theDesc) throws IOException {
		Properties props = new Properties();
		if (thePid != null) {
			props.setProperty(PID_KEY, thePid);
		}
		if (theDesc != null) {
			props.setProperty(DESC_KEY, theDesc);
		}
		if (theSid != null) {
			props.setProperty(SID_KEY, theSid);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		props.storeToXML(bos, "");
		return bos.toString();
	}
	
}
