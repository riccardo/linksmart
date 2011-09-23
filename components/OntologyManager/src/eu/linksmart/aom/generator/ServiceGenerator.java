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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jdom.Element;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Service;
import eu.linksmart.aom.processor.SCPDServiceProcessor.StateVariable;

/**
 * Class responsible for generation of XML of service from service semantic model.
 * 
 * @author Peter Smatana
 *
 */
public class ServiceGenerator extends Generator {
	private Set<StateVariable> stateVariables = new HashSet<StateVariable>();

	/**
	 * Generates the service XML from semantic model.
	 * @param service Semantic model of service.
	 * @param superElm Parent XML element (service list).
	 * @return Service XML.
	 */
	public Element process(Graph service, Element superElm){
		Element serviceElm = addElement("action", superElm);
		addElement("name", service.value(Service.serviceOperation), serviceElm);
		Element argumentList = addElement("argumentList", serviceElm);
		for(Graph input: service.subGraphs(Service.hasInput)){
			input(input, argumentList);
		}
		for(Graph output: service.subGraphs(Service.hasOutput)){
			output(output, argumentList);
		}

		return serviceElm;
	}

	/**
	 * Gets the service state variables of this service.
	 * 
	 * @return Service state variables.
	 */
	public Set<StateVariable> getStateVariables() {
		return stateVariables;
	}

	/**
	 * Generates state table of service variables from state variables.
	 * @param superElm Parrent service XML element.
	 * @return State table XML.
	 */
	public Element stateTable(Element superElm) {
		Element serviceStateTable = addElement("serviceStateTable", superElm);
		for(StateVariable stateVariable: getStateVariables()){
			Map<String, String> atts = new HashMap<String, String>();
			atts.put("sendEvents", (stateVariable.isSendEvents()?"yes":"no"));
			Element stateVariableElm = addElement("stateVariable", atts, serviceStateTable);
			addElement("name", stateVariable.getName(), stateVariableElm);
			addElement("dataType", stateVariable.getDataType(), stateVariableElm);
		}
		return serviceStateTable;
	}

	/**
	 * Generates service input parameter XML from semantic model.
	 * @param param Semantic model of input.
	 * @param superElm Service parent XML.
	 * @return XML of service input.
	 */
	public Element input(Graph param, Element superElm) {
		Element input = addElement("argument", superElm);
		String parameterName = param.value(Service.parameterName);
		String stateVariableName = param.value(Service.relatedStateVariable);
		String parameterType = param.value(Service.parameterType);
		boolean sendEvents = param.value(Service.sendEvents).equals("true");
		addElement("name", parameterName, input);
		addElement("direction", "in", input);
		addElement("relatedStateVariable", stateVariableName, input);
		stateVariables.add(new StateVariable(stateVariableName, parameterType, sendEvents));
		return input;
	}

	/**
	 * Generates service output parameter XML from semantic model.
	 * @param param Semantic model of output.
	 * @param superElm Service parent XML.
	 * @return XML of service output.
	 */
	public Element output(Graph param, Element superElm) {
		Element output = addElement("argument", superElm);
		String parameterName = param.value(Service.parameterName);
		String stateVariableName = param.value(Service.relatedStateVariable);
		String parameterType = param.value(Service.parameterType);
		boolean sendEvents = param.value(Service.sendEvents).equals("true");
		addElement("name", parameterName, output);
		addElement("direction", "out", output);
		addElement("retval", output);
		addElement("relatedStateVariable", stateVariableName, output);
		stateVariables.add(new StateVariable(stateVariableName, parameterType, sendEvents));
		return output;
	}

}
