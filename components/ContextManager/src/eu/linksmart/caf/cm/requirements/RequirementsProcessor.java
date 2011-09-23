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
package eu.linksmart.caf.cm.requirements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.CafConstants;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.engine.event.EventKeyMeta;
import eu.linksmart.caf.cm.engine.event.EventMeta;
import eu.linksmart.caf.cm.impl.util.XmlUtils;
import eu.linksmart.caf.cm.util.CmHelper;
import eu.linksmart.caf.daqc.subscription.Subscription;

/**
 * Helper class for processing context requirements, and producing
 * {@link Subscription}s.
 * 
 * @author Michael Crouch
 * 
 */
public final class RequirementsProcessor {

	/** the {@link Logger} */
	private static Logger logger =
			Logger.getLogger(RequirementsProcessor.class);

	/**
	 * Private Constructor
	 */
	private RequirementsProcessor() {
	};

	/**
	 * Parses the root node, returning the underlying {@link Set} of
	 * {@link EventMeta}
	 * 
	 * @param root
	 *            the root {@link Element}
	 * @return the {@link Set} of {@link EventMeta}
	 */
	public static Set<EventMeta> parseEvents(Element root) {
		Set<EventMeta> events = new HashSet<EventMeta>();
		NodeList list = root.getElementsByTagName("event");
		for (int i = 0; i < list.getLength(); i++) {
			Element element = (Element) list.item(i);
			String topic =
					XmlUtils.getElementValueFromTagName(element, "eventTopic");
			EventMeta event = new EventMeta(topic);

			Set<EventKeyMeta> eventKeys = new HashSet<EventKeyMeta>();
			NodeList keys = element.getElementsByTagName("eventKey");
			for (int j = 0; j < keys.getLength(); j++) {
				Element keyElement = (Element) keys.item(j);

				String name =
						XmlUtils.getElementValueFromTagName(keyElement, "name");
				String dataType =
						XmlUtils.getElementValueFromTagName(keyElement,
								"dataType");
				String rsv =
						XmlUtils.getElementValueFromTagName(keyElement,
								"relatedStateVariable");
								
				EventKeyMeta key = new EventKeyMeta(event, name, dataType);
				
				
				key.setRelatedStateVariable(rsv);
				String unit =
						XmlUtils.getElementValueFromTagName(keyElement, "unit");
				if (unit != null && !"".equals(unit)) {
					key.setUnit(unit);
				}
				eventKeys.add(key);
			}
			event.setKeys(eventKeys);
			events.add(event);
		}

		return events;
	}

	/**
	 * Parses the {@link Set} of {@link EventMeta} into a {@link Set} of
	 * {@link Subscription}s
	 * 
	 * @param events
	 *            the {@link Set} of {@link EventMeta}
	 * @return the {@link Set} of {@link Subscription}s
	 */
	public static Set<Subscription> parseEventsToSubscriptions(
			Set<EventMeta> events) {
		Set<Subscription> subs = new HashSet<Subscription>();
		Iterator<EventMeta> it = events.iterator();
		while (it.hasNext()) {
			EventMeta event = it.next();
			// create subscription
			Subscription sub = new Subscription();
			sub.setProtocol(CafConstants.PUSH_PROTOCOL);
			sub.setDataId(event.getTopic());

			Attribute[] attrs =
					{ CmHelper.createAttribute(CafConstants.PUSH_TOPIC, event
							.getTopic()) };
			sub.setAttributes(attrs);
			subs.add(sub);
		}
		return subs;
	}

	/**
	 * Parses the list of Pull Elements into a {@link Set} of
	 * {@link Subscription}s
	 * 
	 * @param pullListNode
	 *            Pull List {@link Element}
	 * @return {@link Set} of {@link Subscription}s
	 */
	public static Set<Subscription> parsePullList(Element pullListNode) {

		Set<Subscription> subs = new HashSet<Subscription>();
		NodeList pullList = pullListNode.getElementsByTagName("pull");
		for (int i = 0; i < pullList.getLength(); i++) {
			Subscription sub = parsePull((Element) pullList.item(i));
			if (sub != null)
				subs.add(sub);
		}
		return subs;
	}

	/**
	 * Parses a pull {@link Element}, into a {@link Subscription}
	 * 
	 * @param pullElement
	 *            the {@link Element}
	 * @return the {@link Subscription}
	 */
	public static Subscription parsePull(Element pullElement) {

		String returnType =
				XmlUtils.getAttributeValue("returnType", pullElement);
		String freq = XmlUtils.getAttributeValue("frequency", pullElement);
		String method = XmlUtils.getAttributeValue("method", pullElement);
		String assignTo =
				XmlUtils.getAttributeValue("assignToMemberKey", pullElement);

		if ("".equals(returnType) || "".equals(freq) || "".equals(method)
				|| "".equals(assignTo)) {
			logger
					.warn("Pull Element content missing. 'returnType', 'frequency', "
							+ "'method' and 'assignToMemberKey' must all be assigned");
			return null;
		}

		Attribute[] attrs =
				{
						CmHelper.createAttribute(CafConstants.PULL_METHOD,
								method),
						CmHelper.createAttribute(CafConstants.PULL_FREQUENCY,
								freq),
						CmHelper.createAttribute(CafConstants.PULL_RETURNTYPE,
								returnType) };

		Subscription sub = new Subscription();
		sub.setAttributes(attrs);
		sub.setProtocol(CafConstants.PULL_PROTOCOL);
		sub.setDataId(assignTo);

		List<Parameter> paramList = new ArrayList<Parameter>();
		NodeList params = pullElement.getElementsByTagName("argument");
		for (int i = 0; i < params.getLength(); i++) {
			Parameter param = parseArgument((Element) params.item(i));
			if (param == null)
				return null;
			paramList.add(param);
		}
		if (paramList.size() > 0) {
			Parameter[] paramArray =
					(Parameter[]) paramList.toArray(new Parameter[paramList
							.size()]);
			sub.setParameters(paramArray);
		} else {
			sub.setParameters(new Parameter[0]);
		}

		return sub;
	}

	/**
	 * Parses the argument element, into a {@link Parameter}
	 * 
	 * @param argElement
	 *            the argument {@link Element}
	 * @return the {@link Parameter}
	 */
	public static Parameter parseArgument(Element argElement) {

		String paramName = XmlUtils.getAttributeValue("name", argElement);
		String paramType = XmlUtils.getAttributeValue("type", argElement);
		String paramValue = argElement.getTextContent();

		if ("".equals(paramName) || "".equals(paramType)) {
			logger.warn("Argument Element content missing. 'name' and 'type'"
					+ " must be assigned");
			return null;
		}

		Parameter param = new Parameter();
		param.setName(paramName);
		param.setType(paramType);
		param.setValue(paramValue);
		return param;
	}
}
