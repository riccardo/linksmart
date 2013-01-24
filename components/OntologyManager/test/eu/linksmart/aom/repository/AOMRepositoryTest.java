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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;
import org.junit.Test;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.model.Rdfs;
import eu.linksmart.aom.ontology.model.Service;
import eu.linksmart.aom.ontology.model.Unit;
import eu.linksmart.aom.ontology.schema.Property;
import eu.linksmart.aom.ontology.schema.ResourceProperty;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.testutil.DataComparator;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.RepositoryUtil;

public class AOMRepositoryTest {
	@Test
	public void testStore(){
		String location = "store";

		AOMRepository repo = RepositoryFactory.local(location);
		try{
			repo.store(GraphLoader.load(GraphData.deviceURI, GraphData.device));
			assertEquals(RepositoryUtil.repositorySize(repo), 7);
		}
		finally{
			repo.close();
		}
		RepositoryUtil.clean(location);
	}

	@Test
	public void testRemove(){
		String location = "remove";

		AOMRepository repo = RepositoryFactory.local(location);
		try{
			String deviceURI = "http://my.device.uri";
			String infoURI = "http://my.info.uri";
			repo.store(GraphLoader.load(deviceURI, GraphData.device(deviceURI, infoURI)));
			assertEquals(RepositoryUtil.repositorySize(repo), 9);

			Graph device = repo.getResource(deviceURI);
			repo.remove(device.subGraph(Device.info));

			device = repo.getResource(deviceURI);
			assertEquals(RepositoryUtil.repositorySize(repo), 1);
		}
		finally{
			repo.close();
		}
		RepositoryUtil.clean(location);
	}

	@Test
	public void testRemoveRunTimeDevices(){
		String location = "remove";

		AOMRepository repo = RepositoryFactory.local(location);
		try{
			String templateURI = "http://my.device.uri/template";
			String runtimeURI = "http://my.device.uri/runtime";
			repo.store(GraphLoader.load(templateURI, GraphData.physicalDevice(templateURI, true)));
			repo.store(GraphLoader.load(runtimeURI, GraphData.physicalDevice(runtimeURI, false)));
			repo.store(GraphLoader.load("", GraphData.taxonomy()));

			Set<Graph> devices = repo.getDevices("rdf:type;device:Device");
			assertEquals(2, devices.size());
			
			repo.removeRunTimeDevices();

			devices = repo.getDevices("rdf:type;device:Device");
			assertEquals(1, devices.size());
			assertEquals(templateURI, devices.iterator().next().getBaseURI());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			repo.close();
		}
		RepositoryUtil.clean(location);
	}


	@Test
	public void testRemoveStmt(){
		String location = "removeStmt";

		AOMRepository repo = RepositoryFactory.local(location);
		try{
			String deviceURI = "http://my.device.uri";
			String pid1 = "pid1";
			String pid2 = "pid2";
			Graph g = GraphLoader.load(deviceURI, GraphData.device(deviceURI));
			g.add(GraphLoader.load(deviceURI, GraphData.pid(deviceURI, pid1)));
			g.add(GraphLoader.load(deviceURI, GraphData.pid(deviceURI, pid2)));
			repo.store(g);
			assertEquals(RepositoryUtil.repositorySize(repo), 3);

			repo.remove(deviceURI, Device.PID);

			assertEquals(RepositoryUtil.repositorySize(repo), 1);
		}
		finally{
			repo.close();
		}
		RepositoryUtil.clean(location);
	}


	@Test
	public void testGetDevice(){
		String location = "getDevice";

		AOMRepository repo = RepositoryFactory.local(location);
		try{
			Graph device = GraphLoader.load(GraphData.deviceURI, GraphData.device);
			repo.store(device);
			repo.store(GraphLoader.load(GraphData.service1URI, GraphData.service1));
			Graph retrieved = repo.getResource(GraphData.deviceURI);
			assertEquals(device, retrieved);
		}
		finally{
			repo.close();
		}
		RepositoryUtil.clean(location);
	}

	@Test
	public void testClassExists() throws Exception {
		String location = "classExists";
		AOMRepository repo = RepositoryFactory.local(location);

		String myClass1 = "http://my.class/1"; 
		String myClass2 = "http://my.class/2"; 
		RepositoryConnection conn = repo.getConnection();
		try{
			conn.add(new URIImpl(myClass1), Rdfs.subClassOf, Rdfs.rdfsClass);
			conn.add(new URIImpl(myClass2), Rdfs.subClassOf, new URIImpl(myClass1));
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

		assertEquals(repo.classExists(myClass1), true);
		assertEquals(repo.classExists(myClass2), true);
		assertEquals(repo.classExists("http://whatever.uri"), false);

		repo.close();
		RepositoryUtil.clean(location);
	}

	@Test
	public void testInstanceExists() throws Exception {
		String location = "instanceExists";
		AOMRepository repo = RepositoryFactory.local(location);

		String i1 = "http://my.class/1"; 
		String i2 = "http://my.class/2"; 
		RepositoryConnection conn = repo.getConnection();
		try{
			conn.add(new URIImpl(i1), Rdf.rdfType, new URIImpl("http://any.class"));
			conn.add(new URIImpl(i2), new URIImpl("http://any.property"), new URIImpl("http://any.instance"));
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

		assertEquals(repo.instanceExists(i1), true);
		assertEquals(repo.instanceExists(i2), false);

		repo.close();
		RepositoryUtil.clean(location);
	}

	@Test
	public void testGetUnit() throws Exception {
		String location = "getUnit";
		AOMRepository repo = spy(RepositoryFactory.local(location));

		String u1 = "Celsius";
		String u2 = "Nothing";

		doReturn(true).when(repo).instanceExists(Namespace.unit + u1);
		doReturn(false).when(repo).instanceExists(Namespace.unit + u2);

		assertEquals(repo.getUnit(u1), (Namespace.unit + u1));
		assertEquals(repo.getUnit(u2), null);

		repo.close();
		RepositoryUtil.clean(location);
	}


	@Test
	public void testGetUnitValue() throws Exception {
		String location = "getUnitValue";
		AOMRepository repo = spy(RepositoryFactory.local(location));

		String u1 = "Celsius";
		String u2 = "Nothing";
		String u1URI = Namespace.unit + u1;

		doReturn(true).when(repo).instanceExists(u1URI);
		doReturn(false).when(repo).instanceExists(Namespace.unit + u2);

		String val = "some val";
		Graph uv1 = repo.getUnitValue(val, u1);
		assertEquals(
				uv1, 
				GraphLoader.load(
						uv1.getBaseURI(), 
						GraphData.unitValue(
								uv1.getBaseURI(),
								u1URI,
								val)));

		Graph uv2 = repo.getUnitValue(val, u2);
		assertEquals(
				uv2, 
				GraphLoader.load(
						uv2.getBaseURI(), 
						GraphData.unitValue(
								uv2.getBaseURI(),
								val)));

		repo.close();
		RepositoryUtil.clean(location);
	}



	@Test
	public void testGetDevicesByAOMQuery() throws Exception{
		String location = "getDevices";
		AOMRepository repo = RepositoryFactory.local(location);
		String AOMQuery = "device:hasConfiguration";

		String configURI = "http://some.config.uri";
		String d1URI = "http://my.device.uri/1";
		String d2URI = "http://my.device.uri/2";
		String d3URI = "http://my.device.uri/3";

		Graph d1 = GraphLoader.load(d1URI, GraphData.device(d1URI));
		Graph c1 = GraphLoader.load(d1URI, GraphData.config(d1URI, configURI));
		d1.add(c1);

		Graph d2 = GraphLoader.load(d2URI, GraphData.device(d2URI));

		Graph d3 = GraphLoader.load(d3URI, GraphData.device(d3URI));
		Graph c3 = GraphLoader.load(d3URI, GraphData.config(d3URI, configURI));
		d3.add(c3);

		repo.store(d1);
		repo.store(d2);
		repo.store(d3);


		Set<Graph> devices = repo.getDevices(AOMQuery);
		assertTrue(devices.size() == 2);
		assertTrue(devices.contains(d1));
		assertTrue(devices.contains(d3));

		repo.close();
		RepositoryUtil.clean(location);
	}



	@Test
	public void testGetDevicesBySPARQL() throws Exception{
		String location = "getDevicesSPARQL";
		AOMRepository repo = RepositoryFactory.local(location);

		String d1URI = Namespace.device + "Device_1";
		String d2URI = Namespace.device + "Device_2";
		String d3URI = Namespace.device + "Device_3";
		String c1URI = Namespace.configuration + "Configuration_1";
		String c2URI = Namespace.configuration + "Configuration_2";
		String c3URI = Namespace.configuration + "Configuration_3";

		String name1 = "name 1";
		String impl1 = "impl 1";
		String name2 = "name 2";
		String impl2 = "impl 2";

		Graph d1 = GraphLoader.load(d1URI, GraphData.device(d1URI));
		Graph c1 = GraphLoader.load(d1URI, GraphData.configContent(d1URI, c1URI, name1, impl1));
		d1.add(c1);

		Graph d2 = GraphLoader.load(d2URI, GraphData.device(d2URI));

		Graph d3 = GraphLoader.load(d3URI, GraphData.device(d3URI));
		Graph c3 = GraphLoader.load(d3URI, GraphData.configContent(d3URI, c3URI, name2, impl2));
		d3.add(c3);

		repo.store(d1);
		repo.store(d2);
		repo.store(d3);

		Set<Graph> devices = repo.getDevices(d1);
		assertTrue(devices.size() == 1);
		assertTrue(devices.contains(d1));

		devices = repo.getDevices(d2);
		assertTrue(devices.size() == 3);
		assertTrue(devices.contains(d1));
		assertTrue(devices.contains(d2));
		assertTrue(devices.contains(d3));

		devices = repo.getDevices(d3);
		assertTrue(devices.size() == 1);
		assertTrue(devices.contains(d3));

		repo.close();
		RepositoryUtil.clean(location);
	}


	@Test 
	public void testIsInStaticModel(){
		String location = "isInStaticModel";
		AOMRepository repo = RepositoryFactory.local(location);

		String myUnit = Namespace.unit + "Mpeg";
		String anyEntity = Namespace.unit + "NotUnit";
		Graph m = GraphLoader.load("", GraphData.model());
		Graph u = GraphLoader.load("", GraphData.unit(myUnit));

		repo.store(m);
		repo.store(u);

		assertTrue(repo.isInStaticModel(new URIImpl(myUnit)));
		assertFalse(repo.isInStaticModel(new URIImpl(anyEntity)));

		repo.close();
		RepositoryUtil.clean(location);
	}


	@Test 
	public void testGetSuperClassesOf(){
		String location = "superClasses";
		AOMRepository repo = RepositoryFactory.local(location);

		Graph t = GraphLoader.load("", GraphData.taxonomy());

		repo.store(t);

		Set<String> exp = new HashSet<String>();
		exp.add(Device.Device.stringValue());
		exp.add(Device.PhysicalDevice.stringValue());

		assertEquals(exp, repo.getSuperClassesOf(Namespace.device + "SomeDevice"));


		exp.add(Namespace.device + "SomeDevice");
		assertEquals(exp, repo.getSuperClassesOf(Namespace.device + "AnotherDevice"));

		repo.close();
		RepositoryUtil.clean(location);
	}


	@Test 
	public void testGetSubClassesOf(){
		String location = "subClasses";
		AOMRepository repo = RepositoryFactory.local(location);

		Graph t = GraphLoader.load("", GraphData.propertyTaxonomy());

		repo.store(t);

		Set<String> exp1 = new HashSet<String>();
		exp1.add(Namespace.device+"PhysicalSubDevice");

		Set<String> exp2 = new HashSet<String>();
		exp2.add(Namespace.device+"SemanticSubDevice");

		Set<String> exp3 = new HashSet<String>();
		exp3.add(Namespace.device+"PhysicalDevice");
		exp3.add(Namespace.device+"PhysicalSubDevice");
		exp3.add(Namespace.device+"SemanticDevice");
		exp3.add(Namespace.device+"SemanticSubDevice");

		assertEquals(exp1, repo.getSubClassesOf(Namespace.device + "PhysicalDevice"));
		assertEquals(exp2, repo.getSubClassesOf(Namespace.device + "SemanticDevice"));
		assertEquals(exp3, repo.getSubClassesOf(Namespace.device + "Device"));

		repo.close();
		RepositoryUtil.clean(location);
	}


	@Test 
	public void testGetInstancesOf(){
		String location = "instances";
		AOMRepository repo = RepositoryFactory.local(location);

		Graph t = GraphLoader.load("", GraphData.propertyTaxonomy());
		t.add(GraphLoader.load("", GraphData.instances()));

		repo.store(t);


		assertEquals(
				new HashSet<String>(Arrays.asList(Namespace.device+"i1")), 
				repo.getInstancesOf(Namespace.device + "PhysicalDevice", false));
		assertEquals(
				new HashSet<String>(Arrays.asList(Namespace.device+"i2")), 
				repo.getInstancesOf(Namespace.device + "SemanticDevice", false));
		assertEquals(
				new HashSet<String>(), 
				repo.getInstancesOf(Namespace.device + "Device", false));
		assertEquals(
				new HashSet<String>(Arrays.asList(Namespace.device+"i1", Namespace.device+"i2", Namespace.device+"i3")), 
				repo.getInstancesOf(Namespace.device + "Device", true));

		repo.close();
		RepositoryUtil.clean(location);
	}

	@Test 
	public void testGetClassProperties(){
		String location = "classProps";
		AOMRepository repo = RepositoryFactory.local(location);

		String p11URI = "http://property.uri/1/1";
		String p12URI = "http://property.uri/1/2";
		String p13URI = "http://property.uri/1/3";
		String p21URI = "http://property.uri/2/1";
		String p22URI = "http://property.uri/2/2";
		String p23URI = "http://property.uri/2/3";
		String classURI1 = "http://class.uri/1";
		String classURI2 = "http://class.uri/2";
		Graph p11 = GraphLoader.load(
				p11URI, 
				GraphData.propertyModel(p11URI, classURI1, Namespace.xsd+"string"));
		Graph p12 = GraphLoader.load(
				p12URI, 
				GraphData.propertyModel(p12URI, classURI1, Namespace.xsd+"boolean"));
		Graph p13 = GraphLoader.load(
				p13URI, 
				GraphData.propertyModel(p13URI, classURI1, Namespace.device+"SomeClass"));

		Graph p21 = GraphLoader.load(
				p21URI, 
				GraphData.propertyModel(p21URI, classURI2, Namespace.xsd+"integer"));
		Graph p22 = GraphLoader.load(
				p22URI, 
				GraphData.propertyModel(p22URI, classURI2, Namespace.xsd+"float"));
		Graph p23 = GraphLoader.load(
				p23URI, 
				GraphData.propertyModel(p23URI, classURI2, Namespace.device+"AnotherClass"));

		repo.store(p11);
		repo.store(p12);
		repo.store(p13);
		repo.store(p21);
		repo.store(p22);
		repo.store(p23);


		assertEquals(
				new HashSet<Graph>(Arrays.asList(p11, p12, p13)),
				repo.getClassProperties(classURI1));
		assertEquals(
				new HashSet<Graph>(Arrays.asList(p21, p22, p23)),
				repo.getClassProperties(classURI2));

		repo.close();
		RepositoryUtil.clean(location);
	}


	@Test 
	public void testGetClassPropertiesInherit(){
		String location = "classPropsInherit";
		AOMRepository repo = RepositoryFactory.local(location);

		String p1URI = Namespace.service + "Property_1";
		String p2URI = Namespace.service + "Property_2";
		String p3URI = Namespace.service + "Property_3";
		String classURI1 = Namespace.device + "Device";
		String classURI2 = Namespace.device + "PhysicalDevice";
		String classURI3 = Namespace.device + "PhysicalSubDevice";

		Graph t = GraphLoader.load(
				"", 
				GraphData.propertyTaxonomy());
		Graph p1 = GraphLoader.load(
				p1URI, 
				GraphData.propertyModel(p1URI, classURI1, Namespace.xsd+"string"));
		Graph p2 = GraphLoader.load(
				p2URI, 
				GraphData.propertyModel(p2URI, classURI2, Namespace.xsd+"boolean"));
		Graph p3 = GraphLoader.load(
				p3URI, 
				GraphData.propertyModel(p3URI, classURI3, Namespace.xsd+"integer"));

		repo.store(t);
		repo.store(p1);
		repo.store(p2);
		repo.store(p3);


		assertEquals(
				new HashSet<Graph>(Arrays.asList(p1)),
				repo.getClassProperties(classURI1));
		assertEquals(
				new HashSet<Graph>(Arrays.asList(p1, p2)),
				repo.getClassProperties(classURI2));
		assertEquals(
				new HashSet<Graph>(Arrays.asList(p1, p2, p3)),
				repo.getClassProperties(classURI3));

		repo.close();
		RepositoryUtil.clean(location);
	}



	@Test 
	public void testGetDevicesWithServicesQuery(){
		String location = "devicesWithServicesBase";
		AOMRepository repo = RepositoryFactory.local(location);

		String deviceURI1 = "http://device.uri/1";
		String deviceURI2 = "http://device.uri/2";
		String deviceURI3 = "http://device.uri/3";

		String serviceURI11 = "http://service.uri/11";
		String serviceURI12 = "http://service.uri/12";
		String serviceURI13 = "http://service.uri/13";
		String serviceURI21 = "http://service.uri/21";
		String serviceURI22 = "http://service.uri/22";
		String serviceURI31 = "http://service.uri/31";

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

		Graph device2 = GraphLoader.load(
				deviceURI2, 
				GraphData.physicalDevice(deviceURI2, false, deviceURI2+"/template"));
		device2.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI2, serviceURI21, Namespace.unit + "OtherUnit")));
		device2.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI2, serviceURI22, Namespace.unit + "Celsius")));

		Graph device3 = GraphLoader.load(
				deviceURI3, 
				GraphData.physicalDevice(deviceURI3, false, deviceURI3+"/template"));
		device3.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI3, serviceURI31, Namespace.unit + "OtherUnit")));

		repo.store(device1);
		repo.store(device2);
		repo.store(device3);

		String deviceQuery = "device:isDeviceTemplate;\"false\"^^xsd:boolean";
		String serviceQuery = "service:hasOutput/service:parameterUnit;unit:Celsius";

		Map<String, Set<String>> expected = new HashMap<String, Set<String>>();
		Set<String> services1 = new HashSet<String>();
		services1.add(serviceURI11);
		services1.add(serviceURI13);

		Set<String> services2 = new HashSet<String>();
		services2.add(serviceURI22);

		expected.put(deviceURI1, services1);
		expected.put(deviceURI2, services2);

		try{
			Map<String, Set<String>> result = repo.getDevicesWithServicesMap(serviceQuery, deviceQuery);
			assertEquals(expected, result);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		repo.close();
		RepositoryUtil.clean(location);
	}

	@Test 
	public void testGetDevicesWithServicesQueryNoMatchService(){
		String location = "devicesWithServicesBaseNoMatch";
		AOMRepository repo = RepositoryFactory.local(location);

		String deviceURI1 = "http://device.uri/1";
		String deviceURI2 = "http://device.uri/2";
		String deviceURI3 = "http://device.uri/3";

		String serviceURI11 = "http://service.uri/11";
		String serviceURI12 = "http://service.uri/12";
		String serviceURI13 = "http://service.uri/13";
		String serviceURI21 = "http://service.uri/21";
		String serviceURI22 = "http://service.uri/22";
		String serviceURI31 = "http://service.uri/31";


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

		Graph device2 = GraphLoader.load(
				deviceURI2, 
				GraphData.physicalDevice(deviceURI2, false, deviceURI2+"/template"));
		device2.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI2, serviceURI21, Namespace.unit + "OtherUnit")));
		device2.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI2, serviceURI22, Namespace.unit + "Celsius")));

		Graph device3 = GraphLoader.load(
				deviceURI3, 
				GraphData.physicalDevice(deviceURI3, false, deviceURI3+"/template"));
		device3.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI3, serviceURI31, Namespace.unit + "OtherUnit")));

		repo.store(device1);
		repo.store(device2);
		repo.store(device3);

		String serviceQuery = "service:hasOutput/service:parameterUnit;unit:NoUnit";

		Map<String, Set<String>> expected = new HashMap<String, Set<String>>();
		try{
			Map<String, Set<String>> result = repo.getDevicesWithServicesMap(serviceQuery, "");
			assertEquals(expected, result);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		repo.close();
		RepositoryUtil.clean(location);
	}


	@Test 
	public void testGetDevicesWithServicesQueryNoMatchDevice(){
		String location = "devicesWithServicesBaseNoMatch";
		AOMRepository repo = RepositoryFactory.local(location);

		String deviceURI1 = "http://device.uri/1";
		String deviceURI2 = "http://device.uri/2";
		String deviceURI3 = "http://device.uri/3";

		String serviceURI11 = "http://service.uri/11";
		String serviceURI12 = "http://service.uri/12";
		String serviceURI13 = "http://service.uri/13";
		String serviceURI21 = "http://service.uri/21";
		String serviceURI22 = "http://service.uri/22";
		String serviceURI31 = "http://service.uri/31";


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

		Graph device2 = GraphLoader.load(
				deviceURI2, 
				GraphData.physicalDevice(deviceURI2, false, deviceURI2+"/template"));
		device2.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI2, serviceURI21, Namespace.unit + "OtherUnit")));
		device2.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI2, serviceURI22, Namespace.unit + "Celsius")));

		Graph device3 = GraphLoader.load(
				deviceURI3, 
				GraphData.physicalDevice(deviceURI3, false, deviceURI3+"/template"));
		device3.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI3, serviceURI31, Namespace.unit + "OtherUnit")));

		repo.store(device1);
		repo.store(device2);
		repo.store(device3);

		String serviceQuery = "service:hasOutput/service:parameterUnit;unit:NoUnit";
		String deviceQuery = "device:hasHardware";

		Map<String, Set<String>> expected = new HashMap<String, Set<String>>();
		try{
			Map<String, Set<String>> result = repo.getDevicesWithServicesMap(serviceQuery, deviceQuery);
			assertEquals(expected, result);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		repo.close();
		RepositoryUtil.clean(location);
	}


	@Test 
	public void testGetDevicesWithServicesData(){
		String location = "devicesWithServicesData";
		AOMRepository repo = RepositoryFactory.local(location);

		String deviceURI1 = "http://device.uri/1";
		String deviceURI2 = "http://device.uri/2";
		String deviceURI3 = "http://device.uri/3";

		String serviceURI11 = "http://service.uri/11";
		String serviceURI12 = "http://service.uri/12";
		String serviceURI13 = "http://service.uri/13";
		String serviceURI21 = "http://service.uri/21";
		String serviceURI22 = "http://service.uri/22";
		String serviceURI31 = "http://service.uri/31";

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

		Graph device2 = GraphLoader.load(
				deviceURI2, 
				GraphData.physicalDevice(deviceURI2, false, deviceURI2+"/template"));
		device2.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI2, serviceURI21, Namespace.unit + "OtherUnit")));
		device2.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI2, serviceURI22, Namespace.unit + "Celsius")));

		Graph device3 = GraphLoader.load(
				deviceURI3, 
				GraphData.physicalDevice(deviceURI3, false, deviceURI3+"/template"));
		device3.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI3, serviceURI31, Namespace.unit + "OtherUnit")));

		repo.store(device1);
		repo.store(device2);
		repo.store(device3);

		Map<String, Set<String>> dsMap = new HashMap<String, Set<String>>();
		Set<String> services1 = new HashSet<String>();
		services1.add(serviceURI11);
		services1.add(serviceURI13);

		Set<String> services2 = new HashSet<String>();
		services2.add(serviceURI22);

		dsMap.put(deviceURI1, services1);
		dsMap.put(deviceURI2, services2);

		try{
			Set<Graph> result = repo.getDevicesWithServices(dsMap);
			assertEquals(2, result.size());

			ValueFactory f = repo.getValueFactory();
			ResourceUtil.addStatement(
					serviceURI11, 
					Service.matchedService, 
					"true", 
					XMLSchema.BOOLEAN,
					f, 
					device1);
			ResourceUtil.addStatement(
					serviceURI13, 
					Service.matchedService, 
					"true", 
					XMLSchema.BOOLEAN,
					f, 
					device1);
			ResourceUtil.addStatement(
					serviceURI22, 
					Service.matchedService, 
					"true", 
					XMLSchema.BOOLEAN,
					f, 
					device2);


			assertTrue(result.contains(device1));
			assertTrue(result.contains(device2));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		repo.close();
		RepositoryUtil.clean(location);
	}

	@Test 
	public void testGetDevicesWithServicesNoMatch(){
		String location = "devicesWithServicesNoMatch";
		AOMRepository repo = RepositoryFactory.local(location);

		String deviceURI1 = "http://device.uri/1";
		String deviceURI2 = "http://device.uri/2";
		String deviceURI3 = "http://device.uri/3";

		String serviceURI11 = "http://service.uri/11";
		String serviceURI12 = "http://service.uri/12";
		String serviceURI13 = "http://service.uri/13";
		String serviceURI21 = "http://service.uri/21";
		String serviceURI22 = "http://service.uri/22";
		String serviceURI31 = "http://service.uri/31";

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

		Graph device2 = GraphLoader.load(
				deviceURI2, 
				GraphData.physicalDevice(deviceURI2, false, deviceURI2+"/template"));
		device2.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI2, serviceURI21, Namespace.unit + "OtherUnit")));
		device2.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI2, serviceURI22, Namespace.unit + "Celsius")));

		Graph device3 = GraphLoader.load(
				deviceURI3, 
				GraphData.physicalDevice(deviceURI3, false, deviceURI3+"/template"));
		device3.add(GraphLoader.load(
				"",
				GraphData.service(deviceURI3, serviceURI31, Namespace.unit + "OtherUnit")));

		repo.store(device1);
		repo.store(device2);
		repo.store(device3);

		Map<String, Set<String>> dsMap = new HashMap<String, Set<String>>();

		try{
			Set<Graph> result = repo.getDevicesWithServices(dsMap);
			assertEquals(0, result.size());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		repo.close();
		RepositoryUtil.clean(location);
	}

	@Test 
	public void testGetDevicesWithServices(){
		String location = "getDevicesWithServices";
		AOMRepository repo = spy(RepositoryFactory.local(location));

		String sq = "some service query";
		String dq = "some device query";
		Map<String, Set<String>> dsMap = new HashMap<String, Set<String>>();
		Set<String> devices = new HashSet<String>();

		try{
			doReturn(dsMap).when(repo).getDevicesWithServicesMap(sq, dq);
			doReturn(devices).when(repo).getDevicesWithServices(dsMap);

			assertEquals(devices, repo.getDevicesWithServices(sq, dq));
		}
		catch(Exception e){
			e.printStackTrace();
		}

		repo.close();
		RepositoryUtil.clean(location);
	}

	@Test
	public void testGetUpdateValue()  {
		String location = "getUpdateValue";

		String deviceURI = "http://device.uri";
		String instanceURI = "http://inst.uri";
		String propertyURI = "http://prop.uri";
		String valueURI = "http://value.uri";
		String path = "query";
		List<String> stBase = Arrays.asList(instanceURI, propertyURI, valueURI);
		Graph device = mock(Graph.class);

		AOMRepository repo = spy(RepositoryFactory.local(location));

		try{
			doReturn(device).when(repo).getResource(deviceURI);
			doReturn(stBase).when(device).getUpdateStatementBase(path);
			
			repo.updateValue(deviceURI, path);

			verify(repo, times(1)).remove(instanceURI, new Property(propertyURI));
			verify(repo, times(1)).add(instanceURI, propertyURI, valueURI, false);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		repo.close();
		RepositoryUtil.clean(location);

	}



	private void testQuery(AOMRepository repo, String q){
		RepositoryConnection conn = repo.getConnection();
		try{
			System.out.println("FIRING: SPARQL \n"+q);
			TupleQueryResult result = repo.sparqlQuery(conn, q);
			while(result.hasNext()){
				System.out.println("> "+result.next());
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
	}

	private void addDomain(Graph p, String domain){
		ResourceUtil.addStatement(
				p.getBaseURI(), 
				Rdfs.domain, 
				domain, 
				new ValueFactoryImpl(), 
				p);
	}

	@Test
	public void testGetPropertyAnootationModels(){
		String location = "propertyAnnotationModels";
		AOMRepository repo = RepositoryFactory.local(location);

		String modelURI = "http://model.uri";
		Graph model = GraphLoader.load(modelURI, GraphData.propertyAnnotationModel());

		repo.store(model);


		Graph p1 = GraphLoader.load(Namespace.device + "propertySingleValueFormFieldProperty", 
				GraphData.property("SingleValueFormFieldProperty", Namespace.device, "Device", "Range1"));
		addDomain(p1, Namespace.device + "PhysicalDevice");
		addDomain(p1, Namespace.device + "PhysicalSubDevice");
		addDomain(p1, Namespace.device + "SemanticDevice");
		addDomain(p1, Namespace.device + "SemanticSubDevice");

		Graph p2 = GraphLoader.load(Namespace.service + "propertyMultiValueFormFieldProperty", 
				GraphData.property("MultiValueFormFieldProperty", Namespace.service, "Device", "Range2"));
		addDomain(p2, Namespace.device + "PhysicalDevice");
		addDomain(p2, Namespace.device + "PhysicalSubDevice");
		addDomain(p2, Namespace.device + "SemanticDevice");
		addDomain(p2, Namespace.device + "SemanticSubDevice");

		Graph p3 = GraphLoader.load(Namespace.event + "propertySingleValueFormProperty", 
				GraphData.property("SingleValueFormProperty", Namespace.event, "PhysicalDevice", "Range3"));
		addDomain(p3, Namespace.device + "PhysicalSubDevice");

		Graph p4 = GraphLoader.load(Namespace.energy + "propertyMultiValueFormProperty", 
				GraphData.property("MultiValueFormProperty", Namespace.energy, "SemanticDevice", "Range4"));
		addDomain(p4, Namespace.device + "SemanticSubDevice");

		Graph p5 = GraphLoader.load(Namespace.application + "propertySingleValueAnnotationProperty", 
				GraphData.property("SingleValueAnnotationProperty", Namespace.application, "PhysicalSubDevice", "Range5"));

		Graph p6 = GraphLoader.load(Namespace.unit + "propertyMultiValueAnnotationProperty", 
				GraphData.property("MultiValueAnnotationProperty", Namespace.unit, "SemanticSubDevice", "Range6"));

		Set<Graph> expected = new HashSet<Graph>(Arrays.asList(p1, p2, p3, p4, p5, p6));
		Set<Graph> properties = repo.getPropertyAnnotationModels();
		assertEquals(expected, properties);

		repo.close();
		RepositoryUtil.clean(location);
	}

	//  @Test
	//  public void test(){
	//    String location = "sesame1";
	//
	//    AOMRepository repo = RepositoryFactory.local(location);
	//    ValueFactory f = repo.getValueFactory();
	//    try{
	//      String hURI = Namespace.hardware + "Hardware_1";
	//      String dURI = Namespace.hardware + "Display_1";
	//      String swURI = Namespace.hardware + "UnitValue_1";
	//      String shURI = Namespace.hardware + "UnitValue_2";
	//      String sURI = Namespace.hardware + "Speaker_1";
	//      Graph g = new Graph(hURI);
	//      ResourceUtil.addStatement(
	//          hURI,
	//          Rdf.rdfType,
	//          Namespace.hardware + "Hardware",
	//          f,
	//          g);
	//      ResourceUtil.addStatement(
	//          hURI,
	//          new ResourceProperty(Namespace.hardware + "hasDisplay"),
	//          dURI,
	//          f,
	//          g);
	//      ResourceUtil.addStatement(
	//          dURI,
	//          new ResourceProperty(Namespace.hardware + "screenWidth"),
	//          swURI,
	//          f,
	//          g);
	//      ResourceUtil.addStatement(
	//          dURI,
	//          Rdf.rdfType,
	//          Namespace.hardware + "Display",
	//          f,
	//          g);
	//      ResourceUtil.addStatement(
	//          swURI,
	//          new ResourceProperty(Namespace.hardware + "value"),
	//          "keket",
	//          XMLSchema.STRING,
	//          f,
	//          g);
	//      ResourceUtil.addStatement(
	//          dURI,
	//          new ResourceProperty(Namespace.hardware + "screenHeight"),
	//          shURI,
	//          f,
	//          g);
	//      ResourceUtil.addStatement(
	//          shURI,
	//          new ResourceProperty(Namespace.hardware + "value"),
	//          "chujo",
	//          XMLSchema.STRING,
	//          f,
	//          g);
	//      ResourceUtil.addStatement(
	//          hURI,
	//          new ResourceProperty(Namespace.hardware + "hasSpeaker"),
	//          sURI,
	//          f,
	//          g);
	//      ResourceUtil.addStatement(
	//          sURI,
	//          Rdf.rdfType,
	//          Namespace.hardware + "Speaker",
	//          f,
	//          g);
	//      Graph c = g.clone(repo, Namespace.hardware + "MyTemplate");
	//      repo.store(c);
	//      System.out.println("BEFORE: \n"+repo.getResource(c.getBaseURI()).describe());
	//      String swCURI = c.subGraph(new ResourceProperty(Namespace.hardware + "hasDisplay")).value(new ResourceProperty(Namespace.hardware + "screenWidth"));
	//      System.out.println("SW U : "+swCURI);
	//      repo.remove(c.value(new ResourceProperty(Namespace.hardware + "hasDisplay")), Namespace.hardware + "screenWidth", swCURI);
	//      System.out.println("AFTER: \n"+repo.getResource(c.getBaseURI()).describe());
	//      
	//    }
	//    finally{
	//      repo.close();
	//    }
	//    RepositoryUtil.clean(location);
	//  }
}
