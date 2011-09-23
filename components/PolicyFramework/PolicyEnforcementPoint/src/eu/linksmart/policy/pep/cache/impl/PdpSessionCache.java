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
package eu.linksmart.policy.pep.cache.impl;

import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pep.cache.PdpSessionMemory;

/**
 * <p>A cache for PDP responses to session/method call pairs</p>
 * 
 * <p>Data for the cache is stored and maintained by a {@link PdpSessionMemory},
 *  the present class encapsulates that class and handles all actions other than
 *  CRUD operations and the removal of expired session data.</p>
 * 
 * @author Marco Tiemann
 * 
 */
public class PdpSessionCache {
	
	/** {@link PdpSessionMemory} */
	private PdpSessionMemory sessionMemory = null;
	
	/** No-args constructor */
	public PdpSessionCache() {
		super();
		// default memory implementation
		sessionMemory = new MemPdpSessionMemory();
	}
	
	/**
	 * @return
	 * 				the {@link PdpSessionMemory}
	 */
	public PdpSessionMemory getSessionMemory() {
		return sessionMemory;
	}
	
	/**
	 * @param theSessionMemory
	 * 				the {@link PdpSessionMemory}
	 */
	public void setSessionMemory(PdpSessionMemory theSessionMemory) {
		sessionMemory = theSessionMemory;
	}
	
	/**
	 * @param theSessionId
	 * 				the session ID
	 * @param theParameters
	 * 				the method call
	 * @param theDecision
	 * 				the PDP decision
	 * @param theTimestamp
	 * 				the time stamp
	 */
	public void add(String theSessionId, String theParameters, 
			ResponseCtx theDecision, long theTimestamp) {
		PdpSessionItem ntry = new PdpSessionItem(theSessionId, 
				theParameters, theDecision, theTimestamp);
		sessionMemory.add(ntry);
	}
	
	/**
	 * @param theSessionId
	 * 				the session ID
	 * @param theParameters
	 * 				the method call
	 * @param theDecision
	 * 				the PDP decision {@link ResponseCtx}
	 * @param theTimestamp
	 * 				the time stamp
	 * @param theTimeout
	 * 				the timeout value in milliseconds
	 */
	public void add(String theSessionId, String theParameters, 
			ResponseCtx theDecision, long theTimestamp, long theTimeout) {
		PdpSessionItem ntry = new PdpSessionItem(theSessionId, 
				theParameters, theDecision, theTimestamp, theTimeout);
		sessionMemory.add(ntry);
	}
	
	/**
	 * @param theSessionId
	 * 				the session ID
	 * @param sndHid
	 * 				the sender HID
	 * @param recHid
	 * 				the receiver HID
	 * @param theParameters
	 * 				the method call
	 * @param theDecision
	 * 				the PDP decision {@link ResponseCtx}
	 * @param theTimestamp
	 * 				the time stamp
	 */
	public void add(String theSessionId, String sndHid, String recHid, 
			String theParameters, ResponseCtx theDecision, long theTimestamp) {
		PdpSessionItem ntry = new PdpSessionItem(theSessionId, sndHid, 
				recHid, theParameters, theDecision, theTimestamp);
		sessionMemory.add(ntry);
	}
	
	/**
	 * @param theSessionId
	 * 				the session ID
	 * @param sndHid
	 * 				the sender HID
	 * @param recHid
	 * 				the receiver HID
	 * @param theParameters
	 * 				the method call
	 * @param theDecision
	 * 				the PDP decision {@link ResponseCtx}
	 * @param theTimestamp
	 * 				the time stamp
	 * @param theTimeout
	 * 				the timeout value in milliseconds
	 */
	public void add(String theSessionId, String sndHid, String recHid, 
			String theParameters, ResponseCtx theDecision, long theTimestamp,
			long theTimeout) {
		PdpSessionItem ntry = new PdpSessionItem(theSessionId, sndHid, 
				recHid, theParameters, theDecision, theTimestamp, theTimeout);
		sessionMemory.add(ntry);
	}
	
	/**
	 * @param theSessionId
	 * 				the session ID
	 * @param theParameters
	 * 				the method call
	 * @param theTimestamp
	 * 				the time stamp
	 * @return
	 * 				the stored decision or <code>null</code> if no match found
	 */
	public ResponseCtx evaluate(String theSessionId, String theParameters, 
			long theTimestamp) {
		PdpSessionItem ntry = sessionMemory.find(theSessionId, 
				theParameters, theTimestamp);
		if (ntry != null) {			
			return ntry.getDecision();
		}
		return null;
	}
	
	/**
	 * @param theSessionId
	 * 				the session ID
	 * @param theSndHid
	 * 				the sender HID
	 * @param theRecHid
	 * 				the receiver HID
	 * @param theParameters
	 * 				the method call
	 * @param theTimestamp
	 * 				the time stamp
	 * @return
	 * 				the stored decision or <code>null</code> if no match found
	 */
	public ResponseCtx evaluate(String theSessionId, String theSndHid, 
			String theRecHid, String theParameters, long theTimestamp) {
		PdpSessionItem ntry = sessionMemory.find(theSessionId, 
				theParameters, theTimestamp);
		if ((ntry != null) && (ntry.getSenderHid() != null) 
				&& (ntry.getReceiverHid() != null)
				&& (theSndHid != null) && (theRecHid != null)
				&& (theSndHid.equals(ntry.getSenderHid())) 
				&& (theRecHid.equals(ntry.getReceiverHid()))) {
			return ntry.getDecision();
		}
		return null;
	}
	
}
