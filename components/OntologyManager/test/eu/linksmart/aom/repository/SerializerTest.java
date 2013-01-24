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

package eu.linksmart.aom.repository;

import static org.junit.Assert.assertEquals;

import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Test;
import org.openrdf.model.impl.ValueFactoryImpl;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.schema.ResourceProperty;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.Serializer;
import eu.linksmart.aom.testutil.DataLoader;

public class SerializerTest {

  @Test
  public void testSerialize(){
    String deviceURI = "http://device.uri#d";
    String propertyURI = "http://property.uri#p";
    String valueURI = "http://value.uri#v";
    Graph g = new Graph(deviceURI);
    ResourceUtil.addStatement(
        deviceURI, 
        new ResourceProperty(propertyURI), 
        valueURI, 
        new ValueFactoryImpl(), 
        g);
    String rdf = Serializer.serialize(g);
    
    Element root = DataLoader.parseString(rdf);
    Namespace ns = Namespace.getNamespace("rdf", eu.linksmart.aom.ontology.model.Namespace.rdf);
    Namespace pns = Namespace.getNamespace("http://property.uri#");
    assertEquals("RDF", root.getName());
    assertEquals(deviceURI, root.getChild("Description", ns).getAttributeValue("about", ns));
    assertEquals(valueURI, root.getChild("Description", ns).getChild("p", pns).getAttributeValue("resource", ns));
    
  }
}
