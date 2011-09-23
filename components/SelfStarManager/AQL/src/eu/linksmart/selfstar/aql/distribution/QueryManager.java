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
package eu.linksmart.selfstar.aql.distribution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import eu.linksmart.selfstar.aql.db.CachingIterator;
import eu.linksmart.selfstar.aql.db.MergingIterator;
import eu.linksmart.selfstar.aql.db.QueryTree;
import eu.linksmart.selfstar.aql.db.Schema;
import eu.linksmart.selfstar.aql.db.SchemaException;
import eu.linksmart.selfstar.aql.db.Tuple;
import eu.linksmart.selfstar.aql.db.TupleIterator;
import eu.linksmart.selfstar.aql.utils.Base64;

/*
 * AQL protocol
1. 
 In a pub-sub environment nodes can join and leave as they please. 
 The AQL protocol must be robust to variations in the set of reachable nodes. 

2. 
 It is impossible to know how many nodes are reachable except by sending a 
 query and count the replies received within a threshold, or by using heartbeats. AQL
 uses heartbeats and the QueryManager maintains the int variable 'deviceestimate' as 
 a count of the number of devices/querymanagers reachable from the pub sub connector 
 this one is connected to. 


3.
 A slow node can be slow at replying because it is connected through a low-bandwidth
 connection. It can also be slow at replying because it has a lot of data. The first
 case cannot be taken into account except for setting the timeouts to a high value.
 The second can be accommodated by the t_short+t_long scheme

4. 
 In case the issuing entity knows how many nodes should reply (possibly because of trust
 in a result of a previously issued discovery query), the query request can be accompanied
 by a parameter inputting this into the protocol for the particular request. This way, it
 is possible to guarantee that a query-response is complete under the assumption that the
 node-count aql was informed of is accurate. 

 */
/**
 * The AQL connector: a role provides one interface which uses a Publish/Subscribe role to implement
 * the AQL protocol. 
 * <br><br><b> AQL Protocol</b><br>
 * There are two kinds of roles in the AQL protocol, <i>nodes</i> and <i>initiators</i>. Each has a
 * corresponding topic in the publish-subscribe connector. An <i>initiator</i> initiates distributed query
 * processing by requesting <i>nodes</i> to answer a query. The message exchange is as follows:
 * <ol>
 * <li>An initiator receives a query to be evaluated. It splits it into a local query Q_l and a global query Q_g.</li>
 * <li>The initiator publishes an aql_req event to the topic nodes. The message contains the local query Q_l.
 * 		The initiator message is a map with the following elements:
 * <ul>	<li><i>query_id</i>A unique id for this query resolution.</li>
 *		<li><i>type</i> An integer denoting the msg type, ie QueryManager.AQL_REQ for requests or 
 *			QueryManager.AQL_REPLY for replies.</li>
 *</ul>
 *</li> 
 * <li>Each node evaluates the local query contained in the aql_req message and replies
 * 		with a message aql_reply on topic initiators.</li>
 * <li>When the number of replies received equals the number of subscribers to the nodes topic, the
 * 		initiator merges the results of the partial queries and evaluates the global part of the query.</li>
 * </ol>
 */
public class QueryManager implements EventHandler{

	private static QueryManager instance;
	private UUID node_id=UUID.randomUUID();
	
	public UUID getUUID(){
		return node_id;
	}
	
	public static QueryManager getInstance(){
		return instance;
	}
	
	public static void initPubSubConnector(EventAdmin pubsubrole){
		if (instance==null)
			instance= new QueryManager(pubsubrole);
		else
			instance.pubsubconnector=pubsubrole;
	}
	
	
	static final int MAXCONCURRENTQUERYS=100;
	
	/**
	 * Each query manager has a local count of the queries it issued, to identify the correct reply collector
	 * for incoming replies to queries.
	 */
	long currentQueryId;
	/*
	 * A QM should be unique within its set of communication partners. Each aql msg is annotated with its QM dynamic id.
	 * a dynamic id is increased to max(did)+1 for this node if this node sees a msg from another QM with a lower timestamp.
	 * max(did) is the highest did seen up until now by this node ... don't implement this at first...
	 */
	
	
	private EventAdmin pubsubconnector;
	
	private int heartbeatdelta=2000;
	
	private QueryManager(EventAdmin psrole){
		pubsubconnector=psrole;
		instance = this;
		new Thread (){
			public void run(){
				Event heartbeatevent = newEvent(heartbeattopic,AQLMessageTypes.heartbeat,null);
				while (true)
					try {
						sleep(heartbeatdelta);
						debug("publishing heartbeat:");
						pubsubconnector.postEvent(heartbeatevent);
						purgeOldHeartbeats(); 
					} catch (Exception e) {
					// ignore
					}
			}
		}.start();
	}
	
	LogService logservice;
	public void setLogger(LogService log){
		this.logservice = log;
	}
	
	public void unsetLogger(){
		this.logservice=null;
	}
	
	enum AQLMessageTypes{
		request,
		reply,
		req_ack,
		heartbeat
	}
	
	private Event newEvent(String topic, AQLMessageTypes type, Object[] keyvals){
		Hashtable<Object, Object> ht;
		if (keyvals!=null && keyvals.length>0){
			debug("\n1.2: creating new event");
				if (((keyvals.length % 2) != 0) )
				throw new RuntimeException("keyvals array supplied to QueryManager.newEvent() has uneven number of elements - ie the number of keys doesn't match the number of values");
			else {
				debug("\n1.3: creating new event");
				ht = new Hashtable<Object, Object>(2+keyvals.length/2);
				debug("\n1.3.1: creating new event");
				
				for (int i=0;i<keyvals.length;i+=2){
					debug("\n1.3.2: creating new event"+i);
					debug("\n1.3.3: adding values"+keyvals[i]+" and "+keyvals[i+1]);
					ht.put(keyvals[i], keyvals[i+1]);
					debug("ok");
				}
			}
			debug("\n1.4: creating new event");			
		}
		else
			ht=new Hashtable<Object, Object>(2);
		debug("\n1.5: creating new event");
		ht.put("node_id", getUUID().toString());
		ht.put("type", type.toString());
		return new Event(topic,(java.util.Map)ht); 
	}
	
	
	private UUID getUUID(Event e){
		return UUID. fromString((String) e.getProperty("node_id"));
	}
	
	final static String nodetopic="aql/nodes";
	final static String initiatortopic="aql/initiators";
	final static String heartbeattopic = "aql/heartbeat";
	
	private String[] subscribetopics=new String[]{
			nodetopic,
			initiatortopic,
			heartbeattopic
	};
	
	public String[] getSubscribeTopics(){
		return subscribetopics;
	}
	
	public void handleEvent(Event e) {
		printEvent(e);
		if (e.getTopic().equals(nodetopic))
			handleNodeMsg(e);
		if (e.getTopic().equals(initiatortopic))
			handleInitiatorMsg(e);
		if (e.getTopic().equals(heartbeattopic))
			handleHeartbeatMsg(e);
	}

	/**
	 * Handle an initiatr message - by deferring to the appropriate reply collector.
	 * @param e
	 */
	private void handleInitiatorMsg(Event e) {
		if (!getInitiatorUUID(e).equals(getUUID()))
			return;
		// This is the right query manager to handle this reply...
		// delegate to reply collector
		Long qseq= getQuerySequenceNumber(e);
		getReplyCollector(qseq).handleAQLReply(e);
		// replies, or intent-to-reply notifications
	}
	
	/**
	 * Get the initiator id from this event
	 * @param e
	 * @return
	 */
	private UUID getInitiatorUUID(Event e){
		debug("getuuid initiator:"+e.getProperty(P_INITIATORID).getClass());
		return UUID.fromString((String) e.getProperty(P_INITIATORID));
	}

	/**
	 * Retrieve the sequence number from the event e, as a Long
	 * @param e
	 * @return
	 */
	private Long getQuerySequenceNumber(Event e){
		return Long.parseLong((String)e.getProperty(P_QUERYSEQ));
	}
	
	/**
	 * Reads local queries from the event, processes them locally and returns the result.
	 * @param e
	 */
	private void handleNodeMsg(Event e) {
		
		debug("Received node message:"+e);
		debug("Processing query locally");
		ArrayList<QueryTree> queries = getQueries(e);
		byte[][] result_e = new byte[queries.size()][];
		debug("Processing queries:"+queries);
		for (int i=0;i<queries.size();i++)
			try {
				debug("***Node replying, adding result for query "+ queries.get(i)+" result is:"+ new CachingIterator(queries.get(i).instantiate()).toString());
				result_e[i]=TupleCoder.encode(queries.get(i).instantiate());
				debug("result size:"+result_e[i].length);
			} catch (Exception e1) {
				debug(e1.getStackTrace().toString());
				e1.printStackTrace();
			}
		debug("RETURNING THE RESULT:");
		String b64result=encodeByteArrayArray(result_e);
		Object[] properties = new Object[]{
				P_QRESULT,b64result,
				P_QUERYSEQ, e.getProperty(P_QUERYSEQ),
				P_INITIATORID, getUUID(e).toString() // the initiators id should be returned, so the appropriate initiator handles the reply...
		};
		debug("\n1:creating resultevent...");
		Event re = newEvent(initiatortopic, AQLMessageTypes.reply, properties);
		debug("\n2:posting resultevent...");
		pubsubconnector.postEvent(re);
		debug("Result returned");
		printEvent(re);
	}
	
	private String encodeByteArrayArray(byte[][] value){
		String s="error";
		try {
			s = Base64.encodeObject(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	/**
	 * Decode to a byte[][] a byte[][] that is Base64 encoded in a String
	 * @param value
	 * @return
	 */
	private byte[][] getByteArrayArray(String value){
		byte[][] b = null;
		try {
			b = (byte[][]) Base64.decodeToObject(value);
		} catch (Exception e){
			e.printStackTrace();
		}
		return b;
	}
	
	static String P_QRESULT="result";
	static String P_INITIATORID="initiatorid";
	ArrayList<QueryTree> getQueries(Event e){
		byte[][] locals_e= getByteArrayArray((String)e.getProperty(P_LOCALQ));
		if (locals_e==null)
			throw new RuntimeException("Event"+e+"did not have a property "+P_LOCALQ);
		ArrayList<QueryTree> locals = new ArrayList<QueryTree>(locals_e.length);
		for (byte[] barr:locals_e)
			try {
				locals.add(TupleCoder.decode_query(barr));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		return locals;
	}
	
	/**
	 * The messages from which no heartbeat is received within the heartbeatfrequency
	 */
	
	class HeartBeat {
		long timestamp;
		UUID deviceid;
		public HeartBeat(long timestamp, UUID deviceid){
			this.timestamp=timestamp;
			this.deviceid=deviceid;
		}
	}

	int deviceestimate;
	
	public static int getDeviceEstimate(){
		return getInstance().deviceestimate;
	}
	
	Hashtable<UUID,Long> hbtable = new Hashtable<UUID,Long>();
	ArrayList<HeartBeat> hbqueue = new ArrayList<HeartBeat>();
	
	void printEvent(Event e){
		debug("QueryManager handling event:"+e);
		for (String s:e.getPropertyNames())
			debug("\t"+s+" is "+e.getProperty(s));
	}
	
	private void handleHeartbeatMsg(Event e){
		long thistime = System.currentTimeMillis();
		hbtable.put(getUUID(e),thistime);
		hbqueue.add(new HeartBeat(thistime,getUUID(e)));
	}
	
	private final void purgeOldHeartbeats(){
		long threshold = System.currentTimeMillis()-heartbeatdelta-500; // assume variation up to half a second in heartbeat message reception tims
		while (hbqueue.get(0).timestamp<threshold){
			UUID id = hbqueue.remove(0).deviceid;
			if (hbtable.get(id)<threshold)
				hbtable.remove(id);
		}
		debug("Updated heartbeats, current device estimate:"+hbtable.size());
		deviceestimate=hbtable.size();
	}
	
	
	
	public final boolean debug=false;
	
	/**
	 * print a debug message if debugging is enabled
	 */
	public void debug(String s){
		if (debug){
			if (logservice!=null)
				logservice.log(LogService.LOG_DEBUG, s);
			else
				System.out.println(s);
		}
	}
	
	/**
	 * Log an error message
	 * @param s
	 */
	
	void error(StackTraceElement[] ste){
		for (StackTraceElement e:ste)
			error(e.toString());
	}
	
	void error(String s){
		if (logservice!=null)
			logservice.log(LogService.LOG_ERROR, s);
		else
			System.out.println(s);
	}
	
	/**
	 * Log a message with level INFO
	 * @param s
	 */
	void info(String s){
		if (logservice!=null)
			logservice.log(LogService.LOG_INFO, s);
		else
			System.out.println(s);
	}
	
	//query count
	public TupleIterator doDistributedQuery(QueryTree query){
		debug("Evaluating distributed query at "+getUUID()+"query:"+query.toString());
		ArrayList<QueryTree> locals=query.getLocalQueries();
		byte[][] locals_e = new byte[locals.size()][];
		int i=0;
		debug("**found local part"+locals);
		for (QueryTree q:locals)
			try {
				locals_e[i++]=TupleCoder.encode_query(q);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		currentQueryId++;
		Object [] properties = new Object[]{
				P_QUERYSEQ, Long.toString(currentQueryId),
				P_LOCALQ,encodeByteArrayArray(locals_e)
		};
		// set up reply collector, and store global query in that...
		debug("**properties:"+properties);
		Event e = newEvent("aql/nodes", AQLMessageTypes.request,properties);
		debug("**event created");
		allocateReplyCollector(currentQueryId,query);
		debug("Replycollector allocated ... publishing event:");
		printEvent(e);
		pubsubconnector.postEvent(e);
		final ReplyCollector rc = collectors.get(currentQueryId);
		TupleIterator rval=null;
		try {
			rc.waitresult();
			debug("\n\n** done waiting for result");
			rval=rc.getResult();
		} catch (InterruptedException e1) {
			// it doesn't happen, but...
			error(e1.getStackTrace());
		}
		/*if (debug){
			new Thread(){
				public void run(){
					while (!rc.isReady())
						try {
						sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					TupleIterator ti = rc.getResult();
					debug("RESULT.hasnext()"+ti.hasNext());
					for (Tuple t:rc.getResult())
						debug("it's;"+ t);
				}
			}.start();
		}*/
		return rval;
	}
	
	static String P_QUERYSEQ="query_sequence_number";
	static String P_LOCALQ="localqueries";
	ArrayList<ReplyCollector> tmpcollectors = new ArrayList<ReplyCollector>(5);
	
	private ReplyCollector allocateReplyCollector(long queryid, QueryTree query){
		ReplyCollector r=new ReplyCollector(queryid, query);
		collectors.put(queryid, r);
		return r;
	}
	
	private Hashtable<Long,ReplyCollector> collectors=new Hashtable<Long,ReplyCollector>();
	
	private ReplyCollector getReplyCollector(long queryid){
		if (!collectors.containsKey(queryid))
			throw new RuntimeException("The reply collector must be allocated beforehand");
		return collectors.get(queryid);
		
	}
	
	private class ReplyCollector{
		
		@SuppressWarnings("unused")
		private long queryid;
		private QueryTree query;
		private int receivedreplies=0;
		private int replies2Expect;
		ArrayList<byte[][]> parts=new ArrayList<byte[][]>(30);
		boolean resultready;
		
		private CountDownLatch completed;
		
		public ReplyCollector(long queryid, QueryTree query){
			//this.queryid=queryid;
			//this.query=query;
			//collectors.put(queryid, this);
			reset(queryid, query);
		}
		
		public void reset(long newqueryid, QueryTree query){
			this.queryid=newqueryid;
			this.query=query;
			replies2Expect=deviceestimate; // asigned at time of 
			receivedreplies=0;
			parts.clear();
			resultready=false;
			completed = new CountDownLatch(replies2Expect);
		}
		
		public void waitresult() throws InterruptedException{
			completed.await(15, TimeUnit.SECONDS);
		}
		
		/*private boolean queryIdEquals(long id){
			return id==queryid;
		}*/
		
		public synchronized boolean isReady(){
			return resultready;
		}
		
		public TupleIterator getResult(){
			if (!resultready){
				Exception e = new RuntimeException("Result not ready yet. Please check isReady() first.");
				e.printStackTrace();
				return null;
/*				return new TupleIterator(){
					public boolean hasNext(){return false; }
					public Tuple next(){ throw new NoSuchElementException();}
					public void remove() { throw new UnsupportedOperationException();}
					public Iterator<Tuple> iterator() {return this;}
					public Schema getSchema() {return null;}
				};*/
			}
			// for each element in the local query, merge across all devices
			
			debug("****parts.size="+parts.size()+"parts.get(0).length="+parts.get(0).length);
			if (debug){
				debug("Contents of collected results:");
				for (int dev=0;dev<parts.size();dev++)
					for(int qp=0;qp < parts.get(dev).length; qp++){
						CachingIterator ci;
						try {
							ci = new CachingIterator(TupleCoder.decode(parts.get(dev)[qp]));
							debug("Received RESULT: "+ci);
						} catch (Exception e) {
							debug(e.getStackTrace().toString());
						}
					}
			}

			int localpartcount=parts.get(0).length;
			ArrayList<TupleIterator> queryparts = new ArrayList<TupleIterator>(localpartcount);
			byte[] pack;
			TupleIterator[] toMerge=new TupleIterator[parts.size()];
			for (int qpartindex=0;qpartindex<localpartcount;qpartindex++){
				for (int devindex=0;devindex<parts.size();devindex++){
					pack=parts.get(devindex)[qpartindex];
					try {
						toMerge[devindex]=TupleCoder.decode(pack);
				/*		if (debug){
							CachingIterator ci = new CachingIterator(TupleCoder.decode(parts.get(devindex)[qpartindex]));
							debug("Appended RESULT: "+ci);
						}*/

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				TupleIterator m = new MergingIterator(toMerge);
					
				queryparts.add(m);
			}
			
			if (debug){
				for (TupleIterator m: queryparts){
					CachingIterator ci=new CachingIterator(m);
					debug("COLLECTED RESULT: "+ci);
				}
			}
			
			// Merging complete ... now evaluate global query
			ArrayList<QueryTree> queries=query.getLocalQueries();
			
			if (queries.size()==1 && queries.get(0).equals(query)){
				debug("special case, query has depth 1");
				return queryparts.get(0);
			
			}
			//debug(query.toString());
			debug("Queries:"+queries+":"+queryparts);
			return query.instantiateGlobal(queries, queryparts);
			
		}
		
		void handleAQLReply(Event e){
			receivedreplies++;
			debug("\nReceived reply (from "+getUUID(e)+")"+receivedreplies+" /"+replies2Expect);
			parts.add((byte[][]) getByteArrayArray((String)e.getProperty(P_QRESULT)));
			if (receivedreplies>=replies2Expect)
				resultready=true;
			completed.countDown();
			// get reply-data, add data to aggregate
			// check if this was the last, and if so remove self from replycollectors and return query.
		}
		
	}
}
