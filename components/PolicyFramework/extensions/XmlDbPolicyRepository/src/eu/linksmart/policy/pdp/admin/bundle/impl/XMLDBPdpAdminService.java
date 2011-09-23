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

import java.io.ByteArrayInputStream;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.exist.storage.io.ExistIOException;
import org.exist.xmldb.LocalCollectionManagementService;
import org.exist.xmldb.XmldbURI;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import eu.linksmart.existdb.ExistDatabaseManager;

import eu.linksmart.policy.pdp.PdpAdmin;
import eu.linksmart.policy.pdp.PdpAdminError;

/**
 * <p>XML DB {@link PdpAdmin} implementation</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 */
public class XMLDBPdpAdminService implements PdpAdmin {
	
	/** logger */
	private static final Logger logger 
			= Logger.getLogger(XMLDBPdpAdminService.class);
	
	/** active policy identifier */
	private static final String ACTIVE = "ActivePolicy";
	
	/** inactive policy identifier */
	private static final String INACTIVE = "DisabledPolicy";
	
	/** database identifier */
	private static final String DB_ID = "pdpdb"; 
	
	/** database root <code>URI</code> */
	private static final String dbRootUri = "xmldb:" + DB_ID + ":///db/";
	
	/** active collection identifier */
	private static final String activeCollectionId = "ActivePolicies";
	
	/** inactive collection identifier */
	private static final String inActiveCollectionId = "DisabledPolicies";
	
	/** schema collection identifier */
	private static final String schemaCollectionId = "Schemas";
	
	/** property key -> value <code>HashMap</code> */
	private HashMap<String, String> propValue = null;
	
	/** policy ID -> status <code>HashMap</code> */
	private HashMap<String, String> polIdStatus = null;
	
	/** instance {@link BundleContext} */
	@SuppressWarnings("unused")
	private BundleContext context = null;
	
	/** instance {@link ExistDatabaseManager} */
	private ExistDatabaseManager database = null;
	
	/** {@link DocumentBuilder} */
	private DocumentBuilder xmlDocBuilder = null;
	
	/**
	 * Constructor
	 * 
	 * @param theLocation
	 * 				the database location
	 * @param theCtx
	 * 				the {@link BundleContext}
	 * @throws Exception
	 * 				any thrown <code>Exception</code>
	 */
	public XMLDBPdpAdminService(String theLocation, BundleContext theCtx) 
			throws Exception {
		context = theCtx;
		propValue = new HashMap<String, String>();
		polIdStatus = new HashMap<String, String>();
		// ensure policy db folders are created
		if (!PdpDbCreator.doesDbExist(theLocation)) {
			boolean result = PdpDbCreator.createPolicyDb(theLocation, theCtx);			
			if (!result) {
				throw new Exception("Failed to create PDP Database");
			}
		}
		database = new ExistDatabaseManager(theLocation, DB_ID);
		// start database
		logger.info(database.start());
		// create db collection if not present already
		database.createDbCollection(XMLDBPdpAdminService.activeCollectionId);
		database.createDbCollection(XMLDBPdpAdminService.inActiveCollectionId);
		database.createDbCollection(XMLDBPdpAdminService.schemaCollectionId);
		// instantiate document builder
		DocumentBuilderFactory factory 
				= DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);
		// we are namespace aware
		factory.setNamespaceAware(true);
		// we are not doing any validation
		factory.setValidating(false);
		try {
			xmlDocBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			logger.error("Incorrect parser configuration: " 
					+ pce.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", pce);
			}
		}
		// populate lookup map
		for (String activePolicyId : database.getDbCollection(
				XMLDBPdpAdminService.activeCollectionId).listResources()) {
			polIdStatus.put(activePolicyId, ACTIVE);
		}
		for (String inactivePolicyId : database.getDbCollection(
				XMLDBPdpAdminService.inActiveCollectionId).listResources()) {
			polIdStatus.put(inactivePolicyId, INACTIVE);
		}
		logger.info("PDP database initialised");
	}
	
	/** shuts down database */	
	public void shutdown() {		
		database.shutdown();
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#activatePolicy(java.lang.String)
	 */
	@Override
	public boolean activatePolicy(String thePolicyId) throws RemoteException {
		if (polIdStatus.containsKey(thePolicyId)) {
			if (polIdStatus.get(thePolicyId).equals(ACTIVE)) {
				return true;
			}
		}
		if(movePolicy(inActiveCollectionId, activeCollectionId, thePolicyId)) {
			polIdStatus.put(thePolicyId, ACTIVE);
			return true;
		}
		throw new RemoteException(PdpAdminError.POLICY_NOT_FOUND.toString());
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#deactivatePolicy(java.lang.String)
	 */
	@Override
	public boolean deactivatePolicy(String thePolicyId) throws RemoteException {		
		if ((polIdStatus.containsKey(thePolicyId)) 
				&& (polIdStatus.get(thePolicyId).equals(INACTIVE))) {
			return true;			
		}
		if (movePolicy(activeCollectionId, inActiveCollectionId, thePolicyId)) {
			polIdStatus.put(thePolicyId, INACTIVE);
			return true;
		}
		throw new RemoteException(PdpAdminError.POLICY_NOT_FOUND.toString());
	}	
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getPolicy(java.lang.String)
	 */
	@Override
	public String getPolicy(String thePolicyId) throws RemoteException {
		try	{
			if (polIdStatus.containsKey(thePolicyId)) {
				String status = polIdStatus.get(thePolicyId);
				Collection col = null;
				if (status.equals(ACTIVE)) {
					col = database.getDbCollection(activeCollectionId);
				}
				if (status.equals(INACTIVE)) {
					col = database.getDbCollection(inActiveCollectionId);
				}
				if (col != null) {
					Resource resource = col.getResource(thePolicyId);		        	
		        	String result = (String) resource.getContent();
		        	return result;
				}
			}
			throw new RemoteException(
					PdpAdminError.POLICY_NOT_FOUND.toString());
		} catch(XMLDBException xde) {
			logger.error("DB exception: " + xde.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", xde);
			}
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString(), xde);
		} catch (ExistIOException eie) {
			logger.error("DB exception: " + eie.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", eie);
			}
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString(), eie);
		}
	}

	/**
	 * @param thePolicyId
	 * 				the policy identifier
	 * @return
	 * 				the {@link Document}
	 * @throws RemoteException
	 * 				any {@link RemoteException} thrown by <code>getPolicy</code>
	 */
	public Document getPolicyAsDocument(String thePolicyId) 
			throws RemoteException {
    	if (xmlDocBuilder == null) {
			logger.error("DocumentBuilder not initialized, returning null");
			return null;
    	}
		String policyXml = getPolicy(thePolicyId);
		try {
			return xmlDocBuilder.parse(
					new ByteArrayInputStream(policyXml.getBytes("UTF8")));
		} catch (Exception e) {
			logger.error("Error parsing Policy to Document: " 
					+ e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getActivePolicyList()
	 */
	@Override
	public String[] getActivePolicyList() {
		return getPolicyList(true);
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getInActivePolicyList()
	 */
	@Override
	public String[] getInActivePolicyList() {
		return getPolicyList(false);
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#publishPolicy(java.lang.String, 
	 * 		java.lang.String)
	 */
	@Override
	public boolean publishPolicy(String thePolicyId, String thePolicy) 
			throws RemoteException {
		// check PolicyId map
		if (polIdStatus.containsKey(thePolicyId)) {
			logger.info("PolicyID '" + thePolicyId + "' already in DB");
			throw new RemoteException(
					PdpAdminError.POLICY_ID_ALREADY_TAKEN.toString());
		}		
		try	{			
			Collection inActiveCol =  this.database.getDbCollection(
					inActiveCollectionId);
			XMLResource res = (XMLResource) inActiveCol.createResource(
					thePolicyId, "XMLResource");
			polIdStatus.put(thePolicyId, INACTIVE);
			res.setContent(thePolicy);
			inActiveCol.storeResource(res);
			return true;
		} catch(Exception e) {
			logger.error("Exception: " + e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString(), e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#removePolicy(java.lang.String)
	 */
	@Override
	public boolean removePolicy(String policyId) throws RemoteException {
		try {
			if (polIdStatus.containsKey(policyId)) {
				String status = polIdStatus.get(policyId);
				Collection col = null;
				if (status.equals(ACTIVE)) {
					col = database.getDbCollection(activeCollectionId);
				}
				if (status.equals(INACTIVE)) {
					col = database.getDbCollection(inActiveCollectionId);
				}
				if (col != null) {
					Resource resource = col.getResource(policyId);
					col.removeResource(resource);
					polIdStatus.remove(policyId);
		        	return true;
				}
				throw new RemoteException(
						PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString());
			}
			/* we return true if a policy didn't need to be removed */
			return true;			
		} catch(XMLDBException xde) {
			logger.error("DB exception: " + xde.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", xde);
			}
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString(), xde);
		} catch(ExistIOException xio) {
			logger.error("DB exception: " + xio.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", xio);
			}
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString(), xio);
		}
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PdpAdmin#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String theId) throws RemoteException {
		if (propValue.containsKey(theId))	{
			return propValue.get(theId);
		}
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
		if (propValue.containsKey(theKey)) {
			propValue.put(theKey, theValue);
			return true;
		}
		throw new RemoteException(
				PdpAdminError.PROPERTY_NOT_SUPPORTED.toString());
	}

	/**
	 * @param theQuery
	 * 				the XPATH query
	 * @return
	 * 				the {@link ResourceSet}
	 */
	public ResourceSet queryXPath(String theQuery) {
		try	{
			return database.doXPathQuery(activeCollectionId, theQuery);
		}
		catch (Exception e) {
			logger.error("Exception: " + e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			return null;
		}
	}
	
	/**
	 * @param theSource
	 * 				the source name
	 * @param theTarget
	 * 				the target name
	 * @param thePolicyId
	 * 				the policy identifier
	 * @return
	 * 				the success indicator flag
	 *@throws RemoteException
	 *				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR
	 *					if a database exception is caught
	 */
	private boolean movePolicy(String theSource, String theTarget, 
			String thePolicyId) throws RemoteException {
		try	{
			Collection root = DatabaseManager.getCollection(
					dbRootUri, "admin", "");
			LocalCollectionManagementService mgtService 
					= (LocalCollectionManagementService) root.getService(
							"CollectionManagementService", "1.0");  
			XmldbURI src = XmldbURI.xmldbUriFor(dbRootUri + theSource + "/" 
					+ thePolicyId);
			XmldbURI trg = XmldbURI.xmldbUriFor(dbRootUri + theTarget);
			XmldbURI newname = XmldbURI.xmldbUriFor(thePolicyId);			
			mgtService.moveResource(src, trg, newname);
			return true;
		}
		catch(Exception e) {
			logger.error("Exception: " + e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			throw new RemoteException(
					PdpAdminError.PDP_ADMIN_INTERNAL_ERROR.toString(), e);	
		}
	}

	/**
	 * Helper function for returning lists of policies
	 * 
	 * @param theActive
	 * 				a flag indicating whether to return active policies
	 * @return
	 * 				the policies
	 */
	private String[] getPolicyList(boolean theActive) {
		try {
			Collection col = null;
			if (theActive) {
				col = database.getDbCollection(activeCollectionId);
			} else {
				col = database.getDbCollection(inActiveCollectionId);
			}				
			return col.listResources();
		}
		catch(Exception e) {
			logger.error("Exception: " + e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			return null;
		}
	}

}
