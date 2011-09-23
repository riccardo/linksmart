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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.managers.CmInternalManager;

/**
 * The ActionManager is an {@link CmInternalManager} that is used as a global in
 * the Drools Rule Engine, to handle any {@link ThenAction} objects inserted
 * into the Working Memory as the output of a rule. <p>
 * 
 * It is configured with multiple {@link ActionProcessor} objects, that each
 * handle a particular String action Id. <p>
 * 
 * @author Michael Crouch
 */
public class ActionManager extends CmInternalManager {

	/** The Id for this Internal Manager */
	public static final String MANAGER_ID = "eu.linksmart.caf.cm.ActionManager";

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(ActionManager.class);

	/**
	 * {@link Set} of {@link ActionProcessor}
	 */
	private Set<ActionProcessor> actionProcessors;

	/**
	 * Class constructor
	 */
	public ActionManager() {
		this(null);
	}

	/**
	 * Class constructor passing a Set of {@link ActionProcessor}
	 * 
	 * @param actionProcessors
	 *            Initialised set of {@link ActionProcessor}s
	 */
	public ActionManager(Set<ActionProcessor> actionProcessors) {
		this.actionProcessors = actionProcessors;
		if (this.actionProcessors == null)
			this.actionProcessors = new HashSet<ActionProcessor>();
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
	 * Receives posted {@link ThenAction}s, and forwards them to the appropriate
	 * {@link ActionProcessor} to be handled
	 * 
	 * @param action
	 *            The posted {@link ThenAction}
	 * @return boolean denoting whether the action has been handled
	 */
	public boolean processAction(ThenAction action) {
		logger.info("Processing action '" + action.getId() + "'");
		ActionProcessor proc = getActionProcessor(action.getId());
		if (proc != null) {
			(new ProcessActionThread(proc, action)).run();
			return true;
		}
		logger.warn("No corresponding ActionProcessor exists for action id '"
				+ action.getId() + "'");
		return false;
	}

	/**
	 * Returns whether an {@link ActionProcessor} for the given actionId has
	 * been registered with the ActionManager
	 * 
	 * @param actionId
	 *            the Id of an action
	 * @return boolean
	 */
	public boolean hasActionProcessor(String actionId) {
		if (getActionProcessor(actionId) == null)
			return false;
		return true;
	}

	/**
	 * Returns the {@link ActionProcessor} registered to handle the give
	 * actionId
	 * 
	 * @param actionId
	 *            the Id of an action
	 * @return {@link ActionProcessor} for the actionId
	 */
	public ActionProcessor getActionProcessor(String actionId) {
		Iterator<ActionProcessor> it = actionProcessors.iterator();
		while (it.hasNext()) {
			ActionProcessor proc = it.next();
			if (proc.canProcessAction(actionId)) {

				return proc;
			}
		}
		return null;
	}

	/**
	 * Registers an {@link ActionProcessor} with the ActionManager
	 * 
	 * @param processor
	 *            the {@link ActionProcessor} to add
	 */
	public void addActionProcessor(ActionProcessor processor) {
		if (!actionProcessors.contains(processor))
			actionProcessors.add(processor);
	}

	/**
	 * Thread to be created an appropriate {@link ActionProcessor} has been
	 * found to handle the associated {@link ThenAction}, in order for it to run
	 * in a separate Thread, in case the processing of the {@link ThenAction}
	 * takes time
	 * 
	 * @author Michael Crouch
	 */
	private class ProcessActionThread extends Thread {

		/** the {@link ActionProcessor} */
		private ActionProcessor processor;

		/** the {@link ThenAction} */
		private ThenAction action;

		/**
		 * Contructor for Thread, passing the {@link ActionProcessor} and the
		 * associated {@link ThenAction}
		 * 
		 * @param processor
		 *            the {@link ActionProcessor}
		 * @param action
		 *            the {@link ThenAction}
		 */
		public ProcessActionThread(ActionProcessor processor, ThenAction action) {
			this.processor = processor;
			this.action = action;
		}

		/**
		 * Processes the {@link ThenAction}
		 */
		public void run() {
			processor.processAction(action);
		}
	}
}
