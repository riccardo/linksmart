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

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import eu.linksmart.caf.cm.engine.contexts.Application;
import eu.linksmart.caf.cm.engine.event.EventMeta;
import eu.linksmart.caf.cm.engine.members.ContextMember;
import eu.linksmart.caf.cm.exceptions.ContextManagerException;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.impl.util.XmlUtils;
import eu.linksmart.caf.cm.requirements.RequirementsProcessor;
import eu.linksmart.caf.cm.specification.ContextSpecification;
import eu.linksmart.caf.cm.specification.Member;
import eu.linksmart.caf.daqc.DataAcquisitionComponent;
import eu.linksmart.caf.daqc.subscription.DaqcSubscription;
import eu.linksmart.caf.daqc.subscription.Subscription;

/**
 * Handles the creation and management of {@link Application} contexts.
 * 
 * @author Michael Crouch
 * 
 */
public class ApplicationContextManager extends CmInternalManager {

	/** The Id for this Internal Manager */
	public static final String MANAGER_ID =
			"eu.linksmart.caf.cm.ApplicationContextManager";

	/** the {@link Logger} */
	private static final Logger logger =
			Logger.getLogger(ApplicationContextManager.class);

	/** the {@link RuleEngine} */
	private RuleEngine ruleEngine;

	/** the {@link RequirementsManager} */
	private RequirementsManager reqManager;

	@Override
	public String getManagerId() {
		return MANAGER_ID;
	}

	@Override
	public void initialise(CmManagerHub hub) {
		this.ruleEngine = (RuleEngine) hub.getManager(RuleEngine.MANAGER_ID);
		this.reqManager =
				(RequirementsManager) hub
						.getManager(RequirementsManager.MANAGER_ID);
	}
	
	@Override
	public void completedInit() {
		//Do nothing		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	/**
	 * Generates and returns the {@link Application} context, to be installed
	 * into the {@link RuleEngine}.
	 * 
	 * @param spec
	 *            the {@link ContextSpecification}
	 * @return the {@link Application}
	 * @throws ContextManagerException
	 */
	public Application processContextSpecification(ContextSpecification spec)
			throws ContextManagerException {

		String ctxId = spec.getDefinition().getName();

		Application appCtx =
				new Application(ctxId, spec.getDefinition().getAuthor(), spec
						.getDefinition().getVersion());
		appCtx.setApplicationUri(spec.getDefinition().getApplicationUri());

		/**
		 * Add Members
		 */
		for (Member mem : spec.getDefinition().getMembers()) {
			ContextMember member =
					new ContextMember(appCtx, mem.getId(), mem
							.getDefaultValue(), mem.getDataType(), mem
							.getInstanceOf());
			member.setInstanceOf(mem.getInstanceOf());
			appCtx.addMember(member);
		}
		return appCtx;
	}

	/**
	 * Process the required Application subscriptions for the given
	 * {@link Application} context, with the given {@link ContextSpecification}
	 * .<p> Returns the {@link DaqcSubscription} to send to the
	 * {@link DataAcquisitionComponent}
	 * 
	 * @param application
	 *            the {@link Application} context
	 * @param specification
	 *            the {@link ContextSpecification}
	 * @return the {@link DaqcSubscription}
	 */
	public DaqcSubscription processApplicationSubscriptions(
			Application application, ContextSpecification specification) {

		// Process Requirements XML
		Document doc = null;
		try {
			doc =
					XmlUtils.getXmlStringAsDocument(specification
							.getRequirementsXml());
		} catch (Exception e) {
			logger.error("Error parsing Application Requirements XML: "
					+ e.getLocalizedMessage(), e);
			return null;
		}

		if (doc != null) {
			NodeList nodeList =
					doc.getDocumentElement()
							.getElementsByTagName("application");
			if (nodeList == null || nodeList.getLength() == 0) {
				logger.info("No context requirements found for "
						+ "Application '" + application.getName() + "'");
				return null;
			}

			if (nodeList.getLength() > 1)
				logger.warn("Multiple <application> nodes found in context "
						+ "requirements. Can only used first.");

			Set<Subscription> subs = new HashSet<Subscription>();

			Element appElement = (Element) nodeList.item(0);
			Set<EventMeta> eventMeta =
					RequirementsProcessor.parseEvents(appElement);
			if (eventMeta.size() > 0) {
				Set<Subscription> eventSubs =
						RequirementsProcessor
								.parseEventsToSubscriptions(eventMeta);
				subs.addAll(eventSubs);
			}

			Set<Subscription> pullSubs =
					RequirementsProcessor.parsePullList(appElement);
			if (pullSubs.size() > 0)
				subs.addAll(pullSubs);

			if (subs.size() > 0)
				return reqManager.getDaqcSubscription(subs, true, application
						.getContextId());
		}
		return null;
	}
}
