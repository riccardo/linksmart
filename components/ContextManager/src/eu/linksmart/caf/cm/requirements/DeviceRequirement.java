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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.CafConstants;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.engine.contexts.Device;
import eu.linksmart.caf.cm.impl.util.XmlUtils;
import eu.linksmart.caf.cm.util.CmHelper;
import eu.linksmart.caf.daqc.subscription.Subscription;

/**
 * 
 * Runtime representation of a Device Requirement as specified in the Context
 * Requirements configuration file. As new devices are discovered, this decides
 * whether PULL subscriptions are needed.
 * 
 * @author Michael Crouch
 * 
 */
public class DeviceRequirement {

	/** the {@link Logger} */
	private static final Logger logger =
			Logger.getLogger(DeviceRequirement.class);

	/** the {@link XPathExpressionException} to get the matching text */
	private XPathExpression deviceMatchXPath;

	/** regular expression to match against */
	private String regExMatch;

	/**
	 * {@link Map} of the required state variables, and their frequencies (in
	 * ms)
	 */
	private Map<String, String> stateVariables;

	/** flag denoting whether the requirement is valid */
	private boolean isValid = true;

	/**
	 * Constructor, passing the requirement element to process
	 * 
	 * @param requirementElement
	 *            the requirement {@link Element}
	 * @throws Exception
	 */
	public DeviceRequirement(Element requirementElement) {
		stateVariables = new HashMap<String, String>();
		this.regExMatch =
				XmlUtils.getElementValueFromTagName(requirementElement,
						"matchTo");
		String deviceXPath =
				XmlUtils.getElementValueFromTagName(requirementElement,
						"deviceXPath");

		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			deviceMatchXPath = xpath.compile(deviceXPath);
		} catch (XPathExpressionException e1) {
			logger.error("Error compiling XPath", e1);
			isValid = false;
			return;
		}

		NodeList list =
				requirementElement.getElementsByTagName("stateVariable");
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			String freq = XmlUtils.getAttributeValue("frequency", node);
			String regex = node.getTextContent();

			if ((freq != null || "".equals(freq))
					&& (regex != null || "".equals(regex))) {
				// Check that freq is an integer
				// If not, and exception will be thrown
				try {
					Integer.valueOf(freq);
				} catch (Exception e) {
					StringBuffer buffer =
							new StringBuffer("Error processing: Frequency (");
					buffer.append(freq).append(") is not numeric.");
					logger.error(buffer.toString(), e);
					isValid = false;
				}

				// add to sv
				stateVariables.put(regex, freq);
			} else {
				logger.error("Error with DeviceRequirements: Frequency=" + freq
						+ ", RegEx=" + regex);
				isValid = false;
			}
		}
	}

	/**
	 * Constructor, passing the xpath for retrieving the matching parameter, and
	 * the Regular Expression to match it against
	 * 
	 * @param deviceXPath
	 *            the XPath query
	 * @param regExMatch
	 *            the Regular Expression
	 * @throws XPathExpressionException
	 * 
	 */
	public DeviceRequirement(String deviceXPath, String regExMatch)
			throws XPathExpressionException {

	}

	/**
	 * Gets the isValid
	 * 
	 * @return the isValid
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * Match the {@link DeviceRequirement} against the deviceXml given
	 * 
	 * @param deviceXml
	 *            the deviceXml as a {@link Node}
	 * @return whether it matches
	 */
	public boolean match(Node deviceXml) {
		String result;
		try {
			result =
					(String) deviceMatchXPath.evaluate(deviceXml,
							XPathConstants.STRING);
			if (result.matches(regExMatch))
				return true;
		} catch (XPathExpressionException e) {
			logger.warn("Error processing XPath query: "
					+ e.getLocalizedMessage());
		}

		return false;
	}

	/**
	 * Parses the XML to set up pull subscriptions for the stateVariables and
	 * units
	 * 
	 * @param device
	 *            the {@link Device}
	 * @param deviceXml
	 *            the device description XML (SPCD) root {@link Element}
	 * @return the {@link Set} of {@link Subscription}s
	 */
	public Set<Subscription> getAdditionalSubscriptions(Device device,
			Element deviceXml) {
		Set<Subscription> subs = new HashSet<Subscription>();

		// first stateVariables
		Iterator<String> it = stateVariables.keySet().iterator();
		while (it.hasNext()) {
			String svMatch = it.next();
			String freq = stateVariables.get(svMatch);
			Iterator<String> toPull =
					getMatchingStateVars(svMatch, device).iterator();
			while (toPull.hasNext()) {
				Subscription sub =
						getStateVariableSub(device, deviceXml, toPull.next(),
								freq);
				if (sub != null)
					subs.add(sub);
			}
		}
		return subs;
	}

	/**
	 * Gets the {@link Set} of State variables that match the regular
	 * expression, from the {@link Device}
	 * 
	 * @param svMatch
	 *            the Regular Expression
	 * @param device
	 *            the {@link Device}
	 * @return the {@link Set} of State variables that match
	 */
	private Set<String> getMatchingStateVars(String svMatch, Device device) {
		Set<String> matching = new HashSet<String>();
		Iterator<String> it = device.getStateVariables().iterator();
		while (it.hasNext()) {
			String sv = it.next();
			if (sv.matches(svMatch))
				matching.add(sv);
		}
		return matching;
	}

	/**
	 * Gets the {@link Subscription} for the given StateVariable, with the given
	 * frequency, from the {@link Device} and deviceXml.<p> Only pulls from
	 * methods that return the given stateVariable, without and input arguments.
	 * 
	 * @param device
	 *            the {@link Device}
	 * @param deviceXml
	 *            the device XML
	 * @param stateVar
	 *            the State Variable to pull
	 * @param freq
	 *            the frequency
	 * @return the {@link Subscription}
	 */
	private Subscription getStateVariableSub(Device device, Node deviceXml,
			String stateVar, String freq) {
		// get all action nodes return the stateVar
		NodeList nodes =
				XmlUtils.getNodesXpath(deviceXml,
						"//direction[text()=\"out\"]/../relatedStateVariable[text()=\""
								+ stateVar + "\"]/../../../..");
		for (int i = 0; i < nodes.getLength(); i++) {
			// check for method with no input args
			Element e = (Element) nodes.item(i);
			NodeList directionNodes = e.getElementsByTagName("direction");
			boolean hasInput = false;
			for (int j = 0; j < directionNodes.getLength(); j++) {
				Node dirNode = directionNodes.item(j);
				if (!dirNode.getTextContent().equals("out"))
					hasInput = true;
			}

			if (!hasInput) {
				String method = XmlUtils.getElementValueFromTagName(e, "name");
				String typeXpath =
						"//stateVariable/name[text()=\"" + stateVar
								+ "\"]/../dataType/text()";
				String returnType =
						XmlUtils.getValueOfXpath(deviceXml, typeXpath);

				Subscription sub = new Subscription();
				sub.setProtocol(CafConstants.PULL_PROTOCOL);
				sub.setDataId(stateVar);
				sub.setParameters(new Parameter[0]);

				Attribute[] attrs =
						{
								CmHelper.createAttribute(
										CafConstants.PULL_METHOD, method),
								CmHelper.createAttribute(
										CafConstants.PULL_RETURNTYPE,
										returnType),
								CmHelper.createAttribute(
										CafConstants.DATASOURCE_HID, device
												.getHid()),
								CmHelper.createAttribute(
										CafConstants.PULL_FREQUENCY, freq) };

				sub.setAttributes(attrs);
				return sub;
			}
		}

		return null;
	}
}
