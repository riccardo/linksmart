/**
 * Subscription.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.eventmanager;

public class Subscription  implements java.io.Serializable {
    private java.lang.String topic;

    private java.lang.String HID;

    private java.lang.String endpoint;

    private java.lang.String description;

    private int priority;

    private eu.linksmart.eventmanager.Part[] parts;

    private int numberOfRetries;

    private java.util.Calendar dateTime;

    public Subscription() {
    }

    public Subscription(
           java.lang.String topic,
           java.lang.String HID,
           java.lang.String endpoint,
           java.lang.String description,
           int priority,
           eu.linksmart.eventmanager.Part[] parts,
           int numberOfRetries,
           java.util.Calendar dateTime) {
           this.topic = topic;
           this.HID = HID;
           this.endpoint = endpoint;
           this.description = description;
           this.priority = priority;
           this.parts = parts;
           this.numberOfRetries = numberOfRetries;
           this.dateTime = dateTime;
    }


    /**
     * Gets the topic value for this Subscription.
     * 
     * @return topic
     */
    public java.lang.String getTopic() {
        return topic;
    }


    /**
     * Sets the topic value for this Subscription.
     * 
     * @param topic
     */
    public void setTopic(java.lang.String topic) {
        this.topic = topic;
    }


    /**
     * Gets the HID value for this Subscription.
     * 
     * @return HID
     */
    public java.lang.String getHID() {
        return HID;
    }


    /**
     * Sets the HID value for this Subscription.
     * 
     * @param HID
     */
    public void setHID(java.lang.String HID) {
        this.HID = HID;
    }


    /**
     * Gets the endpoint value for this Subscription.
     * 
     * @return endpoint
     */
    public java.lang.String getEndpoint() {
        return endpoint;
    }


    /**
     * Sets the endpoint value for this Subscription.
     * 
     * @param endpoint
     */
    public void setEndpoint(java.lang.String endpoint) {
        this.endpoint = endpoint;
    }


    /**
     * Gets the description value for this Subscription.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this Subscription.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the priority value for this Subscription.
     * 
     * @return priority
     */
    public int getPriority() {
        return priority;
    }


    /**
     * Sets the priority value for this Subscription.
     * 
     * @param priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }


    /**
     * Gets the parts value for this Subscription.
     * 
     * @return parts
     */
    public eu.linksmart.eventmanager.Part[] getParts() {
        return parts;
    }


    /**
     * Sets the parts value for this Subscription.
     * 
     * @param parts
     */
    public void setParts(eu.linksmart.eventmanager.Part[] parts) {
        this.parts = parts;
    }

    public eu.linksmart.eventmanager.Part getParts(int i) {
        return this.parts[i];
    }

    public void setParts(int i, eu.linksmart.eventmanager.Part _value) {
        this.parts[i] = _value;
    }


    /**
     * Gets the numberOfRetries value for this Subscription.
     * 
     * @return numberOfRetries
     */
    public int getNumberOfRetries() {
        return numberOfRetries;
    }


    /**
     * Sets the numberOfRetries value for this Subscription.
     * 
     * @param numberOfRetries
     */
    public void setNumberOfRetries(int numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }


    /**
     * Gets the dateTime value for this Subscription.
     * 
     * @return dateTime
     */
    public java.util.Calendar getDateTime() {
        return dateTime;
    }


    /**
     * Sets the dateTime value for this Subscription.
     * 
     * @param dateTime
     */
    public void setDateTime(java.util.Calendar dateTime) {
        this.dateTime = dateTime;
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
            ((this.topic==null && other.getTopic()==null) || 
             (this.topic!=null &&
              this.topic.equals(other.getTopic()))) &&
            ((this.HID==null && other.getHID()==null) || 
             (this.HID!=null &&
              this.HID.equals(other.getHID()))) &&
            ((this.endpoint==null && other.getEndpoint()==null) || 
             (this.endpoint!=null &&
              this.endpoint.equals(other.getEndpoint()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            this.priority == other.getPriority() &&
            ((this.parts==null && other.getParts()==null) || 
             (this.parts!=null &&
              java.util.Arrays.equals(this.parts, other.getParts()))) &&
            this.numberOfRetries == other.getNumberOfRetries() &&
            ((this.dateTime==null && other.getDateTime()==null) || 
             (this.dateTime!=null &&
              this.dateTime.equals(other.getDateTime())));
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
        if (getTopic() != null) {
            _hashCode += getTopic().hashCode();
        }
        if (getHID() != null) {
            _hashCode += getHID().hashCode();
        }
        if (getEndpoint() != null) {
            _hashCode += getEndpoint().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        _hashCode += getPriority();
        if (getParts() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParts());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParts(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getNumberOfRetries();
        if (getDateTime() != null) {
            _hashCode += getDateTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
