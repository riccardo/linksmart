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
import java.util.LinkedList;
import java.util.List;



/**
 * Class that wraps a set of local subscriptions.
 */
public class LocalSubscriptions {
	
	static final long  serialVersionUID = -1L; 
	
	/**
	 * <b>subscriptions</b> : List containing LocalSubscriptions.
	 */
	List<LocalSubscription> subscriptions;
	
	/**
	 * <b>LocalSubscriptions Constructor</b>
	 */
	public LocalSubscriptions() {
		// No-argument constructor needed by JAX-WS
		subscriptions = new LinkedList<LocalSubscription>();
	}

	/**
	 * <b>setSubscriptions</b>
	 * Sets the list of subscriptions to the given value.
	 * @param subscriptions list to set as the list of subscriptions.
	 */
	public void setSubscriptions(List<LocalSubscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	/**
	 * <b>getSubscriptions</b>
	 * Returns the List of subscriptions. 
	 * @return List of subscriptions.
	 */
	public List<LocalSubscription> getSubscriptions() {
		return subscriptions;
	}
	
	/**
	 * <b>getMatches</b>
	 * Returns a list of LocalSubscriptions that match with the given topic pattern.
	 * @param topicPattern String defining a topic pattern.
	 * @return List of LocalSubscriptions.
	 */
	public List<LocalSubscription> getMatches(String topicPattern) {
		List<LocalSubscription> result = new LinkedList<LocalSubscription>();
		for (LocalSubscription subscription: subscriptions) {
			if (subscription.matches(topicPattern)) {
				result.add(subscription);
			}
		}
		return result;
	}
	
	/**
	 * <b>getSubscriptionsByTopicAndURL</b>
	 * Returns a List of LocalSubscriptions that match with the given topic and URL.
	 * @param topic String defining a topic. 
	 * @param url URL 
	 * @return List of LocalSubscriptions.
	 */
	private List<LocalSubscription> getSubscriptionsByTopicAndURL(String topic, URL url) {
		List<LocalSubscription> result = new LinkedList<LocalSubscription>();
		for (LocalSubscription subscription : subscriptions) {
	        if (subscription.getTopic().equals(topic) 
	        		&& subscription.getURL()!= null && subscription.getURL().equals(url)) {
				result.add(subscription);
			}
		}
		return result;
	}
	
	private List<LocalSubscription> getSubscriptionsByTopicAndHID(String topic, String HID) {
		List<LocalSubscription> result = new LinkedList<LocalSubscription>();
		for (LocalSubscription subscription : subscriptions) {
	        if (subscription.getTopic().equals(topic) 
	        		&& subscription.getHID()!= null && subscription.getHID().equals(HID)) {
				result.add(subscription);
			}
		}
		return result;
	} 
	
	/**
	 * <b>getSubscriptionsByURL</b>
	 * Returns all the subscriptions of the given URL.
	 * @param url URL 
	 * @return a List containing all the subscriptions of the given URL.
	 */
	private List<LocalSubscription> getSubscriptionsByURL(URL url) {
		List<LocalSubscription> result = new LinkedList<LocalSubscription>();
		for (LocalSubscription subscription : subscriptions) {
	        if (subscription.getURL().equals(url)) {
				result.add(subscription);
			}
		}
		return result;
	}
	
	private List<LocalSubscription> getSubscriptionsByHID(String hid) {
		List<LocalSubscription> result = new LinkedList<LocalSubscription>();
		for (LocalSubscription subscription : subscriptions) {
	        if (subscription.getHID()!= null && subscription.getHID().equals(hid)) {
				result.add(subscription);
			}
		}
		return result;
	}
	
	/**
	 * <b>remove</b>
	 * Removes all the subscriptions of a given topic, subscribed by the given subscriber. 
	 * @param topic String specifying a topic.
	 * @param subscriber URL of a subscriber.
	 */
	public void remove(String topic, URL subscriber) {
		for (LocalSubscription subscription: getSubscriptionsByTopicAndURL(topic, subscriber)) {
			subscriptions.remove(subscription);
		}
	}
	
	public boolean remove(String topic, String hid) {
		boolean result = false;
		for (LocalSubscription subscription : getSubscriptionsByTopicAndHID(topic, hid)) {
			subscriptions.remove(subscription);
			result = true; 
		}
		return result;
	}
	
	/**
	 * <b>add</b>
	 * Adds a new subscription to the given topic for the given subscriber URL, if there is no subscription 
	 * for the given topic by the given subscriber. 
	 * @param topic String specifying a topic.
	 * @param subscriber URL of the subscriber.
	 */
	public void add(String topic, URL subscriber) {
		if (getSubscriptionsByTopicAndURL(topic, subscriber).size() == 0) {
			subscriptions.add(new LocalSubscription(topic, subscriber));
		}
	}
	public void add(String topic, URL subscriber, String hid) {
		if (getSubscriptionsByTopicAndURL(topic, subscriber).size() == 0) {
			subscriptions.add(new LocalSubscription(topic, subscriber, hid));
		}
	}
	
	/**
	 * <b>clear</b>
	 * Removes all the subscriptions of the given URL subscriber.
	 * @param url URL of the subscriber.
	 */
	public LinkedList<String> clear(URL url) {
		LinkedList<String> topicPatternsRemoved = new LinkedList<String>();
		for (LocalSubscription subscription: getSubscriptionsByURL(url)) {
			subscriptions.remove(subscription);
			if(getMatches(subscription.getTopic()).size() == 0)
				topicPatternsRemoved.add(subscription.getTopic());
		}
		return topicPatternsRemoved;
	}
	
	public LinkedList<String> clear(String hid) {
		LinkedList<String> topicPatternsRemoved = new LinkedList<String>();
		for (LocalSubscription subscription: getSubscriptionsByHID(hid)) {
			subscriptions.remove(subscription);
			if(getMatches(subscription.getTopic()).size() == 0)
				topicPatternsRemoved.add(subscription.getTopic());
		}
		return topicPatternsRemoved;
	}
}
