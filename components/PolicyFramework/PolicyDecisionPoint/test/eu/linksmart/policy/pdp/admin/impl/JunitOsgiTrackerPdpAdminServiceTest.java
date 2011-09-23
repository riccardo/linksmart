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
package eu.linksmart.policy.pdp.admin.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.rmi.RemoteException;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;


import eu.linksmart.policy.pdp.PdpAdmin;
import eu.linksmart.policy.pdp.admin.impl.OsgiTrackerPdpAdminService;

/**
 * Unit test for {@link OsgiTrackerPdpAdminService}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitOsgiTrackerPdpAdminServiceTest {

	/** test policy */
	private static final String POLICY =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<Policy PolicyId=\"ExamplePolicy\""  
		+ "RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-combining-alg" 
		+ "orithm:permit-overrides\"" 
		+ "xmlns=\"urn:oasis:names:tc:xacml:1.0:policy\""  
		+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
		+ "xsi:schemaLocation=\"urn:oasis:names:tc:xacml:1.0:policy cs-xacml-sc" 
		+ "hema-policy-01.xsd\">"
		+ "<Target>"
		+ "<Subjects>"
 		+ "<AnySubject/>"
		+ "</Subjects>"
		+ "<Resources>"
		+ "<AnyResource/>"
		+ "</Resources>"
		+ "<Actions>"
		+ "<AnyAction/>"
		+ "</Actions>"
		+ "</Target>"
		+ "<Rule Effect=\"Permit\" RuleId=\"DefaultPermit\"/>"
		+ "</Policy>";
	
	@Test
	public void testOsgiTrackerPdpAdminService() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		replay(adminMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		String oc = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
		try {
			expect(bundleCtxMock.createFilter(oc)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq(oc));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.PdpAdmin)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		try {
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"PdpAdmin", null)).andReturn(refs);
			expect(bundleCtxMock.getService(refs[0])).andReturn(adminMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		OsgiTrackerPdpAdminService admin 
				= new OsgiTrackerPdpAdminService(bundleCtxMock);
		assertNotNull(admin);
		verify(filterMock);
		verify(refMock);
		verify(adminMock);
		verify(bundleCtxMock);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testDeactivatePolicy() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		try {
			expect(adminMock.publishPolicy("MyLittlePolicy", POLICY))
					.andReturn(true);
			expect(adminMock.activatePolicy("MyLittlePolicy"))
					.andReturn(true);
			expect(adminMock.deactivatePolicy("MyLittlePolicy"))
					.andReturn(true);
			expect(adminMock.removePolicy("MyLittlePolicy"))
					.andReturn(true);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(adminMock);
		
		BundleContext contextMock = createMock(BundleContext.class);
		String oc = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
			contextMock.addServiceListener(isA(ServiceListener.class), eq(oc));
			contextMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"PdpAdmin", null)).andReturn(refs);
			expect(contextMock.getService(refs[0])).andReturn(adminMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		
		OsgiTrackerPdpAdminService admin 
				= new OsgiTrackerPdpAdminService(contextMock);
		try {
			admin.publishPolicy("MyLittlePolicy", POLICY);
			admin.activatePolicy("MyLittlePolicy");
			admin.deactivatePolicy("MyLittlePolicy");
			admin.removePolicy("MyLittlePolicy");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(refMock);
		verify(adminMock);
		verify(contextMock);
	}

	@Test
	public void testGetActivePolicyList() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		String[] policies = new String[0];
		try {
			expect(adminMock.getActivePolicyList()).andReturn(policies);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(adminMock);
		
		BundleContext contextMock = createMock(BundleContext.class);
		String oc = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		try {
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.PdpAdmin)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		try {
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"PdpAdmin", null)).andReturn(refs);
			expect(contextMock.getService(refs[0])).andReturn(adminMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		
		OsgiTrackerPdpAdminService admin 
				= new OsgiTrackerPdpAdminService(contextMock);
		try {
			assertNotNull(admin.getActivePolicyList());
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(refMock);
		verify(adminMock);
		verify(contextMock);
	}

	@Test
	public void testGetInActivePolicyList() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		String[] policies = new String[0];
		try {
			expect(adminMock.getInActivePolicyList()).andReturn(policies);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(adminMock);
		
		BundleContext contextMock = createMock(BundleContext.class);
		String oc = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		try {
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.PdpAdmin)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		try {
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"PdpAdmin", null)).andReturn(refs);
			expect(contextMock.getService(refs[0])).andReturn(adminMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		OsgiTrackerPdpAdminService admin 
				= new OsgiTrackerPdpAdminService(contextMock);
		try {
			assertNotNull(admin.getInActivePolicyList());
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(refMock);
		verify(adminMock);
		verify(contextMock);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testGetPolicy() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);

		PdpAdmin adminMock = createMock(PdpAdmin.class);
		try {
			expect(adminMock.publishPolicy(eq("MyLittlePolicy"), eq(POLICY)))
					.andReturn(true);
			expect(adminMock.getPolicy("MyLittlePolicy"))
					.andReturn(POLICY);
			expect(adminMock.removePolicy("MyLittlePolicy"))
					.andReturn(true);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(adminMock);
		
		BundleContext contextMock = createMock(BundleContext.class);
		String oc = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		try {
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.PdpAdmin)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		try {
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"PdpAdmin", null)).andReturn(refs);
			expect(contextMock.getService(refs[0])).andReturn(adminMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		OsgiTrackerPdpAdminService admin 
				= new OsgiTrackerPdpAdminService(contextMock);
		try {
			admin.publishPolicy("MyLittlePolicy", POLICY);
			admin.getPolicy("MyLittlePolicy");
			admin.removePolicy("MyLittlePolicy");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(refMock);
		verify(adminMock);
		verify(contextMock);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testGetProperty() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		try {
			expect(adminMock.setProperty(eq("ping"), eq("pong"))).andReturn(true);
			expect(adminMock.getProperty("ping")).andReturn("pong");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(adminMock);
		
		BundleContext contextMock = createMock(BundleContext.class);
		String oc = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		try {
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.PdpAdmin)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		try {
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"PdpAdmin", null)).andReturn(refs);
			expect(contextMock.getService(refs[0])).andReturn(adminMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		OsgiTrackerPdpAdminService admin 
				= new OsgiTrackerPdpAdminService(contextMock);
		try {
			admin.setProperty("ping", "pong");
			assertEquals("pong", admin.getProperty("ping"));
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(refMock);
		verify(adminMock);
		verify(contextMock);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testPublishPolicy() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
				
		BundleContext contextMock = createMock(BundleContext.class);
		String oc = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		try {
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.PdpAdmin)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		try {
			expect(adminMock.publishPolicy("MyLittlePolicy", POLICY))
					.andReturn(true);
			expect(adminMock.removePolicy("MyLittlePolicy"))
					.andReturn(true);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(adminMock);
		try {
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"PdpAdmin", null)).andReturn(refs);
			expect(contextMock.getService(refs[0])).andReturn(adminMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		
		OsgiTrackerPdpAdminService admin 
				= new OsgiTrackerPdpAdminService(contextMock);
		try {
			admin.publishPolicy("MyLittlePolicy", POLICY);
			admin.removePolicy("MyLittlePolicy");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(refMock);
		verify(adminMock);
		verify(contextMock);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testRemovePolicy() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		try {
			expect(adminMock.publishPolicy("MyLittlePolicy", POLICY))
					.andReturn(true);
			expect(adminMock.removePolicy("MyLittlePolicy"))
					.andReturn(true);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(adminMock);
		
		BundleContext contextMock = createMock(BundleContext.class);
		String oc = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		try {
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.PdpAdmin)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		try {
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"PdpAdmin", null)).andReturn(refs);
			expect(contextMock.getService(refs[0])).andReturn(adminMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		OsgiTrackerPdpAdminService admin 
				= new OsgiTrackerPdpAdminService(contextMock);
		try {
			admin.publishPolicy("MyLittlePolicy", POLICY);
			admin.removePolicy("MyLittlePolicy");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(refMock);
		verify(adminMock);
		verify(contextMock);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testSetProperty() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);
		
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		try {
			expect(adminMock.setProperty("ping", "pong")).andReturn(true);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(adminMock);
		
		BundleContext contextMock = createMock(BundleContext.class);
		String oc = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		try {
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.PdpAdmin)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		try {
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"PdpAdmin", null)).andReturn(refs);
			expect(contextMock.getService(refs[0])).andReturn(adminMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		
		OsgiTrackerPdpAdminService admin 
				= new OsgiTrackerPdpAdminService(contextMock);
		try {
			admin.setProperty("ping", "pong");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(refMock);
		verify(adminMock);
		verify(contextMock);
	}

	@Test
	public void testRegister() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		replay(adminMock);
		
		BundleContext contextMock = createMock(BundleContext.class);
		String oc = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		try {
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.PdpAdmin)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		try {
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"PdpAdmin", null)).andReturn(refs);
			expect(contextMock.getService(refs[0])).andReturn(adminMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		OsgiTrackerPdpAdminService admin = new OsgiTrackerPdpAdminService(
				contextMock);
		admin.register(refs[0]);
		verify(filterMock);
		verify(refMock);
		verify(adminMock);
		verify(contextMock);
	}

	@Test
	public void testRemove() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(3);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(3);
		replay(refMock);
		
		PdpAdmin adminMock = createMock(PdpAdmin.class);
		replay(adminMock);
		
		BundleContext contextMock = createMock(BundleContext.class);
		String oc = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
		try {
			expect(contextMock.createFilter(oc)).andReturn(filterMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		try {
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq(oc));
			contextMock.addServiceListener(anyObject(ServiceListener.class), 
					eq("(objectclass=eu.linksmart.policy.pdp.PdpAdmin)"));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		try {
			expect(contextMock.getServiceReferences("eu.linksmart.policy.pdp." +
					"PdpAdmin", null)).andReturn(refs);
			expect(contextMock.getService(refs[0])).andReturn(adminMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(contextMock);
		OsgiTrackerPdpAdminService admin = new OsgiTrackerPdpAdminService(
				contextMock);
		admin.register(refs[0]);
		admin.remove(refs[0]);
		verify(filterMock);
		verify(refMock);
		verify(adminMock);
		verify(contextMock);
	}

}
