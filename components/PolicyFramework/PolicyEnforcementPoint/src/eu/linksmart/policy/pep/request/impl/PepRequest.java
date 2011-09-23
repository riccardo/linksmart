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
package eu.linksmart.policy.pep.request.impl;

import java.util.Set;

import com.sun.xacml.ctx.Attribute;

import eu.linksmart.policy.pep.request.impl.PepRequest;

/**
 * <p>PEP request data POJO</p>
 * 
 * @author Marco Tiemann
 *
 */
public class PepRequest {
	
	/** action {@link Attributes} */
	private Set<Attribute> actionAttrs = null;
	
	/** action attribute string */
	private String actionAttrString = null;
	
	/** sender HID */
	private String sndHid = null;
	
	/** sender certificate reference */
	private String sndCert = null;
	
	/** receiver HID */
	private String recHid = null;
	
	/** receiver certificate reference */
	private String recCert = null;
	
	/** session ID */
	private String sessionId = null;
	
	/**
	 * Constructor
	 * 
	 * @param theAttributes
	 * 				the action {@link Attributes}
	 * @param theAttrString
	 * 				the action attribute string
	 * @param theSndHid
	 * 				the sender HID
	 * @param theSndCert
	 * 				the sender certificate reference
	 * @param theRecHid
	 * 				the receiver HID
	 * @param theRecCert
	 * 				the receiver certificate reference
	 * @param theSessionId
	 * 				the session ID
	 */
	public PepRequest(Set<Attribute> theAttributes, String theAttrString, 
			String theSndHid, String theSndCert, String theRecHid, 
			String theRecCert, String theSessionId) {
		super();
		actionAttrs = theAttributes;
		actionAttrString = theAttrString;
		sndHid = theSndHid;
		sndCert = theSndCert;
		recHid = theRecHid;
		recCert = theRecCert;
		sessionId = theSessionId;
	}

	/**
	 * @return
	 * 			the action {@link Attributes}
	 */
	public Set<Attribute> getActionAttrs() {
		return actionAttrs;
	}
	
	/**
	 * @return
	 * 			the action attribute <code>String</code>
	 */
	public String getActionAttrString() {
		return actionAttrString;
	}

	/**
	 * @return
	 * 			the sender HID
	 */
	public String getSndHid() {
		return sndHid;
	}

	/**
	 * @return
	 * 			the sender certificate reference
	 */
	public String getSndCert() {
		return sndCert;
	}

	/**
	 * @return
	 * 			the receiver HID
	 */
	public String getRecHid() {
		return recHid;
	}

	/**
	 * @return
	 * 			the receiver certificate reference
	 */
	public String getRecCert() {
		return recCert;
	}

	/**
	 * @return
	 * 			the session ID
	 */
	public String getSessionId() {
		return sessionId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionAttrString == null) 
				? 0 : actionAttrString.hashCode());
		result = prime * result + ((actionAttrs == null) 
				? 0 : actionAttrs.hashCode());
		result = prime * result + ((recCert == null) 
				? 0 : recCert.hashCode());
		result = prime * result + ((recHid == null) 
				? 0 : recHid.hashCode());
		result = prime * result + ((sessionId == null) 
				? 0 : sessionId.hashCode());
		result = prime * result + ((sndCert == null) 
				? 0 : sndCert.hashCode());
		result = prime * result + ((sndHid == null) 
				? 0 : sndHid.hashCode());
		return result;
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
		PepRequest other = (PepRequest) theObj;
		if (actionAttrString == null) {
			if (other.actionAttrString != null) {
				return false;
			}
		} else if (!actionAttrString.equals(other.actionAttrString)) {
			return false;
		}
		if (actionAttrs == null) {
			if (other.actionAttrs != null) {
				return false;
			}
		} else if (!actionAttrs.equals(other.actionAttrs)) {
			return false;
		}
		if (recCert == null) {
			if (other.recCert != null) {
				return false;
			}
		} else if (!recCert.equals(other.recCert)) {
			return false;
		}
		if (recHid == null) {
			if (other.recHid != null) {
				return false;
			}
		} else if (!recHid.equals(other.recHid)) {
			return false;
		}
		if (sessionId == null) {
			if (other.sessionId != null) {
				return false;
			}
		} else if (!sessionId.equals(other.sessionId)) {
			return false;
		}
		if (sndCert == null) {
			if (other.sndCert != null) {
				return false;
			}
		} else if (!sndCert.equals(other.sndCert)) {
			return false;
		}
		if (sndHid == null) {
			if (other.sndHid != null) {
				return false;
			}
		} else if (!sndHid.equals(other.sndHid)) {
			return false;
		}
		return true;
	}
	
}
