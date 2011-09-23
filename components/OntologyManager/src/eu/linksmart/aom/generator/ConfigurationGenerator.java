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

import org.jdom.Element;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Configuration;
import eu.linksmart.aom.ontology.model.Device;

/**
 * Class responsible for generation of configuration XML from 
 * semantic model of configuration information attached to device. 
 * Class is also responsible for generation of several XML views on 
 * configurations of .
 * 
 * @author Peter Kostelnik
 *
 */
public class ConfigurationGenerator extends Generator {
	/**
	 * Generates XML for configuration file from ontology model.
	 * @param file Graph of configuration file.
	 * @param config Parent XML node of configuration.
	 * @return Configuration XML extended by configuration file.
	 */
	public Element processFile(Graph file, Element config){
		Map<String, String> attrs = new HashMap<String, String>();
		attrs.put("downloadURL", file.value(Configuration.downloadURL));
		attrs.put("storageFolder", file.value(Configuration.storageFolder));
		addElement("AssociatedFile", attrs, config);
		return config;
	}

	/**
	 * Generates XML of device configuration from ontology model.
	 * @param g Configuration graph.
	 * @return XML element of device configuration.
	 */
	public Element process(Graph g){
		Map<String, String> attrs = new HashMap<String, String>();
		attrs.put("implementationClass", g.value(Configuration.implementationClass));
		attrs.put("name", g.value(Configuration.name));
		Element config = element("Configuration", attrs);

		for(Graph file: g.subGraphs(Configuration.configurationFile)){
			processFile(file, config);
		}
		return config;
	}

	/**
	 * Generates the XML for configurations of concrete device type.
	 * @param devices Set of device graphs of concrete device type.
	 * @param config Parent XML element to add the configurations XML.
	 * @return Parent XML element extended by configuration of concrete type.
	 */
	public Element getConfigurations(HashSet<Graph> devices, Element config){
		for(Graph device: devices){
			addElement(process(device.subGraph(Device.hasConfiguration)), config);
		}
		return config;
	}

	/**
	 * Used for DDK support. Returns the XML containing configurations grouped by device type.
	 * DDK then can select existing configurations for selected device type. 
	 * @param devices Map of devices grouped by type. The key is the deviceType, the value 
	 * is the set of device graphs of this type.
	 * @return XML of configurations in ontology.
	 */
	public Element getConfigurations(Map<String, HashSet<Graph>> devices){
		Element configs = new Element("DeviceTypes");
		for(String deviceType: devices.keySet()){ 
			Map<String, String> attrs = new HashMap<String, String>();
			attrs.put("name", deviceType);
			Element config = addElement("DeviceType", attrs, configs);
			getConfigurations(devices.get(deviceType), config);
		}
		return configs;
	}
}
