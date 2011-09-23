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
package eu.linksmart.caf.daqc.reporting;

import eu.linksmart.caf.daqc.report.DataReport;
import eu.linksmart.caf.daqc.subscription.Subscriber;

/**
 * Abstract Stub that defines the interface for communication with a
 * {@link Subscriber}, irrespective of method (OSGi / Web Service)
 * 
 * @author Michael Crouch
 */
public abstract class SubscriberStub {

	/** the {@link Subscriber} */
	private Subscriber subscriber;

	/**
	 * Constructor
	 * 
	 * @param subcriber
	 *            the {@link Subscriber}
	 */
	public SubscriberStub(Subscriber subcriber) {
		this.subscriber = subcriber;
	}

	/**
	 * Gets the {@link Subscriber}
	 * 
	 * @return the {@link Subscriber}
	 */
	public Subscriber getSubscriber() {
		return subscriber;
	}

	/**
	 * Sets the {@link Subscriber}
	 * 
	 * @param subscriber
	 *            the {@link Subscriber}
	 */
	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	/**
	 * Send the {@link DataReport} to the {@link Subscriber}
	 * 
	 * @param report
	 *            the {@link DataReport}
	 * @return success
	 */
	public abstract boolean report(DataReport report);

	/**
	 * Matches the give HID with the configured HID for the
	 * {@link SubscriberStub}
	 * 
	 * @param hid
	 *            the HID
	 * @return matching result
	 */
	public abstract boolean matchHid(String hid);
}
