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
package eu.linksmart.caf.cm.event;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;


import eu.linksmart.caf.cm.engine.contexts.BaseContext;
import eu.linksmart.caf.cm.engine.event.EventInstance;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.managers.RuleEngine;
import eu.linksmart.eventmanager.Part;

/**
 * Implemented {@link IEventHandler} to listen for events fired from the Policy
 * Enforcement Point regarding access attempts to LinkSmart entities.<p> Events are
 * handled by inserting them into the {@link RuleEngine} with the resource's
 * context as the source of the event.
 * 
 * @author Michael Crouch
 * 
 */
public class PolicyAccessEventHandler implements IEventHandler {

	/** the {@link Logger} */
	private static final Logger logger =
			Logger.getLogger(PolicyAccessEventHandler.class);

	/** Access Permitted Event Topic */
	private static final String ACCESS_PERMITTED_EVENT_TOPIC =
			"pep/access-permitted";
	/** Access Denied Event Topic */
	private static final String ACCESS_DENIED_EVENT_TOPIC = "pep/access-denied";
	/** Access Event Resource HID key */
	private static final String ACCESS_EVENT_RESOURCE_HID_KEY =
			"pep/access/resource-hid";
	/** Access Event Resource HID key */
	private static final String ACCESS_EVENT_SUBJECT_HID_KEY =
			"pep/access/subject-hid";
	/** Access Event Method key */
	private static final String ACCESS_EVENT_METHOD_KEY =
			"pep/access/action-method";

	/**
	 * {@link Set} of event topics handled
	 */
	private static Set<String> handledTopics;

	/** the {@link RuleEngine} */
	private RuleEngine ruleEngine;

	static {
		handledTopics = new HashSet<String>();
		handledTopics.add(ACCESS_PERMITTED_EVENT_TOPIC);
		handledTopics.add(ACCESS_DENIED_EVENT_TOPIC);
	}

	@Override
	public Set<String> getHandledTopics() {
		return handledTopics;
	}

	@Override
	public boolean canHandleEvent(String topic) {
		if (handledTopics.contains(topic))
			return true;
		return false;
	}

	@Override
	public void handleEvent(String topic, Part[] parts) {
		
		logger.info("Access Event received: " + topic);
		/**
		 * Identifies source device / application context that had some reported
		 * access decision made, and post the relevant EventInstance *
		 */
		// Get source context
		String resourceHID = "";
		BaseContext rootCtx = null;
		for (Part part : parts) {
			if (part.getKey().equals(ACCESS_EVENT_RESOURCE_HID_KEY)) {
				resourceHID = part.getValue();
				rootCtx =
						ruleEngine.getContextByHid(BaseContext.class,
								resourceHID);
				break;
			}
		}

		if (rootCtx == null) {
			logger.error("No source ctx found for " + resourceHID);
			return;
		}
		EventInstance event = new EventInstance(topic, rootCtx);
		for (Part part : parts) {
			event.addEventMember(part.getKey(), part.getValue());
		}
		ruleEngine.insert(event);
		ruleEngine.fireAllRules();
	}

	@Override
	public void register(CmManagerHub managers) {
		this.ruleEngine =
				(RuleEngine) managers.getManager(RuleEngine.MANAGER_ID);
	}

	@Override
	public void unregistering() {
		// Nothing to do
	}

}
