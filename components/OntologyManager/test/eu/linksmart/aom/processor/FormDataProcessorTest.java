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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Event;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.processor.FormDataProcessor;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.StubData;

public class FormDataProcessorTest{
  @Test
  public void testProcess() throws Exception{
    
    String dURI = "http://device.uri/1";
    String pURI = "http://property.uri/1";
    
    String formData = "" + 
    "<formdata>" + 
    "  <rdftype>http://class.uri/1</rdftype>" + 
    "  <parent>" + 
    "    <uri>" + dURI + "</uri>" + 
    "    <relation>" + pURI + "</relation>" + 
    "  </parent>" + 
    "  <properties>" + 
    "    <property>" + 
    "      <name>http://property.uri/2</name>" + 
    "      <value>value1</value>" + 
    "      <dataType>string</dataType>" + 
    "    </property>" + 
    "    <property>" + 
    "      <name>http://property.uri/3</name>" + 
    "      <value>true</value>" + 
    "      <dataType>boolean</dataType>" + 
    "    </property>" + 
    "  </properties>" + 
    "</formdata>";
    
    AOMRepository repo = mock(AOMRepository.class);
    RepositoryConnection conn =mock(RepositoryConnection.class);
    RepositoryResult rr = mock(RepositoryResult.class);

    String expectedURI = "http://hardware.uri/1";

    doReturn(conn).when(repo).getConnection();
    doReturn(rr).when(conn).getStatements(new URIImpl(dURI), new URIImpl(pURI), (Value)null, false);
    doReturn(false).when(rr).hasNext();
    doReturn(expectedURI).when(repo).getURI(any(String.class)); 
    doReturn(new ValueFactoryImpl()).when(repo).getValueFactory(); 

    FormDataProcessor fdp = new FormDataProcessor(repo);

    assertEquals(fdp.store(formData, false), expectedURI );
    
    Graph expectedGraph = GraphLoader.load(dURI, GraphData.formData(dURI));
    verify(repo).store(expectedGraph);
    verify(conn).getStatements(new URIImpl(dURI), new URIImpl(pURI), (Value)null, false);
  }
  @Test
  public void testProcessNoParent(){
    
    String formData = "" + 
    "<formdata>" + 
    "  <rdftype>http://class.uri/1</rdftype>" + 
    "  <properties>" + 
    "    <property>" + 
    "      <name>http://property.uri/2</name>" + 
    "      <value>value1</value>" + 
    "      <dataType>string</dataType>" + 
    "    </property>" + 
    "    <property>" + 
    "      <name>http://property.uri/3</name>" + 
    "      <value>true</value>" + 
    "      <dataType>boolean</dataType>" + 
    "    </property>" + 
    "  </properties>" + 
    "</formdata>";
    
    AOMRepository repo = mock(AOMRepository.class);
    String expectedURI = "http://hardware.uri/1";
    doReturn(expectedURI).when(repo).getURI(any(String.class)); 
    doReturn(new ValueFactoryImpl()).when(repo).getValueFactory(); 
    FormDataProcessor fdp = new FormDataProcessor(repo);
    
    assertEquals(fdp.store(formData, true), expectedURI );
    
    Graph expectedGraph = GraphLoader.load(expectedURI, GraphData.formDataNoParent());
    verify(repo).store(expectedGraph);
  }
}
