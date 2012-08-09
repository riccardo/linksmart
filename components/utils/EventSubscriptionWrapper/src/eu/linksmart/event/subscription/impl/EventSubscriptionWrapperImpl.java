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
	//Topic -> Service HID
	private Map<String, String> topicIDs;
	
	private LinkedList<String> queuedTopicToSubscribe = new LinkedList<String>();
	private List<String> subscribedTopics = new ArrayList<String>();

	protected void activate(ComponentContext context) {
		LOG.info("Starting "
				+ context.getBundleContext().getBundle().getSymbolicName());
		this.context = context;
		subscriberHIDs = new HashMap<String, String>();
		eventManagers = new HashMap<String, EventManagerPort>();
		topicIDs = new HashMap<String, String>();

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
	}

	protected void deactivate(ComponentContext context) {
		// Stop running threads
		subscriberThread.end();
		locatorThread.end();

		// Unsubscribe from EventManagers
		Iterator<EventManagerPort> it = eventManagers.values().iterator();
		while (it.hasNext()) {
			EventManagerPort eventManager = it.next();
			for (String topic : subscribedTopics) {
				try {
					String subscriberHID = subscriberHIDs.get(topicIDs.get(topic));
					boolean success = eventManager.unsubscribeWithHID(topic, subscriberHID);
					LOG.info("Successfully deregistered topic from EventManager. "
							+ success);
				} catch (RemoteException e) {
					LOG.error("Unable to unsubscribe from EventManager!", e);
				}
			}
		}
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
		context.getBundleContext().registerService(EventSubscriber.class.getName(),
				subscriber, props);
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
		if (!subscribedTopics.contains(topic)
				&& !queuedTopicToSubscribe.contains(topic)) {
			//Put topic to queue
			queuedTopicToSubscribe.add(topic);
			//Save topic with associated HID
			topicIDs.put(topic, serviceID);
			LOG.debug("Added topic to subscription queue: " + topic);
		}
	}
	
	@Override
	public void unsubscribeAllTopics(String serviceID) {
		// TODO Auto-generated method stub
		EventManagerPort eventManager = eventManagers.get(serviceID);
		String subscriberHID = subscriberHIDs.get(serviceID);
		try {
			eventManager.clearSubscriptionsWithHID(subscriberHID);
		} catch (RemoteException e) {
			LOG.error("Unable to unsubscribeAllTopics from HID: " + subscriberHID, e);
		}
	}

	@Override
	public void unsubscribeTopic(String serviceID, String topic) {
		// TODO Auto-generated method stub
		EventManagerPort eventManager = eventManagers.get(serviceID);
		String subscriberHID = subscriberHIDs.get(serviceID);
		try {
			eventManager.unsubscribe(topic, subscriberHID);
		} catch (RemoteException e) {
			LOG.error("Unable to unsubscribe topic(" + topic + ") from HID: " + subscriberHID, e);
		}
	}

	/**
	 * Continuously subscribed queued topics to Event Managers
	 */
	private class EventManagerQueuedSubscriberThread implements Runnable {

		private String topic;
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
					String topic = queuedTopicToSubscribe.getFirst();
					String serviceID = topicIDs.get(topic);
					EventManagerPort eventManager = eventManagers.get(serviceID);
					String subscriberHID = subscriberHIDs.get(serviceID);
					
					if (eventManager != null && subscriberHID != null) {
						try {
							LOG.info("*********** Now trying to subscribe with TOPIC: "
									+ topic);
							// try to subscribe topics in queue, if succeed
							// move the topic to subscribedTopic, if failed
							// re-queue the topic
							if (eventManager.subscribeWithHID(topic, subscriberHID,	0)) {
								queuedTopicToSubscribe.removeFirst();
								subscribedTopics.add(topic);
								LOG.info("*************** Successfully subscribed to topic: "
										+ topic
										+ "(with HID: "
										+ subscriberHID + ")");
							} else {
								LOG.warn("Unable to subscribe to topic: "
										+ topic + ". Trying again.");
							}
						} catch (RemoteException e) {
							LOG.error("Unable to subscribe to topic: "
									+ topic + ". Trying again.", e);
							queuedTopicToSubscribe.add(topic);
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
