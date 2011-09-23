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
package eu.linksmart.policy.pep.impl;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * <p>PEP XACML constants enumeration</p>
 * 
 * @author Marco Tiemann
 *
 */
public enum PepXacmlConstants {

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
			"urn:linksmart:1.0:policy:obligation:cache:lifetime"),
	/** LinkSmart debug obligation */
	OBLIGATION_DEBUG(
			"urn:linksmart:1.0:policy:obligation:debug"),
	/** LinkSmart debug return applied policy name obligation */
	OBLIGATION_DEBUG_POLICY_NAME(
			"urn:linksmart:1.0:policy:obligation:debug:policy:name"),
	/** LinkSmart send mail obligation */
	OBLIGATION_SEND_MESSAGE(
			"urn:linksmart:1.0:policy:obligation:sendmessage"),
	/** LinkSmart send mail obligation */
	OBLIGATION_SEND_MESSAGE_FROM(
			"urn:linksmart:1.0:policy:obligation:sendmessage:from"),
	/** LinkSmart send mail obligation */
	OBLIGATION_SEND_MESSAGE_TO(
			"urn:linksmart:1.0:policy:obligation:sendmessage:to"),			
	/** LinkSmart send mail obligation */
	OBLIGATION_SEND_MESSAGE_SUBJECT(
			"urn:linksmart:1.0:policy:obligation:sendmessage:subject"),
	/** LinkSmart send mail obligation */
	OBLIGATION_SEND_MESSAGE_MESSAGE(
			"urn:linksmart:1.0:policy:obligation:sendmessage:content"),
	/** LinkSmart dispatch event obligation */
	OBLIGATION_DISPATCH_EVENT(
			"urn:linksmart:1.0:policy:obligation:dispatchevent"),
	/** LinkSmart dispatch event key obligation */
	OBLIGATION_DISPATCH_EVENT_TOPIC(
			"urn:linksmart:1.0:policy:obligation:dispatchevent:topic"),
	/** LinkSmart dispatch event key obligation */
	OBLIGATION_DISPATCH_EVENT_KEY(
			"urn:linksmart:1.0:policy:obligation:dispatchevent:key"),
	/** LinkSmart dispatch event value obligation */
	OBLIGATION_DISPATCH_EVENT_VALUE(
			"urn:linksmart:1.0:policy:obligation:dispatchevent:value"),
	/** Access Permitted Event Topic */
	ACCESS_PERMITTED_EVENT_TOPIC(
			"pep/access-permitted"),
	/** Access Denied Event Topic */
	ACCESS_DENIED_EVENT_TOPIC(
			"pep/access-denied"),
	/** Access Event Resource HID key */
	ACCESS_EVENT_RESOURCE_HID_KEY(
			"pep/access/resource-hid"),
	/** Access Event Resource HID key */
	ACCESS_EVENT_SUBJECT_HID_KEY(
			"pep/access/subject-hid"),
	/** Access Event Resource HID key */
	ACCESS_EVENT_ACTION_ATTRS_KEY(
			"pep/access/action-attributes"),
	/** Access Event Method key */
	ACCESS_EVENT_METHOD_KEY(
			"pep/access/action-method");
	
	
	/** urn */
	private String urn;
	
	/**
	 * Constructor
	 * 
	 * @param theUrn
	 * 				the urn
	 */
	private PepXacmlConstants(String theUrn) {
		urn = theUrn;
	}
	
	/**
	 * @return
	 * 				the urn
	 */
	public String getUrn() {
		return urn;
	}
	
	/**
	 * @return
	 * 				the <code>URI</code>
	 */
	public URI getUri() {
		try {
			return new URI(getUrn());
		} catch (URISyntaxException use) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getUrn();
	}
	
}
