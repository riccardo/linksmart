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
import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.PolicyFinderResult;

import eu.linksmart.policy.pdp.finder.impl.OsgiTrackerPolicyFinderModule;

/**
 * Unit test for {@link OsgiTrackerPolicyFinderModule}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitOsgiTrackerPolicyFinderModuleTest {

	@SuppressWarnings("boxing")
	@Test
	public void testIsRequestSupported() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PolicyFinderModule finderModuleMock 
				= createMock(PolicyFinderModule.class);
		expect(finderModuleMock.isRequestSupported()).andReturn(true);
		replay(finderModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=com.sun.xacml.fin" +
					"der.PolicyFinderModule)")).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=com.sun.xacml.finder.PolicyFinderModule)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("com.sun.xacml.finder.P" +
					"olicyFinderModule", null)).andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(finderModuleMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=com.sun.xacml.finder.PolicyFinderModule)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPolicyFinderModule finderModule
				= new OsgiTrackerPolicyFinderModule(bundleCtxMock);
		assertTrue(finderModule.isRequestSupported());
		verify(filterMock);
		verify(finderModuleMock);
		verify(bundleCtxMock);
		verify(refMock);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testIsIdReferenceSupported() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PolicyFinderModule finderModuleMock 
				= createMock(PolicyFinderModule.class);
		expect(finderModuleMock.isIdReferenceSupported()).andReturn(true);
		replay(finderModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=com.sun.xacml.fin" +
					"der.PolicyFinderModule)")).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=com.sun.xacml.finder.PolicyFinderModule)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("com.sun.xacml.finder.P" +
					"olicyFinderModule", null)).andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(finderModuleMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=com.sun.xacml.finder.PolicyFinderModule)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPolicyFinderModule finderModule
				= new OsgiTrackerPolicyFinderModule(bundleCtxMock);
		assertTrue(finderModule.isIdReferenceSupported());
		verify(filterMock);
		verify(finderModuleMock);
		verify(bundleCtxMock);
		verify(refMock);
	}

	@Test
	public void testInvalidateCache() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PolicyFinderModule finderModuleMock 
				= createMock(PolicyFinderModule.class);
		finderModuleMock.invalidateCache();
		replay(finderModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=com.sun.xacml.fin" +
					"der.PolicyFinderModule)")).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=com.sun.xacml.finder.PolicyFinderModule)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("com.sun.xacml.finder.P" +
					"olicyFinderModule", null)).andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(finderModuleMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=com.sun.xacml.finder.PolicyFinderModule)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPolicyFinderModule finderModule
				= new OsgiTrackerPolicyFinderModule(bundleCtxMock);
		finderModule.invalidateCache();
		verify(filterMock);
		verify(finderModuleMock);
		verify(bundleCtxMock);
		verify(refMock);
	}

	@Test
	public void testOsgiTrackerPolicyFinderModule() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PolicyFinderModule finderModuleMock 
				= createMock(PolicyFinderModule.class);
		replay(finderModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=com.sun.xacml.fin" +
					"der.PolicyFinderModule)")).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=com.sun.xacml.finder.PolicyFinderModule)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("com.sun.xacml.finder.P" +
					"olicyFinderModule", null)).andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(finderModuleMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=com.sun.xacml.finder.PolicyFinderModule)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPolicyFinderModule finderModule
				= new OsgiTrackerPolicyFinderModule(bundleCtxMock);
		assertNotNull(finderModule);
		verify(filterMock);
		verify(finderModuleMock);
		verify(bundleCtxMock);
		verify(refMock);
	}

	@Test
	public void testFindPolicyEvaluationCtx() {
		EvaluationCtx evalCtxMock = createMock(EvaluationCtx.class);
		replay(evalCtxMock);

		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		PolicyFinderModule finderModuleMock 
				= createMock(PolicyFinderModule.class);
		expect(finderModuleMock.findPolicy(evalCtxMock)).andReturn(
				createMock(PolicyFinderResult.class));
		replay(finderModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=com.sun.xacml.fin" +
					"der.PolicyFinderModule)")).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=com.sun.xacml.finder.PolicyFinderModule)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("com.sun.xacml.finder.P" +
					"olicyFinderModule", null)).andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(finderModuleMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=com.sun.xacml.finder.PolicyFinderModule)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPolicyFinderModule finderModule
				= new OsgiTrackerPolicyFinderModule(bundleCtxMock);
		assertNotNull(finderModule.findPolicy(evalCtxMock));
		verify(filterMock);
		verify(finderModuleMock);
		verify(bundleCtxMock);
		verify(evalCtxMock);
		verify(refMock);
	}

	@Test
	public void testFindPolicyURIInt() {
		EvaluationCtx evalCtxMock = createMock(EvaluationCtx.class);
		replay(evalCtxMock);

		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		PolicyFinderModule finderModuleMock 
				= createMock(PolicyFinderModule.class);
		try {
			expect(finderModuleMock.findPolicy(new URI(""), 1)).andReturn(
					createMock(PolicyFinderResult.class));
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}
		replay(finderModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=com.sun.xacml.fin" +
					"der.PolicyFinderModule)")).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=com.sun.xacml.finder.PolicyFinderModule)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("com.sun.xacml.finder.P" +
					"olicyFinderModule", null)).andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(finderModuleMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=com.sun.xacml.finder.PolicyFinderModule)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPolicyFinderModule finderModule
				= new OsgiTrackerPolicyFinderModule(bundleCtxMock);
		try {
			assertNotNull(finderModule.findPolicy(new URI(""), 1));
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}
		verify(filterMock);
		verify(finderModuleMock);
		verify(bundleCtxMock);
		verify(refMock);
	}

	@Test
	public void testGetIdentifier() {
		EvaluationCtx evalCtxMock = createMock(EvaluationCtx.class);
		replay(evalCtxMock);
		
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		PolicyFinderModule finderModuleMock 
				= createMock(PolicyFinderModule.class);
		expect(finderModuleMock.getIdentifier()).andReturn("JohnDoe");
		replay(finderModuleMock);

		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=com.sun.xacml.fin" +
					"der.PolicyFinderModule)")).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=com.sun.xacml.finder.PolicyFinderModule)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("com.sun.xacml.finder.P" +
					"olicyFinderModule", null)).andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(finderModuleMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=com.sun.xacml.finder.PolicyFinderModule)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPolicyFinderModule finderModule
				= new OsgiTrackerPolicyFinderModule(bundleCtxMock);
		assertNotNull(finderModule.getIdentifier());
		verify(filterMock);
		verify(finderModuleMock);
		verify(bundleCtxMock);
	}

	@Test
	public void testInitPolicyFinder() {
		PolicyFinder finderMock = createMock(PolicyFinder.class);
		replay(finderMock);
		
		EvaluationCtx evalCtxMock = createMock(EvaluationCtx.class);
		replay(evalCtxMock);
		
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PolicyFinderModule finderModuleMock 
				= createMock(PolicyFinderModule.class);
		finderModuleMock.init(finderMock);
		replay(finderModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=com.sun.xacml.fin" +
					"der.PolicyFinderModule)")).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=com.sun.xacml.finder.PolicyFinderModule)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("com.sun.xacml.finder.P" +
					"olicyFinderModule", null)).andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(finderModuleMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=com.sun.xacml.finder.PolicyFinderModule)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		OsgiTrackerPolicyFinderModule finderModule
				= new OsgiTrackerPolicyFinderModule(bundleCtxMock);
		finderModule.init(finderMock);
		verify(filterMock);
		verify(finderModuleMock);
		verify(bundleCtxMock);
		verify(evalCtxMock);
	}

	@Test
	public void testRegister() {
		ServiceReference rMock = createMock(ServiceReference.class);
		replay(rMock);
		
		PolicyFinder finderMock = createMock(PolicyFinder.class);
		replay(finderMock);
		
		EvaluationCtx evalCtxMock = createMock(EvaluationCtx.class);
		replay(evalCtxMock);
		
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PolicyFinderModule finderModuleMock 
				= createMock(PolicyFinderModule.class);
		replay(finderModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=com.sun.xacml.fin" +
					"der.PolicyFinderModule)")).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=com.sun.xacml.finder.PolicyFinderModule)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("com.sun.xacml.finder.P" +
					"olicyFinderModule", null)).andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(finderModuleMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=com.sun.xacml.finder.PolicyFinderModule)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPolicyFinderModule finderModule
				= new OsgiTrackerPolicyFinderModule(bundleCtxMock);
		finderModule.register(rMock);
		verify(filterMock);
		verify(finderModuleMock);
		verify(bundleCtxMock);
		verify(rMock);
		verify(refMock);
	}

	@Test
	public void testRemove() {
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference rMock = createMock(ServiceReference.class);
		expect(rMock.getBundle()).andReturn(bndlMock);
		replay(rMock);
		PolicyFinder finderMock = createMock(PolicyFinder.class);
		replay(finderMock);
		EvaluationCtx evalCtxMock = createMock(EvaluationCtx.class);
		replay(evalCtxMock);
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		PolicyFinderModule finderModuleMock 
				= createMock(PolicyFinderModule.class);
		finderModuleMock.init(finderMock);
		replay(finderModuleMock);
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=com.sun.xacml.fin" +
					"der.PolicyFinderModule)")).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=com.sun.xacml.finder.PolicyFinderModule)"));
			ServiceReference[] refs = new ServiceReference[1];
			
			ServiceReference refMock = createMock(ServiceReference.class);
			expect(refMock.getBundle()).andReturn(bndlMock);
			replay(refMock);
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("com.sun.xacml.finder.P" +
					"olicyFinderModule", null)).andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(finderModuleMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=com.sun.xacml.finder.PolicyFinderModule)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		OsgiTrackerPolicyFinderModule finderModule
				= new OsgiTrackerPolicyFinderModule(bundleCtxMock);
		finderModule.register(rMock);
		finderModule.remove(rMock);
	}

}
