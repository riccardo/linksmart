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

/**
 * The subscription for a particular piece of data from a data source, as part of the
 * {@link DaqcSubscription}.<p>
 * Contains:
 * <ol type="i">
 * <li>protocol - the id of the protocol for the acquisition of the data</li> 
 * <li>dataId - the id that the {@link Subscriber} identifies the data as</li>
 * <li>attributes - any protocol-related attributes required to set up the subscription</li>
 * <li>parameters - any parameters to use in the processing of the subscription</li> 
 * </ol>
 * @author Michael Crouch
 *
 */
public class Subscription implements java.io.Serializable {
    private eu.linksmart.caf.Attribute[] attributes;

    private java.lang.String dataId;

    private eu.linksmart.caf.Parameter[] parameters;

    private java.lang.String protocol;

    public Subscription() {
    }

    public Subscription(
           eu.linksmart.caf.Attribute[] attributes,
           java.lang.String dataId,
           eu.linksmart.caf.Parameter[] parameters,
           java.lang.String protocol) {
           this.attributes = attributes;
           this.dataId = dataId;
           this.parameters = parameters;
           this.protocol = protocol;
    }


    /**
     * Gets the attributes value for this Subscription.
     * 
     * @return attributes
     */
    public eu.linksmart.caf.Attribute[] getAttributes() {
        return attributes;
    }


    /**
     * Sets the attributes value for this Subscription.
     * 
     * @param attributes
     */
    public void setAttributes(eu.linksmart.caf.Attribute[] attributes) {
        this.attributes = attributes;
    }


    /**
     * Gets the dataId value for this Subscription.
     * 
     * @return dataId
     */
    public java.lang.String getDataId() {
        return dataId;
    }


    /**
     * Sets the dataId value for this Subscription.
     * 
     * @param dataId
     */
    public void setDataId(java.lang.String dataId) {
        this.dataId = dataId;
    }


    /**
     * Gets the parameters value for this Subscription.
     * 
     * @return parameters
     */
    public eu.linksmart.caf.Parameter[] getParameters() {
        return parameters;
    }


    /**
     * Sets the parameters value for this Subscription.
     * 
     * @param parameters
     */
    public void setParameters(eu.linksmart.caf.Parameter[] parameters) {
        this.parameters = parameters;
    }


    /**
     * Gets the protocol value for this Subscription.
     * 
     * @return protocol
     */
    public java.lang.String getProtocol() {
        return protocol;
    }


    /**
     * Sets the protocol value for this Subscription.
     * 
     * @param protocol
     */
    public void setProtocol(java.lang.String protocol) {
        this.protocol = protocol;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Subscription)) return false;
        Subscription other = (Subscription) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.attributes==null && other.getAttributes()==null) || 
             (this.attributes!=null &&
              java.util.Arrays.equals(this.attributes, other.getAttributes()))) &&
            ((this.dataId==null && other.getDataId()==null) || 
             (this.dataId!=null &&
              this.dataId.equals(other.getDataId()))) &&
            ((this.parameters==null && other.getParameters()==null) || 
             (this.parameters!=null &&
              java.util.Arrays.equals(this.parameters, other.getParameters()))) &&
            ((this.protocol==null && other.getProtocol()==null) || 
             (this.protocol!=null &&
              this.protocol.equals(other.getProtocol())));
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
        if (getAttributes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAttributes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAttributes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDataId() != null) {
            _hashCode += getDataId().hashCode();
        }
        if (getParameters() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParameters());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParameters(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getProtocol() != null) {
            _hashCode += getProtocol().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
