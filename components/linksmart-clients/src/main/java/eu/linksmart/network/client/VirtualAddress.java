/**
 * VirtualAddress.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.network.client;

public class VirtualAddress  implements java.io.Serializable {
    private byte[] bytes;

    private java.lang.Long contextID1;

    private java.lang.Long contextID2;

    private java.lang.Long contextID3;

    private java.lang.Long deviceID;

    private java.lang.Integer level;

    public VirtualAddress() {
    }

    public VirtualAddress(
           byte[] bytes,
           java.lang.Long contextID1,
           java.lang.Long contextID2,
           java.lang.Long contextID3,
           java.lang.Long deviceID,
           java.lang.Integer level) {
           this.bytes = bytes;
           this.contextID1 = contextID1;
           this.contextID2 = contextID2;
           this.contextID3 = contextID3;
           this.deviceID = deviceID;
           this.level = level;
    }


    /**
     * Gets the bytes value for this VirtualAddress.
     * 
     * @return bytes
     */
    public byte[] getBytes() {
        return bytes;
    }


    /**
     * Sets the bytes value for this VirtualAddress.
     * 
     * @param bytes
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }


    /**
     * Gets the contextID1 value for this VirtualAddress.
     * 
     * @return contextID1
     */
    public java.lang.Long getContextID1() {
        return contextID1;
    }


    /**
     * Sets the contextID1 value for this VirtualAddress.
     * 
     * @param contextID1
     */
    public void setContextID1(java.lang.Long contextID1) {
        this.contextID1 = contextID1;
    }


    /**
     * Gets the contextID2 value for this VirtualAddress.
     * 
     * @return contextID2
     */
    public java.lang.Long getContextID2() {
        return contextID2;
    }


    /**
     * Sets the contextID2 value for this VirtualAddress.
     * 
     * @param contextID2
     */
    public void setContextID2(java.lang.Long contextID2) {
        this.contextID2 = contextID2;
    }


    /**
     * Gets the contextID3 value for this VirtualAddress.
     * 
     * @return contextID3
     */
    public java.lang.Long getContextID3() {
        return contextID3;
    }


    /**
     * Sets the contextID3 value for this VirtualAddress.
     * 
     * @param contextID3
     */
    public void setContextID3(java.lang.Long contextID3) {
        this.contextID3 = contextID3;
    }


    /**
     * Gets the deviceID value for this VirtualAddress.
     * 
     * @return deviceID
     */
    public java.lang.Long getDeviceID() {
        return deviceID;
    }


    /**
     * Sets the deviceID value for this VirtualAddress.
     * 
     * @param deviceID
     */
    public void setDeviceID(java.lang.Long deviceID) {
        this.deviceID = deviceID;
    }


    /**
     * Gets the level value for this VirtualAddress.
     * 
     * @return level
     */
    public java.lang.Integer getLevel() {
        return level;
    }


    /**
     * Sets the level value for this VirtualAddress.
     * 
     * @param level
     */
    public void setLevel(java.lang.Integer level) {
        this.level = level;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VirtualAddress)) return false;
        VirtualAddress other = (VirtualAddress) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.bytes==null && other.getBytes()==null) || 
             (this.bytes!=null &&
              java.util.Arrays.equals(this.bytes, other.getBytes()))) &&
            ((this.contextID1==null && other.getContextID1()==null) || 
             (this.contextID1!=null &&
              this.contextID1.equals(other.getContextID1()))) &&
            ((this.contextID2==null && other.getContextID2()==null) || 
             (this.contextID2!=null &&
              this.contextID2.equals(other.getContextID2()))) &&
            ((this.contextID3==null && other.getContextID3()==null) || 
             (this.contextID3!=null &&
              this.contextID3.equals(other.getContextID3()))) &&
            ((this.deviceID==null && other.getDeviceID()==null) || 
             (this.deviceID!=null &&
              this.deviceID.equals(other.getDeviceID()))) &&
            ((this.level==null && other.getLevel()==null) || 
             (this.level!=null &&
              this.level.equals(other.getLevel())));
        __equalsCalc = null;
        return _equals;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VirtualAddress.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://network.linksmart.eu", "VirtualAddress"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bytes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "bytes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contextID1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "contextID1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contextID2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "contextID2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contextID3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "contextID3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "deviceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("level");
        elemField.setXmlName(new javax.xml.namespace.QName("http://network.linksmart.eu", "level"));
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
