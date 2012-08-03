package eu.linksmart.event.example.publisher.impl;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.event.example.publisher.EventPublisherExample;
import eu.linksmart.event.publication.EventPublicationWrapper;
import eu.linksmart.eventmanager.Part;

/**
 * Basic example of publishing an event
 * 
 * @author jahnadmin
 * 
 */
public class EventPublisherExampleImpl implements
		EventPublisherExample {

		/* Description of the EventManager */
	private static final String EVENT_MANAGER_PID = "EventManager:Marc";
	/* Topic of the events */
	private static final String SENSOR_EVENT_TOPIC = "EVENT/POST_PROCESSED/WEATHER_FORECAST/PASSEIG_DE_GRACIA/LINE_3/2_HOUR_AHEAD";

	/* Thread that handles event creation */
	private PublisherThread publisherThread;
	private Logger LOG = Logger.getLogger(EventPublisherExampleImpl.class);
	private EventPublicationWrapper eventPublicationWrapper;

	private static final String SERVICE_ID = EventPublisherExample.class
			.getSimpleName();

	protected void activate(ComponentContext context) {
		LOG.info("Starting "
				+ context.getBundleContext().getBundle().getSymbolicName());

		eventPublicationWrapper = (EventPublicationWrapper) context.locateService(EventPublicationWrapper.class.getSimpleName());
		eventPublicationWrapper.findEventManager(SERVICE_ID, EVENT_MANAGER_PID);
		
		// Start the Thread that publishes events
		publisherThread = new PublisherThread();
		Thread eventGenerator = new Thread(publisherThread);
		eventGenerator.start();
	}

	protected void deactivate(ComponentContext context) {
		if (publisherThread != null) {
			publisherThread.end();
		}

		LOG.debug("Stopped "
				+ context.getBundleContext().getBundle().getSymbolicName());
	}

	/**
	 * Generate event every few seconds
	 */
	private class PublisherThread implements Runnable {
		private boolean stopped = false;

		public synchronized void end() {
			this.stopped = true;
		}

		@Override
		public void run() {
			while (!stopped) {

				publish();

				try {
					LOG.debug("Waiting for 5 seconds.");
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					LOG.info("Interrupt Thread");
					break;
				}
			}
		}

		private void publish() {
			if (eventPublicationWrapper.isEventManagerLocated(SERVICE_ID)) {
				// Create Event
				Part[] weatherForecastEvent = new Part[7];
				weatherForecastEvent[0] = new Part("temperature",Float.toString(26.3f));
				weatherForecastEvent[1] = new Part("relativeHumidity",Float.toString(45.3f));
				weatherForecastEvent[2] = new Part("windSpeed",Float.toString(1.1f));
				weatherForecastEvent[3] = new Part("windDirection",Float.toString(270.0f));
				weatherForecastEvent[4] = new Part("windDirectionString","West");
				weatherForecastEvent[5] = new Part("skyCondition", "sunny");
				weatherForecastEvent[5] = new Part("icon","http://iconserver/sunny.gif");

				// Publish Event
				try {
					boolean result = eventPublicationWrapper.publishEvent(SERVICE_ID ,SENSOR_EVENT_TOPIC,
							weatherForecastEvent);
					
					if (result) {
						LOG.debug("Event published successfully");
					} else {
						LOG.debug("Unable to publish event.");
					}
				} catch (RemoteException e) {
					LOG.error(
							"Unable to publish sensor reading via Event Manager",
							e);
				}
			}
		}
	}
}
