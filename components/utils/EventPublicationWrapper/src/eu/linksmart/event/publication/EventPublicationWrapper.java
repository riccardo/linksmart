package eu.linksmart.event.publication;

import java.rmi.RemoteException;

import eu.linksmart.eventmanager.Part;

public interface EventPublicationWrapper {
	public static final String DESCRIPTION = "EventPublisher";
	public static final String SID = "EventPublisher";
	/**
	 * Initiates a search for an EventManager
	 * @param serviceID service ID of calling component
	 * @param eventManagerPID name of EventManager to look for
	 */
	public void findEventManager(String serviceID, String eventManagerPID);
	/**
	 * Adds a timestamp and publishes an event via the EventManager that the calling component
	 * has triggered a search before
	 * @param serviceID service ID of calling component
	 * @param topic Topic of event
	 * @param valueParts content of event without timestamp
	 * @return success of publishing
	 * @throws RemoteException EventManager could not be found (>LS1.3: only return is used. throws is only kept for 
	 * compatibility reasons)
	 */
	public boolean publishEvent(String serviceID, String topic, Part[] valueParts) throws RemoteException;
	/**
	 * Adds a timestamp and publishes an event via the EventManager that the calling component
	 * has triggered a search before. If the publication was not successful, the event is queued.
	 * If publication is possible again at a later time, the queued event is published then.
	 * The wrapper can queue up to 8192 events. If more events have to be queued, the oldest ones
	 * are discarded.
	 * @param serviceID service ID of calling component
	 * @param topic Topic of event
	 * @param valueParts content of event without timestamp
	 * @return true if publication was successful at first try
	 */
	public boolean publishEventBuffered(String serviceID, String topic, Part[] valueParts);
	/**
	 * Indicates if a particular EventManager was found
	 * @param serviceID service ID of calling component
	 * @return true if the EventManager which was triggered to be looked for by a former call of
	 * findEventManager(String serviceID, String eventManagerPID) using serviceID was found
	 * false otherwise
	 */
	public boolean isEventManagerLocated(String serviceID);
}
