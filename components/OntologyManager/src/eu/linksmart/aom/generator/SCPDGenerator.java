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
import org.jdom.Namespace;
import org.openrdf.model.impl.URIImpl;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.processor.Processor;

/**
 * Class responsible for generation of XML containing the full device SCPD from 
 * semantic model of device. This class serves also as facade to all XML generators including generators of:
 * <ul>
 *   <li>configuration</li>
 *   <li>services</li>
 *   <li>events</li>
 *   <li>energy profiles</li>
 *   <li>semantic device preconditions</li>
 * </ul>
 * The full SCPD information is composed of the outlined parts.
 * 
 * @author Peter Smatana, Peter Kostelnik
 *
 */
public class SCPDGenerator extends Generator{

	private String className(String uri){
		try{
			return new URIImpl(uri).getLocalName();
		}
		catch(Exception e){}
		return "Device";
	}

	/**
	 * Generates the error information, which may occur in semantic device resolution process.
	 * @param g Error graph.
	 * @return XML of error occurred in semantic device resolution. 
	 */
	public Element error(Graph g) {
		Element error = new Element("error");
		error.setAttribute("message", g.value(Device.errorMessage));
		for(String templateURI : g.values(Device.errorTemplateURI)){
			addElement("matchedTemplateURI", templateURI, error);
		}

		return error;
	}

	/**
	 * Generates the full device SCPD XML using generators of particular SCPD parts including:
	 * <ul>
	 *   <li>manufacturer information</li>
	 *   <li>services</li>
	 *   <li>events</li>
	 *   <li>energy profiles</li>
	 *   <li>semantic device preconditions</li>
	 *   <li>configuration</li>
	 * </ul>
	 * @param g Device graph.
	 * @return Full device SCPD XML.
	 */
	public Element process(Graph g) {
		Element root = element("root", Processor.SCPD_DEVICE_NS);
		Element device = addElement("device", root);
		addElement("deviceURI", g.getBaseURI(), device);
		String pid = g.value(Device.PID);
		if(pid != null){
			addElement("PID", pid, device);
		}
		Element dClass = addElement("deviceClass", className(g.value(Rdf.rdfType)), device);
		dClass.setNamespace(Namespace.getNamespace(Processor.SCPD_EXTENSION));
		addElement("deviceType", g.value(Device.deviceUPnPType), device);
		processInfo(g.subGraph(Device.info), device);
		processServices(g, device);
		processEvents(g, device);
		processEnergyProfile(g, device);
		processPreconditions(g, device);
		return root;
	}

	/**
	 * Generates the list of SCPDs of devices identified in semantic device resolution process.
	 * @param devices Semantic models of devices.
	 * @return SCPD list of devices returned by semantic device resolution.
	 */
	public Element process(Set<Graph> devices) {
		Element scpdList = new Element("scpdList");
		for(Graph device : devices){
			addElement(process(device), scpdList);
		}
		return scpdList;
	}

	/**
	 * Generates XML of device manufacturer and model information from semantic model.
	 * @param infoGraph Basic device information semantic model.
	 * @param deviceElm Parent XML element.
	 * @return Extended device element.
	 */
	public Element processInfo(Graph infoGraph, Element deviceElm) {
		String friendlyName = infoGraph.value(Device.friendlyName);
		addElement("friendlyName", friendlyName, deviceElm);
		String manufacturer = infoGraph.value(Device.manufacturer);
		addElement("manufacturer", manufacturer, deviceElm);
		String manufacturerURL = infoGraph.value(Device.manufacturerURL);
		addElement("manufacturerURL", manufacturerURL, deviceElm);
		String modelDescription = infoGraph.value(Device.modelDescription);
		addElement("modelDescription", modelDescription, deviceElm);
		String modelName = infoGraph.value(Device.modelName);
		addElement("modelName", modelName, deviceElm);
		String modelNumber = infoGraph.value(Device.modelNumber);
		addElement("modelNumber", modelNumber, deviceElm);
		return deviceElm;
	}

	/**
	 * Generates XML of services from device semantic model.
	 * @param deviceGraph Device semantic model.
	 * @param deviceElm Parent XML element.
	 * @return Extended device XML element.
	 */
	public Element processServices(Graph deviceGraph, Element deviceElm) {
		return processServices(deviceGraph, deviceElm, new ServiceGenerator());
	}

	/**
	 * Generates XML of services from device semantic model.
	 * @param deviceGraph Device semantic model.
	 * @param deviceElm Parent XML element.
	 * @param sg Service generator to be used.
	 * @return Extended device XML element.
	 */
	public Element processServices(Graph deviceGraph, Element deviceElm, ServiceGenerator sg) {
		Set<Graph> services = deviceGraph.subGraphs(Device.hasService);
		if (!services.isEmpty()) {
			Element serviceList = addElement("serviceList", deviceElm);
			Element service = addElement("service", serviceList);
			Element SCPDURL = addElement("SCPDURL", service);
			Element scpd = element("scpd", Processor.SCPD_SERVICE_NS);
			addElement(scpd, SCPDURL);
			Element actionList = addElement("actionList", scpd);
			for(Graph action: services) {
				sg.process(action, actionList);
			}
			sg.stateTable(scpd);
		}
		return deviceElm;
	}

	/**
	 * Generates XML of events from device semantic model.
	 * @param deviceGraph Device semantic model.
	 * @param deviceElm Parent XML element.
	 * @return Extended device XML element.
	 */
	public Element processEvents(Graph deviceGraph, Element deviceElm) {
		return processEvents(deviceGraph, deviceElm, new EventGenerator());
	}

	/**
	 * Generates XML of events from device semantic model.
	 * @param deviceGraph Device semantic model.
	 * @param deviceElm Parent XML element.
	 * @param eg Event generator to be used.
	 * @return Extended device XML element.
	 */
	public Element processEvents(Graph deviceGraph, Element deviceElm, EventGenerator eg) {
		Set<Graph> events = deviceGraph.subGraphs(Device.hasEvent);
		if (!events.isEmpty()) {
			Element eventList = addElement("eventList", deviceElm);
			for(Graph event: events) {
				eg.process(event, eventList);
			}
		}
		return deviceElm;
	}

	/**
	 * Generates XML of energy profile from device semantic model.
	 * @param deviceGraph Device semantic model.
	 * @param deviceElm Parent XML element.
	 * @return Extended device XML element.
	 */
	public Element processEnergyProfile(Graph deviceGraph, Element deviceElm) {
		Graph energyProfileGraph = deviceGraph.subGraph(Device.hasEnergyProfile);
		if(energyProfileGraph != null){
			(new EnergyProfileGenerator()).process(energyProfileGraph, deviceElm);
		}
		return deviceElm;
	}

	/**
	 * Generates XML of semantic device preconditions from device semantic model.
	 * @param deviceGraph Device semantic model.
	 * @param deviceElm Parent XML element.
	 * @return Extended device XML element.
	 */
	public Element processPreconditions(Graph deviceGraph, Element deviceElm) {
		Set<Graph> preconditionGraphs = deviceGraph.subGraphs(Device.hasPrecondition);
		if(preconditionGraphs.size() > 0){
			(new PreconditionGenerator()).process(preconditionGraphs, deviceElm);
		}
		return deviceElm;
	}
}
