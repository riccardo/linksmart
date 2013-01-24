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
 * Class containing the definition of properties and classes for Event ontology.
 * 
 * @author Peter Kostelnik
 *
 */

public class Event {
	public static final OntologyClass Event = 
		new OntologyClass(Namespace.event + "Event");

	public static final OntologyClass EventKey = 
		new OntologyClass(Namespace.event + "EventKey");

	public static final OntologyClass MetaInformation = 
		new OntologyClass(Namespace.event + "MetaInformation");

	public final static LiteralProperty hasTopic = 
		new LiteralProperty(Namespace.event + "hasTopic");

	public final static ResourceProperty hasKey = 
		new ResourceProperty(Namespace.event + "hasKey");

	public final static ResourceProperty hasMetaInformation = 
		new ResourceProperty(Namespace.event + "hasMetaInformation");

	//MetaInformation
	public final static LiteralProperty description = 
		new LiteralProperty(Namespace.event + "description");

	public final static LiteralProperty trigger = 
		new LiteralProperty(Namespace.event + "trigger");

	public final static ResourceProperty frequency = 
		new ResourceProperty(Namespace.event + "frequency");

	//EventKey
	public final static LiteralProperty keyName = 
		new LiteralProperty(Namespace.event + "keyName");

	public final static ResourceProperty valueUnit = 
		new ResourceProperty(Namespace.event + "valueUnit");

	public final static LiteralProperty dataType = 
		new LiteralProperty(Namespace.event + "dataType");

	public final static LiteralProperty maxValue = 
		new LiteralProperty(Namespace.event + "maxValue");

	public final static LiteralProperty minValue = 
		new LiteralProperty(Namespace.event + "minValue");

	public final static LiteralProperty relatedStateVariable = 
		new LiteralProperty(Namespace.event + "relatedStateVariable");

}
