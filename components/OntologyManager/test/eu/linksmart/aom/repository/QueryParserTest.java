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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import eu.linksmart.aom.repository.QueryParser;

public class QueryParserTest {
  @Test 
  public void testTranslate() throws Exception {
    String AOMQuery = "" +
    "device:hasService/service:hasInput/service:quality;unit:required_value, \n" +
    "device:hasConfiguration\n";

    String SPARQLQuery = "" +
    "SELECT DISTINCT ?AOMDevice WHERE { \n" +
    "  ?AOMDevice device:hasService ?device_hasService_1. \n" +
    "  ?device_hasService_1 service:hasInput ?service_hasInput_2. \n" +
    "  ?service_hasInput_2 service:quality unit:required_value. \n" +
    "  ?AOMDevice device:hasConfiguration ?device_hasConfiguration_3. \n" +
    "}";
    
    assertEquals(SPARQLQuery, new QueryParser().translate(AOMQuery));

  
    String AOMQueryLiteral = "" +
    "device:hasService/service:serviceOperation;\"some value\"^^xsd:string, \n";

    String SPARQLQueryLiteral = "" +
    "SELECT DISTINCT ?AOMDevice WHERE { \n" +
    "  ?AOMDevice device:hasService ?device_hasService_1. \n" +
    "  ?device_hasService_1 service:serviceOperation \"some value\"^^xsd:string. \n" +
    "}";
    
    assertEquals(SPARQLQueryLiteral, new QueryParser().translate(AOMQueryLiteral));

    String AOMQueryMultiple = "" +
    "device:hasService/service:hasInput/service:quality;unit:required_value, \n" +
    "device:hasService/service:hasInput/service:quality;unit:another_value, \n" +
    "device:hasConfiguration\n";

    String SPARQLQueryMultiple = "" +
    "SELECT DISTINCT ?AOMDevice WHERE { \n" +
    "  ?AOMDevice device:hasService ?device_hasService_1. \n" +
    "  ?device_hasService_1 service:hasInput ?service_hasInput_2. \n" +
    "  ?service_hasInput_2 service:quality unit:required_value. \n" +
    "  ?AOMDevice device:hasService ?device_hasService_3. \n" +
    "  ?device_hasService_3 service:hasInput ?service_hasInput_4. \n" +
    "  ?service_hasInput_4 service:quality unit:another_value. \n" +
    "  ?AOMDevice device:hasConfiguration ?device_hasConfiguration_5. \n" +
    "}";
    
    assertEquals(SPARQLQueryMultiple, new QueryParser().translate(AOMQueryMultiple));

  }

  @Test 
  public void testTranslateQueryWithDoubleDotLiteral() throws Exception {
    String AOMQuery = "" +
    "device:hasService/service:hasInput/service:quality;\"my value\"^^xsd:string, \n" + 
    "device:hasService/service:hasInput/service:quality;\"my:value:is:strange\"^^xsd:string";

    String SPARQLQuery = "" +
    "SELECT DISTINCT ?AOMDevice WHERE { \n" +
    "  ?AOMDevice device:hasService ?device_hasService_1. \n" +
    "  ?device_hasService_1 service:hasInput ?service_hasInput_2. \n" +
    "  ?service_hasInput_2 service:quality \"my value\"^^xsd:string. \n" +
    "  ?AOMDevice device:hasService ?device_hasService_3. \n" +
    "  ?device_hasService_3 service:hasInput ?service_hasInput_4. \n" +
    "  ?service_hasInput_4 service:quality \"my:value:is:strange\"^^xsd:string. \n" +
    "}";
    
    assertEquals(SPARQLQuery, new QueryParser().translate(AOMQuery));
  }

  @Test 
  public void testTranslateService() throws Exception {
    String AOMServiceQuery = "" +
    "service:hasInput/service:quality;unit:required_value, \n" +
    "service:operationName\n";

    String AOMDeviceQuery = "" +
    "device:hasHardware/hardware:hasDisplay;hardware:required_value, \n" +
    "device:PID\n";

    String SPARQLQuery = "" +
    "SELECT DISTINCT ?AOMDevice ?AOMService WHERE { \n" +
    "  ?AOMDevice device:hasService ?AOMService. \n" +
    "  ?AOMDevice device:isDeviceTemplate \"false\"^^xsd:boolean. \n" +
    "  ?AOMDevice device:hasHardware ?device_hasHardware_3. \n" +
    "  ?device_hasHardware_3 hardware:hasDisplay hardware:required_value. \n" +
    "  ?AOMDevice device:PID ?device_PID_4. \n"+ 
    "  ?AOMService service:hasInput ?service_hasInput_1. \n" +
    "  ?service_hasInput_1 service:quality unit:required_value. \n" +
    "  ?AOMService service:operationName ?service_operationName_2. \n" +
    "}";

    assertEquals(SPARQLQuery, new QueryParser().translateService(AOMServiceQuery, AOMDeviceQuery));

    String AOMServiceQueryMultiple = "" +
    "service:hasInput/service:quality;unit:required_value, \n" +
    "service:hasInput/service:quality;unit:another_value, \n" +
    "service:operationName, \n" + 
    "service:operationName\n";

    String AOMDeviceQueryMultiple = "" +
    "device:hasHardware/hardware:hasDisplay;hardware:required_value, \n" +
    "device:hasHardware/hardware:hasDisplay;hardware:another_value, \n" +
    "device:PID\n";

    String SPARQLQueryMultiple = "" +
    "SELECT DISTINCT ?AOMDevice ?AOMService WHERE { \n" +
    "  ?AOMDevice device:hasService ?AOMService. \n" +
    "  ?AOMDevice device:isDeviceTemplate \"false\"^^xsd:boolean. \n" +
    "  ?AOMDevice device:hasHardware ?device_hasHardware_5. \n" +
    "  ?device_hasHardware_5 hardware:hasDisplay hardware:required_value. \n" +
    "  ?AOMDevice device:hasHardware ?device_hasHardware_6. \n" +
    "  ?device_hasHardware_6 hardware:hasDisplay hardware:another_value. \n" +
    "  ?AOMDevice device:PID ?device_PID_7. \n"+ 
    "  ?AOMService service:hasInput ?service_hasInput_1. \n" +
    "  ?service_hasInput_1 service:quality unit:required_value. \n" +
    "  ?AOMService service:hasInput ?service_hasInput_2. \n" +
    "  ?service_hasInput_2 service:quality unit:another_value. \n" +
    "  ?AOMService service:operationName ?service_operationName_3. \n" +
    "  ?AOMService service:operationName ?service_operationName_4. \n" +
    "}";

    assertEquals(SPARQLQueryMultiple, new QueryParser().translateService(AOMServiceQueryMultiple, AOMDeviceQueryMultiple));

  }


  @Test 
  public void testParse() throws Exception {
    String AOMQuery = "" +
    "device:hasService/service:hasInput/service:quality;unit:required_value, \n" +
    "device:hasConfiguration, \n";

    List<List<String>> expected = new ArrayList<List<String>>(); 

    expected.add(
        Arrays.asList("device:hasService", "service:hasInput", "service:quality;unit:required_value"));
    expected.add(
        Arrays.asList("device:hasConfiguration"));
    
    assertEquals(expected, new QueryParser().parse(AOMQuery));
  }

  @Test 
  public void testParseValue() throws Exception {
    String AOMQuery = "" +
    "device:hasService/service:hasInput/service:quality;unit:required_value," +
    "device:hasConfiguration";

    Map<String, List<String>> expected = new HashMap<String, List<String>>(); 

    expected.put("unit:required_value",
        Arrays.asList("device:hasService", "service:hasInput", "service:quality"));
    expected.put(null,
        Arrays.asList("device:hasConfiguration"));
    
    assertEquals(expected, new QueryParser().parseValue(AOMQuery));
  }

}
