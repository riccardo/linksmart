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

/**
 * <p>Composite data type for LinkSmart session and PDP decision data</p> 
 * 
 * @author Marco Tiemann
 *
 */
public class PdpSessionItem implements Comparable<PdpSessionItem> {

	/** session ID */
	private String sessionId = null;
	
	/** method call */
	private String parameters = null;
	
	/** call timestamp */
	private Long timestamp = null;
	
	/** timeout value in milliseconds */
	private Long timeout = null;
	
	/** PDP decision {@link ResponseCtx} */
	private ResponseCtx decision = null;
	
	/** sender HID */
	private String sndHid = null;
	
	/** receiver HID */
	private String recHid  = null;
	
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
	public PdpSessionItem(String theSessionId, String theParamters) {
		super();
		sessionId = theSessionId;
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
	public PdpSessionItem(String theSessionId, String theParameters, 
			long theTimestamp) {
		this(theSessionId, theParameters);
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
	public PdpSessionItem(String theSessionId, String theParameters,
			ResponseCtx theDecision, long theTimestamp) {
		this(theSessionId, theParameters);
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
	public PdpSessionItem(String theSessionId, String theParameters,
			ResponseCtx theDecision, long theTimestamp, long theTimeout) {
		this(theSessionId, theParameters);
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
	public PdpSessionItem(String theSessionId, String theSndHid, 
			String theRecHid, String theParameters,
			ResponseCtx theDecision, long theTimestamp) {
		this(theSessionId, theParameters, theDecision, theTimestamp);
		sndHid = theSndHid;
		recHid = theRecHid;
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
	public PdpSessionItem(String theSessionId, String theSndHid, 
			String theRecHid, String theParameters,
			ResponseCtx theDecision, long theTimestamp, long theTimeout) {
		this(theSessionId, theParameters, theDecision, theTimestamp);
		sndHid = theSndHid;
		recHid = theRecHid;
		timeout = new Long(theTimeout);
	}

	/**
	 * @return
	 * 				the session ID
	 */
	public String getSessionId() {
		return sessionId;
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
	 * 				the sender HID
	 */
	public String getSenderHid() {
		return sndHid;
	}

	/**
	 * @return
	 * 				the receiver HID
	 */
	public String getReceiverHid() {
		return recHid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PdpSessionItem theRef) {	
		if (theRef == null) {
			return 1;
		}
		if (getSessionId() == null) {
			return (theRef.getSessionId() == null) ? (getParameters() == null) 
					? (theRef.getParameters() == null) 
							? 0 : -1 : (theRef.getParameters() == null) 
									? 1	: getParameters().compareTo(
											theRef.getParameters()) : -1;
		}
		if (theRef.getSessionId() == null) {
			return 1;
		}
		int r = getSessionId().compareTo(theRef.getSessionId());
		if (r != 0) {
			return r;
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
		if (recHid == null) {
			if (obj.recHid != null) {
				return false;
			}
		} else if (!recHid.equals(obj.recHid)) {
			return false;
		}
		if (sessionId == null) {
			if (obj.sessionId != null) {
				return false;
			}
		} else if (!sessionId.equals(obj.sessionId)) {
			return false;
		}
		if (sndHid == null) {
			if (obj.sndHid != null) {
				return false;
			}
		} else if (!sndHid.equals(obj.sndHid)) {
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
		result = prime * result + ((recHid == null) 
				? 0 : recHid.hashCode());
		result = prime * result + ((sessionId == null) 
				? 0 : sessionId.hashCode());
		result = prime * result + ((sndHid == null) 
				? 0 : sndHid.hashCode());
		result = prime * result + ((timestamp == null) 
				? 0 : timestamp.hashCode());
		return result;
	}
	
}
