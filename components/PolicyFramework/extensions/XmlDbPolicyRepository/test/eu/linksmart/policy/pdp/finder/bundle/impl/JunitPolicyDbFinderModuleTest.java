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
package eu.linksmart.policy.pdp.finder.bundle.impl;

import static org.easymock.EasyMock.*;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Test;
import org.xmldb.api.base.Resource;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.finder.PolicyFinder;

import eu.linksmart.policy.pdp.admin.bundle.impl.XMLDBPdpAdminService;
import eu.linksmart.policy.pdp.finder.bundle.impl.PolicyDbFinderModule;

/**
 * Unit test for {@link PolicyDbFinderModule}
 * 
 * @author Marco Tiemann
 * 
 */
public class JunitPolicyDbFinderModuleTest extends TestCase {

	@Test
	public void testIsRequestSupported() {
		XMLDBPdpAdminService adminServiceMock 
				= createMock(XMLDBPdpAdminService.class);
		replay(adminServiceMock);
		
		PolicyDbFinderModule finderModule = new PolicyDbFinderModule(
				adminServiceMock);
		assertTrue(finderModule.isRequestSupported());
		verify(adminServiceMock);
	}

	@Test
	public void testPolicyDbFinderModule() {
		XMLDBPdpAdminService adminServiceMock 
				= createMock(XMLDBPdpAdminService.class);
		replay(adminServiceMock);
		
		PolicyDbFinderModule finderModule = new PolicyDbFinderModule(
				adminServiceMock);
		assertNotNull(finderModule);
		verify(adminServiceMock);
	}

	@Test
	public void testInitPolicyFinder() {
		XMLDBPdpAdminService adminServiceMock 
				= createMock(XMLDBPdpAdminService.class);
		replay(adminServiceMock);
		
		PolicyFinder finderMock = createMock(PolicyFinder.class);
		replay(finderMock);
		PolicyDbFinderModule finderModule = new PolicyDbFinderModule(
				adminServiceMock);
		finderModule.init(finderMock);
		verify(adminServiceMock);
	}

	@Test
	public void testGetIdentifier() {
		XMLDBPdpAdminService adminServiceMock 
				= createMock(XMLDBPdpAdminService.class);
		replay(adminServiceMock);
		
		PolicyDbFinderModule finderModule = new PolicyDbFinderModule(
				adminServiceMock);
		assertNotNull(finderModule.getIdentifier());
		verify(adminServiceMock);
	}

	@Test
	public void testFindPolicyEvaluationCtx() {
		XMLDBPdpAdminService adminServiceMock 
				= createMock(XMLDBPdpAdminService.class);
		String[] policies = new String[0];
		expect(adminServiceMock.getActivePolicyList()).andReturn(policies);
		replay(adminServiceMock);
		
		PolicyFinder finderMock = createMock(PolicyFinder.class);
		replay(finderMock);

		EvaluationCtx evalCtxMock = createMock(EvaluationCtx.class);
		try {
			expect(
					evalCtxMock.getResourceAttribute(new URI(
							"http://www.w3.org/2001/X" + "MLSchema#string"),
							new URI("linksmart:policy:resource:pid"), null))
					.andReturn(createMock(EvaluationResult.class));
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}
		replay(evalCtxMock);
		
		PolicyDbFinderModule finderModule = new PolicyDbFinderModule(
				adminServiceMock);
		finderModule.init(finderMock);
		finderModule.findPolicy(evalCtxMock);
		verify(adminServiceMock);
		verify(finderMock);
		verify(evalCtxMock);
	}

	@Test
	public void testLoadPolicy() {
		XMLDBPdpAdminService adminServiceMock 
				= createMock(XMLDBPdpAdminService.class);
		replay(adminServiceMock);
		
		PolicyFinder finderMock = createMock(PolicyFinder.class);
		replay(finderMock);

		PolicyDbFinderModule finderModule = new PolicyDbFinderModule(
				adminServiceMock);
		finderModule.init(finderMock);
		assertNull(finderModule.loadPolicy(createMock(Resource.class),
				finderMock));
		verify(adminServiceMock);
		verify(finderMock);
	}

}
