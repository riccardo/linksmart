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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for parsing the AOM queries specifying expectations and requirements for
 * devices or services search.
 * 
 * @author Peter Kostelnik
 *
 */
public class QueryParser {
	
	private List<String> translateBase(String query, String baseVar, int index) throws Exception {
		String sparql = "";

		List<List<String>> clauses = parse(query);
		Iterator<List<String>> i = clauses.iterator();
		while(i.hasNext()){
			List<String> sparqlResult = translateSequence(i.next(), baseVar, index);
			if(sparqlResult == null) return null;
			String sparqlPart = sparqlResult.get(0);
			if(sparqlPart == null) return null;
			index = Integer.parseInt(sparqlResult.get(1));
			sparql += sparqlPart;
		}
		return Arrays.asList(sparql, index + "");
	}

	/**
	 * Translates AOM query to SPARQL query.
	 * AOM query is defined as comma separated sequences of properties
	 * navigating from device to target entity. Each sequence ends with
	 * the required value of target entity. In value is not defined, 
	 * then the sentence asks for entity existence. <br/>
	 * Example:<br/>
	 * <pre>
	 * AOM query:: 
	 * device:hasService/service:hasInput/service:quality;prefix:required_value,
	 * device:hasConfiguration
	 * 
	 * translated SPARQL:
	 * SELECT DISTINCT ?AOMDevice WHERE {
	 *   ?AOMDevice device:hasService ?hasService. 
	 *   ?hasService service:hasInput ?hasInput. 
	 *   ?hasInput service:quality prefix:required_value.
	 *   
	 *   ?AOMDevice device:hasConfiguration ?hasConfiguration.
	 * }
	 * </pre>
	 * When query is executed, results are filtered by required values. 
	 * In this case, retrieved devices must have some service
	 * with input having operation named 'prefix:required_value' and devices must have some configuration(s).
	 * The required value must be the instance, not the literal. 
	 * All used properties and values must follow the ontology vocabulary.
	 * @param AOM query.
	 * @return SPARQL query.
	 */
	public String translate(String query) throws Exception {
		boolean incorrect = false;

		List<String> sparqlResult = translateBase(query, "?AOMDevice", 1);
		if(sparqlResult == null)
			throw new Exception("Incorrect AOM query: \n["+query+"]\n");

		String sparql = sparqlResult.get(0);

		if(sparql == null || sparql.trim().equals("")) incorrect = true;;

		if(incorrect)
			throw new Exception("Incorrect AOM query: \n["+query+"]\n");

		return "SELECT DISTINCT ?AOMDevice WHERE { \n" + 
		sparql + 
		"}";
	}

	/**
	 * Translates AOM query to SPARQL query searching for service. Thus, all services
	 * matching query are retrieved, to each service there is retrieved also device URI.
	 * @param AOM query.
	 * @return SPARQL query.
	 */
	public String translateService(String sQuery, String dQuery) throws Exception {
		boolean incorrect = false;

		List<String> sSparqlResult = translateBase(sQuery, "?AOMService", 1);

		if(sSparqlResult == null)
			throw new Exception("Incorrect AOM service query: \n["+sQuery+"]\n");

		String sSparql = sSparqlResult.get(0);

		int index = Integer.parseInt(sSparqlResult.get(1));
		String dSparql = "";
		if(!dQuery.trim().equals("")){
			List<String> dSparqlResult = translateBase(dQuery, "?AOMDevice", index);
			if(dSparqlResult == null)
				throw new Exception("Incorrect AOM device query: \n["+dQuery+"]\n");
			dSparql = dSparqlResult.get(0);
		}

		if(sSparql == null || sSparql.trim().equals("")) {
			throw new Exception("Incorrect AOM service query: \n["+sQuery+"]\n");
		}
		if(dSparql == null){
			throw new Exception("Incorrect AOM device query: \n["+dQuery+"]\n");
		}

		return "SELECT DISTINCT ?AOMDevice ?AOMService WHERE { \n" +
		"  ?AOMDevice device:hasService ?AOMService. \n" + 
		"  ?AOMDevice device:isDeviceTemplate \"false\"^^xsd:boolean. \n" + 
		dSparql + 
		sSparql + 
		"}";
	}

	/**
	 * Parses AOM query to map of clauses.
	 * AOM query is defined as comma separated sequences of properties
	 * navigating from device to target entity. Last item may end with
	 * the required value of target entity.  The keys of returned map are the last 
	 * items in the sequences - the names of required value. Each key is connected
	 * with list of parsed sequences to be retrieved from matching entities. Required value
	 * is the last item separated by semicolon. Semicolon must be presented only
	 * in case, that the target entity value is defined. Between
	 * the last property in sequence and the target entity must not be any white space.  
	 * Example:<br/>
	 * <pre>
	 * AOM requirement query:: 
	 * device:hasService/service:hasInput/service:quality;unit:Celsius,
	 * device:hasConfiguration
	 * 
	 * parsed as map:
	 * service:quality;unit:Celsius -> List(device:hasService, service:hasInput, service:quality;unit:Celsius)
	 * device:hasConfiguration -> List(device:hasConfiguration)
	 * </pre>
	 * All used properties and values must follow the ontology vocabulary.
	 * @param AOM query.
	 * @return Parsed query.
	 */
	public List<List<String>> parse(String clauses) throws Exception {
		try{
			List<List<String>> parsed = new ArrayList<List<String>>();
			boolean incorrect = false;

			String[] parts = clauses.split(",");
			for(int i = 0; i < parts.length; i++){
				if(!parts[i].trim().equals("")){
					List<String> clause = parseClause(parts[i]);
					if(clause == null) incorrect = true;
					parsed.add(clause);
				}
			}

			if(parsed.size() == 0) incorrect = true;

			if(incorrect)
				throw new Exception("Incorrect AOM clauses: \n["+clauses+"]\n");

			return parsed;

		}
		catch(Exception e){
			throw new Exception("Incorrect AOM clauses: \n["+clauses+"]\n");
		}
	}


	private String[] queryItem(String item){
		String[] itemParts = new String[2];

		String[] literalParts = item.split("\\^\\^xsd:");
		if(literalParts.length == 2){
			String[] literalPartsUpdated = new String[2];
			literalPartsUpdated[0] = literalParts[0] + "^^xsd";
			literalPartsUpdated[1] = literalParts[1];
			return literalPartsUpdated;
		}

		String[] parts = item.split(":");
		if(parts.length == 2){
			String prefix = parts[0].trim();
			String value = parts[1].trim();
			itemParts[0] = prefix;
			itemParts[1] = value;
			return itemParts;
		}
		return null;
	}
	
	private String[] lastQueryItem(String item){
		String[] parts = item.split(";");
		if(parts.length == 1) return queryItem(item);
		if(parts.length == 2){
			String[] itemParts = new String[4];
			String[] propertyParts = queryItem(parts[0]);
			String[] valueParts = queryItem(parts[1]);
			itemParts[0] = propertyParts[0];
			itemParts[1] = propertyParts[1];
			itemParts[2] = valueParts[0];
			itemParts[3] = valueParts[1];
			return itemParts;
		}
		return null;
	}
	
	private List<String> translateSequence(List<String> sequence, String baseVar, int index){
		String sparql = "";

		String lastVar = "";
		for(int i = 0; i < sequence.size(); i++){
			if(i == (sequence.size() - 1)){
				String[] item = lastQueryItem(sequence.get(i));
				if(item == null) return null;

				String propertyPrefix = item[0];
				String propertyValue = item[1];

				if(sequence.size() == 1){
					lastVar = baseVar;
				}

				if(item.length == 4){
					String valuePrefix = item[2];
					String valueValue = item[3];
					sparql += "  "+lastVar+" "+propertyPrefix+":"+propertyValue+" "+valuePrefix+":"+valueValue+". \n";
				}
				else{
					String var = "?"+propertyPrefix+"_"+propertyValue+"_"+index;
					index = index + 1;
					sparql += "  "+lastVar+" "+propertyPrefix+":"+propertyValue+" "+var+". \n";
				}

			}
			else{
				String[] item = queryItem(sequence.get(i));
				if(item == null) return null;
				String prefix = item[0];
				String value = item[1];

				String var = "?"+prefix+"_"+value+"_"+index;
				index = index + 1;
				if(i == 0){
					sparql += "  "+baseVar+" "+prefix+":"+value+" "+var+". \n";
				}
				else{
					sparql += "  "+lastVar+" "+prefix+":"+value+" "+var+". \n";
				}
				lastVar = var;
			}
		}
		return Arrays.asList(sparql, index + "");
	}

	private List<String> parseClause(String clause){
		List<String> sequence = new ArrayList<String>();
		String[] parts = clause.split("/");
		for(int i = 0; i < parts.length; i++){
			String part = parts[i];
			if(!part.trim().equals("")){
				sequence.add(part.trim());
			}
		}

		if(sequence.size() == 0) return null;
		return sequence;
	}

	public Map<String, List<String>> parseValue(String query) throws Exception {
		List<List<String>> clauses = parse(query);
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for(List<String> c : clauses){
			String last = c.get(c.size() - 1);
			String[] item = lastQueryItem(last);
			if(item == null) {
				map.put(null, c);
			}
			else{
				String propertyPrefix = item[0];
				String propertyValue = item[1];

				if(item.length == 4){
					String valuePrefix = item[2];
					String valueValue = item[3];
					c.remove(c.size() - 1);
					c.add(propertyPrefix + ":" + propertyValue);
					map.put(valuePrefix + ":" + valueValue, c);
				}
				else{
					map.put(null, c);
				}
			}

		}
		return map;
	}
}
