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
/**
 * 
 */
package eu.linksmart.caf.cm.event;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;


import eu.linksmart.caf.cm.engine.contexts.BaseContext;
import eu.linksmart.caf.cm.engine.contexts.Device;
import eu.linksmart.caf.cm.engine.members.ContextMember;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.impl.util.NumericHelper;
import eu.linksmart.caf.cm.managers.RuleEngine;
import eu.linksmart.eventmanager.Part;


/**
 * Handler for the <code>deviceStateChanged</code> events from the .NET 
 * <code>DeviceServiceManager</code>. Locates the relavent device service,
 * and updates the given member.
 * 
 * @author Michael Crouch
 *
 */
public class DeviceStateChangedHandler implements IEventHandler {

	/** the {@link Logger} */
	private static final Logger logger =
			Logger.getLogger(DeviceStateChangedHandler.class);

	/** Event topic when device state changed */
	private static final String DEVICE_STATE_CHANGED_TOPIC =
			"deviceStateChanged";
	
	/** Related State Variable Name */
	private static final String VARIABLE_NAME_KEY = "LinkSmartVariable";

	/** Related State Variable Value */
	private static final String VARIABLE_VALUE_KEY = "LinkSmartVariableValue";
	
	/** Device HID key  */
	private static final String HID_KEY = "linksmartidStaticWS";
	
	/** Device URN key  */
	private static final String DEVICE_URN_KEY = "DeviceURN";
	
	
	/**
	 * {@link Set} of event topics handled
	 */
	private static Set<String> handledTopics;

	/** the {@link RuleEngine} */
	private RuleEngine ruleEngine;

	static {
		handledTopics = new HashSet<String>();
		handledTopics.add(DEVICE_STATE_CHANGED_TOPIC);
	}

	@Override
	public Set<String> getHandledTopics() {
		return handledTopics;
	}

	@Override
	public boolean canHandleEvent(String topic) {
		if (handledTopics.contains(topic))
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.caf.cm.event.IEventHandler#handleEvent(java.lang.String, eu.linksmart.eventmanager.Part[])
	 */
	@Override
	public void handleEvent(String topic, Part[] parts) {
		String hid = null;
		String stateVar = null;
		String newValue = null;
		for (Part part : parts){
			if (part.getKey().equals(VARIABLE_NAME_KEY)){
				stateVar = part.getValue();
			}
			else if (part.getKey().equals(VARIABLE_VALUE_KEY)){
				newValue = part.getValue();
			}
			else if (part.getKey().equals(HID_KEY)){
				hid = part.getValue();
			}
		}

		if (hid == null || "".equals(hid)){
			return;
		}


		Device device = (Device) ruleEngine.getContextByHid(Device.class, hid);
		if (device == null){
			logger.error("Could not find Device context with HID " + hid);
			return;
		}

		if (stateVar == null || "".equals(stateVar)){
			logger.error("No State Variable given (" + VARIABLE_NAME_KEY + ")");
			return;
		}
		
		ContextMember member = device.getMember(stateVar);
		if (member == null){
			logger.error("Device with HID " + hid + ", has no member " +
					"with key " + stateVar);
			
			String dataType;
			if (NumericHelper.isNumericValue(newValue)){
				dataType = "double";
			}
			else{
				dataType = "string";
			}

			member = new ContextMember(device, stateVar, newValue, dataType, stateVar);
			device.addMember(member);
			logger.info("Device with HID " + hid + ", member " + 
					stateVar + " created with value " + newValue);						
			ruleEngine.insert(member);
			ruleEngine.update(device);
			//ruleEngine.fireAllRules();
			return;
		}
		

		if (newValue == null || "".equals(newValue)){
			logger.error("No value (" + VARIABLE_VALUE_KEY + ") to set " +
					"State Variable (" + stateVar + ") to.");
			return;
		}

		member.setStrValue(newValue);
		logger.info("Device with HID " + hid + ", member " + 
				stateVar + " updated with value " + newValue);
		ruleEngine.update(member);
		ruleEngine.update(device);
		ruleEngine.fireAllRules();
	}

	@Override
	public void register(CmManagerHub managers) {
		this.ruleEngine =
				(RuleEngine) managers.getManager(RuleEngine.MANAGER_ID);
	}

	@Override
	public void unregistering() {
		// Nothing to do
	}

}
