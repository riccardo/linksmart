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
package eu.linksmart.policy.pdp.ext.attribute.impl;

import com.sun.xacml.attr.AttributeProxy;

/**
 * <p>Represents a custom attribute to be used by the PDP</p>
 * 
 * <p>Actual attribute (extending {@link AttributeValue}) must be implemented, 
 * and an AttributeProxy for it added to this class along with the attribute 
 * identifier</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 */
public class CustomAttribute {

	/** identifier */
	private String identifier = null;
	
	/** {@link AttributeProxy} */
	private AttributeProxy proxy = null;
	
	/**
	 * Constructor
	 * 
	 * @param theIdentifier
	 * 				the identifier
	 */
	public CustomAttribute(String theIdentifier) {
		this(theIdentifier, null);
	}
	
	/**
	 * Constructor
	 * 
	 * @param theIdentifier
	 * 				the identifier
	 * @param theProxy
	 * 				the {@link AttributeProxy}
	 */
	public CustomAttribute(String theIdentifier, AttributeProxy theProxy) {
		identifier = theIdentifier;
		proxy = theProxy;
	}

	/**
	 * @return
	 * 				the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param theIdentifier
	 * 				the identifier
	 */
	public void setIdentifier(String theIdentifier) {
		identifier = theIdentifier;
	}

	/**
	 * @return
	 * 				the {@link AttributeProxy}
	 */
	public AttributeProxy getProxy() {
		return proxy;
	}

	/**
	 * @param theProxy
	 * 				the {@link AttributeProxy}
	 */
	public void setProxy(AttributeProxy theProxy) {
		proxy = theProxy;
	}	
	
}
