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

import java.io.PrintStream;


import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.exceptions.ContextManagerException;
import eu.linksmart.caf.cm.exceptions.RuleParserException;
import eu.linksmart.caf.cm.rules.Action;
import eu.linksmart.caf.cm.rules.ContextRuleSet;
import eu.linksmart.caf.cm.rules.Rule;

/**
 * Abstract class for ActionProcessors to implement, providing the functionality
 * for reporting whether the implemented ActionProcessor can handle a particular
 * actionId, to process it, and also to provide the encoding for the action, to
 * be used in the DRL rules.
 * 
 * @author Michael Crouch
 */
public abstract class ActionProcessor {

	/**
	 * Returns whether the ActionProcessor is capable of processing the given
	 * actionType
	 * 
	 * @param actionType
	 *            the String ID for the action
	 * @return boolean stating whether the ActionProcessor can handle the id
	 */
	public abstract boolean canProcessAction(String actionType);

	/**
	 * Processes the {@link ThenAction}
	 * 
	 * @param action
	 *            the {@link ThenAction}
	 * @return boolean stating success of processing the action
	 */
	public abstract boolean processAction(ThenAction action);

	/**
	 * Encodes the DRL-representation for the {@link Action} specified in the
	 * RHS of a Context rule. <p> Default functionality is to add the
	 * {@link Action} as a {@link ThenAction}, and insert into the Working
	 * Memory to be handled by the {@link ActionManager}.
	 * 
	 * @param action
	 *            the {@link Action} from the {@link Rule} in a
	 *            {@link ContextRuleSet}
	 * @param actionName
	 *            the name of the action, to be encoded in DRL [
	 * @param out
	 *            the {@link PrintStream} to encode the DRL code to
	 * @throws RuleParserException
	 */
	public void encodeAction(Action action, String actionName, PrintStream out)
			throws RuleParserException {

		out.println("ThenAction " + actionName + " = new ThenAction(\""
				+ action.getId() + "\");");

		for (int j = 0; j < action.getAttributes().length; j++) {
			Attribute attr = action.getAttributes()[j];
			out.println(actionName + ".addAttribute(\"" + attr.getId() + "\", "
					+ attr.getValue() + ");");
		}

		for (int k = 0; k < action.getParameters().length; k++) {
			Parameter param = action.getParameters()[k];
			out.println(actionName + ".addParameter(\"" + param.getName()
					+ "\", \"" + param.getType() + "\", " + param.getValue()
					+ ");");
		}

		out.println("insert(" + actionName + ");");
	}

}
