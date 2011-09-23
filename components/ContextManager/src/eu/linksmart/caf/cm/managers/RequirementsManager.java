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
 * Copyright (C) 2006-2010 University of Reading,
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.linksmart.caf.cm.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.CafConstants;
import eu.linksmart.caf.cm.ContextManagerError;
import eu.linksmart.caf.cm.engine.contexts.BaseContext;
import eu.linksmart.caf.cm.engine.contexts.Device;
import eu.linksmart.caf.cm.engine.contexts.MemberedContext;
import eu.linksmart.caf.cm.engine.event.BaseEvent;
import eu.linksmart.caf.cm.engine.event.EventInstance;
import eu.linksmart.caf.cm.engine.members.ContextMember;
import eu.linksmart.caf.cm.engine.members.EventMember;
import eu.linksmart.caf.cm.exceptions.ContextManagerException;
import eu.linksmart.caf.cm.exceptions.ErrorListException;
import eu.linksmart.caf.cm.impl.CmConfigurator;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.impl.ContextManagerApplication;
import eu.linksmart.caf.cm.impl.util.XmlUtils;
import eu.linksmart.caf.cm.requirements.DeviceRequirement;
import eu.linksmart.caf.daqc.DataAcquisitionComponent;
import eu.linksmart.caf.daqc.report.Data;
import eu.linksmart.caf.daqc.report.DataReport;
import eu.linksmart.caf.daqc.subscription.DaqcSubscription;
import eu.linksmart.caf.daqc.subscription.DaqcSubscriptionResponse;
import eu.linksmart.caf.daqc.subscription.Result;
import eu.linksmart.caf.daqc.subscription.Subscriber;
import eu.linksmart.caf.daqc.subscription.Subscription;

/**
 * Ensures that the data / event subscription requirements of a context are
 * fulfilled, providing functionalities for created {@link DaqcSubscription}s,
 * and performing the interaction with the {@link DataAcquisitionComponent}.<p>
 * 
 * Stores active {@link Subscription}s, mapped against the contextId
 * 
 * @author Michael Crouch
 */
public class RequirementsManager extends CmInternalManager {

	/** The Id for this Internal Manager */
	public static final String MANAGER_ID =
			"eu.linksmart.caf.cm.RequirementsManager";

	/** the {@link Logger} */
	private static final Logger logger =
			Logger.getLogger(RequirementsManager.class);

	/** Location of the default context requirements xml */
	private static final String REQUIREMENTS_LOC =
			"resources/context-requirements.xml";

	/** the separator used to split subscriptionIds */
	private static final String SEPARATOR = "||";

	/** the {@link ContextManagerApplication} */
	private ContextManagerApplication cmApp;

	/** the {@link RuleEngine} */
	private RuleEngine ruleEngine;
	
	/** the {@link ContextUpdateManager} */
	private ContextUpdateManager updateManager;

	/**
	 * {@link Map} of {@link Set} of active {@link Subscription}s, mapped by the
	 * contextId
	 */
	private final Map<String, Set<Subscription>> activeSubscriptions;

	/** {@link Set} of the {@link DeviceRequirement}s */
	private final Set<DeviceRequirement> deviceRequirements;

	/**
	 * Constructor
	 */
	public RequirementsManager() {
		deviceRequirements = new HashSet<DeviceRequirement>();
		activeSubscriptions = new HashMap<String, Set<Subscription>>();
	}

	@Override
	public String getManagerId() {
		return MANAGER_ID;
	}

	@Override
	public void initialise(CmManagerHub hub) {
		this.cmApp = hub.getCmApp();
		this.ruleEngine = (RuleEngine) hub.getManager(RuleEngine.MANAGER_ID);
		this.updateManager = (ContextUpdateManager) hub.getManager(ContextUpdateManager.MANAGER_ID);
		initRequirementConfig();
	}
	
	@Override
	public void completedInit() {
		//Do nothing		
	}

	@Override
	public void shutdown() {
		// None
	}

	/**
	 * Initialises the requirement configuration
	 */
	private void initRequirementConfig() {
		/**
		 * if it doesn't exist, create folder and copy base device requirements
		 * xml file
		 */
		String filename =
				cmApp.getConfigurator().get(CmConfigurator.CONTEXT_REQ_LOC);
		File cmReqFile =
				new File(ContextManagerApplication.CM_FOLDER + "/" + filename);
		if (cmReqFile.exists()) {
			// load file
			loadDeviceRequirements(cmReqFile);
		} else {
			logger.info("Context Requirements does not exist at '"
					+ cmReqFile.getPath() + "'");
			logger.info("Copying default requirement configuration to "
					+ filename);
			ComponentContext context = cmApp.getComponentContext();
			URL url =
					context.getBundleContext().getBundle().getResource(
							REQUIREMENTS_LOC);

			try {
				InputStream in = url.openStream();
				OutputStream out = new FileOutputStream(cmReqFile);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				// now load default
				loadDeviceRequirements(cmReqFile);
			} catch (Exception e) {
				logger.error("Error copying default context requirement "
						+ "configuration. " + e.getMessage(), e);

			}
		}
	}

	/**
	 * Loads the {@link DeviceRequirement}s from the give XML configuration
	 * {@link File}
	 * 
	 * @param cmReqFile
	 *            the {@link File}
	 */
	private void loadDeviceRequirements(File cmReqFile) {
		try {
			deviceRequirements.clear();
			Document doc =
					XmlUtils.getInputStreamAsDocument(new FileInputStream(
							cmReqFile));
			NodeList list =
					doc.getDocumentElement()
							.getElementsByTagName("requirement");
			for (int i = 0; i < list.getLength(); i++) {
				DeviceRequirement req =
						new DeviceRequirement((Element) list.item(i));
				if (req.isValid()) {
					deviceRequirements.add(req);
				}
			}
			logger.info("Device context requirements loaded");
		} catch (Exception e) {
			logger.error("Error processing context requirements at "
					+ cmReqFile.getPath() + ": " + e.getMessage(), e);
			return;
		}
	}

	/**
	 * Creates the {@link DaqcSubscription} wrapping the array of
	 * {@link Subscription}s.
	 * 
	 * @param subs
	 *            the array of {@link Subscription}
	 * @param doNormalise
	 *            whether to normalise the {@link Subscription} dataIds
	 * @param contextId
	 *            the contextId to normalise with
	 * @return the {@link DaqcSubscription}
	 */
	public DaqcSubscription getDaqcSubscription(Subscription[] subs,
			boolean doNormalise, String contextId) {
		DaqcSubscription daqcSub = new DaqcSubscription();
		Subscriber subscriber = new Subscriber();
		subscriber.setSubscriberPid(cmApp.getPid());
		subscriber.setSubscriberHid(cmApp.getHid());

		if (doNormalise) {
			normaliseSubscriptions(contextId, subs);
		}

		daqcSub.setSubscriber(subscriber);
		daqcSub.setSubscriptions(subs);

		return daqcSub;
	}

	/**
	 * Creates the {@link DaqcSubscription} using the {@link Set} of
	 * {@link Subscription}s
	 * 
	 * @param subs
	 *            the {@link Set} of {@link Subscription}
	 * @param doNormalise
	 *            whether to normalise the {@link Subscription} dataIds
	 * @param contextId
	 *            the contextId to normalise with
	 * @return the {@link DaqcSubscription}
	 */
	public DaqcSubscription getDaqcSubscription(Set<Subscription> subs,
			boolean doNormalise, String contextId) {
		Subscription[] subArray =
				(Subscription[]) subs.toArray(new Subscription[subs.size()]);
		return getDaqcSubscription(subArray, doNormalise, contextId);
	}

	/**
	 * Sends the {@link DaqcSubscription} to the
	 * {@link DataAcquisitionComponent}, and records the status of the
	 * subscription
	 * 
	 * @param contextId
	 *            the contextId
	 * @param daqcSub
	 *            the {@link DaqcSubscription}
	 * @throws ContextManagerException
	 *             any errors...
	 */
	public void processSubscription(String contextId, DaqcSubscription daqcSub)
			throws ContextManagerException {
		try {
			if (daqcSub == null)
				return;

			DataAcquisitionComponent daqc = cmApp.getDataAcquisitionComponent();
			DaqcSubscriptionResponse response = daqc.subscribe(daqcSub);

			// record context subscriptions
			activeSubscriptions.put(contextId, new HashSet<Subscription>());

			for (Subscription sub : daqcSub.getSubscriptions()) {
				activeSubscriptions.get(contextId).add(sub);
			}

			StringBuffer buffer = new StringBuffer();
			boolean hasError = false;
			for (Result result : response.getResults()) {
				if (!result.getMsg().equals("OK")) {
					hasError = true;
					buffer.append(result.getId()).append(" : ").append(
							result.getMsg()).append("\n");
				}
			}
			if (hasError)
				throw new ContextManagerException(contextId,
						"Error with Subscripitions to Daqc", buffer.toString());
		} catch (ServiceException e) {
			throw new ContextManagerException("",
					"Error creating Web Service call to Daqc", e
							.getLocalizedMessage());
		} catch (Exception e) {
			throw new ContextManagerException("", "Error calling Daqc", e
					.getLocalizedMessage());
		}
	}

	/**
	 * Cancels all {@link Subscription}s for the given contextId
	 * 
	 * @param contextId
	 *            the contextId
	 * @param remove
	 *            boolean stating whether to remove the stored
	 *            {@link Subscription}s
	 */
	public void cancelSubscriptions(String contextId, boolean remove) {
		String hid = cmApp.getHid();
		Set<Subscription> subIds = activeSubscriptions.get(contextId);
		cancelSubscriptions(hid, subIds);
		if (remove)
			activeSubscriptions.remove(contextId);
	}

	/**
	 * Cancels the {@link Set} of {@link Subscription}s, for the given hid
	 * 
	 * @param hid
	 *            the subscriber hid
	 * @param subs
	 *            the {@link Set} of {@link Subscription}s
	 */
	private void cancelSubscriptions(String hid, Set<Subscription> subs) {
		DataAcquisitionComponent daqc;
		try {
			daqc = cmApp.getDataAcquisitionComponent();

			if (subs == null)
				return;

			Iterator<Subscription> it = subs.iterator();
			while (it.hasNext()) {
				Subscription sub = it.next();
				try {
					daqc.cancelSubscription(sub.getProtocol(), hid, sub
							.getDataId());
				} catch (Exception e) {
					logger.error("Error cancelling Subscription ["
							+ sub.getProtocol() + " | " + hid + " | "
							+ sub.getDataId() + "] : " + e.getMessage(), e);
				}
			}
		} catch (Exception e1) {
			logger.error("Error getting DataAcquisitionComponent: "
					+ e1.getMessage(), e1);
		}
	}

	/**
	 * Cancels all {@link Subscription}s to the Daqc
	 * 
	 * @param remove
	 *            boolean stating whether to remove the stored
	 *            {@link Subscription}s
	 */
	public void cancelAllSubscriptions(boolean remove) {
		Iterator<String> it = activeSubscriptions.keySet().iterator();
		while (it.hasNext()) {
			cancelSubscriptions(it.next(), remove);
		}
	}

	/**
	 * Re-subscribes for data for all stored {@link Subscription}s
	 * 
	 * @throws ErrorListException
	 * @throws ContextManagerException
	 */
	public void renewAllSubscriptions() throws ErrorListException,
			ContextManagerException {

		ErrorListException errors = new ErrorListException();
		Iterator<String> it = activeSubscriptions.keySet().iterator();
		while (it.hasNext()) {
			String contextId = it.next();
			try {
				DaqcSubscription daqcSub =
						getDaqcSubscription(activeSubscriptions.get(contextId),
								false, null);
				if (daqcSub != null) {
					this.processSubscription(contextId, daqcSub);
				}
			} catch (ContextManagerException e) {
				errors.addError(new ContextManagerError(e.getMessage(), e
						.getContextId(), e.getAction()));
			}
		}

		if (errors.getErrorList().size() > 0)
			throw errors;
	}

	/**
	 * Gets the Pull {@link Subscription}s for the {@link Device} with the SCPD
	 * description {@link Document}. All {@link Subscription} ids returned only
	 * specify the StateVariable in question, and not the source contextId.
	 * 
	 * @param device
	 *            the {@link Device}
	 * @param scpd
	 *            the device description SCPD as {@link Document}
	 * @return a {@link Set} of {@link Subscription}
	 */
	public Set<Subscription> getDeviceRequirements(Device device, Document scpd) {
		Set<Subscription> subs = new HashSet<Subscription>();

		Iterator<DeviceRequirement> it = deviceRequirements.iterator();
		while (it.hasNext()) {
			DeviceRequirement req = it.next();
			if (req.match(scpd.getDocumentElement())) {
				Set<Subscription> toAdd =
						req.getAdditionalSubscriptions(device, scpd
								.getDocumentElement());
				subs.addAll(toAdd);
			}
		}

		return subs;
	}

	/**
	 * Updates all
	 * 
	 * @return success
	 */
	public boolean updateAll() {
		return false;
	}

	/**
	 * Normalises the {@link Subscription}s.<p> Involves adding the contextId to
	 * each {@link Subscription} dataId, with a separator.<p> For example:<p>
	 * <code>[contextId][separator][sub-dataId]</code>
	 * 
	 * @param contextId
	 *            the contextId
	 * @param subs
	 *            the array of {@link Subscription}s
	 */
	public void normaliseSubscriptions(String contextId, Subscription[] subs) {
		for (Subscription sub : subs) {
			String prefix = contextId + SEPARATOR;
			if (!sub.getDataId().startsWith(prefix)) {
				String newId = prefix + sub.getDataId();
				sub.setDataId(newId);
			}
		}
	}

	/**
	 * Handles reported data, as a {@link DataReport} inserting it into the
	 * {@link RuleEngine}, after determining the data protocol
	 * 
	 * @param dataReport
	 *            the {@link DataReport}
	 * @return success
	 */
	public boolean handleReportedData(DataReport dataReport) {
		for (Data data : dataReport.getReportedData()) {
			if (data.getProtocol().equals("daqc:protocol:pull")) {
				// handle pull data
				for (String alias : dataReport.getAliases()) {
					String[] aliasArray = alias.split(SEPARATOR);
					if (aliasArray.length == 2) {
						if (data.getAttributes().length != 0) {
							String value = data.getAttributes()[0].getValue();
							doPullUpdate(aliasArray[0], aliasArray[1], value);
						}
					} else {
						StringBuffer buffer = new StringBuffer();
						buffer
								.append("Pull data received but dataId (")
								.append(alias)
								.append(
										") is not in correct format. Should be <contextId>")
								.append(SEPARATOR).append("<memberId>.");
						logger.error(buffer);
					}
				}
			} else if (data.getProtocol().equals("daqc:protocol:push")) {

				doPushUpdate(dataReport.getAliases(), data);

			} else {
				// unrecognised protocol
				logger.warn("Data reported with unrecognised protocol: "
						+ data.getProtocol());
			}
		}

		// Working Memory updated, so fire all rules
		ruleEngine.fireAllRules();
		return true;
	}

	/**
	 * Does the Pull update by retrieving the {@link BaseContext} with the given
	 * contextId, and updates the value of its member (with the key give), with
	 * data
	 * 
	 * @param contextId
	 *            the contextId
	 * @param memberId
	 *            the memberId
	 * @param data
	 *            the updated data
	 */
	private void doPullUpdate(String contextId, String memberId, String data) {
		BaseContext base =
				ruleEngine.getContextByContextId(BaseContext.class, contextId);
		if (base == null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Error handling reported Pull data: Context '")
					.append(contextId).append(" does not exist");
			logger.error(buffer.toString());
		} else {
			if (base instanceof MemberedContext) {
				MemberedContext mCtx = (MemberedContext) base;
				ContextMember member = mCtx.getMember(memberId);

				if (member == null) {
					StringBuffer buffer = new StringBuffer();
					buffer.append("Error updating with Pull data: ").append(
							mCtx.getClass().getSimpleName()).append(": ")
							.append(contextId).append(
									" has no member with key '").append(
									memberId).append("'");
					logger.error(buffer.toString());
					return;
				} else {
					member.setStrValue(data);
					ruleEngine.update(member);
					ruleEngine.update(mCtx);
				}
			} else {
				StringBuffer buffer = new StringBuffer();
				buffer.append("Error updating with Pull data: Context '")
						.append(contextId).append("' is not a ").append(
								MemberedContext.class.getSimpleName());
				logger.error(buffer.toString());
			}
		}

	}

	/**
	 * Does the Push update by creating the {@link EventInstance}, with the
	 * source contextId, and inserting it into the {@link RuleEngine}, updating
	 * the source context
	 * 
	 * @param aliases
	 *            the event aliases
	 * @param data
	 *            the {@link Data}
	 */
	private void doPushUpdate(String[] aliases, Data data) {
		
		String hid = getAttributeValue("HID", data.getAttributes());
		String topic =
				getAttributeValue(CafConstants.PUSH_TOPIC, data.getAttributes());

		Set<BaseContext> sourceCtxSet = new HashSet<BaseContext>();
		if (hid != null && !"".equals(hid)) {
			BaseContext sourceCtx =
					ruleEngine.getContextByHid(BaseContext.class, hid);
			if (sourceCtx != null) {
				sourceCtxSet.add(sourceCtx);
			}
		}

		if (sourceCtxSet.isEmpty()) {
			for (String alias : aliases) {
				String[] aliasArray = alias.split(SEPARATOR);

				BaseContext sourceCtx =
						ruleEngine.getContextByContextId(BaseContext.class,
								aliasArray[0]);
				if (sourceCtx != null) {
					sourceCtxSet.add(sourceCtx);
				}
			}
		}

		if (sourceCtxSet.isEmpty()) {
			EventInstance event = new EventInstance(topic, "");
			event.setMembers(getEventMembers(event, data));
			ruleEngine.insert(event);
			StringBuffer buffer = new StringBuffer("Event with topic '");
			buffer
					.append(topic)
					.append(
							"' added to the RuleEngine, but no source context could be found");
			logger.warn(buffer.toString());
		} else {
			
			Iterator<BaseContext> it = sourceCtxSet.iterator();
			while (it.hasNext()) {
				BaseContext src = it.next();
				EventInstance event = new EventInstance(topic, src);
				event.setMembers(getEventMembers(event, data));
				
				if (src instanceof Device) {
					//update device members
					Device dev = (Device) src;
					updateManager.updateDeviceFromEvent(event, dev);
				}
				
				ruleEngine.insert(event);
				ruleEngine.update(src);
			}
		}
		ruleEngine.fireAllRules();
	}

	/**
	 * Extracts the values of the {@link Attribute} with the given id, from the
	 * array of {@link Attribute}
	 * 
	 * @param attrId
	 *            the attribute id
	 * @param attrs
	 *            the array of {@link Attribute}
	 * @return the attribute value
	 */
	private String getAttributeValue(String attrId, Attribute[] attrs) {
		for (Attribute attr : attrs) {
			if (attr.getId().equalsIgnoreCase(attrId))
				return attr.getValue();
		}
		return "";
	}

	/**
	 * Generates the {@link Set} of {@link EventMember}s, from the supplied
	 * {@link Data}
	 * 
	 * @param event
	 *            the {@link BaseEvent}
	 * @param data
	 *            the {@link Data} from the {@link DataReport}
	 * @return the {@link Set} of {@link EventMember}s
	 */
	private Set<EventMember> getEventMembers(BaseEvent event, Data data) {
		Set<EventMember> members = new HashSet<EventMember>();
		for (Attribute attr : data.getAttributes()) {
			if (!attr.getId().equals("Event.Topic")) {
				EventMember member =
						new EventMember(event, attr.getId(), attr.getValue(),
								"");
				members.add(member);
			}
		}
		return members;
	}

}
