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

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * @author Administrator
 */
public abstract class PubSubConnector {
	
	public abstract void publish(PubSubMessage msg);
	public abstract void subscribe(int topic, Receiver subscriber);
	
	private static Hashtable cstreams = new Hashtable();
	
	static void addStream(Object key, Object str){
		if (cstreams.containsKey(key)){
			((Vector)cstreams.get(key)).add(str);
		}
		else {
			Vector v = new Vector(2);
			v.add(str);
			cstreams.put(key,v);			
		}
	}
	
	public static void printStreamStatus(){
		Iterator ki = cstreams.keySet().iterator();
		while (ki.hasNext()){
			Object k = ki.next();
			Iterator vi = ((Vector)cstreams.get(k)).iterator();
		    System.out.println("Object:"+k);
		    while (vi.hasNext()){
		    	Object str = vi.next();
		    	System.out.print("\t:bytecount:");
		    	if (str instanceof InputStreamCounting){
		    		InputStreamCounting isc = (InputStreamCounting)str;
		    		System.out.println(" IN: "+isc.getCount());
		    	}
		    	if (str instanceof OutputStreamCounting){
		    		OutputStreamCounting osc = (OutputStreamCounting)str;
		    		System.out.println("OUT:"+osc.getCount());
		    	}
		    }
		}
	}

	static class SubscriptionHandler{

		private Hashtable subscribers;
		private HashSet proxies = new HashSet();
		
		SubscriptionHandler(){
			subscribers = new Hashtable(3);
		}
		
		void addProxy(Receiver proxy){
			proxies.add(proxy);
		}
		
		void addSubscriber(Integer topic, Receiver subscriber){
			if(!subscribers.containsKey(topic)){
				subscribers.put(topic,new Vector());
			}
			Vector subs = (Vector)subscribers.get(topic);
			if (!subs.contains(subscriber))
					subs.add(subscriber);
			Iterator i = proxies.iterator();
			while (i.hasNext()){
				Receiver r = (Receiver)i.next();
				if (r!=subscriber){
					r.handleDataPackage(new PubSubMessage(PubSubMessage.SUBSCRIBE,topic));
					//System.out.println("\n\n$corrective action taken.");
				}
			}
			/*Iterator j = subscribers.values().iterator();
			while(j.hasNext()){
				Iterator i = ((Vector)j.next()).iterator();
				while (i.hasNext()){
					Receiver r = (Receiver)i.next();
					if (r instanceof prox && r!=subscriber){
						r.handleDataPackage(new PubSubMessage(PubSubMessage.SUBSCRIBE,topic));
						System.out.println("\n\n$corrective action taken.");
					}
				}
			}*/
			//System.out.println("Subscription added to"+topic+subscriber);
		}
		
		void removeSubscriber(Integer topic, Receiver subscriber){
			
		}
		
		void notifyAll(PubSubMessage msg, Receiver origin){
			//System.out.println(subscribers);
			//System.out.println(msg.getTopic());
			if (!subscribers.containsKey(msg.getTopic())){
				System.out.println("Doesn't contain key"+msg.getTopic()+"!"+subscribers.keys());
				return;
			}
			Iterator i = ((Vector)subscribers.get(msg.getTopic())).iterator();
			while(i.hasNext()){
				Receiver r = (Receiver)i.next();
				if (r!=origin)
					r.handleDataPackage(msg);
			}
		}
		
		private Vector getSubscribers(int topic){
			return (Vector)subscribers.get(new Integer(topic));
		}
		
		private boolean hasSubscribers(int topic){
			return subscribers.containsKey(new Integer(topic));
		}
		public boolean isSubscribing(Receiver sub, int topic){
			return hasSubscribers(topic) && getSubscribers(topic).contains(sub);
		}

		public Set getTopics(){
			return subscribers.keySet();
		}
	}
}
