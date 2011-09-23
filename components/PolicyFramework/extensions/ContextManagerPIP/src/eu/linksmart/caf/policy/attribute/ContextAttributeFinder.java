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
package eu.linksmart.caf.policy.attribute;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import org.apache.log4j.Logger;

import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.ContextManager;
import eu.linksmart.caf.cm.client.ContextManagerServiceLocator;
import eu.linksmart.caf.cm.query.QueryResponse;
import eu.linksmart.caf.cm.query.QueryRow;
import eu.linksmart.network.NetworkManagerApplication;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.ctx.Status;

import eu.linksmart.policy.pdp.PolicyDecisionPoint;
import eu.linksmart.policy.pdp.ext.impl.LinkSmartEvaluationContext;
import eu.linksmart.policy.pdp.ext.impl.PipModule;

/**
 * {@link PipModule} that resolves attributes specified in XACML policies,
 * to context data from the {@link ContextManager}.
 * @author Michael Crouch
 *
 */
@SuppressWarnings("unchecked")
public class ContextAttributeFinder extends PipModule{
	
	/** Attribute ID for the resource context */
	public static final String RESOURCE_CONTEXT = "linksmart:context:resource";
	
	/** Attribute ID for the subject context */
	public static final String SUBJECT_CONTEXT = "linksmart:context:subject";
	
	/** Attribute ID for a specified (in issuer) application context */
	public static final String APPLICATION_CONTEXT = "linksmart:context:application";
	
	/** Attribute ID for the resource environment context */
	public static final String RESOURCE_ENVIRONMENT_CONTEXT 
			= "linksmart:context:environment:resource";
	
	/** Attribute ID for the subject environment context */
	public static final String SUBJECT_ENVIRONMENT_CONTEXT 
			= "linksmart:context:environment:subject";
	
	/** Attribute ID for the resource location context */
	public static final String RESOURCE_LOCATION_CONTEXT 
			= "linksmart:context:location:resource";
	
	/** Attribute ID for the subject location context */
	public static final String SUBJECT_LOCATION_CONTEXT 
			= "linksmart:context:location:subject";
	
	/** the {@link Logger} */
	private static final Logger logger 
			= Logger.getLogger(ContextAttributeFinder.class);
	
	/** the PIP identifier */
	private static final String PIP_IDENTIFIER 
			= "ContextManagerPIP_AttributeFinder";
	
	/** Named query for retrieving an Application context by context "name" */
	private static final String GET_APP_CONTEXT_NAME 
			= "ContextManager.CoreQuery.GetApplicationByName";
	
	/** Named query for retrieving a context by hid */
	private static final String GET_CONTEXT_HID 
			= "ContextManager.CoreQuery.GetContextByHID";
	
	/** Named query for retrieving the environment of the given entity by HID */
	private static final String GET_ENVIRONMENT_QUERY 
			= "ContextManager.CoreQuery.GetEnvironmentOfContextWithPID";
	
	/** Named query for retrieving the location of the given entity by HID */
	private static final String GET_LOCATION_QUERY 
			= "ContextManager.CoreQuery.GetLocationOfContextWithPID";
	
	/** the id of the Subject HID attribute */
	private static final String SUBJECT_HID 
			= "urn:oasis:names:tc:xacml:1.0:subject:subject-id";
	
	/** the id of the Resource HID attribute */
	private static final String RESOURCE_HID 
			= "urn:oasis:names:tc:xacml:1.0:resource:resource-id";
		
	/** the {@link Set} of supported designators */
	private static Set supportedDesignators;
	
	/** the {@link Set} of supported atrtibuteIds */
	private static Set supportedAttributeIds;
	
	/** the URI for the String type */
	private static URI stringUri;
	
	/** the {@link NetworkManagerApplication} */
	private NetworkManagerApplication nm;
		
	/** the {@link BundleContext} */
	private BundleContext bundleCtx;
	
	/** the http port */
	private String httpPort;	
	
	static{
		supportedDesignators = new HashSet();
		supportedDesignators.add(
				new Integer(AttributeDesignator.SUBJECT_TARGET));
		supportedDesignators.add(
				new Integer(AttributeDesignator.RESOURCE_TARGET));
		supportedDesignators.add(
				new Integer(AttributeDesignator.ENVIRONMENT_TARGET));

		supportedAttributeIds = new HashSet();

		try {
			supportedAttributeIds.add(new URI(RESOURCE_CONTEXT));
			supportedAttributeIds.add(new URI(RESOURCE_ENVIRONMENT_CONTEXT));
			supportedAttributeIds.add(new URI(RESOURCE_LOCATION_CONTEXT));
			supportedAttributeIds.add(new URI(SUBJECT_CONTEXT));
			supportedAttributeIds.add(new URI(SUBJECT_ENVIRONMENT_CONTEXT));
			supportedAttributeIds.add(new URI(SUBJECT_LOCATION_CONTEXT));
			supportedAttributeIds.add(new URI(APPLICATION_CONTEXT));
			
			stringUri = URI.create(StringAttribute.identifier);
			
		} catch (URISyntaxException se) {
			logger.error("Exception when adding AttributeDesignator types", se);
		}
		
	}
	
	/**
	 * OSGi Component activator
	 * @param context the {@link ComponentContext}
	 */
	protected void activate(final ComponentContext context){	
		if (nm == null)
			nm = (NetworkManagerApplication) context.locateService(
					"NetworkManager");
			
		this.bundleCtx = context.getBundleContext();
		httpPort = System.getProperty("org.osgi.service.http.port");
		if (httpPort == null)
			httpPort = "8082";
		
		logger.info("ContextPIP Initialised");
	}
	
	/**
	 * OSGi Component deactivator
	 * @param context the {@link ComponentContext}
	 */
	protected void deactivate(ComponentContext context){
		//do nothing
	}
	

	@Override
	public String getIdentifier() {
		return PIP_IDENTIFIER;
	}

	@Override
	public boolean isDesignatorSupported() {
		return true;
	}
	
	@Override
	public Set getSupportedDesignatorTypes() {
		return supportedDesignators;
	}

	@Override
	public Set getSupportedIds() {
		return supportedAttributeIds;
	}

	@Override
	public EvaluationResult findAttribute(URI attributeType, URI attributeId,
			URI issuer, URI subjectCategory, EvaluationCtx context,
			int designatorType) {
	
		if (!supportedDesignators.contains(designatorType))
            return new EvaluationResult(BagAttribute.
                    createEmptyBag(attributeType));
		
		ContextManager cm = getLocalContextManager();
		String contextId = "";
		
		//get context HID using issuer as PID
		if (issuer != null)	{
			contextId = issuer.toString();
		}
//		else {
//			/** 
//			 * Parse Issuer
//			 * 
//			 * Extracts the CM PID, and the contextId, if specified
//			 */
//			String[] issuerStr = issuer.toString().split("|");
//			String cmPID;
//			if (issuerStr.length > 1) {
//				contextId = issuerStr[issuerStr.length-1];
//				cmPID = issuerStr[0];
//				if (!"".equals(cmPID)) {
//					cm = getRemoteContextManagerWithPID(cmPID);
//				} else {
//					cm = getLocalContextManager();
//				}
//			} else {
//				cmPID = issuer.toString();
//				contextId = "";
//				cm = getRemoteContextManagerWithPID(cmPID);
//			}								
//			if (cm == null)	{
//				return getError(Status.STATUS_PROCESSING_ERROR, 
//						"No Context Manager");
//			}
//		}
		
		//get HID of defined context resource / subject
		String hid = extractHID(context, designatorType);
		
		String content = getContext(hid, contextId, attributeId, cm);
		if (content == null){
			return getError(Status.STATUS_MISSING_ATTRIBUTE, "Context not found");
		}
		
		StringAttribute contentAttribute = new StringAttribute(content);
		Set attrSet = new HashSet();
		attrSet.add(contentAttribute);
		BagAttribute returnBag = new BagAttribute(stringUri, attrSet);
		
		//add content as attribute to EvaluationCtx
		if (context instanceof LinkSmartEvaluationContext) {			
			LinkSmartEvaluationContext linksmartEvalCtx 
					= (LinkSmartEvaluationContext) context;
			if (designatorType == AttributeDesignator.SUBJECT_TARGET) {
				linksmartEvalCtx.addSubjectAttribute(returnBag, attributeId);
			} else if (designatorType == AttributeDesignator.RESOURCE_TARGET) {
				linksmartEvalCtx.addResourceAttribute(returnBag, attributeId);
			}
		}	
		return new EvaluationResult(returnBag);
	}
	
	/**
	 * Extracts the HID of the Subject / Resource in question 
	 * @param context the {@link EvaluationCtx}
	 * @param designatorType the designator type
	 * @return the extracted HID
	 */
	private String extractHID(EvaluationCtx context, int designatorType){
		
		EvaluationResult result = new EvaluationResult(
				BagAttribute.createEmptyBag(stringUri));
		
		//if subject
		if (designatorType == AttributeDesignator.SUBJECT_TARGET){
			result = context.getSubjectAttribute(stringUri, 
					URI.create(SUBJECT_HID), null);
		}
		//if resource
		else if (designatorType == AttributeDesignator.RESOURCE_TARGET)	{
			result = context.getResourceAttribute(stringUri, 
					URI.create(RESOURCE_HID), null);
		}
		
		BagAttribute bag = (BagAttribute) result.getAttributeValue();
		if (bag.size() != 1) {
            return null;
        }
		StringAttribute pidAttr = (StringAttribute) bag.iterator().next();
		return pidAttr.getValue();
	}
	
	/** 
	 * Gets the Local {@link ContextManager} from the OSGi framework.  
	 * @return the local {@link ContextManager}
	 */
	private ContextManager getLocalContextManager(){
		ServiceReference ref = bundleCtx.getServiceReference(
				ContextManager.class.getName());
		if (ref != null){
			return (ContextManager) bundleCtx.getService(ref);
		}
		return null;
	}
	
	/**
	 * Gets the remote {@link ContextManager} with the given PID, by searching 
	 * for it through the {@link NetworkManagerApplication}.
	 * @param pid the {@link ContextManager} PID
	 * @return the {@link ContextManager}
	 */
	private ContextManager getRemoteContextManagerWithPID(String pid){
		try {
			
			String query = "((PID==" + pid + ")&&(SID==eu.linksmart.caf.cm))";
			String[] results = nm.getHIDByAttributes("0", "ContextManagerPIP", 
					query, 1000, 1);
			if (results.length == 0)
				return null;
			return getRemoteContextManager(results[0]);
		} catch (RemoteException e) {
			logger.error("Could not find ContextManager with PID '" + pid + "'");
			return null;
		}
	}
	
	/**
	 * Gets the remove {@link ContextManager} with the given HID
	 * @param cmHid the HID
	 * @return the {@link ContextManager}
	 */
	private ContextManager getRemoteContextManager(String cmHid){
		String pdpHid = "0";
		ServiceReference[] refs;
		try {
			refs = bundleCtx.getAllServiceReferences(
					PolicyDecisionPoint.class.getName(), null);
			
			for (ServiceReference ref : refs){
				String hid = (String) ref.getProperty("HID");
				if (hid != null && !"".equals(hid)){
					pdpHid = hid;
					break;
				}
			}
		} catch (InvalidSyntaxException e1) {
			logger.error("Exception getting PDP ServiceReferences:" 
					+ e1.getLocalizedMessage(), e1);
		}
		String url = "http://localhost:" + httpPort + "/SOAPTunneling/" 
				+ pdpHid + "/" + cmHid + "/0/hola";
		
		ContextManagerServiceLocator locator 
				= new ContextManagerServiceLocator();
		locator.setContextManagerEndpointAddress(url);
		try {
			return locator.getContextManager();
		} catch (ServiceException e) {
			logger.error("ServiceException getting ContextManager client", e);
			return null;
		}
		
	}
	
	/**
	 * Gets the required encoded context from the {@link ContextManager}
	 * @param hid the hid of the context to get
	 * @param name the name of the context
	 * @param attributeIdUri the attributeId to resovle
	 * @param cm the {@link ContextManager}
	 * @return the encoded (as XML) result
	 */
	private String getContext(String hid, String name, URI attributeIdUri, 
			ContextManager cm) {
		QueryResponse resp = null;
		String query = null;
		String attributeId = attributeIdUri.toString();
		Parameter[] params;
		if (attributeId.equals(APPLICATION_CONTEXT)){
			query = GET_APP_CONTEXT_NAME;
			if ("".equals(name))
				logger.error("No application name provided in Issuer");
			params = new Parameter[1];
			params[0] = new Parameter("theName", "string", name);
		} else if (attributeId.equals(RESOURCE_CONTEXT)||attributeId.equals(
				SUBJECT_CONTEXT)) {
			query = GET_CONTEXT_HID;
			params = new Parameter[1];
			params[0] = new Parameter("theHid", "string", hid);
		} else if (attributeId.equals(RESOURCE_ENVIRONMENT_CONTEXT)
				|| attributeId.equals(SUBJECT_ENVIRONMENT_CONTEXT))	{
			query = GET_ENVIRONMENT_QUERY;
			params = new Parameter[1];
			params[0] = new Parameter("theHid", "string", hid);
		} else if (attributeId.equals(RESOURCE_LOCATION_CONTEXT)
				|| attributeId.equals(SUBJECT_LOCATION_CONTEXT)) {
			query = GET_LOCATION_QUERY;
			params = new Parameter[1];
			params[0] = new Parameter("theHid", "string", hid);
		} else {
			params = new Parameter[0];
		}
		
		try {
			resp = cm.executeNamedQuery(query, params);	
		} catch (RemoteException e) {
			logger.error("RemoteException", e);
		}
		
		if (resp == null) {
			return null;
		}
		
		for (QueryRow row : resp.getResults())	{
			if (row.getRowId().equals("result")) {
				return row.getResultContent();
			}
		}
		return null;
	}

	/**
	 * Generates the error {@link EvaluationResult}
	 * @param statusCode the status code
	 * @param message the message
	 * @return the {@link EvaluationResult}
	 */
	private static EvaluationResult getError(String statusCode, String message) {
		ArrayList code = new ArrayList();
        code.add(statusCode);
        Status status = new Status(code, message);
        return new EvaluationResult(status);
	}
	
}
