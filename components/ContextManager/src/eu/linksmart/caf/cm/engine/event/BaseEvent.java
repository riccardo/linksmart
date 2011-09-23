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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import eu.linksmart.caf.cm.engine.Encodeable;
import eu.linksmart.caf.cm.engine.TimeService;
import eu.linksmart.caf.cm.engine.contexts.BaseContext;
import eu.linksmart.caf.cm.engine.contexts.Device;
import eu.linksmart.caf.cm.engine.members.EventMember;

/**
 * Base class / Fact Type for an Event. Contains a {@link Set} of
 * {@link EventMember}s, as well as the Topic of the event. The source context
 * of the event, is included with it's contextId and the {@link BaseContext}
 * object. <p>
 * 
 * Additionally, the Event may also have a Duration, such that it may be
 * interpreted by the Rule Engine for Complex Event Processing. If no duration
 * is specified, 0 is used - defining the Event as being a single point-in-time
 * Event.<p>
 * 
 * Implements {@link Encodeable} so that it can record the details of the Event
 * 
 * @author Michael Crouch
 * 
 */
public abstract class BaseEvent implements Encodeable {

	/** the contextId */
	private String contextId;

	/** the source {@link BaseContext} */
	private BaseContext source;

	/** the topic */
	private String topic;

	/** the {@link Set} of {@link EventMember}s */
	private Set<EventMember> members;

	/** the timestamp */
	private String timestamp;

	/** the duration of the Event (in ms) */
	private long eventDuration = 0;

	/**
	 * Constructor specifying the topic with the source {@link BaseContext}
	 * 
	 * @param topic
	 *            the topic of the Event
	 * @param source
	 *            the source {@link BaseContext}
	 */
	public BaseEvent(String topic, BaseContext source) {
		this(topic, getSourceContextId(source));
		this.source = source;

	}

	/**
	 * Constructor passing the topic and contextId of the source
	 * {@link BaseContext}
	 * 
	 * @param topic
	 *            the topic of the event
	 * @param contextId
	 *            the source contextId
	 */
	public BaseEvent(String topic, String contextId) {
		this.members = new HashSet<EventMember>();
		this.topic = topic;
		this.contextId = contextId;
		this.timestamp = TimeService.getInstance().getCurrentTimestamp();
	}

	/**
	 * Method to return the source contextId, checking whether the 
	 * source object is null. If so, the contextId is returned as ""
	 * @param source the source {@link BaseContext} (could be null)
	 * @return the contextId
	 */
	private static String getSourceContextId(BaseContext source){
		if (source ==  null)
			return "";
		return source.getContextId();
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
	 *            the topic to set
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * Gets the contextId of the Event
	 * 
	 * @return the contextId
	 */
	public String getContextId() {
		return contextId;
	}

	/**
	 * Sets the contextId of the Event
	 * 
	 * @param contextId
	 *            the contextId
	 */
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	/**
	 * Adds an {@link EventMember} to the Event
	 * 
	 * @param member
	 *            the {@link EventMember}
	 */
	public void addEventMember(EventMember member) {
		members.add(member);
	}

	/**
	 * Creates an {@link EventMember}, using the given id and value), and adds
	 * to the Event {@link EventMember}
	 * 
	 * @param id
	 *            the id of the {@link EventMember}
	 * @param value
	 *            the value of the {@link EventMember}
	 */
	public void addEventMember(String id, String value) {
		addEventMember(new EventMember(this, id, value, ""));
	}

	/**
	 * Creates an {@link EventMember}, using the given id, value and
	 * instanceOf), and adds to the Event {@link EventMember}
	 * 
	 * @param id
	 *            the id of the {@link EventMember}
	 * @param value
	 *            the value of the {@link EventMember}
	 * @param instanceOf
	 *            the instanceOf
	 */
	public void addEventMember(String id, String value, String instanceOf) {
		addEventMember(new EventMember(this, id, value, instanceOf));
	}

	/**
	 * Gets the {@link EventMember} with the given id
	 * 
	 * @param key
	 *            the key of the {@link EventMember} to get
	 * @return the {@link EventMember}
	 */
	public EventMember getEventMember(String key) {
		Iterator<EventMember> it = members.iterator();
		while (it.hasNext()) {
			EventMember member = it.next();
			if (member.getKey().equalsIgnoreCase(key))
				return member;
		}
		return null;
	}

	/**
	 * Gets the source {@link BaseContext}
	 * 
	 * @return the source {@link BaseContext}
	 */
	public BaseContext getSource() {
		return source;
	}

	/**
	 * Sets the source {@link BaseContext}
	 * 
	 * @param source
	 *            the source {@link BaseContext}
	 */
	public void setSource(BaseContext source) {
		this.source = source;
	}

	/**
	 * Gets the {@link Set} of {@link EventMember}s
	 * 
	 * @return the {@link Set} of {@link EventMember}s
	 */
	public Set<EventMember> getMembers() {
		return members;
	}

	/**
	 * Sets the {@link Set} of {@link EventMember}s
	 * 
	 * @param members
	 *            the {@link Set} of {@link EventMember}s
	 */
	public void setMembers(Set<EventMember> members) {
		this.members = members;
	}

	/**
	 * Gets the duration of the Event, in ms
	 * 
	 * @return the duration
	 */
	public long getEventDuration() {
		return eventDuration;
	}

	/**
	 * Sets the duration of the Event
	 * 
	 * @param eventDuration
	 *            the duration in ms
	 */
	public void setEventDuration(long eventDuration) {
		this.eventDuration = eventDuration;
	}

	/**
	 * Gets the timestamp
	 * 
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp
	 * 
	 * @param timestamp
	 *            the timestamp
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Inherits all the details of the passed {@link BaseEvent}, essentially
	 * cloning the Event
	 * 
	 * @param event
	 *            the {@link BaseEvent} to inherit details from
	 */
	public void inherit(BaseEvent event) {		
		this.contextId = event.getContextId();
		this.topic = event.getTopic();
		this.eventDuration = event.getEventDuration();
		this.source = event.getSource();
		this.members = new HashSet<EventMember>();
		Iterator<EventMember> it = event.getMembers().iterator();
		while(it.hasNext()){
			EventMember old = it.next();
			EventMember cloned = new EventMember(this, old.getKey(), old.getValue(), old.getValue());
			cloned.setTopic(old.getTopic());
			cloned.setInstanceOf(old.getInstanceOf());
			members.add(cloned);
		}
		this.timestamp = event.getTimestamp();
	}
	
	@Override 
	public String toString(){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		this.encode(out);
		return baos.toString();
	}

	@Override
	public void encode(PrintStream out) {

		out.println("<Event contextId=\"" + this.contextId + "\"  topic=\""
				+ this.topic + "\" timestamp=\"" + timestamp + "\" duration=\""
				+ this.eventDuration + "\">");

		Iterator<EventMember> it = this.members.iterator();
		while (it.hasNext()) {
			it.next().encode(out);
		}
		out.println("</Event>");
	}

}
