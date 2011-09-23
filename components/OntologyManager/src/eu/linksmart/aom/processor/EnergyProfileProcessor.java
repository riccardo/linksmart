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
import eu.linksmart.aom.ontology.model.Energy;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.schema.ResourceProperty;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;

/**
 * Class responsible for generation the ontology model from energy profile XML.
 * 
 * @author Peter Smatana
 *
 */
public class EnergyProfileProcessor extends Processor {
	ValueFactory f;
	public EnergyProfileProcessor(AOMRepository repository) {
		super(repository);
		this.f = repository.getValueFactory();
	}

	/**
	 * Generates semantic model from energy classification XML.
	 * @param epURI Energy profile ontology URI.
	 * @param root Energy classification XML element of.
	 * @param g Energy profile semantic model.
	 * @return Extended energy profile semantic model.
	 */
	public Graph processClassification(String epURI, Element root, Graph g){
		Element cs = root.getChild("energyclassification");
		if(cs != null){
			String ecURI = repository.getURI(Energy.EnergyClassification);

			ResourceUtil.addStatement(
					epURI, 
					Energy.classification, 
					ecURI, 
					f, 
					g);      
			ResourceUtil.addStatement(
					ecURI, 
					Rdf.rdfType, 
					Energy.EnergyClassification.stringValue(), 
					f, 
					g);      
			ResourceUtil.addStatement(
					ecURI, 
					Energy.classificationSystem, 
					cs.getChildTextTrim("system"), 
					XMLSchema.STRING, 
					f, 
					g);      
			ResourceUtil.addStatement(
					ecURI, 
					Energy.classificationValue, 
					cs.getChildTextTrim("value"), 
					XMLSchema.STRING, 
					f, 
					g);      
		}

		return g;
	}

	/**
	 * Generates semantic model from energy operation XML.
	 * @param epURI Energy profile ontology URI.
	 * @param root Energy operation XML element of.
	 * @param g Energy profile semantic model.
	 * @return Extended energy profile semantic model.
	 */
	public Graph processOperation(String epURI, Element root, Graph g){
		Element op = root.getChild("operation");
		if(op != null){
			String opURI = repository.getURI(Energy.EnergyOperation);

			ResourceUtil.addStatement(
					epURI, 
					Energy.operation, 
					opURI, 
					f, 
					g);      
			ResourceUtil.addStatement(
					opURI, 
					Rdf.rdfType, 
					Energy.EnergyOperation.stringValue(), 
					f, 
					g);

			Graph uv = repository.getUnitValue(
					op.getChildTextTrim("minimumruntime"), 
					op.getChild("minimumruntime").getAttributeValue("unit"));
			g.add(uv);
			ResourceUtil.addStatement(
					opURI, 
					Energy.minimumRunTime, 
					uv.getBaseURI(), 
					f, 
					g);      
		}

		return g;
	}

	/**
	 * Generates semantic model from energy life time XML.
	 * @param epURI Energy life time ontology URI.
	 * @param root Energy life time XML element.
	 * @param g Energy profile semantic model.
	 * @return Extended energy profile semantic model.
	 */
	public Graph processLifeTime(String epURI, Element root, Graph g){
		Element lt = root.getChild("lifetime");
		if(lt != null){
			String ltURI = repository.getURI(Energy.EnergyLifeTime);

			ResourceUtil.addStatement(
					epURI, 
					Energy.lifeTime, 
					ltURI, 
					f, 
					g);      
			ResourceUtil.addStatement(
					ltURI, 
					Rdf.rdfType, 
					Energy.EnergyLifeTime.stringValue(), 
					f, 
					g);

			Graph uvExpected = repository.getUnitValue(
					lt.getChildTextTrim("expected"), 
					lt.getChild("expected").getAttributeValue("unit"));
			Graph uvStartCost = repository.getUnitValue(
					lt.getChildTextTrim("startcost"), 
					lt.getChild("startcost").getAttributeValue("unit"));
			Graph uvShutDownCost = repository.getUnitValue(
					lt.getChildTextTrim("shutdowncost"), 
					lt.getChild("shutdowncost").getAttributeValue("unit"));

			g.add(uvExpected);
			g.add(uvStartCost);
			g.add(uvShutDownCost);

			ResourceUtil.addStatement(
					ltURI, 
					Energy.expected, 
					uvExpected.getBaseURI(), 
					f, 
					g);      
			ResourceUtil.addStatement(
					ltURI, 
					Energy.startCost, 
					uvStartCost.getBaseURI(), 
					f, 
					g);      
			ResourceUtil.addStatement(
					ltURI, 
					Energy.shutDownCost, 
					uvShutDownCost.getBaseURI(), 
					f, 
					g);      
		}

		return g;
	}

	/**
	 * Generates semantic model from energy mode XML.
	 * @param epURI Energy profile ontology URI.
	 * @param root Energy mode XML element.
	 * @param g Energy profile semantic model.
	 * @return Extended energy profile semantic model.
	 */
	public Graph processMode(String epURI, ResourceProperty relation, Element mode, Graph g){
		String mURI = repository.getURI(Energy.EnergyMode);
		ResourceUtil.addStatement(
				mURI, 
				Rdf.rdfType, 
				Energy.EnergyMode.stringValue(), 
				f, 
				g);
		ResourceUtil.addStatement(
				epURI, 
				relation, 
				mURI, 
				f, 
				g);

		ResourceUtil.addStatement(
				mURI, 
				Energy.modeName, 
				mode.getAttributeValue("name"), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				mURI, 
				Energy.modeMax, 
				mode.getChildTextTrim("max"), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				mURI, 
				Energy.modeAverage, 
				mode.getChildTextTrim("average"), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				mURI, 
				Energy.modeStart, 
				mode.getChildTextTrim("start"), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				mURI, 
				Energy.modeShutdown, 
				mode.getChildTextTrim("shutdown"), 
				XMLSchema.STRING, 
				f, 
				g);

		return g;
	}

	private Graph processModes(String modesNode, ResourceProperty relation, String epURI, Element root, Graph g){
		Element c = root.getChild(modesNode);
		if(c != null){
			Element effect = c.getChild("effect");
			if(effect != null){
				List<Element> modes = effect.getChildren("mode");
				for(Element mode : modes) {
					processMode(epURI, relation, mode, g);
				}
			}
		}
		return g;
	}

	/**
	 * Generates semantic model from energy consumption XML.
	 * @param epURI Energy profile ontology URI.
	 * @param root Energy consumption XML element.
	 * @param g Energy profile semantic model.
	 * @return Extended energy profile semantic model.
	 */
	public Graph processConsumption(String epURI, Element root, Graph g){
		return processModes(
				"energyconsumption",
				Energy.consumption,
				epURI, 
				root, 
				g);
	}
	
	/**
	 * Generates semantic model from energy generation XML.
	 * @param epURI Energy profile ontology URI.
	 * @param root Energy generation XML element.
	 * @param g Energy profile semantic model.
	 * @return Extended energy profile semantic model.
	 */
	public Graph processGeneration(String epURI, Element root, Graph g){
		return processModes(
				"energygeneration", 
				Energy.generation,
				epURI, 
				root, 
				g);
	}

	/**
	 * Generates semantic model of energy profile from XML and attaches it to device.
	 * @param deviceURI Ontology URI of device.
	 * @param xml Energy profile XML.
	 * @return Semantic model of energy profile.
	 */
	public Graph process(String deviceURI, String xml){
		Element root = parse(xml);

		if(root == null) return null;

		String epURI = repository.getURI(Energy.EnergyProfile);

		Graph g = new Graph(deviceURI);
		ResourceUtil.addStatement(epURI, Rdf.rdfType, Energy.EnergyProfile.stringValue(), f, g);
		ResourceUtil.addStatement(deviceURI, Device.hasEnergyProfile, epURI, f, g);

		processClassification(epURI, root, g);
		processOperation(epURI, root, g);
		processLifeTime(epURI, root, g);
		processConsumption(epURI, root, g);
		processGeneration(epURI, root, g);

		return g;
	}
}
