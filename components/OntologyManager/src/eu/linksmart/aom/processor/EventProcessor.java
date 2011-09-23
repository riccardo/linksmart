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

package eu.linksmart.aom.processor;

import java.util.List;

import org.jdom.Element;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.XMLSchema;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Event;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.schema.OntologyClass;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;

/**
 * Class responsible for generation the ontology model from device events XML.
 * 
 * @author Peter Smatana, Peter Kostelnik
 *
 */
public class EventProcessor  extends Processor {
	ValueFactory f;
	public EventProcessor(AOMRepository repository) {
		super(repository);
		this.f = repository.getValueFactory();
	}


	/**
	 * Generates the semantic model of event metainformation and adds it to events model.
	 * @param metaInformation XML of event metainformation.
	 * @param eventURI Actualy processed event URI.
	 * @param g Semantic model of device events.
	 */
	public void processMetaInformation(Element metaInformation, String eventURI, Graph g) {
		org.jdom.Namespace ns = metaInformation.getNamespace();  
		String metaInformationURI = repository.getURI(Event.MetaInformation); 
		ResourceUtil.addStatement(
				eventURI, 
				Event.hasMetaInformation, 
				metaInformationURI, 
				f, 
				g);

		ResourceUtil.addStatement(
				metaInformationURI, 
				Rdf.rdfType, 
				Event.MetaInformation.stringValue(), 
				f, 
				g);

		ResourceUtil.addStatement(
				metaInformationURI, 
				Event.trigger, 
				metaInformation.getChildTextTrim("eventTrigger", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				metaInformationURI, 
				Event.description, 
				metaInformation.getChildTextTrim("description", ns), 
				XMLSchema.STRING, 
				f, 
				g);
		Element frequency = metaInformation.getChild("frequency", ns);
		String value = frequency.getChildTextTrim("value");
		String unit = frequency.getChildTextTrim("unit");
		Graph unitG = repository.getUnitValue(value, unit);

		ResourceUtil.addStatement(
				metaInformationURI, 
				Event.frequency, 
				unitG.getBaseURI(), 
				f, 
				g);

		g.add(unitG);
	}

	/**
	 * Generates the semantic model of event key and adds it to events model.
	 * @param eventKey XML of event key.
	 * @param eventURI Actualy processed event URI.
	 * @param g Semantic model of device events.
	 */
	public void processEventKey(Element eventKey, String eventURI, Graph g) {
		org.jdom.Namespace ns = eventKey.getNamespace(); 
		String eventKeyURI = repository.getURI(Event.EventKey); 
		ResourceUtil.addStatement(
				eventURI, 
				Event.hasKey, 
				eventKeyURI, 
				f, 
				g);

		ResourceUtil.addStatement(
				eventKeyURI, 
				Rdf.rdfType, 
				Event.EventKey.stringValue(), 
				f, 
				g);

		String valueUnit = eventKey.getChildTextTrim("unit", ns);
		if (valueUnit != null){
			String unitURI = repository.getUnit(valueUnit);
			if (unitURI != null) {
				ResourceUtil.addStatement(
						eventKeyURI, 
						Event.valueUnit, 
						unitURI, 
						f, 
						g);
			}
		}
		ResourceUtil.addStatement(
				eventKeyURI, 
				Event.keyName, 
				eventKey.getChildTextTrim("name", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				eventKeyURI, 
				Event.dataType, 
				eventKey.getChildTextTrim("dataType", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		String relatedStateVariable = eventKey.getChildTextTrim("relatedStateVariable", ns);
		if (relatedStateVariable != null) {
			ResourceUtil.addStatement(
					eventKeyURI, 
					Event.relatedStateVariable, 
					relatedStateVariable, 
					XMLSchema.STRING, 
					f, 
					g);
		}
		Element allowedValueRange = eventKey.getChild("allowedValueRange", ns); 
		if (allowedValueRange != null){
			ResourceUtil.addStatement(
					eventKeyURI, 
					Event.maxValue, 
					allowedValueRange.getChildTextTrim("max", ns), 
					XMLSchema.STRING, 
					f, 
					g);

			ResourceUtil.addStatement(
					eventKeyURI, 
					Event.minValue, 
					allowedValueRange.getChildTextTrim("min", ns), 
					XMLSchema.STRING, 
					f, 
					g);
		}
	}

	/**
	 * Generates the semantic model of device event and adds it to model of all device events.
	 * @param event XML of device event.
	 * @param g Semantic model of events.
	 */
	public void processEvent(Element event, Graph g) {
		org.jdom.Namespace ns = event.getNamespace();  

		String eventType = event.getChildTextTrim("eventType", ns);
		String eventTopic = event.getChildTextTrim("eventTopic", ns);
		OntologyClass ontologyClass= null;
		String classURI = Namespace.event + eventType;
		if (repository.classExists(classURI)){
			ontologyClass = new OntologyClass(classURI);
		} else {
			ontologyClass = Event.Event;
		}
		String eventURI = repository.getURI(ontologyClass);
		String deviceURI = g.getBaseURI();
		ResourceUtil.addStatement(deviceURI, Device.hasEvent, eventURI, f, g);
		ResourceUtil.addStatement(eventURI, Rdf.rdfType, ontologyClass.stringValue(), f, g);
		ResourceUtil.addStatement(eventURI, Event.hasTopic, eventTopic, XMLSchema.STRING, f, g);

		Element metaInformation = event.getChild("metaInformation", ns);
		processMetaInformation(metaInformation, eventURI, g);

		List<Element> eventKeys = event.getChild("eventKeyList", ns).getChildren("eventKey", ns);
		for(Element eventKey: eventKeys){
			processEventKey(eventKey, eventURI, g);
		}
	}

	/**
	 * Generates the semantic model from device events XML and attaches it to device.
	 * @param deviceURI Ontology URI of device.
	 * @param xml Events XML.
	 * @return Semantic model of device events.
	 */
	public Graph process(String deviceURI, String xml){
		Element root = parse(xml);

		if(root == null) return null;

		List<Element> events = root.getChildren("event");
		Graph g = new Graph(deviceURI);
		for(Element event: events){
			processEvent(event, g);
		}
		return g;
	}
}
