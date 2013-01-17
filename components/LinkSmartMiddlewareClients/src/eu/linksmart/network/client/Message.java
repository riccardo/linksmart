/**
 * Message.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.network.client;

public class Message  implements java.io.Serializable {
    private byte[] data;

    private java.lang.String[] keySet;

    private eu.linksmart.network.client.VirtualAddress receiverVirtualAddress;

    private eu.linksmart.network.client.VirtualAddress senderVirtualAddress;

    private java.lang.String topic;

    public Message() {
    }

    public Message(
           byte[] data,
           java.lang.String[] keySet,
           eu.linksmart.network.client.VirtualAddress receiverVirtualAddress,
           eu.linksmart.network.client.VirtualAddress senderVirtualAddress,
           java.lang.String topic) {
           this.data = data;
           this.keySet = keySet;
           this.receiverVirtualAddress = receiverVirtualAddress;
           this.senderVirtualAddress = senderVirtualAddress;
           this.topic = topic;
    }


    /**
     * Gets the data value for this Message.
     * 
     * @return data
     */
    public byte[] getData() {
        return data;
    }


    /**
     * Sets the data value for this Message.
     * 
     * @param data
     */
    public void setData(byte[] data) {
        this.data = data;
    }


    /**
     * Gets the keySet value for this Message.
     * 
     * @return keySet
     */
    public java.lang.String[] getKeySet() {
        return keySet;
    }


    /**
     * Sets the keySet value for this Message.
     * 
     * @param keySet
     */
    public void setKeySet(java.lang.String[] keySet) {
        this.keySet = keySet;
    }


    /**
     * Gets the receiverVirtualAddress value for this Message.
     * 
     * @return receiverVirtualAddress
     */
    public eu.linksmart.network.client.VirtualAddress getReceiverVirtualAddress() {
        return receiverVirtualAddress;
    }


    /**
     * Sets the receiverVirtualAddress value for this Message.
     * 
     * @param receiverVirtualAddress
     */
    public void setReceiverVirtualAddress(eu.linksmart.network.client.VirtualAddress receiverVirtualAddress) {
        this.receiverVirtualAddress = receiverVirtualAddress;
    }


    /**
     * Gets the senderVirtualAddress value for this Message.
     * 
     * @return senderVirtualAddress
     */
    public eu.linksmart.network.client.VirtualAddress getSenderVirtualAddress() {
        return senderVirtualAddress;
    }


    /**
     * Sets the senderVirtualAddress value for this Message.
     * 
     * @param senderVirtualAddress
     */
    public void setSenderVirtualAddress(eu.linksmart.network.client.VirtualAddress senderVirtualAddress) {
        this.senderVirtualAddress = senderVirtualAddress;
    }


    /**
     * Gets the topic value for this Message.
     * 
     * @return topic
     */
    public java.lang.String getTopic() {
        return topic;
    }


    /**
     * Sets the topic value for this Message.
     * 
     * @param topic
     */
    public void setTopic(java.lang.String topic) {
        this.topic = topic;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Message)) return false;
        Message other = (Message) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.data==null && other.getData()==null) || 
             (this.data!=null &&
              java.util.Arrays.equals(this.data, other.getData()))) &&
            ((this.keySet==null && other.getKeySet()==null) || 
             (this.keySet!=null &&
              java.util.Arrays.equals(this.keySet, other.getKeySet()))) &&
            ((this.receiverVirtualAddress==null && other.getReceiverVirtualAddress()==null) || 
             (this.receiverVirtualAddress!=null &&
              this.receiverVirtualAddress.equals(other.getReceiverVirtualAddress()))) &&
            ((this.senderVirtualAddress==null && other.getSenderVirtualAddress()==null) || 
             (this.senderVirtualAddress!=null &&
              this.senderVirtualAddress.equals(other.getSenderVirtualAddress()))) &&
            ((this.topic==null && other.getTopic()==null) || 
             (this.topic!=null &&
              this.topic.equals(other.getTopic())));
        __equalsCalc = null;
        return _equals;
    }


    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Message.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://network.linksmart.eu", "Message"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("data");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "data"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("keySet");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "keySet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://networkmanager.network.linksmart.eu/", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receiverVirtualAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "receiverVirtualAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://network.linksmart.eu", "VirtualAddress"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("senderVirtualAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "senderVirtualAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://network.linksmart.eu", "VirtualAddress"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("topic");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "topic"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
