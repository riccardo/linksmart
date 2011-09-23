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
package eu.linksmart.caf.cm;

import java.rmi.RemoteException;

import eu.linksmart.dac.DacCallbackInterface;

import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.query.ContextQuery;
import eu.linksmart.caf.cm.query.QueryResponse;
import eu.linksmart.caf.cm.query.QuerySet;
import eu.linksmart.caf.cm.rules.ContextRuleSet;
import eu.linksmart.caf.cm.specification.ContextSpecification;
import eu.linksmart.caf.daqc.report.DaqcReportingService;
import eu.linksmart.caf.daqc.subscription.Subscription;
import eu.linksmart.eventmanager.EventSubscriber;

/**
 * The interface for the Context Manager. <p>
 * The Context Manager allows for pluggable rules, included in {@link ContextSpecification}s, 
 * that perform the interpretation of data from data sources, providing situational awareness,
 * with associated context-sensitive actions to be performed. <p>
 * In addition, it provides mechanisms for the provisioning of contextual information, through
 * installable queries ({@link QuerySet}) - to accompany the default queries.<p>
 * This interfaces defines the methods for the creation / installation and management of {@link ContextSpecification}s 
 * and {@link QuerySet}s, and also the methods for querying the Context Manager.
 * @author Michael Crouch
 *
 */
public interface ContextManager extends DaqcReportingService, EventSubscriber, DacCallbackInterface, java.rmi.Remote {
	
    /**
     * Installs the Application context defined by the {@link ContextSpecification} in the Context Manager. 
     * The associated {@link ContextRuleSet} is inserted into the Rule Engine, along with the stubs of 
     * the context itself. The context is associated with the given HID.<p>
     * Any additional data required by the Context are 
     * processed.<p>
     * Returns an error if the application already exists.<p>
     * Returns a {@link ContextResponse} containing the success of the operation, including
     * any errors - as {@link ContextManagerError}s.
     * @param specification the {@link ContextSpecification}
     * @param hid the HID of the installed application
     * @return the {@link ContextResponse}
     * @throws java.rmi.RemoteException
     */
    public eu.linksmart.caf.cm.ContextResponse installApplicationContext(eu.linksmart.caf.cm.specification.ContextSpecification specification, String hid) throws java.rmi.RemoteException;
    
    /**
     * Installs the Application Context, defined by the {@link ContextSpecification} 
     * provided encoded as String XML.<p>
     * The associated {@link ContextRuleSet} is inserted into the Rule Engine, along with the stubs of 
     * the context itself. The context is associated with the given HID.<p>
     * Any additional data required by the Context are 
     * processed.<p>
     * Returns the contextId of the newly created context, or throws an exception 
     * with the processing errors
     * @param specification the {@link ContextSpecification} as Xml
     * @param hid the HID of the application
     * @return the contextId of the created context
     * @throws java.rmi.RemoteException any errors processing the {@link ContextSpecification}
     */
    public String installApplicationContextAsXml(String specification, String hid) throws java.rmi.RemoteException;

    /**
     * Removes the Application {@link ContextSpecification} with the given contextId
     * @param contextId the contextId to remove
     * @throws java.rmi.RemoteException
     */
    public void removeContextSpecification(java.lang.String contextId) throws java.rmi.RemoteException;
    
    
    /**
     * Returns an XML encoded representation of the context with
     * the given contextId
     * @param contextId the contextId
     * @return the XML-encoded current context
     */
    public String getCurrentContext(String contextId) throws java.rmi.RemoteException;;
    
    /**
     * Updates the value of the specified context Member (with the given key), in the context
     * with the given contextId, with the given value.
     * @param applicationContextId contextId of the Application context
     * @param memberKey key of the member to update
     * @param value value to update with
     */
    public void setApplicationContextMember(String applicationContextId, String memberKey, String value) throws java.rmi.RemoteException;;
            
    /**
     * Gets all {@link ContextSpecification}s with the contextIds given
     * @param ids array of contextIds
     * @return the array of matching {@link ContextSpecification}s
     * @throws java.rmi.RemoteException
     */
    public eu.linksmart.caf.cm.specification.ContextSpecification[] getContextSpecifications(java.lang.String[] ids) throws java.rmi.RemoteException;
    
    /**
     * Gets all contextIds for installed Application {@link ContextSpecification}s
     * @return array of contextIds
     * @throws java.rmi.RemoteException
     */
    public java.lang.String[] getApplicationContextList() throws java.rmi.RemoteException;
   
    /** 
     * Gets all contextIds that match the given query. The query should assign the query results to
     * the variable <code>output</code>.<p>
     * Example query: <p>
     * 		<code>output : Device( deviceType == "Thermometer" )</code>
     * @param query
     * @return
     * @throws java.rmi.RemoteException
     */
    public java.lang.String[] getContextIdsMatchingQuery(String query) throws java.rmi.RemoteException;
    
    /**
     * Gets the {@link ContextSpecification} with the given contextId
     * @param contextId the contextId
     * @return the matching {@link ContextSpecification}
     * @throws java.rmi.RemoteException
     */
    public eu.linksmart.caf.cm.specification.ContextSpecification getContextSpecification(java.lang.String contextId) throws java.rmi.RemoteException;
    
    /**
     * Executes the query with the given name, passing the given {@link Parameter}s as arguments. 
     * Query results are returned in a {@link QueryResponse}.<p>
     * If no arguments to the query, pass null or an empty {@link Parameter} array.
     * @param queryName the name of the query
     * @param args array of {@link Parameter}s as arguments
     * @return the query results, as {@link QueryResponse}
     * @throws java.rmi.RemoteException
     */
    public eu.linksmart.caf.cm.query.QueryResponse executeNamedQuery(java.lang.String queryName, eu.linksmart.caf.Parameter[] args) throws java.rmi.RemoteException;
    
    /**
     * Executes the argument-less {@link ContextQuery} passed, and returns the results in a
     * {@link QueryResponse}. The query should assign the query results to
     * the variable <code>output</code>.
     * @param query the {@link ContextQuery}
     * @return the {@link QueryResponse}
     * @throws java.rmi.RemoteException
     */
    public eu.linksmart.caf.cm.query.QueryResponse executeSingleQuery(eu.linksmart.caf.cm.query.ContextQuery query) throws java.rmi.RemoteException;
    
    /**
     * Executes the DRL-formatted query, given as a String. The result is encoded
     * as XML, with wrapper tags as follows:<p> 
     * <code>&lt;output&gt;Encoded Output&lt;/output&gt;</code><p>
     * The output of the query must be assigned to the variable 
     * "<code>output</code>"
     * @param query the DRL query
     * @return the encoded results
     * @throws RemoteException Thrown if there are errors with the query etc
     */
    public String executeQuery(String query) throws RemoteException;
    
    /**
     * Gets the {@link QuerySet} with the package name given
     * @param pkgName the package name of the {@link QuerySet} to get
     * @return the matching {@link QuerySet}
     * @throws java.rmi.RemoteException
     */
    public eu.linksmart.caf.cm.query.QuerySet getQuerySet(java.lang.String pkgName) throws java.rmi.RemoteException;
    
    /**
     * Returns an array of all installed query packages
     * @return the array of query package names
     * @throws java.rmi.RemoteException
     */
    public java.lang.String[] getQuerySetPackages() throws java.rmi.RemoteException;
   
    /**
     * Installs the given {@link QuerySet} to the Context Manager. Any errors processing the 
     * {@link QuerySet} are returned in the {@link ContextResponse}.
     * @param querySet the {@link QuerySet} to install
     * @return the {@link ContextResponse}
     * @throws java.rmi.RemoteException
     */
    public eu.linksmart.caf.cm.ContextResponse installQuerySet(eu.linksmart.caf.cm.query.QuerySet querySet) throws java.rmi.RemoteException;
    
     /**
     * Remove the {@link QuerySet} with the given package name
     * @param pkgName the package name of the {@link QuerySet} to remove
     * @return the {@link ContextResponse}
     * @throws java.rmi.RemoteException
     */
    public eu.linksmart.caf.cm.ContextResponse removeQuerySet(java.lang.String pkgName) throws java.rmi.RemoteException;
   
  
}
