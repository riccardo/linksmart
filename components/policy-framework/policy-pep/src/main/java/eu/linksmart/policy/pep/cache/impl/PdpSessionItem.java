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

import org.wso2.balana.ctx.ResponseCtx;

import eu.linksmart.network.VirtualAddress;


/**
 * <p>Composite data type for LinkSmart session and PDP decision data</p> 
 * 
 * @author Marco Tiemann
 *
 */
public class PdpSessionItem implements Comparable<PdpSessionItem> {
	/** method call */
	private String parameters = null;
	
	/** call timestamp */
	private Long timestamp = null;
	
	/** timeout value in milliseconds */
	private Long timeout = null;
	
	/** PDP decision {@link ResponseCtx} */
	private ResponseCtx decision = null;
	
	/** sender VAD */
	private VirtualAddress sndVad = null;
	
	/** receiver VAD */
	private VirtualAddress recVad  = null;
	
	/**
	 * Constructor
	 * 
	 * @param theTimestamp
	 * 				the timestamp
	 */
	public PdpSessionItem(Long theTimestamp) {
		super();
		timestamp = theTimestamp;
	}
	
	/**
	 * Constructor
	 * 
	 * @param theSessionId
	 * 				the session ID
	 * @param theParamters
	 * 				the method call
	 */
	public PdpSessionItem(String theParamters) {
		super();
		parameters = theParamters;
	}
	
	/**
	 * Constructor
	 * 
	 * @param theSessionId
	 * 				the session ID
	 * @param theParameters
	 * 				the method call
	 * @param theTimestamp
	 * 				the timestamp
	 */
	public PdpSessionItem(String theParameters, 
			long theTimestamp) {
		this(theParameters);
		timestamp = new Long(theTimestamp);
	}
	
	/**
	 * Constructor
	 * 
	 * @param theSessionId
	 * 				the session ID
	 * @param theParameters
	 * 				the method call
	 * @param theDecision
	 * 				the PDP decision {@link ResponseCtx}
	 * @param theTimestamp
	 * 				the timestamp
	 */
	public PdpSessionItem(String theParameters,
			ResponseCtx theDecision, long theTimestamp) {
		this(theParameters);
		decision = theDecision;
		timestamp = new Long(theTimestamp);
	}
	
	/**
	 * Constructor
	 * 
	 * @param theSessionId
	 * 				the session ID
	 * @param theParameters
	 * 				the method call
	 * @param theDecision
	 * 				the PDP decision {@link ResponseCtx}
	 * @param theTimestamp
	 * 				the timestamp
	 * @param theTimeout
	 * 				the timeout value in milliseconds
	 */
	public PdpSessionItem(String theParameters,
			ResponseCtx theDecision, long theTimestamp, long theTimeout) {
		this(theParameters);
		decision = theDecision;
		timestamp = new Long(theTimestamp);
		timeout = new Long(theTimeout);
	}
	
	/**
	 * Constructor
	 * 
	 * @param theSessionId
	 * 				the session ID
	 * @param theSndHid
	 * 				the sender HID
	 * @param theRecHid
	 * 				the receiver HID
	 * @param theParameters
	 * 				the method call
	 * @param theDecision
	 * 				the PDP decision {@link ResponseCtx}
	 * @param theTimestamp
	 * 				the timestamp
	 */
	public PdpSessionItem(VirtualAddress theSndVad, 
			VirtualAddress theRecVad, String theParameters,
			ResponseCtx theDecision, long theTimestamp) {
		this(theParameters, theDecision, theTimestamp);
		sndVad = theSndVad;
		recVad = theRecVad;
	}
	
	/**
	 * Constructor
	 * 
	 * @param theSessionId
	 * 				the session ID
	 * @param theSndHid
	 * 				the sender HID
	 * @param theRecHid
	 * 				the receiver HID
	 * @param theParameters
	 * 				the method call
	 * @param theDecision
	 * 				the PDP decision {@link ResponseCtx}
	 * @param theTimestamp
	 * 				the timestamp
	 * @param theTimeout
	 * 				the timeout value in milliseconds
	 */
	public PdpSessionItem(VirtualAddress theSndVad, 
			VirtualAddress theRecVad, String theParameters,
			ResponseCtx theDecision, long theTimestamp, long theTimeout) {
		this(theParameters, theDecision, theTimestamp);
		sndVad = theSndVad;
		recVad = theRecVad;
		timeout = new Long(theTimeout);
	}
	

	/**
	 * @return
	 * 				the method call
	 */
	public String getParameters() {
		return parameters;
	}
	
	/**
	 * @return
	 * 				the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * @return
	 * 				the timeout value in milliseconds
	 */
	public Long getTimeout() {
		return timeout;
	}

	/**
	 * @param theTimestamp
	 * 				the timestamp
	 */
	public void setTimestamp(Long theTimestamp) {
		timestamp  = theTimestamp;
	}
	
	/**
	 * @return
	 * 				the PDP decision {@link ResponseCtx}
	 */
	public ResponseCtx getDecision() {
		return decision;
	}

	/**
	 * @param theDecision
	 * 				the PDP decision {@link ResponseCtx}
	 */
	public void setDecision(ResponseCtx theDecision) {
		decision = theDecision;
	}
	
	/**
	 * @return
	 * 				the sender Virtual Address
	 */
	public VirtualAddress getSenderVad() {
		return sndVad;
	}

	/**
	 * @return
	 * 				the receiver Virtual Address
	 */
	public VirtualAddress getReceiverVad() {
		return recVad;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PdpSessionItem theRef) {	
		if (theRef == null) {
			return 1;
		}
		return (getParameters() == null) ? (theRef.getParameters() == null) 
				? 0 : -1 : (theRef.getParameters() == null) 
						? 1 : getParameters().compareTo(theRef.getParameters());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object theObj) {
		if (this == theObj) {
			return true;
		}
		if (theObj == null) {
			return false;
		}
		if (getClass() != theObj.getClass()) {
			return false;
		}
		PdpSessionItem obj = (PdpSessionItem) theObj;
		if (decision == null) {
			if (obj.decision != null) {
				return false;
			}
		} else if (!decision.equals(obj.decision)) {
			return false;
		}
		if (parameters == null) {
			if (obj.parameters != null) {
				return false;
			}
		} else if (!parameters.equals(obj.parameters)) {
			return false;
		}
		if (recVad == null) {
			if (obj.recVad != null) {
				return false;
			}
		} else if (!recVad.equals(obj.recVad)) {
			return false;
		}
		if (sndVad == null) {
			if (obj.sndVad != null) {
				return false;
			}
		} else if (!sndVad.equals(obj.sndVad)) {
			return false;
		}
		if (timestamp == null) {
			if (obj.timestamp != null) {
				return false;
			}
		} else if (!timestamp.equals(obj.timestamp)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((decision == null) 
				? 0 : decision.hashCode());
		result = prime * result + ((parameters == null) 
				? 0 : parameters.hashCode());
		result = prime * result + ((recVad == null) 
				? 0 : recVad.hashCode());
		result = prime * result + ((sndVad == null) 
				? 0 : sndVad.hashCode());
		result = prime * result + ((timestamp == null) 
				? 0 : timestamp.hashCode());
		return result;
	}
	
}
