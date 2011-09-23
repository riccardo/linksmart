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
import java.util.Map;

import org.apache.log4j.Logger;
import org.drools.builder.KnowledgeBuilder;


import eu.linksmart.caf.cm.action.ActionManager;
import eu.linksmart.caf.cm.engine.CompiledRulePackage;
import eu.linksmart.caf.cm.exceptions.ContextManagerException;
import eu.linksmart.caf.cm.exceptions.ErrorListException;
import eu.linksmart.caf.cm.exceptions.RuleParserException;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.rules.ContextRuleSet;
import eu.linksmart.caf.cm.rules.drl.RulePkgBuilder;
import eu.linksmart.caf.cm.specification.ContextSpecification;
import eu.linksmart.caf.cm.util.CmHelper;

/**
 * Handles the process of adding / parsing Rules to the Rule Engine.
 * 
 * Also manages Rule Packages of installed Contexts
 * 
 */
public class RulesManager extends CmInternalManager {
	
	/** The Id for this Internal Manager */
	public static final String MANAGER_ID = "eu.linksmart.caf.cm.RulesManager";

	private static final Logger logger = Logger.getLogger(RulesManager.class); 
	
	/** Prefix for context rule packages */
	private static final String PACKAGE_PREFIX = "eu.linksmartmiddleware.";

	/** the {@link RuleEngine} */
	private RuleEngine ruleEngine;

	/** the {@link RulePkgBuilder} */
	private RulePkgBuilder ruleBuilder;

	/** {@link Map} contextId and its associated installed rule packages */
	private final Map<String, String> installedPackages;

	/**
	 * Constructor
	 */
	public RulesManager() {
		this.installedPackages = new HashMap<String, String>();
	}

	@Override
	public void initialise(CmManagerHub hub) {
		this.ruleBuilder =
				new RulePkgBuilder((ActionManager) hub
						.getManager(ActionManager.MANAGER_ID));
		this.ruleEngine = (RuleEngine) hub.getManager(RuleEngine.MANAGER_ID);
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
	 * Processes the {@link ContextRuleSet} specified in the
	 * {@link ContextSpecification}
	 * 
	 * If it is successfully parsed to DRL, and compiled successfully into
	 * Knowledge Packages, it is returned in a {@link CompiledRulePackage}
	 * 
	 * @param spec
	 *            the {@link ContextSpecification} to process
	 * @param contextId
	 *            the contextId
	 * @throws ContextManagerException
	 * @throws ErrorListException
	 * @return the {@link CompiledRulePackage}
	 */
	public CompiledRulePackage compileContextSpecification(
			ContextSpecification spec, String contextId)
			throws ContextManagerException, ErrorListException {

		String pkgName =
				CmHelper.normalise(PACKAGE_PREFIX
						+ spec.getDefinition().getName()
						+ contextId.replace('-', '_'));

		String drl;
		try {
			drl = ruleBuilder.buildDrl(pkgName, spec.getRuleSet(), contextId);
			logger.info("Application Rule Package parsed to DRL:\n\n" + drl);
		} catch (RuleParserException e) {
			throw new ContextManagerException(contextId,
					"Error parsing ContextRuleSet to DRL", e
							.getLocalizedMessage());
		}
		KnowledgeBuilder knowledgePkg = compileRulePackage(drl);

		return new CompiledRulePackage(contextId, pkgName, knowledgePkg);
	}

	/**
	 * Installs the given {@link CompiledRulePackage} to the {@link RuleEngine}.
	 * 
	 * @param compiledPkg
	 *            the {@link CompiledRulePackage} to install
	 */
	public void installCompiledRulePackage(CompiledRulePackage compiledPkg) {
		ruleEngine.addBuiltKnowledge(compiledPkg.getCompiledKnowledge());
		String[] entry = compiledPkg.getEntry();
		installedPackages.put(entry[0], entry[1]);
	}

	/**
	 * Compiles the given DRL into Drools Knowledge, stored in the
	 * {@link KnowledgeBuilder}
	 * 
	 * @param drl
	 *            the DRL
	 * @return the compiled {@link KnowledgeBuilder}
	 * @throws ErrorListException
	 */
	public KnowledgeBuilder compileRulePackage(String drl)
			throws ErrorListException {
		KnowledgeBuilder builder = ruleEngine.getKnowledgeBuilderSession();
		ruleEngine.compileRulePackage(drl, builder);
		return builder;
	}

	/**
	 * Removes all Rule Packages associated with this contextId from the
	 * {@link RuleEngine}
	 * 
	 * @param contextId
	 *            the contextId
	 * @throws ContextManagerException
	 */
	public void removeContextSpecification(String contextId)
			throws ContextManagerException {
		String pkgName = installedPackages.get(contextId);

		if (pkgName == null)
			return;
		ruleEngine.removePackage(pkgName);
		installedPackages.remove(pkgName);
	}
}
