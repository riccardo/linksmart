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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.linksmart.caf.cm.ContextManagerError;

/**
 * A thrown exception with multiple {@link ContextManagerError}s, stored in a
 * {@link List}.
 * 
 * @author Michael Crouch
 * 
 */
public final class ErrorListException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link List} of {@link ContextManagerError}s
	 */
	private List<ContextManagerError> errorList;

	/**
	 * Constructor
	 */
	public ErrorListException() {
		errorList = new ArrayList<ContextManagerError>();
	}

	/**
	 * Constructor passing the {@link List} of {@link ContextManagerError}s
	 * 
	 * @param errorList
	 *            the {@link List} of {@link ContextManagerError}s
	 */
	public ErrorListException(List<ContextManagerError> errorList) {
		this.errorList = errorList;
	}

	/**
	 * Constructor creating a single {@link ContextManagerError}
	 * 
	 * @param contextId
	 *            the contextId for the {@link ContextManagerError}
	 * @param action
	 *            the action for the {@link ContextManagerError}
	 * @param message
	 *            the message for the {@link ContextManagerError}
	 */
	public ErrorListException(String contextId, String action, String message) {
		errorList = new ArrayList<ContextManagerError>();
		ContextManagerError error =
				new ContextManagerError(message, contextId, action);
		errorList.add(error);
	}

	/**
	 * Creates a {@link ContextManagerError} and adds it to the list
	 * 
	 * @param id
	 *            the id of the error
	 * @param subject
	 *            the subject of the error
	 * @param description
	 *            the description of the error
	 */
	public void addError(String id, String subject, String description) {
		ContextManagerError error =
				new ContextManagerError(description, id, subject);
		errorList.add(error);
	}

	/**
	 * Adds a {@link ContextManagerError} to the error list
	 * 
	 * @param error
	 *            the {@link ContextManagerError}
	 */
	public void addError(ContextManagerError error) {
		errorList.add(error);
	}

	/**
	 * Sets the {@link List} of {@link ContextManagerError}s
	 * 
	 * @param errorList
	 *            the {@link List} of {@link ContextManagerError}s
	 */
	public void setErrorSet(List<ContextManagerError> errorList) {
		this.errorList = errorList;
	}

	/**
	 * Gets the {@link List} of {@link ContextManagerError}s
	 * 
	 * @return the {@link List} of {@link ContextManagerError}s
	 */
	public List<ContextManagerError> getErrorList() {
		return errorList;
	}

	/**
	 * Gets the {@link List} of {@link ContextManagerError}s as an array
	 * 
	 * @return the {@link List} of {@link ContextManagerError}s as an array
	 */
	public ContextManagerError[] getErrorArray() {
		return errorList.toArray(new ContextManagerError[errorList.size()]);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		Iterator<ContextManagerError> it = errorList.iterator();
		while (it.hasNext()) {
			ContextManagerError error = it.next();
			buffer.append(error.getErrorId()).append(" : ").append(
					error.getErrorSubject()).append(" : ").append(
					error.getErrorDescription()).append("\n");
		}
		return buffer.toString();
	}
}
