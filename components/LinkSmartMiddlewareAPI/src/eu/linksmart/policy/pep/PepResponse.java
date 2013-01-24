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
package eu.linksmart.policy.pep;

/**
 * PEP response value object class for access decisions, details of incomplete 
 * request, details of request failure
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 *
 */
public class PepResponse {

	/** PERMIT decision */
	public static final int DECISION_PERMIT = 100;
	
	/** DENY decision */
	public static final int DECISION_DENY = 200;
	
	/** NO decision */
	public static final int DECISION_NONE = 300;
	
	/** OK request handling */
	public static final int CODE_OK = 0;
	
	/** Error while handling request */
	public static final int CODE_PEP_REQUEST_ERROR = 1;
	
	/** Indeterminate state while handling request */
	public static final int CODE_PDP_REQUEST_INDETERMINATE = 2;
	
	/** Policies not applicable to given request */
	public static final int CODE_PDP_REQUEST_NONAPPLICABLE = 3;
	
	/** PEP configuration error */
	public static final int CODE_PEP_CONFIG_ERROR = 3;
	
	/** decision */
	private int decision;
	
	/** status */
	private String status;
	
	/** error code */
	private int errorCode;
	
	/** error message */
	private String errorMsg;
	
	/**
	 * Constructor
	 * 
	 * @param theDecision
	 * 				the decision
	 * @param theStatus
	 * 				the status
	 * @param theErrorCode
	 * 				the error code
	 * @param theErrorMsg
	 * 				the error message
	 */
	public PepResponse(int theDecision, String theStatus, int theErrorCode, 
			String theErrorMsg) {
		decision = theDecision;
		status = theStatus;
		errorCode = theErrorCode; 
		errorMsg = theErrorMsg;
	}
	
	/**
	 * @return
	 * 				the decision
	 */
	public int getDecision() {
		return decision;
	}

	/**
	 * @return
	 * 				the status
	 */	
	public String getStatus() {
		return status;
	}

	/**
	 * @return
	 * 				the error code
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @return
	 * 				the error message
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param theDecision
	 * 				the decision
	 * @param theStatus
	 * 				the status
	 * @param theErrorCode
	 * 				the error code
	 * @param theErrorMsg
	 * 				the error message
	 * @return
	 * 				the {@link PepResponse} instance
	 */
	public static PepResponse getInstance(int theDecision, String theStatus, 
			int theErrorCode, String theErrorMsg) {
		return new PepResponse(theDecision, theStatus, theErrorCode, 
				theErrorMsg);
	}
	
}
