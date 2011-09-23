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
package eu.linksmart.caf.cm.engine.members;

import java.io.PrintStream;

import org.apache.log4j.Logger;


import eu.linksmart.caf.cm.engine.Encodeable;
import eu.linksmart.caf.cm.engine.TimeService;
import eu.linksmart.caf.cm.engine.contexts.BaseContext;
import eu.linksmart.caf.cm.engine.event.UpdatedMemberEvent;
import eu.linksmart.caf.cm.impl.util.NumericHelper;
import eu.linksmart.caf.cm.managers.RuleEngine;
import eu.linksmart.caf.cm.rules.Rule;
import eu.linksmart.caf.cm.specification.ContextSpecification;

/**
 * Fact Type class representing a member of a {@link BaseContext}, as defined in
 * the {@link ContextSpecification}.<p> The {@link ContextMember} is a simple
 * key-value type, with some additional qualification possible through the
 * instanceOf parameter.<p> Contains the contextId and key, the dataType - along
 * with representations of the current value of the Member, in String and (if
 * the dataType is numeric) as a Double. Finally, it also contains an instanceOf
 * variable, defining what the Value represents - for example, the unit of the
 * value. <p>
 * 
 * Data Types should be represented using the XML Schema qualified data types
 * (e.g. String = "http://www.w3.org/2001/XMLSchema#string")
 * 
 * @author Michael Crouch
 * 
 */
public final class ContextMember implements Encodeable {

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(ContextMember.class);

	/** the parent contextId */
	private String contextId;

	/** the parent {@link BaseContext} */
	private BaseContext context;

	/** the dataType */
	private String dataType;

	/** the related state variable (UPnP) */
	private String relatedStateVariable;

	/** the instanceOf - e.g. unit */
	private String instanceOf;

	/** the key */
	private String key;

	/** the String value of the member */
	private String strValue;

	/** the numeric value of the member */
	private Double numValue;

	/** the last updated timestamp */
	private String timestamp;

	/** the flag denoting whether the datatype is numeric */
	private boolean numeric = false;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the hosting {@link BaseContext}
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @param dataType
	 *            the data type
	 * @param relatedStateVariable
	 *            the relatedStateVariable
	 */
	public ContextMember(BaseContext context, String key, String value,
			String dataType, String relatedStateVariable) {
		this.context = context;
		this.contextId = context.getContextId();
		this.key = key;
		this.setDataType(dataType);
		this.strValue = value;
		this.numValue = null;
		this.converStrValToNum();
		this.timestamp = TimeService.getInstance().getCurrentTimestamp();
		this.relatedStateVariable = relatedStateVariable;
	}

	/**
	 * Gets the String value of the member
	 * 
	 * @return the value as String
	 */
	public String getStrValue() {
		return strValue;
	}

	/**
	 * Sets the String value of the Member. If the value is of numeric type, it
	 * also updated the numeric value (converting strValue to Double)
	 * 
	 * @param strValue
	 *            the String value to set
	 */
	public void setStrValue(String strValue) {
		UpdatedMemberEvent event = ContextMember.getUpdatedEvent(this);

		this.strValue = strValue;

		if (strValue == null) {
			numValue = null;
			return;
		}

		// If Member is numeric, convert to Double
		converStrValToNum();
		this.timestamp = TimeService.getInstance().getCurrentTimestamp();
		if (logger.isDebugEnabled()){
			logger.debug("Member '" + key + "' of '" + contextId + "' updated at " 
					+ timestamp + ". New value is '" + strValue + "'");
		}
		ContextMember.fireUpdatedEvent(event);
	}

	/**
	 * Attempts to convert the String value of the {@link ContextMember} into a
	 * double, and store as the Numeric Value
	 */
	private void converStrValToNum() {
		// If Member is numeric, convert to Double
		if (numeric) {
			if (strValue == null || "".equals(strValue))
				return;
			
			try {
				numValue = Double.parseDouble(strValue);
			} catch (Exception e) {
				logger.error("Error converting to '" + strValue
						+ "' to numeric (double): " + e.getMessage());
			}
		}
	}

	/**
	 * Gets the numeric value of the Member
	 * 
	 * @return the numeric value, as a Double
	 */
	public Double getNumValue() {
		return numValue;
	}

	/**
	 * Sets the numeric value of the member, as also updates the String value
	 * 
	 * @param numValue
	 *            the numeric value to set
	 */
	public void setNumValue(Double numValue) {
		UpdatedMemberEvent event = ContextMember.getUpdatedEvent(this);

		this.numValue = numValue;
		strValue = Double.toString(numValue);
		setTimestamp(TimeService.getInstance().getCurrentTimestamp());

		if (logger.isDebugEnabled()){
			logger.debug("Member '" + key + "' of '" + contextId + "' updated at " 
					+ timestamp + ". New value is '" + strValue + "'");
		}
		ContextMember.fireUpdatedEvent(event);
	}

	/**
	 * Returns the timestamp for the latest change in this member
	 * 
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp for the latest change of this member
	 * 
	 * @param timestamp
	 *            the timestamp
	 */
	private void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Returns the dataType of the member.
	 * 
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Sets the dataType of this Member. If the type is numeric, the numeric
	 * flag is set, so that String values are converted to Doubles
	 * 
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
		try {
			numeric = NumericHelper.isNumericType(dataType);
		} catch (Exception e) {
			logger.error("Error determining whether '" + dataType
					+ "' is numeric: " + e.getMessage());
		}
	}

	/**
	 * Gets the related state variable of the Member
	 * 
	 * @return the related state variable
	 */
	public String getRelatedStateVariable() {
		return relatedStateVariable;
	}

	/**
	 * Sets the related state variable of the Member
	 * 
	 * @param relatedStateVariable
	 *            the related state variable
	 */
	public void setRelatedStateVariable(String relatedStateVariable) {
		this.relatedStateVariable = relatedStateVariable;
	}

	/**
	 * Returns the flag defining whether the dataType of the Member is numeric
	 * 
	 * @return the numeric flag
	 */
	public boolean isNumeric() {
		return numeric;
	}

	/**
	 * Sets whether the dataType is numeric
	 * 
	 * @param numeric
	 *            the numeric flag
	 */
	public void setNumeric(boolean numeric) {
		this.numeric = numeric;
	}

	/**
	 * Gets the contextId of the Member
	 * 
	 * @return the contextId
	 */
	public String getContextId() {
		return contextId;
	}

	/**
	 * Sets the contextId of the Member
	 * 
	 * @param contextId
	 *            the contextId
	 */
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	/**
	 * Gets the hosting {@link BaseContext} of the member
	 * 
	 * @return the {@link BaseContext}
	 */
	public BaseContext getContext() {
		return context;
	}

	/**
	 * Sets the hosting {@link BaseContext} of the member
	 * 
	 * @param context
	 *            the {@link BaseContext}
	 */
	public void setContext(BaseContext context) {
		this.context = context;
	}

	/**
	 * Gets the key of the {@link ContextMember}
	 * 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key of the {@link ContextMember}
	 * 
	 * @param key
	 *            the key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Sets the instanceOf for the Member. Typically, this would be the unit
	 * value.
	 * 
	 * @param instanceOf
	 *            the instanceOf
	 */
	public void setInstanceOf(String instanceOf) {
		this.instanceOf = instanceOf;
	}

	/**
	 * Gets the Member instanceOf
	 * 
	 * @return the instanceOf
	 */
	public String getInstanceOf() {
		return instanceOf;
	}

	/**
	 * Clones the provided {@link ContextMember}, returning the clone
	 * 
	 * @param member
	 *            the {@link ContextMember} to clone
	 * @return the cloned {@link ContextMember}
	 */
	private static ContextMember clone(ContextMember member) {
		ContextMember clone =
				new ContextMember(member.getContext(), member.getKey(), member
						.getStrValue(), member.getDataType(), member
						.getRelatedStateVariable());
		clone.setInstanceOf(member.getInstanceOf());
		clone.setTimestamp(member.getTimestamp());
		return clone;
	}

	/**
	 * Fires the {@link ContextMember} updated event to the {@link Rule}
	 * 
	 * @param event
	 *            the {@link UpdatedMemberEvent}
	 */
	private static void fireUpdatedEvent(UpdatedMemberEvent event) {
//		RuleEngine re = RuleEngine.getSingleton();
//		if (re != null) {
//			re.insert(event);
//		}
	}

	/**
	 * Generates the {@link UpdatedMemberEvent}, by cloning the
	 * {@link ContextMember}
	 * 
	 * @param member
	 *            the {@link ContextMember}
	 * @return the {@link UpdatedMemberEvent}
	 */
	private static UpdatedMemberEvent getUpdatedEvent(ContextMember member) {
		ContextMember cloned = ContextMember.clone(member);
		return new UpdatedMemberEvent(cloned, member);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("<Member key=\"");
		buffer.append(key).append("\" dataType=\"").append(dataType).append(
				"\" timestamp=\"").append(timestamp).append("\"");
		if ((instanceOf != null) && (!"".equals(instanceOf))) {
			buffer.append(" instanceOf=\"").append(instanceOf).append("\"");
		}
		if ((relatedStateVariable != null)
				&& (!"".equals(relatedStateVariable))) {
			buffer.append(" relatedStateVariable=\"").append(
					relatedStateVariable).append("\"");
		}

		buffer.append(">").append(strValue).append("</Member>");
		return buffer.toString();
	}

	@Override
	public void encode(PrintStream out) {
		out.println(toString());
	}
}
