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
 * Copyright (C) 2006-2010
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
package eu.linksmart.limbo.frontend.ontology;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import eu.linksmart.limbo.repository.Repository;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * * <b>Class OntologyHandler</b>
 * This class is used for the handle ontology interaction for Limbo, using Jena apis, for exmaple, get property values for device informaton, 
 * gets the device direct type, device type state machine

 * Dec.23 add getAllSubClass, getAllDeviceInstance(), getDevicetype according to its partial name, getDeviceInstance(String partofname)
 * Dec 12 add get direct type
 * Nov. 26 get all properties
 * Nov. 22 Handling of loop (e.g. disjointwith) inside ontology
 * Aug. 17 added XML output
 * Aug. 9 added hash set to filter out repeated properties
 * 
 * 
 */
@Component(properties={
		"limbo.platform=.*"
})
public class OntologyHandler implements OntologyService {

	/**
	 * <b>linksmartOntologyURI</b> : ontology uri
	 */
	private static URI linksmartOntologyURI = null;    
	/**
	 * <b>OntModel</b> : Jena owl model
	 */
	private static OntModel om = null;
	/**
	 * <b>map</b> : used to store key value
	 */
	private Map<String, String> map = new HashMap<String, String>();
	/**
	 * <b>stringBuffer</b> : used to store properties
	 */
	private StringBuffer stringBuffer = new StringBuffer();

	/**
	 * <b>statements</b> : used to store traversed statements
	 */

	private HashSet<Statement> statements = new LinkedHashSet<Statement>();

	/**
	 * <b>ontoclass</b> : hash set store visited ontoclass
	 */
	private HashSet<OntClass> ontoclass=new HashSet<OntClass>();

	private ComponentContext context;
	private Repository repository;

	protected void activate(ComponentContext ctxt)  {
		this.context = ctxt;
	}

	protected void deactivate(ComponentContext ctxt) {	

	}

	@Reference
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void unsetRepository(Repository repository) {
		this.repository = null;
	}
	/**
	 *<b>OntologyHandler Constructor</b>
	 *	load the ontology model for the whole ontology
	 * @param ontologyURI - an ontology uri
	 * 					
	 */ 


	//FIXME: Instead of loading a model from file call the OntologyManager and retrieve the information about the device
	//
	public OntModel loadLinksmartOWL(URI ontologyURI) throws Exception{

		/* ApplicationOntologyManagerServiceLocator locator = new ApplicationOntologyManagerServiceLocator();
		 ApplicationOntologyManagerSoapBindingStub stub = (ApplicationOntologyManagerSoapBindingStub)locator.getApplicationOntologyManager(new URL("http://192.168.200.50:8080/axis/services/ApplicationOntologyManager"));
		 File wsdlFile = this.repository.getWSDLFile();

		        StringBuffer fileData = new StringBuffer(1000);
		        BufferedReader reader = new BufferedReader(
		                new FileReader(this.repository.getWSDLFile().getAbsolutePath()));
		        char[] buf = new char[1024];
		        int numRead=0;
		        while((numRead=reader.read(buf)) != -1){
		            fileData.append(buf, 0, numRead);
		        }
		        reader.close();
		       String content = fileData.toString();



		 String[] deviceInfo = stub.processLIMBOCall("phone_1", content);
		for(String s : deviceInfo)
			System.out.println("deviceInfo: "+s);
		 */
		try {
			linksmartOntologyURI = ontologyURI;

			Model base = FileManager.get().loadModel(linksmartOntologyURI.toString());
			om = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, base);
			return om;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 *<b>OntologyHandler Constructor</b>
	 *	The OntologyHandler Constructor that takes a ontology uri to create an instance
	 * @param ontologyURI - an ontology uri
	 * 					
	 */

	public OntologyHandler(URI ontologyURI) {
		try {
			loadLinksmartOWL(ontologyURI);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * <b>getDeviceType</b>
	 * This method gets the direct type of the individual uri
	 * 	  
	 * @param individualuri - individual uri
	 *  
	 * @return String - direct type for that individual
	 */	


	public String getDirectType(String individualuri) {
		Individual ind=om.getIndividual(individualuri);
		String r= ind.getRDFType(true).getLocalName();
		return r;		
	}
	/**
	 * <b>getDeviceType</b>
	 * This method get its device type in the ontology, from a partial wsdl name
	 * 	  
	 * @param partofname - partial WSDL name (e.g. th03)
	 *  
	 * @return String - return device type containing part of the device name
	 */	

	public String getDeviceType(String partofname) {
		//get all linksmart device instances
		HashSet<OntClass> instances=getAllSubClass("file:./resources/Device.owl#LinkSmartDevice");
		for (Iterator<OntClass> i = instances.iterator();i.hasNext();){
			OntClass c = (OntClass) i.next();
			for (ExtendedIterator j = c.listInstances(); j.hasNext(); ) { 
				Individual ind= (Individual)j.next();
				String name= ind.getLocalName();
				//  System.out.println("name"+name);
				if (name.toLowerCase().contains(partofname.toLowerCase())) 
					return c.getURI();

			}

		}

		return null;		
	}
	/**
	 * <b>getDeviceInstance</b>
	 * This method get the device instance from a partial device name
	 * 	  
	 * @param partofname - partial device name
	 *  
	 * @return String - return any instance contains part of the name
	 */

	public String getDeviceInstance(String partofname) {
		//get all linksmart device instances
		HashSet<OntClass> instances=getAllSubClass("file:./resources/Device.owl#LinkSmartDevice");
		for (Iterator<OntClass> i = instances.iterator();i.hasNext();){
			OntClass c = (OntClass) i.next();
			for (ExtendedIterator j = c.listInstances(); j.hasNext(); ) { 
				Individual ind= (Individual)j.next();
				String name= ind.getLocalName();
				//  System.out.println("name"+name);
				if (name.toLowerCase().contains(partofname.toLowerCase())) 
					return ind.getURI();

			}

		}

		return null;		
	}

	/**
	 * <b>getAllSubClass</b>
	 * This method get the get All SubClasses for a concept with a URI
	 * 
	 * @param uri - a concept with specific URI
	 *  
	 * @return HashSet<OntClass> - hash set for sub classes
	 */
	public HashSet<OntClass> getAllSubClass(String uri) {
		OntClass temp=om.getOntClass(uri);
		for (ExtendedIterator i = temp.listSubClasses(); i.hasNext(); ) {

			OntClass c = (OntClass) i.next();
			ontoclass.add(c);
			getAllSubClass(c.getURI());			  
		}
		return ontoclass;

	}
	/**
	 * <b>getDeviceStateMachine</b>
	 * This method get the device state machine given the device instance with a URI
	 * 
	 * @param deviceinstance - a device resource with specificURI
	 *  
	 * @return String - state machine name
	 */
	public HashSet<String> getAllDeviceInstance() {
		HashSet<OntClass> instances=getAllSubClass("file:./resources/Device.owl#LinkSmartDevice");
		HashSet<String>  stringinstance=new HashSet<String>();
		for (Iterator<OntClass> i = instances.iterator();i.hasNext();){
			OntClass c = (OntClass) i.next();
			for (ExtendedIterator j = c.listInstances(); j.hasNext(); ) { 
				Individual ind= (Individual)j.next();
				String name= ind.getLocalName();

				stringinstance.add(name);


			}

		}

		return 	stringinstance;
	}

	/**
	 * <b>getDeviceStateMachine</b>
	 * This method get the device state machine given the device instance with a URI
	 * 
	 * @param deviceinstance - a device resource with specificURI
	 *  
	 * @return String - state machine name
	 */
	public String getDeviceStateMachine(URI deviceinstance){
		String statemachine="";

		try {
			OntologyHandler oh = new OntologyHandler(new URI("file:./resources/Device.owl"));
			Map<String, String> map = oh.getAllOntologyProperties(deviceinstance);
			for (String key: map.keySet()) {
				//	System.out.println(" key:  "+key);
				if (key.endsWith("hasStateMachine")) {
					// if (map.get(key).startsWith(device.toString().substring(device.toString().indexOf("#")+1))) {
					statemachine=map.get(key);
					//	System.out.println(" sm:  "+statemachine);
					//} else {
					// Default configuration
					//}
				}
			}
		} catch (Exception e) {
			System.out.println("here in get device statamachine");
			e.printStackTrace();
		}
		System.out.println(" sm:  "+statemachine);
		return statemachine;
	}

	/**
	 * <b>getLinkSmartOnProperties</b>
	 * This method get all the properties, including URI ones for a resource with specificURI
	 * 
	 * @param specificURI - a resource resource with specificURI
	 *  
	 * @return Map<String, String> - A map holds the subject, object pair
	 */
	public Map<String, String> getLinkSmartOnProperties(URI specificURI) {

		Resource instanceResource = om.getResource(specificURI.toString());
		return getLiteralProperties(instanceResource);
		//return map;

	}

	/**
	 * <b>getAllProperties</b>
	 * This method get all the properties, including URI ones for a resource with specificURI
	 * 
	 * @param specificURI - a resource resource with specificURI
	 *  
	 * @return Map<String, String> - A map holds the subject, object pair
	 */
	public Map<String, String> getAllOntologyProperties(URI specificURI) {

		Resource instanceResource = om.getResource(specificURI.toString());
		return getAllProperties(instanceResource);
		//return map;

	}

	/**
	 * <b>getStatements</b>
	 * This method recursively get the statements and put into a set in order to filter out visited nodes
	 note that RDF node can be Literal, URI named, or blank node which could
	 lead to the deadlock of this method
	 if not using the isURIResource() method
	 * @param resource - a resource on an ontology
	 *  
	 * @return HashSet<Statement> - A hashset holds the statement
	 */


	private HashSet<Statement> getStatements(Resource resource) {


		for (StmtIterator si = resource.listProperties(); si.hasNext();) {
			Statement statement = (Statement) si.nextStatement();
			//System.out.println("statement  "+statement);
			if (! statements.contains(statement)) {
				statements.add(statement);
				RDFNode object = statement.getObject(); // get the object

				if ((object instanceof Literal)) {

					//	String objValue = ((Literal) object).getValue().toString();
					//	map.put(propertyItem, objValue);
				}

				// if it is another object property
				else {
					if (object.isURIResource()) {
						// System.out.println("URI "+statement);
						Resource r = statement.getResource();
						getStatements(r);
					}
				}
			}
		}
		return statements;
	}

	/**
	 * <b>getAllProperties</b>
	 * This method get all the properties, including URI ones for a resource
	 * 
	 * @param resource - a resource on an ontology
	 *  
	 * @return Map<String, String> - A map holds the subject, object pair
	 */

	private Map<String, String> getAllProperties(Resource resource) {
		Statement temp = null;
		HashSet<Statement> statementSet=getStatements(resource);
		Iterator<Statement> it = statementSet.iterator();
		while (it.hasNext()) {

			temp = (Statement) it.next();
			Resource subject = temp.getSubject(); // get the subject
			Property predicate = temp.getPredicate(); // get the predicate
			String localnamePredicate = predicate.getLocalName();
			String propertyItem = subject.getLocalName() + "_"
			+ localnamePredicate;

			RDFNode object = temp.getObject(); // get the object

			if ((object instanceof Literal)) {

				String objValue = ((Literal) object).getValue().toString();
				map.put(propertyItem, objValue);
			}
			else {
				if (object.isURIResource()) {
					String objValue = object.toString().substring(object.toString().indexOf("#")+1);
					map.put(propertyItem, objValue);
				}
			}

		}
		return map;

	}

	/**
	 * <b>getLiteralProperties</b>
	 * This method get the literal properties for a resource
	 * 
	 * @param resource - a resource on an ontology
	 *  
	 * @return Map<String, String> - A map holds the subject, object pair
	 */
	private Map<String, String> getLiteralProperties(Resource resource) {
		Statement temp = null;
		HashSet<Statement> statementSet=getStatements(resource);
		Iterator<Statement> it = statementSet.iterator();
		while (it.hasNext()) {

			temp = (Statement) it.next();
			Resource subject = temp.getSubject(); // get the subject
			Property predicate = temp.getPredicate(); // get the predicate
			String localnamePredicate = predicate.getLocalName();
			String propertyItem = subject.getLocalName() + "_"
			+ localnamePredicate;

			RDFNode object = temp.getObject(); // get the object

			if ((object instanceof Literal)) {

				String objValue = ((Literal) object).getValue().toString();
				map.put(propertyItem, objValue);
			}

		}
		return map;

	}

	/**
	 * <b>getPropertiesToXMLBuffer</b>
	 * This method returns the properties associated with an URI resource (for a device) 
	 * write as string
	 * @param specificURI - resource uri in the ontology
	 *  

	 */
	private void getPropertiesToBuffer(Resource resource) {
		Statement temp = null;
		HashSet<Statement> statementSet=getStatements(resource);
		Iterator<Statement> it = statementSet.iterator();

		while (it.hasNext()) {

			temp = (Statement) it.next();
			Resource subject = temp.getSubject(); // get the subject
			Property predicate = temp.getPredicate(); // get the predicate
			String localnamePredicate = predicate.getLocalName();
			String propertyItem = subject.getLocalName() + "_"
			+ localnamePredicate;

			RDFNode object = temp.getObject(); // get the object

			if ((object instanceof Literal)) {

				String objValue = ((Literal) object).getValue().toString();
				stringBuffer.append(propertyItem + "   " + objValue + "\n");
			}

		}
	}

	/**
	 * <b>getPropertiesToXMLBuffer</b>
	 * This method returns the properties associated with an URI resource (for a device) 
	 * write XML format
	 * @param specificURI - resource uri in the ontology
	 *  

	 */

	private void getPropertiesToXMLBuffer(Resource resource) {
		Statement temp = null;
		HashSet<Statement> statementSet=getStatements(resource);
		String header="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		stringBuffer.append(header);
		stringBuffer.append("<device>\n");
		Iterator<Statement> it = statementSet.iterator();

		while (it.hasNext()) {

			temp = it.next();
			Resource subject = temp.getSubject(); // get the subject
			Property predicate = temp.getPredicate(); // get the predicate
			String localnamePredicate = predicate.getLocalName();
			String propertyItem = subject.getLocalName() + "_"
			+ localnamePredicate;

			RDFNode object = temp.getObject(); // get the object

			if ((object instanceof Literal)) {

				String objValue = ((Literal) object).getValue().toString();
				stringBuffer.append("<"+propertyItem+">\n");
				stringBuffer.append("   " + objValue + "\n");
				stringBuffer.append("</"+propertyItem+">\n");

			}

		}
		stringBuffer.append("</device>\n");

	}
	/**
	 * <b>getPropertiesToString</b>
	 * This method returns the properties associated with an URI resource (for a device) 
	 * write to a txt file sperated by line, and three space characters for each item in one line
	 * @param specificURI - resource uri in the ontology
	 *  
	 * @return String - string for properties in txt
	 */
	public String getPropertiesToString(URI specificURI) {

		Resource instanceResource = om.getResource(specificURI.toString());
		getPropertiesToBuffer(instanceResource);
		// write to a txt file sperated by line, and three space characters for
		// each item in one line

		return stringBuffer.toString();

	}

	/**
	 * <b>getPropertiesToString</b>
	 * This method returns the properties associated with an URI resource (for a device) 
	 * write to a XML file sperated by line, and three space characters for each item in one line
	 * @param specificURI - resource uri in the ontology
	 *  
	 * @return String - string for properties in xml
	 */
	public String getPropertiesToXML(URI specificURI) {

		Resource instanceResource = om.getResource(specificURI.toString());
		getPropertiesToXMLBuffer(instanceResource);
		// write to a txt file sperated by line, and three space characters for
		// each item in one line

		return stringBuffer.toString();

	}

	/**
	 * <b>main</b>
	 * for testing as a standalone app
	 */
	public static void main(String[] args) {
		// read the argument file, or the default
		// StringBuffer stringBuffer=new StringBuffer();
		URI uri1 = null, uri2 = null;
		// Create a URI
		try {
			uri1 = new URI("file:./resources/Device.owl");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		OntologyHandler specificInfo = new OntologyHandler(uri1);
		String r=specificInfo.getDirectType("file:./resources/Device.owl#SEP910");
		System.out.println("r:  "+r);
		r=specificInfo.getDeviceType("th03");
		System.out.println("type     "+r);
		r=specificInfo.getDirectType("file:./resources/Device.owl#PicoTh03_Indoor");
		System.out.println("r:  "+r);
		try {
			uri2 = new URI(
			"file:./resources/Device.owl#PicoTh03_Indoor");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		Map<String, String> map = specificInfo.getLinkSmartOnProperties(uri2);
		for (Iterator<Map.Entry<String, String>> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String,String> entry = (Map.Entry<String,String>) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			System.out.println((String) key + " " + (String) value);
		}
	}
}
