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
 * Copyright (C) 2006-2010 [Fraunhofer SIT, Julian Schuette] the HYDRA
 * consortium, EU project IST-2005-034891
 * 
 * This file is part of LinkSmart.
 * 
 * LinkSmart is free software: you can redistribute it and/or modify it under
 * the terms of the GNU LESSER GENERAL PUBLIC LICENSE version 3 as published by
 * the Free Software Foundation.
 * 
 * LinkSmart is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with LinkSmart. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.linksmart.policy.pip.ontology;

import java.util.HashSet;
import java.util.Set;

import org.apache.axis.utils.ClassUtils;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.aom.ApplicationOntologyManager;
import eu.linksmart.policy.pdp.ext.function.impl.PdpFunctionScope;
import eu.linksmart.policy.pdp.ext.impl.PipModule;
import com.sun.xacml.cond.Function;

/**
 * Interface for retrieving XACML attributes from the Context Manager Requests
 * to the Context Manager must specify the Issuer of the Attribute (in the
 * Policy) as "ContextManager"
 * 
 */
public class OntologyPIP extends PipModule {

	private static final Logger logger = Logger.getLogger(OntologyPIP.class);
	private QueryFunction dlq;
	private static ComponentContext context;
	private ApplicationOntologyManager ontManager;
	
	/** supported target functions */
	private Set<Function> pdpTargetFunctions = new HashSet<Function>();

	/**
	 * Activator method of the Semantic PIP. Registers the following two custom
	 * XACML function: <p>
	 * urn:oasis:names:tc:xacml:1.0:function:sem:satisfiesQuery <br>
	 * urn:oasis:names:tc:xacml:1.0:function:sem:Property
	 */
	protected void activate(final ComponentContext context) {
		logger.debug("Ontology PIP " + this
				+ " starts (OntologyManager required)");
		ClassUtils.setDefaultClassLoader(Thread.currentThread()
				.getContextClassLoader());
		OntologyPIP.context = context;
	}

	protected void deactivate() {
		logger.debug("Ontology PIP stopped");
	}

	protected void bindAOM(ApplicationOntologyManager aom) {
		this.ontManager = aom;

		// -- Add custom functions ---
		dlq =
				new QueryFunction(
						"urn:oasis:names:tc:xacml:1.0:function:sem:satisfiesQuery");
		dlq.setOntologyManager(ontManager);
		pdpTargetFunctions.add(dlq);

		logger.info("Ontology PIP is ready and connected to Ontology Manager.");
	}

	protected void unbindAOM(ApplicationOntologyManager aom) {
		this.ontManager = null;
		pdpTargetFunctions.remove(dlq);
		dlq = null;
	}

	@Override
	public Set<Function> getFunctions(final PdpFunctionScope theScope) {
		switch (theScope) {
			case TARGET: {
				return pdpTargetFunctions;
			}
			default: {
				return new HashSet<Function>();
			}
		}
	}

	@Override
	public String getIdentifier() {
		return super.getIdentifier();
	}

	@Override
	public boolean isDesignatorSupported() {
		return false;
	}
}
