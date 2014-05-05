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
package eu.linksmart.policy.pep.response.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.sun.xacml.Obligation;
import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.response.PepObligationObserver;
import eu.linksmart.policy.pep.response.impl.OsgiTrackerPepObligationObserver;

/**
 * Unit test for {@link OsgiTrackerPepObligationObserver}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitOsgiTrackerPepObligationObserverTest {

	@Test
	public void testOsgiTrackerPepObligationObserver() {
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		PepObligationObserver observerMock 
				= createMock(PepObligationObserver.class);
		replay(observerMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
					.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null))
					.andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(observerMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPepObligationObserver observer
				= new OsgiTrackerPepObligationObserver(bundleCtxMock);
		assertNotNull(observer);
		verify(refMock);
		verify(filterMock);
		verify(observerMock);
		verify(bundleCtxMock);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testEvaluate() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		PepObligationObserver observerMock 
				= createMock(PepObligationObserver.class);
		expect(observerMock.evaluate(isA(Obligation.class), 
				isA(PepRequest.class), isA(ResponseCtx.class)))
				.andReturn(true);
		replay(observerMock);		
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
					.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			ServiceReference[] refs = new ServiceReference[1];			
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null))
					.andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(observerMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPepObligationObserver observer
				= new OsgiTrackerPepObligationObserver(bundleCtxMock);
		assertTrue(observer.evaluate(createMock(Obligation.class), 
				createMock(PepRequest.class), createMock(ResponseCtx.class)));
		verify(refMock);
		verify(filterMock);
		verify(observerMock);
		verify(bundleCtxMock);
	}

	@Test
	public void testRegister() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		PepObligationObserver observerMock 
				= createMock(PepObligationObserver.class);
		replay(observerMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(4);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(4);
		replay(refMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
					.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			ServiceReference[] refs = new ServiceReference[1];			
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null))
					.andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(observerMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPepObligationObserver observer
				= new OsgiTrackerPepObligationObserver(bundleCtxMock);
		observer.register(refMock);
		verify(refMock);
		verify(filterMock);
		verify(observerMock);
		verify(bundleCtxMock);
	}

	@Test
	public void testRemove() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		PepObligationObserver observerMock 
				= createMock(PepObligationObserver.class);
		replay(observerMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(5);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(5);
		replay(refMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
					.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			ServiceReference[] refs = new ServiceReference[1];			
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null))
					.andReturn(refs);
			expect(bundleCtxMock.getService(refMock)).andReturn(observerMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPepObligationObserver observer
				= new OsgiTrackerPepObligationObserver(bundleCtxMock);
		observer.register(refMock);
		observer.remove(refMock);
		verify(refMock);
		verify(filterMock);
		verify(observerMock);
		verify(bundleCtxMock);
	}

}
