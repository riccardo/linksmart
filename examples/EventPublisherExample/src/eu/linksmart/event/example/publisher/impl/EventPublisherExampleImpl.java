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
	private static final String EVENT_MANAGER_PID = "EventManager:CHANGEME";
	/* Topic of the events */
	private static final String WEATHER_EVENT_TOPIC = "MEASUREMENT/POST_PROCESSED/WEATHER_FORECAST/PASSEIG_DE_GRACIA/LINE_3/2_HOUR_AHEAD";
	private static final String CO2_EVENT_TOPIC = "EVENT/CO2";

	/* Thread that handles event creation */
	private PublisherThread publisherThread;
	private Logger LOG = Logger.getLogger(EventPublisherExampleImpl.class);
	private EventPublicationWrapper eventPublicationWrapper;

	private static final String SERVICE_ID = "EventPublisher:ExamplePublisher";

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
				// Create Events
				Part[] weatherForecastEvent = new Part[7];
				weatherForecastEvent[0] = new Part("temperature",Float.toString(26.3f));
				weatherForecastEvent[1] = new Part("relativeHumidity",Float.toString(45.3f));
				weatherForecastEvent[2] = new Part("windSpeed",Float.toString(1.1f));
				weatherForecastEvent[3] = new Part("windDirection",Float.toString(270.0f));
				weatherForecastEvent[4] = new Part("windDirectionString","West");
				weatherForecastEvent[5] = new Part("skyCondition", "sunny");
				weatherForecastEvent[6] = new Part("icon","http://iconserver/sunny.gif");
				Part[] co2Event = new Part[1];
				co2Event[0] = new Part("concentration",Float.toString(14.5f));

				// Publish Events
				try {
					boolean result = eventPublicationWrapper.publishEvent(SERVICE_ID ,WEATHER_EVENT_TOPIC,
							weatherForecastEvent);
					boolean result2 = eventPublicationWrapper.publishEvent(SERVICE_ID ,CO2_EVENT_TOPIC, co2Event);
					if (result && result2) {
						LOG.debug("Events published successfully");
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
