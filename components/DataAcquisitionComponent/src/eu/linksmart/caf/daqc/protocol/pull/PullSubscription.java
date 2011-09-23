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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.log4j.Logger;

import eu.linksmart.caf.daqc.impl.DAqCApplication;
import eu.linksmart.caf.daqc.protocol.DAqCProtocol;
import eu.linksmart.caf.daqc.reporting.DataReportingManager;
import eu.linksmart.caf.daqc.util.TypeFactory;

import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.daqc.report.Data;

/**
 * A subscription for "pulling" data from a data source. The data is pulled at
 * the lowest rate of all registered {@link PullSubscriber}s.
 * 
 * @author Michael Crouch
 * 
 */
public class PullSubscription implements Runnable {

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(PullSubscription.class);
	
	/** the XSD Schame namespace */
	private static final String URI_2001_SCHEMA_XSD =
			"http://www.w3.org/2001/XMLSchema";

	/** the method */
	private final String method;
	
	/** the namespace */
	private final String ns;
	
	/** the {@link List} of {@link Parameter}s */
	private final List<Parameter> params;
	
	/** the target url */
	private final String trgUrl;
	
	/** the target hid */
	private final String trgHid;
	
	/** the SOAP action */
	private final String soapAction;

	/** the handle for the pulling task */
	private ScheduledFuture<?> taskHandle;
	
	/** the {@link Set} of {@link PullSubscriber}s */
	private final Set<PullSubscriber> subscribers;
	
	/** the current pulling frequency (in ms) */
	private long currentFreq;
	
	/** the {@link PullProtocol} */
	private final PullProtocol protocol;

	/**
	 * Constructor
	 * 
	 * @param daqc
	 *            the {@link DAqCApplication}
	 * @param protocol
	 *            the {@link PullProtocol} managing the {@link PullSubscription}
	 * @param trgHid
	 *            the HID to call
	 * @param method
	 *            the method to call
	 * @param soapAction
	 *            the SOAP Action (may be null)
	 * @param ns
	 *            the namespace (may be null)
	 * @param params
	 *            any parameters to pass in the call (may be null)
	 */
	public PullSubscription(DAqCApplication daqc, PullProtocol protocol,
			String trgHid, String method, String soapAction, String ns,
			List<Parameter> params) {
		this.protocol = protocol;
		String portStr = daqc.getHttpPort();
		this.trgHid = trgHid;
		this.trgUrl =
				"http://localhost:" + portStr + "/SOAPTunneling/"
						+ daqc.getHid() + "/" + trgHid + "/" + 0 + "/hola";
		this.method = method;
		this.ns = ns;
		if (params == null) {
			this.params = new ArrayList<Parameter>();
		} else {
			this.params = params;
		}
		this.soapAction = soapAction;
		currentFreq = -1;
		this.subscribers = new HashSet<PullSubscriber>();

	}

	/**
	 * Adds a {@link PullSubscriber} to the subscription
	 * 
	 * @param subscriber
	 *            the {@link PullSubscriber}
	 */
	public void addSubscriber(PullSubscriber subscriber) {
		PullSubscriber temp =
				getPullSubscriber(subscriber.getSubscriberStub()
						.getSubscriber().getSubscriberHid());

		if (temp == null) {
			subscribers.add(subscriber);
			return;
		}

		if (subscriber.getWantedFrequency() < temp.getWantedFrequency()) {
			temp.setWantedFrequency(subscriber.getWantedFrequency());
		}

		for (String str : subscriber.getDataIds()) {
			temp.addDataId(str);
		}
	}

	/**
	 * Removes the {@link PullSubscriber} with the given HID from the
	 * {@link PullSubscription}
	 * 
	 * @param subscriberHid
	 *            the hid of the subscriber
	 */
	public void removeSubscriber(String subscriberHid) {
		PullSubscriber sub = getPullSubscriber(subscriberHid);
		if (sub == null)
			return;
		subscribers.remove(sub);
	}

	/**
	 * Removes given dataId from the {@link PullSubscriber} with the given
	 * subscriber HID
	 * 
	 * @param subscriberHid
	 *            the hid of the subscriber
	 * @param dataId
	 *            the dataId to remove
	 */
	public void removeSubscriberDataId(String subscriberHid, String dataId) {
		PullSubscriber sub = getPullSubscriber(subscriberHid);
		if (sub == null)
			return;

		sub.removeDataId(dataId);

		if (sub.getDataIds().length == 0)
			subscribers.remove(sub);
	}

	/**
	 * Gets the Method name to call
	 * 
	 * @return the method name
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Returns the {@link List} of {@link Parameter}s to be passed in the call
	 * 
	 * @return the {@link List} of {@link Parameter}s
	 */
	public List<Parameter> getParams() {
		return params;
	}

	/**
	 * Gets the HID to call
	 * 
	 * @return the HID
	 */
	public String getTrgHid() {
		return trgHid;
	}

	/**
	 * Gets the current frequency (in ms) of the pull task
	 * 
	 * @return the current frequency (in ms)
	 */
	public long getCurrentFreq() {
		return currentFreq;
	}

	/**
	 * Updates the current getting frequency for this {@link PullSubscription},
	 * by iterating through all registered {@link PullSubscriber}s, and finding
	 * the lowest specified wanted frequency. <p> If the frequency has been
	 * changed from what was previously set, then true is returned.
	 * 
	 * @return whether the frequency has been changed
	 */
	public boolean updateCurrentFreq() {
		// scan all Subscribers for lowest frequency
		// return true if a change is made
		long lowest = -1;

		Iterator<PullSubscriber> it = subscribers.iterator();
		while (it.hasNext()) {
			long freq = it.next().getWantedFrequency();
			if (lowest < 0 || freq < lowest)
				lowest = freq;
		}

		currentFreq = lowest;

		if (currentFreq != lowest)
			return true;
		return false;

	}

	/**
	 * Gets the {@link Set} of {@link PullSubscriber}s to this subscription
	 * 
	 * @return the {@link Set} of {@link PullSubscriber}s
	 */
	public Set<PullSubscriber> getPullSubscribers() {
		return subscribers;
	}

	/**
	 * Returns whether the subscription has a {@link PullSubscriber} with the
	 * given hid and dataId
	 * 
	 * @param subscriberHid
	 *            the hid of the subscriber
	 * @param dataId
	 *            the dataId for the subscriber
	 * @return whether it exists
	 */
	public boolean hasSubscriber(String subscriberHid, String dataId) {

		PullSubscriber sub = getPullSubscriber(subscriberHid);
		if (sub == null)
			return false;
		if (sub.hasDataId(dataId))
			return true;
		return false;
	}

	/**
	 * Returns the {@link PullSubscriber} with the given hid
	 * 
	 * @param subscriberHid
	 *            the hid of the subscriber
	 * @return the {@link PullSubscriber}
	 */
	public PullSubscriber getPullSubscriber(String subscriberHid) {
		Iterator<PullSubscriber> it = subscribers.iterator();
		while (it.hasNext()) {
			PullSubscriber sub = it.next();
			if (sub.getSubscriberStub().matchHid(subscriberHid))
				return sub;
		}
		return null;
	}

	/**
	 * Executes the pull call to the configured web service, and then calls the
	 * {@link DataReportingManager} to report the data to all subscribers.<p> If
	 * there's an error retrieving the data, the {@link DataReportingManager} is
	 * called to report the error to subscribers.
	 */
	@Override
	public void run() {
		// get data from web service
		String resultToReport;
		try {
			// create ws call
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new URL(trgUrl));

			// if named space given, set method with namespace
			if (this.ns == null) {
				call.setOperationName(this.method);
			} else {
				call.setOperationName(new QName(this.ns, this.method));
			}

			// if no SOAP action given, then use the method name
			if (this.soapAction == null) {
				call.setSOAPActionURI(this.method);
			} else {
				call.setSOAPActionURI(this.soapAction);
			}

			// Create array of Objects, with objects as the required type, to
			// pass
			// in the call
			int size = params.size();
			Object[] toSend;
			if (size > 0) {
				toSend = new Object[size];
				for (int i = 0; i < size; i++) {
					Parameter param = params.get(i);
					QName type;
					if (param.getType().contains("#")) {
						String[] typeSpl = param.getType().split("#");
						type = new QName(typeSpl[0], typeSpl[1]);
					} else {
						type = new QName(URI_2001_SCHEMA_XSD, param.getType());
					}
					call.addParameter(param.getName(), type, ParameterMode.IN);
					// Use TypeFactory to get the object in the right type
					toSend[i] =
							TypeFactory.getObjectAsType(param.getValue(), param
									.getType());
				}
			} else {
				// if nothing to send, create an empty array
				toSend = new Object[] {};
			}
			resultToReport = (String) call.invoke(toSend);
		} catch (Exception e) {
			logger.error("Error calling Pull WS (" + trgHid + "."
					+ method + "): " + e.getLocalizedMessage());
			logger.error("Cancelling PullSubscription as a result of error");

			// cancel task
			this.stop();
			protocol.report(subscribers, null, false,
					"Error calling LinkSmart service [" + trgHid + "]: "
							+ e.getLocalizedMessage());
			return;
		}

		try {
			// report new pull data
			long currentTime = System.currentTimeMillis();
			Iterator<PullSubscriber> it = subscribers.iterator();
			Set<PullSubscriber> toCall = new HashSet<PullSubscriber>();
			while (it.hasNext()) {
				PullSubscriber subscriber = it.next();
				if (subscriber.isReadyToFire(currentFreq, currentTime)) {
					toCall.add(subscriber);
					subscriber.setLastFired(currentTime);
				}
			}

			// Set<Data> dataSet = new HashSet<Data>();
			Data data = new Data();
			data.setProtocol(PullProtocol.PROTOCOL_ID);
			data.setTimestamp(DAqCProtocol.getTimestamp());

			Attribute attr = new Attribute();
			attr.setId("");
			attr.setValue(resultToReport);

			Attribute[] attrs = { attr };
			data.setAttributes(attrs);
			// dataSet.add(data);
			protocol.report(toCall, data, true, "OK");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the handle, as {@link ScheduledFuture} for the subscription task
	 * 
	 * @param handle
	 *            the {@link ScheduledFuture}
	 */
	public void setScheduledHandle(ScheduledFuture handle) {
		this.taskHandle = handle;
	}

	/**
	 * Stops the processing of the subscription
	 */
	public void stop() {
		this.taskHandle.cancel(false);
	}

/**
	 * Matches the given {@link PullSubscription} against this one, 
	 * and evaluates whether they are equal. <p>
	 * Includes matching of the {@link Parameter}s to be used in the call.
	 * @param sub the {@link PullSubscription
	 * @return whether they are equal
	 */
	public boolean matchPullSubscription(PullSubscription sub) {

		if (!trgHid.equals(sub.getTrgHid()) || !method.equals(sub.getMethod()))
			return false;

		// match params
		if (params.size() != sub.getParams().size())
			return false;

		Iterator<Parameter> thisIt = params.iterator();
		Iterator<Parameter> subParamsIt = sub.getParams().iterator();
		while (thisIt.hasNext()) {
			Parameter a = thisIt.next();
			Parameter b = subParamsIt.next();

			if (!a.getName().equals(b.getName()))
				return false;
			if (!a.getType().equals(b.getType()))
				return false;
			if (!a.getValue().equals(b.getValue()))
				return false;
		}
		return true;
	}
}