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

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jdom.Element;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;


import eu.linksmart.aom.discovery.DeviceDiscovery;
import eu.linksmart.aom.generator.EventGenerator;
import eu.linksmart.aom.generator.SCPDGenerator;
import eu.linksmart.aom.generator.ServiceGenerator;
import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Event;
import eu.linksmart.aom.ontology.model.Rdfs;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.processor.DiscoveryProcessor;
import eu.linksmart.aom.processor.EnergyProfileProcessor;
import eu.linksmart.aom.processor.EventProcessor;
import eu.linksmart.aom.processor.Processor;
import eu.linksmart.aom.processor.SCPDProcessor;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.testutil.DataComparator;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.RepositoryUtil;
import eu.linksmart.aom.testutil.StubData;

public class SCPDGeneratorTest extends StubData {

  @Test
  public void testProcess(){
    String deviceURI = "http://device.uri";
    String ns = Processor.SCPD_DEVICE_NS;
    Graph g = GraphLoader.load(deviceURI, GraphData.physicalDevice(deviceURI, false));
    g.add(GraphLoader.load(deviceURI, GraphData.pid(deviceURI, "some pid")));
    ResourceUtil.addStatement(
    		deviceURI, 
    		Device.deviceUPnPType, 
    		"some upnp",
    		XMLSchema.STRING,
    		new ValueFactoryImpl(), 
    		g);
    
    SCPDGenerator scpdg = spy(new SCPDGenerator());
    doReturn(null).when(scpdg).processInfo(any(Graph.class), any(Element.class));
    doReturn(null).when(scpdg).processServices(any(Graph.class), any(Element.class));
    doReturn(null).when(scpdg).processEvents(any(Graph.class), any(Element.class));
    doReturn(null).when(scpdg).processEnergyProfile(any(Graph.class), any(Element.class));
    doReturn(null).when(scpdg).processPreconditions(any(Graph.class), any(Element.class));
    Element rootElm = new Element("root", ns);
    Element deviceElm = new Element("device", ns);
    rootElm.addContent(deviceElm);
    doReturn(rootElm).when(scpdg).element(anyString(), anyString());
    doReturn(deviceElm).when(scpdg).addElement(anyString(), any(Element.class));
    String scpd =  
    "<root xmlns='"+ns+"'>" + 
    "  <device>" + 
    "    <deviceURI>"+deviceURI+"</deviceURI>" + 
    "    <deviceType>some upnp</deviceType>" + 
    "    <deviceClass xmlns=\"hydra\">PhysicalDevice</deviceClass>" + 
    "    <PID>some pid</PID>" + 
    "  </device>" + 
    "</root>";
    
    DataComparator dc = new DataComparator();


    Element expected = dc.readXML(scpd);
    Element result = scpdg.process(g);
    
//    System.out.println("EXP :: " + scpdg.toString(expected));
//    System.out.println(g.describe());
//    System.out.println("RES :: " + scpdg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
    
    InOrder inOrder = Mockito.inOrder(scpdg);

    inOrder.verify(scpdg).processInfo(g.subGraph(Device.info), deviceElm);
    inOrder.verify(scpdg).processServices(g, deviceElm);
    inOrder.verify(scpdg).processEvents(g, deviceElm);
    inOrder.verify(scpdg).processEnergyProfile(g, deviceElm);
  }

  @Test
  public void testProcessInfo(){
    String deviceURI = "http://device.uri";
    String infoURI = "http://info.uri";
    String ns = "some_ns";
    Graph infoGraph = GraphLoader.load(deviceURI, GraphData.scpdManufacturer(deviceURI, infoURI)).subGraph(Device.info);
    
    SCPDGenerator scpdg = spy(new SCPDGenerator());
    
    String scpd =  
      "<device xmlns='"+ns+"'>" + 
      "  <friendlyName>Basic Phone</friendlyName>" + 
      "  <manufacturer>CNet</manufacturer>" + 
      "  <manufacturerURL>http://www.cnet.se</manufacturerURL>" + 
      "  <modelDescription>Basic Phone</modelDescription>" + 
      "  <modelName>Z600</modelName>" + 
      "  <modelNumber>1</modelNumber>" + 
      "</device>";
    
    DataComparator dc = new DataComparator();
    
    Element superElm = spy(scpdg.element("device", ns));
    Element expected = dc.readXML(scpd);
    Element result = scpdg.processInfo(infoGraph, superElm);
    
//    System.out.println("EXP :: " + scpdg.toString(expected));
//    System.out.println("RES :: " + scpdg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
    
    verify(superElm,times(6)).addContent(any(Element.class));
  }

  @Test
  public void testProcessServices(){
    String ns = "some_ns";
    Graph deviceGraph = GraphLoader.load(GraphData.deviceURI, GraphData.device);
    
    SCPDGenerator scpdg = spy(new SCPDGenerator());
    
    String scpd =  
      "<device xmlns='"+ns+"'>" + 
      "  <serviceList>" + 
      "    <service>" + 
      "      <SCPDURL>" + 
      "        <scpd xmlns='"+Processor.SCPD_SERVICE_NS+"'>" + 
      "          <actionList/>" + 
      "        </scpd>" + 
      "      </SCPDURL>" + 
      "    </service>" + 
      "  </serviceList>" + 
      "</device>";
    
    DataComparator dc = new DataComparator();
    
    Element superElm = spy(scpdg.element("device", ns));
    Element expected = dc.readXML(scpd);
    ServiceGenerator sg = spy(new ServiceGenerator());
    doReturn(null).when(sg).process(any(Graph.class), any(Element.class));
    doReturn(null).when(sg).stateTable(any(Element.class));
    Element result = scpdg.processServices(deviceGraph, superElm, sg);
    
//    System.out.println("EXP :: " + scpdg.toString(expected));
//    System.out.println("RES :: " + scpdg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
    verify(sg, times(2)).process(any(Graph.class), any(Element.class));
    verify(sg).stateTable(any(Element.class));
    
  }

  @Test
  public void testProcessEvents(){
    String deviceURI = "http://device.uri";
    String eventURI = "http://event.uri";
    String ns = "some_ns";
    Graph deviceGraph = GraphLoader.load(deviceURI, GraphData.event(deviceURI, eventURI, Event.Event.stringValue()));
    
    SCPDGenerator scpdg = spy(new SCPDGenerator());
    
    String scpd =  
      "<device xmlns='"+ns+"'>" + 
      "  <eventList/>" + 
      "</device>";
    
    DataComparator dc = new DataComparator();
    
    Element superElm = spy(scpdg.element("device", ns));
    Element expected = dc.readXML(scpd);
    EventGenerator eg = spy(new EventGenerator());
    doReturn(null).when(eg).process(any(Graph.class), any(Element.class));
    Element result = scpdg.processEvents(deviceGraph, superElm, eg);
    
//    System.out.println("EXP :: " + scpdg.toString(expected));
//    System.out.println("RES :: " + scpdg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
    
  }
  
  @Test
  public void testCompleteSCPDGenerating(){
    String location = "scpdgenerator";
    AOMRepository repo = initRepo(location);
    ValueFactory vf = repo.getValueFactory();
    Graph g = new Graph("");
    g.add(vf.createStatement(Device.PhysicalDevice, Rdfs.subClassOf, Device.Device));
    repo.store(g);
    
//    RepositoryUtil.listDevices(repo);
//    System.out.println("SIZE:: "+RepositoryUtil.repositorySize(repo));
    
    Set<Graph> devices = new HashSet<Graph>();
    try{
      devices = repo.getDevices("rdf:type;device:Device");
    }
    catch(Exception e){}
    
    SCPDGenerator scpdGenerator = new SCPDGenerator();
    for(Graph device: devices){
      Element parsedDevice = scpdGenerator.process(device);
//      System.out.println("DEVICE :: " + device);
//      System.out.println("GENERATED XML :: " + scpdGenerator.toString(parsedDevice));
    }

    shutDownRepo(repo, location);
  }
  
  @Test
  public void testError(){
    AOMRepository repo = repositoryStub();
    doReturn(new ValueFactoryImpl()).when(repo).getValueFactory();
    Set<Graph> matched = new HashSet<Graph>(
        Arrays.asList(
            new Graph("http://my.template/1"), 
            new Graph("http://my.template/2")));
    DeviceDiscovery d = new DeviceDiscovery(repo);
    Graph errorMultiMatch = d.errorGraph("multiple matches found", matched);
    Graph errorNoMatch = d.errorGraph("no matches found");

    SCPDGenerator g = new SCPDGenerator();
    String expectedMulti = "" +
    "<error message=\"multiple matches found\">\n"+
    "  <matchedTemplateURI>http://my.template/1</matchedTemplateURI>\n"+
    "  <matchedTemplateURI>http://my.template/2</matchedTemplateURI>\n"+
    "</error>";
    String expectedNo = "" +
    "<error message=\"no matches found\"/>";
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expectedMulti, g.toString(g.error(errorMultiMatch))));
    assertTrue(dc.sameAs(expectedNo, g.toString(g.error(errorNoMatch))));
  }
  
  public void shutDownRepo(AOMRepository repo, String location){
    repo.close();
    RepositoryUtil.clean(location);
  }
  public AOMRepository initRepo(String location){
    AOMRepository repo = RepositoryFactory.local(location);
    
    SCPDProcessor sp = new SCPDProcessor(repo);
    EventProcessor evp = new EventProcessor(repo);
    EnergyProfileProcessor enp = new EnergyProfileProcessor(repo);
    DiscoveryProcessor dp = new DiscoveryProcessor(repo);

    Graph device = sp.process(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));
    String deviceURI = device.getBaseURI();
    Graph events = evp.process(deviceURI, DataLoader.xmlToString("test/resources/event/events1.xml"));
    Graph energy = enp.process(deviceURI, DataLoader.xmlToString("test/resources/energy/energy.xml"));
    String discoXML = DataLoader.xmlToString("test/resources/discovery/bluetooth.xml");        
    Graph disco = dp.process(deviceURI, discoXML);
    
    device.add(events);
    device.add(energy);
    device.add(disco);
    repo.store(device);

    return repo;
  }
}
