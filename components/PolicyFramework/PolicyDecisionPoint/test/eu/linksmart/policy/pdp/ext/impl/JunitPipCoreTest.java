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
package eu.linksmart.policy.pdp.ext.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.cond.Function;

import eu.linksmart.policy.pdp.ext.function.impl.PdpFunctionScope;
import eu.linksmart.policy.pdp.ext.impl.PipCore;
import eu.linksmart.policy.pdp.ext.impl.PipModule;

/**
 * Unit test for {@link PipCore}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitPipCoreTest {

	@SuppressWarnings("boxing")
	@Test
	public void testPipCore() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);		
		String oc = "(objectClass=eu.linksmart.policy.pdp.ext.impl.PipModule)";
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		expect(pipModuleMock.getIdentifier()).andReturn("modMock");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(true);
		expect(pipModuleMock.getFunctions(PdpFunctionScope.CONDITION))
				.andReturn(new HashSet<Function>());
		expect(pipModuleMock.getFunctions(PdpFunctionScope.GENERAL))
				.andReturn(new HashSet<Function>());
		expect(pipModuleMock.getFunctions(PdpFunctionScope.TARGET))
				.andReturn(new HashSet<Function>());
		replay(pipModuleMock);

		BundleContext contextMock = createMock(BundleContext.class);
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"ext.impl.PipModule", null)).andReturn(refs);
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.ext.impl.PipModu" +
							"le)"));
			expect(contextMock.getService(refs[0])).andReturn(pipModuleMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		
		assertNotNull(new PipCore(contextMock));
		verify(filterMock);
		verify(refMock);
		verify(pipModuleMock);
		verify(contextMock);
	}

	@SuppressWarnings({ "boxing", "unchecked" })
	@Test
	public void testFindAttributeStringNodeURIEvaluationCtxString() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		String oc = "(objectClass=eu.linksmart.policy.pdp.ext.impl.PipModule)";
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		PipModule modMock = createMock(PipModule.class);
		expect(modMock.getIdentifier()).andReturn("modMock");
		expect(modMock.isDesignatorSupported()).andReturn(true);
		expect(modMock.isSelectorSupported()).andReturn(true);
		expect(modMock.getFunctions(PdpFunctionScope.CONDITION))
				.andReturn(new HashSet<Function>());
		expect(modMock.getFunctions(PdpFunctionScope.GENERAL))
				.andReturn(new HashSet<Function>());
		expect(modMock.getFunctions(PdpFunctionScope.TARGET))
				.andReturn(new HashSet<Function>());
		try {
			expect(modMock.findAttribute("", null, new URI(""), null, ""))
					.andReturn(new EvaluationResult(new BagAttribute(
							new URI(""), new HashSet())));
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail("Exception");
		}
		replay(modMock);

		BundleContext contextMock = createMock(BundleContext.class);
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"ext.impl.PipModule", null)).andReturn(refs);
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.ext.impl.PipModu" +
							"le)"));
			expect(contextMock.getService(refs[0])).andReturn(modMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(contextMock);
		
		PipCore pipCore = new PipCore(contextMock);
		try {
			pipCore.findAttribute("", null, new URI(""), null, "");
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(refMock);
		verify(modMock);
		verify(contextMock);
	}

	@SuppressWarnings({ "boxing", "unchecked" })
	@Test
	public void testFindAttributeURIURIURIURIEvaluationCtxInt() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		String oc = "(objectClass=eu.linksmart.policy.pdp.ext.impl.PipModule)";
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		PipModule modMock = createMock(PipModule.class);
		expect(modMock.getIdentifier()).andReturn("modMock");
		expect(modMock.isDesignatorSupported()).andReturn(true);
		expect(modMock.isSelectorSupported()).andReturn(true);
		expect(modMock.getFunctions(PdpFunctionScope.CONDITION))
				.andReturn(new HashSet<Function>());
		expect(modMock.getFunctions(PdpFunctionScope.GENERAL))
				.andReturn(new HashSet<Function>());
		expect(modMock.getFunctions(PdpFunctionScope.TARGET))
				.andReturn(new HashSet<Function>());
		try {
			expect(modMock.findAttribute(new URI(""), new URI(""), new URI(""), 
					new URI(""), null, 1)).andReturn(new EvaluationResult(
					new BagAttribute(new URI(""), new HashSet())));
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail("Exception");
		}
		replay(modMock);

		BundleContext contextMock = createMock(BundleContext.class);
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"ext.impl.PipModule", null)).andReturn(refs);
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.ext.impl.PipModu" +
							"le)"));
			expect(contextMock.getService(refs[0])).andReturn(modMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		
		PipCore pipCore = new PipCore(contextMock);
		try {
			pipCore.findAttribute(new URI(""), new URI(""), new URI(""), 
					new URI(""), null, 1);
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(refMock);
		verify(modMock);
		verify(contextMock);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testRegister() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		String oc = "(objectClass=eu.linksmart.policy.pdp.ext.impl.PipModule)";
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		PipModule modMock = createMock(PipModule.class);
		expect(modMock.getIdentifier()).andReturn("modMock");
		expectLastCall().times(2);
		expect(modMock.isDesignatorSupported()).andReturn(true);
		expectLastCall().times(2);
		expect(modMock.isSelectorSupported()).andReturn(true);
		expectLastCall().times(2);
		expect(modMock.getFunctions(PdpFunctionScope.CONDITION))
				.andReturn(new HashSet<Function>());
		expectLastCall().times(2);
		expect(modMock.getFunctions(PdpFunctionScope.GENERAL))
				.andReturn(new HashSet<Function>());
		expectLastCall().times(2);
		expect(modMock.getFunctions(PdpFunctionScope.TARGET))
				.andReturn(new HashSet<Function>());
		expectLastCall().times(2);
		replay(modMock);

		BundleContext contextMock = createMock(BundleContext.class);
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"ext.impl.PipModule", null)).andReturn(refs);
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.ext.impl.PipModu" +
							"le)"));
			expect(contextMock.getService(refs[0])).andReturn(modMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		PipCore pipCore = new PipCore(contextMock);
		pipCore.register(refs[0]);
		verify(filterMock);
		verify(refMock);
		verify(modMock);
		verify(contextMock);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testRemove() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		String oc = "(objectClass=eu.linksmart.policy.pdp.ext.impl.PipModule)";
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		PipModule modMock = createMock(PipModule.class);
		expect(modMock.getIdentifier()).andReturn("modMock");
		expectLastCall().times(2);
		expect(modMock.isDesignatorSupported()).andReturn(true);
		expectLastCall().times(2);
		expect(modMock.isSelectorSupported()).andReturn(true);
		expectLastCall().times(2);
		expect(modMock.getFunctions(PdpFunctionScope.CONDITION))
				.andReturn(new HashSet<Function>());
		expectLastCall().times(2);
		expect(modMock.getFunctions(PdpFunctionScope.GENERAL))
				.andReturn(new HashSet<Function>());
		expectLastCall().times(2);
		expect(modMock.getFunctions(PdpFunctionScope.TARGET))
				.andReturn(new HashSet<Function>());
		expectLastCall().times(2);
		replay(modMock);

		BundleContext contextMock = createMock(BundleContext.class);
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"ext.impl.PipModule", null)).andReturn(refs);
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.ext.impl.PipModule)"));
			expect(contextMock.getService(refs[0])).andReturn(modMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		
		PipCore pipCore = new PipCore(contextMock);
		pipCore.register(refs[0]);
		pipCore.remove(refs[0]);
		verify(filterMock);
		verify(refMock);
		verify(modMock);
		verify(contextMock);
	}

}
