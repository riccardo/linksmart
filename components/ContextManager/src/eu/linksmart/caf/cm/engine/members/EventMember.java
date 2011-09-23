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
package eu.linksmart.caf.cm.engine.members;

import java.io.PrintStream;

import eu.linksmart.caf.cm.engine.Encodeable;
import eu.linksmart.caf.cm.engine.event.BaseEvent;

/**
 * A key-value member associated with a {@link BaseEvent} event that may be
 * fired internally, or received from the EventManager.
 * 
 * @author Michael Crouch
 * 
 */
public class EventMember implements Encodeable {

	/** the parent {@link BaseEvent} */
	private BaseEvent event;

	/** the parent event topic */
	private String topic;

	/** the key */
	private String key;

	/** the value */
	private String value;

	/** the instanceOf */
	private String instanceOf;

	/**
	 * Constructor
	 * 
	 * @param event
	 *            the host {@link BaseEvent}
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @param instanceOf
	 *            the instanceOf (may be null)
	 */
	public EventMember(BaseEvent event, String key, String value,
			String instanceOf) {
		this.event = event;
		this.key = key;
		this.value = value;
		this.instanceOf = instanceOf;
		if (event != null)
			this.topic = event.getTopic();
	}

	/**
	 * Gets the hosting {@link BaseEvent}
	 * 
	 * @return the {@link BaseEvent}
	 */
	public BaseEvent getEvent() {
		return event;
	}

	/**
	 * Sets the host {@link BaseEvent}
	 * 
	 * @param event
	 *            the {@link BaseEvent}
	 */
	public void setEvent(BaseEvent event) {
		this.event = event;
	}

	/**
	 * Gets the associated Event topic
	 * 
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Sets the associated Event topic
	 * 
	 * @param eventTopic
	 *            the topic
	 */
	public void setTopic(String eventTopic) {
		this.topic = eventTopic;
	}

	/**
	 * Gets the key
	 * 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key
	 * 
	 * @param key
	 *            the key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * gets the Value
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value
	 * 
	 * @param value
	 *            the value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the instanceOf
	 * 
	 * @return the instanceOf
	 */
	public String getInstanceOf() {
		return instanceOf;
	}

	/**
	 * Sets the instanceOf
	 * 
	 * @param instanceOf
	 *            the instanceOf
	 */
	public void setInstanceOf(String instanceOf) {
		this.instanceOf = instanceOf;
	}

	@Override
	public void encode(PrintStream out) {
		out.println("<EventMember key=\"" + key + "\" instanceOf=\""
				+ instanceOf + "\">" + value + "</EventMember>");
	}

}
