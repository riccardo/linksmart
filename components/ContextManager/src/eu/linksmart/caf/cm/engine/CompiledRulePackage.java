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
package eu.linksmart.caf.cm.engine;

import java.util.Map.Entry;

import org.drools.builder.KnowledgeBuilder;

import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.cm.rules.ContextRuleSet;
import eu.linksmart.caf.cm.util.CmHelper;

/**
 * Class holds the compiled {@link KnowledgeBuilder}, built by the Rule Engine,
 * along with rule package entry (contextId & package name) for the compiled
 * {@link ContextRuleSet}.
 * 
 * @author Michael Crouch
 * 
 */
public class CompiledRulePackage {

	/**
	 * The rule package entry, stored as an {@link Attribute} with the contextId
	 * and associated rule package
	 */
	private Attribute entry;

	/** The {@link KnowledgeBuilder} */
	private KnowledgeBuilder compiledKnowledge;

	/**
	 * Constructor passing the {@link ContextRulePackage} and the built
	 * knowledge, as a {@link KnowledgeBuilder}
	 * 
	 * @param contextId
	 *            the contextId
	 * @param packageName
	 *            the packageName
	 * @param compiledKnowledge
	 *            the {@link KnowledgeBuilder}
	 */
	public CompiledRulePackage(String contextId, String packageName,
			KnowledgeBuilder compiledKnowledge) {
		entry = CmHelper.createAttribute(contextId, packageName);
		this.compiledKnowledge = compiledKnowledge;
	}

	/**
	 * Gets the {@link KnowledgeBuilder}
	 * 
	 * @return the {@link KnowledgeBuilder}
	 */
	public KnowledgeBuilder getCompiledKnowledge() {
		return compiledKnowledge;
	}

	/**
	 * Sets the {@link KnowledgeBuilder}
	 * 
	 * @param compiledKnowledge
	 *            the {@link KnowledgeBuilder} to set
	 */
	public void setCompiledKnowledge(KnowledgeBuilder compiledKnowledge) {
		this.compiledKnowledge = compiledKnowledge;
	}

	/**
	 * Gets the entry. <p> In format <code>[ {contextId}, {packageName} ]</code>
	 * 
	 * @return the entry
	 */
	public String[] getEntry() {
		return new String[] { entry.getId(), entry.getValue() };
	}

}
