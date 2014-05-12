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
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.w3c.dom.Node;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.ctx.xacml3.Result;
import org.wso2.balana.xacml3.Attributes;

import eu.linksmart.network.Message;
import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.policy.pdp.PolicyDecisionPoint;
import eu.linksmart.policy.pep.PepResponse;
import eu.linksmart.policy.pep.PepService;
import eu.linksmart.policy.pep.cache.impl.PdpSessionCache;
import eu.linksmart.policy.pep.request.SoapAttrParser;
import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.request.impl.StaxSoapAttrParser;
import eu.linksmart.utils.Part;

/**
 * <p>Default {@link PepService} implementation</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 */
public class PepApplication implements PepService {

	/** logger */
	private static final Logger logger = Logger.getLogger(PepApplication.class);

	/** PDP access denied <code>String</code> */
	private static final String PDP_RESPONSE_DENY
	= "PdpAccessDenied";

	/** PDP missing <code>String</code> */
	private static final String PDP_RESPONSE_MISSING
	= "PdpMissing";

	/** {@link BundleContext} */
	private BundleContext bundleContext = null;

	//TODO add IdentityMgr

	/** PDP bundle when available **/
	PolicyDecisionPoint pdp = null;

	/** If using remote PDP this contains its attributes **/
	Registration pdpRegistration = null;

	/** {@link PepConfigurator} */
	private PepConfigurator configurator = null;

	/** {@link PdpSessionCache} */
	private PdpSessionCache pdpSessionCache = null;

	/** {@link SoapAttrParser} */
	private SoapAttrParser soapAttrExtractor = new StaxSoapAttrParser();

	/** flag indicating whether to use PDP session buffer */
	private boolean usePdpSessionCache = false;

	/** flag indicating whether to deny on unfulfilled obligations */
	private boolean denyOnUnfulfilledObligations = true;

	/** 
	 * flag indicating whether to default to deny for all "Non permit"
	 * PDP responses
	 */
	private boolean defaultToDeny = false;

	private IdentityManager idMgr;

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.PepService#requestAccessDecision(
	 * 		java.lang.String, java.lang.String, java.lang.String, 
	 * 		java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public PepResponse requestAccessDecision(final VirtualAddress theSndVad,
			final VirtualAddress theRecVad, final String topic, final byte[] msg) {
		if(theSndVad == null || theRecVad == null) {
			throw new IllegalArgumentException("Cannot make access decision without service information!");
		}
		//find out what kind of request this is
		Attributes actionAttrs = null;
		if(isSoapMessage(msg)) {
			actionAttrs = soapAttrExtractor
					.extractActionsFromSoapMsg(new String(msg));
		} else if (isRestMessage(msg)) {
			//handle as string as we are not interested in the payload - only headers
			actionAttrs = extractActionsFromRestMsg(new String(msg));
		}

		if(actionAttrs == null) {
			//create request solely based on sender and receiver and topic
			AttributeValue av = new StringAttribute(topic);
			Set<Attribute> attrs = new HashSet<Attribute>();
			attrs.add(new Attribute(
					URI.create(PepXacmlConstants.ACTION_ACTION_ID.getUrn()),
					null, 
					new DateTimeAttribute(), 
					av,
					true,
					XACMLConstants.XACML_VERSION_3_0));
			actionAttrs = new Attributes(URI.create(XACMLConstants.ACTION_CATEGORY), attrs);
		}

		return evalAccessRequest(new PepRequest(actionAttrs, 
				extrActionAsStringFromAttrs(actionAttrs),
				theSndVad, theRecVad));
	}


	private Attributes extractActionsFromRestMsg(String message) {
		Set<Attribute> attrs = new HashSet<Attribute>();
		String action = null;
		if(message.startsWith("POST")) {
			action="POST";
		} else if (message.startsWith("PUT")) {
			action = "PUT";
		} else if (message.startsWith("GET")) {
			action = "GET";
		} else if (message.startsWith("DELETE")) {
			action = "DELETE";
		}

		AttributeValue av = new StringAttribute(action);
		attrs.add(new Attribute(
				URI.create(PepXacmlConstants.ACTION_ACTION_ID.getUrn()),
				null, 
				new DateTimeAttribute(), 
				av,
				true,
				XACMLConstants.XACML_VERSION_3_0));
		Attributes attrAction = new Attributes(URI.create(XACMLConstants.ACTION_CATEGORY), attrs);
		return attrAction;
	}


	private boolean isRestMessage(byte[] msg) {
		//Determine if message has all necessary HTTP headers to be parsed as such
		String message = new String(msg);
		if (message.startsWith("POST") ||
				message.startsWith("GET") ||
				message.startsWith("DELETE") ||
				message.startsWith("PUT")) {
			return true;
		} else {
			return false;
		}
	}


	private boolean isSoapMessage(byte[] msg) {
		// TODO Auto-generated method stub
		return true;
	}


	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.PepService#requestAccessDecisionWMethod(
	 * 		java.lang.String, java.lang.String, java.lang.String, 
	 * 		java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public PepResponse requestAccessDecisionWMessage(final Message msg) {
		return requestAccessDecision(
				msg.getSenderVirtualAddress(),
				msg.getReceiverVirtualAddress(),
				msg.getTopic(),
				msg.getData());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.PepService#flushCache()
	 */
	@Override
	public void flushCache() {
		logger.debug("Flushing PDP response cache");
		if ((usePdpSessionCache) && (pdpSessionCache != null)) {
			pdpSessionCache.getSessionMemory().flush();
		}

	}

	/**
	 * <p>Changes configuration parameters, for example when a property changes 
	 * and some action has to be taken</p>
	 * 
	 * <p>If properties are read dynamically, nothing needs to be done here.</p>
	 * 
	 * @param theUpdates
	 * 				the configuration updates
	 */
	public synchronized void applyConfigurations(Hashtable<?, ?> theUpdates) {
		if (theUpdates == null) {
			return;
		}
		logger.info("Configuring");
		flushCache();
		if (theUpdates.containsKey(PepConfigurator.PEP_USE_PDP_CACHING)) {
			usePdpSessionCache = Boolean.valueOf(configurator.get(
					PepConfigurator.PEP_USE_PDP_CACHING)).booleanValue();
			logger.debug("PDP session caching: " + usePdpSessionCache);
		}
		if (usePdpSessionCache) {
			pdpSessionCache = new PdpSessionCache();
			if (theUpdates.containsKey(PepConfigurator.PEP_CACHE_LIFETIME)) {
				pdpSessionCache.getSessionMemory().setLifetime(
						Long.valueOf(configurator.get(
								PepConfigurator.PEP_CACHE_LIFETIME)).longValue());			
				logger.debug("PDP session caching lifetime: "
						+ pdpSessionCache.getSessionMemory().getLifetime());
			}
			if (theUpdates.containsKey(PepConfigurator.PEP_CACHE_KEEPALIVE)) {
				pdpSessionCache.getSessionMemory().setKeepAlive(
						Boolean.valueOf(configurator.get(
								PepConfigurator.PEP_CACHE_KEEPALIVE))
								.booleanValue());
				logger.debug("PDP session caching keepalive: "
						+ pdpSessionCache.getSessionMemory().getKeepAlive());
			}
		}
		if (theUpdates.containsKey(
				PepConfigurator.PEP_DEFAULT_TO_DENY_RESPONSE)) {
			defaultToDeny = Boolean.valueOf(configurator.get(
					PepConfigurator.PEP_DEFAULT_TO_DENY_RESPONSE)).booleanValue();
			logger.debug("Default to DENY response: " + defaultToDeny);
		}
		// read configuration parameters for obligation handling
		if (theUpdates.containsKey(
				PepConfigurator.PEP_DENY_ON_UNFULFILLED_OBLIGATION)) {
			denyOnUnfulfilledObligations = Boolean.valueOf(configurator.get(
					PepConfigurator.PEP_DENY_ON_UNFULFILLED_OBLIGATION))
					.booleanValue();
			logger.debug("Deny on unfulfilled obligation: " 
					+ denyOnUnfulfilledObligations);
		}

		logger.info("Configured");
	}

	/**
	 * @return
	 * 				the {@link PepConfigurator} or <code>null</code>
	 */
	public PepConfigurator getConfigurator() {
		return configurator;
	}

	/**
	 * @return
	 * 				the {@link SoapAttrParser}
	 */
	public SoapAttrParser getAttributeParser() {
		return soapAttrExtractor;
	}

	/**
	 * @param theParser
	 * 				the {@link SoapAttrParser} to set
	 * @throws IllegalArgumentException
	 * 				if the argument <code>theParser</code> is 
	 * 				<code>null</code>
	 */
	public void setAttrParser(SoapAttrParser theParser) {
		if (theParser == null) {
			throw new IllegalArgumentException("SoapAttrParser may "
					+ "not be null");
		}
		soapAttrExtractor = theParser;
	}

	/**
	 * @return
	 * 				the {@link PdpSessionCache}
	 */
	public PdpSessionCache getSessionCache() {
		return pdpSessionCache;
	}

	/**
	 * @param theCache
	 * 				the {@link PdpSessionCache}
	 */
	public void setSessionCache(PdpSessionCache theCache) {
		pdpSessionCache  = theCache;
	}

	/**
	 * @return
	 * 				the flag indicating whether a session cache is used
	 */
	public boolean isUseSessionCache() {
		return usePdpSessionCache;
	}

	/**
	 * @param theFlag
	 * 				a flag determining whether a session cache is to be used
	 */
	public void setUseSessionCache(boolean theFlag) {
		usePdpSessionCache = theFlag;
	}

	/**
	 * Activates instance in bundle
	 * 
	 * @param theContext
	 * 				the {@link ComponentContext}
	 */
	protected void activate(final ComponentContext theContext) {
		logger.info("Activating");
		bundleContext = theContext.getBundleContext();
		configurator = new PepConfigurator(bundleContext, this);
		configurator.registerConfiguration();
		logger.info("Activated");
	}

	/**
	 * Deactivates instance in bundle
	 * 
	 * @param theContext
	 * 				the {@link ComponentContext}
	 */
	protected void deactivate(ComponentContext theContext) {
		logger.debug("Deactivating");
	}

	protected void bindIdentityManager(IdentityManager idMgr) {
		this.idMgr = idMgr;
	}

	protected void unbindIdentityManager(IdentityManager idMgr) {
		this.idMgr = null;
	}

	protected void bindPolicyDecisionPoint(PolicyDecisionPoint pdp) {
		this.pdp = pdp;
	}

	protected void unbindPolicyDecisionPoint(PolicyDecisionPoint pdp) {
		pdp = null;
	}

	//	/**
	//	 * @param theSenderHid
	//	 * 				the sender HID
	//	 * @param theReceiverHid
	//	 * 				the receiver HID
	//	 * @return
	//	 * 				a dummy session ID
	//	 */
	//	private String buildLocalSessionId(String theSenderHid, 
	//			String theReceiverHid) {
	//		return theSenderHid + "::" + theReceiverHid;
	//	}

	/**
	 * Returns subject attributes for the argument sender HID and sender 
	 * attributes
	 * 
	 * @param theSenderHid
	 * 				the sender HID
	 * @param theSenderAttrs
	 * 				the sender attributes
	 * @return
	 * 				the {@link Subject}s
	 */
	private Attributes extrSubject(Registration theSenderAttrs) {
		HashSet<Attribute> attrs = new HashSet<Attribute>();
		Attribute attr = new Attribute(
				URI.create(PepXacmlConstants.SUBJECT_SUBJECT_ID.getUrn()),
				null,
				new DateTimeAttribute(),
				new StringAttribute(theSenderAttrs.getVirtualAddressAsString()),
				true,
				XACMLConstants.XACML_VERSION_3_0);
		attrs.add(attr);
		for (Part part : theSenderAttrs.getAttributes()) {
			String key = part.getKey();
			String value = part.getValue();
			attr = new Attribute(
					URI.create(PepXacmlConstants.SUBJECT_LINK_SMART_PREFIX
							.getUrn() + key.toLowerCase()), 
							null,
							new DateTimeAttribute(),
							new StringAttribute(value),
							true,
							XACMLConstants.XACML_VERSION_3_0);
			attrs.add(attr);
		}
		Attributes attrSubject = new Attributes(URI.create(XACMLConstants.SUBJECT_CATEGORY),attrs);
		return attrSubject;
	}

	/**
	 * @param theAttrs
	 * 				the {@link Attribute} <code>Set</code>
	 * @return 
	 * 				the <code>String</code>-encoded action attributes or 
	 * 				<code>null</code>
	 */
	private String extrActionAsStringFromAttrs(Attributes theAttrs) {
		if ((theAttrs != null) && (!theAttrs.getAttributes().isEmpty())) {
			String[] orderedAttributes = new String[theAttrs.getAttributes().size()];
			int i = 0;
			for (Attribute attribute : theAttrs.getAttributes()) {
				ByteArrayOutputStream outstream = new ByteArrayOutputStream();
				attribute.encode(outstream);
				orderedAttributes[i] = outstream.toString();
				i++;
			}
			if (orderedAttributes.length > 1) {
				Arrays.sort(orderedAttributes);
			}
			StringBuffer actionMsgBuff = new StringBuffer();
			actionMsgBuff.append("<ActionAttributes>");
			int oal = orderedAttributes.length;
			for (i=0; i < oal; i++) {
				actionMsgBuff.append(orderedAttributes[i]);
			}
			return actionMsgBuff.append("</ActionAttributes>").toString();
		}
		return null;
	}

	/**
	 * @param theReceiverHid
	 * 				the receiver HID
	 * @param theRecAttrs
	 * 				the receiver attributes
	 * @return
	 * 				the {@link Attribute} <code>Set</code>
	 */
	private Attributes extrResource(Registration theRecAttrs) {
		Set<Attribute> attrs = new HashSet<Attribute>();
		Attribute attr = new Attribute(
				URI.create(PepXacmlConstants.RESOURCE_RESOURCE_ID.getUrn()), 
				null,
				new DateTimeAttribute(),
				new StringAttribute(theRecAttrs.getVirtualAddressAsString()),
				true,
				XACMLConstants.XACML_VERSION_3_0);
		attrs.add(attr);
		for (Part part : theRecAttrs.getAttributes()) {
			String key = part.getKey();
			String value = part.getValue();
			attr = new Attribute(URI.create(
					PepXacmlConstants.RESOURCE_LINK_SMART_PREFIX.getUrn()
					+ key.toLowerCase()), 
					null,
					new DateTimeAttribute(),
					new StringAttribute(value),
					true,
					XACMLConstants.XACML_VERSION_3_0);
			attrs.add(attr);
		}

		Attributes attrResource = new Attributes(URI.create(XACMLConstants.RESOURCE_CATEGORY), attrs);
		return attrResource;
	}

	//	/**
	//	 * Checks session cache
	//	 * 
	//	 * @param sndHid
	//	 * 			the sender HID
	//	 * @param sndProps
	//	 * 			the sender properties extracted from CryptoHID certificate
	//	 * @param recHid
	//	 * 			the received HID
	//	 * @param recProps
	//	 * 			the receiver properties extracted from CryptoHID certificates
	//	 * @param message
	//	 * 			the SOAP message or method call
	//	 * @param sessionId
	//	 * 			the session ID
	//	 * @return
	//	 * 			the {@link ResponseCtx} if available data has been sufficient to 
	//	 * 			evaluate given the session ID, <code>null</code> otherwise
	//	 */
	//	private ResponseCtx qrySessionCache(VirtualAddress sndVad, Properties sndProps,
	//			VirtualAddress recVad, Properties recProps, String message) {
	//		if (message != null) {
	//			return pdpSessionCache.evaluate(sndVad, 
	//					recVad, message, System.currentTimeMillis());
	//		}		
	//		// default is no decision based on session ID data
	//		return null;
	//	}

	/**
	 * @param theRequest
	 * 				the {@link RequestCtx}
	 * @return
	 * 				the PDP {@link ResponseCtx}
	 */
	@SuppressWarnings({"unchecked" })
	private ResponseCtx qryPdp(RequestCtx theRequest) {
		try {
			if (pdp != null) {
				return pdp.evaluate(theRequest);
			} else {
				//get PDP over LinkSmart
				resPdpPid(configurator.get(PepConfigurator.PDP_PID));
				if(pdpRegistration != null) {
					//transform query into xml and make request
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					theRequest.encode(baos);
					String reqXml = baos.toString();
					qryPdpOverNetwork(reqXml);
					//TODO query PDP over LinkSmart
					return null;
				} else {
					logger.warn("No PDP retrieved, hence no decision");
					ArrayList<String> code = new ArrayList<String>();
					code.add(PDP_RESPONSE_MISSING);
					Status status = new Status(code, PDP_RESPONSE_MISSING);
					Result res = new Result(Result.DECISION_INDETERMINATE, status);
					return new ResponseCtx(res);
				}
			}
		} catch (Exception e) {
			logger.error("Error getting Access Decision from PDP: " 
					+ e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			ArrayList<String> code = new ArrayList<String>();
			code.add(PDP_RESPONSE_DENY);
			Status status = new Status(code, PDP_RESPONSE_DENY);
			Result res = new Result(Result.DECISION_INDETERMINATE, status);
			return new ResponseCtx(res);
		}
	}

	private void qryPdpOverNetwork(String reqXml) {
		// TODO Auto-generated method stub

	}


	private Node getXACMLResponse(byte[] bytes) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * @param theResp
	 * 				the {@link ResponseCtx}
	 * @param theRequest
	 * 				the {@link PepRequest}
	 * @return 
	 * 				the {@link PepResponse}
	 */
	private PepResponse evalPdpResponse(ResponseCtx theResp, 
			PepRequest theRequest) {
		// only handle the first result from the response
		Result result = (Result) theResp.getResults().iterator().next();
		String msg = result.getStatus().getMessage();
		if (PDP_RESPONSE_DENY.equals(msg)) {
			return new PepResponse(PepResponse.DECISION_NONE, "ERROR", 
					PepResponse.CODE_PEP_REQUEST_ERROR, 
					"Error evaluating request with provided attributes");
		}
		if (PDP_RESPONSE_MISSING.equals(msg)) {
			return new PepResponse(PepResponse.DECISION_NONE, "ERROR", 
					PepResponse.CODE_PEP_REQUEST_ERROR, "No PDP result");
		}
		int decision = PepResponse.DECISION_NONE;
		String status = "ERROR";
		int statusCode = PepResponse.CODE_PEP_REQUEST_ERROR;
		String errorMsg = "No results returned";
		// rewrite INDETERMINATE and NOT_APPLICABLE if PEP defaults to deny			
		if (defaultToDeny) {
			if ((Result.DECISION_INDETERMINATE == result.getDecision()) 
					|| (Result.DECISION_NOT_APPLICABLE 
							== result.getDecision())) {
				result = new Result(Result.DECISION_DENY, 
						result.getStatus(),
						result.getObligations(),
						result.getAdvices(),
						null,
						result.getAttributes());
			}
		}
		switch (result.getDecision()) {
		case Result.DECISION_PERMIT : {
			decision = PepResponse.DECISION_PERMIT;
			status = msg;
			statusCode = PepResponse.CODE_OK;
			errorMsg = "";
			break;
		}
		case Result.DECISION_DENY : {
			decision = PepResponse.DECISION_DENY;
			status = msg;
			statusCode = PepResponse.CODE_OK;
			errorMsg = "";
			break;
		}
		case Result.DECISION_INDETERMINATE : {
			decision = PepResponse.DECISION_NONE;
			status = msg;
			statusCode = PepResponse.CODE_PDP_REQUEST_INDETERMINATE;
			errorMsg = "Error at PDP - Indeterminate";
			break;
		}
		case Result.DECISION_NOT_APPLICABLE : {
			decision = PepResponse.DECISION_NONE;
			status = msg;
			statusCode = PepResponse.CODE_PDP_REQUEST_NONAPPLICABLE;
			errorMsg = "Error at PDP - No Applicable Policy found";
			break;
		}
		default: {
			// intentionally left blank
		}
		}
		boolean fulfilled = fulfillObligations(result, theRequest, theResp);
		//check if obligations are fulfilled, else deny
		if ((denyOnUnfulfilledObligations) && (!fulfilled)) {
			logger.info("One or more obligations could not be satisfied, "
					+ "setting decision to DENY");
			decision = PepResponse.DECISION_DENY;
			status = msg;
			statusCode = PepResponse.CODE_OK;
			errorMsg = "";
		}
		return new PepResponse(decision, status, statusCode, errorMsg);
	}

	/**
	 * Carries out all actions required by returned PDP obligations
	 * 
	 * @param theResult
	 * 				the {@link Result}
	 * @param theRequest
	 * 				the {@link PepRequest}
	 * @param theResponse
	 * 				the {@link ResponseCtx}
	 * @return
	 * 				a flag indicating whether all obligations were fulfilled
	 */
	private boolean fulfillObligations(Result theResult, PepRequest theRequest,
			ResponseCtx theResponse) {	
		//TODO
		return true;
	}

	/**
	 * Evaluates access request
	 * 
	 * @param theRequest
	 * 				the {@link PepRequest}
	 * @return
	 * 				the {@link PepResponse}
	 */
	private PepResponse evalAccessRequest(final PepRequest theRequest) {
		//get all properties that are available locally
		Registration sndReg = getRegistrationForVad(theRequest.getSndVad());
		Registration recReg = getRegistrationForVad(theRequest.getRecVad());
		//		if ((usePdpSessionCache) && (pdpSessionCache != null)) {
		//			// evaluate session
		//			ResponseCtx sesResponse = qrySessionCache(theRequest.getSndVad(), 
		//					sndProps, theRequest.getRecVad(), recProps, 
		//					theRequest.getActionAttrString());
		//			if (sesResponse != null) {
		//				// session evaluation led to an actual result
		//				logger.debug("Returning cached decision");
		//				return evalPdpResponse(sesResponse, theRequest);				
		//			}
		//			// else the session cache did not return a result
		//		}		
		//		// PEP2PEP calls are permitted without PDP verification
		//		if (isAuthdPepToPdpCall(sndProps, recProps)) {
		//			return new PepResponse(PepResponse.DECISION_PERMIT, 
		//					"Authenticated PEP->PDP call", PepResponse.CODE_OK, "");
		//		}	

		Set<Attributes> attrs = new HashSet<Attributes>();
		Attributes subject = extrSubject(sndReg);
		attrs.add(subject);
		Attributes resource = extrResource(recReg);
		attrs.add(resource);
		attrs.add(theRequest.getActionAttrs());
		RequestCtx req = new RequestCtx(attrs, null);
		ResponseCtx pdpResponse = qryPdp(req);
		//		if ((usePdpSessionCache) && (pdpSessionCache != null)) {
		//			// we only care about the first result
		//			HashSet<Obligation> responseOblis = new HashSet<Obligation>();
		//			Result r = (Result) pdpResponse.getResults().iterator().next();
		//			pdpSessionCache.add(theRequest.getSndVad(), theRequest.getRecVad(), 
		//					theRequest.getActionAttrString(), new ResponseCtx(
		//							new Result(r.getDecision(), r.getStatus(), 
		//									r.getResource(), responseOblis)), 
		//									System.currentTimeMillis());			
		//		}
		return evalPdpResponse(pdpResponse, theRequest);
	}

	/**
	 * @param thePdpPid
	 * 				the PDP PID
	 * @return
	 * 				the PDP HID or <code>null</code> when a 
	 * 				{@link RemoteException} occurs
	 */
	private void resPdpPid(String thePdpPid) {
		if(pdpRegistration == null) {
			Registration[] regs = idMgr.getServiceByAttributes(
					new Part[]{}, IdentityManager.SERVICE_RESOLVE_TIMEOUT, true, true);
			if(regs != null && regs.length == 1) {
				pdpRegistration = regs[0];
			}
		}
	}

	//	/**
	//	 * Returns URL for making calls through NetworkManager SoapTunnelling to the  
	//	 * given Hid
	//	 * 
	//	 * @param theRecHid
	//	 * 				the target HID
	//	 * @param theSessionId
	//	 * 				the session ID
	//	 * @return
	//	 * 				the URL string
	//	 */
	//	private String resSoapTunnelAddr(String theRecHid, String theSessionId) {
	//		return "http://localhost:" + httpPort + "/SOAPTunneling/" + pepHid + "/" 
	//				+ theRecHid + "/" + theSessionId + "/";
	//	}

	private Registration getRegistrationForVad(VirtualAddress vad) {
		return idMgr.getServiceInfo(vad);
	}

}
