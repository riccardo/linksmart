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

package eu.linksmart.aom.repository;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.ValueFactory;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.schema.ResourceProperty;
import eu.linksmart.aom.ontology.util.ResourceUtil;

/**
 * Class responsible for generation the inverse properties from devices to application entities.
 * In model, the application entities can be associated to devices using device PIDs. To be able
 * to query the application context from devices, it is required to generate the inverse links.
 * PIDRuleResolver is executed each time when some device gets the PID assigned. All inverse 
 * links are generated using the information derived from ontology.
 * 
 * @author Peter Kostelnik
 *
 */
public class PIDRuleResolver {
	AOMRepository repository;

	public PIDRuleResolver(AOMRepository repository){
		this.repository = repository;
	}

	private RuleResult getResult(String property, Set<RuleResult> results){
		for(RuleResult r : results){
			if(r.property.equals(property)){
				return r;
			}
		}    
		return null;
	}

	/**
	 * Identifies the entities having associated the PID, their properties and inverse properties.
	 * @param pid PID assigned to device.
	 * @return Set of rule results.
	 */
	public Set<RuleResult> ruleResults(String pid){
		Set<RuleResult> results = new HashSet<RuleResult>();
		String query = "" +
		"SELECT ?resource ?property ?inverse WHERE {\n" +
		"  ?resource ?property ?dpid. \n" +
		"  ?dpid rdf:type rule:DevicePID. \n" +
		"  ?dpid rule:PID \""+pid+"\"^^xsd:string. \n" +
		"  ?property rule:generatesInverseProperty ?inverse. \n" +
		"}";
		Set<SPARQLResult> sResults = repository.sparql(query);

		for(SPARQLResult r : sResults){
			String property = r.value("property");
			RuleResult existing = getResult(property, results);
			if(existing != null){
				existing.inverseProperties.add(r.value("inverse"));
				existing.resources.add(r.value("resource"));
			}
			else{
				RuleResult newResult = new RuleResult(property);
				newResult.inverseProperties.add(r.value("inverse"));
				newResult.resources.add(r.value("resource"));
				results.add(newResult);
			}
		}
		return results;
	}

	private Graph addResources(Graph device, RuleResult result){
		ValueFactory f = repository.getValueFactory();
		for(String invProperty : result.inverseProperties){
			for(String resource : result.resources){
				ResourceUtil.addStatement(
						device.getBaseURI(), 
						new ResourceProperty(invProperty), 
						resource, 
						f, 
						device);
			}
		}
		return device;
	}

	/**
	 * Resolves all properties from device to application entity. Property rules are defined in 
	 * ontology. Each time the PID is assigned to some device, resolver looks for all entities
	 * having this PID assigned, it takes all entity properties and using the ontology model
	 * it generates the inverse properties from device to identified entities.
	 * 
	 * @param pid New PID assigned to device.
	 */
	public void resolve(String pid){
		try{
			Set<Graph> devices = repository.getDevices("device:PID;\""+pid+"\"^^xsd:string,device:isDeviceTemplate;\"false\"^^xsd:boolean");
			if(devices.size() == 1){
				Graph device = devices.iterator().next();
				Set<RuleResult> results = ruleResults(pid);
				for(RuleResult result : results){
					repository.store(addResources(device, result));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
