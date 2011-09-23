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
package eu.linksmart.policy.pep.response.impl;

import org.apache.log4j.Logger;

import com.sun.xacml.Obligation;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;

import eu.linksmart.policy.pep.impl.PepXacmlConstants;
import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.response.PepObligationObserver;
import eu.linksmart.policy.pep.response.impl.DebugPepObligationObserver;

/**
 * <p>Debug {@link PepObligationObserver} implementation</p>
 * 
 * <p>This {@link PepObligationObserver} can be used in combination with XACML 
 * policy obligations to track which policy has been applied when evaluating an 
 * access request.</p>
 * 
 * @author Marco Tiemann
 *
 */
public class DebugPepObligationObserver implements PepObligationObserver {

	/** logger */
	private static final Logger logger 
			= Logger.getLogger(DebugPepObligationObserver.class);
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.response.PepObligationObserver#evaluate(
	 * 		com.sun.xacml.Obligation, 
	 * 		eu.linksmart.policy.pep.request.PepRequest, 
	 * 		com.sun.xacml.ctx.ResponseCtx)
	 */
	@Override
	public boolean evaluate(Obligation theObligation, PepRequest theRequest,
			ResponseCtx theResponse) {
		if (PepXacmlConstants.OBLIGATION_DEBUG.getUrn()
				.equalsIgnoreCase(theObligation.getId().toString())) {
			for (Object obj : theObligation.getAssignments()) {
				Attribute attr = (Attribute) obj;
				if (PepXacmlConstants.OBLIGATION_DEBUG_POLICY_NAME.getUrn()
						.equalsIgnoreCase(attr.getId().toString())) {
					logger.info("Policy: " + attr.getValue().encode());
				} else {
					logger.info("Attribute id: " + attr.getId());
					logger.info("Attribute type: " + attr.getType().toString());
					logger.info("Attribute value: " + attr.getValue().encode());
				}
			}
			String s = (theObligation.getFulfillOn() < Result.DECISIONS.length) 
					? Result.DECISIONS[theObligation.getFulfillOn()] 
					                   : "unknown"; 
			logger.info("Fulfilled on: " + s);
			return true;
		}
		return false;
	}

}
