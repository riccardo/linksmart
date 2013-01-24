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
import java.util.Map;
import java.util.Set;

import org.jdom.Element;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.model.Rdfs;
import eu.linksmart.aom.ontology.schema.Property;
import eu.linksmart.aom.repository.AOMRepository;

/**
 * Class responsible for generating the various parts of ontology model for IDE support.
 * 
 * @author Peter Kostelnik
 *
 */
public class ModelGenerator extends Generator {
	AOMRepository repository;

	public ModelGenerator(AOMRepository repository) {
		this.repository = repository;
	}

	private Element addURIs(Graph property, String name, Property p, Element propertyElm){
		Element elm = addElement(name, propertyElm);
		for(String uri : property.values(p)){
			addElement("uri", uri, elm);
		}
		return elm;
	}
	
	/**
	 * Generates XML of editable property and adds it to the property model XML.
	 * @param property Graph of property ontology model.
	 * @param model XML element of property models.
	 * @return XML of added property model.
	 */
	public Element processProperty(Graph property, Element model){
		Element propertyElm = addElement("property", model);

		Element info = addElement("info", propertyElm);
		addElement("type", new URIImpl(property.value(Rdf.rdfType)).getLocalName(), info);
		addElement("uri", property.getBaseURI(), info);
		URI propertyURI = new URIImpl(property.getBaseURI());
		addElement("prefix", Namespace.prefix(propertyURI.getNamespace()), info);
		addElement("localName", propertyURI.getLocalName(), info);

		addURIs(property, "domain", Rdfs.domain, propertyElm);
		addURIs(property, "range", Rdfs.range, propertyElm);

		return propertyElm;
	}

	/**
	 * Generates the XML containing information of all annotation or form properties in ontology.
	 * Used to support the IDE. This information is used to drive the ide behaviour, when
	 * extending instance information using the editable properties.
	 * @param properties Property ontology models.
	 * @return XML containing the information of annotation or form properties.
	 */
	public Element getPropertyAnnotationModel(Set<Graph> properties){
		Element model = new Element("model");
		for(Graph property : properties){
			processProperty(property, model);
		}
		return model;
	}

	private Element deviceTypes(String classURI){
		Element node = null;
		if(repository.classExists(classURI)){
			URI uri = new URIImpl(classURI);
			node = getTypeNode(uri.getLocalName());
			for(String subClass : repository.getSubClassesOf(classURI, false)){
				addElement(deviceTypes(subClass), node);
			}
		}
		return node;
	}

	private Element results(Set<Graph> devices){
		Element node = new Element("results");
		if(devices.size() == 0) return node;

		for(Graph device : devices){
			String instanceURI = device.getBaseURI();
			URI uri = new URIImpl(instanceURI);
			addElement(getInstanceNode(
					instanceURI, 
					Namespace.prefix(uri.getNamespace()), 
					uri.getLocalName(),
					repository.isInStaticModel(uri)), 
					node);
		}
		return node;
	}
	private Element classLiterals(String classURI){
		Element node = new Element("classLiterals");
		for(Graph p : repository.getClassProperties(classURI)){
			URI range = new URIImpl(p.value(Rdfs.range));
			if(range.getNamespace().equals(Namespace.xsd)){
				URI propertyURI = new URIImpl(p.getBaseURI());
				Element info = addElement("propertyInfo", node);
				addBaseInfo(
						propertyURI.stringValue(), 
						Namespace.prefix(propertyURI.getNamespace()), 
						propertyURI.getLocalName(), 
						info);
				addElement("datatype", range.getLocalName(), info);
			}
		}
		return node;
	}

	private Element addPropertyInfo(URI predicateURI, Element node){
		Element propertyInfo = addElement("popertyInfo", node);
		addBaseInfo(
				predicateURI.stringValue(), 
				Namespace.prefix(predicateURI.getNamespace()), 
				predicateURI.getLocalName(), 
				propertyInfo);
		return propertyInfo;
	}

	private Element addObjectProperty(URI object, URI predicateURI, Element node){
		Element property = addElement("objectProperty", node);
		Element info = addElement("info", property);
		addBaseInfo(
				object.stringValue(), 
				Namespace.prefix(object.getNamespace()), 
				object.getLocalName(), 
				info);
		addElement("static", repository.isInStaticModel(object)+"", info);

		addPropertyInfo(predicateURI, property);

		return property;
	}
	private Element addDatatypeProperty(Literal object, URI predicateURI, Element node){
		Element property = addElement("datatypeProperty", node);
		Element info = addElement("info", property);

		addElement("value", object.stringValue(), info);
		addElement("datatype", object.getDatatype().getLocalName(), info);

		addPropertyInfo(predicateURI, property);

		return property;
	}
	private Element addProperty(Statement st, Element node){
		URI predicate = st.getPredicate();
		Value object = st.getObject();
		if(object instanceof Literal){
			return addDatatypeProperty((Literal)object, predicate, node);
		}
		else{
			return addObjectProperty((URI)object, predicate, node);
		}
	}

	private Element tree(String instanceURI){
		Element node = null;
		if(repository.instanceExists(instanceURI)){
			Graph instance = repository.getInstanceProperties(instanceURI);

			String rdfType = instance.value(Rdf.rdfType);
			URI uri = new URIImpl(instanceURI);
			Set<String> superClasses = repository.getSuperClassesOf(rdfType);
			if(rdfType != null && rdfType.equals(Device.Device.stringValue()) ||
					superClasses.contains(Device.Device.stringValue())){
				node = getInstanceNode(
						instanceURI, 
						Namespace.prefix(uri.getNamespace()), 
						uri.getLocalName(),
						repository.isInStaticModel(uri),
						instance.value(Device.isDeviceTemplate),
						instance.value(Device.isTesting));
			}
			else{
				node = getInstanceNode(
						instanceURI, 
						Namespace.prefix(uri.getNamespace()), 
						uri.getLocalName(),
						repository.isInStaticModel(uri));
			}

			for(Statement statement : instance.getStmts()){
				addProperty(statement, node);
			}
		}
		return node;
	}

	private void addInstances(String classURI, Element node){
		for(String instanceURI : repository.getInstancesOf(classURI, false)){
			URI uri = new URIImpl(instanceURI);
			Graph instance = repository.getInstanceProperties(instanceURI);
			addElement(
					getInstanceNode(
							instanceURI, 
							Namespace.prefix(uri.getNamespace()), 
							uri.getLocalName(),
							repository.isInStaticModel(uri),
							instance.value(Device.isDeviceTemplate),
							instance.value(Device.isTesting)), 
							node);
		}
	}

	/**
	 * Recursively generates the information of ontology class including subclasses and instances. Used 
	 * for ontology browsing in IDE. 
	 * @param classURI Ontology URI.
	 * @param instances Generate also instances of the classes or not?.
	 * @return XML containing full hierarchic class description.
	 */
	public Element tree(String classURI, boolean instances){
		Element node = null;
		if(repository.classExists(classURI)){
			URI uri = new URIImpl(classURI);
			node = getClassNode(classURI, Namespace.prefix(uri.getNamespace()), uri.getLocalName());
			for(String subClass : repository.getSubClassesOf(classURI, false)){
				addElement(tree(subClass, instances), node);
			}
			if(instances){
				addInstances(classURI, node);
			}
		}
		return node;
	}

	/**
	 * Recursively generates the information of basic device ontology class including subclasses and instances. Used 
	 * for ontology browsing in IDE. 
	 * @return XML containing full hierarchic device taxonomy description.
	 */
	public String getDeviceTree(){
		Element tree = tree(Device.Device.stringValue(), true);
		if(tree != null){
			return toString(tree);
		}
		return null;
	}

	/**
	 * Recursively generates the information of ontology class including subclasses and instances. Used 
	 * for ontology browsing in IDE. 
	 * @param classURI Ontology URI.
	 * @param includeInstances Generate also instances of the classes or not?.
	 * @return XML containing full hierarchic class description.
	 */
	public String getTree(String classURI, boolean includeInstances){
		Element tree = tree(classURI, includeInstances);
		if(tree != null){
			return toString(tree);
		}
		return null;
	}

	/**
	 * Recursively generates the full information of ontology instance. Used 
	 * for instance browsing in IDE. 
	 * @param instanceURI Ontology URI.
	 * @return XML containing full hierarchic instance description.
	 */
	public String getInstanceTree(String instanceURI){
		Element instance = tree(instanceURI);
		if(instance != null){
			return toString(instance);
		}
		return null;
	}

	/**
	 * Generates XML containing the class literal of specified class. Used to generate 
	 * the form when creating new instance in IDE.
	 * @param classURI Ontology URI.
	 * @return XML containing class literals.
	 */
	public String getClassLiterals(String classURI){
		Element model = classLiterals(classURI);
		if(model != null){
			return toString(model);
		}
		return null;
	}

	/**
	 * Generates XML of query results to support the query evaluator in IDE.
	 * @param devices Query results.
	 * @return XML of serialized query results.
	 */
	public String getResults(Set<Graph> devices){
		Element out = results(devices);
		if(out != null){
			return toString(out);
		}
		return null;
	}

	/**
	 * Generates actual taxonomy of devices. 
	 * @return XML containing the taxonomy of device types taking care about the device hierarchy.
	 */
	public String getDeviceTypes(){
		Element types = deviceTypes(Device.Device.stringValue());
		Element root = new Element("DeviceTypes");
		if(types != null){
			addElement(types, root);
		}
		return toString(root);
	}


	private void addBaseInfo(String uri, String prefix, String localName, Element info){
		addElement("uri", uri, info);
		addElement("prefix", prefix, info);
		addElement("localName", localName, info);
	}

	private Element getTypeNode(String localName) {
		Map<String, String> attrs = new HashMap<String, String>();
		attrs.put("name", localName);
		Element node = element("DeviceType", attrs);
		return node;
	}

	/**
	 * Returns XML information of ontology class.
	 * @param uri Ontology URI.
	 * @param prefix Namespace prefix.
	 * @param localName Local name.
	 * @return Class XML information.
	 */
	public Element getClassNode(
			String uri, 
			String prefix, 
			String localName) {
		Element node = new Element("classNode");
		Element info = addElement("info", node);

		addBaseInfo(uri, prefix, localName, info);

		return node;
	}

	/**
	 * Returns XML information of ontology instance.
	 * @param uri Ontology URI.
	 * @param prefix Namespace prefix.
	 * @param localName Local name.
	 * @param isStatic Flag if instance is in static taxonomy.
	 * @return Instance XML information.
	 * @param isTemplate Flag if instance is device template.
	 * @param isTesting Flag if instance was created as testing in IDE.
	 * @return Instance XML information.
	 */
	public Element getInstanceNode(
			String uri, 
			String prefix, 
			String localName,
			boolean isStatic,
			String isTemplate,
			String isTesting) {

		Element node = getInstanceNode(uri, prefix, localName, isStatic);
		Element info = node.getChild("info");

		String type = "testing";
		if(isTemplate != null){
			if(isTemplate.equals("false")){
				type = "runtime";
				if(isTesting != null && isTesting.equals("true")){
					type = "testing";
				}
			}
			else{
				type = "template";
			}
		}
		addElement("type", type, info);
		return node;
	}

	/**
	 * Returns XML information of ontology instance.
	 * @param uri Ontology URI.
	 * @param prefix Namespace prefix.
	 * @param localName Local name.
	 * @param isStatic Flag if instance is in static taxonomy.
	 * @return Instance XML information.
	 */
	public Element getInstanceNode(
			String uri, 
			String prefix, 
			String localName,
			boolean isStatic) {
		Element node = new Element("instanceNode");
		Element info = addElement("info", node);

		addBaseInfo(uri, prefix, localName, info);
		addElement("static", isStatic+"", info);
		return node;
	}
}
