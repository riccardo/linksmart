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
import java.util.Set;

import org.jdom.Element;
import org.jdom.Namespace;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.XMLSchema;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;

/**
 * Class responsible for generation of ontology models of semantic device preconditions from d
 * SCPD XML device list.
 * 
 * @author Peter Kostelnik
 *
 */
public class PreconditionProcessor  extends Processor {
	ValueFactory f;
	public PreconditionProcessor(AOMRepository repository) {
		super(repository);
		this.f = repository.getValueFactory();
	}

	/**
	 * Generates the model of semantic device precondition and adds it to the device graph.
	 * Semantic device precondition types are identified using the attributes in precondition XML
	 * structure. 
	 * @param precondition XML representation of precondition.
	 * @param g Device graph.
	 * @return Graph extended with processed precondition.
	 */
	public Graph processPrecondition(Element precondition, Graph g){
		Namespace ns = precondition.getNamespace();

		String deviceType = precondition.getAttributeValue("deviceType");

		if(deviceType == null) return g;
		deviceType = eu.linksmart.aom.ontology.model.Namespace.device + deviceType;
		Set<String> superClasses = repository.getSuperClassesOf(deviceType);
		if(!superClasses.contains(Device.Device.stringValue())) 
			deviceType = Device.Device.stringValue();

		String preconditionURI = null; 
		String preconditionType = null; 

		String preconditionId = precondition.getAttributeValue("id");
		if(preconditionId == null) return g;

		String pids = precondition.getAttributeValue("PID");
		if(pids != null){
			preconditionURI = repository.getURI(Device.SemanticDevicePIDPrecondition);
			preconditionType = Device.SemanticDevicePIDPrecondition.stringValue();
			String[] pidParts = pids.split(";");
			for(String pid : pidParts){
				if(pid != null && !pid.trim().equals("")){
					ResourceUtil.addStatement(
							preconditionURI, 
							Device.preconditionPID, 
							pid,
							XMLSchema.STRING,
							f, 
							g);
				}
			}
		}
		else{
			preconditionURI = repository.getURI(Device.SemanticDeviceQueryPrecondition);
			preconditionType = Device.SemanticDeviceQueryPrecondition.stringValue();
			String query = precondition.getAttributeValue("query");
			String cardinality = precondition.getAttributeValue("cardinality");
			if(query != null && !query.trim().equals("") &&
					cardinality != null && !cardinality.trim().equals("")){
				ResourceUtil.addStatement(
						preconditionURI, 
						Device.preconditionQuery, 
						query,
						XMLSchema.STRING,
						f, 
						g);
				ResourceUtil.addStatement(
						preconditionURI, 
						Device.preconditionCardinality, 
						cardinality,
						XMLSchema.STRING,
						f, 
						g);
			}
		}
		ResourceUtil.addStatement(
				preconditionURI, 
				Rdf.rdfType, 
				preconditionType, 
				f, 
				g);
		ResourceUtil.addStatement(
				preconditionURI, 
				Device.preconditionId, 
				preconditionId,
				XMLSchema.STRING,
				f, 
				g);
		ResourceUtil.addStatement(
				preconditionURI, 
				Device.preconditionDeviceType, 
				deviceType,
				XMLSchema.STRING,
				f, 
				g);
		ResourceUtil.addStatement(
				g.getBaseURI(), 
				Device.hasPrecondition, 
				preconditionURI, 
				f, 
				g);
		return g;
	}

	/**
	 * Generates the semantic device preconditions models from precondition list XML.
	 * @param deviceList XML containing the preconditions definitions.
	 * @param g Device graph.
	 * @return Graph extended with semantic device preconditions.
	 */
	public Graph process(Element deviceList, Graph g){
		Namespace ns = deviceList.getNamespace();
		List<Element> preconditions = deviceList.getChildren("precondition", ns);
		for(Element precondition: preconditions){
			processPrecondition(precondition, g);
		}
		return g;
	}
}
