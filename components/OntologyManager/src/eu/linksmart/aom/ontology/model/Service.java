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
 * Class containing the definition of properties and classes for Service ontology.
 * 
 * @author Peter Kostelnik
 *
 */
public class Service {
	public final static LiteralProperty serviceOperation = 
		new LiteralProperty(Namespace.service + "serviceOperation");

	public final static LiteralProperty textDecription = 
		new LiteralProperty(Namespace.service + "textDecription");

	public final static LiteralProperty serviceCost = 
		new LiteralProperty(Namespace.service + "serviceCost");

	public final static ResourceProperty hasInput = 
		new ResourceProperty(Namespace.service + "hasInput");

	public final static ResourceProperty hasOutput = 
		new ResourceProperty(Namespace.service + "hasOutput");

	public final static LiteralProperty parameterName = 
		new LiteralProperty(Namespace.service + "parameterName");

	public final static LiteralProperty parameterType = 
		new LiteralProperty(Namespace.service + "parameterType");

	public final static ResourceProperty parameterUnit = 
		new ResourceProperty(Namespace.service + "parameterUnit");

	public final static LiteralProperty sendEvents = 
		new LiteralProperty(Namespace.service + "sendEvents");

	public final static LiteralProperty relatedStateVariable = 
		new LiteralProperty(Namespace.service + "relatedStateVariable");

	public final static OntologyClass PhysicalService = 
		new OntologyClass(Namespace.service + "PhysicalService");

	public final static OntologyClass ServiceInput = 
		new OntologyClass(Namespace.service + "ServiceInput");

	public final static OntologyClass ServiceOutput = 
		new OntologyClass(Namespace.service + "ServiceOutput");

	public final static LiteralProperty matchedService = 
		new LiteralProperty(Namespace.service + "matchedService");

}
