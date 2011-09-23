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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.linksmart.aom.discovery.PreconditionCardinality;

public class PreconditionCardinalityTest {

  @Test
  public void testCardinality(){
    String c1 = "3";
    PreconditionCardinality pc1 = new PreconditionCardinality(c1);
    assertEquals(3, pc1.exactly);

    String c2 = "3..6";
    PreconditionCardinality pc2 = new PreconditionCardinality(c2);
    assertEquals(3, pc2.min);
    assertEquals(6, pc2.max);
    
    String c3 = "123..678";
    PreconditionCardinality pc3 = new PreconditionCardinality(c3);
    assertEquals(123, pc3.min);
    assertEquals(678, pc3.max);
    
    String c4 = "0..M";
    PreconditionCardinality pc4 = new PreconditionCardinality(c4);
    assertEquals(0, pc4.min);
    assertEquals(-1, pc4.max);

    String c5 = "234..M";
    PreconditionCardinality pc5 = new PreconditionCardinality(c5);
    assertEquals(234, pc5.min);
    assertEquals(-1, pc5.max);

    String c_err1 = "wrong_one";
    PreconditionCardinality pc_err1 = new PreconditionCardinality(c_err1);
    assertEquals(0, pc_err1.min);
    assertEquals(-1, pc_err1.max);
    
    String c_err2 = "3..X";
    PreconditionCardinality pc_err2 = new PreconditionCardinality(c_err2);
    assertEquals(0, pc_err2.min);
    assertEquals(-1, pc_err2.max);

    String c_err3 = "X..3";
    PreconditionCardinality pc_err3 = new PreconditionCardinality(c_err3);
    assertEquals(0, pc_err3.min);
    assertEquals(-1, pc_err3.max);

    String c_err4 = "X..Y";
    PreconditionCardinality pc_err4 = new PreconditionCardinality(c_err4);
    assertEquals(0, pc_err4.min);
    assertEquals(-1, pc_err4.max);

    String c_err5 = "1...M";
    PreconditionCardinality pc_err5 = new PreconditionCardinality(c_err5);
    assertEquals(0, pc_err5.min);
    assertEquals(-1, pc_err5.max);

    String c_err6 = "1.M";
    PreconditionCardinality pc_err6 = new PreconditionCardinality(c_err6);
    assertEquals(0, pc_err6.min);
    assertEquals(-1, pc_err6.max);
  }
  
  @Test
  public void testIsSatisfied(){
    PreconditionCardinality pc1 = new PreconditionCardinality("3");
    assertTrue(pc1.isSatisfied(3));
    assertFalse(pc1.isSatisfied(4));
    assertFalse(pc1.isSatisfied(2));

    PreconditionCardinality pc2 = new PreconditionCardinality("3..5");
    assertTrue(pc2.isSatisfied(3));
    assertTrue(pc2.isSatisfied(4));
    assertTrue(pc2.isSatisfied(5));
    assertFalse(pc2.isSatisfied(2));
    assertFalse(pc2.isSatisfied(6));

    PreconditionCardinality pc3 = new PreconditionCardinality("3..M");
    assertTrue(pc3.isSatisfied(3));
    assertTrue(pc3.isSatisfied(800));
    assertFalse(pc3.isSatisfied(2));

    PreconditionCardinality pc4 = new PreconditionCardinality("0..M");
    assertTrue(pc4.isSatisfied(0));
    assertTrue(pc4.isSatisfied(800));

    PreconditionCardinality pc5 = new PreconditionCardinality("wrong one");
    assertTrue(pc5.isSatisfied(0));
    assertTrue(pc5.isSatisfied(800));
  }
}
