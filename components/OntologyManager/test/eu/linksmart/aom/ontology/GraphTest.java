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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.model.Service;
import eu.linksmart.aom.ontology.schema.ResourceProperty;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.testutil.GraphComparator;
import eu.linksmart.aom.testutil.RepositoryUtil;

public class GraphTest {
	@Test
	public void testValues(){
		Graph g = GraphLoader.load(GraphData.deviceURI, GraphData.device);
		HashSet<String> expected1 = new HashSet<String>();
		expected1.add(GraphData.service1URI);
		expected1.add(GraphData.service2URI);
		assertEquals(
				g.values(Device.hasService),
				expected1);


		HashSet<String> expected2 = new HashSet<String>();
		expected2.add(Device.PhysicalDevice.stringValue());
		assertEquals(
				g.values(Rdf.rdfType),
				expected2);

		assertEquals(
				g.values(Service.serviceOperation),
				new HashSet<String>());

		Graph sg = GraphLoader.load(GraphData.service1URI, GraphData.service1);
		HashSet<String> expected3 = new HashSet<String>();
		expected3.add("some operation 1");
		assertEquals(
				sg.values(Service.serviceOperation),
				expected3);
	}

	@Test
	public void testSubGraphs(){
		Graph g = GraphLoader.load(GraphData.deviceURI, GraphData.device);
		HashSet<Graph> expected = new HashSet<Graph>();
		expected.add(GraphLoader.load(GraphData.service1URI, GraphData.service1));
		expected.add(GraphLoader.load(GraphData.service2URI, GraphData.service2));
		assertEquals(
				g.subGraphs(Device.hasService),
				expected);

	}

	@Test
	public void testEquals(){
		String n3_1 = GraphData.prefixes + "device:One device:prop service:Two.";
		String n3_2 = GraphData.prefixes + "device:Two device:prop service:Three.";

		String uri_1 = "http://my.uri/1";
		String uri_2 = "http://my.uri/2";

		Graph g1 = GraphLoader.load(uri_1, n3_1);
		Graph g2 = GraphLoader.load(uri_2, n3_1);
		Graph g3 = GraphLoader.load(uri_1, n3_2);

		assertTrue(g1.equals(GraphLoader.load(uri_1, n3_1)));
		assertFalse(g1.equals(g2));
		assertFalse(g1.equals(g3));
	}

	@Test
	public void testToSPARQL(){
		String dURI = Namespace.device + "Device_1";
		String cURI = Namespace.configuration + "Configuration_1";
		String fURI1 = Namespace.configuration + "ConfigurationFile_1";
		String fURI2 = Namespace.configuration + "ConfigurationFile_2";
		String name = "some name";
		String impl = "some impl";
		String down = "some down";
		String store = "some store";
		Graph d = GraphLoader.load(dURI, GraphData.device(dURI));
		Graph c = GraphLoader.load(dURI, GraphData.configContent(dURI, cURI, name, impl));
		c.add(GraphLoader.load(dURI, GraphData.configFileContent(cURI, fURI1, down+" 1", store+" 1")));
		c.add(GraphLoader.load(dURI, GraphData.configFileContent(cURI, fURI2, down+" 2", store+" 2")));
		d.add(c);

		String sparql = d.toSPARQL();
		String expected = "" +
		"SELECT DISTINCT ?AOMDevice WHERE { \n" +
		"  ?AOMDevice rdf:type device:Device. \n" +
		"  ?AOMDevice device:hasConfiguration ?Configuration_1. \n" +
		"  ?Configuration_1 rdf:type configuration:Configuration. \n" +
		"  ?Configuration_1 configuration:implementationClass \"some impl\"^^xsd:string. \n" +
		"  ?Configuration_1 configuration:name \"some name\"^^xsd:string. \n" +
		"  ?Configuration_1 configuration:configurationFile ?ConfigurationFile_1. \n" +
		"  ?ConfigurationFile_1 rdf:type configuration:ConfigurationFile. \n" +
		"  ?ConfigurationFile_1 configuration:downloadURL \"some down 1\"^^xsd:string. \n" +
		"  ?ConfigurationFile_1 configuration:storageFolder \"some store 1\"^^xsd:string. \n" +
		"  ?Configuration_1 configuration:configurationFile ?ConfigurationFile_2. \n" +
		"  ?ConfigurationFile_2 rdf:type configuration:ConfigurationFile. \n" +
		"  ?ConfigurationFile_2 configuration:downloadURL \"some down 2\"^^xsd:string. \n" +
		"  ?ConfigurationFile_2 configuration:storageFolder \"some store 2\"^^xsd:string. \n" +
		"}";
		String[] sparqlLines = sparql.split("\n");
		String[] expectedLines = expected.split("\n");
		assertTrue(expectedLines.length == sparqlLines.length);
		for(int i = 0; i < expectedLines.length; i++){
			String expLine = expectedLines[i];
			boolean exists = false;
			for(int j = 0; j < sparqlLines.length; j++){
				if(sparqlLines[j].equals(expLine)){
					exists = true;
					break;
				}
			}
			assertTrue(exists);
		}
	}

	@Test
	public void testClone(){
		String location = "clone";
		AOMRepository repo = RepositoryFactory.local(location);

		String deviceURI = "http://my.device.uri";
		String configURI = "http://my.config.uri";
		String myUnit = Namespace.unit + "TestingUnit";
		Graph d = GraphLoader.load(deviceURI, GraphData.device(deviceURI));
		ResourceUtil.addStatement(
				deviceURI, 
				new ResourceProperty(Namespace.device + "testProperty"), 
				myUnit, 
				repo.getValueFactory(), 
				d);

		Graph c = GraphLoader.load(configURI, GraphData.config(deviceURI, configURI));
		Graph f1 = GraphLoader.load(configURI, GraphData.configFile(configURI, "http://my.file.uri/1"));
		Graph f2 = GraphLoader.load(configURI, GraphData.configFile(configURI, "http://my.file.uri/2"));
		c.add(f1);
		c.add(f2);
		d.add(c);

		Graph m = GraphLoader.load("", GraphData.model());
		Graph u = GraphLoader.load("", GraphData.unit(myUnit));
		repo.store(m);
		repo.store(u);

		Graph clone = d.clone(repo);
		assertTrue(GraphComparator.sameAs(d, clone, repo));

		repo.close();
		RepositoryUtil.clean(location);

	}
	@Test
	public void testCloneFromTemplate(){
		String location = "cloneFromTemplate";
		AOMRepository repo = RepositoryFactory.local(location);

		String deviceURI = "http://my.device.uri";

		Graph d = GraphLoader.load(deviceURI, GraphData.device(deviceURI));

		String templateURI = "http://cloned-from-template.uri";
		Graph clone = d.clone(repo, templateURI);
		ValueFactory f = repo.getValueFactory();
		ResourceUtil.addStatement(
				d.getBaseURI(), 
				Device.clonedFromTemplate, 
				templateURI, 
				f, 
				d);
		ResourceUtil.removeStatement(
				d.getBaseURI(), 
				Device.isDeviceTemplate, 
				"true", 
				XMLSchema.BOOLEAN, 
				f, 
				d);
		ResourceUtil.addStatement(
				d.getBaseURI(), 
				Device.isDeviceTemplate, 
				"false", 
				XMLSchema.BOOLEAN, 
				f, 
				d);

		assertTrue(GraphComparator.sameAs(d, clone, repo));

		repo.close();
		RepositoryUtil.clean(location);
	}
	@Test 
	public void testGetRequirements(){

		String deviceURI1 = "http://device.uri/1";

		String serviceURI11 = "http://service.uri/11";
		String serviceURI12 = "http://service.uri/12";
		String serviceURI13 = "http://service.uri/13";

		Graph device1 = GraphLoader.load(
				deviceURI1, 
				GraphData.physicalDevice(deviceURI1, false, deviceURI1+"/template"));
		device1.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI1, serviceURI11, Namespace.unit + "Celsius")));
		device1.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI1, serviceURI12, Namespace.unit + "OtherUnit")));
		device1.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI1, serviceURI13, Namespace.unit + "Celsius")));
		device1.add(GraphLoader.load(
				"",
				GraphData.hardware(deviceURI1, "screen width", "point", "resolution")));

		ValueFactory f = new ValueFactoryImpl();
		ResourceUtil.addStatement(
				serviceURI11, 
				Service.serviceCost, 
				"cost 11 a", 
				XMLSchema.STRING,
				f, 
				device1);
		ResourceUtil.addStatement(
				serviceURI11, 
				Service.serviceCost, 
				"cost 11 b", 
				XMLSchema.STRING,
				f, 
				device1);
		ResourceUtil.addStatement(
				serviceURI13, 
				Service.serviceCost, 
				"cost 13 a", 
				XMLSchema.STRING,
				f, 
				device1);

		ResourceUtil.addStatement(
				deviceURI1, 
				Device.PID, 
				"pid 1", 
				XMLSchema.STRING,
				f, 
				device1);
		ResourceUtil.addStatement(
				deviceURI1, 
				Device.PID, 
				"pid 2", 
				XMLSchema.STRING,
				f, 
				device1);

		Map<String, Set<String>> rMap = new HashMap<String, Set<String>>();
		Set<String> outputs = new HashSet<String>();
		outputs.add(serviceURI11 + "/out");
		outputs.add(serviceURI12 + "/out");
		outputs.add(serviceURI13 + "/out");

		Set<String> costs = new HashSet<String>();
		costs.add("cost 11 a");
		costs.add("cost 11 b");
		costs.add("cost 13 a");

		Set<String> units = new HashSet<String>();
		units.add("unit:Celsius");
		units.add("unit:OtherUnit");

		Set<String> pids = new HashSet<String>();
		pids.add("pid 1");
		pids.add("pid 2");

		Set<String> plain = new HashSet<String>();
		Set<String> deep = new HashSet<String>();


		Set<String> value = new HashSet<String>();
		value.add("screen width");

		Set<String> inUnit = new HashSet<String>();
		inUnit.add("unit:point");

		Set<String> screenWidth = new HashSet<String>();
		screenWidth.add("screen width!unit:point");

		Set<String> screenHeight = new HashSet<String>();
		screenHeight.add("-!-");

		Set<String> resolution = new HashSet<String>();
		resolution.add("resolution!-");

		rMap.put("service:hasOutput", outputs);
		rMap.put("service:serviceCost", costs);
		rMap.put("service:parameterUnit", units);
		rMap.put("device:PID", pids);
		rMap.put("notexisting:plain", plain);
		rMap.put("notexisting:deep", deep);
		rMap.put("unit:value", value);
		rMap.put("unit:inUnit", inUnit);
		rMap.put("hardware:screenWidth", screenWidth);
		rMap.put("hardware:screenHeight", screenHeight);
		rMap.put("hardware:resolution", resolution);

		String rQuery = "" +
		"device:hasService/service:hasOutput,\n" +
		"device:hasService/service:serviceCost,\n" +
		"device:hasService/service:hasOutput/service:parameterUnit,\n" +
		"device:hasService/notexisting:deep,\n" +
		"notexisting:plain,\n" +
		"device:hasHardware/hardware:hasDisplay/hardware:screenWidth,\n" +
		"device:hasHardware/hardware:hasDisplay/hardware:screenWidth/unit:inUnit,\n" +
		"device:hasHardware/hardware:hasDisplay/hardware:screenWidth/unit:value,"+
		"device:hasHardware/hardware:hasDisplay/hardware:screenHeight,\n" +
		"device:hasHardware/hardware:hasDisplay/hardware:resolution,\n" +
		"device:PID";

		String rQueryEmpty = "";

		try{
			Map<String, Set<String>> result = device1.getRequirements(rQuery);
			//      System.out.println("Q: \n"+rQuery);
			//      System.out.println("RES: \n"+result);
			//      System.out.println("EXP: \n"+rMap);
			assertEquals(rMap, result);

			Map<String, Set<String>> resultEmpty = device1.getRequirements(rQueryEmpty);
			assertEquals(new HashMap<String, Set<String>>(), resultEmpty);

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test 
	public void testGetUpdateValue(){

		String deviceURI = "http://device.uri/1";

		String serviceURI = "http://service.uri/1";

		Graph device = GraphLoader.load(
				deviceURI, 
				GraphData.physicalDevice(deviceURI, false, deviceURI+"/template"));
		device.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI, serviceURI, Namespace.unit + "Celsius")));
		device.add(GraphLoader.load(
				"",
				GraphData.hardware(deviceURI, "screen width", "point", "resolution")));

		ValueFactory f = new ValueFactoryImpl();
		ResourceUtil.addStatement(
				serviceURI, 
				Service.serviceCost, 
				"cost 1", 
				XMLSchema.STRING,
				f, 
				device);

		ResourceUtil.addStatement(
				deviceURI, 
				Device.PID, 
				"pid 1", 
				XMLSchema.STRING,
				f, 
				device);
		ResourceUtil.addStatement(
				deviceURI, 
				Device.PID, 
				"pid 2", 
				XMLSchema.STRING,
				f, 
				device);


		try{
			String pathSingle = "" +
			"device:hasService/service:serviceCost;service:value\n";
			assertEquals(Arrays.asList(
					"http://service.uri/1", 
					"http://localhost/ontologies/Service.owl#serviceCost", 
					"http://localhost/ontologies/Service.owl#value"),
					device.getUpdateStatementBase(pathSingle));

			String pathMultiple = "" +
			"device:PID;ns:value\n";
			assertEquals(null,
					device.getUpdateStatementBase(pathMultiple));

			String pathNoValue = "" +
			"device:clonedFromTemplate\n";
			assertEquals(null,
					device.getUpdateStatementBase(pathNoValue));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
