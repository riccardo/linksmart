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
package eu.linksmart.policy.pdp.impl;

/**
 * <p>XACML PDP constants</p> 
 * 
 * @author Marco Tiemann
 *
 */
public enum PdpXacmlConstants {

	/** XACML subject ID string */
	SUBJECT_SUBJECT_ID(
			"urn:oasis:names:tc:xacml:1.0:subject:subject-id"),
	/** LinkSmart subject PID */
	SUBJECT_SUBJECT_PID(
			"linksmart:policy:subject:pid"),
	/** LinkSmart generic subject prefix */
	SUBJECT_LINK_SMART_PREFIX(
			"linksmart:policy:subject:"),
			
	/** XACML resource ID string */
	RESOURCE_RESOURCE_ID(
			"urn:oasis:names:tc:xacml:1.0:resource:resource-id"),
	/** LinkSmart resource PID */
	RESOURCE_RESOURCE_PID(
			"linksmart:policy:resource:pid"),
	/** LinkSmart generic resource prefix */
	RESOURCE_LINK_SMART_PREFIX(
			"linksmart:policy:resource:"),
			
	/** XACML action ID string */
	ACTION_ACTION_ID(
			"urn:oasis:names:tc:xacml:1.0:action:action-id"),
	/** LinkSmart generic action prefix */
	ACTION_LINK_SMART_PREFIX(
			"linksmart:policy:action:"),
	
	/** LinkSmart generic function prefix */
	FUNCTION_LINK_SMART_PREFIX(
			"linksmart:policy:function:"),
			
	/** LinkSmart cache obligation */
	OBLIGATION_CACHE(
			"urn:linksmart:1.0:policy:obligation:cache"),
	/** LinkSmart cache obligation lifetime attribute */
	OBLIGATION_CACHE_LIFETIME(
			"urn:linksmart:1.0:policy:obligation:cache:lifetime");
	
	/** urn */
	private String urn;
	
	/**
	 * Constructor
	 * 
	 * @param theUrn
	 * 				the urn
	 */
	private PdpXacmlConstants(String theUrn) {
		urn = theUrn;
	}
	
	/**
	 * @return
	 * 				the urn
	 */
	public String getUrn() {
		return urn;
	}
	
}
