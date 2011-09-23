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
package eu.linksmart.caf.cm.managers;

import java.util.Iterator;

import org.apache.log4j.Logger;

import eu.linksmart.caf.cm.engine.contexts.Device;
import eu.linksmart.caf.cm.engine.event.EventInstance;
import eu.linksmart.caf.cm.engine.event.EventKeyMeta;
import eu.linksmart.caf.cm.engine.event.EventMeta;
import eu.linksmart.caf.cm.engine.members.ContextMember;
import eu.linksmart.caf.cm.engine.members.EventMember;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.impl.util.NumericHelper;

/**
 * Called from the RuleEngine to process and update Contexts
 * 
 * @author Michael Crouch
 * 
 */
public class ContextUpdateManager extends CmInternalManager {
	
	/** The Id for this Internal Manager */
	public static final String MANAGER_ID =
			"eu.linksmart.caf.cm.ContextUpdateManager";

	/** the {@link Logger} */
	private static final Logger logger = 
		Logger.getLogger(ContextUpdateManager.class);
	
	/** the {@link RuleEngine} */
	private RuleEngine ruleEngine;

	@Override
	public String getManagerId() {
		// TODO Auto-generated method stub
		return MANAGER_ID;
	}

	@Override
	public void initialise(CmManagerHub hub) {
		this.ruleEngine = (RuleEngine) hub.getManager(RuleEngine.MANAGER_ID);
	}

	@Override
	public void completedInit() {
		//Do nothing		
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	/**
	 * Contextualises the {@link EventInstance}, using determined attributes of
	 * the event from the {@link Device} description
	 * 
	 * @param event
	 *            the {@link EventInstance}
	 * @param device
	 *            the {@link Device}
	 */
	public void contextualiseEvent(EventInstance event, Device device) {
		// Get the associated DeviceEvent
		EventMeta de = getDeviceEvent(event, device);
		if (de == null)
			return;

		event.setEventDuration(de.getDuration());
	}

	/**
	 * Updates the given {@link Device} context, as a result of the
	 * {@link EventInstance} received from the actual device
	 * 
	 * @param event
	 *            the {@link EventInstance}
	 * @param device
	 *            the {@link Device}
	 */
	public void updateDeviceFromEvent(EventInstance event, Device device) {
		// Get the associated DeviceEvent
		EventMeta de = getDeviceEvent(event, device);
		if (de == null){
			logger.info("Could not find EventMeta for event '" 
					+ event.getTopic() + "' from device " 
					+ device.getDevicePid() + "/" + device.getHid() + "/" 
					+ device.getContextId() + ". Will attempt to process Parts " +
							"to ContextMembers");
			
			Iterator<EventMember> it = event.getMembers().iterator();
			while (it.hasNext()){
				EventMember eMember = it.next();
				if (eMember.getKey().equalsIgnoreCase("HID")){
					// do nothing
				}
				else {
					//don't have relatedStateVariable so try and match by key alone
					ContextMember cMember = device.getMember(eMember.getKey());
					if (cMember == null){
						String dataType = "String";
						if (NumericHelper.isNumericValue(eMember.getValue()))
							dataType = "Double";						
						cMember = new ContextMember(device, 
													eMember.getKey(), 
													eMember.getValue(), 
													dataType, 
													eMember.getKey());
						device.addMember(cMember);
						ruleEngine.insert(cMember);
						ruleEngine.update(device);
					}
					else {
						cMember.setStrValue(eMember.getValue());
						ruleEngine.update(cMember);
					}
				}
			}
			
			return;
		}
		
		/**
		 * Cycle through event parts, and add update the appropriate
		 * ContextMember
		 */
		Iterator<EventKeyMeta> it = de.getKeys().iterator();
		while (it.hasNext()) {
			EventKeyMeta key = it.next();
			EventMember eventMember = event.getEventMember(key.getName());
			ContextMember ctxMember = findMatchingMember(key, device);
			if ((eventMember != null) && (ctxMember != null)) {
				ctxMember.setStrValue(eventMember.getValue());
				/**
				 * Updates the ContextMember
				 */
				ruleEngine.update(ctxMember);
				
			}
		}
	}

	/**
	 * Performs the lookup to find the corresponding ContextMember of the
	 * {@link Device}, for the {@link EventKeyMeta}
	 * 
	 * @param key
	 *            the {@link EventKeyMeta}
	 * @param device
	 *            the {@link Device}
	 * @return the {@link ContextMember}
	 */
	private ContextMember findMatchingMember(EventKeyMeta key, Device device) {
		Iterator<ContextMember> it = device.getMembers().iterator();
		while (it.hasNext()) {
			ContextMember member = it.next();
			if ((member.getRelatedStateVariable() != null)
					&& (!"".equals(member.getRelatedStateVariable()))) {
				if (member.getRelatedStateVariable().equals(
						key.getRelatedStateVariable()))
					return member;
			}

			if (member.getKey().equals(key.getName()))
				return member;
		}
		return null;
	}

	/**
	 * Gets the {@link EventMeta} associated with the given
	 * {@link EventInstance} from the {@link Device} context
	 * 
	 * @param event
	 *            the {@link EventInstance}
	 * @param device
	 *            the {@link Device}
	 * @return the {@link EventMeta} found, or null
	 */
	private EventMeta getDeviceEvent(EventInstance event, Device device) {
		// Get the associated DeviceEvent
		Iterator<EventMeta> it = device.getDeviceEvents().iterator();
		while (it.hasNext()) {
			EventMeta de = it.next();
			if (de.getTopic().equals(event.getTopic())) {
				return de;
			}
		}
		return null;
	}
}
