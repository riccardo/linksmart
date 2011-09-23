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
package eu.linksmart.caf.cm.rules.drl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.cm.action.ActionManager;
import eu.linksmart.caf.cm.action.ActionProcessor;
import eu.linksmart.caf.cm.engine.contexts.Application;
import eu.linksmart.caf.cm.exceptions.RuleParserException;
import eu.linksmart.caf.cm.managers.RuleEngine;
import eu.linksmart.caf.cm.rules.Action;
import eu.linksmart.caf.cm.rules.ContextRuleSet;
import eu.linksmart.caf.cm.rules.DeclaredFunction;
import eu.linksmart.caf.cm.rules.DeclaredType;
import eu.linksmart.caf.cm.rules.Rule;
import eu.linksmart.caf.cm.specification.ContextSpecification;

/**
 * Handles the parsing of {@link ContextRuleSet}s to DRL, to be loaded to the
 * {@link RuleEngine}, extending the {@link BaseDrlBuilder}<p> It is configured
 * with an {@link ActionManager}, to handle the encoding of particular Actions,
 * with the specified {@link ActionProcessor} handling how the action is encoded
 * to DRL.
 * 
 * @author Michael Crouch
 */
public class RulePkgBuilder extends BaseDrlBuilder {

	/** the {@link ActionManager} */
	private ActionManager actionManager;

	/**
	 * Constructor
	 * 
	 * @param actionManager
	 *            the {@link ActionManager}
	 */
	public RulePkgBuilder(ActionManager actionManager) {
		this.actionManager = actionManager;
	}

	/**
	 * Builds the DRL file, from the {@link ContextRuleSet} with the given
	 * package name, and returns as a String.
	 * 
	 * @param packageName
	 *            the name of the rule package
	 * @param ruleSet
	 *            the {@link ContextRuleSet} from a {@link ContextSpecification}
	 * @param contextId 
	 * 			the contextId of the {@link Application} context, used to
	 * 			get the <code>$this</code> variable
	 * @return the String DRL
	 * @throws RuleParserException
	 *             any parsing errors
	 */
	public String buildDrl(String packageName, ContextRuleSet ruleSet, String contextId)
			throws RuleParserException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);

		out.println("package " + packageName);

		encodeImports(ruleSet.getImports(), out);
		encodeEventDeclaration(out);
		encodeDeclaredFunctions(ruleSet.getFunctions(), out);
		encodeDeclaredTypes(ruleSet.getTypes(), out);

		for (int i = 0; i < ruleSet.getRules().length; i++) {
			this.encodeRule(ruleSet.getRules()[i], contextId, out);
		}
		return baos.toString();
	}

	/**
	 * Encodes a {@link Rule}
	 * 
	 * @param rule
	 *            the {@link Rule}
	 * @param contextId 
	 * 			  the contextId
	 * @param out
	 *            the {@link PrintStream} to output to
	 * @throws RuleParserException
	 *             errors parsing - e.g unrecognised actions
	 */
	public void encodeRule(Rule rule, String contextId, PrintStream out)
			throws RuleParserException {

		out.println("rule \"" + rule.getRuleId() + "\"");

		for (int i = 0; i < rule.getRuleAttributes().length; i++) {
			Attribute attr = rule.getRuleAttributes()[i];
			out.println(attr.getId() + " " + attr.getValue());
		}

		out.println("when");
		if (contextId != null || !"".equals(contextId)) {
			out.println("$this : Application( contextId == \"" + contextId + "\" )");
		}
		
		out.println(rule.getWhenClause());
		out.println("then");
		int actionCount = 0;
		for (int j = 0; j < rule.getActions().length; j++) {
			String actionName = "action" + actionCount;
			Action action = rule.getActions()[j];
			ActionProcessor processor =
					actionManager.getActionProcessor(action.getId());
			if (processor == null) {
				throw new RuleParserException("Unrecognised Rule Action '"
						+ action.getId() + "'.", rule);
			} else {
				try {
					processor.encodeAction(action, actionName, out);
				} catch (RuleParserException e) {
					e.setErrorRule(rule);
					throw e;
				}
				actionCount++;
			}
		}
		out.println("end");
	}

}
