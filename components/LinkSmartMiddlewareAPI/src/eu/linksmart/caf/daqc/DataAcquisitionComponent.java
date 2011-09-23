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
package eu.linksmart.caf.daqc;


import eu.linksmart.caf.daqc.subscription.DaqcSubscription;
import eu.linksmart.caf.daqc.subscription.DaqcSubscriptionResponse;
import eu.linksmart.caf.daqc.subscription.Subscription;
import eu.linksmart.eventmanager.EventSubscriber;

/**
 * The interface for the Data Acquisition Component.<p>
 * Provides functionalities for subscribing to the DAqC with {@link DaqcSubscription}s, and well
 * as the means for cancelling {@link Subscription}s. 
 * @author Michael Crouch
 *
 */
public interface DataAcquisitionComponent extends EventSubscriber, java.rmi.Remote {
	
	/**
	 * Subscribes for all data specified by the {@link Subscription}s in the {@link DaqcSubscription}.<p>
	 * The returned {@link DaqcSubscriptionResponse} contains the status of each {@link Subscription}. 
	 * @param daqcSub the {@link DaqcSubscription}
	 * @return the {@link DaqcSubscriptionResponse}
	 * @throws java.rmi.RemoteException
	 */
    public eu.linksmart.caf.daqc.subscription.DaqcSubscriptionResponse subscribe(eu.linksmart.caf.daqc.subscription.DaqcSubscription daqcSub) throws java.rmi.RemoteException;
    
    /**
     * Cancels the {@link Subscription} with the given dataId, for the subscriber with the
     * given Hid, from the specified protocol.
     * @param protocol the protocol id
     * @param subscriberHid the HID of the Subscriber
     * @param dataId the Data Id
     * @return success
     * @throws java.rmi.RemoteException
     */
    public boolean cancelSubscription(java.lang.String protocol, java.lang.String subscriberHid, java.lang.String dataId) throws java.rmi.RemoteException;
}
