package eu.linksmart.event.publication.impl;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.event.publication.EventPublicationWrapper;
import eu.linksmart.eventmanager.EventManagerPort;
import eu.linksmart.eventmanager.Part;
import eu.linksmart.remote.service.store.LinkSmartRemoteServiceStore;

public class EventPublicationWrapperImpl implements EventPublicationWrapper {
	
	private Logger LOG = Logger.getLogger(EventPublicationWrapperImpl.class.getName());
	protected SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" );
	private LinkSmartRemoteServiceStore remoteServiceStore;
	private EventManagerLocatorThread locatorThread;
	private QueuedEventsPublisherThread queuedEventsPublisherThread;
	
	//Map<ServiceID,EventManager>
	private Map<String, EventManager> eventManagers;
	private LimitedQueue<Event> eventQueue;
	//How many events can be queued
	private final int QUEUE_LIMIT = 8192;	
	
	protected void activate(ComponentContext context) {
		LOG.info("Starting "
				+ context.getBundleContext().getBundle().getSymbolicName());
		
		// Get the LinkSmartRemoteServiceStore
		remoteServiceStore = (LinkSmartRemoteServiceStore) context
				.locateService(LinkSmartRemoteServiceStore.class
						.getSimpleName());
				
		eventManagers = new HashMap<String, EventManager>();
		eventQueue = new LimitedQueue<Event>(QUEUE_LIMIT);
		
		LOG.info("Started "
				+ context.getBundleContext().getBundle().getSymbolicName());
	}
	
	protected void deactivate(ComponentContext context) {
		LOG.info("Stopping "
				+ context.getBundleContext().getBundle().getSymbolicName());
		
		if (locatorThread != null){
			locatorThread.end();
		}
		if (queuedEventsPublisherThread != null) {
			queuedEventsPublisherThread.end();
		}
		
		LOG.info("Stopped "
				+ context.getBundleContext().getBundle().getSymbolicName());
	}

	@Override
	public boolean publishEvent(String serviceID, String topic, Part[] valueParts)
			throws RemoteException {
		Part[] timestampedParts = addTimestamp(valueParts);
		
		if (!isEventManagerLocated(serviceID)) {
			LOG.warn("Unable to publish. Event Manager not found: Topic=" + topic);
			return false;
		}
				
		LOG.debug("Trying to publish sensor event: Topic=" + topic);
		
		try {
			getEventManager(serviceID).eventManagerPort.publish(topic, timestampedParts);
			return true;
		}
		catch (RemoteException ex) {
			//if Event Manager disappears, start new search for that Event Manager
			String eventManagerPID = eventManagers.get(serviceID).eventManagerPID;
			remoteServiceStore.removeRemoteHydraServiceByDescription(eventManagerPID);
			eventManagers.remove(serviceID);
			findEventManager(serviceID, eventManagerPID);
			LOG.warn("Unable to publish. Event Manager not found: Topic=" + topic);
			return false;
		}
	}

	@Override
	public boolean publishEventBuffered(String serviceID, String topic,
			Part[] valueParts) {
		Part[] timestampedParts = addTimestamp(valueParts);
		
		if (!isEventManagerLocated(serviceID)) {
			LOG.warn("Unable to publish. Event Manager not found: Topic=" + topic);
			queueEvent(serviceID, topic, timestampedParts);
			return false;
		}
		LOG.debug("Trying to publish sensor event: Topic=" + topic);
		
		try {
			getEventManager(serviceID).eventManagerPort.publish(topic, timestampedParts);
			return true;
		}
		catch (RemoteException ex) {
			queueEvent(serviceID, topic, timestampedParts);
			if (queuedEventsPublisherThread != null) {
				queuedEventsPublisherThread.end();
			}
			//if Event Manager disappears, start new search for that Event Manager
			String eventManagerPID = eventManagers.get(serviceID).eventManagerPID;
			remoteServiceStore.removeRemoteHydraServiceByDescription(eventManagerPID);
			eventManagers.remove(serviceID);
			findEventManager(serviceID, eventManagerPID);
			LOG.warn("Unable to publish. Event Manager not found: Topic=" + topic);
			return false;
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

	/**
	 * Adds the current timestamp to the event
	 * @param event Event to add timestamp to
	 * @return event including timestamp
	 */
	private Part[] addTimestamp(Part[] event) {
		Part timestamp = new Part("timestamp", sdf.format(new Date()));
		Part[] timestampedParts = new Part[event.length+1];
		System.arraycopy(event, 0, timestampedParts, 0, event.length);
		timestampedParts[timestampedParts.length-1] = timestamp;
		return timestampedParts;
	}
	
	/**
	 * Removes timestamp from event and queues it. Once the appropriate EventManager
	 * is available again, the next try for the event publication is started.
	 * @param serviceID
	 * @param topic
	 * @param event
	 */
	private void queueEvent(String serviceID, String topic, Part[] event) {
		//Remove timestamp
		Part[] eventWithoutTimestamp = new Part[event.length-1];
		System.arraycopy(event, 0, eventWithoutTimestamp, 0, event.length-1);
		
		eventQueue.add(new Event(serviceID, topic, event));
	}

	/**
	 * Republishes the queued events
	 * @author Jentsch
	 *
	 */
	private class QueuedEventsPublisherThread implements Runnable {
		private boolean stopped = false;
		private String serviceID;
		
		public QueuedEventsPublisherThread(String serviceID) {
			this.serviceID = serviceID;
		}

		@Override
		public void run() {
			//There is good chance that the EventManager was not reachable by subscribers as well.
			//So, we wait a few seconds before publishing the queued events for giving subscribers
			//some time to subscribe
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) { }
			
			while (!stopped && !eventQueue.isEmpty()) {
				Event event = removeFirstEvent(serviceID);
				if (event != null) {
					try {
						LOG.debug("Publish queued event: " + event.topic + ". Queue size: " + eventQueue.size());
						boolean republishSuccess = publishEventBuffered(event.serviceID, event.topic, event.value);
						//New Thread is started when EventManager is relocated
						if (!republishSuccess) {
							stopped = true;
						}
					}
					catch (Exception e) {}
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {}
			}
		}
		
		/**
		 * Retrieves and removes first element with service ID from eventqueue
		 * @return Event element or null if not found
		 */
		private Event removeFirstEvent(String serviceID) {
			for (Event event : eventQueue) {
				if (event.serviceID.equalsIgnoreCase(serviceID)) {
					eventQueue.remove(event);
					return event;
				}
			}
			return null;
		}
		
		public synchronized void end() {
			this.stopped = true;
		}
	}
	
	/**
	 * Continuously tries to locate EventManager
	 * @author Jentsch
	 *
	 */
	private class EventManagerLocatorThread implements Runnable {
		private String serviceID;
		private String eventManagerDescription;
		private boolean stopped = false;

		public synchronized void end() {
			this.stopped = true;
		}

		public EventManagerLocatorThread(String serviceID, String eventManagerDescription) {
			this.serviceID = serviceID;
			this.eventManagerDescription = eventManagerDescription;
		}

		@Override
		public void run() {
			while (eventManagers.get(serviceID) == null && !stopped) {
				try {
					//try to locate EventManager
					EventManagerPort eventManagerPort = (EventManagerPort) remoteServiceStore
							.getRemoteHydraServiceByDescription(
									eventManagerDescription,
									EventManagerPort.class.getName());
					if (eventManagerPort != null) {
						//if EventManager was found, save it with associated service ID
						EventManager eventManager = new EventManager(eventManagerPort, eventManagerDescription);
						eventManagers.put(serviceID, eventManager);
						//Start queue publisher, for eventually queued events
						queuedEventsPublisherThread = new QueuedEventsPublisherThread(serviceID);
						Thread publisherThread = new Thread(queuedEventsPublisherThread);
						publisherThread.start();
						stopped = true;
					}
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
	
	/**
	 * A queue with a maximum size. If the queue reached its limit and another element
	 * is added, the oldest element is removed.
	 * @author Jentsch
	 *
	 * @param <E>
	 */
	private class LimitedQueue<E> extends LinkedList<E> {
	    private int limit;

	    public LimitedQueue(int limit) {
	        this.limit = limit;
	    }

	    @Override
	    public boolean add(E o) {
	        super.add(o);
	        while (size() > limit) { super.remove(); }
	        return true;
	    }
	}

	protected EventManager getEventManager(String serviceID) {
		return this.eventManagers.get(serviceID);
	}

	public boolean isEventManagerLocated(String serviceID) {
		return (getEventManager(serviceID) == null ? false : true);
	}
	
	private class EventManager {
		public EventManagerPort eventManagerPort = null;
		public String eventManagerPID = null;
		
		public EventManager(EventManagerPort eventManagerPort, String eventManagerPID) {
			this.eventManagerPort = eventManagerPort;
			this.eventManagerPID = eventManagerPID;
		}
	}

	private class Event {
		public String serviceID = null;
		public String topic = null;
		public Part[] value = null;
		
		public Event(String serviceID, String topic, Part[] value) {
			this.serviceID = serviceID;
			this.topic = topic;
			this.value = value;
		}
	}
}
