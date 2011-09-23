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
package eu.linksmart.caf.cm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.util.CmHelper;

/**
 * ThenAction to be processed by the appropriate {@link ActionProcessor}.
 * ThenAction object is created in the THEN part of rules, and handled by the
 * {@link ActionManager}.
 * 
 * @author Michael Crouch
 */
public class ThenAction {

	/** the action id */
	private String id;

	/** {@link Map} of action attributes */
	private Map<String, String> attributes;

	/** {@link List} of {@link Parameter}s */
	private List<Parameter> parameters;

	/**
	 * Constructor providing the id of the ThenAction
	 * 
	 * @param id
	 *            the action id
	 */
	public ThenAction(String id) {
		this.id = id;
		attributes = new HashMap<String, String>();
		parameters = new ArrayList<Parameter>();
	}

	/**
	 * Creates and returns a ThenAction
	 * 
	 * @param id
	 *            the ID of the ThenAction
	 * @return the created ThenAction
	 */
	public static ThenAction create(String id) {
		return new ThenAction(id);
	}

	/**
	 * Adds an id-value attribute to the ThenAction
	 * 
	 * @param id
	 *            the id of the attribute
	 * @param value
	 *            the value of the attribute
	 */
	public void addAttribute(String id, String value) {
		attributes.put(id, value);
	}

	/**
	 * Adds a parameter (name-type-value), created as a {@link Parameter}, to
	 * the ThenAction, stored in a List
	 * 
	 * @param name
	 *            the name of the parameter
	 * @param type
	 *            the type of the parameter
	 * @param value
	 *            the value of the parameter
	 */
	public void addParameter(String name, String type, String value) {
		this.addParameter(CmHelper.createParameter(name, type, value));
	}

	/**
	 * Adds the {@link Parameter} to the ThenAction List of parameters
	 * 
	 * @param param
	 *            the {@link Parameter} to add
	 */
	public void addParameter(Parameter param) {
		parameters.add(param);
	}

	/**
	 * Returns the ID of the ThenAction
	 * 
	 * @return the String id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the List of {@link Parameter}s
	 * 
	 * @return the List of {@link Parameter}s
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * Returns the value of the attribute with the given attrId
	 * 
	 * @param attrId
	 *            the id of the attribute
	 * @return the value of the attribute
	 */
	public String getAttribute(String attrId) {
		if (attributes.containsKey(attrId))
			return attributes.get(attrId);
		return null;
	}

}
