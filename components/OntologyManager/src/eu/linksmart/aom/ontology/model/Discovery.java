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
 * Copyright (C) 2006-2010 Technical University of Kosice
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

package eu.linksmart.aom.ontology.model;

import eu.linksmart.aom.ontology.schema.LiteralProperty;
import eu.linksmart.aom.ontology.schema.OntologyClass;
import eu.linksmart.aom.ontology.schema.ResourceProperty;

/**
 * Class containing the definition of properties and classes for Discovery ontology.
 * 
 * @author Peter Kostelnik
 *
 */
public class Discovery {
	public static final OntologyClass DiscoveryInfo = 
		new OntologyClass(Namespace.discovery + "DiscoveryInfo");

	// TellStick
	public static final OntologyClass TellStick = 
		new OntologyClass(Namespace.discovery + "TellStick");

	public final static LiteralProperty tsName = 
		new LiteralProperty(Namespace.discovery + "tsName");

	public final static LiteralProperty tsVendor = 
		new LiteralProperty(Namespace.discovery + "tsVendor");


	// RFIDTag
	public static final OntologyClass RFIDTag = 
		new OntologyClass(Namespace.discovery + "RFIDTag");

	public final static LiteralProperty rfidVendor = 
		new LiteralProperty(Namespace.discovery + "rfidVendor");

	// Sensor
	public static final OntologyClass Sensor = 
		new OntologyClass(Namespace.discovery + "Sensor");

	public final static LiteralProperty sType = 
		new LiteralProperty(Namespace.discovery + "sType");

	public final static LiteralProperty sVendor = 
		new LiteralProperty(Namespace.discovery + "sVendor");

	// PhysicalDeviceSource
	public static final OntologyClass PhysicalDeviceSource = 
		new OntologyClass(Namespace.discovery + "PhysicalDeviceSource");

	public final static LiteralProperty pdsDeviceType = 
		new LiteralProperty(Namespace.discovery + "pdsDeviceType");

	public final static LiteralProperty pdsVendor = 
		new LiteralProperty(Namespace.discovery + "pdsVendor");


	
	// BlueTooth
	public static final OntologyClass BlueTooth = 
		new OntologyClass(Namespace.discovery + "BlueTooth");

	public final static LiteralProperty btName = 
		new LiteralProperty(Namespace.discovery + "btName");

	public final static LiteralProperty btType = 
		new LiteralProperty(Namespace.discovery + "btType");

	public final static LiteralProperty btClass = 
		new LiteralProperty(Namespace.discovery + "btClass");

	// BlueTooth service
	public static final OntologyClass BlueToothService = 
		new OntologyClass(Namespace.discovery + "BlueToothService");

	public final static ResourceProperty btService = 
		new ResourceProperty(Namespace.discovery + "btService");

	public final static LiteralProperty btServiceName = 
		new LiteralProperty(Namespace.discovery + "btServiceName");

	public final static LiteralProperty btServiceType = 
		new LiteralProperty(Namespace.discovery + "btServiceType");
}
