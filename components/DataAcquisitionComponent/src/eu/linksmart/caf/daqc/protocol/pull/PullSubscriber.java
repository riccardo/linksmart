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

import eu.linksmart.caf.daqc.reporting.SubscriberStub;

/**
 * Represents the details of a subscriber to a {@link PullSubscription},
 * including the {@link SubscriberStub}, the dataId(s) for the subscriptions,
 * the desired frequency, and time last fired.
 * 
 * @author Michael Crouch
 * 
 */
public class PullSubscriber {

	/** the {@link SubscriberStub} */
	private SubscriberStub subscriber;
	
	/** the array of data ids / aliases */
	private String[] dataIds;
	
	/** the wanted frequency */
	private long wantedfrequency;
	
	/** the time since last fired */
	private long lastFired;

	/**
	 * Constructor
	 * 
	 * @param subscriber
	 *            the {@link SubscriberStub}
	 * @param wantedfrequency
	 *            the desired frequency
	 */
	public PullSubscriber(SubscriberStub subscriber, long wantedfrequency) {
		this.subscriber = subscriber;
		this.wantedfrequency = wantedfrequency;
		this.dataIds = new String[0];
	}

	/**
	 * Gets the String array of dataIds
	 * 
	 * @return the dataIds
	 */
	public String[] getDataIds() {
		return dataIds.clone();
	}

	/**
	 * Adds a dataId
	 * 
	 * @param dataId
	 *            the dataId
	 */
	public void addDataId(String dataId) {
		if (!hasDataId(dataId)) {
			int newLength = dataIds.length + 1;
			String[] newArray = new String[newLength];
			System.arraycopy(dataIds, 0, newArray, 0, dataIds.length);
			newArray[newLength - 1] = dataId;
			dataIds = newArray;
		}
	}

	/**
	 * Removes the dataId
	 * 
	 * @param dataId
	 *            the dataId to remove
	 */
	public void removeDataId(String dataId) {
		if (hasDataId(dataId)) {
			int newLength = dataIds.length - 1;
			String[] newArray = new String[newLength];
			boolean removed = false;
			for (int i = 0; i < dataIds.length; i++) {
				String str = dataIds[i];
				if (str.equals(dataId)) {
					removed = true;
				} else {
					if (removed) {
						newArray[i - 1] = str;
					} else {
						newArray[i] = str;
					}
				}
			}
			dataIds = newArray;
		}
	}

	/**
	 * Returns whether {@link PullSubscriber} contains the give dataId
	 * 
	 * @param dataId
	 *            the dataId for the data
	 * @return whether it contains it
	 */
	public boolean hasDataId(String dataId) {
		for (String str : dataIds) {
			if (str.equals(dataId))
				return true;
		}
		return false;
	}

	/**
	 * Gets the wanted frequency
	 * 
	 * @return the frequency
	 */
	public long getWantedFrequency() {
		return wantedfrequency;
	}

	/**
	 * Sets the wanted frequency (ms)
	 * 
	 * @param frequency
	 *            the frequency (ms)
	 */
	public void setWantedFrequency(long frequency) {
		this.wantedfrequency = frequency;
	}

	/**
	 * Gets the {@link SubscriberStub}
	 * 
	 * @return the {@link SubscriberStub}
	 */
	public SubscriberStub getSubscriberStub() {
		return subscriber;
	}

	/**
	 * Gets the time last fired (in ms)
	 * 
	 * @return time (in ms)
	 */
	public long getLastFired() {
		return lastFired;
	}

	/**
	 * Sets the time last fired
	 * 
	 * @param lastFired
	 *            the time last fired
	 */
	public void setLastFired(long lastFired) {
		this.lastFired = lastFired;
	}

	/**
	 * Evaluates whether the subscriber is ready to fire, based on the desired
	 * frequency of the subscriber and the current getting frequency of the
	 * {@link PullSubscription}
	 * 
	 * @param currentGettingFreq
	 *            the current frequency of the {@link PullSubscription}
	 * @param currentTime
	 *            the current system clock time
	 * @return whether it is ready to fire
	 */
	public boolean isReadyToFire(long currentGettingFreq, long currentTime) {
		long timeSinceLastFired = currentTime - lastFired;
		if (timeSinceLastFired >= wantedfrequency)
			return true;

		long timeToNextDesiredFiring = wantedfrequency - timeSinceLastFired;

		if (timeToNextDesiredFiring > currentGettingFreq)
			return false;
		return true;
	}

}
