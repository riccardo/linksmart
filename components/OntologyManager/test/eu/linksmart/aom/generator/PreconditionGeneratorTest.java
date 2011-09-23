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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

import org.jdom.Element;
import org.junit.Test;
import org.openrdf.model.impl.ValueFactoryImpl;


import eu.linksmart.aom.generator.PreconditionGenerator;
import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.testutil.DataComparator;

public class PreconditionGeneratorTest {

  @Test
  public void testProcess(){
    PreconditionGenerator g = spy(new PreconditionGenerator());

    Graph p1 = mock(Graph.class);
    Graph p2 = mock(Graph.class);
    Set<Graph> preconditions = new HashSet<Graph>();
    preconditions.add(p1);
    preconditions.add(p2);
    try{
      doReturn(null).when(g).processPrecondition(any(Graph.class), any(Element.class));
      String expected = "" +
      "<deviceList/>";
      DataComparator dc = new DataComparator();
      Element result = g.process(preconditions, new Element("myDevice"));
      assertTrue(dc.sameAs(expected, g.toString(result)));
      verify(g, times(2)).processPrecondition(any(Graph.class), any(Element.class));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  
  public void addIsSatisfiedBy(Graph precondition, String deviceURI){
    ResourceUtil.addStatement(
        precondition.getBaseURI(), 
        Device.isSatisfiedBy, 
        deviceURI, 
        new ValueFactoryImpl(), 
        precondition);
  }
  @Test
  public void testProcessPIDPrecondition(){
    PreconditionGenerator g = new PreconditionGenerator();


    String deviceURI = "http://device.uri";
    Graph devicePID = GraphLoader.load(
        deviceURI, 
        GraphData.preconditionPID(
            deviceURI, 
            "http://pid.precondition.uri", 
            "pid-id", 
            "SomeDeviceType", 
            "PID1"));
    Graph pid = devicePID.subGraph(Device.hasPrecondition);
    pid.add(GraphLoader.load(
        "", 
        GraphData.preconditionPID(
            "http://pid.precondition.uri", 
            "PID2")));
    addIsSatisfiedBy(pid, "http://my-device/1");
    addIsSatisfiedBy(pid, "http://my-device/2");
    Element response = new Element("deviceList");
    try{

      Element resultPID = g.processPrecondition(pid, response);
      String expectedPID = "" +
      "<precondition id=\"pid-id\" \n" +
      "              PID=\"PID1;PID2\" \n" +
      "              deviceType=\"SomeDeviceType\">\n" +
      "  <satisfyingDevices>\n"+
      "    <deviceURI>http://my-device/1</deviceURI>\n"+
      "    <deviceURI>http://my-device/2</deviceURI>\n"+
      "  </satisfyingDevices>\n"+
      "</precondition>";
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expectedPID, g.toString(resultPID)));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test
  public void testProcessQueryPrecondition(){
    PreconditionGenerator g = new PreconditionGenerator();


    String deviceURI = "http://device.uri";
    Graph deviceQ = GraphLoader.load(
        deviceURI, 
        GraphData.preconditionQuery(
            deviceURI, 
            "http://pid.precondition.uri", 
            "pid-id", 
            "SomeDeviceType", 
            "1..M",
            "my query"));
    Graph query = deviceQ.subGraph(Device.hasPrecondition);
    addIsSatisfiedBy(query, "http://my-device/1");
    addIsSatisfiedBy(query, "http://my-device/2");
    Element response = new Element("deviceList");
    try{

      Element resultQuery = g.processPrecondition(query, response);
      String expectedQuery = "" +
      "<precondition id=\"pid-id\" \n" +
      "              cardinality=\"1..M\" \n" +
      "              query=\"my query\" \n" +
      "              deviceType=\"SomeDeviceType\">\n"+
      "  <satisfyingDevices>\n"+
      "    <deviceURI>http://my-device/1</deviceURI>\n"+
      "    <deviceURI>http://my-device/2</deviceURI>\n"+
      "  </satisfyingDevices>\n"+
      "</precondition>";
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expectedQuery, g.toString(resultQuery)));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }


}
