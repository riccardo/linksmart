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

import java.io.PrintStream;


import eu.linksmart.caf.ActionConstants;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.action.ActionProcessor;
import eu.linksmart.caf.cm.action.ThenAction;
import eu.linksmart.caf.cm.engine.event.EventInstance;
import eu.linksmart.caf.cm.exceptions.RuleParserException;
import eu.linksmart.caf.cm.rules.Action;
import eu.linksmart.caf.cm.util.CmHelper;

/**
 * Implemented {@link ActionProcessor} for publishing a new
 * {@link EventInstance} into the Working Memory<p>
 * 
 * Expects an attribute specifying the topic and source (context) of the Event.
 * 
 * {@link Parameter}s passed in the {@link ThenAction} are used as the
 * {@link EventProperty}s of the {@link EventInstance}
 * 
 * @author Michael Crouch
 * 
 */
public class InternalEventAction extends ActionProcessor {

	/** The Action ID handled by this {@link ActionProcessor} */
	public static final String ACTION_ID =
			ActionConstants.INTERNAL_EVENT_ACTION;

	@Override
	public boolean canProcessAction(String actionType) {
		return actionType.equalsIgnoreCase(ACTION_ID);
	}

	/**
	 * Does nothing. {@link EventInstance} is created and inserted as part of
	 * the DRL code
	 */
	@Override
	public boolean processAction(ThenAction action) {

		// Internal event processing handled in "then" code
		return true;
	}

	/**
	 * Encodes the {@link Action} to create and insert the {@link EventInstance}
	 * object in the DRL code
	 */
	@Override
	public void encodeAction(Action action, String actionName, PrintStream out)
			throws RuleParserException {
		String topic =
				CmHelper.getAttributeValueWithId(
						ActionConstants.INTERNAL_EVENT_TOPIC, action
								.getAttributes());
		String source =
				CmHelper.getAttributeValueWithId(
						ActionConstants.INTERNAL_EVENT_SOURCE, action
								.getAttributes());
		if (source == null){
			source = "$this";
		}
		
		if (topic == null)
			throw new RuleParserException("Error encoding internal Event '"
					+ actionName + "': No topic specified (id = '"
					+ ActionConstants.INTERNAL_EVENT_TOPIC + "'", null);

		out.println("EventInstance " + actionName + " = new EventInstance("
				+ topic + ", " + source + ");");

		for (Parameter param : action.getParameters()) {
			out.println(actionName + ".addEventMember(\"" + param.getName()
					+ "\", " + param.getValue() + ");");
		}
		out.println("insert(" + actionName + ");");
	}

}
