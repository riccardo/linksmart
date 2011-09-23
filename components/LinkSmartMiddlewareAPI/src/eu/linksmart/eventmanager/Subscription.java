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


public class Subscription implements java.io.Serializable {

	private java.util.Calendar date;
	private java.lang.String topic;
	private String URL;
	private java.lang.String HID;

	/**
	 * Default Constructor
	 */
	public Subscription() {}

	/**
	 * Constructor
	 * 
	 * @param date the date value for this Subscription
	 * @param topic the topic value for this Subscription
	 * @param URL the URL value for this Subscription
	 * @param HID the HID value for this Subscription
	 */
	public Subscription(java.util.Calendar date, java.lang.String topic,
			String URL, java.lang.String HID) {
		
		this.date = date;
		this.topic = topic;
		this.URL = URL;
		this.HID = HID;
	}

	/**
	 * Gets the date value for this Subscription
	 * 
	 * @return the date value for this Subscription
	 */
	public java.util.Calendar getDate() {
		return date;
	}

	/**
	 * Sets the date value for this Subscription
	 * 
	 * @param date the date value for this Subscription
	 */
	public void setDate(java.util.Calendar date) {
		this.date = date;
	}


	/**
	 * Gets the topic value for this Subscription
	 * 
	 * @return the topic value for this Subscription
	 */
	public java.lang.String getTopic() {
		return topic;
	}

	/**
	 * Sets the topic value for this Subscription
	 * 
	 * @param topic the topic value for this Subscription
	 */
	public void setTopic(java.lang.String topic) {
		this.topic = topic;
	}

	/**
	 * Gets the URL value for this Subscription
	 * 
	 * @return the URL value for this Subscription
	 */
	public String getURL() {
		return URL;
	}

	/**
	 * Sets the URL value for this Subscription
	 * 
	 * @param URL the URL value for this Subscription
	 */
	public void setURL(String URL) {
		this.URL = URL;
	}

	/**
	 * Gets the HID value for this Subscription
	 * 
	 * @return the HID value for this Subscription
	 */
	public java.lang.String getHID() {
		return HID;
	}

	/**
	 * Sets the HID value for this Subscription
	 * 
	 * @param HID the HID value for this Subscription
	 */
	public void setHID(java.lang.String HID) {
		this.HID = HID;
	}

	private java.lang.Object __equalsCalc = null;
	
	/**
	 * Returns true if the object "obj" is "equal to" this one.
	 * 
	 * @param obj the object to compare
	 * @return true if the object "obj" is "equal to" this one 
	 */
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Subscription)) {
			return false;
		}
		
		Subscription other = (Subscription) obj;
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && 
			((this.date == null && other.getDate() == null) || 
				(this.date != null && this.date.equals(other.getDate()))) &&
			((this.topic == null && other.getTopic() == null) ||
				(this.topic != null && this.topic.equals(other.getTopic()))) &&
			((this.URL == null && other.getURL() == null) ||
				(this.URL != null && this.URL.equals(other.getURL()))) &&
			((this.HID == null && other.getHID() == null) ||
				(this.HID != null && this.HID.equals(other.getHID())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;
	
	/**
	 * Returns a hash code value for the object
	 * 
	 * @return a hash code value for the object
	 */
	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		if (getDate() != null) {
			_hashCode += getDate().hashCode();
		}
		if (getTopic() != null) {
			_hashCode += getTopic().hashCode();
		}
		if (getURL() != null) {
			_hashCode += getURL().hashCode();
		}
		if (getHID() != null) {
			_hashCode += getHID().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	/**
	 * Returns the string representation of Subscription
	 * 
	 * @return The string representation of subscription
	 */		
	@Override
	public String toString() {
		return "Subscriber URL: "+ URL + " HID: "+ HID + 
			" Topic: " + topic + " Date: "+ date;
	}

}
