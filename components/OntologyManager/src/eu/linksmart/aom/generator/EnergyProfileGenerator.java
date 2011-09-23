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
import java.util.Map;
import java.util.Set;

import org.jdom.Element;
import org.openrdf.model.impl.URIImpl;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Energy;
import eu.linksmart.aom.ontology.model.Unit;

/**
 * Class responsible for generation of energy XML from 
 * semantic model of energy profile information attached to device.
 * 
 * @author Peter Smatana
 *
 */
public class EnergyProfileGenerator extends Generator{

	/**
	 * Generates the energy profile XML from the energy profile model. 
	 * @param epg Model of energy profile.
	 * @param superElm Parent XML element to add the energy profile XML.
	 * @return Energy profile XML.
	 */
	public Element process(Graph epg, Element superElm) {
		Element energyProfile = element("energyprofile", superElm.getNamespace().getURI());
		addElement(energyProfile, superElm);
		Element classification = addElement("energyclassification", energyProfile);
		processClassification(epg.subGraph(Energy.classification), classification);
		Element consumptionsElm = addElement("effect", addElement("energyconsumption", energyProfile));
		Set<Graph> consumptions= epg.subGraphs(Energy.consumption);
		for(Graph consumption: consumptions){
			processMode(consumption, consumptionsElm);
		}
		Element generationsElm = addElement("effect", addElement("energygeneration", energyProfile));
		Set<Graph> generations= epg.subGraphs(Energy.generation);
		for(Graph generation: generations){
			processMode(generation, generationsElm);
		}
		Element operation = addElement("operation", energyProfile);
		processOperation(epg.subGraph(Energy.operation), operation);
		Element lifetime = addElement("lifetime", energyProfile);
		processLifeTime(epg.subGraph(Energy.lifeTime), lifetime);
		return energyProfile;
	}

	/**
	 * Generates the energy classification XML from semantic model.
	 * @param classification Classification model.
	 * @param superElm Energy profile XML element.
	 * @return Extended energy profile element.
	 */
	public Element processClassification(Graph classification, Element superElm) {
		if(classification != null){
			String system = classification.value(Energy.classificationSystem);
			String value = classification.value(Energy.classificationValue);
			addElement("system", system,  superElm);
			addElement("value", value,  superElm);
		}
		return superElm;
	}

	/**
	 * Generates the energy mode XML from semantic model.
	 * @param mode Energy mode model.
	 * @param superElm Energy profile XML element.
	 * @return Extended energy profile element.
	 */
	public Element processMode(Graph emode, Element superElm) {
		Map<String, String> atts = new HashMap<String, String>();
		atts.put("name", emode.value(Energy.modeName));

		Element mode = element("mode", atts);
		addElement(mode, superElm);
		addElement("max", emode.value(Energy.modeMax), mode);
		addElement("average", emode.value(Energy.modeAverage), mode);
		addElement("start", emode.value(Energy.modeStart), mode);
		addElement("shutdown", emode.value(Energy.modeShutdown), mode);
		return superElm;
	}

	private void addProp(Graph prop, Element superElm, String propName) {
		if(prop != null){
			String unitStr = prop.value(Unit.inUnit);
			Map<String, String> atts = new HashMap<String, String>();
			if (unitStr != null){
				String unit = new URIImpl(prop.value(Unit.inUnit)).getLocalName();
				atts.put("unit", unit);
			}
			String value = prop.value(Unit.value);
			Element expectedElm = element(propName, atts, value);
			addElement(expectedElm, superElm);
		}
	}

	/**
	 * Generates the energy lifetime XML from semantic model.
	 * @param lifeTime Energy life time model.
	 * @param superElm Energy profile XML element.
	 * @return Extended energy profile element.
	 */
	public Element processLifeTime(Graph lifeTime, Element superElm) {
		if(lifeTime != null){
			addProp(lifeTime.subGraph(Energy.expected), superElm, "expected");
			addProp(lifeTime.subGraph(Energy.startCost), superElm, "startcost");
			addProp(lifeTime.subGraph(Energy.shutDownCost), superElm, "shutdowncost");
		}
		return superElm;
	}

	/**
	 * Generates the energy operation XML from semantic model.
	 * @param operation Energy operation time model.
	 * @param superElm Energy profile XML element.
	 * @return Extended energy profile element.
	 */
	public Element processOperation(Graph operation, Element superElm) {
		if(operation != null){
			addProp(operation.subGraph(Energy.minimumRunTime), superElm, "minimumruntime");
		}
		return superElm;
	}
}
