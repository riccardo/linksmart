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
package eu.linksmart.caf.cm.exceptions;

import eu.linksmart.caf.cm.ContextManagerError;
import eu.linksmart.caf.cm.ContextResponse;

/**
 * 
 * Exception thrown in the Context Manager, representing an error in processing
 * some request.<p>
 * 
 * Contains: <ol type="i"> <li>Context ID - Contextual Id of the action throwing
 * the error</li> <li>Action - the action being made that throws the error</li>
 * <li>Message - the error message</li> </ol>
 * 
 * @author Michael Crouch
 * 
 */
public class ContextManagerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** the contextId */
	private String contextId;

	/** the action */
	private String action;

	/** the message */
	private String message;

	/**
	 * Constructor passing the contextId, action and message of the exception
	 * 
	 * @param contextId
	 *            the contextId
	 * @param action
	 *            the action
	 * @param message
	 *            the message
	 */
	public ContextManagerException(String contextId, String action,
			String message) {
		this.contextId = contextId;
		this.message = message;
		this.action = action;

	}

	/**
	 * Gets the contextId
	 * 
	 * @return the contextId
	 */
	public String getContextId() {
		return contextId;
	}

	/**
	 * Gets the message
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the error message
	 * 
	 * @param message
	 *            the message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Sets the contextId
	 * 
	 * @param contextId
	 *            the contextId
	 */
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	/**
	 * Gets the action of the exception
	 * 
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action
	 * 
	 * @param action
	 *            the action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Gets the error as a {@link ContextManagerError} object, for returning in
	 * the {@link ContextResponse}
	 * 
	 * @return the generated {@link ContextManagerError}
	 */
	public ContextManagerError getError() {
		return new ContextManagerError(message, contextId, action);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(contextId).append(" : ").append(action).append(" : ")
				.append(message).append("\n");
		return buffer.toString();
	}

}
