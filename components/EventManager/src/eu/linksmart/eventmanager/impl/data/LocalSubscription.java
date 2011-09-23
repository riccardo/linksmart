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
 * Copyright (C) 2006-2010 
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
package eu.linksmart.eventmanager.impl.data;

import java.net.URL;
import java.util.Date;

import eu.linksmart.eventmanager.EventSubscriber;

import eu.linksmart.eventmanager.subscriber.client.EventSubscriberServiceLocator;

/**
 * 
 * This class represents a subscription. A subscription is defined 
 * by a topic, a subscriber, an URL of the subscriber and a Date.
 *
 */

public class LocalSubscription {
	
	/**
	 * <b>topic</b> : The topic name of this LocalDescription.
	 */
	private String topic;
	private int counter;
	
	/**
	 * <b>port</b> : The subscriber of this subscription.
	 */
	private EventSubscriber port;
	
	/**
	 * <b>subscriberURL</b> : URL of the subscriber.
	 */
	private URL subscriberURL;
	
	
	private String subscriberHID; 
	/**
	 * <b>date</b> : Date of the subscription.
	 */
	private Date date;

	/**
	 * <b>LocalSubscription Default Constructor</b>
	 */
	public LocalSubscription() {
		date = new Date();
		counter = 0;
	}

	/**
	 * <b>LocalSubscription Constructor</b>
	 * @param topic The subscription topic. 
	 * @param subscriberURL The subscriber URL.
	 */
	public LocalSubscription(String topic, URL subscriberURL) {
		this();
		this.topic = topic;
		this.subscriberURL = subscriberURL;
		this.subscriberHID = null;
	}
	
	public LocalSubscription(String topic, URL subscriber, String hid) {
		this();
		this.topic = topic;
		this.subscriberURL = subscriber;
		this.subscriberHID = hid;
	}

	/**
	 * <b>equals</b>
	 * Compares the topic of the given LocalSubscription with the topic of this LocalSubscription
	 * and returns true if the topics match, false if not.
	 * @param other LocalSubscription.
	 * @return true if the topic of the given LocalSubscription. 
	 * matches the topic of this LocalSubscription, false if not.
	 *
	 */
	public boolean equals(Object other) {
		boolean result = false;
		if (other != null) {
			if (other instanceof LocalSubscription) {
				LocalSubscription otherSubscriber = (LocalSubscription) other;
				result = topic.equals(otherSubscriber.getTopic());
			}
		}
		return result;
	}

	/**
	 * <b>matches</b>
	 * Returns true if the given topic matches the topic of this LocalSubscription
	 * @param topic String specifying a topic.
	 * @return true if the given topic matches the topic of this LocalSubscription, false if not.
	 */
	public boolean matches(String topic) {
		return topic.matches(getTopic());
	}

	/**
	 * <b>setTopic</b>
	 * Sets the topic of this LocalSubscription to the given value.
	 * @param topic new Topic.
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * <b>getTopic</b>
	 * Returns the topic of this subscription.
	 * @return topic field of this subscription.
	 */
	public String getTopic() {
		return topic;
	}

	/*
	 * "_": to prevent JAX-WS from serializing "port"
	 */
	/**
	 * <b>_setPort</b>
	 * Sets the EventSubscriber of this LocalSubscription to the given value.
	 * @param port new EventSubscriber.
	 */
	public void _setPort(EventSubscriber port) {
		this.port = port;
	}

	/**
	 * <b>_getPort</b>
	 * Returns the EventSubscriber of this LocalSubscription.
	 * @return EventSubscriber of this LocalSubscription.
	 */
	public EventSubscriber _getPort() {
		if (port == null) {
			try {
				port =new EventSubscriberServiceLocator().getEventSubscriber(getURL());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return port;
	}

	/**
	 * <b>setURL</b>
	 * Sets the subscriber URL to the given value.
	 * @param subscriberURL new subscriber URL.
	 */
	public void setURL(URL subscriberURL) {
		this.subscriberURL = subscriberURL;
	}

	/**
	 * <b>getURL</b>
	 * Returns the subscriberURL of this LocalSubscription.
	 * @return URL for the subscriber URL.
	 */
	public URL getURL() {
		return subscriberURL;
	}
	
	public void setHID(String newHID) {
		this.subscriberHID = newHID;
	}
	
	public String getHID() {
		return this.subscriberHID;
	}

	/**
	 * <b>setDate</b>
	 * Sets the date of this LocalSubscription to the given value.
	 * @param date new Date for the subscription.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * <b>getDate</b>
	 * Returns the date of this subscription.
	 * @return Date field of this LocalSubscription.
	 */
	public Date getDate() {
		return date;
	}
	
	public synchronized int increaseCounter() {
		counter ++;
		return counter;
	}
	public synchronized int getCounter() {
		return counter;
	}
}
