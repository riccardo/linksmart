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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jdom.Element;
import org.junit.Test;


import eu.linksmart.aom.generator.EnergyProfileGenerator;
import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Energy;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.testutil.DataComparator;

public class EnergyProfileGeneratorTest {
  @Test
  public void testProcess() {
    String deviceURI = "http://device.uri";
    String energyProfileURI = "http://energyprofile.uri";
    String classificationURI = "http://classification.uri";
    String consumptionURI = "http://consumption.uri";
    String generationURI = "http://generation.uri";
    String lifeTimeURI = "http://lifetime.uri";
    String operationURI = "http://operation.uri";
    String ns = "some_ns";
    Graph g = GraphLoader.load(deviceURI,
        GraphData.energyProfile(deviceURI, energyProfileURI, classificationURI, consumptionURI,
            generationURI, lifeTimeURI, operationURI))
        .subGraph(Device.hasEnergyProfile);

    EnergyProfileGenerator epg = spy(new EnergyProfileGenerator());
    doReturn(null).when(epg).processClassification(any(Graph.class), any(Element.class));
    doReturn(null).when(epg).processMode(any(Graph.class), any(Element.class));
    doReturn(null).when(epg).processLifeTime(any(Graph.class), any(Element.class));
    doReturn(null).when(epg).processOperation(any(Graph.class), any(Element.class));

    String event =  
        "<energyprofile xmlns='" + ns + "'>" + 
        "  <energyclassification/>" + 
        "  <energyconsumption>" + 
        "    <effect/>" + 
        "  </energyconsumption>" + 
        "  <energygeneration>" + 
        "    <effect/>" + 
        "  </energygeneration>" + 
        "  <operation/>" + 
        "  <lifetime/>" + 
        "</energyprofile>";

    DataComparator dc = new DataComparator();
    Element expected = dc.readXML(event);

    Element superElm = epg.element("superElem", ns);
    Element result = epg.process(g, superElm);

//    System.out.println("EXP :: " + epg.toString(expected));
//    System.out.println("RES :: " + epg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
    verify(epg).processClassification(any(Graph.class), any(Element.class));
    verify(epg, times(2)).processMode(any(Graph.class), any(Element.class));
    verify(epg).processOperation(any(Graph.class), any(Element.class));
    verify(epg).processLifeTime(any(Graph.class), any(Element.class));
  }

  @Test
  public void testProcessClassification() {
    String energyProfileURI = "http://energyprofile.uri";
    String classificationURI = "http://classification.uri";
    String ns = "some_ns";
    Graph g = GraphLoader.load(energyProfileURI,
        GraphData.energyClassification(energyProfileURI, classificationURI)).subGraph(Energy.classification);

    EnergyProfileGenerator epg = spy(new EnergyProfileGenerator());

    String event =  
        "<energyclassification xmlns='" + ns +"'>" + 
        "  <system>SIS</system>" + 
        "  <value>B</value>" + 
        "</energyclassification>";

    DataComparator dc = new DataComparator();
    Element expected = dc.readXML(event);

    Element superElm = epg.element("energyclassification", ns);
    Element result = epg.processClassification(g, superElm);

//    System.out.println("EXP :: " + epg.toString(expected));
//    System.out.println("RES :: " + epg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
  }
  @Test
  public void testProcessOperation() {
    String energyProfileURI = "http://energyprofile.uri";
    String operationURI = "http://operation.uri";
    String unitValueURI = "http://unitvalue.uri";
    String ns = "some_ns";
    Graph g = GraphLoader.load(energyProfileURI,
        GraphData.energyOperationWithValue(energyProfileURI, operationURI,
            unitValueURI, Namespace.unit + "seconds", "5")).subGraph(Energy.operation);

    EnergyProfileGenerator epg = spy(new EnergyProfileGenerator());

    String event =  
        "<operation xmlns='" + ns +"'>" + 
        "  <minimumruntime unit='seconds'>5</minimumruntime>" + 
        "</operation>";

    DataComparator dc = new DataComparator();
    Element expected = dc.readXML(event);

    Element superElm = epg.element("operation", ns);
    Element result = epg.processOperation(g, superElm);

//    System.out.println("EXP :: " + epg.toString(expected));
//    System.out.println("RES :: " + epg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
  }
  @Test
  public void testProcessLifeTime() {
    String energyProfileURI = "http://energyprofile.uri";
    String ltURI = "http://lifetime.uri";
    String expURI = "http://expected.uri";
    String scURI = "http://startcost.uri";
    String sdURI = "http://shutdowncost.uri";
    String ns = "some_ns";
    Graph g = GraphLoader.load(energyProfileURI,
        GraphData.energyLifeTimeWithValues(energyProfileURI, ltURI,
            expURI, scURI, sdURI)).subGraph(Energy.lifeTime);

    EnergyProfileGenerator epg = spy(new EnergyProfileGenerator());

    String event =  
        "<lifetime xmlns='" + ns +"'>" + 
        "  <expected unit='minutes'>1</expected>" + 
        "  <startcost unit='minutes'>2</startcost>" + 
        "  <shutdowncost unit='minutes'>3</shutdowncost>" + 
        "</lifetime>";

    DataComparator dc = new DataComparator();
    Element expected = dc.readXML(event);

    Element superElm = epg.element("lifetime", ns);
    Element result = epg.processLifeTime(g, superElm);

//    System.out.println("EXP :: " + epg.toString(expected));
//    System.out.println("RES :: " + epg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
  }
  @Test
  public void testProcessEnergyMode() {
    String energyProfileURI = "http://energyprofile.uri";
    String relationURI = Energy.consumption.stringValue();
    String modeURI = "http://mode.uri";
    String ns = "some_ns";
    Graph g = GraphLoader.load(energyProfileURI,
        GraphData.energyMode(energyProfileURI, relationURI, modeURI)).subGraph(Energy.consumption);

    EnergyProfileGenerator epg = spy(new EnergyProfileGenerator());

    String event =  
        "<effect xmlns='" + ns +"'>" + 
        "  <mode name='running'>" + 
        "    <max>1</max>" + 
        "    <average>2</average>" + 
        "    <start>3</start>" + 
        "    <shutdown>4</shutdown>" + 
        "  </mode>" + 
        "</effect>";

    DataComparator dc = new DataComparator();
    Element expected = dc.readXML(event);

    Element superElm = epg.element("effect", ns);
    Element result = epg.processMode(g, superElm);

//    System.out.println("EXP :: " + epg.toString(expected));
//    System.out.println("RES :: " + epg.toString(result));
    assertTrue(new DataComparator().sameAs(expected, result));
  }
}
