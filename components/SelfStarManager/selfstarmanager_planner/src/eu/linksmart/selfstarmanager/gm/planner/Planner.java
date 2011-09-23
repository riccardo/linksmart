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
package eu.linksmart.selfstarmanager.gm.planner;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import eu.linksmart.selfstar.aql.AQLService;
import eu.linksmart.selfstar.aql.db.CachingIterator;
import eu.linksmart.selfstar.aql.db.Schema;
import eu.linksmart.selfstar.aql.db.SchemaException;
import eu.linksmart.selfstar.aql.db.Tuple;
import eu.linksmart.selfstar.aql.db.TupleIterator;

public class Planner implements EventHandler {
	private LogService log;
	private EventAdmin eventAdmin;
	private ADLPlanner planner;
	private AQLService aql;

	//@Override
	public void handleEvent(Event event) {
		log.log(LogService.LOG_DEBUG, "received event : " + event);
		if(((String)event.getProperty("topic")).equals("goalmanagement/requestplan")){
			for(String key:event.getPropertyNames()){
				System.out.println(key+"="+event.getProperty(key));
			}
		}
	}

	protected void setAql(AQLService aql){
		this.aql=aql;
	}
	
	protected void unsetAql(AQLService aql){
		this.aql=null;
	}
	protected void setLog(LogService log) {
		this.log = log;
	}
	
	protected void unsetLog(LogService log) {
		this.log = null;
	}
	
	protected void setPlanner(ADLPlanner planner){
		this.planner=planner;
	}
	
	protected void unsetPlanner(ADLPlanner p){
		this.planner=null;
	}

	protected void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;	
	}

	protected void unsetEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void activate(ComponentContext context) {
		// Subscribe
		Dictionary properties = new Hashtable();
		properties.put(EventConstants.EVENT_TOPIC, new String[]{"goalmanagement/requestplan"});
		context.getBundleContext().registerService(EventHandler.class.getName(), this, properties);

		// Publish
		Dictionary eventProperties = new Hashtable();
		eventProperties.put("plan", "myplan");
		Event event = new Event("componentcontrol/newplan", eventProperties);
		eventAdmin.postEvent(event);
		log.log(LogService.LOG_INFO, "posted event");
		testPlanner();
		testMakeDynamic();
	}
	
	
	@SuppressWarnings("rawtypes")
	private void testPlanner(){
		String res="/Users/ingstrup/Documents/workspaces/LinkSmart3/selfstarmanager-nfit/components/selfstarmanager_planner_wrapper/resources/";
		File domain = new File(res+"protocoldomain.pddl");
		File problem = new File(res+"generatedproblem.pddl");
		ArrayList plan = planner.executePlanner(problem, domain);
		//ArrayList plan = pw.runPlanner();
		System.out.println("Plan:"+plan);
		@SuppressWarnings("unchecked")
		ArrayList<String> script=new ASLTranslator().translatePlan(plan);
		for(String line:script)
			System.out.println(line);
	}
	
	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	void testMakeDynamic(){
		String res="/Users/ingstrup/Documents/workspaces/LinkSmart3/selfstarmanager-nfit/components/selfstarmanager_planner_wrapper/resources/";
		File domain = new File(res+"protocoldomain.pddl");
		File problem = new File(res+"generatedproblem-tmp.pddl");
		// query for getting connection data:
		String clientep = "LOCAL ( RENAME [TargetEndpoint->Address DeviceID->ClientDID]( ClientEndpoints ))";
		String serverep = "LOCAL ( RENAME [ServingEndpoint->Address DeviceID->ServerDID] (ServerEndpoints))";
		String cquery = "GLOBAL ( NATURALJOIN ( "+clientep+" "+serverep+"))";
		// query for getting bundle data:
		String bquery = "GLOBAL(LOCAL ( PROJECT [DeviceID SymbolicName ID State] (EQUIJOIN \"ID\" (Deployment Bundles) )))";
		// get current configuration:
		TupleIterator iconf=aql.processLocalQuery(bquery);
		TupleIterator iconn=aql.processLocalQuery(cquery);
		// set goal state: 
		if (!iconf.hasNext() || !iconn.hasNext()){
			log.log(log.LOG_ERROR, "Unable to retrieve configuration data from AQL.");
		}
		PrintStream p=null;
		final Tuple t = iconn.next();
		getIteratorInitial(t);
		getIteratorGoal(t);
		try {
			p = new PrintStream(problem);
			PlanProblemGenerator.setFile(p);
			PlanProblemGenerator.generateProtocolPlan(
					new CachingIterator(iconf),
					new CachingIterator(iconf),
					new CachingIterator(getIteratorInitial(t)),
					new CachingIterator(getIteratorGoal(t)));
			System.out.println("Generated plan; wrote to "+problem.getAbsolutePath());
			// invoke planner and print result:
			ArrayList plan = planner.executePlanner(problem, domain);
			//ArrayList plan = pw.runPlanner();
			System.out.println("Plan:"+plan);
			ArrayList<String> script=new ASLTranslator().translatePlan(plan);
			for(String line:script)
				System.out.println(line);

		} catch (Exception e) {
			// this is very unlikely since we just created the file...
			log.log(log.LOG_ERROR,e.getStackTrace().toString());
		}
		
	}

	TupleIterator getIteratorGoal(final Tuple t){
		Tuple gt = t.getSchema().newTuple();
		try {
			for(String s:t.getSchema().getFieldNameIterable())
			gt.setValue(s, t.getValue(s));
		gt.setValue("Protocol", "udp");
		} catch (SchemaException e) {
			e.printStackTrace();
		}
		final Tuple tf = gt;
		return new CachingIterator(new TupleIterator(){

			@Override
			public void remove() {
				throw new RuntimeException("Not supported");
				
			}
			boolean hasnext=true;
			@Override
			public boolean hasNext() {
				return hasnext;
			}

			@Override
			public Tuple next() {
				hasnext=false;
				return tf;
			}

			@Override
			public Iterator<Tuple> iterator() {
				return this;
			}

			@Override
			public Schema getSchema() {
				return t.getSchema();
			}
			
		});
	}

	
	TupleIterator getIteratorInitial(final Tuple t){
		return new CachingIterator(new TupleIterator(){

			@Override
			public void remove() {
				throw new RuntimeException("Not supported");
				
			}
			boolean hasnext=true;
			@Override
			public boolean hasNext() {
				return hasnext;
			}

			@Override
			public Tuple next() {
				hasnext=false;
				return t;
			}

			@Override
			public Iterator<Tuple> iterator() {
				return this;
			}

			@Override
			public Schema getSchema() {
				return t.getSchema();
			}
			
		});
	}
}
