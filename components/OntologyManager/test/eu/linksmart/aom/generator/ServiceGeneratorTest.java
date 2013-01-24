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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

import org.jdom.Element;
import org.junit.Test;


import eu.linksmart.aom.generator.ServiceGenerator;
import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Service;
import eu.linksmart.aom.processor.SCPDServiceProcessor.StateVariable;
import eu.linksmart.aom.testutil.DataComparator;

public class ServiceGeneratorTest {

  @Test
  public void testProcess(){
    ServiceGenerator sg = spy(new ServiceGenerator());
    doReturn(null).when(sg).input(any(Graph.class), any(Element.class));
    doReturn(null).when(sg).output(any(Graph.class), any(Element.class));

    String deviceURI = "http://device.uri";
    String serviceURI = "http://service.uri";
    String paramInURI = "http://paramin.uri";
    String paramOutURI = "http://paramout.uri";
    
    Graph serviceGraph = GraphLoader.load(deviceURI, 
        GraphData.scpdServiceWithParams(deviceURI, serviceURI, paramInURI, paramOutURI)).
    subGraph(Device.hasService);

    String ns = "some_ns";
    Element superElm = sg.element("superElem", ns);
    String sv =  
      "<action xmlns='"+ns+"'>" + 
      "  <name>ReadMessage</name>" + 
      "  <argumentList/>" + 
      "</action>";
    
    DataComparator dc = new DataComparator();
    
    Element expected = dc.readXML(sv);
    Element result = sg.process(serviceGraph, superElm);
    
//    System.out.println("EXP :: " + sg.toString(expected));
//    System.out.println("RES :: " + sg.toString(result));
    assertTrue(dc.sameAs(expected, result));
    verify(sg).input(any(Graph.class), any(Element.class));
    verify(sg).output(any(Graph.class), any(Element.class));
  }

  @Test
  public void testInput(){
    ServiceGenerator sg = spy(new ServiceGenerator());
    String paramName = "status"; 

    String serviceURI = "http://service.uri";
    String paramInURI = "http://paramin.uri";
    
    Graph serviceGraph = GraphLoader.load(serviceURI, 
        GraphData.scpdServiceParamterIn(serviceURI, paramInURI)).
    subGraph(Service.hasInput);

    String ns = "some_ns";
    Element superElm = sg.element("superElem", ns);
    String in =  
      "<argument xmlns='"+ns+"'>" + 
      "  <name>" + paramName + "</name>" + 
      "  <direction>in</direction>" + 
      "  <relatedStateVariable>A_ARG_TYPE_ReadMessage_status</relatedStateVariable>" + 
      "</argument>";
    
    DataComparator dc = new DataComparator();
    
    Element expected = dc.readXML(in);
    Element result = sg.input(serviceGraph, superElm);
    
    assertTrue(dc.sameAs(expected, result));
    
    Set<StateVariable> exStateVariables = new HashSet<StateVariable>();
    exStateVariables.add(new StateVariable("A_ARG_TYPE_ReadMessage_status", "string", false));
    
    assertEquals(exStateVariables, sg.getStateVariables());
  }
  
  @Test
  public void testOutput(){
    ServiceGenerator sg = spy(new ServiceGenerator());
    String paramName = "Message"; 
    
    String serviceURI = "http://service.uri";
    String paramOutURI = "http://paramout.uri";
    
    Graph serviceGraph = GraphLoader.load(serviceURI, 
        GraphData.scpdServiceParamterOut(serviceURI, paramOutURI)).
        subGraph(Service.hasOutput);
    
    String ns = "some_ns";
    Element superElm = sg.element("superElem", ns);
    String in =  
      "<argument xmlns='"+ns+"'>" + 
      "  <name>" + paramName + "</name>" + 
      "  <direction>out</direction>" + 
      "  <retval />" + 
      "  <relatedStateVariable>Message</relatedStateVariable>" + 
      "</argument>";
    
    DataComparator dc = new DataComparator();
    
    Element expected = dc.readXML(in);
    Element result = sg.output(serviceGraph, superElm);
    
    assertTrue(dc.sameAs(expected, result));
    
    Set<StateVariable> exStateVariables = new HashSet<StateVariable>();
    exStateVariables.add(new StateVariable("Message", "string", false));
    
    assertEquals(exStateVariables, sg.getStateVariables());
  }
  
  @Test
  public void testStateVariable(){
    ServiceGenerator sg = spy(new ServiceGenerator());
    Set<StateVariable> stateVariables = new HashSet<StateVariable>();
    StateVariable v1 = new StateVariable("DiscoveryPending", "boolean", false); 
    StateVariable v2 = new StateVariable("ServiceChannel", "i2", true);
    stateVariables.add(v1);
    stateVariables.add(v2);
    
    doReturn(stateVariables).when(sg).getStateVariables();

    String ns = "some_ns";
    Element superElm = sg.element("superElem", ns);
    sg.stateTable(superElm);

    String sv =  
      "<serviceStateTable xmlns='"+ns+"'>" + 
      "  <stateVariable sendEvents='no'>" + 
      "    <name>DiscoveryPending</name>" + 
      "    <dataType>boolean</dataType>" + 
      "  </stateVariable>" + 
      "  <stateVariable sendEvents='yes'>" + 
      "    <name>ServiceChannel</name>" + 
      "    <dataType>i2</dataType>" + 
      "  </stateVariable>" + 
      "</serviceStateTable>";
    
    DataComparator dc = new DataComparator();
    
    Element expected = dc.readXML(sv);
    Element result = sg.stateTable(superElm);
    
//    System.out.println("EXP :: " + sg.toString(expected));
//    System.out.println("RES :: " + sg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
  }
}
