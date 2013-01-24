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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.junit.Test;
import org.openrdf.model.impl.ValueFactoryImpl;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Discovery;
import eu.linksmart.aom.processor.DiscoveryProcessor;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.StubData;

public class DiscoveryProcessorTest extends StubData {
  @Test
  public void testProcess(){
    DiscoveryProcessor p = spy(new DiscoveryProcessor(repositoryStub()));
    String deviceURI = "http://my.device.uri";
    
    p.process(deviceURI, DataLoader.xmlToString("test/resources/discovery/tellstick.xml"));
    verify(p).processTellstick(any(String.class), any(Element.class));

    p.process(deviceURI, DataLoader.xmlToString("test/resources/discovery/rfidtag.xml"));
    verify(p).processRFIDTag(any(String.class), any(Element.class));

    p.process(deviceURI, DataLoader.xmlToString("test/resources/discovery/sensor.xml"));
    verify(p).processSensor(any(String.class), any(Element.class));

    p.process(deviceURI, DataLoader.xmlToString("test/resources/discovery/bluetooth.xml"));
    verify(p).processBlueTooth(any(String.class), any(Element.class));

    Graph x = p.process(deviceURI, DataLoader.xmlToString("test/resources/discovery/physicaldevicesource.xml"));
    verify(p).processPhysicalDeviceSource(any(String.class), any(Element.class));
}

  @Test
  public void testProcessTellstick(){
    DiscoveryProcessor p = spy(new DiscoveryProcessor(repositoryStub()));
    String deviceURI = "http://my.device.uri";
    
    Graph d = p.processTellstick(deviceURI, DataLoader.parse("test/resources/discovery/tellstick.xml"));
    Graph ts = d.subGraph(Device.hasDiscoveryInfo);
    
    assertEquals(
        ts, 
        GraphLoader.load(
            ts.getBaseURI(), 
            GraphData.tellstick(deviceURI, d.value(Device.hasDiscoveryInfo))));
  }

  @Test
  public void testProcessRFIDTag(){
    DiscoveryProcessor p = spy(new DiscoveryProcessor(repositoryStub()));
    String deviceURI = "http://my.device.uri";
    
    Graph d = p.processRFIDTag(deviceURI, DataLoader.parse("test/resources/discovery/rfidtag.xml"));
    Graph rfid = d.subGraph(Device.hasDiscoveryInfo);
    
    assertEquals(
        rfid, 
        GraphLoader.load(
            rfid.getBaseURI(), 
            GraphData.rfid(deviceURI, d.value(Device.hasDiscoveryInfo))));
  }

  @Test
  public void testProcessSensor(){
    DiscoveryProcessor p = spy(new DiscoveryProcessor(repositoryStub()));
    String deviceURI = "http://my.device.uri";
    
    Graph d = p.processSensor(deviceURI, DataLoader.parse("test/resources/discovery/sensor.xml"));
    Graph sensor = d.subGraph(Device.hasDiscoveryInfo);
    
    assertEquals(
        sensor, 
        GraphLoader.load(
            sensor.getBaseURI(), 
            GraphData.sensor(deviceURI, d.value(Device.hasDiscoveryInfo))));
  }

  @Test
  public void testProcessBlueTooth(){
    DiscoveryProcessor p = spy(new DiscoveryProcessor(repositoryStub()));
    doReturn(new Graph("http://service.mock")).when(p).processBlueToothService(any(String.class), any(Element.class), any(Graph.class));

    String deviceURI = "http://my.device.uri";
    
    Graph d = p.processBlueTooth(deviceURI, DataLoader.parse("test/resources/discovery/bluetooth.xml"));
    Graph bt = d.subGraph(Device.hasDiscoveryInfo);
    
    assertEquals(
        bt, 
        GraphLoader.load(
            bt.getBaseURI(), 
            GraphData.bluetooth(deviceURI, d.value(Device.hasDiscoveryInfo))));
    
    verify(p).processBlueToothServices(any(String.class), any(Element.class), any(Graph.class));
    verify(p, times(9)).processBlueToothService(any(String.class), any(Element.class), any(Graph.class));
  }

  @Test
  public void testProcessBlueToothService() throws Exception {
    DiscoveryProcessor p = spy(new DiscoveryProcessor(repositoryStub()));
    String discoURI = "http://my.disco.uri";
    
    XPath xp = XPath.newInstance("//bluetoothservice[1]");

    Graph bts = p.processBlueToothService(
        discoURI, 
        (Element)xp.selectSingleNode(DataLoader.parse("test/resources/discovery/bluetooth.xml")),
        new Graph(discoURI));
    
    assertEquals(
        bts, 
        GraphLoader.load(
            bts.getBaseURI(), 
            GraphData.bluetoothService(discoURI, bts.value(Discovery.btService))));
    
  }

  @Test
  public void testProcessPhysicalDeviceSource(){
    DiscoveryProcessor p = spy(new DiscoveryProcessor(repositoryStub()));
    String deviceURI = "http://my.device.uri";
    
    Graph d = p.processPhysicalDeviceSource(deviceURI, DataLoader.parse("test/resources/discovery/physicaldevicesource.xml"));
    Graph pds = d.subGraph(Device.hasDiscoveryInfo);
    
    assertEquals(
        pds, 
        GraphLoader.load(
            pds.getBaseURI(), 
            GraphData.physicalDeviceSource(deviceURI, d.value(Device.hasDiscoveryInfo))));
  }

}
