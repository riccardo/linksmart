/*
 * Using source code from Sun's com.sun.xacml.PDP.java
 * 
 * Copyright 2003-2004 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistribution of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * 
 *   2. Redistribution in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use in
 * the design, construction, operation or maintenance of any nuclear facility.
 */
package eu.linksmart.policy.pdp.impl;

import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.finder.PolicyFinderResult;
import com.sun.xacml.finder.ResourceFinderResult;
import com.sun.xacml.ParsingException;
import com.sun.xacml.EvaluationCtx;

import eu.linksmart.policy.pdp.ext.impl.LinkSmartEvaluationContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Policy Decision Point for LINK_SMART, based on the Sun XACML Implementation
 * Uses Source Code from Sun XACML 1.2 PDP.java; evaluates Request Contexts 
 * against Policies, returning a ResponseContext containing the decision</p>
 * 
 * <p>This abstract default LinkSmartPDP adding no extra functionality beyond the 
 * functionalities provided by the Sun reference implementation.</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 */
public abstract class AbstractLinkSmartPDP {

	/** {@link PdpDecisionConfig} */
    private PdpDecisionConfig config = null;
    
    /**
     * Returns {@link PdpDecisionConfig} reference
     * 
     * @return
     * 			{@link PdpDecisionConfig}	
     */
    public abstract PdpDecisionConfig getLinkSmartPDPConfig();  
    
    /**
     * Evaluates {@link RequestContext} coming from PEP; gets relevant policy, 
     * evaluates against the request, then returns a {@link ResponseContext} 
     * with the result
     * 
     * @param theReq
     * 				the {@link RequestCtx}
     * @return
     * 				the {@link ResponseCtx}
     */
    public ResponseCtx evaluate(RequestCtx theReq) {
        try {
            // create EvaluationContext from RequestContext            
        	config = getLinkSmartPDPConfig();         	
        	if  (config == null) {
        		config = new PdpDecisionConfig(null, null, null, null);
        	}                    
            EvaluationCtx evalCtx = new LinkSmartEvaluationContext(theReq, 
            		config.getAttributeFinder());
            // evaluate it
            ResponseCtx response = evaluate(evalCtx);
            // return result
            return response;
        } catch (ParsingException pe) {
        	/*
             * There was something wrong with the request, so we return
             * INDETERMINATE with a status of syntax error. This
             * may change if a more appropriate status type exists.
             */
            ArrayList<String> code = new ArrayList<String>();
            code.add(Status.STATUS_SYNTAX_ERROR);
            Status status = new Status(code, pe.getMessage());
            return new ResponseCtx(
            		new Result(Result.DECISION_INDETERMINATE, status));
        }
    }
    
    /**
     * @param theEvalCtx
     * 				the {@link EvaluationCtx}
     * @return
     * 				the {@link ResponseCtx}
     */
    @SuppressWarnings("unchecked")
	public ResponseCtx evaluate(EvaluationCtx theEvalCtx) {   
         // see if we need to call the resource finder
        if (theEvalCtx.getScope() != EvaluationCtx.SCOPE_IMMEDIATE) {
            AttributeValue parent = theEvalCtx.getResourceId();
            ResourceFinderResult resourceResult = null;
            if (theEvalCtx.getScope() == EvaluationCtx.SCOPE_CHILDREN) {
                resourceResult = config.getResourceFinder().findChildResources(
                		parent, theEvalCtx);
            } else {
                resourceResult = config.getResourceFinder()
                		.findDescendantResources(parent, theEvalCtx);
            }
            // see if we actually found anything
            if (resourceResult.isEmpty()) {
            	/*
            	 * The specification is not explicit regarding how to treat an 
            	 * empty result. We treat it as a processing error, but 
            	 * this may be changed in future versions.
            	 */
                ArrayList<String> code = new ArrayList<String>();
                code.add(Status.STATUS_PROCESSING_ERROR);
                String msg = "Couldn't find any resources to work on.";
                return new  ResponseCtx(
                		new Result(Result.DECISION_INDETERMINATE,
                				new Status(code, msg),
                                theEvalCtx.getResourceId().encode()));
            }
            // setup a set to keep track of the results
            HashSet<Result> results = new HashSet<Result>();
            /*
             * At this point we need to go through all the resources we
             * successfully found and collect results.
             */
            Iterator<AttributeValue> it 
            		= resourceResult.getResources().iterator();
            while (it.hasNext()) {
                // get the next resource, and set it in the EvaluationCtx
                AttributeValue resource = it.next();
                theEvalCtx.setResourceId(resource);
                // do the evaluation, and set the resource in the result
                Result result = evaluateContext(theEvalCtx);
                result.setResource(resource.encode());
                // add the result
                results.add(result);
            }
            /*
             * Now that we've done all the successes, we add all failures from 
             * the finder result
             */
            Map failures = resourceResult.getFailures();
            it = failures.keySet().iterator();
            while (it.hasNext()) {
                // get the next resource, and use it to get its Status data
                AttributeValue resource = it.next();
                Status status = (Status) failures.get(resource);
                // add a new result
                results.add(new Result(Result.DECISION_INDETERMINATE,
                                       status, resource.encode()));
            }
            // return the set of results
            return new ResponseCtx(results);
        }
        /*
         * The scope was IMMEDIATE (or missing), so we can just evaluate
         * the request and return whatever we get back
         */
        return new ResponseCtx(evaluateContext(theEvalCtx));
    }

    /**
     * A private helper routine that resolves a policy for the given 
     * context and then evaluates based on that policy
     * 
     * @param context
     * 				the {@link EvaluationCtx}
     * @return
     * 				the {@link Result}
     */
    private Result evaluateContext(EvaluationCtx context) {
        // first off, try to find a policy    	
    	PolicyFinderResult finderResult 
    			= config.getPolicyFinder().findPolicy(context);
        // see if there weren't any applicable policies
        if (finderResult.notApplicable()) {
            return new Result(Result.DECISION_NOT_APPLICABLE,
            		context.getResourceId().encode());
        }
        // see if there were any errors in trying to get a policy
        if (finderResult.indeterminate()) {
            return new Result(Result.DECISION_INDETERMINATE,
            		finderResult.getStatus(),
                    context.getResourceId().encode());
        }
        // we found a valid policy, so we can do the evaluation
        return finderResult.getPolicy().evaluate(context);
    }
    
}
