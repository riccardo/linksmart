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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.Obligation;
import com.sun.xacml.ParsingException;
import com.sun.xacml.Policy;
import com.sun.xacml.PolicySet;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.ComparisonFunction;
import com.sun.xacml.cond.DateMathFunction;
import com.sun.xacml.cond.EqualFunction;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.finder.PolicyFinderResult;
import com.sun.xacml.finder.ResourceFinderResult;

import eu.linksmart.policy.pdp.PolicyDecisionPoint;
import eu.linksmart.policy.pdp.ext.impl.LinkSmartEvaluationContext;

/**
 * <p>LinkSmart {@link PolicyDecisionPoint} implementation</p>
 * 
 * <p>Components relevant for the PDP can be configured via the 
 * <code>pdpConfig</code> {@link PdpDecisionConfiguration} field that 
 * bundles required finders.</p>
 * 
 * <p>This implementation provides configurable caching behavior instructions 
 * that can be handled by LinkSmart PEP implementations. Specifically, the session 
 * lifetime for session caching can be configured. This should generally be 
 * set to the value expected to be set in the counterpart PEP(s).</p>
 * 
 * <p>Additionally, the default session caching behavior for decisions created 
 * by this PDP can be configured to either a default ALLOW or a default DENY 
 * behavior. These behaviors differ in the way session lifetime instructions 
 * are handled as outlined below.</p>
 * 
 * <p>For default ALLOW behavior, no default session lifetime obligations 
 * are added to PEP responses. This setting should also be used if caching is 
 * not used in an application in order to reduce the size of PDP responses. 
 * As exceptions to the default ALLOW behavior, some date- and time-based 
 * conditions in XACML policies are always annotated with a session cache 
 * lifetime obligation with a value of zero milliseconds in order to prevent 
 * session caching errors for repeating date and/or time windows. If a session 
 * lifetime obligation was specified for a policy that contains any of such 
 * date- and time-based conditions, that specified lifetime obligation will be 
 * disregarded and a zero millisecond lifetime obligation will be returned 
 * instead. The following date- and time-based conditions are treated this 
 * way (this list may change subject to security reviews in future versions):
 * </p>
 * 
 * <ul>
 * 	<li>yearMonthDuration-equal</li> 
 * 	<li>yearMonthDuration-one-and-only</li>
 * 	<li>yearMonthDuration-bag-size</li> 
 * 	<li>yearMonthDuration-is-in</li> 
 * 	<li>yearMonthDuration-bag</li> 
 * 	<li>yearMonthDuration-intersection</li> 
 * 	<li>yearMonthDuration-at-least-one-member-of</li> 
 * 	<li>yearMonthDuration-union</li> 
 * 	<li>yearMonthDuration-subset</li> 
 * 	<li>yearMonthDuration-set-equals</li> 
 * 	<li>DateMathFunction.NAME_DATE_ADD_YEARMONTHDURATION</li> 
 * 	<li>DateMathFunction.NAME_DATE_SUBTRACT_YEARMONTHDURATION</li> 
 * 	<li>date-bag-size</li> 
 * 	<li>date-is-in</li> 
 * 	<li>date-bag</li> 
 * 	<li>date-intersection</li> 
 * 	<li>date-at-least-one-member-of</li> 
 * 	<li>date-union</li> 
 * 	<li>date-subset</li> 
 * 	<li>date-set-equals</li> 
 * 	<li>DateMathFunction.NAME_DATETIME_ADD_DAYTIMEDURATION</li> 
 * 	<li>DateMathFunction.NAME_DATETIME_SUBTRACT_DAYTIMEDURATION</li> 
 * 	<li>DateMathFunction.NAME_DATETIME_ADD_YEARMONTHDURATION</li> 
 * 	<li>DateMathFunction.NAME_DATETIME_SUBTRACT_YEARMONTHDURATION</li> 
 * 	<li>dateTime-bag-size</li> 
 * 	<li>dateTime-is-in</li> 
 * 	<li>dateTime-bag</li> 
 * 	<li>dateTime-intersection</li> 
 * 	<li>dateTime-at-least-one-member-of</li> 
 * 	<li>dateTime-union</li> 
 * 	<li>dateTime-subset</li> 
 * 	<li>dateTime-set-equals</li> 
 * 	<li>dayTimeDuration-equal</li> 
 * 	<li>dayTimeDuration-one-and-only</li> 
 * 	<li>dayTimeDuration-bag-size</li> 
 * 	<li>dayTimeDuration-is-in</li> 
 * 	<li>dayTimeDuration-bag</li> 
 * 	<li>dayTimeDuration-union</li> 
 * 	<li>dayTimeDuration-subset</li> 
 * 	<li>dayTimeDuration-set-equals</li> 
 * 	<li>time-bag-size</li> 
 * 	<li>time-is-in</li> 
 * 	<li>time-bag</li> 
 * 	<li>time-intersection</li> 
 * 	<li>time-at-least-one-member-of</li> 
 * 	<li>time-union</li> 
 * 	<li>time-subset</li> 
 * 	<li>time-set-equals</li>  
 * </ul>
 * 
 * <p>For all XACML standard date, datetime and time conditions other than the 
 * ones listed directly above, this PDP implementation checks whether PDP 
 * responses are identical when comparing a PDP response for the timestamp at 
 * request time with a request for (timestamp + configured session lifetime). 
 * If the policy additionally provides a session lifetime obligation, that 
 * obligation is checked as well. If policy-specified lifetimes can be 
 * verified, they are kept. If no policy-specific lifetimes are present, but 
 * the (timestamp + configured session lifetime) case can be verified, the 
 * session lifetime is set as lifetime obligation value. If neither can be 
 * verified, a caching lifetime of zero milliseconds is added as an obligation 
 * in the PDP response.</p>
 * 
 * <p>For default DENY behavior, a caching lifetime obligation with a value of 
 * zero milliseconds is added to all PDP responses for which no other session 
 * lifetime obligation has been specified. If a session lifetime obligation has 
 * been specified, it will be returned as part of the result regardless of 
 * any conditions that may be part of the policy. This means that the 
 * limitations outlined above for default ALLOW behavior do not apply or are in 
 * any way included for default DENY behavior.</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 */
public class PluginLinkSmartPDP implements PolicyDecisionPoint {
	
	/** logger */
	private static final Logger logger 
			= Logger.getLogger(PluginLinkSmartPDP.class);
	
	/** "uncheckable" functions wrt caching */ 
	private static final String[] UNCHECKABLE_FUNCTIONS = {
		"yearMonthDuration-equal", 
		"yearMonthDuration-one-and-only",
		"yearMonthDuration-bag-size",
		"yearMonthDuration-is-in",
		"yearMonthDuration-bag",
		"yearMonthDuration-intersection",
		"yearMonthDuration-at-least-one-member-of",
		"yearMonthDuration-union",
		"yearMonthDuration-subset",
		"yearMonthDuration-set-equals",
		DateMathFunction.NAME_DATE_ADD_YEARMONTHDURATION,
		DateMathFunction.NAME_DATE_SUBTRACT_YEARMONTHDURATION,
		"date-bag-size",
		"date-is-in",
		"date-bag",
		"date-intersection",
		"date-at-least-one-member-of",
		"date-union",
		"date-subset",
		"date-set-equals",
		DateMathFunction.NAME_DATETIME_ADD_DAYTIMEDURATION,
		DateMathFunction.NAME_DATETIME_SUBTRACT_DAYTIMEDURATION,
		DateMathFunction.NAME_DATETIME_ADD_YEARMONTHDURATION,
		DateMathFunction.NAME_DATETIME_SUBTRACT_YEARMONTHDURATION,
		"dateTime-bag-size",
		"dateTime-is-in",
		"dateTime-bag",
		"dateTime-intersection",
		"dateTime-at-least-one-member-of",
		"dateTime-union",
		"dateTime-subset",
		"dateTime-set-equals",
		"dayTimeDuration-equal", 
		"dayTimeDuration-one-and-only",
		"dayTimeDuration-bag-size",
		"dayTimeDuration-is-in",
		"dayTimeDuration-bag",
		"dayTimeDuration-union",
		"dayTimeDuration-subset",
		"dayTimeDuration-set-equals",
		"time-bag-size",
		"time-is-in",
		"time-bag",
		"time-intersection",
		"time-at-least-one-member-of",
		"time-union",
		"time-subset",
		"time-set-equals",
	};
	
	/** "checkable" functions wrt caching */ 
	private static final String[] CHECKABLE_FUNCTIONS = {
		EqualFunction.NAME_DATE_EQUAL,
		ComparisonFunction.NAME_DATE_GREATER_THAN, 
		ComparisonFunction.NAME_DATE_GREATER_THAN_OR_EQUAL,
		ComparisonFunction.NAME_DATE_LESS_THAN,
		ComparisonFunction.NAME_DATE_LESS_THAN_OR_EQUAL,
		EqualFunction.NAME_DATETIME_EQUAL,
		ComparisonFunction.NAME_DATETIME_GREATER_THAN,
		ComparisonFunction.NAME_DATETIME_GREATER_THAN_OR_EQUAL,
		ComparisonFunction.NAME_DATETIME_LESS_THAN,
		ComparisonFunction.NAME_DATETIME_LESS_THAN_OR_EQUAL,
		EqualFunction.NAME_TIME_EQUAL,
		ComparisonFunction.NAME_TIME_GREATER_THAN,
		ComparisonFunction.NAME_TIME_GREATER_THAN_OR_EQUAL,
		ComparisonFunction.NAME_TIME_LESS_THAN,
		ComparisonFunction.NAME_TIME_LESS_THAN_OR_EQUAL
	};
	
	/** {@link PdpDecisionConfig} */
	private PdpDecisionConfig pdpConfig = null;
	
	/** flag determining whether to allow caching by default */
	private boolean defaultCachingToAllow = false;
	
	/** session lifetime */
	private long sessionLifetime = 30000L;
	
	/**
	 * Constructor
	 * 
	 * @param theConfig
	 * 				the {@link PdpDecisionConfig}
	 */
	public PluginLinkSmartPDP(PdpDecisionConfig theConfig) {
		pdpConfig = theConfig;
	}
	
	/**
	 * @return
	 * 				the session lifetime in milliseconds
	 */
	public long getSessionLifetime() {
		return sessionLifetime;
	}
	
	/**
	 * @param theSessionLifetime
	 * 				the session lifetime in milliseconds
	 */
	public void setSessionLifetime(long theSessionLifetime) {
		sessionLifetime = theSessionLifetime;
	}
	
	/**
	 * @return
	 * 				the flag indicating whether caching is allowed by default
	 */
	public boolean isAllowDefaultCaching() {
		return defaultCachingToAllow;
	}
	
	/**
	 * @param theFlag
	 * 				the flag indicating whether to allow caching by default
	 */
	public void setAllowDefaultCaching(boolean theFlag) {
		defaultCachingToAllow = theFlag;
	}	

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pdp.PolicyDecisionPoint#evaluate(
	 * 		java.lang.String)
	 */
	@Override
	public String evaluate(String theReqXml) {
		ResponseCtx resp = null;
		try {
			resp = evaluate(RequestCtx.getInstance(
					new ByteArrayInputStream(theReqXml.getBytes())));
		} catch (Exception e) {
			logger.warn("Error parsing request: " + e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
			resp = new ResponseCtx(new Result(Result.DECISION_INDETERMINATE, 
					"Error parsing Request"));
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resp.encode(baos);
        return baos.toString();
	}

	/**
	 * Evaluates a PEP {@link RequestCtx}
	 * 
     * @param theReq
     * 				the PEP {@link RequestCtx}
     * @return
     * 				the PDP {@link ResponseCtx}
     */
    public ResponseCtx evaluate(RequestCtx theReq) {
        try {
            // create the EvaluationContext from the RequestContext
        	if  (pdpConfig == null) {
        		pdpConfig = new PdpDecisionConfig(null, null, null, null);
        	}        
            EvaluationCtx evalCtx = new LinkSmartEvaluationContext(theReq, 
            		pdpConfig.getAttributeFinder());
            // evaluate it
            return evaluate(evalCtx);
        } catch (ParsingException pe) {
        	logger.warn("Parsing exception occured: " 
        			+ pe.getLocalizedMessage());
        	if (logger.isDebugEnabled()) {
        		logger.debug("Stack trace: ", pe);
			}
        	/*
             * there was something wrong with the request, so we return
             * Indeterminate with a status of syntax error...though this
             * may change if a more appropriate status type exists
             */
            ArrayList<Object> code = new ArrayList<Object>();
            code.add(Status.STATUS_SYNTAX_ERROR);
            Status status = new Status(code, pe.getMessage());
            return new ResponseCtx(new Result(Result.DECISION_INDETERMINATE,
            		status));
        }
    }
    
    /**
     * Evaluates an {@link EvaluationCtx}
     * 
     * @param theEvalCtx
     * 				the {@link EvaluationCtx}
     * @return
     * 				the {@link ResponseCtx}
     */
    @SuppressWarnings("unchecked")
	public ResponseCtx evaluate(EvaluationCtx theEvalCtx) {   
         // see if we need to call the resource finder
        if (EvaluationCtx.SCOPE_IMMEDIATE != theEvalCtx.getScope()) {
            AttributeValue parent = theEvalCtx.getResourceId();
            ResourceFinderResult resourceResult = null;
            if (EvaluationCtx.SCOPE_CHILDREN == theEvalCtx.getScope()) {
                resourceResult = pdpConfig.getResourceFinder()
                		.findChildResources(parent, theEvalCtx);
            } else {
                resourceResult = pdpConfig.getResourceFinder()
                		.findDescendantResources(parent, theEvalCtx);
            }
            // see if we actually found anything
            if (resourceResult.isEmpty()) {
            	/*
                 * this is a problem, since we couldn't find any resources
                 * to work on...the spec is not explicit about what kind of
                 * error this is, so we're treating it as a processing error
                 */
                ArrayList code = new ArrayList();
                code.add(Status.STATUS_PROCESSING_ERROR);
                String msg = "Couldn't find any resources to work on";
                return new
                    ResponseCtx(new Result(Result.DECISION_INDETERMINATE,
                    		new Status(code, msg),
                            theEvalCtx.getResourceId().encode()));
            }
            // setup a set to keep track of the results
            HashSet results = new HashSet();
            /*
             * at this point, we need to go through all the resources we
             * successfully found and start collecting results
             */
            Iterator it = resourceResult.getResources().iterator();
            while (it.hasNext()) {
                // get the next resource, and set it in the EvaluationCtx
                AttributeValue resource = (AttributeValue) it.next();
                theEvalCtx.setResourceId(resource);
                // do the evaluation, and set the resource in the result
                Result result = evaluateContext(theEvalCtx);
                result.setResource(resource.encode());
                // add the result
                results.add(result);
            }
            /*
             * now that we've done all the successes, we add all the failures
             * from the finder result
             */
            Map failureMap = resourceResult.getFailures();
            it = failureMap.keySet().iterator();
            while (it.hasNext()) {
                // get the next resource, and use it to get its Status data
                AttributeValue resource = (AttributeValue) it.next();
                Status status = (Status) failureMap.get(resource);
                // add a new result
                results.add(new Result(Result.DECISION_INDETERMINATE,
                                       status, resource.encode()));
            }
            // return the set of results
            return new ResponseCtx(results);
        }
        /*
         * the scope was IMMEDIATE (or missing), so we can just evaluate
         * the request and return whatever we get back             
         */
        return new ResponseCtx(evaluateContext(theEvalCtx));        
    }
    
    /**
     * Checks whether the argument policy XML <code>String</code> can be 
     * parsed by the PDP implementation
     * 
     * @param thePolicyXml
     * 				the policy XML <code>String</code>
     * @return
     * 				a success (<code>true</code>)/failure(<code>false</code> 
     * 				indicator
     */
    public boolean validatePolicy(String thePolicyXml) 
    		throws ParserConfigurationException, SAXException, IOException, 
    		ParsingException {
   		DocumentBuilderFactory factory 
				= DocumentBuilderFactory.newInstance();
   		factory.setIgnoringComments(true);
   		factory.setIgnoringElementContentWhitespace(true);
   		// we are namespace aware
   		factory.setNamespaceAware(true);
   		// we are not doing any validation
   		factory.setValidating(false);
   		DocumentBuilder xmlDocBuilder = factory.newDocumentBuilder();
     	Document doc = xmlDocBuilder.parse(
      			new ByteArrayInputStream(thePolicyXml.getBytes()));
        // handle the policy if it is a known type
        Element root = doc.getDocumentElement();
        String name = root.getTagName();
        if ("Policy".equals(name)) {
        	try {
        		Policy.getInstance(root);
        	} catch(IllegalArgumentException iae) {
        		logger.warn("Policy could not be represented internally and "
        				+ "was not added");
        		return false;
        	}
         	return true;
        } else if ("PolicySet".equals(name)) {
         	PolicySet.getInstance(root, pdpConfig.getPolicyFinder());
            return true;
        } else {
        	// this is not a root type that we know how to handle
         	logger.warn("Cannot handle policy root type: "
          			+ root.getLocalName());
        }
		logger.warn("Policy could not be validated and was not added");
        return false;
    }

    /**
     * Resolves a policy for the given context and attempts to evaluate based 
     * on the policy
     * 
     * @param theContext
     * 				{@link EvaluationCtx}
     * @return
     * 				{@link Result}
     */
    private Result evaluateContext(EvaluationCtx theContext) {
        // first off, try to find a policy
    	PolicyFinderResult finderResult 
    			= pdpConfig.getPolicyFinder().findPolicy(theContext);
        // see if there weren't any applicable policies
        if (finderResult.notApplicable()) {
            return new Result(Result.DECISION_NOT_APPLICABLE,
            		theContext.getResourceId().encode());
        }
        // see if there were any errors in trying to get a policy
        if (finderResult.indeterminate()) {
            return new Result(Result.DECISION_INDETERMINATE,
            		finderResult.getStatus(),
                    theContext.getResourceId().encode());
        }
        // we found a valid policy, so we can do the evaluation
        try {
        	AbstractPolicy policy = finderResult.getPolicy();
        	Result result = policy.evaluate(theContext);
        	if (defaultCachingToAllow) {
        		// get policy file
        		OutputStream out = new ByteArrayOutputStream();
        		policy.encode(out);
        		String plcXml = out.toString();
        		Result unRes = evalForUncheckableFunctions(plcXml, result);
        		if (unRes != null) {
        			return unRes;
        		}
        		return evalForCheckableFunctions(policy, plcXml, result, 
        				theContext);
        	}
        	// if default caching is DENY, search for existing cache obligations
        	boolean cacheObligationFound = false;
        	for (Object oblObj : result.getObligations()) {
        		Obligation obl = (Obligation) oblObj;
        		if (PdpXacmlConstants.OBLIGATION_CACHE.getUrn()
        				.equalsIgnoreCase(obl.getId().toString())) {
        			cacheObligationFound = true;
        			break;
        		}
        	}
        	if (!cacheObligationFound) {        		
        		Obligation obli = getCacheObligation(result.getDecision(), "0");
        		if (obli != null)
        			result.addObligation(obli);
        	}        	
        	return result;
        } catch(Exception e) {
        	logger.error("Exception occured in PDP: " 
        			+ e.getLocalizedMessage());
        	if (logger.isDebugEnabled()) {
        		logger.debug("Stack trace: ", e);
			}
            return new Result(Result.DECISION_INDETERMINATE,
                    finderResult.getStatus(),
                    theContext.getResourceId().encode());
        }
    }
    
    /**
     * @param thePoliy
     * 				the policy XML <code>String</code>
     * @param theResult
     * 				the {@link Result}
     * @return
     * 				the {@link Result} or <code>null</code>
     */
    private Result evalForUncheckableFunctions(String thePoliy, 
    		Result theResult) {
		// check for actions that should never be cached
		for (String key : UNCHECKABLE_FUNCTIONS) {
			if (thePoliy.contains(key)) {
				/*
				 * never cache as our simplistic checker method cannot
				 * verify these
				 */
				boolean cacheObligationFound = false;
				HashSet<Obligation> clearedObligations
						= new HashSet<Obligation>();        				
				for (Object oblObj : theResult.getObligations()) {
					Obligation obl = (Obligation) oblObj;
					if (PdpXacmlConstants.OBLIGATION_CACHE.getUrn()
							.equalsIgnoreCase(obl.getId().toString())) {  
						cacheObligationFound = true;
					} else {
						clearedObligations.add(obl);
					}
				}
				Obligation obli = getCacheObligation(theResult.getDecision(), 
						"0");
				if (cacheObligationFound) {        				
					clearedObligations.add(obli);
					return new Result(theResult.getDecision(),
							theResult.getStatus(), clearedObligations);
				}
				theResult.addObligation(obli);
				return theResult;								
			}
		}
		return null;
    }
    
    /**
     * @param thePolicy
     * 				the {@link AbstractPolicy}
     * @param thePolicyString
     * 				the policy <code>String</code>
     * @param theResult
     * 				the {@link Result}
     * @param theEvalCtx
     * 				the {@link EvaluationCtx}
     * @return
     * 				the {@link Result}
     */
    private Result evalForCheckableFunctions(AbstractPolicy thePolicy, 
    		String thePolicyString, Result theResult, EvaluationCtx theEvalCtx) {
    	long evalTime = System.currentTimeMillis();
   		boolean hasCheckableTimes = false;
		for (String key : CHECKABLE_FUNCTIONS) {
			if (thePolicyString.contains(key)) {
				hasCheckableTimes = true;
				break;
			}
		}
		if (hasCheckableTimes) {
			// check whether any caching obligations exist
			HashSet<Obligation> cacheObligations
					= new HashSet<Obligation>();
			HashSet<Obligation> otherObligations
					= new HashSet<Obligation>();
			for (Object oblObj : theResult.getObligations()) {
				Obligation obl = (Obligation) oblObj;
				if (PdpXacmlConstants.OBLIGATION_CACHE.getUrn()
						.equalsIgnoreCase(obl.getId().toString())) {
					cacheObligations.add(obl);
				} else {
					otherObligations.add(obl);
				}
			}
			DateTimeAttribute orig = theEvalCtx.getCurrentDateTime();
			/*
			 * check whether adding the cache obligation lifetime 
			 * PDP response changes the policy evaluation
			 */
			boolean foundBadObligation = false;
			long maxOblTime = 0L;
			for (Obligation cObl : cacheObligations) {
				for (Object attrObj : cObl.getAssignments()) {
					Attribute attr = (Attribute) attrObj;
					if (PdpXacmlConstants.OBLIGATION_CACHE_LIFETIME
							.getUrn().equalsIgnoreCase(attr.getType()
									.toString())) {
						long cTime = Long.valueOf(
								attr.getValue().toString()).longValue();
						if (cTime > maxOblTime) {
							maxOblTime = cTime;
						}
						DateTimeAttribute soon = new DateTimeAttribute(
								new Date(evalTime + cTime),	0, 0, 0);
						theEvalCtx.setCurrentDateTime(soon);
						Result futResult = thePolicy.evaluate(theEvalCtx);
						theEvalCtx.setCurrentDateTime(orig);
	        			// if it does not match, set cache time to zero
	        			if (theResult.getDecision() 
	        					!= futResult.getDecision()) {
	        				otherObligations.add(getCacheObligation(
	        						theResult.getDecision(), "0"));
	        				foundBadObligation = true;
	        				break;
	        			} 
					}
				}
			} 
			if (foundBadObligation) {
				/*
				 * No caching was already set above, return a result 
				 * without any policy-specified caching obligations
				 */
 				otherObligations.add(getCacheObligation(
 						theResult.getDecision(), 
							Long.toString(sessionLifetime)));
 				return new Result(theResult.getDecision(),
 						theResult.getStatus(), otherObligations);
 			/*
 			 * Only makes sense if we have not already implicitly 
 			 * checked the sessionLifetime
 			 */
 			} else if (maxOblTime < sessionLifetime) {
				/*
				 *  check whether adding the session lifetime 
				 *  changes the policy evaluation 
				 */
				DateTimeAttribute soon = new DateTimeAttribute(
						new Date(System.currentTimeMillis() 
								+ sessionLifetime),	0, 0, 0);
				theEvalCtx.setCurrentDateTime(soon);
				Result futResult = thePolicy.evaluate(theEvalCtx);
				theEvalCtx.setCurrentDateTime(orig);
				// if decisions do no match and there are no obligations
				if ((theResult.getDecision() != futResult.getDecision()) 
						&& (cacheObligations.isEmpty())) {     							 
					otherObligations.add(getCacheObligation(
							theResult.getDecision(), "0"));
					return new Result(theResult.getDecision(),
							theResult.getStatus(), otherObligations);
				/*
				 * we implicitly keep obligations in the policy here;
				 * those policies have been checked and are working if 
				 * this code is visited
				 */
    			} else if (cacheObligations.isEmpty()) {
    				otherObligations.add(getCacheObligation(
    						theResult.getDecision(), 
							Long.toString(sessionLifetime)));
    				return new Result(theResult.getDecision(),
    						theResult.getStatus(), 
							otherObligations);            					
    			}     			
			}
		}
		return theResult;
    }
    
    /**
     * @param theDecision
     * 				the access decision
     * @param theLifetime
     * 				the lifetime as a <code>String</code>
     * @return
     * 				the {@link Obligation}
     */
    private Obligation getCacheObligation(int theDecision, String theLifetime) {
    		if ((theDecision != Result.DECISION_PERMIT) 
    				|| (theDecision != Result.DECISION_DENY)) {
    			return null;
    		}    	
    		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
			Attribute attr = null;
			try {
				attr = new Attribute(new URI(
						PdpXacmlConstants.OBLIGATION_CACHE_LIFETIME.getUrn()),
						null,
						new DateTimeAttribute(),
						new StringAttribute(theLifetime));
				attrs.add(attr);
				Obligation obli = new Obligation(new URI(
						PdpXacmlConstants.OBLIGATION_CACHE.getUrn()), 
						theDecision, attrs);
				return obli;
			} catch (URISyntaxException use) {
				logger.error("URISyntaxException while creating obligation: "
						+ use.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", use);
				}
			}
			return null;
    }

}
