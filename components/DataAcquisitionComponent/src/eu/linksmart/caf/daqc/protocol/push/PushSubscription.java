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
package eu.linksmart.caf.daqc.protocol.push;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

import eu.linksmart.caf.daqc.impl.DAqCApplication;
import eu.linksmart.caf.daqc.reporting.SubscriberFactory;
import eu.linksmart.caf.daqc.reporting.SubscriberStub;

import eu.linksmart.caf.daqc.subscription.Subscriber;
import eu.linksmart.eventmanager.Part;

/**
 * Represents the subscription to an Event Manager, for a particular topic, and
 * from a given event source (as denoted in the {@link Part}s of an event.<p>
 * Maintains a {@link Set} of {@link PushSubscriber}s to the event, to be
 * reported of its occurrence, each with their own data id.
 * 
 * @author Michael Crouch
 */
public class PushSubscription {

	/** {@link Set} of {@link PushSubscriber}s */
	private Set<PushSubscriber> subscribers;

	/** the topic of the event */
	private String eventTopic;

	/**
	 * Constructor
	 * 
	 * @param eventTopic
	 *            the topic of the Event
	 */
	public PushSubscription(String eventTopic) {
		this.eventTopic = eventTopic;
		subscribers = new HashSet<PushSubscriber>();
	}

	/**
	 * Gets the {@link Set} of {@link PushSubscriber}s
	 * 
	 * @return the {@link Set} of {@link PushSubscriber}s
	 */
	public Set<PushSubscriber> getSubscribers() {
		return subscribers;
	}

	/**
	 * Sets the {@link Set} of {@link PushSubscriber}s
	 * 
	 * @param subscribers
	 *            the {@link Set} of {@link PushSubscriber}s
	 */
	public void setSubscribers(Set<PushSubscriber> subscribers) {
		this.subscribers = subscribers;
	}

	/**
	 * Gets the {@link PushSubscriber}with the given HID
	 * 
	 * @param hid
	 *            the hid
	 * @return the {@link PushSubscriber}
	 */
	public PushSubscriber getSubscriber(String hid) {
		Iterator<PushSubscriber> it = subscribers.iterator();
		while (it.hasNext()) {
			PushSubscriber subscriber = it.next();
			if (subscriber.getSubscriberStub().matchHid(hid)) {
				return subscriber;
			}
		}
		return null;
	}

	/**
	 * Gets the {@link PushSubscriber}that has the given HID, as well as the
	 * given alias
	 * 
	 * @param hid
	 *            the hid to match
	 * @param alias
	 *            the alias to match
	 * @return
	 */
	public PushSubscriber getSubscriberWithHidAlias(String hid, String alias) {
		PushSubscriber subscriber = getSubscriber(hid);
		if (subscriber != null) {
			if (subscriber.hasAlias(alias))
				return subscriber;
		}
		return null;
	}

	/**
	 * Removes the {@link PushSubscriber} with the given HID
	 * 
	 * @param hid
	 *            the HID
	 * @return success
	 */
	public boolean removeSubscriber(String hid) {
		PushSubscriber subscriber = getSubscriber(hid);
		if (subscriber != null) {
			subscribers.remove(subscriber);
			return true;
		}
		return false;
	}

	/**
	 * Adds a {@link PushSubscriber} to the subscription.<p> The
	 * {@link PushSubscriber} is generated, by first generating the
	 * {@link SubscriberStub} for the given {@link Subscriber}, using the
	 * {@link SubscriberFactory}.
	 * 
	 * @param daqc
	 *            the {@link DAqCApplication} to be used by the
	 *            {@link SubscriberFactory}
	 * @param subscriber
	 *            the {@link Subscriber}
	 * @param alias
	 *            the alias
	 */
	public void addSubscriber(DAqCApplication daqc, Subscriber subscriber,
			String alias) {
		PushSubscriber pushSubscriber =
				getSubscriber(subscriber.getSubscriberHid());
		if (pushSubscriber != null) {
			if (!pushSubscriber.hasAlias(alias))
				pushSubscriber.addAlias(alias);
		} else {
			SubscriberStub stub =
					SubscriberFactory.generateSubscriberStub(daqc, subscriber);
			pushSubscriber = new PushSubscriber(stub, alias);
			subscribers.add(pushSubscriber);
		}
	}

	/**
	 * Gets the Event Topic
	 * 
	 * @return the topic
	 */
	public String getEventTopic() {
		return eventTopic;
	}

	/**
	 * Sets the Event Topic
	 * 
	 * @param eventTopic
	 *            the topic
	 */
	public void setEventTopic(String eventTopic) {
		this.eventTopic = eventTopic;
	}
}
