///**
// * Copyright (C) 2006-2010 [Telefonica I+D]
// *                         the HYDRA consortium, EU project IST-2005-034891
// *
// * This file is part of LinkSmart.
// *
// * LinkSmart is free software: you can redistribute it and/or modify
// * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
// * version 3 as published by the Free Software Foundation.
// *
// * LinkSmart is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
// */
//
///**
// * This file was auto-generated from WSDL
// * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
// */
//
//package eu.linksmart.eventmanager.client;
//
//import eu.linksmart.eventmanager.Subscription;
//
//
//public class Subscription_Helper {
//	/* Type metadata. */
//	private static org.apache.axis.description.TypeDesc typeDesc =
//		new org.apache.axis.description.TypeDesc(Subscription.class, true);
//
//	static {
//		typeDesc.setXmlType(new javax.xml.namespace.QName(
//			"http://eventmanager.linksmart.eu", "subscription"));
//		org.apache.axis.description.ElementDesc elemField = 
//			new org.apache.axis.description.ElementDesc();
//		elemField.setFieldName("HID");
//		elemField.setXmlName(new javax.xml.namespace.QName(
//			"", "HID"));
//		elemField.setXmlType(new javax.xml.namespace.QName(
//			"http://www.w3.org/2001/XMLSchema", "string"));
//		elemField.setNillable(true);
//		typeDesc.addFieldDesc(elemField);
//		elemField = new org.apache.axis.description.ElementDesc();
//		elemField.setFieldName("URL");
//		elemField.setXmlName(new javax.xml.namespace.QName(
//			"", "URL"));
//		elemField.setXmlType(new javax.xml.namespace.QName(
//			"http://www.w3.org/2001/XMLSchema", "string"));
//		elemField.setNillable(true);
//		typeDesc.addFieldDesc(elemField);
//		elemField = new org.apache.axis.description.ElementDesc();
//		elemField.setFieldName("date");
//		elemField.setXmlName(new javax.xml.namespace.QName(
//			"", "date"));
//		elemField.setXmlType(new javax.xml.namespace.QName(
//			"http://www.w3.org/2001/XMLSchema", "dateTime"));
//		elemField.setNillable(true);
//		typeDesc.addFieldDesc(elemField);
//		elemField = new org.apache.axis.description.ElementDesc();
//		elemField.setFieldName("topic");
//		elemField.setXmlName(new javax.xml.namespace.QName(
//			"", "topic"));
//		elemField.setXmlType(new javax.xml.namespace.QName(
//			"http://www.w3.org/2001/XMLSchema", "string"));
//		elemField.setNillable(true);
//		typeDesc.addFieldDesc(elemField);
//	}
//
//	/**
//	 * Return type metadata object
//	 */
//	public static org.apache.axis.description.TypeDesc getTypeDesc() {
//		return typeDesc;
//	}
//		
//	/**
//	 * Get Custom Serializer
//	 */
//	public static org.apache.axis.encoding.Serializer getSerializer(
//			java.lang.String mechType, java.lang.Class _javaType,
//			javax.xml.namespace.QName _xmlType) {
//		
//		return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, 
//			_xmlType, typeDesc);
//	}
//
//	/**
//	 * Get Custom Deserializer
//	 */
//	public static org.apache.axis.encoding.Deserializer getDeserializer(
//			java.lang.String mechType, java.lang.Class _javaType,
//			javax.xml.namespace.QName _xmlType) {
//		
//		return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, 
//			_xmlType, typeDesc);
//	}
//
//}
