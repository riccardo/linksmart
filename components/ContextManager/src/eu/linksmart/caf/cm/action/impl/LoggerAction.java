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
package eu.linksmart.caf.cm.action.impl;

import java.util.Iterator;

import org.apache.log4j.Logger;


import eu.linksmart.caf.ActionConstants;
import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.action.ActionProcessor;
import eu.linksmart.caf.cm.action.ThenAction;

/**
 * Implemented {@link ActionProcessor} logging information to the {@link Logger}
 * <p> The {@link ThenAction} that triggers it must contain the message to be
 * logged as a {@link Parameter}. No {@link Attribute}s are mandatory, but they
 * can be supplied to add the source and scope of the log. <p> If no source or
 * scope (or unrecognised scope) is provided, then the 'info' scope is used.
 * 
 * @author Michael Crouch
 * 
 */
public class LoggerAction extends ActionProcessor {

	/** The Action ID handled by this {@link ActionProcessor} */
	public static final String ACTION_ID = ActionConstants.LOGGER_ACTION;

	/** The Logger Scope value for <code>logger.info()</code> */
	public static final String INFO_SCOPE = "info";

	/** The Logger Scope value for <code>logger.debug()</code> */
	public static final String DEBUG_SCOPE = "debug";

	/** The Logger Scope value for <code>logger.error()</code> */
	public static final String ERROR_SCOPE = "error";

	/** The Logger Scope value for <code>logger.warn()</code> */
	public static final String WARN_SCOPE = "warn";

	/** The Logger Scope value for <code>logger.fatal()</code> */
	public static final String FATAL_SCOPE = "fatal";

	/** The Logger Scope value for <code>logger.trace()</code> */
	public static final String TRACE_SCOPE = "trace";

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(LoggerAction.class);

	@Override
	public boolean canProcessAction(String actionType) {
		return actionType.equalsIgnoreCase(ACTION_ID);
	}

	/**
	 * Logs the given message(s) to the Logger with the source and scope
	 * specified in the {@link ThenAction} attributes.
	 */
	@Override
	public boolean processAction(ThenAction action) {

		// Get scope
		String scope = action.getAttribute(ActionConstants.LOGGER_SCOPE);
		if (scope == null)
			scope = INFO_SCOPE;

		String source = action.getAttribute(ActionConstants.LOGGER_SOURCE);

		try {
			if (action.getParameters().size() == 0) {
				logger.error("Logger Action has no Parameters to log");
				return true;
			}

			Iterator<Parameter> it = action.getParameters().iterator();
			while (it.hasNext()) {
				Parameter param = (Parameter) it.next();
				String msg;
				if (source == null) {
					msg = param.getValue();
				} else {
					msg = "[" + source + "]" + param.getValue();
				}
				log(scope, msg);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Logs the given message to the given scope
	 * 
	 * @param scope
	 *            the scope
	 * @param message
	 *            the message
	 */
	private void log(String scope, String message) {

		if (scope.equalsIgnoreCase(DEBUG_SCOPE)) {
			logger.debug(message);
		} else if (scope.equalsIgnoreCase(ERROR_SCOPE)) {
			logger.error(message);
		} else if (scope.equalsIgnoreCase(WARN_SCOPE)) {
			logger.warn(message);
		} else if (scope.equalsIgnoreCase(FATAL_SCOPE)) {
			logger.fatal(message);
		} else if (scope.equalsIgnoreCase(TRACE_SCOPE)) {
			logger.trace(message);
		} else {
			logger.info(message);
		}
	}
}
