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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jdom.Element;
import org.junit.Test;


import eu.linksmart.aom.generator.ConfigurationGenerator;
import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.testutil.DataComparator;

public class ConfigurationGeneratorTest {
  @Test
  public void testProcess(){
    ConfigurationGenerator g = spy(new ConfigurationGenerator());
    doReturn(null).when(g).processFile(any(Graph.class), any(Element.class));
    String configURI = "http://my.config";

    String xml = "<Configuration name=\"config name\" implementationClass=\"some.impl.Class\"/>";
    Element config = g.process(GraphLoader.load(configURI, GraphData.configXML("http://my.device", configURI)));
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(config, dc.readXML(xml)));
    verify(g, times(2)).processFile(any(Graph.class), any(Element.class));
  }

  @Test
  public void testProcessFile(){
    ConfigurationGenerator g = spy(new ConfigurationGenerator());
    String configURI = "http://my.config";

    String config = "any-config-root";
    String xml = "" +
    "<"+config+">\n" +
    "  <AssociatedFile downloadURL=\"http://to.download/1\" storageFolder=\"file://to.store/1\"/>\n" + 
    "</"+config+">\n";
    Element configFile = g.processFile(
        GraphLoader.load(configURI, GraphData.configFile("http://my.device", configURI)), 
        new Element(config));
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(configFile, dc.readXML(xml)));
  }

  @Test
  public void testGetConfigurations(){
    ConfigurationGenerator g = spy(new ConfigurationGenerator());
    doReturn(null).when(g).getConfigurations(any(HashSet.class), any(Element.class));

    Map<String, HashSet<Graph>> devices = new HashMap<String, HashSet<Graph>>();
    HashSet<Graph> someDevices = new HashSet<Graph>();
    someDevices.add(new Graph(""));
    someDevices.add(new Graph(""));
    HashSet<Graph> anotherDevices = new HashSet<Graph>();
    anotherDevices.add(new Graph(""));
    anotherDevices.add(new Graph(""));
    devices.put("SomeDevice", someDevices);
    devices.put("AnotherDevice", anotherDevices);

    Element configs = g.getConfigurations(devices);

    String xml = "" +
    "<DeviceTypes>\n" +
    "  <DeviceType name=\"SomeDevice\"/>\n" +
    "  <DeviceType name=\"AnotherDevice\"/>\n" +
    "</DeviceTypes>\n" +
    "";
    DataComparator dc = new DataComparator();
    
    assertTrue(dc.sameAs(dc.readXML(xml), configs));
    verify(g, times(2)).getConfigurations(any(HashSet.class), any(Element.class));
  }

  @Test
  public void testGetTypeConfigurations(){
    ConfigurationGenerator g = spy(new ConfigurationGenerator());
    
    String impl1 = "implClass1";
    String name1 = "name1";
    String down1 = "down1";
    String store1 = "store1";
    String impl2 = "implClass2";
    String name2 = "name2";
    String down2 = "down2";
    String store2 = "store2";
    String d1URI = "http://device.uri.1";
    String c1URI = "http://config.uri.1";
    String d2URI = "http://device.uri.2";
    String c2URI = "http://config.uri.2";
    
    Graph d1 = GraphLoader.load(d1URI, GraphData.device(d1URI));
    Graph c1 = GraphLoader.load(d1URI, GraphData.configContent(d1URI, c1URI, name1, impl1));
    c1.add(GraphLoader.load(d1URI, GraphData.configFileContent(c1URI, down1, store1)));
    d1.add(c1);

    Graph d2 = GraphLoader.load(d2URI, GraphData.device(d2URI));
    Graph c2 = GraphLoader.load(d2URI, GraphData.configContent(d2URI, c2URI, name2, impl2));
    c2.add(GraphLoader.load(d2URI, GraphData.configFileContent(c2URI, down2, store2)));
    d2.add(c2);

    HashSet<Graph> devices = new HashSet<Graph>();
    devices.add(d1);
    devices.add(d2);

    String configRoot = "device-type-config";
    Element config = new Element(configRoot);
    Element typeConfig = g.getConfigurations(devices, config);
    
    String xml = "" +
    "<"+configRoot+">\n" +
    "  <Configuration  implementationClass=\""+impl1+"\" name=\""+name1+"\">\n" +
    "    <AssociatedFile downloadURL=\""+down1+"\" storageFolder=\""+store1+"\"/>\n" +
    "  </Configuration>\n" +
    "  <Configuration  implementationClass=\""+impl2+"\" name=\""+name2+"\">\n" +
    "    <AssociatedFile downloadURL=\""+down2+"\" storageFolder=\""+store2+"\"/>\n" +
    "  </Configuration>\n" +
    "</"+configRoot+">\n" +
    "";

    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(dc.readXML(xml), typeConfig));
  }
}
