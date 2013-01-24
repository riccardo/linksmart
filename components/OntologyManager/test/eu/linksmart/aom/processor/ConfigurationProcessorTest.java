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
import eu.linksmart.aom.ontology.model.Configuration;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.processor.ConfigurationProcessor;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.RepositoryUtil;
import eu.linksmart.aom.testutil.StubData;

public class ConfigurationProcessorTest extends StubData {
  @Test
  public void testProcess(){
    ConfigurationProcessor p = spy(new ConfigurationProcessor(repositoryStub()));
    String deviceURI = "http://my.device.uri";

    doReturn(new Graph("http://stubbed")).when(p).processFile(any(String.class), any(Element.class), any(Graph.class));

    Graph d = p.process(deviceURI, DataLoader.xmlToString("test/resources/configuration/config.xml"));
    Graph c = d.subGraph(Device.hasConfiguration);
    
    assertEquals(
        c, 
        GraphLoader.load(
            c.getBaseURI(), 
            GraphData.config(deviceURI, d.value(Device.hasConfiguration))));
    
    verify(p, times(2)).processFile(any(String.class), any(Element.class), any(Graph.class));
  }

  @Test
  public void testProcessFile() throws Exception {
    ConfigurationProcessor p = spy(new ConfigurationProcessor(repositoryStub()));
    String configURI = "http://my.device.uri";

    
    XPath xp = XPath.newInstance("//AssociatedFile[1]");

    Graph f = p.processFile(
        configURI, 
        (Element)xp.selectSingleNode(DataLoader.parse("test/resources/configuration/config.xml")),
        new Graph(configURI));

    assertEquals(
        f, 
        GraphLoader.load(
            f.getBaseURI(), 
            GraphData.configFile(configURI, f.value(Configuration.configurationFile))));
  }
}
