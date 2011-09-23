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

import eu.linksmart.caf.cm.rules.ContextRuleSet;
import eu.linksmart.caf.cm.rules.Rule;

/**
 * Exception thrown when parsing a {@link ContextRuleSet} into DRL
 * 
 * @author Michael Crouch
 * 
 */
public class RuleParserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6247640345977915253L;

	/** the {@link Rule} causing the error */
	private Rule errorRule;

	/** the message */
	private String message;

	/**
	 * Constructor passing the error message, and the {@link Rule} that caused
	 * the error
	 * 
	 * @param message
	 *            the error message
	 * @param errorRule
	 *            the {@link Rule} that caused the error
	 */
	public RuleParserException(String message, Rule errorRule) {
		this.message = message;
		this.errorRule = errorRule;
	}

	/**
	 * Gets the {@link Rule} that caused the error
	 * 
	 * @return the {@link Rule}
	 */
	public Rule getErrorRule() {
		return errorRule;
	}

	/**
	 * Sets the {@link Rule} that caused the error
	 * 
	 * @param errorRule
	 *            the {@link Rule}
	 */
	public void setErrorRule(Rule errorRule) {
		this.errorRule = errorRule;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * Sets the error message
	 * 
	 * @param message
	 *            the error message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
