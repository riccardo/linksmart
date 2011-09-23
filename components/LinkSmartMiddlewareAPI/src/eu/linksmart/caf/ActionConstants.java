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
package eu.linksmart.caf;

import eu.linksmart.caf.cm.ContextManager;


/**
 * List of the constants related to each type of context-sensitive
 * action that can be defined by rules in the {@link ContextManager}
 * 
 * @author Michael Crouch
 *
 */
public class ActionConstants {
	
	/// The Actions
	
	/** The Action ID for a Web Service Call (through InsideLinkSmart communication) */
	public static final String INSIDE_LINK_SMART_CALL_ACTION = "cm:action:ws:InsideLinkSmartCall";
	
	/** The Action ID for a Logging action (to the Logger) */
	public static final String LOGGER_ACTION = "cm:action:Logger";
	
	/** The Action ID for a Drools Code Injection Action */
	public static final String DRL_CODE_INJ_ACTION = "cm:action:DroolsCodeInjection";
	
	/** The Action ID for an Internal Event action */
	public static final String INTERNAL_EVENT_ACTION = "cm:action:event:InternalEvent";;
	
	
	/** The Action ID for an External Event Action */
	public static final String EXTERNAL_EVENT_ACTION = "cm:action:event:ExternalEvent";;
	
	/// Inside LinkSmart Call Action Attributes
	
	/**
	 * The Attribute ID for the PID (Persistent Identifier) of the LinkSmart service
	 * to call
	 */
	public static final String WSCALL_PID = "target.PID";

	/**
	 * The Attribute ID for the SID (Service Identifier) of the LinkSmart service to
	 * call
	 */
	public static final String WSCALL_SID = "target.SID";

	/**
	 * The Attribute ID for the HID (LinkSmart Identifier) of the LinkSmart service to
	 * call
	 */
	public static final String WSCALL_HID = "target.HID";

	/** The Attribute ID for name of the method to call */
	public static final String WSCALL_METHOD = "ws.Method";

	/** The Attribute ID for namespace of the service to call */
	public static final String WSCALL_NAMESPACE = "ws.Namespace";

	/**
	 * The Attribute ID for SOAPAction of the service to call. If not passed,
	 * the method name is used instead
	 */
	public static final String WSCALL_SOAPACTION = "ws.SoapAction";
	
	/// Logger Action Attributes
	
	/** The Attribute ID for the scope to log the message to */
	public static final String LOGGER_SCOPE = "logger.Scope";
	
	/** The Attribute ID for the source to log the message from */
	public static final String LOGGER_SOURCE = "logger.Source";
	
	/// Internal Event Action Attributes
	
	/** The Attribute ID for the topic of the Event to be published internally */
	public static final String INTERNAL_EVENT_TOPIC = "internalEvent.Topic";

	/** The Attribute ID for the contextId of the source context */
	public static final String INTERNAL_EVENT_SOURCE = 
											"internalEvent.SourceContextId";
	
	/// Event Manager Event ActionAttributes
	
	/** The Attribute ID for the topic of the Event to publish */
	public static final String EXTERNAL_EVENT_TOPIC = "externalEvent.Topic";
	
}
