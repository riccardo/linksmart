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
package eu.linksmart.policy.pdp.admin.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.linksmart.policy.pdp.PdpAdmin;
import eu.linksmart.policy.pdp.PdpAdminError;

/**
 * <p>Monitors policies stored in a local file system</p>
 *
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 */
public class FileSystemPdpAdminService implements PdpAdmin {

	/** logger */
	private static final Logger logger 
			= Logger.getLogger(FileSystemPdpAdminService.class);
	
	/** default file system repository location */
	private static final String DEFAULT_LOCATION
			= "PolicyFramework/PolicyDecisionPoint/PolicyFolder";
	
	/** active policy folder identifier */
	private static final String ACTIVE = "Active";
	
	/** inactive policy folder identifier */
	private static final String INACTIVE = "Inactive";
	
	/** file database location */
	private String fileBaseLoc;
	
	/** active policy folder reference */
	private File activeFolder = null;
	
	/** inactive policy folder reference */
	private File inactiveFolder = null;
	
	/** policy <code>Map</code> */
	private Map<String, Boolean> policyMap = null;
	
	/**
	 * Constructor
	 * 
	 * @param theLocation
	 * 				repository location
	 */
	public FileSystemPdpAdminService(String theLocation) {
		if (theLocation == null) {
			logger.warn("NULL file system location provided, using default "
					+ "location");
			fileBaseLoc = DEFAULT_LOCATION;
		} else {
			fileBaseLoc = theLocation;
		}
		policyMap = new HashMap<String, Boolean>();
		File baseFolder = new File(fileBaseLoc);
		baseFolder.mkdirs();
		if (baseFolder.isDirectory()) {
			activeFolder = new File(baseFolder.getAbsolutePath() 
					+ File.separator + ACTIVE);
			inactiveFolder = new File(baseFolder.getAbsolutePath() 
					+ File.separator + INACTIVE);
			activeFolder.mkdirs();
			inactiveFolder.mkdirs();
		}
		refresh();
	}
	
	/**
	 * @return
	 * 				the active policy folder <code>File</code>
	 */
	public File getActivePolicyFolder(){
		return activeFolder;
	}
	
	/**
	 * @return
	 * 				the inactive policy folder <code>File</code>
	 */
	public File getInactivePolicyFolder(){
		return inactiveFolder;
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#activatePolicy(java.lang.String)
	 */
	@Override
	public boolean activatePolicy(String thePolicyId) throws RemoteException {
		boolean result = movePolicy(inactiveFolder, activeFolder, thePolicyId);
		refresh();
		return result;
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#deactivatePolicy(java.lang.String)
	 */
	@Override
	public boolean deactivatePolicy(String thePolicyId) throws RemoteException {
		boolean result = movePolicy(activeFolder, inactiveFolder, thePolicyId);
		refresh();
		return result;
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getActivePolicyList()
	 */
	@Override
	public String[] getActivePolicyList() throws RemoteException {
		return getPolicyList(true);
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getInActivePolicyList()
	 */
	@Override
	public String[] getInActivePolicyList() throws RemoteException {
		return getPolicyList(false);
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getPolicy(java.lang.String)
	 */
	@Override
	public String getPolicy(String thePolicyId) throws RemoteException {		
		refresh();
		if (policyMap.containsKey(thePolicyId)) {
			File policy;
			boolean policyStatus = policyMap.get(thePolicyId).booleanValue();
			if (policyStatus) {
				policy = new File(activeFolder.getAbsolutePath() 
						+ File.separator + thePolicyId);
			} else {
				policy = new File(inactiveFolder.getAbsolutePath() 
						+ File.separator + thePolicyId);
			}
			StringBuilder contents = new StringBuilder();
			try  {
				BufferedReader input = new BufferedReader(
						new FileReader(policy));
				String line = null;
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
				input.close();
			} catch (IOException ioe){
				logger.error("Exception: " + ioe.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", ioe);
				}
				throw new RemoteException(
						PdpAdminError.POLICY_NOT_VALID.toString(), ioe);
			}
			return contents.toString();
		}
		logger.info("Could not find policy with ID: " + thePolicyId);
		throw new RemoteException(
				PdpAdminError.POLICY_NOT_FOUND.toString());
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#publishPolicy(java.lang.String, 
	 * 		java.lang.String)
	 */
	@Override
	public boolean publishPolicy(String thePolicyId, String thePolicy)
			throws RemoteException {
		refresh();
		if (policyMap.containsKey(thePolicyId)) {
			logger.warn("Cannot publish policy \"" + thePolicyId 
					+ "\" - already exists!");
			throw new RemoteException(
					PdpAdminError.POLICY_ID_ALREADY_TAKEN.toString());
		}		
		try {
			File policyFile = new File(inactiveFolder.getAbsolutePath() 
					+ File.separator + thePolicyId);
			if (policyFile.createNewFile()) {
				FileOutputStream out = new FileOutputStream(policyFile);
				out.write(thePolicy.getBytes());
				out.flush();
				out.close();
				refresh();
				return true;
			}
			logger.warn("Attempted to overwrite existing file: " 
					+ policyFile.getCanonicalPath());
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
		} catch (Exception e) {
			logger.error("Exception: " + e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#removePolicy(java.lang.String)
	 */
	@Override
	public boolean removePolicy(String thePolicyId) throws RemoteException {
		refresh();	
		if (!policyMap.containsKey(thePolicyId)) {
			return true;
		}
		File policy;
		if (policyMap.get(thePolicyId).booleanValue()) {
			policy = new File(activeFolder.getAbsolutePath() 
					+ File.separator + thePolicyId);
		} else {
			policy = new File(inactiveFolder.getAbsolutePath() 
					+ File.separator + thePolicyId);
		}
		if (policy.exists()) {
			policy.delete();
		}
		refresh();	
		return true;
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String theId) throws RemoteException {
		throw new RemoteException(
				PdpAdminError.PROPERTY_NOT_SUPPORTED.toString());
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#setProperty(java.lang.String, 
	 * 		java.lang.String)
	 */
	@Override
	public boolean setProperty(String theKey, String theValue) 
			throws RemoteException {
		throw new RemoteException(
				PdpAdminError.PROPERTY_NOT_SUPPORTED.toString());
	}
	
	/** Refreshes the instance data */
	private void refresh(){
		policyMap.clear();
		String[] list = activeFolder.list();
		int ll = list.length;
		for (int i=0; i < ll; i++) {
			policyMap.put(list[i], Boolean.TRUE);
		}
		list = inactiveFolder.list();
		ll = list.length;
		for (int i=0; i < list.length; i++) {
			if (policyMap.containsKey(list[i])) {
				logger.warn("Policy with same id stored in both ACTIVE and " 
						+ "INACTIVE folders? Policy will be marked ACTIVE.");
			} else {
				policyMap.put(list[i], Boolean.FALSE);
			}
		}
	}
	
	/**
	 * @param theFrom
	 * 				the origin folder
	 * @param theTo
	 * 				the target folder
	 * @param thePolicyId
	 * 				the policy identifier
	 * @return
	 * 				a success indicator flag
	 * @throws RemoteException
	 * 				PdpAdminError.POLICY_NOT_FOUND
	 * 					if the policy could not be found
	 * 				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR
	 * 					if an internal error occurred
	 */
	private boolean movePolicy(File theFrom, File theTo, String thePolicyId) 
			throws RemoteException {
		try {
			File policy = new File(theFrom.getAbsolutePath() 
					+ File.separator + thePolicyId);
			if (policy.exists()) {
				File movedPolicy = new File(theTo.getAbsolutePath() 
						+ File.separator + thePolicyId);
				if (!movedPolicy.exists()) {
					policy.renameTo(movedPolicy);
				}
				refresh();
				return true;
			}
			throw new RemoteException(
					PdpAdminError.POLICY_NOT_FOUND.toString());
		}
		catch (Exception e) {
			logger.error("Exception: " + e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString(), e);
		}
	}
	
	/**
	 * Returns either active or inactive policies 
	 * 
	 * @param getActive
	 * 				a flag indicating whether to return active or inactive 
	 * 				policies
	 * @return
	 * 				policies as XML <code>String</code>s
	 */
	private String[] getPolicyList(boolean getActive) {		
		refresh();
		final ArrayList<String> resultList = new ArrayList<String>();
		final Iterator<String> it = policyMap.keySet().iterator();
		String policyId = null;
		while(it.hasNext()) {
			policyId = it.next();
			if (policyMap.get(policyId).booleanValue() == getActive) {
				resultList.add(policyId);
			}
		}
		return resultList.toArray(new String[resultList.size()]);
	}
	
}
