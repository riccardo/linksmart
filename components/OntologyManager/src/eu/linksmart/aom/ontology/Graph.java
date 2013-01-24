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

package eu.linksmart.aom.ontology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;

import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.model.Unit;
import eu.linksmart.aom.ontology.schema.LiteralProperty;
import eu.linksmart.aom.ontology.schema.Property;
import eu.linksmart.aom.ontology.schema.ResourceProperty;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.QueryParser;

/**
 * Class responsible for maintaining, searching and serialization of ontology graphs.
 * The graph contains the base ontology URI of the root instance and the set of 
 * the statements. 
 * This class extends and improves the Sesame GraphImpl class. 
 * Improvements:
 * <ul>
 *   <li>Statements are holded in set instead of list.</li>
 *   <li>Various methods for extending the graph were added.</li>
 *   <li>Various methods for graph search were added.</li>
 *   <li>Methods for graph serialization were added.</li>
 * </ul> 
 * 
 * @author Peter Kostelnik
 *
 */
public class Graph extends GraphImpl {
	private String baseURI = "";
	private HashSet<Statement> stmts = new HashSet<Statement>();

	public Graph(String baseURI, Collection<Statement> s){
		this.baseURI = baseURI;
		addAll(s);
	}

	public Graph(String baseURI){
		this.baseURI = baseURI;
	}

	@Override
	public Iterator<Statement> iterator() {
		return this.stmts.iterator();
	}

	@Override
	public boolean add(Statement st) {
		return stmts.add(st);
	}

	/**
	 * Adds another graph to this graph.
	 * @param g Graph to add.
	 * @return This extended graph.
	 */
	public Graph add(Graph g) {
		this.stmts.addAll(g.stmts);
		return this;
	}

	/**
	 * Adds the triplet to the graph.
	 * @param s Subject URI.
	 * @param p Property URI.
	 * @param o Object URI.
	 * @param f ValueFactory for triplestore specific statement creation.
	 * @return
	 */
	public Graph add(String s, String p, String o, ValueFactory f) {
		this.stmts.add(ResourceUtil.statement(s, p, o, f));
		return this;
	}

	/**
	 * Removes statement from the graph.
	 * @param st Statement to remove.
	 * @return Notification on operation success.
	 */
	public boolean remove(Statement st) {
		return stmts.remove(st);
	}

	@Override
	public int size() {
		return stmts.size();
	}

	@Override 
	public boolean equals(Object o){
		if(o instanceof Graph){
			Graph g = (Graph)o;
			return (
					this.baseURI.equals(g.baseURI) &&
					this.stmts.equals(g.stmts));
		}
		else return false;
	}

	@Override 
	public String toString(){
		return "[GRAPH: " + this.baseURI + ":" + this.stmts.size() + "]";
	}


	/**
	 * Helper method which serializes the graph content to string including all triplets.
	 * @return Graph serialization.
	 */
	public String show(){
		String out = "\n\n[GRAPH: " + this.baseURI + ":" + this.stmts.size() + "]\n";
		Iterator<Statement> i = this.stmts.iterator();
		while(i.hasNext()){
			Statement s = i.next();
			out += "  ["+s.getSubject()+":"+s.getPredicate()+":"+s.getObject()+":"+s.getContext()+"]\n";
		}
		return out;
	}

	@Override 
	public int hashCode(){
		return stmts.hashCode();
	}

	public String getBaseURI(){
		return this.baseURI;
	}

	public void setBaseURI(String baseURI){
		this.baseURI = baseURI;
	}
	public HashSet<Statement> getStmts(){
		return this.stmts;
	}

	/**
	 * Returns the set of property values for triplet: baseURI property value. If values
	 * of property are instances, the URIs are retrieved. For literals, the value of literals are 
	 * retrieved.
	 * @param property Ontology property.
	 * @return Set of property values.
	 */
	public HashSet<String> values(Property property) {
		HashSet<String> values = new HashSet<String>();
		Iterator<Statement> i = match(new URIImpl(baseURI), property, null);
		while(i.hasNext()){
			values.add(i.next().getObject().stringValue());
		}
		return values;
	}

	/**
	 * Returns the property value for triplet: baseURI property value. If value
	 * of property is instance, the URI is retrieved. For literal, the value of literal is 
	 * retrieved. If the property has more values, first is returned (values in set are 
	 * in random order, so, first returned means - one of the values). 
	 * @param property Ontology property.
	 * @return Property value.
	 */
	public String value(Property property) {
		HashSet<String> values = values(property);
		if(values.size() == 0){
			return null; 
		}
		return values.iterator().next();
	}

	private Graph subGraph(Value base, Graph g){
		if(base instanceof Resource){
			Iterator<Statement> i = match((Resource)base, null, null);
			while(i.hasNext()){
				Statement s = i.next();
				g.add(s);
				subGraph(s.getObject(), g);
			}
		}
		return g;
	}

	private Graph subGraph(Statement s){
		Value base = s.getObject();
		Graph g = new Graph(base.stringValue());
		g.add(s);
		return subGraph(base, g);
	}

	/**
	 * Returns the all values of property for triplet: baseURI property value. Value must 
	 * be resource (instance). For each instance value of property, full subgraph is recursively generated.
	 * @param property Ontology property.
	 * @return Set of full subgraphs of property values.
	 */
	public HashSet<Graph> subGraphs(ResourceProperty property) {
		HashSet<Graph> graphs = new HashSet<Graph>();
		Iterator<Statement> i = match(new URIImpl(baseURI), property, null);
		while(i.hasNext()){
			Statement s = i.next();
			graphs.add(subGraph(s));
		}
		return graphs;
	}

	/**
	 * Returns the value of property for triplet: baseURI property value. Value must 
	 * be resource (instance). For instance value of property, full subgraph is recursively generated.
	 * If there were more values matched, one of them is returned (as the values are in set 
	 * containing the retrieved values in random order).
	 * @param property Ontology property.
	 * @return Set of full subgraphs of property values.
	 */
	public Graph subGraph(ResourceProperty property) {
		HashSet<Graph> graphs = subGraphs(property);
		if(graphs.size() == 0){
			return null; 
		}
		return graphs.iterator().next();
	}

	private String indent(int indent, String cnt) {
		String out = "";
		for(int i = 0; i < indent; i++){
			out += "  ";
		}
		return out + "> " + cnt + "\n";
	}

	private String describe(Value base, int indent) {
		String out = "";
		if(base instanceof Resource){
			URI baseURI = new URIImpl(base.stringValue());
			String basePrefix = Namespace.prefix(baseURI.getNamespace());
			String baseValue = "";
			if(basePrefix == null){
				baseValue = baseURI.stringValue();
			}
			else{
				baseValue =  basePrefix + ":" + baseURI.getLocalName();
			}
			out += indent(indent, baseValue);
			indent += 2;
			Iterator<Statement> i = match((Resource)base, null, null);
			while(i.hasNext()){
				Statement s = i.next();
				Value obj = s.getObject();
				String value = obj.stringValue();
				if(obj instanceof Resource) {
					URI objURI = new URIImpl(obj.stringValue());
					String prefix = Namespace.prefix(objURI.getNamespace());
					if(prefix == null){
						value = obj.stringValue();
					}
					else{
						value =  prefix + ":" + objURI.getLocalName();
					}
				}
				if(obj instanceof Literal){
					Literal literal = (Literal)obj;
					URI dt = literal.getDatatype();
					value = "\"" + 
					literal.stringValue() + 
					"\" type of " + 
					Namespace.prefix(dt.getNamespace()) + ":" + 
					dt.getLocalName();
				}
				URI p = s.getPredicate();

				out += indent(indent, (Namespace.prefix(p.getNamespace()) + ":" + p.getLocalName() + ": " + value));
				if(!p.stringValue().equals(Rdf.rdfType.stringValue())){
					out += describe(s.getObject(), (indent + 2));
				}
			}
		}
		return out;
	}

	/**
	 * Returns the human readable serialization of graph. The serialization is recursively
	 * generated, for each instance, the values of all properties are listed.
	 * @return Graph serialization.
	 */
	public String describe() {
		return describe(new URIImpl(baseURI), 0);
	}

	private String describe(int indent) {
		return describe(new URIImpl(baseURI), indent);
	}

	private String toSPARQL(Value base){
		String sparql = "";
		if(base instanceof Resource){
			URI baseURI = new URIImpl(base.stringValue());
			String var = "?" + baseURI.getLocalName();
			if(baseURI.stringValue().equals(getBaseURI())){
				var = "?AOMDevice";
			}
			Iterator<Statement> i = match((Resource)base, null, null);
			while(i.hasNext()){
				Statement s = i.next();

				Value obj = s.getObject();
				URI p = s.getPredicate();

				String value = obj.stringValue();
				if(obj instanceof Resource) {
					URI objURI = new URIImpl(obj.stringValue());
					value = "?" + objURI.getLocalName();
					if(p.stringValue().equals(Rdf.rdfType.stringValue())){
						value = Namespace.prefix(objURI.getNamespace()) + ":" + objURI.getLocalName();
					}
				}
				else if(obj instanceof Literal){
					Literal literal = (Literal)obj;
					URI dt = literal.getDatatype();
					value = "\"" + 
					literal.stringValue() + 
					"\"^^" + 
					Namespace.prefix(dt.getNamespace()) + ":" + 
					dt.getLocalName();
				}
				sparql += "" +
				"  " + var + " " + 
				(Namespace.prefix(p.getNamespace()) + ":" + p.getLocalName() + " " + 
						value + ". \n");
				if(!p.stringValue().equals(Rdf.rdfType.stringValue())){
					sparql += toSPARQL(s.getObject());
				}
			}

		}
		return sparql;
	}

	/**
	 * Generates the SPARQL query for this graph. The graph must be device instance graph. 
	 * If query is executed, all devices containing the same graph pattern are matched.
	 * @return SPARQL serialization of the graph.
	 */
	public String toSPARQL() {
		return "" + 
		"SELECT DISTINCT ?AOMDevice WHERE { \n" +
		toSPARQL(new URIImpl(baseURI)) + 
		"}";
	}

	private String cloneSuffix(AOMRepository repo){
		return "/" + repo.getURI("RUNTIME");
	}

	private Graph clone(Value base, String baseURI, Graph cloned, AOMRepository repo){
		ValueFactory f = repo.getValueFactory();
		Iterator<Statement> i = match((Resource)base, null, null);
		while(i.hasNext()){
			Statement s = i.next();

			Value obj = s.getObject();
			URI p = s.getPredicate();

			String value = obj.stringValue();
			if(obj instanceof Resource) {
				URI objURI = new URIImpl(obj.stringValue());
				if(Namespace.isStaticProperty(p) ||
						repo.isInStaticModel(obj)){
					value = objURI.stringValue();
				}
				else{
					value = objURI.stringValue() + cloneSuffix(repo);
					clone(obj, value, cloned, repo);
				}
				ResourceUtil.addStatement(
						baseURI, 
						new ResourceProperty(p.stringValue()), 
						value, 
						f, 
						cloned);
			}
			else if(obj instanceof Literal){
				Literal literal = (Literal)obj;
				URI dt = literal.getDatatype();
				ResourceUtil.addStatement(
						baseURI, 
						new LiteralProperty(p.stringValue()), 
						literal.stringValue(), 
						dt, 
						f, 
						cloned);
			}
		}
		return cloned;
	}

	/**
	 * Recursively creates the clone of this graph. All values of all properties are cloned, except
	 * the values of static properties and instances of static taxonomies.
	 * @param repo Repository.
	 * @return The graph clone.
	 */
	public Graph clone(AOMRepository repo){
		String clonedBaseURI = getBaseURI() + cloneSuffix(repo);
		return clone(new URIImpl(getBaseURI()), clonedBaseURI, new Graph(clonedBaseURI), repo);
	}

	/**
	 * Recursively creates the clone of this graph. Graph is expected to be device graph. 
	 * All values of all properties are cloned, except
	 * the values of static properties and instances of static taxonomies.
	 * Information that graph was cloned from concrete template is added to the clone.
	 * @param repo Repository.
	 * @param templateURI URI of device template, from which this graph is cloned.
	 * @return The graph clone.
	 */
	public Graph clone(AOMRepository repo, String templateURI){
		ValueFactory f = repo.getValueFactory();
		Graph cloned = clone(repo);
		ResourceUtil.addStatement(
				cloned.getBaseURI(), 
				Device.clonedFromTemplate, 
				templateURI, 
				f, 
				cloned);
		ResourceUtil.removeStatement(
				cloned.getBaseURI(), 
				Device.isDeviceTemplate, 
				"true", 
				XMLSchema.BOOLEAN, 
				f, 
				cloned);
		ResourceUtil.addStatement(
				cloned.getBaseURI(), 
				Device.isDeviceTemplate, 
				"false", 
				XMLSchema.BOOLEAN, 
				f, 
				cloned);
		return cloned;
	}


	private String getUnitValue(Graph g, int index) throws Exception {
		String rdfType = g.value(Rdf.rdfType);
		if(rdfType != null && rdfType.equals(Unit.UnitValue.stringValue())){
			String value = g.value(Unit.value);
			String inUnit = g.value(Unit.inUnit);
			if(value != null){
				String out = value + "!";
				if(inUnit != null){
					URI inUnitURI = new URIImpl(inUnit);
					String prefix = Namespace.prefix(inUnitURI.getNamespace());
					if(prefix == null){
						out += inUnit;
					}
					else{
						out +=  prefix + ":" + inUnitURI.getLocalName();
					}
					return out;
				}
				else  return out + "-";
			}
			else return "-!-";
		}
		return null;
	}

	private Set<String> getRequirements(Graph g, ResourceProperty p, int index) throws Exception {
		Set<String> requirements = new HashSet<String>();
		Iterator<Statement> i = g.getStmts().iterator();
		while(i.hasNext()){
			Statement st = i.next();
			if(st.getPredicate().stringValue().equals(p.stringValue())){
				Value obj = st.getObject();
				String value = obj.stringValue();
				if(obj instanceof Resource) {
					String unitValue = getUnitValue(g, index + 2);
					if(unitValue == null){
						URI objURI = new URIImpl(obj.stringValue());
						String prefix = Namespace.prefix(objURI.getNamespace());
						if(prefix == null){
							value = obj.stringValue();
						}
						else{
							value =  prefix + ":" + objURI.getLocalName();
						}
					}
					else {
						value = unitValue;
					}
				}
				if(obj instanceof Literal){
					Literal literal = (Literal)obj;
					URI dt = literal.getDatatype();
					value = literal.stringValue(); 
				}
				requirements.add(value);
			}
		}
		return requirements;
	}

	private Set<String> getRequirements(List<String> sequence, Graph g, int index) throws Exception {
		String prop = sequence.get(index);
		String propParts[] = prop.split(":");
		String uri = Namespace.uri(propParts[0]);
		String value = propParts[1];
		ResourceProperty property = new ResourceProperty(uri + value);

		Set<Graph> gs = g.subGraphs(property);
		if(gs.size() == 0) {
			return new HashSet<String>();
		}

		if(index == (sequence.size() - 1)){
			Set<String> requirements = new HashSet<String>();
			for(Graph r : gs){
				requirements.addAll(getRequirements(r, property, index + 2));
			}
			return requirements;
		}
		else{
			Set<String> requirements = new HashSet<String>();
			for(Graph r : gs){
				requirements.addAll(getRequirements(sequence, r, (index + 1)));
			}
			return requirements;
		}
	}

	/**
	 * Retrieves the map of requirements matching the query string from this graph.
	 * @param query Requirements query string.
	 * @return Map of requirement values, keys are the requirements, values are requirement values.
	 * @throws Exception
	 */
	public Map<String, Set<String>> getRequirements(String query) throws Exception {
		try {
			Map<String, Set<String>> rMap = new HashMap<String, Set<String>>();
			if(query.trim().equals("")) return rMap;
			List<List<String>> sequences = new QueryParser().parse(query);
			for(List<String> sequence : sequences){
				rMap.put(sequence.get(sequence.size() - 1), getRequirements(sequence, this, 0));
			}
			return rMap;
		}
		catch(Exception e){
			e.printStackTrace();
			throw new Exception("Incorrect AOM requirement clauses: \n["+query+"]\n");
		}
	}

	private List<String> getUpdateStatementBase(List<String> sequence, Graph g, int index) throws Exception {
		String prop = sequence.get(index);
		String propParts[] = prop.split(":");
		String uri = Namespace.uri(propParts[0]);
		String value = propParts[1];
		ResourceProperty property = new ResourceProperty(uri + value);

		Set<Graph> gs = g.subGraphs(property);
		if(gs.size() != 1) {
			return null;
		}

		if(index == (sequence.size() - 1)){
			List<String> result = new ArrayList<String>();
			result.add(g.getBaseURI());
			result.add(property.stringValue());
			return result;
		}
		else{
			

			Graph sg = gs.iterator().next();
			return getUpdateStatementBase(sequence, sg, (index + 1));
		}
	}


	public List<String> getUpdateStatementBase(String path) throws Exception {
		try {
			if(path.trim().equals("")) return null;
			Map<String, List<String>> sequences = new QueryParser().parseValue(path);
			if(sequences.size() != 1){
				return null;
			}
			String value = sequences.keySet().iterator().next();
			if(value == null) return null;
			List<String> base = getUpdateStatementBase(sequences.get(value), this, 0);
			if(base == null) return null;
			String valParts[] = value.split(":");
			String uri = Namespace.uri(valParts[0]);
			String val = valParts[1];

			base.add(uri + val);
			return base;
		}
		catch(Exception e){
			e.printStackTrace();
			throw new Exception("Incorrect AOM requirement clauses: \n["+path+"]\n");
		}
	}
}

