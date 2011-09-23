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

package eu.linksmart.aom.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;


import eu.linksmart.aom.discovery.SemanticDeviceDiscovery;
import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.testutil.GraphComparator;
import eu.linksmart.aom.testutil.RepositoryUtil;
import eu.linksmart.aom.testutil.StubData;

public class SemanticDeviceDiscoveryTest extends StubData {
  @Test
  public void testGetSemanticDeviceTemplates(){
    String location = "semanticDeviceTemplates";
    AOMRepository repo = RepositoryFactory.local(location);

    String rd1URI = "http://run-time.physical.device";
    String dt1URI = "http://template.physical.device";
    String rsd1URI = "http://run-time.semantic.device";
    String sdt1URI = "http://template.semantic.device/1";
    String sdt2URI = "http://template.semantic.device/2";


    Graph rd1 = GraphLoader.load(rd1URI, GraphData.physicalDevice(rd1URI, false));
    Graph dt1 = GraphLoader.load(dt1URI, GraphData.physicalDevice(dt1URI, true));
    Graph rsd1 = GraphLoader.load(rsd1URI, GraphData.semanticDevice(rsd1URI, false));
    Graph sdt1 = GraphLoader.load(sdt1URI, GraphData.semanticDevice(sdt1URI, true));
    Graph sdt2 = GraphLoader.load(sdt2URI, GraphData.semanticDevice(sdt2URI, true));

    repo.store(rd1);
    repo.store(dt1);
    repo.store(rsd1);
    repo.store(sdt1);
    repo.store(sdt2);

    SemanticDeviceDiscovery disco = new SemanticDeviceDiscovery(repo);
    Set<Graph> templates = disco.getSemanticDeviceTemplates();

    assertEquals(templates.size(), 2);
    assertTrue(templates.contains(sdt1));
    assertTrue(templates.contains(sdt2));

    repo.close();
    RepositoryUtil.clean(location);
  }

  private Graph physicalDevicesQuery(boolean isTemplate, ValueFactory f){
    String queryURI = "http://physical.device.uri";
    Graph query = new Graph(queryURI);
    ResourceUtil.addStatement(
        queryURI, 
        Device.isDeviceTemplate, 
        isTemplate + "", 
        XMLSchema.BOOLEAN, 
        f, 
        query);
    ResourceUtil.addStatement(
        queryURI, 
        Rdf.rdfType, 
        Device.PhysicalDevice.stringValue(), 
        f, 
        query);
    return query;
  }


  @Test
  public void testRemoveRunTimeSemanticDevices(){
    String location = "removeRunTimeSemanticDevices";
    AOMRepository repo = RepositoryFactory.local(location);

    String rd1URI = "http://run-time.physical.device";
    String dt1URI = "http://template.physical.device";
    String rsd1URI = "http://run-time.semantic.device/1";
    String rsd2URI = "http://run-time.semantic.device/2";
    String sdt1URI = "http://template.semantic.device/1";


    Graph rd1 = GraphLoader.load(rd1URI, GraphData.physicalDevice(rd1URI, false));
    Graph dt1 = GraphLoader.load(dt1URI, GraphData.physicalDevice(dt1URI, true));
    Graph rsd1 = GraphLoader.load(rsd1URI, GraphData.semanticDevice(rsd1URI, false));
    Graph rsd2 = GraphLoader.load(rsd2URI, GraphData.semanticDevice(rsd2URI, false));
    Graph sdt1 = GraphLoader.load(sdt1URI, GraphData.semanticDevice(sdt1URI, true));

    repo.store(rd1);
    repo.store(dt1);
    repo.store(rsd1);
    repo.store(rsd2);
    repo.store(sdt1);

    ValueFactory f = repo.getValueFactory();
    SemanticDeviceDiscovery disco = new SemanticDeviceDiscovery(repo);
    Set<Graph> physicalTemplates = repo.getDevices(physicalDevicesQuery(true, f));
    Set<Graph> physicalRuntime = repo.getDevices(physicalDevicesQuery(false, f));
    Set<Graph> semanticTemplates = disco.getSemanticDeviceTemplates();
    Set<Graph> semanticRuntime = repo.getDevices(disco.semanticDevicesQuery(false));

    assertEquals(semanticTemplates.size(), 1);
    assertTrue(semanticTemplates.contains(sdt1));

    assertEquals(semanticRuntime.size(), 2);
    assertTrue(semanticRuntime.contains(rsd1));
    assertTrue(semanticRuntime.contains(rsd2));

    assertEquals(physicalTemplates.size(), 1);
    assertTrue(physicalTemplates.contains(dt1));

    assertEquals(physicalRuntime.size(), 1);
    assertTrue(physicalRuntime.contains(rd1));

    disco.removeRunTimeSemanticDevices();

    physicalTemplates = repo.getDevices(physicalDevicesQuery(true, f));
    physicalRuntime = repo.getDevices(physicalDevicesQuery(false, f));
    semanticTemplates = disco.getSemanticDeviceTemplates();
    semanticRuntime = repo.getDevices(disco.semanticDevicesQuery(false));

    assertEquals(semanticTemplates.size(), 1);
    assertTrue(semanticTemplates.contains(sdt1));

    assertEquals(semanticRuntime.size(), 0);

    assertEquals(physicalTemplates.size(), 1);
    assertTrue(physicalTemplates.contains(dt1));

    assertEquals(physicalRuntime.size(), 1);
    assertTrue(physicalRuntime.contains(rd1));

    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void testClone(){
    AOMRepository repo = repositoryStub();

    String tURI1 = "http://template.uri/1";
    String tURI2 = "http://template.uri/2";
    String tURI3 = "http://template.uri/3";
    Graph t1 = spy(new Graph(tURI1));
    Graph t2 = spy(new Graph(tURI2));
    Graph t3 = spy(new Graph(tURI3));
    Set<Graph> templates = new HashSet<Graph>();
    templates.add(t1);
    templates.add(t2);
    templates.add(t3);

    Graph r1 = mock(Graph.class);
    Graph r2 = mock(Graph.class);
    Graph r3 = mock(Graph.class);
    Set<Graph> rediscovered = new HashSet<Graph>();
    rediscovered.add(r1);
    rediscovered.add(r2);
    rediscovered.add(r3);


    doReturn(r1).when(t1).clone(repo, tURI1);
    doReturn(r2).when(t2).clone(repo, tURI2);
    doReturn(r3).when(t3).clone(repo, tURI3);

    SemanticDeviceDiscovery disco = new SemanticDeviceDiscovery(repo);
    Set<Graph> result = disco.clone(templates);
    assertEquals(rediscovered, result);
  }


  @Test
  public void testGetRediscoveredTemplate(){
    AOMRepository repo = repositoryStub();
    doReturn(new ValueFactoryImpl()).when(repo).getValueFactory();

    String sdt1URI = "http://template.semantic.device/1";
    String sdt2URI = "http://template.semantic.device/2";

    Graph sdt1 = GraphLoader.load(sdt1URI, GraphData.semanticDevice(sdt1URI, true));
    Graph sdt2 = GraphLoader.load(sdt2URI, GraphData.semanticDevice(sdt2URI, true));
    Set<Graph> templates = new HashSet<Graph>();
    templates.add(sdt1);
    templates.add(sdt2);

    Graph c1 = sdt1.clone(repo, sdt1.getBaseURI());
    Graph c2 = sdt2.clone(repo, sdt2.getBaseURI());

    SemanticDeviceDiscovery disco = spy(new SemanticDeviceDiscovery(repo));
    Set<Graph> cloned = disco.clone(templates);

    Graph r1 = disco.getRediscoveredTemplate(sdt1, cloned);
    Graph r2 = disco.getRediscoveredTemplate(sdt2, cloned);

    assertTrue(GraphComparator.sameAs(c1, r1, repo));
    assertFalse(GraphComparator.sameAs(c1, r2, repo));
    assertTrue(GraphComparator.sameAs(c2, r2, repo));
    assertFalse(GraphComparator.sameAs(c2, r1, repo));

  }

  @Test
  public void testIsPhysicalOrSemantic(){
	    String location = "physicalOrSemantic";
	    AOMRepository repo = RepositoryFactory.local(location);

	    repo.store(GraphLoader.load("", GraphData.propertyTaxonomy()));

	    String p1 = Namespace.device + "PhysicalSubDevice";
	    String p2 = "PhysicalSubDevice";

	    String s1 = Namespace.device + "SemanticSubDevice";
	    String s2 = "SemanticSubDevice";
	    
    SemanticDeviceDiscovery disco = new SemanticDeviceDiscovery(repo);

    assertTrue(disco.isPhysicalDevice(p1));
    assertTrue(disco.isPhysicalDevice(p2));
    assertFalse(disco.isSemanticDevice(p1));
    assertFalse(disco.isSemanticDevice(p2));

    assertTrue(disco.isSemanticDevice(s1));
    assertTrue(disco.isSemanticDevice(s2));
    assertFalse(disco.isPhysicalDevice(s1));
    assertFalse(disco.isPhysicalDevice(s2));
    
    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void testSatisfy(){
    Graph precondition = spy(new Graph("http://precondition.uri"));
    doReturn(Device.SemanticDevicePIDPrecondition.stringValue()).when(precondition).value(Rdf.rdfType);

    Graph device = mock(Graph.class);
    Set<Graph> rediscovered = mock(Set.class);
    Set<String> dependency = mock(Set.class);
    SemanticDeviceDiscovery discoPID = spy(new SemanticDeviceDiscovery(null));
    try{
      doReturn(true).when(discoPID).satisfyPID(device, precondition, rediscovered, dependency);
      discoPID.satisfy(device, precondition, rediscovered, dependency);
      verify(discoPID).satisfyPID(device, precondition, rediscovered, dependency);
      verify(discoPID, times(0)).satisfyQuery(device, precondition, rediscovered, dependency);

      SemanticDeviceDiscovery discoQuery = spy(new SemanticDeviceDiscovery(null));
      doReturn(true).when(discoQuery).satisfyQuery(device, precondition, rediscovered, dependency);
      doReturn(Device.SemanticDeviceQueryPrecondition.stringValue()).when(precondition).value(Rdf.rdfType);
      discoQuery.satisfy(device, precondition, rediscovered, dependency);
      verify(discoQuery, times(0)).satisfyPID(device, precondition, rediscovered, dependency);
      verify(discoQuery).satisfyQuery(device, precondition, rediscovered, dependency);
    }
    catch(Exception e){
      e.printStackTrace();
    }

  }

  @Test
  public void testSatisfyPID(){
    Graph precondition = spy(new Graph("http://precondition.uri"));
    String deviceType = "SomeType"; 
    Graph device = mock(Graph.class);
    Set<Graph> rediscovered = mock(Set.class);
    Set<String> dependency = mock(Set.class);

    SemanticDeviceDiscovery physicalPID = spy(new SemanticDeviceDiscovery(null));
    try{
      doReturn(null).when(physicalPID).satisfyPhysicalPID(precondition);
      doReturn(deviceType).when(precondition).value(Device.preconditionDeviceType);
      doReturn(true).when(physicalPID).isPhysicalDevice(deviceType);
      doReturn(false).when(physicalPID).isSemanticDevice(deviceType);

      physicalPID.satisfyPID(device, precondition, rediscovered, dependency);
      verify(physicalPID).satisfyPhysicalPID(precondition);
      verify(physicalPID, times(0)).satisfySemanticPID(device, precondition, rediscovered, dependency);


      SemanticDeviceDiscovery semanticPID = spy(new SemanticDeviceDiscovery(null));
      doReturn(null).when(semanticPID).satisfySemanticPID(device, precondition, rediscovered, dependency);
      doReturn(deviceType).when(precondition).value(Device.preconditionDeviceType);
      doReturn(false).when(semanticPID).isPhysicalDevice(deviceType);
      doReturn(true).when(semanticPID).isSemanticDevice(deviceType);

      semanticPID.satisfyPID(device, precondition, rediscovered, dependency);
      verify(semanticPID, times(0)).satisfyPhysicalPID(precondition);
      verify(semanticPID).satisfySemanticPID(device, precondition, rediscovered, dependency);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }


  @Test
  public void resolveDevicesPIDSetup1() throws Exception {
    String location = "resolveDevicesPIDSetup";
    AOMRepository repo = RepositoryFactory.local(location);

    String p1URI = "http://pid.precondition/1";
    String p2URI = "http://pid.precondition/2";
    String p3URI = "http://pid.precondition/3";

    String pid1 = "my-pid-1";
    String pid2 = "my-pid-2";
    String pid3 = "my-pid-3";
    String pid4 = "my-pid-4";

    String prId1 = "id1";
    String prId2 = "id2";
    String prId3 = "id3";

    String rd1URI = "http://run-time.physical.device/1";
    String rd2URI = "http://run-time.physical.device/2";
    String rd3URI = "http://run-time.physical.device/3";
    String sdt1URI = "http://template.semantic.device/1";
    String sdt2URI = "http://template.semantic.device/2";


    Graph rd1 = GraphLoader.load(rd1URI, GraphData.physicalDevice(rd1URI, false));
    Graph rd2 = GraphLoader.load(rd2URI, GraphData.physicalDevice(rd2URI, false));
    Graph sdt1 = GraphLoader.load(sdt1URI, GraphData.semanticDevice(sdt1URI, true));
    Graph sdt2 = GraphLoader.load(sdt2URI, GraphData.semanticDevice(sdt2URI, true));

    sdt1.add(GraphLoader.load("", GraphData.pid(sdt1URI, pid1)));
    sdt2.add(GraphLoader.load("", GraphData.pid(sdt2URI, pid2)));
    rd1.add(GraphLoader.load("", GraphData.pid(rd1URI, pid3)));
    rd2.add(GraphLoader.load("", GraphData.pid(rd2URI, pid4)));


    /*
     * SETUP:
     * SD1[ PID1 ]: 
     *   Precondition 1: SemanticDevice: PID2
     *   Precondition 2: PhysicalDevice: PID3
     * SD2[ PID2 ]: 
     *   Precondition 3: PhysicalDevice: PID3, PID4
     * RD1[ PID 3] 
     * RD2[ PID 4] 
     */
    Graph p1 = GraphLoader.load(
        p1URI, 
        GraphData.preconditionPID(
            sdt1URI, 
            p1URI, 
            prId1, 
            Device.SemanticDevice.stringValue(), 
            pid2));
    Graph p2 = GraphLoader.load(
        p2URI, 
        GraphData.preconditionPID(
            sdt1URI, 
            p2URI, 
            prId2, 
            Device.PhysicalDevice.stringValue(), 
            pid3));
    sdt1.add(p1);
    sdt1.add(p2);


    Graph p3 = GraphLoader.load(
        p3URI, 
        GraphData.preconditionPID(
            sdt2URI, 
            p3URI, 
            prId3, 
            Device.PhysicalDevice.stringValue(), 
            pid3));
    p3.add(GraphLoader.load(
        "", 
        GraphData.preconditionPID(p3URI, pid4)));
    sdt2.add(p3);

    repo.store(sdt1);
    repo.store(sdt2);
    repo.store(rd1);
    repo.store(rd2);

    SemanticDeviceDiscovery disco = new SemanticDeviceDiscovery(repo);
    disco.resolveDevices();
//    RepositoryUtil.listDevices(repo);

//    System.out.println("FIRING: ");
    Set<Graph> devices = repo.getDevices("rdf:type;device:SemanticDevice,device:isDeviceTemplate;\"false\"^^xsd:boolean");
//    System.out.println("FIRED");
//    System.out.println(devices);
    assertEquals(2, devices.size());
    Graph r1 = deviceByPID(pid1, devices);
    Graph r2 = deviceByPID(pid2, devices);

    Set<String> satisfyingP1 = new HashSet<String>();
    satisfyingP1.add(r2.getBaseURI());

    Set<String> satisfyingP2 = new HashSet<String>();
    satisfyingP2.add(rd1URI);

    Set<String> satisfyingP3 = new HashSet<String>();
    satisfyingP3.add(rd1URI);
    satisfyingP3.add(rd2URI);

    assertEquals(
        satisfyingP1, 
        preconditionByID(
            prId1,
            r1.subGraphs(Device.hasPrecondition)).values(Device.isSatisfiedBy));
    assertEquals(
        satisfyingP2, 
        preconditionByID(
            prId2,
            r1.subGraphs(Device.hasPrecondition)).values(Device.isSatisfiedBy));
    assertEquals(
        satisfyingP3, 
        preconditionByID(
            prId3,
            r2.subGraphs(Device.hasPrecondition)).values(Device.isSatisfiedBy));

    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void resolveDevicesQuerySetup1() throws Exception {
    String location = "resolveDevicesQuerySetup1";
    AOMRepository repo = RepositoryFactory.local(location);

    String p1URI = "http://pid.precondition/1";
    String p2URI = "http://pid.precondition/2";
    String p3URI = "http://pid.precondition/3";

    String pid1 = "my-pid-1";
    String pid2 = "my-pid-2";
    String pid3 = "my-pid-3";
    String pid4 = "my-pid-4";

    String prId1 = "id1";
    String prId2 = "id2";
    String prId3 = "id3";

    String rd1URI = "http://run-time.physical.device/1";
    String rd2URI = "http://run-time.physical.device/2";
    String rd3URI = "http://run-time.physical.device/3";
    String sdt1URI = "http://template.semantic.device/1";
    String sdt2URI = "http://template.semantic.device/2";


    Graph rd1 = GraphLoader.load(rd1URI, GraphData.physicalDevice(rd1URI, false));
    Graph rd2 = GraphLoader.load(rd2URI, GraphData.physicalDevice(rd2URI, false));
    Graph sdt1 = GraphLoader.load(sdt1URI, GraphData.semanticDevice(sdt1URI, true));
    Graph sdt2 = GraphLoader.load(sdt2URI, GraphData.semanticDevice(sdt2URI, true));

    sdt1.add(GraphLoader.load("", GraphData.pid(sdt1URI, pid1)));
    sdt2.add(GraphLoader.load("", GraphData.pid(sdt2URI, pid2)));
    rd1.add(GraphLoader.load("", GraphData.pid(rd1URI, pid3)));
    rd2.add(GraphLoader.load("", GraphData.pid(rd2URI, pid4)));


    /*
     * SETUP:
     * SD1[ PID1 ]: 
     *   Precondition 1: SemanticDevice: Q[PID;"my-pid-2"^^xsdd:string], C[1..M]
     *   Precondition 2: PhysicalDevice: Q[PID;"my-pid-3"^^xsdd:string], C[1]
     * SD2[ PID2 ]: 
     *   Precondition 3: PhysicalDevice: Q[PID], C[2]
     * RD1[ PID 3] 
     * RD2[ PID 4] 
     */
    Graph p1 = GraphLoader.load(
        p1URI, 
        GraphData.preconditionQuery(
            sdt1URI, 
            p1URI, 
            prId1, 
            Device.SemanticDevice.stringValue(), 
            "1..M",
        "device:PID;\\\"my-pid-2\\\"^^xsd:string"));
    Graph p2 = GraphLoader.load(
        p2URI, 
        GraphData.preconditionQuery(
            sdt1URI, 
            p2URI, 
            prId2, 
            Device.PhysicalDevice.stringValue(), 
            "1",
        "device:PID;\\\"my-pid-3\\\"^^xsd:string"));
    sdt1.add(p1);
    sdt1.add(p2);


    Graph p3 = GraphLoader.load(
        p3URI, 
        GraphData.preconditionQuery(
            sdt2URI, 
            p3URI, 
            prId3, 
            Device.PhysicalDevice.stringValue(), 
            "1..M",
        "device:PID"));
    sdt2.add(p3);

    repo.store(sdt1);
    repo.store(sdt2);
    repo.store(rd1);
    repo.store(rd2);

    SemanticDeviceDiscovery disco = new SemanticDeviceDiscovery(repo);
    disco.resolveDevices();
//    RepositoryUtil.listDevices(repo);

    Set<Graph> devices = repo.getDevices("rdf:type;device:SemanticDevice,device:isDeviceTemplate;\"false\"^^xsd:boolean");
//    System.out.println(devices);
    assertEquals(2, devices.size());
    Graph r1 = deviceByPID(pid1, devices);
    Graph r2 = deviceByPID(pid2, devices);

    /*
     * SETUP:
     * SD1[ PID1 ]: 
     *   Precondition 1: SemanticDevice: Q[PID;"my-pid-2"^^xsdd:string], C[1..M]
     *   Precondition 2: PhysicalDevice: Q[PID;"my-pid-3"^^xsdd:string], C[1]
     * SD2[ PID2 ]: 
     *   Precondition 3: PhysicalDevice: Q[PID], C[2]
     * RD1[ PID 3] 
     * RD2[ PID 4] 
     */
    Set<String> satisfyingP1 = new HashSet<String>();
    satisfyingP1.add(r2.getBaseURI());

    Set<String> satisfyingP2 = new HashSet<String>();
    satisfyingP2.add(rd1URI);

    Set<String> satisfyingP3 = new HashSet<String>();
    satisfyingP3.add(rd1URI);
    satisfyingP3.add(rd2URI);

    assertEquals(
        satisfyingP1, 
        preconditionByID(
            prId1,
            r1.subGraphs(Device.hasPrecondition)).values(Device.isSatisfiedBy));
    assertEquals(
        satisfyingP2, 
        preconditionByID(
            prId2,
            r1.subGraphs(Device.hasPrecondition)).values(Device.isSatisfiedBy));
    assertEquals(
        satisfyingP3, 
        preconditionByID(
            prId3,
            r2.subGraphs(Device.hasPrecondition)).values(Device.isSatisfiedBy));


    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void resolveDevicesQuerySetup2() throws Exception {
    String location = "resolveDevicesQuerySetup2";
    AOMRepository repo = RepositoryFactory.local(location);

    String p1URI = "http://pid.precondition/1";
    String p2URI = "http://pid.precondition/2";
    String p3URI = "http://pid.precondition/3";
    String p4URI = "http://pid.precondition/4";
    String p5URI = "http://pid.precondition/5";

    String pid1 = "my-pid-1";
    String pid2 = "my-pid-2";
    String pid3 = "my-pid-3";
    String pid4 = "my-pid-4";
    String pid5 = "my-pid-5";

    String prId1 = "id1";
    String prId2 = "id2";
    String prId3 = "id3";
    String prId4 = "id4";
    String prId5 = "id5";

    String rd1URI = "http://run-time.physical.device/1";
    String rd2URI = "http://run-time.physical.device/2";
    String rd3URI = "http://run-time.physical.device/3";
    String sdt1URI = "http://template.semantic.device/1";
    String sdt2URI = "http://template.semantic.device/2";
    String sdt3URI = "http://template.semantic.device/3";


    Graph rd1 = GraphLoader.load(rd1URI, GraphData.physicalDevice(rd1URI, false));
    Graph rd2 = GraphLoader.load(rd2URI, GraphData.physicalDevice(rd2URI, false));
    Graph sdt1 = GraphLoader.load(sdt1URI, GraphData.semanticDevice(sdt1URI, true));
    Graph sdt2 = GraphLoader.load(sdt2URI, GraphData.semanticDevice(sdt2URI, true));
    Graph sdt3 = GraphLoader.load(sdt3URI, GraphData.semanticDevice(sdt3URI, true));

    sdt1.add(GraphLoader.load("", GraphData.pid(sdt1URI, pid1)));
    sdt2.add(GraphLoader.load("", GraphData.pid(sdt2URI, pid2)));
    sdt3.add(GraphLoader.load("", GraphData.pid(sdt3URI, pid5)));
    rd1.add(GraphLoader.load("", GraphData.pid(rd1URI, pid3)));
    rd2.add(GraphLoader.load("", GraphData.pid(rd2URI, pid4)));


    /*
     * SETUP:
     * SD1[ PID1 ]: 
     *   Precondition 1: SemanticDevice: Q[PID;"my-pid-2"^^xsdd:string], C[1..M]
     *   Precondition 2: PhysicalDevice: Q[PID;"my-pid-3"^^xsdd:string], C[1]
     * SD2[ PID2 ]: 
     *   Precondition 3: SemanticDevice: Q[PID;"my-pid-1"^^xsd:string], C[1..M]
     *   Precondition 4: PhysicalDevice: Q[PID], C[2]
     * SD3[ PID5 ]: 
     *   Precondition 5: PhysicalDevice: PID3, PID4
     * RD1[ PID 3] 
     * RD2[ PID 4] 
     */
    Graph p1 = GraphLoader.load(
        p1URI, 
        GraphData.preconditionQuery(
            sdt1URI, 
            p1URI, 
            prId1, 
            Device.SemanticDevice.stringValue(), 
            "1..M",
        "device:PID;\\\"my-pid-2\\\"^^xsd:string"));
    Graph p2 = GraphLoader.load(
        p2URI, 
        GraphData.preconditionQuery(
            sdt1URI, 
            p2URI, 
            prId2, 
            Device.PhysicalDevice.stringValue(), 
            "1",
        "device:PID;\\\"my-pid-3\\\"^^xsd:string"));
    sdt1.add(p1);
    sdt1.add(p2);


    Graph p3 = GraphLoader.load(
        p3URI, 
        GraphData.preconditionQuery(
            sdt2URI, 
            p3URI, 
            prId3, 
            Device.SemanticDevice.stringValue(), 
            "1..M",
        "device:PID;\\\"my-pid-1\\\"^^xsd:string"));
    Graph p4 = GraphLoader.load(
        p4URI, 
        GraphData.preconditionQuery(
            sdt2URI, 
            p4URI, 
            prId4, 
            Device.PhysicalDevice.stringValue(), 
            "1..M",
        "device:PID"));
    sdt2.add(p3);
    sdt2.add(p4);

    Graph p5 = GraphLoader.load(
        p5URI, 
        GraphData.preconditionQuery(
            sdt3URI, 
            p5URI, 
            prId5, 
            Device.PhysicalDevice.stringValue(), 
            "1..M",
        "device:PID"));
    sdt3.add(p5);
    
    
//    System.out.println("SD 1: \n"+sdt1.describe());
//    System.out.println("SD 2: \n"+sdt2.describe());
    repo.store(sdt1);
    repo.store(sdt2);
    repo.store(sdt3);
    repo.store(rd1);
    repo.store(rd2);
//    RepositoryUtil.listDevices(repo);

    SemanticDeviceDiscovery disco = spy(new SemanticDeviceDiscovery(repo));
    disco.resolveDevices();
//    RepositoryUtil.listDevices(repo);

    Set<Graph> devices = repo.getDevices("rdf:type;device:SemanticDevice,device:isDeviceTemplate;\"false\"^^xsd:boolean");
//    System.out.println(devices);
    assertEquals(1, devices.size());
    Graph r1 = deviceByPID(pid5, devices);

    /*
     * SETUP:
     * SD1[ PID1 ]: 
     *   Precondition 1: SemanticDevice: Q[PID;"my-pid-2"^^xsdd:string], C[1..M]
     *   Precondition 2: PhysicalDevice: Q[PID;"my-pid-3"^^xsdd:string], C[1]
     * SD2[ PID2 ]: 
     *   Precondition 3: PhysicalDevice: Q[PID], C[2]
     * RD1[ PID 3] 
     * RD2[ PID 4] 
     */
    Set<String> satisfyingP5 = new HashSet<String>();
    satisfyingP5.add(rd1URI);
    satisfyingP5.add(rd2URI);

    assertEquals(
        satisfyingP5, 
        preconditionByID(
            prId5,
            r1.subGraphs(Device.hasPrecondition)).values(Device.isSatisfiedBy));

    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void resolveDevicesCycleSetup1() throws Exception {
    String location = "resolveDevicesCycleSetup1";
    AOMRepository repo = RepositoryFactory.local(location);

    String p1URI = "http://pid.precondition/1";
    String p2URI = "http://pid.precondition/2";
    String p3URI = "http://pid.precondition/3";
    String p4URI = "http://pid.precondition/4";
    String p5URI = "http://pid.precondition/4";

    String pid1 = "my-pid-1";
    String pid2 = "my-pid-2";
    String pid3 = "my-pid-3";
    String pid4 = "my-pid-4";
    String pid5 = "my-pid-5";

    String prId1 = "id1";
    String prId2 = "id2";
    String prId3 = "id3";
    String prId4 = "id4";
    String prId5 = "id5";

    String rd1URI = "http://run-time.physical.device/1";
    String sdt1URI = "http://template.semantic.device/1";
    String sdt2URI = "http://template.semantic.device/2";
    String sdt3URI = "http://template.semantic.device/3";
    String sdt4URI = "http://template.semantic.device/4";


    Graph rd1 = GraphLoader.load(rd1URI, GraphData.physicalDevice(rd1URI, false));
    Graph sdt1 = GraphLoader.load(sdt1URI, GraphData.semanticDevice(sdt1URI, true));
    Graph sdt2 = GraphLoader.load(sdt2URI, GraphData.semanticDevice(sdt2URI, true));
    Graph sdt3 = GraphLoader.load(sdt3URI, GraphData.semanticDevice(sdt3URI, true));
    Graph sdt4 = GraphLoader.load(sdt4URI, GraphData.semanticDevice(sdt4URI, true));

    sdt1.add(GraphLoader.load("", GraphData.pid(sdt1URI, pid1)));
    sdt2.add(GraphLoader.load("", GraphData.pid(sdt2URI, pid2)));
    sdt3.add(GraphLoader.load("", GraphData.pid(sdt3URI, pid3)));
    sdt4.add(GraphLoader.load("", GraphData.pid(sdt4URI, pid5)));
    rd1.add(GraphLoader.load("", GraphData.pid(rd1URI, pid4)));


    /*
     * SETUP:
     * SD1[ PID1 ]: 
     *   Precondition 1: SemanticDevice: PID2
     * SD2[ PID2 ]: 
     *   Precondition 2: SemanticDevice: PID3
     * SD3[ PID3 ]: 
     *   Precondition 3: SemanticDevice: PID1
     *   Precondition 4: PhysicalDevice: PID4
     * SD4[ PID5 ]: 
     *   Precondition 5: PhysicalDevice: PID4
     * RD1[ PID 4] 
     */
    Graph p1 = GraphLoader.load(
        p1URI, 
        GraphData.preconditionPID(
            sdt1URI, 
            p1URI, 
            prId1, 
            Device.SemanticDevice.stringValue(), 
            pid2));
    sdt1.add(p1);
    
    Graph p2 = GraphLoader.load(
        p2URI, 
        GraphData.preconditionPID(
            sdt2URI, 
            p2URI, 
            prId2, 
            Device.SemanticDevice.stringValue(), 
            pid3));
    sdt2.add(p2);

    Graph p3 = GraphLoader.load(
        p3URI, 
        GraphData.preconditionPID(
            sdt3URI, 
            p3URI, 
            prId3, 
            Device.SemanticDevice.stringValue(), 
            pid1));
    Graph p4 = GraphLoader.load(
        p4URI, 
        GraphData.preconditionPID(
            sdt3URI, 
            p4URI, 
            prId4, 
            Device.PhysicalDevice.stringValue(), 
            pid4));
    sdt3.add(p3);
    sdt3.add(p4);
    
    Graph p5 = GraphLoader.load(
        p5URI, 
        GraphData.preconditionPID(
            sdt4URI, 
            p5URI, 
            prId5, 
            Device.PhysicalDevice.stringValue(), 
            pid4));
    sdt4.add(p5);

    repo.store(sdt1);
    repo.store(sdt2);
    repo.store(sdt3);
    repo.store(sdt4);
    repo.store(rd1);

    SemanticDeviceDiscovery disco = spy(new SemanticDeviceDiscovery(repo));
    disco.resolveDevices();
//    RepositoryUtil.listDevices(repo);

    Set<Graph> devices = repo.getDevices("rdf:type;device:SemanticDevice,device:isDeviceTemplate;\"false\"^^xsd:boolean");
//    System.out.println(devices);
    assertEquals(1, devices.size());
    Graph r1 = deviceByPID(pid5, devices);

    Set<String> satisfyingP5 = new HashSet<String>();
    satisfyingP5.add(rd1URI);

    assertEquals(
        satisfyingP5, 
        preconditionByID(
            prId5,
            r1.subGraphs(Device.hasPrecondition)).values(Device.isSatisfiedBy));

    repo.close();
    RepositoryUtil.clean(location);
  }

  private Graph deviceByPID(String pid, Set<Graph> devices){
    for(Graph d : devices){
      if(pid.equals(d.value(Device.PID))) return d;
    }
    return null;
  }
  private Graph preconditionByID(String id, Set<Graph> preconditions){
    for(Graph p : preconditions){
      if(id.equals(p.value(Device.preconditionId))) return p;
    }
    return null;
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

}
