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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.jdom.Element;
import org.junit.Test;


import eu.linksmart.aom.generator.EventGenerator;
import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Event;
import eu.linksmart.aom.testutil.DataComparator;

public class EventGeneratorTest {
  @Test
  public void testProcess(){
    String deviceURI = "http://device.uri";
    String eventURI = "http://event.uri";
    String ns = "some_ns";
    Graph g = GraphLoader.load(deviceURI, GraphData.eventWithKey(deviceURI, eventURI, Event.Event.stringValue(), "myTopic")).
    subGraph(Device.hasEvent);
    
    EventGenerator eg = spy(new EventGenerator());
    doReturn(null).when(eg).processMetaInformation(any(Graph.class), any(Element.class));
    doReturn(null).when(eg).processEventKey(any(Graph.class), any(Element.class));
    
    String event =  
    "<event xmlns='"+ns+"'>" + 
    "  <eventType>Event</eventType>" + 
    "  <eventTopic>myTopic</eventTopic>" + 
    "  <eventKeyList/>" + 
    "</event>";
    
    DataComparator dc = new DataComparator();
    Element expected = dc.readXML(event);
    
    Element superElm = eg.element("superElem", ns);
    Element result = eg.process(g, superElm);
    
//    System.out.println("EXP :: " + eg.toString(expected));
//    System.out.println("RES :: " + eg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
    verify(eg).processMetaInformation(any(Graph.class), any(Element.class));
    verify(eg).processEventKey(any(Graph.class), any(Element.class));
  }
  @Test
  public void testProcessMetaInformation(){
    String metaInformationURI = "http://metainformation.uri";
    String frequencyURI = "http://frequency.uri";
    String eventURI = "http://event.uri";
    String ns = "some_ns";
    Graph g = GraphLoader.load(eventURI, GraphData.metaInformation(eventURI, metaInformationURI, frequencyURI)).subGraph(Event.hasMetaInformation);
    
    EventGenerator eg = spy(new EventGenerator());
    
    String metaInformation =  
    "<metaInformation xmlns='"+ns+"'>" + 
    "  <frequency>" + 
    "    <value>10</value>" + 
    "    <unit>Seconds</unit>" + 
    "  </frequency>" + 
    "  <eventTrigger>TemperatureChange</eventTrigger>" + 
    "  <description>Reports change on temperature</description>" + 
    "</metaInformation>";

    DataComparator dc = new DataComparator();
    Element expected = dc.readXML(metaInformation);
    
    Element superElm = spy(eg.element("superElem", ns));
    Element result = eg.processMetaInformation(g, superElm);
    
//    System.out.println("EXP :: " + eg.toString(expected));
//    System.out.println("RES :: " + eg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
    verify(superElm).addContent(result);
  }

  @Test
  public void testProcessEventKey1(){
    String eventKeyURI = "http://eventkey.uri";
    String eventURI = "http://event.uri";
    String ns = "some_ns";
    Graph g = GraphLoader.load(eventURI, GraphData.eventKey1(eventURI, eventKeyURI)).subGraph(Event.hasKey);
    
    EventGenerator eg = spy(new EventGenerator());
    
    String metaInformation =  
      "<eventKey xmlns='"+ns+"'>" + 
      "  <name>HID</name>" + 
      "  <dataType>String</dataType>" + 
      "</eventKey>";
    
    DataComparator dc = new DataComparator();
    Element expected = dc.readXML(metaInformation);
    
    Element superElm = spy(eg.element("superElem", ns));
    Element result = eg.processEventKey(g, superElm);
    
//    System.out.println("EXP :: " + eg.toString(expected));
//    System.out.println("RES :: " + eg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
    verify(superElm).addContent(result);
  }
  
  @Test
  public void testProcessEventKey2(){
    String eventKeyURI = "http://eventkey.uri";
    String eventURI = "http://event.uri";
    String ns = "some_ns";
    Graph g = GraphLoader.load(eventURI, GraphData.eventKey2(eventURI, eventKeyURI)).subGraph(Event.hasKey);
    
    EventGenerator eg = spy(new EventGenerator());
    
    String metaInformation =  
      "<eventKey xmlns='"+ns+"'>" + 
      "  <name>TemperatureValue</name>" + 
      "  <dataType>Integer</dataType>" + 
      "  <relatedStateVariable>Temperature</relatedStateVariable>" + 
      "  <allowedValueRange>" + 
      "    <max>50</max>" + 
      "    <min>-30</min>" + 
      "  </allowedValueRange>" + 
      "  <unit>Celsius</unit>" + 
      "</eventKey>";
    
    DataComparator dc = new DataComparator();
    Element expected = dc.readXML(metaInformation);
    
    Element superElm = spy(eg.element("superElem", ns));
    Element result = eg.processEventKey(g, superElm);
    
//    System.out.println("EXP :: " + eg.toString(expected));
//    System.out.println("RES :: " + eg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
    verify(superElm).addContent(result);
  }
}
