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
package eu.linksmart.policy.pep.response;

import com.sun.xacml.Obligation;
import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pep.request.impl.PepRequest;

/**
 * <p>Obligation handler interface</p>
 * 
 * <p>This is the generic interface for handlers that can be plugged into the 
 * {@link PepApplication} to satisfy {@link Obligation}s returned as the 
 * result of XACML policy evaluations.</p>
 * 
 * @author Marco Tiemann
 * 
 */
public interface PepObligationObserver {

	/**
	 * <p>Evaluates an {@link Obligation}</p>
	 * 
	 * <p>This method evaluates the argument {@link Obligation} only. 
	 * <code>Obligation</code>s provided as part of the 
	 * {@link ResponseCtx} argument may be used to satisfy the argument 
	 * <code>Obligation</code>.</p> 
	 * 
	 * <p>Depending on the implementation of obligation handling, it may be 
	 * sufficient to have only one <code>PepObligationObserver</code> evaluate 
	 * an <code>Obligation</code> successfully. To facilitate this, an 
	 * <code>PepObligationObserver</code> instance returns a <code>boolean</code> 
	 * flag indicating whether it has successfully handled an argument 
	 * <code>Obligation</code>.</p>
	 * 
	 * @param theObligation
	 * 				the {@link Obligation} to evaluate
	 * @param theRequest
	 * 				the {@link PepRequest}
	 * @param theResponse
	 * 				the response {@link ResponseCtx}
	 * @return
	 * 				a flag indicating whether the obligation has been handled
	 */
	public boolean evaluate(Obligation theObligation, PepRequest theRequest, 
			ResponseCtx theResponse);
	
}
