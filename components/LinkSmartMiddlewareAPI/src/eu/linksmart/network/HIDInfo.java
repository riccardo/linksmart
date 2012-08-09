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
 * Copyright (C) 2006-2012 [Telefonica I+D, Fraunhofer FIT]
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
 * HIDInfo class stores all the information about an LinkSmart ID
 */

package eu.linksmart.network;

import java.util.Properties;
import java.io.Serializable;

import eu.linksmart.utils.Part;

/**
 * Class to store information about HIDs
 */
public class HIDInfo implements Serializable {
	private HID hid;
	private Part[] attributes;

	/**
	 * Create an HIDInfo object with the given HID and Description.
	 * 
	 * @param hid
	 * @param description
	 */
	public HIDInfo(HID hid, String description) {
		this.hid = hid;
		Part[] attributes = { new Part(HIDAttribute.DESCRIPTION.name(),
				description) };
		this.attributes = attributes;
	}

	/**
	 * Create an HIDInfo object with the given HID and Attributes. Attributes
	 * should be key-values pairs with the key of type {@link HIDAttribute}
	 * 
	 * @param hid
	 * @param attributes
	 */
	public HIDInfo(HID hid, Part[] attributes) {
		this.hid = hid;
		this.attributes = attributes;
	}

	/**
	 * Prints the the HIDInfo like this: hid:description
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(hid).append(": ");
		for (Part attribute : attributes) {
			sb.append(attribute.getValue()).append(";");
		}
		return sb.toString();
	}

	/**
	 * Return the attribute {@link HIDAttribute} DESCRIPTION for this HIDInfo.
	 * 
	 * @return the description, or null if no description exists
	 */
	@Deprecated
	public String getDescription() {
		if (this.attributes == null) {
			return null;
		}
		for (Part attribute : this.attributes) {
			if (attribute.getKey().equals(HIDAttribute.DESCRIPTION.name())) {
				return attribute.getValue();
			}
		}
		return null;
	}

	/**
	 * Sets the {@link HIDAttribute} description for this HIDInfo
	 * 
	 * @param description
	 *            the description to set
	 */
	@Deprecated public void setDescription(String description) {
		if (this.attributes == null) {
			Part[] attributes = { new Part(HIDAttribute.DESCRIPTION.name(),
					description) };
			this.attributes = attributes;
		} else {
			for (Part attribute : attributes) {
				if (attribute.getKey().equals(HIDAttribute.DESCRIPTION.name())) {
					attribute.setValue(description);
					return;
				}
			}
			// description does not exist yet
			Part[] attributes = new Part[this.attributes.length + 1];
			System.arraycopy(this.attributes, 0, attributes, 0,
					attributes.length);
			attributes[this.attributes.length] = new Part(
					HIDAttribute.DESCRIPTION.name(), description);
			this.attributes = attributes;
		}
	}

	/**
	 * Get the properties
	 * 
	 * @return the properties
	 */
	public Part[] getAttributes() {
		return attributes;
	}

	/**
	 * Set the properties.
	 * 
	 * At the moment it replaces the existing properties with the array
	 * TODO make this method really do what it's meant to do ;)
	 * @param attr
	 *            the properties to set
	 */
	public void setAttributes(Part[] attr) {
		this.attributes = attr;
//		if (attr.containsKey(HIDAttribute.DESCRIPTION.name())) {
//			setDescription(attr.getProperty(HIDAttribute.DESCRIPTION.name()));
//		}
	}

	public HID getHID() {
		return hid;
	}
	
	public String getHIDAsString(){
		return hid.toString();
	}

	public void setHID(HID hid) {
		this.hid = hid;
	}

    private java.lang.Object __equalsCalc = null;
    /* Adapted from AXIS-generated code
     */
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof HIDInfo)) return false;
        HIDInfo other = (HIDInfo) obj;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.hid==null && other.getHID()==null) || 
             (this.hid!=null &&
              this.hid.equals(other.getHID()))) &&
            ((this.attributes==null && other.getAttributes()==null) || 
             (this.attributes!=null &&
              java.util.Arrays.equals(this.attributes, other.getAttributes())));
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
        if (getHID() != null) {
            _hashCode += getHID().hashCode();
        }
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
        __hashCodeCalc = false;
        return _hashCode;
    }

	
}
