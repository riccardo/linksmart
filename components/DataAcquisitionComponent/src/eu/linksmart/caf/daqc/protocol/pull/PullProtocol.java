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
package eu.linksmart.caf.daqc.protocol.pull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import eu.linksmart.caf.daqc.exception.SubscriptionException;
import eu.linksmart.caf.daqc.impl.DAqCApplication;
import eu.linksmart.caf.daqc.protocol.DAqCProtocol;
import eu.linksmart.caf.daqc.reporting.DataReportRunnable;
import eu.linksmart.caf.daqc.reporting.SubscriberFactory;
import eu.linksmart.caf.daqc.reporting.SubscriberStub;
import eu.linksmart.caf.daqc.util.ResultFactory;

import eu.linksmart.caf.CafConstants;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.daqc.report.Data;
import eu.linksmart.caf.daqc.subscription.Result;
import eu.linksmart.caf.daqc.subscription.Subscriber;
import eu.linksmart.caf.daqc.subscription.Subscription;

/**
 * Implemented {@link DAqCProtocol} to perform PULL functionality. <p> This
 * involves setting up processes for calling a given service, and 'pulling' the
 * latest values from them at a given frequency.
 * 
 * @author Michael Crouch
 * 
 */
public class PullProtocol extends DAqCProtocol {

	/** ID of the Protocol */
	public static final String PROTOCOL_ID = CafConstants.PULL_PROTOCOL;
	
	/** {@link Set} of active {@link PullSubscription}s */
	private final Set<PullSubscription> subscriptions;

	/** the {@link ScheduledExecutorService} for managing threads */
	private final ScheduledExecutorService pullThreadPool;
	
	/** the {@link DAqCApplication} */
	private final DAqCApplication daqc;

	/**
	 * Constructor. <p> Initialises {@link ScheduleExecutorService} with a
	 * thread pool.
	 * 
	 * @param daqc
	 *            the {@link DAqCApplication}
	 */
	public PullProtocol(DAqCApplication daqc) {
		super(daqc);
		this.daqc = daqc;
		pullThreadPool = Executors.newScheduledThreadPool(1);
		subscriptions = new HashSet<PullSubscription>();
	}

	@Override
	public boolean protocolMatch(String subProtocol) {
		if (subProtocol.equals(PROTOCOL_ID))
			return true;
		return false;
	}

	@Override
	public boolean cancelSubscription(String subscriberHid, String dataId) {
		PullSubscription pSub = getPullSubscription(subscriberHid, dataId);
		if (pSub == null)
			return false;
		PullSubscriber subscriber = pSub.getPullSubscriber(subscriberHid);
		if (subscriber == null)
			return false;

		subscriber.removeDataId(dataId);
		if (subscriber.getDataIds().length == 0) {
			pSub.stop();
			subscriptions.remove(pSub);
		}
		return true;
	}

	@Override
	public Result processSubscription(Subscriber subscriber, Subscription sub) {
		// get details of request
		PullSubscription pSub;
		try {
			pSub = generatePullSubscription(subscriber, sub);
		} catch (SubscriptionException e) {
			return e.getResult();
		}

		// Check if a sub for this already exists
		Iterator<PullSubscription> it = subscriptions.iterator();
		while (it.hasNext()) {
			PullSubscription temp = it.next();
			if (pSub.matchPullSubscription(temp)) {
				// add subscriber to subscription and check if it needs to be
				// updated
				temp.addSubscriber(pSub.getPullSubscribers().iterator().next());

				if (temp.updateCurrentFreq()) {
					temp.stop();
					this.startTask(temp);
				}
				return ResultFactory.create(true, sub.getDataId(), "OK");
			}
		}
		// If it got this far, then there is no existing subscription running
		try {
			subscriptions.add(pSub);
			pSub.updateCurrentFreq();
			this.startTask(pSub);
		} catch (Exception e) {
			return ResultFactory.create(false, sub.getDataId(),
					"Error initialising new Task: " + e.getLocalizedMessage());
		}

		return ResultFactory.create(true, sub.getDataId(), "OK");
	}

	/**
	 * Generates a {@link PullSubscription} for the received
	 * {@link Subscription} and {@link Subscriber}.
	 * 
	 * @param subscriber
	 *            the {@link Subscriber}
	 * @param sub
	 *            the {@link Subscription}
	 * @return the created {@link PullSubscription}
	 * @throws SubscriptionException
	 */
	private PullSubscription generatePullSubscription(Subscriber subscriber,
			Subscription sub) throws SubscriptionException {

		String dataSourceHid = null;
		String method = null;
		String ns = null;
		String soapaction = null;
		String freqStr = null;

		// Get call details
		try {
			// Get HID to call
			dataSourceHid = this.getDataSourceHid(sub);
			if (dataSourceHid == null)
				throw new SubscriptionException(ResultFactory.create(false, sub
						.getDataId(), "Could not retrieve datasource HID"));

			// Get method to call
			method = getAttribute(CafConstants.PULL_METHOD, sub);
			if (method == null)
				throw new SubscriptionException(ResultFactory.create(false, sub
						.getDataId(),
						"Could not retrieve method name from Subscription"));

			// Get frequency at which to call
			freqStr = getAttribute(CafConstants.PULL_FREQUENCY, sub);
			if (freqStr == null)
				throw new SubscriptionException(ResultFactory.create(false, sub
						.getDataId(),
						"Could not retrieve PULL frequency from Subscription"));
			long frequency = Long.parseLong(freqStr);

			// Get non-critical attributes
			ns = getAttribute(CafConstants.PULL_NAMESPACE, sub);
			soapaction = getAttribute(CafConstants.PULL_SOAPACTION, sub);

			// Creates the Pull Subscriber
			SubscriberStub stub =
					SubscriberFactory.generateSubscriberStub(daqc, subscriber);
			PullSubscriber pullSubscriber = new PullSubscriber(stub, frequency);
			pullSubscriber.addDataId(sub.getDataId());

			// Extract the parameters to call with
			List<Parameter> paramList = new ArrayList<Parameter>();
			for (int i = 0; i < sub.getParameters().length; i++) {
				paramList.add(sub.getParameters()[i]);
			}

			PullSubscription pSub =
					new PullSubscription(daqc, this, dataSourceHid, method,
							soapaction, ns, paramList);
			pSub.addSubscriber(pullSubscriber);
			return pSub;
		} catch (Exception e) {
			throw new SubscriptionException(ResultFactory.create(false, sub
					.getDataId(), e.getLocalizedMessage()));
		}
	}

	@Override
	public void shutdownProtocol() {
		this.killAllSubscriptions();
		pullThreadPool.shutdown();
	}

	/**
	 * Starts the processing of the given {@link PullSubscription}
	 * 
	 * @param task
	 *            the {@link PullSubscription}
	 */
	private void startTask(PullSubscription task) {
		ScheduledFuture handle =
				pullThreadPool.scheduleAtFixedRate(task, 1000, task
						.getCurrentFreq(), TimeUnit.MILLISECONDS);
		task.setScheduledHandle(handle);
	}

	/**
	 * Gets the {@link PullSubscription} with the given subscriberHid and dataId
	 * 
	 * @param subscriberHid
	 *            the subscriberHid
	 * @param dataId
	 *            the dataId
	 * @return the {@link PullSubscription}
	 */
	private PullSubscription getPullSubscription(String subscriberHid,
			String dataId) {
		Iterator<PullSubscription> it = subscriptions.iterator();
		while (it.hasNext()) {
			PullSubscription sub = (PullSubscription) it.next();
			if (sub.hasSubscriber(subscriberHid, dataId))
				return sub;
		}
		return null;
	}

	/**
	 * Kills all {@link PullSubscription}s
	 */
	private void killAllSubscriptions() {
		Iterator it = subscriptions.iterator();
		while (it.hasNext()) {
			PullSubscription sub = (PullSubscription) it.next();
			sub.stop();
		}
	}

	/**
	 * Reports the {@link Data} to all {@link PullSubscriber}s provided
	 * 
	 * @param reportTo
	 *            the {@link Set} of {@link PullSubscriber}s to report to
	 * @param data
	 *            the {@link Data} to report
	 * @param status
	 *            the status
	 * @param message
	 *            the message
	 */
	public void report(Set<PullSubscriber> reportTo, Data data, boolean status,
			String message) {

		Data[] dataToSend;
		if (data == null) {
			// report empty array
			dataToSend = new Data[0];
		} else {
			dataToSend = new Data[1];
			dataToSend[0] = data;
		}
		Iterator<PullSubscriber> it = reportTo.iterator();
		while (it.hasNext()) {
			PullSubscriber subscriber = it.next();
			DataReportRunnable runnable =
					new DataReportRunnable(subscriber.getSubscriberStub(),
							subscriber.getDataIds(), dataToSend, status,
							message);
			daqc.getDataReportingManager().report(runnable);
		}
	}
}
