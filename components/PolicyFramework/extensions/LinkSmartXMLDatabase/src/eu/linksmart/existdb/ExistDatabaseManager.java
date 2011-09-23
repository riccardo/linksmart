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
package eu.linksmart.existdb;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.OutputKeys;

import org.apache.log4j.Logger;
import org.exist.storage.io.ExistIOException;
import org.exist.xmldb.DatabaseInstanceManager;
import org.exist.xmldb.XQueryService;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XPathQueryService;

/**
 * eXist DB OSGi bundle wrapper
 * 
 * Exposes some functionalities of the eXist XML DB via OSGi.
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 *
 */
public class ExistDatabaseManager {
	
	/** logger */
	private static final Logger logger 
			= Logger.getLogger(ExistDatabaseManager.class);
	
	/** location of database */
	private String dbLocation;
	
	/** database ID */
	private String dbId;
	
	/** flag indicating whether the database instance has been started */
	private boolean started;
	
	/**
	 * Constructor
	 *
	 * @param theDbLocation
	 * 				the database location
	 * @param theDbId
	 * 				the database ID
	 * @throws IOException
	 * 				any {@link IOException} that is thrown by eXist
	 */
	public ExistDatabaseManager(String theDbLocation, String theDbId) 
			throws IOException {
		super();
		started = false;
		dbLocation = theDbLocation;
		dbId = theDbId;
		if (!dbExistAtLoc(dbLocation))
			throw new IOException("Database does not exist at given location. " 
					+ "Must be initialised.");
	}
	
	/**
	 * Starts the database
	 * 
	 * @return
	 * 				success indicator flag
	 * @throws XMLDBException 
	 * 				any {@link XMLDBException} that is thrown by eXist
	 * @throws ClassNotFoundException 
	 * 				if driver class has not been found
	 * @throws IllegalAccessException
	 * 			 	if access problems occur
	 * @throws InstantiationException
	 * 				any {@link InstantionException} that are thrown 				 
	 */
	@SuppressWarnings("unchecked")
	public boolean start() throws XMLDBException, ClassNotFoundException, 
			InstantiationException, IllegalAccessException{
		if (started) {
			return started;
		}
		Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");  
		Database database = (Database) cl.newInstance();  
		database.setProperty("create-database", "true");  
		database.setProperty("configuration", dbLocation + "/conf.xml");  
		database.setProperty("database-id", dbId);  
		DatabaseManager.registerDatabase(database);		
		started = true;
		return started;
	}
	
	/**
	 * Shuts down the database
	 * 
	 * @return
	 * 					success indicator flag
	 */
	public boolean shutdown(){
		if (!started) {
			return true;
		}		
		try {
			Collection root = DatabaseManager.getCollection(
					"xmldb:" + dbId + ":///db/", "admin", "");
			DatabaseInstanceManager manager = (DatabaseInstanceManager) 
					root.getService("DatabaseInstanceManager", "1.0");   
			manager.shutdown();
			return true;
		}
		catch(Exception e) {
			logger.error("Error shutting down - " + e.getLocalizedMessage());
			return false;
		}
	}
	
	/**
	 * Creates the named Collection, if it doesn't already exist
	 * 
	 * @param colName
	 * 				name of collection
	 * @return
	 * 				success indicator flag
	 * @throws ExistIOException 
	 * 				any {@link ExistIOException} that is being thrown
	 */
	public boolean createDbCollection(String colName) throws ExistIOException {
		if (!started) {
			throw new ExistIOException("ExistDatabaseManager: DB '" + dbId 
					+ "' not started");
		}		
		Collection col = null;		
    	try {
    		// get the collection  
    		col = DatabaseManager.getCollection("xmldb:" + dbId + ":///db/" 
    				+ colName, "admin", "");   
    		if (col == null) {  
    	    	Collection root = DatabaseManager.getCollection(
    	    			"xmldb:" + dbId + ":///db/", "admin", ""); 
    			CollectionManagementService mgtService = 
    				(CollectionManagementService) 
    				root.getService("CollectionManagementService", "1.0");  
    			col = mgtService.createCollection("/db/" + colName);  	
    	    }  
    		col.setProperty(OutputKeys.INDENT, "yes");
    	    return true;
    	}
    	catch (Exception e) {
			logger.error("Error connecting to XMLDB");
			logger.debug("Stack trace: ", e);
			return false;
		}
	}
	
	/**
	 * Returns the appropriate collection from the DB. If it doesn't exist, 
	 * NULL is returned
	 * 
	 * @param colName
	 * 				the collection name
	 * @return
	 * 				the {@link Collection}
	 * @throws ExistIOException 
	 * 				any {@link ExistIOException} that is thrown
	 */
	public Collection getDbCollection(String colName) 
			throws ExistIOException {		
		if (!started) {
			throw new ExistIOException("ExistDatabaseManager: DB '" + dbId 
					+ "' not started");
		}		
		Collection col = null;		
    	try {
    		// get the collection  
    		col = DatabaseManager.getCollection("xmldb:" + dbId + ":///db/" 
    				+ colName, "admin", "");   
    	    return col;
    	}
    	catch (Exception e) {
			logger.error("Error connecting to XMLDB");
			logger.debug("Stack trace: ", e);
			return null;
		}
	}

	/**
	 * Returns whether the DB exists at the location
	 * 
	 * @param dbLoc
	 * 				database location
	 * @return
	 * 				true if a DB exists at argument location
	 */
	public static boolean dbExistAtLoc(String dbLoc) {
		if (!"".equals(dbLoc)) {
			//check if "conf.xml" exists at location
			File existDbHome = new File(dbLoc);			
			if (!existDbHome.exists()) {
				return false;
			}			
			if (existDbHome.isDirectory()) {
				File[] files = existDbHome.listFiles();
				for(int i=0; i<files.length; i++) {
					File file = files[i];
					if (file.getName().equals("conf.xml")) {
						return true;
					}
				}
			}
		}	
		return false;
	}
	
	/**
	 * Performs an XPath query on the given Collection
	 * 
	 * @param colName
	 * 				the {@link Collection} to query
	 * @param query
	 * 				the query to execute
	 * @return
	 * 				the result {@link ResourceSet}
	 * @throws XMLDBException 
	 * 				any {@link XMLDBException} that is being thrown
	 * @throws ExistIOException 
	 * 				any {@link ExistIOException} that is being thrown
	 */
	public ResourceSet doXPathQuery(String colName, String query) 
			throws XMLDBException, ExistIOException	{
		if (!started) {
			throw new ExistIOException("ExistDatabaseManager: DB '" + dbId 
					+ "' not started");
		}		
		XPathQueryService service = (XPathQueryService) 
				getDbCollection(colName).getService("XPathQueryService", 
						"1.0");
		service.setProperty("indent", "yes");
		return service.query(query);
	}
	
	/**
	 * Compiles and runs an XQuery on the given collection
	 * 
	 * @param colName
	 * 				the {@link Collection} to query
	 * @param xquery
	 * 				the XPATH query to execute
	 * @return
	 * 				the result {@link ResourceSet}
	 * @throws XMLDBException 
	 * 				any {@link XMLDBException} that is being thrown
	 * @throws ExistIOException 
	 * 				any {@link ExistIOException} that is being thrown
	 */
	public ResourceSet runXQuery(String colName, String xquery) 
			throws XMLDBException, ExistIOException	{
		if (!started) {
			throw new ExistIOException("ExistDatabaseManager: DB '" + dbId 
					+ "' not started");
		}
		XQueryService service = (XQueryService) 
				getDbCollection(colName).getService("XQueryService", "1.0");
		service.setProperty("indent", "yes");
		CompiledExpression comexp = service.compile(xquery);
		return service.execute(comexp);
	}
	
}
