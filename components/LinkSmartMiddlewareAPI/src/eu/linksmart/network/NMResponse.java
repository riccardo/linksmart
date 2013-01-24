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
 * Network Manager response type
 */
public class NMResponse  {

	private java.lang.String data;
	private java.lang.String sessionID;

	private java.lang.Object __equalsCalc = null;
	private boolean __hashCodeCalc = false;

	/**
	 * Default constructor
	 */
	public NMResponse() {}
	
	/**
	 * Constructor
	 * 
	 * @param data the data value for this NMResponse
	 * @param sessionID the sessionID value for this NMResponse
	 */
	public NMResponse(java.lang.String data, java.lang.String sessionID) {
		this.data = data;
		this.sessionID = sessionID;
	}

	/**
	 * Gets the data value for this NMResponse
	 * 
	 * @return the data value for this NMResponse
	 */
	public java.lang.String getData() {
		return data;
	}

	/**
	 * Sets the data value for this NMResponse
	 * 
	 * @param data the data value for this NMResponse
	 */
	public void setData(java.lang.String data) {
		this.data = data;
	}

	/**
	 * Gets the sessionID value for this NMResponse
	 * 
	 * @return the sessionID value for this NMResponse
	 */
	public java.lang.String getSessionID() {
		return sessionID;
	}

	/**
	 * Sets the sessionID value for this NMResponse
	 * 
	 * @param sessionID the sessionID value for this NMResponse
	 */
	public void setSessionID(java.lang.String sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * Returns true if the object "obj" is "equal to" this one.
	 * 
	 * @param obj the object to compare
	 * @return true if the object "obj" is "equal to" this one 
	 */
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof NMResponse)) {
			return false;
		}
		
		NMResponse other = (NMResponse) obj;
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
			&& (((this.data == null) && (other.getData() == null))
				|| ((this.data != null) && this.data.equals(other.getData())))
			&& (((this.sessionID == null) && (other.getSessionID() == null))
				|| ((this.sessionID != null) && this.sessionID.equals(other.getSessionID())));
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
		if (getData() != null) {
			_hashCode += getData().hashCode();
		}
		if (getSessionID() != null) {
			_hashCode += getSessionID().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

}
