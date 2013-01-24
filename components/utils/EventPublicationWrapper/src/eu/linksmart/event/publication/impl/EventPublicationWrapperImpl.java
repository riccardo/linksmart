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
	
	private class EventManager {
		public EventManagerPort eventManagerPort = null;
		public String eventManagerPID = null;
		
		public EventManager(EventManagerPort eventManagerPort, String eventManagerPID) {
			this.eventManagerPort = eventManagerPort;
			this.eventManagerPID = eventManagerPID;
		}
	}
	
	//Map<ServiceID,EventManager>
	private Map<String, EventManager> eventManagers;
	
	protected void activate(ComponentContext context) {
		LOG.info("Starting "
				+ context.getBundleContext().getBundle().getSymbolicName());
		
		// Get the LinkSmartRemoteServiceStore
		remoteServiceStore = (LinkSmartRemoteServiceStore) context
				.locateService(LinkSmartRemoteServiceStore.class
						.getSimpleName());
				
		eventManagers = new HashMap<String, EventManager>();
		
		LOG.info("Started "
				+ context.getBundleContext().getBundle().getSymbolicName());
	}
	
	protected void deactivate(ComponentContext context) {
		LOG.info("Stopping "
				+ context.getBundleContext().getBundle().getSymbolicName());
		
		if (locatorThread != null){
			locatorThread.end();
		}
		
		LOG.info("Stopped "
				+ context.getBundleContext().getBundle().getSymbolicName());
	}

	@Override
	public boolean publishEvent(String serviceID, String topic, Part[] valueParts)
			throws RemoteException {
		if (!isEventManagerLocated(serviceID)) {
			LOG.warn("Unable to publish Sensor Reading. Event Manager not found: Topic=" + topic);
			return false;
		}
		
		//Add timestamp
		Part timestamp = new Part("timestamp", sdf.format(new Date()));
		Part[] timestampedParts = new Part[valueParts.length+1];
		System.arraycopy(valueParts, 0, timestampedParts, 0, valueParts.length);
		timestampedParts[timestampedParts.length-1] = timestamp;
		
		LOG.debug("Trying to publish sensor event: Topic=" + topic);
		
		try {
			getEventManager(serviceID).eventManagerPort.publish(topic, timestampedParts);
			return true;
		}
		catch (RemoteException ex) {
			//if Event Manager disappears, start new search for that Event Manager
			String eventManagerPID = eventManagers.get(serviceID).eventManagerPID;
			eventManagers.remove(serviceID);
			findEventManager(serviceID, eventManagerPID);
			throw ex;
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
	
	protected EventManager getEventManager(String serviceID) {
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
					EventManagerPort eventManagerPort = (EventManagerPort) remoteServiceStore
							.getRemoteHydraServiceByDescription(
									eventManagerDescription,
									EventManagerPort.class.getName());
					if (eventManagerPort != null) {
						//if EventManager was found, save it with associated service ID
						EventManager eventManager = new EventManager(eventManagerPort, eventManagerDescription);
						eventManagers.put(serviceID, eventManager);
						shouldStop = true;
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
}
