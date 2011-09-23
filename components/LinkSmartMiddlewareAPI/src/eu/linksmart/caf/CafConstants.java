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
package eu.linksmart.caf;

/**
 * List of constant variables for Ids, keys etc, in the 
 * Context Awareness Framework
 * 
 * @author Michael Crouch
 *
 */
public class CafConstants {

	/**
	 * Data Acquisition Component
	 * 
	 * PUSH PROTOCOL
	 */
	
	/** ID of the PUSH Protocol */
	public static final String PUSH_PROTOCOL = "daqc:protocol:push";
	
	/** Attribute ID for the topic of the Event */
	public static final String PUSH_TOPIC = "Event.Topic";
	
	/**
	 * Data Acquisition Component
	 * 
	 * PULL PROTOCOL
	 */
	
	/** ID of the PULL Protocol */
	public static final String PULL_PROTOCOL = "daqc:protocol:pull";
		
	/** Attribute ID for the method to call */
	public static final String PULL_METHOD = "Pull.METHOD";
	
	/** Attribute ID for the frequency of the call */
	public static final String PULL_FREQUENCY = "Pull.FREQUENCY";
	
	/** Attribute ID for the namespace of the call */
	public static final String PULL_NAMESPACE = "Pull.NAMESPACE";
	
	/** Attribute ID for the SOAP Action of the call */
	public static final String PULL_SOAPACTION = "Pull.SOAPACTION";
	
	/** Attribute ID for the return type of the call */
	public static final String PULL_RETURNTYPE = "Pull.RETURNTYPE";
	
	/** Attribute ID for the PID of the data source */
	public static final String DATASOURCE_PID = "Datasource.PID";
	
	/** Attribute ID for the SID of the data source */
	public static final String DATASOURCE_SID = "Datasource.SID";
	
	/** Attribute ID for the HID of the data source */
	public static final String DATASOURCE_HID = "Datasource.HID";
	
	/**
	 * 
	 * Context Manager
	 * 
	 */
	
	
	
}
