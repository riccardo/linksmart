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

package eu.linksmart.aom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.HashSet;
import java.util.Set;

import org.jdom.Element;
import org.junit.Test;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;


import eu.linksmart.aom.generator.ModelGenerator;
import eu.linksmart.aom.generator.SCPDGenerator;
import eu.linksmart.aom.impl.ApplicationOntologyManagerSoapBindingImpl;
import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Service;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.processor.SCPDProcessor;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.testutil.DataComparator;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.RepositoryUtil;

public class ApplicationOntologyManagerSoapBindingImplTest {
  
  @Test
  public void testCreateDevice() throws Exception {
    String location = "createDevice";
    AOMRepository repo = RepositoryFactory.local(location);
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String deviceURI = 
      impl.createDeviceTemplate(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));

    assertEquals(65, RepositoryUtil.repositorySize(repo));
    assertEquals(65, repo.getResource(deviceURI).getStmts().size());
    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void testAssignPID() throws Exception {
    String location = "assignPID";
    AOMRepository repo = RepositoryFactory.local(location);
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String deviceURI = 
      impl.createDeviceTemplate(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));

    Graph device = repo.getResource(deviceURI);
    assertEquals(device.value(Device.PID), null);

    String pid = "myPID";
    impl.assignPID(deviceURI, pid);

    device = repo.getResource(deviceURI);
    assertEquals(device.value(Device.PID), pid);

    repo.close();
    RepositoryUtil.clean(location);
  }
  
  @Test
  public void testRemoveDevice() throws Exception {
    String location = "removeDevice";
    AOMRepository repo = RepositoryFactory.local(location);
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String deviceURI = 
      impl.createDeviceTemplate(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));

    assertEquals(65, RepositoryUtil.repositorySize(repo));
    assertEquals(65, repo.getResource(deviceURI).getStmts().size());
    
    impl.removeDevice(deviceURI);
    assertEquals(0, RepositoryUtil.repositorySize(repo));
    assertEquals(null, repo.getResource(deviceURI));
    
    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void testResolveDevice() throws Exception {
    String location = "resolveDevice";
    AOMRepository repo = RepositoryFactory.local(location);
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String discoXML1 = DataLoader.xmlToString("test/resources/discovery/tellstick.xml");
    String discoXML2 = DataLoader.xmlToString("test/resources/discovery/sensor.xml");
    String discoXML3 = DataLoader.xmlToString("test/resources/discovery/bluetooth.xml");
    
    String templateURI = 
      impl.createDeviceTemplate(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));
    impl.assignDiscoveryInfo(
        templateURI, 
        discoXML1);

    String templateMoreMatchesURI1 = 
      impl.createDeviceTemplate(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));
    impl.assignDiscoveryInfo(
        templateMoreMatchesURI1, 
        discoXML2);
    String templateMoreMatchesURI2 = 
      impl.createDeviceTemplate(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));
    impl.assignDiscoveryInfo(
        templateMoreMatchesURI2, 
        discoXML2);

    SCPDGenerator g = new SCPDGenerator();
    String expected = g.toString(g.process(repo.getResource(templateURI)));
    String scpd1 = impl.resolveDevice(discoXML1);
    String scpd2 = impl.resolveDevice(discoXML2);
    String scpd3 = impl.resolveDevice(discoXML3);
    
    Element scpdElm1 = DataLoader.parseString(scpd1);
    Element scpdElm2 = DataLoader.parseString(scpd2);
    Element scpdElm3 = DataLoader.parseString(scpd3);
    assertTrue(scpdElm1.getName().equals("root"));
    assertTrue(scpdElm2.getName().equals("error"));
    assertTrue(scpdElm3.getName().equals("error"));
    
    repo.close();
    RepositoryUtil.clean(location);
  }



  @Test
  public void testAssignDiscoveryInfo() throws Exception {
    String location = "assignDiscoInfo";
    AOMRepository repo = RepositoryFactory.local(location);
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String deviceURI = 
      impl.createDeviceTemplate(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));

    impl.assignDiscoveryInfo(
        deviceURI, 
        DataLoader.xmlToString("test/resources/discovery/bluetooth.xml"));

    assertEquals(109, RepositoryUtil.repositorySize(repo));
    assertEquals(109, repo.getResource(deviceURI).getStmts().size());

    impl.assignDiscoveryInfo(
        deviceURI, 
        DataLoader.xmlToString("test/resources/discovery/tellstick.xml"));

//    int stmtsNew = 60;
//    assertEquals(RepositoryUtil.repositorySize(repo), stmtsNew);
//    assertEquals(repo.getResource(deviceURI).getStmts().size(), stmtsNew);
    repo.close();
    RepositoryUtil.clean(location);

  }

  @Test
  public void testAssignEventModel() throws Exception {
    String location = "assignEvent";
    AOMRepository repo = RepositoryFactory.local(location);
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String deviceURI = 
      impl.createDeviceTemplate(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));

    impl.assignEventModel(
        deviceURI, 
        DataLoader.xmlToString("test/resources/event/events1.xml"));

    assertEquals(140, RepositoryUtil.repositorySize(repo));
    assertEquals(140, repo.getResource(deviceURI).getStmts().size());
    
    impl.assignEventModel(
        deviceURI, 
        DataLoader.xmlToString("test/resources/event/events2.xml"));

    assertEquals(90, RepositoryUtil.repositorySize(repo));
    assertEquals(90, repo.getResource(deviceURI).getStmts().size());


    repo.close();
    RepositoryUtil.clean(location);

  }

  @Test
  public void testAssignEnergyProfile() throws Exception {
    String location = "assignEnergy";
    AOMRepository repo = RepositoryFactory.local(location);
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String deviceURI = 
      impl.createDeviceTemplate(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));

    impl.assignEnergyProfile(
        deviceURI, 
        DataLoader.xmlToString("test/resources/energy/energy.xml"));

    assertEquals(115, RepositoryUtil.repositorySize(repo));
    assertEquals(115, repo.getResource(deviceURI).getStmts().size());

    impl.assignEnergyProfile(
        deviceURI, 
        DataLoader.xmlToString("test/resources/energy/energy1.xml"));

    assertEquals(101, RepositoryUtil.repositorySize(repo));
    assertEquals(101, repo.getResource(deviceURI).getStmts().size());
    repo.close();
    RepositoryUtil.clean(location);

  }

  @Test
  public void testAssignConfiguration() throws Exception {
    String location = "assignConfig";
    AOMRepository repo = RepositoryFactory.local(location);
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String deviceURI = 
      impl.createDeviceTemplate(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));

    impl.assignConfiguration(
        deviceURI, 
        DataLoader.xmlToString("test/resources/configuration/config.xml"));

    assertEquals(77, RepositoryUtil.repositorySize(repo));
    assertEquals(77, repo.getResource(deviceURI).getStmts().size());

    impl.assignConfiguration(
        deviceURI, 
        DataLoader.xmlToString("test/resources/configuration/config1.xml"));

    assertEquals(81, RepositoryUtil.repositorySize(repo));
    assertEquals(81, repo.getResource(deviceURI).getStmts().size());

    repo.close();
    RepositoryUtil.clean(location);

  }


  @Test
  public void testGetConfigurations() throws Exception {
    String location = "getConfigs";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String type1 = "http://some.type/SomeDeviceType";
    String type2 = "http://some.type/AnotherDeviceType";
    Set<Graph> devices = new HashSet<Graph>();
    devices.add(deviceStub(type1, 1));
    devices.add(deviceStub(type2, 2));
    devices.add(deviceStub(type1, 3));
    devices.add(deviceStub(type2, 4));
    doReturn(devices).when(repo).getDevices(any(String.class));

    String configs = impl.getConfigurations();

    String xml = "" +
    "<DeviceTypes> \n" +
    "  <DeviceType name=\""+type1+"\"> \n" + 
    "    <Configuration name=\"dev1\" implementationClass=\"\"/> \n" + 
    "    <Configuration name=\"dev3\" implementationClass=\"\"/> \n" + 
    "  </DeviceType> \n" + 
    "  <DeviceType name=\""+type2+"\"> \n" + 
    "    <Configuration name=\"dev2\" implementationClass=\"\"/> \n" + 
    "    <Configuration name=\"dev4\" implementationClass=\"\"/> \n" + 
    "  </DeviceType> \n" + 
    "</DeviceTypes> \n";


    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(xml, configs));

    repo.close();
    RepositoryUtil.clean(location);

  }

  @Test 
  public void testGetDevicesWithServicesErrorQuery() throws Exception{
    try{
      String location = "dsErrorQuery";
      AOMRepository repo = spy(RepositoryFactory.local(location));
      ApplicationOntologyManagerSoapBindingImpl impl = 
        spy(new ApplicationOntologyManagerSoapBindingImpl(repo));
      String result = impl.getDevicesWithServices("bad query", "", "", "");
      String expected = "" +
      "<response>\n" +
      "  <error>Incorrect AOM service query: \n[bad query]</error>\n" +
      "</response>";
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expected, result));
      repo.close();
      RepositoryUtil.clean(location);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test 
  public void testGetDevicesWithServicesErrorDeviceQuery() throws Exception{
    try{
      String location = "dsErrorQuery";
      AOMRepository repo = spy(RepositoryFactory.local(location));
      ApplicationOntologyManagerSoapBindingImpl impl = 
        spy(new ApplicationOntologyManagerSoapBindingImpl(repo));
      String result = impl.getDevicesWithServices("service:hasOutput", "bad device query", "", "");
      String expected = "" +
      "<response>\n" +
      "  <error>Incorrect AOM device query: \n[bad device query]</error>\n" +
      "</response>";
      System.out.println(result);
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expected, result));
      repo.close();
      RepositoryUtil.clean(location);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test 
  public void testGetDevicesWithServicesErrorDeviceRequirements() throws Exception{
    try{
      String location = "dsErrorDevRequirements";
      AOMRepository repo = spy(RepositoryFactory.local(location));
      ApplicationOntologyManagerSoapBindingImpl impl = 
        spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

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

      repo.store(device1);

      String result = impl.getDevicesWithServices("service:hasOutput", "", "bad dev requirements", "");
      String expected = "" +
      "<response>\n" +
      "  <error>Incorrect AOM requirement clauses: \n[bad dev requirements]</error>\n" +
      "</response>";
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expected, result));
      repo.close();
      RepositoryUtil.clean(location);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test 
  public void testGetDevicesWithServicesErrorServiceRequirements() throws Exception{
    try{
      String location = "dsErrorServRequirements";
      AOMRepository repo = spy(RepositoryFactory.local(location));
      ApplicationOntologyManagerSoapBindingImpl impl = 
        spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

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

      repo.store(device1);

      String result = impl.getDevicesWithServices("service:hasOutput", "", "device:PID", "bad serv requirements");
      String expected = "" +
      "<response>\n" +
      "  <error>Incorrect AOM requirement clauses: \n[bad serv requirements]</error>\n" +
      "</response>";
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expected, result));
      repo.close();
      RepositoryUtil.clean(location);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }


  @Test 
  public void testGetDevicesWithServices() throws Exception{
    String location = "ds";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String deviceURI1 = "http://device.uri/1";
    String deviceURI2 = "http://device.uri/2";
    String deviceURI3 = "http://device.uri/3";

    String serviceURI11 = "http://service.uri/11";
    String serviceURI12 = "http://service.uri/12";
    String serviceURI13 = "http://service.uri/13";
    String serviceURI21 = "http://service.uri/21";
    String serviceURI22 = "http://service.uri/22";
    String serviceURI31 = "http://service.uri/31";
    String serviceURI32 = "http://service.uri/32";

    ValueFactory f = new ValueFactoryImpl();

    Graph device1 = GraphLoader.load(
        deviceURI1, 
        GraphData.physicalDevice(deviceURI1, false, deviceURI1+"/template"));
    device1.add(GraphLoader.load(
        "",
        GraphData.pid(deviceURI1, "pid 1")));
    device1.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI1, serviceURI11, Namespace.unit + "Celsius")));
    device1.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI1, serviceURI12, Namespace.unit + "OtherUnit")));
    device1.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI1, serviceURI13, Namespace.unit + "Celsius")));
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

    Graph device2 = GraphLoader.load(
        deviceURI2, 
        GraphData.physicalDevice(deviceURI2, false, deviceURI2+"/template"));
    device2.add(GraphLoader.load(
        "",
        GraphData.pid(deviceURI2, "pid 2")));
    device2.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI2, serviceURI21, Namespace.unit + "OtherUnit")));
    device2.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI2, serviceURI22, Namespace.unit + "Celsius")));

    ResourceUtil.addStatement(
        serviceURI22, 
        Service.matchedService, 
        "true", 
        XMLSchema.BOOLEAN,
        f, 
        device2);

    Graph device3 = GraphLoader.load(
        deviceURI3, 
        GraphData.physicalDevice(deviceURI3, false, deviceURI3+"/template"));
    device3.add(GraphLoader.load(
        "",
        GraphData.pid(deviceURI3, "pid 3")));
    device3.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI3, serviceURI31, Namespace.unit + "OtherUnit")));
    device3.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI3, serviceURI32, Namespace.unit + "Celsius")));

    ResourceUtil.addStatement(
        serviceURI32, 
        Service.matchedService, 
        "true", 
        XMLSchema.BOOLEAN,
        f, 
        device3);


    String deviceQuery = "" +
    "device:PID,";
    String serviceQuery = "" +
    "service:hasOutput/service:parameterUnit;unit:Celsius,";
    String deviceRequirements = "" +
    "rdf:type,\n " +
    "device:clonedFromTemplate,\n " +
    "device:hasService/service:serviceOperation";
    String serviceRequirements = "" +
    "service:hasOutput/service:parameterUnit,\n" +
    "service:serviceOperation";

    repo.store(device1);
    repo.store(device2);
    repo.store(device3);

    String result = impl.getDevicesWithServices(serviceQuery, deviceQuery, deviceRequirements, serviceRequirements);
    String expected = DataLoader.xmlToString("test/resources/query/dsQueryResult3.xml");
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expected, result));
    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test 
  public void testGetDevicesWithServicesNoDeviceQuery() throws Exception{
    String location = "ds";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String deviceURI1 = "http://device.uri/1";
    String deviceURI2 = "http://device.uri/2";
    String deviceURI3 = "http://device.uri/3";

    String serviceURI11 = "http://service.uri/11";
    String serviceURI12 = "http://service.uri/12";
    String serviceURI13 = "http://service.uri/13";
    String serviceURI21 = "http://service.uri/21";
    String serviceURI22 = "http://service.uri/22";
    String serviceURI31 = "http://service.uri/31";
    String serviceURI32 = "http://service.uri/32";

    ValueFactory f = new ValueFactoryImpl();

    Graph device1 = GraphLoader.load(
        deviceURI1, 
        GraphData.physicalDevice(deviceURI1, false, deviceURI1+"/template"));
    device1.add(GraphLoader.load(
        "",
        GraphData.pid(deviceURI1, "pid 1")));
    device1.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI1, serviceURI11, Namespace.unit + "Celsius")));
    device1.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI1, serviceURI12, Namespace.unit + "OtherUnit")));
    device1.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI1, serviceURI13, Namespace.unit + "Celsius")));
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

    Graph device2 = GraphLoader.load(
        deviceURI2, 
        GraphData.physicalDevice(deviceURI2, false, deviceURI2+"/template"));
    device2.add(GraphLoader.load(
        "",
        GraphData.pid(deviceURI2, "pid 2")));
    device2.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI2, serviceURI21, Namespace.unit + "OtherUnit")));
    device2.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI2, serviceURI22, Namespace.unit + "Celsius")));

    ResourceUtil.addStatement(
        serviceURI22, 
        Service.matchedService, 
        "true", 
        XMLSchema.BOOLEAN,
        f, 
        device2);

    Graph device3 = GraphLoader.load(
        deviceURI3, 
        GraphData.physicalDevice(deviceURI3, false, deviceURI3+"/template"));
    device3.add(GraphLoader.load(
        "",
        GraphData.pid(deviceURI3, "pid 3")));
    device3.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI3, serviceURI31, Namespace.unit + "OtherUnit")));
    device3.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI3, serviceURI32, Namespace.unit + "Celsius")));

    ResourceUtil.addStatement(
        serviceURI32, 
        Service.matchedService, 
        "true", 
        XMLSchema.BOOLEAN,
        f, 
        device3);


    String deviceQuery = "";
    String serviceQuery = "" +
    "service:hasOutput/service:parameterUnit;unit:Celsius,";
    String deviceRequirements = "" +
    "rdf:type,\n " +
    "device:clonedFromTemplate,\n " +
    "device:hasService/service:serviceOperation";
    String serviceRequirements = "" +
    "service:hasOutput/service:parameterUnit,\n" +
    "service:serviceOperation";

    repo.store(device1);
    repo.store(device2);
    repo.store(device3);

    String result = impl.getDevicesWithServices(serviceQuery, deviceQuery, deviceRequirements, serviceRequirements);
    String expected = DataLoader.xmlToString("test/resources/query/dsQueryResult3.xml");
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expected, result));
    repo.close();
    RepositoryUtil.clean(location);
  }


  @Test 
  public void testGetDevicesErrorQuery() throws Exception{
    String location = "dErrorQuery";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));
    String result = impl.getDevices("bad query", "");
    String expected = "" +
    "<response>\n" +
    "  <error>Incorrect AOM query: \n[device:isDeviceTemplate;\"false\"^^xsd:boolean\n, bad query]</error>\n" +
    "</response>";
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expected, result));
    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test 
  public void testGetDevicesErrorDeviceRequirements() throws Exception{
    try{
      String location = "dErrorDevRequirements";
      AOMRepository repo = spy(RepositoryFactory.local(location));
      ApplicationOntologyManagerSoapBindingImpl impl = 
        spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

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

      repo.store(device1);

      String result = impl.getDevices("device:hasService", "bad dev requirements");
      String expected = "" +
      "<response>\n" +
      "  <error>Incorrect AOM requirement clauses: \n[bad dev requirements]</error>\n" +
      "</response>";
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expected, result));
      repo.close();
      RepositoryUtil.clean(location);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test 
  public void testGetDevices() throws Exception{
    String location = "d";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String deviceURI1 = "http://device.uri/1";
    String deviceURI2 = "http://device.uri/2";

    String serviceURI11 = "http://service.uri/11";
    String serviceURI12 = "http://service.uri/12";
    String serviceURI13 = "http://service.uri/13";
    String serviceURI21 = "http://service.uri/21";
    String serviceURI22 = "http://service.uri/22";

    ValueFactory f = new ValueFactoryImpl();

    Graph device1 = GraphLoader.load(
        deviceURI1, 
        GraphData.physicalDevice(deviceURI1, false, deviceURI1+"/template"));
    device1.add(GraphLoader.load(
        "",
        GraphData.pid(deviceURI1, "pid 1")));
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
        GraphData.hardware(deviceURI1, "screen width", "Px", "resolution")));
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

    Graph device2 = GraphLoader.load(
        deviceURI2, 
        GraphData.physicalDevice(deviceURI2, false, deviceURI2+"/template"));
    device2.add(GraphLoader.load(
        "",
        GraphData.pid(deviceURI2, "pid 2")));
    device2.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI2, serviceURI21, Namespace.unit + "OtherUnit")));
    device2.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI2, serviceURI22, Namespace.unit + "Celsius")));

    ResourceUtil.addStatement(
        serviceURI22, 
        Service.matchedService, 
        "true", 
        XMLSchema.BOOLEAN,
        f, 
        device2);

    String deviceQuery = "" +
    "device:hasService/service:hasOutput/service:parameterUnit;unit:Celsius,";
    String deviceRequirements = "" +
    "rdf:type,\n " +
    "device:clonedFromTemplate,\n " +
    "device:hasService/service:serviceOperation, " + 
    "device:hasHardware/hardware:hasDisplay/hardware:screenWidth, " +
    "device:hasHardware/hardware:hasDisplay/hardware:resolution, ";

    repo.store(device1);
    repo.store(device2);
    
    String result = impl.getDevices(deviceQuery, deviceRequirements);
    String expected = DataLoader.xmlToString("test/resources/query/dQueryResult2.xml");
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expected, result));
    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test 
  public void testGetDevicesEmptyQuery() throws Exception{
    String location = "dEmpty";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));


    String result = impl.getDevices("", "");
    String expected = "<response/>";
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expected, result));
    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test 
  public void testGetDevicesWithServicesEmptyQuery() throws Exception{
    String location = "dsEmpty";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));


    String result = impl.getDevicesWithServices("", "", "", "");
    String expected = "<response/>";
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expected, result));
    repo.close();
    RepositoryUtil.clean(location);
  }


  @Test 
  public void testGetDevicesWithServicesIDE() throws Exception{
    String location = "dsIDE";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String deviceURI1 = "http://device.uri/1";
    String deviceURI2 = "http://device.uri/2";

    String serviceURI11 = "http://service.uri/11";
    String serviceURI12 = "http://service.uri/12";
    String serviceURI13 = "http://service.uri/13";
    String serviceURI21 = "http://service.uri/21";
    String serviceURI22 = "http://service.uri/22";

    ValueFactory f = new ValueFactoryImpl();

    Graph device1 = GraphLoader.load(
        deviceURI1, 
        GraphData.physicalDevice(deviceURI1, false, deviceURI1+"/template"));
    device1.add(GraphLoader.load(
        "",
        GraphData.pid(deviceURI1, "pid 1")));
    device1.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI1, serviceURI11, Namespace.unit + "Celsius")));
    device1.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI1, serviceURI12, Namespace.unit + "OtherUnit")));
    device1.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI1, serviceURI13, Namespace.unit + "Celsius")));
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

    Graph device2 = GraphLoader.load(
        deviceURI2, 
        GraphData.physicalDevice(deviceURI2, false, deviceURI2+"/template"));
    device2.add(GraphLoader.load(
        "",
        GraphData.pid(deviceURI2, "pid 2")));
    device2.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI2, serviceURI21, Namespace.unit + "OtherUnit")));
    device2.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI2, serviceURI22, Namespace.unit + "Celsius")));

    ResourceUtil.addStatement(
        serviceURI22, 
        Service.matchedService, 
        "true", 
        XMLSchema.BOOLEAN,
        f, 
        device2);

    String serviceQuery = "" +
    "service:hasOutput/service:parameterUnit;unit:Celsius,";

    repo.store(device1);
    repo.store(device2);

    String result = impl.getDevicesWithServices(serviceQuery);
    String expected = DataLoader.xmlToString("test/resources/query/dsQueryResultIDE.xml");
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expected, result));
    repo.close();
    RepositoryUtil.clean(location);
  }


  @Test 
  public void testGetDevicesWithServicesIDEEmptyQuery() throws Exception{
    String location = "dsIDEEmptyQuery";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));


    String result = impl.getDevicesWithServices("");
    String expected = "<results/>";
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expected, result));
    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test 
  public void testGetDevicesIDE() throws Exception{
    String location = "dIDE";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    String deviceURI1 = "http://device.uri/1";
    String deviceURI2 = "http://device.uri/2";

    String serviceURI11 = "http://service.uri/11";
    String serviceURI12 = "http://service.uri/12";
    String serviceURI13 = "http://service.uri/13";
    String serviceURI21 = "http://service.uri/21";
    String serviceURI22 = "http://service.uri/22";

    ValueFactory f = new ValueFactoryImpl();

    Graph device1 = GraphLoader.load(
        deviceURI1, 
        GraphData.physicalDevice(deviceURI1, false, deviceURI1+"/template"));
    device1.add(GraphLoader.load(
        "",
        GraphData.pid(deviceURI1, "pid 1")));
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
        GraphData.hardware(deviceURI1, "screen width", "Px", "resolution")));
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

    Graph device2 = GraphLoader.load(
        deviceURI2, 
        GraphData.physicalDevice(deviceURI2, false, deviceURI2+"/template"));
    device2.add(GraphLoader.load(
        "",
        GraphData.pid(deviceURI2, "pid 2")));
    device2.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI2, serviceURI21, Namespace.unit + "OtherUnit")));
    device2.add(GraphLoader.load(
        "",
        GraphData.service(deviceURI2, serviceURI22, Namespace.unit + "Celsius")));

    ResourceUtil.addStatement(
        serviceURI22, 
        Service.matchedService, 
        "true", 
        XMLSchema.BOOLEAN,
        f, 
        device2);

    String deviceQuery = "" +
    "device:hasService/service:hasOutput/service:parameterUnit;unit:Celsius,";

    repo.store(device1);
    repo.store(device2);
    
    String result = impl.getDevices(deviceQuery);
    String expected = DataLoader.xmlToString("test/resources/query/dsQueryResultIDE.xml");
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expected, result));
    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test 
  public void testGetDevicesIDEEmptyQuery() throws Exception{
    String location = "dIDEEmptyQuery";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    ApplicationOntologyManagerSoapBindingImpl impl = 
      spy(new ApplicationOntologyManagerSoapBindingImpl(repo));

    
    String result = impl.getDevices("");
    String expected = "<results/>";
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expected, result));
    repo.close();
    RepositoryUtil.clean(location);
  }


  
  private Graph deviceStub(String deviceType, int deviceId){
    String deviceURI = "http://device.uri/"+deviceId;
    String configURI = "http://config.uri/"+deviceId;
    Graph d = GraphLoader.load(
        deviceURI, 
        GraphData.device(deviceType, deviceId));

    Graph c = GraphLoader.load(
        deviceURI, 
        GraphData.configContent(deviceURI, configURI, "dev"+deviceId, ""));

    d.add(c);

    return d;
  }
  
}
