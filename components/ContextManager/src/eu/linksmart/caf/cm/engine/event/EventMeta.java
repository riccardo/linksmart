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
package eu.linksmart.caf.cm.engine.event;

import java.util.Set;

import eu.linksmart.caf.cm.engine.contexts.Device;

/**
 * Representation of an Event that can be thrown by a LinkSmart Entity
 * 
 * @author Michael Crouch
 * 
 */
public class EventMeta {

	/** the type of the event */
	private String type;

	/** the event topic */
	private String topic;

	/** the duration of the event */
	private long duration;

	/** the frequency of the event */
	private int frequency;

	/** the event trigger */
	private String trigger;

	/** the event description */
	private String description;

	/** {@link Set} of {@link EventKeyMeta} */
	private Set<EventKeyMeta> keys;

	/**
	 * Constructor
	 * 
	 * @param topic
	 *            the topic
	 */
	public EventMeta(String topic) {
		this.topic = topic;
	}

	/**
	 * Gets the topic
	 * 
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Sets the topic
	 * 
	 * @param topic
	 *            the topic
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * Gets the duration
	 * 
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Sets the duration
	 * 
	 * @param duration
	 *            the duration
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * Gets the type
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type
	 * 
	 * @param type
	 *            the type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the frequency
	 * 
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Sets the frequency
	 * 
	 * @param frequency
	 *            the frequency
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * Gets the trigger
	 * 
	 * @return the trigger
	 */
	public String getTrigger() {
		return trigger;
	}

	/**
	 * Sets the trigger
	 * 
	 * @param trigger
	 *            the trigger
	 */
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	/**
	 * Gets the description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description
	 * 
	 * @param description
	 *            the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the {@link Set} of {@link EventKeyMeta}s
	 * 
	 * @return the {@link Set} of {@link EventKeyMeta}s
	 */
	public Set<EventKeyMeta> getKeys() {
		return keys;
	}

	/**
	 * Sets the {@link Set} of {@link EventKeyMeta}s
	 * 
	 * @param keys
	 *            the {@link Set} of {@link EventKeyMeta}s
	 */
	public void setKeys(Set<EventKeyMeta> keys) {
		this.keys = keys;
	}

}
