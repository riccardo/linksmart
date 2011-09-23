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

import org.junit.Test;


public class DataComparatorTest {
  @Test
  public void testSameXMLElementName(){
    DataComparator dc = new DataComparator();
    
    assertFalse(dc.sameAs("<root1></root1>", "<root2></root2>"));
    assertFalse(dc.sameAs("<root xmlns=\"my-ns\"></root>", "<root></root>"));
    assertFalse(dc.sameAs("<root xmlns=\"my-ns\"></root>", "<root xmlns=\"another-ns\"></root>"));
    assertTrue(dc.sameAs("<root></root>", "<root></root>"));
    assertTrue(dc.sameAs("<root xmlns=\"my-ns\"></root>", "<root xmlns=\"my-ns\"></root>"));
  }

  @Test
  public void testSameXMLElementAttributes(){
    DataComparator dc = new DataComparator();
    
    assertFalse(dc.sameAs("<root a1=\"1\" a2=\"2\"/>", "<root a1=\"1\"/>"));
    assertFalse(dc.sameAs("<root a1=\"1\"/>", "<root a1=\"1\" a2=\"2\"/>"));
    assertFalse(dc.sameAs("<root a1=\"1\"/>", "<root a1=\"2\"/>"));
    assertTrue(dc.sameAs("<root a1=\"1\" a2=\"2\"/>", "<root a1=\"1\" a2=\"2\"/>"));
    assertTrue(dc.sameAs("<root a1=\"1\" a2=\"2\"/>", "<root a2=\"2\" a1=\"1\"/>"));
  }

  @Test
  public void testSameXMLElementStructure(){
    DataComparator dc = new DataComparator();

    String xml1 = 
      "<root>" +
      "  <elm1/>" +
      "  <elm2/>" +
      "</root>";
    String xml2 = 
      "<root>" +
      "  <elm2/>" +
      "  <elm1/>" +
      "</root>";
    String xml3 = 
      "<root>" +
      "  <elm2/>" +
      "</root>";
    assertTrue(dc.sameAs(xml1, xml1));
    assertTrue(dc.sameAs(xml1, xml2));
    assertFalse(dc.sameAs(xml1, xml3));
    
    String xml4 = 
      "<root>" +
      "  <elm1>" +
      "    <sub-elm1/>" +
      "    <sub-elm2>" +
      "      <sub-sub-elm1/>" +
      "      <sub-sub-elm2/>" +
      "    </sub-elm2>" +
      "  </elm1>" +
      "  <elm2/>" +
      "</root>";
    String xml5 = 
      "<root>" +
      "  <elm1>" +
      "    <sub-elm2>" +
      "      <sub-sub-elm1/>" +
      "      <sub-sub-elm2/>" +
      "    </sub-elm2>" +
      "    <sub-elm1/>" +
      "  </elm1>" +
      "  <elm2/>" +
      "</root>";
    String xml6 = 
      "<root>" +
      "  <elm1>" +
      "    <sub-elm2>" +
      "      <sub-sub-elm2/>" +
      "    </sub-elm2>" +
      "    <sub-elm1/>" +
      "  </elm1>" +
      "  <elm2/>" +
      "</root>";
    assertTrue(dc.sameAs(xml4, xml4));
    assertTrue(dc.sameAs(xml4, xml5));
    assertFalse(dc.sameAs(xml4, xml6));
  }
}
