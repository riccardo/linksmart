package eu.linksmart.event.example.subscriber.impl;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.event.example.subscriber.EventSubscriberExample;
import eu.linksmart.event.subscription.EventSubscriptionWrapper;
import eu.linksmart.eventmanager.EventSubscriber;
import eu.linksmart.eventmanager.Part;

public class EventSubscriberExampleImpl implements EventSubscriberExample, 
	EventSubscriber {

	private Logger LOG = Logger.getLogger(EventSubscriberExampleImpl.class);

	private static final String SERVICE_ID = "EventSubscriber:ExampleSubscriber";

	private static final String EVENT_MANAGER_PID = "EventManager:FIT:Training";
	private static final String WEATHER_EVENT_TOPIC = "EVENT/POST_PROCESSED/WEATHER_FORECAST/PASSEIG_DE_GRACIA/LINE_3/2_HOUR_AHEAD";
	private static final String CO2_EVENT_TOPIC = "EVENT/.*";

	private EventSubscriptionWrapper eventSubscriptionWrapper;

	protected void activate(ComponentContext context) {
		LOG.debug("Starting "
				+ context.getBundleContext().getBundle().getSymbolicName());
		
		//Subscribe to Event Manager with topic Topic
		eventSubscriptionWrapper = (EventSubscriptionWrapper) context
				.locateService(EventSubscriptionWrapper.class.getSimpleName());
		eventSubscriptionWrapper.registerCallback(this, SERVICE_ID);
		eventSubscriptionWrapper.findEventManager(SERVICE_ID, EVENT_MANAGER_PID);
		eventSubscriptionWrapper.subscribeWithTopic(SERVICE_ID, WEATHER_EVENT_TOPIC);
		eventSubscriptionWrapper.subscribeWithTopic(SERVICE_ID, CO2_EVENT_TOPIC);

		LOG.debug("Started "
				+ context.getBundleContext().getBundle().getSymbolicName());
	}

	protected void deactivate(ComponentContext context) {
		eventSubscriptionWrapper.deregisterCallback(SERVICE_ID);
		LOG.debug("Stopped "
				+ context.getBundleContext().getBundle().getSymbolicName());
	}

	@Override
	public Boolean notify(String topic, Part[] parts) throws RemoteException {
		LOG.info("Received event: " + topic + ": " + parts[0].getValue() + "; "
				+ parts[1].getValue());
		return true;
	}

	@Override
	public Boolean notifyXmlEvent(String xmlEventString) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}
