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

package eu.linksmart.aom.discovery;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.vocabulary.XMLSchema;


import eu.linksmart.aom.discovery.DeviceDiscovery;
import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.processor.DiscoveryProcessor;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.RepositoryUtil;

public class DeviceDiscoveryTest {


  @Test
  public void testResolveDeviceBestMatch(){
    String location = "resolve1";
    AOMRepository repo = RepositoryFactory.local(location);
    
    DiscoveryProcessor dp = new DiscoveryProcessor(repo);
    
    String xml1 = DataLoader.xmlToString("test/resources/discovery/bluetooth.xml");
    String xml2 = DataLoader.xmlToString("test/resources/discovery/tellstick.xml");
    String xml3 = DataLoader.xmlToString("test/resources/discovery/rfidtag.xml");
    String xml4 = DataLoader.xmlToString("test/resources/discovery/sensor.xml");
    
    
    String dURI1 = Namespace.device + "device_1";
    Graph d1 = dp.process(dURI1, xml1);
    ResourceUtil.addStatement(
        d1.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d1);

    String dURI2 = Namespace.device + "device_2";
    Graph d2 = dp.process(dURI2, xml2);
    ResourceUtil.addStatement(
        d2.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d2);

    String dURI3 = Namespace.device + "device_3";
    Graph d3 = dp.process(dURI3, xml3);
    ResourceUtil.addStatement(
        d3.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d3);

    String dURI4 = Namespace.device + "device_4";
    Graph d4 = dp.process(dURI4, xml4);
    ResourceUtil.addStatement(
        d4.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d4);
    
    String dURI5 = Namespace.device + "device_5";
    Graph d5 = dp.process(dURI5, xml4);
    ResourceUtil.addStatement(
        d5.getBaseURI(), 
        Device.isDeviceTemplate, 
        "false", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d5);
    
    repo.store(d1);
    repo.store(d2);
    repo.store(d3);
    repo.store(d4);
    repo.store(d5);
    
    DeviceDiscovery d = new DeviceDiscovery(repo);
    assertEquals(d1, d.resolveDevice(xml1));
    assertEquals(d2, d.resolveDevice(xml2));
    assertEquals(d3, d.resolveDevice(xml3));
    assertEquals(d4, d.resolveDevice(xml4));

    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void testResolveNoMatch(){
    String location = "resolve2";
    AOMRepository repo = RepositoryFactory.local(location);
    
    DiscoveryProcessor dp = new DiscoveryProcessor(repo);
    
    String xml1 = DataLoader.xmlToString("test/resources/discovery/bluetooth.xml");
    String xml2 = DataLoader.xmlToString("test/resources/discovery/tellstick.xml");
    String xml3 = DataLoader.xmlToString("test/resources/discovery/rfidtag.xml");
    String xml4 = DataLoader.xmlToString("test/resources/discovery/sensor.xml");
    
    String dURI1 = Namespace.device + "device_1";
    Graph d1 = dp.process(dURI1, xml1);
    ResourceUtil.addStatement(
        d1.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d1);

    String dURI5 = Namespace.device + "device_5";
    Graph d5 = dp.process(dURI5, xml1);
    ResourceUtil.addStatement(
        d5.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d5);

    String dURI2 = Namespace.device + "device_2";
    Graph d2 = dp.process(dURI2, xml2);
    ResourceUtil.addStatement(
        d2.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d2);

    String dURI3 = Namespace.device + "device_3";
    Graph d3 = dp.process(dURI3, xml3);
    ResourceUtil.addStatement(
        d3.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d3);

   
    repo.store(d1);
    repo.store(d2);
    repo.store(d3);
    repo.store(d5);
    
    DeviceDiscovery d = new DeviceDiscovery(repo);
    
    Set<Graph> matched = new HashSet<Graph>(Arrays.asList(d1, d5));
    Graph errorNoMatch = d.errorGraph("no matches found");
    Graph errorMultiMatch = d.errorGraph("multiple matches found", matched);
    assertEquals(errorMultiMatch, d.resolveDevice(xml1));
    assertEquals(d2, d.resolveDevice(xml2));
    assertEquals(d3, d.resolveDevice(xml3));
    assertEquals(errorNoMatch, d.resolveDevice(xml4));

    repo.close();
    RepositoryUtil.clean(location);
  }
  

  @Test
  public void testResolveBetterMatch(){
    String location = "resolve3";
    AOMRepository repo = RepositoryFactory.local(location);
    
    DiscoveryProcessor dp = new DiscoveryProcessor(repo);
    
    String xml1 = DataLoader.xmlToString("test/resources/discovery/bluetooth.xml");
    String xml2 = DataLoader.xmlToString("test/resources/discovery/bluetooth-less.xml");

    String dURI1 = Namespace.device + "device_1";
    Graph d1 = dp.process(dURI1, xml1);
    ResourceUtil.addStatement(
        d1.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d1);

    String dURI2 = Namespace.device + "device_2";
    Graph d2 = dp.process(dURI2, xml2);
    ResourceUtil.addStatement(
        d2.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d2);
    
    repo.store(d1);
    repo.store(d2);
    
    DeviceDiscovery d = new DeviceDiscovery(repo);
    assertEquals(d1, d.resolveDevice(xml1));
    assertEquals(d2, d.resolveDevice(xml2));

    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void testResolveBetterMatchMoreBest(){
    String location = "resolve4";
    AOMRepository repo = RepositoryFactory.local(location);
    
    DiscoveryProcessor dp = new DiscoveryProcessor(repo);
    
    String xml1 = DataLoader.xmlToString("test/resources/discovery/bluetooth.xml");
    String xml2 = DataLoader.xmlToString("test/resources/discovery/bluetooth-less.xml");

    String dURI1 = Namespace.device + "device_1";
    Graph d1 = dp.process(dURI1, xml1);
    ResourceUtil.addStatement(
        d1.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d1);

    String dURI2 = Namespace.device + "device_2";
    Graph d2 = dp.process(dURI2, xml1);
    ResourceUtil.addStatement(
        d2.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d2);
    
    String dURI3 = Namespace.device + "device_3";
    Graph d3 = dp.process(dURI3, xml2);
    ResourceUtil.addStatement(
        d3.getBaseURI(), 
        Device.isDeviceTemplate, 
        "true", 
        XMLSchema.BOOLEAN,
        repo.getValueFactory(),
        d3);
    
    repo.store(d1);
    repo.store(d2);
    repo.store(d3);
    
    DeviceDiscovery d = new DeviceDiscovery(repo);
    Set<Graph> matched = new HashSet<Graph>(Arrays.asList(d1, d2));
    Graph errorMultiMatch = d.errorGraph("multiple matches found", matched);
    assertEquals(errorMultiMatch, d.resolveDevice(xml1));
    assertEquals(d3, d.resolveDevice(xml2));

    repo.close();
    RepositoryUtil.clean(location);
  }
}
