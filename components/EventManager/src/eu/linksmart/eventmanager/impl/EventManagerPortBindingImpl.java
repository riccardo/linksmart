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
package eu.linksmart.eventmanager.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.linksmart.clients.RemoteWSClientProvider;
import eu.linksmart.network.CryptoHIDResult;
import eu.linksmart.network.NetworkManagerApplication;

import eu.linksmart.eventmanager.impl.data.LocalSubscription;
import eu.linksmart.eventmanager.impl.data.LocalSubscriptions;
import eu.linksmart.eventmanager.impl.data.Publication;
import eu.linksmart.eventmanager.impl.data.Topic;
import eu.linksmart.external.ThreadPool;
import eu.linksmart.tools.EventManagerServerStatus;
import eu.linksmart.tools.GetEventManagerSubscriptions;

/**
 * 
 * Implementation of EventManagerPort and entry point for the OSGi DS component
 * 
 * See the documentation of EventManagerPort for further details
 *
 */
public class EventManagerPortBindingImpl implements eu.linksmart.eventmanager.EventManagerPort {
	public static LocalSubscriptions subscriptions = new LocalSubscriptions(); 
	private static Logger log = Logger.getLogger(EventManagerPortBindingImpl.class);
	public static final String CM_SERVICE_PID = "eu.linksmart.eventmanager.EventManager";
	
	public static final String EVENT_MANAGER_PATH = "/axis/services/EventManagerPort";
	public static final String NETWORK_MANAGER_PATH = "/axis/services/NetworkManagerApplication"; 
	
	private static LinkedList<Topic> topics = new LinkedList<Topic>();
	private static ThreadPool threads = new ThreadPool();


	private static int PRIORITY = 10;
	private static int MAXRETRIES = 3;

	private String eventManagerHID; 
	 
	private boolean activated = false;
	private HttpService http;
	
	private NetworkManagerApplication nmOSGi;
	private RemoteWSClientProvider clientProvider;
	private NetworkManagerApplication nmWSClient;
	private EventManagerConfigurator configurator;
	

	/**
	 * <b>start</b>
	 * Starts the EventManagerServer
	 * @throws Exception
	 */
	private void init() throws Exception {
		 
		 createHIDForEventManager(false);
	    
		log.info("Started EventManager");
		
	}

	protected void activate(ComponentContext context ) {
		log.debug("EventManager Activating!");
		if (subscriptions==null)
			subscriptions = new LocalSubscriptions();
		
		configurator = new EventManagerConfigurator(context.getBundleContext(), this);
		
		try {
		    init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (http != null) {
			try {
				log.info("Activating");
				http.registerServlet("/EventManagerStatus", new EventManagerServerStatus(context), null, null);
				http.registerServlet("/GetEventManagerSubscriptions", new GetEventManagerSubscriptions(context), null, null);
			} catch (ServletException e) {
				log.error(e.getMessage(), e);
			} catch (NamespaceException e) {
				log.error(e.getMessage(), e);
			}
		}
		
		configurator.registerConfiguration();
	
		this.activated  = true;
	    log.debug("EventManager Activated!");
	}

	/*This method is called:
	 * - init() -> Takes the configuration stored
	 * - updateConfiguration
	 * - bindWSProvider
	 * - bindNetworkManager
	*/
	private void createHIDForEventManager(boolean renewCert) {
		
		if (eventManagerHID!= null) return; //Only do this once
		
		boolean withNetworkManager = Boolean.parseBoolean(configurator.get(EventManagerConfigurator.USE_NETWORK_MANAGER));
	     
	     log.info("withNetworkManager: "+ withNetworkManager);
	     
	     if(withNetworkManager) {
	    	 boolean osgi = Boolean.parseBoolean( configurator.get(EventManagerConfigurator.USE_NETWORK_MANAGER_OSGI));
	    	 if (osgi == true) {
	    		 //Communicate directly with the Network Manager using OSGi
	    		 if (nmOSGi != null) {
	    			try {
	    				if ((configurator.get(EventManagerConfigurator.CERT_REF)==null) || renewCert == true) {
	    					String pid = configurator.get(EventManagerConfigurator.PID);
    						if ((pid == null)||(pid.equals(""))) {
    							pid = "EventManager:" + InetAddress.getLocalHost().getHostName();
    						}
    						String xmlAttributes = getXMLAttributeProperties(pid, "EventManagerPort", pid);
    						CryptoHIDResult result = nmOSGi.createCryptoHID(xmlAttributes, 
    								"http://localhost:"+System.getProperty("org.osgi.service.http.port")+ EVENT_MANAGER_PATH);
    						this.eventManagerHID = result.getHID();
    						configurator.setConfiguration(EventManagerConfigurator.CERT_REF, result.getCertRef());

	    				
	    				}
	    				else {
	    					//TODO refactoring 
	    					//We think this line is not sense making
//	    					this.eventManagerHID = nmOSGi.createCryptoHIDfromReference(configurator.get(EventManagerConfigurator.CERT_REF), 
//	    							"http://localhost:"+System.getProperty("org.osgi.service.http.port")+ EVENT_MANAGER_PATH);
//	    					if (this.eventManagerHID == null) {
	    						//Certificate ref is not valid...
	    						String pid = configurator.get(EventManagerConfigurator.PID);
	    						if ((pid == null)||(pid.equals(""))) {
	    							pid = "EventManager:" + InetAddress.getLocalHost().getHostName();
	    						}
	    						String xmlAttributes = getXMLAttributeProperties(pid, "EventManagerPort", pid);
	    						CryptoHIDResult result = nmOSGi.createCryptoHID(xmlAttributes, 
	    								"http://localhost:"+System.getProperty("org.osgi.service.http.port")+ EVENT_MANAGER_PATH);
	    						this.eventManagerHID = result.getHID();
	    						configurator.setConfiguration(EventManagerConfigurator.CERT_REF, result.getCertRef());
//	    					}
	    				}
	    				
    						
	    				log.info("EventManager HID: " + eventManagerHID);
	    			} catch (RemoteException e) {
	 					log.error(e.getMessage(), e);
	 				} catch (UnknownHostException e) {
	 					log.error(e.getMessage(), e);
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
	    		 }
	    		 
	    	 }
	    	 else {
	    		//Load the WS client and try to communicated with NM
	    		 if (clientProvider!=null) {
	    			 if (configurator.get(EventManagerConfigurator.NM_ADDRESS)!= null){
	    					try {
	    						
	    						if (nmWSClient==null) {
	    							try {
	    								this.nmWSClient = (NetworkManagerApplication)clientProvider.
	    										getRemoteWSClient(NetworkManagerApplication.class.getName(), 
	    										(String)configurator.get(EventManagerConfigurator.NM_ADDRESS), false);
	    							} catch (Exception e1) {
	    								log.error(e1.getMessage(), e1);
	    							}
	    						}
	    						if (configurator.get(EventManagerConfigurator.CERT_REF)!=null) {
	    	    					this.eventManagerHID = nmWSClient.createCryptoHIDfromReference(configurator.get(EventManagerConfigurator.CERT_REF), 
	    	    							"http://localhost:"+System.getProperty("org.osgi.service.http.port")+ EVENT_MANAGER_PATH);
	    	    					if (this.eventManagerHID == null) {
	    	    						//Certificate ref is not valid...
	    	    						String pid = configurator.get(EventManagerConfigurator.PID);
	    	    						if ((pid == null)||(pid.equals(""))) {
	    	    							pid = "EventManager:" + InetAddress.getLocalHost().getHostName();
	    	    						}
	    	    						String xmlAttributes = getXMLAttributeProperties(pid, "EventManagerPort", pid);
	    	    						CryptoHIDResult result = nmWSClient.createCryptoHID(xmlAttributes, 
	    	    								"http://localhost:"+System.getProperty("org.osgi.service.http.port")+ EVENT_MANAGER_PATH);
	    	    						this.eventManagerHID = result.getHID();
	    	    						configurator.setConfiguration(EventManagerConfigurator.CERT_REF, result.getCertRef());
	    	    					}
	    	    				}
	    	    				else {
	        						String pid = configurator.get(EventManagerConfigurator.PID);
	        						if ((pid == null)||(pid.equals(""))) {
	        							pid = "EventManager:" + InetAddress.getLocalHost().getHostName();
	        						}
	        						String xmlAttributes = getXMLAttributeProperties(pid, "EventManagerPort", pid);
	        						CryptoHIDResult result = nmWSClient.createCryptoHID(xmlAttributes, 
	        								"http://localhost:"+System.getProperty("org.osgi.service.http.port")+ EVENT_MANAGER_PATH);
	        						this.eventManagerHID = result.getHID();
	        						configurator.setConfiguration(EventManagerConfigurator.CERT_REF, result.getCertRef());

	    	    				}	    						
	    						
	    						log.info("EventManager HID: " + eventManagerHID);
	    						
	    					} catch (RemoteException e) {
	    						log.error(e.getMessage(), e);
	    					} catch (IOException e) {
	    						log.error(e.getMessage(), e);
							} 
	    			 }
	    			 else {
	    				 log.info("init - No Network Manager URL specfied. Will not be possible to create HID");
	    			 }
	    		 }
	    		 
	    	 }
	    	 
	    }
	}
	
	public String getXMLAttributeProperties(String pid, String sid, String desc)throws IOException
	{
		Properties descProps = new Properties();
		descProps.setProperty(NetworkManagerApplication.PID, pid);
		descProps.setProperty(NetworkManagerApplication.DESCRIPTION, desc);
		descProps.setProperty(NetworkManagerApplication.SID, sid);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		descProps.storeToXML(bos, "");
		return bos.toString();
	}
	

	protected void deactivate(ComponentContext context ) {
		//Remove the HID from NM!
		removeEventManagerHID();
		
		//Clear subscriptions
		stop();
		System.gc();
		System.runFinalization();
		System.gc();
		log.debug("EventManager Deactivated");
	}
	
	private void removeEventManagerHID() {
		if (eventManagerHID== null) return; //Only do this once
		
		boolean withNetworkManager = Boolean.parseBoolean(configurator.get(EventManagerConfigurator.USE_NETWORK_MANAGER));
	     
	     log.info("withNetworkManager: "+ withNetworkManager);
	     
	     
	     boolean osgi = Boolean.parseBoolean( configurator.get(EventManagerConfigurator.USE_NETWORK_MANAGER_OSGI));
	    	 if (osgi == true) {
	    		 //Communicate directly with the Network Manager using OSGi
	    		 if (nmOSGi != null) {
	    			try {
	    				this.nmOSGi.removeHID(eventManagerHID);
	    				this.eventManagerHID = null;	 					
	    				log.info("Removed Event Manager HID");
	    			} catch (RemoteException e) {
	 					log.error(e.getMessage(), e);
	 				}
	    		 }
	    		 
	    	 }
	    	 else {
	    		//Load the WS client and try to communicated with NM
	    		 if (clientProvider!=null) {
	    			 if (configurator.get(EventManagerConfigurator.NM_ADDRESS)!= null){
	    					try {
	    						if (nmWSClient==null) {
	    							try {
	    								this.nmWSClient = (NetworkManagerApplication)clientProvider.
	    										getRemoteWSClient(NetworkManagerApplication.class.getName(), 
	    										(String)configurator.get(EventManagerConfigurator.NM_ADDRESS), false);
	    							} catch (Exception e1) {
	    								log.error(e1.getMessage(), e1);
	    							}
	    						}
	    						nmWSClient.removeHID(eventManagerHID);
	    		    			this.eventManagerHID = null;	 					
	    		    			log.info("Removed Event Manager HID");
	    						
	    					} catch (RemoteException e) {
	    						log.error(e.getMessage(), e);
	    					} catch (Exception e) {
	    						log.error(e.getMessage(), e);
	    					}
	    			 }
	    			 else {
	    				 log.info("init - No Network Manager URL specfied. Will not be possible to create HID");
	    			 }
	    		 }
	    		 
	    	 }
	    	 
	    
		
	}
	
	protected void setConfigurationAdmin(ConfigurationAdmin cm) {
		if (configurator != null) {
			configurator.bindConfigurationAdmin(cm);
			if (activated == true) {
				configurator.registerConfiguration();
			}
		}
	}
	

	protected void unsetConfigurationAdmin(ConfigurationAdmin cm) {
		configurator.unbindConfigurationAdmin(cm);
	}
	
	protected void setNm (NetworkManagerApplication nm) {
		this.nmOSGi = nm;
		if (activated) {
				createHIDForEventManager(false);
			}
			
	}
	
	protected void unsetNm (NetworkManagerApplication nm) {
		if (activated) {
			removeEventManagerHID();
		}
		this.nmOSGi = null;
		
	}
	
	protected void setClientProvider(RemoteWSClientProvider clientProvider) {
		log.info("RemoteWSClientProvider bound");
		this.clientProvider = clientProvider;
		if (activated) {
			try {
				this.nmWSClient = (NetworkManagerApplication)clientProvider.
						getRemoteWSClient(NetworkManagerApplication.class.getName(), 
						(String)configurator.get(EventManagerConfigurator.NM_ADDRESS), false);
			} catch (Exception e1) {
				log.error(e1.getMessage(), e1);
				e1.printStackTrace();
			}

			createHIDForEventManager(false);
		}
	}
	
	protected void unsetClientProvider(RemoteWSClientProvider clientProvider) {
		removeEventManagerHID();
		this.clientProvider = null;
		this.nmWSClient = null;
		log.info("RemoteWSClientProvider unbound");
	}
	
	protected void setHttpService(HttpService http) {
		log.info("Disabled registration of servlet");
		this.http = http;
		if (activated) {
				log.info("Disabled registration of servlet");
		}	
	}
	
	protected void unsetHttpService(HttpService http) {
		http.unregister("/EventManagerStatus");
		http.unregister("/getEventManagerSubscriptions");
	}
	
	@SuppressWarnings("unchecked")
	public void applyConfigurations(Hashtable updates) {
		// TODO Fill it to change configuration, for example when the withNetworkManager property changes
		log.info("Applying configurations");
		boolean createdHID = false;
		if (updates.containsKey(EventManagerConfigurator.PID)) {
			log.info("Applying configurations");
			removeEventManagerHID();
			createHIDForEventManager(true);
			createdHID = true;
		}
		
		if (updates.containsKey(EventManagerConfigurator.USE_NETWORK_MANAGER)) {
			boolean useNetworkManager = Boolean.parseBoolean((String) updates.get(EventManagerConfigurator.USE_NETWORK_MANAGER));
			if (useNetworkManager == true) {
				if (createdHID == false)
				createHIDForEventManager(false);
			}
			else {
				removeEventManagerHID();
			}
		}
		
	}

	@Override
	public boolean subscribe(java.lang.String topic, String subscriber) throws java.rmi.RemoteException {
		
		log.info("EventManager subscribe on: " + topic + " from " + subscriber);
		boolean result;
		try {
			subscriptions.add(topic, new URL(subscriber.toString()));
			if(!this.hasTopic(topic)) {
				this.insertTopic(new Topic(topic, PRIORITY));
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return result;
	}
	
	private boolean hasTopic(String topic) {
		Iterator<Topic> topicsIterator = topics.iterator();
		while(topicsIterator.hasNext()) {
			Topic currentTopic = topicsIterator.next();
			if(currentTopic.getTopicName().equals(topic))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean subscribeWithHID(String topic, String hid) throws java.rmi.RemoteException {
		try {
			if(!Boolean.parseBoolean(configurator.get(EventManagerConfigurator.USE_NETWORK_MANAGER)))
				return false;
			if (eventManagerHID == null) return false;
			log.info("EventManager subscribeWithHID on: " + topic + " from " + hid);
			
			//FIXME Put in the configuration just the address and then build the addresses based on the path, which is always the same
			String nmAddress = ((String)configurator.get(EventManagerConfigurator.NM_ADDRESS));
			String nmLocation = nmAddress.substring(0, nmAddress.indexOf("/axis/services/NetworkManagerApplication"));
			subscriptions.add(topic, new URL(nmLocation+ "/SOAPTunneling"+"/"+eventManagerHID+"/"+hid+"/0/"), hid);
			if(!this.hasTopic(topic)) {
				this.insertTopic(new Topic(topic, PRIORITY));
			}
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean unsubscribe(java.lang.String topic, String subscriber) throws java.rmi.RemoteException {
		try {
			subscriptions.remove(topic, new URL(subscriber));
			if(subscriptions.getMatches(topic).size() == 0)
				topics.remove(new Topic(topic, getTopicPriority(topic)));
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean unsubscribeWithHID(java.lang.String topic, java.lang.String hid) throws java.rmi.RemoteException {
		try {
			if(!Boolean.parseBoolean(configurator.get(EventManagerConfigurator.USE_NETWORK_MANAGER)))
				return false;
			boolean result = subscriptions.remove(topic, hid);
			if(subscriptions.getMatches(topic).size() == 0)
				topics.remove(new Topic(topic, getTopicPriority(topic)));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public eu.linksmart.eventmanager.Subscription[] getSubscriptions() throws java.rmi.RemoteException {
		eu.linksmart.eventmanager.Subscription[] result 
		= new eu.linksmart.eventmanager.Subscription[subscriptions.getSubscriptions().size()];
		for (int i = 0; i < subscriptions.getSubscriptions().size(); i++) {
			eu.linksmart.eventmanager.Subscription axisSubscription = new eu.linksmart.eventmanager.Subscription();
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(subscriptions.getSubscriptions().get(i).getDate());
			axisSubscription.setDate(calendar);
			axisSubscription.setTopic(subscriptions.getSubscriptions().get(i).getTopic());
			if(subscriptions.getSubscriptions().get(i).getHID() != null)
				axisSubscription.setHID(subscriptions.getSubscriptions().get(i).getHID());
			axisSubscription.setURL(subscriptions.getSubscriptions().get(i).getURL().toString());
			result[i] = axisSubscription;
		}
		return result;
	}

	@Override
	public boolean clearSubscriptions(String subscriber) throws java.rmi.RemoteException {
		try {
			Iterator<String> topicsToRemoveIterator = subscriptions.clear(new URL(subscriber)).iterator();
			while(topicsToRemoveIterator.hasNext()) {
				String topic = topicsToRemoveIterator.next();
				topics.remove(new Topic(topic, getTopicPriority(topic)));
			}
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean clearSubscriptionsWithHID(java.lang.String hid) throws java.rmi.RemoteException {
		try {
			if(!Boolean.parseBoolean(configurator.get(EventManagerConfigurator.USE_NETWORK_MANAGER)))
				return false;
			Iterator<String> topicsToRemoveIterator = subscriptions.clear(hid).iterator();
			while(topicsToRemoveIterator.hasNext()) {
				String topic = topicsToRemoveIterator.next();
				topics.remove(new Topic(topic, getTopicPriority(topic)));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void insertTopic(Topic t) {
		if(topics.size() == 0)
			topics.add(t);
		else {
			Iterator<Topic> topicsIterator = topics.iterator();
			int i = 0;
			boolean inserted = false;
			while(topicsIterator.hasNext()) {
				Topic currentTopic = topicsIterator.next();
				if(currentTopic.getPriority() > t.getPriority()) {
					topics.add(i, t);
					inserted = true;
					break;
				}
				else
					i++;
			}
			if(!inserted)
				topics.add(t);
		}
	}
	
	private int getTopicPriority(String topicPattern) {
		Iterator<Topic> topicsIterator = topics.iterator();
		while(topicsIterator.hasNext()) {
			Topic currentTopic = topicsIterator.next();
			if(topicPattern.matches(currentTopic.getTopicName()))
				return currentTopic.getPriority();
		}
		return -1;
	}

	
	@Override
	public boolean publish(final java.lang.String topicPattern, final eu.linksmart.eventmanager.Part[] event) throws java.rmi.RemoteException {
		log.info("EventManager publish on: " + topicPattern);
		Thread t = new Thread(new Runnable() {
			public void run() {
				for (final LocalSubscription subscription: subscriptions.getMatches(topicPattern)) {
					new Thread(new Runnable() {
						

						public void run() {
						
						if (subscription.getCounter() < MAXRETRIES) {

							log.info("                     to: " + subscription.getURL());
							
							try {
								subscription._getPort().notify(topicPattern,event);
								//TODO!!!
							} catch (RemoteException e) {
								subscription.increaseCounter();
								log.error("No anwser from subscriber..." + e.getMessage(), e);
							}
						}
						else {
							log.info("Removing subscription from ");
							if (subscription.getHID() != null)
								subscriptions.remove(subscription.getTopic(), subscription.getHID());						
							else subscriptions.remove(subscription.getTopic(), subscription.getURL());
						}
					}
					}).start();
				}
				}
			});
		int topicPriority = getTopicPriority(topicPattern);
		if(topicPriority == -1)
			return true;
		threads.runTask(new Publication(t, new Topic(topicPattern, topicPriority)));
		
		return true;
	}
	
	@Override
	public boolean setPriority(java.lang.String topic, int priority) {
		//set the priority of the topic to the given value, order the topics structure and the threads structure
		if(priority <= 0) 
			return false;
		Iterator<Topic> topicsIterator = topics.iterator();
		while(topicsIterator.hasNext()) {
			Topic currentTopic = topicsIterator.next();
			if(currentTopic.getTopicName().equals(topic)) {
				topics.remove(currentTopic);
				Topic freshTopic = new Topic(topic, priority);
				insertTopic(freshTopic);
				threads.updateTasks(topic, priority);
				return true;
			}
		}
		return false;
	}

	public static void stop() {
		subscriptions = null;
	}
}
