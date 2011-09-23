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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.junit.Test;
import org.openrdf.model.impl.ValueFactoryImpl;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.processor.PreconditionProcessor;
import eu.linksmart.aom.processor.Processor;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.RepositoryUtil;
import eu.linksmart.aom.testutil.StubData;

public class PreconditionProcessorTest extends StubData {

  @Test
  public void testProcess() throws Exception {
    AOMRepository repo = repositoryStub();
    
    XPath xp = XPath.newInstance("//device:deviceList");
    xp.addNamespace("device", Processor.SCPD_DEVICE_NS);

    Graph d = new Graph("http://device.uri");
    PreconditionProcessor p = spy(new PreconditionProcessor(repo));
    p.process(
        (Element)xp.selectSingleNode(DataLoader.parse("test/resources/scpd/semantic-scpd.xml")), 
        d);
    
    verify(p, times(2)).processPrecondition(any(Element.class), any(Graph.class));

  }

  @Test
  public void testProcessPIDPrecondition() throws Exception {
    String location = "preconditionPID";
    AOMRepository repo = RepositoryFactory.local(location);
    repo.store(GraphLoader.load("", GraphData.taxonomy()));
    
    XPath xp = XPath.newInstance("//device:deviceList[1]/device:precondition[1]");
    xp.addNamespace("device", Processor.SCPD_DEVICE_NS);
    
    Graph d = new Graph("http://device.uri");
    PreconditionProcessor p = spy(new PreconditionProcessor(repo));
    p.processPrecondition(
        (Element)xp.selectSingleNode(DataLoader.parse("test/resources/scpd/semantic-scpd.xml")), 
        d);
    
    Graph pid = d.subGraph(Device.hasPrecondition);
    assertEquals(Device.SemanticDevicePIDPrecondition.stringValue(), pid.value(Rdf.rdfType));
    
    assertEquals("myWindMeters", pid.value(Device.preconditionId));
    Set<String> pids = pid.values(Device.preconditionPID);
    assertEquals(2, pids.size());
    assertTrue(pids.contains("GardenWind"));
    assertTrue(pids.contains("RoofWind"));
    
    repo.close();
    RepositoryUtil.clean(location);

  }


  @Test
  public void testProcessQueryPrecondition() throws Exception {
    String location = "preconditionQuery";
    AOMRepository repo = RepositoryFactory.local(location);
    repo.store(GraphLoader.load("", GraphData.taxonomy()));
    
    XPath xp = XPath.newInstance("//device:deviceList[1]/device:precondition[2]");
    xp.addNamespace("device", Processor.SCPD_DEVICE_NS);
    
    Graph d = new Graph("http://device.uri");
    PreconditionProcessor p = spy(new PreconditionProcessor(repo));
    p.processPrecondition(
        (Element)xp.selectSingleNode(DataLoader.parse("test/resources/scpd/semantic-scpd.xml")), 
        d);
    
    Graph query = d.subGraph(Device.hasPrecondition);
    assertEquals(Device.SemanticDeviceQueryPrecondition.stringValue(), query.value(Rdf.rdfType));
    
    assertEquals("myThermometers", query.value(Device.preconditionId));
    assertEquals("3..M", query.value(Device.preconditionCardinality));
    assertTrue(query.value(Device.preconditionQuery).contains("rdf:type;device:Thermometer"));
    
    repo.close();
    RepositoryUtil.clean(location);

  }
}
