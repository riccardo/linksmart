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
import eu.linksmart.aom.ontology.model.Discovery;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;

/**
 * Class responsible for generation of ontology model from device discovery information XML.
 * 
 * @author Peter Kostelnik
 *
 */

public class DiscoveryProcessor extends Processor {
	ValueFactory f;
	public DiscoveryProcessor(AOMRepository repository) {
		super(repository);
		this.f = repository.getValueFactory();
	}

	/**
	 * Generates ontology model for tell stick discovery information.
	 * @param deviceURI Ontology URI of device.
	 * @param disco Discovery information XML.
	 * @return Discovery information graph.
	 */
	public Graph processTellstick(String deviceURI, Element disco){
		Graph g = new Graph(deviceURI);

		String discoURI = repository.getURI(Discovery.TellStick);

		org.jdom.Namespace ns = disco.getNamespace();
		ResourceUtil.addStatement(deviceURI, Device.hasDiscoveryInfo, discoURI, f, g);

		ResourceUtil.addStatement(discoURI, Rdf.rdfType, Discovery.TellStick.stringValue(), f, g);

		ResourceUtil.addStatement(
				discoURI, 
				Discovery.tsName, 
				disco.getChildTextTrim("name", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				discoURI, 
				Discovery.tsVendor, 
				disco.getChildTextTrim("vendor", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		return g;
	}

	/**
	 * Generates ontology model for RFID tag discovery information.
	 * @param deviceURI Ontology URI of device.
	 * @param disco Discovery information XML.
	 * @return Discovery information graph.
	 */
	public Graph processRFIDTag(String deviceURI, Element disco){
		Graph g = new Graph(deviceURI);

		String discoURI = repository.getURI(Discovery.RFIDTag);

		org.jdom.Namespace ns = disco.getNamespace();
		ResourceUtil.addStatement(deviceURI, Device.hasDiscoveryInfo, discoURI, f, g);

		ResourceUtil.addStatement(discoURI, Rdf.rdfType, Discovery.RFIDTag.stringValue(), f, g);

		ResourceUtil.addStatement(
				discoURI, 
				Discovery.rfidVendor, 
				disco.getChildTextTrim("manufacturercode", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		return g;
	}

	/**
	 * Generates ontology model for sensor discovery information.
	 * @param deviceURI Ontology URI of device.
	 * @param disco Discovery information XML.
	 * @return Discovery information graph.
	 */
	public Graph processSensor(String deviceURI, Element disco){
		Graph g = new Graph(deviceURI);

		String discoURI = repository.getURI(Discovery.Sensor);

		org.jdom.Namespace ns = disco.getNamespace();
		ResourceUtil.addStatement(deviceURI, Device.hasDiscoveryInfo, discoURI, f, g);

		ResourceUtil.addStatement(discoURI, Rdf.rdfType, Discovery.Sensor.stringValue(), f, g);

		ResourceUtil.addStatement(
				discoURI, 
				Discovery.sType, 
				disco.getChildTextTrim("type", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				discoURI, 
				Discovery.sVendor, 
				disco.getChildTextTrim("vendor", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		return g;
	}

	/**
	 * Adds the model of bluetooth service to bluetooth discovery model.
	 * @param discoURI Ontology URI of discovery model.
	 * @param service XML element of bluetooth service.
	 * @param g Discovery model graph.
	 * @return Discovery graph extended with the service.
	 */
	public Graph processBlueToothService(String discoURI, Element service, Graph g){
		String serviceURI = repository.getURI(Discovery.BlueToothService);

		org.jdom.Namespace ns = service.getNamespace();
		ResourceUtil.addStatement(discoURI, Discovery.btService, serviceURI, f, g);

		ResourceUtil.addStatement(serviceURI, Rdf.rdfType, Discovery.BlueToothService.stringValue(), f, g);

		ResourceUtil.addStatement(
				serviceURI, 
				Discovery.btServiceName, 
				service.getChildTextTrim("servicename", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		Element types = service.getChild("servicetypes", ns);
		if(types != null){
			List<Element> sTypes = types.getChildren("servicetype", ns); 
			for(Element st : sTypes){
				ResourceUtil.addStatement(
						serviceURI,
						Discovery.btServiceType,
						st.getTextTrim(), 
						XMLSchema.STRING, 
						f, 
						g);
			}
		}

		return g;
	}
	
	/**
	 * Adds the models of bluetooth services to bluetooth discovery model.
	 * @param discoURI Ontology URI of discovery model.
	 * @param services XML element of bluetooth service.
	 * @param g Discovery model graph.
	 * @return Discovery graph extended with services.
	 */
	public Graph processBlueToothServices(String discoURI, Element services, Graph g){
		if(services != null){
			org.jdom.Namespace ns = services.getNamespace();
			List<Element> serviceList = services.getChildren("bluetoothservice", ns);
			for(Element service : serviceList){
				processBlueToothService(discoURI, service, g);
			}
		}
		return g;
	}
	
	/**
	 * Generates ontology model for blue tooth discovery information.
	 * @param deviceURI Ontology URI of device.
	 * @param disco Discovery information XML.
	 * @return Discovery information graph.
	 */
	public Graph processBlueTooth(String deviceURI, Element disco){
		Graph g = new Graph(deviceURI);

		String discoURI = repository.getURI(Discovery.BlueTooth);

		org.jdom.Namespace ns = disco.getNamespace();
		ResourceUtil.addStatement(deviceURI, Device.hasDiscoveryInfo, discoURI, f, g);

		ResourceUtil.addStatement(discoURI, Rdf.rdfType, Discovery.BlueTooth.stringValue(), f, g);

		ResourceUtil.addStatement(
				discoURI, 
				Discovery.btName, 
				disco.getChildTextTrim("name", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				discoURI, 
				Discovery.btType, 
				disco.getChildTextTrim("majordevicetype", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				discoURI, 
				Discovery.btClass, 
				disco.getChildTextTrim("deviceclass", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		processBlueToothServices(discoURI, disco.getChild("bluetoothservices", ns), g);
		return g;
	}

	/**
	 * Generates ontology model for generic physical device discovery information.
	 * @param deviceURI Ontology URI of device.
	 * @param disco Discovery information XML.
	 * @return Discovery information graph.
	 */
	public Graph processPhysicalDeviceSource(String deviceURI, Element disco){
		Graph g = new Graph(deviceURI);

		String discoURI = repository.getURI(Discovery.PhysicalDeviceSource);

		org.jdom.Namespace ns = disco.getNamespace();
		ResourceUtil.addStatement(deviceURI, Device.hasDiscoveryInfo, discoURI, f, g);

		ResourceUtil.addStatement(discoURI, Rdf.rdfType, Discovery.PhysicalDeviceSource.stringValue(), f, g);

		ResourceUtil.addStatement(
				discoURI, 
				Discovery.pdsDeviceType, 
				disco.getChildTextTrim("deviceType", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		ResourceUtil.addStatement(
				discoURI, 
				Discovery.pdsVendor, 
				disco.getChildTextTrim("vendor", ns), 
				XMLSchema.STRING, 
				f, 
				g);

		return g;
	}

	
	/**
	 * Generates discovery information of specific type and assigns it to device URI. Actually,
	 * the four types of discovery information are processed:
	 * <ul>
	 *   <li>tellstick</li>
	 *   <li>bluetootk</li>
	 *   <li>sensor</li>
	 *   <li>rfid tag</li>
	 * </ul>
	 * Discovery type is identified by the discovery XML structure.
	 * @param deviceURI Ontology URI of device.
	 * @param xml Discovery information XML.
	 * @return Graph of discovery information.
	 */
	public Graph process(String deviceURI, String xml){
		Element root = parse(xml);

		if(root == null) return null;

		if(root.getName().equals("tellstickdevice")){
			return processTellstick(deviceURI, root);
		}
		else if(root.getName().equals("rfidtagdevice")){
			return processRFIDTag(deviceURI, root);
		}
		else if(root.getName().equals("sensor")){
			return processSensor(deviceURI, root);
		}
		else if(root.getName().equals("bluetoothdevice")){
			return processBlueTooth(deviceURI, root);
		}
		else if(root.getName().equals("physicaldevicesource")){
			return processPhysicalDeviceSource(deviceURI, root);
		}
		return null;
	}

}
