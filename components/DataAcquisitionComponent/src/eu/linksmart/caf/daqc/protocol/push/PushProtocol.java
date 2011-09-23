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

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.linksmart.caf.daqc.exception.SubscriptionException;
import eu.linksmart.caf.daqc.impl.DAqCApplication;
import eu.linksmart.caf.daqc.protocol.DAqCProtocol;
import eu.linksmart.caf.daqc.reporting.DataReportRunnable;
import eu.linksmart.caf.daqc.util.ResultFactory;

import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.CafConstants;
import eu.linksmart.caf.daqc.report.Data;
import eu.linksmart.caf.daqc.subscription.Result;
import eu.linksmart.caf.daqc.subscription.Subscriber;
import eu.linksmart.caf.daqc.subscription.Subscription;
import eu.linksmart.eventmanager.EventManagerPort;
import eu.linksmart.eventmanager.Part;

/**
 * Protocol for handling Events from the Event Manager. Essentially acts as a
 * middleman, subscribing to events from the Event Manager, and then
 * interpreting the and reporting to the subscriber.
 * 
 * @author Michael Crouch
 * 
 */
public class PushProtocol extends DAqCProtocol {

	/** ID of the Protocol */
	public static final String PROTOCOL_ID = CafConstants.PUSH_PROTOCOL;

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(PushProtocol.class);
	
	/** the {@link Set} of {@link PushSubscription}s */
	private final Set<PushSubscription> subs;
	
	/** the {@link DAqCApplication} */
	private final DAqCApplication daqc;

	/** the {@link EventManagerPort} */
	private final EventManagerPort em;

	/**
	 * Constructor
	 * 
	 * @param daqc
	 *            the {@link DAqCApplication}
	 */
	public PushProtocol(DAqCApplication daqc) {
		super(daqc);
		this.daqc = daqc;
		em = daqc.getEventManager();
		this.subs = new HashSet<PushSubscription>();
	}

	@Override
	public boolean protocolMatch(String subProtocol) {
		if (subProtocol.equals(PROTOCOL_ID))
			return true;
		return false;
	}

	/**
	 * Determines whether a {@link PushSubscription} already exists, matching
	 * the one passed
	 * 
	 * @param sub
	 *            the {@link PushSubscription} to mathc
	 * @return whether it already exists
	 */
	public boolean hasExistingPushSubscription(PushSubscription sub) {
		if (findPushSubscription(sub.getEventTopic()) == null)
			return false;
		return true;
	}

	/**
	 * Retrieves the stored {@link PushSubscription}, with the given topic
	 * Returns null if none exists.
	 * @param eventTopic the topic of the event
	 * @return the {@link PushSubscription} - null if none found
	 */
	public PushSubscription findPushSubscription(String eventTopic) {
		Iterator<PushSubscription> it = subs.iterator();
		while (it.hasNext()) {
			PushSubscription temp = it.next();
			if (temp.getEventTopic().equalsIgnoreCase(eventTopic))
				return temp;
		}
		return null;
	}

	/**
	 * Cancels the subscription with the given dataId, for the
	 * {@link PushSubscriber} with the given hid. <p> If there are no further
	 * subscribers for the associated Event topic, from the Event Manager, then
	 * the subscription to the Event Manager is also cancelled.
	 */
	@Override
	public boolean cancelSubscription(String subscriberHid, String dataId) {
		Iterator<PushSubscription> it = subs.iterator();
		while (it.hasNext()) {
			PushSubscription subscription = it.next();
			PushSubscriber subscriber =
					subscription.getSubscriberWithHidAlias(subscriberHid,
							dataId);
			if (subscriber != null) {
				subscriber.removeAlias(dataId);
				if (subscriber.getAliases().length == 0) {
					subscription.removeSubscriber(subscriberHid);
					if (subscription.getSubscribers().size() == 0) {
						this.cancelSubscriptionToEM(subscription);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Subscribes for the topic specified in the {@link Subscription}, to the
	 * Event Manager specified, if such a subscription (created as a
	 * {@link PushSubscription}) does not already exist. If so, a
	 * {@link PushSubscriber} is generated and added to the
	 * {@link PushSubscription}.
	 */
	@Override
	public Result processSubscription(Subscriber subscriber, Subscription sub) {

		
		PushSubscription temp;
		try {
			temp = generatePushSubscription(subscriber, sub);
		} catch (SubscriptionException e) {
			return e.getResult();
		}
		

		PushSubscription pushSub = findPushSubscription(temp.getEventTopic());

		if (pushSub == null) {
			pushSub = temp;
			boolean result = subscribeToEventManager(pushSub);
			if (!result) {
				return ResultFactory.create(false, sub.getDataId(),
						"Could not subscribe to Event Manager");
			}
			storePushSubscription(pushSub);
		}
		pushSub.addSubscriber(daqc, subscriber, sub.getDataId());

		return ResultFactory.create(true, sub.getDataId(), "OK");
	}

	/**
	 * Stores the {@link PushSubscription}, if it is not already stored
	 * 
	 * @param pushSub
	 *            the {@link PushSubscription}
	 */
	private void storePushSubscription(PushSubscription pushSub) {
		if (!hasExistingPushSubscription(pushSub)) {
			subs.add(pushSub);
		}
	}

	/**
	 * Generates the {@link PushSubscription} object from the {@link Subscriber}
	 * and the {@link Subscription}
	 * 
	 * @param subscriber
	 *            the {@link Subscriber}
	 * @param sub
	 *            the {@link Subscription}
	 * @return the {@link PushSubscription}
	 * @throws Exception
	 */
	private PushSubscription generatePushSubscription(Subscriber subscriber,
			Subscription sub) throws SubscriptionException {

		String eventTopic = getAttribute(CafConstants.PUSH_TOPIC, sub);

		if (eventTopic == null) {
			logger.error("Error parsing Push subscription: No Event Topic specified");
			throw new SubscriptionException(ResultFactory.create(false, sub.getDataId(), "No Event Topic specified!"));
		}

		PushSubscription pSub = new PushSubscription(eventTopic);
		pSub.addSubscriber(daqc, subscriber, sub.getDataId());

		return pSub;

	}

	/**
	 * Cancels subscriptions to all Event Managers
	 */
	@Override
	public void shutdownProtocol() {
		try {
			em.clearSubscriptionsWithHID(daqc.getHid());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calls the Event Manager specified in the {@link PushSubscription}, and
	 * subscribes to the given Topic
	 * 
	 * @param pushSub
	 *            the {@link PushSubscription}
	 * @return success
	 */
	private boolean subscribeToEventManager(PushSubscription pushSub) {
		try {
			return em.subscribeWithHID(pushSub.getEventTopic(), daqc.getHid());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Cancels the {@link PushSubscription} with the Event Manager
	 * 
	 * @param pushSub
	 *            the {@link PushSubscription}
	 * @return success
	 */
	private boolean cancelSubscriptionToEM(PushSubscription pushSub) {
		try {
			return em
					.unsubscribeWithHID(pushSub.getEventTopic(), daqc.getHid());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * The forward notification from the Event Manager.<p> The Push Protocol
	 * initiates a thread for reporting the event to all relevant
	 * {@link PushSubscriber}s, and returns true.
	 * 
	 * @param topic
	 *            the topic of the Event
	 * @param parts
	 *            array of key-value {@link Part}s of the Event
	 * @return received successfully
	 */
	public boolean notify(final String topic, final Part[] parts) {
		// return quick response to EventManager by running reaction code in new
		// thread
		daqc.getDataReportingManager().getReportingThreadPool().execute(
				new Runnable() {

					public void run() {
						Set<Attribute> attrSet = new HashSet<Attribute>();
						Data data = new Data();
						for (int i = 0; i < parts.length; i++) {
							Part part = parts[i];
							Attribute attr = new Attribute();
							attr.setId(part.getKey());
							attr.setValue(part.getValue());
							attrSet.add(attr);
						}
						// add topic
						attrSet.add(new Attribute(CafConstants.PUSH_TOPIC,
								topic));
						data.setAttributes((Attribute[]) attrSet
								.toArray(new Attribute[attrSet.size()]));
						data.setProtocol(PROTOCOL_ID);
						PushSubscription pushSub = findPushSubscription(topic);
						if (pushSub == null) {
							// No current subscriptions for this Event - so
							// cancel it
							cancelSubscriptionToEM(pushSub);
							return;
						}

						report(pushSub, data, true, "OK");
					}
				});
		return true;
	}

	/**
	 * Reports the {@link Data} to all {@link PushSubscriber}s in the
	 * {@link PushSubscription}
	 * 
	 * @param sub
	 *            the {@link PushSubscription}
	 * @param data
	 *            the {@link Data} to report
	 * @param status
	 *            the status
	 * @param message
	 *            the message
	 */
	public void report(PushSubscription sub, Data data, boolean status,
			String message) {
		Data[] dataToSend = { data };
		Iterator<PushSubscriber> it = sub.getSubscribers().iterator();
		while (it.hasNext()) {
			PushSubscriber subscriber = it.next();
			DataReportRunnable runnable =
					new DataReportRunnable(subscriber.getSubscriberStub(),
							subscriber.getAliases(), dataToSend, status,
							message);
			daqc.getDataReportingManager().report(runnable);
		}
	}
}
