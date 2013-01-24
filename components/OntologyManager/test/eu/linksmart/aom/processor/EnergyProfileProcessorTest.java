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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.junit.Test;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Energy;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.schema.ResourceProperty;
import eu.linksmart.aom.processor.EnergyProfileProcessor;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.StubData;

public class EnergyProfileProcessorTest extends StubData {
  public AOMRepository repoStub(){
    AOMRepository repo = repositoryStub();
    doReturn(new Graph("http://any.uri")).when(repo).getUnitValue(
        any(String.class), any(String.class));
    return repo;
  }
  @Test
  public void testProcess(){
    EnergyProfileProcessor p = spy(new EnergyProfileProcessor(repoStub()));

    String deviceURI = "http://my.device.uri";

    p.process(deviceURI, DataLoader.xmlToString("test/resources/energy/energy.xml"));
    verify(p).processClassification(any(String.class), any(Element.class), any(Graph.class));
    verify(p).processOperation(any(String.class), any(Element.class), any(Graph.class));
    verify(p).processLifeTime(any(String.class), any(Element.class), any(Graph.class));
    verify(p).processConsumption(any(String.class), any(Element.class), any(Graph.class));
    verify(p).processGeneration(any(String.class), any(Element.class), any(Graph.class));

  }

  @Test
  public void testProcessClassification(){
    EnergyProfileProcessor p = spy(new EnergyProfileProcessor(repoStub()));
    String epURI = "http://my.eProfile.uri";

    Graph ec = p.processClassification(
        epURI, 
        DataLoader.parse("test/resources/energy/energy.xml"),
        new Graph(epURI));

    assertEquals(
        ec, 
        GraphLoader.load(
            ec.getBaseURI(), 
            GraphData.energyClassification(
                epURI, 
                ec.value(Energy.classification))));

  }

  @Test
  public void testProcessOperation(){
    
    AOMRepository repo = repoStub();
    String uvURI = "http://unitvalue.uri";
    String value=  "5";
    String unit = "seconds";
    Graph uvg = GraphLoader.load(
        uvURI, 
        GraphData.unitValue(uvURI, (Namespace.unit + unit), value));
    doReturn(uvg).when(repo).getUnitValue(
        value, unit);
    
    EnergyProfileProcessor p = spy(new EnergyProfileProcessor(repo));
    String epURI = "http://my.eProfile.uri";

    Graph eo = p.processOperation(
        epURI, 
        DataLoader.parse("test/resources/energy/energy.xml"),
        new Graph(epURI));

    assertEquals(
        eo, 
        GraphLoader.load(
            eo.getBaseURI(), 
            GraphData.energyOperation(
                epURI, 
                eo.value(Energy.operation),
                uvURI)).add(uvg));

  }

  @Test
  public void testProcessLifeTime(){
    
    AOMRepository repo = repoStub();
    String uvURI1 = "http://unitvalue.uri/1";
    String value1=  "1000000";
    String uvURI2 = "http://unitvalue.uri/2";
    String value2=  "1000";
    String uvURI3 = "http://unitvalue.uri/3";
    String value3=  "100";
    String unit = "seconds";
    Graph uvg1 = GraphLoader.load(
        uvURI1, 
        GraphData.unitValue(uvURI1, (Namespace.unit + unit), value1));
    Graph uvg2 = GraphLoader.load(
        uvURI2, 
        GraphData.unitValue(uvURI2, (Namespace.unit + unit), value2));
    Graph uvg3 = GraphLoader.load(
        uvURI3, 
        GraphData.unitValue(uvURI3, (Namespace.unit + unit), value3));

    doReturn(uvg1).when(repo).getUnitValue(
        value1, unit);
    doReturn(uvg2).when(repo).getUnitValue(
        value2, unit);
    doReturn(uvg3).when(repo).getUnitValue(
        value3, unit);
    
    EnergyProfileProcessor p = spy(new EnergyProfileProcessor(repo));
    String epURI = "http://my.eProfile.uri";

    Graph eo = p.processLifeTime(
        epURI, 
        DataLoader.parse("test/resources/energy/energy.xml"),
        new Graph(epURI));

    assertEquals(
        eo, 
        GraphLoader.load(
            eo.getBaseURI(), 
            GraphData.energyLifeTime(
                epURI, 
                eo.value(Energy.lifeTime),
                uvURI1, uvURI2, uvURI3)).add(uvg1).add(uvg2).add(uvg3));

  }

  @Test
  public void testProcessConsumption(){
    AOMRepository repo = repoStub();
    
    EnergyProfileProcessor p = spy(new EnergyProfileProcessor(repo));
    String epURI = "http://my.eProfile.uri";

    Graph ec = p.processConsumption(
        epURI, 
        DataLoader.parse("test/resources/energy/energy.xml"),
        new Graph(epURI));

    verify(p, times(2)).processMode(any(String.class), any(ResourceProperty.class), any(Element.class), any(Graph.class));
  }

  @Test
  public void testProcessGeneration(){
    AOMRepository repo = repoStub();
    
    EnergyProfileProcessor p = spy(new EnergyProfileProcessor(repo));
    String epURI = "http://my.eProfile.uri";

    Graph eg = p.processGeneration(
        epURI, 
        DataLoader.parse("test/resources/energy/energy.xml"),
        new Graph(epURI));

    verify(p, times(2)).processMode(any(String.class), any(ResourceProperty.class), any(Element.class), any(Graph.class));
  }

  @Test
  public void testProcessMode() throws Exception {
    EnergyProfileProcessor p = spy(new EnergyProfileProcessor(repoStub()));
    String epURI = "http://my.eProfile.uri";

    XPath xp = XPath.newInstance("//mode[1]");

    ResourceProperty relation = new ResourceProperty("http://my.mode.relation");
    Graph m = p.processMode(
        epURI, 
        relation, 
        (Element)xp.selectSingleNode(DataLoader.parse("test/resources/energy/energy.xml")),
        new Graph(epURI));

    assertEquals(
        m, 
        GraphLoader.load(
            m.getBaseURI(), 
            GraphData.energyMode(
                epURI, 
                relation.stringValue(), 
                m.value(relation))));
  }

}
