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

package eu.linksmart.aom.testutil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.processor.ConfigurationProcessor;
import eu.linksmart.aom.processor.DiscoveryProcessor;
import eu.linksmart.aom.repository.AOMRepository;

public class GraphComparatorTest extends StubData {
  ValueFactory vf = new ValueFactoryImpl();
  AOMRepository repo = repositoryStub();
  {
    doReturn(false).when(repo).isInStaticModel(any(Value.class));
    doReturn(true).when(repo).isInStaticModel(new ValueFactoryImpl().createURI(Namespace.unit + "minutes"));
    doReturn(true).when(repo).isInStaticModel(new ValueFactoryImpl().createURI(Namespace.unit + "seconds"));
  }

  @Test
  public void testSameGraphStructure1(){
    String device1 = "http://device.uri/1";
    Graph g1 = new Graph(device1);
    Graph g2 = new Graph(device1); 
    assertTrue(GraphComparator.sameAs(g1, g2, repo));
  }
  @Test
  public void testSameGraphStructure2(){
    String device1 = "http://device.uri/1";
    String device2 = "http://device.uri/2";
    Graph g1 = new Graph(device1);
    Graph g2 = new Graph(device2); 
    assertTrue(GraphComparator.sameAs(g1, g2, repo));
  }

  @Test
  public void testSameGraphStructure3(){
    String device1 = "http://device.uri/1";
    String someClass = "http://class.uri/1";
    Graph g1 = new Graph(device1);
    Graph g2 = new Graph(device1); 
    ResourceUtil.addStatement(device1, Rdf.rdfType, someClass, vf, g1);
    ResourceUtil.addStatement(device1, Rdf.rdfType, someClass, vf, g2);
    assertTrue(GraphComparator.sameAs(g1, g2, repo));
  }

  @Test
  public void testSameGraphStructure4(){
    String device1 = "http://device.uri/1";
    String device2 = "http://device.uri/2";
    String someClass = "http://class.uri/1";
    Graph g1 = new Graph(device1);
    Graph g2 = new Graph(device2); 
    ResourceUtil.addStatement(device1, Rdf.rdfType, someClass, vf, g1);
    ResourceUtil.addStatement(device2, Rdf.rdfType, someClass, vf, g2);
    assertTrue(GraphComparator.sameAs(g1, g2, repo));
  }
  
  @Test
  public void testSameGraphStructure5(){
    String device1 = "http://device.uri/1";
    String device2 = "http://device.uri/2";
    String cfg1 = "http://cfg.uri/1";
    String cfg2 = "http://cfg.uri/2";
    String someClass = "http://class.uri/1";
    Graph g1 = new Graph(device1);
    Graph g2 = new Graph(device2); 
    ResourceUtil.addStatement(device1, Rdf.rdfType, someClass, vf, g1);
    ResourceUtil.addStatement(device1, Device.hasConfiguration, cfg1, vf, g1);
    ResourceUtil.addStatement(device2, Rdf.rdfType, someClass, vf, g2);
    ResourceUtil.addStatement(device2, Device.hasConfiguration, cfg2, vf, g2);
    assertTrue(GraphComparator.sameAs(g1, g2, repo));
  }
  
  @Test
  public void testSameGraphStructure6(){
    String device1 = "http://device.uri/1";
    String device2 = "http://device.uri/2";
    String cfg1 = "http://cfg.uri/1";
    String cfg2 = "http://cfg.uri/2";
    String someClass = "http://class.uri/1";
    Graph g1 = new Graph(device1);
    Graph g2 = new Graph(device2); 
    ResourceUtil.addStatement(device1, Rdf.rdfType, someClass, vf, g1);
    ResourceUtil.addStatement(device1, Device.hasConfiguration, cfg1, vf, g1);
    ResourceUtil.addStatement(device2, Rdf.rdfType, someClass, vf, g2);
    ResourceUtil.addStatement(device2, Device.hasConfiguration, cfg2, vf, g2);
    ResourceUtil.addStatement(cfg2, Device.hasConfiguration, cfg1, vf, g2);
    assertFalse(GraphComparator.sameAs(g1, g2, repo));
  }
  
  @Test
  public void testSameGraphStructure7(){
    String device1 = "http://device.uri/1";
    String device2 = "http://device.uri/2";
    String cfg1 = "http://cfg.uri/1";
    String cfg2 = "http://cfg.uri/2";
    String someClass1 = "http://class.uri/1";
    String someClass2 = "http://class.uri/2";
    Graph g1 = new Graph(device1);
    Graph g2 = new Graph(device2); 
    ResourceUtil.addStatement(device1, Rdf.rdfType, someClass1, vf, g1);
    ResourceUtil.addStatement(device1, Device.hasConfiguration, cfg1, vf, g1);
    ResourceUtil.addStatement(device2, Rdf.rdfType, someClass2, vf, g2);
    ResourceUtil.addStatement(device2, Device.hasConfiguration, cfg2, vf, g2);
    assertFalse(GraphComparator.sameAs(g1, g2, repo));
  }
  
  @Test
  public void testSameGraphStructure8(){
    String epURI1 = "http://ep.uri/1";
    String ltURI1 = "http://lt.uri/1";
    String expURI1 = "http://exp.uri/1";
    String scURI1 = "http://sc.uri/1";
    String sdURI1 = "http://sd.uri/1";
    String epURI2 = "http://ep.uri/2";
    String ltURI2 = "http://lt.uri/2";
    String expURI2 = "http://exp.uri/2";
    String scURI2 = "http://sc.uri/2";
    String sdURI2 = "http://sd.uri/2";
    Graph g1 = GraphLoader.load(epURI1, GraphData.energyLifeTimeWithValues(epURI1, ltURI1, expURI1, scURI1, sdURI1));
    Graph g2 = GraphLoader.load(epURI2, GraphData.energyLifeTimeWithValues2(epURI2, ltURI2, expURI2, scURI2, sdURI2));
    assertFalse(GraphComparator.sameAs(g1, g2, repo));
  }
  
  @Test
  public void testSameGraphStructure9(){
    String device1 = "http://device.uri/1";
    String device2 = "http://device.uri/2";
    String cfg1 = "http://cfg.uri/1";
    String cfg2 = "http://cfg.uri/2";
    String someClass1 = "http://class.uri/1";
    String someClass2 = "http://class.uri/2";
    Graph g1 = new Graph(device1);
    Graph g2 = new Graph(device2); 
    ResourceUtil.addStatement(device1, Rdf.rdfType, someClass1, vf, g1);
    ResourceUtil.addStatement(device1, Device.hasConfiguration, cfg1, vf, g1);
    ResourceUtil.addStatement(device2, Rdf.rdfType, someClass1, vf, g2);
    ResourceUtil.addStatement(cfg1, Device.hasConfiguration, cfg2, vf, g2);
    ResourceUtil.addStatement(device2, Device.hasConfiguration, cfg1, vf, g2);
    assertFalse(GraphComparator.sameAs(g1, g2, repo));
  }
  
  @Test
  public void testSameGraphStructureFull1(){
    String discoXML = DataLoader.xmlToString("test/resources/discovery/bluetooth.xml");        

    DiscoveryProcessor dp = new DiscoveryProcessor(repo);
    String deviceURI1 = "http://device.uri/1";
    String deviceURI2 = "http://device.uri/2";

    Graph g1 = dp.process(deviceURI1, discoXML);
    Graph g2 = dp.process(deviceURI2, discoXML); 

    assertTrue(GraphComparator.sameAs(g1, g2, repo));
  }
  @Test
  public void testSameGraphStructureFull2(){
    String discoXML1 = DataLoader.xmlToString("test/resources/discovery/bluetooth.xml");        
    String discoXML2 = DataLoader.xmlToString("test/resources/discovery/bluetooth-less.xml");        
    
    DiscoveryProcessor dp = new DiscoveryProcessor(repo);
    String deviceURI1 = "http://device.uri/1";
    String deviceURI2 = "http://device.uri/2";
    
    Graph g1 = dp.process(deviceURI1, discoXML1);
    Graph g2 = dp.process(deviceURI2, discoXML2); 
    
    assertFalse(GraphComparator.sameAs(g1, g2, repo));
  }
  @Test
  public void testSameGraphStructureFull3(){
    String configXML1 = DataLoader.xmlToString("test/resources/configuration/config1.xml");        
    String configXML2 = DataLoader.xmlToString("test/resources/configuration/config1.xml");        
    
    ConfigurationProcessor cp = new ConfigurationProcessor(repo);
    String deviceURI1 = "http://device.uri/1";
    String deviceURI2 = "http://device.uri/2";
    
    Graph g1 = cp.process(deviceURI1, configXML1);
    Graph g2 = cp.process(deviceURI2, configXML2); 
    
    assertTrue(GraphComparator.sameAs(g1, g2, repo));
  }
}
