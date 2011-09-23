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

package eu.linksmart.aom.processor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.junit.Test;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.Repository;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Event;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.processor.EventProcessor;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.StubData;

public class EventProcessorTest extends StubData {
  @Test
  public void testProcess(){
    EventProcessor p = spy(new EventProcessor(repositoryStub()));
    String deviceURI = "http://my.device.uri";
    
    doNothing().when(p).processEvent(any(Element.class), any(Graph.class));
    
    p.process(deviceURI, DataLoader.xmlToString("test/resources/event/events1.xml"));
    verify(p,times(3)).processEvent(any(Element.class), any(Graph.class));
}
  
  @Test
  public void testProcessEventTopicUnExists(){
    Element root = DataLoader.parse("test/resources/event/events1.xml");
    org.jdom.Namespace ns = root.getNamespace();
    Element event = root.getChild("event", ns);

    EventProcessor p = eventProcessor(false);

    String deviceURI = "http://my.device.uri";
    Graph g = new Graph(deviceURI);  
    
    p.processEvent(event, g);

    Graph expG = GraphLoader.load(
        deviceURI, 
        GraphData.event(deviceURI, g.value(Device.hasEvent), Event.Event.stringValue()));
    ResourceUtil.addStatement(
    		g.value(Device.hasEvent), 
    		Event.hasTopic, 
    		"myTopic", 
    		XMLSchema.STRING, 
    		new ValueFactoryImpl(), 
    		expG);


    assertEquals(expG, g);

    verify(p,times(3)).processEventKey(any(Element.class), anyString(), any(Graph.class));
    verify(p).processMetaInformation(any(Element.class), anyString(), any(Graph.class));
  }

  @Test
  public void testProcessEventTypeExists(){
    Element root = DataLoader.parse("test/resources/event/events1.xml");
    org.jdom.Namespace ns = root.getNamespace();
    Element event = root.getChild("event", ns);
    
    EventProcessor p = eventProcessor(true);
    
    String deviceURI = "http://my.device.uri";
    Graph g = new Graph(deviceURI);  
    
    p.processEvent(event, g);
    
    Graph expG = GraphLoader.load(
        deviceURI, 
        GraphData.event(deviceURI, g.value(Device.hasEvent), Namespace.event + "TemperatureChange"));
    ResourceUtil.addStatement(
    		g.value(Device.hasEvent), 
    		Event.hasTopic, 
    		"myTopic", 
    		XMLSchema.STRING, 
    		new ValueFactoryImpl(), 
    		expG);
   
    
    assertEquals(expG, g);
    
    verify(p,times(3)).processEventKey(any(Element.class), anyString(), any(Graph.class));
    verify(p).processMetaInformation(any(Element.class), anyString(), any(Graph.class));
  }
  
  private EventProcessor eventProcessor(boolean classExists){
    AOMRepository repo = repositoryStub();
    doReturn(classExists).when(repo).classExists(anyString());
    EventProcessor p = spy(new EventProcessor(repo));
    doNothing().when(p).processEventKey(any(Element.class), anyString(), any(Graph.class));
    doNothing().when(p).processMetaInformation(any(Element.class), anyString(), any(Graph.class));
    return p;
  }
  
  @Test
  public void testProcessEventKey1() throws JDOMException{
    Element root = DataLoader.parse("test/resources/event/events1.xml");
    org.jdom.Namespace ns = root.getNamespace();
    XPath xpath = XPath.newInstance("//eventKeyList[1]/eventKey[1]");
    Element eventKey = (Element)xpath.selectSingleNode(root);
    
    AOMRepository repo = repositoryStub();
    doReturn(true).when(repo).instanceExists(anyString());
    EventProcessor p = spy(new EventProcessor(repo));
    
    String eventURI = "http://my.event.uri";
    Graph g = new Graph(eventURI);  
    
    p.processEventKey(eventKey, eventURI, g);
    
    Graph expG = GraphLoader.load(
        eventURI, 
        GraphData.eventKey1(eventURI, 
            g.value(Event.hasKey)
            ));
    
    assertEquals(expG, g);
  }
  
  @Test
  public void testProcessEventKey2() throws JDOMException{
    Element root = DataLoader.parse("test/resources/event/events1.xml");
    org.jdom.Namespace ns = root.getNamespace();
    XPath xpath = XPath.newInstance("//eventKeyList[1]/eventKey[3]");
    Element eventKey = (Element)xpath.selectSingleNode(root);
    
    AOMRepository repo = repositoryStub();
    doReturn(true).when(repo).instanceExists(anyString());
    doReturn(Namespace.unit + "Celsius").when(repo).getUnit(anyString());
    EventProcessor p = spy(new EventProcessor(repo));
    
    String eventURI = "http://my.event.uri";
    Graph g = new Graph(eventURI);  
    
    p.processEventKey(eventKey, eventURI, g);
    
    Graph expG = GraphLoader.load(
        eventURI, 
        GraphData.eventKey2(eventURI, 
            g.value(Event.hasKey)
        ));
    assertEquals(expG, g);
  }
  
  @Test
  public void testProcessMetaInformation(){
    Element root = DataLoader.parse("test/resources/event/events1.xml");
    org.jdom.Namespace ns = root.getNamespace();
    Element metaInformation = root.getChild("event", ns).getChild("metaInformation", ns);
    
    String uri = "http://freq.uri";
    Graph uv = GraphLoader.load(
        uri,
        GraphData.uv(uri));
    AOMRepository repo = repositoryStub();
    doReturn(true).when(repo).instanceExists(anyString());
    doReturn(uv).when(repo).getUnitValue(anyString(), anyString());
    EventProcessor p = spy(new EventProcessor(repo));
    
    String eventURI = "http://my.event.uri";
    Graph g = new Graph(eventURI);  
    
    p.processMetaInformation(metaInformation, eventURI, g);
    
    Graph expG = GraphLoader.load(
        eventURI, 
        GraphData.metaInformation(eventURI, 
            g.value(Event.hasMetaInformation),
            g.subGraph(Event.hasMetaInformation).value(Event.frequency)
            ));
    
    
    assertEquals(expG, g);
  }
}
