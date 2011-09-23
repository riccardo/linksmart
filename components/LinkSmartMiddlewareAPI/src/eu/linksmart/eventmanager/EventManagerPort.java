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
 * Copyright (C) 2006-2010 [Telefonica I+D]
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

/**
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.eventmanager;


public interface EventManagerPort extends java.rmi.Remote {
	
	/**
	 * Subscribe method - Saves a subscription made by subscriber with the 
	 * given URI, to the specific topic.
	 * 
	 * @param topic topic to be subscribed
	 * @param subscriber address of the subscriber
	 * @return true or false depending on the result of saving the subscription.
	 * @throws java.rmi.RemoteException
	 */
	public boolean subscribe(java.lang.String topic, String subscriber) 
		throws java.rmi.RemoteException;
		
	public boolean subscribeWithHID(java.lang.String topic, java.lang.String hid) 
		throws java.rmi.RemoteException;
	
	/**
	 * uUsubscribe method - Deletes a subscription made by subscriber with the 
	 * given URI, to the specific topic.
	 * 
	 * @param topic topic to be unsubscribed
	 * @param subscriber address of the unsubscriber
	 * @return true or false depending on the result of erasing the subscription.
	 * @throws java.rmi.RemoteException
	 */
	public boolean unsubscribe(java.lang.String topic, String subscriber) 
		throws java.rmi.RemoteException;
	
	public boolean unsubscribeWithHID(java.lang.String topic, java.lang.String hid) 
		throws java.rmi.RemoteException;
	
	/**
	 * getSubscriptions method - Returns all the existing subscriptions.
	 * 
	 * @return an array with all the subscriptions.
	 * @throws java.rmi.RemoteException
	 */
	public eu.linksmart.eventmanager.Subscription[] getSubscriptions()
		throws java.rmi.RemoteException;

	/**
	 * clearSubscriptions method - Erases all the subscriptions made by a 
	 * subscriber with the given URI
	 * 
	 * @param subscriber address of the subscriber.
	 * @return true or false depending on the result of the operation.
	 * @throws java.rmi.RemoteException
	 */
	public boolean clearSubscriptions(String subscriber) 
		throws java.rmi.RemoteException;
		
	public boolean clearSubscriptionsWithHID(java.lang.String hid) 
		throws java.rmi.RemoteException;
	
	/**
	 * publish method - Publish's an event which will be received by everyone 
	 * that subscribed to the given topic.
	 *  
	 * @param topicPattern topic of the event.
	 * @param event event to be published.
	 * @return true or false depending on the result of the publishing.
	 * @throws java.rmi.RemoteException
	 */
	public boolean publish(java.lang.String topicPattern, 
		eu.linksmart.eventmanager.Part[] event) throws java.rmi.RemoteException;
	
	/**
	 * setPriority method - Sets the priority to the given topic.
	 *  
	 * @param topic topic to set the priority
	 * @param priority the priority to set
	 * @return true or false depending on the result of the set.
	 * @throws java.rmi.RemoteException
	 */
	public boolean setPriority(java.lang.String topic, int priority) 
		throws java.rmi.RemoteException;
	
}
