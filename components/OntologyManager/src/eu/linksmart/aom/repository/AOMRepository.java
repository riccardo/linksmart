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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jdom.Element;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Model;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.model.Rdfs;
import eu.linksmart.aom.ontology.model.Service;
import eu.linksmart.aom.ontology.model.Unit;
import eu.linksmart.aom.ontology.schema.LiteralProperty;
import eu.linksmart.aom.ontology.schema.OntologyClass;
import eu.linksmart.aom.ontology.schema.Property;
import eu.linksmart.aom.ontology.util.ResourceUtil;

/**
 * Class responsible for handling the triplestore and all triplestore related functionality.
 * Class holds the Sesame repository created using RepositoryFactory.
 *  
 * @author Peter Kostelnik, Peter Smatana
 *
 */
public class AOMRepository {
	private Repository repository;

	public AOMRepository(Repository repository){
		this.repository = repository;
	}

	/**
	 * Gets the new repository connection.
	 * @return Connection or null it there is some repository problem.
	 */
	public RepositoryConnection getConnection() {
		try {
			return this.repository.getConnection();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Gets the triplestore specific value factory for creating the repository resources, such
	 * as statements.
	 * @return ValueFactory instance.
	 */
	public ValueFactory getValueFactory(){
		return this.repository.getValueFactory();
	}

	/**
	 * Correctly shuts down the repository.
	 */
	public void close() {
		try{
			this.repository.shutDown();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Stores the graph into the repository.
	 * @param g Graph to store.
	 */
	public void store(Graph g){
		RepositoryConnection conn = getConnection();
		try{
			conn.add(g.getStmts());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Removes the graph from the repository.
	 * @param g Graph to remove.
	 */
	public void remove(Graph g){
		RepositoryConnection conn = getConnection();
		try{
			conn.remove(g.getStmts());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Removes all values of literal property for statements having the deviceURI as subject.
	 * @param deviceURI Subject of statements to remove.
	 * @param p Property for which the statements should be removed.
	 */
	public void remove(String deviceURI, Property p){
		RepositoryConnection conn = getConnection();
		try{
			conn.remove(new URIImpl(deviceURI), p, null);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}


	/**
	 * Checks, if repository contains the class with specified URI. The URI is class, 
	 * if there exist information, that URI is rdfs:subClassOf some resource.
	 * @param classURI Ontology class URI to inspect.
	 * @return Notification if URI is ontology class.
	 */
	public boolean classExists(String classURI){
		RepositoryConnection conn = getConnection();
		try{
			RepositoryResult<Statement> result = 
				conn.getStatements(new URIImpl(classURI), Rdfs.subClassOf, null, true);
			if(result.hasNext()) return true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Checks, if repository contains the instance with specified URI. The URI is instance, 
	 * if there exist information, that URI is rdf:type of some resource.
	 * @param instanceURI Ontology instance URI to inspect.
	 * @return Notification if URI is ontology instance.
	 */
	public boolean instanceExists(String instanceURI){
		RepositoryConnection conn = getConnection();
		try{
			RepositoryResult<Statement> result = 
				conn.getStatements(new URIImpl(instanceURI), Rdf.rdfType, null, false);
			if(result.hasNext()) return true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return false;
	}

	private String queryPrefixes(){
		return "" +
		"PREFIX rdf: <"+Namespace.rdf+"> \n" +
		"PREFIX rdfs: <"+Namespace.rdfs+"> \n" +
		"PREFIX xsd: <"+Namespace.xsd+"> \n" + 
		"PREFIX device: <"+Namespace.device+"> \n" +
		"PREFIX service: <"+Namespace.service+"> \n" +
		"PREFIX event: <"+Namespace.event+"> \n" +
		"PREFIX energy: <"+Namespace.energy+"> \n" +
		"PREFIX discovery: <"+Namespace.discovery+"> \n" +
		"PREFIX configuration: <"+Namespace.configuration+"> \n" +
		"PREFIX hardware: <"+Namespace.hardware+"> \n" +
		"PREFIX unit: <"+Namespace.unit+"> \n" +
		"PREFIX security: <"+Namespace.security+"> \n" +
		"PREFIX model: <"+Namespace.model+"> \n" +
		"PREFIX application: <"+Namespace.application+"> \n" +
		"PREFIX rule: <"+Namespace.rule+"> \n" +
		"";
	}

	private QueryResult<Statement> graphQuery(RepositoryConnection conn, String queryString)
	throws Exception {
		GraphQuery query = conn.prepareGraphQuery(
				QueryLanguage.SPARQL, 
				queryPrefixes() + queryString);
		return query.evaluate();
	}

	private Graph getResource(RepositoryConnection conn, Value base, Graph g) throws Exception {
		if(base instanceof Resource){
			RepositoryResult<Statement> result = conn.getStatements((Resource)base, null, null, false);
			while(result.hasNext()){
				Statement s = result.next();
				g.add(s);
				if(!Namespace.isStaticProperty(s.getPredicate()) &&
						(!(s.getObject() instanceof Literal)) &&
						!isInStaticModel(s.getObject())){
					getResource(conn, s.getObject(), g);
				}
			}
		}
		return g;
	}
	
	/**
	 * Recursively retrieves the full graph for the root resource specified as instance URI.
	 * @param instanceURI URI of instance for which the graph is retrieved.
	 * @return Instance Graph.
	 */
	public Graph getResource(String instanceURI) {
		RepositoryConnection conn = getConnection();
		try{
			Graph device = getResource(conn, new URIImpl(instanceURI), new Graph(instanceURI));
			if(device.getStmts().size() == 0) return null;
			return device;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return null;
	}

	
	private String value(String var, BindingSet b){
		Value value = b.getValue(var);
		if(value == null) return "";
		return value.stringValue();
	}

	/**
	 * Executes the SPARQL query.
	 * @param conn Repository connection.
	 * @param queryString SPARQL query.
	 * @return Query resutls.
	 * @throws Exception
	 */
	public TupleQueryResult sparqlQuery(RepositoryConnection conn, String queryString) throws Exception {
		TupleQuery query = conn.prepareTupleQuery(
				QueryLanguage.SPARQL, 
				(queryPrefixes() + queryString));
		return query.evaluate();
	}

	/**
	 * Retrieves the ontology URI of unit instance for unit name, if such a unit exists in ontology.
	 * @param unit Local name of the unit.
	 * @return Unit ontology URI.
	 */
	public String getUnit(String unit) {
		String unitURI = Namespace.unit + unit;
		if(instanceExists(unitURI)) return unitURI;
		return null;
	}

	/**
	 * Retrieves the graph of UnitValue instance if unit with specified name exists.
	 * @param value Definition of unit value.
	 * @param unit Unit name.
	 * @return Graph of UnitValue instance.
	 */
	public Graph getUnitValue(String value, String unit) {
		String unitURI = getUnit(unit);
		ValueFactory f = getValueFactory();
		String uvURI = getURI(Unit.UnitValue);
		Graph g = new Graph(uvURI);

		ResourceUtil.addStatement(
				uvURI, 
				Rdf.rdfType, 
				Unit.UnitValue.stringValue(), 
				f, 
				g);

		ResourceUtil.addStatement(
				uvURI, 
				Unit.value, 
				value, 
				XMLSchema.STRING, 
				f, 
				g);

		if(unitURI != null) {
			ResourceUtil.addStatement(
					uvURI, 
					Unit.inUnit, 
					unitURI, 
					f, 
					g);
		}

		return g;
	}


	/**
	 * Retrieves the graphs of devices matching the AOM query. From AOM query, the SPARQL query is generated.
	 * @param query AOM query.
	 * @return Graphs of matching devices.
	 * @throws Exception
	 */
	public Set<Graph> getDevices(String query) throws Exception {
		HashSet<Graph> devices = new HashSet<Graph>();
		if(query == null || query.trim().equals("")) return devices;
		RepositoryConnection conn = getConnection();
		try{
			String sparql = new QueryParser().translate(query);
			TupleQueryResult result = sparqlQuery(conn, sparql);
			while(result.hasNext()){
				Graph device = getResource(value("AOMDevice", result.next()));
				if(device != null && device.getStmts().size() > 0){
					devices.add(device);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return devices;
	}

	
	/**
	 * Retrieves the graphs of devices matching the query defined as graph. From graph, the 
	 * SPARQL query is generated and executed.
	 * @param query AOM query.
	 * @return Graphs of matching devices.
	 * @throws Exception
	 */
	public Set<Graph> getDevices(Graph query){
		HashSet<Graph> devices = new HashSet<Graph>();
		RepositoryConnection conn = getConnection();
		try{
			String sparql = query.toSPARQL();
			TupleQueryResult result = sparqlQuery(conn, sparql);
			while(result.hasNext()){
				Graph device = getResource(value("AOMDevice", result.next()));
				if(device != null && device.getStmts().size() > 0){
					devices.add(device);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return devices;
	}

	/**
	 * Checks if instance has rdf:type, which is annotated as StaticTaxonomy
	 * in Model ontology.
	 * @param Value of investigated instance.
	 * @return Flag reporting if instance is in static taxonomy.
	 */
	public boolean isInStaticModel(Value obj){
		if(obj == null) return false;
		RepositoryConnection conn = getConnection();
		try{
			RepositoryResult<Statement> result = conn.getStatements((Resource)obj, Rdf.rdfType, null, true);
			while(result.hasNext()){
				Statement s = result.next();
				if(s.getObject().stringValue().equals(Model.StaticTaxonomy.stringValue())){
					return true;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Evaluates SPARQL query.
	 * @param query SPARQL query.
	 * @return Query results as instances of SPARQLQuery instances.
	 */
	public Set<SPARQLResult> sparql(String query){
		Set<SPARQLResult> result = new HashSet<SPARQLResult>();
		RepositoryConnection conn = getConnection();
		try{
			TupleQueryResult results = sparqlQuery(conn, query);
			while(results.hasNext()){
				result.add(new SPARQLResult(results.next()));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return result;
	}


	/**
	 * Retrieves the map of matched services and their devices.
	 * @param serviceQuery Service AOM query.
	 * @param deviceQuery Device AOM query.
	 * @return Map of deviceURIs of matched devices and related serviceURIs of matched services.
	 * @throws Exception
	 */
	public Map<String, Set<String>> getDevicesWithServicesMap(String serviceQuery, String deviceQuery) throws Exception {
		Map<String, Set<String>> devicesServices = new HashMap<String, Set<String>>();
		String query = new QueryParser().translateService(serviceQuery, deviceQuery);
		Set<SPARQLResult> results = sparql(query);
		Iterator<SPARQLResult> i = results.iterator();
		while(i.hasNext()){
			SPARQLResult result = i.next();
			String deviceURI = result.value("AOMDevice");
			String serviceURI = result.value("AOMService");
			Set<String> services = null;
			if (devicesServices.get(deviceURI) == null){
				services = new HashSet<String>();
			}
			else{
				services = devicesServices.get(deviceURI);
			}
			services.add(serviceURI);
			devicesServices.put(deviceURI, services);
		}
		return devicesServices;
	}

	/**
	 * Returns the set of devices retrieved from map of deviceURIs and related serviceURIs.
	 * @param dsMap Map of matching devices and their matching services.
	 * @return Query results as the set of device graphs.
	 */
	public Set<Graph> getDevicesWithServices(Map<String, Set<String>> dsMap) {
		Set<Graph> devices = new HashSet<Graph>();
		Iterator<String> i = dsMap.keySet().iterator();
		while(i.hasNext()){
			String deviceURI = i.next();
			Graph device = getResource(deviceURI);

			Set<String> matched = dsMap.get(deviceURI);
			Set<Graph> services = device.subGraphs(Device.hasService);

			ValueFactory f = getValueFactory();
			for(Graph service : services){
				String serviceURI = service.getBaseURI();
				if(matched.contains(serviceURI)){
					ResourceUtil.addStatement(
							serviceURI, 
							Service.matchedService, 
							"true", 
							XMLSchema.BOOLEAN, 
							f, 
							device);
				}
			}

			devices.add(device);
		}
		return devices;
	}

	/**
	 * Executes service and device query and returns the query results as the set of 
	 * devices containing the matched services.
	 * @param serviceQuery Service query.
	 * @param deviceQuery Device query.
	 * @return Query results.
	 * @throws Exception
	 */
	public Set<Graph> getDevicesWithServices(String serviceQuery, String deviceQuery) throws Exception{
		if(serviceQuery == null || serviceQuery.trim().equals("")) return new HashSet<Graph>();
		return getDevicesWithServices(getDevicesWithServicesMap(serviceQuery, deviceQuery));
	}


	/**
	 * Updates the value of property of device specified by ontology path.
	 * @param deviceURI Ontology URI of device.
	 * @param path AOM specific ontology path of the property.
	 * @return Notification on operation success.
	 */
	public boolean updateValue(String deviceURI, String path) {
		Graph device = getResource(deviceURI);
		try{
			List<String> toUpdate = device.getUpdateStatementBase(path);
			if(toUpdate == null){
				return false;
			}
			else{
				String sURI = toUpdate.get(0);
				String pURI = toUpdate.get(1);
				String oURI = toUpdate.get(2);
				remove(sURI, new Property(pURI));
				add(sURI, pURI, oURI, false);
				return true;
			}
		}
		catch(Exception e){
			return false;
		}
	}

	/**
	 * Retrieves list of super classes for taxonomy class;
	 * @param Class URI.
	 * @return Collection of super classes;
	 */
	public Set<String> getSuperClassesOf(String classURI){
		Set<String> superClasses = new HashSet<String>();
		RepositoryConnection conn = getConnection();
		try{
			RepositoryResult<Statement> result = 
				conn.getStatements(new URIImpl(classURI), Rdfs.subClassOf, null, true);
			while(result.hasNext()){
				String sc = result.next().getObject().stringValue();
				if(!Namespace.isStaticType(new URIImpl(sc)) && !sc.equals(classURI)){
					superClasses.add(sc);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return superClasses;
	}

	/**
	 * Retrieves list of sub classes for taxonomy class;
	 * @param Class URI.
	 * @return Collection of super classes;
	 */
	public Set<String> getSubClassesOf(String classURI, boolean inferred){
		Set<String> subClasses = new HashSet<String>();
		RepositoryConnection conn = getConnection();
		try{
			RepositoryResult<Statement> result = 
				conn.getStatements(null, Rdfs.subClassOf, new URIImpl(classURI), inferred);
			while(result.hasNext()){
				String sc = result.next().getSubject().stringValue();
				if(!Namespace.isStaticType(new URIImpl(sc)) && !sc.equals(classURI)){
					subClasses.add(sc);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return subClasses;
	}

	public Set<String> getSubClassesOf(String classURI){
		return getSubClassesOf(classURI, true);
	}

	/**
	 * Retrieves list of instances for taxonomy class;
	 * @param classURI Class URI.
	 * @param inferred Include also inferred instances.
	 * @return Collection of instances;
	 */
	public Set<String> getInstancesOf(String classURI, boolean inferred){
		Set<String> instances = new HashSet<String>();
		RepositoryConnection conn = getConnection();
		try{
			RepositoryResult<Statement> result = 
				conn.getStatements(null, Rdf.rdfType, new URIImpl(classURI), inferred);
			while(result.hasNext()){
				instances.add(result.next().getSubject().stringValue());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return instances;
	}

	/**
	 * Retrieves graph of all values of direct instance properties;
	 * @param instanceURI Instance URI.
	 * @return Property Instance plain property graph;
	 */
	public Graph getInstanceProperties(String instanceURI){
		RepositoryConnection conn = getConnection();
		try{
			Graph instance = new Graph(instanceURI);
			RepositoryResult<Statement> result = 
				conn.getStatements(new URIImpl(instanceURI), null, null, false);
			while(result.hasNext()){
				instance.add(result.next());
			}
			return instance;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Retrieves information of all properties with domain set to specified class.
	 * @param classURI Class URI.
	 * @return Properties of the class;
	 */
	public Set<Graph> getClassProperties(String classURI){
		Set<Graph> props = new HashSet<Graph>();
		RepositoryConnection conn = getConnection();
		try{
			RepositoryResult<Statement> dResult = 
				conn.getStatements(null, Rdfs.domain, new URIImpl(classURI), false);
			while(dResult.hasNext()){
				Statement dst = dResult.next();

				RepositoryResult<Statement> rResult = 
					conn.getStatements(dst.getSubject(), Rdfs.range, null, false);

				if(rResult.hasNext()){
					Statement rst = rResult.next();
					Graph p = new Graph(dst.getSubject().stringValue());
					p.add(dst);
					p.add(rst);
					props.add(p);
				}
			}
			for(String superClassURI : getSuperClassesOf(classURI)){
				props.addAll(getClassProperties(superClassURI));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return props;
	}

	/**
	 * Serializes the whole repository to the RDF/XML.
	 * @return RDF/XML of whole repository.
	 */
	public String serialize(){
		RepositoryConnection conn = getConnection();
		try{
			StringWriter w = new StringWriter();

			RDFHandler handler = new RDFXMLPrettyWriter(w);
			conn.export(handler);

			return w.toString();
		}
		catch(Exception e){}
		finally{
			try{
				conn.close();
			}
			catch(Exception ex){}
		}
		return null;
	}

	/**
	 * Updates the repository with new RDF/XML information.
	 * @param xml RDF/XML to add into repository.
	 * @return Notification on operation success.
	 */
	public boolean update(String xml) {
		RepositoryConnection conn = getConnection();
		try {
			conn.add(new StringReader(xml), "", RDFFormat.RDFXML);
			return true;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (RDFParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * Clears the whole repository.
	 * @return Notification on operation success.
	 */
	public boolean clear() {
		RepositoryConnection conn = getConnection();
		try {
			conn.clear();
			return true;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Removes the statement from the repository.
	 * @param sURI Subject URI.
	 * @param pURI Property URI.
	 * @param oURI Object URI.
	 * @return Subject URI of removed statement.
	 */
	public String remove(String sURI, String pURI, String oURI) {
		RepositoryConnection conn = getConnection();
		try {
			Statement stmt = ResourceUtil.statement(
					sURI, 
					pURI, 
					oURI, 
					repository.getValueFactory());
			conn.remove(stmt);
			if(!Namespace.isStaticProperty(new URIImpl(pURI)) &&
					!isInStaticModel(new URIImpl(oURI))){
				Graph g = getResource(oURI);
				if(g != null){
					remove(g);
				}
			}
			return sURI;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Removes all run-time device instances including testing and semantic devices. 
	 * @return Notification on operation success.
	 */
	public boolean removeRunTimeDevices(){
		try{
			Set<Graph> devices = getDevices("device:isDeviceTemplate;\"false\"^^xsd:boolean");
			for(Graph d : devices){
				remove(d);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * Removes the statement with literal value from the repository.
	 * @param sURI Subject URI.
	 * @param pURI Property URI.
	 * @param value Literal value.
	 * @param dataType XSD datatype URI.
	 * @return Subject URI of removed statement.
	 */
	public String remove(String sURI, String pURI, String value, String dataType) {
		RepositoryConnection conn = getConnection();
		try {
			Statement stmt = ResourceUtil.statement(
					sURI, 
					pURI, 
					value, 
					Namespace.dataTypeURI(dataType), 
					repository.getValueFactory());
			conn.remove(stmt);
			return sURI;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Adds the statement to the repository.
	 * @param sURI Subject URI.
	 * @param pURI Property URI.
	 * @param value Literal value.
	 * @param append Should there be only one (false) or multiple (true) values?.
	 * @return Subject URI of removed statement.
	 */
	public String add(String sURI, String pURI, String oURI, boolean append) {
		RepositoryConnection conn = getConnection();
		try {
			Statement stmt = ResourceUtil.statement(
					sURI, 
					pURI, 
					oURI, 
					repository.getValueFactory());
			if (!append){
				conn.remove(new URIImpl(sURI), new URIImpl(pURI), null);
			}
			conn.add(stmt);
			return sURI;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Adds the statement with literal value to the repository.
	 * @param sURI Subject URI.
	 * @param pURI Property URI.
	 * @param value Literal value.
	 * @param dataType XSD datatype URI.
	 * @param append Should there be only one (false) or multiple (true) values?.
	 * @return Subject URI of removed statement.
	 */
	public String add(String sURI, String pURI, String value, String dataType, boolean append) {
		RepositoryConnection conn = getConnection();
		try {
			Statement stmt = ResourceUtil.statement(
					sURI, 
					pURI, 
					value, 
					Namespace.dataTypeURI(dataType), 
					repository.getValueFactory());
			if (!append){
				conn.remove(new URIImpl(sURI), new URIImpl(pURI), null);
			}
			conn.add(stmt);
			return sURI;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Graph getPropertyAnnotationModel(Resource property, String rdfType){
		ValueFactory f = getValueFactory();
		Graph g = new Graph(property.stringValue());
		ResourceUtil.addStatement(
				g.getBaseURI(), 
				Rdf.rdfType, 
				rdfType, 
				f, 
				g);
		RepositoryConnection conn = getConnection();
		try {
			RepositoryResult<Statement> domains = conn.getStatements(property, Rdfs.domain, null, true);
			while(domains.hasNext()){
				String domain = domains.next().getObject().stringValue();
				ResourceUtil.addStatement(
						g.getBaseURI(), 
						Rdfs.domain, 
						domain, 
						f, 
						g);
				for(String subDomain : getSubClassesOf(domain)){
					ResourceUtil.addStatement(
							g.getBaseURI(), 
							Rdfs.domain, 
							subDomain, 
							f, 
							g);
				}
			}
			RepositoryResult<Statement> range = conn.getStatements(property, Rdfs.range, null, false);
			while(range.hasNext()){
				ResourceUtil.addStatement(
						g.getBaseURI(), 
						Rdfs.range, 
						range.next().getObject().stringValue(), 
						f, 
						g);
			}
		} 
		catch (RepositoryException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				conn.close();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return g;
	}

	private Set<Graph> getPropertyAnnotationModels(String rdfType){
		Set<Graph> properties = new HashSet<Graph>();
		RepositoryConnection conn = getConnection();
		try {
			RepositoryResult<Statement> props = conn.getStatements(null, Rdf.rdfType, new URIImpl(rdfType), false);
			while(props.hasNext()){
				properties.add(getPropertyAnnotationModel(props.next().getSubject(), rdfType));
			}
		} 
		catch (RepositoryException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				conn.close();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		return properties;
	}

	/**
	 * Retieves the full models of annotation and form properties.
	 * @return Models of editable properties.
	 */
	public Set<Graph> getPropertyAnnotationModels(){
		Set<Graph> properties = new HashSet<Graph>();
		properties.addAll(getPropertyAnnotationModels(Namespace.property + "SingleValueFormFieldProperty"));
		properties.addAll(getPropertyAnnotationModels(Namespace.property + "MultiValueFormFieldProperty"));
		properties.addAll(getPropertyAnnotationModels(Namespace.property + "SingleValueFormProperty"));
		properties.addAll(getPropertyAnnotationModels(Namespace.property + "MultiValueFormProperty"));
		properties.addAll(getPropertyAnnotationModels(Namespace.property + "SingleValueAnnotationProperty"));
		properties.addAll(getPropertyAnnotationModels(Namespace.property + "MultiValueAnnotationProperty"));
		return properties;
	}


	/**
	 * Generates the unique name of instance of specific class.
	 * @param c Ontology class to create new instance.
	 * @return Unique URI for new instance.
	 */
	public String getURI(OntologyClass c){
		return getURI(c.stringValue());
	}

	/**
	 * Adds the unique ID to ontology URI.
	 * @param resource Ontology resource URI to make unique.
	 * @return Unique URI for resource.
	 */
	public String getURI(String resource){
		return (resource + "_" + UUID.randomUUID()).replaceAll("-", "_");
	}
}
