package eu.linksmart.event.subscription.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.event.subscription.EventSubscriptionWrapper;
import eu.linksmart.eventmanager.EventManagerPort;
import eu.linksmart.eventmanager.EventSubscriber;
import eu.linksmart.network.NetworkManagerApplication;
import eu.linksmart.remote.service.store.LinkSmartRemoteServiceStore;

public class EventSubscriptionWrapperImpl implements EventSubscriptionWrapper {

	private static final String OSGI_SERVICE_HTTP_PORT = System
			.getProperty("org.osgi.service.http.port");
	private static final String AXIS_SERVICES_PATH = "http://localhost:"
			+ OSGI_SERVICE_HTTP_PORT + "/axis/services/";

	private Logger LOG = Logger.getLogger(EventSubscriptionWrapperImpl.class.getName());
	private ComponentContext context;
	private LinkSmartRemoteServiceStore remoteServiceStore;
	private EventManagerQueuedSubscriberThread subscriberThread;
	private EventManagerLocatorThread locatorThread;
	private NetworkManagerApplication networkManager;
	
	// Subscriber Service ID <-> EventManager
	private Map<String, EventManagerPort> eventManagers;
	// Subscriber Service ID <-> HID
	private Map<String, String> subscriberHIDs;
	// Subscriber Service ID <-> ServiceRegistration
	private Map<String, ServiceRegistration> serviceRegistrations;
	//Topic -> Service IDs
	private Map<String, List<String>> topicIDs;
	//Topic-ServiceID combinations which still must be subscribed
	private LinkedList<String []> queuedTopicToSubscribe = new LinkedList<String []>();

	protected void activate(ComponentContext context) {
		LOG.info("Starting "
				+ context.getBundleContext().getBundle().getSymbolicName());
		
		this.context = context;
		subscriberHIDs = new HashMap<String, String>();
		eventManagers = new HashMap<String, EventManagerPort>();
		topicIDs = new HashMap<String, List<String>>();
		serviceRegistrations = new HashMap<String, ServiceRegistration>();

		// Get the LinkSmartRemoteServiceStore
		remoteServiceStore = (LinkSmartRemoteServiceStore) context
				.locateService(LinkSmartRemoteServiceStore.class
						.getSimpleName());

		// Get the NetworkManager
		networkManager = (NetworkManagerApplication) context
				.locateService(NetworkManagerApplication.class.getSimpleName());

		//Start subscriber thread
		subscriberThread = new EventManagerQueuedSubscriberThread();
		Thread subscriberQueueThread = new Thread(subscriberThread);
		subscriberQueueThread.start();
		
		LOG.info("Started "
				+ context.getBundleContext().getBundle().getSymbolicName());
	}

	protected void deactivate(ComponentContext context) {
		LOG.info("Stopping "
				+ context.getBundleContext().getBundle().getSymbolicName());
		
		// Stop running threads
		if (subscriberThread != null){
			subscriberThread.end();
		}
		if (locatorThread != null){
			locatorThread.end();
		}

		// Unsubscribe from EventManagers
		if (eventManagers != null) { 
			for (String serviceID : eventManagers.keySet()) {
				try {
					eventManagers.get(serviceID).clearSubscriptionsWithDescription(serviceID);
				} catch (RemoteException e) {
				}
			}
		}
		
		LOG.info("Stopped "
				+ context.getBundleContext().getBundle().getSymbolicName());
	}

	@Override
	public void registerCallback(EventSubscriber subscriber, String serviceID) {
		// Register the Subscriber at the NetworkManager
		try {
			String subscriberURL = AXIS_SERVICES_PATH + serviceID;
			//Save HID associated to service ID
			subscriberHIDs.put(serviceID,
					networkManager.createHIDwDesc(serviceID, subscriberURL));
		} catch (RemoteException e) {
			LOG.error("Unable to create HID for " + serviceID, e);
		}

		// Publish as Web Service
		Hashtable props = new Hashtable();
		props.put("SOAP.service.name", serviceID);
		serviceRegistrations.put(serviceID,
			context.getBundleContext().registerService(EventSubscriber.class.getName(),
					subscriber, props));
	}

	@Override
	public void deregisterCallback(String serviceID) {
		String subscriberHID = subscriberHIDs.get(serviceID);
		// Deregister service at Network Manager
		try {
			networkManager.removeHID(subscriberHID);
		} catch (RemoteException e) {
			LOG.error("Unable to deregister HID: " + subscriberHID, e);
		}
		ServiceRegistration sr = serviceRegistrations.remove(serviceID);
		if (sr != null) {
			sr.unregister();
		}
	}

	@Override
	public void findEventManager(String serviceID, String eventManagerPID) {
		// Start the thread that looks for the EventManager
		locatorThread = new EventManagerLocatorThread(serviceID,
				eventManagerPID);
		Thread myLocatorThread = new Thread(locatorThread);
		myLocatorThread.start();
	}

	@Override
	public synchronized void subscribeWithTopic(String serviceID, String topic) {
		//Put topic to subscription queue
		if (!isTopicQueued(serviceID, topic)) {
			queuedTopicToSubscribe.add(new String[]{topic, serviceID});
		}
		//Save topic with associated service ID
		List<String> servicesSubscribedToTopic = topicIDs.get(topic);
		if (servicesSubscribedToTopic == null) {
			servicesSubscribedToTopic = new LinkedList<String>();
		}
		servicesSubscribedToTopic.add(serviceID);
		topicIDs.put(topic, servicesSubscribedToTopic);
		LOG.debug("Added topic '" + topic + "' for service '" + serviceID + "' to subscription queue");
	}
	
	/**
	 * Checks whether the serviceID/topic combination is already queued
	 * @return true if already queued, false if not
	 */
	private boolean isTopicQueued(String serviceID, String topic) {
		for (String[] queuedTopic : queuedTopicToSubscribe) {
			if (topic.equalsIgnoreCase(queuedTopic[0])
					&& serviceID.equalsIgnoreCase(queuedTopic[1])) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void unsubscribeAllTopics(String serviceID) {
		EventManagerPort eventManager = eventManagers.get(serviceID);
		try {
			eventManager.clearSubscriptionsWithDescription(serviceID);
			//Update internal map
			for (String topic : topicIDs.keySet()) {
				List<String> servicesSubscribedToTopic = topicIDs.get(topic);
				servicesSubscribedToTopic.remove(serviceID);
				topicIDs.put(topic, servicesSubscribedToTopic);
			}
		} catch (RemoteException e) {
			LOG.error("Unable to unsubscribeAllTopics from ID: " + serviceID, e);
		}
	}

	@Override
	public void unsubscribeTopic(String serviceID, String topic) {
		EventManagerPort eventManager = eventManagers.get(serviceID);
		try {
			eventManager.unsubscribeWithDescription(topic, serviceID);
			//Remove from internal map
			List<String> servicesSubscribedToTopic = topicIDs.get(topic);
			servicesSubscribedToTopic.remove(serviceID);
			topicIDs.put(topic, servicesSubscribedToTopic);
		} catch (RemoteException e) {
			LOG.error("Unable to unsubscribe topic(" + topic + ") from HID: " + serviceID, e);
		}
	}

	/**
	 * Continuously subscribed queued topics to Event Managers
	 */
	private class EventManagerQueuedSubscriberThread implements Runnable {

		private boolean shouldStop = false;

		public synchronized void end() {
			this.shouldStop = true;
		}

		@Override
		public void run() {
			while (!shouldStop) {
				// Wait for a second
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					LOG.error("Error while waiting.");
				}
				
				if (!queuedTopicToSubscribe.isEmpty()) {
					//Get first topic-ServiceID combination which must be subscribed
					String topic = queuedTopicToSubscribe.getFirst()[0];
					String serviceID = queuedTopicToSubscribe.getFirst()[1];
					//Get EventManager to which is shall be subscribed to
					EventManagerPort eventManager = eventManagers.get(serviceID);
					
					if (eventManager != null && serviceID != null) {
						try {
							LOG.info("*********** Now trying to subscribe with TOPIC: "
									+ topic
									+ "(with Description: "
									+ serviceID + ")");
							// try to subscribe 
							if (eventManager.subscribeWithDescription(topic, serviceID, 0)) {
								queuedTopicToSubscribe.removeFirst();
								LOG.info("*************** Successfully subscribed to topic: "
										+ topic
										+ "(with Description: "
										+ serviceID + ")");
							} else {
								LOG.warn("Unable to subscribe to topic: "
										+ topic + ". Trying again.");
							}
						} catch (RemoteException e) {
							LOG.error("Unable to subscribe to topic: "
									+ topic + ". Trying again.", e);
						}
					}
				}
			}
		}
	}

	/**
	 * Find Event Manager
	 */
	private class EventManagerLocatorThread implements Runnable {

		private String eventManagerDescription;
		private String serviceID;
		private boolean shouldStop = false;

		public synchronized void end() {
			this.shouldStop = true;
		}

		public EventManagerLocatorThread(String serviceID,
				String eventManagerDescription) {
			this.serviceID = serviceID;
			this.eventManagerDescription = eventManagerDescription;
		}

		@Override
		public void run() {
			while (eventManagers.get(serviceID) == null && !shouldStop) {
				try {
					//Try to find event manager
					EventManagerPort eventManager = (EventManagerPort) remoteServiceStore
							.getRemoteHydraServiceByDescription(
									eventManagerDescription,
									EventManagerPort.class.getName());
					//clear old subscriptions
					eventManager.clearSubscriptionsWithDescription(serviceID);
					//save with associated service ID
					eventManagers.put(serviceID, eventManager);
				} catch (Exception e) {
					LOG.error("Cannot find EventManager for description "
							+ eventManagerDescription + ". Trying again");
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					LOG.error("Error while waiting.");
				}
			}
		}
	}
}
