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
package eu.linksmart.caf.daqc.subscription;

import eu.linksmart.caf.daqc.DataAcquisitionComponent;

/**
 * The container for {@link Subscription}s to be sent to the {@link DataAcquisitionComponent}.<p>
 * As well as the contained array of {@link Subscription}s, it also contains a {@link Subscriber}
 * object, with the details of the subscriber to report to.
 * @author Michael Crouch
 *
 */
public class DaqcSubscription  implements java.io.Serializable {
    private eu.linksmart.caf.daqc.subscription.Subscriber subscriber;

    private eu.linksmart.caf.daqc.subscription.Subscription[] subscriptions;

    /**
     * Constructor
     */
    public DaqcSubscription() {
    }

    /**
     * Constructor
     * @param subscriber the {@link Subscriber}
     * @param subscriptions the array of {@link Subscription}
     */
    public DaqcSubscription(
           eu.linksmart.caf.daqc.subscription.Subscriber subscriber,
           eu.linksmart.caf.daqc.subscription.Subscription[] subscriptions) {
           this.subscriber = subscriber;
           this.subscriptions = subscriptions;
    }


    /**
     * Gets the subscriber value for this DaqcSubscription.
     * 
     * @return subscriber
     */
    public eu.linksmart.caf.daqc.subscription.Subscriber getSubscriber() {
        return subscriber;
    }


    /**
     * Sets the subscriber value for this DaqcSubscription.
     * 
     * @param subscriber
     */
    public void setSubscriber(eu.linksmart.caf.daqc.subscription.Subscriber subscriber) {
        this.subscriber = subscriber;
    }


    /**
     * Gets the subscriptions value for this DaqcSubscription.
     * 
     * @return subscriptions
     */
    public eu.linksmart.caf.daqc.subscription.Subscription[] getSubscriptions() {
        return subscriptions;
    }


    /**
     * Sets the subscriptions value for this DaqcSubscription.
     * 
     * @param subscriptions
     */
    public void setSubscriptions(eu.linksmart.caf.daqc.subscription.Subscription[] subscriptions) {
        this.subscriptions = subscriptions;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DaqcSubscription)) return false;
        DaqcSubscription other = (DaqcSubscription) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.subscriber==null && other.getSubscriber()==null) || 
             (this.subscriber!=null &&
              this.subscriber.equals(other.getSubscriber()))) &&
            ((this.subscriptions==null && other.getSubscriptions()==null) || 
             (this.subscriptions!=null &&
              java.util.Arrays.equals(this.subscriptions, other.getSubscriptions())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getSubscriber() != null) {
            _hashCode += getSubscriber().hashCode();
        }
        if (getSubscriptions() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSubscriptions());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSubscriptions(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
