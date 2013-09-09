/**
 * NMResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.network;

public class NMResponse  implements java.io.Serializable {
    private java.lang.String message;

    private eu.linksmart.network.Message messageObject;

    private java.lang.Integer status;

    public NMResponse() {
    }

    public NMResponse(
           java.lang.String message,
           eu.linksmart.network.Message messageObject,
           java.lang.Integer status) {
           this.message = message;
           this.messageObject = messageObject;
           this.status = status;
    }


    /**
     * Gets the message value for this NMResponse.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this NMResponse.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }


    /**
     * Gets the messageObject value for this NMResponse.
     * 
     * @return messageObject
     */
    public eu.linksmart.network.Message getMessageObject() {
        return messageObject;
    }


    /**
     * Sets the messageObject value for this NMResponse.
     * 
     * @param messageObject
     */
    public void setMessageObject(eu.linksmart.network.Message messageObject) {
        this.messageObject = messageObject;
    }


    /**
     * Gets the status value for this NMResponse.
     * 
     * @return status
     */
    public java.lang.Integer getStatus() {
        return status;
    }


    /**
     * Sets the status value for this NMResponse.
     * 
     * @param status
     */
    public void setStatus(java.lang.Integer status) {
        this.status = status;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NMResponse)) return false;
        NMResponse other = (NMResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            ((this.messageObject==null && other.getMessageObject()==null) || 
             (this.messageObject!=null &&
              this.messageObject.equals(other.getMessageObject()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus())));
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
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getMessageObject() != null) {
            _hashCode += getMessageObject().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NMResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://network.linksmart.eu", "NMResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messageObject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "messageObject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://network.linksmart.eu", "Message"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
