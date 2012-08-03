package eu.linksmart.event.publication.impl;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
	
	//Map<ServiceID,EventManager>
	private Map<String, EventManagerPort> eventManagers;
	
	protected void activate(ComponentContext context) {
		// Get the LinkSmartRemoteServiceStore
		remoteServiceStore = (LinkSmartRemoteServiceStore) context
				.locateService(LinkSmartRemoteServiceStore.class
						.getSimpleName());
				
		eventManagers = new HashMap<String, EventManagerPort>();
	}
	
	protected void deactivate(ComponentContext context) {
		locatorThread.end();
	}

	@Override
	public boolean publishEvent(String serviceID, String topic, Part[] valueParts)
			throws RemoteException {
		//Get event manager for service ID
		if (getEventManager(serviceID) == null) {
			LOG.warn("Unable to publish Sensor Reading: Topic=" + topic);
			return false;
		}
		
		//Add timestamp
		Part timestamp = new Part("timestamp", sdf.format(new Date()));
		Part[] timestampedParts = new Part[valueParts.length+1];
		System.arraycopy(valueParts, 0, timestampedParts, 0, valueParts.length);
		timestampedParts[timestampedParts.length-1] = timestamp;
		
		LOG.debug("Trying to publish sensor event: Topic=" + topic);
		
		return getEventManager(serviceID).publish(topic, timestampedParts);
	}

	@Override
	public void findEventManager(String serviceID, String eventManagerPID) {
		// Start the thread that looks for the EventManager
		locatorThread = new EventManagerLocatorThread(serviceID,
				eventManagerPID);
		Thread myLocatorThread = new Thread(locatorThread);
		myLocatorThread.start();
	}
	
	protected EventManagerPort getEventManager(String serviceID) {
		return this.eventManagers.get(serviceID);
	}
	
	public boolean isEventManagerLocated(String serviceID) {
		return (getEventManager(serviceID) == null ? false : true);
	}
	
	private class EventManagerLocatorThread implements Runnable {
		private String serviceID;
		private String eventManagerDescription;
		private boolean shouldStop = false;

		public synchronized void end() {
			this.shouldStop = true;
		}

		public EventManagerLocatorThread(String serviceID, String eventManagerDescription) {
			this.serviceID = serviceID;
			this.eventManagerDescription = eventManagerDescription;
		}

		@Override
		public void run() {
			while (eventManagers.get(serviceID) == null && !shouldStop) {
				try {
					//try to locate EventManager
					EventManagerPort eventManager = (EventManagerPort) remoteServiceStore
							.getRemoteHydraServiceByDescription(
									eventManagerDescription,
									EventManagerPort.class.getName());
					//if EventManager was found, save it with associated service ID
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
