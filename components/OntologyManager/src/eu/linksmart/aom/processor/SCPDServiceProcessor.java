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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.XMLSchema;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.model.Service;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;

/**
 * Class responsible for generation the services ontology models from XML.
 * 
 * @author Peter Smatana
 *
 */
public class SCPDServiceProcessor extends Processor {
	ValueFactory f;
	public SCPDServiceProcessor(AOMRepository repository) {
		super(repository);
		this.f = repository.getValueFactory();
	}

	private boolean shouldProcess(String serviceType) {
		return !serviceType.toLowerCase().contains("hydraservice") &&
		!serviceType.toLowerCase().contains("powerservice") &&
		!serviceType.toLowerCase().contains("locationservice") &&
		!serviceType.toLowerCase().contains("energyservice") &&
		!serviceType.toLowerCase().contains("memoryservice") &&
		!serviceType.toLowerCase().contains("bluetoothservice");
	}

	public static class StateVariable {
		private String name;
		private String dataType;
		private boolean sendEvents;

		public StateVariable(String name, String dataType, boolean sendEvents) {
			this.name = name;
			this.dataType = dataType;
			this.sendEvents = sendEvents;
		}

		public String getName() {
			return name;
		}

		public String getDataType() {
			return dataType;
		}

		public boolean isSendEvents() {
			return sendEvents;
		}

		@Override
		public String toString() {
			return "[[" + name + "][" + dataType + "][" + sendEvents + "]]";
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StateVariable other = (StateVariable) obj;
			if (dataType == null) {
				if (other.dataType != null)
					return false;
			} else if (!dataType.equals(other.dataType))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (sendEvents != other.sendEvents)
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + (sendEvents ? 1231 : 1237);
			return result;
		}
	}

	/**
	 * Generates state variables information from XML.
	 * @param stateTable State variables XML.
	 * @return State table information.
	 */
	public Map<String, StateVariable> stateTable(Element stateTable) {
		Map<String, StateVariable> st = new HashMap<String, StateVariable>();
		Namespace ns = stateTable.getNamespace();
		List<Element> stateVariables = stateTable.getChildren("stateVariable", ns);
		for (Element stateVariable: stateVariables){
			String sendEvents = stateVariable.getAttributeValue("sendEvents");
			String name = stateVariable.getChildTextTrim("name", ns);
			String dataType = stateVariable.getChildTextTrim("dataType", ns);
			st.put(name, new StateVariable(name, dataType, sendEvents.equals("yes")));
		}
		return st;
	}

	/**
	 * Generates the semantic models of service actions.
	 * @param actions XML of actions.
	 * @param g Device model. 
	 * @param stateTable State variables information.
	 * @return Extended device model.
	 */
	public Graph processActions(List<Element> actions, Graph g,
			Map<String, StateVariable> stateTable) {
		for(Element action: actions) {
			processAction(action, g, stateTable);
		}
		return g;
	}

	/**
	 * Generates the semantic model of service.
	 * @param service Service XML.
	 * @param g Device model.
	 * @return Extended device model.
	 */
	public Graph processService(Element service, Graph g) {
		try{
			XPath xp = XPath.newInstance(".//s:serviceStateTable");
			xp.addNamespace("s", SCPD_SERVICE_NS);

			Map<String, StateVariable> stateTable = stateTable((Element)xp.selectSingleNode(service));

			xp = XPath.newInstance(".//s:actionList/s:action");
			xp.addNamespace("s", SCPD_SERVICE_NS);
			g = processActions((List<Element>)xp.selectNodes(service), g, stateTable);
			return g;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return g;
	}

	/**
	 * Processes the semantic models of events and adds it to device model.
	 * @param serviceList XML of device services.
	 * @param g Semantic model of device.
	 * @return Extended device model.
	 */
	public Graph process(Element serviceList, Graph g){
		Namespace ns = serviceList.getNamespace();
		List<Element> services = serviceList.getChildren("service", ns);
		for(Element service: services){
			String serviceType = service.getChildTextTrim("serviceType", ns);
			if (shouldProcess(serviceType)) {
				g = processService(service, g);
			}
		}
		return g;
	}

	/**
	 * Generates semantic model of service parameter.
	 * @param parameter XML of service parameter.
	 * @param serviceURI Ontology URI of actually processed service.
	 * @param g Device model.
	 * @param stateTable State variables information.
	 * @return Extended device model.
	 */
	public Graph processParameter(Element parameter, String serviceURI, Graph g,
			Map<String, StateVariable> stateTable){

		String parameterURI = "";
		Namespace ns = parameter.getNamespace();
		String direction = parameter.getChildTextTrim("direction", ns);
		if(direction.equals("in")){
			parameterURI = repository.getURI(Service.ServiceInput);
			ResourceUtil.addStatement(parameterURI, Rdf.rdfType, Service.ServiceInput.stringValue(), f, g);
			ResourceUtil.addStatement(serviceURI, Service.hasInput, parameterURI, f, g);
		}
		else{
			parameterURI = repository.getURI(Service.ServiceOutput);
			ResourceUtil.addStatement(parameterURI, Rdf.rdfType, Service.ServiceOutput.stringValue(), f, g);
			ResourceUtil.addStatement(serviceURI, Service.hasOutput, parameterURI, f, g);
		}

		ResourceUtil.addStatement(
				parameterURI, 
				Service.parameterName, 
				parameter.getChildTextTrim("name", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		StateVariable var = stateTable.get(parameter.getChildTextTrim("relatedStateVariable", ns));

		if(var != null){
			ResourceUtil.addStatement(
					parameterURI, 
					Service.parameterType, 
					var.getDataType(), 
					XMLSchema.STRING, 
					f, 
					g);

			ResourceUtil.addStatement(
					parameterURI, 
					Service.relatedStateVariable, 
					var.name + "", 
					XMLSchema.STRING, 
					f, 
					g);

			ResourceUtil.addStatement(
					parameterURI, 
					Service.sendEvents, 
					var.isSendEvents() + "", 
					XMLSchema.BOOLEAN, 
					f, 
					g);
		}

		return g;
	}
	
	/**
	 * Generates semantic model of service action.
	 * @param action XML of service action.
	 * @param g Device model.
	 * @param stateTable State variables information.
	 * @return Extended device model.
	 */
	public Graph processAction(Element action, Graph g,
			Map<String, StateVariable> stateTable) {

		String serviceURI = repository.getURI(Service.PhysicalService);
		ResourceUtil.addStatement(g.getBaseURI(), Device.hasService, serviceURI, f, g);
		Namespace ns = action.getNamespace();

		ResourceUtil.addStatement(
				serviceURI, 
				Rdf.rdfType, 
				Service.PhysicalService.stringValue(), 
				f, 
				g);

		ResourceUtil.addStatement(
				serviceURI, 
				Service.serviceOperation, 
				action.getChildTextTrim("name", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		Element argList = action.getChild("argumentList", ns);
		if(argList != null){
			List<Element> parameters = action.getChild("argumentList", ns).getChildren("argument", ns);
			for(Element parameter: parameters){
				processParameter(parameter, serviceURI, g, stateTable);
			}
		}
		return g;
	}
}
