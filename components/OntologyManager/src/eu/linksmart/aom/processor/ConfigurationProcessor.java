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
import eu.linksmart.aom.ontology.model.Configuration;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;

/**
 * Class responsible for generation the ontology model from Configuration XML.
 * 
 * @author Peter Kostelnik
 *
 */
public class ConfigurationProcessor extends Processor {
	ValueFactory f;
	public ConfigurationProcessor(AOMRepository repository) {
		super(repository);
		this.f = repository.getValueFactory();
	}

	/**
	 * Generates the ontology graph for configuration file XML. 
	 * @param configURI Ontology URI of configuration model.
	 * @param file XML of configuration file.
	 * @param g Configuration graph to be extended.
	 * @return Configuration graph.
	 */
	public Graph processFile(String configURI, Element file, Graph g){
		String fileURI = repository.getURI(Configuration.ConfigurationFile);
		ResourceUtil.addStatement(
				configURI, 
				Configuration.configurationFile, 
				fileURI, 
				f, 
				g);

		ResourceUtil.addStatement(
				fileURI, 
				Rdf.rdfType, 
				Configuration.ConfigurationFile.stringValue(), 
				f, 
				g);

		ResourceUtil.addStatement(
				fileURI, 
				Configuration.downloadURL, 
				file.getAttributeValue("downloadURL"), 
				XMLSchema.STRING,
				f, 
				g);

		ResourceUtil.addStatement(
				fileURI, 
				Configuration.storageFolder, 
				file.getAttributeValue("storageFolder"), 
				XMLSchema.STRING,
				f, 
				g);

		return g;
	}
	
	/**
	 * Generates ontology model for configuration XML. 
	 * @param deviceURI Ontology URI of device to be extended with configuration model.
	 * @param xml Configuration XML.
	 * @return Configuration graph.
	 */
	public Graph process(String deviceURI, String xml){
		Element root = parse(xml);

		if(root == null) return null;

		Graph g = new Graph(deviceURI);
		String configURI = repository.getURI(Configuration.Configuration);

		ResourceUtil.addStatement(
				deviceURI, 
				Device.hasConfiguration, 
				configURI, 
				f, 
				g);

		ResourceUtil.addStatement(
				configURI, 
				Rdf.rdfType, 
				Configuration.Configuration.stringValue(), 
				f, 
				g);

		ResourceUtil.addStatement(
				configURI, 
				Configuration.implementationClass, 
				root.getAttributeValue("implementationClass"), 
				XMLSchema.STRING,
				f, 
				g);

		ResourceUtil.addStatement(
				configURI, 
				Configuration.name, 
				root.getAttributeValue("name"), 
				XMLSchema.STRING,
				f, 
				g);

		List<Element> files = root.getChildren("AssociatedFile");
		for(Element f : files){
			processFile(configURI, f, g);
		}

		return g;
	}

}
