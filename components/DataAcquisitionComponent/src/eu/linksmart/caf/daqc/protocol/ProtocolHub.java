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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.linksmart.caf.daqc.util.ResultFactory;

import eu.linksmart.caf.daqc.subscription.Result;
import eu.linksmart.caf.daqc.subscription.Subscriber;
import eu.linksmart.caf.daqc.subscription.Subscription;

/**
 * Hub managing all {@link DAqCProtocol}s, handling the process of routing
 * requests to the appropriate protocol.
 * 
 * @author Michael Crouch
 * 
 */
public class ProtocolHub {

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(ProtocolHub.class);
	
	/** the {@link Set} of {@link DAqCProtocol}s */
	private Set<DAqCProtocol> protocols;

	/**
	 * Constructor
	 */
	public ProtocolHub() {
		this(null);
	}

	/**
	 * Constructor
	 * 
	 * @param protocols
	 *            {@link} Set of {@link DAqCProtocol}
	 */
	public ProtocolHub(Set<DAqCProtocol> protocols) {
		this.protocols = protocols;
		if (this.protocols == null)
			this.protocols = new HashSet<DAqCProtocol>();
	}

	/**
	 * Adds the {@link DAqCProtocol} to the hub
	 * 
	 * @param protocol
	 *            the {@link DAqCProtocol}
	 */
	public void addProtocol(DAqCProtocol protocol) {
		if (!protocols.contains(protocol))
			protocols.add(protocol);
	}

	/**
	 * Gets the {@link DAqCProtocol} with the given id
	 * 
	 * @param protocolId
	 *            the id
	 * @return the {@link DAqCProtocol}
	 */
	public DAqCProtocol getProtocol(String protocolId) {
		Iterator<DAqCProtocol> it = protocols.iterator();
		while (it.hasNext()) {
			DAqCProtocol protocol = (DAqCProtocol) it.next();
			if (protocol.protocolMatch(protocolId))
				return protocol;
		}
		return null;
	}

	/**
	 * Directs the request to the correct {@link DAqCProtocol}
	 * 
	 * @param subscriber
	 *            the {@link Subscriber}
	 * @param sub
	 *            the {@link Subscription}
	 * @return the {@link Result}
	 */
	public Result processSubscription(Subscriber subscriber, Subscription sub) {
		Iterator it = protocols.iterator();
		while (it.hasNext()) {
			DAqCProtocol protocol = (DAqCProtocol) it.next();
			if (protocol.protocolMatch(sub.getProtocol()))
				return protocol.processSubscription(subscriber, sub);
		}

		return ResultFactory
				.create(false, sub.getDataId(), "No Protocol Match");
	}

	/**
	 * Forwards the cancel request to the correct {@link DAqCProtocol}
	 * 
	 * @param protocolId
	 *            id of the protocol
	 * @param subscriberHid
	 *            HID of the cacelling subscriber
	 * @param dataId
	 *            the data to cancel
	 * @return success
	 */
	public boolean cancelSubscription(String protocolId, String subscriberHid,
			String dataId) {
		logger.debug("DAqC: Cancelling data subscription for subscriberHid '"
				+ subscriberHid + "', dataid '" + dataId + "', and protocol '"
				+ protocolId + "'");
		Iterator it = protocols.iterator();
		while (it.hasNext()) {
			DAqCProtocol protocol = (DAqCProtocol) it.next();
			if (protocol.protocolMatch(protocolId))
				return protocol.cancelSubscription(subscriberHid, dataId);
		}
		return true;
	}

	/**
	 * Shuts down all {@link DAqCProtocol}s
	 */
	public void shutdownProtocols() {
		Iterator it = protocols.iterator();
		while (it.hasNext()) {
			DAqCProtocol protocol = (DAqCProtocol) it.next();
			protocol.shutdownProtocol();
		}
	}
}
