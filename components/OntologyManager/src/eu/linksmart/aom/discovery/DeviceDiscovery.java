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

package eu.linksmart.aom.discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.openrdf.model.vocabulary.XMLSchema;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.processor.DiscoveryProcessor;
import eu.linksmart.aom.repository.AOMRepository;
/**
 * Component responsible for semantic discovery of devices. When the new device
 * enters the network, the low level discovery manager acquires the discovery
 * information, which is transformed to XML document and used for resolution of
 * devices in ontology. Discovery XML is transformed to the SPARQL query
 * and devices matching the information are retrieved. 
 * The best matching device model is returned. 
 * 
 * @author Peter Kostelnik
 */
public class DeviceDiscovery {
	AOMRepository repository;

	private class BetterMatch implements Comparator<Graph>{
		private Graph disco;
		public BetterMatch(Graph disco){
			this.disco = disco;
		}
		@Override 
		public int compare(Graph device1, Graph device2){
			Graph g1 = device1.subGraph(Device.hasDiscoveryInfo);
			Graph g2 = device2.subGraph(Device.hasDiscoveryInfo);
			if(Math.abs(disco.size() - g1.size()) < Math.abs(disco.size() - g2.size())){
				return -1;
			}
			if(Math.abs(disco.size() - g1.size()) > Math.abs(disco.size() - g2.size())){
				return 1;
			}
			return 0;
		}
	}

	public DeviceDiscovery(AOMRepository repository) {
		this.repository = repository;
	}

	/**
	 * Generates the error graph returned, when discovery has failed because
	 * no match was found. 
	 * @param message Error message.
	 * @return Error graph containing the error description. 
	 */
	public Graph errorGraph(String message){
		String errorURI = "http://error.message.uri";
		Graph error = new Graph(errorURI);
		ResourceUtil.addStatement(
				errorURI, 
				Device.errorMessage, 
				message, 
				XMLSchema.STRING, 
				repository.getValueFactory(),
				error);
		return error;
	}

	/**
	 * Generates the error graph returned, when discovery has failed because of the
	 * undecidable multiple matches. 
	 * @param message Error message to be included in the error graph for case, when 
	 * more templates were matched.
	 * @param templates The list of templates matched by low-level discovery information 
	 * @return Error graph containing the error description and list of URIs of matched tempaltes. 
	 */
	public Graph errorGraph(String message, Set<Graph> templates){
		Graph error = errorGraph(message);
		for(Graph template : templates){
			ResourceUtil.addStatement(
					error.getBaseURI(), 
					Device.errorTemplateURI, 
					template.getBaseURI(), 
					repository.getValueFactory(),
					error);
		}
		return error;
	}

	/**
	 * 
	 * Resolves the low-level discovery information. Low-level discovery information
	 * is transformed to the SPARQL query and devices to be matched are retrieved.
	 * If the more devices have matched discovery information, the candidate with number of triples 
	 * closer to the number of triples in graph of discovery information is returned (in other
	 * words, the more similar description is the better). 
	 * The result of discovery process can be:
	 * <ul>
	 *   <li>Graph of discovered device: if only one device matched the discovery information</li>
	 *   <li>Error graph: containing the description of error if no device was found, or 
	 *   if more devices have matched the discovery information.</li>
	 *   <li></li>
	 * </ul>
	 * @param discovery XML containing low-level discovery information.
	 * @return Full graph of discovered device or error graph if the discovery failed.
	 */
	public Graph resolveDevice(String discovery){
		DiscoveryProcessor dp = new DiscoveryProcessor(repository);
		Graph disco = dp.process(repository.getURI(Device.Device), discovery);
		ResourceUtil.addStatement(
				disco.getBaseURI(), 
				Device.isDeviceTemplate, 
				"true", 
				XMLSchema.BOOLEAN,
				repository.getValueFactory(),
				disco);

		Set<Graph> templates = repository.getDevices(disco);
		if(templates.size() == 1){
			return templates.iterator().next();
		}
		else if(templates.size() > 1){
			List<Graph> list = new ArrayList<Graph>(templates);
			Collections.sort(list, new BetterMatch(disco));
			int first = list.get(0).subGraph(Device.hasDiscoveryInfo).size();
			int seccond = list.get(1).subGraph(Device.hasDiscoveryInfo).size();
			if(first == seccond) {
				return errorGraph("multiple matches found", templates);
			}
			return list.get(0);
		}
		return errorGraph("no matches found");
	}

}
