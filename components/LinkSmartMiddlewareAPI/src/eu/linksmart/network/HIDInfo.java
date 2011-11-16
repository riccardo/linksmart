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

	private String endpoint;
	private String description;
	private Properties attributes;
	
	/**
	 * Constructor. Creates an instance of HIDInfo with its parameters
	 * 
	 * @param description the description
	 * @param endpoint the endpoint
	 */
	public HIDInfo(String description, String endpoint) {
		
		super();
		this.endpoint = endpoint;
		this.description = description;
		this.attributes = null;
	}
	
	/**
	 * Constructor. Creates an instance of HIDInfo with its parameters
	 *  
	 * @param description the description
	 * @param endpoint the endpoint
	 * @param attributes the properties
	 */
	public HIDInfo(String description, String endpoint,
			Properties attributes) {
		
		super();
		this.endpoint = endpoint;
		this.description = description;
		this.attributes = attributes;
	}
	
	/**
	 * Returns a string with the HID info separated by ":"
	 * 
	 * @return the HID info separated by ":"
	 */
	@Override
	public String toString() {
		return description + ":" + endpoint;
	}


	

	
	/**
	 * Gets the endpoint
	 * 
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}
	
	/**
	 * Sets the endpoint
	 * 
	 * @param endpoint the endpoint to set
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	/**
	 * Gets the description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @param attr the properties to set
	 */
	public void setAttributes(Properties attr) {
		this.attributes = attr;
	}

}
