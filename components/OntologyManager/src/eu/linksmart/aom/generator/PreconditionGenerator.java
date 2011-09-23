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
import eu.linksmart.aom.ontology.model.Rdf;

/**
 * Class responsible for generation of semantic device preconditions XML.
 * 
 * @author Peter Kostelnik
 *
 */
public class PreconditionGenerator extends Generator {

	/**
	 * Generates the XML containig the precondition and adds it to the list of semantic device preconditions.
	 * @param precondition Ontology model of precondition.
	 * @param preconditionList Parent element.
	 * @return XML containing the precondition list.
	 */
	public Element processPrecondition(Graph precondition, Element preconditionList){
		Map<String, String> attrs = new HashMap<String, String>(); 
		attrs.put("id", precondition.value(Device.preconditionId));
		attrs.put("deviceType", precondition.value(Device.preconditionDeviceType));

		String preconditionType = precondition.value(Rdf.rdfType);

		if(preconditionType != null && preconditionType.equals(Device.SemanticDevicePIDPrecondition.stringValue())){
			String pids = "";
			Iterator<String> i = precondition.values(Device.preconditionPID).iterator();
			while(i.hasNext()){
				pids += i.next();
				if(i.hasNext()) pids += ";";
			}
			attrs.put("PID", pids);
		}
		else if(preconditionType != null && preconditionType.equals(Device.SemanticDeviceQueryPrecondition.stringValue())){
			attrs.put("query", precondition.value(Device.preconditionQuery));
			attrs.put("cardinality", precondition.value(Device.preconditionCardinality));
		}
		Element preconditionElm = addElement("precondition", attrs, preconditionList);
		Element satisfying = addElement("satisfyingDevices", preconditionElm);
		for(String deviceURI : precondition.values(Device.isSatisfiedBy)){
			addElement("deviceURI", deviceURI, satisfying);
		}
		return preconditionElm;
	}

	/**
	 * Generates XML of semantic device preconditions and adds it to device XML element.
	 * @param preconditions Ontology models of preconditions.
	 * @param device Device XML element.
	 * @return XML containing the precondition list.
	 */
	public Element process(Set<Graph> preconditions, Element device){
		Element preconditionList = addElement("deviceList", device);
		for(Graph precondition : preconditions){
			processPrecondition(precondition, preconditionList);
		}
		return preconditionList;
	}
}
