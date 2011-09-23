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
package eu.linksmart.caf.cm.engine.event;

/**
 * Description of an Event thrown by a LinkSmart Entity, as described by the
 * {@link EventMeta}
 * 
 * @author Michael Crouch
 * 
 */
public class EventKeyMeta {

	/** the parent {@link EventMeta} */
	private EventMeta event;

	/** the Event key name */
	private String name;

	/** the related state variable */
	private String relatedStateVariable;

	/** the data type */
	private String dataType;

	/** the allowed (numerical) minimum value */
	private Double allowedMin;

	/** the allowed (numerical) maximum value */
	private Double allowedMax;

	/** the unit */
	private String unit;

	/**
	 * Constructor
	 * 
	 * @param event
	 *            the {@link EventMeta}
	 * @param name
	 *            the key name
	 * @param dataType
	 *            the dataType
	 */
	public EventKeyMeta(EventMeta event, String name, String dataType) {
		this.event = event;
		this.name = name;
		this.dataType = dataType;
	}

	/**
	 * Gets the {@link EventMeta}
	 * 
	 * @return the {@link EventMeta}
	 */
	public EventMeta getEvent() {
		return event;
	}

	/**
	 * Sets the {@link EventMeta}
	 * 
	 * @param event
	 *            the {@link EventMeta}
	 */
	public void setEvent(EventMeta event) {
		this.event = event;
	}

	/**
	 * Gets the name of the key
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the key
	 * 
	 * @param name
	 *            the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the related state variable
	 * 
	 * @return the related state variable
	 */
	public String getRelatedStateVariable() {
		return relatedStateVariable;
	}

	/**
	 * Sets the related state variable
	 * 
	 * @param relatedStateVariable
	 *            the related state variable
	 */
	public void setRelatedStateVariable(String relatedStateVariable) {
		this.relatedStateVariable = relatedStateVariable;
	}

	/**
	 * Gets the dataType
	 * 
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Sets the dataType
	 * 
	 * @param dataType
	 *            the DataType
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * Gets the allowed minimum
	 * 
	 * @return the allowed minimum
	 */
	public double getAllowedMin() {
		return allowedMin;
	}

	/**
	 * Sets the allowed minimum
	 * 
	 * @param allowedMin
	 *            the allowed minimum
	 */
	public void setAllowedMin(Double allowedMin) {
		this.allowedMin = allowedMin;
	}

	/**
	 * Gets the allowed maximum
	 * 
	 * @return the allowed maximum
	 */
	public double getAllowedMax() {
		return allowedMax;
	}

	/**
	 * Sets the allowed maximum
	 * 
	 * @param allowedMax
	 *            the allowed maximum
	 */
	public void setAllowedMax(Double allowedMax) {
		this.allowedMax = allowedMax;
	}

	/**
	 * Gets the associated unit
	 * 
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Sets the associated unit
	 * 
	 * @param unit
	 *            the unit
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
