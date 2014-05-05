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
package eu.linksmart.policy.pdp.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.AttributeFinderModule;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.ResourceFinder;
import com.sun.xacml.finder.ResourceFinderModule;

import eu.linksmart.policy.pdp.PdpAdmin;
import eu.linksmart.policy.pdp.impl.PdpDecisionConfig;

/**
 * Unit test for {@link PdpDecisionConfig}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitPdpDecisionConfigTest {

	@Test
	public void testPdpDecisionConfig() {
		assertNotNull(new PdpDecisionConfig());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPdpDecisionConfigAttributeFinderPolicyFinderResourceFinderPdpAdmin() {
		AttributeFinder attrFinderMock = createMock(AttributeFinder.class);
		expect(attrFinderMock.getModules()).andReturn(new ArrayList());
		expectLastCall().times(2);
		attrFinderMock.setModules(isA(List.class));
		replay(attrFinderMock);
		
		PolicyFinder polFinderMock = createMock(PolicyFinder.class);
		replay(polFinderMock);
		
		ResourceFinder resFinderMock = createMock(ResourceFinder.class);
		replay(resFinderMock);
		
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		replay(adminMock);
		
		assertNotNull(new PdpDecisionConfig(attrFinderMock, polFinderMock, 
				resFinderMock, adminMock));
		verify(attrFinderMock);
		verify(polFinderMock);
		verify(resFinderMock);
		verify(adminMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPdpDecisionConfigAttributeFinderPolicyFinderResourceFinderPdpAdminPolicyFinderModule() {
		AttributeFinder attrFinderMock = createMock(AttributeFinder.class);
		expect(attrFinderMock.getModules()).andReturn(new ArrayList());
		expectLastCall().times(2);
		attrFinderMock.setModules(isA(List.class));
		replay(attrFinderMock);
		
		PolicyFinder polFinderMock = createMock(PolicyFinder.class);		
		expect(polFinderMock.getModules()).andReturn(new HashSet());
		expectLastCall().times(2);
		polFinderMock.setModules(isA(Set.class));
		replay(polFinderMock);
		
		ResourceFinder resFinderMock = createMock(ResourceFinder.class);
		replay(resFinderMock);
		
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		replay(adminMock);
		
		PolicyFinderModule finderModuleMock 
				= createMock(PolicyFinderModule.class);
		replay(finderModuleMock);
		
		assertNotNull(new PdpDecisionConfig(attrFinderMock, polFinderMock, 
				resFinderMock, adminMock, finderModuleMock));
		verify(attrFinderMock);
		verify(polFinderMock);
		verify(resFinderMock);
		verify(adminMock);
	}

	@Test
	public void testGetAttributeFinder() {
		assertNotNull(new PdpDecisionConfig().getAttributeFinder());
	}

	@SuppressWarnings("boxing")
	@Test
	public void testAddAttributeFinderModule() {
		AttributeFinderModule attrFinderMock 
				= createMock(AttributeFinderModule.class);
		expect(attrFinderMock.isDesignatorSupported()).andReturn(true);
		expect(attrFinderMock.isSelectorSupported()).andReturn(true);
		replay(attrFinderMock);
		
		PdpDecisionConfig deConfig = new PdpDecisionConfig();
		deConfig.addAttributeFinderModule(attrFinderMock);
		verify(attrFinderMock);
	}

	@Test
	public void testGetPolicyFinder() {
		assertNotNull(new PdpDecisionConfig().getPolicyFinder());
	}

	@SuppressWarnings("boxing")
	@Test
	public void testAddPolicyFinderModule() {
		PolicyFinderModule polFinderMock = createMock(PolicyFinderModule.class);
		expect(polFinderMock.isRequestSupported()).andReturn(true);
		expect(polFinderMock.isIdReferenceSupported()).andReturn(true);
		replay(polFinderMock);

		PdpDecisionConfig deConfig = new PdpDecisionConfig();
		deConfig.addPolicyFinderModule(polFinderMock);
		verify(polFinderMock);
	}

	@Test
	public void testGetResourceFinder() {
		assertNotNull(new PdpDecisionConfig().getResourceFinder());
	}

	@SuppressWarnings("boxing")
	@Test
	public void testAddResourceFinderModule() {
		ResourceFinderModule resModuleMock 
				= createMock(ResourceFinderModule.class);
		expect(resModuleMock.isChildSupported()).andReturn(true);
		expect(resModuleMock.isDescendantSupported()).andReturn(true);
		replay(resModuleMock);

		PdpDecisionConfig deConfig = new PdpDecisionConfig();
		deConfig.addResourceFinderModule(resModuleMock);
		verify(resModuleMock);
	}

	@Test
	public void testGetPdpAdminService() {
		assertNull(new PdpDecisionConfig().getPdpAdminService());		
	}

	@Test
	public void testSetPdpAdminService() {
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		replay(adminMock);
		
		PdpDecisionConfig pdpConfig = new PdpDecisionConfig();
		pdpConfig.setPdpAdminService(adminMock);
		assertNotNull(pdpConfig.getPdpAdminService());
		verify(adminMock);
	}

}
