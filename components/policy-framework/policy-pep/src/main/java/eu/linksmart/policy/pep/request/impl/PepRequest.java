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

import java.util.List;
import java.util.Set;

import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.xacml3.Attributes;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.security.communication.SecurityProperty;

/**
 * <p>PEP request data POJO</p>
 * 
 * @author Marco Tiemann
 *
 */
public class PepRequest {
	
	/** action {@link Attributes} */
	private Attributes actionAttrs = null;
	
	/** action attribute string */
	private String actionAttrString = null;
	
	/** sender VAD */
	private VirtualAddress sndVad = null;
	
	/** receiver VAD */
	private VirtualAddress recVad = null;

	private Set<SecurityProperty> appliedSecurity;
	
	public Set<SecurityProperty> getAppliedSecurity() {
		return appliedSecurity;
	}

	/**
	 * Constructor
	 * 
	 * @param theAttributes
	 * 				the action {@link Attributes}
	 * @param theAttrString
	 * 				the action attribute string
	 * @param theSndVad
	 * 				the sender HID
	 * @param theSndCert
	 * 				the sender certificate reference
	 * @param theRecVad
	 * 				the receiver HID
	 * @param theRecCert
	 * 				the receiver certificate reference
	 * @param theSessionId
	 * 				the session ID
	 */
	public PepRequest(Attributes theAttributes, String theAttrString, 
			VirtualAddress theSndVad,  VirtualAddress theRecVad, Set<SecurityProperty> theAppliedSecurity) {
		super();
		actionAttrs = theAttributes;
		actionAttrString = theAttrString;
		sndVad = theSndVad;
		recVad = theRecVad;
		appliedSecurity = theAppliedSecurity;
	}

	/**
	 * @return
	 * 			the action {@link Attributes}
	 */
	public Attributes getActionAttrs() {
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
	public VirtualAddress getSndVad() {
		return sndVad;
	}

	/**
	 * @return
	 * 			the receiver HID
	 */
	public VirtualAddress getRecVad() {
		return recVad;
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
		result = prime * result + ((recVad == null) 
				? 0 : recVad.hashCode());
		result = prime * result + ((sndVad == null) 
				? 0 : sndVad.hashCode());
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
		if (recVad == null) {
			if (other.recVad != null) {
				return false;
			}
		} else if (!recVad.equals(other.recVad)) {
			return false;
		}
		if (sndVad == null) {
			if (other.sndVad != null) {
				return false;
			}
		} else if (!sndVad.equals(other.sndVad)) {
			return false;
		}
		if(appliedSecurity == null) {
			if(other.appliedSecurity != null) {
				return false;
			}
		} else if (!appliedSecurity.equals(other.appliedSecurity)) {
			return false;
		}
		return true;
	}
	
}
