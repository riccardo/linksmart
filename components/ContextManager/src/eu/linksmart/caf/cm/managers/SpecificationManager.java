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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import eu.linksmart.caf.cm.exceptions.ContextManagerException;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.specification.ContextSpecification;
import eu.linksmart.caf.cm.specification.Definition;

/**
 * Handles the storage and maintenance of defined {@link ContextSpecification}s
 * 
 */
public class SpecificationManager extends CmInternalManager {

	/** The Id for this Internal Manager */
	public static final String MANAGER_ID =
			"eu.linksmart.caf.cm.SpecificationManager";

	/**
	 * {@link HashMap} of contextIds and the associated
	 * {@link ContextSpecification}s
	 */
	private final HashMap<String, ContextSpecification> specs;

	/**
	 * Constructor
	 */
	public SpecificationManager() {
		specs = new HashMap<String, ContextSpecification>();
	}

	@Override
	public void initialise(CmManagerHub hub) {
		// None
	}

	@Override
	public void completedInit() {
		//Do nothing		
	}
	
	@Override
	public void shutdown() {
		// None
	}

	@Override
	public String getManagerId() {
		return MANAGER_ID;
	}

	/**
	 * Checks that the given {@link ContextSpecification} does not already
	 * exist, with that cont, and then stores
	 * 
	 * @param contextId
	 *            the contextId
	 * @param specification
	 *            the {@link ContextSpecification}
	 */
	public void storeContextSpecification(String contextId,
			ContextSpecification specification) throws ContextManagerException {

		if (hasContextId(contextId)) {
			throw new ContextManagerException(contextId,
					"Error storing ContextSpecifcation",
					"ContextID already exists");
		}
		specs.put(contextId, specification);
	}

	/**
	 * Returns whether the contextId already exists
	 * 
	 * @param contextId
	 *            the contextId
	 * @return whether it exists
	 * @throws ContextManagerException
	 */
	public boolean hasContextId(String contextId)
			throws ContextManagerException {

		String[] stored = getContextIdList();

		for (int i = 0; i < stored.length; i++) {
			String id = stored[i];
			if (id.equalsIgnoreCase(contextId))
				return true;
		}
		return false;
	}

	/**
	 * Return all {@link ContextSpecification}s specified in the String array
	 * 
	 * @param contextIds
	 *            array of contextIds
	 * @return {@link Set} of {@link ContextSpecification}s
	 */
	public Set<ContextSpecification> getContextSpecifications(
			String[] contextIds) throws ContextManagerException {

		Set<ContextSpecification> specSet = new HashSet<ContextSpecification>();

		for (int i = 0; i < contextIds.length; i++) {

			ContextSpecification spec = getContextSpecification(contextIds[i]);
			specSet.add(spec);
		}
		return specSet;
	}

	/**
	 * Returns the {@link ContextSpecification} with the given contextId;
	 * 
	 * @param contextId
	 *            the contextId
	 * @return the {@link ContextSpecification}
	 * @throws ContextManagerException
	 */
	public ContextSpecification getContextSpecification(String contextId)
			throws ContextManagerException {

		ContextSpecification ctx = specs.get(contextId);
		if (ctx == null)
			throw new ContextManagerException(contextId,
					"Error retrieving ContextSpecification",
					"Context doesn't exist");
		return ctx;
	}

	/**
	 * Returns the {@link Definition} for {@link ContextSpecification} with the
	 * given contextId
	 * 
	 * @param contextId
	 *            the contextId
	 * @return the {@link Definition}
	 * @throws ContextManagerException
	 */
	public Definition getContextDefinition(String contextId)
			throws ContextManagerException {
		ContextSpecification spec = this.getContextSpecification(contextId);
		return spec.getDefinition();
	}

	/**
	 * Returns all {@link Definition}s for all {@link ContextSpecification}s
	 * 
	 * @return the {@link Set} of {@link Definition}s
	 * @throws ContextManagerException
	 */
	public Set<Definition> getAllContextDefinitions()
			throws ContextManagerException {
		Set<Definition> defs = new HashSet<Definition>();
		Iterator<String> it = specs.keySet().iterator();
		while (it.hasNext()) {
			defs.add(this.getContextDefinition(it.next()));
		}
		return defs;
	}

	/**
	 * Return all {@link Definition}s for the {@link ContextSpecification}s
	 * specified by the array of contextIds
	 * 
	 * @param contextIds
	 *            the array of contextIds
	 * @return the {@link Set} of {@link Definition}s
	 * @throws ContextManagerException
	 */
	public Set<Definition> getContextDefnitions(String[] contextIds)
			throws ContextManagerException {

		try {
			Set<Definition> defSet = new HashSet<Definition>();

			for (int i = 0; i < contextIds.length; i++) {

				Definition def = getContextDefinition(contextIds[i]);
				defSet.add(def);
			}
			return defSet;
		} catch (ContextManagerException e) {
			e.setAction("Error getting ContextDefinitions {" + e.getAction()
					+ "}");
			throw e;
		}
	}

	/**
	 * Returns a list of contextIds for the stored {@link ContextSpecification}s
	 * 
	 * @return the array of contextIds
	 * @throws ContextManagerException
	 */
	public String[] getContextIdList() throws ContextManagerException {
		Set<String> contextIds = specs.keySet();
		if (contextIds.size() > 0) {
			return contextIds.toArray(new String[contextIds.size()]);
		} else
			return new String[0];
	}

	/**
	 * Removes the {@link ContextSpecification}, with the given contextId
	 * 
	 * @param contextId
	 *            the contextId to remove
	 */
	public void removeContextSpecification(String contextId) {
		specs.remove(contextId);
	}

}
