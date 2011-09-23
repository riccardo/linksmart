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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Service;

/**
 * Class responsible for generation of XML of query results.
 * 
 * @author Peter Kostelnik
 *
 */
public class QueryResponseGenerator extends Generator {

	/**
	 * Generates XML of matched service.
	 * @param service Ontology model of service.
	 * @param deviceElm XML element of parent device element.
	 * @param serviceRequirements Requirements specified for services.
	 * @return XML of matched service.
	 * @throws Exception
	 */
	public Element serviceResult(Graph service, Element deviceElm, String serviceRequirements) throws Exception {
		if(service.value(Service.matchedService) != null){
			Map<String, String> attrs = new HashMap<String, String>();
			attrs.put("operation", service.value(Service.serviceOperation));
			Element serviceElm = addElement("service", attrs, deviceElm);
			addRequirements(service, "serviceProperties", serviceElm, serviceRequirements);
			return serviceElm;
		}
		return null;
	}

	private Element addRequirements(Graph g, String elmName, Element root, String reqQuery) throws Exception {
		Element propsElm = addElement(elmName, root);
		Map<String, Set<String>> requirements = g.getRequirements(reqQuery);
		Iterator<String> i = requirements.keySet().iterator();
		while(i.hasNext()){
			String name = i.next();
			Set<String> values = requirements.get(name);
			Map<String, String> attrs = new HashMap<String, String>();
			attrs.put("name", name);
			Element propertyElm = addElement("property", attrs, propsElm);
			for(String value : values){
				String[] valueParts = value.split("!");
				if(valueParts.length == 2){
					String val = valueParts[0];
					if(!val.trim().equals("-")){
						String unit = valueParts[1];
						if(unit.trim().equals("-")){
							addElement("value", val, propertyElm);
						}
						else{
							Map<String, String> valAttrs = new HashMap<String, String>();
							valAttrs.put("unit", unit);
							addElement("value", val, valAttrs, propertyElm);
						}
					}
				}
				else{
					addElement("value", value, propertyElm);
				}
			}
		}
		return root;
	}

	private Element deviceResult(Graph device, Element response, String deviceRequirements, String serviceRequirements, boolean includeServices) throws Exception{
		Map<String, String> attrs = new HashMap<String, String>();
		attrs.put("pid", device.value(Device.PID));
		attrs.put("uri", device.getBaseURI());
		Element deviceElm = addElement("device", attrs, response);
		addRequirements(device, "deviceProperties", deviceElm, deviceRequirements);
		if(includeServices){
			for(Graph service : device.subGraphs(Device.hasService)){
				serviceResult(service, deviceElm, serviceRequirements);
			}
		}
		return deviceElm;
	}

	/**
	 * Generates XML of device query result.
	 * @param device Ontolgy model of matched device.
	 * @param response Parent XML element of query results.
	 * @param deviceRequirements Requirements specified for devices.
	 * @param serviceRequirements Requirements specified for services.
	 * @return XML of device query result.
	 * @throws Exception
	 */
	public Element deviceResult(Graph device, Element response, String deviceRequirements, String serviceRequirements) throws Exception{
		return deviceResult(device, response, deviceRequirements, serviceRequirements, true);
	}

	/**
	 * Generates XML of device query result.
	 * @param device Ontolgy model of matched device.
	 * @param response Parent XML element of query results.
	 * @param deviceRequirements Requirements specified for devices.
	 * @return XML of device query result.
	 * @throws Exception
	 */
	public Element deviceResult(Graph device, Element response, String deviceRequirements) throws Exception{
		return deviceResult(device, response, deviceRequirements, "", false);
	}


	/**
	 * Generates XML for devices with services query results.
	 * @param devices Ontology models of matched devices.
	 * @param deviceRequirements Requirements specified for devices.
	 * @param serviceRequirements Requirements specified for services.
	 * @return XML of devices with services query results.
	 * @throws Exception
	 */
	public Element devicesWithServicesResult(Set<Graph> devices, String deviceRequirements, String serviceRequirements) throws Exception{
		Element response = new Element("response");
		for(Graph device : devices){
			deviceResult(device, response, deviceRequirements, serviceRequirements);
		}
		return response;
	}

	/**
	 * Generates XML for device query results.
	 * @param devices Ontology models of matched devices.
	 * @param deviceRequirements Requirements specified for devices.
	 * @return XML of device query results.
	 * @throws Exception
	 */
	public Element devicesResult(Set<Graph> devices, String deviceRequirements) throws Exception{
		Element response = new Element("response");
		for(Graph device : devices){
			deviceResult(device, response, deviceRequirements);
		}
		return response;
	}

	/**
	 * Generates XML of error result when query parser failed.
	 * @param e Exception caught by query parser.
	 * @return XML of error information.
	 */
	public Element error(Throwable e){
		Element response = new Element("response");
		addElement("error", e.getMessage(), response);
		return response;
	}
}
