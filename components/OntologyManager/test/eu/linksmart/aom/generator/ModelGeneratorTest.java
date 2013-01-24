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


import eu.linksmart.aom.generator.ModelGenerator;
import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdfs;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.testutil.DataComparator;
import eu.linksmart.aom.testutil.RepositoryUtil;

public class ModelGeneratorTest {
  
  @Test
  public void testAnnotationModel(){
    ModelGenerator g = spy(new ModelGenerator(null));

    Graph p1 = mock(Graph.class);
    Graph p2 = mock(Graph.class);
    Set<Graph> props = new HashSet<Graph>();
    props.add(p1);
    props.add(p2);
    try{
      doReturn(null).when(g).processProperty(any(Graph.class), any(Element.class));
      String expected = "" +
      "<model/>";
      DataComparator dc = new DataComparator();
      Element result = g.getPropertyAnnotationModel(props);
      assertTrue(dc.sameAs(expected, g.toString(result)));
      verify(g, times(2)).processProperty(any(Graph.class), any(Element.class));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  
  private void addDomain(Graph p, String domain){
    ResourceUtil.addStatement(
        p.getBaseURI(), 
        Rdfs.domain, 
        domain, 
        new ValueFactoryImpl(), 
        p);
  }
  @Test
  public void testProcessProperty(){
    ModelGenerator g = new ModelGenerator(null);

    Graph p = GraphLoader.load(Namespace.device + "propertySingleValueFormFieldProperty", 
        GraphData.property("SingleValueFormFieldProperty", Namespace.device, "Device", "SomeRange"));
    addDomain(p, Namespace.device + "PhysicalDevice");
    addDomain(p, Namespace.device + "PhysicalSubDevice");
    addDomain(p, Namespace.device + "SemanticDevice");
    addDomain(p, Namespace.device + "SemanticSubDevice");

    Element response = new Element("model");
    try{

      Element result = g.processProperty(p, response);
      String expected = "" +
      "  <property>\n" + 
      "    <info>\n" + 
      "      <type>SingleValueFormFieldProperty</type>\n" + 
      "      <uri>http://localhost/ontologies/Device.owl#propertySingleValueFormFieldProperty</uri>\n" + 
      "      <prefix>device</prefix>\n" + 
      "      <localName>propertySingleValueFormFieldProperty</localName>\n" + 
      "    </info>\n" + 
      "    <domain>\n" + 
      "      <uri>http://localhost/ontologies/Device.owl#Device</uri>\n" + 
      "      <uri>http://localhost/ontologies/Device.owl#PhysicalDevice</uri>\n" + 
      "      <uri>http://localhost/ontologies/Device.owl#PhysicalSubDevice</uri>\n" + 
      "      <uri>http://localhost/ontologies/Device.owl#SemanticDevice</uri>\n" + 
      "      <uri>http://localhost/ontologies/Device.owl#SemanticSubDevice</uri>\n" + 
      "    </domain>\n" + 
      "    <range>\n" + 
      "      <uri>http://localhost/ontologies/Device.owl#SomeRange</uri>\n" + 
      "    </range>\n" + 
      "  </property>\n"; 
      DataComparator dc = new DataComparator();
      assertTrue(dc.sameAs(expected, g.toString(result)));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Test 
  public void testEmptyTree(){
    String location = "emptyTree";
    AOMRepository repo = RepositoryFactory.local(location);

    ModelGenerator g = spy(new ModelGenerator(repo));

    String result = g.getDeviceTree();
    assertEquals(null, result);
    verify(g).tree(Device.Device.stringValue(), true);

    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void testClassNode(){
    String n = "" +  
    "<classNode>\n" + 
    "  <info>\n" +
    "    <uri>uri</uri>\n" +
    "    <prefix>prefix</prefix>\n" +
    "    <localName>localName</localName>\n" +
    "  </info>\n" +
    "</classNode>\n"; 

    DataComparator dc = new DataComparator();

    Element expected = dc.readXML(n);
    Element result = new ModelGenerator(null).getClassNode("uri", "prefix", "localName");

    assertTrue(dc.sameAs(expected, result));
  }

  @Test
  public void testInstanceNode(){
    String nt = "" +  
    "<instanceNode>\n" + 
    "  <info>\n" +
    "    <uri>uri</uri>\n" +
    "    <prefix>prefix</prefix>\n" +
    "    <localName>localName</localName>\n" +
    "    <static>true</static>\n" +
    "  </info>\n" +
    "</instanceNode>\n"; 

    String nf = "" +  
    "<instanceNode>\n" + 
    "  <info>\n" +
    "    <uri>uri</uri>\n" +
    "    <prefix>prefix</prefix>\n" +
    "    <localName>localName</localName>\n" +
    "    <static>false</static>\n" +
    "  </info>\n" +
    "</instanceNode>\n"; 

    DataComparator dc = new DataComparator();

    Element expectedT = dc.readXML(nt);
    Element expectedF = dc.readXML(nf);
    Element resultT = new ModelGenerator(null).getInstanceNode("uri", "prefix", "localName", true);
    Element resultF = new ModelGenerator(null).getInstanceNode("uri", "prefix", "localName", false);

    assertTrue(dc.sameAs(expectedT, resultT));
    assertTrue(dc.sameAs(expectedF, resultF));
  }


  @Test
  public void testInstanceNodeType(){
    String tmp = "" +  
    "<instanceNode>\n" + 
    "  <info>\n" +
    "    <uri>uri</uri>\n" +
    "    <prefix>prefix</prefix>\n" +
    "    <localName>localName</localName>\n" +
    "    <static>false</static>\n" +
    "    <type>template</type>\n" +
    "  </info>\n" +
    "</instanceNode>\n"; 

    String rnt = "" +  
    "<instanceNode>\n" + 
    "  <info>\n" +
    "    <uri>uri</uri>\n" +
    "    <prefix>prefix</prefix>\n" +
    "    <localName>localName</localName>\n" +
    "    <static>false</static>\n" +
    "    <type>runtime</type>\n" +
    "  </info>\n" +
    "</instanceNode>\n"; 

    String tst = "" +  
    "<instanceNode>\n" + 
    "  <info>\n" +
    "    <uri>uri</uri>\n" +
    "    <prefix>prefix</prefix>\n" +
    "    <localName>localName</localName>\n" +
    "    <static>false</static>\n" +
    "    <type>testing</type>\n" +
    "  </info>\n" +
    "</instanceNode>\n"; 

    DataComparator dc = new DataComparator();

    Element expectedTMP = dc.readXML(tmp);
    Element expectedRNT = dc.readXML(rnt);
    Element expectedTST = dc.readXML(tst);
    Element resultNull = new ModelGenerator(null).getInstanceNode(
        "uri", 
        "prefix", 
        "localName", 
        false, 
        null,
        null);
    assertTrue(dc.sameAs(expectedTST, resultNull));

    Element resultTmpNull = new ModelGenerator(null).getInstanceNode(
        "uri", 
        "prefix", 
        "localName", 
        false, 
        null,
    "any value");
    assertTrue(dc.sameAs(expectedTST, resultTmpNull));

    Element resultTmp = new ModelGenerator(null).getInstanceNode(
        "uri", 
        "prefix", 
        "localName", 
        false, 
        "true",
    "any value");
    assertTrue(dc.sameAs(expectedTMP, resultTmp));

    Element resultRnt = new ModelGenerator(null).getInstanceNode(
        "uri", 
        "prefix", 
        "localName", 
        false, 
        "false",
    "false");
    assertTrue(dc.sameAs(expectedRNT, resultRnt));

    Element resultTst = new ModelGenerator(null).getInstanceNode(
        "uri", 
        "prefix", 
        "localName", 
        false, 
        "false",
    "true");
    assertTrue(dc.sameAs(expectedTST, resultTst));

  }

  @Test 
  public void testTreeInstances(){
    String location = "treeInstances";
    AOMRepository repo = RepositoryFactory.local(location);

    ModelGenerator g = spy(new ModelGenerator(repo));

    Graph t = GraphLoader.load(
        "", 
        GraphData.propertyTaxonomy());
    t.add(GraphLoader.load(
        "", 
        GraphData.deviceClass()));
    t.add(GraphLoader.load(
        "", 
        GraphData.instances()));

    repo.store(t);

    String result = g.toString(g.tree(Device.PhysicalDevice.stringValue(), true));
    String expected = "" +
    "  <classNode>\n" +
    "    <info>\n" +
    "      <uri>http://localhost/ontologies/Device.owl#PhysicalDevice</uri>\n" +
    "      <prefix>device</prefix>\n" +
    "      <localName>PhysicalDevice</localName>\n" +
    "    </info>\n" +
    "    <classNode>\n" +
    "      <info>\n" +
    "        <uri>http://localhost/ontologies/Device.owl#PhysicalSubDevice</uri>\n" +
    "        <prefix>device</prefix>\n" +
    "        <localName>PhysicalSubDevice</localName>\n" +
    "      </info>\n" +
    "      <instanceNode>\n" +
    "        <info>\n" +
    "          <uri>http://localhost/ontologies/Device.owl#i3</uri>\n" +
    "          <prefix>device</prefix>\n" +
    "          <localName>i3</localName>\n" +
    "          <type>testing</type>\n" +
    "          <static>false</static>\n" +
    "        </info>\n" +
    "      </instanceNode>\n" +
    "    </classNode>\n" +
    "    <instanceNode>\n" +
    "      <info>\n" +
    "        <uri>http://localhost/ontologies/Device.owl#i1</uri>\n" +
    "        <prefix>device</prefix>\n" +
    "        <localName>i1</localName>\n" +
    "        <type>testing</type>\n" +
    "        <static>false</static>\n" +
    "      </info>\n" +
    "    </instanceNode>\n" +
    "  </classNode>\n";

    assertTrue(new DataComparator().sameAs(expected, result));

    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test 
  public void testTreeNoInstances(){
    String location = "treeNoInstances";
    AOMRepository repo = RepositoryFactory.local(location);

    ModelGenerator g = spy(new ModelGenerator(repo));

    Graph t = GraphLoader.load(
        "", 
        GraphData.propertyTaxonomy());
    t.add(GraphLoader.load(
        "", 
        GraphData.deviceClass()));
    t.add(GraphLoader.load(
        "", 
        GraphData.instances()));

    repo.store(t);

    String result = g.toString(g.tree(Device.PhysicalDevice.stringValue(), false));
    String expected = "" +
    "  <classNode>\n" +
    "    <info>\n" +
    "      <uri>http://localhost/ontologies/Device.owl#PhysicalDevice</uri>\n" +
    "      <prefix>device</prefix>\n" +
    "      <localName>PhysicalDevice</localName>\n" +
    "    </info>\n" +
    "    <classNode>\n" +
    "      <info>\n" +
    "        <uri>http://localhost/ontologies/Device.owl#PhysicalSubDevice</uri>\n" +
    "        <prefix>device</prefix>\n" +
    "        <localName>PhysicalSubDevice</localName>\n" +
    "      </info>\n" +
    "    </classNode>\n" +
    "  </classNode>\n";

    assertTrue(new DataComparator().sameAs(expected, result));

    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void testInstanceTreeDevice(){
    String location = "instanceTreeDevice";
    AOMRepository repo = RepositoryFactory.local(location);

    ModelGenerator g = spy(new ModelGenerator(repo));

    String device1URI = Namespace.device+"MyDeviceGraph";
    String device2URI = Namespace.device+"MyDevicePlain";
    String templateURI = Namespace.device+"MyTemplate";
    Graph d1 = GraphLoader.load(
        device1URI, 
        GraphData.physicalDevice(device1URI, false, templateURI));
    Graph d2 = GraphLoader.load(
        device2URI, 
        GraphData.device(device2URI));
    ResourceUtil.addStatement(
        Namespace.device + "Device", 
        Rdfs.subClassOf, 
        Namespace.model + "StaticTaxonomy", 
        repo.getValueFactory(), 
        d2);
    Graph t = GraphLoader.load(
        "", 
        GraphData.propertyTaxonomy());

    repo.store(d1);
    repo.store(d2);
    repo.store(t);

    String expectedGraph = "" +
    "<instanceNode>\n" +
    "  <info>\n" +
    "    <uri>http://localhost/ontologies/Device.owl#MyDeviceGraph</uri>\n" +
    "    <prefix>device</prefix>\n" +
    "    <localName>MyDeviceGraph</localName>\n" +
    "    <static>true</static>\n" +
    "    <type>runtime</type>\n" +
    "  </info>\n" +
    "  <objectProperty>\n" +
    "    <info>\n" +
    "      <uri>http://localhost/ontologies/Device.owl#PhysicalDevice</uri>\n" +
    "      <prefix>device</prefix>\n" +
    "      <localName>PhysicalDevice</localName>\n" +
    "      <static>false</static>\n" +
    "    </info>\n" +
    "    <popertyInfo>\n" +
    "      <uri>http://www.w3.org/1999/02/22-rdf-syntax-ns#type</uri>\n" +
    "      <prefix>rdf</prefix>\n" +
    "      <localName>type</localName>\n" +
    "    </popertyInfo>\n" +
    "  </objectProperty>\n" +
    "  <objectProperty>\n" +
    "    <info>\n" +
    "      <uri>http://localhost/ontologies/Device.owl#MyTemplate</uri>\n" +
    "      <prefix>device</prefix>\n" +
    "      <localName>MyTemplate</localName>\n" +
    "      <static>false</static>\n" +
    "    </info>\n" +
    "    <popertyInfo>\n" +
    "      <uri>http://localhost/ontologies/Device.owl#clonedFromTemplate</uri>\n" +
    "      <prefix>device</prefix>\n" +
    "      <localName>clonedFromTemplate</localName>\n" +
    "    </popertyInfo>\n" +
    "  </objectProperty>\n" +
    "  <datatypeProperty>\n" +
    "    <info>\n" +
    "      <value>false</value>\n" +
    "      <datatype>boolean</datatype>\n" +
    "    </info>\n" +
    "    <popertyInfo>\n" +
    "      <uri>http://localhost/ontologies/Device.owl#isDeviceTemplate</uri>\n" +
    "      <prefix>device</prefix>\n" +
    "      <localName>isDeviceTemplate</localName>\n" +
    "    </popertyInfo>\n" +
    "  </datatypeProperty>\n" +
    "</instanceNode>\n";

    String expectedPlain = "" +
    "<instanceNode>\n" +
    "  <info>\n" +
    "    <uri>http://localhost/ontologies/Device.owl#MyDevicePlain</uri>\n" +
    "    <prefix>device</prefix>\n" +
    "    <localName>MyDevicePlain</localName>\n" +
    "    <static>true</static>\n" +
    "    <type>testing</type>\n" +
    "  </info>\n" +
    "  <objectProperty>\n" +
    "    <info>\n" +
    "      <uri>http://localhost/ontologies/Device.owl#Device</uri>\n" +
    "      <prefix>device</prefix>\n" +
    "      <localName>Device</localName>\n" +
    "      <static>false</static>\n" +
    "    </info>\n" +
    "    <popertyInfo>\n" +
    "      <uri>http://www.w3.org/1999/02/22-rdf-syntax-ns#type</uri>\n" +
    "      <prefix>rdf</prefix>\n" +
    "      <localName>type</localName>\n" +
    "    </popertyInfo>\n" +
    "  </objectProperty>\n" +
    "</instanceNode>\n";
    DataComparator dc = new DataComparator();
    
    assertTrue(dc.sameAs(expectedGraph, g.getInstanceTree(device1URI)));
    assertTrue(dc.sameAs(expectedPlain, g.getInstanceTree(device2URI)));
    assertEquals(null, g.getInstanceTree(templateURI));
    repo.close();
    RepositoryUtil.clean(location);
  }


  @Test
  public void testInstanceTreeNotDevice(){
    String location = "instanceTreeNotDevice";
    AOMRepository repo = RepositoryFactory.local(location);

    ModelGenerator g = spy(new ModelGenerator(repo));

    String unitURI = Namespace.unit+"MyUnit";
    Graph u = GraphLoader.load(
        unitURI, 
        GraphData.unitValue(unitURI, "some val"));

    repo.store(u);

    String expectedGraph = "" +
    "<instanceNode>" +
    "  <info>" +
    "    <uri>http://localhost/ontologies/Unit.owl#MyUnit</uri>" +
    "    <prefix>unit</prefix>" +
    "    <localName>MyUnit</localName>" +
    "    <static>false</static>" +
    "  </info>" +
    "  <datatypeProperty>" +
    "    <info>" +
    "      <value>some val</value>" +
    "      <datatype>string</datatype>" +
    "    </info>" +
    "    <popertyInfo>" +
    "      <uri>http://localhost/ontologies/Unit.owl#value</uri>" +
    "      <prefix>unit</prefix>" +
    "      <localName>value</localName>" +
    "    </popertyInfo>" +
    "  </datatypeProperty>" +
    "  <objectProperty>" +
    "    <info>" +
    "      <uri>http://localhost/ontologies/Unit.owl#UnitValue</uri>" +
    "      <prefix>unit</prefix>" +
    "      <localName>UnitValue</localName>" +
    "      <static>false</static>" +
    "    </info>" +
    "    <popertyInfo>" +
    "      <uri>http://www.w3.org/1999/02/22-rdf-syntax-ns#type</uri>" +
    "      <prefix>rdf</prefix>" +
    "      <localName>type</localName>" +
    "    </popertyInfo>" +
    "  </objectProperty>" +
    "</instanceNode>";
    DataComparator dc = new DataComparator();
  
    assertTrue(dc.sameAs(expectedGraph, g.getInstanceTree(unitURI)));
    repo.close();
    RepositoryUtil.clean(location);
  }

  
  @Test
  public void testGetClassLiterals(){
    String location = "classsLiterals";
    AOMRepository repo = RepositoryFactory.local(location);

    ModelGenerator g = spy(new ModelGenerator(repo));

    String p11URI = Namespace.service + "Property_1";
    String p12URI = Namespace.service + "Property_2";
    String p13URI = Namespace.service + "Property_3";
    String p21URI = Namespace.service + "Property_4";
    String p22URI = Namespace.service + "Property_5";
    String p23URI = Namespace.service + "Property_6";
    String classURI1 = "http://class.uri/1";
    String classURI2 = "http://class.uri/2";
    Graph p11 = GraphLoader.load(
        p11URI, 
        GraphData.propertyModel(p11URI, classURI1, Namespace.xsd+"string"));
    Graph p12 = GraphLoader.load(
        p12URI, 
        GraphData.propertyModel(p12URI, classURI1, Namespace.xsd+"boolean"));
    Graph p13 = GraphLoader.load(
        p13URI, 
        GraphData.propertyModel(p13URI, classURI1, Namespace.device+"SomeClass"));

    Graph p21 = GraphLoader.load(
        p21URI, 
        GraphData.propertyModel(p21URI, classURI2, Namespace.xsd+"integer"));
    Graph p22 = GraphLoader.load(
        p22URI, 
        GraphData.propertyModel(p22URI, classURI2, Namespace.xsd+"float"));
    Graph p23 = GraphLoader.load(
        p23URI, 
        GraphData.propertyModel(p23URI, classURI2, Namespace.device+"AnotherClass"));

    repo.store(p11);
    repo.store(p12);
    repo.store(p13);
    repo.store(p21);
    repo.store(p22);
    repo.store(p23);

    String expected1 = "" +
    "<classLiterals>\n" +
    "  <propertyInfo>\n" +
    "    <uri>"+p11URI+"</uri>\n" +
    "    <prefix>service</prefix>\n" +
    "    <localName>Property_1</localName>\n" +
    "    <datatype>string</datatype>\n" +
    "  </propertyInfo>\n" +
    "  <propertyInfo>\n" +
    "    <uri>"+p12URI+"</uri>\n" +
    "    <prefix>service</prefix>\n" +
    "    <localName>Property_2</localName>\n" +
    "    <datatype>boolean</datatype>\n" +
    "  </propertyInfo>\n" +
    "</classLiterals>";

    String expected2 = "" +
    "<classLiterals>" +
    "  <propertyInfo>" +
    "    <uri>"+p21URI+"</uri>" +
    "    <prefix>service</prefix>" +
    "    <localName>Property_4</localName>" +
    "    <datatype>integer</datatype>" +
    "  </propertyInfo>" +
    "  <propertyInfo>" +
    "    <uri>"+p22URI+"</uri>" +
    "    <prefix>service</prefix>" +
    "    <localName>Property_5</localName>" +
    "    <datatype>float</datatype>" +
    "  </propertyInfo>" +
    "</classLiterals>";
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expected1, g.getClassLiterals(classURI1)));
    assertTrue(dc.sameAs(expected2, g.getClassLiterals(classURI2)));
    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void testGetClassLiteralsIncludingSuperClassesProperties(){
    String location = "classsLiteralsInherit";
    AOMRepository repo = RepositoryFactory.local(location);

    ModelGenerator g = spy(new ModelGenerator(repo));

    String p1URI = Namespace.service + "Property_1";
    String p2URI = Namespace.service + "Property_2";
    String p3URI = Namespace.service + "Property_3";
    String classURI1 = Namespace.device + "Device";
    String classURI2 = Namespace.device + "PhysicalDevice";
    String classURI3 = Namespace.device + "PhysicalSubDevice";
    
    Graph t = GraphLoader.load(
            "", 
            GraphData.propertyTaxonomy());
    Graph p1 = GraphLoader.load(
        p1URI, 
        GraphData.propertyModel(p1URI, classURI1, Namespace.xsd+"string"));
    Graph p2 = GraphLoader.load(
        p2URI, 
        GraphData.propertyModel(p2URI, classURI2, Namespace.xsd+"boolean"));
    Graph p3 = GraphLoader.load(
        p3URI, 
        GraphData.propertyModel(p3URI, classURI3, Namespace.xsd+"integer"));

    repo.store(t);
    repo.store(p1);
    repo.store(p2);
    repo.store(p3);

    String expected1 = "" +
    "<classLiterals>\n" +
    "  <propertyInfo>\n" +
    "    <uri>"+p1URI+"</uri>\n" +
    "    <prefix>service</prefix>\n" +
    "    <localName>Property_1</localName>\n" +
    "    <datatype>string</datatype>\n" +
    "  </propertyInfo>\n" +
    "</classLiterals>";

    String expected2 = "" +
    "<classLiterals>" +
    "  <propertyInfo>\n" +
    "    <uri>"+p1URI+"</uri>\n" +
    "    <prefix>service</prefix>\n" +
    "    <localName>Property_1</localName>\n" +
    "    <datatype>string</datatype>\n" +
    "  </propertyInfo>\n" +
    "  <propertyInfo>\n" +
    "    <uri>"+p2URI+"</uri>\n" +
    "    <prefix>service</prefix>\n" +
    "    <localName>Property_2</localName>\n" +
    "    <datatype>boolean</datatype>\n" +
    "  </propertyInfo>\n" +
    "</classLiterals>";


    String expected3 = "" +
    "<classLiterals>" +
    "  <propertyInfo>\n" +
    "    <uri>"+p1URI+"</uri>\n" +
    "    <prefix>service</prefix>\n" +
    "    <localName>Property_1</localName>\n" +
    "    <datatype>string</datatype>\n" +
    "  </propertyInfo>\n" +
    "  <propertyInfo>\n" +
    "    <uri>"+p2URI+"</uri>\n" +
    "    <prefix>service</prefix>\n" +
    "    <localName>Property_2</localName>\n" +
    "    <datatype>boolean</datatype>\n" +
    "  </propertyInfo>\n" +
    "  <propertyInfo>\n" +
    "    <uri>"+p3URI+"</uri>\n" +
    "    <prefix>service</prefix>\n" +
    "    <localName>Property_3</localName>\n" +
    "    <datatype>integer</datatype>\n" +
    "  </propertyInfo>\n" +
    "</classLiterals>";

    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs(expected1, g.getClassLiterals(classURI1)));
    assertTrue(dc.sameAs(expected2, g.getClassLiterals(classURI2)));
    assertTrue(dc.sameAs(expected3, g.getClassLiterals(classURI3)));
    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test 
  public void testEmptyTypes(){
    String location = "emptyTypes";
    AOMRepository repo = RepositoryFactory.local(location);

    ModelGenerator g = spy(new ModelGenerator(repo));

    String result = g.getDeviceTypes();
    DataComparator dc = new DataComparator();
    assertTrue(dc.sameAs("<DeviceTypes/>", result));

    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test 
  public void testGetDeviceTypes(){
    String location = "deviceTypes";
    AOMRepository repo = RepositoryFactory.local(location);

    ModelGenerator g = spy(new ModelGenerator(repo));

    Graph t = GraphLoader.load(
        "", 
        GraphData.propertyTaxonomy());
    t.add(GraphLoader.load(
        "", 
        GraphData.deviceClass()));
    t.add(GraphLoader.load(
        "", 
        GraphData.instances()));

    repo.store(t);

    String result = g.getDeviceTypes();
    String expected = "" +
    "<DeviceTypes>\n" +
    "  <DeviceType name=\"Device\">\n" +
    "    <DeviceType name=\"PhysicalDevice\">\n" +
    "      <DeviceType name=\"PhysicalSubDevice\"/>\n" +
    "    </DeviceType>\n" +
    "    <DeviceType name=\"SemanticDevice\">\n" +
    "      <DeviceType name=\"SemanticSubDevice\"/>\n" +
    "    </DeviceType>\n" +
    "  </DeviceType>\n" +
    "</DeviceTypes>";

    assertTrue(new DataComparator().sameAs(expected, result));

    repo.close();
    RepositoryUtil.clean(location);
  }


}
