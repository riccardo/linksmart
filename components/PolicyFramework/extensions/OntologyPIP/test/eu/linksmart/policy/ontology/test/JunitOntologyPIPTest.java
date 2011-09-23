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
 * Copyright (C) 2006-2010 [Fraunhofer SIT, Julian Schuette]
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

package eu.linksmart.policy.ontology.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.aom.ApplicationOntologyManager;
import eu.linksmart.policy.pip.ontology.OntologyPIP;
import eu.linksmart.policy.pip.ontology.QueryFunction;
import com.sun.xacml.EvaluationCtx;

/**
 * Unit tests for {@link OntologyPIP}
 * 
 * @author Julian Schuette
 * 
 */
public class JunitOntologyPIPTest {

	@Test
	public void testActivate() {
		System.out.println("OntologyPIP Test Execution");
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		replay(compCtxMock);
	}

	@Test
	public void testEval() {
		System.out.println("Testing OntologyPIP evaluation");

		// Create OntologyManager mock
		ApplicationOntologyManager ontMock =
				createMock(ApplicationOntologyManager.class);

		// Create ComponentContext mock
		ComponentContext cmpCtxMock = createMock(ComponentContext.class);
		// expect(cmpCtxMock.locateService("OntologyManager")).andReturn(ontMock);
		replay(cmpCtxMock);

		// Create EvaluateContext mock object
		EvaluationCtx mockContext = createMock(EvaluationCtx.class);

		QueryFunction qf =
				new QueryFunction(
						"urn:oasis:names:tc:xacml:1.0:function:sem:satisfiesQuery");
		qf.setOntologyManager(ontMock);
		List inputs = new LinkedList();
		qf.evaluate(inputs, mockContext);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEvaluate() {
		/*
		 * NetworkManagerApplication nmMock =
		 * createMock(NetworkManagerApplication.class); replay(nmMock);
		 * 
		 * BundleContext bnCtxMock = createMock(BundleContext.class); Dictionary
		 * d = new Hashtable(); d.put(Constants.SERVICE_PID,
		 * "eu.linksmart.policy.pep.obl.debug"); replay(bnCtxMock);
		 * 
		 * ComponentContext cmpCtxMock = createMock(ComponentContext.class);
		 * expect(cmpCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		 * replay(cmpCtxMock);
		 * 
		 * ResponseCtx resCtxMock = createMock(ResponseCtx.class);
		 * replay(resCtxMock);
		 * 
		 * SemanticPIP observer = new SemanticPIP(); ArrayList assignments = new
		 * ArrayList(); Attribute attr = null; Obligation obli = null;
		 * 
		 * try { // /* applicable obligation
		 */
		// attr = new Attribute(new URI(
		// PepXacmlConstants.OBLIGATION_DEBUG_POLICY_NAME.getUrn()),
		// "me", new DateTimeAttribute(),
		// new StringAttribute("hello"));
		// assignments.add(attr);
		// obli = new Obligation(new URI(
		// PepXacmlConstants.OBLIGATION_DEBUG.getUrn()), 1,
		// assignments);
		// observer.activate(cmpCtxMock);
		// assertTrue(observer.evaluate(obli, new PepRequest(null, null, null,
		// null, null, null, null), resCtxMock));
		// } catch (URISyntaxException use) {
		// use.printStackTrace();
		// fail("Exception");
		// } catch (NullPointerException npe) {
		// fail("Exception");
		// }
		// assignments.clear();
		// try {
		// /* non-applicable obligation */
		// attr = new Attribute(new URI(
		// PepXacmlConstants.OBLIGATION_SEND_MESSAGE_FROM.getUrn()),
		// "me", new DateTimeAttribute(),
		// new StringAttribute("hello"));
		// assignments.add(attr);
		// obli = new Obligation(new URI(
		// PepXacmlConstants.OBLIGATION_SEND_MESSAGE.getUrn()), 1,
		// assignments);
		// observer.activate(cmpCtxMock);
		// assertFalse(observer.evaluate(obli, new PepRequest(null, null, null,
		// null, null, null, null), resCtxMock));
		// } catch (URISyntaxException use) {
		// use.printStackTrace();
		// fail("Exception");
		// }
		// verify(nmMock);
		// verify(bnCtxMock);
		// verify(cmpCtxMock);
		// verify(resCtxMock);
	}

}
