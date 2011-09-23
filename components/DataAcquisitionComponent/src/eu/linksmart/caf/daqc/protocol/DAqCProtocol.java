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
package eu.linksmart.caf.daqc.protocol;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import eu.linksmart.caf.daqc.impl.DAqCApplication;

import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.CafConstants;
import eu.linksmart.caf.daqc.DataAcquisitionComponent;
import eu.linksmart.caf.daqc.subscription.Result;
import eu.linksmart.caf.daqc.subscription.Subscriber;
import eu.linksmart.caf.daqc.subscription.Subscription;

/**
 * Abstract base class for the Protocols of the Data Acquisition Component.<p>
 * Defines methods for managing subscriptions, and some generic functionalities
 * 
 * @author Michael Crouch
 * 
 */
public abstract class DAqCProtocol {

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(DAqCProtocol.class);
	
	/** the {@link DAqCApplication} */
	private final DAqCApplication daqc;

	/**
	 * Constructor
	 * 
	 * @param daqc
	 *            the {@link DataAcquisitionComponent}
	 */
	public DAqCProtocol(DAqCApplication daqc) {
		this.daqc = daqc;
	}

	/**
	 * Matches the provided protocol id against the protocol's id to see whether
	 * it is applicable
	 * 
	 * @param subProtocol
	 *            the id of the protocol to match
	 * @return success
	 */
	public abstract boolean protocolMatch(String subProtocol);

	/**
	 * Processes the {@link Subscription} subscription for reporting to the
	 * given {@link Subscriber}. <p> The {@link Result} of the processing is
	 * returned, containing the status of the subscription.
	 * 
	 * @param subscriber
	 *            the {@link Subscriber}
	 * @param sub
	 *            the {@link Subscription}
	 * @return the {@link Result}
	 */
	public abstract Result processSubscription(Subscriber subscriber,
			Subscription sub);

	/**
	 * Cancels the {@link Subscription} with id 'dataId', for the subscriber
	 * with the given Hid
	 * 
	 * @param subscriberHid
	 *            the hid of the subscriber cancelling
	 * @param dataId
	 *            the dataId of the subscription to cancel
	 * @return success
	 */
	public abstract boolean cancelSubscription(String subscriberHid,
			String dataId);

	/**
	 * Shuts down the protocol, cancelling all subscriptions.
	 */
	public abstract void shutdownProtocol();

	/**
	 * Gets the HID of the data source, from the {@link Subscription}. If the
	 * HID is not provided as an {@link Attribute}, then the Network Manager is
	 * queried for the HID of the service with the given PID and (if given) SID.
	 * 
	 * @param sub
	 *            the {@link Subscription}
	 * @return the retrieved HID
	 */
	public String getDataSourceHid(Subscription sub) {
		// Check to see if HID attribute included
		String dsHid = getAttribute(CafConstants.DATASOURCE_HID, sub);
		if (dsHid != null)
			return dsHid;

		// If not, get PID and SID from Subscription
		String pid = getAttribute(CafConstants.DATASOURCE_PID, sub);
		String sid = getAttribute(CafConstants.DATASOURCE_SID, sub);

		// If no PID, cannot proceed
		if (pid == null)
			return null;

		// Query NM
		String query = "(PID==" + pid + ")";
		if (sid != null)
			query = "(" + query + "&&(SID==" + sid + "))";

		String hid = null;
		try {
			hid = daqc.getHidMatchingQuery(query);
		} catch (Exception e) {
			logger.error("Exception getting querying NetworkManager for Datasource HID: "
					+ e.getMessage());
		}

		if (hid == null) {
			logger.error("Error - could not get Datasource HID");
		}

		return hid;
	}

	/**
	 * Retrieves the value of {@link Attribute} with the given id from the
	 * {@link Subscription}. If none, null is returned.
	 * 
	 * @param id
	 *            the id of the {@link Attribute}
	 * @param sub
	 *            the {@link Subscription}
	 * @return the value of the {@link Attribute}
	 */
	public String getAttribute(String id, Subscription sub) {

		if (sub.getAttributes() == null)
			return null;

		for (int i = 0; i < sub.getAttributes().length; i++) {
			Attribute attr = sub.getAttributes()[i];
			if (attr.getId().equalsIgnoreCase(id))
				return attr.getValue();
		}
		return null;
	}

	/**
	 * Returns a generated String timestamp in the format
	 * "yyyy-MM-dd HH:mm:ss Z"
	 * 
	 * @return the timestamp
	 */
	public static String getTimestamp() {
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ENGLISH);
		return dfm.format(new Date());
	}
}
