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

package eu.linksmart.aom.generator;

import java.util.Set;

import org.jdom.Element;
import org.openrdf.model.impl.URIImpl;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Event;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.model.Unit;


/**
 * Class responsible for generation of XML containing the device events from 
 * semantic models of event information attached to device.
 * 
 * @author Peter Smatana, Peter Kostelnik
 *
 */
public class EventGenerator extends Generator {

	/**
	 * Generates the XML containing device events from semantic model of device.
	 * @param g Semantic model of device.
	 * @param superElm Device XML element to be extended.
	 * @return XML containing description of device events.
	 */
	public Element process(Graph g, Element superElm) {
		Element event = addElement("event", superElm);
		String type = new URIImpl(g.value(Rdf.rdfType)).getLocalName();
		addElement("eventType", type, event);
		addElement("eventTopic", g.value(Event.hasTopic), event);
		Graph metaInformation = g.subGraph(Event.hasMetaInformation);
		Element eventKeyList = addElement("eventKeyList", event);
		Set<Graph> eventKeys = g.subGraphs(Event.hasKey);
		for (Graph eventKey : eventKeys) {
			processEventKey(eventKey, eventKeyList);
		}
		processMetaInformation(metaInformation, event);
		return event;
	}

	/**
	 * Generates XML of event key from semantic model.
	 * @param eventKeyGraph Semantic model of event key.
	 * @param eventKeyList Parent element to be extended.
	 * @return Event key XML.
	 */
	public Element processEventKey(Graph eventKeyGraph, Element eventKeyList) {
		Element eventKey = element("eventKey", eventKeyList.getNamespace().getURI());
		String minValue = eventKeyGraph.value(Event.minValue);
		String maxValue = eventKeyGraph.value(Event.maxValue);
		if ((minValue != null) || (maxValue != null)) {
			Element allowedValueRange = addElement("allowedValueRange", eventKey);
			if (minValue != null) addElement("min", minValue, allowedValueRange);
			if (maxValue != null) addElement("max", maxValue, allowedValueRange);
		}

		String relatedStateVariable = eventKeyGraph.value(Event.relatedStateVariable);
		if (relatedStateVariable != null) addElement("relatedStateVariable", relatedStateVariable, eventKey);
		String valueUnit = eventKeyGraph.value(Event.valueUnit);
		if (valueUnit != null) addElement("unit", new URIImpl(valueUnit).getLocalName(), eventKey);

		String name = eventKeyGraph.value(Event.keyName);
		addElement("name", name, eventKey);

		String dataType = eventKeyGraph.value(Event.dataType);
		addElement("dataType", dataType, eventKey);

		addElement(eventKey, eventKeyList);
		return eventKey;
	}

	/**
	 * Generates XML of event metainformation from semantic model.
	 * @param metaInformationGraph Semantic model of event metainformation.
	 * @param event Parent element to be extended.
	 * @return Event metainformation XML.
	 */
	public Element processMetaInformation(Graph metaInformationGraph, Element event) {
		Element metaInformation = element("metaInformation", event.getNamespace().getURI());
		if(metaInformationGraph != null){

			Graph frequency = metaInformationGraph.subGraph(Event.frequency);
			if(frequency != null){
				String value = frequency.value(Unit.value);
				Element freq = addElement("frequency", metaInformation);
				String unitStr = frequency.value(Unit.inUnit);
				if (unitStr != null) {
					String unit = new URIImpl(unitStr).getLocalName();
					addElement("unit", unit, freq);
				}
				addElement("value", value, freq);
			}

			String eventTrigger = metaInformationGraph.value(Event.trigger);
			addElement("eventTrigger", eventTrigger, metaInformation);

			String description = metaInformationGraph.value(Event.description);
			addElement("description", description, metaInformation);

		}
		addElement(metaInformation, event);
		return metaInformation;
	}
}
