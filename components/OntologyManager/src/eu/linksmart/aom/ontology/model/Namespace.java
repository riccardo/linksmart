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

package eu.linksmart.aom.ontology.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Class containing the definition of ontologies namespaces and URIs. 
 * Class is also responsible for definition and distinguishing of instances from 
 * static properties and static taxonomies.
 * 
 * @author Peter Kostelnik
 *
 */
public class Namespace {
	private static Map<String, String> prefixes = new HashMap<String, String>();
	private static Set<String> staticProperty = new HashSet<String>();
	private static Set<String> staticType = new HashSet<String>();
	private static Set<String> staticPropertyException = new HashSet<String>();

	public final static String ontologyURI = "http://localhost/ontologies/";

	public final static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public final static String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public final static String xsd = "http://www.w3.org/2001/XMLSchema#";
	public final static String device = ontologyURI + "Device.owl#";
	public final static String service = ontologyURI + "Service.owl#";
	public final static String discovery = ontologyURI + "Discovery.owl#";
	public final static String event = ontologyURI + "Event.owl#";
	public final static String energy = ontologyURI + "Energy.owl#";
	public final static String configuration = ontologyURI + "Configuration.owl#";
	public final static String hardware = ontologyURI + "Hardware.owl#";
	public final static String unit = ontologyURI + "Unit.owl#";
	public final static String security = ontologyURI + "Security.owl#";
	public final static String model = ontologyURI + "StaticTaxonomyModel.owl#";
	public final static String application = ontologyURI + "Application.owl#";
	public final static String rule = ontologyURI + "PIDRuleModel.owl#";
	public final static String property = ontologyURI + "StaticPropertyModel.owl#";

	public final static String unknownURI = ontologyURI + "Unknown#";

	static {
		prefixes.put(rdf, "rdf");
		prefixes.put(rdfs, "rdfs");
		prefixes.put(xsd, "xsd");
		prefixes.put(device, "device");
		prefixes.put(service, "service");
		prefixes.put(discovery, "discovery");
		prefixes.put(event, "event");
		prefixes.put(energy, "energy");
		prefixes.put(configuration, "configuration");
		prefixes.put(unit, "unit");
		prefixes.put(security, "security");
		prefixes.put(hardware, "hardware");
		prefixes.put(model, "model");
		prefixes.put(application, "application");
		prefixes.put(rule, "rule");
		prefixes.put(property, "property");
	}
	static {
		staticProperty.add(rdf);
		staticProperty.add(rdfs);
		staticProperty.add(property);
		staticProperty.add(application);
		staticPropertyException.add(Device.clonedFromTemplate.stringValue());
		staticPropertyException.add(Device.isSatisfiedBy.stringValue());
	}
	static {
		staticType.add(rdf);
		staticType.add(rdfs);
	}

	/**
	 * Returns the prefix of specified ontology URI.
	 * @param uri Ontology URI.
	 * @return Namespace refix for ontology URI or null.
	 */
	public static String prefix(String uri){
		String prefix = prefixes.get(uri);
		if(prefix == null) return null;
		return prefix;
	}

	/**
	 * Returns the namespace prefix of specified instance URI including ontology URI and resource name.
	 * @param uri Ontology URI.
	 * @return Nemaspace refix for ontology URI or null.
	 */
	public static String prefixForURI(String uri){
		URI u = new URIImpl(uri);
		String ln = u.getLocalName();
		return prefix(uri.substring(0, uri.length() - ln.length()));
	}

	/**
	 * Returns the URI for namespace prefix.
	 * @param prefix Namespace prefix.
	 * @return Ontology URI or null.
	 */
	public static String uri(String prefix){
		Iterator<String> i = prefixes.keySet().iterator();
		while(i.hasNext()){
			String uri = i.next();
			if(prefixes.get(uri).equals(prefix)) return uri; 
		}
		return unknownURI;
	}


	/**
	 * Check, if the property is static. Static properties are used in graph operations, for example
	 * not to remove or clone static values, such as ontology classes or static instances 
	 * (e.g. units or security mechanisms). Static instances must not be multiplied in the repository and
	 * must not be removed.
	 * @param p Ontology property.
	 * @return Notification if property is static.
	 */
	public static boolean isStaticProperty(URI p){
		if(staticProperty.contains(p.getNamespace())) return true;
		if(staticPropertyException.contains(p.stringValue())) return true;
		return false;
	}

	/**
	 * Identifies, if URI is static type. Static types are currently ontology classes from
	 * RDF and RDSF ontologies. Purpose of static type identification is to stop recursive
	 * searches on the top of taxonomies defined in ontologies.
	 * @param t Type URI.
	 * @return Notification if URI is static type.
	 */
	public static boolean isStaticType(URI t){
		if(staticType.contains(t.getNamespace())) return true;
		return false;
	}

	/**
	 * Returns the full XSD URI for datatype name (for example, for "string" input, 
	 * the URI "http://www.w3.org/2001/XMLSchema#string" is returned).
	 * @param dataType Datatype name.
	 * @return 
	 */
	public static String dataTypeURI(String dataType){
		String dt = "";
		if(dataType.equals("integer")) dt = "integer";
		else if(dataType.equals("float")) dt = "float";
		else if(dataType.equals("double")) dt = "double";
		else if(dataType.equals("boolean")) dt = "boolean";
		else dt = "string";
		return xsd + dt;
	}
}
