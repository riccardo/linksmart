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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;
import org.junit.Test;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;


import eu.linksmart.aom.generator.QueryResponseGenerator;
import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Service;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.testutil.DataComparator;
import eu.linksmart.aom.testutil.DataLoader;


public class QueryResponseGeneratorTest {

  @Test
  public void testDevicesWithServicesResult(){
    QueryResponseGenerator g = spy(new QueryResponseGenerator());

    Graph d1 = mock(Graph.class);
    Graph d2 = mock(Graph.class);
    Set<Graph> devices = new HashSet<Graph>();
    devices.add(d1);
    devices.add(d2);
    try{
      doReturn(null).when(g).deviceResult(any(Graph.class), any(Element.class), any(String.class), any(String.class));
      String expected = "" +
      "<response/>";
      DataComparator dc = new DataComparator();
      Element result = g.devicesWithServicesResult(devices, "", "");
      assertTrue(dc.sameAs(expected, g.toString(result)));
      verify(g, times(2)).deviceResult(any(Graph.class), any(Element.class), any(String.class), any(String.class));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test
  public void testDeviceResultNoRequirements(){
    QueryResponseGenerator g = spy(new QueryResponseGenerator());

    String devRequirements = "";
    String servRequirements = "";

    Graph device = mock(Graph.class);
    doReturn("my-pid").when(device).value(Device.PID);
    doReturn("http://my.device").when(device).getBaseURI();
    Graph s1 = mock(Graph.class);
    Graph s2 = mock(Graph.class);

    Set<Graph> services = new HashSet<Graph>();
    services.add(s1);
    services.add(s2);

    Element response = new Element("response");
    try{
      doReturn(null).when(g).serviceResult(any(Graph.class), any(Element.class), any(String.class));
      doReturn(services).when(device).subGraphs(Device.hasService);

      Element result = g.deviceResult(device, response, devRequirements, servRequirements);
      String expected = "" +
      "<device pid=\"my-pid\" uri=\"http://my.device\">" +
      "<deviceProperties/>" +
      "</device>";
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expected, g.toString(result)));
      verify(g, times(2)).serviceResult(any(Graph.class), any(Element.class), any(String.class));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }


  @Test
  public void testDeviceResultWithRequirements(){
    QueryResponseGenerator g = spy(new QueryResponseGenerator());

    String devRequirements = "some requirements";
    String servRequirements = "";

    try{
      Map<String, Set<String>> requirements = new HashMap<String, Set<String>>();
      requirements.put("prop1", new HashSet<String>(Arrays.asList("prop1_value1", "prop1_value2")));
      requirements.put("prop2", new HashSet<String>(Arrays.asList("prop2_value1", "prop2_value2!unit:someUnit")));
      requirements.put("prop3", new HashSet<String>(Arrays.asList("-!-", "prop3_value1!-")));
      Graph device = mock(Graph.class);
      doReturn("my-pid").when(device).value(Device.PID);
      doReturn("http://my.device").when(device).getBaseURI();
      doReturn(requirements).when(device).getRequirements(devRequirements);
      Graph s1 = mock(Graph.class);
      Graph s2 = mock(Graph.class);

      Set<Graph> services = new HashSet<Graph>();
      services.add(s1);
      services.add(s2);

      Element response = new Element("response");
      doReturn(null).when(g).serviceResult(any(Graph.class), any(Element.class), any(String.class));
      doReturn(services).when(device).subGraphs(Device.hasService);

      Element result = g.deviceResult(device, response, devRequirements, servRequirements);
      String expected = "" +
      "<device pid=\"my-pid\" uri=\"http://my.device\">\n" +
      "  <deviceProperties>\n" +
      "    <property name=\"prop1\">\n" +
      "      <value>prop1_value1</value>\n" +
      "      <value>prop1_value2</value>\n" +
      "    </property>\n" +
      "    <property name=\"prop2\">\n" +
      "      <value>prop2_value1</value>\n" +
      "      <value unit=\"unit:someUnit\">prop2_value2</value>\n" +
      "    </property>\n" +
      "    <property name=\"prop3\">\n" +
      "      <value>prop3_value1</value>\n" +
      "    </property>\n" +
      "  </deviceProperties>\n" + 
      "</device>";
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expected, g.toString(result)));
      verify(g, times(2)).serviceResult(any(Graph.class), any(Element.class), any(String.class));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test
  public void testServiceResultNotMatched(){
    QueryResponseGenerator g = spy(new QueryResponseGenerator());

    String servRequirements = "";

    Graph service = mock(Graph.class);
    doReturn("my-operation").when(service).value(Service.serviceOperation);

    Element device = new Element("device");
    try{
      Element result = g.serviceResult(service, device, servRequirements);
      assertEquals(null, result);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test
  public void testServiceResultNoRequirements(){
    QueryResponseGenerator g = spy(new QueryResponseGenerator());

    String servRequirements = "";

    Graph service = mock(Graph.class);
    doReturn("my-operation").when(service).value(Service.serviceOperation);
    doReturn("any value").when(service).value(Service.matchedService);

    Element device = new Element("device");
    try{

      Element result = g.serviceResult(service, device, servRequirements);
      String expected = "" +
      "<service operation=\"my-operation\">" +
      "  <serviceProperties/>" +
      "</service>";
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expected, g.toString(result)));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test
  public void testServiceResultWithRequirements(){
    QueryResponseGenerator g = spy(new QueryResponseGenerator());

    String servRequirements = "some requirements";

    try{
      Map<String, Set<String>> requirements = new HashMap<String, Set<String>>();
      requirements.put("prop1", new HashSet<String>(Arrays.asList("prop1_value1", "prop1_value2")));
      requirements.put("prop2", new HashSet<String>(Arrays.asList("prop2_value1")));

      Graph service = mock(Graph.class);
      doReturn("my-operation").when(service).value(Service.serviceOperation);
      doReturn("any value").when(service).value(Service.matchedService);
      doReturn(requirements).when(service).getRequirements(servRequirements);

      Element device = new Element("device");

      Element result = g.serviceResult(service, device, servRequirements);
      String expected = "" +
      "<service operation=\"my-operation\">" +
      "  <serviceProperties>" +
      "    <property name=\"prop1\">\n" +
      "      <value>prop1_value1</value>\n" +
      "      <value>prop1_value2</value>\n" +
      "    </property>\n" +
      "    <property name=\"prop2\">\n" +
      "      <value>prop2_value1</value>\n" +
      "    </property>\n" +
      "  </serviceProperties>" +
      "</service>";
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expected, g.toString(result)));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test
  public void testDevicesWithServicesIntegration(){
    
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

    Set<Graph> devices = new HashSet<Graph>(Arrays.asList(device1, device2));

    String deviceRequirements = "" +
    		"rdf:type,\n " +
    		"device:clonedFromTemplate,\n " +
    		"device:hasService/service:serviceOperation";
    String serviceRequirements = "" +
    		"service:hasOutput/service:parameterUnit,\n" +
    		"service:serviceOperation";
    QueryResponseGenerator g = spy(new QueryResponseGenerator());
    try{
      Element result = g.devicesWithServicesResult(devices, deviceRequirements, serviceRequirements);
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(DataLoader.parse("test/resources/query/dsQueryResult1.xml"), result));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  
  @Test
  public void testErrorMessage(){
    
    String msg = "some error";
    QueryResponseGenerator g = spy(new QueryResponseGenerator());
    Element result = g.error(new Exception(msg));
    String expected = "" +
    "<response>" +
    "  <error>"+msg+"</error>" +
    "</response>";
    
  }

  @Test
  public void testDevicesResult(){
    QueryResponseGenerator g = spy(new QueryResponseGenerator());

    Graph d1 = mock(Graph.class);
    Graph d2 = mock(Graph.class);
    Set<Graph> devices = new HashSet<Graph>();
    devices.add(d1);
    devices.add(d2);
    try{
      doReturn(null).when(g).deviceResult(any(Graph.class), any(Element.class), any(String.class));
      String expected = "" +
      "<response/>";
      DataComparator dc = new DataComparator();
      Element result = g.devicesResult(devices, "");
      assertTrue(dc.sameAs(expected, g.toString(result)));
      verify(g, times(2)).deviceResult(any(Graph.class), any(Element.class), any(String.class));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test
  public void testDeviceWithoutServicesResultNoRequirements(){
    QueryResponseGenerator g = spy(new QueryResponseGenerator());

    String devRequirements = "";

    Graph device = mock(Graph.class);
    doReturn("my-pid").when(device).value(Device.PID);
    doReturn("http://my.device").when(device).getBaseURI();

    Element response = new Element("response");
    try{

      Element result = g.deviceResult(device, response, devRequirements);
      String expected = "" +
      "<device pid=\"my-pid\" uri=\"http://my.device\">" +
      "<deviceProperties/>" +
      "</device>";
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expected, g.toString(result)));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test
  public void testDeviceWithoutServicesResultWithRequirements(){
    QueryResponseGenerator g = spy(new QueryResponseGenerator());

    String devRequirements = "some requirements";

    try{
      Map<String, Set<String>> requirements = new HashMap<String, Set<String>>();
      requirements.put("prop1", new HashSet<String>(Arrays.asList("prop1_value1", "prop1_value2")));
      requirements.put("prop2", new HashSet<String>(Arrays.asList("prop2_value1")));
      Graph device = mock(Graph.class);
      doReturn("my-pid").when(device).value(Device.PID);
      doReturn("http://my.device").when(device).getBaseURI();
      doReturn(requirements).when(device).getRequirements(devRequirements);

      Element response = new Element("response");

      Element result = g.deviceResult(device, response, devRequirements);
      String expected = "" +
      "<device pid=\"my-pid\" uri=\"http://my.device\">\n" +
      "  <deviceProperties>\n" +
      "    <property name=\"prop1\">\n" +
      "      <value>prop1_value1</value>\n" +
      "      <value>prop1_value2</value>\n" +
      "    </property>\n" +
      "    <property name=\"prop2\">\n" +
      "      <value>prop2_value1</value>\n" +
      "    </property>\n" +
      "  </deviceProperties>\n" + 
      "</device>";
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expected, g.toString(result)));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test
  public void testDevicesWithoutServicesIntegration(){
    
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

    Set<Graph> devices = new HashSet<Graph>(Arrays.asList(device1, device2));

    String deviceRequirements = "" +
        "rdf:type,\n " +
        "device:clonedFromTemplate,\n " +
        "device:hasService/service:serviceOperation";
    QueryResponseGenerator g = spy(new QueryResponseGenerator());
    try{
      Element result = g.devicesResult(devices, deviceRequirements);
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(DataLoader.parse("test/resources/query/dQueryResult1.xml"), result));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

}
