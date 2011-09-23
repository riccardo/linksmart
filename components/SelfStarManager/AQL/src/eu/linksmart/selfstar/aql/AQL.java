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
package eu.linksmart.selfstar.aql;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Hashtable;

import org.antlr.runtime.RecognitionException;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import eu.linksmart.selfstar.aql.db.QueryTree;
import eu.linksmart.selfstar.aql.db.RelationalOperators;
import eu.linksmart.selfstar.aql.db.Table;
import eu.linksmart.selfstar.aql.db.TableRegistry;
import eu.linksmart.selfstar.aql.db.Tuple;
import eu.linksmart.selfstar.aql.db.TupleFilter;
import eu.linksmart.selfstar.aql.db.TupleIterator;
import eu.linksmart.selfstar.aql.distribution.QueryManager;
import eu.linksmart.selfstar.aql.distribution.TupleCoder;
import eu.linksmart.selfstar.aql.osgisensor.FelixOSGiSensor;

public class AQL implements EventHandler, AQLService{

	LogService log;
	EventAdmin eventAdmin;
	ComponentContext cc;
	
	
	public void handleEvent(Event arg0) {
		debug("AQL.class received event"+arg0);
		
	}
/*
 * Sample ASL for installing this bundle;

init_device(local);
init_component(aql,/Users/ingstrup/Documents/workspaces/LinkSmart2/flamenco_aql/exported/flamenco_aql.jar);
deploy_component(local,aql);
init_service(local, aql,aql_s);
start_service(aql_s);

 */
	
	private void testOSGiSensor(){
		info("Bundles table:\n"+TableRegistry.getInstance().getTable("Bundles").toTabularString());	

		// test selection:
		ThreadMXBean threadData = ManagementFactory.getThreadMXBean( );
		long t1=threadData.getCurrentThreadCpuTime();
		
		//for(int i=0;i<1000;i++){
			@SuppressWarnings("unused") // used to test performance
			TupleIterator tsi,tpi=RelationalOperators.projectionIterator(new String[]{"ID","Location"}, TableRegistry.getInstance().getTable("Bundles").iterator());
			tsi=RelationalOperators.selectionIterator(tpi, new TupleFilter(){
				public boolean accepts(Tuple t) {
					if (((Long)t.getValue("ID"))>0)
						return true;
					return false;
				}
			}); 
		long t2=threadData.getCurrentThreadCpuTime();
		info("Projection +selection timing: "+((t2-t1)/1000000.0)+" ms"); //\u00b5s");
	}
	
	FelixOSGiSensor sensor;
	protected void activate(ComponentContext cc){
		info("AQL started");
		info("AQL assigned the following UUID to this device: "+ QueryManager.getInstance().getUUID().toString());
		try {
		sensor=new FelixOSGiSensor(cc.getBundleContext());
		this.cc=cc;
		setupAQLSubscriptions();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cc.getBundleContext().registerService(AQLService.class.getName(), this, null);
		testChannelQuery();
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		if (((String)System.getProperty("AQL.printallevents", "false").toLowerCase()).equals("true"))
				enablePrintAllEvents();
		
	}
	
	@SuppressWarnings("unchecked")
	void setupAQLSubscriptions(){
		String[] topics = QueryManager.getInstance().getSubscribeTopics();//new String[] {"flamenco/aql"};
		@SuppressWarnings("rawtypes")
		Hashtable ht = new Hashtable();
		ht.put(EventConstants.EVENT_TOPIC, topics);
		cc.getBundleContext().registerService(EventHandler.class.getName(),  
				QueryManager.getInstance(),ht);
		debug("aql subscriber service registered.");
	}

	void enablePrintAllEvents(){
		Hashtable ht = new Hashtable();
		ht.put(EventConstants.EVENT_TOPIC, new String[]{"*"});
		cc.getBundleContext().registerService(EventHandler.class.getName(),new EventHandler() {
			
			@Override
			public void handleEvent(Event event) {
				info("Event: "+event.getTopic());
				for (String s: event.getPropertyNames())
					info("\t\t"+s+"="+event.getProperty(s));
			}
		},ht);
		info("Enabled printing of all messages");
	}
	
	void testClientTable(){
		try { 
			System.out.println("Testing clienttable");
		
		info("infotest");
		TupleIterator t=TableRegistry.getInstance().getIterator("ClientEndpoints");
		info("ClientEndpoints: hasnext="+t.hasNext());
		while(t.hasNext())
			info("\t"+t.next());
		System.out.println("Done testing clienttable");
		System.out.println("Testing servertable");
		info("infotest");
		t=TableRegistry.getInstance().getIterator("ServerEndpoints");
		info("ServerEndpoints: hasnext="+t.hasNext());
		while(t.hasNext())
			info("\t"+t.next());
		System.out.println("Done testing servertable");
		} catch (Exception e) {
			
		}
	}
	
	@SuppressWarnings("rawtypes")
	void test(){
		ThreadMXBean threadData = ManagementFactory.getThreadMXBean( );

		// test event publication; verify subscription
		eventAdmin.postEvent(new Event("flamenco/aql", (java.util.Map)new Hashtable()));
		TupleIterator bundles = TableRegistry.getInstance().getIterator("Bundles");
		Table decoded;
		try {
			long t2,t1=threadData.getCurrentThreadCpuTime();
			byte encoded[] = TupleCoder.encode(bundles);
			t2=threadData.getCurrentThreadCpuTime();
			double timedelta=((t2-t1)/1000000.0);			
			System.out.println("Encoded the bundle table, size="+encoded.length+"bytes (time:"+timedelta+" ms)");
			t1=threadData.getCurrentThreadCpuTime();
			decoded=TupleCoder.decodeTable(encoded);
			t2=threadData.getCurrentThreadCpuTime();
			timedelta=((t2-t1)/1000000.0);			
			System.out.println("Decoded the bundle table, size="+encoded.length+" (time:"+timedelta+" ms)");
			System.out.println("Decoded table:\n"+decoded.toTabularString());	

			info("Bundles:");
			TupleIterator ti=processLocalQuery("Bundles");
			info("Schema\n"+ti.getSchema());
			for (Tuple t:ti){
				for (String name:ti.getSchema().getFieldNameIterable())
					System.out.print("\t"+t.getValue(name));
				System.out.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*

			GLOBAL ( 
				NATURALJOIN ( 
			 		LOCAL ( 
			 			RENAME [TargetEndpoint->Address DeviceID->ClientDID]( ClientEndpoints )
			 		)
			 		LOCAL ( 
			 			RENAME [ServingEndpoint->Address DeviceID->ServerDID] (ServerEndpoints)
			 		)
			 	)
			 )



	 */
	void testChannelQuery(){
		try {
			System.out.println("properties: http.clientprotocol.connections.27="+System.getProperty("http.clientprotocol.connections.27"));
			String clientep = "LOCAL ( RENAME [TargetEndpoint->Address DeviceID->ClientDID]( ClientEndpoints ))";
			String serverep = "LOCAL ( RENAME [ServingEndpoint->Address DeviceID->ServerDID] (ServerEndpoints))";
			String query = "GLOBAL ( NATURALJOIN ( "+clientep+" "+serverep+"))";
			System.out.println("Testing channel query:"+query);
			System.out.println("Query:"+query);
			System.out.println("Query, parsed:"+QueryTree.parseQuery(query).toString());
			System.out.println("Testing local query:");
			System.out.println("Client part, locally:");
			TupleIterator result=QueryTree.parseQuery(clientep).instantiate();
			System.out.println("Got result, hasNext:"+result.hasNext());
			while(result.hasNext())
				System.out.println(result.next());
			System.out.println("Server part, locally:");
			result=QueryTree.parseQuery(serverep).instantiate();
			System.out.println("Got result, hasNext:"+result.hasNext());
			while(result.hasNext())
				System.out.println(result.next());
			System.out.println("testing with naturaljoin:");
			result=QueryTree.parseQuery(query).instantiate();
			System.out.println("Got result, hasNext:"+result.hasNext());
			while(result.hasNext())
				System.out.println(result.next());
			System.out.println("Testing distributed, local clientpart "+clientep);
			result=QueryManager.getInstance().doDistributedQuery(QueryTree.parseQuery(clientep));
			System.out.println("Got result, hasNext:"+result.hasNext());
			while(result.hasNext())
				System.out.println(result.next());
			System.out.println("Testing distributed, local clientpart AGAIN"+clientep);
			result=QueryManager.getInstance().doDistributedQuery(QueryTree.parseQuery(clientep));
			System.out.println("Got result, hasNext:"+result.hasNext());
			while(result.hasNext())
				System.out.println(result.next());
			System.out.println("Testing distributed, local serverpart "+serverep);
			result=QueryManager.getInstance().doDistributedQuery(QueryTree.parseQuery(serverep));
			System.out.println("Got result, hasNext:"+result.hasNext());
			while(result.hasNext())
				System.out.println(result.next());

			System.out.println("Testing distributed, simulated full query: "+query);
			TupleIterator ts = QueryManager.getInstance().doDistributedQuery(QueryTree.parseQuery(serverep));
			TupleIterator tc = QueryManager.getInstance().doDistributedQuery(QueryTree.parseQuery(clientep));
			result = RelationalOperators.naturaljoinIterator(ts, tc);
			while(result.hasNext())
				System.out.println(result.next());


			System.out.println("Testing distributed, full query: "+query);
			result=QueryManager.getInstance().doDistributedQuery(QueryTree.parseQuery(query));
			System.out.println("Got result="+result+" , hasNext:"+result.hasNext());
			while(result.hasNext())
				System.out.println(result.next());
			
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	void testDistributed(){
		try {
			String qstring=" SELECT State==\"32\" ( Bundles )";
			QueryTree query=QueryTree.parseQuery(qstring);
			System.out.println(query);
			
			TupleIterator ti = query.instantiate();
			System.out.println("Printing query result, hasNext()="+ti.hasNext());
			while (ti.hasNext())
				System.out.println("Tuple:" +ti.next());
			
			System.out.println("***Here");
			TupleIterator tsi,tpi=RelationalOperators.projectionIterator(new String[]{"ID","State"}, TableRegistry.getInstance().getIterator("Bundles"));
			tsi=RelationalOperators.selectionIterator(tpi, new TupleFilter(){
				public boolean accepts(Tuple t) {
					System.out.println("comparing:"+t.getValue("State")+t.getValue("State").getClass());
					if (((Integer)t.getValue("State"))==Bundle.ACTIVE)
						return true;
					return false;
				}
			});
			System.out.println("***Here2"+tpi);
			System.out.println("Testing progr con query:"+tsi.hasNext());
			while (tsi.hasNext())
				System.out.println("Tuple"+tsi.next());
			
			
			
			QueryManager qm = QueryManager.getInstance();
		
			qm.doDistributedQuery(query);
		
		
		} catch (RecognitionException e) {
			e.printStackTrace();
		}
		
		//qm.doDistributedQuery(query);
	}
	
	private void testDistributed2(){
		try {
			System.out.println("Test distributed 2");
			//String qstring="LOCAL ( SELECT State==\"32\" ( PROJECT [State] (Bundles) ))";
			//String qstring="LOCAL ( SELECT State==\"32\" ( PROJECT [DeviceID ID State] (EQUIJOIN \"ID\" (Deployment Bundles) )))";
			//String qstring=" GLOBAL ( SELECT State==\"32\" ( LOCAL ( PROJECT [DeviceID SymbolicName ID State] (EQUIJOIN \"ID\" (Deployment Bundles) ))))";
			String qstring=" GLOBAL ( SELECT State==\"32\" ( LOCAL ( PROJECT [DeviceID SymbolicName ID State] (EQUIJOIN \"ID\" (Deployment Bundles) ))))";
			TupleIterator ti;
			final QueryTree query=QueryTree.parseQuery(qstring);
			System.out.println(query);
			ti=query.instantiate();
			System.out.println("*** local execution of the query:, ti.hasnext="+ti.hasNext());
			while (ti.hasNext())
				System.out.println(ti.next());
			
			if (System.getProperty("eventmanager.registermanager","false").equals("true")){
				System.out.println("running query...");
				new Thread(){
				public void run(){
					while(QueryManager.getDeviceEstimate()<2){
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("\nwaiting for more devices\n");
					}
					System.out.println("\n-----Doing distributed query-----\n");
					long t1=System.currentTimeMillis();
					TupleIterator result=QueryManager.getInstance().doDistributedQuery(query);
					for (Tuple t:result)
						System.out.println("\t"+t);
					long t2=System.currentTimeMillis();
					System.out.println("Distributed query timing:"+(t2-t1)+" ms including printing"); //\u00b5s");


				}
			}.start();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void setLog(LogService log) {
		System.out.println("AQL v1.1");
		System.out.println("AQL: logger set");
		this.log = log;
		if (eventAdmin!=null && QueryManager.getInstance()==null){
			QueryManager.initPubSubConnector(eventAdmin);
			QueryManager.getInstance().setLogger(log);
		}
	}
	
	protected void unsetLog(LogService log) {
		System.out.println("AQL: logger unset");
		this.log = null;
		if (QueryManager.getInstance()!=null)
			QueryManager.getInstance().unsetLogger();
		
	}
	
	@SuppressWarnings("static-access")
	private void debug(String s){	
		if (this.log!=null)
			this.log.log(log.LOG_DEBUG, s);
		else
			System.out.println("DEBUG: "+s);
	}
	@SuppressWarnings("static-access")
	private void info(String s){
		//if (this.log!=null)
		//	this.log.log(log.LOG_INFO, s);
		//else
			System.out.println("INFO: "+s);
	}

	@SuppressWarnings("static-access")
	private void error(String s){
		if (this.log!=null)
			this.log.log(log.LOG_ERROR, s);
		else
			System.out.println("ERROR: "+s);
	}

	
	protected void setEventAdmin(EventAdmin eventAdmin) {
		info("AQL: EventAdmin set");
		this.eventAdmin = eventAdmin;
		QueryManager.initPubSubConnector(eventAdmin);
	}

	protected void unsetEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}

	public TupleIterator processDistributedQuery(String query) {
		QueryTree q=null;
		try {
			q = QueryTree.parseQuery(query);
		} catch (RecognitionException e) {
			error(e.getStackTrace().toString());
		}
		return QueryManager.getInstance().doDistributedQuery(q);
	}

	int ocount=0;
	public TupleIterator processLocalQuery(String query) {
		System.out.println("Object-count:"+ocount++);
		QueryTree q=null;
		try {
			q = QueryTree.parseQuery(query);
		} catch (RecognitionException e) {
			error(e.getStackTrace().toString());
		}
		return q.instantiate();
	}
}
