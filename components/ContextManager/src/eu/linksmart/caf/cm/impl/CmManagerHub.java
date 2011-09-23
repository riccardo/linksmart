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
package eu.linksmart.caf.cm.impl;

import java.util.HashMap;
import java.util.Iterator;

import eu.linksmart.caf.cm.managers.CmInternalManager;

/**
 * Contains all "inner" Managers of the Context Manager, in a single
 * configuration so that can be shared amongst each other
 * 
 * @author Michael Crouch
 */
public class CmManagerHub {

	/** {@link HashMap} of {@link CmInternalManager}s, mapped by the id */
	private HashMap<String, CmInternalManager> managerMap;

	/** the {@link ContextManagerApplication} */
	private ContextManagerApplication cmApp;

	/**
	 * Constructor
	 * 
	 * @param cmApp
	 *            the {@link ContextManagerApplication}
	 */
	public CmManagerHub(ContextManagerApplication cmApp) {
		this.cmApp = cmApp;
		this.managerMap = new HashMap<String, CmInternalManager>();
	}

	/**
	 * Adds a {@link CmInternalManager} to the Manager Hub
	 * 
	 * @param manager
	 *            the {@link CmInternalManager}
	 */
	public void addManager(CmInternalManager manager) {
		managerMap.put(manager.getManagerId(), manager);
		manager.registerManagerHub(this);
	}

	/**
	 * Gets the {@link CmInternalManager} with the given id, from the Manager
	 * Hub
	 * 
	 * @param managerId
	 *            the id of the {@link CmInternalManager} to get
	 * @return the {@link CmInternalManager}
	 */
	public CmInternalManager getManager(String managerId) {
		return managerMap.get(managerId);
	}

	/**
	 * Removes the {@link CmInternalManager} with the given managerId
	 * 
	 * @param managerId
	 *            the managerId
	 */
	public void removeManager(String managerId) {
		managerMap.remove(managerId);
	}

	/**
	 * Shuts down all managers
	 */
	public void shutdown() {
		Iterator<CmInternalManager> it = managerMap.values().iterator();
		while (it.hasNext()) {
			it.next().shutdown();
		}
	}

	/**
	 * Gets the {@link ContextManagerApplication}
	 * 
	 * @return the {@link ContextManagerApplication}
	 */
	public ContextManagerApplication getCmApp() {
		return cmApp;
	}

	/**
	 * Sets the {@link ContextManagerApplication}
	 * 
	 * @param cmApp
	 *            the {@link ContextManagerApplication}
	 */
	public void setCmApp(ContextManagerApplication cmApp) {
		this.cmApp = cmApp;
	}

	/**
	 * Collectively initialise all registered managers
	 */
	public void initialiseConfiguration() {
		// First register all with the managerHub
		Iterator<CmInternalManager> it = managerMap.values().iterator();
		while (it.hasNext()) {
			it.next().initialise(this);
		}
		
		it = managerMap.values().iterator();
		while (it.hasNext()) {
			it.next().completedInit();
		}
	}
}
