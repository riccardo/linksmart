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
 * HIDInfo class stores all the information about an LinkSmart ID
 */

package eu.linksmart.network;

import java.util.Properties;

/**
 * Class to store information about HIDs
 */
public class HIDInfo {
	private HID hid;
	private Properties attributes;

	/**
	 * Create an HIDInfo object with the given HID and Description.
	 * 
	 * @param hid
	 * @param description
	 */
	public HIDInfo(HID hid, String description) {
		this.hid = hid;
		this.attributes = new Properties();
		this.attributes.put(HIDAttribute.DESCRIPTION.name(), description);
	}

	/**
	 * Create an HIDInfo object with the given HID and Attributes. Attributes
	 * should be key-values pairs with the key of type {@link HIDAttribute}
	 * 
	 * @param hid
	 * @param attributes
	 */
	public HIDInfo(HID hid, Properties attributes) {
		this.hid = hid;
		this.attributes = attributes;
	}

	
	/**
	 * Prints the the HIDInfo like this: hid:description
	 */
	@Override
	public String toString() {
		return hid.toString() + ":" + getDescription();
	}

	/**
	 * Return the attribute {@link HIDAttribute} DESCRIPTION for this HIDInfo.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return this.attributes.getProperty(HIDAttribute.DESCRIPTION.name());
	}

	/**
	 * Sets the {@link HIDAttribute} description for this HIDInfo
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		if (this.attributes == null){
			this.attributes = new Properties();
		}
		this.attributes.put(HIDAttribute.DESCRIPTION.name(), description);
	}

	/**
	 * Get the properties
	 * 
	 * @return the properties
	 */
	public Properties getAttributes() {
		return attributes;
	}

	/**
	 * Set the properties
	 * 
	 * @param attr
	 *            the properties to set
	 */
	public void setAttributes(Properties attr) {
		this.attributes = attr;
		if (attr.containsKey(HIDAttribute.DESCRIPTION.name())) {
			setDescription(attr.getProperty(HIDAttribute.DESCRIPTION.name()));
		}
	}

	public HID getHID() {
		return hid;
	}

	public void setHID(HID hid) {
		this.hid = hid;
	}

}
