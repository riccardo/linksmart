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
 * Class containing the definition of properties and classes for Device ontology.
 * 
 * @author Peter Kostelnik
 *
 */
public class Device {
	// basics
	public final static LiteralProperty isDeviceTemplate = 
		new LiteralProperty(Namespace.device + "isDeviceTemplate");

	public final static ResourceProperty clonedFromTemplate = 
		new ResourceProperty(Namespace.device + "clonedFromTemplate");

	public final static LiteralProperty PID = 
		new LiteralProperty(Namespace.device + "PID");

	public final static LiteralProperty HID = 
		new LiteralProperty(Namespace.device + "HID");

	public final static LiteralProperty deviceUPnPType = 
		new LiteralProperty(Namespace.device + "deviceUPnPType");

	// SemanticDevice
	public final static ResourceProperty hasPrecondition = 
		new ResourceProperty(Namespace.device + "hasPrecondition");

	public final static OntologyClass SemanticDevicePrecondition = 
		new OntologyClass(Namespace.device + "SemanticDevicePrecondition");

	public final static LiteralProperty preconditionId = 
		new LiteralProperty(Namespace.device + "preconditionId");

	public final static LiteralProperty preconditionDeviceType = 
		new LiteralProperty(Namespace.device + "preconditionDeviceType");

	public final static ResourceProperty isSatisfiedBy = 
		new ResourceProperty(Namespace.device + "isSatisfiedBy");

	public final static OntologyClass SemanticDevicePIDPrecondition = 
		new OntologyClass(Namespace.device + "SemanticDevicePIDPrecondition");

	public final static LiteralProperty preconditionPID = 
		new LiteralProperty(Namespace.device + "preconditionPID");

	public final static OntologyClass SemanticDeviceQueryPrecondition = 
		new OntologyClass(Namespace.device + "SemanticDeviceQueryPrecondition");

	public final static LiteralProperty preconditionQuery = 
		new LiteralProperty(Namespace.device + "preconditionQuery");

	public final static LiteralProperty preconditionCardinality = 
		new LiteralProperty(Namespace.device + "preconditionCardinality");

	// Temporary property used for storing evaluation
	// value in semantic device discovery. Presented
	// only while rediscovery process is running to
	// help recursive evaluation of embedded semantic
	// devices.
	// You will (hopefully) not find this in ontology. 
	public final static LiteralProperty evaluation = 
		new LiteralProperty(Namespace.device + "evaluation");

	// Temporary property to distinguish testing instances created
	// manually in IDE (for testing purposes) from real run-time
	// devices. In application run-time, all testing devices 
	// must be removed.
	public final static LiteralProperty isTesting = 
		new LiteralProperty(Namespace.device + "isTesting");

	// Temporary property generated when device discovery fails.
	public final static LiteralProperty errorMessage = 
		new LiteralProperty(Namespace.device + "errorMessage");

	// Temporary property generated when device discovery fails.
	public final static ResourceProperty errorTemplateURI = 
		new ResourceProperty(Namespace.device + "errorTemplateURI");

	// InfoDescrption
	public static final OntologyClass InfoDescription = 
		new OntologyClass(Namespace.device + "InfoDescription");

	public final static ResourceProperty info = 
		new ResourceProperty(Namespace.device + "info");

	public final static LiteralProperty  friendlyName = 
		new LiteralProperty(Namespace.device + "friendlyName");

	public final static LiteralProperty manufacturer = 
		new LiteralProperty(Namespace.device + "manufacturer");

	public final static LiteralProperty manufacturerURL = 
		new LiteralProperty(Namespace.device + "manufacturerURL");

	public final static LiteralProperty modelDescription = 
		new LiteralProperty(Namespace.device + "modelDescription");

	public final static LiteralProperty modelName = 
		new LiteralProperty(Namespace.device + "modelName");

	public final static LiteralProperty modelNumber = 
		new LiteralProperty(Namespace.device + "modelNumber");


	// Service
	public final static ResourceProperty hasService = 
		new ResourceProperty(Namespace.device + "hasService");

	// Discovery
	public final static ResourceProperty hasDiscoveryInfo = 
		new ResourceProperty(Namespace.device + "hasDiscoveryInfo");

	// Event
	public final static ResourceProperty hasEvent = 
		new ResourceProperty(Namespace.device + "hasEvent");

	// Energy
	public final static ResourceProperty hasEnergyProfile = 
		new ResourceProperty(Namespace.device + "hasEnergyProfile");

	// Configuration
	public final static ResourceProperty hasConfiguration = 
		new ResourceProperty(Namespace.device + "hasConfiguration");

	// Hardware
	public final static ResourceProperty hasHardware = 
		new ResourceProperty(Namespace.device + "hasHardware");

	// Taxonomy
	public static final OntologyClass Device = 
		new OntologyClass(Namespace.device + "Device");

	public static final OntologyClass PhysicalDevice = 
		new OntologyClass(Namespace.device + "PhysicalDevice");

	public static final OntologyClass SemanticDevice = 
		new OntologyClass(Namespace.device + "SemanticDevice");
}
