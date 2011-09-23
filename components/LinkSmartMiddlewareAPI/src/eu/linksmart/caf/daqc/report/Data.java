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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.caf.daqc.report;

import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.daqc.subscription.Subscription;


/**
 * Represents reported data from a {@link Subscription}, as part of a {@link DataReport}.<p>
 * Contains the id of the protocol used to retrieve the data, as well as a timestamp.<p>
 * The data is returned as an array of key-value {@link Attribute}s.
 * @author Michael Crouch
 *
 */
public class Data {
	private String timestamp;
	private String protocol;
	private Attribute[] attributes;
	
	/**
	 * Gets the Timestamp
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Sets the timestamp
	 * @param timestamp the timestamp
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Gets the protocol id
	 * @return the id
	 */
	public String getProtocol() {
		return protocol;
	}
	
	/**
	 * Sets the protcol id
	 * @param protocol the id
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	/**
	 * Gets the data as and array of {@link Attribute}s
	 * @return the {@link Attribute} array
	 */
	public Attribute[] getAttributes() {
		return attributes;
	}
	
	/**
	 * Sets the data as and array of {@link Attribute}s
	 * @param attributes the {@link Attribute} array
	 */
	public void setAttributes(Attribute[] attributes) {
		this.attributes = attributes;
	}
	
	
}
