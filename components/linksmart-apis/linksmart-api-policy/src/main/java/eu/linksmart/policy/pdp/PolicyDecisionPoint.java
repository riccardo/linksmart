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
package eu.linksmart.policy.pdp;

import java.rmi.Remote;
import java.rmi.RemoteException;

//import org.wso2.balana.ctx.abstractrequestctx;
//import org.wso2.balana.ctx.responsectx;

/**
 * Policy Decision Point interface
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 * @see {@link LocalPolicyDecisionPoint}
 *
 */
public interface PolicyDecisionPoint extends Remote {
	
	/**
	 * Evaluates a request encoded as an XACML XML <code>String</code> and returns 
	 * the response as an XML <code>String</code> 
	 * 
	 * @param theRequest
	 * 				the request XML <code>String</code>
	 * @return
	 * 				the response XML <code>String</code>
	 * @throws RemoteException
	 * 				any {@link RemoteException} that is thrown
	 */
    public String evaluate(String theRequest) throws RemoteException;
    
}
