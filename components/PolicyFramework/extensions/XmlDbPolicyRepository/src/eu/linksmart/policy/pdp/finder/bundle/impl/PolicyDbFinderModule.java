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
package eu.linksmart.policy.pdp.finder.bundle.impl;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.MatchResult;
import com.sun.xacml.Policy;
import com.sun.xacml.PolicySet;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.PolicyFinderResult;

import eu.linksmart.policy.pdp.admin.bundle.impl.XMLDBPdpAdminService;
import eu.linksmart.policy.pdp.impl.PdpXacmlConstants;

/**
 * <p>PolicyDbFinder for retrieving Policies from an XMLDB</p>
 * 
 * <p>Default behavior is to match all Policies stored against the request.</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 */
public class PolicyDbFinderModule extends PolicyFinderModule  {   
    
	/** resource ID */
    public static final String RESOURCE_ID = EvaluationCtx.RESOURCE_ID;
    
    /** resource type ID */
    public static final String RESOURCE_ID_TYPE = StringAttribute.identifier; 

	/** logger */
	private static final Logger logger 
			= Logger.getLogger(PolicyDbFinderModule.class);
    
    /** PID <code>URI</code> */
    private URI pidURI = null;
    
    /** resource type <code>URI</code> */
    private URI resourceType = null;    
    
	/** {@link PolicyFinder} */
	private PolicyFinder finder = null;
    
	/** {@link XMLDBPdpAdminService} */
    private XMLDBPdpAdminService dbAdmin = null;
    
    /**
     * Constructor
     * 
     * @param theDbAdmin
     * 				the {@link XMLDBPdpAdminService}
     */
    public PolicyDbFinderModule(XMLDBPdpAdminService theDbAdmin) {
    	dbAdmin = theDbAdmin;
    	resourceType = URI.create(RESOURCE_ID_TYPE);
    	pidURI = URI.create(PdpXacmlConstants.RESOURCE_RESOURCE_PID.getUrn());
    }
    
    /* (non-Javadoc)
     * @see com.sun.xacml.finder.PolicyFinderModule#init(
     * 		com.sun.xacml.finder.PolicyFinder)
     */
    @Override
	public void init(PolicyFinder theFinder) {
        finder = theFinder;
    }
    
    /* (non-Javadoc)
     * @see com.sun.xacml.finder.PolicyFinderModule#getIdentifier()
     */
    @Override
    public String getIdentifier() {
        return getClass().getPackage().getName() 
        		+ "." + getClass().getSimpleName();
    }    
   
    /* (non-Javadoc)
     * @see com.sun.xacml.finder.PolicyFinderModule#isRequestSupported()
     */
    @Override
    public boolean isRequestSupported() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.sun.xacml.finder.PolicyFinderModule#findPolicy(
     * 		com.sun.xacml.EvaluationCtx)
     */
    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx theCtx) {
    	try{
    		return findDbQuery(theCtx);
    	}
    	catch(Exception e) {
    		logger.error("Exception while retrieving policy from database: " 
    				+ e.getLocalizedMessage());
    		if (logger.isDebugEnabled()) {
    			logger.debug("Stack trace: ", e);
    		}
    		// do not fail hard
    		return new PolicyFinderResult();
    	} 
    }

    /**
	 * Loads a policy returned from a database
	 * 
	 * @param theResource
	 * 				the {@link Resource}, which must be an {@link XMLResource}
	 * @param theFinder
	 * 				the {@link PolicyFinder}
	 * @return
	 * 				the {@link AbstractPolicy}
	 */
	protected AbstractPolicy loadPolicy(Resource theResource, 
			PolicyFinder theFinder) {
        try {
        	if ((theResource == null) || (theResource.getResourceType() == null) 
        			|| (!theResource.getResourceType().equals(
        			XMLResource.RESOURCE_TYPE))) {
        		return null;
        	}
        	DocumentBuilderFactory factory 
        			= DocumentBuilderFactory.newInstance();
        	factory.setIgnoringComments(true);
        	// as of 1.2, we always are namespace aware
        	factory.setNamespaceAware(true);
        	// we're not doing any validation
        	factory.setValidating(false);
        	DocumentBuilder db = factory.newDocumentBuilder();   
        	Document doc = db.parse(new ByteArrayInputStream(
        					((String) theResource.getContent()).getBytes()));
        	// handle the policy if it is a known type
        	Element root = doc.getDocumentElement();
        	String name = root.getTagName();
        	if ("Policy".equals(name)) {
        		return Policy.getInstance(root);
        	} 
        	if ("PolicySet".equals(name)) {
        		return PolicySet.getInstance(root, theFinder);
        	}
        	// this is not one of the root types we can handle
        	logger.error("Cannot handle root type: " + name);
        	return null;
        } catch(Exception e) {
        	logger.error("Exception while loading policy: " 
        			+ e.getLocalizedMessage());
        	if (logger.isDebugEnabled()) {
        		logger.debug("Stack trace: ", e);
        	}
        	return null;
        }
    }    
    
    /**
     * @param theCtx
     * 				the {@link EvaluationCtx}
     * @return
     * 				the {@link PolicyFinderResult}
     */
    private PolicyFinderResult findDbQuery(EvaluationCtx theCtx) {
    	AbstractPolicy selectedPolicy = null;
    	try	{
    		// get the expected PID attribute
    		EvaluationResult resourceAttr = theCtx.getResourceAttribute(
    				resourceType, pidURI, null);    		    		
    		BagAttribute bag = (BagAttribute) resourceAttr.getAttributeValue();
    		if ((bag == null) || (!bag.iterator().hasNext())) {
    			return findMatchAll(theCtx);
    		}
    		String resourceVal 
    				= ((AttributeValue) bag.iterator().next()).encode();    		    		    		
    		// guery DB for match with policies
    		String query = "root(//Policy//ResourceAttributeDesignator[" 
    				+ "@AttributeId=\"" 
    				+ PdpXacmlConstants.RESOURCE_RESOURCE_PID
    						.getUrn() 
    				+ "\"]/../AttributeValue[text()=\"" 
    				+ resourceVal 
    				+ "\"])";  
    		// iterate through results and match against request
    		ResourceSet resSet = dbAdmin.queryXPath(query);
    		if ((resSet == null) || (resSet.getSize() == 0L)) {
    			return findMatchAll(theCtx);
    		}    		
    		ResourceIterator it = resSet.getIterator();
    		while (it.hasMoreResources()) {
    			Resource res = it.nextResource();
    			AbstractPolicy abPolicy = loadPolicy(res, finder);
    			if (abPolicy != null) {
    				// see whether we match
    				MatchResult match = abPolicy.match(theCtx);
    				int result = match.getResult();
    				// if there was an error, we stop right away
    				if (result == MatchResult.INDETERMINATE) {
    					return new PolicyFinderResult(match.getStatus());
    				}
    				if (result == MatchResult.MATCH) {
    					// if we matched before, this is an error
    					if (selectedPolicy != null) {
    						ArrayList<String> code = new ArrayList<String>();
    						code.add(Status.STATUS_PROCESSING_ERROR);
    						Status status = new Status(code, 
    								"too many applicable top-level policies");
    						return new PolicyFinderResult(status);
    					}
    					// otherwise remember this policy
    					selectedPolicy = abPolicy;
    				}
    			}
    		}
    		if (selectedPolicy != null) {
    			return new PolicyFinderResult(selectedPolicy);
    		}
    	}
    	catch (Exception e) {
    		logger.error("Exception when querying DB for applicable policies: "
    				+ e.getLocalizedMessage());
    		if (logger.isDebugEnabled()) {
    			logger.debug("Stack trace: ", e);
    		}
    	}
    	return findMatchAll(theCtx);
    }
    
    /**
     * @param theCtx
     * 				the {@link EvaluationCtx}
     * @return
     * 				the {@link PolicyFinderResult}
     */    
    private PolicyFinderResult findMatchAll(EvaluationCtx theCtx) {
    	AbstractPolicy selectedPolicy = null;
    	try	{
    		String[] resSet = dbAdmin.getActivePolicyList();
    		int rsl = resSet.length;
    		for (int i=0; i < rsl; i++)	{
    			Document doc = dbAdmin.getPolicyAsDocument(resSet[i]);
    			if (doc != null) {
    				Element root = doc.getDocumentElement();
    				String name = root.getTagName();
    				AbstractPolicy abPolicy = null;
    				if ("Policy".equals(name)) {
    					abPolicy = Policy.getInstance(root);
    				} else if ("PolicySet".equals(name)) {
    					abPolicy = PolicySet.getInstance(root, finder);
    				} 
    				if (abPolicy != null) {
    					// see if we match
    					MatchResult match = abPolicy.match(theCtx);
    					int result = match.getResult();
    					// if there was an error, we stop right away
    					if (result == MatchResult.INDETERMINATE) {
    						return new PolicyFinderResult(match.getStatus());
    					}
    					if (result == MatchResult.MATCH) {
    						// if we matched before, this is an error
    						if (selectedPolicy != null) {
    							ArrayList<String> code = new ArrayList<String>();
    							code.add(Status.STATUS_PROCESSING_ERROR);
    							Status status = new Status(code, 
    									"too many applicable top-level policies");
    							return new PolicyFinderResult(status);
    						}
    						// otherwise, remember this policy
    						selectedPolicy = abPolicy;
    					}
    				}
    			}
    		}
    		if (selectedPolicy != null) {
                return new PolicyFinderResult(selectedPolicy);
    		}   
    	} catch (Exception e) {
    		logger.error("Exception while iterating through policies: " 
    				+ e.getLocalizedMessage());
    		if (logger.isDebugEnabled()) {
    			logger.debug("Stack trace: ", e);
    		}
    	}
        return new PolicyFinderResult(); 
    }
    
}
