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
package eu.linksmart.policy.pdp.finder.impl;

import static org.easymock.EasyMock.*;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.finder.PolicyFinder;

import eu.linksmart.policy.pdp.admin.impl.FileSystemPdpAdminService;
import eu.linksmart.policy.pdp.finder.impl.LocalFolderPolicyFinderModule;

/**
 * Unit test for {@link LocalFolderPolicyFinderModule}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitLocalFolderPolicyFinderModuleTest extends TestCase {

	@Test
	public void testIsRequestSupported() {
		FileSystemPdpAdminService pdpAdminMock 
				= createMock(FileSystemPdpAdminService.class);
		expect(pdpAdminMock.getActivePolicyFolder()).andReturn(new File(""));
		replay(pdpAdminMock);
		
		LocalFolderPolicyFinderModule finder 
				= new LocalFolderPolicyFinderModule(pdpAdminMock);
		assertTrue(finder.isRequestSupported());
		verify(pdpAdminMock);
	}

	@Test
	public void testIsIdReferenceSupported() {
		FileSystemPdpAdminService pdpAdminMock 
				= createMock(FileSystemPdpAdminService.class);
		expect(pdpAdminMock.getActivePolicyFolder()).andReturn(new File(""));
		replay(pdpAdminMock);
		
		LocalFolderPolicyFinderModule finder 
				= new LocalFolderPolicyFinderModule(pdpAdminMock);
		assertFalse(finder.isIdReferenceSupported());
		verify(pdpAdminMock);
	}

	@Test
	public void testLocalFolderPolicyFinderModule() {
		FileSystemPdpAdminService pdpAdminMock 
				= createMock(FileSystemPdpAdminService.class);
		expect(pdpAdminMock.getActivePolicyFolder()).andReturn(new File(""));
		replay(pdpAdminMock);
		
		LocalFolderPolicyFinderModule finder 
				= new LocalFolderPolicyFinderModule(pdpAdminMock);
		assertNotNull(finder);
		verify(pdpAdminMock);
	}

	@Test
	public void testInitPolicyFinder() {
		PolicyFinder finderMock = createMock(PolicyFinder.class);
		FileSystemPdpAdminService pdpAdminMock 
				= createMock(FileSystemPdpAdminService.class);
		expect(pdpAdminMock.getActivePolicyFolder()).andReturn(new File(""));
		replay(pdpAdminMock);
		
		LocalFolderPolicyFinderModule finder 
				= new LocalFolderPolicyFinderModule(pdpAdminMock);
		finder.init(finderMock);
		verify(pdpAdminMock);
	}

	@Test
	public void testListFilePolicies() {
		FileSystemPdpAdminService pdpAdminMock 
				= createMock(FileSystemPdpAdminService.class);
		expect(pdpAdminMock.getActivePolicyFolder()).andReturn(new File(""));
		replay(pdpAdminMock);
		
		LocalFolderPolicyFinderModule finder 
				= new LocalFolderPolicyFinderModule(pdpAdminMock);
		finder.listFilePolicies(new File(""));
		verify(pdpAdminMock);
	}

	@Test
	public void testFindPolicyEvaluationCtx() {
		FileSystemPdpAdminService pdpAdminMock 
				= createMock(FileSystemPdpAdminService.class);
		EvaluationCtx evalCtxMock = createMock(EvaluationCtx.class);
		expect(pdpAdminMock.getActivePolicyFolder()).andReturn(new File(""));
		replay(pdpAdminMock);
		
		LocalFolderPolicyFinderModule finder 
				= new LocalFolderPolicyFinderModule(pdpAdminMock);
		finder.findPolicy(evalCtxMock);
		verify(pdpAdminMock);
	}

	@Test
	public void testGetIdentifier() {
		FileSystemPdpAdminService pdpAdminMock 
				= createMock(FileSystemPdpAdminService.class);
		expect(pdpAdminMock.getActivePolicyFolder()).andReturn(new File(""));
		replay(pdpAdminMock);
		
		LocalFolderPolicyFinderModule finder 
				= new LocalFolderPolicyFinderModule(pdpAdminMock);
		assertNotNull(finder.getIdentifier());
		verify(pdpAdminMock);
	}

}
