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

import org.jdom.Element;
import org.junit.Test;
import org.openrdf.model.impl.ValueFactoryImpl;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.model.Rdfs;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.processor.SCPDProcessor;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.RepositoryUtil;
import eu.linksmart.aom.testutil.StubData;

public class SCPDProcessorTest extends StubData {

  @Test
  public void testProcessPhysical(){
    String scpdClassName = "OntologyDeviceClass";
    AOMRepository repo = repositoryStub();
    doReturn(true).when(repo).classExists(Namespace.device + scpdClassName);
    

    SCPDProcessor p = spy(new SCPDProcessor(repo));
    Graph sg = mock(Graph.class);
    doReturn(sg).when(p).services(any(Element.class), any(Graph.class));
    Graph d = p.process(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));
    
    Graph info = d.subGraph(Device.info);
    assertEquals(
        info, 
        GraphLoader.load(
            info.getBaseURI(), 
            GraphData.scpdManufacturer(d.getBaseURI(), d.value(Device.info))));
    assertEquals(d.value(Rdf.rdfType), Namespace.device + scpdClassName);
    assertEquals(d.value(Device.deviceUPnPType), "upnp:type");
    
    verify(p, times(0)).preconditions(any(Element.class), any(Graph.class));
    verify(p).services(any(Element.class), any(Graph.class));
  }

  @Test
  public void testProcessNotExistingClassPhysical(){
    String scpdClassName = "OntologyDeviceClass";
    AOMRepository repo = repositoryStub();
    

    SCPDProcessor p = spy(new SCPDProcessor(repo));
    Graph sg = mock(Graph.class);
    doReturn(sg).when(p).services(any(Element.class), any(Graph.class));
    Graph d = p.process(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));
    
    Graph info = d.subGraph(Device.info);
    assertEquals(
        info, 
        GraphLoader.load(
            info.getBaseURI(), 
            GraphData.scpdManufacturer(d.getBaseURI(), d.value(Device.info))));
    assertEquals(d.value(Rdf.rdfType), Device.PhysicalDevice.stringValue());
    
    verify(p, times(0)).preconditions(any(Element.class), any(Graph.class));
    verify(p).services(any(Element.class), any(Graph.class));
  }

  @Test
  public void testProcessSemantic(){
    String scpdClassName = "SemanticOntologyDeviceClass";
    String location = "semanticSCPD";
    AOMRepository repo = RepositoryFactory.local(location);
    
    Graph t = GraphLoader.load("", GraphData.taxonomy());
    ResourceUtil.addStatement(
        Namespace.device + scpdClassName, 
        Rdfs.subClassOf, 
        Namespace.device + "SemanticDevice", 
        repo.getValueFactory(), 
        t);
    repo.store(t);

    SCPDProcessor p = spy(new SCPDProcessor(repo));
    Graph sg = mock(Graph.class);
    doReturn(sg).when(p).services(any(Element.class), any(Graph.class));
    Graph d = p.process(DataLoader.xmlToString("test/resources/scpd/semantic-scpd.xml"));
    
    Graph info = d.subGraph(Device.info);
    assertEquals(
        info, 
        GraphLoader.load(
            info.getBaseURI(), 
            GraphData.scpdManufacturer(d.getBaseURI(), d.value(Device.info))));
    assertTrue(d.getBaseURI().contains(Namespace.device + scpdClassName));
    assertEquals("some pid", d.value(Device.PID));
    assertEquals(d.value(Rdf.rdfType), Namespace.device + scpdClassName);
    
    verify(p, times(1)).preconditions(any(Element.class), any(Graph.class));
    verify(p).services(any(Element.class), any(Graph.class));
    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void testProcessNotExistingClassSemantic(){
    String scpdClassName = "SemanticOntologyDeviceClass";
    String location = "semanticSCPDNotExisting";
    AOMRepository repo = RepositoryFactory.local(location);
    
    Graph t = GraphLoader.load("", GraphData.taxonomy());
    repo.store(t);

    SCPDProcessor p = spy(new SCPDProcessor(repo));
    Graph sg = mock(Graph.class);
    doReturn(sg).when(p).services(any(Element.class), any(Graph.class));
    Graph d = p.process(DataLoader.xmlToString("test/resources/scpd/semantic-scpd.xml"));
    
    Graph info = d.subGraph(Device.info);
    System.out.print("");
    assertEquals(
        info, 
        GraphLoader.load(
            info.getBaseURI(), 
            GraphData.scpdManufacturer(d.getBaseURI(), d.value(Device.info))));
    assertTrue(d.getBaseURI().contains(Device.SemanticDevice.stringValue()));
    assertEquals(d.value(Rdf.rdfType), Device.SemanticDevice.stringValue());
    
    verify(p, times(1)).preconditions(any(Element.class), any(Graph.class));
    verify(p).services(any(Element.class), any(Graph.class));
    repo.close();
    RepositoryUtil.clean(location);
  }

}
