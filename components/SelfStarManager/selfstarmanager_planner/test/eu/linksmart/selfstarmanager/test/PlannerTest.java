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
 * Copyright (C) 2006-2010 
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
package eu.linksmart.selfstarmanager.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.PrintStream;

import org.junit.Test;
import org.osgi.framework.Bundle;

import eu.linksmart.selfstar.aql.db.CachingIterator;
import eu.linksmart.selfstar.aql.db.Schema;
import eu.linksmart.selfstar.aql.db.Table;
import eu.linksmart.selfstar.aql.db.Tuple;
import eu.linksmart.selfstarmanager.gm.planner.PlanProblemGenerator;

public class PlannerTest {
	@Test
	public void testSetup() {
		assertEquals(1, 1);
	}
	
	@Test
	public void testProblemStateGeneration() throws Exception{
		// retrieve table from aql: deviceid, id, symbolicname
		
		System.out.println("Testing pddl problem generation.");
		Schema cfgschema=new Schema("configschema");
		cfgschema.addField("DeviceID", String.class);
		cfgschema.addField("ID", Long.class);
		cfgschema.addField("SymbolicName", String.class);
		cfgschema.addField("State", Integer.class);
		Table cfgtable=new Table("Configuration",cfgschema);
		Tuple t = cfgtable.addTuple();
		t.setValue("DeviceID", "device1");
		t.setValue("ID", 3L);
		t.setValue("SymbolicName", "HTTP.BT");
		t.setValue("State", Bundle.ACTIVE);
		t = cfgtable.addTuple();
		t.setValue("DeviceID", "device1");
		t.setValue("ID", 2L);
		t.setValue("SymbolicName", "HTTP.TCP");
		t.setValue("State", Bundle.RESOLVED);
		t = cfgtable.addTuple();
		t.setValue("DeviceID", "device2");
		t.setValue("ID", 2L);
		t.setValue("SymbolicName", "HTTP.BT");
		t.setValue("State",Bundle.ACTIVE);
		t = cfgtable.addTuple();
		t.setValue("DeviceID", "device2");
		t.setValue("ID", 2L);
		t.setValue("SymbolicName", "HTTP.UDP");
		t.setValue("State",Bundle.RESOLVED);
		
		
		PlanProblemGenerator.generatePlan(new CachingIterator(cfgtable.iterator()),new CachingIterator(cfgtable.iterator()));
		
		// now test generation of results
		
		
		
		
		
		


		
	}
	@Test
	public void testProblemStateGeneration2() throws Exception{
		// retrieve table from aql: deviceid, id, symbolicname
		
		System.out.println("\n\nTesting pddl problem generation with protocols.");
		
		Table initialext = makeTable("test1", new String[]{"ClientDID", "ServerDID", "ClientID", "Address", "Protocol"},
				new String[][]{
					new String[]{"d77e6b53-f96a-4ce4-afa3-37d1dd9a00cb", 
								"d77e6b53-f96a-4ce4-afa3-37d1dd9a00cb", 
								"24",
								"127.0.0.1:8080",
								"tcp"}
				});
		Table goalext = makeTable("test2", new String[]{"ClientDID", "ServerDID", "ClientID", "Address", "Protocol"},
				new String[][]{
					new String[]{"d77e6b53-f96a-4ce4-afa3-37d1dd9a00cb", 
								"d77e6b53-f96a-4ce4-afa3-37d1dd9a00cb", 
								"24",
								"127.0.0.1:8080",
								"udp"}
				});

		String did="d77e6b53-f96a-4ce4-afa3-37d1dd9a00cb";
		Table initialcfg = makeTypedTable("bundlestmp", 
				new String[]{"DeviceID", "ID", "State", "SymbolicName"},
				new Class[]{String.class, Long.class, Integer.class, String.class},
				new String[][]{
					new String[]{did, "15", "32", "sc_udp_protocol"},
					new String[]{did, "14", "32", "sc_tcp_protocol"},
					
				});
		
				
		
		
		//String basedir="/Users/ingstrup/Documents/workspaces/"/selfstarmanager_planner/resources/final-adlprob.pddl
		System.out.println(new File("./resources/").getAbsolutePath());
		PrintStream p = new PrintStream(new File("./resources/generatedproblem.pddl"));
		PlanProblemGenerator.setFile(p);
		
		PlanProblemGenerator.generateProtocolPlan(
				new CachingIterator(initialcfg.iterator()),
				new CachingIterator(initialcfg.iterator()),
				new CachingIterator(initialext.iterator()),
				new CachingIterator(goalext.iterator()));
		
		// now test generation of results
		
	}

	@Test
	public void testEscaping(){
		System.out.println("***Testing escape and unescape methods**");
		String escaped,unescaped;
		String[] teststrings = new String[]{
				"org.apache.log4j", 
				"somename0with00zeroes001andones001and10020010otherstuff02001",
				"hej"
		};
		for (String test:teststrings){
			//test="somename0with00zeroes001andones001and10020010otherstuff02001";

			System.out.println("\nTeststring="+test);
			escaped = PlanProblemGenerator.escapeName(test);
			unescaped = PlanProblemGenerator.unescapeName(escaped);
			System.out.println("Escaped   ="+escaped);
			System.out.println("Unescaped ="+unescaped);
			System.out.println("Original  ="+test);
			System.out.flush();
			assertEquals(test,unescaped);
		}
	}
	
	Table makeTable(String tablename, String[] headers, String[][] values){
		Table rval=null;
		try{
			Schema s = new Schema(tablename+"Schema");
			for (String name: headers)
				s.addField(name, String.class);
			rval = new Table(tablename, s);
			for (String[] row: values){
				Tuple t = rval.addTuple();
				for(int i=0;i<row.length;i++){
					t.setValue(i, row[i]);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return rval;
	}

	@SuppressWarnings("rawtypes")
	Table makeTypedTable(String tablename, String[] headers, Class[] types, String[][] values){
		Table rval=null;
		try{
			Schema s = new Schema(tablename+"Schema");
			for (int i=0;i<headers.length;i++)
				s.addField(headers[i], types[i]);
			rval = new Table(tablename, s);
			for (String[] row: values){
				Tuple t = rval.addTuple();
				for(int i=0;i<row.length;i++){
					Object value = row[i];
					if (types[i].equals(Long.class))
						value=Long.parseLong((String) value);
					if(types[i].equals(Integer.class))
						value=Integer.parseInt((String) value);
					t.setValue(i, value);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return rval;
	}
	
	
}
