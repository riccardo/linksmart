/**
 * Registration.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.network;

public class Registration  implements java.io.Serializable {
    private eu.linksmart.utils.Part[] attributes;

    private java.lang.String description;

    private eu.linksmart.network.VirtualAddress virtualAddress;

    private java.lang.String virtualAddressAsString;

    public Registration() {
    }

    public Registration(
           eu.linksmart.utils.Part[] attributes,
           java.lang.String description,
           eu.linksmart.network.VirtualAddress virtualAddress,
           java.lang.String virtualAddressAsString) {
           this.attributes = attributes;
           this.description = description;
           this.virtualAddress = virtualAddress;
           this.virtualAddressAsString = virtualAddressAsString;
    }


    /**
     * Gets the attributes value for this Registration.
     * 
     * @return attributes
     */
    public eu.linksmart.utils.Part[] getAttributes() {
        return attributes;
    }


    /**
     * Sets the attributes value for this Registration.
     * 
     * @param attributes
     */
    public void setAttributes(eu.linksmart.utils.Part[] attributes) {
        this.attributes = attributes;
    }


    /**
     * Gets the description value for this Registration.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this Registration.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the virtualAddress value for this Registration.
     * 
     * @return virtualAddress
     */
    public eu.linksmart.network.VirtualAddress getVirtualAddress() {
        return virtualAddress;
    }


    /**
     * Sets the virtualAddress value for this Registration.
     * 
     * @param virtualAddress
     */
    public void setVirtualAddress(eu.linksmart.network.VirtualAddress virtualAddress) {
        this.virtualAddress = virtualAddress;
    }


    /**
     * Gets the virtualAddressAsString value for this Registration.
     * 
     * @return virtualAddressAsString
     */
    public java.lang.String getVirtualAddressAsString() {
        return virtualAddressAsString;
    }


    /**
     * Sets the virtualAddressAsString value for this Registration.
     * 
     * @param virtualAddressAsString
     */
    public void setVirtualAddressAsString(java.lang.String virtualAddressAsString) {
        this.virtualAddressAsString = virtualAddressAsString;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Registration)) return false;
        Registration other = (Registration) obj;
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
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.virtualAddress==null && other.getVirtualAddress()==null) || 
             (this.virtualAddress!=null &&
              this.virtualAddress.equals(other.getVirtualAddress()))) &&
            ((this.virtualAddressAsString==null && other.getVirtualAddressAsString()==null) || 
             (this.virtualAddressAsString!=null &&
              this.virtualAddressAsString.equals(other.getVirtualAddressAsString())));
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
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getVirtualAddress() != null) {
            _hashCode += getVirtualAddress().hashCode();
        }
        if (getVirtualAddressAsString() != null) {
            _hashCode += getVirtualAddressAsString().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Registration.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://network.linksmart.eu", "Registration"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attributes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "attributes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://utils.linksmart.eu", "Part"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://utils.linksmart.eu", "Part"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("virtualAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "virtualAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://network.linksmart.eu", "VirtualAddress"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("virtualAddressAsString");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "virtualAddressAsString"));
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
