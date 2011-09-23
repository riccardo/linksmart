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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author ingstrup@daimi.au.dk
 *  (c) 2005 Mads Ingstrup
 */
public class PubSubConnectorImpl_TCP extends PubSubConnector{
	
	private static int SERVER=0,CLIENT=1;
	private int role;
	private Role impl;
	
	static interface Role{
		void publish(PubSubMessage msg, Receiver origin);
		void subscribe(int topic, Receiver subscriber);
		void publish(PubSubMessage msg);
//		void getSubscriberCount(int topic);
	}
	
	private class ServerSideRole implements Role {

		private PubSubConnector.SubscriptionHandler subhandler = new SubscriptionHandler();
		ServerSocket ss;

		ServerSideRole() throws IOException{
			p("Making serversiderole");
			ss = new ServerSocket(8001);
			p(ss);
			new Thread(){
				public void run(){
					while (true){
						try {
							Socket s = ss.accept();
							new SSProxy(s).start();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}
		
		
		class SSProxy extends Thread implements Receiver,prox {
			
			Socket sock;
			ObjectInputStream oi;
			ObjectOutputStream oo;
					
			SSProxy(Socket remote){
				try{
					sock=remote;
					boolean counting = false;
					if (counting){
						InputStreamCounting isc = new InputStreamCounting(sock.getInputStream());
						OutputStreamCounting osc = new OutputStreamCounting(sock.getOutputStream());
						oi = new ObjectInputStream(isc);//sock.getInputStream());
						oo = new ObjectOutputStream(osc);//sock.getOutputStream());
						PubSubConnector.addStream(sock,isc);
						PubSubConnector.addStream(sock,osc);
					} else {
						oi = new ObjectInputStream(sock.getInputStream());
						oo = new ObjectOutputStream(sock.getOutputStream());
					}
					//	p("new SSProxy made");
					subhandler.addProxy(this);
					// send a subscribe for each existing subscription on serverside ...
					PubSubMessage msg = new PubSubMessage(PubSubMessage.SUBSCRIBE,null);
					Iterator i = PubSubConnectorImpl_TCP.ServerSideRole.this.subhandler.getTopics().iterator();
					while(i.hasNext()){
						msg.setTopic((Integer)i.next());
						oo.writeObject(msg);
					}
						
				} catch(Exception e){e.printStackTrace();}
			}
			
			public void run(){
				PubSubMessage msg;
				while (true){
					try {
						msg = (PubSubMessage)oi.readObject();
						//PubSubConnector.printStreamStatus();
						if (msg.isT_PUBLISH()){
							publish(msg,PubSubConnectorImpl_TCP.ServerSideRole.SSProxy.this);
							//p("SERVER received MESSAGE:"+msg.getPayload().getClass()+msg.getTopic());
							//System.out.println(msg.getTopic());
							//System.out.println("Publishing from client");
						}
						if (msg.isT_SUBSCRIBE()){
							subscribe(msg.getTopic(),PubSubConnectorImpl_TCP.ServerSideRole.SSProxy.this);
							p("**subscription added"+msg.getTopic());
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(0);
						break;
					}
					
				}
			}
		
			/* (non-Javadoc)
			 * @see distribution.Receiver#handleDataPackage(distribution.PubSubMessage)
			 */
			public void handleDataPackage(PubSubMessage psm) {
				p("**handling locally published to send "+psm);
				try {
					oo.writeObject(psm);
				} catch (IOException e) {e.printStackTrace();}
				
			}
			
		}


		public synchronized void publish(PubSubMessage msg, Receiver origin) {
			subhandler.notifyAll(msg,origin);
			p("published");
		}

		public synchronized void publish(PubSubMessage msg){
			publish(msg,null);
		}
		
		public synchronized void subscribe(int topic, Receiver subscriber) {
			subhandler.addSubscriber(topic,subscriber);
			p("Subscriber added:("+topic+")"+subscriber+"class:"+subscriber.getClass());
		}
		
	}
	
	private class ClientSideRole implements Role, Receiver{

		private PubSubConnector.SubscriptionHandler subhandler = new SubscriptionHandler();
		private ObjectInputStream coi;
		private ObjectOutputStream coo;
		Socket cs;

		private ClientSideRole() throws UnknownHostException, IOException{
			p("making client side role");
			cs = new Socket(InetAddress.getLocalHost(),8001);
			boolean counting = false;
			if (counting){
				InputStreamCounting isc = new InputStreamCounting(cs.getInputStream());
				OutputStreamCounting osc = new OutputStreamCounting(cs.getOutputStream());
				coo = new ObjectOutputStream(osc);
				coi = new ObjectInputStream(isc);
				PubSubConnector.addStream(cs,isc);
				PubSubConnector.addStream(cs,osc);
			} else {
				coo = new ObjectOutputStream(cs.getOutputStream());
				coi = new ObjectInputStream(cs.getInputStream());
			}
			
			p("made client"+cs);
			new Thread(){
				public void run(){
					PubSubMessage msg;
					while (true){
						try{
							msg = (PubSubMessage)coi.readObject();
							if (msg.isT_PUBLISH()){
								p("Received MESSAGE:"+msg.getPayload().getClass()+msg.getTopic());
								publishLocal(msg,ClientSideRole.this);
							}
							if (msg.isT_SUBSCRIBE()){
								subscribeLocal(msg.getTopic(),PubSubConnectorImpl_TCP.ClientSideRole.this);
								p("??Received remote request for subscription"+msg.getTopic_int());
							}
						} catch (Exception e) {e.printStackTrace(); break;}
					}
				}
			}.start();
		}
		
		public void publish(PubSubMessage msg, Receiver origin) {
			publishLocal(msg,origin);
			//publish(msg);
		}

		public void publish(PubSubMessage msg){
			//p("publish:topic:"+msg.getTopic());
			publishLocal(msg,null);
		}
		
		public void subscribe(int topic, Receiver subscriber) {
			// justsend it to the server - will be handled correctly by the SSProxy
			// this role already subscribes, then there's no need to resend it to the server
			//if (!subhandler.isSubscribing(PubSubConnectorImpl_TCP.ClientSideRole.this,topic)){
				try{
					p("¤¤ Adding remote subscription to "+topic);
					PubSubMessage msg = new PubSubMessage(PubSubMessage.SUBSCRIBE, topic);
					coo.writeObject(msg);
				} catch (Exception e) {e.printStackTrace();}
			//}
			p("¤¤ Adding local subscription to "+topic);
			subscribeLocal(topic, subscriber);
		}
		
		public void handleDataPackage(PubSubMessage msg){
			try {
				p("Sending MESSAGE:"+msg.getPayload().getClass());
				coo.writeObject(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void publishLocal(PubSubMessage msg, Receiver nonrec){
			p("client publishing locally:"+msg.getTopic());
			subhandler.notifyAll(msg,nonrec);
			
		}
		
		private void subscribeLocal(int topic, Receiver subscriber){
			subhandler.addSubscriber(topic,subscriber);
		}
		
	}
	
	static PubSubConnectorImpl_TCP instance;
	public static PubSubConnectorImpl_TCP getInstance(){
		if (instance==null)
			instance = new PubSubConnectorImpl_TCP();
		return instance;
	}
	
	private PubSubConnectorImpl_TCP(){
		try{
			impl = new ServerSideRole();
			role = SERVER;
			p("(acting as server)");
		} catch (IOException e) {
			try {
				// if can't bind to this port, this role of the connector is second or later to be 
				// instantiated -> hence it should just connect to the role that has the serversocket...
				impl = new ClientSideRole();
				role = CLIENT;
				p("(acting as client)");
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		} catch (Exception se){
			se.printStackTrace();
		}
		
	}
	
	public synchronized void publish(PubSubMessage m, Receiver sender){
		impl.publish(m,sender);
	}
	
	public synchronized void publish(PubSubMessage m){
		impl.publish(m);
		
	}
	
	public synchronized void subscribe(int topic, Receiver r){
		impl.subscribe(topic,r);
	}

	public static void main(String args[]){
		int rc=0;
		String m=null;
		if (args.length==2){
			System.out.println("Usage: java xxx <repcount> <string>\nJust listening...");
			rc = Integer.parseInt(args[0]);
			m = args[1];
			System.out.println(m + rc);
		}
		final String msg = m;
		final int repcount=rc;
		final PubSubConnector psconn = PubSubConnectorImpl_TCP.getInstance();
		psconn.subscribe(PubSubMessage.aqlrequest, new Receiver(){
			public void handleDataPackage(PubSubMessage msg){
				System.out.println("\n$received:"+msg.getPayload());
			}
		});
		new Thread(){
			public void run(){
				int i = repcount;
				while(i-->0){
					PubSubMessage psm = new PubSubMessage(PubSubMessage.aqlrequest, msg+i);
					psconn.publish(psm);
					System.out.println("publishing in thread..." + i + msg);
					try {Thread.sleep(3000);} catch (Exception e) {System.out.print("interrupted");}
				}
			}
		}.start();
	}

	private void p(String s){
		//System.out.println(s);
	}
	
	private void p(Object o){
		p(o.toString());
	}
	
}


















