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

import org.jdom.Element;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.XMLSchema;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.schema.OntologyClass;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;

/**
 * Class responsible for generation the device full ontology model from SCPD XML.
 * 
 * @author Peter Smatana, Peter Kostelnik
 *
 */
public class SCPDProcessor extends Processor {
	ValueFactory f;
	public SCPDProcessor(AOMRepository repository) {
		super(repository);
		this.f = repository.getValueFactory();
	}


	/**
	 * Generates semantic model of services and adds it to device model.
	 * @param serviceList XML element of services.
	 * @param g Device model.
	 * @return Extended device model
	 */
	public Graph services(Element serviceList, Graph g){
		SCPDServiceProcessor p = new SCPDServiceProcessor(repository);
		return p.process(serviceList, g);
	}

	/**
	 * Generates semantic model of semantic device preconditions and adds it to device model.
	 * @param deviceList XML element of semantic device preconditions. 
	 * @param g Device model.
	 * @return Extended device model
	 */
	public Graph preconditions(Element deviceList, Graph g){
		PreconditionProcessor p = new PreconditionProcessor(repository);
		return p.process(deviceList, g);
	}

	private String getDeviceClass(Element device, org.jdom.Namespace ns){
		Element scpdDeviceType = device.getChild("deviceClass", ns);
		if(scpdDeviceType != null && scpdDeviceType.getText() != null && !scpdDeviceType.getText().trim().equals("")){
			String classURI = Namespace.device + scpdDeviceType.getText();
			if(repository.classExists(classURI)){
				return classURI;
			}
		}
		return null;
	}
	
	/**
	 * Generates the basic device semantic model from SCPD. Model is created by adding
	 * basic manufacturer and model information, models of device services and in case
	 * of semantic devices also the models of preconditions.
	 * @param xml SCPD XML.
	 * @return Device semantic model.
	 */
	public Graph process(String xml){
		Element root = parse(xml);

		if(root == null) return null;

		org.jdom.Namespace ns = root.getNamespace();
		Element device = root.getChild("device", ns);
		if(device == null) return null;

		String deviceClass = getDeviceClass(device, org.jdom.Namespace.getNamespace(SCPD_EXTENSION));
		Element deviceList = device.getChild("deviceList", ns);
		if(deviceClass == null){
			if(deviceList != null){
				deviceClass = Device.SemanticDevice.stringValue();
			}
			else{
				deviceClass = Device.PhysicalDevice.stringValue();
			}
		}

		String deviceURI = repository.getURI(new OntologyClass(deviceClass));
		String infoURI = repository.getURI(Device.InfoDescription);
		Graph g = new Graph(deviceURI);


		if(deviceList != null){
			g.setBaseURI(deviceURI);
			String pid = device.getChildTextTrim("PID", ns);
			if(pid != null){
				ResourceUtil.addStatement(
						deviceURI, 
						Device.PID, 
						pid, 
						XMLSchema.STRING,
						f, 
						g);
			}
			preconditions(deviceList, g);
		}


		ResourceUtil.addStatement(
				deviceURI, 
				Rdf.rdfType, 
				deviceClass, 
				f, 
				g);

		ResourceUtil.addStatement(
				deviceURI, 
				Device.deviceUPnPType, 
				device.getChildTextTrim("deviceType", ns), 
				XMLSchema.STRING,
				f, 
				g);

		ResourceUtil.addStatement(
				deviceURI, 
				Device.isDeviceTemplate, 
				"true", 
				XMLSchema.BOOLEAN, 
				f, 
				g);

		ResourceUtil.addStatement(deviceURI, Device.info, infoURI, f, g);

		ResourceUtil.addStatement(
				infoURI, 
				Device.friendlyName, 
				device.getChildTextTrim("friendlyName", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				infoURI, 
				Rdf.rdfType, 
				Device.InfoDescription.stringValue(), 
				f, 
				g);

		ResourceUtil.addStatement(
				infoURI, 
				Device.manufacturer, 
				device.getChildTextTrim("manufacturer", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				infoURI, 
				Device.manufacturerURL, 
				device.getChildTextTrim("manufacturerURL", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				infoURI, 
				Device.modelDescription, 
				device.getChildTextTrim("modelDescription", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				infoURI, 
				Device.modelName, 
				device.getChildTextTrim("modelName", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				infoURI, 
				Device.modelNumber, 
				device.getChildTextTrim("modelNumber", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		services(device.getChild("serviceList", ns), g);

		return g;
	}
}
