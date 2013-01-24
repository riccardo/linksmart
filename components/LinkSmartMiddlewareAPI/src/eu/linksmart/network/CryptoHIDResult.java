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

package eu.linksmart.network;

/**
 * CryptoHID result type
 */
public class CryptoHIDResult  implements java.io.Serializable {
	
	private java.lang.String HID;
	private java.lang.String certRef;

	private java.lang.Object __equalsCalc = null;
	private boolean __hashCodeCalc = false;
	
	/**
	 * Default constructor
	 */
	public CryptoHIDResult() {}

	/**
	 * Constructor
	 * 
	 * @param HID the HID value for this CryptoHIDResult
	 * @param certRef the certRef value for this CryptoHIDResult
	 */
	public CryptoHIDResult(java.lang.String HID, java.lang.String certRef) {
		this.HID = HID;
		this.certRef = certRef;
	}

	/**
	 * Gets the HID value for this CryptoHIDResult
	 * 
	 * @return the HID value for this CryptoHIDResult
	 */
	public java.lang.String getHID() {
		return HID;
	}

	/**
	 * Sets the HID value for this CryptoHIDResult
	 * 
	 * @param HID the HID value for this CryptoHIDResult
	 */
	public void setHID(java.lang.String HID) {
		this.HID = HID;
	}

	/**
	 * Gets the certRef value for this CryptoHIDResult
	 * 
	 * @return the certRef value for this CryptoHIDResult
	 */
	public java.lang.String getCertRef() {
		return certRef;
	}

	/**
	 * Sets the certRef value for this CryptoHIDResult
	 * 
	 * @param certRef the certRef value for this CryptoHIDResult
	 */
	public void setCertRef(java.lang.String certRef) {
		this.certRef = certRef;
	}

	/**
	 * Returns true if the object "obj" is "equal to" this one.
	 * 
	 * @param obj the object to compare
	 * @return true if the object "obj" is "equal to" this one 
	 */
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof CryptoHIDResult)) {
			return false;
		}
		
		CryptoHIDResult other = (CryptoHIDResult) obj;
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
		_equals = true
			&& (((this.HID == null) && (other.getHID() == null))
				|| ((this.HID != null) && this.HID.equals(other.getHID())))
			&& (((this.certRef == null) && (other.getCertRef() == null))
				|| ((this.certRef != null) && this.certRef.equals(other.getCertRef())));
		__equalsCalc = null;
		return _equals;
	}

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
		if (getHID() != null) {
			_hashCode += getHID().hashCode();
		}
		if (getCertRef() != null) {
			_hashCode += getCertRef().hashCode();
		}
		
		__hashCodeCalc = false;
		return _hashCode;
	}

}
