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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.wso2.balana.ObligationResult;
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
import org.wso2.balana.xacml3.Obligation;

import eu.linksmart.network.Message;
import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.policy.LinkSmartXacmlConstants;
import eu.linksmart.policy.pdp.PolicyDecisionPoint;
import eu.linksmart.policy.pep.ObligationExecutor;
import eu.linksmart.policy.pep.PepResponse;
import eu.linksmart.policy.pep.PepService;
import eu.linksmart.policy.pep.cache.impl.PdpSessionCache;
import eu.linksmart.policy.pep.request.SoapAttrParser;
import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.request.impl.StaxSoapAttrParser;
import eu.linksmart.security.communication.SecurityProperty;
import eu.linksmart.utils.Part;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;

/**
 * <p>Default {@link PepService} implementation</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * @author Mark Vinkovits
 * 
 */
@Component(name="eu.linksmart.policy.pep", immediate=true)
@Service({PepService.class})
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

	/**
	 * The list of possible executors to evaluate obligations against.
	 */
	private List<ObligationExecutor> obligationExecs = new ArrayList<ObligationExecutor>(); 
	
	@Reference(name="ConfigurationAdmin",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
		    bind="bindConfigAdmin",
		    unbind="unbindConfigAdmin",
		    policy=ReferencePolicy.STATIC)
	protected ConfigurationAdmin configAdmin = null;

	/** PDP bundle when available **/
	@Reference(name="PolicyDecisionPoint",
			cardinality = ReferenceCardinality.OPTIONAL_UNARY,
			bind="bindPolicyDecisionPoint",
			unbind="unbindPolicyDecisionPoint",
			policy= ReferencePolicy.DYNAMIC)
	PolicyDecisionPoint pdp = null;

	@Reference(name="ObligationExecutor",
			cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
			bind="bindObligationExecutor",
			unbind="unbindObligationExecutor",
			policy= ReferencePolicy.DYNAMIC)
	private ObligationExecutor oblExe;
	
	protected void bindPolicyDecisionPoint(PolicyDecisionPoint pdp) {
		this.pdp = pdp;
	}

	protected void unbindPolicyDecisionPoint(PolicyDecisionPoint pdp) {
		pdp = null;
	}

	protected void bindObligationExecutor(ObligationExecutor obligationEx) {
		obligationExecs.add(obligationEx);
	}

	protected void unbindObligationExecutor(ObligationExecutor obligationEx) {
		int index = 0;
		boolean found = false;
		//find index of ObligationExecutor with same id
		for (ObligationExecutor oe : this.obligationExecs) {
			if (oe.getId() != null && oe.getId().equals(obligationEx.getId())) {
				found = true;
				break;
			}
			index++;
		}
		if(found) {
			//remove item based on index
			this.obligationExecs.remove(index);
		}
	}
	
	protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
        this.configAdmin = configAdmin;
    }

    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
        this.configAdmin = null;
    }
    
    /**
	 * Activates instance in bundle
	 * 
	 * @param theContext
	 * 				the {@link ComponentContext}
	 */
	@Activate
	protected void activate(final ComponentContext theContext) {
		logger.info("activating policy-pep");
		bundleContext = theContext.getBundleContext();
		configurator = new PepConfigurator(bundleContext, this, configAdmin);
		configurator.registerConfiguration();
		logger.info("activated policy-pep");
	}

	/**
	 * Deactivates instance in bundle
	 * 
	 * @param theContext
	 * 				the {@link ComponentContext}
	 */
	@Deactivate
	protected void deactivate(ComponentContext theContext) {
		logger.debug("deactivating policy-pep");
	}

	private PepResponse requestAccessDecision(final VirtualAddress theSndVad,
			final VirtualAddress theRecVad, final String topic, final byte[] msg, final Set<SecurityProperty> appliedSecurity) {
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
					URI.create(LinkSmartXacmlConstants.ACTION_ACTION_ID.getUrn()),
					null, 
					new DateTimeAttribute(), 
					av,
					true,
					XACMLConstants.XACML_VERSION_3_0));
			actionAttrs = new Attributes(URI.create(XACMLConstants.ACTION_CATEGORY), attrs);
		}

		return evalAccessRequest(new PepRequest(actionAttrs, 
				extrActionAsStringFromAttrs(actionAttrs),
				theSndVad, theRecVad, appliedSecurity));
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.PepService#requestAccessDecisionWMethod(
	 * 		java.lang.String, java.lang.String, java.lang.String, 
	 * 		java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public PepResponse requestAccessDecisionForNetworkManager(final Message msg, final Set<SecurityProperty> appliedSecurity) {
		return requestAccessDecision(
				msg.getSenderVirtualAddress(),
				msg.getReceiverVirtualAddress(),
				msg.getTopic(),
				msg.getData(),
				appliedSecurity);
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.PepService#requestAccessDecision(
	 * 		eu.linksmart.network.VirtualAddress, eu.linksmart.network.VirtualAddress, 
	 * 		java.lang.String, java.lang.byte[])
	 */
	@Override
	public PepResponse requestAccessDecision(final VirtualAddress theSndVad,
			final VirtualAddress theRecVad, final String topic, final byte[] msg) {
		return requestAccessDecision(
				theSndVad,
				theRecVad,
				topic,
				msg,
				null);
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
				URI.create(LinkSmartXacmlConstants.ACTION_ACTION_ID.getUrn()),
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
	private Attributes extrSubject(VirtualAddress senderVad) {
		HashSet<Attribute> attrs = new HashSet<Attribute>();
		Attribute attr = new Attribute(
				URI.create(LinkSmartXacmlConstants.SUBJECT_SUBJECT_ID.getUrn()),
				null,
				new DateTimeAttribute(),
				new StringAttribute(senderVad.toString()),
				true,
				XACMLConstants.XACML_VERSION_3_0);
		attrs.add(attr);

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
				StringBuilder sb = new StringBuilder();
				attribute.encode(sb);
				orderedAttributes[i] = sb.toString();
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
	private Attributes extrResource(VirtualAddress receiverVad) {
		Set<Attribute> attrs = new HashSet<Attribute>();
		Attribute attr = new Attribute(
				URI.create(LinkSmartXacmlConstants.RESOURCE_RESOURCE_ID.getUrn()), 
				null,
				new DateTimeAttribute(),
				new StringAttribute(receiverVad.toString()),
				true,
				XACMLConstants.XACML_VERSION_3_0);
		attrs.add(attr);

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
			//transform query into xml and make request
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			theRequest.encode(baos);
			String reqXml = baos.toString();
			if (pdp != null) {
				String response = pdp.evaluate(reqXml);
				Element node =  DocumentBuilderFactory
						.newInstance()
						.newDocumentBuilder()
						.parse(new ByteArrayInputStream(response.getBytes()))
						.getDocumentElement();
				ResponseCtx respObject = ResponseCtx.getInstance(node);
				return respObject;
			} else {
				//get PDP over LinkSmart
				resPdpPid(configurator.get(PepConfigurator.PDP_PID));
				if(pdpRegistration != null) {
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
			PepRequest theRequest, RequestCtx theRequestCtx) {
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
		boolean fulfilled = fulfillObligations(result, theRequestCtx, theResp);
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
	private boolean fulfillObligations(Result theResult, RequestCtx theRequest,
			ResponseCtx theResponse) {	
		for(ObligationResult obl : theResult.getObligations()) {
			boolean fulfilled = false;
			//go through each executor to try to fulfill obligations
			for(ObligationExecutor oblExec : this.obligationExecs) {
				StringBuilder sbObl = new StringBuilder();
				obl.encode(sbObl);
				ByteArrayOutputStream reqStream = new ByteArrayOutputStream();
				theRequest.encode(reqStream);
				ByteArrayOutputStream respStream = new ByteArrayOutputStream();
				theRequest.encode(respStream);
				if(oblExec.evaluate(sbObl.toString(), reqStream.toString(), respStream.toString())) {
					fulfilled = true;
					break;
				}
			}
			//if this obligation was not fulfilled stop evaluation
			if(!fulfilled){
				return false;
			}
		}
		//if we get here all obligations were fulfilled
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
		Attributes subject = extrSubject(theRequest.getSndVad());
		attrs.add(subject);
		Attributes resource = extrResource(theRequest.getRecVad());
		attrs.add(resource);
		attrs.add(theRequest.getActionAttrs());
		if(theRequest.getAppliedSecurity() != null) {
			Attributes connection = extrConnection(theRequest.getAppliedSecurity());
			attrs.add(connection);
		}

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
		return evalPdpResponse(pdpResponse, theRequest, req);
	}

	private Attributes extrConnection(Set<SecurityProperty> appliedSecurity) {
		Set<Attribute> attrs = new HashSet<Attribute>();
		for(SecurityProperty prop : appliedSecurity) {
			Attribute attr = new Attribute(
					URI.create(LinkSmartXacmlConstants.CONNECTION_PROPERTY_APPLIED.getUrn()), 
					null,
					new DateTimeAttribute(),
					new StringAttribute(prop.name()),
					true,
					XACMLConstants.XACML_VERSION_3_0);
			attrs.add(attr);
		}

		Attributes attrResource = new Attributes(URI.create(LinkSmartXacmlConstants.CONNECTION_PROPERTY_CATEGORY.getUrn()), attrs);
		return attrResource;
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
			//TODO
//			Registration[] regs = idMgr.getServiceByAttributes(
//					new Part[]{}, IdentityManager.SERVICE_RESOLVE_TIMEOUT, true, true);
//			if(regs != null && regs.length == 1) {
//				pdpRegistration = regs[0];
//			}
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
}
