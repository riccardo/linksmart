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
package eu.linksmart.selfstar.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Hashtable;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import eu.linksmart.selfstar.aql.db.CachingIterator;
import eu.linksmart.selfstar.aql.db.QueryTree;
import eu.linksmart.selfstar.aql.db.RelationalOperators;
import eu.linksmart.selfstar.aql.db.Schema;
import eu.linksmart.selfstar.aql.db.Table;
import eu.linksmart.selfstar.aql.db.Tuple;
import eu.linksmart.selfstar.aql.db.TupleFilter;
import eu.linksmart.selfstar.aql.db.TupleIterator;
import eu.linksmart.selfstar.aql.distribution.TupleCoder;
import eu.linksmart.selfstar.aql.utils.Base64;

public class AQLTest {
	@Test
	public void testSetup() {
		assertEquals(1, 1);
	} 
	
	Table t;
	@Test
	public void testAQLSchemas() throws Exception{
		Schema s = new Schema("test:books");
		s.addField("author", String.class);
		s.addField("title", String.class);
		s.addField("copiesprinted", Integer.class);
		Table inventory = new Table("Books", s);
		Tuple t = s.newTuple();
		t.setValue(0, "Philip K. Dick");
		t.setValue(1, "Ubik");
		t.setValue(2, 2000);
		inventory.addTuple(t);
		t=inventory.addTuple();
		t.setValue("author","Charles Dickens");
		t.setValue("title", "David Copperfield");
		t.setValue("copiesprinted", 11235813);
		//System.out.println(inventory.toTabularString());	
		this.t=inventory;
	}
	
	@Test
	public void testTableSelection() throws Exception{
		testAQLSchemas();
		// try to get a subtable
		Table tsub=t.selectElements(new int[]{1,2}, t.getSchema().getIntFromName("copiesprinted"), 2000);
		System.out.println(tsub.toTabularString());
		assertEquals(2000, tsub.getTupleAt(0).getValue(1));
		assertEquals(1,tsub.size());
	}
	
	@Test
	public void testSelectionIterator() throws Exception {
		testAQLSchemas();
		System.out.println("Testing selection iterator");
		TupleFilter filter=new TupleFilter(){

			public boolean accepts(Tuple t) {
				return t.getValue("title").equals("Ubik");
			}
			
		};
		TupleIterator ti=RelationalOperators.selectionIterator(t.iterator(), filter);
		System.out.println(t.getTupleAt(0).getValue("title").equals("Ubik"));
		System.out.println(ti.hasNext());
		while(ti.hasNext())
			System.out.println(ti.next());
		ti=RelationalOperators.selectionIterator(t.iterator(), filter);
		assertEquals("Philip K. Dick", ti.next().getValue("author"));
	}
	
	@Test
	public void testCachingIterator() throws Exception{
		System.out.println("Testing caching iterator");
		testAQLSchemas();
		CachingIterator ci = new CachingIterator(t.iterator());
		System.out.println(ci.next().getValue("author"));
		ci.reset();
		int i=0;
		Tuple[] tups = new Tuple[2];
		while(ci.hasNext()){
			tups[i++]=ci.next();
		}
		assertEquals(2, i);
		assertEquals("Philip K. Dick", tups[0].getValue("author"));
		assertEquals("Charles Dickens", tups[1].getValue("author"));
	}
	
	@Test
	public void testEquiJoin() throws Exception{
		testAQLSchemas();
		System.out.println("Testing EQUIJOIN"); 
		Schema s = new Schema("shoppinglist");
		s.addField("author", String.class);
		s.addField("item", String.class);
		s.addField("copies2buy", Integer.class);
		Table t2=new Table(s);
		Tuple tp=t2.addTuple();
		tp.setValue("author", "Philip K. Dick");
		tp.setValue("item", "book");
		tp.setValue("copies2buy", 2);
		tp=t2.addTuple();
		tp.setValue("author", "any");
		tp.setValue("item", "scifibook");
		tp.setValue("copies2buy", 2000);
		TupleIterator it=RelationalOperators.equijoinIterator("author", t.iterator(), t2.iterator());
		if (it.hasNext()){
			tp=it.next();
			System.out.println(tp.getSchema());
			System.out.println(tp);
		}
		while(it.hasNext()){
			System.out.println(it.next());
		}
		
	}
	
	@Test
	public void testProjection() throws Exception{
		testAQLSchemas();
		System.out.println("Testing projection");
		// try to get a subtable
		Schema tgt=new Schema("sub");
		tgt.addField("author", String.class);
		TupleIterator ti=RelationalOperators.projectionIterator(tgt, t.iterator());
		System.out.println("iterator"+ti);
		while(ti.hasNext())
			System.out.println("element:"+ti.next());
		ti=RelationalOperators.projectionIterator(tgt, t.iterator());
		assertEquals(ti.next().getValue("author"),"Philip K. Dick");
		assertEquals(ti.next().getValue("author"),"Charles Dickens");
		//for (Tuple tup:Join.projectionIterator(tgt, t.iterator()))
		//		System.out.println(tup);

		//assertEquals(2000, tsub.getTupleAt(0).getValue(1));
		//assertEquals(1,tsub.size());
	}

	
	
	@Test
	public void testQueryTree() throws Exception{
		testAQLSchemas();
		System.out.println("Testing QueryTree.makeTableNode()");
		QueryTree tq = QueryTree.makeTableNode("Books");
		TupleIterator ti = tq.instantiate();
		while(ti.hasNext())
			System.out.println("element:"+ti.next());
		
		System.out.println("Testing makeSelectionNode (author=\"Philip K. Dick\")");
		QueryTree sq = QueryTree.makeSelectNode(tq, "author", "Philip K. Dick");
		ti = sq.instantiate();
		while(ti.hasNext())
			System.out.println("element:"+ti.next());
		System.out.println("Testing makeProjectionNode (retain author field)");
		QueryTree pq = QueryTree.makeProjectNode(new String[]{"author","title"}, tq);
		ti = pq.instantiate();
		pit(ti);
		System.out.println("\nTesting equijoin on author field of above two:");
		QueryTree ejq = QueryTree.makeEquiJoinNode(sq, pq, "author");
		pit(ejq.instantiate());
		ejq.print();
		System.out.println("Testing parser");
		tq = QueryTree.parseQuery(ejq.toString());
		tq.print();
		pq.setType(QueryTree.LOCAL);
		assertEquals(true,pq.isLocal());
		ejq.print();
		
	}

	@Test
	public void testCoding() throws Exception {
		System.out.println("Testing en- and de-coding of AQL tables/iterators to and from byte arrays");
		testAQLSchemas();
		
		ThreadMXBean threadData = ManagementFactory.getThreadMXBean( );
		long t2,t1=threadData.getCurrentThreadCpuTime();
		byte encoded[] = TupleCoder.encode(t);
		t2=threadData.getCurrentThreadCpuTime();
		double timedelta=((t2-t1)/1000000.0);			
		System.out.println("Encoded the table"+t.getName()+", size="+encoded.length+"bytes (time:"+timedelta+" ms)");
		t1=threadData.getCurrentThreadCpuTime();
		Table books=TupleCoder.decodeTable(encoded);
		t2=threadData.getCurrentThreadCpuTime();
		timedelta=((t2-t1)/1000000.0);			
		System.out.println("Decoded the bundle table, size="+encoded.length+" (time:"+timedelta+" ms)");
		System.out.println("Decoded table:\n"+books.toTabularString());	
		System.out.println("Testing en- and de-coding of QueryTree instances");
		QueryTree tq = QueryTree.makeTableNode("Books");
		TupleIterator ti = tq.instantiate();
		System.out.println("Testing makeSelectionNode (author=\"Philip K. Dick\")");
		QueryTree sq = QueryTree.makeSelectNode(tq, "author", "Philip K. Dick");
		encoded=TupleCoder.encode_query(sq);
		tq=TupleCoder.decode_query(encoded);
		System.out.println("Decoded and encoded:");
		System.out.println(sq.toString());
		System.out.println(tq.toString());
		System.out.println("Encoded and decoded equal?: "+sq.equals(tq));
		assertTrue(sq.equals(tq));
		
		System.out.println("Testing decoding with additional encoding to strings in Base64:");
		encoded = TupleCoder.encode(t);
		String b64_encoded = Base64.encodeObject(encoded);
		System.out.println("Encoded size:"+encoded.length+" in Base64:"+b64_encoded.length());
		books = TupleCoder.decodeTable((byte[])Base64.decodeToObject(b64_encoded));
		System.out.println("Decoded table:\n"+books.toTabularString());	
		
		
	}

	@Test
	public void testRename(){
		try {
		testAQLSchemas();
		System.out.println("Testing Rename operator");
		Hashtable<String,String> map = new Hashtable<String,String>();
		map.put("author", "forfatter");
		System.out.println(t.toTabularString());
		TupleIterator ti=RelationalOperators.renameIterator(map, t.iterator());
		System.out.println("Schema"+ti.getSchema());
		while(ti.hasNext())
			System.out.println("element: forfatter="+ti.next().getValue("forfatter"));
		ti=RelationalOperators.renameIterator(map, t.iterator());
		assertEquals(ti.next().getValue("forfatter"),"Philip K. Dick");
		assertEquals(ti.next().getValue("forfatter"),"Charles Dickens");
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	@Test
	public void testNaturalJoin() throws Exception {
		Table t1 = makeTable("test1", new String[]{"department", "name", "salary"},
				new String[][]{
					new String[]{"1", "john", "1200"},
					new String[]{"2", "john", "1202"},
					new String[]{"3", "peter", "1204"}
				});
		Table t2 = makeTable("test2", new String[]{"department", "name", "job"},
				new String[][]{
					new String[]{"1", "john", "janitor"},
					new String[]{"2", "john", "german"},
					new String[]{"3", "peter", "postalwoker"}
				});
		System.out.println("Table 1:"+t1.toTabularString());
		System.out.println("Table 2:"+t2.toTabularString());
		String query = "NATURALJOIN ( test1 test2)";
		TupleIterator ti = QueryTree.parseQuery(query).instantiate();
		System.out.println("Instantiated naturaljoin iterator over test1 and test2"+ti+" hasnext="+ti.hasNext());
		while(ti.hasNext())
			System.out.println(ti.next());
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
	
	void pit(TupleIterator ti){
		while(ti.hasNext())
			System.out.println("element:"+ti.next());

	}

	
	/*
	public void installBundleInspector() throws Exception{
		int port = 8080;
		String wsdir="/Users/ingstrup/Documents/workspaces/LinkSmart2";
		String fcmain=wsdir+"/flamenco_change_main/lib";
		System.setProperty("ASL.withShell", "true");
		System.setProperty("ASL.debug", "true");
		Framework framework = new FelixFramework();
		framework.setBundleLocations(new String[]{
					"file:"+wsdir+"/flamenco_change_main/lib/org.osgi.compendium.jar",
					"file:"+wsdir+"/flamenco_asl/exported/flamenco_asl.jar",
					"file:"+wsdir+"/BundleInspector/exported/eu.linksmart.bundleinspector_1.0.0.jar",
					// ASL should really take care of things (via resources/init.asl) from here
					// but it does not appear to start the bundles
					// OSGI general
//					"file:lib/org.apache.felix.shell.jar",
//					"file:lib/org.apache.felix.shell.tui.jar",
					"file:"+fcmain+"/org.apache.felix.scr.jar",
					"file:"+fcmain+"/org.apache.felix.eventadmin.jar",
//					"file:lib/org.apache.felix.log.jar",
//					// Flamenco-specific
//					"file:../../../sdk/flamenco/flamenco_optimizer/exported/flamenco_optimizer.jar",
//					"file:../../../sdk/flamenco/flamenco_planner/exported/flamenco_planner.jar",
//					"file:../../../sdk/flamenco/flamenco_semaps/exported/flamenco_semaps.jar",
//					"file:../../../sdk/flamenco/flamenco_reasoner/exported/flamenco_reasoner.jar",
					// Test-specific
//					"file:../socket_commands/exported/socket_commands.jar"
		});
		framework.start(new String[]{
			"org.osgi.service.http.port", "" + port,
			"org.osgi.service.http.port.secure", "8083",
			Constants.FRAMEWORK_STORAGE, "fw-cache"
			});
		
	}
	*/
	
	@Test
	public void testQuerySplitting() throws RecognitionException{
		System.out.println("Testing query splitting");
		String clientep = "LOCAL ( RENAME [TargetEndpoint->Address DeviceID->ClientDID]( ClientEndpoints ))";
		String serverep = "LOCAL ( RENAME [ServingEndpoint->Address DeviceID->ServerDID] (ServerEndpoints))";
		String query = "GLOBAL ( NATURALJOIN ( "+clientep+" "+serverep+"))";
		System.out.println("Testing channel query:"+query);
		System.out.println("Query:"+query);
		QueryTree qt = QueryTree.parseQuery(query);
		System.out.println("Query, parsed:"+qt);
		 ArrayList<QueryTree> locals = qt.getLocalQueries();
		 System.out.println("local queries: "+locals);
		 for (QueryTree q:locals)
			 System.out.println("Query:"+q);
	}
	
	
	public static void main(String args[]){
		try {
			//for(int i=0;i<10000;i++)
				new AQLTest().testNaturalJoin();
			//new ASLTest().installBundleInspector();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
