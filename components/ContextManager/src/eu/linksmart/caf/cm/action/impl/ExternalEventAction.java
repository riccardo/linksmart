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
package eu.linksmart.caf.cm.action.impl;

import java.util.Iterator;

import org.apache.log4j.Logger;


import eu.linksmart.caf.ActionConstants;
import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.action.ActionProcessor;
import eu.linksmart.caf.cm.action.ThenAction;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.impl.ContextManagerApplication;
import eu.linksmart.eventmanager.EventManagerPort;
import eu.linksmart.eventmanager.Part;

/**
 * Implemented {@link ActionProcessor} for publishing an Event to the Event
 * Manager<p>
 * 
 * Expects an attribute specifying the topic of the Event, can also accept the
 * PID of the Event Manager to publish to. If no Event Manager PID is provided,
 * then the default configured Event Manager PID is used.
 * 
 * {@link Parameter}s passed in the {@link ThenAction} are used as the
 * {@link Part}s of the Event to publish
 * 
 * @author Michael Crouch
 * 
 */
public class ExternalEventAction extends ActionProcessor {

	/** The Action ID handled by this {@link ActionProcessor} */
	public static final String ACTION_ID =
			ActionConstants.EXTERNAL_EVENT_ACTION;

	/** the {@link Logger} */
	private static final Logger logger =
			Logger.getLogger(ExternalEventAction.class);

	/** the {@link ContextManagerApplication} */
	private ContextManagerApplication cm;

	/**
	 * Constructor for the Action, passing the {@link CmManagerHub}
	 * 
	 * @param managers
	 *            the {@link CmManagerHub}
	 */
	public ExternalEventAction(CmManagerHub managers) {
		this.cm = managers.getCmApp();
	}

	@Override
	public boolean canProcessAction(String actionType) {
		return actionType.equalsIgnoreCase(ACTION_ID);
	}

	/**
	 * Retrieves the HID for the Event Manager to publish to, from the Network
	 * Manager, then publishes the Event.
	 */
	@Override
	public boolean processAction(ThenAction action) {
		try {

			String topic =
					action.getAttribute(ActionConstants.EXTERNAL_EVENT_TOPIC);
			if (topic == null) {
				logger.error("Event Topic ("
						+ ActionConstants.EXTERNAL_EVENT_TOPIC + ") is NULL");
				return false;
			}

			EventManagerPort em = cm.getEventManager();
			if (em == null) {
				logger.error("No Event Manager found!");
				return false;
			}

			// Publish Event
			int partsSize = action.getParameters().size();
			if (hasHID(action))
				partsSize++;

			Part[] parts = new Part[partsSize];
			Iterator<Parameter> it = action.getParameters().listIterator();
			int cnt = 0;
			while (it.hasNext()) {
				Parameter param = it.next();
				parts[cnt] = new Part(param.getName(), param.getValue());
				cnt++;
			}
			if (cnt < partsSize) {
				logger.info("Fired event doesn't contain an 'HID' part. " 
					+ "Adding 'HID' part, with the hid of the ContextManager");
				parts[cnt] = new Part("HID", cm.getHid());
			}
			logger.info("Firing External event '" + topic + "'");
			em.publish(topic, parts);
			return true;
		} catch (Exception e) {
			logger.error("Error calling EventManager: "
					+ e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * Determines whether the {@link ThenAction} already contains an
	 * {@link Attribute} with the Id "HID"
	 * 
	 * @param action
	 *            the {@link ThenAction}
	 * @return result
	 */
	private boolean hasHID(ThenAction action) {
		Iterator<Parameter> it = action.getParameters().iterator();
		while (it.hasNext()) {
			if (it.next().getName().equals("HID"))
				return true;
		}
		return false;
	}

}
