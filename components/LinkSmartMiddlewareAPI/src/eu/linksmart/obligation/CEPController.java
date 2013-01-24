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
package eu.linksmart.obligation;

import java.util.TreeMap;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;




public interface CEPController {

	/**
	 * Returns the total number of received events.
	 * 
	 * @return
	 */
	public long getReceivedEvents();

	/**
	 * Starts listening to an event pattern.
	 * 
	 * @param listener
	 */
	public void activateEventPattern(EventListener listener);

	/**
	 * Deactivates an event pattern.
	 * 
	 * @param listener
	 */
	public void deactivateEventListener(EventListener listener);

	/**
	 * Saves all event listeners to the file Events.sv.
	 */
	public void saveToFile();

	/**
	 * Loads all listeners from the file Events.sv.
	 * <p>
	 * The event listeners are loaded but not activated yet.
	 */
	public void loadFromFile();

	/**
	 * Activates a situation listener.
	 * 
	 * @param situation
	 */
	public void activateSituationListener(Situation situation);

	/**
	 * Deactivates a situation listener.
	 * 
	 * @param situation
	 */
	public void deactivateSituationListener(String situationName);

	/**
	 * Activates an event-condition-action (ECA) policy.
	 * 
	 * @param policy
	 */
	public void activatePolicy(Policy policy);

	/**
	 * Stops listening to a policy's trigger event and removes the policy from the "registered policy" list.
	 * 
	 * @param policy
	 */
	public void deactivatePolicy(Policy policy);

	/**
	 * Please note that this is the "low level" rule assembly API.
	 */
	public void loadRulesFromFile() throws Exception;

	public void addEventListener(String name, EventListener eventListener);

	public void removeEventListener(String eventListenerName);

	/**
	 * Returns a list of all actions available.
	 * 
	 * @return
	 */
	public Vector<String> getAvailableActions();

	public Vector<String> getAvailableChannels();

	public abstract TreeMap<String, EventListener> getRegisteredListeners();

	public abstract DefaultMutableTreeNode buildEventTree(final String parentClass, DefaultMutableTreeNode parentNode);
	
	public TreeMap<String, Situation> getSituations();
	
}