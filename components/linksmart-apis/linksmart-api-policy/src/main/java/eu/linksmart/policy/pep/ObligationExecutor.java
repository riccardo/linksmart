package eu.linksmart.policy.pep;

//import org.wso2.balana.ObligationResult;
//import org.wso2.balana.ctx.AbstractRequestCtx;
//import org.wso2.balana.ctx.ResponseCtx;

public interface ObligationExecutor {
	
	/**
	 * Tries to fulfill the action requested in the obligation, given the information
	 * from the request and the response. For each obligation there has to be at least
	 * one ObligationExecutor that was able to fulfill the obligations, for a request 
	 * to be permitted. 
	 * @param obligation ObligationResult XACML XML string
	 * @param request Request XACML XML string
	 * @param response Evaluation response XACML XML string
	 * @return true if obligation was fulfilled, false else
	 */
	boolean evaluate(String obligation, String request, String response);
	
	
	/**
	 * Returns a string that distinguishes this ObligationExecutor from others.
	 * @return e.g. class name
	 */
	String getId();
}
